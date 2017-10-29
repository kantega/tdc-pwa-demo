package no.kantega.tdc2017.push;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.security.Security;

/**
 * Web app startup class.
 */
@WebListener
public class ApplicationStartup implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

    /**
     * Register BouncyCaste cryptography provider on startup.
     *
     * @param sce Startp event
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Security.addProvider(new BouncyCastleProvider());
        log.info("Added BouncyCastle provider");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { }
}
