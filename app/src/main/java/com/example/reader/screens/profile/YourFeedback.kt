package com.example.reader.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reader.R
import com.example.reader.ui.theme.ReaderTheme
import com.example.reader.ui.theme.animatedScaffoldContainerColor
import com.example.reader.ui.theme.animatedTopBarContainerColor
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.profile.FeedbackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourFeedbackScreen(
    navController: NavController,
    feedbackViewModel: FeedbackViewModel = hiltViewModel()
) {
    var feedback by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(3) } // default to very satisfied
    // Show a short-lived thank you popup before navigating back
    var showThanks by remember { mutableStateOf(false) }

    val sentiments = listOf(
        Icons.Filled.SentimentVeryDissatisfied to "Very bad",
        Icons.Filled.SentimentDissatisfied to "Bad",
        Icons.Filled.SentimentSatisfied to "Good",
        Icons.Filled.SentimentVerySatisfied to "Great"
    )

    // When the thank-you popup is shown, wait briefly then navigate back
    LaunchedEffect(showThanks) {
        if (showThanks) {
            delay(1500)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Feedback") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.solar__alt_arrow_left_line_duotone),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedTopBarContainerColor()
                )
            )
        },
        containerColor = animatedScaffoldContainerColor()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Text field row with icon box same height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = sentiments[selectedIndex].first,
                        contentDescription = "Selected sentiment: ${sentiments[selectedIndex].second}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                TextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    placeholder = { Text("Write your feedback…") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    minLines = 5,
                    maxLines = 8
                )
            }

            // Sentiment selector
            Text(
                text = "How was your experience?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sentiments.forEachIndexed { index, (icon, label) ->
                    val selected = index == selectedIndex
                    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(bg, RoundedCornerShape(12.dp))
                            .border(
                                width = if (selected) 1.dp else 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    // Save feedback to Realm DB
                    feedbackViewModel.saveFeedback(feedback, selectedIndex)
                    // Show a quick thank-you popup, then navigate back
                    showThanks = true
                },
                enabled = feedback.isNotBlank() && !showThanks,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Send Feedback")
            }

            Spacer(Modifier.height(16.dp))
        }

        // Simple transient thank-you dialog
        if (showThanks) {
            AlertDialog(
                onDismissRequest = { /* Block manual dismiss to ensure brief display */ },
                confirmButton = {},
                title = { Text("Thank you!") },
                text = { Text("Thanks for your feedback. It has been saved!") }
            )
        }
    }
}

@Preview(name = "YourFeedback - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewYourFeedbackLight() {
    ReaderTheme(darkTheme = false) {
        YourFeedbackScreen(rememberNavController())
    }
}

@Preview(name = "YourFeedback - Dark", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewYourFeedbackDark() {
    ReaderTheme(darkTheme = true) {
        YourFeedbackScreen(rememberNavController())
    }
}
