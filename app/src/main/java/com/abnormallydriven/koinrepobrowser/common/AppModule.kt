package com.abnormallydriven.koinrepobrowser.common

import com.abnormallydriven.koinrepobrowser.repodetails.RepositoryDetailsFragmentViewModel
import com.abnormallydriven.koinrepobrowser.repos.RepositoryAdapter
import com.abnormallydriven.koinrepobrowser.repos.RepositoryDiffUtilItemCallback
import com.abnormallydriven.koinrepobrowser.repos.RepositoryListFragment
import com.abnormallydriven.koinrepobrowser.repos.RepositoryListFragmentViewModel
import com.google.gson.Gson
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<GithubApi> {

        val gson = Gson()

        val retrofitClient = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

        val githubApi = retrofitClient.create(GithubApi::class.java)

        githubApi
    }

    single<GithubRepository>{
        GithubRepository(get())
    }

    single<Scheduler>(named("ui")){ AndroidSchedulers.mainThread()}
    single<Scheduler>(named("computation")){ Schedulers.computation()}
    single<Scheduler>(named("io")){ Schedulers.io()}


    scope(named<RepositoryListFragment>()){
        scoped { RepositoryDiffUtilItemCallback()}
        scoped { RepositoryAdapter(get())}
    }


    viewModel{ RepositoryListFragmentViewModel(get(), get(named("io")), get(named("ui")))}
    viewModel{ RepositoryDetailsFragmentViewModel(get(), get(named("io")), get(named("ui")))}
}