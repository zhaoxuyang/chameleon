package com.zxy.skin.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zxy.skin.demo.R;


public class Fragment1 extends Fragment {

    private View mView;

    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment1, container, false);
            mListView = mView.findViewById(R.id.fragmet1_listview);
            mListView.setAdapter(new MyListViewAdapter());
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                View view = getLayoutInflater().inflate(R.layout.fragment1_listview_item, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder();
                myViewHolder.textView = view.findViewById(R.id.textview);
                myViewHolder.button = view.findViewById(R.id.button);
                view.setTag(myViewHolder);
                convertView = view;
            }
            MyViewHolder myViewHolder = (MyViewHolder) convertView.getTag();
            myViewHolder.textView.setText("item:"+position);
            myViewHolder.button.setText("btn:"+position);
            return convertView;
        }
    }

    public static class MyViewHolder{
        public TextView textView;
        public Button button;
    }
}
