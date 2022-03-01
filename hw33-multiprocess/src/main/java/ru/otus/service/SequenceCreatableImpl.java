package ru.otus.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SequenceCreatableImpl implements SequenceCreatable{

    @Override
    public List<Integer> generateSequence(int begin, int end) {

        return Collections.unmodifiableList(IntStream
                                             .range(begin, end)
                                             .boxed()
                                             .collect(Collectors.toList()));

    }

}
