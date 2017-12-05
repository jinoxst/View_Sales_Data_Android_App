package jp.ne.jinoxst.mas.itg.uriage.util.https;

import java.security.KeyStore;

import jp.ne.jinoxst.mas.itg.uriage.util.Constant;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.net.Uri;

public class HttpsClientConnector {
    private Uri.Builder builder;

    public HttpsClientConnector(String service, String method){
        builder = new Uri.Builder();
        builder.scheme(Constant.HTTPS);
        builder.encodedAuthority(Constant.SRVER_URL);
        builder.path(Constant.BASE_PATH + "/" + service + "/" + method);
    }

    public HttpGet getRequest(){
        return new HttpGet(builder.build().toString());
    }

    public void setParameter(String key, String value){
        builder.appendQueryParameter(key, value);
    }

//    public DefaultHttpClient getHttpClient(){
//        SchemeRegistry schreg = new SchemeRegistry();
//        SocketFactory socketFactory = new EasySSLSocketFactory();
//        schreg.register(new Scheme(Constant.HTTPS, socketFactory, Constant.HTTPS_PORT));
//        HttpParams params = new BasicHttpParams();
//        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
//        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
//        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//        //        HttpConnectionParams.setSocketBufferSize(params, 4096);//ソケットバッファサイズ 4KB
//        //        HttpConnectionParams.setSoTimeout(params, 20000);//ソケット通信タイムアウト20秒
//        //        HttpConnectionParams.setConnectionTimeout(params, 20000);//HTTP通信タイムアウト20秒
//        //        HttpProtocolParams.setContentCharset(params, "UTF-8");//文字コードをUTF-8と明示
//        //        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);//HTTP1.1
//
//        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, schreg);
//        DefaultHttpClient httpClient = new DefaultHttpClient(connManager, params);
//
//        return httpClient;
//    }

    public DefaultHttpClient getHttpClient(){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
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
}
