package com.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 演示POST请求
 * 爬取问诊系统订单
 *
 * @author debo
 * @date 2021-08-19
 */
public class WeiyiConsultPageProcessor implements PageProcessor {

    private Site site = Site.me()
            .setDomain("consult.gops.guahao.cn")
            // cookie
            .addHeader("Cookie", "_grms_sid_=1622533201580013193460571; _hsid_d_=zfuQpB6EVOEYTodO+3u0MuYa8sl9kJ0gS4xOL+lMOBHtQ19AR9hDuP/4bqMYVlyM; OUTFOX_SEARCH_USER_ID_NCOO=237877997.94223464; _gsid_=bjVdROZ7Ggi1pyTo3JBvsIG6FgJVW428RJ7J1SiTFaHNZVR0IdMsglrmg1XszF0C; _gsid_=/5uSEqZzPpPY3dfEHkCdSGv8S8GEw2Yg+4vJM6lqWvgCsK7+41AZTQ2WtzafIkZ4xf500Ze6R58=; _gi___=DUEMvdETLbB8Ctieu5f0X/64geyYNlhojAa++DbfdOo=; __u__t__=YX6p9i/LuQRrzc1S+9HkuBT1NTd/di3Rrzs8NOpUnn1IcI7jPNJ7ug==; _u__a_=63EGGAO414FS6Wwrvv19KtbmS04yeWaDC/9FGtZydLVkkZu6mryJVzgxJ5Omc4H6kdksN9SQ4mMSqRUXLIm6p65gCgcjd1rfZH67nWjWOM2tPZ4qeuRfaOeondG3s+oSVA3xV+kkr1ahFon3OdvMYG3M7di/LjycW2TD6BB+CCRZlx/XgC+AU3OrMsD5A1eQt82uLP1R4Ty60Rk9i2w3BUTfhLP51rUfPgLJuUOR0rG6gdIQZTV6mPkiDniHvF3IJYo4c6bqbiJfdjBoZ/S/wNqsQOGBCLLO3aikdhw7bq7xuPMVySMmi9k9VWD9woYzrI1pTl73QraZv+1iqK0ALbaf59OEOIjFaVj/ZIoYwdEu9zrPyrZH1P5Is9KP5WNO7vHMfaU1Ffq7q8kFa1lfxf2YH9LxzdMuWJeBAzK5XrFnfcPsIG0x65RhzfJ0/fU9XCNV+M69rRQ0alIAstZqC5aWsK8/y5jOV96s4FfuaM1lmVBvGvCaUMVqgDXvC8JQ2m/RXVqDUdDbWJyND5U7MHMO1a5/hKPSDhfKPW9ekp9kKshKWOFCFvJN0jAPzq16hP+T2X31NDEcjjBsEvvMlg==; _gp___=vpd82ZQWX382clss2konqxU5oV1NyvNJP+hdUn2iT0SqX3wyDOEfag==; _guli___=E0Ndo87U5W1VQD5jKoWMFe3/vhm2a8P0kiKtfEla7NzCQcCZCxOqtl2U4zr4QJg0DSeyCR+SowE=; ___lt_=kFoX25ywpgld1GiZOCri7wr9sbSWU79jsG7XwZ7T4e8pODB4WqCk+SZO3Q+QHkm1; ___lk__=ZLzX36svzPE115tBkd88zhcPUiY/1l6gJPK/jhX/yQt1g7ljscYZKw==; _gback___=http://consult.gops.guahao.cn/servicecode/edit?serviceCode=IMAGE_TEXT_MAIN||||http://consult.gops.guahao.cn/search/consult/list||||http://consult.gops.guahao.cn/resources/js/bootstrap-select.min.js.map")
            // UA
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36");

    @Override
    public void process(Page page) {
        if ("http://consult.gops.guahao.cn/search/consult/list".equals(page.getUrl().get())) {
            // 订单详情页url
            List<String> detailLinks = page.getHtml()
                    .links()
                    .regex("http://consult.gops.guahao.cn/consult/fasttxt/detail/.*")
                    .all();
            page.addTargetRequests(detailLinks);
            return;
        }

        // 订单来源
        String source = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][1]/div/text()")
                .get()
                .trim();
        // 订单号
        String orderKey = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][2]/div/text()")
                .get()
                .trim();
        // 下单时间
        String orderTime = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][3]/div/text()")
                .get()
                .trim();

        // 病情描述
        String consultDesc = page.getHtml()
                .xpath("//div[@class='form-body']/div[@class='form-group'][19]/div/text()")
                .get()
                .trim();

        page.putField("orderKey", orderKey);
        page.putField("source", source);
        page.putField("orderTime", orderTime);
        page.putField("consultDesc", consultDesc);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        List<Request> requests = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Request request = new Request("http://consult.gops.guahao.cn/search/consult/list");
            request.setMethod("POST");
            Map<String, Object> map = new HashMap<>();
            map.put("pageNo", i);
            // 极速图文
            map.put("consultType", 6);
            HttpRequestBody requestBody = HttpRequestBody.form(map, "utf8");
            request.setRequestBody(requestBody);
            requests.add(request);
        }
        Spider.create(new WeiyiConsultPageProcessor())
                // 添加起始页面
                .addRequest(requests.toArray(new Request[0]))
                // 保存结果
                .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                .thread(5)
                .run();
    }
}