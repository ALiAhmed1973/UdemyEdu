package com.aliahmed1973.udemyedu.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aliahmed1973.udemyedu.databinding.CoursesLoadStateFooterViewItemBinding

class CoursesLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CoursesLoadStateAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CoursesLoadStateFooterViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
init {
    binding.buttonRetry.setOnClickListener {
        retry.invoke()
    }
}

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState is LoadState.Error
                textViewError.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(
        holder: CoursesLoadStateAdapter.ViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): CoursesLoadStateAdapter.ViewHolder {
        val binding = CoursesLoadStateFooterViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
}