package com.aliahmed1973.udemyedu.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aliahmed1973.udemyedu.database.CourseDatabase
import com.aliahmed1973.udemyedu.database.DBCourseWithInstructor
import com.aliahmed1973.udemyedu.database.DatabaseCourseInstructor
import com.aliahmed1973.udemyedu.database.RemoteKeys
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asDBCourses
import retrofit2.HttpException
import java.io.IOException


private const val COURSE_STARTING_PAGE_INDEX = 1
private const val TAG = "CourseRemoteMediator"

@ExperimentalPagingApi
class CourseRemoteMediator(private val service: Service, private val db: CourseDatabase) :
    RemoteMediator<Int, DBCourseWithInstructor>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBCourseWithInstructor>
    ): MediatorResult {


        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: COURSE_STARTING_PAGE_INDEX
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
            val response = service.getCourses(page, state.config.pageSize)
            val netCourses = response.courses
            val endOfPaginationReached = netCourses.isEmpty()


            val dbCourses = response.asDBCourses()
            val dbCoursesInstructors: List<List<DatabaseCourseInstructor>> = netCourses.map { courses ->
                courses.instructor.map {
                    DatabaseCourseInstructor(
                        name = it.name,
                        jopTitle = it.jopTitle ?: "",
                        instructorImage = it.instructorImage,
                        url = it.url,
                        mylistId = courses.id
                    )
                }
            }


            val prevKey = if (page == COURSE_STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().clearRemoteKeys()
                    db.courseDao().clearCourses()
                    db.courseDao().clearCourseInstructor()
                }

                val keys = dbCourses.map {
                    RemoteKeys(trackingId = it.trackingId, prevKey = prevKey, nextKey = nextKey)
                }
                Log.d(TAG, "load: $keys")
                db.remoteKeysDao().insertAll(keys)
                db.courseDao().insertAllCourses(dbCourses)
                dbCoursesInstructors.forEach {
                    db.courseDao().insertAllInstructors(it)
                }

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DBCourseWithInstructor>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { course ->
                // Get the remote keys of the last item retrieved
                db.remoteKeysDao().remoteKeysRepoId(course.mylistCourse.trackingId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DBCourseWithInstructor>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { course ->
                // Get the remote keys of the first items retrieved
                db.remoteKeysDao().remoteKeysRepoId(course.mylistCourse.trackingId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, DBCourseWithInstructor>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.mylistCourse?.trackingId?.let { id ->
                db.remoteKeysDao().remoteKeysRepoId(trackingId = id)
            }
        }
    }
}