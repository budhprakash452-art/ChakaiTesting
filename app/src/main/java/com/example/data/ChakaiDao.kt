package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChakaiDao {
    // Stores
    @Query("SELECT * FROM stores ORDER BY createdAt DESC")
    fun observeAllStores(): Flow<List<StoreEntity>>

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun getStoreCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity): Long

    @Update
    suspend fun updateStore(store: StoreEntity)

    @Query("UPDATE stores SET status = :status WHERE id = :storeId")
    suspend fun updateStoreStatus(storeId: Long, status: String)

    @Query("DELETE FROM stores WHERE id = :storeId")
    suspend fun deleteStoreById(storeId: Long)

    // Menu & Grocery Items
    @Query("SELECT * FROM menu_items ORDER BY id DESC")
    fun observeAllMenuItems(): Flow<List<MenuItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)

    @Query("DELETE FROM menu_items WHERE id = :itemId")
    suspend fun deleteMenuItemById(itemId: Long)

    @Query("DELETE FROM menu_items WHERE storeId = :storeId")
    suspend fun deleteMenuItemsByStore(storeId: Long)

    // Delivery Partners
    @Query("SELECT * FROM delivery_partners ORDER BY createdAt DESC")
    fun observeAllDeliveryPartners(): Flow<List<DeliveryPartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryPartner(partner: DeliveryPartnerEntity): Long

    @Update
    suspend fun updateDeliveryPartner(partner: DeliveryPartnerEntity)

    @Query("UPDATE delivery_partners SET status = :status WHERE id = :partnerId")
    suspend fun updateDeliveryPartnerStatus(partnerId: Long, status: String)

    @Query("UPDATE delivery_partners SET isOnline = :isOnline WHERE id = :partnerId")
    suspend fun updateDeliveryPartnerOnline(partnerId: Long, isOnline: Boolean)

    @Query("UPDATE delivery_partners SET completedDeliveries = completedDeliveries + 1, totalEarnings = totalEarnings + :earning WHERE id = :partnerId")
    suspend fun creditDeliveryPartnerEarning(partnerId: Long, earning: Int)

    // Cart
    @Query("SELECT * FROM cart_items ORDER BY menuItemId ASC")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE menuItemId = :menuItemId LIMIT 1")
    suspend fun getCartItem(menuItemId: Long): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE menuItemId = :menuItemId")
    suspend fun deleteCartItem(menuItemId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("UPDATE orders SET status = :status, deliveryPartnerId = :partnerId, deliveryPartnerName = :partnerName, deliveryPartnerMobile = :partnerMobile WHERE id = :orderId")
    suspend fun assignDeliveryPartnerAndUpdateStatus(
        orderId: Long,
        status: String,
        partnerId: Long,
        partnerName: String,
        partnerMobile: String
    )

    // Customers
    @Query("SELECT * FROM customers ORDER BY registeredAt DESC")
    fun observeAllCustomers(): Flow<List<CustomerRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomer(customer: CustomerRecordEntity)

    // Reset helper
    @Query("DELETE FROM stores")
    suspend fun clearStores()

    @Query("DELETE FROM menu_items")
    suspend fun clearMenuItems()

    @Query("DELETE FROM delivery_partners")
    suspend fun clearDeliveryPartners()

    @Query("DELETE FROM orders")
    suspend fun clearOrders()

    @Query("DELETE FROM customers")
    suspend fun clearCustomers()
}
