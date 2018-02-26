package doseo.dodam.com.dodam.Object;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 조현정 on 2018-02-05.
 */

public class Book {

    private String isbn;
    private String title;
    private String category_name;
    private String book_cover;
    private ArrayList<String> book_authors = new ArrayList<String>();
    private String publisher;
    private int fixed_price;
    private String pub_date;    //자료형 체크하기
    private String review;      // 사용자 서평

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public ArrayList<String> getBook_authors() {return book_authors;}

    public void setBook_authors(ArrayList<String> book_authors) {this.book_authors = book_authors;}

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getFixed_price() {
        return fixed_price;
    }

    public void setFixed_price(int fixed_price) {
        this.fixed_price = fixed_price;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setBookAttribute(JSONObject j){
        try{
            this.title = j.getJSONArray("documents").getJSONObject(0).getString("title");
            this.category_name = j.getJSONArray("documents").getJSONObject(0).getString("category");
            this.book_cover = j.getJSONArray("documents").getJSONObject(0).getString("thumbnail");

            int i=0;
            while(j.getJSONArray("documents").getJSONObject(0).getJSONArray("authors").length() <i){
                book_authors.add(i,j.getJSONArray("documents").getJSONObject(0).getJSONArray("authors").getString(i));
                i++;
            }
            this.publisher =j.getJSONArray("documents").getJSONObject(0).getString("publisher");
            this.fixed_price = Integer.parseInt(j.getJSONArray("documents").getJSONObject(0).getString("price"));
            this.pub_date = j.getJSONArray("documents").getJSONObject(0).getString("datetime").substring(0,10);

        }catch(JSONException e){
            e.printStackTrace();
        }

    }
}
