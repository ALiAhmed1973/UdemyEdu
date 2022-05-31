package com.aliahmed1973.udemyedu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Review (

    val id:Long,

    val content:String,

    val rating:Float,

    val createdTime:String,

    val reviewUser: ReviewUser
        ):Parcelable