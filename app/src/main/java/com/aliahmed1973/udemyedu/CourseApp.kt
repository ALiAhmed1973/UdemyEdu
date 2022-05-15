package com.aliahmed1973.udemyedu

import android.app.Application
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.database.CourseDatabase
import com.aliahmed1973.udemyedu.network.CourseApi

class CourseApp:Application() {

    private val courseDatabase by lazy {
        CourseDatabase.getDatabase(this)
    }

    private val courseService = CourseApi.service

    val repository by lazy { CourseRepository(courseDatabase.courseDao(),courseService) }
}