package foodie.app.rubikkube.foodie.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.databinding.FragmentHomeBinding
import foodie.app.rubikkube.foodie.databinding.FragmentNewFeedsBinding

class NewFeedsFragment : Fragment() {
    private val feedAdapter: FeedAdapter = FeedAdapter()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentNewFeedsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.binding = FragmentNewFeedsBinding.inflate(inflater, container, false)

        this.binding.newFeeds.apply {
            this.adapter = feedAdapter
            this.layoutManager = LinearLayoutManager(this.context)
        }

        this.loadData()

        this.binding.newFeedsRefresh.setOnRefreshListener {
            this.viewModel.invalidate()
            this.loadData()
        }

        return this.binding.root
    }

    private fun loadData() {
        this.viewModel = ViewModelProvider(this).get(NewFeedsViewModel::class.java)

        this.viewModel.feedPagedList?.observe(viewLifecycleOwner, Observer {
            if (this.binding.homeFeedsProgress.isVisible)
                this.binding.homeFeedsProgress.visibility = View.GONE

            if (this.binding.newFeedsRefresh.isRefreshing)
                this.binding.newFeedsRefresh.isRefreshing = false

            feedAdapter.submitList(it)
        })
    }

    fun scrollToTop() {
        this.binding.newFeeds.smoothScrollToPosition(0)
    }
}