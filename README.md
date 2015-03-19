# Bolscript #
Bolscript is a free and open source program for **entering, displaying and managing tabla compositions**. Compositions are entered in a textbased notation and the tabla sequences are displayed on the tal, also in devanagari.

![http://oudbrothers.de/bolscript/screenshots/0.1/all.png](http://oudbrothers.de/bolscript/screenshots/0.1/all.png)

[More screenshots...](https://github.com/hannesold/bolscript/blob/wiki/MoreScreenshots.md)

# State of this project #
This was exported from code.google.com/p/bolscript, where I last maintained it in 2011.

By magnificent zombie super powers, this software is still operational (long live java vm ^^) but is currently not under further development.

I would greatly appreciate any developer who wants to contribute, take over the project, or adopt it for modern platforms. Any tabla or indian music experience would be of course very helpful.
Just write me an email to [bolscript@gmail.com](mailto:bolscript@gmail.com)

## Install on Windows ##
[Download](http://code.google.com/p/bolscript/downloads/list), Unzip, then move the .exe and the .jar file anywhere you like, for example to c:\programs\bolscript\ or so. Start the program by double clicking the .exe. If you want to have a link to the program on your desktop or in your startmenu you will have to add it manually. _I have tested it on Win XP and Win 7_

## Install on Mac ##
[Download](http://code.google.com/p/bolscript/downloads/list), open the dmg and drag the Bolscript.app application in your applications folder. _I have tested it from Mac OS X 10.6 and 10.10_

## Install on Linux ##
You can download the windows version and forget about the .exe. Launch the jar in some way (java -jar bolscript.jar...).
_I have tested it on Xubuntu_

### System requirements ###
Basically any system with an installed minimum [Java Version 6](http://java.com/de/download/) should be ok.

# How to enter compositions #
Check out the wiki page about [bolscript syntax](https://github.com/hannesold/bolscript/blob/wiki/syntax.md).<br />
Please note that the wiki is still _very fresh and incomplete_, I might add things when I have time.

# How to share compositions #
### Share individual compositions via email ###
You can either drag&drop any composition from your bolscript library view to your email program or file system to send a copy, or use right-click "reveal in finder" / "show in windows explorer" to let your file manager show you where the file lies. If you want to import a file to bolscript, just drag it on your library, or place it in your compositions folder and choose File -> Refresh library.
Alternatively, you can open a composition, and save is as pdf File -> Save as Pdf, if you want to send only the resulting layouted composition.

### Use dropbox to keep compositions synchronised over multiple devices ###
Sign up for http://dropbox.com and install the client apropriate for your operating system. Place your bolscript library in you dropbox folder to share everything. Or: you can also share a subfolder within you compositions library with your multiple devices, or friends. For this, you need to create a new subfolder, say "shared via dropbox" in your bolscriptlibrary/compositions folder, and then create a linked folder in your dropbox folder, say "shared bolscript compositions". In OS X /linux open a Terminal and use
```
ln -s "/path/to/bolscriptlibrary/compositions/shared via dropbox"  "~/Dropbox/shared bolscript compositions"
```
In Windows open Command Line and use
```
mklink /D "C:\Users\Steve\Documents\Dropbox\shared bolscript compositions" "C:\Path\To\bolscriptlibrary\compositions\shared via dropbox" 
```
You can set up sharing of your dropbox subfolder on http://dropbox.com.
More on these topics: http://lifehacker.com/5154698/sync-files-and-folders-outside-your-my-dropbox-folder, http://www.dropbox.com/help/12

## Contributors ##
Many thanks to Thomas K. for conceptual work and defining the bol base, thanks to Jatinder Thakur for bols and the sample kaida which is included.

## Feedback ##
Just drop me a mail, I'll be glad to help you out! [bolscript@gmail.com](mailto:bolscript@gmail.com)
