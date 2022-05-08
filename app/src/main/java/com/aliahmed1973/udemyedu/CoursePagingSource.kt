package com.aliahmed1973.udemyedu

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aliahmed1973.udemyedu.model.Course
import com.aliahmed1973.udemyedu.network.Service
import com.aliahmed1973.udemyedu.network.asCourseModel
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "CoursePagingSource"
private const val COURSE_STARTING_PAGE_INDEX =1
class CoursePagingSource(private val service:Service) :PagingSource<Int,Course>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Course> {
        val position = params.key ?: COURSE_STARTING_PAGE_INDEX

        return try {
            val response =  service.getCourses(position,params.loadSize)
            val courses = response.asCourseModel()
            Log.d(TAG, "load: ${courses}")
            LoadResult.Page(courses,
            prevKey = if (position==COURSE_STARTING_PAGE_INDEX) null else position-1,
            nextKey = if(courses.isEmpty()) null else position+1)
        }catch (e:IOException)
        {
            LoadResult.Error(e)
        }catch (e:HttpException)
        {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Course>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}