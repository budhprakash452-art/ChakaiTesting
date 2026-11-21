package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StoreEntity::class,
        MenuItemEntity::class,
        DeliveryPartnerEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        CustomerRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChakaiDatabase : RoomDatabase() {
    abstract fun chakaiDao(): ChakaiDao

    companion object {
        @Volatile
        private var INSTANCE: ChakaiDatabase? = null

        fun getInstance(context: Context): ChakaiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChakaiDatabase::class.java,
                    "chakaigo_local.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

object ChakaiDemoSeeder {
    suspend fun seedDemoData(dao: ChakaiDao) {
        val now = System.currentTimeMillis()

        // Customers
        dao.upsertCustomer(CustomerRecordEntity(DemoConstants.DEMO_MOBILE, "Rahul Kumar", "Chakai Chowk", now - 86_400_000L * 4))
        dao.upsertCustomer(CustomerRecordEntity("9801456721", "Priya Devi", "Bamdah", now - 86_400_000L * 3))
        dao.upsertCustomer(CustomerRecordEntity("9431209876", "Ravi Shankar", "Madhopur", now - 86_400_000L * 2))
        dao.upsertCustomer(CustomerRecordEntity("9122334455", "Sunita Kumari", "Batpar", now - 86_400_000L))

        // Approved Restaurants
        val r1Id = dao.insertStore(
            StoreEntity(
                storeType = StoreType.RESTAURANT.name,
                name = "Maa Durga Family Restaurant",
                ownerName = "Rajesh Barnwal",
                ownerMobile = DemoConstants.DEMO_MOBILE,
                addressLandmark = "Near Chakai Chowk, Main Road, Chakai",
                photoUri = PresetImageKeys.THALI,
                rating = 4.6,
                deliveryTimeMins = "25-30 min",
                status = ApprovalStatus.APPROVED.name,
                createdAt = now - 500_000L
            )
        )
        val r2Id = dao.insertStore(
            StoreEntity(
                storeType = StoreType.RESTAURANT.name,
                name = "Shahi Tandoor & Bihari Dhaba",
                ownerName = "Manoj Singh",
                ownerMobile = "9835112233",
                addressLandmark = "Near Chakai Block Office, Jamui Road",
                photoUri = PresetImageKeys.HERO,
                rating = 4.5,
                deliveryTimeMins = "30-35 min",
                status = ApprovalStatus.APPROVED.name,
                createdAt = now - 450_000L
            )
        )
        val r3Id = dao.insertStore(
            StoreEntity(
                storeType = StoreType.RESTAURANT.name,
                name = "Chakai Spice & Fast Food Corner",
                ownerName = "Deepak Gupta",
                ownerMobile = "9708223344",
                addressLandmark = "Bus Stand Road, Chakai Bazar",
                photoUri = PresetImageKeys.SNACKS,
                rating = 4.4,
                deliveryTimeMins = "20-25 min",
                status = ApprovalStatus.APPROVED.name,
                createdAt = now - 400_000L
            )
        )
        // Pending Restaurant for Admin Approval demo
        val rPendingId = dao.insertStore(
            StoreEntity(
                storeType = StoreType.RESTAURANT.name,
                name = "Champaran Handi House",
                ownerName = "Santosh Yadav",
                ownerMobile = "9934556677",
                addressLandmark = "Madhopur More, NH-333, Chakai",
                photoUri = PresetImageKeys.THALI,
                rating = 4.5,
                deliveryTimeMins = "30 min",
                status = ApprovalStatus.PENDING.name,
                createdAt = now - 120_000L
            )
        )

        // Approved General Stores / Kirana
        val g1Id = dao.insertStore(
            StoreEntity(
                storeType = StoreType.GENERAL_STORE.name,
                name = "Shree Ganesh Kirana & General Store",
                ownerName = "Suresh Keshri",
                ownerMobile = DemoConstants.DEMO_MOBILE,
                addressLandmark = "Chakai Chowk, Near SBI ATM",
                photoUri = PresetImageKeys.KIRANA,
                rating = 4.7,
                deliveryTimeMins = "15-20 min",
                status = ApprovalStatus.APPROVED.name,
                createdAt = now - 350_000L
            )
        )
        val g2Id = dao.insertStore(
            StoreEntity(
                storeType = StoreType.GENERAL_STORE.name,
                name = "Barnwal Super Mart & Daily Needs",
                ownerName = "Anil Barnwal",
                ownerMobile = "9431887766",
                addressLandmark = "Main Bazar, Opposite High School, Chakai",
                photoUri = PresetImageKeys.KIRANA,
                rating = 4.5,
                deliveryTimeMins = "20-25 min",
                status = ApprovalStatus.APPROVED.name,
                createdAt = now - 300_000L
            )
        )
        // Pending General Store for Admin Approval demo
        val gPendingId = dao.insertStore(
            StoreEntity(
                storeType = StoreType.GENERAL_STORE.name,
                name = "Kisan Fresh Kirana & Dairy Mart",
                ownerName = "Pramod Mahto",
                ownerMobile = "9570443322",
                addressLandmark = "Bamdah Road, Near Petrol Pump, Chakai",
                photoUri = PresetImageKeys.KIRANA,
                rating = 4.4,
                deliveryTimeMins = "20 min",
                status = ApprovalStatus.PENDING.name,
                createdAt = now - 90_000L
            )
        )

        // Food Menu Items
        dao.insertMenuItems(
            listOf(
                MenuItemEntity(
                    storeId = r1Id,
                    name = "Special Bihari Litti Chokha (Ghee)",
                    category = "Bihari Special",
                    price = 80,
                    unitOrDescription = "4 pcs roasted litti with baingan-aloo chokha & chutney",
                    isVeg = true,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r1Id,
                    name = "Paneer Butter Masala",
                    category = "Main Course",
                    price = 190,
                    unitOrDescription = "Rich creamy tomato gravy with soft paneer cubes",
                    isVeg = true,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r1Id,
                    name = "Deluxe Veg Thali (शाही थाली)",
                    category = "Thali",
                    price = 140,
                    unitOrDescription = "Dal tadka, paneer sabzi, jeera rice, 4 roti, raita & sweet",
                    isVeg = true,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r1Id,
                    name = "Butter Naan (2 pcs)",
                    category = "Breads",
                    price = 65,
                    unitOrDescription = "Tandoor baked soft naan brushed with Amul butter",
                    isVeg = true,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r2Id,
                    name = "Champaran Mutton / Chicken Handi (Half)",
                    category = "Non-Veg Special",
                    price = 260,
                    unitOrDescription = "Slow-cooked earthen pot masala chicken with whole garlic",
                    isVeg = false,
                    photoUri = PresetImageKeys.HERO
                ),
                MenuItemEntity(
                    storeId = r2Id,
                    name = "Hyderabadi Chicken Biryani",
                    category = "Biryani",
                    price = 180,
                    unitOrDescription = "Full plate aromatic basmati biryani with raita & salan",
                    isVeg = false,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r2Id,
                    name = "Dal Tadka & Jeera Rice Combo",
                    category = "Main Course",
                    price = 125,
                    unitOrDescription = "Desi ghee jeera tadka arhar dal with steamed basmati rice",
                    isVeg = true,
                    photoUri = PresetImageKeys.THALI
                ),
                MenuItemEntity(
                    storeId = r3Id,
                    name = "Hot Chakai Samosa Plate (2 pcs)",
                    category = "Snacks",
                    price = 30,
                    unitOrDescription = "Crispy spicy potato samosas with tamarind & mint chutney",
                    isVeg = true,
                    photoUri = PresetImageKeys.SNACKS
                ),
                MenuItemEntity(
                    storeId = r3Id,
                    name = "Veg Hakka Chowmein (Full)",
                    category = "Fast Food",
                    price = 90,
                    unitOrDescription = "Street-style smoky wok-tossed noodles with veggies",
                    isVeg = true,
                    photoUri = PresetImageKeys.SNACKS
                ),
                MenuItemEntity(
                    storeId = r3Id,
                    name = "Crispy Masala Dosa",
                    category = "South Indian",
                    price = 85,
                    unitOrDescription = "Served hot with sambhar and fresh coconut chutney",
                    isVeg = true,
                    photoUri = PresetImageKeys.SNACKS
                ),
                MenuItemEntity(
                    storeId = r3Id,
                    name = "Paneer Chilli Dry",
                    category = "Fast Food",
                    price = 150,
                    unitOrDescription = "Crispy paneer tossed with bell peppers & spicy garlic sauce",
                    isVeg = true,
                    photoUri = PresetImageKeys.SNACKS
                ),
                MenuItemEntity(
                    storeId = rPendingId,
                    name = "Ahuna Chicken Handi (Full)",
                    category = "Non-Veg Special",
                    price = 420,
                    unitOrDescription = "Signature earthen handi chicken, serves 3",
                    isVeg = false,
                    photoUri = PresetImageKeys.THALI
                ),
                // Kirana / General Store Essentials
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Fortune Kachi Ghani Mustard Oil (सरसों तेल)",
                    category = "Oil & Ghee",
                    price = 155,
                    unitOrDescription = "1L",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Sudha Gold Fresh Milk (सुधा दूध)",
                    category = "Dairy",
                    price = 32,
                    unitOrDescription = "500ml",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Aashirvaad Shudh Chakki Atta (आटा)",
                    category = "Atta & Rice",
                    price = 235,
                    unitOrDescription = "5kg",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Tata Sampann Toor Dal (अरहर दाल)",
                    category = "Dal & Pulses",
                    price = 168,
                    unitOrDescription = "1kg",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Uttam Crystal Sugar (चीनी)",
                    category = "Daily Essentials",
                    price = 48,
                    unitOrDescription = "1kg",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g1Id,
                    name = "Lifebuoy Total Bath Soap",
                    category = "Personal Care",
                    price = 120,
                    unitOrDescription = "1 Pack (4 pcs)",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g2Id,
                    name = "India Gate Basmati Rice (बासमती चावल)",
                    category = "Atta & Rice",
                    price = 115,
                    unitOrDescription = "1kg",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g2Id,
                    name = "Tata Tea Premium (चाय पत्ती)",
                    category = "Daily Essentials",
                    price = 240,
                    unitOrDescription = "500g",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g2Id,
                    name = "Surf Excel Quick Wash Detergent",
                    category = "Household",
                    price = 145,
                    unitOrDescription = "1kg",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = g2Id,
                    name = "Amul Pasteurised Butter",
                    category = "Dairy",
                    price = 60,
                    unitOrDescription = "100g",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                ),
                MenuItemEntity(
                    storeId = gPendingId,
                    name = "Fresh Deshi Cow Ghee",
                    category = "Oil & Ghee",
                    price = 340,
                    unitOrDescription = "500ml",
                    isVeg = true,
                    photoUri = PresetImageKeys.KIRANA
                )
            )
        )

        // Delivery Partners
        val dp1Id = dao.insertDeliveryPartner(
            DeliveryPartnerEntity(
                fullName = "Sandeep Kumar",
                mobile = DemoConstants.DEMO_MOBILE,
                vehicleType = VehicleType.BIKE.name,
                preferredVillagesCsv = "Chakai Chowk, Chakai Bazar / Bus Stand, Madhopur, Bamdah, Batpar",
                status = ApprovalStatus.APPROVED.name,
                isOnline = true,
                completedDeliveries = 24,
                totalEarnings = 840,
                createdAt = now - 600_000L
            )
        )
        dao.insertDeliveryPartner(
            DeliveryPartnerEntity(
                fullName = "Amit Mandal",
                mobile = "9801122334",
                vehicleType = VehicleType.BIKE.name,
                preferredVillagesCsv = "Chakai Chowk, Gajhi, Saroun, Bichkorwa",
                status = ApprovalStatus.APPROVED.name,
                isOnline = true,
                completedDeliveries = 18,
                totalEarnings = 615,
                createdAt = now - 550_000L
            )
        )
        // Pending Delivery Partner for Admin Approval demo
        dao.insertDeliveryPartner(
            DeliveryPartnerEntity(
                fullName = "Vikas Yadav",
                mobile = "9708844556",
                vehicleType = VehicleType.E_RICKSHAW.name,
                preferredVillagesCsv = "Madhopur, Dulampur, Chandramandi, Ramchandradih",
                status = ApprovalStatus.PENDING.name,
                isOnline = false,
                completedDeliveries = 0,
                totalEarnings = 0,
                createdAt = now - 80_000L
            )
        )

        // Pre-loaded Live Demo Orders
        dao.insertOrder(
            OrderEntity(
                orderCode = "CG-1041",
                customerName = "Rahul Kumar",
                customerMobile = DemoConstants.DEMO_MOBILE,
                villageArea = "Madhopur",
                landmark = "Near Shiv Mandir, Madhopur",
                distanceKm = 3,
                storeId = r1Id,
                storeName = "Maa Durga Family Restaurant",
                storeAddress = "Near Chakai Chowk, Main Road, Chakai",
                storeType = StoreType.RESTAURANT.name,
                itemsSummary = "2x Special Bihari Litti Chokha (₹160), 1x Paneer Butter Masala (₹190)",
                itemSubtotal = 350,
                deliveryFee = 15,
                grandTotal = 365,
                paymentMode = PaymentMode.UPI.name,
                status = OrderStatus.READY_FOR_PICKUP.name,
                createdAt = now - 900_000L
            )
        )
        dao.insertOrder(
            OrderEntity(
                orderCode = "CG-1042",
                customerName = "Priya Devi",
                customerMobile = "9801456721",
                villageArea = "Bamdah",
                landmark = "Bamdah High School Gate",
                distanceKm = 6,
                storeId = g1Id,
                storeName = "Shree Ganesh Kirana & General Store",
                storeAddress = "Chakai Chowk, Near SBI ATM",
                storeType = StoreType.GENERAL_STORE.name,
                itemsSummary = "1x Fortune Mustard Oil 1L (₹155), 2x Sudha Gold Milk 500ml (₹64), 1x Sugar 1kg (₹48)",
                itemSubtotal = 267,
                deliveryFee = 18,
                grandTotal = 285,
                paymentMode = PaymentMode.COD.name,
                status = OrderStatus.PLACED.name,
                createdAt = now - 450_000L
            )
        )
        dao.insertOrder(
            OrderEntity(
                orderCode = "CG-1039",
                customerName = "Rahul Kumar",
                customerMobile = DemoConstants.DEMO_MOBILE,
                villageArea = "Chakai Chowk",
                landmark = "Near Bus Stand Gate",
                distanceKm = 0,
                storeId = r2Id,
                storeName = "Shahi Tandoor & Bihari Dhaba",
                storeAddress = "Near Chakai Block Office, Jamui Road",
                storeType = StoreType.RESTAURANT.name,
                itemsSummary = "1x Hyderabadi Chicken Biryani (₹180), 1x Dal Tadka & Jeera Rice Combo (₹125)",
                itemSubtotal = 305,
                deliveryFee = 10,
                grandTotal = 315,
                paymentMode = PaymentMode.COD.name,
                status = OrderStatus.DELIVERED.name,
                deliveryPartnerId = dp1Id,
                deliveryPartnerName = "Sandeep Kumar",
                deliveryPartnerMobile = DemoConstants.DEMO_MOBILE,
                createdAt = now - 7_200_000L
            )
        )
    }
}
