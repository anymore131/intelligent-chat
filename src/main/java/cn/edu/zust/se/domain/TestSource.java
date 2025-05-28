package cn.edu.zust.se.domain;

import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;

import java.io.InputStream;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.lang.String.format;

public class TestSource implements DocumentSource {

    public static final String SOURCE = "source";

    private final InputStream inputStream;
    private final String fileName;

    public TestSource(InputStream inputStream, String fileName) {
        this.inputStream = ensureNotNull(inputStream, "inputStream");
        this.fileName = ensureNotBlank(fileName, "fileName");
    }

    @Override
    public InputStream inputStream() {
        return inputStream;
    }

    @Override
    public Metadata metadata() {
        return Metadata.from(SOURCE, format("%s", fileName));
    }
}