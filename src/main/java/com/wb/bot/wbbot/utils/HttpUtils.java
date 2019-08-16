package com.wb.bot.wbbot.utils;

import com.wb.bot.wbbot.beans.ActionResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.util.*;

public class HttpUtils {


    private static final int timeOut = 180000;

    private static final int http_success_status = 200;


    public static String doGet(String url, Map<String, String> paramMap, CookieStore cookies, Map<String, String> headerMap, CookieStore cookieList) {
        return doGet(url, paramMap, cookies, headerMap, cookieList, null);
    }

    public static String doGet(String url, Map<String, String> paramMap, CookieStore cookies, Map<String, String> headerMap, CookieStore cookieList, HttpHost proxy) {
        return doGet(url, paramMap, cookies, headerMap, cookieList, proxy, null);
    }

    public static String doPost(String url, Map<String, String> paramMap, CookieStore cookies, Map<String, String> headerMap, CookieStore cookieList) {
        return doPost(url, paramMap, cookies, headerMap, cookieList, null);
    }

    public static String doGet(String url, Map<String, String> paramMap, CookieStore cookies, Map<String, String> headerMap, CookieStore cookieList, HttpHost proxy, Integer time) {
        CloseableHttpClient client = createHttpClient(cookies);
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        InputStream is = null;
        StringWriter writer = null;
        try {
            if (paramMap != null) {
                final List<NameValuePair> params = new ArrayList<>();
                for (final String key : paramMap.keySet()) {
                    params.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
                final String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8.toString()));
                if (StringUtils.isNotBlank(paramStr)) {
                    url = url + "?" + paramStr;
                }
            }
            HttpGet get = new HttpGet(url);
            get.setConfig(getRequestConfig(time, proxy));
            if (cookies != null) {
                get.setHeader("Cookie", getCookieString(cookies).toString());
            }
            if (headerMap != null) {
                Set<Map.Entry<String, String>> entries = headerMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }
            }
            final HttpResponse resp = client.execute(get, localContext);
            is = resp.getEntity().getContent();
            if (cookieList != null) {
                saveCookie(resp, cookieList, cookieStore);
            }
            writer = new StringWriter();
            IOUtils.copy(is, writer, Consts.UTF_8);
            final int stateCode = resp.getStatusLine().getStatusCode();
            if (stateCode != http_success_status) {
                throw new RuntimeException("error code " + stateCode);
            }
            return writer.toString();
        } catch (final Exception e) {
        } finally {
            close(client, is, writer);
        }
        return null;
    }


    public static String doPost(String url, Map<String, String> paramMap, CookieStore cookies, Map<String, String> headerMap, CookieStore cookieList, HttpHost proxy) {
        CloseableHttpClient client = createHttpClient(cookies);
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        InputStream is = null;
        StringWriter writer = null;
        try {
            final List<NameValuePair> params = new ArrayList<>();
            if (paramMap != null) {
                for (final String key : paramMap.keySet()) {
                    params.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
            }
            HttpPost post = new HttpPost(url);
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(params, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.setEntity(entity);
            post.setConfig(getRequestConfig(timeOut, proxy));
            if (cookies != null) {
                post.setHeader("Cookie", getCookieString(cookies).toString());
            }
            if (headerMap != null) {
                Set<Map.Entry<String, String>> entries = headerMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse resp = client.execute(post, localContext);
            is = resp.getEntity().getContent();
            if (cookieList != null) {
                saveCookie(resp, cookieList, cookieStore);
            }
            writer = new StringWriter();
            IOUtils.copy(is, writer, "utf-8");
            final int stateCode = resp.getStatusLine().getStatusCode();
            if (stateCode != http_success_status) {
                throw new RuntimeException("error code " + stateCode);
            }
            return writer.toString();
        } catch (IOException e) {
        } finally {
            close(client, is, writer);
        }
        return null;

    }


    /**
     * 文件下载
     */
    public static boolean downloadFile(String url, String src) {
        final CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            OutputStream out = new FileOutputStream(src);
            byte[] bytes = EntityUtils.toByteArray(entity);
            out.write(bytes);
            out.flush();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 资源关闭
     */
    private static void close(CloseableHttpClient client, InputStream is, StringWriter writer) {
        if (is != null) {
            try {
                is.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取配置
     */
    private static RequestConfig getRequestConfig(Integer time, HttpHost proxy) {
        if (time == null) {
            time = timeOut;
        }
        return RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setRedirectsEnabled(true)
                .setProxy(proxy)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC)).setSocketTimeout(time)
                .setConnectionRequestTimeout(time)
                .setConnectTimeout(time).setConnectionRequestTimeout(time).build();
    }

    /**
     * cookie转换信息
     */
    private static StringBuilder getCookieString(CookieStore cookies) {
        StringBuilder cookieString = new StringBuilder();
        List<Cookie> list = cookies.getCookies();
        for (Cookie cookie : list) {
            cookieString.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }
        return cookieString;
    }

    /**
     * cookie保存
     */
    private static void saveCookie(HttpResponse resp, CookieStore cookieList, CookieStore respCookieStore) {
        Header[] allHeaders = resp.getAllHeaders();
        for (Header allHeader : allHeaders) {
            if (allHeader.getName().equals("Set-Cookie")) {
                String[] cookieString = allHeader.getValue().split(";");
                Cookie cookie = new BasicClientCookie(cookieString[0].split("=")[0]
                        , cookieString[0].split("=")[1]);
                cookieList.addCookie(cookie);
            }
        }
        List<Cookie> cookies = respCookieStore.getCookies();
        for (Cookie cookie : cookies) {
            ActionResult.getInstance().getCookieStore().addCookie(cookie);
        }
    }

    /**
     * ssl证书配置
     */
    private static CloseableHttpClient createHttpClient(CookieStore cookies) {
        CloseableHttpClient client = null;
        try {
            //使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书 否则调用微博api会报错
            // 信任所有
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(2000).build();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            client = HttpClients.custom().setSSLSocketFactory(ssl).setDefaultCookieStore(cookies).setDefaultSocketConfig(socketConfig).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

}
