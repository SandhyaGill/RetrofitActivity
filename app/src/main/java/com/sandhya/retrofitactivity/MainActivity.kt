package com.sandhya.retrofitactivity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sandhya.retrofitactivity.databinding.ActivityMainBinding
import com.sandhya.retrofitactivity.databinding.DialogItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag
import retrofit2.http.Url
import java.net.Authenticator

interface RetrofitInterface{
    @GET("users")
    fun getApiResponse() : Call<GetApiResponse>

    @GET("users/{id}")
    fun getSingleUserApi(@Path("id") string: String) : Call<GetApiResponseItem>

    @POST("users")
    @FormUrlEncoded
    fun postUser(@Header("Authorization") authorization: String,
                 @Field("email") email: String,
                 @Field("name") name: String,
                 @Field("gender") gender: String,
                 @Field("status") status: String) : Call<GetApiResponseItem>

    @GET("users")
    fun getUsersPerPage(
        @Query("page") page: Int,
        @Query("per_page") perpage: Int) : Call<GetApiResponse>
}

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adapter: RetrofitAdapter
    var apiList = arrayListOf <GetApiResponseItem>()
    var retrofit : Retrofit? = null
    var apiInterface : RetrofitInterface? = null
    var page = 0
    var perPageQuery = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RetrofitAdapter(this, apiList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        retrofit = Retrofit.Builder().baseUrl("https://gorest.co.in/public/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit?.create(RetrofitInterface::class.java)

        binding.btnGetApi.setOnClickListener {
            apiInterface?.getApiResponse()?.enqueue(object : Callback<GetApiResponse> {
                override fun onResponse(
                    call: Call<GetApiResponse>,
                    response: Response<GetApiResponse>
                ) {
                    Log.e("Tag", "Api Response ${response.body()}")
                    val responseBody = response.body()
                    responseBody?.let {
//                            apiList.addAll(it)
                        apiList.addAll(it as ArrayList<GetApiResponseItem>)
                        adapter.notifyDataSetChanged()

                    }
                }

                override fun onFailure(call: Call<GetApiResponse>, t: Throwable) {
                }
            })
        }

        binding.btnSingle.setOnClickListener {
            apiInterface?.getSingleUserApi("2322070")?.enqueue(object: Callback<GetApiResponseItem>{
                override fun onResponse(
                    call: Call<GetApiResponseItem>,
                    response: Response<GetApiResponseItem>
                ) {
                    Log.e("Tag","single item ${response.body()}")
                    (response.body() as? GetApiResponseItem)?.let { it1 -> apiList.add(it1) }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetApiResponseItem>, t: Throwable) {
                }
            })

        }

        binding.btnPagination.setOnClickListener {
            page = 0
            hitPaginationApi()
        }
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                Log.e("Tag","In Scroll")
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                Log.e("Tag", "last Item ${lastVisibleItemPosition} ${apiList.size}")

                if (lastVisibleItemPosition == apiList.size - 1) {
                    binding.progressBar.visibility = View.VISIBLE
                    page++
                    hitPaginationApi()


                }
            }
        })
        binding.fab.setOnClickListener {
            var dialog = Dialog(this)
            var dialogItemBinding = DialogItemBinding.inflate(layoutInflater)
            dialog.setContentView(dialogItemBinding.root)

            dialogItemBinding.btnPost.setOnClickListener {
                if (dialogItemBinding.tierName.text.toString().isNullOrEmpty()){
                    dialogItemBinding.tilName.error = resources.getString(R.string.enter_name)
                }else if (dialogItemBinding.tierEmail.text.toString().isNullOrEmpty()) {
                    dialogItemBinding.tilEmail.error = resources.getString(R.string.enter_email)
//                } else if (dialogItemBinding.radioGroup.checkedRadioButtonId == -1){
//                    Toast.makeText(this,"Please select your gender", Toast.LENGTH_SHORT).show()
//                }
                }else {
                    var selectGender = if(dialogItemBinding.rbMale.isSelected)
                        "Male" else "Female"
                    var isActive = if (dialogItemBinding.cbStatus.isChecked)
                        "active" else "inactive"
                    apiInterface?.postUser("Bearer 5d403abd1182e85b92e5d7fb92d747b8f68200ef9a6b0992e0a780976f25b41a",
                        dialogItemBinding.tierEmail.text.toString(),
                        dialogItemBinding.tierName.text.toString(),
                        selectGender,
                        isActive)?.enqueue(object : Callback<GetApiResponseItem>{
                        override fun onResponse(
                            call: Call<GetApiResponseItem>,
                            response: Response<GetApiResponseItem>
                        ) {
                            Log.e("Tag","response body ${response.body()} ")
//                            Log.e("Tag","response error body ${response.errorBody()} ")
                            if (response.isSuccessful)
                                apiList.add(response.body() as GetApiResponseItem)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(this@MainActivity,"User Added Successfully",Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<GetApiResponseItem>, t: Throwable) {
                            Log.e("TAG", "in failure ${t.message}")
                        }
                    })
                    dialog.dismiss()
                }
            }
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        }

    }

    private fun hitPaginationApi() {

        apiInterface?.getUsersPerPage(page, perPageQuery)?.enqueue(object: Callback<GetApiResponse>{
            override fun onResponse(
                call: Call<GetApiResponse>,
                response: Response<GetApiResponse>
            ) {
                Log.e("Tag","Pagination response ${response.body()}")
                var response = response.body()
                binding.progressBar.visibility = View.GONE
                apiList.addAll(response as ArrayList<GetApiResponseItem>)
                adapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<GetApiResponse>, t: Throwable) {
            }
        })
    }
}