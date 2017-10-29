package no.kantega.tdc2017.push;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageStore {

    private static List<String> messages = new ArrayList<>();

    static {
        messages.add("Hei TDC!");
    }

    public static void addMessage(String message) {
        messages.add(message);
    }

    public static Collection<String> getMessages() {
        return messages;
    }
}
