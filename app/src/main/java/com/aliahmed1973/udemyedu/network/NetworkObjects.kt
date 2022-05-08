package com.aliahmed1973.udemyedu.network

import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.model.CourseInstructor
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class NetworkCoursesContainer(@Json(name = "results") val courses: List<NetworkCourse>,@Json(name = "count")val count:Int)

@JsonClass(generateAdapter = true)
data class NetworkCourseInstructor(
    @Json(name = "display_name")
    val name: String,
    @Json(name = "job_title")
    val jopTitle: String?,
    @Json(name = "image_100x100")
    val instructorImage: String,
    @Json(name = "url")
    val url: String
)

@JsonClass(generateAdapter = true)
data class NetworkCourse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String,
    @Json(name = "is_paid")
    val isPaid: Boolean,
    @Json(name = "price")
    val price: String,
    @Json(name = "image_480x270")
    val courseImage: String,
    @Json(name = "published_title")
    val publishedTitle: String,
    @Json(name = "headline")
    val headLine: String,
    @Json(name = "visible_instructors")
    val instructor: List<NetworkCourseInstructor>
)

fun NetworkCoursesContainer.asCourseModel(): List<Course> {
    return courses.map {
       val courseInstructor= it.instructor.map {
           CourseInstructor(name = it.name, jopTitle = it.jopTitle?:"", instructorImage = it.instructorImage, url = it.url)
       }
        Course(
            id = it.id, title = it.title, url = it.url,
            isPaid = it.isPaid, price = it.price, courseImage = it.courseImage,
            publishedTitle = it.publishedTitle,
            headLine = it.headLine,
            instructor = courseInstructor,
            courseNote = null,
            isAddedToMylist = false
        )
    }
}
