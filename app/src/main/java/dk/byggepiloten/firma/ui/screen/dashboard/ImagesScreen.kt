// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/ImagesScreen.kt
// FULD VERSION – sp import tilføjet (fontSize = 20.sp)
// + Alt andet uændret

package dk.byggepiloten.firma.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.dashboard.TaskDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
                    if (request.images.isNotEmpty()) {
                        item {
                            Text(
                                "Generelle billeder",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Spacer(Modifier.height(16.dp))
                            ImageGallery(images = request.images, coroutineScope = coroutineScope)
                        }
                    }

                    items(request.labeledPhotos.entries.toList()) { entry ->
                        val (label, urls) = entry
                        if (urls.isNotEmpty()) {
                            Text(
                                label,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Spacer(Modifier.height(16.dp))
                            ImageGallery(images = urls, coroutineScope = coroutineScope)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGallery(images: List<String>, coroutineScope: kotlinx.coroutines.CoroutineScope) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = "Billede ${page + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gridItems(images.withIndex().toList()) { (index, url) ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}