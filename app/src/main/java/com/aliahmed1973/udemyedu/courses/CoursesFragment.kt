package com.aliahmed1973.udemyedu.courses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.databinding.CoursesFragmentBinding

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
        with(binding.rvCourses)
        {
            adapter = CoursesAdapter(CoursesAdapter.CourseClickListener {
                findNavController().navigate(
                    CoursesFragmentDirections.actionCoursesFragmentToCourseDetailsFragment(
                        it
                    )
                )
            })
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val layoutmanger: LinearLayoutManager =
                            recyclerView.layoutManager as LinearLayoutManager
                        val visiblePos = layoutmanger.findLastVisibleItemPosition()
                        val numItems = recyclerView.adapter?.itemCount
                        if (visiblePos >= numItems!! - 1) {
                            Log.d(TAG, "onScrolled: " + "yessss")
                            //  coursesViewModel.addNum()
                        }

                    }

                }
            })
        }
//        coursesViewModel.pageNum.observe(viewLifecycleOwner){
//            Log.d(TAG, "onViewCreated: pageNum"+"$it")
//        //    coursesViewModel.getCoursesList(it)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}