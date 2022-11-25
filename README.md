Run instructions
================

This project is run by running the PrinterServer.Java file to start the server,
then afterwards running the Client.Java file. You will then be presented with the
login screen, for the Printer server.
An example of PrinterServer and Client being run

================================================
```
PrinterServer.main();
Client.main();
```
Then the following output is given:
```
Please select an option for: 
 1: Access Control List 
 2: Role Based Access Control
 ```
These options allow the user to select which access control policy is used,
this is strictly for testing purposes.
By inputting
```
2
```
into the terminal, you select option 2, which is the RBAC policy, and as such
are presented with:
```
Set policy to RBAC
Welcome to the print server 
 1: Login 
 2: Exit
 ```
 This is the screen, where a user logs in, this file will show logging in as
 an admin Alice, thus option 1 is selected.
 ```
Enter username
Alice
Enter password
Alice
Login successful
 ```
Entering a correct username and password logs the user in, after which the user
is now allowed access to the printer server.
As Alice is an admin they are presented with more options, as shown:
 ```
 Server Options: 
                 1: Start Server                         2: Stop Server                          3: Restart Server
Printer Functions:
                 4: Print file
                 5: Print the job queue of a specific printer
                 6: Move a job on a specfic printer to the top of the queue
                 7: Get status of a printer
 Config Options:
                 8: Read a config parameter
                 9: Set a config parameter
10: Exit Server GUI

Access Control Options:
                 12: Add a user to the access control
                 13: Remove a user from the access control
                 14: Change a user's role in the access control
 ```
 Only admin users are presented with the Access Control Options, and are the only
 ones allowed to access it.
 As the printer server is not started, before doing anything else the server has to be started
 
 As such option 1 is selected:
 ```
  1
 ```
 Where the server will respond with the message 
 ```
 Server is starting
 ```
Now each option is able to be freely accepted, if the user is allowed to access the inputted function

Adding a user to the Access Control
===================================
Depending on policy selected the output will be slightly different, however the differences are minor.
For this test the RBAC policy is selected, and will thus show that output.

The following is inputted into the terminal:
```
12
```
You are now presented with the following output:
```
Enter the username of the user you want to add to the access control
```
The username needs to be a valid user, and has to already be existing in the system. All the users 
described in the Assignment 3 report, are included.

For this test, a user named Boby is added to the access control as a user.
As such the following output is presented, while following the on-screen instructions:
```
Enter the username of the user you want to add to the access control
Boby
Enter the name of the role you want to add the user to
user
User Boby added to access control with role user
```
Boby is now added as a user in the users_roles.json file.

Deleting a user in the Access Control
=====================================


