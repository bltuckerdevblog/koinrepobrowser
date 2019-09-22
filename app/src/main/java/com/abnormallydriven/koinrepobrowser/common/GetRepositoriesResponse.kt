package com.abnormallydriven.koinrepobrowser.common

import com.google.gson.annotations.SerializedName

data class GetRepositoriesResponse(

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("owner")
	val ownerDto: OwnerDto,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("name")
	val name: String
)