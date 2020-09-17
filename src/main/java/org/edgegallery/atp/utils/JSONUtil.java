package org.edgegallery.atp.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static String marshal(Object obj) throws IOException {
		return MAPPER.writeValueAsString(obj);
	}

	public static <T> T unMarshal(String obj, Class<T> type) throws IOException {
		return MAPPER.readValue(obj, type);
	}

	public static void main(String args[]) throws IOException {

	}
}
