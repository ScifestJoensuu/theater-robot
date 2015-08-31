
#include <SoftwareSerial.h>
#include <AFMotor.h>
#include <Servo.h>

#define AT_TRIES 120

//SoftwareSerial Serial1(30, 31); // RX, TX

int led = 13;

boolean Serial1_enabled = true;
boolean echo = false;

void setup()  
{
  Serial.begin(9600);
  while (!Serial) {;}
  
  Serial.println("> Running startup sequence..");

  Serial1.begin(9600);
  
  //init stuff
  Serial.println("> Setting up misc stuff..");
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  
  if(Serial1_enabled) {
  Serial.println("> Setting up bluetooth..");
  String res = "";
  while((res = sendATcmd("AT")).length() <= 0);
  //while((res = sendATcmd("AT+RESET")).length() <= 0);
  //while((res = sendATcmd("AT+ROLE0")).length() <= 0);
  //while((res = sendATcmd("AT+IMME1")).length() <= 0);
  //while((res = sendATcmd("AT+ADDR?")).length() <= 0);
  while((res = sendATcmd("AT+NAMESlave")).length() <= 0);
  Serial.println("> Bluetooth ready..");
  
  boolean connection = false;
  do {
    Serial.print("> Handshaking with proxy..");
  // try connecting
    for(int i = 0; i < 5; i++) {
      delay(500);
      Serial.print("..");
    }
    connection = true;
    Serial.println("OK");
  } while(!connection);
  }
  
  Serial.println("> Startup sequence ready..");
  Serial.println("> Listening the proxy..");
 
  Serial.println("> Init done.");
}

void loop()
{
  readBluetooth();
  readSerial();
}

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}

String sendATcmd(String cmd) {
  char* buf = (char*) malloc(sizeof(char)*cmd.length()+1);
  cmd.toCharArray(buf, cmd.length()+1); 
  Serial.print(">> Sending cmd: ");
  Serial.println(buf);
  Serial1.write(buf);
  free(buf);
  return waitForSerial1Response();
}

String readSerial() {
  String data = "";
  if(Serial.available()) {
    delay(50);
    while(Serial.available()) {
      data += (char)Serial.read();
    }
    
    //Serial.print("Read data: ");
    //Serial.println(data);
    if(data.startsWith("AT")) {
      sendATcmd(data);
    } else {
      Serial.print(">> Sending normal message: ");
      Serial.println(data);
      //parseMessage(data);
      Serial1.println(data);
    }
  }
  return data;
}

String readBluetooth() {
  String data = "";
  if(Serial1.available()) {
    delay(50);
    while(Serial1.available()) {
      data += (char)Serial1.read();
      if(data.endsWith("}")) break;
    }
    //Serial.print("Read data: ");
    //Serial.println(data);
    //String response = parseMessage(data);
    
    if(echo) {
      Serial.print(">> Echo message: ");
      Serial.println(data);
      Serial1.print(data);
    } else {
      Serial.print(">> Received message: ");
      Serial.println(data);
      //Serial1.print(response);
    }
  }
  return data;
}

String waitForSerial1Response() {
  String response = "";
  boolean ok = false;
  int tried = 0;
  while(!ok) {
    //Serial.println(tried);
    delay(100);
    while(Serial1.available()) {
      response += (char)Serial1.read();
      ok = true;
    }
    tried++;
    if(tried > AT_TRIES) break;
  }
  if(ok) {
    Serial.print(">> Response: ");
    Serial.println(response);
  }
  else Serial.println(">> Did not receive any response!");
  return response;
}


