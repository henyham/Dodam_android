package doseo.dodam.com.dodam.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import doseo.dodam.com.dodam.R;

/**
 * Created by Administrator on 2018-03-01.
 */

public class SearchBookResultAdapter extends BaseAdapter{

    private Context context;
    private List<JSONObject> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;
    private Bitmap bitmap;

    public SearchBookResultAdapter(List<JSONObject> list, Context context){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_searchbook_listview, null);

            viewHolder = new ViewHolder();
            viewHolder.searchBookCover = convertView.findViewById(R.id.search_book_cover);
            viewHolder.searchBookTitle = convertView.findViewById(R.id.search_book_title);
            viewHolder.searchBookAuthor = convertView.findViewById(R.id.search_book_author);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        try {
            Thread mThread = new Thread(){
                @Override
                public void run(){
                    try{
                        String tmpUrl = list.get(position).getString("bookCover");

                        int i = tmpUrl.indexOf("?");
                        String tmp_image_url = tmpUrl.substring(i+1);
                        tmpUrl = "http://t1.daumcdn.net/thumb/R155x225/?" + tmp_image_url;

                        URL url = new URL(tmpUrl);

                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    }catch(MalformedURLException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();

            try{
                mThread.join();
                //Log.d("profile url6: ", currentUser.getUserProfile());
                viewHolder.searchBookCover.setImageBitmap(bitmap);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            viewHolder.searchBookTitle.setText(list.get(position).getString("title"));
            viewHolder.searchBookAuthor.setText(list.get(position).getString("author"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView searchBookCover;
        TextView searchBookTitle;
        TextView searchBookAuthor;
    }

}

