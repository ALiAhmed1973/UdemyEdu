package com.aliahmed1973.udemyedu.ui.mylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.databinding.MylistItemBinding
import com.aliahmed1973.udemyedu.model.Course

class MyCourseListAdapter(private val viewModel: MyCoursesListViewModel):ListAdapter<Course,MyCourseListAdapter.CoursViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Course>(){
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }

    lateinit var courseClickListener: (Course)->Unit


    inner class CoursViewHolder (private var binding:MylistItemBinding): RecyclerView.ViewHolder(binding.root){
        lateinit var itemCourse:Course
        fun bind(course: Course?) {
            itemCourse=course!!
            binding.course=course
            binding.executePendingBindings()
        }
        init {
            binding.imageListIcon.setOnClickListener {
                viewModel.removeCourseFromList(itemCourse)
            }

            binding.cardViewCourse.setOnClickListener {
                courseClickListener(itemCourse)
            }

        }

    }

    fun getCourse(func:(Course)->Unit)
    {
        courseClickListener = func
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyCourseListAdapter.CoursViewHolder {
       return CoursViewHolder(MylistItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyCourseListAdapter.CoursViewHolder, position: Int) {
        val course = getItem(position)
        holder.bind(course)
    }
}