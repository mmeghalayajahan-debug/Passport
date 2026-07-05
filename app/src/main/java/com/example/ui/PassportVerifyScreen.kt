package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.focus.FocusManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.data.PassportRecord
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TechPrimary
import com.example.ui.theme.TechSecondary
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PassportVerifyScreen(viewModel: PassportViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val hasSearched by viewModel.hasSearched.collectAsState()
    val allPassports by viewModel.allPassports.collectAsState()

    val regSuccess by viewModel.registrationSuccess.collectAsState()
    val regError by viewModel.registrationError.collectAsState()

    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Verify Lookup", "Register Document")

    // Handle Registration Toast Messages
    LaunchedEffect(regSuccess, regError) {
        if (regSuccess != null) {
            snackbarHostState.showSnackbar(regSuccess!!)
            viewModel.clearNotifications()
            activeTab = 0 // Switch to lookup screen to view the newly created passport
        } else if (regError != null) {
            snackbarHostState.showSnackbar(regError!!)
            viewModel.clearNotifications()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Apply safe navigation padding for the bottom bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TechSurface)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SECUREVERIFY SYSTEM • CLASSIFIED BORDER CONTROL • VERSION 4.1",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TechBackground)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // High-Tech Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Banner background drawable
                Image(
                    painter = painterResource(id = R.drawable.img_app_banner),
                    contentDescription = "Security Dashboard Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Ambient glassmorphic dark overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    TechBackground.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Header content overlay
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TechSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SECUREVERIFY",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        Text(
                            text = "Global Passport Verification & Threat Alert System",
                            color = TechSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "High Tech Scan",
                        tint = TechSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Material 3 Tabs
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = TechSurface,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = TechSecondary,
                        height = 3.dp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.testTag(
                                    if (index == 0) "search_tab_button" else "register_tab_button"
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.QrCodeScanner else Icons.Default.AppRegistration,
                                contentDescription = null,
                                tint = if (activeTab == index) TechSecondary else TextSecondary
                            )
                        },
                        selectedContentColor = TechSecondary,
                        unselectedContentColor = TextSecondary,
                        modifier = Modifier.height(72.dp)
                    )
                }
            }

            // Content based on Active Tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (activeTab == 0) {
                    SearchAndVerifyPanel(
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        searchResult = searchResult,
                        isSearching = isSearching,
                        hasSearched = hasSearched,
                        allPassports = allPassports,
                        focusManager = focusManager,
                        onScanClick = {
                            focusManager.clearFocus()
                            viewModel.searchPassport()
                        }
                    )
                } else {
                    RegisterPassportPanel(
                        viewModel = viewModel,
                        focusManager = focusManager
                    )
                }
            }
        }
    }
}

