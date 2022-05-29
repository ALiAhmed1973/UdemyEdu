package com.aliahmed1973.udemyedu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val id: Int,

    val title: String,

    val url: String,

    val isPaid: Boolean,

    val price: String,

    val courseImage: String,

    val publishedTitle: String,

    val headLine: String,

    val instructor: List<CourseInstructor>,

    val courseNote:List<CourseNote?>?,

    var isAddedToMylist:Boolean,

    val trackingId:String
) : Parcelable
