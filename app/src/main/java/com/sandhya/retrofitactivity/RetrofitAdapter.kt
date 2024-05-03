package com.sandhya.retrofitactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sandhya.retrofitactivity.databinding.RetrofitApiItemBinding

class RetrofitAdapter(var context: Context, var retrofitApiData: List<GetApiResponseItem>) : RecyclerView.Adapter<RetrofitAdapter.ViewHolder>() {
    class ViewHolder(var binidng : RetrofitApiItemBinding) : RecyclerView.ViewHolder(binidng.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RetrofitApiItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return retrofitApiData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binidng.apply {
            tvId.text = retrofitApiData[position].id.toString()
            tvName.text = retrofitApiData[position].name
            tvEmail.text = retrofitApiData[position].email
            tvGender.text = retrofitApiData[position].gender
            if (retrofitApiData[position].status.equals("active", true)){
                viewStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }else{
                viewStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red))

            }

        }
    }
}