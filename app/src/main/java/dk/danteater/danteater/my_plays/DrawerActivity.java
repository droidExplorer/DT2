package dk.danteater.danteater.my_plays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mvnordic.mviddeviceconnector.DeviceSecurity;

import dk.danteater.danteater.Messages.FragmentMessage;
import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.drawer.ActionBarDrawerToggle;
import dk.danteater.danteater.drawer.DrawerArrowDrawable;
import dk.danteater.danteater.guide.FragmentGuide;
import dk.danteater.danteater.login.LoginActivity;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.model.ComplexPreferences;
import dk.danteater.danteater.search.FragmentSearch;
import dk.danteater.danteater.settings.SettingsFragment;
import dk.danteater.danteater.tab_inspiration.FragmnentInspiration;



/**
 * Created by nirav on 25-08-2014.
 */
public class DrawerActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    NavigationDrawerAdapter navigationDrawerAdapterForStudent;
    NavigationDrawerAdapter navigationDrawerAdapterForTeacher;
    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
//    private ActionBarDrawerToggle actionBarDrawerToggle;
    private User user;
    private String[] leftSliderDataForTeacher = {"Søg", "Mine stykker", "Beskeder", "Dramaøvelser", "Indstillinger", "Hjælp"};
    private String[] leftSliderDataForStudent = {"Mine stykker", "Beskeder", "Dramaøvelser", "Indstillinger"};
    private boolean isPupil;
    private WMTextView logoutImage, logoutUser, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_plays);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(DrawerActivity.this, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        isPupil = user.checkPupil(user.getRoles());

        logoutImage = (WMTextView) findViewById(R.id.logoutImage);
        logoutButton = (WMTextView) findViewById(R.id.logoutButton);
        logoutUser = (WMTextView) findViewById(R.id.logoutUser);


        //Load My Places First
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if(isPupil) {
            FragmentMyPlayPupil fragmentMyPlayPupil = FragmentMyPlayPupil.newInstance("", "");

            if (manager.findFragmentByTag("my_plays_pupil") == null) {
                ft.replace(R.id.main_content, fragmentMyPlayPupil, "my_plays_pupil").commit();
            }
        } else {
            FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");
            if (manager.findFragmentByTag("my_plays") == null) {
                ft.replace(R.id.main_content, fragmentMyPlay, "my_plays").commit();
            }
        }

        // header
        txtHeader.setText("Mine stykker");
        initFields();
        initDrawer();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("onResume ","in drawer activty");
    }

    private void initFields() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        navigationDrawerAdapterForStudent=new NavigationDrawerAdapter(DrawerActivity.this, leftSliderDataForStudent);
       navigationDrawerAdapterForTeacher=new NavigationDrawerAdapter(DrawerActivity.this, leftSliderDataForTeacher);
        if (isPupil) {
            leftDrawerList.setAdapter(navigationDrawerAdapterForStudent);
        } else {
            leftDrawerList.setAdapter(navigationDrawerAdapterForTeacher);
        }

        leftDrawerList.setOnItemClickListener(this);

    }

    private void initDrawer() {

        //logout contents
        logoutUser.setText(user.getFirstName().toString()+" "+ user.getLastName().toString());
        logoutImage.setText(user.getFirstName().toString().substring(0, 2));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("settings",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();


//                SharedPreferences preferencesTutorial = getSharedPreferences("show_tutorial",MODE_PRIVATE);
//                SharedPreferences.Editor editorTutorial = preferencesTutorial.edit();
//                editorTutorial.putBoolean("isShowTutorial", true);
//                editorTutorial.commit();

                DeviceSecurity   m_device_security = new DeviceSecurity(DrawerActivity.this);
                m_device_security.releaseDeviceRegistration();
                Intent i = new Intent(DrawerActivity.this,LoginActivity.class );
                startActivity(i);
                finish();

            }
        });

        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(DrawerActivity.this, drawer, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {
//
//            public void onDrawerClosed(View view) {
//
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                if (isPupil) {
//                    navigationDrawerAdapterForStudent.notifyDataSetChanged();
//                } else {
//                    navigationDrawerAdapterForTeacher.notifyDataSetChanged();
//                }
//            }
//
//        };
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,drawerArrow, R.string.drawer_open,R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                                if (isPupil) {
                    navigationDrawerAdapterForStudent.notifyDataSetChanged();
                } else {
                    navigationDrawerAdapterForTeacher.notifyDataSetChanged();
                }
//                invalidateOptionsMenu();
            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Add your onclick logic here
        drawer.closeDrawers();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //drawer items for pupil
        if (isPupil) {
            switch (position) {
                case 0:
                    FragmentMyPlayPupil fragmentMyPlayPupil = FragmentMyPlayPupil.newInstance("", "");

                    if (manager.findFragmentByTag("my_plays_pupil") == null) {
                        ft.replace(R.id.main_content, fragmentMyPlayPupil, "my_plays_pupil").commit();
                    }
                    txtHeader.setText("Mine stykker");
                    break;

                case 1:
                    FragmentMessage fragmentMessage = FragmentMessage.newInstance("", "");
                    if (manager.findFragmentByTag("message") == null) {
                        ft.replace(R.id.main_content, fragmentMessage, "message").commit();
                    }
                    txtHeader.setText("Beskeder");
                    break;

                case 2:

                    Bundle args = new Bundle();
                    args.putString("inspirationType","drawer");

                    FragmnentInspiration fragmentExcerciseForStudent = FragmnentInspiration.newInstance("", "");
                    fragmentExcerciseForStudent.setArguments(args);
                    if (manager.findFragmentByTag("excercise_student") == null) {
                        ft.replace(R.id.main_content, fragmentExcerciseForStudent, "excercise_student").commit();
                    }
                    txtHeader.setText("Dramaøvelser");

                    break;

                case 3:
                    SettingsFragment fragmentSettings = SettingsFragment.newInstance("", "");
                    if (manager.findFragmentByTag("Settings") == null) {
                        ft.replace(R.id.main_content, fragmentSettings, "Settings").commit();
                    }
                    txtHeader.setText("Indstillinger");
                    break;
            }

        } else { //drawer items for teacher
            switch (position) {
                case 0:
                    FragmentSearch fragmentSearch = FragmentSearch.newInstance("", "");
                    if (manager.findFragmentByTag("search") == null) {
                        ft.replace(R.id.main_content, fragmentSearch, "search").commit();
                    }
                    txtHeader.setText("Søg skuespil");
                    break;

                case 1:
                    FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");
                    if (manager.findFragmentByTag("my_plays") == null) {
                        ft.replace(R.id.main_content, fragmentMyPlay, "my_plays").commit();
                    }
                    txtHeader.setText("Mine stykker");
                    break;

                case 2:
                    FragmentMessage fragmentMessage = FragmentMessage.newInstance("", "");
                    if (manager.findFragmentByTag("message") == null) {
                        ft.replace(R.id.main_content, fragmentMessage, "message").commit();
                    }
                    txtHeader.setText("Beskeder");
                    break;

                case 3:
                    Bundle args = new Bundle();
                    args.putString("inspirationType","drawer");

                    FragmnentInspiration fragmentExcerciseForStudent = FragmnentInspiration.newInstance("", "");
                    fragmentExcerciseForStudent.setArguments(args);
                    if (manager.findFragmentByTag("excercise_student") == null) {
                        ft.replace(R.id.main_content, fragmentExcerciseForStudent, "excercise_student").commit();
                    }
                    txtHeader.setText("Dramaøvelser");
                    break;

                case 4:
                    SettingsFragment fragmentSettings = SettingsFragment.newInstance("", "");
                    if (manager.findFragmentByTag("Settings") == null) {
                        ft.replace(R.id.main_content, fragmentSettings, "Settings").commit();
                    }
                    txtHeader.setText("Indstillinger");
                    break;


                case 5:
                    FragmentGuide fragmentGuide = FragmentGuide.newInstance("", "");
                    if (manager.findFragmentByTag("Guide") == null) {
                        ft.replace(R.id.main_content, fragmentGuide, "Guide").commit();
                    }
                    txtHeader.setText("Hjælp");
                    break;
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerToggle.onOptionsItemSelected(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();


    }


    //region Drawer code
    // Navigation Drawer Adapter
    public class NavigationDrawerAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        String[] leftSliderData;

        public NavigationDrawerAdapter(Context context, String[] leftSliderData) {
            this.context = context;
            this.leftSliderData = leftSliderData;
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
            WMTextView txtDrawerItem,badgeValue;
        }


        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer, parent, false);
                holder = new ViewHolder();
                holder.txtDrawerItem = (WMTextView) convertView.findViewById(R.id.txtDrawerItem);
                holder.badgeValue = (WMTextView) convertView.findViewById(R.id.notification_count_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtDrawerItem.setText(leftSliderData[position]);
           SharedPreferences preferencesBadgeValue = getSharedPreferences("badge_value",MODE_PRIVATE);
            if(isPupil) { // student view
                if(position==1){
                    String badgeValue=preferencesBadgeValue.getString("badge_count","0");
                    if(badgeValue.equalsIgnoreCase("0")){
                        holder.badgeValue.setVisibility(View.GONE);
                    } else {
                        holder.badgeValue.setVisibility(View.VISIBLE);
                        holder.badgeValue.setText(badgeValue);
                    }
                }else {
                    holder.badgeValue.setVisibility(View.GONE);
                }
            } else { //teacher view
              if(position==2){
                  String badgeValue=preferencesBadgeValue.getString("badge_count","0");
                  if(badgeValue.equalsIgnoreCase("0")){
                      holder.badgeValue.setVisibility(View.GONE);
                  } else {
                      holder.badgeValue.setVisibility(View.VISIBLE);
                      holder.badgeValue.setText(badgeValue);
                  }
              }else {
                  holder.badgeValue.setVisibility(View.GONE);
              }
            }

            return convertView;

        }
    }
    //</editor-fold>


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.badge_value,menu);

        return false;
    }
}
