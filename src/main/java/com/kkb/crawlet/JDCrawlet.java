package com.kkb.crawlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkb.pojo.Item;
import com.kkb.service.ItemService;
import com.kkb.utils.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JDCrawlet {

    @Autowired
    private HttpClientUtils httpClientUtils;

    @Autowired
    private ItemService itemService;

    // 解析 JSON 数据
    public static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 定时抓取数据：100 * 1000
     * @throws Exception
     */
    @Scheduled(fixedDelay =  100 * 1000)
    public void executeTask() throws Exception {

        // 指定需要爬取数据的网页
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&suggest=1.his.0.0&cid2=653&cid3=655&s=163&click=0&page=";

        // for 循环
        for(int i = 1; i < 10; i += 2){
            // 发起请求
            String content = httpClientUtils.getContent(url + i);

            // 解析数据
            parseContent(content);
        }

        System.out.println("已经全部执行成功了...");

    }

    /**
     * 解析数据
     * @param content
     * @throws Exception
     */
    private void parseContent(String content) throws Exception {

        // 使用 jsoup 来解析数据
        Document doc = Jsoup.parse(content);

        // 获取商品 --> spu
        // 拿到整个商品之后，才能获取里面对应的不同种类
        // 此处是获取了整个页面的商品
        Elements spus = doc.select("div#J_goodsList > ul > li");

        // 循环遍历，获取单个商品
        for (Element spuElement : spus) {

            // 1. 获取当前商品的 id
            Long spuID = Long.parseLong(spuElement.attr("data-spu"));


            // 2. 获取当前商品中的不同种类（多个）
            Elements skus = spuElement.select("li.ps-item img");

            // 3. 循环遍历
            for (Element skuElement : skus) {

                // 获取不同种类的商品 id
                Long skuID = Long.parseLong(spuElement.attr("data-sku"));

                // 在封装数据之前，先要去判断是否数据已存在
                Item item = new Item();
                item.setSku(skuID);

                List<Item> list = itemService.findAll(item);
                if(list.size() > 0){
                    continue;
                }

                // 封装数据

                // 商品数据
                item.setSpu(spuID);
                item.setSku(skuID);
                item.setUrl("https://item.jd.com/"+ skuID +".html");
                item.setCreated(new Date());
                item.setUpdated(item.getCreated());

                // 商品标题
                String titleHtml = httpClientUtils.getContent(item.getUrl());
                String title = Jsoup.parse(titleHtml).select("div.sku-name").text();
                item.setTitle(title);

                // 商品的价格
                String priceStr = "https://p.3.cn/prices/mgets?skuIds=J_" + skuID;
                String priceJSON = httpClientUtils.getContent(priceStr);
                // 解析 JSON 数据
                double price = mapper.readTree(priceJSON).get(0).get("p").asDouble();
                item.setPrice(price);

                // 图片的地址
                String picStr = "https:" + skuElement.attr("data-lazy-img")
                        .replace("/n9/", "/n1/");
                String picName = httpClientUtils.getImage(picStr);
                item.setPic(picName);

                // 将处理好的数据，直接存储到数据库中
                itemService.save(item);

            }
        }
    }
}
