package com.aliahmed1973.udemyedu.ui.coursedetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStateAtLeast
import androidx.navigation.fragment.navArgs
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.databinding.CourseDetailsFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "CourseDetailsFragment"

class CourseDetailsFragment : Fragment() {
    private lateinit var binding: CourseDetailsFragmentBinding
    private val viewModel: CourseDetailsViewModel by viewModels {
        CourseDetailsViewModel.Factory((requireContext().applicationContext as CourseApp).repository)
    }
    private val args by navArgs<CourseDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CourseDetailsFragmentBinding.inflate(inflater)
        binding.lifecycleOwner=this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter  = ReviewAdapter()
        val course = args.courseDetails
        viewModel.checkCourseInDatabase(course)
       
        binding.rvCourseReviews.adapter =adapter
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.databaseCourse.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collectLatest {
                        Log.d(TAG, "onViewCreated: $it")
                        if (it != null) {
                            viewModel.setCourse(it)
                        } else {
                            viewModel.setCourse(course)
                        }
                    }
            }

            launch {  viewModel.courseReview.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    adapter.submitList(it)
                } }
        }



        binding.imageViewMarkIcon.setOnClickListener {
            viewModel.addOrRemoveCourseFromList()
        }
    }
}