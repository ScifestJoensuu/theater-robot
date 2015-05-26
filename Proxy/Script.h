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
#include <string>
#include <json/json.h>
#include <iostream>

using namespace std;

class Script
{
  string script_txt;
	vector<ScriptCommand*>::iterator cmd_iterator;
	vector<ScriptCommand*> commands;
public:
	Script();
	Script(string script);
	bool parseScriptFromString(string script);
	void addCommand(ScriptCommand* c);
	ScriptCommand* getNextCommand();
};


#endif /* SCRIPT_H_ */
