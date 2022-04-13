# Path-finding
A*, bfs, ucs

Program implemented as part of Laboratory exercise based on AI.


## Table of contents
* [General info](#general-info)
* [Feature](#feature)
* [Use](#use)

## General info
This program represents three algorithms for path finding in AI. Uniform cost search, A star and Breath first search. 
Programmed in Java.


## Feature
Project is useful for many implementations with right file representation.

## Use
There is a file in resources called istra.txt that has names od Istra towns and distance between them. 

![image](https://user-images.githubusercontent.com/62765687/163183056-ca123eee-ac08-49e4-9975-46f535ae1984.png)

Code is written in Java so make a call with 
```
>>> mvn compile
>>> java -cp target/classes ui.Solution --alg <<bfs, astar, ucs>> --ss istra.txt --h
istra_heuristic.txt
```
where --alg is flag that next command is type of algorithm, --ss is flag for file in which are distance between towns. 
Last flag (--h) is used for heuristic file

You need to add heuristic file only while using Astar algorithm.
