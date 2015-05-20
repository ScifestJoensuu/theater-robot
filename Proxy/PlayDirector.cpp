/*
 * PlayDirector.cpp
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#include "PlayDirector.h"

/*
void directRobotTo(Robot r);
void dircetRobotTo(int x, int y);
*/

PlayDirector::PlayDirector()
{
	script = new Script();
}

void PlayDirector::startSession(int connection)
{

}

void PlayDirector::test()
{
	script = new Script();
	script->addCommand(ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 50, 50));
	script->addCommand(ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 100, 100));
	script->addCommand(ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 150, 50));
	executeScript();
}

void PlayDirector::executeScript()
{
	cout << ">> Executing script.." << endl;
	ScriptCommand *cmd;
	while((cmd = script->getNextCommand()) != nullptr) {
		executeCommand(cmd);
	}
	cout << ">> Script ended" << endl;
}

void PlayDirector::executeCommand(ScriptCommand *cmd)
{
	bool ok = false;
	while(!ok) {
		switch (cmd->cmd_t) {
		case ScriptCommand::DRIVE:
			switch (cmd->target_t) {
			case ScriptCommand::POINT:
				stage->getRobot(cmd->robot_id)->setCheckpoint();
				ok = directRobotTo(cmd->robot_id, StagePoint(cmd->target_x, cmd->target_y));
				break;
			case ScriptCommand::ROBOT:
				stage->getRobot(cmd->robot_id)->setCheckpoint();
				ok = directRobotTo(cmd->robot_id, cmd->target_id);
				break;
			}
			break;
		}
	}
}

bool PlayDirector::directRobotTo(string robot_id, StagePoint p)
{
	cout << ">> Directing robot to " << p.x << ", " << p.y << endl;
	if(stage->robotAtPoint(robot_id, &p)) return true;
	Robot* robot = stage->getRobot(robot_id);
	StagePoint* robot_location = stage->findRobot(robot);
	int distance = robot_location->getDistance(&p);
	int robot_dir = robot->getShortDirection();
	int target_dir = robot_location->getAngle(&p);
	int dir_change = target_dir - robot_dir;

	cout << ">>> Robot position:   " << robot_location->x << ", " << robot_location->y << endl;
	cout << ">>> Robot direction:  " << robot_dir << endl;
	cout << ">>> Target direction: " << target_dir << endl;
	cout << ">>> Direction change: " << dir_change << endl;

	if(dir_change < 0) {
		robot->turnLeft(dir_change);
	} else if(dir_change > 0) {
		robot->turnLeft(dir_change);
	}
	robot->driveForward();

	// wait

	return directRobotTo(robot_id, StagePoint p);
}

bool PlayDirector::directRobotTo(string robot_id, string target_id)
{

}
