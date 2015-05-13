#include "BluetoothManager.h"

//int tries = 0;

vector<BluetoothConnection*> BluetoothManager::scanDevices()
{
  cout << ">> Scanning bluetooth devices.." << endl;
  vector<BluetoothConnection*> devices;

  inquiry_info *ii = NULL;
  int max_rsp, num_rsp;
  int dev_id, sock, len, flags;
  int i;
  char addr[19] = { 0 };
  char name[248] = { 0 };

  dev_id = hci_get_route(NULL);
  sock = hci_open_dev( dev_id );
  if (dev_id < 0 || sock < 0) {
      perror("opening socket");
      exit(1);
  }

  len  = 8;
  max_rsp = 255;
  flags = IREQ_CACHE_FLUSH;
  ii = (inquiry_info*)malloc(max_rsp * sizeof(inquiry_info));
    
  num_rsp = hci_inquiry(dev_id, len, max_rsp, NULL, &ii, flags);
  if( num_rsp < 0 ) perror("hci_inquiry");

  for (i = 0; i < num_rsp; i++) {
    ba2str(&(ii+i)->bdaddr, addr);
    memset(name, 0, sizeof(name));
    if (hci_read_remote_name(sock, &(ii+i)->bdaddr, sizeof(name), name, 0) < 0) {
      strcpy(name, "[unknown]");
    }
    printf("%s  %s\n", addr, name);
    string startswith("Stage");
    string n = name;
    if(!n.compare(0, startswith.size(), startswith)) {
      BluetoothConnection *tmp = new BluetoothConnection(name, addr);
      devices.push_back(tmp);
    }
  }
  cout << ">> Found " << devices.size() << " bluetooth devices" << endl;
  free( ii );
  close( sock );
  /*
  if(!stage->cornersOk()) {
    if(tries < 3) return scanDevices();
  }
  */
  return devices;
}

void BluetoothManager::setStage(Stage* s) 
{
  stage = s;
}

Stage* BluetoothManager::getStage() 
{
  return stage;
}
