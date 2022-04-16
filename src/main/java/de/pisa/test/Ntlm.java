package de.pisa.test;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hc.client5.http.auth.StandardAuthScheme.NTLM;

import java.util.concurrent.TimeUnit;

public class Ntlm {

    private static final String JSON_STRING = "{\"name\": \"Nick\",\"customer\": \"\",\"vendor\": \"\",\"territory\": \"\",\"phone\": \"\",\"email\": \"\",\"homepage\": \"\",\"lang\": \"\",\"address\": \"\",\"zip\": \"\",\"city\": \"\",\"country\": \"\"}"; 
    private static final Logger LOG = LoggerFactory.getLogger(Ntlm.class);
	
	private final static String URL = "https://<host>:<port>/<api-path>/<api-method>";
	private final static String HOST = "<fqn-api-hostname>";
	private final static int PORT = 8080;
	
	private final static String NTLM_USER = "ntlm_user";
	private final static String NTLM_PASSWORD = "ntlm_password";
	private final static String WORKSTATION = "<fqn-caller-workstation>";
	private final static String NTLM_DOMAIN = "<fqn-domain-name>";
	
	private final static int TIMEOUT_SECONDS = 10;
	
    public static void main(String[] args) {
        final Ntlm caller = new Ntlm();
        
        try {
	        final BasicCredentialsProvider credentials = caller.getCredentialsProvider();
	        final String result = caller.executePost(credentials);
	        
	        System.out.println("POST result");
	        System.out.println(result);
        } catch(Exception e) {
        	System.err.println(e);
        }
    }

    private BasicCredentialsProvider getCredentialsProvider() {
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        final HttpHost targetHost = new HttpHost(HOST, PORT);
    
        AuthScope authScope = new AuthScope(targetHost, null, NTLM);
        
        Credentials creds = new NTCredentials(NTLM_USER, NTLM_PASSWORD.toCharArray(), WORKSTATION, NTLM_DOMAIN);
        credentialsProvider.setCredentials(authScope, creds);
               
        return credentialsProvider;
    }

    private String executePost(final BasicCredentialsProvider credentials) throws Exception {
    	final RequestConfig config = RequestConfig.custom()
    			  .setConnectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    			  .setConnectionRequestTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)    			  
    			  .setResponseTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)                  
                  .build();
        final CloseableHttpClient client = HttpClientBuilder.create()
          .setDefaultCredentialsProvider(credentials)  
          .setDefaultRequestConfig(config)          
          .build();

        StringEntity requestEntity = new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON);
    
        final HttpPost post = new HttpPost(URL);
        post.setEntity(requestEntity);

        final CloseableHttpResponse response = client.execute(post);

        int statusCode = response.getCode();
        LOG.debug("Result status code: "+statusCode);
        
        final Header[] headers = response.getHeaders();
        LOG.debug("Headers: " + headers);
        
        return EntityUtils.toString(response.getEntity());
    }
}
