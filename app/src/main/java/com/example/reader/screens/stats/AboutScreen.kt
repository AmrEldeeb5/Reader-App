package com.example.reader.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reader.R
import com.example.reader.ui.theme.ReaderTheme
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Us") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.solar__alt_arrow_left_line_duotone),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Reader",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Our mission is to make reading delightful and accessible for everyone. With Reader, you can discover, save, and enjoy books with a clean, modern interface.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Features
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("What you can do", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val items = listOf(
                    "Explore new books tailored to you",
                    "Save favorites to read later",
                    "Track your reading stats"
                )
                items.forEach { line ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.solar__check_circle_bold),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(line, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Contact",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "support@reader.app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(name = "About - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAboutLight() {
    ReaderTheme(darkTheme = false) {
        AboutScreen(rememberNavController())
    }
}

@Preview(name = "About - Dark", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewAboutDark() {
    ReaderTheme(darkTheme = true) {
        AboutScreen(rememberNavController())
    }
}