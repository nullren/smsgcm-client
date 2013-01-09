SMSGCM
------

This is just a quick little thing based off of 
[Google's GCM Demo](http://developer.android.com/google/gcm/demo.html) application.

Apache License included in their demo application:


## What is this?

This is a stupid project I started so that one day I would be able to
send and receive text messages from inside bitlbee. The first step was
to create an application on the phone to send incoming text messages
to the clients and to gather messages sent from the clients and send
them over the air to my SMS recipients. There are other tools in
existence that do this already.

Things that make this unique are that I use Google Cloud Messaging to
notify the phone of messages to be sent. Also, user accounts are
handled by SSL certificates and revocation lists. This way, I don't
have to deal with any authentication stuff.

## Where is the server?

This app requires a server to be the central hub of all the clients.
It is a Tomcat web application and it can be found [here](https://bitbucket.org/nullren/smsgcm-server).

## How do I install?

The most important thing is creating your SSL client certificate. I am
the CA for my server, so if you want to compile and use this app, you
must first send me your csr. Otherwise, this is useless unless you
implement your own server.
