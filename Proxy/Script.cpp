/*
 * Script.cpp
 *
 *  Created on: 19.5.2015
 *      Author: mikko
 */

#include "Script.h"

Script::Script()
{
}

Script::Script(string script)
{
	parseScriptFromString(script);
}

void Script::parseScriptFromString(string s)
{
	script_txt = s;

	// TODO...

	this->cmd_iterator = this->commands.begin();
}

void Script::addCommand(ScriptCommand* cmd)
{
	this->commands.push_back(cmd);
}

ScriptCommand* Script::getNextCommand()
{
	if(this->cmd_iterator != this->commands.end())
		return *cmd_iterator++;
	else
		return nullptr;
}

