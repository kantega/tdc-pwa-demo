// Version for cache versioning and logging
const version = "v13";

// Content cache name
const CACHE_NAME = "content-cache-" + version;

// List of content URLs to cache. Note that "feedback.html" is not included, as it should only work online.
const urlsToCache = [
    "./",
    "./index.html",
    "./pages/offline.html",
    "./pages/messages.html",
    "./pages/program.html",
    "./styles.css",
    "./script/demo.js",
    "./script/messages.js"
];

// Create cache on install
self.addEventListener("install", function(event) {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(function(cache) {
                console.info("ServiceWorker caching files");
                // Cache all files in list. If any of the URLs fail then service worker install will fail.
                return cache.addAll(urlsToCache);
            })
    );
});

// Intercept network events and check if we can use cache
// The "event" object here (and above) is of type FetchEvent (
self.addEventListener("fetch", function(event) {
    // Use browser default for requests that are not GET
    if (event.request.method !== "GET") {
        console.debug("Not using cache for " + event.request.method + " request for " + event.request.url);
        return;
    }

    // Use cache for GET if possible
    event.respondWith(
        // Check cache status
        caches.match(event.request)
            .then(function(response) {
                    // Cache promise will always return success, but "response" is null if not in cache
                    if (response) {
                        // Cache hit
                        console.debug("Returning from SW cache: " + event.request.url);
                        return response;
                    } else {
                        // Not in cache, try to get resource from network
                        console.debug("Not in cache: " + event.request.url);
                        return fetch(event.request);
                    }

                }
            )
            .catch(function(error) {
                // This will catch errors from "fetch(event.request)" above, i.e. if server or network is offline
                console.info('Error, ', error);
                return caches.match('./pages/offline.html');
            })
    );
});

self.addEventListener("activate", function (event) {
    console.info("ServiceWorker activated " + version);

    // Delete old caches
    event.waitUntil(
        caches.keys().then(function(keyList) {
            return Promise.all(keyList.map(function(key) {
                if (key !== CACHE_NAME) {
                    console.info("Deleting old cache " + key);
                    return caches.delete(key);
                }
            }));
        })
    );
});

self.addEventListener("push", function(event) {

    if (event.data) {
        var data = event.data.text();
        console.debug("Showing notification for push data: ", data);

        self.registration.showNotification("Melding fra TDC", {
            body: data,
            icon: "./images/logo.png"
        })
    } else {
        console.warn("Push event with no data");
    }
});
