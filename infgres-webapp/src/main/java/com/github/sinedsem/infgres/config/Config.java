package com.github.sinedsem.infgres.config;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

@Configuration
public class Config {

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }


    @Bean
    public CloseableHttpClient getHttpClient() throws GeneralSecurityException { // todo: think about this exception
//        SSLContext sslContext = SSLContext.getInstance("ssl");
//        sslContext.init(null, new TrustManager[]{new DummyTrustManager()}, new SecureRandom());
//        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create()
//                .register("https", ssf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        cm.setDefaultConnectionConfig(connectionConfig);

        return HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

}
