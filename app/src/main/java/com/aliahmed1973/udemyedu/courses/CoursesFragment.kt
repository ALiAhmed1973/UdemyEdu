package com.aliahmed1973.udemyedu.courses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.databinding.CoursesFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "CoursesFragment"

class CoursesFragment : Fragment() {
    private var _binding: CoursesFragmentBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels {
        CoursesViewModel.Factory((requireContext().applicationContext as CourseApp).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CoursesFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = this.coursesViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CoursesAdapter(CoursesAdapter.CourseClickListener {
            findNavController().navigate(
                CoursesFragmentDirections.actionCoursesFragmentToCourseDetailsFragment(
                    it
                )
            )
        })

       binding.apply {
           rvCourses.setHasFixedSize(true)
           rvCourses.adapter=adapter
       }


        lifecycleScope.launch {
            coursesViewModel.courses.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
                .collectLatest{
                    adapter.submitData(it)
                    Log.d(TAG, "onViewCreated: ${it}")
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}