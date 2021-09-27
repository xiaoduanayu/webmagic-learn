package com.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author debo
 */
public class GithubTrendingPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setDomain("github.com");

    @Override
    public void process(Page page) {
        // 项目总数
        int size = page.getHtml().xpath("//article[@class='Box-row']").all().size();
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            // 名称
            String name = page.getHtml().xpath("//article[@class='Box-row'][" + i + "]/h1/a/text()").get().trim();
            // 描述
            String desc = page.getHtml().xpath("//article[@class='Box-row'][" + i + "]/p/text()").get();
            // star
            String star = page.getHtml()
                    .xpath("//article[@class='Box-row'][" + i + "]/div[2]/a[1]/text()")
                    .get()
                    .trim();
            // 链接
            String href = page.getHtml().xpath("//article[@class='Box-row'][" + i + "]/h1/a/@href").get().trim();
            href = "https://github.com" + href;
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("desc", desc);
            map.put("star", star);
            map.put("href", href);
            list.add(map);
        }
        page.putField("results", list);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 8889)));
        Spider.create(new GithubTrendingPageProcessor())
                .addUrl("https://github.com/trending")
                // 保存结果
                .addPipeline(new JsonFilePipeline(System.getProperty("user.dir") + "/result"))
                // 设置代理
                .setDownloader(httpClientDownloader)
                .thread(5)
                .run();
    }
}