package com.aliahmed1973.udemyedu.ui.courses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.database.asCourseModel
import com.aliahmed1973.udemyedu.databinding.CoursesFragmentBinding
import com.aliahmed1973.udemyedu.ui.CoursesLoadStateAdapter
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
            rvCourses.adapter = adapter.withLoadStateHeaderAndFooter(
                header = CoursesLoadStateAdapter { adapter.retry() },
                footer = CoursesLoadStateAdapter { adapter.retry() }
            )

            retryButton.setOnClickListener {
                adapter.retry()
            }
        }

        adapter.addLoadStateListener {
            loadState->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading ||loadState.mediator?.refresh is LoadState.Loading
                rvCourses.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error ||  loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0

                emptyList.isVisible = isListEmpty

            }
        }



        lifecycleScope.launch {
            coursesViewModel.courses.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    adapter.submitData(it.map {
                        db->db.asCourseModel()
                    })
                    Log.d(TAG, "onViewCreated: ${it}")
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}