#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <unistd.h>
#include <iostream>
#include <thread>

using namespace std;

void threadTest(string msg);

int main(int argc, char **argv)
{

	thread t1(threadTest, "moi");
	thread t2(threadTest, "tere");
	t1.join();
	t2.join();

    struct sockaddr_rc addr = { 0 };
    int s, status;
    char dest[18] = "20:14:05:05:14:66";

    // allocate a socket
    s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    // set the connection parameters (who to connect to)
    addr.rc_family = AF_BLUETOOTH;
    addr.rc_channel = (uint8_t) 1;
    str2ba( dest, &addr.rc_bdaddr );

    // connect to server
    status = connect(s, (struct sockaddr *)&addr, sizeof(addr));

    // send a message
    if( status == 0 ) {
	for(int i = 0; i < 5; i++) {
        	status = write(s, "hello!", 6);
		std::cout << i << std::endl;
		sleep(1);
	}
    }
    char buf[1024] = {0};
    int bytes_read = read(s, buf, sizeof(buf));
    if(bytes_read > 0) printf("received [%s]\n", buf);

    if( status < 0 ) perror("uh oh");

    close(s);
    return 0;
}

void sendMsg() {

}

void threadTest(string msg) {
	for(int i = 0; i < 10 ; i++) {
		std::cout << msg << std::endl;
		sleep(1);
	}
}
