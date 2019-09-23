package com.abnormallydriven.koinrepobrowser.repos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.abnormallydriven.koinrepobrowser.R
import com.abnormallydriven.koinrepobrowser.common.GithubApi
import com.abnormallydriven.koinrepobrowser.common.GithubRepository
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.databinding.FragmentRepositoryListBinding
import com.abnormallydriven.koinrepobrowser.repodetails.RepositoryDetailsFragmentFactory
import com.google.gson.Gson
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryListFragment : Fragment() {

    private var modelSubscription: Disposable? = null

    private lateinit var binding: FragmentRepositoryListBinding

    private lateinit var viewModel: RepositoryListFragmentViewModel

    private lateinit var repositoryAdapter: RepositoryAdapter

    private lateinit var computationScheduler: Scheduler

    private lateinit var uiScheduler: Scheduler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(RepositoryListFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val diffCallback = RepositoryDiffUtilItemCallback()

        repositoryAdapter = RepositoryAdapter(diffCallback)

        computationScheduler = Schedulers.computation()

        uiScheduler = AndroidSchedulers.mainThread()

        if (savedInstanceState == null) {

            val gson = Gson()

            val retrofitClient = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

            val githubApi = retrofitClient.create(GithubApi::class.java)

            val githubRepository = GithubRepository(githubApi)

            viewModel.initialize(githubRepository, Schedulers.io(), AndroidSchedulers.mainThread())
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.repositoriesRecyclerView.adapter = repositoryAdapter
    }


    override fun onStart() {
        super.onStart()

        viewModel.fetchData()

        modelSubscription = viewModel.modelObservable
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