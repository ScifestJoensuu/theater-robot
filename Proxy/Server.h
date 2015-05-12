/*
 * Server.h
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#ifndef SERVER_H
#define SERVER_H

class Server
{
public:
	void init(int port);
	int waitForConnection();
};




#endif /* SERVER_H_ */
