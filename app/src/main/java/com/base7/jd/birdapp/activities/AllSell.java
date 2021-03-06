package com.base7.jd.birdapp.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.base7.jd.birdapp.R;
import com.base7.jd.birdapp.adapters.BirdAdapter;
import com.base7.jd.birdapp.datamodels.Bird;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AllSell extends AppCompatActivity {

    private DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("rGRdX7ofp5YLgeCSt3TZGGfPKQl2").child("birdSelled");

    private Query query;

    private RecyclerView recyclerView;

    private ProgressDialog dialog;

    private ArrayList<Bird> arrayList = new ArrayList<>();

    private Spinner spnSearch;

    private String[] monthArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sell);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spnSearch = (Spinner) findViewById(R.id.spnSearch);
        ArrayAdapter<String> spnMonthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, monthArray);
        spnMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSearch.setAdapter(spnMonthAdapter);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        spnSearch.setSelection(month);

        spnSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                query = rf.orderByChild("monthSell").equalTo("" + i);
                upload();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dialog = new ProgressDialog(this);
        dialog.setMessage("please wait..!");
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem menuItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!(newText.equals(""))) {
                    onSearch(newText);
                    dialog.show();
                } else {
                    dialog.show();
                    upload();
                }

                return false;
            }
        });

        return true;
    }

    public void upload() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bird b = ds.getValue(Bird.class);
                    arrayList.add(b);
                }

                recyclerView.setAdapter(new BirdAdapter(arrayList, AllSell.this));
                dialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onSearch(String query) {
        Query q = rf.orderByChild("id").equalTo(query);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bird b = ds.getValue(Bird.class);
                    if (b.getStatus().equals("Sold"))
                        arrayList.add(b);
                }

                recyclerView.setAdapter(new BirdAdapter(arrayList, AllSell.this));
                dialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
