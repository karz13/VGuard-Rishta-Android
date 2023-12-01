package com.tfl.vguardrishta.ui.components.vguard.schemes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.models.SchemeImages


class SchemeImagesAdapter(
    private val context: Context,
    adrotater: List<SchemeImages>,
    val listener: (SchemeImages) -> Unit
) : PagerAdapter() {
    private val adrotater: List<SchemeImages>
    private val layoutInflater: LayoutInflater
    override fun getCount(): Int {
        return adrotater.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view =
            layoutInflater.inflate(R.layout.item_first_images, container, false)
        val ivFirstImage =
            view.findViewById<ImageView>(R.id.ivFirstImage)
        view.setOnClickListener { listener(adrotater[position]) }
        val image = adrotater[position].imgPath
        if (image != null && !image.isEmpty()) {

            Glide.with(context).load(ApiService.imageBaseUrl+image)
                .placeholder(R.drawable.no_image).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivFirstImage)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    init {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.adrotater = adrotater
    }
}