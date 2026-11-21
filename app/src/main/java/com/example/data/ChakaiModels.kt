package com.example.data

enum class UserRole(
    val titleEn: String,
    val titleHi: String,
    val subtitle: String
) {
    CUSTOMER("Customer", "कस्टमर", "Order food & daily kirana"),
    RESTAURANT("Restaurant Partner", "रेस्टोरेंट", "Manage menu & food orders"),
    GENERAL_STORE("General Store / Kirana", "जनरल स्टोर", "Manage groceries & orders"),
    DELIVERY_PARTNER("Delivery Partner", "डिलीवरी पार्टनर", "Deliver in Chakai & earn"),
    ADMIN("Admin", "एडमिन", "Approve partners & monitor live orders")
}

enum class StoreType(val labelEn: String, val labelHi: String) {
    RESTAURANT("Restaurant", "खाना / रेस्टोरेंट"),
    GENERAL_STORE("General Store", "किराना सामान")
}

enum class ApprovalStatus(val labelEn: String, val labelHi: String) {
    PENDING("Pending Admin Approval", "एडमिन अप्रूवल पेंडिंग"),
    APPROVED("Approved", "अप्रूव्ड"),
    REJECTED("Rejected", "रिजेक्टेड")
}

enum class OrderStatus(
    val stepIndex: Int,
    val labelEn: String,
    val labelHi: String
) {
    PLACED(0, "Order Placed", "ऑर्डर प्लेस हुआ"),
    ACCEPTED_BY_STORE(1, "Accepted by Store", "स्टोर ने स्वीकार किया"),
    READY_FOR_PICKUP(1, "Ready for Pickup", "पिकअप के लिए तैयार"),
    DELIVERY_ACCEPTED(2, "Partner Assigned", "पार्टनर असाइन हुआ"),
    PICKED_UP(2, "Picked Up / Out for Delivery", "डिलीवरी के लिए निकला"),
    DELIVERED(3, "Delivered", "डिलीवर हो गया")
}

enum class PaymentMode(val labelEn: String, val labelHi: String) {
    COD("Cash on Delivery", "कैश ऑन डिलीवरी"),
    UPI("UPI", "यूपीआई पेमेंट")
}

enum class VehicleType(val label: String) {
    BIKE("Bike"),
    CYCLE("Cycle"),
    E_RICKSHAW("E-Rickshaw")
}

data class VillageArea(
    val nameEn: String,
    val nameHi: String,
    val distanceKm: Int,
    val deliveryFee: Int
) {
    val displayLabel: String
        get() = "$nameEn ($nameHi) • ${distanceKm} km • ₹$deliveryFee"
}

object ChakaiLocations {
    val villages: List<VillageArea> = listOf(
        VillageArea("Chakai Chowk", "चकाई चौक", 0, 10),
        VillageArea("Chakai Bazar / Bus Stand", "चकाई बाजार", 1, 10),
        VillageArea("Madhopur", "माधोपुर", 3, 15),
        VillageArea("Basbutti", "बसबुट्टी", 4, 15),
        VillageArea("Nawada Chakai", "नवादा चकाई", 5, 15),
        VillageArea("Bamdah", "बामदह", 6, 18),
        VillageArea("Batpar", "बटपार", 8, 20),
        VillageArea("Gajhi", "गजही", 9, 20),
        VillageArea("Chhachhudih", "छछुडीह", 11, 22),
        VillageArea("Bichkorwa", "बिचकोरवा", 13, 25),
        VillageArea("Saroun", "सरौन", 15, 25),
        VillageArea("Ramchandradih", "रामचन्द्रडीह", 17, 28),
        VillageArea("Dulampur", "दुलमपुर", 19, 30),
        VillageArea("Chandramandi", "चंद्रमंडी", 20, 30)
    )

    fun findByName(name: String): VillageArea {
        return villages.firstOrNull {
            it.nameEn.equals(name, ignoreCase = true) || name.contains(it.nameEn, ignoreCase = true)
        } ?: villages.first()
    }
}

object DemoConstants {
    const val DEMO_MOBILE = "9999999911"
    const val DEMO_OTP = "123456"
    const val ADMIN_PIN = "1234"
}

object PresetImageKeys {
    const val HERO = "preset:hero"
    const val THALI = "preset:thali"
    const val SNACKS = "preset:snacks"
    const val KIRANA = "preset:kirana"

    val allPresets: List<Pair<String, String>> = listOf(
        THALI to "Thali & Meals",
        SNACKS to "Snacks & Fast Food",
        KIRANA to "Kirana Essentials",
        HERO to "Chakai Special"
    )
}
