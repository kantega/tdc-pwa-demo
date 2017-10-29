package no.kantega.tdc2017.push;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Servlet for getting current messages, and adding new messages with notification.
 */
@WebServlet("/api/messages")
public class MessageServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MessageServlet.class);

    private PushService pushService;

    private ExecutorService executorService;

    /**
     * Load encryption keys and set up PushService from "web-push" library.
     */
    @Override
    public void init() throws ServletException {
        super.init();

        // Read encryption keys from config
        InputStream is = getClass().getResourceAsStream("/config.properties");
        Properties config = new Properties();
        try {
            config.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration: " + e.getMessage(), e);
        }

        // Initialize service from "web-push"
        try {
            pushService = new PushService(config.getProperty("publicKey"),
                                          config.getProperty("privateKey"),
                                          "https://2017.trondheimdc.no/program#11:00-5");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to initialize push service");
        }

        // Simple ExcecutorService to get asynchronous notification for clients
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Get list of current messages.
     *
     * @param req  HTTP request
     * @param resp HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        // Don't do this at home, kids: Hand-written JSON to avoid adding another dependency
        String escaped = MessageStore.getMessages().stream()
                .map(MessageServlet::escapeJson)
                .collect(Collectors.joining("\",\n\""));

        PrintWriter writer = resp.getWriter();
        writer.print("[\"");
        writer.print(escaped);
        writer.print("\"]");
    }

    /**
     * Add a new message and notify clients.
     *
     * @param req  HTTP request
     * @param resp HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = req.getParameter("message");
        if (message == null || message.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            MessageStore.addMessage(message.trim());
            resp.setStatus(HttpServletResponse.SC_CREATED);

            notifyClients(message.trim());
        }
    }

    /**
     * Notify all clients on new message.
     *
     * @param message The message to send
     */
    private void notifyClients(String message) {

        byte[] encodedMessage = message.getBytes(Charset.forName("UTF-8"));

        for (Subscription subscription : SubscriptionStore.getSubscriptions()) {
            Notification notification = new Notification(subscription.getEndpoint(),
                                                         subscription.getClientKey(),
                                                         subscription.getSharedSecret(),
                                                         encodedMessage);

            executorService.execute(() -> {
                try {
                    HttpResponse response = pushService.send(notification);
                    log.debug("Notify for {} returned {}", subscription.getEndpoint(), response.getStatusLine());
                } catch (Exception e) {
                    log.warn("Notify for {} threw execption {}: {}",
                            subscription.getEndpoint(), e.getClass().getName(), e.getMessage());
                }
            });
        }
    }

    private static String escapeJson(String input){
        return input.replace("\n", "\\n").replace("\"", "\\\"");
    }
}
