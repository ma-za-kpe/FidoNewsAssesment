package com.fido.newappassesment.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fido.newappassesment.ui.navigation.MainNavGraph
import com.fido.newappassesment.ui.theme.NewAppAssesmentTheme

@Composable
fun NewsApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        MainNavGraph()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NewAppAssesmentTheme {
        NewsApp()
    }
}