# Alumni

This is my project of an web application that facilitates the communcation of parents, structured as a chat.

## Technologies - Languages+Frameworks ##
*Java 8 / Spring + Junit 5* - for this backend.  
*Typescript / Angular 9*  - frontend - the socketio compat. version is dependent on the framework version used).  
*SocketIO* - netty version -> 1.7.17
Check the compatible version on the frontend repo for JavaScript.

It worked on localhost!
The project works on docker with a slightly different configuration.
## Structure
The user logs in, then he navigates through groups, which have channels, where he can talk.

This application currently holds the following functionality:

- Super Admin

Assign users with Group admin or Super admin roles.
Also has group admin privileges
Can remove users from the chat application.

- Group Admin

Group administrators will have an input form to add new channels and buttons to remove channels. Note, the general default channel cannot be removed (however, an administrator can remove a group from the Dashboard component).

Group administrators can also add users to the group and remove users from the group. Adding a non-existent user simply creates the user and adds it to the group. Adding a user to the group automatically adds them to the default general channel.
You can create one group for each school and one for all channel administrators.

- Users

Identified by username.

## School / Channel
The Channel component contains all the features of the user interface of a chat channel. It contains a list of users, the name of the current channel, the chat box and the text field, and the button to send messages.
## Dashboard
The Dashboard component contains all available groups to the user, username, current email address, an input field to update their email, and a sign-out button.
For group administrators, there is a form to create new groups and buttons to remove groups. Default beginner and general groups cannot be removed.
For a super administrator, he can see the entire list of users in the system. Super administrators can remove users from the system and assign group or super admin roles to existing users.
## Group
The Group component contains all the information for the groups. This includes the group name, the connected user, the channels available to the user, and a disconnect button.

