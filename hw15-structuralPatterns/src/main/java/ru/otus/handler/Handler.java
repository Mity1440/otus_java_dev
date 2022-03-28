package ru.otus.handler;

import ru.otus.security.model.Message;
import ru.otus.listener.Listener;

public interface Handler {
    Message handle(Message msg);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
