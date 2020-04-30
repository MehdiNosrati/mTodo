package io.mns.base.app.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import io.mns.androidlib.*
import io.mns.base.app.R
import kotlin.math.hypot
import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        preAnimationSetup()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        performCircularReveal()
        Handler().postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            1000
        )
    }
    private fun performCircularReveal() {
        if (!hasSourceBounds) {
            rootContentLayout.isInvisible = false
        } else {
            sourceBounds { sourceBounds ->
                rootContentLayout.run {
                    screenBounds { rootLayoutBounds ->
                        // Verify if sourceBounds is valid
                        if (rootLayoutBounds.contains(sourceBounds)) {
                            val circle = createCircularReveal(
                                centerX = sourceBounds.centerX() - rootLayoutBounds.left,
                                centerY = sourceBounds.centerY() - rootLayoutBounds.top,
                                startRadius = (minOf(sourceBounds.width(), sourceBounds.height()) * 0.2).toFloat(),
                                endRadius = hypot(width.toFloat(), height.toFloat())
                            ).apply {
                                isInvisible = false
                                duration = 500L
                            }
                            AnimatorSet()
                                .apply { playTogether(circle, statusBarAnimator, navigationBarAnimator) }
                                .start()
                        } else {
                            isInvisible = false
                        }
                    }
                }
            }
        }
    }
}
