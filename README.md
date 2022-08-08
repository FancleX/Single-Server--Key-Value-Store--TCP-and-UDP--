# Single-Server--Key-Value-Store--TCP-and-UDP--
Guidelines 
Project #1 should be electronically submitted to Canvas by midnight on the due date.  A submission link 
is provided on Canvas. 
 
# Assignment Overview 
For this project, you will write a server program that will serve as a key value store.  It will be set up to 
allow a single client to communicate with the server and perform three basic operations:   
1) PUT (key, value)  
2) GET (key) 
3) DELETE(key)   
A Hash Map could be used for setting up Key value stores.  See 
For this project you will set up your server to be single-threaded and it only has to respond to a single 
request at a time (e.g. it need not be multi-threaded – that will be part of Project #2).   You must also 
use two distinct L4 communication protocols: UDP and TCP.  What this means is that your client and 
server programs must use sockets (no RPC....yet, that’s project #2) and be configurable such that you 
can dictate that client and server communicate using UDP for a given test run, but also be able to 
accomplish the same task using TCP.  If you choose, you could have two completely separate sets of 
applications, one that uses UDP and one that uses TCP or you may combine them.   
 
Your implementation may be written in Java.  Your source code should be well-factored and well-
commented. That means you should comment your code and appropriately split the project into multiple 
functions and/or classes; for example, you might have a helper function/class to encode/decode UDP 
packets for your protocol, or you might share some logic between this part of the assignment and the 
TCP client/server in the next part. 
 
# The client must fulfill the following requirements: 
 
• The client must take the following command line arguments, in the order listed: 
o The hostname or IP address of the server (it must   
accept either).
o The port number of the server. 
• The client should be robust to server failure by using a timeout mechanism to deal with an 
unresponsive server; if it does not receive a response to a particular request, you should note it in 
a client log and send the remaining requests. 
• You will have to design a simple protocol to communicate packet contents for the three request 
types along with data passed along as part of the requests (e.g. keys, values, etc.) The client must 
be robust to malformed or unrequested datagram packets. If it receives such a datagram packet, 
it should report it in a human-readable way (e.g., “received unsolicited response acknowledging 
2
unknown PUT/GET/DELETE with an invalid KEY” - something to that effect to denote an 
receiving an erroneous request) to your server log.    
• Every line the client prints to the client log should be time-stamped with the current system time. 
You may format the time any way you like as long as your output maintains millisecond 
precision.  
• You must have two instances of your client (or two separate clients):   
o One that communicates with the server over TCP 
o One that communicates with the server over 
UDP  
# The server must fulfill the following requirements: 
 
• The server must take the following command line arguments, in the order listed: 
• The port number it is to listen for datagram packets on. 
• The server should run forever (until forcibly killed by an external signal, such as a Control-C, a 
kill, or pressing the “Stop” button in Eclipse). 
• The server must display the requests received, and its responses, both in a human readable 
fashion; that is, it must explicitly print to the server log that it received a query from a particular 
InetAddress and port number for a specific word, and then print to the log its response to 
that query. 
• The server must be robust to malformed datagram packets. If it receives a malformed datagram 
packet, it should report it in a human-readable way (e.g., “received malformed request of length 
n from <address>:<port>”) to the server log. Do not attempt to just print malformed datagram 
packets to standard error verbatim; you won’t like the results. 
• Every line the server prints to standard output or standard error must be time-stamped with the 
current system time (i.e., System.currentTimeMillis()). You may format the time any 
way you like as long as your output maintains millisecond precision.  
• You must have two instances of your server (or two separate servers):   
o One that communicates with the server over TCP 
o One that communicates with the server over 
UDP 
# Other notes: 
You should use your client to pre-populate the Key-Value store with data and a set of keys.  The 
composition of the data is up to you in terms of what you want to store there.  Once the key-value store 
is populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs.  

# How to Run

For client:

```java
java -jar client.jar <host-name> <port-number> <protocol>
```

For server:

```
java -jar server.jar <tcp-port-number> <udp-port-number>
```

# Examples with description

For client:

```
java -jar client.jar localhost 6677 tcp
or java -jar client.jar localhost 4349 udp

Standar output:
2022-06-01 11:32:21: UDP Client started
2022-06-01 11:32:21: Please input a number to use services: 1. GET, 2. PUT, 3. DELETE, 4. Exit
2022-06-01 11:32:21: Received RESPONSE_NORMAL response of length 255 bytes with body {Request type: PUT : response: Request Completed} from Server
2022-06-01 11:32:21: Received RESPONSE_NORMAL response of length 255 bytes with body {Request type: PUT : response: Request Completed} from Server
2022-06-01 11:32:21: Received RESPONSE_NORMAL response of length 255 bytes with body {Request type: PUT : response: Request Completed} from Server
2022-06-01 11:32:21: Received RESPONSE_NORMAL response of length 255 bytes with body {Request type: PUT : response: Request Completed} from Server
2022-06-01 11:32:21: Received RESPONSE_NORMAL response of length 255 bytes with body {Request type: PUT : response: Request Completed} from Server
2022-06-01 11:34:01: Please input a number to use services: 1. GET, 2. PUT, 3. DELETE, 4. Exit
1
2022-06-01 11:34:03: Input a key to establish the GET request (up to 80 characters not empty): 
978
2022-06-01 11:34:07: Received RESPONSE_NORMAL response of length 244 bytes with body {Request type: GET : response: dfasdf} from Server

```

For server:

```
java -jar server.jar 43422 39943

Standard output:
2022-06-01 11:34:01: Received PUT request of length 230 bytes with body {key: qwe : value: 2323} from /127.0.0.1: 64294
2022-06-01 11:34:01: Sent RESPONSE_NORMAL response of length 255 bytes with body {request type: PUT : response content: Request Completed} to /127.0.0.1: 64294
2022-06-01 11:34:01: Received PUT request of length 230 bytes with body {key: cvc : value: 2323} from /127.0.0.1: 64294
2022-06-01 11:34:01: Sent RESPONSE_NORMAL response of length 255 bytes with body {request type: PUT : response content: Request Completed} to /127.0.0.1: 64294
2022-06-01 11:34:01: Received PUT request of length 230 bytes with body {key: qwe : value: 2323} from /127.0.0.1: 64294
2022-06-01 11:34:01: Sent RESPONSE_NORMAL response of length 255 bytes with body {request type: PUT : response content: Request Completed} to /127.0.0.1: 64294
2022-06-01 11:34:07: Received GET request of length 224 bytes with body {key: 978 : value: null} from /127.0.0.1: 64294
2022-06-01 11:34:07: Sent RESPONSE_NORMAL response of length 244 bytes with body {request type: GET : response content: dfasdf} to /127.0.0.1: 64294

```

# Assumption

A test class provided to test illegal requests / responses for client / server. It tested UDP for example. Illegal means sending arbitrary message without using defined the protocol and sending wrong request type with the protocol. The hostname and port should be modified to your condition before use. The TCP is different, TCP server will shut down the connection when an illegal client send illegal message.

# Limitation

Client times out can either lose connection with server for 20 seconds or user does not any action for 20 seconds. If user is interactable, it will stay running.
