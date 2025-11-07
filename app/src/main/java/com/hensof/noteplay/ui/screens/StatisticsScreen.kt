package com.hensof.noteplay.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hensof.noteplay.ui.viewmodel.StatisticsViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Overall Stats Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(
                            value = "${uiState.totalNotes}",
                            label = "Total Notes",
                            emoji = "📝"
                        )
                        StatItem(
                            value = "${uiState.categoryStats.size}",
                            label = "Categories",
                            emoji = "📁"
                        )
                        StatItem(
                            value = "${uiState.favoriteCount}",
                            label = "Favorites",
                            emoji = "⭐"
                        )
                    }
                }
            }

            // Pie Chart
            if (uiState.categoryStats.any { it.count > 0 }) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Notes by Category",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            PieChart(
                                data = uiState.categoryStats.filter { it.count > 0 },
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }
                }
            }

            // Category Breakdown
            item {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.categoryStats) { stat ->
                CategoryStatCard(
                    emoji = stat.category.emoji,
                    categoryName = stat.category.displayName,
                    count = stat.count,
                    color = stat.category.color,
                    percentage = if (uiState.totalNotes > 0)
                        (stat.count.toFloat() / uiState.totalNotes * 100).toInt()
                    else 0
                )
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryStatCard(
    emoji: String,
    categoryName: String,
    count: Int,
    color: Color,
    percentage: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "$count notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = color.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$percentage% of total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PieChart(
    data: List<com.hensof.noteplay.ui.viewmodel.CategoryStats>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.count }
    val angles = data.map { 360f * it.count / total }
    
    var startAngle by remember { mutableStateOf(-90f) }
    
    val animatedAngles = angles.map { angle ->
        animateFloatAsState(
            targetValue = angle,
            animationSpec = tween(durationMillis = 1000),
            label = "angle"
        ).value
    }
    
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2
        val strokeWidth = radius * 0.3f
        
        var currentAngle = -90f
        animatedAngles.forEachIndexed { index, angle ->
            drawArc(
                color = data[index].category.color,
                startAngle = currentAngle,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset(
                    (size.width - canvasSize) / 2,
                    (size.height - canvasSize) / 2
                ),
                size = Size(canvasSize, canvasSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            currentAngle += angle
        }
    }
}

