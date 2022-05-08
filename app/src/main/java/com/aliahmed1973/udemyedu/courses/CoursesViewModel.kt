package com.aliahmed1973.udemyedu.courses

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.repository.CourseRepository
import kotlinx.coroutines.launch

private const val TAG = "CoursesViewModel"
class CoursesViewModel(private val courseRepository: CourseRepository) : ViewModel() {



    val courses = courseRepository.getCoursesFromServer().cachedIn(viewModelScope)


    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository): ViewModelProvider.NewInstanceFactory()
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (CoursesViewModel(repository) as T)
        }
    }
}

