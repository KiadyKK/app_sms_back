package org.acme.middleware;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Map;

@ApplicationScoped
public class HttpClientService {

    private static final Logger LOG = Logger.getLogger(HttpClientService.class);

    @ConfigProperty(name = "http.request.max.retry")
    Integer maxRetries;

    public String get(String url) {
        try (CloseableHttpClient client = HttpClients.custom()
                .setRetryHandler(retryHandler())
                .build()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                } else {
                    return "";
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending GET request", e);
            throw new RuntimeException(e.getCause());
        }
    }

    public String post(String url, JsonObject data) {
        return post(url, data, new JsonObject());
    }

    public String post(String url, JsonObject data, JsonObject headers) {
        try (CloseableHttpClient client = HttpClients.custom()
                .setRetryHandler(retryHandler())
                .build()) {
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(data.toString()));
            for (Map.Entry<String, Object> entry : headers) {
                request.addHeader(entry.getKey(), entry.getValue().toString());
            }
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                } else {
                    return "";
                }
            }
        } catch (IOException e) {
            LOG.error("Error while sending POST request", e);
            throw new RuntimeException(e.getCause());
        }
    }

    private HttpRequestRetryHandler retryHandler() {
        return (exception, executionCount, context) -> {
            if (executionCount >= maxRetries) {
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            if (exception instanceof UnknownHostException) {
                return false;
            }
            if (exception instanceof SSLException) {
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }

}
