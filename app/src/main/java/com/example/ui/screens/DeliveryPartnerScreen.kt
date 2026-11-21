package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ApprovalStatus
import com.example.data.ChakaiLocations
import com.example.data.DeliveryPartnerEntity
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.UserRole
import com.example.data.VehicleType
import com.example.ui.components.ChakaiTopBar
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PendingApprovalScreen
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.ChakaiOrangeContainer
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.KiranaGreenContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusRed
import com.example.viewmodel.ChakaiUiState
import com.example.viewmodel.ChakaiViewModel

@Composable
fun DeliveryPartnerScreen(
    uiState: ChakaiUiState,
    viewModel: ChakaiViewModel
) {
    val myProfiles = remember(uiState.deliveryPartners, uiState.session.mobileNumber) {
        uiState.deliveryPartners.filter { it.mobile == uiState.session.mobileNumber }
    }

    var selectedProfileId by remember(myProfiles) { mutableStateOf(myProfiles.firstOrNull()?.id) }
    var showOnboarding by remember(myProfiles) { mutableStateOf(myProfiles.isEmpty()) }
    var activeTab by remember { mutableIntStateOf(0) } // 0=Available & Active, 1=Completed History

    val activePartner = myProfiles.firstOrNull { it.id == selectedProfileId } ?: myProfiles.firstOrNull()

    Scaffold(
        topBar = {
            ChakaiTopBar(
                activeRole = UserRole.DELIVERY_PARTNER,
                mobileNumber = uiState.session.mobileNumber,
                pendingApprovalsCount = uiState.totalPendingApprovals,
                onSwitchRole = { viewModel.selectRole(it) },
                onGoToRolePicker = { viewModel.selectRole(null) },
                onResetDemoData = { viewModel.resetDemoData() },
                onLogout = { viewModel.logout() },
                subtitle = activePartner?.let { "${it.fullName} • ${it.vehicleEnum.label}" } ?: "Partner Onboarding"
            )
        },
        bottomBar = {
            if (!showOnboarding && activePartner != null && activePartner.statusEnum == ApprovalStatus.APPROVED) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Default.DeliveryDining, contentDescription = "Available Deliveries") },
                        label = { Text("Deliveries (डिलीवरी)") },
                        modifier = Modifier.testTag("delivery_tab_available")
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.History, contentDescription = "Completed") },
                        label = { Text("History (${activePartner.completedDeliveries})") },
                        modifier = Modifier.testTag("delivery_tab_history")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (myProfiles.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    myProfiles.forEach { partner ->
                        FilterChip(
                            selected = !showOnboarding && activePartner?.id == partner.id,
                            onClick = {
                                selectedProfileId = partner.id
                                showOnboarding = false
                            },
                            label = { Text("${partner.fullName} (${partner.statusEnum.name.take(3)})") }
                        )
                    }
                    FilterChip(
                        selected = showOnboarding,
                        onClick = { showOnboarding = true },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("New Partner Registration") },
                        modifier = Modifier.testTag("delivery_new_registration_chip")
                    )
                }
            }

            when {
                showOnboarding || activePartner == null -> {
                    DeliveryOnboardingForm(
                        mobileNumber = uiState.session.mobileNumber,
                        onSubmit = { fullName, vehicle, villages ->
                            viewModel.registerDeliveryPartner(
                                fullName = fullName,
                                mobile = uiState.session.mobileNumber,
                                vehicleType = vehicle,
                                villages = villages,
                                onRegistered = { newId ->
                                    selectedProfileId = newId
                                    showOnboarding = false
                                }
                            )
                        }
                    )
                }
                activePartner.statusEnum != ApprovalStatus.APPROVED -> {
                    PendingApprovalScreen(
                        partnerTitle = "${activePartner.fullName} (${activePartner.vehicleEnum.label})",
                        partnerSubtitle = "Preferred Villages: ${activePartner.preferredVillagesCsv}",
                        status = activePartner.statusEnum,
                        onSwitchToAdmin = { viewModel.selectRole(UserRole.ADMIN) },
                        onRegisterAnother = { showOnboarding = true }
                    )
                }
                activeTab == 0 -> {
                    DeliveryAvailableTab(
                        partner = activePartner,
                        orders = uiState.orders,
                        onToggleOnline = { online -> viewModel.toggleDeliveryOnline(activePartner, online) },
                        onAcceptDelivery = { order -> viewModel.acceptDelivery(order, activePartner) },
                        onMarkPickedUp = { order -> viewModel.markOrderPickedUp(order) },
                        onMarkDelivered = { order -> viewModel.markOrderDelivered(order, activePartner) }
                    )
                }
                else -> {
                    val completed = uiState.orders.filter {
                        it.deliveryPartnerId == activePartner.id && it.statusEnum == OrderStatus.DELIVERED
                    }
                    DeliveryHistoryTab(partner = activePartner, completedOrders = completed)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeliveryOnboardingForm(
    mobileNumber: String,
    onSubmit: (fullName: String, vehicle: VehicleType, villages: List<String>) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf(VehicleType.BIKE) }
    var selectedVillages by remember { mutableStateOf(setOf("Chakai Chowk", "Madhopur", "Bamdah")) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = StatusBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delivery Partner Registration (डिलीवरी पार्टनर)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Earn per delivery in Chakai & nearby villages within 20 km. Status starts as \"Pending Admin Approval\".",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; errorMsg = null },
                    label = { Text("Full Name (पूरा नाम)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delivery_fullname_input")
                )
                OutlinedTextField(
                    value = "+91 $mobileNumber",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mobile Number (ऑटो-फ़िल्ड)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Vehicle Type (वाहन चुनें)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VehicleType.entries.forEach { vehicle ->
                        FilterChip(
                            selected = vehicleType == vehicle,
                            onClick = { vehicleType = vehicle },
                            label = { Text(vehicle.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = "Preferred Delivery Villages (20 km of Chakai)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChakaiLocations.villages.forEach { village ->
                        val isSelected = selectedVillages.contains(village.nameEn)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedVillages = if (isSelected) {
                                    selectedVillages - village.nameEn
                                } else {
                                    selectedVillages + village.nameEn
                                }
                            },
                            label = { Text("${village.nameEn} (${village.distanceKm}km)", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = StatusRed, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> errorMsg = "Please enter your full name"
                            selectedVillages.isEmpty() -> errorMsg = "Select at least one village area"
                            else -> onSubmit(fullName, vehicleType, selectedVillages.toList())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("delivery_register_submit")
                ) {
                    Text("Submit for Admin Approval", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun DeliveryAvailableTab(
    partner: DeliveryPartnerEntity,
    orders: List<OrderEntity>,
    onToggleOnline: (Boolean) -> Unit,
    onAcceptDelivery: (OrderEntity) -> Unit,
    onMarkPickedUp: (OrderEntity) -> Unit,
    onMarkDelivered: (OrderEntity) -> Unit
) {
    val myActiveOrders = remember(orders, partner.id) {
        orders.filter {
            it.deliveryPartnerId == partner.id &&
                (it.statusEnum == OrderStatus.DELIVERY_ACCEPTED || it.statusEnum == OrderStatus.PICKED_UP)
        }
    }
    val readyOrders = remember(orders) {
        orders.filter { it.statusEnum == OrderStatus.READY_FOR_PICKUP }
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Online / Offline Availability Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (partner.isOnline) KiranaGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.2.dp,
                    if (partner.isOnline) KiranaGreen else MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (partner.isOnline) KiranaGreen else StatusRed)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (partner.isOnline) "ONLINE • डिलीवरी के लिए उपलब्ध" else "OFFLINE • अभी ऑफलाइन हैं",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (partner.isOnline) KiranaGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${partner.fullName} • ${partner.vehicleEnum.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = partner.isOnline,
                        onCheckedChange = onToggleOnline,
                        colors = SwitchDefaults.colors(checkedTrackColor = KiranaGreen),
                        modifier = Modifier.testTag("delivery_online_switch")
                    )
                }
            }
        }

        // Earnings & Stats Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${partner.completedDeliveries}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Deliveries Done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = KiranaGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${partner.totalEarnings}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = KiranaGreen
                        )
                        Text(
                            text = "Total Earnings (कमाई)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Active Assigned Deliveries
        if (myActiveOrders.isNotEmpty()) {
            item {
                Text(
                    text = "Active Delivery In Progress (${myActiveOrders.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StatusBlue
                )
            }
            items(myActiveOrders, key = { "active_${it.id}" }) { order ->
                DeliveryOrderCard(
                    order = order,
                    onAcceptDelivery = {},
                    onMarkPickedUp = { onMarkPickedUp(order) },
                    onMarkDelivered = { onMarkDelivered(order) }
                )
            }
        }

        // Available Ready-for-Pickup Deliveries
        item {
            Text(
                text = "Available Deliveries Near Chakai (${if (partner.isOnline) readyOrders.size else 0})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (!partner.isOnline) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.DeliveryDining,
                    title = "You are Offline",
                    subtitle = "Turn ON the Online switch above to receive ready-for-pickup orders in Chakai."
                )
            }
        } else if (readyOrders.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.LocalShipping,
                    title = "No Orders Ready for Pickup Right Now",
                    subtitle = "When a Restaurant or Kirana marks an order 'Ready for Pickup', it appears here instantly."
                )
            }
        } else {
            items(readyOrders, key = { "ready_${it.id}" }) { order ->
                DeliveryOrderCard(
                    order = order,
                    onAcceptDelivery = { onAcceptDelivery(order) },
                    onMarkPickedUp = {},
                    onMarkDelivered = {}
                )
            }
        }
    }
}

@Composable
private fun DeliveryOrderCard(
    order: OrderEntity,
    onAcceptDelivery: () -> Unit,
    onMarkPickedUp: () -> Unit,
    onMarkDelivered: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Surface(
                    color = KiranaGreenContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Earn ₹${order.deliveryEarning}",
                        style = MaterialTheme.typography.labelLarge,
                        color = KiranaGreen,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            OrderStatusBadge(status = order.statusEnum)

            // Pickup Info
            Surface(
                color = ChakaiOrangeContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = ChakaiOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("PICKUP: ${order.storeName}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(order.storeAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Drop Village Info
            Surface(
                color = StatusBlueContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DROP: ${order.villageArea} (${order.distanceKm} km from Chakai)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${order.customerName} • ${order.landmark}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+91 ${order.customerMobile} • ${order.paymentEnum.labelEn} (₹${order.grandTotal})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            when (order.statusEnum) {
                OrderStatus.READY_FOR_PICKUP -> {
                    Button(
                        onClick = onAcceptDelivery,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_accept_${order.id}")
                    ) {
                        Text("Accept Delivery (डिलीवरी स्वीकार करें)")
                    }
                }
                OrderStatus.DELIVERY_ACCEPTED -> {
                    Button(
                        onClick = onMarkPickedUp,
                        colors = ButtonDefaults.buttonColors(containerColor = ChakaiOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_pickup_${order.id}")
                    ) {
                        Text("Mark Picked Up (ऑर्डर पिकअप किया)")
                    }
                }
                OrderStatus.PICKED_UP -> {
                    Button(
                        onClick = onMarkDelivered,
                        colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_complete_${order.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Delivered • Collect ₹${order.deliveryEarning}")
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun DeliveryHistoryTab(
    partner: DeliveryPartnerEntity,
    completedOrders: List<OrderEntity>
) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Completed Deliveries • Total Earned ₹${partner.totalEarnings}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (completedOrders.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.History,
                    title = "No Completed Orders in This Session",
                    subtitle = "Lifetime completed count: ${partner.completedDeliveries} deliveries (₹${partner.totalEarnings})."
                )
            }
        } else {
            items(completedOrders, key = { it.id }) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${order.orderCode} • ${order.storeName}", fontWeight = FontWeight.Bold)
                            Text(
                                "Dropped at ${order.villageArea} (${order.distanceKm} km)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "+₹${order.deliveryEarning}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = KiranaGreen
                        )
                    }
                }
            }
        }
    }
}
