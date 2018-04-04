package com.perusdajepara.kelapaparkjepara.mainfragments


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.restofragments.MakananFragment
import com.perusdajepara.kelapaparkjepara.restofragments.MinumanFragment


/**
 * A simple [Fragment] subclass.
 */
class RestoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_resto, container, false)

        val pager = v.findViewById<ViewPager>(R.id.resto_pager)
        val tab = v.findViewById<TabLayout>(R.id.resto_tab)

        val resto = RestoTabAdapter(fragmentManager)
        resto.addFragment(MakananFragment(), "Makanan")
        resto.addFragment(MinumanFragment(), "Minuman")

        pager.adapter = resto
        tab.setupWithViewPager(pager)

        return v
    }

    class RestoTabAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){

        val fragmentItems: ArrayList<Fragment> = ArrayList()
        val fragmentTitle: ArrayList<String> = ArrayList()

        fun addFragment(fragment: Fragment, title: String){
            fragmentItems.add(fragment)
            fragmentTitle.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragmentItems[position]
        }

        override fun getCount(): Int {
            return fragmentItems.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitle[position]
        }
    }
}
