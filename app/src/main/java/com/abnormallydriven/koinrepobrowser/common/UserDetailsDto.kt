package com.abnormallydriven.koinrepobrowser.common

import com.google.gson.annotations.SerializedName


data class UserDetailsDto(

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("login")
	val login: String,

	@field:SerializedName("blog")
	val blog: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("followers")
	val followers: Int,

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

	@field:SerializedName("following")
	val following: Int,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("location")
	val location: String? = null

)