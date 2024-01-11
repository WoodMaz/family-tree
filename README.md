# Documentation

### Features
As of this moment this project is a simple CRUD app. It allows to create a new member,
who can be related to other members. 


### Database
This project uses MongoDB. Currently, there is one collection called 'User' which contains:
* username
* encrypted password
* role (not used yet)
* collection of family tree members (called Person in project)

Every member contains:
* name
* surname
* dates of birth and death
* id of mother, father and spouse
* note

### TODO list

Database:
* consider whether the database should be changed to SQL
* consider whether 'Person' should be not in the 'User' document

Application:
* create methods for updating and deleting family members
* test and develop security
* consider advanced features:
* * sharing data between users
* * joining individual trees
* * storing multimedia
* * downloading a tree as e.g. PDF
* * import/export as .ged

Bugs:
* ID is not generated for members