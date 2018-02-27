package doseo.dodam.com.dodam.Connection;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 조현정 on 2018-02-23.
 */

public class GetHttpURLConnection {
    public String request(String _url, String _header_info_key,String _header_info_value, ContentValues _params) {
        try{
            URL reqUrl = new URL(_url);
            HttpURLConnection urlConn = (HttpURLConnection)reqUrl.openConnection();

            //url 연결 설정
            urlConn.setReadTimeout(10000);
            urlConn.setConnectTimeout(15000);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            //header값 판별
            if(_header_info_key != null)
                urlConn.setRequestProperty(_header_info_key, _header_info_value);

            //연결 요청 확인
            //실패 시 null을 리턴하고 메소드 종료.
            System.out.println("resCode : " + urlConn.getResponseCode());
            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            //요청한 URL의 출력물을 BufferReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String input;
            StringBuffer sb = new StringBuffer();
            while((input = reader.readLine()) != null){
                sb.append(input);
            }

            return sb.toString();

        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

}
