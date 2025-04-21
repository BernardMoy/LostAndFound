# Lost and Found Mobile App

A lost and found mobile application for the third year dissertation project at the University of Warwick. It mainly provides these extra features compared to existing applications:

- Comprehensive item details, including images, category and location
- Searching items that allows users to see a list of matching items
- The images of the items are used for comparison through a neural network model

# Features

- User account creation with university emails
- Uploading lost and found items with comprehensive details
- After reporting a lost item, users can search for similar found items
- Claim an item and the found user can approve claims
- Image recognition is used for the general scoring algorithm to compare two items
- Chat with each other in app and receive push notifications
- Receive push notifications when there is a change to the status of user's items
- A UI following usability principles
- Accessibility settings to assist users with special needs
- Admin system to allow other users to be reported

# Deployment
This is an app mainly developed for Android devices, so it can be run in an emulator in Android Studio with the following steps:

1. Open the project in Android Studio
2. Perform a Gradle sync
3. Run on the following emulator:

Android Studio version: `Android Studio Ladybug Feature Drop | 2-24.2.2`
Emulator: `Medium Phone API 35`

# Secrets
Include the ```SENDER_EMAIL```, ```SENDER_PASSWORD```, ```MAPS_API_KEY```, the ```google-services.json``` and the ```service-account.json``` for the full functionality.
