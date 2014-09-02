Casper
======
Casper is a Clojure web application that provides a friendly, easy way to create one-time secrets.  Create a secret, get a unique URL that returns your secret once.  The next time the URL is queried (GET), the secret is gone.

Your secret can be anything you want.  Plain text, JSON, a private key or a public key.  Anything you can put into a POST.

Your secret is safe with us until it is viewed with the one-time URL.  We encrypt (AES 256) it and save it until you retreive it.  Once you retrieve it, we delete it.

API
===

* **GET /create** 

Returns HTML for a simple form for creating your secret.  Click "create" and you get a one-time URL for displaying your secret.

* **POST /create**

Used to create your secrets from a script.  Use the *secret* form variable to transmit your secret. Here is an example with `curl:`

`curl -v --data-urlencode "secret=your personal secret"`"

**201** - Secret created and one-time password URL returned.

**404** - Missing *secret* form name

Your unique one-time URL returned from the `curl` call.

Developers
==========
1. Get leiningen (the awesome clojure project automation tool), 
2. Clone the repository
3. lein run "a port number"

Thanks
======
Thanks to Tommy Bishop for the great idea.



