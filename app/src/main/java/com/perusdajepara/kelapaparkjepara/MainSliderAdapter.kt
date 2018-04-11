package com.perusdajepara.kelapaparkjepara

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.util.zip.Inflater

/**
 * Created by mrdoyon on 3/27/18.
 */
class MainSliderAdapter(val imgData: ArrayList<String>): PagerAdapter(){
    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun getCount(): Int {
        return imgData.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val v = LayoutInflater.from(container?.context).inflate(R.layout.row_image_slider_main, container, false)
        val img = v.findViewById<ImageView>(R.id.main_image_slider)
        Glide.with(container?.context).load(imgData[position]).into(img)

        container?.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object`as RelativeLayout)
    }

}