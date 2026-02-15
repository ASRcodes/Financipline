package com.example.financipline

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financipline.databinding.FragmentDashboardBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var pendingAdapter: PendingExpenseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure Horizontal Layout for Action Required
        binding.rvPendingExpenses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 1. Check & Request Notification Permission
        checkNotificationPermission()

        // 2. Load User Data and Calculate Progress
        loadDashboardData()
    }

    private fun checkNotificationPermission() {
        val enabledListeners = Settings.Secure.getString(requireContext().contentResolver, "enabled_notification_listeners")
        val isEnabled = enabledListeners?.contains(requireContext().packageName) == true

        if (!isEnabled) {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Auto-Tracking")
            .setMessage("To keep your streak alive automatically, Financipline needs to hear your payment notifications. Enable it in the next screen!")
            .setPositiveButton("Go to Settings") { _, _ ->
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
            .setNegativeButton("Maybe Later", null)
            .show()
    }

    private fun loadDashboardData() {
        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id ?: return

        lifecycleScope.launch {
            try {
                // Fetch Profile for Daily Limit
                val profileResponse = SupabaseManager.client.postgrest["profiles"]
                    .select { filter { eq("id", userId) } }

                // Fetch Today's Expenses
                val expensesResponse = SupabaseManager.client.postgrest["expenses"]
                    .select { filter { eq("user_id", userId) } }

                val allExpenses = JSONArray(expensesResponse.data)
                val pendingList = mutableListOf<JSONObject>()
                val confirmedList = mutableListOf<JSONObject>()

                // Split expenses into Pending (Action Required) and Confirmed (Math)
                for (i in 0 until allExpenses.length()) {
                    val item = allExpenses.getJSONObject(i)
                    if (item.optBoolean("is_pending", true)) {
                        pendingList.add(item)
                    } else {
                        confirmedList.add(item)
                    }
                }

                // Initialize/Update the Action Required Adapter
                pendingAdapter = PendingExpenseAdapter(pendingList) {
                    loadDashboardData() // Refresh logic when a card is categorized
                }
                binding.rvPendingExpenses.adapter = pendingAdapter

                // Update the Progress Ring and Status
                updateUI(profileResponse.data, confirmedList)

            } catch (e: Exception) {
                Toast.makeText(context, "Sync Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(profileRaw: String, confirmedList: List<JSONObject>) {
        try {
            // 1. Parse Profile Data
            val profileArray = JSONArray(profileRaw)
            if (profileArray.length() == 0) return

            val profileJson = profileArray.getJSONObject(0)
            val dailyLimit = profileJson.optDouble("daily_limit", 0.0)
            val currentStreak = profileJson.optInt("current_streak", 0)

            // 2. Sum up Confirmed Expenses
            var totalSpentToday = 0.0
            for (item in confirmedList) {
                totalSpentToday += item.getDouble("amount")
            }

            val remaining = dailyLimit - totalSpentToday

            // 3. Update Text Views
            binding.tvRemainingAmount.text = "₹${String.format("%.0f", remaining)}"
            binding.tvStreakDisplay.text = "🔥 $currentStreak Day Saving Streak!"

            // 4. Update Progress Ring
            val progressPercent = if (dailyLimit > 0) {
                ((totalSpentToday / dailyLimit) * 100).toInt().coerceAtMost(100)
            } else 0
            binding.budgetProgressRing.progress = progressPercent

            // 5. Overspending Color Alert
            if (remaining < 0) {
                binding.tvRemainingAmount.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                binding.tvRemainingAmount.setTextColor(resources.getColor(com.google.android.material.R.color.design_default_color_primary))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData() // Auto-refresh when user opens the app after a notification
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}