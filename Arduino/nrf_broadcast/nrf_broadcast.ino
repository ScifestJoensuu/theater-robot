#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

#define channel        0x4c                  // nrf24 communication channel
#define writingPipe    0xF0F0F0F0E1LL        // nrf24 communication address
#define dataRate       RF24_250KBPS          // nrf24 data rate (lower == more distance)
#define paLevel        RF24_PA_HIGH          // nrf24 power level (black ebay models have some problems with PA_MAX)

RF24 radio(7, 8);

char receive_payload[33]; // +1 to allow room for a terminating NULL char

char id = '0';

int ledPin = 5;
boolean ledState = false;

void setup(void) {
  Serial.begin(57600);
  printf_begin();
  
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  
  radio.begin();
  radio.setPALevel(paLevel);
  radio.setChannel(channel);
  radio.openReadingPipe(0, writingPipe);
  radio.enableDynamicPayloads();
  radio.setDataRate(dataRate);
  radio.setAutoAck(false);
  radio.startListening();

  radio.printDetails();
}

void loop(void) {   
  printf(".");
  byte pipeNo;
  if (radio.available()) {
    printf(" data available\n");
    uint8_t len;
    while (radio.available()) {
      len = radio.getDynamicPayloadSize();
      radio.read(receive_payload, len);
      receive_payload[len] = 0;
      printf("Got payload size=%i value=%s\n\r", len, receive_payload);
    }
    if(checkId(receive_payload))
      toggleLed();
  }

  delay(100);
}

boolean checkId(char msg[]) {
  if(msg[0] == id || msg[0] == 'a') return true;
  return false;
}

void toggleLed() {
  digitalWrite(ledPin, ledState);
  ledState = !ledState;
}
