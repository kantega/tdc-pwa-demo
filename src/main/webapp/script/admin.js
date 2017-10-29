
// Send new messages from admin console
document.getElementById("sendMessage").addEventListener("click", function(event) {
   event.target.disabled = true;
   var message = document.getElementById("message").value;

   var params = new URLSearchParams();
   params.append("message", message);

   fetch("../api/messages", {
       method: "POST",
       body: params
   }).then(function (response) {
       if (response.ok) {
           console.debug("Message sent to server");
           document.getElementById("message").value = "";
       } else {
           console.error("Message caused error " + response.status);
       }
   }).catch(function (error) {
       console.error("Failed to send message: ", error);
   }).then(function () {
       event.target.disabled = false;
   })

});