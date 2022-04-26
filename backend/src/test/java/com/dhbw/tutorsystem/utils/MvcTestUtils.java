package com.dhbw.tutorsystem.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class MvcTestUtils {

    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private String basePath;

    public MvcTestUtils(String basePath, MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.basePath = basePath;
    }

    public MvcResult perform(RequestType requestType, String relativePath, HttpStatus expectedHttpResponseValue)
            throws Exception {
        return performRequest(getRequestBuilder(requestType, basePath + relativePath), null,
                expectedHttpResponseValue);
    }

    public MvcResult perform(RequestType requestType, String relativePath, Object request,
            HttpStatus expectedHttpResponseValue)
            throws Exception {
        return performRequest(getRequestBuilder(requestType, basePath + relativePath), request,
                expectedHttpResponseValue);
    }

    public MvcResult performExpectException(RequestType requestType, String relativePath, Object request,
            HttpStatus expectedHttpResponseValue,
            String expectedMessage, Class<?> expectedErrorClass) throws Exception {
        return performRequestExpectException(getRequestBuilder(requestType, basePath + relativePath), request,
                expectedHttpResponseValue,
                expectedMessage,
                expectedErrorClass);
    }

    private MockHttpServletRequestBuilder getRequestBuilder(RequestType requestType, String path) {
        switch (requestType) {
            case GET:
                return get(path);
            case POST:
                return post(path);
            case PUT:
                return put(path);
            case DELETE:
                return delete(path);
            default:
                return null;
        }
    }

    private MvcResult performRequest(MockHttpServletRequestBuilder builder, Object request,
            HttpStatus expectedHttpResponseValue) throws Exception {
        MvcResult result = mvc.perform(builder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andReturn();
        // check http result code matches
        assertEquals(expectedHttpResponseValue.value(), result.getResponse().getStatus());
        return result;
    }

    private MvcResult performRequestExpectException(MockHttpServletRequestBuilder builder, Object request,
            HttpStatus expectedHttpResponseValue,
            String expectedMessage, Class<?> expectedErrorClass) throws Exception {
        MvcResult result = performRequest(builder, request, expectedHttpResponseValue);
        // check exception properties
        assertEquals(expectedMessage,
                new JSONObject(result.getResponse().getContentAsString()).getString("message"));
        assertTrue(expectedErrorClass.isInstance(result.getResolvedException()));
        return result;
    }
}
