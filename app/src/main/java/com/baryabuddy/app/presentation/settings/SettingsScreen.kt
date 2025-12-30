package com.baryabuddy.app.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onSettingsSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Budget Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Monthly Income
                OutlinedTextField(
                    value = uiState.monthlyIncome,
                    onValueChange = { viewModel.setMonthlyIncome(it) },
                    label = { Text("Monthly Income") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.incomeError != null,
                    supportingText = uiState.incomeError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fixed Bills
                OutlinedTextField(
                    value = uiState.fixedBills,
                    onValueChange = { viewModel.setFixedBills(it) },
                    label = { Text("Fixed Bills") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.billsError != null,
                    supportingText = uiState.billsError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Savings Goal
                OutlinedTextField(
                    value = uiState.savingsGoal,
                    onValueChange = { viewModel.setSavingsGoal(it) },
                    label = { Text("Savings Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.goalError != null,
                    supportingText = uiState.goalError?.let { { Text(it) } }
                )

                uiState.validationError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.saveProfile()
                            if (success) {
                                onSettingsSaved()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Settings")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

