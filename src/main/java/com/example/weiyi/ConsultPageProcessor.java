package com.example.weiyi;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * 爬取问诊系统订单详情页
 *
 * @author debo
 * @date 2021-08-19
 */
public class ConsultPageProcessor implements PageProcessor {

    public HashMap<String, String> orderMap = new HashMap<>();

    private Site site = Site.me()
            .setDomain("consult.gops.guahao.cn")
            // cookie
            .addHeader("Cookie", "_grms_sid_=1622533201580013193460571; _hsid_d_=zfuQpB6EVOEYTodO+3u0MuYa8sl9kJ0gS4xOL+lMOBHtQ19AR9hDuP/4bqMYVlyM; OUTFOX_SEARCH_USER_ID_NCOO=237877997.94223464; _gsid_=m1TBuGde/8ndMA5T69QW0ModaQD5BYoAS3Lnr/tpEby1LY6694fzPxXf8kpX/TzwPu3vktF9J2s=; _gsid_=iaf+mYoYGkMOiPHLNGSncNWbj5lvtu35JfZy4X673OCPWwFC0yBOYg2Fo6HFxGflHqOELch4nKw=; _gi___=lVNGGHP+mOHngNPUIJ8Bo8muAGZtScMJWOCxiHhQ9rg=; __u__t__=KMiw7s8F0tnxXCeL4IuGh7sgDKqVs4Cx6SQtSdAMEZwcSrWF6NK1Rw==; _u__a_=ILfEgMYWlVfuicrcjKSbc2H6IELd+gnTtSYDggx4pHXqIDay2S8N9QGHfJyXvAbkNo/JsOsTOg/fivdqTe4hbuFoAyFRdFVr1Z+na4/azJrseimgDzkyBKVEilT7N126iFIOT/Fst3P+O4u/X9XZWM0iictdLQ6BK4CZdxcNqfEhRx83a6fRo5H6ri8MVCMlruAwvVdXyQDuc6kwJmbinvAs+4dwRwpEr4kmHpthfXRWvw7xIbEmUQhQ+uSbKd+Lk8H3Ft0mHWIqhLS9nI1ML+skhRJko1IWeciJwPnx/mU17jpYgC4v2A4t73L0GH8EV3upDotIcPOpvot0jpXa4UjdxhBhesRUj+W6y8Z+zMT/UGRep5jlNXzJZP/PObejUTfw+EA7gnPEuX+6zuvZ6JeD/ZNO7Fi0QodVkbbmti1QeSnLvXV13dBWVAi4rGcD9GBCqMQ52ZnsrwFUSmgk8PfgqTQ0VpCMXa9VYSUzqwTWN927M+c9faUvxHZhH9TVvc8sc0qPxGaV8qdTtvd+lyoALvwlk8W8N6aFW6Jd/0ET1G8booQd827ZqcH54AdSYBcU+i8YPNgJM4O64ywC5A==; _gp___=/JOuLWjMi+4fRTy0LPP3Dumd9ZA7UAe07a83eKSEFykmUWuDgsKGzA==; _guli___=5k5CCpoXOIm0/EAsnnKfM8i4hwnAjDG7zWfOmtIFtamSlUVHbHWfSHxLWd2U52mj8j3EqaaoP30=; ___lt_=dLw9ALaZgE/KkmBcc1pcCD9bjBCC7ZswzysB28e2b8qSKd2Du5qLc49PKBOY7S9g; ___lk__=PK8b/WoNcLVOZnouBUphX3YcZbYkAbGM1Ap+cYrGS5SX8i+3wsYIVw==; _gback___=http://consult.gops.guahao.cn/search/consult/list?consultDateStart=2021-08-16+16%3A45&consultDateEnd=2021-08-24+16%3A45&orderDateStart=&orderDateEnd=&consultType=&consultStatus=&source=&complainStatus=&payStatus=&doctorPhone=&keyWord=&expertName=&patientPhone=&patientName=&patientAnswerPhone=&orderKey=5iena6r0p4210820171644967&commentStatus=&thirdOrderKey=&commentDateStart=&commentDateEnd=&returnVisit=&csi=&revisitFollowing=&actTag=&feeStart=&feeEnd=&isGoHospital=&doctorUserId=&hasPrescribed=&patientUser=&nurseOrder=0&medicationGuidance=&boughtDrug=0&medicalInsurance=2&sortType=0&finishType=&clearStatus=||||http://consult.gops.guahao.cn/consult/fasttxt/detail/5iena6r0p4210820171644967?leftMenuFlg=2||||http://consult.gops.guahao.cn/resources/js/bootstrap-select.min.js.map'")
            // UA
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");

    @Override
    public void process(Page page) {
        // 匹配科室
        String matchDept = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][12]/div/text()")
                .get().replaceAll("\\u00a0", "").trim();
        // 接诊科室
        String treatDept = getTreatDept(page);
        // 医生ID
        String doctorId = getDoctorId(page);
        // 病情描述
        String consultDesc = getConsultDesc(page);
        // 订单号
        String orderKey = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][2]/div/text()")
                .get();
        orderKey = StringUtils.substringBefore(orderKey, "【").trim();
        // QID
        String qid = orderMap.get(orderKey);
        page.putField("orderKey", orderKey);
        page.putField("qid", qid);
        page.putField("matchDept", matchDept);
        page.putField("treatDept", treatDept);
        page.putField("doctorId", doctorId);
        page.putField("consultDesc", consultDesc);
    }

    private String getConsultDesc(Page page) {
        return page.getHtml().xpath("//div[@class='form-body']/div[@class='form-group'][19]/div/text()").get().trim();
    }

    private String getDoctorId(Page page) {
        String s = page.getHtml().xpath("//div[@class='form-body']/div[@class='form-group'][14]/div/text()").get();
        s = StringUtils.substringBetween(s, "医生ID：", "|").trim();
        return s;
    }

    private String getTreatDept(Page page) {
        String s = page.getHtml().xpath("//div[@class='form-body']/div[@class='form-group'][14]/div/text()").get();
        s = StringUtils.substringBetween(s, "科室：", "|").trim();
        return s;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws Exception {
        ConsultPageProcessor consultPageProcessor = new ConsultPageProcessor();
        BufferedReader reader = new BufferedReader(new FileReader("/home/debo/IdeaProjects/webmagic-learn/src/main/resources/order.txt"));
        String s;
        while ((s = reader.readLine()) != null) {
            String[] arr = s.split(",");
            String qid = arr[0];
            String orderKey = arr[1];
            consultPageProcessor.orderMap.put(orderKey, qid);
            Spider.create(consultPageProcessor)
                    // 添加起始页面
                    .addUrl("http://consult.gops.guahao.cn/consult/fasttxt/detail/" + orderKey + "?leftMenuFlg=2")
                    // 保存结果
                    .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                    .thread(1)
                    .run();
        }
    }
}