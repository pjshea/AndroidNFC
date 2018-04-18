# AndroidNFC
NFC sharing app in Android Environment

![Activity Diagram](http://yuml.me/bd9d42d5.png)
https://yuml.me/diagram/activity/draw

	(start)->(Main Activity)-><a>[done]->(end),<a>[send pic]->(Send Picture)->(Display Message),<a>[send message]->(Send Message)->(Display Message),<a>[send contact]->(Send Contact)->(Display Message),(Display Message)->(Main Activity)


Configure following before running :
1. In AndroidManifest.xml, please change android:minSdkVersion as per your device's version.
	Please note: NFC is supported only for Android 4.1 or higher

Dynamic selection of single image and the transfer is implemented.
	
Next Steps:

- [x] 1. Create UI/code for selecting pictures from the file explorer to share. - Parth
- [ ] 2. Create UI/code for selecting messages to share - Pat
- [ ] 3. Create UI/code for selecting contacts to share - Pat
- [ ] 4. Developing backend code to share messages - Pat
- [ ] 5. Developing backend code to share contacts - Sichen
- [x] 6. Developing backend code to share pictures - Parth




