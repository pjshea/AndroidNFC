# AndroidNFC
NFC sharing app in Android Environment

![Activity Diagram](http://yuml.me/bd9d42d5.png)
https://yuml.me/diagram/activity/draw

	(start)->(Main Activity)-><a>[done]->(end),<a>[send pic]->(Send Picture)->(Display Message),<a>[send message]->(Send Message)->(Display Message),<a>[send contact]->(Send Contact)->(Display Message),(Display Message)->(Main Activity)


Configure following before running :
1. Please hardcode the file name until we get UI:

	In MainActivity.java,
	In class "FileUriCallback" ---> String transferFile = "xxxx";

2. In AndroidManifest.xml, please change android:minSdkVersion as per your device's version.
	Please note: NFC is supported only for Android 4.1 or higher
	
Currently, only pictures from this location (/storage/emulated/0/Pictures) are available for transfer.

Next Steps:

- [ ] 1. Create UI for selecting pictures to share.
- [ ] 2. Create UI for selecting messages to share.
- [ ] 3. Create UI for selecting contacts to share.
- [ ] 4. Developing backend code to share messages.
- [ ] 5. Developing backend code to share contacts.




