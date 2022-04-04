# TravelerWeatherApp
TravelerWeatherApp V1 - Kotlin Project

## What is this?
This was a project developed in Kotlin for a technical test I had in an interview for a position as an Android Developer. 
This is my first big project I developed in Kotlin since I normally program using java.

## What were the requirements?

>The tourism ministry wants to keep travelers informed of the weather at the main
>capitals of the world. For this they want to create an app where the user can visualize
>the location of the capitals on a map with their current weather status (temperature,
>humidity, cloudiness and wind), an icon representing the weather and local time.
>
>They also want to show that same information for the location of the traveler. Each
>user must identify themselves prior to using the app by entering the following
>information: 
>* Name
>* Email 
>* Date of birth. 
>
>Since the most intrepid travelers go to places without WIFI connection, the latest information displayed must be available
>offline. Additionally, the user can search the same information for cities that are not in the initial load.
>
>For development the ministry have the API
>[openweathermap.org] (https://openweathermap.org/).
>
>Observations:
>* The registration data is kept in the session only (there is no service available).
>* The minSdk of the app will be 21 for android the min taget 11 for iOS.
>* The minimum capitals to show are 5.
>* The user's location must be as accurate as possible.
>
>Additional:
>* Use images of cities.
>* Add animations to the map pins.
>* Use Google Places to ensure the validity of the cities given by the user.
>* Validate the registration data.

## What languages, technologies and libraries were used for this app?
* Kotlin
* MVVM Architecture
* Room Database, Repositories and DAO
* API Requests to OpenWeatherMap
* Custom Markers for Google maps

## How to use?
* Modify the API_KEY and use your personal Google API Key for Google Places to work

## Preview
<img src="https://user-images.githubusercontent.com/5034892/161454799-ad83f13c-04dc-4ae4-a269-939ede9657a0.jpeg" width="300" height="600"> <img src="https://user-images.githubusercontent.com/5034892/161454823-e7599f6f-a082-4d02-baec-f3c231697f6c.jpeg" width="300" height="600"> <img src="https://user-images.githubusercontent.com/5034892/161454828-575edbb4-38b3-417c-a5d8-ad084b20059e.jpeg" width="300" height="600">


