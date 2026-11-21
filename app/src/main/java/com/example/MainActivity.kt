package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.StoreType
import com.example.data.UserRole
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.DeliveryPartnerScreen
import com.example.ui.screens.OtpLoginScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.StorePartnerScreen
import com.example.ui.theme.ChakaiGoTheme
import com.example.ui.theme.ChakaiOrange
import com.example.viewmodel.ChakaiViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChakaiGoTheme {
                ChakaiGoApp()
            }
        }
    }
}

@Composable
fun ChakaiGoApp(viewModel: ChakaiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!uiState.isReady) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = ChakaiOrange)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading ChakaiGo • चकाईगो...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (!uiState.session.isLoggedIn) {
            OtpLoginScreen(
                onLoginVerified = { mobile, role ->
                    viewModel.completeOtpLogin(mobile, role)
                }
            )
        } else if (uiState.session.activeRole == null) {
            RoleSelectionScreen(
                mobileNumber = uiState.session.mobileNumber,
                pendingApprovalsCount = uiState.totalPendingApprovals,
                onSelectRole = { role -> viewModel.selectRole(role) },
                onLogout = { viewModel.logout() }
            )
        } else {
            // System back returns from active dashboard to Role Selection screen
            BackHandler {
                viewModel.selectRole(null)
            }
            when (uiState.session.activeRole) {
                UserRole.CUSTOMER -> CustomerScreen(uiState = uiState, viewModel = viewModel)
                UserRole.RESTAURANT -> StorePartnerScreen(
                    storeType = StoreType.RESTAURANT,
                    uiState = uiState,
                    viewModel = viewModel
                )
                UserRole.GENERAL_STORE -> StorePartnerScreen(
                    storeType = StoreType.GENERAL_STORE,
                    uiState = uiState,
                    viewModel = viewModel
                )
                UserRole.DELIVERY_PARTNER -> DeliveryPartnerScreen(uiState = uiState, viewModel = viewModel)
                UserRole.ADMIN -> AdminScreen(uiState = uiState, viewModel = viewModel)
                null -> Unit
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 76.dp)
        )
    }
}
