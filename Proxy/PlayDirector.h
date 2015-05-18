/*
 * PlayDirector.h
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#ifndef PLAYDIRECTOR_H_
#define PLAYDIRECTOR_H_

#include "Robot.h"

class PlayDirector
{
  void directRobotTo(Robot r);
public:
    PlayDirector();
	void startSession(int connection);
};


#endif /* PLAYDIRECTOR_H_ */
