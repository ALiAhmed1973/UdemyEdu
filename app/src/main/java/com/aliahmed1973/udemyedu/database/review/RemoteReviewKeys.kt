package com.aliahmed1973.udemyedu.database.review

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_review_keys")
class RemoteReviewKeys(
    @PrimaryKey
    val reviewId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)