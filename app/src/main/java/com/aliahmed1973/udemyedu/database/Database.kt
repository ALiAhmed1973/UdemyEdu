package com.aliahmed1973.udemyedu.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.aliahmed1973.udemyedu.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao{
    @Transaction
    @Query("SELECT * FROM mylist_courses")
    fun getCourses(): Flow<List<DBCourseWithInstructor>>

    @Transaction
    @Query("SELECT * FROM mylist_courses WHERE id = :id")
    fun getCourseByID(id:Int):Flow<DBCourseWithInstructor?>

    @Query("SELECT * FROM DatabaseCourseNote WHERE mylistCourseId = :id")
    fun getNotesByCourseId(id:Int):Flow<List<DatabaseCourseNote?>>


    @Insert
    fun insertCourse(course: DatabaseMylistCourse)

    @Insert
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
}

@Database(entities = [DatabaseMylistCourse::class,DatabaseCourseInstructor::class,DatabaseCourseNote::class],version=1, exportSchema = false)
abstract class CourseDatabase:RoomDatabase(){
    abstract fun courseDao():CourseDao

    companion object{
        @Volatile
        private  var INSTANCE: CourseDatabase? =null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this)
                {
                    val instance = Room.databaseBuilder(
                        context,
                        CourseDatabase::class.java,
                        "courses"
                    ).build()
                    INSTANCE=instance
                    instance
                }

        }

    }
}



