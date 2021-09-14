package com.pspdfkit.composepdf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pspdfkit.composepdf.ui.PdfList
import com.pspdfkit.composepdf.ui.theme.ComposePdfTheme
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi

@ExperimentalPSPDFKitApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposePdfTheme {
                Surface(color = MaterialTheme.colors.background) {

                    val state by viewModel.state.collectAsState(State())
                    PdfList(state, viewModel::loadPdfs, viewModel::openDocument)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.state.value.selectedDocumentUri != null) {
            viewModel.closeDocument()
        } else {
            super.onBackPressed()
        }
    }
}