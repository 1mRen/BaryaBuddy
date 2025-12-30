package com.baryabuddy.app.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index + 1 == uiState.currentStep) 32.dp else 12.dp)
                        .height(12.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Progress dot
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Step Content
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                when (uiState.currentStep) {
                    1 -> Step1Content(
                        income = uiState.monthlyIncome,
                        error = uiState.incomeError,
                        onIncomeChange = { viewModel.setMonthlyIncome(it) }
                    )
                    2 -> Step2Content(
                        bills = uiState.fixedBills,
                        error = uiState.billsError,
                        onBillsChange = { viewModel.setFixedBills(it) }
                    )
                    3 -> Step3Content(
                        goal = uiState.savingsGoal,
                        goalError = uiState.goalError,
                        validationError = uiState.validationError,
                        onGoalChange = { viewModel.setSavingsGoal(it) },
                        onSave = {
                            scope.launch {
                                val success = viewModel.validateAndSave()
                                if (success) {
                                    onComplete()
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.currentStep > 1) {
                TextButton(onClick = { viewModel.previousStep() }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (uiState.currentStep < 3) {
                Button(onClick = { viewModel.nextStep() }) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.validateAndSave()
                            if (success) {
                                onComplete()
                            }
                        }
                    }
                ) {
                    Text("Complete")
                }
            }
        }
    }
}

@Composable
fun Step1Content(
    income: String,
    error: String?,
    onIncomeChange: (String) -> Unit
) {
    Column {
        Text(
            text = "What is your monthly income?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = income,
            onValueChange = onIncomeChange,
            label = { Text("Monthly Income") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )
    }
}

@Composable
fun Step2Content(
    bills: String,
    error: String?,
    onBillsChange: (String) -> Unit
) {
    Column {
        Text(
            text = "What are your fixed bills?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = bills,
            onValueChange = onBillsChange,
            label = { Text("Fixed Bills") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )
    }
}

@Composable
fun Step3Content(
    goal: String,
    goalError: String?,
    validationError: String?,
    onGoalChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column {
        Text(
            text = "What is your savings goal?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = goal,
            onValueChange = onGoalChange,
            label = { Text("Savings Goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = goalError != null || validationError != null,
            supportingText = (goalError ?: validationError)?.let { { Text(it) } }
        )
        if (validationError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validationError,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

