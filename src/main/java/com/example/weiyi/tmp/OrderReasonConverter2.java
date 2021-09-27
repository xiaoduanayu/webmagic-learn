package com.example.weiyi.tmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author debo
 * @date 2021-08-23
 */
public class OrderReasonConverter2 {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/home/debo/baidu/0901.txt"));
        String str;
        List<JSONObject> list = new ArrayList<>();
        while ((str = reader.readLine()) != null) {
            JSONObject jsonObject = JSON.parseObject(str);
            // 请求参数
            String msgContent = jsonObject.getString("msgContent");
            // 去掉第一行
            String s = StringUtils.substringAfter(msgContent, "\r\n");
            JSONObject jo = JSONObject.parseObject(s);
            // QID
            String qid = jo.getString("question_id");
            // 响应参数
            String msgResult = jsonObject.getString("msgResult");
            JSONObject jo1 = JSONObject.parseObject(msgResult);
            String question_id = jo1.getString("question_id");
            if (StringUtils.isBlank(question_id)) {
                // System.out.println("没有QID");
                // 响应参数中没有QID
                jo1.put("question_id", qid);
            }
            list.add(jo1);
        }
        Map<String, Integer> reasonMap = new HashMap<>();
        Map<String, Integer> msgMap = new HashMap<>();
        Map<String, Integer> statusMap = new HashMap<>();
        for (JSONObject jsonObject : list) {
            String reason = jsonObject.getString("reason");
            String msg = jsonObject.getString("msg");
            int flag = 0;
            if (reason != null && !reason.trim().isEmpty()) {
                if (reason.equals("非法医生")) {
                    System.out.println(jsonObject.getString("question_id"));
                }
                flag = 1;
                Integer count = reasonMap.get(reason);
                reasonMap.put(reason, Optional.ofNullable(count).map(c -> c + 1).orElse(1));
            }
            if (msg != null && !msg.trim().isEmpty()) {
                if (flag == 1) {
                    System.out.println("重复");
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
                // System.out.println("都是空的："+jsonObject);
                Integer count = statusMap.get("10113");
                statusMap.put("10113", Optional.ofNullable(count).map(c -> c + 1).orElse(1));
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
