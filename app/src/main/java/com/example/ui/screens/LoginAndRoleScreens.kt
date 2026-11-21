package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DemoConstants
import com.example.data.UserRole
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.ChakaiOrangeContainer
import com.example.ui.theme.ChakaiOrangeDark
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.KiranaGreenContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

@Composable
fun OtpLoginScreen(
    onLoginVerified: (mobile: String, role: UserRole?) -> Unit
) {
    var mobileInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAdminDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_chakai),
                    contentDescription = "ChakaiGo Food and Grocery Delivery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Black.copy(alpha = 0.78f)
                                )
                            )
                        )
                )
                // Top subtle Admin Login button
                Surface(
                    onClick = { showAdminDialog = true },
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .testTag("header_admin_login_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Login",
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admin Login",
                            style = MaterialTheme.typography.labelMedium,
                            color = PureWhite
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Surface(
                        color = ChakaiOrange,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "CHAKAI • JAMUI • 20 KM RADIUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ChakaiGo  चकाईगो",
                        style = MaterialTheme.typography.displaySmall,
                        color = PureWhite,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "चकाई और 20 किमी के गाँवों में खाना और किराना डिलीवरी",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.92f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Quick Testing Demo Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = KiranaGreenContainer),
                    border = BorderStroke(1.dp, KiranaGreen.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = KiranaGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Quick Testing Demo Access",
                                style = MaterialTheme.typography.labelLarge,
                                color = KiranaGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Demo Number: +91 ${DemoConstants.DEMO_MOBILE} | OTP: ${DemoConstants.DEMO_OTP}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                mobileInput = DemoConstants.DEMO_MOBILE
                                otpInput = DemoConstants.DEMO_OTP
                                otpSent = true
                                errorMessage = null
                                onLoginVerified(DemoConstants.DEMO_MOBILE, null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auto_fill_demo_login_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-Fill Demo Login (1-Click)")
                        }
                    }
                }

                // Login Form Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Mobile Login (मोबाइल नंबर से लॉगिन)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter any 10-digit Indian mobile number. Use OTP 123456 to verify.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = mobileInput,
                            onValueChange = { raw ->
                                val digits = raw.filter { it.isDigit() }.take(10)
                                mobileInput = digits
                                errorMessage = null
                            },
                            label = { Text("10-digit Mobile Number") },
                            prefix = {
                                Text(
                                    text = "+91 ",
                                    fontWeight = FontWeight.Bold,
                                    color = ChakaiOrange
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = ChakaiOrange)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mobile_number_input")
                        )

                        if (!otpSent) {
                            Button(
                                onClick = {
                                    if (mobileInput.length != 10) {
                                        errorMessage = "Please enter a valid 10-digit mobile number (10 अंकों का नंबर डालें)"
                                    } else {
                                        otpSent = true
                                        errorMessage = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ChakaiOrange),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("send_otp_button")
                            ) {
                                Text("Send OTP (ओटीपी भेजें)", style = MaterialTheme.typography.labelLarge)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }

                        AnimatedVisibility(visible = otpSent) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    color = ChakaiOrangeContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "OTP sent to +91 $mobileInput • Enter 123456 to verify",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ChakaiOrangeDark,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { raw ->
                                        otpInput = raw.filter { it.isDigit() }.take(6)
                                        errorMessage = null
                                    },
                                    label = { Text("6-Digit OTP (ओटीपी दर्ज करें)") },
                                    placeholder = { Text("123456") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = KiranaGreen)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_input")
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            otpSent = false
                                            otpInput = ""
                                        },
                                        modifier = Modifier.weight(0.4f)
                                    ) {
                                        Text("Edit")
                                    }
                                    Button(
                                        onClick = {
                                            if (mobileInput.length != 10) {
                                                errorMessage = "Enter a valid 10-digit mobile number"
                                            } else if (otpInput != DemoConstants.DEMO_OTP) {
                                                errorMessage = "Invalid OTP! Please enter 123456"
                                            } else {
                                                errorMessage = null
                                                onLoginVerified(mobileInput, null)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = KiranaGreen),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .weight(0.6f)
                                            .height(48.dp)
                                            .testTag("verify_otp_button")
                                    ) {
                                        Text("Verify OTP")
                                    }
                                }
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Footer subtle Admin Login link
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showAdminDialog = true },
                        modifier = Modifier.testTag("footer_admin_login_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = StatusPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admin Login (एडमिन लॉगिन • PIN 1234)",
                            color = StatusPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showAdminDialog) {
        AdminLoginDialog(
            onDismiss = { showAdminDialog = false },
            onAdminAuthenticated = {
                showAdminDialog = false
                onLoginVerified(DemoConstants.DEMO_MOBILE, UserRole.ADMIN)
            }
        )
    }
}

