package com.aliahmed1973.udemyedu.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aliahmed1973.udemyedu.data.CoursePagingSource
import com.aliahmed1973.udemyedu.database.*
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.CourseNote
import com.aliahmed1973.udemyedu.model.Review
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asReviewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

private const val TAG = "CourseRepository"

class CourseRepository(
    private val courseDao: CourseDao,
    private val service: Service,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    fun getCoursesFromServer(): Flow<PagingData<Course>> {
        return Pager(config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            maxSize = 100,
            enablePlaceholders = false
        ),
            pagingSourceFactory = { CoursePagingSource(service) }).flow
    }

    suspend fun getCourseReviewFromServer(courseId: Int): Flow<List<Review>> {
        return try {
            val networkResponse = service.getReviews(courseId)
            Log.d(TAG, "getCourseReviewFromServer: " + "${networkResponse}")
            withContext(coroutineDispatcher) {
                flow {
                    emit(  networkResponse.asReviewModel())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCourseReviewFromServer: " + e.message)
            flowOf()
        }
    }

    suspend fun insertCourseToMylist(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.insertCourse(course.asDatabaseCourse())
                courseDao.insertCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
            } catch (e: Exception) {
                Log.e(TAG, "insertCourseToMylist: ${e.message}")
            }

        }
    }

    suspend fun insertCourseInstructorToMylist(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.insertCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
            } catch (e: Exception) {
                Log.e(TAG, "insertCourseInstructorToMylist: ${e.message}")
            }

        }
    }

    suspend fun insertNoteToMylistCourse(courseNote: CourseNote, courseId: Int) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.insertCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    suspend fun UpdateOldNote(courseNote: CourseNote, courseId: Int) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.updateCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    fun getMyCourseslist(): Flow<List<Course>> {
        return courseDao.getCourses().map {
            it.asCourseModel()
        }
    }

    fun getMyCourseById(id: Int): Flow<Course?> {
        return courseDao.getCourseByID(id).map {
            it?.asCourseModel()
        }
    }

    fun getNotesById(id: Int): Flow<List<CourseNote?>?> {
        return courseDao.getNotesByCourseId(id).map {
            it.asNotesModel()
        }
    }


    suspend fun deleteCourseFromList(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.deleteCourse(course.asDatabaseCourse())
                courseDao.deleteCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
                course.courseNote?.asDBNotes(course.id)
                    ?.let {
                        courseDao.deleteCourseNotes(it)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "deleteCourseFromList: ${e.message}")
            }
        }
    }


    suspend fun deleteNoteFromList(courseNote: CourseNote, courseId: Int) {
        withContext(coroutineDispatcher)
        {
            try {
                courseDao.deleteCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "deleteNoteFromList: ${e.message}")
            }
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}