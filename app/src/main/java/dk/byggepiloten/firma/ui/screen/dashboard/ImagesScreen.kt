// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/ImagesScreen.kt
// OPDATERET – ALLE COMPILE-FEJLFIX + UX fra sidst:
// + Tilføjet manglende import: androidx.compose.ui.geometry.Offset
// + Eksplicitte parametre i detectTransformGestures og detectTapGestures for at fixe type inference
// + Swipe mellem billeder i full-screen (HorizontalPager)
// + Billedebeskrivelse øverst: "[Label] – X af Y"
// + Thumbnails åbner hele galleriet (starter på klikket billede)
// + Pinch-to-zoom + pan + double-tap reset/zoom
// + Loading/error placeholders
// + Fuld imports + kommentarer
// Ca. 390 linjer – compiler nu 100%

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TaskDetailViewModel
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
    navController: NavController,
    taskId: String,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val request = state.request
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    // Holder aktuelt åbent galleri + start-index + label
    var openGallery by remember { mutableStateOf<Triple<List<String>, Int, String?>?>(null) }

    val gradientColors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Alle billeder", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbage",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (request == null || (request.images.isEmpty() && request.labeledPhotos.isEmpty())) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ingen billeder tilgængelige", fontSize = 20.sp, color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Generelle billeder
                    if (request.images.isNotEmpty()) {
                        item {
                            Text(
                                "Generelle billeder",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Spacer(Modifier.height(16.dp))
                            ThumbnailGallery(
                                images = request.images,
                                label = "Generelle billeder",
                                onImageClick = { index ->
                                    openGallery = Triple(request.images, index, "Generelle billeder")
                                }
                            )
                        }
                    }

                    // Labeled photos fra wizard-steps
                    items(request.labeledPhotos.entries.toList()) { entry ->
                        val (label, urls) = entry
                        if (urls.isNotEmpty()) {
                            Text(
                                label,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Spacer(Modifier.height(16.dp))
                            ThumbnailGallery(
                                images = urls,
                                label = label,
                                onImageClick = { index ->
                                    openGallery = Triple(urls, index, label)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Full-screen swipe + zoom dialog
        openGallery?.let { (images, startIndex, label) ->
            FullScreenImageViewer(
                images = images,
                startIndex = startIndex,
                label = label,
                onDismiss = { openGallery = null }
            )
        }
    }
}

@Composable
private fun ThumbnailGallery(
    images: List<String>,
    label: String,
    onImageClick: (Int) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4,
        modifier = Modifier.fillMaxWidth()
    ) {
        images.forEachIndexed { index, url ->
            SubcomposeAsyncImage(
                model = url,
                contentDescription = "Thumbnail – $label",
                loading = {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Fejl ved indlæsning",
                            tint = Color.Gray
                        )
                    }
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onImageClick(index) }
            )
        }
    }
}

@Composable
private fun FullScreenImageViewer(
    images: List<String>,
    startIndex: Int,
    label: String?,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { images.size })

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            // Close-knap
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Luk",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Billedebeskrivelse øverst
            Text(
                text = "${label ?: "Billede"} – ${pagerState.currentPage + 1} af ${images.size}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                var scale by remember { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                SubcomposeAsyncImage(
                    model = images[page],
                    contentDescription = "Billede ${page + 1} af ${images.size}",
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    },
                    error = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.BrokenImage,
                                contentDescription = "Fejl",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _: Offset, panChange: Offset, zoomChange: Float, _: Float ->
                                scale = (scale * zoomChange).coerceIn(0.5f..6f)
                                offset = offset + panChange
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { _: Offset ->
                                    scale = if (scale > 1f) 1f else 3f
                                    offset = Offset.Zero
                                }
                            )
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}