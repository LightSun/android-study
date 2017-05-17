package study.heaven7.com.android_study.util;

import java.util.Map;
import java.util.Map.Entry;

public class JsonUtil {

	public static String mapToJson(Map<String,String> map) {
		if (map == null || map.isEmpty())
			return "";
		StringBuilder sBuffer = new StringBuilder();
		sBuffer.append("{");
		for (Entry<String, String> entry : map.entrySet()) {
			sBuffer.append("\"");
			sBuffer.append(entry.getKey());
			sBuffer.append("\":");
			sBuffer.append("\"");
			sBuffer.append(entry.getValue());
			sBuffer.append("\"");
			sBuffer.append(",");
		}
		sBuffer.deleteCharAt(sBuffer.lastIndexOf(","));
		sBuffer.append("}");
		return sBuffer.toString();
	}

}
