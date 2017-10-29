package no.kantega.tdc2017.push;

import java.security.PublicKey;

public class Subscription {

    private String endpoint;

    private PublicKey clientKey;

    private byte[] sharedSecret;

    public Subscription(String endpoint, PublicKey clientKey, byte[] sharedSecret) {
        this.endpoint = endpoint;
        this.clientKey = clientKey;
        this.sharedSecret = sharedSecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public PublicKey getClientKey() {
        return clientKey;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }
}
