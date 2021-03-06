import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
 
public class HttpClientExample {
 
	private final String USER_AGENT = "Mozilla/5.0";
 
	public static void main(String[] args) throws Exception {
 
		HttpClientExample http = new HttpClientExample();
  
		System.out.println("\nTesting 2 - Send Http POST request");
		http.sendPost();
 
	}
 
	// HTTP POST request
	private void sendPost() throws Exception {
 
		String url = "http://localhost:8080/export.wcap";
 
		//HttpClient client = new DefaultHttpClient();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
 
		// add header
		post.setHeader("User-Agent", USER_AGENT);
 
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("id", "KYacxNZ6QQc"));
		urlParameters.add(new BasicNameValuePair("calid", "adamm"));
		urlParameters.add(new BasicNameValuePair("content-out", "text/xml"));
 
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
 
		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());
 
		BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
 
		System.out.println(result.toString());
 
	}
 
}
