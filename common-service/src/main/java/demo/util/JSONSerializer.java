package demo.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    /**
     * list to json
     */
    public static <T> String listToJson(List<T> list) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * map to json
     */
    public static String mapToJson(Map map) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    /**
     * json to javabean
     */
    public static <T> T jsonToObj(String json, Class<T> t) {
        Gson gson = new Gson();
        T obj = gson.fromJson(json, t);//对于javabean直接给出class实例
        return obj;
    }

    /**
     * json字符串转List集合
     */
    public static <T> List<T> jsonToList(String json, Class<T[]> type) {
        Gson gson = new Gson();
        T[] list = new Gson().fromJson(json, type);
        return Arrays.asList(list);
    }

    /**
     * json字符串转List集合
     */
    public static <K, V> List<Map<K, V>> json2Map(String json, Class<Map[]> type) {
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

    //springBoot 启动时只允许有一个main函数，测试完毕需要注释掉
    /*public static void main(String[] args) {
        Stock person1 = new Stock();
        person1.setId(1L);
        person1.setNumber(100L);
        person1.setStockTime(new Date());

        Stock person2 = new Stock();
        person2.setId(2L);
        person2.setNumber(200L);
        person2.setStockTime(new Date());

        List<Stock> lp = new ArrayList<Stock>();
        lp.add(person1);
        lp.add(person2);
        //System.out.println("jsonObj2str:" + listToJson(lp));

        String json = listToJson(lp);
        //Map<String,Object> obj = jsonToMap(json);
        //System.out.print(obj.getId());
        //for(Map.Entry entry : obj.entrySet()){
        //    System.out.print(entry.getKey() + " - " + entry.getValue());
        // }
        //List<Stock> list = (List<Stock>)jsonToObj(json,Stock.class);

        List<Stock> jsonListObject = jsonToList(json, Stock[].class);
        List<Map<String, Object>> maps = json2Map(json, Map[].class);
        *//*for (Stock stock : jsonListObject) {
            System.out.print(stock.getId() + " - " + stock.getNumber() + " - " + stock.getStockTime() + "\n");
        }

        for (Map<String, Object> map : maps) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                //System.out.print("key: " + m.getKey() + " - value: " + m.getValue() + "\n");
            }

            for (String key : map.keySet()) {

            }

            System.out.print("productId: " + map.get("id") + " - productNumber: " + map.get("number") + "\n");
        }*//*

        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        Map<String,Object> map2 = null;

        for(int i=0;i<10;i++){
            map2 = new HashMap<String,Object>();
            map2.put("productId","TEST-00" + i);
            map2.put("productNum",100L);
            mapList.add(map2);
        }

        String json2 = mapList.toString();
        System.out.print("jsonStr: " + json2 + "\n");
        //System.out.print("mapObj: " + json2Map(json2,Map[].class));

        List<Map<String,Object>> objMap = json2Map(json2,Map[].class);
        for(Map<String,Object> obj : objMap){
            //for(Map.Entry entry : obj.entrySet()){
             //   System.out.print("key: " + entry.getKey() + " -- value : " + entry.getValue() + "\n");
            //}
            System.out.print("productId: " + obj.get("productId") +  " -- productNum: " + obj.get("productNum") + "\n");
        }
    }*/
}
