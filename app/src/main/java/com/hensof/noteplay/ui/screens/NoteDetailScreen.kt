package com.hensof.noteplay.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.ui.components.TemplateCard
import com.hensof.noteplay.ui.viewmodel.NoteDetailViewModel
import com.hensof.noteplay.ui.viewmodel.TemplatesViewModel
import com.hensof.noteplay.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    templatesViewModel: TemplatesViewModel,
    noteId: Long,
    onNavigateBack: () -> Unit,
    onFavoriteToggle: (Note) -> Unit = {}
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val category by viewModel.category.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val photoUris by viewModel.photoUris.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val templatesUiState by templatesViewModel.uiState.collectAsState()
    val templates = templatesUiState.templates
    
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var newTag by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addPhotoUri(context, it.toString()) }
    }
    
    // Note loading is now handled in NavGraph
    // Migrate photo URIs when screen is composed
    LaunchedEffect(Unit) {
        viewModel.migratePhotoUris(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId > 0) "Edit Note" else "New Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.toggleFavorite()
                        // Создаем объект Note для передачи в onFavoriteToggle
                        val currentNote = Note(
                            id = noteId,
                            title = title,
                            content = content,
                            category = category,
                            tags = tags,
                            photoUris = photoUris,
                            isFavorite = !isFavorite,
                            createdAt = viewModel.uiState.value.note?.createdAt ?: java.util.Date(),
                            updatedAt = java.util.Date()
                        )
                        onFavoriteToggle(currentNote)
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { 
                        viewModel.saveNote {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Template Button
            if (noteId == 0L) {
                item {
                    OutlinedButton(
                        onClick = { showTemplateDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Template")
                    }
                }
            }
            
            // Category Selector
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = category.color.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.emoji,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
            
            // Title
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.setTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            // Content
            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.setContent(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    label = { Text("Content") },
                    maxLines = 10,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            // Photos
            if (photoUris.isNotEmpty()) {
                item {
                    Text(
                        text = "Photos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(photoUris) { uri ->
                            Box {
                                val imageUri = if (uri.startsWith("/")) {
                                    ImageUtils.getImageUri(context, uri)
                                } else {
                                    Uri.parse(uri)
                                }
                                
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.removePhotoUri(context, uri) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(24.dp)
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Add Photo Button
            item {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photo")
                }
            }
            
            // Tags
            item {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tags) { tag ->
                            InputChip(
                                selected = false,
                                onClick = { viewModel.removeTag(tag) },
                                label = { Text("#$tag") },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                OutlinedButton(
                    onClick = { showTagDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Tag")
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
    
    // Category Dialog
    if (showCategoryDialog) {
        Dialog(onDismissRequest = { showCategoryDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Category",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    NoteCategory.values().forEach { cat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setCategory(cat)
                                    showCategoryDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (cat == category) 
                                    cat.color.copy(alpha = 0.5f)
                                else 
                                    cat.color.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = cat.emoji, style = MaterialTheme.typography.headlineMedium)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = cat.displayName, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    
    // Template Dialog
    if (showTemplateDialog) {
        Dialog(onDismissRequest = { showTemplateDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose Template",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (templatesUiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(templates) { template ->
                                TemplateCard(
                                    template = template,
                                    onClick = {
                                        viewModel.loadTemplate(
                                            template.title,
                                            template.content,
                                            template.category
                                        )
                                        templatesViewModel.incrementTemplateUsage(template.id)
                                        showTemplateDialog = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Tag Dialog
    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Add Tag") },
            text = {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text("Tag name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addTag(newTag)
                        newTag = ""
                        showTagDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

