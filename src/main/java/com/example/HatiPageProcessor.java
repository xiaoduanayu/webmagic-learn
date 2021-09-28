package com.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库爬取
 *
 * @author debo
 * @date 2021-09-19
 */
public class HatiPageProcessor implements PageProcessor {

    private List<String> records = new ArrayList<>();

    private Site site = Site.me()
            .setDomain("hati.gops.guahao.cn")
            // cookie
            .addHeader("Cookie", "_grms_sid_=1622533201580013193460571; _hsid_d_=zfuQpB6EVOEYTodO+3u0MuYa8sl9kJ0gS4xOL+lMOBHtQ19AR9hDuP/4bqMYVlyM; OUTFOX_SEARCH_USER_ID_NCOO=237877997.94223464; _gsid_=bjVdROZ7Ggi1pyTo3JBvsIG6FgJVW428RJ7J1SiTFaHNZVR0IdMsglrmg1XszF0C; _gsid_=/5uSEqZzPpPY3dfEHkCdSGv8S8GEw2Yg+4vJM6lqWvgCsK7+41AZTQ2WtzafIkZ4xf500Ze6R58=; ___lt_=yAyhUYjnxP20b7zLVyOlWYWVo8ihENUEJdSGzmaNkjQHqffNVTv0nGbXwe/qQxxy; ___lk__=M8wLmeYCLXn8GKZFLClyjtFCJ3/QNRA2vVA61ZTG4ckPf/wC+YC6SA==; _gi___=cd5s6Y5l4psWzmepllLmMiKQz+h5E5jKldMNADJckJ0=; __u__t__=IOiJ/4F/IsymuENhIgWFyUfSZWaPaBEQaPbQtr20fD4XKEuhdXnH5Q==; _u__a_=xTWPh8hTcrGJmpEv8RSJ420ssGLTG5DNfB3hcQ8z7YtXjQ815IQtXsokT3O5ne+hbkjLbX7VdztY+CHVNFkbMukwaYTgq9oozNlwpHD/RvyMJHuBamJ5Ml/c4yRbCXnOGRkIBz8Ior7EYbWrh7+iuYq9AGGBWX+WvYIhU3tGAwSg/u8oKbvH6GNbs7+2kxnBcdmg/kNeFm6SyWkp/yCLMun+A1kencwtkkrgHriFZXUVzR3HDgxRtVuiXic1Z2WKB6GmC51gcE7oHJqd02ZspaWXl+66DRa6i5g1UvvxOLiIuyKIWBh+sb7lfcdwpf03tiSgODYGPsGR7RlX0gM3U70NXRZLteLDZlM522QbW+cQ3dVBd1wss2jgXz0KMXcl3aF/6Y/1TYcpJfaCA3IsSsI7AcVO3T9pDwW4WR2CTwJzb/FRgxr9JaDPNqSUbfuTFJ0MBb77eSQtIoGgLSFfRPahF2w9inuaCTgSQ0bHqjmLXXXOqMKQAPdE2XgXtcfhSO4Q6mk4CpRXhwZLM1AqlZmTZjN/R2M0jhYlfUoPFLtFo3maUWoi2CESrrOTqpkrQvzYF/FtUhurxTwcnOFCBw==; _gp___=0FX6RyT4D+kwHBv1+zW1Kb8fG9682AVm3lrdI9NKGbccW0Ol3RMAbw==; _guli___=bTMMiE4kV07BhDtfGUmqrNVTHUYhRrj6m5XbVE6rzDNEuvkQI/RfiP8QHXWCP178Rh6ULWuR8QM=")
            // UA
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36")
            // Referer
            .addHeader("Referer", "http://hati.gops.guahao.cn/dataquery/queryview");

    @Override
    public void process(Page page) {
        Json json = page.getJson();
        List<String> list = json.jsonPath("$.data.data.data").all();
        records.addAll(list);
        String haveMore = json.jsonPath("$.data.data.haveMore").get();
        // 判断是否还有记录
        if ("true".equals(haveMore)) {
            // 拿到最后一条记录的主键
            int lastPkid = Integer.parseInt(json.jsonPath("$.data.data.data[-1:][0]").get());
            // 只取前500条记录
            if (lastPkid > 400) {
                // 保存结果
                page.putField("records", records);
                return;
            }
            // 分页查找
            page.addTargetRequest(getRequest(lastPkid));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new HatiPageProcessor())
                // 添加请求
                .addRequest(getRequest(0))
                // 保存结果
                .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                .thread(1)
                .run();
    }

    private static Request getRequest(int pkid) {
        Request request = new Request("http://hati.gops.guahao.cn/dataquery/query");
        request.setMethod("POST");
        Map<String, Object> params = new HashMap<>();
        params.put("sql", "SELECT pkid,order_info_id,service_code,config_biz,order_biz_type,order_biz_sub_type,gmt_created from config_log where pkid>" + pkid + "order by pkid");
        params.put("dbName", "consult_log");
        params.put("limit", 100);
        params.put("clientIdentifier", "");
        request.setRequestBody(HttpRequestBody.form(params, "utf8"));
        return request;
    }
}