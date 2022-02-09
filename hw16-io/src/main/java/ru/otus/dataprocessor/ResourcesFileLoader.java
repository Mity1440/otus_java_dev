package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.security.model.Measurement;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() throws IOException{

        List<Measurement> measurements = new ArrayList<>();

        String jsonInString = getJsonInStringFromFile();

        fillMeasurementsByJsonData(measurements, jsonInString);

        return measurements;

    }

    private String getJsonInStringFromFile() throws IOException {

        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null){
            throw new FileProcessException("no found json file");
        }

        File file = new File(url.getPath());

        String jsonInString = Files
                .readAllLines(file.toPath())
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));

        return jsonInString;

    }

    private void fillMeasurementsByJsonData(List<Measurement> measurements,
                                            String jsonInString) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        var jsonTree = mapper.readTree(jsonInString);

        jsonTree.iterator().forEachRemaining((o)->{

            if (o.get("name") == null || o.get("value") == null){
                return;
            }

            String mName = o.get("name").asText();
            double mValue = o.get("value").asDouble();

            measurements.add(new Measurement(mName, mValue));

        });

    }

}
