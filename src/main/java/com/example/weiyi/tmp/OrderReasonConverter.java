package com.example.weiyi.tmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author debo
 * @date 2021-08-23
 */
public class OrderReasonConverter {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/home/debo/baidu/bd.txt"));
        Map<String, Integer> reasonMap = new HashMap<>();
        Map<String, Integer> msgMap = new HashMap<>();
        Map<String, Integer> statusMap = new HashMap<>();
        String s;
        while ((s = reader.readLine()) != null) {
            JSONObject jsonObject = JSON.parseObject(s);
            String reason = jsonObject.getString("reason");
            String msg = jsonObject.getString("msg");
            int flag = 0;
            if (reason != null && !reason.trim().isEmpty()) {
                flag = 1;
                Integer count = reasonMap.get(reason);
                reasonMap.put(reason, Optional.ofNullable(count).map(c -> c + 1).orElse(1));
            }
            if (msg != null && !msg.trim().isEmpty()) {
                if (flag == 1) {
                    System.out.println("重复：" + s);
                }
                flag = 2;
                if (msg.startsWith("Error_Rsapi_Receive:invalid resource")) {
                    msg = "Error_Rsapi_Receive:invalid resource";
                }
                if (msg.startsWith("Error_Rsapi_Receive:question closed")) {
                    msg = "Error_Rsapi_Receive:question closed";
                }
                if (msg.startsWith("Error_Rsapi_Receive:unsupported resource")) {
                    msg = "Error_Rsapi_Receive:unsupported resource";
                }
                if (msg.startsWith("Error_Med_Receive:not valid status")) {
                    msg = "Error_Med_Receive:not valid status";
                }
                Integer count = msgMap.get(msg);
                msgMap.put(msg, Optional.ofNullable(count).map(c -> c + 1).orElse(1));
            }
            if (flag == 0) {
                Integer count = statusMap.get(s);
                statusMap.put(s, Optional.ofNullable(count).map(c -> c + 1).orElse(1));

            }
        }
        int c = 0;
        for (Entry<String, Integer> entry : reasonMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "次");
            c = c + entry.getValue();
        }
        for (Entry<String, Integer> entry : msgMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "次");
            c = c + entry.getValue();
        }
        for (Entry<String, Integer> entry : statusMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "次");
            c = c + entry.getValue();
        }
        System.out.println(c);
    }
}
