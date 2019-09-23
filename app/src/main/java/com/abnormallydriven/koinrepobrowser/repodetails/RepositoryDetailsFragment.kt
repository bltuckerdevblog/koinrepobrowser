package com.abnormallydriven.koinrepobrowser.repodetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.abnormallydriven.koinrepobrowser.common.GithubRepository
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.common.UserDetailsDto
import com.abnormallydriven.koinrepobrowser.databinding.FragmentRepoDetailsBinding
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class RepositoryDetailsFragment : Fragment() {

    private val computationScheduler: Scheduler by inject(named("computation"))

    private val uiScheduler: Scheduler by inject(named("ui"))

    private val fragmentViewModel: RepositoryDetailsFragmentViewModel by viewModel()

    private lateinit var binding: FragmentRepoDetailsBinding

    private var modelSubscription: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val repositoryId = arguments?.getInt(REPOSITORY_ID_ARG) ?: 0
        val username = arguments?.getString(OWNER_USERNAME_ARG) ?: ""
        fragmentViewModel.fetchData(repositoryId, username)

        modelSubscription = fragmentViewModel.modelObservable
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