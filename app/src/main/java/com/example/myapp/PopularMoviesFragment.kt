package com.example.myapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.model.UpComingMoviesModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularMoviesFragment : Fragment() {

    //lateinit var moviesViewModel: MoviesViewModel
    private lateinit var searchView: EditText
    private lateinit var searchViewLayout: ConstraintLayout
    private lateinit var progressBar : ProgressBar

    var popularMoviesAdapterObj : PopularMoviesAdapter? = null
    private lateinit var popularRecyclerView : RecyclerView
    var popularMoviesList = arrayListOf<UpComingMoviesModel.Result>()
    private var myInterface: PopularMoviesFragmentInterface? = null
    private var isLoading: Boolean = false
    var currentPage = 1
    val newList = ArrayList<UpComingMoviesModel.Result>()
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is PopularMoviesFragmentInterface){
            myInterface = context
        }else{
            throw java.lang.RuntimeException("$context must Implement PopularMoviesFragmentInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_popular_movies, container, false)

        progressBar = view.findViewById(R.id.popular_progress)
        progressBar.visibility = View.GONE

        val activity = activity as? MainActivity
        searchViewLayout = activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException("TextView not found in activity")

        // Hide the view
        searchViewLayout.visibility = View.VISIBLE

        popularRecyclerView = view.findViewById(R.id.recycler_view_popular)
        currentPage = 1
        popularMoviesAdapterObj = PopularMoviesAdapter(requireActivity(),
            popularMoviesList,
            object : PopularMoviesAdapter.onItemClickListner {
                override fun onItemClicked(item: UpComingMoviesModel.Result, position: Int) {

                    val fragment =
                        PopularItemViewDetailFragment() // replace your custom fragment class

                    val bundle = Bundle()
                    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

                    bundle.putParcelable("Object", item) // use as per your need
                    Log.d(PopularMoviesFragment.TAG, "onItemClicked: $bundle")
                    fragment.arguments = bundle
                    fragmentTransaction?.replace(R.id.nav_host_fragment_container,fragment)
                    fragmentTransaction?.addToBackStack("Detail")
                    fragmentTransaction?.commit()

                }

            })
        popularRecyclerView.adapter = popularMoviesAdapterObj
        callPagination()

        popularMoviesAdapterObj!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }
        })
            myInterface?.attachInstancePopular { listOfSearchMovies ->
            run {
                popularMoviesAdapterObj?.let {
                    if(listOfSearchMovies.results.isEmpty()){
                        it.UpdateSearchedMoviesData(newList)
                    }else{
                        it.UpdateSearchedMoviesData(listOfSearchMovies.results as ArrayList<UpComingMoviesModel.Result>)

                    }
                }
            }
        }

        return view
    }

    private fun callPagination() {
        popularRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutManager = popularRecyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Reached the end of the list, load the next page
                    isLoading = true
                    getPopularMoviesData()

                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPopularMoviesData()
    }

    companion object {
        fun newInstance() = PopularMoviesFragment()
        const val TAG = "PopularMovieFrag"
        const val activeFragment : String = "Popular"
    }

    private fun getPopularMoviesData() {
        progressBar.visibility = View.VISIBLE

        val retrofitData = APIsInterface.create().getPopularMoviesData(currentPage)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {
            override fun onResponse(
                call: Call<UpComingMoviesModel>,
                response: Response<UpComingMoviesModel>
            ) {

                if (response.code() == 200) {

                    try {
                        //success

                        /*moviesViewModel.popularModelList =
                            response.body()!!.results as ArrayList<UpComingMoviesModel.Result>*/
                        val newpopularMoviesList =
                        response.body()!!.results as ArrayList<UpComingMoviesModel.Result>
                        popularMoviesList.addAll(newpopularMoviesList)
                        newList.addAll(newpopularMoviesList) // when we clear search and want to get back to our origional list.
                        popularMoviesAdapterObj!!.updatePopularMoviesData(popularMoviesList)
                        isLoading = false
                        currentPage++

                        progressBar.visibility = View.GONE

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }

                } else {
                    call.cancel()
                }

            }

            override fun onFailure(call: Call<UpComingMoviesModel>, t: Throwable) {
                Log.v("retrofit", "call failed")

            }

        })

    }

}