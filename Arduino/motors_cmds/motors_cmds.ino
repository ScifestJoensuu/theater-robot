
// Adafruit motor shield library:
// http://www.adafruit.com/product/81
#include <AFMotor.h>

AF_DCMotor ml(1);
AF_DCMotor mr(2);

void setup() {
  Serial.begin(9600);

  Serial.println("> Setting up motors..");
  
  ml.setSpeed(200);
  mr.setSpeed(200);
  ml.run(RELEASE);
  mr.run(RELEASE);
}

void loop() {
  //drive a square
  driveFwd();
  delay(1000);
  turnRight();
  delay(500);
  stopMotors();
}

void driveFwd() {
  ml.run(FORWARD);
  mr.run(FORWARD);
}

void driveBwd() {
  ml.run(BACKWARD);
  mr.run(BACKWARD);
}

void turnRight() {
  ml.run(FORWARD);
  mr.run(BACKWARD);
}

void turnLeft() {
  ml.run(BACKWARD);
  mr.run(FORWARD);
}

void stopMotors() {
  ml.run(RELEASE);
  mr.run(RELEASE);
}

