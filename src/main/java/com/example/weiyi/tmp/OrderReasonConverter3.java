package com.example.weiyi.tmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提取医生信息
 *
 * @author debo
 * @date 2021-08-23
 */
public class OrderReasonConverter3 {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/home/debo/baidu/0901.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/debo/baidu/0901_result.txt"));
        String str;
        Map<String,List<JSONObject>> map = new HashMap<>();
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
            if ("非法医生".equals(jo1.getString("reason"))){
                System.out.println("==========");
                String expert_data = jo.getString("expert_data");
                JSONObject expertData = JSONObject.parseObject(expert_data);
                String expert_id = expertData.getString("expert_id");
                String expert_name = expertData.getString("expert_name");
                String expert_level = expertData.getString("expert_level");
                String expert_hospital = expertData.getString("expert_hospital");
                String expert_department = expertData.getString("expert_department");
                List<JSONObject> list = map.computeIfAbsent(expert_id, k -> new ArrayList<>());
                list.add(jo);
            }
        }
        for (Entry<String, List<JSONObject>> entry : map.entrySet()) {
            List<JSONObject> list = entry.getValue();
            JSONObject jsonObject = list.get(0);
            String expert_data = jsonObject.getString("expert_data");
            JSONObject expertData = JSONObject.parseObject(expert_data);
            String expert_id = expertData.getString("expert_id");
            String expert_name = expertData.getString("expert_name");
            String expert_level = expertData.getString("expert_level");
            String expert_hospital = expertData.getString("expert_hospital");
            String expert_department = expertData.getString("expert_department");
            writer.write(expert_id);
            writer.write("\t");
            writer.write(expert_name);
            writer.write("\t");
            writer.write(expert_level);
            writer.write("\t");
            writer.write(expert_hospital);
            writer.write("\t");
            writer.write(expert_department);
            writer.write("\t");
            String question_id = list.stream().map(jo -> jo.getString("question_id")).collect(Collectors.joining(","));
            writer.write(question_id);
            writer.write("\t");
            writer.newLine();
        }
        writer.close();
    }
}
