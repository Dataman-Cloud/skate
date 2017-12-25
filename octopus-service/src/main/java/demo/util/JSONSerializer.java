package demo.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.alibaba.fastjson.JSON;

import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    public static <K, V> List<Map<K, V>> json2ListMap(String json, Class<Map[]> type) {
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

    public static void main(String[] args) {

        String a4 = "{\"result\":\"OK\",\"returnMsg\":\"[{\"productId\":\"SKU-24642\",\"productNum\":100}," +
                "{\"productId\":\"SKU-34563\",\"productNum\":100},{\"productId\":\"SKU-12464\",\"productNum\":100}," +
                "{\"productId\":\"SKU-64233\",\"productNum\":100}]\"}";

        //Map<String,Object> map = new Gson().fromJson(a1, new TypeToken<HashMap<String,Object>>(){}.getType());
        //Map<String,Object> maps = jsonToMap(a1);
        if (a4.endsWith("}]\"}")) {
            a4 = "[" + a4 + "]";
            a4 = a4.replace("\"returnMsg\":\"", "\"returnMsg\":");
            a4 = a4.replace("}]\"}]", "}]}]");
            List<Map<String, Object>> m1 = JSONSerializer.json2ListMap(a4, Map[].class);
            Map<String, Object> m2 = m1.get(0);
            List<Map<String, Object>> m3 = (List<Map<String, Object>>) m2.get("returnMsg");
            System.out.print(m3.toString());
            for (int i = 0; i < m3.size(); i++) {
                Map<String, Object> mm = m3.get(i);
                System.out.print(mm.get("productId") + "  " + mm.get("productNum") + "\n");
            }
        } else {
            System.out.print("null");
        }


        //System.out.print(map.get("result"));
    }
}
