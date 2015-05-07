#include "BluetoothConnection.h"

struct sockaddr_rc addr = { 0 };
//int s, status;
//char dest[18] = "20:14:05:05:14:66";
//string address;

BluetoothConnection::BluetoothConnection() 
{
}

BluetoothConnection::BluetoothConnection(string addr)
{
  init(addr);
}
int BluetoothConnection::getStatus() 
{
  return status;
}

int BluetoothConnection::init(string addr) 
{
  //cout << "Initializing btc object: " << addr << endl;
  msg_counter = 0;
  try_counter = 0;
 //strcpy(address, addr);
  address = addr;

  initConnection();
  connectBluetooth();
  //strcpy(address, addr);
  //  BluetoothConnection::establishConnection();
  //if( status < 0 ) perror("uh oh");

  //close(s);
  return 0;
}

string BluetoothConnection::getAddress()
{
  return address;
}
void BluetoothConnection::initConnection() 
{
  
  cout << "Opening BT connection to: " << address.c_str() << endl;

  // allocate a socket
  s = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
  //cout << "Socket: " << s << endl;
  // set the connection parameters (who to connect to)
  addr.rc_family = AF_BLUETOOTH;
  addr.rc_channel = (uint8_t) 1;
  str2ba( address.c_str(), &addr.rc_bdaddr );

  //  return status;
}

bool BluetoothConnection::connectBluetooth()
{
  // connect to server
  status = connect(s, (struct sockaddr *)&addr, sizeof(addr));
  if(status < 0) cout << "Status " << status << ", errno " << strerror(errno) <<   endl;

  if(status == 0) return true;
  return false;
}

void BluetoothConnection::closeConnection() 
{
  cout << "Closing BT connection" << endl;
  close(s);
}

bool BluetoothConnection::sendMessageAndWaitForResponse(string msg) 
{
  //  cout << "Trying to send a message and wait for a response" << endl;
  try_counter++;
  int result = sendMessage(msg);
  if(result != -1) {
    string response = readMessage();
    if(!response.empty()) {
      return true;
    } else if(try_counter > 5) {
      try_counter = 0;
      cout << "Did not receive any response, giving up.." << endl;
      return false;
    } else {
      return sendMessageAndWaitForResponse(msg);
    }
  } else {
    try_counter++;
    if(try_counter > 5) {
      try_counter = 0;
      cout << "Did not receive any response, giving up.." << endl;
      return false;
    } else {
      return sendMessageAndWaitForResponse(msg);
    }
  }
}

int BluetoothConnection::sendMessage(string msg) 
{
  if(status == -1) connectBluetooth();
  //cout << "Trying to send a message" << endl;
  //  char tmp[1024];
  //  strcpy(tmp, msg.c_str());
  // send a message
  //  connectBluetooth();
  if( status != -1 ) {
    string message = buildMessage(msg);
    cout << "Sending message: '" << message << "'" << endl;
    status = write(s, message.c_str(), strlen(message.c_str()));
    if(status == -1) 
      cout << "Status " << status << ", errno " << strerror(errno) << endl;
  }
  //  closeConnection();
  return status;
}

string BluetoothConnection::readMessage() 
{
  if(status == -1) connectBluetooth();
  cout << "Trying to read a message" << endl;
  //  connectBluetooth();
  char buf[1024] = {0};
  //while(buf != '}')
  if( status != -1 ) {
    int bytes_read = read(s, buf, sizeof(buf));
    if(bytes_read > 0) printf("Received [%s]\n", buf);
    //closeConnection();
  }
  //  closeConnection();
  return buf;
}

string BluetoothConnection::buildMessage(string msg)
{
  stringstream stream;
  stream << "{" << msg_counter++ << ":" << msg << "}";
  return stream.str();;
}
