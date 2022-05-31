package com.aliahmed1973.udemyedu.database

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.*
import com.aliahmed1973.udemyedu.database.review.RemoteReviewKeys
import com.aliahmed1973.udemyedu.database.review.RemoteReviewKeysDao
import com.aliahmed1973.udemyedu.database.review.ReviewDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Transaction
    @Query("SELECT * FROM mylist_courses")
    fun getFavoritesCourses(): Flow<List<DBCourseWithInstructor>>

    @Transaction
    @Query("SELECT * FROM mylist_courses")
    fun getCourses(): PagingSource<Int, DBCourseWithInstructor>

    @Transaction
    @Query("SELECT * FROM mylist_courses WHERE id = :id")
    fun getCourseByID(id: Int): Flow<DBCourseWithInstructor?>

    @Query("SELECT * FROM DatabaseCourseNote WHERE mylistCourseId = :id")
    fun getNotesByCourseId(id: Int): Flow<List<DatabaseCourseNote?>>


    @Insert
    fun insertCourse(course: DatabaseMylistCourse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCourses(course: List<DatabaseMylistCourse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInstructors(courseInstructors: List<DatabaseCourseInstructor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseInstructor(CourseInstructor: DatabaseCourseInstructor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseNote(Note: DatabaseCourseNote)

    @Update
    fun updateCourseNote(Note: DatabaseCourseNote)

    @Delete
    fun deleteCourse(course: DatabaseMylistCourse)

    @Delete
    fun deleteCourseInstructor(course: DatabaseCourseInstructor)

    @Delete
    fun deleteCourseNotes(Notes: List<DatabaseCourseNote?>)

    @Delete
    fun deleteCourseNote(Note: DatabaseCourseNote)

    @Query("DELETE FROM mylist_courses")
    suspend fun clearCourses()

    //    @Query("DELETE FROM DatabaseCourseNote")
//    suspend fun clearCoursesNotes()
    @Query("DELETE FROM course_instructor")
    suspend fun clearCourseInstructor()
}

@Database(
    entities = [DatabaseMylistCourse::class, DatabaseCourseInstructor::class,
        DatabaseCourseNote::class, RemoteKeys::class,DBReview::class,RemoteReviewKeys::class],
    version = 1,
    exportSchema = false
)
abstract class CourseDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun reviewDao(): ReviewDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun remoteReviewKeysDao(): RemoteReviewKeysDao

    companion object {
        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context,
                    CourseDatabase::class.java,
                    "courses"
                ).build()
                INSTANCE = instance
                instance
            }

        }

    }
}



