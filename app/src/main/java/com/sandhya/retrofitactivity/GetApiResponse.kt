package com.sandhya.retrofitactivity


import com.google.gson.annotations.SerializedName

class GetApiResponse : ArrayList<GetApiResponseItem>()
    data class GetApiResponseItem(
        @SerializedName("id")
        val id: Int? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("gender")
        val gender: String? = null,
        @SerializedName("status")
        val status: String? = null
    )
