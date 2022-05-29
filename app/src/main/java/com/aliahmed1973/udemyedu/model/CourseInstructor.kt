package com.aliahmed1973.udemyedu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseInstructor(
    val name: String,
    val jopTitle: String,
    val instructorImage: String,
    val url: String
):Parcelable {
}