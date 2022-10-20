package com.example.bookscanner

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.login_activity.*


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val pagerAdapter: AuthenticationPagerAdapter = AuthenticationPagerAdapter(
            supportFragmentManager
        )
        pagerAdapter.addFragmet(LoginFragment())
        pagerAdapter.addFragmet(RegisterFragment())
        viewPager.adapter = pagerAdapter
    }

    internal inner class AuthenticationPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm) {
        private val fragmentList: ArrayList<Fragment> = ArrayList()
        override fun getItem(i: Int): Fragment {
            return fragmentList[i]
        }

        override fun getCount(): Int {
            return fragmentList.size()
        }

        fun addFragmet(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }
}