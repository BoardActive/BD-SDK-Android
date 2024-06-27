# BrandDrop SDK Android

<img src="https://github.com/BoardActive/BD-SDK-Android/assets/108806504/32d7a6b0-afff-4bbc-ab9b-990c3fd27848?s=200&v=4" width="96" height="96"/>

### Location-Based Engagement
- Enhance your app. Empower your marketing.
    
- It's not about Advertising... It's about "PERSONALIZING"

- BrandDrop's platform connects brands to consumers using location-based engagement.

- Our international patent-pending Visualmatic™ software is a powerful marketing tool allowing brands to set up a virtual perimeter around any location, measure foot-traffic, and engage users with personalized messages when they enter geolocations

- Effectively attribute campaign efficiency by seeing where users go after the impression! 

- Use your BoardActive account to create Places (geo-fenced areas) and Messages (notifications) to deliver custom messages to your app users.


[Click Here to get a BranDrop account](https://app.branddrop.us/login)

The BrandDrop SDK will use a device's location to know when an app user passes into a geo-fence. Passing into a geo-fence can trigger an event allowing you to deliver notifications to your app users.  

## Installing the BranDrop SDK

BrandDrop for Android supports APK 15 and greater. 

To get the most our of the BrandDrop SDK your app will need to allow location permissions. 

To use the BrandDrop SDK your will need to implement our library. 

### SDK
Include the BrandDrop SDK into your project with JitPack repository.

### Dependency

We use JitPack as a repository service, you will add a few lines to the gradle files to import our SDK into your project. (Instructions for Maven, sbt, and leiningen available upon request)

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
    	classpath 'com.android.tools.build:gradle:8.1.4'
    	classpath 'com.google.gms:google-services:4.4.2' 
    }
}

// Add JitPack repository to top level build.gradle
	allprojects {
		google()
        	mavenCentral()
			repositories {
				...
				maven { url 'https://jitpack.io' }
		}
	}
```

Include the following to your App-level build.gradle

```java
...
dependencies {
    ...
    // This line imports the BrandDrop-Android to your project.
    implementation 'com.github.BoardActive:BD-SDK-Android:3.1.0'
    ...
}
```

## Download Example App Source Code
There is an example app provided [here](https://github.com/BoardActive/BD-App-Android) for Android.

## Ask for Help

Our team wants to help. Please contact us 

* Help Documentation: [http://help.boardactive.com/en/](http://help.boardactive.com/en/)
* Call us: [(657)229-4669](tel:+6572294669)
* Email Us [taylor@boardactive.com](mailto:taylor@boardactive.com)
* Online Support [Web Site](https://www.boardactive.com/)
