package com.pspdfkit.composepdf.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pspdfkit.composepdf.State
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.activity.UserInterfaceViewMode
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.jetpack.compose.DocumentView
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi
import com.pspdfkit.jetpack.compose.rememberDocumentState

private const val thumbnailPageIndex = 0

@Composable
@ExperimentalPSPDFKitApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
fun PdfList(
    state: State,
    loadPdfs: () -> Unit,
    openDocument: (Uri) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar {

                val openDocumentTitle = state.selectedDocumentUri
                    ?.let { state.documents[it]?.title }

                Text(text = openDocumentTitle ?: "Jetpack Compose PDF Viewer")
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            AnimatedVisibility(
                state.loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                )  {
                    Text(
                        text = "Please wait while your documents are being loaded",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator()
                }
            }

            AnimatedVisibility(
                visible = !state.loading && state.documents.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "It looks there are no documents yet.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                    Button(onClick = { loadPdfs() }) {
                        Text(
                            text = "Load Documents",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !state.loading && state.documents.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                LazyVerticalGrid(
                    cells = GridCells.Adaptive(100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items (state.documents.toList()) { (uri, document) ->

                        PdfThumbnail(
                            document = document,
                            onClick = { openDocument(uri) }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.selectedDocumentUri != null,
                enter = slideInHorizontally({ it }),
                exit = slideOutHorizontally({ it }),
            ) {

                if (state.selectedDocumentUri == null) {
                    Box(Modifier.fillMaxSize())
                } else {
                    val context = LocalContext.current
                    val pdfActivityConfiguration = remember {
                        PdfActivityConfiguration
                            .Builder(context)
                            .setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_HIDDEN)
                            .build()
                    }

                    val documentState = rememberDocumentState(
                        state.selectedDocumentUri,
                        pdfActivityConfiguration
                    )

                    DocumentView(
                        documentState = documentState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PdfThumbnail(
    document: PdfDocument,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    // Since this can be a costly operation, we wanna memoize the
    // bitmap to prevent recalculating it every time we recompose.
    val thumbnailImage = remember(document) {
        val pageImageSize = document.getPageSize(thumbnailPageIndex).toRect()

        document.renderPageToBitmap(
            context,
            thumbnailPageIndex,
            pageImageSize.width().toInt(),
            pageImageSize.height().toInt()
        ).asImageBitmap()
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
            ) { onClick() }
    ) {
        Column {
            Image(
                bitmap = thumbnailImage,
                contentScale = ContentScale.Crop,
                contentDescription = "Preview for the document ${document.title}",
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = document.title ?: "Untitled Document",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}