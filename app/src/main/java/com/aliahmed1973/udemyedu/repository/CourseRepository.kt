package com.aliahmed1973.udemyedu.repository

import android.util.Log
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.network.CourseApi
import com.aliahmed1973.udemyedu.network.asCourseModel

private const val TAG = "CourseRepository"
class CourseRepository {
    suspend fun getCoursesFromServer(page:Int):List<Course>
    {
        return try {
          val networkresponse=  CourseApi.service.getCourses(page)
            networkresponse.asCourseModel()
        }catch (e:Exception)
        {
            Log.e(TAG, "getCoursesFromServer: "+e.message )
            emptyList()
        }
    }
}