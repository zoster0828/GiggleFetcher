package infrastructure;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class GiggleHttpClient {
    static Logger logger = LogManager.getLogger("GiggleHttpClient");
    public static CloseableHttpClient httpClient = null;
    public GiggleHttpClient(){
        httpClient = HttpClients.createDefault();
        logger.info("Http client load success");
    }

    public static String find(String url){
        if(url.equals("")){
            return "";
        }
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(url);
        }catch(Exception e){
            logger.error("invalid url : "+url);
            return "";
        }

        httpGet.addHeader("User-Agent", "Mozila/5.0");
        httpGet.addHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.addHeader("accept-encoding","gzip, deflate");
        httpGet.addHeader("accept-language","en-US,en;q=0.8");
        httpGet.addHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            logger.error("IO Exception : "+e.toString());
            return "";
        }
        if(httpResponse.getStatusLine().getStatusCode() > 300 && httpResponse.getStatusLine().getStatusCode() != 401){
            logger.error("Something Wrong : "+httpResponse.getStatusLine().getStatusCode()+"\nurl : "+url);
            return "";
        }

        InputStream data = null;
        try {
            data = httpResponse.getEntity().getContent();
        } catch (IOException e) {
            logger.error(e.toString());
            return "";
        }

        String filePath = "src/main/resources/thumbnail/original_"+System.currentTimeMillis()+""+new Random().nextInt(1000)+".jpeg";
        try {
            BufferedInputStream bis = new BufferedInputStream(data);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            int inByte;
            while((inByte = bis.read()) != -1) bos.write(inByte);
            bis.close();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }
    public static String get(String url, HashMap<String, String> headers){
        HttpGet httpGet = new HttpGet(url);
        Iterator<String> iterator = headers.keySet().iterator();

        httpGet.addHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.addHeader("accept-encoding","gzip, deflate, br");
        httpGet.addHeader("accept-language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpGet.addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");

        while(iterator.hasNext()){
            String key = iterator.next();
            httpGet.addHeader(key, headers.get(key));
        }


        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        try {
            if(httpResponse.getHeaders("Content-Type")[0] != null && httpResponse.getHeaders("Content-Type")[0].getValue().contains("euc-kr")){
                reader = new BufferedReader(new InputStreamReader(
                        httpResponse.getEntity().getContent(),"euc-kr"));
            }else {
                reader = new BufferedReader(new InputStreamReader(
                        httpResponse.getEntity().getContent()));
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return "";
        }

        String inputLine = "";
        StringBuffer response = new StringBuffer();

        while (true) {
            try {
                if (!((inputLine = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.append(inputLine);
        }


        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = response.toString();

//        System.out.println(httpResponse.getHeaders("Content-Type")[0].getValue());

        return result;
    }
    public static String get(String url){
        return get(url, new HashMap<>());
    }
}
