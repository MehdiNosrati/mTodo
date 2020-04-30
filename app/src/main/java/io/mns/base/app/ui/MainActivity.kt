package io.mns.base.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.mns.androidlib.toggleTheme
import io.mns.base.app.R
import io.mns.base.app.databinding.ActivityMainBinding
import io.mns.base.app.ui.viewmodels.MainViewModel
import kotlin.math.hypot

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (viewModel.bitmap != null) {
            themeChanged()
        } else {
            binding.imageView.isVisible = false
        }
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        binding.bottomBar.onItemSelected = {
            when (it) {
                0 -> navController.navigate(R.id.action_doneFragment_to_homeFragment)
                1 -> navController.navigate(R.id.action_homeFragment_to_doneFragment)
            }
        }
    }

    override fun onBackPressed() {
        if (navController.navigateUp()) {
            binding.bottomBar.setActiveItem(0)
        } else finish()
    }

    fun hideBottomNav() {
        binding.bottomBar.animate().apply {
            translationY(resources.getDimensionPixelSize(R.dimen.bottom_nav_height).toFloat())
            duration = 300
            start()
        }
    }

    private fun themeChanged() {
        Handler().postDelayed(
            {
                try {
                    binding.imageView.setImageBitmap(viewModel.bitmap)
                    binding.imageView.isVisible = true
                    val w = binding.container.measuredWidth
                    val h = binding.container.measuredHeight
                    val finalRadius = hypot(w.toFloat(), h.toFloat())
                    val cx = resources.getDimensionPixelSize(R.dimen.moon_left)
                    val cy = resources.getDimensionPixelSize(R.dimen.moon_top)
                    val anim = ViewAnimationUtils.createCircularReveal(
                        binding.imageView,
                        cx,
                        cy,
                        finalRadius,
                        0f
                    )
                    anim.duration = 400L
                    anim.doOnEnd {
                        binding.imageView.setImageDrawable(null)
                        binding.imageView.isVisible = false
                    }
                    anim.start()
                } catch (e: Exception) {
                    binding.imageView.isVisible = false
                    binding.imageView.setImageBitmap(null)
                }
            },
            50
        )
    }

    fun toggleTheme() {
        if (binding.imageView.isVisible) {
            return
        }

        val w = binding.container.measuredWidth
        val h = binding.container.measuredHeight
        viewModel.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(viewModel.bitmap!!)
        binding.container.draw(canvas)
        resources.toggleTheme()
    }

    fun showBottomNav() {
        binding.bottomBar.visibility = View.VISIBLE
        if (binding.bottomBar.translationY != 0f) {
            binding.bottomBar.animate().apply {
                translationY(0f)
                duration = 300
                start()
            }
        }
    }
}
