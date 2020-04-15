package com.covid.tracker.httpClient;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Component;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class OkHttp3RestClient {

	OkHttpClient okHttpClient;

	/**
	 * This method makes call to external API
	 * 
	 * @param request will be the okhttp request object
	 * @return the okhttp request
	 * @throws Exception 
	 * @throws IOException        will be thrown if exception occurred while calling
	 *                            the rest API
	 * @throws HydroPerfException
	 */
	public Response makeHttpCall(Request request) throws Exception {
		OkHttpClient client = getHttpClient();
		try {
			return client == null ? null : client.newCall(request).execute();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	private HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hostnameVerifier = (hostname, session) -> true;
		return hostnameVerifier;
	}

	private TrustManager[] getTrustManager() {
		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// accept all clients
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// accept all clients
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };
		return trustManager;
	}

	/**
	 * getHttpClient
	 *
	 * @return : OkHttpClient
	 * @throws Exception
	 */
	private OkHttpClient getHttpClient() throws Exception {
		OkHttpClient client = null;
		if (null != this.okHttpClient) {
			return this.okHttpClient;
		}
		try {
			final TrustManager[] trustAllCerts = getTrustManager();
			final SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory);
			builder.hostnameVerifier(getHostnameVerifier());
			builder.connectTimeout(200, TimeUnit.SECONDS);
			builder.writeTimeout(200, TimeUnit.SECONDS);
			builder.readTimeout(200, TimeUnit.SECONDS);
			// builder.proxy(new Proxy(Proxy.Type.HTTP, new
			// InetSocketAddress("sjc1intproxy01.crd.ge.com", 8080))); //$NON-NLS-1$
			client = builder.build();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return client;
	}

}