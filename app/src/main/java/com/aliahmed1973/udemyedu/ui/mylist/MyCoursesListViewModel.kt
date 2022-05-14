package com.aliahmed1973.udemyedu.ui.mylist

import androidx.lifecycle.*
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.model.Course
import kotlinx.coroutines.launch

class MyCoursesListViewModel(private val repository: CourseRepository) : ViewModel() {

    var myListCourses: LiveData<List<Course>> = repository.getMyCourseslist().asLiveData()


    fun removeCourseFromList(course: Course) {
        viewModelScope.launch {
            repository.deleteCourseFromList(course)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (MyCoursesListViewModel(repository) as T)
        }
    }
}