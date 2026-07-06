package com.freetune.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freetune.app.ui.screens.common.TrackRow

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsState()
    val downloadedIds by viewModel.downloadRepository.downloadedTrackIds.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("FreeTune") })

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Couldn't load your feed: ${state.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (state.forYou.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("For You", style = MaterialTheme.typography.titleLarge)
                                IconButton(onClick = { viewModel.shufflePlay(state.forYou) }) {
                                    Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle play")
                                }
                            }
                        }
                        items(state.forYou, key = { "foryou-${it.id}" }) { track ->
                            TrackRow(
                                track = track,
                                isDownloaded = track.id in downloadedIds,
                                onClick = { viewModel.playTrack(state.forYou, state.forYou.indexOf(track)) },
                                onToggleDownload = { viewModel.toggleDownload(track) },
                            )
                        }
                    }

                    item {
                        Text(
                            "Discover",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    items(state.discover, key = { "discover-${it.id}" }) { track ->
                        TrackRow(
                            track = track,
                            isDownloaded = track.id in downloadedIds,
                            onClick = { viewModel.playTrack(state.discover, state.discover.indexOf(track)) },
                            onToggleDownload = { viewModel.toggleDownload(track) },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
