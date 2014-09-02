#include <SoftwareSerial.h>

SoftwareSerial mySerial(52, 53);

#include <AFMotor.h>

AF_DCMotor m_left(4);
AF_DCMotor m_right(1);

int MAX_LEFT = 250;
int MAX_RIGHT = 250;

int ledpin = 13;
String cmd;
boolean stringComplete = false;

void setup() {
  pinMode(ledpin, OUTPUT);
  
  set_speed(50);
  stop_motors();
  
  Serial.begin(9600);
  Serial.println("start");

  mySerial.begin(9600);
  mySerial.println("i'm ready");
}

void loop(){
  if(mySerial.available()) {
    cmd = "";
    while(mySerial.available() > 0) {
      char inChar = (char)mySerial.read();
      if (inChar == '\n' || inChar == '\r' || inChar == '|') {
        stringComplete = true;
        cmd += "\0";
      } else {
        cmd += inChar;
      }
    }
    Serial.println(cmd);
    if(cmd.startsWith("led_on")) {
      Serial.println("Led on");
      digitalWrite(ledpin, HIGH);
    } else if(cmd.startsWith("led_off")) {
      Serial.println("Led off");
      digitalWrite(ledpin, LOW);
    } else if(cmd.startsWith("drive_fwd")) {
      int fst_colon = cmd.indexOf(":");
      int sec_colon = cmd.indexOf(":", fst_colon+1);
      if(fst_colon != -1 && sec_colon != -1) {
        int time = (cmd.substring(fst_colon+1, sec_colon)).toInt();
        int pwr = (cmd.substring(sec_colon+1)).toInt();
        Serial.print("Driving fwd for ");
        Serial.print(time);
        Serial.print("ms with ");
        Serial.print(pwr);
        Serial.println(" power");
        
        set_speed(pwr);
        drive_fwd();
        delay(time);
        stop_motors();
      }
    } else if(cmd.startsWith("drive_bwd")) {
      int fst_colon = cmd.indexOf(":");
      int sec_colon = cmd.indexOf(":", fst_colon+1);
      if(fst_colon != -1 && sec_colon != -1) {
        int time = (cmd.substring(fst_colon+1, sec_colon)).toInt();
        int pwr = (cmd.substring(sec_colon+1)).toInt();
        Serial.print("Driving bwd for ");
        Serial.print(time);
        Serial.print("ms with ");
        Serial.print(pwr);
        Serial.println(" power");
        
        set_speed(pwr);
        drive_bwd();
        delay(time);
        stop_motors();
      }
    } else if(cmd.startsWith("turn_right")) {
      int fst_colon = cmd.indexOf(":");
      int sec_colon = cmd.indexOf(":", fst_colon+1);
      if(fst_colon != -1 && sec_colon != -1) {
        int time = (cmd.substring(fst_colon+1, sec_colon)).toInt();
        int pwr = (cmd.substring(sec_colon+1)).toInt();
        Serial.print("Turn right for ");
        Serial.print(time);
        Serial.print("ms with ");
        Serial.print(pwr);
        Serial.println(" power");
        
        set_speed(pwr);
        turn_right();
        delay(time);
        stop_motors();
      }
    } else if(cmd.startsWith("turn_left")) {
      int fst_colon = cmd.indexOf(":");
      int sec_colon = cmd.indexOf(":", fst_colon+1);
      if(fst_colon != -1 && sec_colon != -1) {
        int time = (cmd.substring(fst_colon+1, sec_colon)).toInt();
        int pwr = (cmd.substring(sec_colon+1)).toInt();
        Serial.print("Turn left for ");
        Serial.print(time);
        Serial.print("ms with ");
        Serial.print(pwr);
        Serial.println(" power");
        
        set_speed(pwr);
        turn_left();
        delay(time);
        stop_motors();
      }
    }
  }
}

void drive_fwd() {
  m_left.run(FORWARD);
  m_right.run(FORWARD);
}

void drive_bwd() {
  m_left.run(BACKWARD);
  m_right.run(BACKWARD);
}

void stop_motors() {
  m_left.run(RELEASE);
  m_right.run(RELEASE);
}

void turn_left() {
  m_left.run(BACKWARD);
  m_right.run(FORWARD);
}

void turn_right() {
  m_left.run(FORWARD);
  m_right.run(BACKWARD);
}

void set_speed(int s) {
  int left = map(s, 0, 100, 0, MAX_LEFT);
  int right = map(s, 0, 100, 0, MAX_RIGHT);
  
  Serial.print("Setting left motor speed to ");
  Serial.println(left);
  Serial.print("Setting right motor speed to ");
  Serial.println(right);
  m_left.setSpeed(left);
  m_right.setSpeed(right);
}

