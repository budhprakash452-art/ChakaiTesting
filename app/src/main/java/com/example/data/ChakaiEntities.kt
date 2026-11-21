package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storeType: String, // StoreType.name
    val name: String,
    val ownerName: String,
    val ownerMobile: String,
    val addressLandmark: String,
    val photoUri: String,
    val rating: Double = 4.5,
    val deliveryTimeMins: String = "25-30 min",
    val status: String = ApprovalStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    val typeEnum: StoreType
        get() = runCatching { StoreType.valueOf(storeType) }.getOrDefault(StoreType.RESTAURANT)

    val statusEnum: ApprovalStatus
        get() = runCatching { ApprovalStatus.valueOf(status) }.getOrDefault(ApprovalStatus.PENDING)
}

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storeId: Long,
    val name: String,
    val category: String,
    val price: Int,
    val unitOrDescription: String,
    val isVeg: Boolean = true,
    val photoUri: String,
    val isAvailable: Boolean = true
)

@Entity(tableName = "delivery_partners")
data class DeliveryPartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val mobile: String,
    val vehicleType: String, // VehicleType.name
    val preferredVillagesCsv: String,
    val status: String = ApprovalStatus.PENDING.name,
    val isOnline: Boolean = true,
    val completedDeliveries: Int = 0,
    val totalEarnings: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val vehicleEnum: VehicleType
        get() = runCatching { VehicleType.valueOf(vehicleType) }.getOrDefault(VehicleType.BIKE)

    val statusEnum: ApprovalStatus
        get() = runCatching { ApprovalStatus.valueOf(status) }.getOrDefault(ApprovalStatus.PENDING)

    val preferredVillages: List<String>
        get() = preferredVillagesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val menuItemId: Long,
    val storeId: Long,
    val storeName: String,
    val storeType: String,
    val itemName: String,
    val unitOrDescription: String,
    val price: Int,
    val photoUri: String,
    val quantity: Int
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String,
    val customerName: String,
    val customerMobile: String,
    val villageArea: String,
    val landmark: String,
    val distanceKm: Int,
    val storeId: Long,
    val storeName: String,
    val storeAddress: String,
    val storeType: String,
    val itemsSummary: String, // e.g., "2x Bihari Litti Chokha (₹160), 1x Paneer Butter Masala (₹190)"
    val itemSubtotal: Int,
    val deliveryFee: Int,
    val grandTotal: Int,
    val paymentMode: String, // PaymentMode.name
    val status: String = OrderStatus.PLACED.name,
    val deliveryPartnerId: Long? = null,
    val deliveryPartnerName: String? = null,
    val deliveryPartnerMobile: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val statusEnum: OrderStatus
        get() = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PLACED)

    val paymentEnum: PaymentMode
        get() = runCatching { PaymentMode.valueOf(paymentMode) }.getOrDefault(PaymentMode.COD)

    val storeTypeEnum: StoreType
        get() = runCatching { StoreType.valueOf(storeType) }.getOrDefault(StoreType.RESTAURANT)

    val deliveryEarning: Int
        get() = (deliveryFee + 15).coerceAtLeast(25)
}

@Entity(tableName = "customers")
data class CustomerRecordEntity(
    @PrimaryKey val mobile: String,
    val name: String,
    val defaultVillage: String = "Chakai Chowk",
    val registeredAt: Long = System.currentTimeMillis()
)
