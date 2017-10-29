package no.kantega.tdc2017.keygen;

import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.security.*;
import java.util.Base64;

/**
 * This class will generate a pair of encryption keys for using with Web Push.
 */
public class GenerateKeys {

    public static void main(String[] args) {

        // Using BouncyCastle to make it easier to generate the right keys
        Security.addProvider(new BouncyCastleProvider());

        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime256v1");

        KeyPairGenerator g;
        try {
            g = KeyPairGenerator.getInstance("ECDSA", "BC");
            g.initialize(ecSpec, new SecureRandom());
        } catch (GeneralSecurityException e) {
            System.err.println("Failed to generate keys: " + e.getMessage());
            return;
        }
        KeyPair keys = g.generateKeyPair();

        Base64.Encoder encoder = Base64.getEncoder();

        byte[] privateKeyBytes = Utils.savePrivateKey((ECPrivateKey) keys.getPrivate());
        String privateKeyBase64 = new String(encoder.encode(privateKeyBytes));
        System.out.println("Private key = " + privateKeyBase64);


        byte[] publicKeyBytes = Utils.savePublicKey((ECPublicKey) keys.getPublic());
        String publicKeyBase64 = new String(encoder.encode(publicKeyBytes));
        System.out.println("Public key = " + publicKeyBase64);
    }
}
