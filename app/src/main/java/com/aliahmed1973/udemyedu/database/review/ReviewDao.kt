package com.aliahmed1973.udemyedu.database.review

import androidx.paging.PagingSource
import androidx.room.*
import com.aliahmed1973.udemyedu.database.DBReview

@Dao
interface ReviewDao {


    @Query("SELECT * FROM reviews WHERE courseId = :courseId")
    fun getReviews(courseId:Int): PagingSource<Int, DBReview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviews(courseReviews: List<DBReview>)

    @Query("DELETE FROM reviews")
    suspend fun clearReviews()
}