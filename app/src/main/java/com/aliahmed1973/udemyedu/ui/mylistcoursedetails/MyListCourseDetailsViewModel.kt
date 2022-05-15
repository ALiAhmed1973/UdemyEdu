package com.aliahmed1973.udemyedu.ui.mylistcoursedetails

import androidx.lifecycle.*
import com.aliahmed1973.udemyedu.data.CourseRepository
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.CourseNote
import kotlinx.coroutines.launch

private const val TAG = "MyListCourseDetailsView"

class MyListCourseDetailsViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courseDetails = MutableLiveData<Course>()
    val courseDetails: LiveData<Course>
        get() = _courseDetails


    lateinit var courseNotes: LiveData<List<CourseNote?>?>


    var currentCourseNote: CourseNote? = null

    lateinit var courseNotesList: List<CourseNote?>

    fun setCourseDetails(course: Course) {
        _courseDetails.value = course
        viewModelScope.launch {
            courseNotes = repository.getNotesById(course.id).asLiveData()
        }

    }

    fun addNewNote(courseNote: CourseNote) {
        viewModelScope.launch {
            repository.insertNoteToMylistCourse(courseNote, _courseDetails.value!!.id)
        }
    }

    fun updateNote(courseNote: CourseNote) {
        viewModelScope.launch {
            repository.UpdateOldNote(courseNote, _courseDetails.value!!.id)
        }
    }

    fun deleteNote(deletedNotePos: Int) {
        val deletedNote = courseNotesList[deletedNotePos]
        viewModelScope.launch {
            repository.deleteNoteFromList(deletedNote!!, _courseDetails.value!!.id)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: CourseRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return (MyListCourseDetailsViewModel(repository) as T)
        }
    }
}