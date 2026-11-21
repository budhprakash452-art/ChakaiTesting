package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApprovalStatus
import com.example.data.CartItemEntity
import com.example.data.ChakaiDatabase
import com.example.data.ChakaiRepository
import com.example.data.CustomerRecordEntity
import com.example.data.DeliveryPartnerEntity
import com.example.data.MenuItemEntity
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.PaymentMode
import com.example.data.SessionStore
import com.example.data.StoreEntity
import com.example.data.StoreType
import com.example.data.UserRole
import com.example.data.UserSession
import com.example.data.VehicleType
import com.example.data.VillageArea
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChakaiUiState(
    val isReady: Boolean = false,
    val session: UserSession = UserSession(),
    val stores: List<StoreEntity> = emptyList(),
    val menuItems: List<MenuItemEntity> = emptyList(),
    val deliveryPartners: List<DeliveryPartnerEntity> = emptyList(),
    val cartItems: List<CartItemEntity> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val customers: List<CustomerRecordEntity> = emptyList()
) {
    val approvedRestaurants: List<StoreEntity>
        get() = stores.filter { it.typeEnum == StoreType.RESTAURANT && it.statusEnum == ApprovalStatus.APPROVED }

    val approvedGeneralStores: List<StoreEntity>
        get() = stores.filter { it.typeEnum == StoreType.GENERAL_STORE && it.statusEnum == ApprovalStatus.APPROVED }

    val pendingStoresCount: Int
        get() = stores.count { it.statusEnum == ApprovalStatus.PENDING }

    val pendingDeliveryCount: Int
        get() = deliveryPartners.count { it.statusEnum == ApprovalStatus.PENDING }

    val totalPendingApprovals: Int
        get() = pendingStoresCount + pendingDeliveryCount

    val cartTotalCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotal: Int
        get() = cartItems.sumOf { it.price * it.quantity }
}

private data class CoreCatalogState(
    val session: UserSession,
    val stores: List<StoreEntity>,
    val menuItems: List<MenuItemEntity>,
    val deliveryPartners: List<DeliveryPartnerEntity>
)

class ChakaiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ChakaiDatabase.getInstance(application)
    private val sessionStore = SessionStore(application)
    private val repository = ChakaiRepository(database.chakaiDao(), sessionStore)

    private val _isReady = MutableStateFlow(false)
    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private val coreCatalogFlow = combine(
        repository.sessionFlow,
        repository.allStoresFlow,
        repository.allMenuItemsFlow,
        repository.allDeliveryPartnersFlow
    ) { session, stores, menuItems, partners ->
        CoreCatalogState(session, stores, menuItems, partners)
    }

    val uiState: StateFlow<ChakaiUiState> = combine(
        _isReady,
        coreCatalogFlow,
        repository.cartItemsFlow,
        repository.allOrdersFlow,
        repository.allCustomersFlow
    ) { ready, core, cart, orders, customers ->
        ChakaiUiState(
            isReady = ready,
            session = core.session,
            stores = core.stores,
            menuItems = core.menuItems,
            deliveryPartners = core.deliveryPartners,
            cartItems = cart,
            orders = orders,
            customers = customers
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChakaiUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureDemoDataSeeded()
            _isReady.value = true
        }
    }

    private fun emitMessage(msg: String) {
        _toastEvents.tryEmit(msg)
    }

    // Auth & Role Navigation
    fun completeOtpLogin(mobile: String, role: UserRole? = null) {
        viewModelScope.launch {
            repository.completeLogin(mobile, role)
            if (role == UserRole.ADMIN) {
                emitMessage("Admin Panel unlocked • एडमिन लॉगिन सफल")
            } else {
                emitMessage("OTP Verified • +91 $mobile")
            }
        }
    }

    fun selectRole(role: UserRole?) {
        viewModelScope.launch {
            repository.switchRole(role)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            emitMessage("Logged out successfully")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            emitMessage("Chakai demo data restored • डेमो डेटा रीसेट हुआ")
        }
    }

    // Customer Cart & Checkout
    fun addToCart(item: MenuItemEntity, store: StoreEntity) {
        viewModelScope.launch {
            repository.addToCart(item, store)
        }
    }

    fun decrementCartItem(menuItemId: Long) {
        viewModelScope.launch {
            repository.decrementCartItem(menuItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun placeOrder(
        customerName: String,
        customerMobile: String,
        village: VillageArea,
        landmark: String,
        paymentMode: PaymentMode,
        onPlaced: () -> Unit
    ) {
        val current = uiState.value
        val storesById = current.stores.associateBy { it.id }
        viewModelScope.launch {
            val codes = repository.placeOrdersFromCart(
                cartItems = current.cartItems,
                storesById = storesById,
                customerName = customerName.trim(),
                customerMobile = customerMobile.trim(),
                village = village,
                landmark = landmark.trim(),
                paymentMode = paymentMode
            )
            if (codes.isNotEmpty()) {
                emitMessage("Order Placed (${codes.joinToString()})! ऑर्डर कन्फर्म हो गया")
                onPlaced()
            }
        }
    }

    // Restaurant & Kirana Partner Actions
    fun registerStore(
        storeType: StoreType,
        name: String,
        ownerName: String,
        ownerMobile: String,
        addressLandmark: String,
        photoUri: String,
        onRegistered: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.registerStore(
                storeType = storeType,
                name = name,
                ownerName = ownerName,
                ownerMobile = ownerMobile,
                addressLandmark = addressLandmark,
                photoUri = photoUri
            )
            emitMessage("Registration submitted! एडमिन अप्रूवल के लिए भेजा गया")
            onRegistered(id)
        }
    }

    fun saveMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            val isEdit = item.id != 0L
            repository.saveMenuItem(item)
            emitMessage(if (isEdit) "Item updated • आइटम अपडेट हुआ" else "Item added • नया आइटम जोड़ा गया")
        }
    }

    fun deleteMenuItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
            emitMessage("Item deleted • आइटम हटाया गया")
        }
    }

    fun acceptStoreOrder(order: OrderEntity) {
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, OrderStatus.ACCEPTED_BY_STORE)
            emitMessage("Order ${order.orderCode} accepted • ऑर्डर स्वीकार किया गया")
        }
    }

    fun markOrderReadyForPickup(order: OrderEntity) {
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, OrderStatus.READY_FOR_PICKUP)
            emitMessage("Order ${order.orderCode} ready for delivery pickup!")
        }
    }

    // Delivery Partner Actions
    fun registerDeliveryPartner(
        fullName: String,
        mobile: String,
        vehicleType: VehicleType,
        villages: List<String>,
        onRegistered: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.registerDeliveryPartner(fullName, mobile, vehicleType, villages)
            emitMessage("Delivery Partner registration sent for Admin approval!")
            onRegistered(id)
        }
    }

    fun toggleDeliveryOnline(partner: DeliveryPartnerEntity, online: Boolean) {
        viewModelScope.launch {
            repository.setDeliveryPartnerOnline(partner.id, online)
            emitMessage(if (online) "You are now ONLINE • डिलीवरी के लिए तैयार" else "You are now OFFLINE")
        }
    }

    fun acceptDelivery(order: OrderEntity, partner: DeliveryPartnerEntity) {
        viewModelScope.launch {
            repository.acceptDelivery(order, partner)
            emitMessage("Accepted ${order.orderCode} • अब स्टोर से पिकअप करें")
        }
    }

    fun markOrderPickedUp(order: OrderEntity) {
        viewModelScope.launch {
            repository.markOrderPickedUp(order)
            emitMessage("Picked up ${order.orderCode} • डिलीवरी के लिए निकले")
        }
    }

    fun markOrderDelivered(order: OrderEntity, partner: DeliveryPartnerEntity) {
        viewModelScope.launch {
            repository.markOrderDelivered(order, partner)
            emitMessage("Delivered ${order.orderCode}! +₹${order.deliveryEarning} earned")
        }
    }

    // Admin Control Actions
    fun approveStore(store: StoreEntity) {
        viewModelScope.launch {
            repository.setStoreApproval(store.id, ApprovalStatus.APPROVED)
            emitMessage("Approved: ${store.name} • अब कस्टमर को दिखेगा")
        }
    }

    fun rejectStore(store: StoreEntity) {
        viewModelScope.launch {
            repository.setStoreApproval(store.id, ApprovalStatus.REJECTED)
            emitMessage("Rejected: ${store.name}")
        }
    }

    fun deleteStore(store: StoreEntity) {
        viewModelScope.launch {
            repository.deleteStoreAndItems(store.id)
            emitMessage("Removed store: ${store.name}")
        }
    }

    fun approveDeliveryPartner(partner: DeliveryPartnerEntity) {
        viewModelScope.launch {
            repository.setDeliveryPartnerApproval(partner.id, ApprovalStatus.APPROVED)
            emitMessage("Approved Delivery Partner: ${partner.fullName}")
        }
    }

    fun rejectDeliveryPartner(partner: DeliveryPartnerEntity) {
        viewModelScope.launch {
            repository.setDeliveryPartnerApproval(partner.id, ApprovalStatus.REJECTED)
            emitMessage("Rejected Delivery Partner: ${partner.fullName}")
        }
    }
}
