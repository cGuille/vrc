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

### Usage:
Please note that this application is not ready for release yet, especially when it comes to user interface.

#### On your computer
Start VLC. **Note that your computer and your Android Phone *must* be connected to the same network** (e.g. the same router, Freebox or whatever).

Click on "View > Add Interface > Web".

#### On your Android device
Once installed, run the application and go to "Menu > Settings", and set up the two configuration items:
 * Host IP: here write the **local** IP address of the computer running VLC. That means that it is not the IP displayed on whatismyip.com etc., but the IP you can find in your operating system network settings. If it is an IPv4 address, it probably looks like "192.168.0.X". Just change the last number accordingly.
 * VLC HTTP port: if you have not changed the VLC settings, you probably don't need to change this setting.

Now you can go back to the app. When you want to give an order to VLC, hit the "listen" button and just say the command you want. Just wait one or two seconds to see if it worked. There is no "in app" feedback yet but it is planned.


### Currently supported commands (french only):
 * Command name [French command name] (details/détails): « Sentence to say to run the command », « Phrase à prononcer pour lancer la commande »
 * Play / Pause [Lecture / Pause]: « Commencer la lecture », « Reprendre la lecture », « Mettre en pause »;
 * Redo [Refaire] (run the last command again/ré-exécute la dernière commande): « Encore », « Encore une fois »;
 * Stop [Arrêter]: « Arrêter », « Stop »;
 * Restart [Recommencer] (stop + play): « Recommencer », « Recommencer la lecture »;
 * Volume up [Monter le son]: « Augmenter le volume », « Monter le son »;
 * Volume down [Baisser le son]: « Diminuer le volume », « Baisser le son »;
 * Mute [Muet]: « Couper le son »;
 * Reset volume [Ré-initialiser le volume sonore] (set back the volume to 100%/remet le volume à 100%): « Remettre le son »;
 * Stop [Arrêter]: « Arrêter », « Stop »;
 * Toggle fullscreen [Basculer le mode plein écran] : « Basculer le mode plein écran », « Entrer en mode plein écran », « Sortir du mode plein écran »;

### Future improvements:
 * Implement a VLC shutdown command;
 * Implement playlist management commands;
 * Create a real GUI (with **feedback**);
 * Internationalization.
