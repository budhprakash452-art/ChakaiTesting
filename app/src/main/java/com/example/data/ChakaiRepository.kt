package com.example.data

import kotlinx.coroutines.flow.Flow

class ChakaiRepository(
    private val dao: ChakaiDao,
    private val sessionStore: SessionStore
) {
    val sessionFlow: Flow<UserSession> = sessionStore.sessionFlow
    val allStoresFlow: Flow<List<StoreEntity>> = dao.observeAllStores()
    val allMenuItemsFlow: Flow<List<MenuItemEntity>> = dao.observeAllMenuItems()
    val allDeliveryPartnersFlow: Flow<List<DeliveryPartnerEntity>> = dao.observeAllDeliveryPartners()
    val cartItemsFlow: Flow<List<CartItemEntity>> = dao.observeCartItems()
    val allOrdersFlow: Flow<List<OrderEntity>> = dao.observeAllOrders()
    val allCustomersFlow: Flow<List<CustomerRecordEntity>> = dao.observeAllCustomers()

    suspend fun ensureDemoDataSeeded() {
        if (dao.getStoreCount() == 0) {
            ChakaiDemoSeeder.seedDemoData(dao)
            sessionStore.markDemoSeeded()
        }
    }

    suspend fun resetDemoData() {
        dao.clearCart()
        dao.clearOrders()
        dao.clearMenuItems()
        dao.clearStores()
        dao.clearDeliveryPartners()
        dao.clearCustomers()
        ChakaiDemoSeeder.seedDemoData(dao)
    }

    suspend fun completeLogin(mobile: String, role: UserRole? = null) {
        val defaultName = if (mobile == DemoConstants.DEMO_MOBILE) "Rahul Kumar" else ""
        dao.upsertCustomer(CustomerRecordEntity(mobile = mobile, name = defaultName.ifBlank { "Chakai Customer" }))
        sessionStore.saveLogin(mobile = mobile, role = role, customerName = defaultName)
    }

    suspend fun switchRole(role: UserRole?) {
        sessionStore.setActiveRole(role)
    }

    suspend fun logout() {
        sessionStore.logout()
    }

    // Cart operations
    suspend fun addToCart(item: MenuItemEntity, store: StoreEntity): Boolean {
        val existing = dao.getCartItem(item.id)
        if (existing != null) {
            dao.upsertCartItem(existing.copy(quantity = existing.quantity + 1))
            return true
        }
        dao.upsertCartItem(
            CartItemEntity(
                menuItemId = item.id,
                storeId = store.id,
                storeName = store.name,
                storeType = store.storeType,
                itemName = item.name,
                unitOrDescription = item.unitOrDescription,
                price = item.price,
                photoUri = item.photoUri,
                quantity = 1
            )
        )
        return true
    }

    suspend fun decrementCartItem(menuItemId: Long) {
        val existing = dao.getCartItem(menuItemId) ?: return
        if (existing.quantity <= 1) {
            dao.deleteCartItem(menuItemId)
        } else {
            dao.upsertCartItem(existing.copy(quantity = existing.quantity - 1))
        }
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // Order placement
    suspend fun placeOrdersFromCart(
        cartItems: List<CartItemEntity>,
        storesById: Map<Long, StoreEntity>,
        customerName: String,
        customerMobile: String,
        village: VillageArea,
        landmark: String,
        paymentMode: PaymentMode
    ): List<String> {
        if (cartItems.isEmpty()) return emptyList()
        sessionStore.updateCustomerName(customerName)
        dao.upsertCustomer(
            CustomerRecordEntity(
                mobile = customerMobile,
                name = customerName,
                defaultVillage = village.nameEn
            )
        )
        val groupedByStore = cartItems.groupBy { it.storeId }
        val createdCodes = mutableListOf<String>()
        var offset = 0
        for ((storeId, items) in groupedByStore) {
            val store = storesById[storeId]
            val subtotal = items.sumOf { it.price * it.quantity }
            val summary = items.joinToString(", ") { "${it.quantity}x ${it.itemName} (₹${it.price * it.quantity})" }
            val code = "CG-${(1050..9999).random() + offset}"
            offset++
            dao.insertOrder(
                OrderEntity(
                    orderCode = code,
                    customerName = customerName,
                    customerMobile = customerMobile,
                    villageArea = village.nameEn,
                    landmark = landmark.ifBlank { village.nameEn },
                    distanceKm = village.distanceKm,
                    storeId = storeId,
                    storeName = store?.name ?: items.first().storeName,
                    storeAddress = store?.addressLandmark ?: "Chakai Chowk",
                    storeType = store?.storeType ?: items.first().storeType,
                    itemsSummary = summary,
                    itemSubtotal = subtotal,
                    deliveryFee = village.deliveryFee,
                    grandTotal = subtotal + village.deliveryFee,
                    paymentMode = paymentMode.name,
                    status = OrderStatus.PLACED.name
                )
            )
            createdCodes.add(code)
        }
        dao.clearCart()
        return createdCodes
    }

    // Store Partner Onboarding & Menu CRUD
    suspend fun registerStore(
        storeType: StoreType,
        name: String,
        ownerName: String,
        ownerMobile: String,
        addressLandmark: String,
        photoUri: String
    ): Long {
        return dao.insertStore(
            StoreEntity(
                storeType = storeType.name,
                name = name.trim(),
                ownerName = ownerName.trim(),
                ownerMobile = ownerMobile.trim(),
                addressLandmark = addressLandmark.trim(),
                photoUri = photoUri.ifBlank {
                    if (storeType == StoreType.RESTAURANT) PresetImageKeys.THALI else PresetImageKeys.KIRANA
                },
                status = ApprovalStatus.PENDING.name
            )
        )
    }

    suspend fun saveMenuItem(item: MenuItemEntity) {
        if (item.id == 0L) {
            dao.insertMenuItem(item)
        } else {
            dao.updateMenuItem(item)
        }
    }

    suspend fun deleteMenuItem(itemId: Long) {
        dao.deleteMenuItemById(itemId)
    }

    // Store Order Actions
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) {
        dao.updateOrderStatus(orderId, status.name)
    }

    // Delivery Partner Onboarding & Actions
    suspend fun registerDeliveryPartner(
        fullName: String,
        mobile: String,
        vehicleType: VehicleType,
        villages: List<String>
    ): Long {
        return dao.insertDeliveryPartner(
            DeliveryPartnerEntity(
                fullName = fullName.trim(),
                mobile = mobile.trim(),
                vehicleType = vehicleType.name,
                preferredVillagesCsv = villages.joinToString(", "),
                status = ApprovalStatus.PENDING.name,
                isOnline = false
            )
        )
    }

    suspend fun setDeliveryPartnerOnline(partnerId: Long, isOnline: Boolean) {
        dao.updateDeliveryPartnerOnline(partnerId, isOnline)
    }

    suspend fun acceptDelivery(order: OrderEntity, partner: DeliveryPartnerEntity) {
        dao.assignDeliveryPartnerAndUpdateStatus(
            orderId = order.id,
            status = OrderStatus.DELIVERY_ACCEPTED.name,
            partnerId = partner.id,
            partnerName = partner.fullName,
            partnerMobile = partner.mobile
        )
    }

    suspend fun markOrderPickedUp(order: OrderEntity) {
        dao.updateOrderStatus(order.id, OrderStatus.PICKED_UP.name)
    }

    suspend fun markOrderDelivered(order: OrderEntity, partner: DeliveryPartnerEntity) {
        dao.updateOrderStatus(order.id, OrderStatus.DELIVERED.name)
        dao.creditDeliveryPartnerEarning(partner.id, order.deliveryEarning)
    }

    // Admin Control Actions
    suspend fun setStoreApproval(storeId: Long, status: ApprovalStatus) {
        dao.updateStoreStatus(storeId, status.name)
    }

    suspend fun deleteStoreAndItems(storeId: Long) {
        dao.deleteMenuItemsByStore(storeId)
        dao.deleteStoreById(storeId)
    }

    suspend fun setDeliveryPartnerApproval(partnerId: Long, status: ApprovalStatus) {
        dao.updateDeliveryPartnerStatus(partnerId, status.name)
        if (status == ApprovalStatus.APPROVED) {
            dao.updateDeliveryPartnerOnline(partnerId, true)
        }
    }
}
