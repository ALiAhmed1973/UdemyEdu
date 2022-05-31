package com.aliahmed1973.udemyedu.database.review

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteReviewKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteReviewKeys>)

    @Query("SELECT * FROM remote_review_keys WHERE reviewId = :reviewId")
    suspend fun remoteKeysRepoId(reviewId: Long): RemoteReviewKeys?

    @Query("DELETE FROM remote_review_keys")
    suspend fun clearRemoteKeys()

}