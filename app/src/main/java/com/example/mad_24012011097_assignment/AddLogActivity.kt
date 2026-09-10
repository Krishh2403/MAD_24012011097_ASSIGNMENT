package com.example.mad_24012011097_assignment

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_24012011097_assignment.databinding.ActivityAddLogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import android.content.Intent
class AddLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLogBinding
    private val viewModel: FarmViewModel by viewModels()

    private val selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarInsets()

        setupToolbar()
        setupCropDropdown()
        setupDateField()

        binding.btnSave.setOnClickListener {
            saveFarmLog()
        }
    }
    private fun applySystemBarInsets() {
        val toolbarOriginalTopPadding = binding.toolbarAdd.paddingTop
        val saveButtonOriginalBottomMargin =
            (binding.btnSave.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, binding.root)
            .isAppearanceLightNavigationBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarAdd) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                top = toolbarOriginalTopPadding + systemBars.top
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnSave) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = saveButtonOriginalBottomMargin + systemBars.bottom
            }

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setupToolbar() {
        binding.toolbarAdd.setNavigationIcon(
            android.R.drawable.ic_menu_close_clear_cancel
        )

        binding.toolbarAdd.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupCropDropdown() {
        val crops = arrayOf(
            "Wheat",
            "Rice",
            "Corn",
            "Tomato",
            "Potato",
            "Carrot",
            "Sugarcane",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            crops
        )

        binding.cropInput.setAdapter(adapter)

        binding.cropInput.setOnClickListener {
            binding.cropInput.showDropDown()
        }
    }

    private fun setupDateField() {
        binding.editDate.setText(dateFormat.format(selectedDate.time))

        binding.editDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                binding.editDate.setText(dateFormat.format(selectedDate.time))
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveFarmLog() {
        val cropName = binding.cropInput.text.toString().trim()
        val notes = binding.editNotes.text.toString().trim()

        val activityType = when (binding.activityGroup.checkedChipId) {
            R.id.chipSowing -> "Sowing"
            R.id.chipHarvesting -> "Harvesting"
            R.id.chipIrrigation -> "Irrigation"
            R.id.chipFertilizer -> "Fertilizer"
            else -> ""
        }

        if (cropName.isEmpty()) {
            binding.cropLayout.error = "Please enter or choose a crop"
            return
        }

        binding.cropLayout.error = null

        if (activityType.isEmpty()) {
            Toast.makeText(
                this,
                "Please select an activity type",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val ownerId = getSharedPreferences(
            "farmlog_preferences",
            MODE_PRIVATE
        ).getString("user_email", "")
            ?.trim()
            ?.lowercase()
            .orEmpty()

        if (ownerId.isBlank()) {
            Toast.makeText(
                this,
                "Your session has expired. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val log = FarmLog(
            ownerId = ownerId,
            cropName = cropName,
            activityType = activityType,
            date = binding.editDate.text.toString(),
            notes = notes
        )

        viewModel.addLog(log)

        Toast.makeText(
            this,
            "Farm activity saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}