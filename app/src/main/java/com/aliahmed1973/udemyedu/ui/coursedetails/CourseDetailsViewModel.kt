package com.aliahmed1973.udemyedu.ui.coursedetails

import android.util.Log
import androidx.lifecycle.*
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.Review
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "CourseDetailsViewModel"


class CourseDetailsViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courseDetails = MutableLiveData<Course>()
    val courseDetails: LiveData<Course>
        get() = _courseDetails

    private var _courseId = MutableStateFlow(0)




    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val courseReview: Flow<List<Review>> = _courseId.flatMapLatest {
           repository.getCourseReviewFromServer(it)
    }


    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val databaseCourse: Flow<Course?> = _courseId.flatMapLatest {
        repository.getMyCourseById(it)
    }


    fun checkCourseInDatabase(course: Course) {
        viewModelScope.launch {
            _courseId.emit(course.id)
        }
    }


    fun setCourse(course: Course) {
        _courseDetails.postValue(course)
    }

    fun addOrRemoveCourseFromList() {
        Log.d(TAG, "setCourseDetails: " + courseDetails.value)
        viewModelScope.launch {
            _courseDetails.value?.let {
                if (it.isAddedToMylist) {
                    repository.deleteCourseFromList(it)
                } else {
                    it.isAddedToMylist = true
                    repository.insertCourseToMylist(it)
                    repository.insertCourseInstructorToMylist(it)
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