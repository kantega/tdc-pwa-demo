// App server's public encryption key. You'll need to replace this with your own if you run your own server
var SERVER_PUBLIC_KEY = "BMt8IwdZ6N1sUj+Ea/xuz70ciK4zcr6BDL6uMooPcRrA4RL98FB8w5RNlHgYTdvTb/F+L5Il/0BifPN1wIzhCcs=";

// Cached subscription object
var subscription = null;

// Set up page content and event listener on DOM load
document.addEventListener("DOMContentLoaded", function() {

    document.getElementById("subscribeToggle").addEventListener("change", function() {
        if (this.checked) {
            registerPushSubscription();
        } else {
            removePushSubscription();
        }
    });

    updateMessages();
    setSubscriptionStatus();
});

// Load existing messages into message page
function updateMessages() {
    function requestHandler() {
        var container = document.getElementById("messages");

        var messages;

        try {
            messages = JSON.parse(this.responseText);
        } catch (e) {
            // If parsing fails, it's most likely if we're offline and getting the fallback page
            errorHandler();
            return;
        }

        messages.forEach(function (message) {
            var p = document.createElement("p");
            p.appendChild(document.createTextNode(message));
            container.appendChild(p);
        });
    }

    function errorHandler() {
        var container = document.getElementById("messages");
        var p = document.createElement("p");
        p.setAttribute("class", "error");
        p.appendChild(document.createTextNode("Meldinger er dessverre ikke tilgjengelig nå."));
        container.appendChild(p);
    }

    // Not using Fetch API here, as we need to support older browsers
    var request = new XMLHttpRequest();
    request.addEventListener("load", requestHandler);
    request.addEventListener("error", errorHandler);
    request.open("GET", "../api/messages");
    request.send();
}

// Check status for push subscription, and update subscribe checkbox
function setSubscriptionStatus() {
    var checkBox = document.getElementById("subscribeToggle");
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistration().then(function(serviceWorkerRegistration) {
            serviceWorkerRegistration.pushManager.getSubscription().then(function (pushSubscription) {
                if (pushSubscription === null) {
                    console.info("No active push subscription");
                    subscription = null;
                    checkBox.checked = false;
                } else {
                    console.info("Has active subscription", pushSubscription.toJSON());
                    subscription = pushSubscription;
                    checkBox.checked = true;
                }
                checkBox.disabled = false;
            });
        }).catch(function (error) {
            console.error("Can't get subscription status", error);
            checkBox.setAttribute("title", "Nettleser støtter service workers, men får ikke sjekket status.");
        });
    } else {
        checkBox.setAttribute("title", "Beklager, din nettleser støtter ikke service workers.");
    }
}

// Create push subscription when asked by user
function registerPushSubscription() {
    navigator.serviceWorker.getRegistration().then(function(serviceWorkerRegistration) {
        var options = {
            userVisibleOnly: true,
            applicationServerKey: base64ToArrayBuffer(SERVER_PUBLIC_KEY)
        };

        // Send data to message server
        serviceWorkerRegistration.pushManager.subscribe(options).then(function(pushSubscription) {
            console.info("Subscription successful, sending data to server");
            subscription = pushSubscription;

            // Unique subscription URL in endpoint, and browser's encryption information on the two other parameters
            var params = new URLSearchParams();
            params.append("endpoint", pushSubscription.endpoint);
            params.append("clientKey", arrayBufferToBase64(pushSubscription.getKey("p256dh")));
            params.append("sharedSecret", arrayBufferToBase64(pushSubscription.getKey("auth")));
            fetch("../api/subscribe", {
                method: "POST",
                body: params
            }).then(function(response) {
                if (response.ok) {
                    console.info("Data sent to server");
                } else {
                    console.error("Server subscribe sent error " + response.status)
                }

            }).catch(function (error) {
                console.error("Failed to send subscription data to server: ", error);
            });
        })
        .catch(function(error) {
            console.error("Subscription failed: ", error);
        });
    });
}

// Unsubscribe from message server
function removePushSubscription() {
    subscription.unsubscribe().then(function(successful) {
        console.debug("Unsubscribed: " + successful);
    }).catch(function(error) {
        console.error("Failed to unsubscribe: ", error);
    })
}

// Convert byte array to base64
function arrayBufferToBase64(arrayBuffer) {
    var bytes = new Uint8Array(arrayBuffer);

    var str = "";
    for (var i = 0; i < bytes.length; i++) {
        str += String.fromCharCode(bytes[i]);
    }

    return btoa(str);
}

// Base64 to byte array
function base64ToArrayBuffer(base64) {
    var binaryString = atob(base64);

    var uint8 = new Uint8Array(binaryString.length);
    for (var i = 0; i < binaryString.length; i++) {
        uint8[i] = binaryString.charCodeAt(i);
    }

    return uint8;
}