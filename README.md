# Track My Location
This is a simple android app where you can brodcast your location and everyone who has your dynamically generated unique key can track you in near real-time.

I created this project just for learning puposes particularly about firebase (firestore db), google maps api and google location api.

***
### Hot to integrate Google Maps & Location API
To use google maps in an android app you can use *SupportMapFragment* atau *MapView*

- Using google maps fragment (inside a layout or as a root tag)
 
```xml
<fragment
	android:id="@+id/map"
	android:name="com.google.android.gms.maps.SupportMapFragment"
	android:layout_width="match_parent"
	android:layout_height="match_parent"/>
```

- Then you have to add permissions and google maps api key in the *AndroidManifest.xml* file

![](images/as_1.png)
> Add permisions for coase and fine location also add meta-data tag for API key

***
### Getting API Key
- Go to the google cloud console page and click credentials submenu

![](images/ga_1.png)

- Then create a new credential


![](images/ga_2.png)


- New API key has been created, you can copy the key and paste in the manifest file. To restrict access of the key click the restrict button

![](images/ga_3.png)


- You can restrict api key usage by selecting the platform or IP address by which the api call would be requested (Application restrictions)

![](images/ga_4.png)

1. Select one of the application restrictions (in this case Android)
2. Insert your android app package name
3. Insert SHA-1 fingerprint of your app
4. Click Save button

___
> - To get SHA-1 key you can use your android studo by opening your particular project -> select signingReport gradle task -> copy & paste the SHA-1 key into the android app restriction form

![](images/as_2.png)
