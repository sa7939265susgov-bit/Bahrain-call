package com.example

import com.example.viewmodel.ActiveCall
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun outgoingCall_initializesWithRingingAndTwoRepetitions() {
    val call = ActiveCall(
      numberOrEmail = "+973 17000000",
      contactName = "Bahrain Telecom",
      isVideo = false,
      carrier = "Batelco",
      responseSpeech = "Hello! Connected.",
      isRinging = true,
      ringRepetition = 1,
      totalRings = 2,
      isConnected = false
    )

    assertTrue(call.isRinging)
    assertFalse(call.isConnected)
    assertEquals(1, call.ringRepetition)
    assertEquals(2, call.totalRings)

    val connectedCall = call.copy(
      isRinging = false,
      isConnected = true
    )

    assertFalse(connectedCall.isRinging)
    assertTrue(connectedCall.isConnected)
  }
}
