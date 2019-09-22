package com.abnormallydriven.koinrepobrowser.repos

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.abnormallydriven.koinrepobrowser.common.GithubRepository
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.Scheduler

class RepositoryListFragmentViewModel : ViewModel() {

    private lateinit var githubRepository: GithubRepository

    private lateinit var ioScheduler: Scheduler

    private lateinit var uiScheduler: Scheduler

    private val modelRelay = BehaviorRelay.createDefault<RepositoryListFragmentModel>(RepositoryListFragmentModel(emptyList(), true, false))

    val modelObservable: Observable<RepositoryListFragmentModel> = modelRelay

    private var hasFetchedData = false

    fun initialize(githubRepository: GithubRepository, ioScheduler: Scheduler, uiScheduler: Scheduler) {
        this.githubRepository = githubRepository
        this.ioScheduler = ioScheduler
        this.uiScheduler = uiScheduler
    }

    @SuppressLint("CheckResult")
    fun fetchData(){

        if(hasFetchedData){
            return
        }

        hasFetchedData = true

        githubRepository.getRepositories()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ repositoryList ->
                modelRelay.accept(modelRelay.value!!.copy(repositoryList = repositoryList, isLoading = false, isError = false))
            }, { throwable ->
                modelRelay.accept(RepositoryListFragmentModel(emptyList(), false, true))
            })


    }
}



data class RepositoryListFragmentModel(val repositoryList: List<RepositoryDto>,
                                           val isLoading: Boolean,
                                           val isError: Boolean)

