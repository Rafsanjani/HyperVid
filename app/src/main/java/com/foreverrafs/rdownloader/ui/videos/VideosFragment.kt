package com.foreverrafs.rdownloader.ui.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.foreverrafs.rdownloader.MainViewModel
import com.foreverrafs.rdownloader.R
import com.foreverrafs.rdownloader.databinding.FragmentVideosBinding
import com.foreverrafs.rdownloader.databinding.ListEmptyBinding
import com.foreverrafs.rdownloader.model.FacebookVideo
import com.foreverrafs.rdownloader.util.ItemTouchCallback
import com.foreverrafs.rdownloader.util.invisible
import com.foreverrafs.rdownloader.util.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File


class VideosFragment : Fragment(), VideoAdapter.VideoCallback {
    private val vm: MainViewModel by activityViewModels()
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var videoBinding: FragmentVideosBinding
    private lateinit var emptyListBinding: ListEmptyBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        videoBinding = FragmentVideosBinding.inflate(inflater)
        emptyListBinding = videoBinding.emptyLayout

        return videoBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoAdapter = VideoAdapter(requireContext(), this)

        videoBinding.videoListRecyclerView.adapter =
            videoAdapter

        val itemTouchHelperCallback = ItemTouchCallback(videoAdapter)

        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)

        touchHelper.attachToRecyclerView(videoBinding.videoListRecyclerView)

        initEmptyLayoutTexts()

        vm.videosList.observe(viewLifecycleOwner, Observer { videosList ->
            if (videosList.isNotEmpty()) {
                videoBinding.videoListRecyclerView.visible()
                emptyListBinding.root.invisible()

                videoAdapter.setList(videosList)

            } else {
                videoBinding.videoListRecyclerView.invisible()
                emptyListBinding.root.visible()
            }
        })
    }

    private fun initEmptyLayoutTexts() {
        emptyListBinding.apply {
            tvDescription.text = getString(R.string.empty_video_desc)
            tvTitle.text = getString(R.string.empty_video)
        }
    }

    override fun onPause() {
        vm.saveVideoList(videoAdapter.videos)
        super.onPause()
    }

    override fun deleteVideo(video: FacebookVideo) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Video")
            .setMessage("Are you sure you want to delete this video")
            .setPositiveButton("Yes") { _, _ ->
                videoAdapter.deleteVideo(video)
                File(video.path).delete()
            }
            .setNegativeButton("No", null)
            .show()
    }
}