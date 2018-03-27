# AndroidNFC
NFC sharing app in Android Environment

Configure following before running :
1. Please hardcode the file name until we get UI:

	In MainActivity.java,
	In class "FileUriCallback" ---> String transferFile = "xxxx";

2. In AndroidManifest.xml, please change android:minSdkVersion as per your device's version.
	Please note: NFC is supported only for Android 4.1 or higher
	
Currently, only pictures from this location (/storage/emulated/0/Pictures) are available for transfer.

Next Steps:

1. Create UI for selecting pictures, messages and contacts to share.
2. Making backend code generic enough to share data.




