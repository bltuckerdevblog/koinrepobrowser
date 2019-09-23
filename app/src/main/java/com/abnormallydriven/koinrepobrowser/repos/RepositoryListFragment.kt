package com.abnormallydriven.koinrepobrowser.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abnormallydriven.koinrepobrowser.R
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.databinding.FragmentRepositoryListBinding
import com.abnormallydriven.koinrepobrowser.repodetails.RepositoryDetailsFragmentFactory
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class RepositoryListFragment : Fragment() {

    private var modelSubscription: Disposable? = null

    private lateinit var binding: FragmentRepositoryListBinding

    private val fragmentViewModel: RepositoryListFragmentViewModel by viewModel()

    private val repositoryAdapter: RepositoryAdapter by currentScope.inject()

    private val computationScheduler: Scheduler by inject(named("computation"))

    private val uiScheduler: Scheduler by inject(named("ui"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepositoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.repositoriesRecyclerView.adapter = repositoryAdapter
    }


    override fun onStart() {
        super.onStart()

        fragmentViewModel.fetchData()

        modelSubscription = fragmentViewModel.modelObservable
            .map { RepositoryListFragmentBindingModel(it.isLoading, it.isError, it.repositoryList) }
            .subscribeOn(computationScheduler)
            .observeOn(uiScheduler)
            .subscribe({
                binding.model = it
            }, {})
    }

    override fun onResume() {
        super.onResume()

        repositoryAdapter.itemClickHandler = this::openRepoDetailsFragment

    }

    override fun onPause() {
        repositoryAdapter.itemClickHandler = null
        super.onPause()
    }

    override fun onStop() {
        modelSubscription?.dispose()
        super.onStop()
    }

    private fun openRepoDetailsFragment(repositoryDto: RepositoryDto) {

        val fragmentFactory = RepositoryDetailsFragmentFactory()

        val fragment = fragmentFactory.create(repositoryDto)

        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("details")
                .commit()
        }
    }
}

data class RepositoryListFragmentBindingModel(
    val isLoading: Boolean,
    val isError: Boolean,
    val repositoryList: List<RepositoryDto>
) {

    val showLoadingView = isLoading
    val showErrorView = !isLoading && isError
    val showEmptyView = !isLoading && !isError && repositoryList.isEmpty()
    val showRepositoryListView = !isLoading && !isError && repositoryList.isNotEmpty()
}