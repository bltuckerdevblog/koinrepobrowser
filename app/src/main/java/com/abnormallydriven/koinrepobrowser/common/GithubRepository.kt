package com.abnormallydriven.koinrepobrowser.common

import io.reactivex.Single

class GithubRepository(private val githubApi: GithubApi) {

    private var repositoryListCache: List<RepositoryDto>? = null

    private var userCache: MutableMap<String, UserDetailsDto> = mutableMapOf()

    fun getRepositories(): Single<List<RepositoryDto>> {

        if (repositoryListCache != null) {
            return Single.just(repositoryListCache)
        }

        return githubApi.getRepositories()
            .doOnSuccess { repositoryListCache = it }
    }

    fun getUserDetails(username: String): Single<UserDetailsDto> {

        if (userCache[username] != null) {
            return Single.just(userCache[username])
        }

        return githubApi.getUserDetails(username)
            .doOnSuccess { userCache[username] = it }
    }

    fun getRepository(repositoryId: Int): Single<RepositoryDto> {
        return githubApi.getRepository(repositoryId)
    }

}