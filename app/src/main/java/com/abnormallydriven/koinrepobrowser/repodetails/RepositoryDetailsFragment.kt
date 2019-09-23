package com.abnormallydriven.koinrepobrowser.repodetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProviders
import com.abnormallydriven.koinrepobrowser.common.GithubApi
import com.abnormallydriven.koinrepobrowser.common.GithubRepository
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.common.UserDetailsDto
import com.abnormallydriven.koinrepobrowser.databinding.FragmentRepoDetailsBinding
import com.google.gson.Gson
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryDetailsFragment : Fragment() {

    private lateinit var computationScheduler: Scheduler

    private lateinit var uiScheduler: Scheduler

    private lateinit var binding: FragmentRepoDetailsBinding

    private lateinit var viewModel: RepositoryDetailsFragmentViewModel

    private var modelSubscription: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this)[RepositoryDetailsFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        computationScheduler = Schedulers.computation()

        uiScheduler = AndroidSchedulers.mainThread()

        if(savedInstanceState == null){
            val gson = Gson()

            val retrofitClient = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

            val githubApi = retrofitClient.create(GithubApi::class.java)

            val githubRepository = GithubRepository(githubApi)

            val ioScheduler = Schedulers.io()

            viewModel.initialize(githubRepository, ioScheduler, uiScheduler)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val repositoryId = arguments?.getInt(REPOSITORY_ID_ARG) ?: 0
        val username = arguments?.getString(OWNER_USERNAME_ARG) ?: ""
        viewModel.fetchData(repositoryId, username)

        modelSubscription = viewModel.modelObservable
            .map{ RepositoryDetailsFragmentBindingModel(it.userDetailsDto, it.repositoryDto, it.isLoading, it.isError)}
            .subscribeOn(computationScheduler)
            .observeOn(uiScheduler)
            .subscribe({
                binding.model = it
            },{})
    }

    override fun onStop() {
        modelSubscription?.dispose()
        super.onStop()
    }

    companion object{
        const val REPOSITORY_ID_ARG = "arg:repositoryId"
        const val OWNER_USERNAME_ARG = "arg:username"
    }
}


class RepositoryDetailsFragmentBindingModel(val userDetailsDto: UserDetailsDto?,
                                            val repositoryDto: RepositoryDto?,
                                            val isLoading: Boolean,
                                            val isError: Boolean){

    val showLoadingView = isLoading
    val showErrorView = !isLoading && isError
    val showContentView = !isLoading && !isError

    val reponameText = repositoryDto?.name ?: ""
    val projectDescriptionText = repositoryDto?.description ?: ""
    val userAvatarUrl = userDetailsDto?.avatarUrl ?: ""
    val usernameText = userDetailsDto?.login ?: ""
    val ownerNameText = userDetailsDto?.name ?: ""
    val followerCountText = "${userDetailsDto?.followers ?: 0} followers"
    val followingText = "Following ${userDetailsDto?.following ?: 0}"
    val locationText = "Location: ${userDetailsDto?.location ?: "Planet Earth, probably..."}"
    val bioText = "Biography:\n ${userDetailsDto?.bio ?: "This user hasn't written a bio yet. How mysterious!"}"
}


class RepositoryDetailsFragmentFactory(){

    fun create(repositoryDto: RepositoryDto): Fragment{
        val bundle = Bundle()

        bundle.putInt(RepositoryDetailsFragment.REPOSITORY_ID_ARG, repositoryDto.id)
        bundle.putString(RepositoryDetailsFragment.OWNER_USERNAME_ARG, repositoryDto.ownerDto.login)

        val fragment = RepositoryDetailsFragment()
        fragment.arguments = bundle

        return fragment
    }


}