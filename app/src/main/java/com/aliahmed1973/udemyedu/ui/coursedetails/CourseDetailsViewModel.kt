package com.aliahmed1973.udemyedu.ui.coursedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CourseDetailsViewModel"


class CourseDetailsViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courseDetails = MutableStateFlow<Course?>(null)
    val courseDetails = _courseDetails.asStateFlow().stateIn(scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null)

    private val _courseId = MutableStateFlow(0)


    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val courseReview = _courseId.flatMapLatest {
        repository.getCourseReviews(it)
    }


    @kotlinx.coroutines.ExperimentalCoroutinesApi
//    val databaseCourse: StateFlow<Course?> = _courseId.flatMapLatest {
//        repository.getMyCourseById(it)
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = null
//    )


    fun checkCourseInDatabase(course: Course) {
        _courseId.value = course.id
        setCourse(course)
    }


     fun setCourse(course: Course) {
        _courseDetails.value=course
    }

    fun addOrRemoveCourseFromList() {
        viewModelScope.launch {
            _courseDetails.collectLatest {
                it?.let {
                    if (it.isAddedToMylist) {
                        it.isAddedToMylist = false
                        repository.deleteCourseFromList(it)
                    } else {
                        Log.d(TAG, "addOrRemoveCourseFromList: $it")
                        repository.insertCourseToMylist(it)
                        repository.insertCourseInstructorToMylist(it)
                        it.isAddedToMylist = true
                    }
                }
            }

        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (CourseDetailsViewModel(repository) as T)
        }
    }
}