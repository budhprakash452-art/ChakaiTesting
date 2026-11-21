package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ApprovalStatus
import com.example.data.DeliveryPartnerEntity
import com.example.data.MenuItemEntity
import com.example.data.OrderEntity
import com.example.data.PresetImageKeys
import com.example.data.StoreEntity
import com.example.data.StoreType
import com.example.data.UserRole
import com.example.ui.components.ApprovalBadge
import com.example.ui.components.ChakaiTopBar
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SmartProductImage
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import com.example.ui.theme.WarmGold
import com.example.viewmodel.ChakaiUiState
import com.example.viewmodel.ChakaiViewModel

@Composable
fun AdminScreen(
    uiState: ChakaiUiState,
    viewModel: ChakaiViewModel
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0=Approvals, 1=All Stores & Menus, 2=Live Orders

    Scaffold(
        topBar = {
            ChakaiTopBar(
                activeRole = UserRole.ADMIN,
                mobileNumber = uiState.session.mobileNumber,
                pendingApprovalsCount = uiState.totalPendingApprovals,
                onSwitchRole = { viewModel.selectRole(it) },
                onGoToRolePicker = { viewModel.selectRole(null) },
                onResetDemoData = { viewModel.resetDemoData() },
                onLogout = { viewModel.logout() },
                subtitle = "Master Control • ChakaiGo Admin"
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.totalPendingApprovals > 0) {
                                    Badge(containerColor = StatusRed, contentColor = PureWhite) {
                                        Text("${uiState.totalPendingApprovals}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = "Approvals")
                        }
                    },
                    label = { Text("Approvals") },
                    modifier = Modifier.testTag("admin_tab_approvals")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Stores & Menus") },
                    label = { Text("Stores & Menus") },
                    modifier = Modifier.testTag("admin_tab_stores")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.MonitorHeart, contentDescription = "Live Orders") },
                    label = { Text("Live Orders") },
                    modifier = Modifier.testTag("admin_tab_orders")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top KPI Strip (Always visible across Admin tabs)
            AdminKpiStrip(uiState = uiState)

            when (activeTab) {
                0 -> AdminApprovalsTab(
                    stores = uiState.stores,
                    deliveryPartners = uiState.deliveryPartners,
                    onApproveStore = { viewModel.approveStore(it) },
                    onRejectStore = { viewModel.rejectStore(it) },
                    onApprovePartner = { viewModel.approveDeliveryPartner(it) },
                    onRejectPartner = { viewModel.rejectDeliveryPartner(it) }
                )
                1 -> AdminAllStoresMenusTab(
                    stores = uiState.stores,
                    menuItems = uiState.menuItems,
                    onApproveStore = { viewModel.approveStore(it) },
                    onRejectStore = { viewModel.rejectStore(it) },
                    onDeleteStore = { viewModel.deleteStore(it) },
                    onDeleteItem = { viewModel.deleteMenuItem(it.id) }
                )
                else -> AdminLiveOrdersTab(orders = uiState.orders)
            }
        }
    }
}

@Composable
private fun AdminKpiStrip(uiState: ChakaiUiState) {
    val activeDeliveryCount = uiState.deliveryPartners.count { it.statusEnum == ApprovalStatus.APPROVED }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KpiMiniCard("Customers", "${uiState.customers.size}", Icons.Default.People, StatusPurple)
        KpiMiniCard("Restaurants", "${uiState.approvedRestaurants.size}", Icons.Default.Restaurant, ChakaiOrange)
        KpiMiniCard("Kirana Stores", "${uiState.approvedGeneralStores.size}", Icons.Default.Storefront, KiranaGreen)
        KpiMiniCard("Delivery", "$activeDeliveryCount", Icons.Default.DeliveryDining, StatusBlue)
        KpiMiniCard("Total Orders", "${uiState.orders.size}", Icons.Default.ReceiptLong, WarmGold)
    }
}

