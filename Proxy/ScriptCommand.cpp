/*
 * ScriptCommand.cpp
 *
 *  Created on: 19.5.2015
 *      Author: mikko
 */

#include "ScriptCommand.h"

ScriptCommand::ScriptCommand(string rid, command_type ct, target_type tt, string tid)
{
	this->robot_id = rid;
	this->cmd_t = ct;
	this->target_t = tt;
	this->target_id = tid;
	this->target_x = 0;
	this->target_y = 0;
}

ScriptCommand::ScriptCommand(string rid, command_type ct, target_type tt, int x, int y)
{
	this->robot_id = rid;
	this->cmd_t = ct;
	this->target_t = tt;
	this->target_id = nullptr;
	this->target_x = x;
	this->target_y = y;
}

