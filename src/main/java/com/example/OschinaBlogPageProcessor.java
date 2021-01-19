package com.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class OschinaBlogPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("my.oschina.net");

    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().links().regex("https://my\\.oschina\\.net/flashsword/blog/\\d+").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml()
                .css("h1[class='article-box__title']>a")
                .toString());
        page.putField("content", page.getHtml().$("div.content").toString());
        page.putField("tags", page.getHtml().css(".tag-item").all());
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) throws Exception{
        Spider.create(new OschinaBlogPageProcessor()).addUrl("http://my.oschina.net/flashsword/")
                .addPipeline(new ConsolePipeline()).run();
        System.in.read();
    }
}