package foodie.app.rubikkube.foodie.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import foodie.app.rubikkube.foodie.databinding.FragmentFeaturedFeedsBinding

class FeaturedFeedsFragment : Fragment() {
    private val feedAdapter: FeedAdapter = FeedAdapter()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentFeaturedFeedsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.binding = FragmentFeaturedFeedsBinding.inflate(inflater, container, false)

        this.binding.featuredFeeds.apply {
            this.adapter = feedAdapter
            this.layoutManager = LinearLayoutManager(this.context)
        }

        this.loadData()

        this.binding.featuredFeedsRefresh.setOnRefreshListener {
            this.viewModel.invalidate()
            this.loadData()
        }

        return this.binding.root
    }

    private fun loadData() {
        this.viewModel = ViewModelProvider(this).get(FeaturedFeedsViewModel::class.java)

        this.viewModel.feedPagedList?.observe(viewLifecycleOwner, Observer {
            if (this.binding.homeFeedsProgress.isVisible)
                this.binding.homeFeedsProgress.visibility = View.GONE

            if (this.binding.featuredFeedsRefresh.isRefreshing)
                this.binding.featuredFeedsRefresh.isRefreshing = false

            feedAdapter.submitList(it)
        })
    }

    fun scrollToTop() {
        this.binding.featuredFeeds.smoothScrollToPosition(0)
    }
}
