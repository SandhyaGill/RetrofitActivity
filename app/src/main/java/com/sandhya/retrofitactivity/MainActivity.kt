package com.sandhya.retrofitactivity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sandhya.retrofitactivity.databinding.ActivityMainBinding
import com.sandhya.retrofitactivity.databinding.DialogItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetrofitInterface{
    @GET("users")
    fun getApiResponse() : Call<GetApiResponse>
}

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adapter: RetrofitAdapter
    var apiList = arrayListOf <GetApiResponseItem>()
    var retrofit : Retrofit? = null
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

        var apiInterface = retrofit?.create(RetrofitInterface::class.java)
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
        binding.fab.setOnClickListener {
            var dialog = Dialog(this)
            var dialogItemBinding = DialogItemBinding.inflate(layoutInflater)
            dialog.setContentView(dialogItemBinding.root)

            dialogItemBinding.btnPost.setOnClickListener {
                if (dialogItemBinding.tietId.text.toString().isNullOrEmpty()){
                    dialogItemBinding.tilId.error = resources.getString(R.string.enter_id)
                }else if (dialogItemBinding.tierName.text.toString().isNullOrEmpty()){
                    dialogItemBinding.tilName.error = resources.getString(R.string.enter_name)
                }else if (dialogItemBinding.tierEmail.text.toString().isNullOrEmpty()){
                    dialogItemBinding.tilEmail.error = resources.getString(R.string.enter_email)
                } else if (dialogItemBinding.radioGroup.checkedRadioButtonId == -1){
                    Toast.makeText(this,"Please select your gender", Toast.LENGTH_SHORT).show()
                }else {

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
}