@Composable
fun SearchAndVerifyPanel(
    viewModel: PassportViewModel,
    searchQuery: String,
    searchResult: PassportRecord?,
    isSearching: Boolean,
    hasSearched: Boolean,
    allPassports: List<PassportRecord>,
    focusManager: FocusManager,
    onScanClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                border = BorderStroke(1.dp, TechSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SCAN CREDENTIAL NUMBER",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Enter Passport Number (e.g. US1234567)", color = TextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = TechSecondary,
                                unfocusedBorderColor = TechBorder,
                                cursorColor = TechSecondary
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { onScanClick() }
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = TechSecondary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.resetSearch() }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Clear search",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("passport_search_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onScanClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechSecondary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("passport_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search icon"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SCAN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Search Results state or Empty guide
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanning...",
                            tint = TechSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DECRYPTING CHIP DATA...",
                            color = TechSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else if (hasSearched) {
            if (searchResult != null) {
                // Display the Passport Results Screen
                item {
                    PassportResultCard(
                        record = searchResult,
                        onToggleFlag = { flagged, reason ->
                            viewModel.toggleFlagStatus(searchResult.passportNumber, flagged, reason)
                        },
                        onDelete = {
                            viewModel.deletePassport(searchResult.passportNumber)
                        }
                    )
                }
            } else {
                // Record Not Found Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = WarningRedBg.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, WarningRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Not Found Alert",
                                tint = WarningRed,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "INVALID CREDENTIAL / NO RECORD",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "The passport number \"$searchQuery\" was not found in the secure registry database. Confirm the input or register a new document credential in the Registry tab.",
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // First Launch / Empty state with Quick Selection Seeding List
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TechSurface.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, TechSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Search Guide",
                            tint = TechSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "PASSPORT CHIP VERIFICATION",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To look up a credential and view associated biodata, enter a passport number in the input scanner above, or select from the secure registry roster below to run a test scan.",
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick Selection Database Roster
            item {
                Text(
                    text = "ACTIVE SECURE REGISTRY ROSTER (${allPassports.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            if (allPassports.isEmpty()) {
                item {
                    Text(
                        text = "Loading registry data...",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                items(allPassports) { passport ->
                    QuickSelectPassportRow(
                        passport = passport,
                        onSelect = {
                            viewModel.selectPassportForSearch(passport.passportNumber)
                        }
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun QuickSelectPassportRow(
    passport: PassportRecord,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (passport.isFlagged) WarningRed.copy(alpha = 0.5f) else TechSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("quick_select_item_${passport.passportNumber}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mini photo circle or default shield
                val context = LocalContext.current
                val resourceId = remember(passport.photoDrawableName, passport.fullName, passport.passportNumber) {
                    val nameToUse = if (passport.fullName.uppercase().contains("KAMAL") || passport.fullName.uppercase().contains("HOSEN") || passport.passportNumber == "A07176865") {
                        "img_passport_kamal_hosen"
                    } else {
                        passport.photoDrawableName
                    }
                    getResIdByName(context, nameToUse)
                }
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        colorFilter = if (passport.isFlagged) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TechSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TechSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = passport.passportNumber,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    Text(
                        text = passport.fullName,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Flag/Status Pills
            if (passport.isFlagged) {
                Box(
                    modifier = Modifier
                        .background(WarningRedBg.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = WarningRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BLOCKED",
                            color = WarningRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(SuccessGreenBg.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIVE",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, TechSurfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            content()
        }
    }
}

@Composable
fun PassportResultCard(
    record: PassportRecord,
    onToggleFlag: (Boolean, String) -> Unit,
    onDelete: () -> Unit
) {
    var showBlockDialog by remember { mutableStateOf(false) }
    var flagReasonText by remember { mutableStateOf("") }
    var showPhotoLightbox by remember { mutableStateOf(false) }

    val securityGlowColor by animateColorAsState(
        targetValue = if (record.isFlagged) WarningRed else TechSecondary,
        label = "Glow border color"
    )

    val context = LocalContext.current
    val photoResId = remember(record.photoDrawableName, record.fullName, record.passportNumber) {
        val nameToUse = if (record.fullName.uppercase().contains("KAMAL") || record.fullName.uppercase().contains("HOSEN") || record.passportNumber == "A07176865") {
            "img_passport_kamal_hosen"
        } else {
            record.photoDrawableName
        }
        getResIdByName(context, nameToUse)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("passport_result_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Prominent Warning Header if the Passport is BLOCKED
            if (record.isFlagged) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningRed),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("blocked_warning_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarningRedBg,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ENTRY DENIED",
                                color = WarningRedBg,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "This passport is flagged: ${record.flagReason.uppercase()}",
                                color = WarningRedBg.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            } else {
                // Clear Valid Stamp Header
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ACTIVE & VERIFIED PASSPORT CREDENTIAL",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Official Passport Document Layout - Styled as a Bento Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Drawing subtle decorative official security microprint lines
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        val lineCount = 3
                        val step = size.height / (lineCount + 1)
                        for (i in 1..lineCount) {
                            val y = step * i
                            drawLine(
                                color = TechSurfaceVariant.copy(alpha = 0.15f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = pathEffect
                            )
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top header card with authority
                Card(
                    colors = CardDefaults.cardColors(containerColor = TechSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, TechSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PASSPORT / PASSEPORT",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "OFFICIAL SECURE RECORD",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Authority Stamp
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "AUTHORITY: ${record.nationality.uppercase()}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "SECURE CHIP v2.1",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Bento Grid Row 1: Full Name (Span 2 / Full Width)
                BentoCard(
                    label = "FULL NAME / NOM COMPLET",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = record.fullName.uppercase(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // Bento Grid Row 2: Nationality & Passport Number (Side by side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoCard(
                        label = "NATIONALITY / NATIONALITÉ",
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.nationality.uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    BentoCard(
                        label = "PASSPORT NO. / N° PASSEPORT",
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.passportNumber.uppercase(),
                            color = TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Bento Grid Row 3: Expiry Date & Date of Birth / Gender (Side by side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoCard(
                        label = "EXPIRY / EXPIRATION",
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.expiryDate.uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    BentoCard(
                        label = "GENDER & DOB / SEXE & NAISSANCE",
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${record.gender} • ${record.dateOfBirth}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Bento Grid Row 4: Associated Address and Scan Photo (Span 2 / Full Width)
                BentoCard(
                    label = "ASSOCIATED ADDRESS & SCAN / ADRESSE ENREGISTRÉE",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = record.address,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Encrypted Photo Scanner Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TechBackground)
                            .border(1.dp, TechSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { showPhotoLightbox = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoResId != 0) {
                            val imageAlpha = if (record.photoDrawableName == "img_passport_kamal_hosen") 0.95f else 0.85f
                            Image(
                                painter = painterResource(id = photoResId),
                                contentDescription = "Scan Image of ${record.fullName}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = imageAlpha,
                                colorFilter = if (record.isFlagged) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
                            )
                        }

                        // Holographic Laser scanner line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(TextSecondary.copy(alpha = 0.5f))
                        )

                        if (!record.isFlagged) {
                            // Scanner Details Foreground overlay
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (record.photoDrawableName.isNotEmpty()) "${record.photoDrawableName.uppercase()}.JPG" else "SCAN_IMG_SECURE.JPG",
                                        color = TextPrimary.copy(alpha = 0.9f),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ENCRYPTED DIGITIZED PORTRAIT",
                                        color = TextSecondary.copy(alpha = 0.8f),
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .rotate(-15f)
                                        .border(2.dp, Color(0xFFF44336), RoundedCornerShape(8.dp))
                                        .background(Color(0xDD2C0B0B))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            tint = Color(0xFFF44336),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BLOCKED",
                                            color = Color(0xFFF44336),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "পাসপোর্ট ব্লক করা হয়েছে",
                                        color = Color(0xFFF44336),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Default
                                    )
                                }
                            }
                        }

                        // Encrypted pill tag
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .background(TextSecondary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ENCRYPTED",
                                color = Color(0xFF381E72),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Bento Grid Row 5: Machine Readable Zone (MRZ) (Span 2 / Full Width)
                BentoCard(
                    label = "MACHINE READABLE ZONE / ZONE OPTRONIQUE",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (mline1, mline2) = if (record.passportNumber.uppercase().trim() == "A07176865") {
                        Pair("P<BGDHOSEN<<MD<KAMAL<<<<<<<<<<<<<<<<<<<<<<<<<", "A071768650BGD8302102M33041233932850200000266")
                    } else {
                        val mtype = if (record.photoDrawableName.contains("doe")) "P<USADOE<<JOHNATHAN<<<<<<<<<<<<<<<<<<<<<<<<<" else "P<GBRSMITH<<JANE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
                        val pNum = record.passportNumber.padEnd(9, '<')
                        val nCode = record.nationality.take(3).uppercase()
                        Pair(mtype.take(44), "${pNum}${nCode}920924M3501017<<<<<<<<<<<<<<<42".take(44))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = mline1,
                            color = TextSecondary.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mline2.take(44),
                            color = TextSecondary.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions Panel: To Block, Unblock, or Delete
            Card(
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, TechSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete Button
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed),
                        border = BorderStroke(1.dp, WarningRed.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("De-register", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Toggle Flag Button (Block / Unblock)
                    if (record.isFlagged) {
                        Button(
                            onClick = { onToggleFlag(false, "") },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unblock Passport", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { showBlockDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Flag & Block", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet or Custom Dialog for blocking reason
    if (showBlockDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showBlockDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                border = BorderStroke(1.dp, WarningRed),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FLAG CREDENTIAL",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Are you sure you want to flag and block Passport #${record.passportNumber}? Enter the reason below:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = flagReasonText,
                        onValueChange = { flagReasonText = it },
                        placeholder = { Text("e.g. Reported stolen, Expired Visa, Security Threat", color = TextSecondary) },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = WarningRed,
                            unfocusedBorderColor = TechBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showBlockDialog = false },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                onToggleFlag(true, flagReasonText.ifEmpty { "Administrative suspension" })
                                showBlockDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirm Block")
                        }
                    }
                }
            }
        }
    }

    if (showPhotoLightbox && photoResId != 0) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showPhotoLightbox = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F).copy(alpha = 0.95f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${record.fullName} - ${record.passportNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showPhotoLightbox = false },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Lightbox",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = photoResId),
                            contentDescription = "Expanded Scan",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit,
                            colorFilter = if (record.isFlagged) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
                        )
                        if (record.isFlagged) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .rotate(-15f)
                                        .border(3.dp, Color(0xFFF44336), RoundedCornerShape(8.dp))
                                        .background(Color(0xDD2C0B0B))
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            tint = Color(0xFFF44336),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "BLOCKED",
                                            color = Color(0xFFF44336),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 2.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "পাসপোর্ট ব্লক করা হয়েছে",
                                        color = Color(0xFFF44336),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Default
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (record.isFlagged) "SYSTEM SECURITY BLOCK ACTIVE" else "SECURE PHOTO IDENTITY VERIFICATION PASSED",
                        color = if (record.isFlagged) WarningRed else TechSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun PassportField(
    label: String,
    value: String,
    highlight: Boolean = false,
    accentColor: Color? = null
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        Text(
            text = value,
            color = accentColor ?: if (highlight) TechSecondary else TextPrimary,
            fontSize = if (highlight) 16.sp else 13.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (highlight) FontFamily.Monospace else FontFamily.SansSerif,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RegisterPassportPanel(
    viewModel: PassportViewModel,
    focusManager: FocusManager
) {
    val context = LocalContext.current
    val regNumber by viewModel.regPassportNumber.collectAsState()
    val regName by viewModel.regFullName.collectAsState()
    val regGender by viewModel.regGender.collectAsState()
    val regDOB by viewModel.regDOB.collectAsState()
    val regNationality by viewModel.regNationality.collectAsState()
    val regIssueDate by viewModel.regIssueDate.collectAsState()
    val regExpiryDate by viewModel.regExpiryDate.collectAsState()
    val regAddress by viewModel.regAddress.collectAsState()
    val regPhoto by viewModel.regPhotoName.collectAsState()
    val regIsFlagged by viewModel.regIsFlagged.collectAsState()
    val regFlagReason by viewModel.regFlagReason.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "REGISTER NEW PASSPORT CREDENTIAL",
                color = TechSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Enter official biodata to seed the secure database. You can query this passport immediately after registration.",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Passport Number & Name
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { viewModel.regPassportNumber.value = it },
                    label = { Text("Passport Number") },
                    placeholder = { Text("e.g. DE4445556", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_passport_number_input")
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = regName,
                    onValueChange = { viewModel.regFullName.value = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("John Doe", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_name_input")
                )
            }
        }

        // Nationality & DOB
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = regNationality,
                    onValueChange = { viewModel.regNationality.value = it },
                    label = { Text("Nationality") },
                    placeholder = { Text("e.g. Germany", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = regDOB,
                    onValueChange = { viewModel.regDOB.value = it },
                    label = { Text("Date of Birth") },
                    placeholder = { Text("YYYY-MM-DD", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dates Issue & Expiry
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = regIssueDate,
                    onValueChange = { viewModel.regIssueDate.value = it },
                    label = { Text("Date of Issue") },
                    placeholder = { Text("YYYY-MM-DD", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = regExpiryDate,
                    onValueChange = { viewModel.regExpiryDate.value = it },
                    label = { Text("Date of Expiry") },
                    placeholder = { Text("YYYY-MM-DD", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechSecondary,
                        unfocusedBorderColor = TechBorder,
                        focusedLabelColor = TechSecondary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Gender selection
        item {
            Column {
                Text("Gender", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = regGender == "M",
                        onClick = { viewModel.regGender.value = "M" },
                        colors = RadioButtonDefaults.colors(selectedColor = TechSecondary, unselectedColor = TechBorder)
                    )
                    Text("Male", color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(20.dp))
                    RadioButton(
                        selected = regGender == "F",
                        onClick = { viewModel.regGender.value = "F" },
                        colors = RadioButtonDefaults.colors(selectedColor = TechSecondary, unselectedColor = TechBorder)
                    )
                    Text("Female", color = TextPrimary, fontSize = 14.sp)
                }
            }
        }

        // Address Details
        item {
            OutlinedTextField(
                value = regAddress,
                onValueChange = { viewModel.regAddress.value = it },
                label = { Text("Residential Address") },
                placeholder = { Text("Enter full physical residential address", color = TextSecondary) },
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = TechSecondary,
                    unfocusedBorderColor = TechBorder,
                    focusedLabelColor = TechSecondary,
                    unfocusedLabelColor = TextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_address_input")
            )
        }

        // Avatar photo selector
        item {
            Column {
                Text("Assign Passport Photo Profile", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val presets = listOf(
                        "img_passport_john_doe" to "Preset Male 1",
                        "img_passport_jane_smith" to "Preset Female",
                        "img_passport_marcus_kane" to "Preset Male 2"
                    )

                    presets.forEach { (name, label) ->
                        val isSelected = regPhoto == name
                        val resId = getResIdByName(context, name)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    2.dp,
                                    if (isSelected) TechSecondary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (isSelected) TechSecondary.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.regPhotoName.value = name }
                                .padding(6.dp)
                        ) {
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = label,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                color = if (isSelected) TechSecondary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Block / Stolen Alert Flag switch
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechSurfaceVariant, RoundedCornerShape(12.dp))
                    .background(TechSurfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Flag & Block Instantly",
                            color = if (regIsFlagged) WarningRed else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Apply administrative block and lock this passport in lookups.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = regIsFlagged,
                        onCheckedChange = { viewModel.regIsFlagged.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WarningRed,
                            checkedTrackColor = WarningRed.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = TechSurfaceVariant
                        )
                    )
                }

                AnimatedVisibility(
                    visible = regIsFlagged,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        OutlinedTextField(
                            value = regFlagReason,
                            onValueChange = { viewModel.regFlagReason.value = it },
                            placeholder = { Text("Reason (e.g. Reported stolen, fraudulent visa application)", color = TextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = WarningRed,
                                unfocusedBorderColor = TechBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Register Button
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.registerPassport()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_button")
            ) {
                Icon(imageVector = Icons.Default.AddCard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("REGISTER OFFICIAL DOCUMENT", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// Helper to look up generated JPEG/PNG resource identifiers dynamically
fun getResIdByName(context: android.content.Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}
