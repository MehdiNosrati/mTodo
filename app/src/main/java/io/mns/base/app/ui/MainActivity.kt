package io.mns.base.app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.mns.base.app.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        bottomBar.onItemSelected = {
            when (it) {
                0 -> navController.navigate(R.id.action_doneFragment_to_homeFragment)
                1 -> navController.navigate(R.id.action_homeFragment_to_doneFragment)
            }
        }
    }


    override fun onBackPressed() {
        if (navController.navigateUp()) {
            bottomBar.setActiveItem(0)
        } else finish()
    }

    fun hideBottomNav() {
        bottomBar.animate().apply {
            translationY(resources.getDimensionPixelSize(R.dimen.bottom_nav_height).toFloat())
            duration = 300
            start()
        }
    }

    fun showBottomNav() {
        bottomBar.visibility = View.VISIBLE
        if (bottomBar.translationY != 0f) {
            bottomBar.animate().apply {
                translationY(0f)
                duration = 300
                start()
            }
        }
    }
}
