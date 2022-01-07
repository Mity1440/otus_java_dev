package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;
    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {

        ObjectMapper objectMapper = new ObjectMapper();
        String oblectInJSONString = null;

        try {
            oblectInJSONString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new FileProcessException("Error during serialization");
        }

        try {
            Files.writeString(Paths.get(fileName), oblectInJSONString);
        } catch (IOException e) {
            throw new FileProcessException("Error during writing file");
        }


    }
}
