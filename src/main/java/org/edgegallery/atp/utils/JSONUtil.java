package org.edgegallery.atp.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * transfer object to String.
	 * 
	 * @param obj obj
	 * @return String type variable
	 * @throws IOException
	 */
	public static String marshal(Object obj) throws IOException {
		return MAPPER.writeValueAsString(obj);
	}

	/**
	 * transfer String type to special model type.
	 * 
	 * @param <T>
	 * @param src  souce String
	 * @param type target type
	 * @return target type model
	 * @throws IOException
	 */
	public static <T> T unMarshal(String src, Class<T> type) throws IOException {
		return MAPPER.readValue(src, type);
	}
}
