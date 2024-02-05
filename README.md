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

Database:
* create collection 'FamilyTree' which contains:
* -id
* -name
* -description

- Users should have a list of familyTreeId
- Members should have a familyTreeId


Application:
* test and develop security
* consider advanced features:
* * sharing data between users (e.g. by familyTreeId)
* * joining individual trees
* * storing multimedia
