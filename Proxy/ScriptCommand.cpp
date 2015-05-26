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
  this->target_x = -1;
  this->target_y = -1;
}

ScriptCommand::ScriptCommand(string rid, command_type ct, target_type tt, int x, int y)
{
  this->robot_id = rid;
  this->cmd_t = ct;
  this->target_t = tt;
  this->target_id = "no_target_id";
  this->target_x = x;
  this->target_y = y;
}

void ScriptCommand::print()
{
  cout << "ScriptCommand:" << endl;
  cout << "robot_id:  " << robot_id << endl;
  cout << "cmd_t:     " << (cmd_t == DRIVE ? "DRIVE" : "") << endl;
  cout << "target_t:  " << (target_t == POINT ? "POINT" : (target_t == ROBOT ? "ROBOT" : "")) << endl;
  cout << "target_id: " << target_id << endl;
  cout << "target_x:  " << target_x << endl;
  cout << "target_y:  " << target_y << endl;
}
