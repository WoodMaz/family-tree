# Documentation

### Features
This project is a family-tree app. It allows to create a complex tree and export it in a GEDCOM format


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
* id of parents and spouse
* note

### TODO list




Application:
* test and develop security
* * at the moment each user can see each tree if only they know ID. There is need to secure trees. 
* consider advanced features:
* * sharing data between users (e.g. by familyTreeId)
* * joining individual trees
* * storing multimedia
