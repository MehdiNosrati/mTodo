package io.mns.base.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.DoneItem
import io.mns.base.app.ui.viewmodels.DoneViewModel
import org.koin.androidx.compose.koinViewModel

private val DoneGreen = Color(0xFF10B981)
private val DoneTeal = Color(0xFF2DD4BF)
private val DoneBrush = Brush.linearGradient(
    colors = listOf(DoneGreen, DoneTeal),
    start = Offset.Zero,
    end = Offset.Infinite
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DoneScreen(
    onSettingsClick: () -> Unit,
    viewModel: DoneViewModel = koinViewModel()
) {
    val items by viewModel.loadItems().observeAsState(initial = emptyList())
    DoneScreenContent(
        items = items,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DoneScreenContent(
    items: List<DoneItem>,
    onSettingsClick: () -> Unit = {},
    animate: Boolean = true
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Done",
                        style = TextStyle(
                            brush = DoneBrush,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background orb
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .offset(x = 60.dp, y = (-50).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(colors = listOf(DoneGreen.copy(alpha = 0.08f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-50).dp, y = 20.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.radialGradient(colors = listOf(DoneTeal.copy(alpha = 0.06f), Color.Transparent)),
                        CircleShape
                    )
            )

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    DoneEmptyState(animate = animate)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                        DoneItemRow(item = item, modifier = Modifier.animateItem())
                    }
                }
            }
        }
    }
}

@Composable
fun DoneEmptyState(
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    var visible by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) {
        if (animate) visible = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible || !animate,
        enter = fadeIn(tween(400)) + scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape, spotColor = DoneGreen.copy(alpha = 0.3f))
                    .background(DoneBrush, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Nothing finished yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Complete a task to see it here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DoneItemRow(item: DoneItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = DoneGreen.copy(alpha = 0.10f),
                ambientColor = DoneGreen.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(62.dp)
                    .background(
                        DoneBrush,
                        RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(DoneBrush, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DoneGreen.copy(alpha = 0.10f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = DoneGreen
                    )
                }
            }
        }
    }
}
