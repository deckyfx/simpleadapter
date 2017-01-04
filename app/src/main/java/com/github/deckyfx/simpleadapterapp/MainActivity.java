package com.github.deckyfx.simpleadapterapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deckyfx.simpleadapter.AdapterDataSet;
import com.github.deckyfx.simpleadapter.AdapterGroupItem;
import com.github.deckyfx.simpleadapter.AdapterItem;
import com.github.deckyfx.simpleadapter.ExpandableAdapter;
import com.github.deckyfx.simpleadapter.ExpandableAdapterDataSet;
import com.github.deckyfx.simpleadapter.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList a = new ArrayList();
        ArrayList<String> b = new ArrayList<String>();
        AdapterDataSet<TestItem> c = new AdapterDataSet<TestItem>();
        AdapterDataSet d = new AdapterDataSet();
        c.add(new TestItem());
        d.add(new AdapterItem("A"));
        d.add(new AdapterItem("B"));
        d.add(new AdapterItem("C"));
        TestItem e = c.get(0);
        AdapterItem f = d.get(0);
        c.size();
        SimpleAdapter<TestItem> g = new SimpleAdapter<TestItem>(this, c);
        TestItem h = g.getItem(0);
        AdapterDataSet<TestItem> i = c.find("0");
        g.notifyDataSetChanged();
        ListView listview = (ListView) this.findViewById(R.id.listView);
        listview.setAdapter(g);

        ExpandableAdapterDataSet<AdapterGroupItem, TestItem> o = new ExpandableAdapterDataSet<AdapterGroupItem, TestItem>();
        AdapterGroupItem<TestItem> k = new AdapterGroupItem<TestItem>();
        o.add(k);
        new ExpandableAdapter(this, o);
        AdapterGroupItem<TestItem> j = o.get(0);
        //j.childrens.get(0);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsontext = "{\"text\":\"Some text\",\"text2\":\"Some text 2\",\"value\":1234,\"data\":true,\"innerdata\":\"Inner\",\"inneritem\":{\"lol\":1203},\"list\":[{\"lol\":0},{\"lol\":1},{\"lol\":2}],\"_date\":\"2016-11-10\"}";
            TestItem item = mapper.readValue(jsontext, TestItem.class);
            String s = item.text;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static class TestItem extends AdapterItem {
        public String innerdata = "";
        public TestInnerItem inneritem = new TestInnerItem();
        public ArrayList<TestInnerItem> list = new ArrayList<TestInnerItem>();
    }

    public static class TestInnerItem extends AdapterItem {
        public int lol = 0;

    }
}
