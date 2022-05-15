package com.aliahmed1973.udemyedu

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aliahmed1973.udemyedu.ui.coursedetails.ReviewAdapter
import com.aliahmed1973.udemyedu.model.Review

//@BindingAdapter("listData")
//fun bindRecyclerView(recyclerView: RecyclerView, data: List<Course>?) {
//    val adapter = recyclerView.adapter as CoursesAdapter
//    adapter.submitData(data)
//}

//@BindingAdapter("listReviewData")
//fun bindReviewRecyclerView(recyclerView: RecyclerView, data: List<Review>?) {
//    val adapter = recyclerView.adapter as ReviewAdapter
//    data.let { adapter.submitList(data) }
//}

@BindingAdapter("imgUrl")
fun bindImage(imageView: ImageView,imgUrl:String?)
{
    imageView.load(imgUrl)
}

@BindingAdapter("isAdded")
fun bindIconImage(imageview: ImageView,isAdded:Boolean)
{
    if(isAdded) {
        imageview.setImageResource(R.drawable.ic_baseline_bookmark_24)
    }
    else {
        imageview.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
    }

}