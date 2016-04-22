package com.perk.perksdk;

/**
 * <h1>Web Service</h1>
 * This class helps in making POST and GET asynchronous task, to the API
 *
 * @author Perk.com
 * @version 1.0
 * @since 2014-12-01
 */

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebService {

    public static final WebServiceResponse postAPIResponse(String _url,
                                                           String _params) {

        try {

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient httpClient = new DefaultHttpClient(params);
            HttpPost post = new HttpPost(_url);

            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Device-Info", Utils.m_sDeviceInfo);
            post.setHeader("User-Agent", Utils.m_sUserAgent);
            post.setEntity(new StringEntity(_params));

            HttpResponse response = httpClient.execute(post);
            int i = response.getStatusLine().getStatusCode();

            if (response.getEntity() == null) {
                return new WebServiceResponse("", i);
            }
            else {
                return new WebServiceResponse(EntityUtils.toString(response.getEntity()), i);
            }

        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new WebServiceResponse("", 500);

    }

    public static final WebServiceResponse putAPIResponse(String _url,
                                                          String _params) {

        try {

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient httpClient = new DefaultHttpClient(params);
            HttpPut put = new HttpPut(_url);

            put.setHeader("Content-Type", "application/x-www-form-urlencoded");
            put.setHeader("Device-Info", Utils.m_sDeviceInfo);
            put.setHeader("User-Agent", Utils.m_sUserAgent);
            put.setEntity(new StringEntity(_params));

            HttpResponse response = httpClient.execute(put);
            int i = response.getStatusLine().getStatusCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String _response = sb.toString();
            return new WebServiceResponse(_response, i);

        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new WebServiceResponse("", 500);

    }

    public static final WebServiceResponse getAPIResponse(String _url) {

        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient httpClient = new DefaultHttpClient(params);
            HttpGet get = new HttpGet(_url);

            get.setHeader("Content-Type", "application/x-www-form-urlencoded");
            get.setHeader("Device-Info", Utils.m_sDeviceInfo);
            get.setHeader("User-Agent", Utils.m_sUserAgent);
            HttpResponse response = httpClient.execute(get);
            int i = response.getStatusLine().getStatusCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String _response = sb.toString();

            return new WebServiceResponse(_response, i);

        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new WebServiceResponse("", 500);

    }

}
