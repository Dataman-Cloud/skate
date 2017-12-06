package demo.util;

import com.alibaba.fastjson.JSON;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import com.alibaba.fastjson.JSON;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JSONSerializer {

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    @Deprecated
    public static <T> String obj2Json(T object) {
        return JSON.toJSONString(object);
    }

    @Deprecated
    public static <T> T json2Obj(String string, Class<T> clz) {
        return JSON.parseObject(string, clz);
    }

    public static <T> T load(Path path, Class<T> clz) throws IOException {
        return json2Obj(
                new String(Files.readAllBytes(path), DEFAULT_CHARSET_NAME), clz);
    }

    public static <T> void save(Path path, T object) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path,
                obj2Json(object).getBytes(DEFAULT_CHARSET_NAME),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * javabean to json
     */
    public static <T> String objToJson(T object) {
        if (object == null) {
            return null;
        }
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    /**
     * list to json
     */
    public static <T> String listToJson(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * map to json
     */
    public static String mapToJson(Map map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    /**
     * json to javabean
     */
    public static <T> T jsonToObj(String json, Class<T> t) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        Gson gson = new Gson();
        T obj = gson.fromJson(json, t);//对于javabean直接给出class实例
        return obj;
    }

    /**
     * json字符串转List集合
     */
    public static <T> List<T> jsonToList(String json, Class<T[]> type) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        T[] list = new Gson().fromJson(json, type);
        return Arrays.asList(list);
    }

    /**
     * json字符串转List集合
     */
    public static <K, V> List<Map<K, V>> json2Map(String json, Class<Map[]> type) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Map<K, V>[] list = new Gson().fromJson(json, type);
        return Arrays.asList(list);
    }

    /**
     * json字符串转Map
     */
    public static <K, V> Map<K, V> jsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<K, V>>() {
        }.getType();
        Map<K, V> maps = gson.fromJson(json, type);
        return maps;
    }

}
