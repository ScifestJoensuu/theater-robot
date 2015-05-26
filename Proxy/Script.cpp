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

bool Script::parseScriptFromString(string s)
{
  script_txt = s;
  cout << "Parsing script '" << s << "'"<< endl;
  Json::StyledWriter sw;
  Json::Reader reader;
  Json::Value root;
  bool ok = reader.parse(s.c_str(), root);
  if(ok)
    {
      //cout << "Parse ok" << endl;
      //      cout << atoi(root["x"].asString().c_str()) << endl;
      ScriptCommand* cmd = new ScriptCommand(root["id"].asString(), 
					     ScriptCommand::DRIVE, 
					     ScriptCommand::POINT, 
					     atoi(root["x"].asString().c_str()), 
					     atoi(root["y"].asString().c_str()));
	
      cmd->print();

      this->cmd_iterator = this->commands.begin();
      return true;
    }
  cout << "Parsing FAILED" << endl;
  return false;

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

