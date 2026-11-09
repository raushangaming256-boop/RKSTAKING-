package com.example.ui.components.backdrop

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.KhataViewModel

/**
 * Backdrop Catalog Showcase Dialog
 * Demonstrates all authentic Backdrop Catalog components:
 * - LiquidButton (Primary, Secondary, Denge Green, Lenge Red)
 * - LiquidToggle (Hardware 90Hz, 240Hz Touch, Mass 4x Physics, Glass Blur)
 * - LiquidSlider (Specular rim intensity, haptic sensitivity)
 * - LiquidBottomTabs (iOS Frosted Glass Dock preview)
 */
@Composable
fun BackdropCatalogDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val display90HzActive by viewModel.display90HzActive.collectAsStateWithLifecycle()
    val touch240HzActive by viewModel.touch240HzActive.collectAsStateWithLifecycle()
    val mass4xActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val isGlassActive by viewModel.isGlassActive.collectAsStateWithLifecycle()

    var sliderBlurIntensity by remember { mutableFloatStateOf(0.75f) }
    var sliderHapticVolume by remember { mutableFloatStateOf(0.50f) }
    var demoSelectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isDark) 0.40f else 0.85f),
                            Color.White.copy(alpha = if (isDark) 0.08f else 0.20f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            color = if (isDark) Color(0xF217212B) else Color(0xF7FFFFFF),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF007AFF).copy(alpha = 0.16f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Backdrop Catalog 🔮",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Liquid Glass Interactive Component Suite",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Section 1: Liquid Buttons
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0x33FFFFFF) else Color(0x66F2F2F7)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "1. Liquid Buttons (LiquidButton)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007AFF)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Primary iOS Action Button
                            LiquidButton(
                                onClick = {
                                    Toast.makeText(context, "✨ Primary Liquid Button Clicked!", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = true,
                                accentColor = Color(0xFF007AFF),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Text("Primary iOS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Secondary Frosted Glass Button
                            LiquidButton(
                                onClick = {
                                    Toast.makeText(context, "🫧 Frosted Glass Clicked!", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Frosted Glass", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Denge Green Liquid Button
                            LiquidButton(
                                onClick = {
                                    Toast.makeText(context, "🟢 Denge Liquid Button", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = true,
                                accentColor = Color(0xFF34C759),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("देंगे (Denge)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Lenge Red Liquid Button
                            LiquidButton(
                                onClick = {
                                    Toast.makeText(context, "🔴 Lenge Liquid Button", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = true,
                                accentColor = Color(0xFFFF3B30),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("लेंगे (Lenge)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Section 2: Liquid Toggles
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0x33FFFFFF) else Color(0x66F2F2F7)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "2. Liquid Toggles (LiquidToggle)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34C759)
                        )

                        // 90Hz Engine Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("⚡ 90Hz / 120Hz Ultra Smooth", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Locks hardware display mode for buttery UI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            LiquidToggle(
                                checked = display90HzActive,
                                onCheckedChange = { viewModel.setDisplay90HzActive(it) },
                                activeColor = Color(0xFF007AFF)
                            )
                        }

                        // 240Hz Touch Sampling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("👆 240Hz Touch Sampling Rate", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Ultra-low latency touch event pipeline", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            LiquidToggle(
                                checked = touch240HzActive,
                                onCheckedChange = { viewModel.setTouch240HzActive(it) },
                                activeColor = Color(0xFF34C759)
                            )
                        }

                        // Mass 4x Physics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🚀 Mass 4x Spring Physics", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Inertial fling & tactile spring damping", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            LiquidToggle(
                                checked = mass4xActive,
                                onCheckedChange = { viewModel.setMass4xEngineActive(it) },
                                activeColor = Color(0xFFFF9500)
                            )
                        }
                    }
                }

                // Section 3: Liquid Sliders
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0x33FFFFFF) else Color(0x66F2F2F7)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "3. Liquid Sliders (LiquidSlider)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9500)
                        )

                        Text(
                            text = "Specular Glass Blur: ${(sliderBlurIntensity * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        LiquidSlider(
                            value = sliderBlurIntensity,
                            onValueChange = { sliderBlurIntensity = it },
                            accentColor = Color(0xFF007AFF)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Haptic Tactile Tick Steps: ${(sliderHapticVolume * 10).toInt()} / 10",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        LiquidSlider(
                            value = sliderHapticVolume,
                            onValueChange = { sliderHapticVolume = it },
                            steps = 9,
                            accentColor = Color(0xFF34C759)
                        )
                    }
                }

                // Close Button
                LiquidButton(
                    onClick = onDismiss,
                    isPrimary = true,
                    accentColor = Color(0xFF007AFF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK, Got It!", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
