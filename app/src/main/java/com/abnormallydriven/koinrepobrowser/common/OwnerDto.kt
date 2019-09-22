package com.abnormallydriven.koinrepobrowser.common

import com.google.gson.annotations.SerializedName

data class OwnerDto(

	@field:SerializedName("login")
	val login: String,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,

	@field:SerializedName("id")
	val id: Int

)