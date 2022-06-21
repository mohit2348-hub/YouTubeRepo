package com.example.youtuberepo.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtuberepo.R
import com.example.youtuberepo.adapter.VideoListAdapter
import com.example.youtuberepo.data.model.Items
import com.example.youtuberepo.databinding.ActivityMainBinding
import com.example.youtuberepo.util.NetworkUtil
import com.example.youtuberepo.util.QUERY_PAGE_SIZE
import com.example.youtuberepo.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "YoutubeVideosActivity"
@AndroidEntryPoint
class YoutubeVideosActivity : AppCompatActivity(), VideoListAdapter.OnItemClickListener {

    private var binding: ActivityMainBinding? = null

    private val viewModel: YoutubeVideosViewModel by viewModels()

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding?.root!!
        setContentView(view)
        viewModel.getVideosList()


        val videoListAdapter = VideoListAdapter(this)


        binding?.apply {
            rvVideoList.apply {
                adapter = videoListAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@YoutubeVideosActivity.scrollListener)
            }
            btRetry.setOnClickListener {
                viewModel.getVideosList()
                llNoInternet.visibility = View.GONE
            }
        }

        viewModel.videos.observe(this) {
            when (it) {
                is Resource.Success -> {
                    paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    it.data?.let { videosResponse ->
                        videoListAdapter.submitList(videosResponse.items?.toList())
                        val totalPages = videosResponse.pageInfo?.totalResults!!/QUERY_PAGE_SIZE + 2
                        isLastPage = (viewModel.videoListPage* videosResponse.pageInfo.resultsPerPage!!)>= totalPages
                        if (isLastPage)
                            rvVideoList.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    if (!NetworkUtil.hasInternetConnection(this@YoutubeVideosActivity)){
                        llNoInternet.visibility = View.VISIBLE
                    }
                    else{
                        llNoInternet.visibility = View.GONE
                    }
                    paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = true
                    it.message?.let { message ->
                        view.snack(message)
                        Log.e(TAG, "Error: $message")
                    }
                }
                is Resource.Loading -> {
                    paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //State is scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getVideosList()
                isScrolling = false
            }
        }
    }

    override fun onItemClick(items: Items) {
        Log.d(TAG, "onItemClick: "+items.id)
        watchYoutubeVideo(items.id?.videoId!!)
    }
    private fun watchYoutubeVideo(id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$id")
        )
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }
    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }
}