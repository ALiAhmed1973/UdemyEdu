package com.aliahmed1973.udemyedu.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aliahmed1973.udemyedu.database.CourseDatabase
import com.aliahmed1973.udemyedu.database.DBReview
import com.aliahmed1973.udemyedu.database.review.RemoteReviewKeys
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asDBreview
import retrofit2.HttpException
import java.io.IOException

private const val REVIEW_STARTING_PAGE_INDEX = 1
private const val TAG = "ReviewRemoteMediator"
@ExperimentalPagingApi
class ReviewRemoteMediator(private val courseId:Int,private val service: Service, private val db: CourseDatabase):RemoteMediator<Int, DBReview>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBReview>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: REVIEW_STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                nextKey
            }
        }
        try {
            val response = service.getReviews(courseId,page, state.config.pageSize)
            Log.d(TAG, "load: $response")
            val reviews = response.reviews
            val endOfPaginationReached = reviews.isEmpty()


            val dbReviews = response.asDBreview(courseId)


            val prevKey = if (page == REVIEW_STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    db.remoteReviewKeysDao().clearRemoteKeys()
                    db.reviewDao().clearReviews()
                }

                val keys = dbReviews.map {
                    RemoteReviewKeys(reviewId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                db.remoteReviewKeysDao().insertAll(keys)
                db.reviewDao().insertAllReviews(dbReviews)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }




    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DBReview>): RemoteReviewKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { review->
                // Get the remote keys of the last item retrieved
                db.remoteReviewKeysDao().remoteKeysRepoId(review.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DBReview>): RemoteReviewKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { review ->
                // Get the remote keys of the first items retrieved
                db.remoteReviewKeysDao().remoteKeysRepoId(review.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, DBReview>
    ): RemoteReviewKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.remoteReviewKeysDao().remoteKeysRepoId(reviewId = id)
            }
        }
    }
}