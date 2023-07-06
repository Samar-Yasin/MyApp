package com.example.myapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapp.model.UpComingMoviesModel

class PopularItemViewDetailFragment : Fragment() {

    private val basic_url : String = "https://image.tmdb.org/t/p/w342/"
    private lateinit var searchViewLayout: ConstraintLayout
    private var item: UpComingMoviesModel.Result? = null
    private lateinit var PopularMoviePic : ImageView
    private lateinit var title : TextView
    private lateinit var watchMovieButton : Button
    private lateinit var overViewTitle : TextView
    private lateinit var overViewDetail: TextView
    private lateinit var releaseDate: TextView
    private lateinit var releaseDateDetail: TextView
    private lateinit var backButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getParcelable("Object")
            Toast.makeText(requireContext(), item!!.title, Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =inflater.inflate(R.layout.fragment_popular_item_view_detail, container, false)

        PopularMoviePic = view.findViewById(R.id.itemViewImage)
        title = view.findViewById(R.id.itemViewTitle)
        watchMovieButton = view.findViewById(R.id.watch_movie_button)
        overViewTitle = view.findViewById(R.id.overview)
        overViewDetail = view.findViewById(R.id.overviewDetail)
        releaseDate = view.findViewById(R.id.release_date)
        releaseDateDetail = view.findViewById(R.id.release_date_detail)
        backButton = view.findViewById(R.id.back_button)

        val activity = activity as? MainActivity
        searchViewLayout = activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException("TextView not found in activity")

        // Hide the view
        searchViewLayout.visibility = View.GONE

        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w342${item?.backdropPath}")
            .into(PopularMoviePic)
        title.text = item?.originalTitle.toString()
        overViewDetail.text = item?.overview.toString()
        releaseDateDetail.text = item?.releaseDate

        backButton.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()

        }

        return view
    }
    companion object {

        fun newInstance() = PopularItemViewDetailFragment()

    }

}
