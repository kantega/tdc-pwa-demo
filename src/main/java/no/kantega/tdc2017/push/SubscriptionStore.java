package no.kantega.tdc2017.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubscriptionStore {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionStore.class);

    private static List<Subscription> subscriptions = new ArrayList<>();

    public static void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        log.info("Subscription added for URL " + subscription.getEndpoint());
    }

    public static Collection<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
