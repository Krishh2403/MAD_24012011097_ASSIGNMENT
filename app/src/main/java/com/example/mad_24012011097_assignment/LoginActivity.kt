package com.example.mad_24012011097_assignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import android.view.inputmethod.EditorInfo
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.mad_24012011097_assignment.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private var isAwaitingVerification = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarInsets()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener {
                playLoginAnimation()
                if (currentUser.isEmailVerified) {
                    persistSessionAndOpenDashboard(
                        currentUser.displayName.orEmpty(),
                        currentUser.email.orEmpty()
                    )
                } else {
                    showVerificationPending(currentUser.email.orEmpty())
                }
            }
        } else {
            playLoginAnimation()
        }

        binding.btnContinue.setOnClickListener {
            animateButtonAndContinue()
        }

        binding.editPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                animateButtonAndContinue()
                true
            } else {
                false
            }
        }

        binding.tvResend.setOnClickListener {
            resendVerificationEmail()
        }

        binding.tvUseDifferentEmail.setOnClickListener {
            resetToLoginForm()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isAwaitingVerification) {
                        resetToLoginForm()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
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

                if (isAwaitingVerification) {
                    checkVerificationAndContinue()
                } else {
                    validateAndContinue()
                }
            }
            .start()
    }

    private fun validateAndContinue() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim().lowercase()
        val password = binding.editPassword.text.toString()

        binding.nameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        hideStatusMessage()

        var isValid = true

        if (name.length < 2) {
            binding.nameLayout.error = "Enter your name"
            isValid = false
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            binding.emailLayout.error = "Enter a valid email address"
            isValid = false
        }

        if (password.length < 6) {
            binding.passwordLayout.error = "Use at least 6 characters"
            isValid = false
        }

        if (!isValid) return

        setFormEnabled(false)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                setFormEnabled(true)
                val user = result.user
                user?.updateProfile(userProfileChangeRequest { displayName = name })
                user?.sendEmailVerification()
                showVerificationPending(email)
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthUserCollisionException) {
                    signInExistingUser(email, password)
                } else {
                    setFormEnabled(true)
                    binding.passwordLayout.error = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "Password is too weak"
                        else -> exception.localizedMessage ?: "Something went wrong. Try again."
                    }
                }
            }
    }

    private fun signInExistingUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                setFormEnabled(true)
                val user = result.user ?: return@addOnSuccessListener
                if (user.isEmailVerified) {
                    persistSessionAndOpenDashboard(
                        user.displayName ?: binding.editName.text.toString().trim(),
                        email
                    )
                } else {
                    user.sendEmailVerification()
                    showVerificationPending(email)
                }
            }
            .addOnFailureListener { exception ->
                setFormEnabled(true)
                binding.passwordLayout.error = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password for this email"
                    is FirebaseAuthInvalidUserException -> "No account found for this email"
                    else -> exception.localizedMessage ?: "Something went wrong. Try again."
                }
            }
    }

    private fun checkVerificationAndContinue() {
        val user = auth.currentUser
        if (user == null) {
            isAwaitingVerification = false
            return
        }

        setFormEnabled(false)

        user.reload()
            .addOnSuccessListener {
                setFormEnabled(true)
                if (user.isEmailVerified) {
                    persistSessionAndOpenDashboard(user.displayName.orEmpty(), user.email.orEmpty())
                } else {
                    showStatusMessage("Still not verified. Check your inbox and tap the link, then try again.")
                }
            }
            .addOnFailureListener {
                setFormEnabled(true)
                showStatusMessage(it.localizedMessage ?: "Couldn't check verification status. Try again.")
            }
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser ?: return

        user.sendEmailVerification()
            .addOnSuccessListener {
                showStatusMessage("Verification email sent to ${user.email}.")
            }
            .addOnFailureListener {
                showStatusMessage(it.localizedMessage ?: "Couldn't send email. Try again shortly.")
            }
    }

    private fun showVerificationPending(email: String) {
        isAwaitingVerification = true

        binding.nameLayout.visibility = View.GONE
        binding.emailLayout.visibility = View.GONE
        binding.passwordLayout.visibility = View.GONE

        binding.tvResend.visibility = View.VISIBLE
        binding.tvUseDifferentEmail.visibility = View.VISIBLE
        binding.btnContinue.text = "I've verified — Continue"

        showStatusMessage("We sent a verification link to $email. Verify it, then tap Continue.")
    }

    private fun resetToLoginForm() {
        auth.signOut()
        isAwaitingVerification = false

        binding.editEmail.text?.clear()
        binding.editPassword.text?.clear()
        binding.emailLayout.error = null
        binding.passwordLayout.error = null

        binding.nameLayout.visibility = View.VISIBLE
        binding.emailLayout.visibility = View.VISIBLE
        binding.passwordLayout.visibility = View.VISIBLE

        binding.tvResend.visibility = View.GONE
        binding.tvUseDifferentEmail.visibility = View.GONE
        binding.btnContinue.text = "Enter FarmLog  →"

        hideStatusMessage()
        setFormEnabled(true)
    }

    private fun showStatusMessage(message: String) {
        binding.tvStatusMessage.text = message
        binding.tvStatusMessage.visibility = View.VISIBLE
    }

    private fun hideStatusMessage() {
        binding.tvStatusMessage.visibility = View.GONE
    }

    private fun setFormEnabled(enabled: Boolean) {
        binding.btnContinue.isEnabled = enabled
        binding.editName.isEnabled = enabled
        binding.editEmail.isEnabled = enabled
        binding.editPassword.isEnabled = enabled
    }

    private fun persistSessionAndOpenDashboard(name: String, email: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()

        openDashboard()
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

        // Stricter than Patterns.EMAIL_ADDRESS: rejects leading/trailing dots,
        // consecutive dots, and requires an alphabetic TLD of at least 2 chars.
        private val EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9](?:[A-Za-z0-9._%+-]*[A-Za-z0-9])?@" +
                "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?" +
                "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)*" +
                "\\.[A-Za-z]{2,}$"
        )
    }
}