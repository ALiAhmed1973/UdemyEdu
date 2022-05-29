package com.aliahmed1973.udemyedu.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aliahmed1973.udemyedu.data.CourseRepository

private const val TAG = "CoursesViewModel"

class CoursesViewModel(private val courseRepository: CourseRepository) : ViewModel() {


    val courses = courseRepository.getAllCourses().cachedIn(viewModelScope)


    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (CoursesViewModel(repository) as T)
        }
    }
}

