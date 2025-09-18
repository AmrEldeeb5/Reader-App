package com.example.reader.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    HomeTopBar()
                }
            )
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        // Screen content goes here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("content here")
        }
    }
}


@Composable
fun HomeTopBar(
    userName: String = "Andy",
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left side: Avatar + Greeting ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar image
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 12.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            GreetingSection(
                userName = userName
            )
        }

        // --- Right side: Action icons with badge ---
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BadgedIcon(
                icon = Icons.Default.Notifications, // bell icon
                contentDescription = "Notifications",
                onClick = onNotificationsClick
            )
            BadgedIcon(
                icon = Icons.Default.ChatBubble, // chat icon
                contentDescription = "Messages",
                onClick = onMessagesClick
            )
        }
    }
}

