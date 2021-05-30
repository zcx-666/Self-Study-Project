package com.example.study;


import com.alibaba.fastjson.JSONObject;
import com.example.study.model.entity.User;
import com.example.study.model.entity.WxConfig;
import com.example.study.utils.JwtUtils;
import com.example.study.utils.WxPayUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import okhttp3.HttpUrl;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static com.example.study.model.entity.WxConfig.*;

@Data
@Slf4j
//@SpringBootTest
class StudyApplicationTests {
    private static final Integer MIN_RESERVE_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("min_reserve_time"));
    private static final Integer WORK_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_hour"));
    private static final Integer WORK_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("work_minute"));
    private static final Integer CLOSING_HOUR = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_hour"));
    private static final Integer CLOSING_MINUTE = Integer.valueOf(ResourceBundle.getBundle("string").getString("closing_minute"));
    private static final String cookie_name = ResourceBundle.getBundle("string").getString("cookie_name");


    @Test
    void contextLoads() {
        Student student = new Student();
        List<Student> students = new ArrayList<>();
        System.out.println(students);
        students.add(student);
        students.add(student);
        students.add(student);
        System.out.println(students);
        System.out.println(student);
        log.error("异常数据:{},{}", student, student);
        Student a = new Student();
        Student b = new Student();
        System.out.println(a.getName() == b.getName());
        a.setName("123321");
        b.setName("123321");
        System.out.println(a.getName() == b.getName());
        System.out.println(a == b);
    }

    @Test
    void pp() {
        User a = new User();
        User b = new User();
        a.setOpenid("123");
        a.setAvatar("123123");
        System.out.println(a);
        System.out.println(b);
        b.copyUser(a);
        System.out.println(b);
    }


    @Test
    void judgeUser() {
        // cookie寿命无限
    }

    public User selectUserByCookie(@NotNull HttpServletRequest servletRequest) {
        User user = null;
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length <= 0)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookie_name)) {
                String userCookie = JwtUtils.getCookie(cookie.getValue());
                if (userCookie == null) {
                    return null;
                } else {
                    //user = userMapper.selectUserByCookie(userCookie);
                }
            }
        }
        return user;
    }


    @Test
    void sss() {
        HttpUrl url = HttpUrl.parse("https://api.mch.weixin.qq.com/v3/certificates");
        System.out.println(url);
        System.out.println(url.encodedPath());
    }

    public static PrivateKey getPrivateKey(String filename) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            System.out.println(privateKey);
            System.out.println(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }

    @Test
    void getPrivateKey() throws Exception {
    }

    @Test
    void after() throws IOException {
       /* WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                .withWechatpay(wechatpayCertificates);
// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient

// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
        HttpClient httpClient = builder.build();

// 后面跟使用Apache HttpClient一样
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.addHeader("Authorization", "123");//TODO

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
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);

        String bodyAsString = EntityUtils.toString(response.getEntity());
        System.out.println(bodyAsString);*/
    }

    @Data
    class Student {
        public static final int DAY = 0;
        private String name = "zcx";
        private Integer id = 12;
    }

    @Data
    class People extends Student {
        private String name;
        private Integer age = 12;
		/*public People(){
			super();
		}*/
    }

    @Test
    void setup(){
    }

    @Test
    void createOrder() throws Exception {
    }

    /*
    * String reqdata = "{"
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
                + "\"out_trade_no\": \"" +  timeStamp + "\","
                + "\"goods_tag\": \"WXG\","
                + "\"appid\": \""+ WxConfig.APPID+"\"" + "}";*/
    @Test
    void jsonTest() throws Exception {
        String reqdata = "{"
                + "\"amount\": {"
                + "\"total\": 100,"
                + "\"currency\": \"CNY\""
                + "},"
                + "\"mchid\": \"1900006891\","
                + "\"description\": \"Image形象店-深圳腾大-QQ公仔\","
                + "\"notify_url\": \"https://www.weixin.qq.com/wxpay/pay.php\","
                + "\"payer\": {"
                + "\"openid\": \"o4GgauE1lgaPsLabrYvqhVg7O8yA\"" + "},"
                + "\"out_trade_no\": \"1217752501201407033233388881\","
                + "\"goods_tag\": \"WXG\","
                + "\"appid\": \"wxdace645e0bc2c424\"" + "}";
        JSONObject object = new JSONObject();
        JSONObject amount = new JSONObject();
        amount.put("total", 100);
        amount.put("currency", "CNY");
        object.put("amount", amount);
        object.put("mchid", "1900006891");
        object.put("description", "Image形象店-深圳腾大-QQ公仔");
        object.put("notify_url", "https://www.weixin.qq.com/wxpay/pay.php");
        JSONObject payer = new JSONObject();
        payer.put("openid", "o4GgauE1lgaPsLabrYvqhVg7O8yA");
        object.put("payer", payer);
        object.put("out_trade_no", "1217752501201407033233388881");
        object.put("goods_tag", "WXG");
        object.put("appid", "wxdace645e0bc2c424");
        System.out.println(object);
        System.out.println(reqdata);


    }

    @Test
    void urlTest() {
        System.out.println(System.currentTimeMillis());
    }
}
