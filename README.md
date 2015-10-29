DictionaryAttack 
================

CSCI 251-02 Project 1 - Dictionary Attack

Project description: http://www.cs.rit.edu/~ark/251/p1/p1.shtml

Usage: java PasswordCrack <dictionaryFile> <databaseFile>

Performs a dictionary attack using a provited dictionary and a provided database.

Dictionary is a plain text file with one possible password per line.

Database file is a plain text file with a username, one or more whitespace characters and a hashed password.

Passwords are hashed using SHA256, current implementation does the hash 100k times, this can be changed in Hasher.numHashes


