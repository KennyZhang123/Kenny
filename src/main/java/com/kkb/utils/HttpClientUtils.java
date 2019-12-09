package com.kkb.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Component
public class HttpClientUtils {

    // 管理者
    private PoolingHttpClientConnectionManager manager;

    // 构造器
    public HttpClientUtils() throws Exception {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);
    }

    /**
     * 抓取内容
     * @param url
     * @return
     * @throws Exception
     */
    public String getContent(String url) throws Exception {
        // 1. 需要通过 manager 来帮我创建 httpClient
        // 如果你要使用的时候，直接去池子中取即可
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();

        // 2. 设置 url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-agent", "Mozilla/5.0" );

        // 3. 设置相关参数
        httpGet.setConfig(getConfig());

        CloseableHttpResponse response = null;

        try {
            // 3. 发送你的请求（回车），返回响应
            response = httpClient.execute(httpGet);

            // 4. 将响应的数据进行解析，然后获取数据进行渲染
            // 有响应的时候，我们还需要判断 状态码是否 200
            // 只有是 200 的时候整个请求响应过程才是正常的
            if(response.getStatusLine().getStatusCode() == 200){

                String content = "";

                if(response.getEntity() != null) {
                    // 使用 EntityUtils
                    content = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
                return content;
            }

        } catch (Exception ex){
            ex.printStackTrace();

        } finally {
            // 关闭资源，不要浪费
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 抓取图片
     * @param url
     * @return
     * @throws Exception
     */
    public String getImage(String url) throws Exception {
        // 1. 需要通过 manager 来帮我创建 httpClient
        // 如果你要使用的时候，直接去池子中取即可
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();

        // 2. 设置 url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-agent", "Mozilla/5.0" );

        // 3. 设置相关参数
        httpGet.setConfig(getConfig());

        CloseableHttpResponse response = null;

        try {
            // 3. 发送你的请求（回车），返回响应
            response = httpClient.execute(httpGet);

            // 4. 将响应的数据进行解析，然后获取数据进行渲染
            // 有响应的时候，我们还需要判断 状态码是否 200
            // 只有是 200 的时候整个请求响应过程才是正常的
            if(response.getStatusLine().getStatusCode() == 200){

                // 获取图片的文件类型：png   jpg
                String picExtName = url.substring(url.lastIndexOf("."));

                // 使用 UUID ---> 图片的新名字
                String imgName = UUID.randomUUID().toString() + picExtName;

                // 设置图片的保存位置
                // C:\Users\123\Desktop\img
                OutputStream out = new FileOutputStream(new File("D:\\KKB\\Crawer\\photo\\img\\" + imgName ));

                // 使用响应体（输出流）来写文件
                response.getEntity().writeTo(out);

                // 返回图片的名字
                return imgName;
            }

        } catch (Exception ex){
            ex.printStackTrace();

        } finally {
            // 关闭资源，不要浪费
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    /**
     * 设置参数
     * @return
     */
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(10000)
                .build();

        return config;
    }

}
