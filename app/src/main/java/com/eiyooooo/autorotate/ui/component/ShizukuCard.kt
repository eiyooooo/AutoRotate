package com.eiyooooo.autorotate.ui.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eiyooooo.autorotate.R
import com.eiyooooo.autorotate.data.ShizukuStatus
import com.eiyooooo.autorotate.viewmodel.AutoRotateViewModel
import timber.log.Timber

@Composable
fun ShizukuCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation,
    showSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: AutoRotateViewModel = viewModel()
    val shizukuStatus by viewModel.shizukuStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkShizukuPermission()
    }

    when (shizukuStatus) {
        ShizukuStatus.HAVE_PERMISSION -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_connected),
                showDetail = false,
                detail = "",
                onClick = null,
                modifier = modifier,
                elevation = elevation
            )
        }

        ShizukuStatus.NO_PERMISSION -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_authorization_instruction),
                onClick = {
                    viewModel.requestShizukuPermission()
                },
                modifier = modifier,
                elevation = elevation
            )
        }

        ShizukuStatus.VERSION_NOT_SUPPORT -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_need_update),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            addCategory(Intent.CATEGORY_BROWSABLE)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = "https://shizuku.rikka.app/zh-hans/guide/setup/".toUri()
                        }
                        context.startActivity(intent)
                    } catch (t: Throwable) {
                        Timber.e(t, "Open Shizuku instruction failed")
                        showSnackbar(context.getString(R.string.no_browser))
                    }
                },
                modifier = modifier,
                elevation = elevation
            )
        }

        ShizukuStatus.SHIZUKU_NOT_RUNNING -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_setup_instruction),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            addCategory(Intent.CATEGORY_BROWSABLE)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = "https://shizuku.rikka.app/zh-hans/guide/setup/".toUri()
                        }
                        context.startActivity(intent)
                    } catch (t: Throwable) {
                        Timber.e(t, "Open Shizuku instruction failed")
                        showSnackbar(context.getString(R.string.no_browser))
                    }
                },
                modifier = modifier,
                elevation = elevation
            )
        }
    }
}

@Composable
private fun ShizukuContentCard(
    icon: Int,
    title: String,
    showDetail: Boolean,
    detail: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    elevation: CardElevation
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showDetail) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
