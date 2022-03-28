package ru.otus.dataprocessor;

import ru.otus.security.model.Measurement;

import java.io.IOException;
import java.util.List;

public interface Loader {

    List<Measurement> load() throws IOException;
}
