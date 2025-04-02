package com.example.cinemabookingsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemabookingsystem.adapter.BannerMovieAdapter.BannerMovieViewHolder
import com.example.cinemabookingsystem.databinding.ItemBannerMovieBinding
import com.example.cinemabookingsystem.model.Movie
import com.example.cinemabookingsystem.util.GlideUtils

class BannerMovieAdapter(private val mListMovies: List<Movie>?,
                         private val iClickItemListener: IClickItemListener) : RecyclerView.Adapter<BannerMovieViewHolder>() {
    interface IClickItemListener {
        fun onClickItem(movie: Movie?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerMovieViewHolder {
        val itemBannerMovieBinding = ItemBannerMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerMovieViewHolder(itemBannerMovieBinding)
    }

    override fun onBindViewHolder(holder: BannerMovieViewHolder, position: Int) {
        val movie = mListMovies!![position]
        GlideUtils.loadUrlBanner(movie.imageBanner, holder.mItemBannerMovieBinding.imgBanner)
        holder.mItemBannerMovieBinding.layoutItem.setOnClickListener { iClickItemListener.onClickItem(movie) }
    }

    override fun getItemCount(): Int {
        return mListMovies?.size ?: 0
    }

    class BannerMovieViewHolder(val mItemBannerMovieBinding: ItemBannerMovieBinding) : RecyclerView.ViewHolder(mItemBannerMovieBinding.root)
}