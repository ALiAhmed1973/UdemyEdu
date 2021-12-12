package com.aliahmed1973.udemyedu.courses

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aliahmed1973.udemyedu.databinding.CourseItemBinding
import com.aliahmed1973.udemyedu.model.Course

private const val TAG = "CoursesAdapter"
class CoursesAdapter:ListAdapter<Course,CoursesAdapter.CourseViewHolder>(DiffCallback) {

    object DiffCallback :DiffUtil.ItemCallback<Course>(){
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.title==newItem.title
        }

    }


    inner class CourseViewHolder(private var binding: CourseItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course)
        {
            Log.d(TAG, "bind: "+course)
            binding.tvCourseTitle.text=course.title
            binding.imageCourse.load(course.courseImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoursesAdapter.CourseViewHolder {
        return CourseViewHolder(CourseItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CoursesAdapter.CourseViewHolder, position: Int) {
        val course = getItem(position)
        holder.bind(course)
    }
}