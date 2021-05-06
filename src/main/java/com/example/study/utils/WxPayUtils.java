package com.example.study.utils;

import com.example.study.model.entity.WxConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import okhttp3.HttpUrl;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class WxPayUtils {

    // Authorization:
    // GET - getToken("GET", httpurl, "")
    // POST - getToken("POST", httpurl, json)
    private static String schema = "WECHATPAY2-SHA256-RSA2048";
    // HttpUrl httpurl = HttpUrl.parse(url);

    public static String getToken(String method, HttpUrl url, String body) throws Exception{
        String nonceStr = "your nonce string";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        return schema + " mchid=\"" + WxConfig.mchId + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + WxConfig.mchSerialNo + "\","
                + "signature=\"" + signature + "\"";
    }

    private static String sign(byte[] message) throws Exception{
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getPrivateKey());
        sign.update(message);

        return Base64.getEncoder().encodeToString(sign.sign());
    }

    private static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }


    public void CreateOrder(Long total,String description,String openid) throws Exception {
        //请求URL
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String reqdata = "{"
                + "\"amount\": {"
                + "\"total\": " + total + ","
                + "\"currency\": \"CNY\""
                + "},"
                + "\"mchid\": \""+WxConfig.mchId+ "\","
                + "\"attach\": \""+description + "\","
                + "\"description\": \"" + description+  "\","
                + "\"notify_url\": \" ****此处为商品回调地址*********\","
                + "\"payer\": {"
                + "\"openid\": \"" + openid  +"\"" + "},"
                + "\"out_trade_no\": \"" +  openid + timeStamp + "\","
                + "\"appid\": \""+ WxConfig.APPID+"\"" + "}";
        StringEntity entity = new StringEntity(reqdata);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        HttpClient httpClient = initHttpClient();
        //完成签名并执行请求
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

    public void CreateOrderGitHub(Long total,String description,String openid) throws Exception {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid","1900009191")
                .put("appid", "wxd678efh567hg6787")
                .put("description", "Image形象店-深圳腾大-QQ公仔")
                .put("notify_url", "https://www.weixin.qq.com/wxpay/pay.php")
                .put("out_trade_no", "1217752501201407033233368018");
        rootNode.putObject("amount")
                .put("total", 1);
        rootNode.putObject("payer")
                .put("openid", "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");

        objectMapper.writeValue(bos, rootNode);

        httpPost.setEntity(new StringEntity(bos.toString("UTF-8")));
        HttpClient httpClient = initHttpClient();
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);

        String bodyAsString = EntityUtils.toString(response.getEntity());
        System.out.println(bodyAsString);
    }

    public HttpClient initHttpClient() throws Exception{
        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
        PrivateKey merchantPrivateKey = getPrivateKey();
        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(WxConfig.mchId, new PrivateKeySigner(WxConfig.mchSerialNo, merchantPrivateKey)),WxConfig.apiV3Key.getBytes("utf-8"));

        // 初始化httpClient
        HttpClient httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(WxConfig.mchId, WxConfig.mchSerialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();

        return httpClient;
    }


    // 加载商户私钥（privateKey：私钥字符串）
    private static PrivateKey getPrivateKey() throws Exception{
        PrivateKey merchantPrivateKey = PemUtil
                .loadPrivateKey(new ByteArrayInputStream(WxConfig.privateKey.getBytes("utf-8")));
        return merchantPrivateKey;
    }
}
