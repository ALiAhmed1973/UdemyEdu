package com.aliahmed1973.udemyedu.repository

import android.util.Log
import com.aliahmed1973.udemyedu.database.*
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.CourseNote
import com.aliahmed1973.udemyedu.model.Review
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asCourseModel
import com.aliahmed1973.udemyedu.network.asReviewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val TAG = "CourseRepository"
class CourseRepository(private val courseDao: CourseDao,
                       private val service:Service,
private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) {
    var count =0
    suspend fun getCoursesFromServer(page:Int):List<Course>
    {
        return try {
          val networkresponse=  service.getCourses(page)
            count= networkresponse.count
            networkresponse.asCourseModel()
        }catch (e:Exception)
        {
            Log.e(TAG, "getCoursesFromServer: "+e.message )
            emptyList()
        }
    }

    suspend fun getCourseReviewFromServer(courseId:Int):List<Review>
    {
        return try {
            val networkResponse = service.getReviews(courseId)
            Log.d(TAG, "getCourseReviewFromServer: "+"${networkResponse}")
            networkResponse.asReviewModel()
        }catch (e:Exception)
        {
            Log.e(TAG, "getCourseReviewFromServer: "+e.message )
            emptyList()
        }
    }

    suspend fun insertCourseToMylist(course: Course) {
        withContext(ioDispatcher)
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
        withContext(ioDispatcher)
        {
            try {
                courseDao.insertCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
            } catch (e: Exception) {
                Log.e(TAG, "insertCourseInstructorToMylist: ${e.message}")
            }

        }
    }

    suspend fun insertNoteToMylistCourse(courseNote: CourseNote, courseId:Int)
    {
        withContext(ioDispatcher)
        {
            try {
                courseDao.insertCourseNote(courseNote.asDBNote(courseId))
            }catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    suspend fun UpdateOldNote(courseNote: CourseNote, courseId: Int) {
        withContext(ioDispatcher)
        {
            try {
                courseDao.updateCourseNote(courseNote.asDBNote(courseId))
            }catch (e: Exception) {
                Log.e(TAG, "insertNotesToCourse: ${e.message}")
            }
        }
    }

    fun getMyCourseslist():Flow<List<Course>>
    {
        return courseDao.getCourses().map {
            it.asCourseModel()
        }
    }

    fun getMyCourseById(id:Int):Flow<Course?>
    {
        return courseDao.getCourseByID(id).map {
            it?.asCourseModel()
        }
    }

    fun getNotesById(id:Int):Flow<List<CourseNote?>?>
    {
        return courseDao.getNotesByCourseId(id).map{
            it.asNotesModel()
        }
    }



    suspend fun deleteCourseFromList(course: Course)
    {
        withContext(ioDispatcher)
        {
            try {
                courseDao.deleteCourse(course.asDatabaseCourse())
                courseDao.deleteCourseInstructor(course.instructor[0].asDBCourseInstructor(course.id))
                course.courseNote?.asDBNotes(course.id)
                    ?.let {
                        courseDao.deleteCourseNotes(it) }
            }catch (e:Exception)
            {
                Log.e(TAG, "deleteCourseFromList: ${e.message}")
            }
        }
    }


    suspend fun deleteNoteFromList(courseNote: CourseNote,courseId: Int)
    {
        withContext(ioDispatcher)
        {
            try {
                courseDao.deleteCourseNote(courseNote.asDBNote(courseId))
            }catch (e:Exception)
            {
                Log.e(TAG, "deleteNoteFromList: ${e.message}")
            }
        }
    }
}