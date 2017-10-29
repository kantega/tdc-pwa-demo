
// Register service worker when main page loads
window.addEventListener("DOMContentLoaded", function() {
    if ("serviceWorker" in navigator) {
        console.info("Starting ServiceWorker registration");

        navigator.serviceWorker.register("./serviceworker.js").then(function(registration) {
            console.info("ServiceWorker registration successful with scope: ", registration.scope);
        }).catch(function(err) {
            console.error("ServiceWorker registration failed: ", err);
            var footer = document.getElementById("indexFooter");
            footer.textContent = "Nettleseren din støtter service workers, men installasjon feilet.";
            footer.classList.add("error");
        });
    } else {
        console.warn("ServiceWorker is not supported in browser, you're missing out.");
        var footer = document.getElementById("indexFooter");
        footer.textContent = "Nettleseren din støtter ikke service workers.";
        footer.classList.add("warning");
    }
});
