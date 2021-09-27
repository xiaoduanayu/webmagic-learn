package com.example;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 演示子页面爬取
 * 爬取苏宁红孩子母婴商城的全部品牌
 *
 * @author debo
 * @date 2021-01-19
 */
public class RedBabyPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("redbaby.suning.com");

    /**
     * <url, catName>
     */
    private Map<String, String> map = null;

    @Override
    public void process(Page page) {
        if ("https://redbaby.suning.com/".equals(page.getUrl().get())) {
            // 从起始页中拿到分类的a标签
            List<String> links = page.getHtml().xpath("//span[@class='clearfix']/a").all();
            // 拆解a标签为分类名称和分类的页面url
            map = links.stream().collect(Collectors.toMap(this::getUrl, this::getCatName));
            // 将分类的页面url加入请求
            page.addTargetRequests(new ArrayList<>(map.keySet()));
            return;
        }
        String url = page.getUrl().get();
        // 保存分类和品牌的关联关系
        page.putField(map.get(url), getBrandNames(page.getHtml()
                .xpath("//span[@class='brand-name']/text()")
                .all()));
    }

    private List<String> getBrandNames(List<String> list) {
        return list.stream().map(this::getBrandName).collect(Collectors.toList());
    }

    private String getBrandName(String s) {
        return StringUtils.substringBefore(s, "(");
    }

    private String getUrl(String link) {
        return Html.create(link).xpath("a/@href").get();
    }

    private String getCatName(String link) {
        return Html.create(link).xpath("a/text()").get();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new RedBabyPageProcessor())
                // 添加起始页面
                .addUrl("https://redbaby.suning.com/")
                // 保存结果
                .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                .thread(5)
                .run();
    }
}