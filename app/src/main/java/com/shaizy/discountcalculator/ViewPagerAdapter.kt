package com.shaizy.discountcalculator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * Created by syedshahnawazali on 25/02/2018.
 *
 */
class ViewPagerAdapter(fm: FragmentManager, private val list: ArrayList<Info>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = FragmentMain.newInstance(list[position])

    override fun getCount(): Int = list.size

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}