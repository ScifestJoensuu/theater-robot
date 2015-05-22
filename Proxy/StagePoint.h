/*
 * StagePoint.h
 *
 *  Created on: 13.5.2015
 *      Author: mikko
 */

#ifndef STAGEPOINT_H_
#define STAGEPOINT_H_

#include <ctime>
#include <math.h>

using namespace std;

class StagePoint
{

public:
	int x;
	int y;
	time_t timestamp;
	StagePoint();
	StagePoint(int x, int y);

	int getAngle(StagePoint* p);
	int getDistance(StagePoint* p);
	time_t getTime();
	/*
	void setX(int x);
	void setY(int y);
	int getX();
	int getY();
	*/
};


#endif /* STAGEPOINT_H_ */
