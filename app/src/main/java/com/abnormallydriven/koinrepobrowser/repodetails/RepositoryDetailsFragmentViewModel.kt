package com.abnormallydriven.koinrepobrowser.repodetails

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.abnormallydriven.koinrepobrowser.common.GithubRepository
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.common.UserDetailsDto
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class RepositoryDetailsFragmentViewModel : ViewModel() {

    private lateinit var githubRepository: GithubRepository

    private lateinit var ioScheduler: Scheduler

    private lateinit var uiScheduler: Scheduler

    private val modelRelay : BehaviorRelay<RepositoryDetailsFragmentModel> = BehaviorRelay.createDefault(
        RepositoryDetailsFragmentModel(null, null, true, false)
    )

    val modelObservable : Observable<RepositoryDetailsFragmentModel> = modelRelay

    private var hasFetchedData = false

    fun initialize(githubRepository: GithubRepository, ioScheduler: Scheduler, uiScheduler: Scheduler){
        this.githubRepository = githubRepository
        this.ioScheduler = ioScheduler
        this.uiScheduler = uiScheduler
    }

    @SuppressLint("CheckResult")
    fun fetchData(repositoryId: Int, username: String){

        if(hasFetchedData){
            return
        }

        hasFetchedData = true

        val userDetailsSingle = githubRepository.getUserDetails(username)
        val repositorySingle = githubRepository.getRepository(repositoryId)

        val zipperFunction : BiFunction<UserDetailsDto, RepositoryDto, Pair<UserDetailsDto, RepositoryDto>> = BiFunction{ user, repo -> Pair(user, repo)}

        Single.zip(userDetailsSingle, repositorySingle, zipperFunction)
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({
                modelRelay.accept(modelRelay.value!!.copy(userDetailsDto = it.first, repositoryDto = it.second, isLoading = false, isError = false))
            }, {
                modelRelay.accept(modelRelay.value!!.copy(userDetailsDto = null, repositoryDto = null, isLoading = false, isError = true))
            })

    }

}


data class RepositoryDetailsFragmentModel(val userDetailsDto: UserDetailsDto?,
                                          val repositoryDto: RepositoryDto?,
                                          val isLoading: Boolean,
                                          val isError: Boolean)