@Composable
fun RoleSelectionScreen(
    mobileNumber: String,
    pendingApprovalsCount: Int,
    onSelectRole: (UserRole) -> Unit,
    onLogout: () -> Unit
) {
    var showAdminDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ChakaiGo  चकाईगो",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChakaiOrange
                    )
                    Text(
                        text = "Verified: +91 $mobileNumber",
                        style = MaterialTheme.typography.bodySmall,
                        color = KiranaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = onLogout) {
                    Text("Change Number")
                }
            }

            Column {
                Text(
                    text = "Choose Your Role (अपना रोल चुनें)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You can switch between all 5 dashboards anytime from the top bar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RoleSelectionCard(
                title = "1. Customer",
                hindiTitle = "कस्टमर • खाना और किराना मंगाएं",
                description = "Browse approved Chakai restaurants & kirana stores, add to cart & track live village delivery.",
                icon = Icons.Default.ShoppingBag,
                accentColor = ChakaiOrange,
                containerColor = ChakaiOrangeContainer,
                testTag = "role_card_customer",
                onClick = { onSelectRole(UserRole.CUSTOMER) }
            )

            RoleSelectionCard(
                title = "2. Restaurant Partner",
                hindiTitle = "रेस्टोरेंट पार्टनर",
                description = "Register your hotel/dhaba, manage dishes & prices, accept & prepare incoming food orders.",
                icon = Icons.Default.Restaurant,
                accentColor = Color(0xFFE11D48),
                containerColor = Color(0xFFFFE4E6),
                testTag = "role_card_restaurant",
                onClick = { onSelectRole(UserRole.RESTAURANT) }
            )

            RoleSelectionCard(
                title = "3. General Store / Kirana",
                hindiTitle = "जनरल स्टोर / किराना दुकान",
                description = "List daily essentials (Atta, Oil, Milk, Dal, Soap), manage stock & pack customer orders.",
                icon = Icons.Default.Storefront,
                accentColor = KiranaGreen,
                containerColor = KiranaGreenContainer,
                testTag = "role_card_kirana",
                onClick = { onSelectRole(UserRole.GENERAL_STORE) }
            )

            RoleSelectionCard(
                title = "4. Delivery Partner",
                hindiTitle = "डिलीवरी पार्टनर • कमाएं",
                description = "Go Online, pick up ready orders from Chakai stores, deliver within 20 km & track earnings.",
                icon = Icons.Default.DeliveryDining,
                accentColor = StatusBlue,
                containerColor = StatusBlueContainer,
                testTag = "role_card_delivery",
                onClick = { onSelectRole(UserRole.DELIVERY_PARTNER) }
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { showAdminDialog = true },
                    border = BorderStroke(1.dp, StatusPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("role_screen_admin_button")
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = StatusPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (pendingApprovalsCount > 0) {
                            "Admin Panel ($pendingApprovalsCount Pending)"
                        } else {
                            "Admin Login (एडमिन पैनल)"
                        },
                        color = StatusPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showAdminDialog) {
        AdminLoginDialog(
            onDismiss = { showAdminDialog = false },
            onAdminAuthenticated = {
                showAdminDialog = false
                onSelectRole(UserRole.ADMIN)
            }
        )
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    hindiTitle: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = hindiTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open $title",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onAdminAuthenticated: () -> Unit
) {
    var pinOrOtp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = StatusPurple,
                modifier = Modifier.size(34.dp)
            )
        },
        title = {
            Text(
                text = "Admin Control Login",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter Admin PIN (1234) or Demo OTP (123456) to access ChakaiGo Master Control.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = pinOrOtp,
                    onValueChange = {
                        pinOrOtp = it.filter { ch -> ch.isDigit() }.take(6)
                        error = null
                    },
                    label = { Text("Admin PIN or OTP") },
                    placeholder = { Text("1234") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRed
                    )
                }
                TextButton(
                    onClick = {
                        pinOrOtp = DemoConstants.ADMIN_PIN
                        onAdminAuthenticated()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("1-Click Demo Admin Login (PIN 1234)", color = StatusPurple)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pinOrOtp == DemoConstants.ADMIN_PIN || pinOrOtp == DemoConstants.DEMO_OTP) {
                        onAdminAuthenticated()
                    } else {
                        error = "Incorrect PIN. Use 1234 or OTP 123456."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusPurple),
                modifier = Modifier.testTag("admin_login_confirm_button")
            ) {
                Text("Login as Admin")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
