package com.example.myapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.model.UpComingMoviesModel
import com.example.recyclerviewapp.UpComingMoviesAdapter
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpComingMoviesFragment : Fragment() {

    //lateinit var moviesViewModel: MoviesViewModel
    private lateinit var searchView: EditText
    private lateinit var searchViewLayout: ConstraintLayout
    private lateinit var progressBar : ProgressBar

    private var upComingMoviesAdapterObj: UpComingMoviesAdapter? = null
    lateinit var upComingMoviesRecyclerView: RecyclerView
    var upcomingMovieList = arrayListOf<UpComingMoviesModel.Result>()
    val newList = ArrayList<UpComingMoviesModel.Result>()
    private var myInterface: UpComingMoviesFragmentInterface? = null
    private var isLoading: Boolean = false
    var currentPage = 1
    lateinit var layoutManager: LinearLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is UpComingMoviesFragmentInterface){
            myInterface = context
        }else{
            throw java.lang.RuntimeException("$context must Implement UpcomingMoviesInterface")
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_up_coming_movies, container, false)

        progressBar = view.findViewById(R.id.upcoming_progress)
        progressBar.visibility = View.GONE

        upComingMoviesRecyclerView = view.findViewById(R.id.recycler_view_upcoming)

        val activity = activity as? MainActivity
        searchViewLayout = activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException("TextView not found in activity")

        // Hide the view
        searchViewLayout.visibility = View.VISIBLE

        currentPage = 1
        upcomingMovieList.clear()

        upComingMoviesAdapterObj = UpComingMoviesAdapter(requireActivity(),
            upcomingMovieList,
            object : UpComingMoviesAdapter.onItemClickListner {
                override fun onItemClicked(item: UpComingMoviesModel.Result, position: Int) {

                    val fragment =
                        UpComingItemViewDetailFragment() // replace your custom fragment class

                    val bundle = Bundle()
                    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

                    bundle.putParcelable("Object", item) // use as per your need
                    Log.d(PopularMoviesFragment.TAG, "onItemClicked: $bundle")
                    fragment.arguments = bundle
                    fragmentTransaction?.replace(R.id.nav_host_fragment_container, fragment)
                    fragmentTransaction?.addToBackStack("Detail")
                    fragmentTransaction?.commit()
                }

            })

        upComingMoviesRecyclerView.adapter = upComingMoviesAdapterObj

        upComingMoviesAdapterObj!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }
        })

        callPagination()

        myInterface?.attachInstanceUpComing { listOfSearchMovies ->
            run {
                upComingMoviesAdapterObj?.let {
                    if (listOfSearchMovies.results.isEmpty()) {
                        it.UpdateSearchedMoviesData(newList)
                    } else {
                        it.UpdateSearchedMoviesData(listOfSearchMovies.results as ArrayList<UpComingMoviesModel.Result>)
                    }

                }

            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //upComingMoviesAdapterObj = UpComingMoviesAdapter(context!!,)
        //upComingMoviesRecyclerView.adapter = upComingMoviesAdapterObj
        getUpcomingMoviesData()
        //dataObserver()

    }

   /* private fun dataObserver() {
        moviesViewModel.moviesLivesData.observe(viewLifecycleOwner){upComingmoview ->
            upComingMoviesAdapterObj?.UpdateUpComingMoviesData(upComingmoview)
        }
    }*/

    private fun callPagination() {

        upComingMoviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutManager = upComingMoviesRecyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Reached the end of the list, load the next page
                    isLoading = true
                    getUpcomingMoviesData()

                }
            }
        })

    }

    companion object {
        fun newInstance() = UpComingMoviesFragment()
        const val TAG = "UpComingMovieFrag"
        const val activeFragment : String = "UpComing"
    }

    private fun getUpcomingMoviesData() {
        progressBar.visibility = View.VISIBLE
        val retrofitData = APIsInterface.create().getUpcomingMoviesData(currentPage)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {
            override fun onResponse(
                call: Call<UpComingMoviesModel>,
                response: Response<UpComingMoviesModel>
            ) {

                if (response.code() == 200) {

                    try {
                        //success

                        /*moviesViewModel.upcomingModelList =
                            response.body()?.results as ArrayList<UpComingMoviesModel.Result>*/
                        val newComingMovieList =
                        (response.body()?.results as? ArrayList<UpComingMoviesModel.Result>) ?: arrayListOf()
                        upcomingMovieList.addAll(newComingMovieList)
                        newList.addAll(newComingMovieList)
                        upComingMoviesAdapterObj!!.UpdateUpComingMoviesData(upcomingMovieList)
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
                Log.v("Retrofit", "Call Failed")
            }
        })
    }

}