package com.aliahmed1973.udemyedu.ui.courses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.database.DBReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.math.log

private const val TAG = "CoursesViewModel"

class CoursesViewModel(courseRepository: CourseRepository) : ViewModel() {


    val courses = courseRepository.getAllCourses().cachedIn(viewModelScope)


    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (CoursesViewModel(repository) as T)
        }
    }
}

