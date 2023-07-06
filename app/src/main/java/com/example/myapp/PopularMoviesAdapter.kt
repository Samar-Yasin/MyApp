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

class PopularMoviesAdapter (
    private var context: Context,
    private var PopularMoviesList:
        ArrayList<UpComingMoviesModel.Result>,
    private var myListner: onItemClickListner?,

) : RecyclerView.Adapter<PopularMoviesAdapter.PopularViewHandler>() {

    class PopularViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var movieImage = itemView.findViewById<ImageView>(R.id.movie_img)
        var movieName = itemView.findViewById<TextView>(R.id.movie_name)

    }

    override fun onBindViewHolder(holder: PopularViewHandler, position: Int) {

        val imageBasicUrl = "https://image.tmdb.org/t/p/w342"
        if(PopularMoviesList[position].backdropPath == null){

            holder.movieImage.setImageResource(R.drawable.no_img_avail)
        }else {

            val popularMoviesImgURL = imageBasicUrl + PopularMoviesList[position].backdropPath

            Glide.with(context).load(popularMoviesImgURL).into(holder.movieImage)
            holder.movieName.text = PopularMoviesList[position].originalTitle
            holder.itemView.setOnClickListener {
                myListner?.onItemClicked(PopularMoviesList[position], position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHandler {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.popular_movies_item, parent, false)
        return PopularViewHandler(itemView)
    }

    override fun getItemCount(): Int {

        return PopularMoviesList.size

    }

    fun updatePopularMoviesData(popularMovieList: ArrayList<UpComingMoviesModel.Result>) {

        notifyDataSetChanged()
    }
    fun UpdateSearchedMoviesData(popularMovieList: ArrayList<UpComingMoviesModel.Result>) {
        PopularMoviesList.clear()
        this.PopularMoviesList.addAll(popularMovieList)
        notifyDataSetChanged()
    }

    interface onItemClickListner {
        fun onItemClicked(item: UpComingMoviesModel.Result, position: Int)
    }

}