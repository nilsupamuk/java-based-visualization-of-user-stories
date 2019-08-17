# Java Based Visualization of User Stories

## Agile Story
* Cmpe 492 Senior Project
* Nilsu Pamuk
* Boğaziçi University Computer Engineering Department
* Advisor: Dr. Fatma Başak Aydemir

## Description

My goal was to separate the user stories by their actors, verbs and objects so that I can visualize the user stories. I begin my project by reading the user stories that were stored in Excel. I used JFileChooser API to navigate the file system to choose the file visually. Afterwards I used Apache POI library to read data from Excel. Then I shorten sentences manually. After shortening the sentences I had to use Natural Language Processing to analyze elements of before mentioned sentences. After I had detached the elements of sentences, I analyzed patterns and made rules about syntax of these patterns to get actors, verbs and objects for creating Word Cloud and Tree Style Visualizations.

## Project Setup

The project is implemented using Java language in Eclipse IDE. Additionally, used libraries and API's are JFileChooser, Apache POI, Java Regex, Apache OpenNLP, Java Swing, OpenCloud.

### Running The Program

To open the program, you must run Main.java file. When choosing a file through the file chooser, you should select an excel file, in which the desired user stories are on the second sheet and the first column on that second sheet is user story id, second column is the user story in "As a ...., I want to ......, so that ......" format, third column is the weight of the user story.
