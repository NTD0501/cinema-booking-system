package com.example.cinemabookingsystem.activity

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cinemabookingsystem.MyApplication
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.constant.ConstantKey
import com.example.cinemabookingsystem.constant.GlobalFunction
import com.example.cinemabookingsystem.databinding.ActivityMovieDetailBinding
import com.example.cinemabookingsystem.model.Movie
import com.example.cinemabookingsystem.util.DateTimeUtils
import com.example.cinemabookingsystem.util.GlideUtils
import com.example.cinemabookingsystem.util.StringUtil
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MovieDetailActivity : AppCompatActivity() {

    private var mActivityMovieDetailBinding: ActivityMovieDetailBinding? = null
    private var mMovie: Movie? = null
    private var mPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMovieDetailBinding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(mActivityMovieDetailBinding!!.root)
        getDataIntent()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        val movie = bundle[ConstantKey.KEY_INTENT_MOVIE_OBJECT] as Movie?
        getMovieInformation(movie!!.id)
    }

    private fun getMovieInformation(movieId: Long) {
        MyApplication[this].getMovieDatabaseReference().child(movieId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mMovie = snapshot.getValue(Movie::class.java)
                    displayDataMovie()
                    initListener()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayDataMovie() {
        if (mMovie == null) {
            return
        }
        GlideUtils.loadUrl(mMovie?.image, mActivityMovieDetailBinding!!.imgMovie)
        mActivityMovieDetailBinding!!.tvTitleMovie.text = mMovie?.name
        mActivityMovieDetailBinding!!.tvCategoryName.text = mMovie?.categoryName
        mActivityMovieDetailBinding!!.tvDateMovie.text = mMovie?.date
        val strPrice = mMovie?.price.toString() + ConstantKey.UNIT_CURRENCY_MOVIE
        mActivityMovieDetailBinding!!.tvPriceMovie.text = strPrice
        mActivityMovieDetailBinding!!.tvDescriptionMovie.text = mMovie?.description
        if (!StringUtil.isEmpty(mMovie?.url)) {
            initExoPlayer()
        }
    }

    private fun initListener() {
        mActivityMovieDetailBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivityMovieDetailBinding!!.btnWatchTrailer.setOnClickListener { scrollToLayoutTrailer() }
        mActivityMovieDetailBinding!!.imgPlayMovie.setOnClickListener { startVideo() }
        mActivityMovieDetailBinding!!.btnBooking.setOnClickListener { onClickGoToConfirmBooking() }
    }

    private fun onClickGoToConfirmBooking() {
        if (mMovie == null) {
            return
        }
        if (DateTimeUtils.convertDateToTimeStamp(mMovie?.date) < DateTimeUtils.getLongCurrentTimeStamp()) {
            Toast.makeText(this, getString(R.string.msg_movie_date_invalid), Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle()
        bundle.putSerializable(ConstantKey.KEY_INTENT_MOVIE_OBJECT, mMovie)
        GlobalFunction.startActivity(this, ConfirmBookingActivity::class.java, bundle)
    }

    private fun scrollToLayoutTrailer() {
        val duration: Long = 500
        Handler(Looper.getMainLooper()).postDelayed({
            val y = mActivityMovieDetailBinding!!.labelMovieTrailer.y
            val sv = mActivityMovieDetailBinding!!.scrollView
            val objectAnimator = ObjectAnimator.ofInt(sv, "scrollY", 0, y.toInt())
            objectAnimator.start()
            startVideo()
        }, duration)
    }

    private fun initExoPlayer() {
        val mExoPlayerView = mActivityMovieDetailBinding!!.exoplayer
        if (mPlayer != null) {
            return
        }

        // Initialize ExoPlayer
        mPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()

        // Set Player to ExoPlayerView
        mExoPlayerView.player = mPlayer
        mExoPlayerView.hideController()

        // Prepare Media Source
        val mediaItem = MediaItem.fromUri(Uri.parse(mMovie?.url))
        val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, DefaultHttpDataSource.Factory()))
            .createMediaSource(mediaItem)

        mPlayer?.setMediaSource(mediaSource)
        mPlayer?.prepare()
    }

    private fun startVideo() {
        mActivityMovieDetailBinding!!.imgPlayMovie.visibility = View.GONE
        mPlayer?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        mPlayer = null
    }
}
