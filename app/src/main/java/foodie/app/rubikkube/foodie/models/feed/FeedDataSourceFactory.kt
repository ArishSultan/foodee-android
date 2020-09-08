package foodie.app.rubikkube.foodie.models.feed

import androidx.paging.DataSource
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource

class FeedDataSourceFactory(private var fetchFeatured: Boolean = false) : DataSource.Factory<Long, Feed>() {
    private lateinit var dataSource: FeedDataSource
    private val feedLiveDataSource: MutableLiveData<PageKeyedDataSource<Long, Feed>> = MutableLiveData()

    override fun create(): DataSource<Long, Feed> {
        this.dataSource = FeedDataSource(fetchFeatured)
        feedLiveDataSource.postValue(this.dataSource)

        return this.dataSource
    }

    fun getFeedLiveDataSource(): MutableLiveData<PageKeyedDataSource<Long, Feed>> {
        return this.feedLiveDataSource
    }

    fun invalidate() {
        this.dataSource.invalidate()
    }
}