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

class TopRatedMoviesFragment : Fragment() {

    //lateinit var moviesViewModel: MoviesViewModel
    private lateinit var searchView: EditText
    private lateinit var searchViewLayout: ConstraintLayout
    private lateinit var progressBar : ProgressBar

    private var topRatedMoviesAdapterObj: TopRatedMoviesAdapter? = null
    lateinit var topRatedMoviesRecyclerView: RecyclerView
    private var topRatedMovieList = arrayListOf<UpComingMoviesModel.Result>()
    val newList = ArrayList<UpComingMoviesModel.Result>()
    private var isLoading: Boolean = false
    private var myInterface: TopRatedMoviesFragmentInterface? = null
    var currentPage = 1
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is TopRatedMoviesFragmentInterface){
            myInterface = context
        }else{
            throw java.lang.RuntimeException("$context must Implement TopRatedFragmentInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_toprated_movies, container, false)

        progressBar = view.findViewById(R.id.toprated_progress)
        progressBar.visibility = View.GONE

        val activity = activity as? MainActivity
        searchViewLayout = activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException("TextView not found in activity")

        // Hide the view
        searchViewLayout.visibility = View.VISIBLE

        topRatedMoviesRecyclerView = view.findViewById(R.id.recycler_view_toprated)
        currentPage = 1
        topRatedMoviesAdapterObj = TopRatedMoviesAdapter(requireActivity(),
            topRatedMovieList,
        object: TopRatedMoviesAdapter.onItemClickListner{
            override fun onItemClicked(item: UpComingMoviesModel.Result, position: Int) {

            val fragment =
                TopRatedItemViewDetailFragment() // replace your custom fragment class

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
        topRatedMoviesRecyclerView.adapter = topRatedMoviesAdapterObj
        callPagination()

        topRatedMoviesAdapterObj!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }
        })

        myInterface?.attachInstanceTopRated { listOfSearchMovies ->
            run {
                topRatedMoviesAdapterObj?.let {
                    if(listOfSearchMovies.results.isEmpty()){
                        it.UpdateSearchedMoviesData(newList)
                    }else{
                        Log.d(TAG, "Update List: ${topRatedMovieList.size}")
                        it.UpdateSearchedMoviesData(listOfSearchMovies.results as ArrayList<UpComingMoviesModel.Result>)
                    }

                }
            }
        }
        return view
    }

    private fun callPagination() {
        topRatedMoviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutManager = topRatedMoviesRecyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Reached the end of the list, load the next page
                    isLoading = true
                    getTopRatedMoviesData()

                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTopRatedMoviesData()

    }

    companion object {

        fun newInstance() = TopRatedMoviesFragment()
        const val TAG = "TopRatedFrag"
        const val activeFragment : String = "TopRated"
    }

    private fun getTopRatedMoviesData() {
        progressBar.visibility = View.VISIBLE
        val retrofitData = APIsInterface.create().getTopRatedMoviesData(currentPage)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {
            override fun onResponse(
                call: Call<UpComingMoviesModel>,
                response: Response<UpComingMoviesModel>
            ) {

                if (response.code() == 200) {

                    try {
                        //success

                        val newtopRatedMovieList =
                            (response.body()!!.results as ArrayList<UpComingMoviesModel.Result>)
                        /*moviesViewModel.topRatedMoviesList =
                            response.body()!!.results as ArrayList<UpComingMoviesModel.Result>*/
                        topRatedMovieList.addAll(newtopRatedMovieList)
                        newList.addAll(newtopRatedMovieList)
                        topRatedMoviesAdapterObj!!.updateTopRatedMoviesData(topRatedMovieList)
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