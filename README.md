Casper
======
Casper is a Clojure web application that provides a friendly, easy way to create one-time secrets.  Create a secret, get a unique URL that returns your secret once.  The next time the URL is queried (GET), the secret is gone.

Your secret is safe with us until it is viewed with the one-time URL.  We encrypt it and save it until you retreive it.  Once you retrieve it, we delete it.

API
===

* GET /create 

Returns HTML for a simple form for creating your secret.  Click "create" and you get a one-time URL for displaying your secret.

* POST /create

Used to create your secrets from a script.  Use the *secret* form variable to transmit your secret. Here is an example with `curl:`

`curl -v --data-urlencode "secret=your personal secret"`"

Your unique URL returned from the `curl` call.

Thanks
======
Thanks to Tommy Bishop for the great idea.



