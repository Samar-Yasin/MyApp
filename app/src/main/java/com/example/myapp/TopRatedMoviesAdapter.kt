package com.example.myapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.model.UpComingMoviesModel

class TopRatedMoviesAdapter(
    private var context: Context,
    private var TopRatedMoviesList :
ArrayList<UpComingMoviesModel.Result>,
    private var myListner: onItemClickListner?)
    : RecyclerView.Adapter<TopRatedMoviesAdapter.TopRatedViewHandler>(){

    class TopRatedViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView){

        var movieImage = itemView.findViewById<ImageView>(R.id.movie_img)
        var movieName = itemView.findViewById<TextView>(R.id.movie_name)

    }

    override fun onBindViewHolder(holder: TopRatedViewHandler, position: Int) {
        val imageBasicUrl = "https://image.tmdb.org/t/p/w342"
        val topRatedMoviesImgURL = imageBasicUrl + TopRatedMoviesList[position].backdropPath

        Glide.with(context).load(topRatedMoviesImgURL).into(holder.movieImage)
        holder.movieName.text = TopRatedMoviesList[position].originalTitle

        holder.itemView.setOnClickListener {
            myListner?.onItemClicked(TopRatedMoviesList[position], position)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedViewHandler {

        val itemView = LayoutInflater.from(context).inflate(R.layout.toprated_movies_item, parent, false)
        return TopRatedViewHandler(itemView)

    }

    override fun getItemCount(): Int {
        return TopRatedMoviesList.size
    }

    fun updateTopRatedMoviesData(topratedMovieList: ArrayList<UpComingMoviesModel.Result>) {

        Log.e("TopRated", "Size: "+TopRatedMoviesList.size)
        notifyDataSetChanged()
    }
    fun UpdateSearchedMoviesData(topratedMovieList: ArrayList<UpComingMoviesModel.Result>) {
        TopRatedMoviesList.clear()
        this.TopRatedMoviesList.addAll(topratedMovieList)
        notifyDataSetChanged()
    }

    interface onItemClickListner {
        fun onItemClicked(item: UpComingMoviesModel.Result, position: Int)
    }
}