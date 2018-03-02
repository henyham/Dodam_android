package doseo.dodam.com.dodam.Connection;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by 조현정 on 2018-02-23.
 */

public class PostHttpURLConnection {
    public String request(String _url, ContentValues _params){
        //HttpURLConnection 참조 변수
        HttpURLConnection urlConn = null;
        //URL 뒤에 붙여서 보낼 파라미터
        StringBuffer sbParams = new StringBuffer();

        /**
         * 1. StringBuffer에 파라미터 연결
         */
        //보낼 데이터가 없으면 파라미터 비움
        if(_params == null) sbParams.append("");
            //보낼 데이터가 있으면 파라미터 채움
        else{
            //파라미터 2개 이상이면 &가 필요하므로 스위칭할 변수 생성
            boolean isAnd = false;
            //파라미터 키와 값
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                //파라미터가 두개 이상일 때 &사용
                if(isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                //파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &붙임
                if(!isAnd)
                    if(_params.size() >= 2) isAnd = true;
            }
            Log.d("sbParams",sbParams.toString());
        }

        /**
         * 2. HttpURLConnection을 통해 web의 데이터 가져옴
         */
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection)url.openConnection();

            //[2-1]. urlConn 설정
            urlConn.setRequestMethod("POST");   //url요청에 대한 메소드 설정
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            //[2-2]. parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));  //출력 스트림에 출력
            os.flush(); // 버퍼링된 모든 출력 바이트 강제 실행
            os.close(); //출력 스트림을 닫고 모든 시스템 자원 해제

            //[2-3]. 연결 요청 확인
            //실패시 null을 리턴하고 메서드 종료
            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            //[2-4]. 읽어온 결과물 리턴
            // 요청한 URL의 출력물을 BufferedReader로 받음
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            //출력물의 라인과 그 합에 대한 변수
            String line;
            String page ="";

            //라인을 받아와 합침.
            while((line = reader.readLine()) != null){
                page += line;
            }

            return page;
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if(urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    public String requestJSON(String _url, ContentValues _params){
        //HttpURLConnection 참조 변수
        HttpURLConnection urlConn = null;
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        /**
         * 2. HttpURLConnection을 통해 web의 데이터 가져옴
         */
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection)url.openConnection();

            //[2-1]. urlConn 설정
            urlConn.setRequestMethod("POST");   //url요청에 대한 메소드 설정
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            urlConn.setRequestProperty("Content-Type","application/json");
            urlConn.setRequestProperty("Accept","application/json");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            //[2-2]. parameter 전달 및 데이터 읽어오기.
            JSONObject job = new JSONObject();

            //파라미터 키와 값
            String key;
            String value;

            try{
                for(Map.Entry<String, Object> parameter : _params.valueSet()){
                    key = parameter.getKey();
                    value = parameter.getValue().toString();

                    //파라미터가 두개 이상일 때 &사용
                    job.put(key,value);

                }
            } catch(JSONException e){
                e.printStackTrace();
            }

            os = urlConn.getOutputStream();
            os.write(job.toString().getBytes());  //출력 스트림에 출력
            os.flush(); // 버퍼링된 모든 출력 바이트 강제 실행
            os.close(); //출력 스트림을 닫고 모든 시스템 자원 해제

            //[2-3]. 연결 요청 확인
            //실패시 null을 리턴하고 메서드 종료
            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            //[2-4]. 읽어온 결과물 리턴
            // 요청한 URL의 출력물을 BufferedReader로 받음
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            //출력물의 라인과 그 합에 대한 변수
            String line;
            String page ="";

            //라인을 받아와 합침.
            while((line = reader.readLine()) != null){
                page += line;
            }

            return page;
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if(urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }


}
