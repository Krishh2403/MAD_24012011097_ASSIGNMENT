package com.example.mad_24012011097_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.mad_24012011097_assignment.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarInsets()

        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            openDashboard()
            return
        }

        playLoginAnimation()

        binding.btnContinue.setOnClickListener {
            animateButtonAndContinue()
        }

        binding.editEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                animateButtonAndContinue()
                true
            } else {
                false
            }
        }
    }

    private fun applySystemBarInsets() {
        val originalTopPadding = binding.loginContent.paddingTop
        val originalBottomPadding = binding.loginContent.paddingBottom

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightNavigationBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.loginContent) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                top = originalTopPadding + systemBars.top,
                bottom = originalBottomPadding + systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun playLoginAnimation() {
        binding.tvBrand.alpha = 0f
        binding.tvBrand.translationY = -24f

        binding.cardLogin.alpha = 0f
        binding.cardLogin.translationY = 55f

        binding.tvFooter.alpha = 0f

        binding.tvBrand.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500L)
            .setInterpolator(DecelerateInterpolator(1.6f))
            .start()

        binding.cardLogin.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(140L)
            .setDuration(600L)
            .setInterpolator(OvershootInterpolator(0.7f))
            .start()

        binding.tvFooter.animate()
            .alpha(1f)
            .setStartDelay(380L)
            .setDuration(350L)
            .start()
    }

    private fun animateButtonAndContinue() {
        binding.btnContinue.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(90L)
            .withEndAction {
                binding.btnContinue.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(140L)
                    .start()

                validateAndContinue()
            }
            .start()
    }

    private fun validateAndContinue() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim().lowercase()

        binding.nameLayout.error = null
        binding.emailLayout.error = null

        var isValid = true

        if (name.length < 2) {
            binding.nameLayout.error = "Enter your name"
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Enter a valid email address"
            isValid = false
        }

        if (!isValid) return

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()

        binding.btnContinue.isEnabled = false
        binding.btnContinue.text = "Welcome, ${name.substringBefore(" ")}"

        binding.btnContinue.postDelayed({
            openDashboard()
        }, 420L)
    }

    private fun openDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val PREFS_NAME = "farmlog_preferences"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
}