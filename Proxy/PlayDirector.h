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

class PlayDirector
{
	Script* script;
	Stage* stage;
	void executeScript();
	void executeCommand(ScriptCommand *cmd);
	bool directRobotTo(string robot_id, StagePoint p);
	bool directRobotTo(string robot_id, string target_id);
	void test();
public:
    PlayDirector();
	void startSession(int connection);
	void setScript(Script* script);
	void setStage(Stage* s);
	Stage* getStage();
};


#endif /* PLAYDIRECTOR_H_ */