@Composable
private fun KpiMiniCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.width(120.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminApprovalsTab(
    stores: List<StoreEntity>,
    deliveryPartners: List<DeliveryPartnerEntity>,
    onApproveStore: (StoreEntity) -> Unit,
    onRejectStore: (StoreEntity) -> Unit,
    onApprovePartner: (DeliveryPartnerEntity) -> Unit,
    onRejectPartner: (DeliveryPartnerEntity) -> Unit
) {
    var filter by remember { mutableStateOf("All") } // All, Restaurants, Kirana, Delivery
    val pendingRestaurants = stores.filter { it.typeEnum == StoreType.RESTAURANT && it.statusEnum == ApprovalStatus.PENDING }
    val pendingKirana = stores.filter { it.typeEnum == StoreType.GENERAL_STORE && it.statusEnum == ApprovalStatus.PENDING }
    val pendingDelivery = deliveryPartners.filter { it.statusEnum == ApprovalStatus.PENDING }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All" to (pendingRestaurants.size + pendingKirana.size + pendingDelivery.size),
                    "Restaurants" to pendingRestaurants.size,
                    "Kirana" to pendingKirana.size,
                    "Delivery" to pendingDelivery.size
                ).forEach { (name, count) ->
                    FilterChip(
                        selected = filter == name,
                        onClick = { filter = name },
                        label = { Text("$name ($count)") }
                    )
                }
            }
        }

        val totalFiltered = when (filter) {
            "Restaurants" -> pendingRestaurants.size
            "Kirana" -> pendingKirana.size
            "Delivery" -> pendingDelivery.size
            else -> pendingRestaurants.size + pendingKirana.size + pendingDelivery.size
        }

        if (totalFiltered == 0) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.FactCheck,
                    title = "All Caught Up! (कोई पेंडिंग रिक्वेस्ट नहीं)",
                    subtitle = "Every partner registration in this category has been reviewed."
                )
            }
        }

        if (filter == "All" || filter == "Restaurants") {
            if (pendingRestaurants.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Restaurants (${pendingRestaurants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChakaiOrange
                    )
                }
                items(pendingRestaurants, key = { "pr_${it.id}" }) { store ->
                    PendingStoreApprovalCard(
                        store = store,
                        onApprove = { onApproveStore(store) },
                        onReject = { onRejectStore(store) }
                    )
                }
            }
        }

        if (filter == "All" || filter == "Kirana") {
            if (pendingKirana.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending General Stores / Kirana (${pendingKirana.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = KiranaGreen
                    )
                }
                items(pendingKirana, key = { "pk_${it.id}" }) { store ->
                    PendingStoreApprovalCard(
                        store = store,
                        onApprove = { onApproveStore(store) },
                        onReject = { onRejectStore(store) }
                    )
                }
            }
        }

        if (filter == "All" || filter == "Delivery") {
            if (pendingDelivery.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Delivery Partners (${pendingDelivery.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusBlue
                    )
                }
                items(pendingDelivery, key = { "pd_${it.id}" }) { partner ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StatusBlue.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = StatusBlue)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(partner.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        "+91 ${partner.mobile} • ${partner.vehicleEnum.label}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ApprovalBadge(status = partner.statusEnum)
                            }
                            Text(
                                text = "Villages: ${partner.preferredVillagesCsv}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onRejectPartner(partner) },
                                    border = BorderStroke(1.dp, StatusRed),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_reject_partner_${partner.id}")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject", color = StatusRed)
                                }
                                Button(
                                    onClick = { onApprovePartner(partner) },
                                    colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_approve_partner_${partner.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingStoreApprovalCard(
    store: StoreEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isRestaurant = store.typeEnum == StoreType.RESTAURANT
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmartProductImage(
                    photoUri = store.photoUri,
                    contentDescription = store.name,
                    fallbackPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(store.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Owner: ${store.ownerName} • +91 ${store.ownerMobile}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        store.addressLandmark,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    border = BorderStroke(1.dp, StatusRed),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_reject_store_${store.id}")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", color = StatusRed)
                }
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_approve_store_${store.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
private fun AdminAllStoresMenusTab(
    stores: List<StoreEntity>,
    menuItems: List<MenuItemEntity>,
    onApproveStore: (StoreEntity) -> Unit,
    onRejectStore: (StoreEntity) -> Unit,
    onDeleteStore: (StoreEntity) -> Unit,
    onDeleteItem: (MenuItemEntity) -> Unit
) {
    var expandedStoreId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "All Registered Stores & Menus (${stores.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(stores, key = { it.id }) { store ->
            val itemsForStore = menuItems.filter { it.storeId == store.id }
            val isExpanded = expandedStoreId == store.id
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmartProductImage(
                            photoUri = store.photoUri,
                            contentDescription = store.name,
                            fallbackPreset = if (store.typeEnum == StoreType.RESTAURANT) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(store.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                "${store.typeEnum.labelEn} • ${store.ownerName} • ${itemsForStore.size} items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            ApprovalBadge(status = store.statusEnum)
                        }
                        IconButton(onClick = { onDeleteStore(store) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete store", tint = StatusRed)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { expandedStoreId = if (isExpanded) null else store.id },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isExpanded) "Hide Items (${itemsForStore.size})" else "Inspect Items (${itemsForStore.size})")
                        }
                        if (store.statusEnum != ApprovalStatus.APPROVED) {
                            Button(
                                onClick = { onApproveStore(store) },
                                colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen)
                            ) {
                                Text("Approve")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onRejectStore(store) },
                                border = BorderStroke(1.dp, StatusRed)
                            ) {
                                Text("Suspend", color = StatusRed)
                            }
                        }
                    }

                    if (isExpanded) {
                        HorizontalDivider()
                        if (itemsForStore.isEmpty()) {
                            Text(
                                text = "No menu/grocery items added yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            itemsForStore.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "${item.category} • ${item.unitOrDescription} • ₹${item.price}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { onDeleteItem(item) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = StatusRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminLiveOrdersTab(orders: List<OrderEntity>) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Live Orders Monitor (${orders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (orders.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.MonitorHeart,
                    title = "No Live Orders Yet",
                    subtitle = "Orders placed by Chakai customers will stream here in real time."
                )
            }
        } else {
            items(orders, key = { it.id }) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${order.orderCode} • ${order.storeName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "₹${order.grandTotal}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ChakaiOrange
                            )
                        }
                        OrderStatusBadge(status = order.statusEnum)
                        Text(
                            text = "Customer: ${order.customerName} (+91 ${order.customerMobile})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Village: ${order.villageArea} (${order.distanceKm} km) • ${order.landmark}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.itemsSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Delivery Partner: ${order.deliveryPartnerName ?: "Unassigned"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (order.deliveryPartnerName != null) KiranaGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${order.paymentEnum.labelEn} • Fee ₹${order.deliveryFee}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
