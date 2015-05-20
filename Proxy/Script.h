/*
 * Script.h
 *
 *  Created on: 19.5.2015
 *      Author: mikko
 */

#ifndef SCRIPT_H_
#define SCRIPT_H_

#include "ScriptCommand.h"
#include <vector>

using namespace std;

class Script
{
	string script;
	vector<ScriptCommand*>::iterator cmd_iterator;
	vector<ScriptCommand> commands;
public:
	Script();
	Script(string script);
	void parseScriptFromString(string script);
	void addCommand(ScriptCommand c);
	ScriptCommand* getNextCommand();
};


#endif /* SCRIPT_H_ */
