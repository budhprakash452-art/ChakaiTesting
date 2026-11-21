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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ApprovalStatus
import com.example.data.MenuItemEntity
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.PresetImageKeys
import com.example.data.StoreEntity
import com.example.data.StoreType
import com.example.data.UserRole
import com.example.ui.components.ApprovalBadge
import com.example.ui.components.ChakaiTopBar
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PendingApprovalScreen
import com.example.ui.components.PhotoPickerField
import com.example.ui.components.SmartProductImage
import com.example.ui.components.VegNonVegDot
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed
import com.example.viewmodel.ChakaiUiState
import com.example.viewmodel.ChakaiViewModel

@Composable
fun StorePartnerScreen(
    storeType: StoreType,
    uiState: ChakaiUiState,
    viewModel: ChakaiViewModel
) {
    val isRestaurant = storeType == StoreType.RESTAURANT
    val activeRole = if (isRestaurant) UserRole.RESTAURANT else UserRole.GENERAL_STORE
    val accentColor = if (isRestaurant) ChakaiOrange else KiranaGreen

    val myStores = remember(uiState.stores, uiState.session.mobileNumber, storeType) {
        uiState.stores.filter {
            it.typeEnum == storeType && it.ownerMobile == uiState.session.mobileNumber
        }
    }

    var selectedStoreId by remember(myStores) {
        mutableStateOf(myStores.firstOrNull()?.id)
    }
    var showNewRegistrationForm by remember(myStores) {
        mutableStateOf(myStores.isEmpty())
    }
    var activeTab by remember { mutableIntStateOf(0) } // 0=Menu/Inventory, 1=Incoming Orders
    var editingItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var showItemDialog by remember { mutableStateOf(false) }

    val activeStore = myStores.firstOrNull { it.id == selectedStoreId } ?: myStores.firstOrNull()

    Scaffold(
        topBar = {
            ChakaiTopBar(
                activeRole = activeRole,
                mobileNumber = uiState.session.mobileNumber,
                pendingApprovalsCount = uiState.totalPendingApprovals,
                onSwitchRole = { viewModel.selectRole(it) },
                onGoToRolePicker = { viewModel.selectRole(null) },
                onResetDemoData = { viewModel.resetDemoData() },
                onLogout = { viewModel.logout() },
                subtitle = activeStore?.name ?: "Partner Onboarding • Chakai"
            )
        },
        floatingActionButton = {
            if (!showNewRegistrationForm && activeStore != null && activeStore.statusEnum == ApprovalStatus.APPROVED && activeTab == 0) {
                FloatingActionButton(
                    onClick = {
                        editingItem = null
                        showItemDialog = true
                    },
                    containerColor = accentColor,
                    contentColor = PureWhite,
                    modifier = Modifier.testTag("partner_add_item_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        },
        bottomBar = {
            if (!showNewRegistrationForm && activeStore != null && activeStore.statusEnum == ApprovalStatus.APPROVED) {
                val incomingCount = uiState.orders.count {
                    it.storeId == activeStore.id && it.statusEnum != OrderStatus.DELIVERED
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = {
                            Icon(
                                if (isRestaurant) Icons.Default.RestaurantMenu else Icons.Default.Inventory2,
                                contentDescription = "Menu"
                            )
                        },
                        label = { Text(if (isRestaurant) "Menu Management" else "Inventory / Items") },
                        modifier = Modifier.testTag("partner_tab_items")
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                        label = { Text("Incoming Orders ($incomingCount)") },
                        modifier = Modifier.testTag("partner_tab_orders")
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
            // Store Switcher / Register New Store Bar
            if (myStores.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    myStores.forEach { store ->
                        FilterChip(
                            selected = !showNewRegistrationForm && activeStore?.id == store.id,
                            onClick = {
                                selectedStoreId = store.id
                                showNewRegistrationForm = false
                            },
                            label = {
                                Text(
                                    text = "${store.name} (${store.statusEnum.name.take(3)})",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                    FilterChip(
                        selected = showNewRegistrationForm,
                        onClick = { showNewRegistrationForm = true },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = {
                            Text(if (isRestaurant) "Register New Restaurant" else "Register New Store")
                        },
                        modifier = Modifier.testTag("register_new_store_chip")
                    )
                }
            }

            when {
                showNewRegistrationForm || activeStore == null -> {
                    StoreOnboardingForm(
                        storeType = storeType,
                        ownerMobile = uiState.session.mobileNumber,
                        onSubmit = { name, owner, address, photo ->
                            viewModel.registerStore(
                                storeType = storeType,
                                name = name,
                                ownerName = owner,
                                ownerMobile = uiState.session.mobileNumber,
                                addressLandmark = address,
                                photoUri = photo,
                                onRegistered = { newId ->
                                    selectedStoreId = newId
                                    showNewRegistrationForm = false
                                }
                            )
                        }
                    )
                }
                activeStore.statusEnum != ApprovalStatus.APPROVED -> {
                    PendingApprovalScreen(
                        partnerTitle = activeStore.name,
                        partnerSubtitle = "${activeStore.ownerName} • ${activeStore.addressLandmark}",
                        status = activeStore.statusEnum,
                        onSwitchToAdmin = { viewModel.selectRole(UserRole.ADMIN) },
                        onRegisterAnother = { showNewRegistrationForm = true }
                    )
                }
                activeTab == 0 -> {
                    val storeItems = uiState.menuItems.filter { it.storeId == activeStore.id }
                    StoreItemsManagementTab(
                        store = activeStore,
                        items = storeItems,
                        onAddClick = {
                            editingItem = null
                            showItemDialog = true
                        },
                        onEditClick = { item ->
                            editingItem = item
                            showItemDialog = true
                        },
                        onDeleteClick = { viewModel.deleteMenuItem(it.id) }
                    )
                }
                else -> {
                    val storeOrders = uiState.orders.filter { it.storeId == activeStore.id }
                    StoreIncomingOrdersTab(
                        store = activeStore,
                        orders = storeOrders,
                        onAccept = { viewModel.acceptStoreOrder(it) },
                        onMarkReady = { viewModel.markOrderReadyForPickup(it) }
                    )
                }
            }
        }
    }

    if (showItemDialog && activeStore != null) {
        MenuItemEditorDialog(
            storeType = storeType,
            storeId = activeStore.id,
            existingItem = editingItem,
            onDismiss = { showItemDialog = false },
            onSave = { item ->
                viewModel.saveMenuItem(item)
                showItemDialog = false
            }
        )
    }
}

@Composable
private fun StoreOnboardingForm(
    storeType: StoreType,
    ownerMobile: String,
    onSubmit: (name: String, ownerName: String, address: String, photoUri: String) -> Unit
) {
    val isRestaurant = storeType == StoreType.RESTAURANT
    val accent = if (isRestaurant) ChakaiOrange else KiranaGreen
    val defaultPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA

    var storeName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var addressLandmark by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf(defaultPreset) }
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
                    Icon(Icons.Default.Store, contentDescription = null, tint = accent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRestaurant) {
                            "Restaurant Registration (रेस्टोरेंट रजिस्ट्रेशन)"
                        } else {
                            "General Store / Kirana Registration"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Submit your shop details in Chakai. Status defaults to \"Pending Admin Approval\" until approved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it; errorMsg = null },
                    label = { Text(if (isRestaurant) "Restaurant Name (रेस्टोरेंट का नाम)" else "Store Name (दुकान का नाम)") },
                    placeholder = { Text(if (isRestaurant) "e.g., Chakai Tadka Dhaba" else "e.g., Verma Kirana Store") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_store_name")
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it; errorMsg = null },
                    label = { Text("Owner Name (मालिक का नाम)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_owner_name")
                )
                OutlinedTextField(
                    value = "+91 $ownerMobile",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mobile Number (ऑटो-फ़िल्ड)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = addressLandmark,
                    onValueChange = { addressLandmark = it; errorMsg = null },
                    label = { Text("Chakai Address / Landmark (पता व पहचान)") },
                    placeholder = { Text("e.g., Near Chakai Chowk, Bus Stand Road") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = accent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_store_address")
                )

                PhotoPickerField(
                    label = if (isRestaurant) "Restaurant Photo" else "Store Photo",
                    photoUri = photoUri,
                    onPhotoChanged = { photoUri = it },
                    defaultPreset = defaultPreset
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, color = StatusRed, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        when {
                            storeName.isBlank() -> errorMsg = "Please enter shop name"
                            ownerName.isBlank() -> errorMsg = "Please enter owner name"
                            addressLandmark.isBlank() -> errorMsg = "Please enter Chakai address/landmark"
                            else -> onSubmit(storeName, ownerName, addressLandmark, photoUri)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("onboarding_store_submit")
                ) {
                    Text("Submit for Admin Approval", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun StoreItemsManagementTab(
    store: StoreEntity,
    items: List<MenuItemEntity>,
    onAddClick: () -> Unit,
    onEditClick: (MenuItemEntity) -> Unit,
    onDeleteClick: (MenuItemEntity) -> Unit
) {
    val isRestaurant = store.typeEnum == StoreType.RESTAURANT
    val accent = if (isRestaurant) ChakaiOrange else KiranaGreen

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmartProductImage(
                        photoUri = store.photoUri,
                        contentDescription = store.name,
                        fallbackPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = store.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                        Text(
                            text = store.addressLandmark,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ApprovalBadge(status = store.statusEnum)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRestaurant) "Menu Dishes (${items.size})" else "Grocery Inventory (${items.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRestaurant) "Add Dish" else "Add Item")
                }
            }
        }

        if (items.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = if (isRestaurant) Icons.Default.RestaurantMenu else Icons.Default.Inventory2,
                    title = if (isRestaurant) "No dishes in your menu yet" else "No grocery items listed yet",
                    subtitle = "Tap + to add dishes or daily essentials for Chakai customers."
                )
            }
        } else {
            items(items, key = { it.id }) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SmartProductImage(
                            photoUri = item.photoUri,
                            contentDescription = item.name,
                            fallbackPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isRestaurant) {
                                    VegNonVegDot(isVeg = item.isVeg)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${item.category} • ${item.unitOrDescription}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₹${item.price}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = accent
                            )
                        }
                        IconButton(onClick = { onEditClick(item) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = accent)
                        }
                        IconButton(onClick = { onDeleteClick(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
private fun StoreIncomingOrdersTab(
    store: StoreEntity,
    orders: List<OrderEntity>,
    onAccept: (OrderEntity) -> Unit,
    onMarkReady: (OrderEntity) -> Unit
) {
    val isRestaurant = store.typeEnum == StoreType.RESTAURANT
    val accent = if (isRestaurant) ChakaiOrange else KiranaGreen

    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                icon = Icons.Default.ReceiptLong,
                title = "No Incoming Orders Yet",
                subtitle = "Customer orders for ${store.name} will appear here live."
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Incoming Orders (${orders.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        items(orders, key = { it.id }) { order ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
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
                            text = "${order.orderCode} • ${order.customerName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${order.itemSubtotal}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = accent
                        )
                    }
                    OrderStatusBadge(status = order.statusEnum)
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Drop: ${order.villageArea} (${order.distanceKm} km) • ${order.landmark} • +91 ${order.customerMobile}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    when (order.statusEnum) {
                        OrderStatus.PLACED -> {
                            Button(
                                onClick = { onAccept(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("accept_order_${order.id}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accept Order (ऑर्डर स्वीकार करें)")
                            }
                        }
                        OrderStatus.ACCEPTED_BY_STORE -> {
                            Button(
                                onClick = { onMarkReady(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusPurple),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ready_order_${order.id}")
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isRestaurant) "Mark Ready for Pickup (पिकअप के लिए तैयार)"
                                    else "Mark Packed & Ready for Pickup"
                                )
                            }
                        }
                        OrderStatus.READY_FOR_PICKUP -> {
                            Text(
                                text = "Waiting for Delivery Partner to accept pickup...",
                                style = MaterialTheme.typography.labelMedium,
                                color = StatusPurple
                            )
                        }
                        else -> {
                            Text(
                                text = "Assigned to: ${order.deliveryPartnerName ?: "Delivery Partner"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = KiranaGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemEditorDialog(
    storeType: StoreType,
    storeId: Long,
    existingItem: MenuItemEntity?,
    onDismiss: () -> Unit,
    onSave: (MenuItemEntity) -> Unit
) {
    val isRestaurant = storeType == StoreType.RESTAURANT
    val defaultPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA
    val defaultCategories = if (isRestaurant) {
        listOf("Bihari Special", "Main Course", "Thali", "Fast Food", "Snacks", "Biryani", "Breads")
    } else {
        listOf("Oil & Ghee", "Dairy", "Atta & Rice", "Dal & Pulses", "Daily Essentials", "Personal Care", "Household")
    }
    val kiranaUnits = listOf("1L", "500ml", "1kg", "500g", "250g", "5kg", "1 Pack")

    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var priceText by remember { mutableStateOf(existingItem?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(existingItem?.category ?: defaultCategories.first()) }
    var unitOrDesc by remember {
        mutableStateOf(
            existingItem?.unitOrDescription ?: if (isRestaurant) "" else "1kg"
        )
    }
    var isVeg by remember { mutableStateOf(existingItem?.isVeg ?: true) }
    var photoUri by remember { mutableStateOf(existingItem?.photoUri ?: defaultPreset) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingItem != null) "Edit Item" else if (isRestaurant) "Add New Dish" else "Add Grocery Essential",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text(if (isRestaurant) "Dish Name" else "Product Name") },
                    placeholder = { Text(if (isRestaurant) "e.g., Kadhai Paneer" else "e.g., Fortune Mustard Oil") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() }; errorMsg = null },
                    label = { Text("Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    defaultCategories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                if (isRestaurant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VegNonVegDot(isVeg = isVeg)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isVeg) "Pure Veg (शाकाहारी)" else "Non-Veg (मांसाहारी)", fontWeight = FontWeight.SemiBold)
                        }
                        Switch(checked = isVeg, onCheckedChange = { isVeg = it })
                    }
                    OutlinedTextField(
                        value = unitOrDesc,
                        onValueChange = { unitOrDesc = it },
                        label = { Text("Description / Portion") },
                        placeholder = { Text("e.g., Full plate, serves 2") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Unit / Pack Size", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        kiranaUnits.forEach { u ->
                            FilterChip(
                                selected = unitOrDesc == u,
                                onClick = { unitOrDesc = u },
                                label = { Text(u, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = unitOrDesc,
                        onValueChange = { unitOrDesc = it },
                        label = { Text("Custom Unit") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PhotoPickerField(
                    label = if (isRestaurant) "Food Photo" else "Product Photo",
                    photoUri = photoUri,
                    onPhotoChanged = { photoUri = it },
                    defaultPreset = defaultPreset
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, color = StatusRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toIntOrNull()
                    when {
                        name.isBlank() -> errorMsg = "Enter item name"
                        price == null || price <= 0 -> errorMsg = "Enter valid price"
                        else -> onSave(
                            MenuItemEntity(
                                id = existingItem?.id ?: 0L,
                                storeId = storeId,
                                name = name.trim(),
                                category = category,
                                price = price,
                                unitOrDescription = unitOrDesc.ifBlank { if (isRestaurant) "Regular" else "1 Pack" },
                                isVeg = if (isRestaurant) isVeg else true,
                                photoUri = photoUri.ifBlank { defaultPreset },
                                isAvailable = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isRestaurant) ChakaiOrange else KiranaGreen)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
