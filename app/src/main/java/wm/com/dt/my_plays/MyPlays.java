package wm.com.dt.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import wm.com.dt.app.BaseActivity;
import wm.com.dt.R;
import wm.com.dt.customviews.WMTextView;
import wm.com.dt.settings.SettingsActivity;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by nirav on 25-08-2014.
 */
public class MyPlays extends BaseActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String[] leftSliderData={"Indstillinger","Beskeder","Søg","Mine stykker","Inspiration","Guide"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plays);
        txtHeader.setText("Mine Stykker");
        initFields();
        initDrawer();
    }

    private void initFields(){
        drawer=(DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList=(ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setAdapter(new NavigationDrawerAdapter(MyPlays.this, leftSliderData));
        leftDrawerList.setOnItemClickListener(this);

    }
    private void initDrawer() {
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MyPlays.this, drawer,R.drawable.ic_drawer, R.string.open_drawer,R.string.close_drawer) {

            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }

        };
        drawer.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, leftSliderData[position], Toast.LENGTH_LONG).show();
        // Add your onclick logic here

        switch (position){

            case 0:

                Intent i = new Intent(MyPlays.this, SettingsActivity.class);
                startActivity(i);


                break;

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case android.R.id.home:
                actionBarDrawerToggle.onOptionsItemSelected(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    // Navigation Drawer Adapter
    public class NavigationDrawerAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;

        public NavigationDrawerAdapter(Context context, String[] leftSliderData) {
            this.context = context;
        }

        public int getCount() {

            return leftSliderData.length;

        }

        public Object getItem(int position) {
            return leftSliderData[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            WMTextView txtDrawerItem;
        }


        public View getView( final int position, View convertView,
                             ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer, parent,false);
                holder = new ViewHolder();
                holder.txtDrawerItem = (WMTextView) convertView.findViewById(R.id.txtDrawerItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtDrawerItem.setText(leftSliderData[position]);
            return convertView;

        }

    }




}
