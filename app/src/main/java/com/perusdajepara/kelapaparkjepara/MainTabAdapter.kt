package com.perusdajepara.kelapaparkjepara

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by mrdoyon on 3/27/18.
 */
class MainTabAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){

    var fragmentItem: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment){
        fragmentItem.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentItem[position]
    }

    override fun getCount(): Int {
        return fragmentItem.size
    }
}