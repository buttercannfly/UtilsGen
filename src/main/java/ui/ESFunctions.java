package ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import entity.Item;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ESFunctions {
    public static ArrayList<Item> getFromES(String keyword, int curPosition, int size){
        ArrayList<Item> list = new ArrayList<>();
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("http://202.38.78.213:8089/api/es/get?keyword="+keyword.replaceAll(" ","%20")+"&posi="+curPosition+"&size="+size);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            System.out.println(result.toString());
            JSONArray array = JSONObject.parseArray(result.toString(), Feature.UseObjectArray);
            for (Object jb :
                    array) {
                JSONObject jsonObject = JSONObject.parseObject(jb.toString());
                Item item = new Item();
                item.setId(jsonObject.getString("id"));
                item.setTitle(jsonObject.getString("title"));
//                item.setCodes(jsonObject.getString("codes"));
                String codes = jsonObject.get("codes").toString();
                item.setCodes(codes);
                list.add(item);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    public static void updateES(String id,String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://202.38.78.213:8089/api/es/update?id="+id+"&keyword="+text.replaceAll(" ","%20"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    StringBuilder result = new StringBuilder("");
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8))) {
                        for (String line; (line = reader.readLine()) != null; ) {
                            result.append(line);
                        }
                    }
                    System.out.println("ACK"+result.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
