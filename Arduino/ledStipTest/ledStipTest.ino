#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"
#include <Adafruit_NeoPixel.h>
#include <avr/power.h>

#define IDLE 0
#define LUMENLUX 1
#define FOTONOSOL 2

#define PIN 2

#define channel        0x4c                  // nrf24 communication channel
#define writingPipe    0xF0F0F0F0E1LL        // nrf24 communication address
#define dataRate       RF24_250KBPS          // nrf24 data rate (lower == more distance)
#define paLevel        RF24_PA_HIGH          // nrf24 power level (black ebay models have some problems with PA_MAX)

RF24 radio(7, 8);

char receive_payload[33]; // +1 to allow room for a terminating NULL char

Adafruit_NeoPixel strip = Adafruit_NeoPixel(7, PIN, NEO_GRB + NEO_KHZ800);

String id = "sq01";

int ledPin = 5;
boolean ledState = false;
int step_min = 50;
int step_max = 240;
int stepper = 100;
int plus = 1;
int state = 0;

long prev_msg = 0;

void setup(void) {
  Serial.begin(57600);
  printf_begin();
  
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  
  strip.begin();
  strip.show();
  
  radio.begin();
  radio.setPALevel(paLevel);
  radio.setChannel(channel);
  radio.openReadingPipe(0, writingPipe);
  radio.enableDynamicPayloads();
  radio.setDataRate(dataRate);
  radio.setAutoAck(false);
  radio.startListening();

  radio.printDetails();
  
  prev_msg = millis();
  idle();
}

void loop(void) {  
  if(millis() - prev_msg > 30000) {
    idle(); 
  }
  Serial.println(millis()-prev_msg);
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
    prev_msg = millis();
    parseMsg(receive_payload);
  }
  //idle();
  /*delay(1000);
  fotonosol();
  delay(1000);
  lumenlux();
  //if(ledState) nightRider2(strip.Color(255, 0, 0), strip.Color(50, 0, 0), strip.Color(20, 0, 0), strip.Color(0, 0, 0), 100);
  delay(1000);
  */
  //lumenlux();
  
  if(stepper >= step_max) {
    plus = -1;  
  } else if (stepper <= step_min) {
    plus = 1;
  } 
  stepper += plus;
  if(state == LUMENLUX)
    ledStripOn(strip.Color(stepper, 130, 20));
  else if(state == FOTONOSOL)
    ledStripOn(strip.Color(10, 100, stepper));
  else  
    ledStripOn(strip.Color(160, stepper, 210));
  delay(10);
}

void parseMsg(String msg) {
  if(msg.indexOf("{") != -1 && msg.indexOf("}") != -1 && msg.indexOf(":") != -1) {
    msg = msg.substring(msg.indexOf("{") +1, msg.indexOf("}"));
    if(checkId(msg.substring(0, msg.indexOf(":")))) {
      msg = msg.substring(msg.indexOf(":")+1);
      if(msg == "fotonosol") fotonosol();
      else if(msg == "lumenlux") lumenlux();
      else idle(); 
    }
  }
}

void fotonosol() {
  state = FOTONOSOL;
  step_max = 220;
  step_min = 100;
}

void lumenlux() {
  state = LUMENLUX;
  step_max = 225;
  step_min = 110;
}

void idle() {
  state = IDLE;
  step_max = 255;
  step_min = 0;
}

void nightRider2(uint32_t dot, uint32_t dot2, uint32_t dot3, uint32_t background, int wait) {
  for(int i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i-3, background);
      strip.setPixelColor(i-2, dot3);
      strip.setPixelColor(i-1, dot2);
      strip.setPixelColor(i, dot);
      strip.show();
      delay(wait);
  }
  for(int i = strip.numPixels()-1; i >= 0; i--) {
    Serial.println(i );
      strip.setPixelColor(i+3, background);
      strip.setPixelColor(i+2, dot3);
      strip.setPixelColor(i+1, dot2);
      strip.setPixelColor(i, dot);
      strip.show();
      delay(wait);
  }
}

void ledStripOn() {
  for(int i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, strip.Color(255, 0, 0));
      strip.show();
  }
}

void ledStripOn(uint32_t c) {
  for(int i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
      strip.show();
  }
}

void ledStripOff() {
  for(int i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, strip.Color(0,0,0));
      strip.show();
  }
}

boolean checkId(String mid) {
  if(mid.compareTo(id) == 0 || mid.compareTo("all") == 0) return true;
  return false;
}

void toggleLed() {
  digitalWrite(ledPin, ledState);
  if(ledState) ledStripOn();
  else ledStripOff();
  ledState = !ledState;
}


