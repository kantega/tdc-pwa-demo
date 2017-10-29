package no.kantega.tdc2017.push;

import nl.martijndwars.webpush.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Servlet for storing subscriptions.
 */
@WebServlet("/api/subscribe")
public class RegistrationServlet extends HttpServlet {


    /**
     * Save a new push subscription.
     *
     * @param req  HTTP request
     * @param resp HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String endpoint = req.getParameter("endpoint");
        String clientKeyStr = req.getParameter("clientKey");
        String sharedSecret = req.getParameter("sharedSecret");

        if (endpoint == null || clientKeyStr == null || sharedSecret == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PublicKey clientKey;
        try {
            clientKey = Utils.loadPublicKey(clientKeyStr);
        } catch (GeneralSecurityException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getClass() + ": " + e.getMessage());
            return;
        }

        Base64.Decoder decoder = Base64.getDecoder();
        SubscriptionStore.addSubscription(new Subscription(endpoint, clientKey, decoder.decode(sharedSecret)));
    }
}
