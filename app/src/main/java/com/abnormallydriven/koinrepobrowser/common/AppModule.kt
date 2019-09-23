package com.abnormallydriven.koinrepobrowser.common

import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
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
}