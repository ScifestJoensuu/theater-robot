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

byte counter = 1;

void setup(void) {
  Serial.begin(57600);
  printf_begin();

  radio.begin();
  radio.setPALevel(paLevel);
  radio.setChannel(channel);
  radio.openWritingPipe(writingPipe);
  //radio.openReadingPipe(0, writingPipe);
  radio.enableDynamicPayloads();
  radio.setDataRate(dataRate);
  radio.setAutoAck(false);
  radio.startListening();

  radio.printDetails();
}

void loop(void) {   
  radio.stopListening();
  // Take the time, and send it.  This will block until complete
  unsigned long time = millis();
  
  if(false)
  for(int i = 0; i < 3; i++) {
    
    //radio.write( &counter, 1);
    String msg = "";
    msg += i;
    msg += ":m";
    Serial.println(msg);
    //printf("Now sending %s...\n",msg);
    char tmp[50];
    msg.toCharArray(tmp, 50);
    Serial.println(tmp);
    radio.write(tmp, sizeof(tmp));
    delay(1000);
  }
  
  Serial.println("a");
  radio.write("a", 1);
  delay(100);
}
