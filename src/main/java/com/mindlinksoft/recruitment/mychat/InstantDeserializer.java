package com.mindlinksoft.recruitment.mychat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/**
 * Class that implements {@link JsonDeserializer}. Allows for a Deserialized
 * {@link Conversation} to be created from an input Json file.
 */
public class InstantDeserializer implements JsonDeserializer<Instant> {

	@Override
	public Instant deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (!jsonElement.isJsonPrimitive()) {
			throw new JsonParseException("Expected instant represented as JSON number, but no primitive found.");
		}

		JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

		if (!jsonPrimitive.isNumber()) {
			throw new JsonParseException("Expected instant represented as JSON number, but different primitive found.");
		}

		return Instant.ofEpochSecond(jsonPrimitive.getAsLong());
	}

	/**
	 * Takes in the {@code name} of the Json file and converts it to a
	 * {@link Conversation} object
	 * 
	 * @param name Represents the name of the file
	 * @return {@link Conversation} object
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws FileNotFoundException
	 */
	public static Conversation createJsonDeserialized(String name)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

		Gson g = builder.create();

		Conversation c = g.fromJson(new InputStreamReader(new FileInputStream(name)), Conversation.class);

		return c;
	}
}
