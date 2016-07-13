# Pipette

Pipette is an Android iMessage client for [MessageBridge](https://github.com/bsharper/MessageBridge).

Working Features:

 * Listing conversations
 * Sending and receiving text messages
 * Receiving image messages
 * Sending and receiving group messages

## Installation

Prereqs:
 * Mac with iMessage installed
 * [Android Studio](https://developer.android.com/studio/index.html)
 * [node.js](http://nodejs.org)
 * [npm](http://nodejs.org)
 * Android device running Android 4.0.3 or higher
 * git

To get started, `git clone` this repository, and run `git submodule update --init` to clone the server component. Then, `cd` into the `server` folder, and run `npm install`. Once this is complete, run `node app.js`

Open the repository with Android Studio, and run the app. In the overflow menu on the upper right hand corner of the app, touch "Settings". You will need to enter in your Server IP, which is the IP of the computer you ran `node app.js` above. Enter the server port, which is 3000.

Then, restart the app and you should see all your iMessage conversations!

---
Thanks to [bsharper](https://github.com/bsharper) for writing the node.js server!

License: AGPL