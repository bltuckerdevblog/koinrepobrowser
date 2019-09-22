package com.abnormallydriven.koinrepobrowser.common

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {

    @GET("repositories")
    fun getRepositories(): Single<GetRepositoriesResponse>

    @GET("users/{username}")
    fun getUserDetails(@Path("username") username: String): Single<UserDetailsDto>

}