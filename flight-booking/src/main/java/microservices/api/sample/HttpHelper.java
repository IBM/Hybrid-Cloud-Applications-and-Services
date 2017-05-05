package microservices.api.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpHelper {

    private static String USERNAME;
    private static String PASSWORD;
    private static Boolean AUTH = false;
	
    private static SSLContext defaultSSLContext;

    public static void setAuth(String username, String password){
        USERNAME = username;
        PASSWORD = password;
    }

    public static void enableAuth(Boolean enable){
        AUTH = enable;
    }

    public static JsonNode connect(String url, String method, String payload) {
    	System.out.println("Calling url " + url);
        try {
            HttpURLConnection connection;
			try {
				connection = getConnection(url);
			} catch (KeyManagementException | NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new IOException (e1);
			}

            if(AUTH){
                String userPassword = USERNAME + ":" + PASSWORD;
                byte[] userpassword = userPassword.getBytes("UTF-8");
                String encoding = Base64.getEncoder().encodeToString(userpassword);
                connection.setRequestProperty("Authorization", "Basic " + encoding);
            }

            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(method.equals("POST") || method.equals("PUT"));
            connection.setRequestProperty("Accept", "application/json");

            //If applicable, write payload
            if (payload != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                Writer w = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                try {
                    w.write(payload);
                } catch (IOException e) {
                	e.printStackTrace();
                	throw e;
                } finally {
                    if (w != null) {
                        w.close();
                    }
                }
            }

            //Obtain response
            final int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                ObjectMapper objMapper = new ObjectMapper();
                return objMapper.readValue(connection.getInputStream(), JsonNode.class);
            } else {
                InputStream stream = connection.getErrorStream();
                stream = stream != null ? stream : connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[128];
                int charsRead = 0;
                while ((charsRead = reader.read(buffer)) > 0) {
                    sb.append(buffer, 0, charsRead);
                }
                RuntimeException error = new RuntimeException(sb.toString());
                throw error;
            }

        } catch (IOException e1) {
           e1.printStackTrace();
           return null;
        }
    }

    private static HttpURLConnection getConnection(String path) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {

                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(createDefaultSSLSocketFactory());
                }

                return connection;

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw e;
            } catch (KeyManagementException e) {
                e.printStackTrace();
                throw e;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static SSLSocketFactory createDefaultSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = createDefaultSSLContext();
        return sslContext.getSocketFactory();
    }

    //No need to synchronize here, since the change of multiple threads sending APIs to APIC at the same time from the same Liberty server is very highly unlikely
    private static SSLContext createDefaultSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        if (defaultSSLContext == null) {
            if (isIBMJVM()) {
                /*
                 * on IBM JVM, SSL_TLSv2 enables TLSv1.2, TLSv1.1, TLSv1.0.
                 * If SSL_TLSv2 is not supported, try TLSv1.1.
                 */
                defaultSSLContext = newSSLContext("SSL_TLSv2", "TLSv1.1");
            } else {
                /*
                 * on non-IBM JVM, TLSv1.2 enables TLSv1.2, TLSv1.1, TLSv1.0.
                 * If TLSv1.2 is not supported, try TLSv1.1.
                 */
                defaultSSLContext = newSSLContext("TLSv1.2", "TLSv1.1");
            }
        }
        return defaultSSLContext;
    }

    private static SSLContext newSSLContext(String primaryProtocol, String defaultProtocol) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(primaryProtocol);
        } catch (NoSuchAlgorithmException e) {
            sslContext = SSLContext.getInstance(defaultProtocol);
        }
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };

        sslContext.init(null, trustAllCerts, new SecureRandom());

        return sslContext;
    }

    private static boolean isIBMJVM() {
    	String vendor = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("java.vendor");
            }
        });
    	return vendor != null && vendor.toLowerCase().contains("ibm");
    }
    
}
