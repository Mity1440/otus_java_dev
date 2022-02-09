package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.security.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> previousStates;

    public HistoryListener() {
        this.previousStates = new HashMap<>();
    }

    @Override
    public void onUpdated(Message msg) {

        try {
            previousStates.put(msg.getId(), msg.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }

    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(previousStates.get(id));
    }
}
