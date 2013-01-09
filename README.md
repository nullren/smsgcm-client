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

## How do I install?

The most important thing is creating your SSL client certificate. I am
the CA for my server, so if you want to compile and use this app, you
must first send me your csr. Otherwise, this is useless unless you
implement your own server.

### Set Up PKCS12 Keystore

First step is to create your private key and certificate signing 
request. This can be done in one command:

    openssl req -new -newkey rsa:2048 -nodes \
        -keyout key.pem -out csr.pem

Then send me your `csr.pem`, be sure to use your correct email.
After I mail you back your certificate, eg `cert.pem`, you can
then create the p12 file:

    openssl pkcs12 -export -out my.p12 \
        -inkey key.pem -in cert.pem

Remember your password as you will need it.

### Compiling

APIs 10 and 17 are required, as is the GCM API and Android Support
Library. I think that is it.

You then need to copy your `my.p12` file into `res/raw/` naming it
`key_store.p12`.

You should then be able to install
the app onto your phone with `ant debug install`.

### Running

You will likely need to input the password you used when setting
up your PKCS12 keystore. You can set it by going to the program
settings and inputting it. Unregistering, then Re-registering
should be enough to get you reconnected.

## Where is the server?

This app requires a server to be the central hub of all the clients.
It is a Tomcat web application and it can be found [here](https://bitbucket.org/nullren/smsgcm-server).

