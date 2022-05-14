package com.aliahmed1973.udemyedu.ui.coursedetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.databinding.ReviewItemBinding
import com.aliahmed1973.udemyedu.model.Review

class ReviewAdapter:ListAdapter<Review,ReviewAdapter.ReviewViewHolder>(DiffCallback) {


    object DiffCallback : DiffUtil.ItemCallback<Review>(){
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id==newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }

    inner class ReviewViewHolder(private var binding:ReviewItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review?) {
            binding.tvUserName.text = review?.reviewUser?.name
            binding.rating.rating = review?.rating!!
            binding.tvContent.text= review.content
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewAdapter.ReviewViewHolder {
      return ReviewViewHolder(ReviewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        val review=getItem(position)
        holder.bind(review)
    }
}