package com.example.mad_24012011097_assignment

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad_24012011097_assignment.databinding.ActivitySummaryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private val viewModel: FarmViewModel by viewModels()
    private lateinit var farmLogAdapter: FarmLogAdapter

    private var allLogs: List<FarmLog> = emptyList()
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentEmail = getSharedPreferences(
            "farmlog_preferences",
            MODE_PRIVATE
        ).getString("user_email", "")
            ?.trim()
            ?.lowercase()
            .orEmpty()

        if (currentEmail.isBlank()) {
            finish()
            return
        }

        viewModel.setCurrentOwner(currentEmail)

        applySystemBarInsets()
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        observeLogs()
    }

    private fun applySystemBarInsets() {
        val toolbarOriginalTopPadding = binding.toolbarSummary.paddingTop
        val recyclerOriginalBottomPadding = binding.recyclerRecent.paddingBottom

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightNavigationBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarSummary) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                top = toolbarOriginalTopPadding + systemBars.top
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerRecent) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                bottom = recyclerOriginalBottomPadding + systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setupToolbar() {
        binding.toolbarSummary.setNavigationIcon(
            android.R.drawable.ic_menu_close_clear_cancel
        )

        binding.toolbarSummary.setNavigationOnClickListener {
            finish()
        }
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

        binding.recyclerRecent.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecent.adapter = farmLogAdapter
    }

    private fun setupSearch() {
        binding.editSearch.doAfterTextChanged { editable ->
            searchText = editable?.toString()?.trim().orEmpty()
            showFilteredLogs()
        }
    }

    private fun observeLogs() {
        viewModel.allLogs.observe(this) { logs ->
            allLogs = logs ?: emptyList()
            updateActivityDistribution()
            showFilteredLogs()
        }
    }

    private fun updateActivityDistribution() {
        val sowingCount = allLogs.count { it.activityType == "Sowing" }
        val harvestingCount = allLogs.count { it.activityType == "Harvesting" }
        val irrigationCount = allLogs.count { it.activityType == "Irrigation" }
        val fertilizerCount = allLogs.count { it.activityType == "Fertilizer" }

        val totalLogs = allLogs.size.coerceAtLeast(1)

        binding.tvSowing.text = "Sowing: $sowingCount"
        binding.tvHarvesting.text = "Harvesting: $harvestingCount"
        binding.tvIrrigation.text = "Irrigation: $irrigationCount"
        binding.tvFertilizer.text = "Fertilizer: $fertilizerCount"

        binding.progressSowing.progress = sowingCount * 100 / totalLogs
        binding.progressHarvesting.progress = harvestingCount * 100 / totalLogs
        binding.progressIrrigation.progress = irrigationCount * 100 / totalLogs
        binding.progressFertilizer.progress = fertilizerCount * 100 / totalLogs
    }

    private fun showFilteredLogs() {
        val query = searchText.lowercase()

        val filteredLogs = allLogs.filter { log ->
            log.cropName.lowercase().contains(query) ||
                    log.notes.lowercase().contains(query) ||
                    log.activityType.lowercase().contains(query)
        }

        farmLogAdapter.submitList(filteredLogs)
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
            .setMessage("Do you want to permanently remove this activity?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLog(log)
            }
            .show()
    }
}