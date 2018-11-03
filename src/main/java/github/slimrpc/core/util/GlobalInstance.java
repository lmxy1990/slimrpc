package github.slimrpc.core.util;

import com.google.gson.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GlobalInstance {
	private static Gson gson;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));

		gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
		
		gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		
//		gsonBuilder.registerTypeAdapter(com.alibaba.fastjson.JSONObject.class, (JsonDeserializer<JSONObject>) (json, type, jsonDeserializationContext) -> JSON.parseObject(json.getAsJsonObject().toString()));

//		gsonBuilder.registerTypeAdapter(com.alibaba.fastjson.JSONObject.class, (JsonSerializer<JSONObject>) (src, typeOfSrc, context) -> new JsonPrimitive(JSON.toJSONString(src, SerializerFeature.UseISO8601DateFormat)));


		gson = gsonBuilder.create();

	}

	public static Gson getGson() {
		return gson;
	}

}
