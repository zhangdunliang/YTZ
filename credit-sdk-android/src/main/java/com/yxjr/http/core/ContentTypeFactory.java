package com.yxjr.http.core;

/**
 * 报文数据中的Content-Type格式，支持所有文件
 */
public class ContentTypeFactory {
	// private Map<String, String> mTypeMap;
	private static ContentTypeFactory mInstance = new ContentTypeFactory();

	private ContentTypeFactory() {
		// InputStream is =
		// getClass().getResourceAsStream("/assets/content_type.json");
		// ByteArrayOutputStream bos = null;
		// try {
		// bos = new ByteArrayOutputStream();
		// int ch;
		// while ((ch = is.read()) != -1) {
		// bos.write(ch);
		// }
		// byte data[] = bos.toByteArray();
		// String json = new String(data);
		// mTypeMap = new Gson().fromJson(json, new TypeToken<Map<String,
		// String>>() {
		// }.getType());
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// IO.close(bos, is);
		// }
	}

	public String getContentType(String fileName) {
		return "image/jpeg";
		// String end = fileName.contains(".") ?
		// fileName.substring(fileName.lastIndexOf(".")).toLowerCase() : ".*";
		// return mTypeMap.containsKey(end) ? mTypeMap.get(end) :
		// mTypeMap.get(".*");
	}

	public static ContentTypeFactory getInstance() {
		return mInstance;
	}
}
