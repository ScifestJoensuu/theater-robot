/*
 * ScriptCommand.h
 *
 *  Created on: 19.5.2015
 *      Author: mikko
 */

#ifndef SCRIPTCOMMAND_H_
#define SCRIPTCOMMAND_H_

using namespace std;

class ScriptCommand
{
public:
	enum command_type {DRIVE};
	enum target_type {ROBOT, POINT};
	string robot_id;
	command_type cmd_t;
	target_type target_t;
	string target_id;
	int target_x;
	int target_y;

	ScriptCommand(string rid, command_type ct, target_type tt, string tid);
	ScriptCommand(string rid, command_type ct, target_type tt, int x, int y);
};


#endif /* SCRIPTCOMMAND_H_ */
