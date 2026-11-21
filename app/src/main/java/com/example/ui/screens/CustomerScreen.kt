package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CartItemEntity
import com.example.data.ChakaiLocations
import com.example.data.MenuItemEntity
import com.example.data.OrderEntity
import com.example.data.PaymentMode
import com.example.data.PresetImageKeys
import com.example.data.StoreEntity
import com.example.data.StoreType
import com.example.data.UserRole
import com.example.data.VillageArea
import com.example.ui.components.ChakaiTopBar
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderProgressStepper
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SmartProductImage
import com.example.ui.components.VegNonVegDot
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.ChakaiOrangeContainer
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.KiranaGreenContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusRed
import com.example.ui.theme.WarmGold
import com.example.viewmodel.ChakaiUiState
import com.example.viewmodel.ChakaiViewModel

@Composable
fun CustomerScreen(
    uiState: ChakaiUiState,
    viewModel: ChakaiViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Explore, 1=Cart, 2=My Orders
    var activeStoreType by remember { mutableStateOf(StoreType.RESTAURANT) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var openedStoreId by remember { mutableStateOf<Long?>(null) }

    val openedStore = remember(openedStoreId, uiState.stores) {
        uiState.stores.firstOrNull { it.id == openedStoreId }
    }

    if (openedStore != null) {
        BackHandler { openedStoreId = null }
    } else if (selectedTab != 0) {
        BackHandler { selectedTab = 0 }
    }

    val cartQtyByItemId = remember(uiState.cartItems) {
        uiState.cartItems.associate { it.menuItemId to it.quantity }
    }

    Scaffold(
        topBar = {
            ChakaiTopBar(
                activeRole = UserRole.CUSTOMER,
                mobileNumber = uiState.session.mobileNumber,
                pendingApprovalsCount = uiState.totalPendingApprovals,
                onSwitchRole = { viewModel.selectRole(it) },
                onGoToRolePicker = { viewModel.selectRole(null) },
                onResetDemoData = { viewModel.resetDemoData() },
                onLogout = { viewModel.logout() },
                subtitle = "Chakai Chowk & 20 km Villages"
            )
        },
        bottomBar = {
            Column {
                // Floating Cart Bar when browsing Explore or Store Detail
                if (selectedTab == 0 && uiState.cartTotalCount > 0) {
                    val accent = if (activeStoreType == StoreType.RESTAURANT) ChakaiOrange else KiranaGreen
                    Surface(
                        onClick = {
                            openedStoreId = null
                            selectedTab = 1
                        },
                        color = accent,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("floating_view_cart_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${uiState.cartTotalCount} item(s) in Cart",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Subtotal: ₹${uiState.cartSubtotal} + village delivery",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PureWhite.copy(alpha = 0.9f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "View Cart",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PureWhite,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            openedStoreId = null
                            selectedTab = 0
                        },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Explore") },
                        label = { Text("Explore (खोजें)") },
                        modifier = Modifier.testTag("customer_tab_explore")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            openedStoreId = null
                            selectedTab = 1
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (uiState.cartTotalCount > 0) {
                                        Badge(containerColor = ChakaiOrange, contentColor = PureWhite) {
                                            Text("${uiState.cartTotalCount}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart (कार्ट)") },
                        modifier = Modifier.testTag("customer_tab_cart")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            openedStoreId = null
                            selectedTab = 2
                        },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "My Orders") },
                        label = { Text("My Orders") },
                        modifier = Modifier.testTag("customer_tab_orders")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                openedStore != null -> {
                    StoreMenuDetailView(
                        store = openedStore,
                        items = uiState.menuItems.filter { it.storeId == openedStore.id && it.isAvailable },
                        cartQtyByItemId = cartQtyByItemId,
                        onBack = { openedStoreId = null },
                        onAdd = { item -> viewModel.addToCart(item, openedStore) },
                        onDecrement = { itemId -> viewModel.decrementCartItem(itemId) }
                    )
                }
                selectedTab == 0 -> {
                    CustomerExploreTab(
                        activeStoreType = activeStoreType,
                        onStoreTypeChange = {
                            activeStoreType = it
                            selectedCategory = "All"
                        },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { selectedCategory = it },
                        stores = if (activeStoreType == StoreType.RESTAURANT) {
                            uiState.approvedRestaurants
                        } else {
                            uiState.approvedGeneralStores
                        },
                        allMenuItems = uiState.menuItems,
                        cartQtyByItemId = cartQtyByItemId,
                        onOpenStore = { openedStoreId = it.id },
                        onAddItem = { item, store -> viewModel.addToCart(item, store) },
                        onDecrementItem = { viewModel.decrementCartItem(it) }
                    )
                }
                selectedTab == 1 -> {
                    CustomerCartCheckoutTab(
                        cartItems = uiState.cartItems,
                        defaultCustomerName = uiState.session.customerName.ifBlank {
                            uiState.customers.firstOrNull { it.mobile == uiState.session.mobileNumber }?.name ?: ""
                        },
                        mobileNumber = uiState.session.mobileNumber,
                        onIncrement = { cartItem ->
                            val item = uiState.menuItems.firstOrNull { it.id == cartItem.menuItemId }
                            val store = uiState.stores.firstOrNull { it.id == cartItem.storeId }
                            if (item != null && store != null) {
                                viewModel.addToCart(item, store)
                            }
                        },
                        onDecrement = { viewModel.decrementCartItem(it) },
                        onClearCart = { viewModel.clearCart() },
                        onBrowseStores = { selectedTab = 0 },
                        onPlaceOrder = { name, mobile, village, landmark, paymentMode ->
                            viewModel.placeOrder(
                                customerName = name,
                                customerMobile = mobile,
                                village = village,
                                landmark = landmark,
                                paymentMode = paymentMode,
                                onPlaced = { selectedTab = 2 }
                            )
                        }
                    )
                }
                else -> {
                    CustomerOrdersTab(
                        orders = uiState.orders.filter {
                            it.customerMobile == uiState.session.mobileNumber || uiState.session.mobileNumber.isBlank()
                        }.ifEmpty { uiState.orders }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerExploreTab(
    activeStoreType: StoreType,
    onStoreTypeChange: (StoreType) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    stores: List<StoreEntity>,
    allMenuItems: List<MenuItemEntity>,
    cartQtyByItemId: Map<Long, Int>,
    onOpenStore: (StoreEntity) -> Unit,
    onAddItem: (MenuItemEntity, StoreEntity) -> Unit,
    onDecrementItem: (Long) -> Unit
) {
    val storesById = remember(stores) { stores.associateBy { it.id } }
    val approvedItemsForType = remember(stores, allMenuItems) {
        allMenuItems.filter { storesById.containsKey(it.storeId) && it.isAvailable }
    }
    val categories = remember(approvedItemsForType) {
        listOf("All") + approvedItemsForType.map { it.category }.distinct()
    }

    val query = searchQuery.trim()
    val filteredStores = remember(stores, approvedItemsForType, query, selectedCategory) {
        stores.filter { store ->
            val storeItems = approvedItemsForType.filter { it.storeId == store.id }
            val matchesCategory = selectedCategory == "All" || storeItems.any { it.category == selectedCategory }
            val matchesSearch = query.isBlank() ||
                store.name.contains(query, ignoreCase = true) ||
                store.addressLandmark.contains(query, ignoreCase = true) ||
                storeItems.any {
                    it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
                }
            matchesCategory && matchesSearch
        }
    }

    val featuredItems = remember(approvedItemsForType, query, selectedCategory) {
        approvedItemsForType.filter { item ->
            val matchesCat = selectedCategory == "All" || item.category == selectedCategory
            val matchesQ = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true) ||
                item.unitOrDescription.contains(query, ignoreCase = true)
            matchesCat && matchesQ
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Hero Promo Strip
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(
                            id = if (activeStoreType == StoreType.RESTAURANT) {
                                R.drawable.img_hero_chakai
                            } else {
                                R.drawable.img_kirana_store
                            }
                        ),
                        contentDescription = "ChakaiGo Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.78f),
                                        Color.Black.copy(alpha = 0.25f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = if (activeStoreType == StoreType.RESTAURANT) ChakaiOrange else KiranaGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CHAKAI • 20 KM FAST DELIVERY",
                                style = MaterialTheme.typography.labelSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (activeStoreType == StoreType.RESTAURANT) {
                                "गरमा-गरम खाना चकाई से सीधे घर तक"
                            } else {
                                "ताज़ा किराना सामान 20 मिनट में डिलीवरी"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chakai Chowk, Madhopur, Bamdah, Batpar, Saroun & more",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        }

        // Top Toggle Tabs: Restaurants (खाना) vs General Stores / Kirana (किराना सामान)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryModePill(
                    title = "Restaurants",
                    hindi = "खाना",
                    icon = Icons.Default.Restaurant,
                    selected = activeStoreType == StoreType.RESTAURANT,
                    activeColor = ChakaiOrange,
                    testTag = "toggle_restaurants",
                    onClick = { onStoreTypeChange(StoreType.RESTAURANT) },
                    modifier = Modifier.weight(1f)
                )
                CategoryModePill(
                    title = "Kirana / Store",
                    hindi = "किराना सामान",
                    icon = Icons.Default.Storefront,
                    selected = activeStoreType == StoreType.GENERAL_STORE,
                    activeColor = KiranaGreen,
                    testTag = "toggle_kirana",
                    onClick = { onStoreTypeChange(StoreType.GENERAL_STORE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        if (activeStoreType == StoreType.RESTAURANT) {
                            "Search Litti, Paneer, Biryani, Restaurant..."
                        } else {
                            "Search Mustard Oil, Milk, Atta, Dal, Store..."
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input")
            )
        }

        // Category Filter Chips
        if (categories.size > 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val accent = if (activeStoreType == StoreType.RESTAURANT) ChakaiOrange else KiranaGreen
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accent.copy(alpha = 0.16f),
                                selectedLabelColor = accent
                            )
                        )
                    }
                }
            }
        }

        // Approved Stores Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeStoreType == StoreType.RESTAURANT) {
                        "Approved Restaurants (${filteredStores.size})"
                    } else {
                        "Approved Kirana Stores (${filteredStores.size})"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Admin Verified ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = KiranaGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (filteredStores.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = if (activeStoreType == StoreType.RESTAURANT) Icons.Default.Restaurant else Icons.Default.Storefront,
                    title = "No matching approved stores found",
                    subtitle = "Try clearing your search or ask Admin to approve new partner stores."
                )
            }
        } else {
            items(filteredStores, key = { it.id }) { store ->
                val itemCount = approvedItemsForType.count { it.storeId == store.id }
                StoreCard(
                    store = store,
                    itemCount = itemCount,
                    onClick = { onOpenStore(store) }
                )
            }
        }

        // Popular / Matching Items Quick Add Section
        if (featuredItems.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (activeStoreType == StoreType.RESTAURANT) {
                        "Popular Dishes (जल्दी ऑर्डर करें)"
                    } else {
                        "Daily Grocery Essentials (किराना सामान)"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(featuredItems.take(12), key = { "item_${it.id}" }) { item ->
                val store = storesById[item.storeId]
                if (store != null) {
                    CustomerMenuItemCard(
                        item = item,
                        storeName = store.name,
                        isRestaurant = activeStoreType == StoreType.RESTAURANT,
                        quantityInCart = cartQtyByItemId[item.id] ?: 0,
                        onAdd = { onAddItem(item, store) },
                        onDecrement = { onDecrementItem(item.id) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun CategoryModePill(
    title: String,
    hindi: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) activeColor else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, if (selected) activeColor else MaterialTheme.colorScheme.outline),
        shadowElevation = if (selected) 3.dp else 0.dp,
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) PureWhite else activeColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) PureWhite else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = hindi,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) PureWhite.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun StoreCard(
    store: StoreEntity,
    itemCount: Int,
    onClick: () -> Unit
) {
    val isRestaurant = store.typeEnum == StoreType.RESTAURANT
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("store_card_${store.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                SmartProductImage(
                    photoUri = store.photoUri,
                    contentDescription = store.name,
                    fallbackPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = KiranaGreen,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%.1f", store.rating),
                            style = MaterialTheme.typography.labelMedium,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.Star, contentDescription = null, tint = WarmGold, modifier = Modifier.size(14.dp))
                    }
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${store.deliveryTimeMins} • $itemCount items",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isRestaurant) ChakaiOrange else KiranaGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = store.addressLandmark,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Menu →",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRestaurant) ChakaiOrange else KiranaGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreMenuDetailView(
    store: StoreEntity,
    items: List<MenuItemEntity>,
    cartQtyByItemId: Map<Long, Int>,
    onBack: () -> Unit,
    onAdd: (MenuItemEntity) -> Unit,
    onDecrement: (Long) -> Unit
) {
    val isRestaurant = store.typeEnum == StoreType.RESTAURANT
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(165.dp)
                    ) {
                        SmartProductImage(
                            photoUri = store.photoUri,
                            contentDescription = store.name,
                            fallbackPreset = if (isRestaurant) PresetImageKeys.THALI else PresetImageKeys.KIRANA,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            onClick = onBack,
                            color = PureWhite.copy(alpha = 0.92f),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = store.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${store.addressLandmark} • ★ ${store.rating} • ${store.deliveryTimeMins}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (items.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Restaurant,
                    title = "No items listed yet",
                    subtitle = "This store hasn't added menu/grocery items yet."
                )
            }
        } else {
            items(items, key = { it.id }) { item ->
                CustomerMenuItemCard(
                    item = item,
                    storeName = null,
                    isRestaurant = isRestaurant,
                    quantityInCart = cartQtyByItemId[item.id] ?: 0,
                    onAdd = { onAdd(item) },
                    onDecrement = { onDecrement(item.id) }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun CustomerMenuItemCard(
    item: MenuItemEntity,
    storeName: String?,
    isRestaurant: Boolean,
    quantityInCart: Int,
    onAdd: () -> Unit,
    onDecrement: () -> Unit
) {
    val accent = if (isRestaurant) ChakaiOrange else KiranaGreen
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
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
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isRestaurant) {
                        VegNonVegDot(isVeg = item.isVeg)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Surface(
                        color = accent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (storeName != null) {
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.bodySmall,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = item.unitOrDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${item.price}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (quantityInCart == 0) {
                        Button(
                            onClick = onAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = accent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_item_${item.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Surface(
                            color = accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, accent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onDecrement,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = accent, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$quantityInCart",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = accent,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = onAdd,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = accent, modifier = Modifier.size(16.dp))
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
private fun CustomerCartCheckoutTab(
    cartItems: List<CartItemEntity>,
    defaultCustomerName: String,
    mobileNumber: String,
    onIncrement: (CartItemEntity) -> Unit,
    onDecrement: (Long) -> Unit,
    onClearCart: () -> Unit,
    onBrowseStores: () -> Unit,
    onPlaceOrder: (
        name: String,
        mobile: String,
        village: VillageArea,
        landmark: String,
        paymentMode: PaymentMode
    ) -> Unit
) {
    if (cartItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyStateCard(
                icon = Icons.Default.ShoppingCart,
                title = "Your Cart is Empty (कार्ट खाली है)",
                subtitle = "Add tasty dishes from Chakai restaurants or daily essentials from kirana stores!"
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onBrowseStores,
                colors = ButtonDefaults.buttonColors(containerColor = ChakaiOrange)
            ) {
                Text("Browse Chakai Stores")
            }
        }
        return
    }

    var customerName by remember(defaultCustomerName) { mutableStateOf(defaultCustomerName.ifBlank { "Rahul Kumar" }) }
    var selectedVillage by remember { mutableStateOf(ChakaiLocations.villages.first()) }
    var villageDropdownOpen by remember { mutableStateOf(false) }
    var landmark by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.COD) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val storeCount = cartItems.map { it.storeId }.distinct().size
    val totalDeliveryFee = selectedVillage.deliveryFee * storeCount
    val grandTotal = subtotal + totalDeliveryFee

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Cart (${cartItems.sumOf { it.quantity }} items)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onClearCart) {
                    Text("Clear", color = StatusRed)
                }
            }
        }

        items(cartItems, key = { it.menuItemId }) { cartItem ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmartProductImage(
                        photoUri = cartItem.photoUri,
                        contentDescription = cartItem.itemName,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cartItem.itemName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${cartItem.storeName} • ₹${cartItem.price} each",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onDecrement(cartItem.menuItemId) }, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ChakaiOrange)
                        }
                        Text(
                            text = "${cartItem.quantity}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(onClick = { onIncrement(cartItem) }, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = ChakaiOrange)
                        }
                    }
                }
            }
        }

        // Delivery Details Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Delivery Details (डिलीवरी पता - 20 किमी क्षेत्र)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; errorMsg = null },
                        label = { Text("Customer Name (ग्राहक का नाम)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_name_input")
                    )
                    OutlinedTextField(
                        value = "+91 $mobileNumber",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mobile Number (ऑटो-फ़िल्ड)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Village / Area Dropdown (Within 20 km of Chakai)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedVillage.displayLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Village / Area within 20 km of Chakai") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ChakaiOrange) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Select village") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { villageDropdownOpen = true }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { villageDropdownOpen = true }
                                .testTag("checkout_village_dropdown")
                        )
                        DropdownMenu(
                            expanded = villageDropdownOpen,
                            onDismissRequest = { villageDropdownOpen = false },
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            ChakaiLocations.villages.forEach { village ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("${village.nameEn} (${village.nameHi}) • ${village.distanceKm} km")
                                            Text(
                                                "₹${village.deliveryFee}",
                                                fontWeight = FontWeight.Bold,
                                                color = KiranaGreen
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedVillage = village
                                        villageDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark / House Details (नजदीकी पहचान)") },
                        placeholder = { Text("e.g., Near Mandir, Middle School Gate, Tola...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_landmark_input")
                    )

                    // Payment Mode
                    Text(
                        text = "Payment Mode (भुगतान का तरीका)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PaymentMode.entries.forEach { mode ->
                            val selected = paymentMode == mode
                            Surface(
                                onClick = { paymentMode = mode },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) KiranaGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (selected) KiranaGreen else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("payment_mode_${mode.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = if (selected) KiranaGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = mode.labelEn,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) KiranaGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = mode.labelHi,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Itemized Bill Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ChakaiOrange.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bill Summary (बिल विवरण)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    BillRow("Item Subtotal", "₹$subtotal")
                    BillRow(
                        "Delivery Fee (${selectedVillage.nameEn}, ${selectedVillage.distanceKm} km${if (storeCount > 1) " x $storeCount stores" else ""})",
                        "₹$totalDeliveryFee"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "₹$grandTotal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = KiranaGreen
                        )
                    }
                    if (errorMsg != null) {
                        Text(errorMsg!!, color = StatusRed, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (customerName.isBlank()) {
                                errorMsg = "Please enter your name"
                            } else {
                                onPlaceOrder(customerName, mobileNumber, selectedVillage, landmark, paymentMode)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("place_order_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Place Order • ₹$grandTotal (ऑर्डर कन्फर्म करें)", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun BillRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CustomerOrdersTab(orders: List<OrderEntity>) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                icon = Icons.Default.ReceiptLong,
                title = "No Orders Yet (कोई ऑर्डर नहीं)",
                subtitle = "Place your first food or kirana order to track live Chakai delivery step-by-step!"
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "My Live Orders (${orders.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        items(orders, key = { it.id }) { order ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${order.orderCode} • ${order.storeName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${order.villageArea} (${order.distanceKm} km) • ${order.landmark}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "₹${order.grandTotal}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ChakaiOrange
                        )
                    }

                    OrderStatusBadge(status = order.statusEnum)

                    OrderProgressStepper(currentStatus = order.statusEnum)

                    HorizontalDivider()
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Payment: ${order.paymentEnum.labelEn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (order.deliveryPartnerName != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = KiranaGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Partner: ${order.deliveryPartnerName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KiranaGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
