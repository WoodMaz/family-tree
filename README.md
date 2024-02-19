# Documentation

### Features
This project is a family-tree app. It allows to:
* create and manage complex family trees;
* export them in a GEDCOM format;
* merge trees



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
* consider if FamilyTree class should store personId, and Person class shouldn't know about FamilyTree
* more logs
* test and develop security
* consider advanced features:
* * sharing data between users (e.g. by familyTreeId)
* * storing multimedia
