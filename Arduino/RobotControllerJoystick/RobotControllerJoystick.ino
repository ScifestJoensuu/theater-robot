
#include <SoftwareSerial.h>
#include <AFMotor.h>
#include <Servo.h>

#define AT_TRIES 5

SoftwareSerial bt(8, 9); // RX, TX

int led = 13;

int xPin = 1;
int yPin = 0;
int buttonPin = 3;
boolean bt_enabled = true;
boolean echo = false;

int b_count = 0;

void setup()  
{
  Serial.begin(9600);
  while (!Serial) {;}
  
  Serial.println("> Running startup sequence..");

  bt.begin(9600);
  
  //init stuff
  Serial.println("> Setting up misc stuff..");
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  
  //if(bt_enabled) {
  Serial.println("> Setting up bluetooth..");
  String res = "";
  while((res = sendATcmd("AT")).length() <= 0);
  while((res = sendATcmd("AT+RESET")).length() <= 0);
  while((res = sendATcmd("AT+RENEW")).length() <= 0);
  while((res = sendATcmd("AT+ROLE1")).length() <= 0);
  while((res = sendATcmd("AT+IMME1")).length() <= 0);
  while((res = sendATcmd("AT+ADDR?")).length() <= 0);
  while((res = sendATcmd("AT+FILT0")).length() <= 0);
  while((res = sendATcmd("AT+SHOW1")).length() <= 0);
  while((res = sendATcmd("AT+NOTI0")).length() <= 0);
  while((res = sendATcmd("AT+NAMEMaster")).length() <= 0);
  
  Serial.println("> Bluetooth ready..");
  
  delay(500);
  
  boolean connection = false;
  Serial.println("> Searching devices..");
  
  findAndConnect();
  
  bt.print("{0:s:ml:n:1}");
  bt.print("{0:s:mr:n:2}");
  
  Serial.println("> Startup sequence ready..");
  Serial.println("> Listening the proxy..");
 
  Serial.println("> Init done.");
  
}

void loop() {
  
  /*
  readBluetooth();
  readSerial();
  
  sendATcmd("AT+CONNL");
  //readBluetooth();
  delay(100);
  bt.print("moi!");
  delay(100);
  
  sendATcmd("AT");
  readBluetooth();
  */
  int b = analogRead(buttonPin);
  delay(50);
  int x = analogRead(xPin);
  delay(50);
  int y = analogRead(yPin);
  String msg = "{0:c:";
  String cmd;
  Serial.println(b);
  if(b < 5) {
    b_count++;
    if(b_count > 1) {
      //bt.print("{0:c:ds:b:1}");
    }
    if(b_count >= 4) {
      b_count = 0;
      findAndConnect();
    }
  } else if(y < 300) {
    // forward
    cmd = "df";
  } else if(y > 800) {
    // bwd
    cmd = "db";
  } else if(x < 300) {
    // right
    cmd = "dr";
  } else if(x > 800) {
    // left
    cmd = "dl";
  } else {
    // stop
    cmd = "ds";
  }
  msg += cmd;
  msg += ":b:1}";
  bt.print(msg);
  Serial.println(msg);
  delay(100);
  
}

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}

boolean findAndConnect() {
  String res;
  while((res = sendATcmd("AT")).length() <= 0);
  delay(1000);
  while((res = sendATcmd("AT+DISC?")).length() <= 0);
    
  boolean connection = false;
  do {
  // try connecting
  //break;
    res = waitForbtResponse(100);
    if(res.startsWith("OK+DISCE")) {
      connection = true;
      //Serial.println("OK");
    }
    
  } while(!connection);
  
  
  sendATcmd("AT+CONN0");
  
  delay(1000);
  return true;
}

String sendATcmd(String cmd) {
  char* buf = (char*) malloc(sizeof(char)*cmd.length()+1);
  cmd.toCharArray(buf, cmd.length()+1); 
  Serial.print(">> Sending cmd: ");
  Serial.println(buf);
  bt.write(buf);
  free(buf);
  return waitForbtResponse();
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
      bt.print(data);
    }
  }
  return data;
}

String readBluetooth() {
  String data = "";
  if(bt.available()) {
    delay(50);
    while(bt.available()) {
      data += (char)bt.read();
      if(data.endsWith("}")) break;
    }
    //Serial.print("Read data: ");
    //Serial.println(data);
    //String response = parseMessage(data);
    
    if(echo) {
      Serial.print(">> Echo message: ");
      Serial.println(data);
      bt.print(data);
    } else {
      Serial.print(">> Received message: ");
      Serial.println(data);
      //bt.print(response);
    }
  }
  return data;
}

String waitForbtResponse() {
  return waitForbtResponse(AT_TRIES);
}

String waitForbtResponse(int tries) {
  String response = "";
  boolean ok = false;
  int tried = 0;
  while(!ok) {
    //Serial.println(tried);
    delay(100);
    while(bt.available()) {
      response += (char)bt.read();
      ok = true;
    }
    tried++;
    if(tried > tries) break;
  }
  if(ok) {
    Serial.print(">> Response: ");
    Serial.println(response);
  }
  else Serial.println(">> Did not receive any response!");
  return response;
}


