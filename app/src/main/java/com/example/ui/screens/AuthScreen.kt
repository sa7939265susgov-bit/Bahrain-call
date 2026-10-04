package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.viewmodel.BahrainMeetViewModel

@Composable
fun AuthScreen(
    viewModel: BahrainMeetViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val regPhone by viewModel.registrationPhone.collectAsState()
    val regName by viewModel.registrationName.collectAsState()
    val isOtpSent by viewModel.isOtpSent.collectAsState()
    val sentCode by viewModel.sentOtpCode.collectAsState()
    val enteredOtp by viewModel.enteredOtp.collectAsState()
    val otpMsg by viewModel.otpMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_bahrain_meet_1784995162162),
                contentDescription = "Bahrain Meet Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = BahrainRed,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bahrain Meet",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Sign up with your Bahrain Phone Number",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Phone Number Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Enter your Bahrain mobile number to receive your SMS verification code.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Name Input
                OutlinedTextField(
                    value = regName,
                    onValueChange = { viewModel.setRegistrationDetails(regPhone, it) },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g. Ali Al-Bahraini") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Input with +973 prefix badge
                OutlinedTextField(
                    value = regPhone,
                    onValueChange = { viewModel.setRegistrationDetails(it, regName) },
                    label = { Text("Bahrain Phone Number") },
                    placeholder = { Text("33678882 or 3xxxxxxx") },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                        ) {
                            Text("🇧🇭 +973", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_phone_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.sendRegistrationSms() },
                    enabled = regPhone.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_sms_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Sms, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send SMS Verification Code", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // SMS Notification Box Simulation
                AnimatedVisibility(visible = isOtpSent) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = CallAcceptGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = null, tint = CallAcceptGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("SMS Received from Bahrain Meet:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CallAcceptGreen)
                                    Text("Your verification code is [$sentCode]", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = CallAcceptGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = { viewModel.updateEnteredOtp(it) },
                            label = { Text("Enter 6-Digit SMS Code") },
                            placeholder = { Text(sentCode) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_otp_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (viewModel.verifySmsCodeAndLogin()) {
                                    onAuthSuccess()
                                }
                            },
                            enabled = enteredOtp.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_otp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Verify SMS Code & Enter Bahrain Meet", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                if (otpMsg != null && !isOtpSent) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = otpMsg!!, color = BahrainRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
