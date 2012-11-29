package com.thenetcircle.services.common;


import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public final class HttpClients {
  private static final Logger LOG = LoggerFactory.getLogger(HttpClients.class);

  private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";

  public static HttpClientBuilder get() {
    return new HttpClientBuilder();
  }

  public static class HttpClientBuilder {
    private HttpMethod method;
    private String keyStorePath;
    private String password;
    private String url;
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private List<NameValuePair> userParams = new ArrayList<>();
    private String charset = "UTF-8";

    public HttpClientBuilder usePost(final String url, final List<NameValuePair> postParams) {
      method = HttpMethod.POST;
      if (postParams != null) {
        this.userParams = postParams;
      }
      configureHttpClient(httpClient);
      this.url = url;
      return this;
    }

    public HttpClientBuilder addParameters(final KV<?, ?>... kvs) {
      if (userParams == null) {
        userParams = new ArrayList<>();
      }
      for (KV<?, ?> kv : kvs) {
        userParams.add(new BasicNameValuePair(kv.key.toString(), kv.value.toString()));
      }
      return this;
    }


    public HttpClientBuilder usePost(final String url) {
      return usePost(url, null);
    }

    public HttpClientBuilder useSSL(final String keyStorePath, final String password) throws Exception {
      this.keyStorePath = keyStorePath;
      this.password = password;
      configureSSL(httpClient, keyStorePath, password);
      return this;
    }

    public HttpClientBuilder charset(final String charset) {
      this.charset = charset;
      return this;
    }

    public String string() throws Exception {
      return getResult(STRING_RESPONSE_HANDLER);
    }

    public Code responseCode() {
      final String result = getResult(RESPONSE_CODE_RESPONSE_HANDLER);
      try {
        for (final Code code : Code.values()) {
          if (Integer.toString(code.code).equals(result)) {
            return code;
          }
        }
        return Code.Others;
      } catch (Exception e) {
        return Code.Others;
      }
    }

    private String getResult(final ResponseHandler<String> responseHandler) {
      try {
        if (method == HttpMethod.POST) {
          final HttpPost post = new HttpPost(url);
          if (userParams != null) {
            post.setEntity(new UrlEncodedFormEntity(userParams, charset));
          }

          return httpClient.execute(post, responseHandler);
        }
      } catch (Exception e) {
        throw new RuntimeException("the url " + url + " is not accessible", e);
      } finally {
        httpClient.getConnectionManager().shutdown();
      }
      return null;
    }

    private void configureSSL(final HttpClient httpClient,
                              final String keyStoreFile,
                              final String password) throws Exception {

      final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(loadKeyStore(keyStoreFile), password.toCharArray());

      final SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
      final Scheme sch = new Scheme("https", 443, socketFactory);
      httpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }

    private InputStream loadKeyStore(final String path) {
      final InputStream inputStream = Thread.currentThread()
                                            .getContextClassLoader()
                                            .getResourceAsStream(path);
      if (inputStream == null) {
        throw new IllegalArgumentException("keystore file cannot be found at " + path);
      }
      return inputStream;
    }


    private void configureHttpClient(final DefaultHttpClient httpClient) {
      httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
      httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
      //imitate the browser parameters
      httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
      httpClient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
      httpClient.setHttpRequestRetryHandler(DEFAULT_RETRY_HANDLER);
    }


  }

  public enum HttpMethod {
    GET, POST
  }


  private static final ResponseHandler<String> STRING_RESPONSE_HANDLER = new ResponseHandler<String>() {
    public String handleResponse(HttpResponse response) throws IOException {
      final HttpEntity entity = response.getEntity();
      if (entity != null) {
        return EntityUtils.toString(entity);
      } else {
        return null;
      }
    }
  };

  private static final ResponseHandler<String> RESPONSE_CODE_RESPONSE_HANDLER
    = new ResponseHandler<String>() {
    public String handleResponse(HttpResponse response) throws IOException {
      if (response != null && response.getStatusLine() != null) {
        return Integer.toString(response.getStatusLine().getStatusCode());
      } else {
        return null;
      }
    }
  };
  private static final HttpRequestRetryHandler DEFAULT_RETRY_HANDLER = new HttpRequestRetryHandler() {
    /*
     * return false if giving up retry
     */
    public boolean retryRequest(final IOException exception,
                                final int executionCount,
                                final HttpContext context) {

      if (executionCount >= 3) {
        return false;
      }
      if (exception instanceof NoHttpResponseException) {
        return true;
      }

      if (exception instanceof SSLHandshakeException) {
        return false;
      }
      final HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
      final boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
      if (!idempotent) {
        return true;
      }
      return false;
    }
  };


  public static enum Code {
    Created(201),
    Accepted(202),
    NotFound(404),
    Forbidden(403),
    OK(200),
    NoContent(204),
    ServerError(500),
    Others(1000);

    private int code;

    private Code(final int code) {
      this.code = code;
    }

    public int toInt() {
      return code;
    }
  }
}
