package com.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * 基本运用
 * 爬取爱宠网宠物品种
 *
 * @author debo
 * @date 2021-01-19
 */
public class PetPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("www.ichong123.com");

    @Override
    public void process(Page page) {
        List<String> list = page.getHtml().xpath("//div[@class='pet_list']/div[@class='pet_s']/p/a/text()").all();
        if ("http://www.ichong123.com/maomao".equals(page.getUrl().get())) {
            page.putField("猫", list);
        } else {
            page.putField("狗", list);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new PetPageProcessor())
                // 添加起始页面
                .addUrl("http://www.ichong123.com/gougou", "http://www.ichong123.com/maomao")
                // 保存结果
                .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                .thread(5)
                .run();
    }
}