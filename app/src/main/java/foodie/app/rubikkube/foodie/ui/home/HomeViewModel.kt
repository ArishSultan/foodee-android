package foodie.app.rubikkube.foodie.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import foodie.app.rubikkube.foodie.models.feed.Feed
import foodie.app.rubikkube.foodie.models.feed.FeedDataSource
import foodie.app.rubikkube.foodie.models.feed.FeedDataSourceFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class HomeViewModel : ViewModel() {
    abstract var feedPagedList: LiveData<PagedList<Feed>>?
    protected abstract var liveDataSource: LiveData<PageKeyedDataSource<Long, Feed>>?

    abstract fun invalidate()
}