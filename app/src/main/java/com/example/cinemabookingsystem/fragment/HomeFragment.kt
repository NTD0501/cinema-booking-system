package com.example.cinemabookingsystem.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cinemabookingsystem.prefs.DataStoreManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.cinemabookingsystem.MyApplication
import com.example.cinemabookingsystem.adapter.BannerMovieAdapter
import com.example.cinemabookingsystem.adapter.BannerMovieAdapter.IClickItemListener
import com.example.cinemabookingsystem.adapter.MovieAdapter
import com.example.cinemabookingsystem.constant.GlobalFunction.goToMovieDetail
import com.example.cinemabookingsystem.databinding.FragmentHomeBinding
import com.example.cinemabookingsystem.model.Movie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class HomeFragment : Fragment() {
    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mListMovies: MutableList<Movie>? = null
    private var mListMoviesBanner: MutableList<Movie>? = null
    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (mListMoviesBanner == null || mListMoviesBanner!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding!!.viewPager2.currentItem == mListMoviesBanner!!.size - 1) {
            mFragmentHomeBinding!!.viewPager2.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding!!.viewPager2.currentItem = mFragmentHomeBinding!!.viewPager2.currentItem + 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        getListMovies()
        displayAccountInformation()
        return mFragmentHomeBinding!!.root
    }


    private fun getListMovies() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getMovieDatabaseReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListMovies != null) {
                    mListMovies!!.clear()
                } else {
                    mListMovies = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val movie = dataSnapshot.getValue(Movie::class.java)
                    if (movie != null) {
                        mListMovies!!.add(0, movie)
                    }
                }
                displayListBannerMovies()
                displayListAllMovies()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayAccountInformation() {
        val currentUser = DataStoreManager.getUser()

        if (currentUser != null) {
            mFragmentHomeBinding?.tvUserName?.text =
                ("Xin chào " + currentUser.fullname) ?: currentUser.email

            val userRole = if (currentUser.isAdmin) "Admin" else "Member"
            mFragmentHomeBinding?.tvUserRole?.text = userRole
        }

        mFragmentHomeBinding?.imgUser?.setOnClickListener {
            // Chuyển đến màn hình thông tin tài khoản hoặc màn hình profile
            // Ví dụ: GlobalFunction.startActivity(requireActivity(), ProfileActivity::class.java)
        }
    }

    private fun displayListBannerMovies() {
        val bannerMovieAdapter = BannerMovieAdapter(getListBannerMovies(), object : IClickItemListener {
            override fun onClickItem(movie: Movie?) {
                goToMovieDetail(activity, movie)
            }
        })
        mFragmentHomeBinding!!.viewPager2.adapter = bannerMovieAdapter
        mFragmentHomeBinding!!.indicator3.setViewPager(mFragmentHomeBinding!!.viewPager2)
        mFragmentHomeBinding!!.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun getListBannerMovies() : List<Movie> {
        if (mListMoviesBanner != null) {
            mListMoviesBanner!!.clear()
        } else {
            mListMoviesBanner = ArrayList()
        }
        if (mListMovies == null || mListMovies!!.isEmpty()) {
            return mListMoviesBanner!!
        }
        val listClone: List<Movie> = ArrayList(mListMovies!!)
        Collections.sort(listClone) { movie1: Movie, movie2: Movie -> movie2.booked - movie1.booked }
        for (movie in listClone) {
            if (mListMoviesBanner!!.size < MAX_BANNER_SIZE) {
                mListMoviesBanner!!.add(movie)
            }
        }
        return mListMoviesBanner!!
    }

    private fun displayListAllMovies() {
        val gridLayoutManager = GridLayoutManager(activity, 3)
        mFragmentHomeBinding!!.rcvMovie.layoutManager = gridLayoutManager
        val movieAdapter = MovieAdapter(mListMovies, object : MovieAdapter.IManagerMovieListener {
            override fun clickItemMovie(movie: Movie?) {
                goToMovieDetail(activity, movie)
            }
        })
        mFragmentHomeBinding!!.rcvMovie.adapter = movieAdapter
    }


    companion object {
        private const val MAX_BANNER_SIZE = 3
    }
}