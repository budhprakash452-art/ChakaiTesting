package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ApprovalStatus
import com.example.data.OrderStatus
import com.example.data.PresetImageKeys
import com.example.data.UserRole
import com.example.ui.theme.ChakaiOrange
import com.example.ui.theme.ChakaiOrangeContainer
import com.example.ui.theme.KiranaGreen
import com.example.ui.theme.KiranaGreenContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldContainer
import com.example.ui.theme.WarmGoldText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChakaiTopBar(
    activeRole: UserRole,
    mobileNumber: String,
    pendingApprovalsCount: Int,
    onSwitchRole: (UserRole) -> Unit,
    onGoToRolePicker: () -> Unit,
    onResetDemoData: () -> Unit,
    onLogout: () -> Unit,
    subtitle: String = "Chakai, Jamui (20 km)"
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val accentColor = when (activeRole) {
        UserRole.GENERAL_STORE -> KiranaGreen
        UserRole.ADMIN -> StatusPurple
        UserRole.DELIVERY_PARTNER -> StatusBlue
        else -> ChakaiOrange
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = roleIcon(activeRole),
                        contentDescription = activeRole.titleEn,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ChakaiGo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = accentColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = activeRole.titleHi,
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        actions = {
            Box {
                Surface(
                    onClick = { menuExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    color = ChakaiOrangeContainer,
                    border = BorderStroke(1.dp, ChakaiOrange.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("switch_role_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            tint = ChakaiOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Switch",
                            style = MaterialTheme.typography.labelMedium,
                            color = ChakaiOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text(
                            text = "Switch Dashboard (रोल बदलें)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Logged in: +91 $mobileNumber",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider()
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${role.titleEn} (${role.titleHi})",
                                        fontWeight = if (role == activeRole) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (role == UserRole.ADMIN && pendingApprovalsCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = StatusRed,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "$pendingApprovalsCount",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PureWhite,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = roleIcon(role),
                                    contentDescription = null,
                                    tint = if (role == activeRole) ChakaiOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (role == activeRole) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = KiranaGreen
                                    )
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                onSwitchRole(role)
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("All Roles Screen") },
                        leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onGoToRolePicker()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Reset Chakai Demo Data") },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onResetDemoData()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout / Change Number", color = StatusRed) },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = StatusRed
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }
    )
}

fun roleIcon(role: UserRole): ImageVector = when (role) {
    UserRole.CUSTOMER -> Icons.Default.ShoppingBag
    UserRole.RESTAURANT -> Icons.Default.Restaurant
    UserRole.GENERAL_STORE -> Icons.Default.Storefront
    UserRole.DELIVERY_PARTNER -> Icons.Default.DeliveryDining
    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
}

fun presetDrawableForKey(key: String): Int = when (key) {
    PresetImageKeys.HERO -> R.drawable.img_hero_chakai
    PresetImageKeys.SNACKS -> R.drawable.img_food_snacks
    PresetImageKeys.KIRANA -> R.drawable.img_kirana_store
    else -> R.drawable.img_food_thali
}

@Composable
fun SmartProductImage(
    photoUri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    fallbackPreset: String = PresetImageKeys.THALI
) {
    val isPreset = photoUri.isBlank() || photoUri.startsWith("preset:")
    if (isPreset) {
        Image(
            painter = painterResource(id = presetDrawableForKey(photoUri.ifBlank { fallbackPreset })),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        val fallbackRes = presetDrawableForKey(fallbackPreset)
        AsyncImage(
            model = if (photoUri.startsWith("content://") || photoUri.startsWith("file://")) {
                Uri.parse(photoUri)
            } else {
                photoUri
            },
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = fallbackRes),
            error = painterResource(id = fallbackRes),
            modifier = modifier
        )
    }
}

@Composable
fun PhotoPickerField(
    label: String,
    photoUri: String,
    onPhotoChanged: (String) -> Unit,
    defaultPreset: String = PresetImageKeys.THALI,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoChanged(uri.toString())
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmartProductImage(
                photoUri = photoUri.ifBlank { defaultPreset },
                contentDescription = label,
                fallbackPreset = defaultPreset,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Photo (गैलरी से चुनें)", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedTextField(
                    value = if (photoUri.startsWith("preset:")) "" else photoUri,
                    onValueChange = { onPhotoChanged(it) },
                    placeholder = { Text("Or paste image URL (optional)", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetImageKeys.allPresets.forEach { (key, title) ->
                FilterChip(
                    selected = photoUri == key,
                    onClick = { onPhotoChanged(key) },
                    label = { Text(title, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}

@Composable
fun ApprovalBadge(status: ApprovalStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        ApprovalStatus.APPROVED -> KiranaGreenContainer to KiranaGreen
        ApprovalStatus.PENDING -> WarmGoldContainer to WarmGoldText
        ApprovalStatus.REJECTED -> StatusRedContainer to StatusRed
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.labelEn,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        OrderStatus.PLACED -> WarmGoldContainer to WarmGoldText
        OrderStatus.ACCEPTED_BY_STORE -> ChakaiOrangeContainer to ChakaiOrange
        OrderStatus.READY_FOR_PICKUP -> StatusPurpleContainer to StatusPurple
        OrderStatus.DELIVERY_ACCEPTED, OrderStatus.PICKED_UP -> StatusBlueContainer to StatusBlue
        OrderStatus.DELIVERED -> KiranaGreenContainer to KiranaGreen
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = "${status.labelEn} • ${status.labelHi}",
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun VegNonVegDot(isVeg: Boolean, modifier: Modifier = Modifier) {
    val color = if (isVeg) KiranaGreen else StatusRed
    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, color, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
fun OrderProgressStepper(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        "Placed" to "ऑर्डर हुआ",
        "Accepted" to "स्टोर तैयार",
        "Out for Delivery" to "रास्ते में",
        "Delivered" to "डिलीवर"
    )
    val activeStep = currentStatus.stepIndex

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (en, hi) ->
            val completed = index <= activeStep
            val isCurrent = index == activeStep
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                if (index == 0) Color.Transparent
                                else if (completed) KiranaGreen
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 24.dp else 20.dp)
                            .clip(CircleShape)
                            .background(if (completed) KiranaGreen else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (completed) KiranaGreen else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                if (index == steps.lastIndex) Color.Transparent
                                else if (index < activeStep) KiranaGreen
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = en,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal,
                    color = if (completed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = hi,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PendingApprovalScreen(
    partnerTitle: String,
    partnerSubtitle: String,
    status: ApprovalStatus,
    onSwitchToAdmin: () -> Unit,
    onRegisterAnother: (() -> Unit)? = null
) {
    val isRejected = status == ApprovalStatus.REJECTED
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isRejected) StatusRedContainer else WarmGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRejected) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = if (isRejected) StatusRed else WarmGold,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ApprovalBadge(status = status)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isRejected) {
                        "आपका रजिस्ट्रेशन एडमिन द्वारा रिजेक्ट किया गया है"
                    } else {
                        "आपका रजिस्ट्रेशन एडमिन अप्रूवल के लिए पेंडिंग है"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$partnerTitle\n$partnerSubtitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ChakaiOrangeContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Menu listing, orders & customer visibility unlock automatically once ChakaiGo Admin approves your profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onSwitchToAdmin,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_go_admin_button")
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Admin Panel to Approve")
                }
                if (onRegisterAnother != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRegisterAnother,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit New Registration")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
