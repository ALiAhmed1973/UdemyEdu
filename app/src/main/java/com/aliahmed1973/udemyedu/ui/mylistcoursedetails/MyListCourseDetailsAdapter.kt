package com.aliahmed1973.udemyedu.ui.mylistcoursedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.databinding.MylistDetailsItemBinding
import com.aliahmed1973.udemyedu.model.CourseNote

class MyListCourseDetailsAdapter:ListAdapter<CourseNote,MyListCourseDetailsAdapter.NoteViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<CourseNote>(){
        override fun areItemsTheSame(oldItem: CourseNote, newItem: CourseNote): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: CourseNote, newItem: CourseNote): Boolean {
            return oldItem == newItem
        }

    }

    private lateinit var onItemClickListener:(CourseNote)->Unit

    inner class NoteViewHolder(private var binding:MylistDetailsItemBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var courseNote: CourseNote
        fun bind(noteCourse: CourseNote?) {
            binding.tvNote.text=noteCourse?.noteText
            noteCourse?.let {
                courseNote=it
            }
        }
        init {
            binding.tvNote.setOnClickListener {
                onItemClickListener(courseNote)
            }
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListCourseDetailsAdapter.NoteViewHolder {
        return NoteViewHolder(MylistDetailsItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(
        holder: MyListCourseDetailsAdapter.NoteViewHolder,
        position: Int
    ) {
        val noteCourse = getItem(position)
        holder.bind(noteCourse)
    }

    fun getCourseNote(func:(CourseNote)->Unit)
    {
        onItemClickListener=func
    }


}