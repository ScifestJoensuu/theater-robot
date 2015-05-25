/*
 * PlayDirector.h
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#ifndef PLAYDIRECTOR_H_
#define PLAYDIRECTOR_H_

#include "Stage.h"
#include "Robot.h"
#include "Script.h"
#include "ArduinoConnection.h";
#include <thread>

class PlayDirector
{
	Script* script;
	Stage* stage;
	ArduinoConnection* ard;

	void executeCommand(ScriptCommand *cmd);
	bool directRobotTo(string robot_id, StagePoint p);
	bool directRobotTo(string robot_id, string target_id);
	void test();

public:
    PlayDirector();
    void setArduinoConnection(ArduinoConnection* c);
    void executeScript();
    void executeScript(string script);
	void startSession(int connection);
	void setScript(string script);
	void setScript(Script* script);
	void setStage(Stage* s);
	bool sendRobotLocation(Robot* r);
	bool sendAllRobotLocations();
	Stage* getStage();
};


#endif /* PLAYDIRECTOR_H_ */
