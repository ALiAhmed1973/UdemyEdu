package com.aliahmed1973.udemyedu.coursedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.databinding.CourseDetailsFragmentBinding

private const val TAG = "CourseDetailsFragment"

class CourseDetailsFragment : Fragment() {
    private lateinit var binding: CourseDetailsFragmentBinding
    private val viewModel: CourseDetailsViewModel by viewModels {
        CourseDetailsViewModel.Factory((requireContext().applicationContext as CourseApp).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CourseDetailsFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val course = CourseDetailsFragmentArgs.fromBundle(requireArguments()).courseDetails
        viewModel.checkCourseInDatabase(course)
        viewModel.databaseCourse.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.setCourse(it)
            } else {
                viewModel.setCourse(course.copy(isAddedToMylist = false))
            }
        }
        binding.rvCourseReviews.adapter = ReviewAdapter()
        binding.imageViewMarkIcon.setOnClickListener {
            viewModel.addOrRemoveCourseFromList()
        }
    }
}