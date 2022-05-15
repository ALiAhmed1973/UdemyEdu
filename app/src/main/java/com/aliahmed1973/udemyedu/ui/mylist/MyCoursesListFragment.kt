package com.aliahmed1973.udemyedu.ui.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aliahmed1973.udemyedu.CourseApp
import com.aliahmed1973.udemyedu.databinding.MyCoursesListFragmentBinding

class MyCoursesListFragment : Fragment() {
    private lateinit var binding: MyCoursesListFragmentBinding
    private val viewModel: MyCoursesListViewModel by viewModels {
        MyCoursesListViewModel.Factory((requireContext().applicationContext as CourseApp).repository)
    }
    private lateinit var adapter: MyCourseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyCoursesListFragmentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner=this
        adapter = MyCourseListAdapter(viewModel)
        binding.rvMylist.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.myListCourses.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        adapter.getCourse {
            this.findNavController().navigate(
                MyCoursesListFragmentDirections.actionMyCoursesListFragmentToMyListCourseDetailsFragment(
                    it
                )
            )
        }
    }
}