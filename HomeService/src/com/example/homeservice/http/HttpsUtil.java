package com.example.homeservice.http;


import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created with IntelliJ IDEA.
 * User: weiguo.ren
 * Date: 13-10-25
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class HttpsUtil {
    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
            HttpConnectionParams.setSoTimeout(params, 10 * 1000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    /**
     * GET
     *
     * @param uriString
     * @param //map
     * @return
     * @throws Exception
     */
    public static InputStream GET(String uriString) throws Exception {

        try {

            HttpClient httpClient = HttpsUtil.getNewHttpClient();
            HttpGet httpGet = new HttpGet(uriString);
            HttpResponse httpResponse = null;
            httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            httpResponse = httpClient.execute(httpGet);
            System.out.println(httpResponse.getStatusLine().getStatusCode() );
            return httpResponse.getEntity().getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("smartoffice.wafersystems.com", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("smartoffice.wafersystems.com", e.getMessage());
            return null;
        }
//        URL requestUrl = new URL(url);
//        HttpURLConnection urlConnection = (HttpURLConnection) requestUrl
//                .openConnection();
//        urlConnection.setRequestMethod("GET");
//        urlConnection.setConnectTimeout(5 * 1000);
//        if (urlConnection.getResponseCode() == 200) {
//            Util.print("服务器请求成功");
//            return urlConnection.getInputStream();
//        }
//        Util.print("服务器请求失败-错误代码:"+urlConnection.getResponseCode());
//        return null;
    }

}
