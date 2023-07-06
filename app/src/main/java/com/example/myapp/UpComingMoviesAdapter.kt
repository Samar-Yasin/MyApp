package com.example.recyclerviewapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.myapp.R
import com.example.myapp.model.UpComingMoviesModel

class UpComingMoviesAdapter(
    private var context: Context,
    private var UpComingMoviesList:
    ArrayList<UpComingMoviesModel.Result>,
    private var myListner:onItemClickListner?,

    ) : RecyclerView.Adapter<UpComingMoviesAdapter.UpComingMoviesViewHandler>() {

    class UpComingMoviesViewHandler(itemView: View) : ViewHolder(itemView) {

        var movieImage = itemView.findViewById<ImageView>(R.id.movie_img)
        var movieName = itemView.findViewById<TextView>(R.id.movie_name)

    }

    override fun onBindViewHolder(holder: UpComingMoviesViewHandler, position: Int) {
        val imageBasicUrl = "https://image.tmdb.org/t/p/w342"
        val upcomingMoviesImgURL = imageBasicUrl + UpComingMoviesList[position].backdropPath

        Glide.with(context).load(upcomingMoviesImgURL).into(holder.movieImage)
        holder.movieName.text = UpComingMoviesList[position].originalTitle

        holder.itemView.setOnClickListener {
            myListner?.onItemClicked(UpComingMoviesList[position], position)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpComingMoviesViewHandler {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.upcoming_movies_item, parent,
            false
        )
        return UpComingMoviesViewHandler(itemView)
    }

    override fun getItemCount(): Int {
        return UpComingMoviesList.size
    }

    fun UpdateUpComingMoviesData(upcomingMovieList: ArrayList<UpComingMoviesModel.Result>) {

        Log.e("UpComing", "Size: "+UpComingMoviesList.size)
        notifyDataSetChanged()
    }

    fun UpdateSearchedMoviesData(upcomingMovieList: ArrayList<UpComingMoviesModel.Result>) {
        UpComingMoviesList.clear()
        this.UpComingMoviesList.addAll(upcomingMovieList)
        notifyDataSetChanged()
    }


    interface onItemClickListner {
        fun onItemClicked(item: UpComingMoviesModel.Result, position: Int)
    }

}
