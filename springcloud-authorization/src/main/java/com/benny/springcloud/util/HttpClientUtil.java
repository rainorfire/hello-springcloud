package com.benny.springcloud.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * apache httpclient工具类
 * 
 * @author cyj
 *
 */
public class HttpClientUtil {

    private static final Logger
        logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 支持https请求
     * 
     * @param url
     * @param keyStoreFile
     * @param keyPassWord
     * @param responseHandler
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    public static <T> T sslSender(String url, File keyStoreFile, String keyPassWord, ResponseHandler<T> responseHandler)
        throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
            .loadTrustMaterial(keyStoreFile, keyPassWord.toCharArray(), new TrustSelfSignedStrategy()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1"}, null,
            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {

            HttpGet httpget = new HttpGet(url);
            logger.info("Executing request " + httpget.getRequestLine());
            return httpclient.execute(httpget, responseHandler);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送post请求
     * 
     * @param url
     * @param formparams
     * @param connectTimeout 连接超时时间，单位毫秒
     * @param socketTimeout 请求获取数据的超时时间，单位毫秒
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse postSender(String url, List<NameValuePair> formparams, Integer connectTimeout,
        Integer socketTimeout) throws ClientProtocolException, IOException {
        RequestConfig requestConfig =
            RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        return postSender(url, formparams, requestConfig);
    }

    /**
     * 发送POST请求
     * 
     * @param url
     * @param formparams
     * @param requestConfig
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse postSender(String url, List<NameValuePair> formparams,
        RequestConfig requestConfig) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        if (formparams != null && !formparams.isEmpty()) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(entity);
        }
        httppost.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
        logger.info("Executing request " + httppost.getRequestLine());
        return httpclient.execute(httppost);
    }

    /**
     * 发送POST请求
     * 
     * @param url
     * @param formparams
     * @param requestConfig
     * @param headers
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse postSender(String url, List<NameValuePair> formparams,
        RequestConfig requestConfig, Header... headers) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        if (formparams != null && !formparams.isEmpty()) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(entity);
        }
        httppost.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
        if (headers != null && headers.length > 0) {
            httppost.setHeaders(headers);
        }
        logger.info("Executing request " + httppost.getRequestLine());
        return httpclient.execute(httppost);
    }

    /**
     * request body为字符串场景
     * 
     * @param url
     * @param requestBody
     * @param requestConfig
     * @param headers
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse postSender(String url, String requestBody, RequestConfig requestConfig,
        Header... headers) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        if (requestBody != null && !requestBody.isEmpty()) {
            StringEntity entity = new StringEntity(requestBody, "UTF-8");
            httppost.setEntity(entity);
        }
        httppost.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
        if (headers != null && headers.length > 0) {
            httppost.setHeaders(headers);
        }
        logger.info("Executing request " + httppost.getRequestLine());
        return httpclient.execute(httppost);
    }

    /**
     * 发送post请求
     * 
     * @param url
     * @param formparams
     * @param requestConfig
     * @param responseHandler
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static <T> T postSender(String url, List<NameValuePair> formparams, RequestConfig requestConfig,
        ResponseHandler<T> responseHandler) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            if (formparams != null && !formparams.isEmpty()) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
                httppost.setEntity(entity);
            }
            httppost.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
            logger.info("Executing request " + httppost.getRequestLine());
            return httpclient.execute(httppost, responseHandler);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送流数据的POST请求
     * 
     * @param url
     * @param fileName
     * @param inputStream
     * @param attachmentKey
     * @param requestConfig
     * @param responseHandler
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
//    public static <T> T postSenderWithInputstream(String url, String fileName, InputStream inputStream,
//        String attachmentKey, RequestConfig requestConfig, ResponseHandler<T> responseHandler)
//        throws ClientProtocolException, IOException {
//        if (inputStream == null) {
//            throw new IllegalArgumentException("参数异常：InputStream流不能为空");
//        }
//        if (StringUtils.isAnyBlank(url, fileName, attachmentKey)) {
//            throw new IllegalArgumentException("参数异常：url,fileName,attachmentKey都不能为空");
//        }
//        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//        InputStreamBody streamBody = new InputStreamBody(inputStream, fileName);
//        HttpEntity httpEntity = multipartEntityBuilder.addPart(attachmentKey, streamBody).build();
//        return basicPostSender(url, httpEntity, requestConfig, responseHandler);
//    }

    /**
     * 私有发送post请求方法
     * 
     * @param url
     * @param httpEntity
     * @param requestConfig
     * @param responseHandler
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static <T> T basicPostSender(String url, HttpEntity httpEntity, RequestConfig requestConfig,
        ResponseHandler<T> responseHandler) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(httpEntity);
            httppost.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
            logger.info("Executing request " + httppost.getRequestLine());
            return httpclient.execute(httppost, responseHandler);
        } finally {
            httpclient.close();
        }
    }

    /**
     * 发送get请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse getSender(String url) throws ClientProtocolException, IOException {
        return getSender(url, RequestConfig.DEFAULT);
    }

    /**
     * 发送GET请求
     * 
     * @param url
     * @param connectTimeout 连接超时时间，单位毫秒
     * @param socketTimeout 请求获取数据的超时时间，单位毫秒
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse getSender(String url, Integer connectTimeout, Integer socketTimeout)
        throws ClientProtocolException, IOException {
        RequestConfig requestConfig =
            RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        return getSender(url, requestConfig);
    }

    /**
     * 发送Get请求
     * 
     * @param url
     * @param requestConfig
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse getSender(String url, RequestConfig requestConfig)
        throws ClientProtocolException, IOException {
        CloseableHttpResponse responseBody = null;
        CloseableHttpClient httpclient = createSSLClientDefault();
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
        logger.info("Executing request " + httpget.getRequestLine());
        responseBody = httpclient.execute(httpget);
        return responseBody;
    }
    
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            //NoopHostnameVerifier类:  作为主机名验证工具，实质上关闭了主机名验证，它接受任何        
            //有效的SSL会话并匹配到目标主机。
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * get请求发送器
     * 
     * @param <T>
     * @param url
     * @param requestConfig
     * @param responseHandler
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static <T> T getSender(String url, RequestConfig requestConfig, ResponseHandler<T> responseHandler)
        throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
            logger.info("Executing request " + httpget.getRequestLine());
            return httpclient.execute(httpget, responseHandler);
        } finally {
            httpclient.close();
        }
    }

    /**
     * get发送请求
     * 
     * @param url
     * @param requestConfig
     * @param headers
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse getSender(String url, RequestConfig requestConfig, Header... headers)
        throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
        if (headers != null && headers.length > 0) {
            httpget.setHeaders(headers);
        }
        logger.info("Executing request " + httpget.getRequestLine());
        return httpclient.execute(httpget);
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        // getSender("http://blog.csdn.net/wangpeng047/article/details/19624529/",null,new
        // ResponseHandler<CloseableHttpResponse>(){
        //
        // @Override
        // public CloseableHttpResponse handleResponse(HttpResponse response) throws ClientProtocolException,
        // IOException {
        // int status = response.getStatusLine().getStatusCode();
        // if (status >= 200 && status < 300) {
        // HttpEntity entity = response.getEntity();
        // String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
        // System.out.println("*********************** response entity start *******************");
        // System.out.println(responseEntityStr);
        // System.out.println("*********************** response entity end *******************");
        // return (CloseableHttpResponse) response;
        // } else {
        // throw new ClientProtocolException("Unexpected response status: " + status);
        // }
        // }
        //
        // });

        // RequestConfig requestConfig = RequestConfig.custom().build();
        // CloseableHttpResponse response =
        // getSender("http://blog.csdn.net/wangpeng047/article/details/19624529/",requestConfig);
        // HttpEntity entity = response.getEntity();
        // String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
        // System.out.println(responseEntityStr);

        // CloseableHttpResponse response =
        // getSender("http://blog.csdn.net/wangpeng047/article/details/19624529/",1000,6000);
        // HttpEntity entity = response.getEntity();
        // String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
        // System.out.println(responseEntityStr);

        CloseableHttpResponse postSender = postSender("http://blog.csdn.net/wangpeng047/article/details/19624529/",
            null, null, new ResponseHandler<CloseableHttpResponse>() {

                @Override
                public CloseableHttpResponse handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
                        System.out.println("*********************** response entity start *******************");
                        System.out.println(responseEntityStr);
                        System.out.println("*********************** response entity end *******************");
                        return (CloseableHttpResponse) response;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            });
        System.out.println(JsonUtil.object2Json(postSender));

        // CloseableHttpResponse postSender = postSender("http://blog.csdn.net/wangpeng047/article/details/19624529/",
        // null, 6000, 6000);
        // HttpEntity entity = postSender.getEntity();
        // String responseEntityStr = entity != null ? EntityUtils.toString(entity) : null;
        // System.out.println(responseEntityStr);
    }

    private static InputStream getHeliRecordFileInputStream(CloseableHttpResponse closeableHttpResponse) {
        try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            return entity.getContent();
        } catch (IOException ioException) {
            logger.error("", ioException);
            return null;
        }
    }

    // {"code":0,"url":"https://videos.djtest.cn/transcode/changsha/customerbiz/call/20180614192728N000000003653915723293723a1tj58dj1528975647.mp3"}

    static class UploadCloudResult {

        public static final Integer SUCCESS_CODE = 0;

        private Integer code;// 0 成功，其他失败

        private String url; // 云上地址

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
