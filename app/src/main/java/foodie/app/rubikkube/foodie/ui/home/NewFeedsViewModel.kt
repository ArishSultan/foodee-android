package foodie.app.rubikkube.foodie.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import foodie.app.rubikkube.foodie.models.feed.Feed
import foodie.app.rubikkube.foodie.models.feed.FeedDataSourceFactory

class NewFeedsViewModel: HomeViewModel() {
    override var feedPagedList: LiveData<PagedList<Feed>>? = null
    override var liveDataSource: LiveData<PageKeyedDataSource<Long, Feed>>? = null

    private val feedDataSourceFactory: FeedDataSourceFactory = FeedDataSourceFactory(false)

    init {
        this.liveDataSource = feedDataSourceFactory.getFeedLiveDataSource()

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPrefetchDistance(4)
                .setPageSize(20)
                .build()

        this.feedPagedList = LivePagedListBuilder(feedDataSourceFactory, config).build()
    }

    override fun invalidate() {
        this.feedDataSourceFactory.invalidate()
    }
}