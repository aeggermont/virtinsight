# virtinsight

## Main goal of the project.

I am developing this project to get more skills in android development with a focus on services integration for interactive media apps. I am very interested in working with geolocation services, virtual and argumented interactive reality experiences, speech recogniztion and content providers to keep data persistance in the app.  I thought building a basic interactive photo album would be a good start point for me to explore these technologies in the middle term after the completion of this class. 

##Project Description

This is an interactive virtual photo album recorder which creates virtual albums with different media inputs such as photos, speech and texts. The virtual album will eventually be viewed and browsed with virtaul reality wearable lenses to navigame throughout albums and media.  The scope of the project for this class has the following features:

1. Creation of a virtual photo album in an android device 
2. Using the device camera to take photos for an event
3. GPS locations tracker for the photo taken
4. Speech recording and translation to text with the ability to modify/edit entered text
5. Photo album viewer with the option to visualize in a map for all the places visited in case of a trip or jorney

Eventually the app will output the photo album with recorded text and speech to a virtual reality experience using  wearable device lenses and/or synched to a smart TV app with 3D virtual and argumented reality capabilities to view the presentation but these features are out side the scope for the class project. 

## Key Android libraries used

I have experiemented with the following android libraries which are key components to this project: 
* Android speech
* Android Locations using Google Play Services ( 7.5.0 )
* Camera related libraries TBD

## Main Use case

A typical use case for this app is the creation of an album with photos and their corresponding descriptions. Each photo that is taken at a specific place can be given some description and the geolocation of the place where the photo is being taken. The description of the photo can be entered using speech recognition which is recorded in the album and translated to text.  Since geolocation information is being recorded, a map graph can be renedered to visualize different trips and their corresponding places visited.  

## Proof of Concepts developed for this project.

Before starting working on this project, I wanted to ensure that the key features in the app are duable in the time frame given for the completion of the class.  I have experimented with android speech and google play location services:

* https://github.com/aeggermont/poc-androidspeech
* https://github.com/aeggermont/poc-geolocation-googleplay

## Mockups and wireframews for the app

The app built will be compatible with tablet Google Nexus 7 (
