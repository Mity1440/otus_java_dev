package ru.otus.listener.homework;

import ru.otus.security.model.Message;

import java.util.Optional;

public interface HistoryReader {

    Optional<Message> findMessageById(long id);
}
