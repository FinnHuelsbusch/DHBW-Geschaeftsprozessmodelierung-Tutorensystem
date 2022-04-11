package com.dhbw.tutorsystem.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JsonPayloadTestUtils {

    private final String globalTestResourcesBaseDir = "/test-data";

    private String baseDir;
    private ObjectMapper mapper;

    public JsonPayloadTestUtils(String testResourcesInstanceDir, ObjectMapper mapper) {
        this.baseDir = globalTestResourcesBaseDir + testResourcesInstanceDir + "/";
        this.mapper = mapper;
    }

    public JsonNode getExpectedPayload(String fileName) throws IOException {
        Resource testFile = new ClassPathResource(baseDir + fileName);
        String fileContent = new String(Files.readAllBytes(Paths.get(testFile.getURI())));
        return mapper.readTree(fileContent);
    }

    public ObjectNode getMutableExpectedPayload(String fileName) throws IOException {
        return (ObjectNode) getExpectedPayload(fileName);
    }

    public void assertPayloadMatches(String expectedPayloadFilePath, String actualPayload)
            throws JsonMappingException, JsonProcessingException, IOException {
        assertEquals(getExpectedPayload(expectedPayloadFilePath), mapper.readTree(actualPayload));
    }

    public void assertPayloadMatches(ObjectNode expected, String actualPayload)
            throws JsonMappingException, JsonProcessingException, IOException {
        assertEquals(expected, mapper.readTree(actualPayload));
    }
}
