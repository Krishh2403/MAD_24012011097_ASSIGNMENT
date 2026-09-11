package com.example.mad_24012011097_assignment


import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
class MainActivity : AppCompatActivity() {

    private val viewModel: FarmViewModel by viewModels()

    private lateinit var rootView: View
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerLogs: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnSummary: MaterialButton
    private lateinit var filterGroup: ChipGroup
    private lateinit var tvTotalLogs: TextView
    private lateinit var tvActiveCrops: TextView
    private lateinit var tvEmpty: TextView

    private lateinit var farmLogAdapter: FarmLogAdapter

    private var allLogs: List<FarmLog> = emptyList()
    private var selectedFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        rootView = findViewById(android.R.id.content)

        toolbar = findViewById(R.id.toolbar)
        recyclerLogs = findViewById(R.id.recyclerLogs)
        fabAdd = findViewById(R.id.fabAdd)
        btnSummary = findViewById(R.id.btnSummary)
        filterGroup = findViewById(R.id.filterGroup)
        tvTotalLogs = findViewById(R.id.tvTotalLogs)
        tvActiveCrops = findViewById(R.id.tvActiveCrops)
        tvEmpty = findViewById(R.id.tvEmpty)

        val currentEmail = getSharedPreferences(
            "farmlog_preferences",
            MODE_PRIVATE
        ).getString("user_email", "")
            ?.trim()
            ?.lowercase()
            .orEmpty()

        if (currentEmail.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        viewModel.setCurrentOwner(currentEmail)

        applySystemBarInsets()
        setupUserSession()
        setupRecyclerView()
        setupButtons()
        observeFarmLogs()

        rootView.post {
            playDashboardEntrance()
        }
    }

    private fun setupUserSession() {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val userName = preferences
            .getString(KEY_USER_NAME, "")
            .orEmpty()
            .trim()

        toolbar.menu.clear()

        toolbar.menu.add(
            0,
            MENU_LOGOUT,
            0,
            "Log out"
        ).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == MENU_LOGOUT) {
                confirmLogout()
                true
            } else {
                false
            }
        }

        if (userName.isNotEmpty()) {
            val firstName = userName.substringBefore(" ")

            toolbar.subtitle = "Welcome back, $firstName"

            rootView.postDelayed({
                showWelcomeMessage(firstName)
            }, 650L)
        }
    }

    private fun showWelcomeMessage(firstName: String) {
        Snackbar.make(
            rootView,
            "Welcome back, $firstName  🌿  Your farm is ready.",
            Snackbar.LENGTH_LONG
        )
            .setBackgroundTint(Color.parseColor("#176B3A"))
            .setTextColor(Color.WHITE)
            .setAction("INSIGHTS") {
                startActivity(Intent(this, SummaryActivity::class.java))
            }
            .setActionTextColor(Color.parseColor("#FFE0B2"))
            .show()
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Log out of FarmLog?")
            .setMessage(
                "You can sign in again anytime. Your saved farm logs will stay on this device."
            )
            .setNegativeButton("Stay logged in", null)
            .setPositiveButton("Log out") { _, _ ->
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun setupRecyclerView() {
        farmLogAdapter = FarmLogAdapter(
            onLogClick = { log ->
                showLogDetails(log)
            },
            onDeleteClick = { log ->
                confirmDelete(log)
            }
        )

        recyclerLogs.layoutManager = LinearLayoutManager(this)
        recyclerLogs.adapter = farmLogAdapter
    }

    private fun setupButtons() {
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddLogActivity::class.java))
        }

        btnSummary.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        filterGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedFilter = when (checkedId) {
                R.id.chipSowing -> "Sowing"
                R.id.chipHarvesting -> "Harvesting"
                R.id.chipIrrigation -> "Irrigation"
                R.id.chipFertilizer -> "Fertilizer"
                else -> "All"
            }

            showFilteredLogs()
        }
    }

    private fun observeFarmLogs() {
        viewModel.allLogs.observe(this) { logs ->
            allLogs = logs
            updateDashboardStatistics()
            showFilteredLogs()
        }
    }

    private fun showFilteredLogs() {
        val filteredLogs = if (selectedFilter == "All") {
            allLogs
        } else {
            allLogs.filter { it.activityType == selectedFilter }
        }

        farmLogAdapter.submitList(filteredLogs)
        tvEmpty.isVisible = filteredLogs.isEmpty()
    }

    private fun updateDashboardStatistics() {
        tvTotalLogs.text = allLogs.size.toString()

        val activeCrops = allLogs
            .map { it.cropName.trim().lowercase() }
            .distinct()
            .size

        tvActiveCrops.text = activeCrops.toString()
    }

    private fun showLogDetails(log: FarmLog) {
        MaterialAlertDialogBuilder(this)
            .setTitle("${log.cropName} • ${log.activityType}")
            .setMessage(
                "Date: ${log.date}\n\n" +
                        "Notes:\n${log.notes.ifBlank { "No notes added." }}"
            )
            .setNegativeButton("Close", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLog(log)
            }
            .show()
    }

    private fun confirmDelete(log: FarmLog) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete farm log?")
            .setMessage(
                "Do you want to remove the ${log.activityType} activity for ${log.cropName}?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLog(log)
            }
            .show()
    }

    private fun applySystemBarInsets() {
        val toolbarOriginalTopPadding = toolbar.paddingTop
        val recyclerOriginalBottomPadding = recyclerLogs.paddingBottom

        val fabOriginalBottomMargin =
            (fabAdd.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        WindowCompat.getInsetsController(window, rootView)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, rootView)
            .isAppearanceLightNavigationBars = true

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                top = toolbarOriginalTopPadding + systemBars.top
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(recyclerLogs) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                bottom = recyclerOriginalBottomPadding + systemBars.bottom
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(fabAdd) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = fabOriginalBottomMargin + systemBars.bottom
            }

            insets
        }

        ViewCompat.requestApplyInsets(rootView)
    }

    private fun playDashboardEntrance() {
        val cardWelcome = findViewById<View>(R.id.cardWelcome)
        val statsRow = findViewById<View>(R.id.statsRow)
        val filterLabel = findViewById<View>(R.id.tvFilterLabel)
        val filterHint = findViewById<View>(R.id.tvFilterHint)
        val filterScroll = findViewById<View>(R.id.filterScroll)

        val sections = listOf(
            cardWelcome,
            statsRow,
            btnSummary,
            filterLabel,
            filterHint,
            filterScroll
        )

        sections.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 32f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 70L)
                .setDuration(430L)
                .setInterpolator(DecelerateInterpolator(1.8f))
                .start()
        }

        fabAdd.apply {
            alpha = 0f
            scaleX = 0.65f
            scaleY = 0.65f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(380L)
                .setDuration(420L)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    companion object {
        private const val PREFS_NAME = "farmlog_preferences"
        private const val KEY_USER_NAME = "user_name"
        private const val MENU_LOGOUT = 1001
    }
}