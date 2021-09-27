package com.example.weiyi.tmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author debo
 * @date 2021-08-23
 */
public class OrderConverter {

    public static void main(String[] args) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/debo/baidu/dept_result.txt"));
        File file = new File("/home/debo/IdeaProjects/webmagic-learn/result/consult.gops.guahao.cn");
        File[] files = file.listFiles();
        for (File f : files) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String s = reader.readLine();
            JSONObject jsonObject = JSON.parseObject(s);
            writer.write(jsonObject.getString("orderKey"));
            writer.write("\t");
            writer.write(jsonObject.getString("qid"));
            writer.write("\t");
            writer.write(jsonObject.getString("matchDept"));
            writer.write("\t");
            writer.write(jsonObject.getString("treatDept"));
            writer.write("\t");
            writer.write(jsonObject.getString("doctorId"));
            writer.write("\t");
            writer.write(jsonObject.getString("consultDesc"));
            writer.newLine();
        }
        writer.close();
    }
}
