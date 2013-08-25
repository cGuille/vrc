```
___                   _____      _____ 
\  \                 /  /| |    / ____|
 \  \               /  / | |   | |    
  \  \             /  /  | |   | | 
   \  \           /  /   | |   | | 
    \  \         /  /    | |___| |____ 
     \  \       /  /     |______\_____|
      \  \     /  /        _            ____                      _          ____            _             _ 
       \  \   /  /    ___ (_) ___ ___  |  _ \ ___ _ __ ___   ___ | |_ ___   / ___|___  _ __ | |_ _ __ ___ | |
        \  \ /  /    / _ \| |/ __/ _ \ | |_) / _ \ '_ ` _ \ / _ \| __/ _ \ | |   / _ \| '_ \| __| '__/ _ \| |
         \  V  /    | (_) | | (_|  __/ |  _ <  __/ | | | | | (_) | ||  __/ | |__| (_) | | | | |_| | | (_) | |
          \___/      \___/|_|\___\___| |_| \_\___|_| |_| |_|\___/ \__\___|  \____\___/|_| |_|\__|_|  \___/|_|

                      
```
    
VRC (VLC Voice Remote Control) is an Android App to remotely control VLC over HTTP with your voice.

It means that you say orders to your Android phone, and the media played on your computer with VLC (should) obey.

### Usage:
Please note that this application is not ready for release yet, especially when it comes to user interface.

#### On your computer
Start VLC, and open a media file.

##### Enable the VLC web interface

###### The quick way:
Hit `View` in the menu bar, then choose `Add interface` → `Network`.

It is the easiest way to do it, but you will have to repeat that every time you start VLC. To make this setting permanent, check out the second solution.

###### The **recommended way**: 
 * hit `Tools` → `Preferences` in the menu bar;
 * check the `All` radio button, in the left bottom corner;
 * click on the `Main interfaces` item in the list on the left;
 * check the `Web` checkbox and hit the `Save` button.

That's it, you're done about this.

##### Allow external devices to access VLC HTTP interface
To do so, you will need to edit the VLC .hosts file. Depending on your operating system, this file is located:
 * on **Windows**: C:\Program Files (x86)\VideoLAN\VLC\lua\http\.hosts (use “Program Files” instead of “Program Files (x86)” on 32-bit versions of Windows.);
 * on **Mac OS X**: /Applications/VLC.app/Contents/MacOS/share/lua/http/.hosts
 * on **GNU/Linux**: /usr/share/vlc/lua/http/.hosts

Open it with a text editor. *Note that you may need to run your text editor with administrator privileges in order to edit this file*.

You basically need to uncomment (i.e. remove the heading `#` from) those two lines: `fec0::/10` and `192.168.0.0/16`. Your file now should look like this:

```
#
# Access-list for VLC HTTP interface
# $Id$
#

# localhost
::1
127.0.0.1

# link-local addresses
#fe80::/64

# private addresses
#fc00::/7
fec0::/10
#10.0.0.0/8
#172.16.0.0/12
192.168.0.0/16
#169.254.0.0/16

# The world (uncommenting these 2 lines is not quite safe)
#::/0
#0.0.0.0/0
```
Then you're done.

**Note that for basic usage, your computer and your Android Phone *must* be connected to the same network** (e.g. the same router, Freebox or whatever).

#### On your Android device
Once installed, run the application, go to `Menu` → `Settings`, and set up the two configuration items:
 * **Host IP**: here write the **local** IP address of the computer running VLC. That means that it is not the IP displayed on whatismyip.com etc., but the IP you can find in your operating system network settings ([look here to learn how to find it](http://kb.iu.edu/data/aapa.html#web)). If it is an IPv4 address, it probably looks like "192.168.0.X". Just change the last number accordingly.
 * **VLC HTTP port**: if you have not changed the VLC settings, you probably don't need to change this setting.

Now you can go back to the app. When you want to give an order to VLC, hit the `Start listening` button and just say the command you want. Wait a few seconds to see if it worked. You should see a little message telling you what happened fading in and out.


### Currently supported commands (french only):
 * Command name [French command name] - (details/détails): « Sentence to say to run the command », « Phrase à prononcer pour lancer la commande »
 * Play / Pause [Lecture / Pause]: « Commencer la lecture », « Reprendre la lecture », « Mettre en pause »;
 * Redo [Refaire] - (run the last command again/ré-exécute la dernière commande): « Encore », « Encore une fois »;
 * Stop [Arrêter]: « Arrêter », « Stop »;
 * Previous [Précédent]: « Précédent »;
 * Next [Suivant]: « Suviant »;
 * Restart [Recommencer] - (stop + play): « Recommencer », « Recommencer la lecture »;
 * Volume up [Monter le son]: « Augmenter le volume », « Monter le son »;
 * Volume down [Baisser le son]: « Diminuer le volume », « Baisser le son »;
 * Mute [Muet]: « Couper le son »;
 * Reset volume [Ré-initialiser le volume sonore] - (set back the volume to 100%/remet le volume à 100%): « Remettre le son »;
 * Stop [Arrêter]: « Arrêter », « Stop »;
 * Toggle fullscreen [Basculer le mode plein écran] : « Basculer le mode plein écran », « Entrer en mode plein écran », « Sortir du mode plein écran »;

### Future improvements:
 * Implement playlist management commands;
 * Implement a feature that automatically checks if the VLC HTTP interface is reachable;
 * Create a nice GUI;
 * Internationalization.
