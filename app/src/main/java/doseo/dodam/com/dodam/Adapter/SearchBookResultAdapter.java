package doseo.dodam.com.dodam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import doseo.dodam.com.dodam.R;

/**
 * Created by Administrator on 2018-03-01.
 */

public class SearchBookResultAdapter extends BaseAdapter{

    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchBookResultAdapter(List<String> list, Context context){
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_searchbook_listview, null);

            viewHolder = new ViewHolder();
            viewHolder.searchBookTitle = convertView.findViewById(R.id.search_book_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.searchBookTitle.setText(list.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView searchBookTitle;
    }

}

