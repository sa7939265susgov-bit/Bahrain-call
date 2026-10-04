package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BatelcoColor
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.StcColor
import com.example.ui.theme.ZainColor

data class SimCarrierOption(
    val id: String,
    val name: String,
    val prefix: String,
    val color: Color,
    val networkType: String = "5G VoLTE"
)

@Composable
fun SimCardSelector(
    selectedSim: String,
    onSimSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sims = listOf(
        SimCarrierOption("BATELCO", "Batelco", "+973 39xx", BatelcoColor),
        SimCarrierOption("STC", "stc", "+973 33xx", StcColor),
        SimCarrierOption("ZAIN", "Zain", "+973 36xx", ZainColor)
    )

    val activeCarrier = sims.find { it.id.equals(selectedSim, ignoreCase = true) } ?: sims.first()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sim_card_selector")
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        // Header Row with Title and 5G VoLTE Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SimCard,
                    contentDescription = "SIM Card Carrier",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Outgoing Call SIM Card",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = activeCarrier.color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${activeCarrier.name} • 5G VoLTE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeCarrier.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Carrier Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sims.forEach { option ->
                val isSelected = selectedSim.equals(option.id, ignoreCase = true)
                val borderBg by animateColorAsState(
                    if (isSelected) option.color else Color.Transparent,
                    label = "border"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sim_select_${option.id}")
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSimSelected(option.id) },
                    color = if (isSelected) option.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(option.color)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = option.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = option.color,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = option.prefix,
                            fontSize = 10.sp,
                            color = if (isSelected) option.color.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Carrier Status Footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CallAcceptGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Outgoing calls route via ${activeCarrier.name} network (${activeCarrier.prefix})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

