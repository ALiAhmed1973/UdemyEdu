package com.aliahmed1973.udemyedu.ui.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.databinding.CourseItemBinding
import com.aliahmed1973.udemyedu.model.Course

private const val TAG = "CoursesAdapter"
class CoursesAdapter(val courseClickListener:CourseClickListener):PagingDataAdapter<Course,CoursesAdapter.CourseViewHolder>(DiffCallback) {

    object DiffCallback :DiffUtil.ItemCallback<Course>(){
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }


    inner class CourseViewHolder(private var binding: CourseItemBinding):RecyclerView.ViewHolder(binding.root) {
        lateinit var itemCourse:Course
        fun bind(course: Course)
        {
            itemCourse=course
            binding.course=course
        }
        init {
            binding.cardViewCourse.setOnClickListener {
                courseClickListener.onItemClick(itemCourse)
            }
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
        course?.let { holder.bind(it) }
    }

    class CourseClickListener(val listenerFun:(Course)->Unit){
        fun onItemClick(course: Course)=listenerFun(course)
    }
}