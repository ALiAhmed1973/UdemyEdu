package com.aliahmed1973.udemyedu.database

import androidx.room.*
import com.aliahmed1973.udemyedu.model.*
import java.util.*


@Entity(tableName = "mylist_courses")
data class DatabaseMylistCourse(
     val id: Int,

    @ColumnInfo(name = "title") val title: String,

    @ColumnInfo(name = "course_url") val url: String,

    @ColumnInfo(name = "is_paid") val isPaid: Boolean,

    @ColumnInfo(name = "price") val price: String,

    @ColumnInfo(name = "published_title") val publishedTitle: String,

    @ColumnInfo(name = "course_image") val courseImage: String,

    @ColumnInfo(name = "headline") val headLine: String,

    @ColumnInfo(name = "is_added") val isAdded: Boolean,

     @PrimaryKey @ColumnInfo(name = "tracking_id")
     val  trackingId:String

)

@Entity(tableName = "course_instructor")
data class DatabaseCourseInstructor(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String,
    @ColumnInfo(name = "jop_title") val jopTitle: String,
    @ColumnInfo(name = "instructor_image") val instructorImage: String,
     @ColumnInfo(name = "instructor_url") val url: String,
    val mylistId: Int
)

@Entity
data class DatabaseCourseNote(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "note_text") val noteText: String,
    val mylistCourseId: Int
)

data class DBCourseWithInstructor(
    @Embedded val mylistCourse: DatabaseMylistCourse,
    @Relation(
        parentColumn = "id", entityColumn = "mylistId"
    )
    val Instructors: List<DatabaseCourseInstructor>,
    @Relation(
        parentColumn = "id", entityColumn = "mylistCourseId"
    )
    val courseNotes: List<DatabaseCourseNote?>
)

//data class DBCourseWithNotes(
//    @Embedded val mylistCourse: DatabaseMylistCourse,
//
//)
@Entity(tableName = "reviews")
data class DBReview (

    @PrimaryKey(autoGenerate = true)
    val dbId: Int=0,

    val id:Long,

    val content:String,

    val rating:Float,

    val createdTime:String,

    @Embedded
    val reviewUser: ReviewUser,

    val courseid: Int
)

fun Course.asDatabaseCourse(): DatabaseMylistCourse {
    return DatabaseMylistCourse(
        id = id,
        title = title,
        url = url,
        isPaid = isPaid,
        price = price,
        courseImage = courseImage,
        publishedTitle = publishedTitle,
        headLine = headLine,
        isAdded = isAddedToMylist,
        trackingId = trackingId
    )

}

fun CourseInstructor.asDBCourseInstructor(courseid: Int): DatabaseCourseInstructor {
    return DatabaseCourseInstructor(
        name = name,
        jopTitle = jopTitle,
        instructorImage = instructorImage,
        url = url,
        mylistId = courseid
    )
}

fun List<DBCourseWithInstructor>.asCourseModel(): List<Course> {
    return map {
        val instructors = it.Instructors.map {
            CourseInstructor(
                name = it.name,
                jopTitle = it.jopTitle,
                instructorImage = it.instructorImage,
                url = it.url
            )
        }

        val notes = it.courseNotes.asNotesModel()
        Course(
            id = it.mylistCourse.id,
            title = it.mylistCourse.title,
            url = it.mylistCourse.url,
            isPaid = it.mylistCourse.isPaid,
            price = it.mylistCourse.price,
            courseImage = it.mylistCourse.courseImage,
            publishedTitle = it.mylistCourse.publishedTitle,
            headLine = it.mylistCourse.headLine,
            instructor = instructors,
            courseNote = notes,
            isAddedToMylist = it.mylistCourse.isAdded,
            trackingId = it.mylistCourse.trackingId
        )
    }
}

fun DBCourseWithInstructor.asCourseModel(): Course {
    val instructors = Instructors.map {
        CourseInstructor(
            name = it.name,
            jopTitle = it.jopTitle,
            instructorImage = it.instructorImage,
            url = it.url
        )
    }

    val courseNotes = courseNotes.asNotesModel()
    return Course(
        id = mylistCourse.id,
        title = mylistCourse.title,
        url = mylistCourse.url,
        isPaid = mylistCourse.isPaid,
        price = mylistCourse.price,
        courseImage = mylistCourse.courseImage,
        publishedTitle = mylistCourse.publishedTitle,
        headLine = mylistCourse.headLine,
        instructor = instructors,
        courseNote = courseNotes,
        isAddedToMylist = mylistCourse.isAdded,
        trackingId = mylistCourse.trackingId
    )
}

fun List<DatabaseCourseNote?>.asNotesModel(): List<CourseNote?> {
    return map {
        it?.let {
            CourseNote(id=it.id, noteText = it.noteText)
        }

    }
}

fun List<CourseNote?>.asDBNotes(courseid: Int): List<DatabaseCourseNote?> {
    return map {
        it?.let {
            DatabaseCourseNote(id = it.id, noteText = it.noteText, mylistCourseId = courseid)
        }

    }
}

fun CourseNote.asDBNote(courseid: Int):DatabaseCourseNote {
    return DatabaseCourseNote(id=id,noteText = noteText, mylistCourseId = courseid)
}

fun DBReview.asReviewModel():Review
{
    return Review(id=id,content=content, rating = rating,createdTime=createdTime, reviewUser=reviewUser)
}

