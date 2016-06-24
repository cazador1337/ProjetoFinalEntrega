package com.example.joao.projectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.joao.projectfinal.controller.SerieManager;
import com.example.joao.projectfinal.custom.SeriesAdapter;
import com.example.joao.projectfinal.models.Serie;

import java.util.ArrayList;
import java.util.List;

public class RootActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener,
        SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private SerieManager manager;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Serie.open(getApplicationContext(), Serie.DATA_BASE, 1, Serie.class);
        listView = (ListView) findViewById(R.id.list_series);
        manager = new SerieManager(listView, this);
        listView.setOnItemClickListener(this);
        manager.update();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.root, menu);
        SearchView view = (SearchView) menu.findItem(R.id.action_search).getActionView();
        view.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new: {
                Intent intent = new Intent(this, ModifyActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.filter_complete: {
                invertValueRadio(id);
                break;
            }
            case R.id.filter_plan: {
                invertValueRadio(id);
                break;
            }
            case R.id.filter_watching: {
                invertValueRadio(id);
                break;
            }
        }

        manager.setOnlyWatching(isSelected(R.id.filter_watching));
        manager.setOnlyComplete(isSelected(R.id.filter_complete));
        manager.setOnlyPlanToWatch(isSelected(R.id.filter_plan));

        manager.update();
        return true;
    }

    private boolean isSelected(int id){
        RadioButton menuItem = (RadioButton) findViewById(id);
        return menuItem.isChecked();
    }

    private void invertValueRadio(int id) {
        AppCompatRadioButton rb = (AppCompatRadioButton) findViewById(id);
        rb.setChecked(!rb.isChecked());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(!query.isEmpty()){
            manager.search(query);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            manager.update();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ModifyActivity.class);
        intent.putExtra(Serie.ID, manager.get(position));
        startActivityForResult(intent, 0);
    }
}
