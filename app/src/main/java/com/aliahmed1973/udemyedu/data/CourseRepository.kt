package com.aliahmed1973.udemyedu.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aliahmed1973.udemyedu.data.CoursePagingSource
import com.aliahmed1973.udemyedu.database.*
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.CourseNote
import com.aliahmed1973.udemyedu.model.Review
import com.aliahmed1973.udemyedu.network.NetworkReviewContainer
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asReviewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlin.math.log

private const val TAG = "CourseRepository"

@OptIn(ExperimentalPagingApi::class)
class CourseRepository(
    private val db: CourseDatabase,
    private val service: Service,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {


    fun getAllCourses(): Flow<PagingData<DBCourseWithInstructor>> {
        return Pager(config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE
        , enablePlaceholders = false,
            maxSize = 100
        ),
            remoteMediator = CourseRemoteMediator(service,db),
            pagingSourceFactory = { db.courseDao().getCourses()}).flow
    }


     fun getCourseReviews(courseId: Int): Flow<PagingData<DBReview>> {
        return Pager(config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE
            , enablePlaceholders = false,
            maxSize = 100
        ),
            remoteMediator = ReviewRemoteMediator(courseId,service,db),
            pagingSourceFactory = { db.reviewDao().getReviews(courseId)}).flow
    }


    suspend fun insertCourseToMylist(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                db.courseDao().insertCourse(course.asDatabaseCourse())
                db.courseDao().insertCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
            } catch (e: Exception) {
                Log.e(TAG, "insertCourseToMylist: ${e.message}")
            }

        }
    }

    suspend fun insertCourseInstructorToMylist(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                db.courseDao().insertCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
            } catch (e: Exception) {
                Log.e(TAG, "insertCourseInstructorToMylist: ${e.message}")
            }

        }
    }

    suspend fun insertNoteToMylistCourse(courseNote: CourseNote, courseId: Int) {
        withContext(coroutineDispatcher)
        {
            try {
                db.courseDao().insertCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    suspend fun UpdateOldNote(courseNote: CourseNote, courseId: Int) {
        withContext(coroutineDispatcher)
        {
            try {
                db.courseDao().updateCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    fun getMyCourseslist(): Flow<List<Course>> {
        return db.courseDao().getFavoritesCourses().map {
            it.asCourseModel()
        }
    }

    fun getMyCourseById(id: Int): Flow<Course?> {
        return db.courseDao().getCourseByID(id).map {
            it?.asCourseModel()
        }
    }

    fun getNotesById(id: Int): Flow<List<CourseNote?>?> {
        return db.courseDao().getNotesByCourseId(id).map {
            it.asNotesModel()
        }
    }


    suspend fun deleteCourseFromList(course: Course) {
        withContext(coroutineDispatcher)
        {
            try {
                db.courseDao().deleteCourse(course.asDatabaseCourse())
                db.courseDao().deleteCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
                course.courseNote?.asDBNotes(course.id)
                    ?.let {
                        db.courseDao().deleteCourseNotes(it)
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
                db.courseDao().deleteCourseNote(courseNote.asDBNote(courseId))
            } catch (e: Exception) {
                Log.e(TAG, "deleteNoteFromList: ${e.message}")
            }
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}