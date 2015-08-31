#include <SoftwareSerial.h>
SoftwareSerial bt(10, 11); 

int ledPin = 8;

int count = 3;
int x[3] = {0,0,0};
int y[3] = {0,0,0};
//#define AT_TRIES 120
void setup() {
  Serial.begin(9600);
  bt.begin(9600);
  
  Serial.println("start");
  
  pinMode(ledPin, OUTPUT);
  
  for(int i = 0; i < count; i++) {
    x[i] = random(300);
    y[i] = random(200);
  }
  String res;
  while((res = sendATcmd("AT+NAMEStageCornerBL")).length() <= 0);
  /*
  Serial.println("> Setting up bluetooth..");
  String res = "";
  while((res = sendATcmd("AT")).length() <= 0);
  while((res = sendATcmd("AT+RESET")).length() <= 0);
  while((res = sendATcmd("AT+ROLE0")).length() <= 0);
  while((res = sendATcmd("AT+IMME1")).length() <= 0);
  while((res = sendATcmd("AT+ADDR?")).length() <= 0);
  while((res = sendATcmd("AT+NAMESlave")).length() <= 0);
  Serial.println("> Bluetooth ready..");
  */
}

void loop() {
  readBluetooth();
  /*
  String in = readSerial();
  if(in.length() > 0) {
    Serial.print("> Data in: ");
    Serial.println(in);
  }
  */
  //sendData();
  delay(50);
}

String readSerial() {
  String inData="";
  if (bt.available() > 0) {
    int h = bt.available();    
    for (int i = 0; i < h; i++){
      inData += (char)bt.read();
    }
  }
  return inData;
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
    String response = parseMessage(data);
    
    //lastMsg = millis();
    
    Serial.print(">> Received message: ");
    Serial.println(data);
    Serial.print(">>> Sending response: ");
    Serial.println(response);
    bt.print(response);
  }
  return data;
}

void sendData() {
  Serial.println("> Sending data..");
  for(int i = 0; i < count; i++) {
    int xd = random(2);
    int yd = random(2); 
    
    if(xd == 1 && x[i] < 290) {
      x[i] += 5;
    } else if (x[i] > 10){
      x[i] -= 5;
    }
    
    if(yd == 1 && y[i] < 190) {
      y[i] += 5;
    } else if(y[i] > 10){
      y[i] -= 5;
    }
    String msg = "{\"id\":\"";
    msg += i;
    msg += "\", \"x\":\"";
    msg += x[i];
    msg += "\", \"y\":\"";
    msg += y[i];
    msg += "\"}";
    //Serial.println(msg);
    bt.println(msg);
  }
}

String sendATcmd(String cmd) {
  char* buf = (char*) malloc(sizeof(char)*cmd.length()+1);
  cmd.toCharArray(buf, cmd.length()+1); 
  Serial.print(">> Sending cmd: ");
  Serial.println(buf);
  bt.write(buf);
  free(buf);
  return waitForBTResponse(5);
}

String waitForBTResponse(int tries) {
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

String parseMessage(String data) {
  String response = "fail";
  boolean ok = true;
  data.trim();
  if(data.startsWith("{") && data.endsWith("}")) {
    data = data.substring(1, data.length()-1);
    //String args[5];
    String id;
    String msg;
    if(data.indexOf(":") != -1) {
      int index = data.indexOf(":");
      id = data.substring(0,index);
      //data = data.substring(index+1);
      msg = data.substring(index+1);
      
      Serial.println(id);
      Serial.println(msg);
      if(msg.compareTo("led_on") == 0) {  
        digitalWrite(ledPin, HIGH);
      } else if (msg.compareTo("led_off") == 0) {  
        digitalWrite(ledPin, LOW);
      } else {  //unknown type
        ok = false;
      }
    } 
    
    // everything ok
    if(ok) {
      response = "{"+id+":ok}";
    } else {
      response = "{"+id+":error}";
    }
  }
  return response;
}
