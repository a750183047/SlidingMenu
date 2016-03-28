package com.yan.slidingmenu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

public class MainActivity extends AppCompatActivity {

    private DragLayout mDragLayout;
    private ListView mLeftList;

    private static final String[] setting = {"set1","set2","set3","set4","set5","set6","set7","set8","set9","set10","set11","set12",
    "set13","set14"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLeftList = (ListView) findViewById(R.id.left_list);
        mLeftList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,setting){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView mText = (TextView) view.findViewById(android.R.id.text1);
                mText.setTextColor(Color.WHITE);
                return view;
            }
        });

        mLeftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.showToast(MainActivity.this,"position"+position);
            }
        });



    }
}
