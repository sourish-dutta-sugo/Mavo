package com.mavo.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.R
import com.mavo.app.ui.animation.PremiumFadeSpec
import com.mavo.app.ui.animation.PremiumSpringSpec
import com.mavo.app.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = PremiumSpringSpec
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = PremiumFadeSpec
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(900)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(AppColors.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_transparent),
                    contentDescription = null,
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Mavo",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.textPrimary,
                letterSpacing = (-0.5).sp
            )
        }
    }
}
