package dk.danteater.danteater.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.danteater.danteater.Messages.MessageUnread;
import dk.danteater.danteater.R;
import dk.danteater.danteater.customviews.HUD;
import dk.danteater.danteater.customviews.ListDialog;
import dk.danteater.danteater.customviews.WMEdittext;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.model.API;
import dk.danteater.danteater.model.CallWebService;
import dk.danteater.danteater.model.ComplexPreferences;
import dk.danteater.danteater.tab_info.PlayInfoActivity;


public class FragmentSearch extends Fragment implements AdapterView.OnItemClickListener, ListDialog.setSelectedListner {

    private ArrayList<MessageUnread> messageUnreadArrayList=new ArrayList<MessageUnread>();
    MessageUnread messageUnread=new MessageUnread();
    public String badgeValue;
    private Menu menu;
    private User currentUser;
    private static int CLICKED_POSITION = 0;
    private HUD dialog;

    private TextView btnSearch;
    private ListView filterCategoryList;
    private ArrayList<Integer> imageList = new ArrayList<Integer>();
    private ArrayList<String> titleList = new ArrayList<String>();
    private ArrayList<BeanSearch> beanSearchList;
    private Reader reader;
    ListDialog listDialog;
    WMEdittext searchBox;
    private BeanSearch searchResultPlay;
    private SparseArray listSubItems;
    private String[] MEDVIRKENDE = {"Alle", "1-5", "6-10", "11-15", "16-20", "21-30", "30+"};
    private String[] ALDER = {"Alle", "7-9", "10-12", "13-15", "16+"};
    private String[] MUSIK = {"Alle", "Intet", "Lidt", "Musical"};
    private String[] VARIGHED = {"Alle", "1-30 min", "30-45 min", "45-60 min", "60-75 min", "75-120 min", "Over 120 min"};
    String memberString;
    String durationString;
    private ListView searchResultList;
    private RelativeLayout searchFilterView;
    private ImageView searchIcon;
    private LinearLayout emptyView;
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        return fragment;
    }

    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser =complexPreferences.getObject("current_user", User.class);
        listSubItems = new SparseArray();
        listSubItems.put(0, new ArrayList<String>(Arrays.asList(MEDVIRKENDE)));
        listSubItems.put(1, new ArrayList<String>(Arrays.asList(ALDER)));
        listSubItems.put(2, new ArrayList<String>(Arrays.asList(MUSIK)));
        listSubItems.put(3, new ArrayList<String>(Arrays.asList(VARIGHED)));
          Timer  timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getAllUnreadMessagesForUser();
            }
        },0,1000*60*10);

    }


    private void getAllUnreadMessagesForUser() {

        new CallWebService("http://api.danteater.dk/api/MessageUnread/"+currentUser.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                Type listType=new TypeToken<List<MessageUnread>>(){
                }.getType();
                if(response !=null) {
                    messageUnreadArrayList = new GsonBuilder().create().fromJson(response, listType);
                    if (getActivity() != null) {
                        SharedPreferences preferencesBadgeValue = getActivity().getSharedPreferences("badge_value", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencesBadgeValue.edit();
                        if (messageUnreadArrayList.size() > 0) {
                            messageUnread = messageUnreadArrayList.get(messageUnreadArrayList.size() - 1);
                            editor.putString("badge_count", messageUnread.unreadMessageCount.toString().trim());
                            editor.commit();
                        } else {
                            editor.putString("badge_count", "0");
                            editor.commit();
                        }

                        badgeValue = preferencesBadgeValue.getString("badge_count", "0");


                        if (badgeValue.equalsIgnoreCase("0")) {
                            setHasOptionsMenu(false);
//                        View count = menu.findItem(R.id.badge).getActionView();
//                        Log.e("badge value",badgeValue);
//                        count.setVisibility(View.GONE);
                        } else {

                            setHasOptionsMenu(true);
                            if (menu != null) {
                                View count = menu.findItem(R.id.badge).getActionView();
                                count.setVisibility(View.VISIBLE);
                                TextView notifCount = (TextView) count.findViewById(R.id.notif_count);
                                notifCount.setText(String.valueOf(badgeValue));

                            }
                        }


                    }
                }
            }

            @Override
            public void error(VolleyError error) {

                Log.e("error: ",error+"");

            }
        }.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu=menu;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchBox = (WMEdittext) view.findViewById(R.id.search_box);
        imageList.add(R.drawable.ic_number_of_participants);
        imageList.add(R.drawable.ic_age);
        imageList.add(R.drawable.ic_music);
        imageList.add(R.drawable.ic_duration);
        titleList.add("Medvirkende");
        titleList.add("Alder");
        titleList.add("Musik");
        titleList.add("Varighed");
        filterCategoryList = (ListView) view.findViewById(R.id.filterCategoryList);
        searchFilterView=(RelativeLayout) view.findViewById(R.id.searchFilterView);


        searchIcon=(ImageView) view.findViewById(R.id.searchIcon);
        emptyView=(LinearLayout) view.findViewById(R.id.empty);
        filterCategoryList.setAdapter(new FilterCategoryAdapter(getActivity(), titleList, imageList));
        filterCategoryList.setOnItemClickListener(this);
        searchResultList=(ListView)view.findViewById(R.id.searchResultList);
        btnSearch = (TextView) view.findViewById(R.id.btnSearch);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                searchFilterView.setVisibility(View.VISIBLE);
                searchResultList.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);

            }
        });

        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
            if(isFocused) {
                searchFilterView.setVisibility(View.VISIBLE);
                searchResultList.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            }
            }
        });
//        searchBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                searchBox.setFocusableInTouchMode(true);
////                searchBox.setFocusable(true);
//
//            }
//        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                startSearching();
                return true;
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearching();
            }
        });

        return view;
    }


    private void startSearching() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("Søger");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                JSONObject params = new JSONObject();


                try {
                    View memberView = filterCategoryList.getChildAt(0);
                    TextView members = (TextView) memberView.findViewById(R.id.txtSelectedValue);

                    View ageView = filterCategoryList.getChildAt(1);
                    TextView ages = (TextView) ageView.findViewById(R.id.txtSelectedValue);

                    View musicView = filterCategoryList.getChildAt(2);
                    TextView musics = (TextView) musicView.findViewById(R.id.txtSelectedValue);

                    View durationView = filterCategoryList.getChildAt(3);
                    TextView durations = (TextView) durationView.findViewById(R.id.txtSelectedValue);


                    if (members.getText().toString().trim().equals("1-5")) {
                        memberString = "1,5";
                    } else if (members.getText().toString().trim().equals("6-10")) {
                        memberString = "6,10";
                    } else if (members.getText().toString().trim().equals("11-15")) {
                        memberString = "11,15";
                    } else if (members.getText().toString().trim().equals("16-20")) {
                        memberString = "16,20";
                    } else if (members.getText().toString().trim().equals("21-30")) {
                        memberString = "21,30";
                    } else if (members.getText().toString().trim().equals("30+")) {
                        memberString = "30,30";
                    } else {
                        memberString = "";
                    }


                    if (durations.getText().toString().trim().equals("1-30 min")) {
                        durationString = "1,30";
                    } else if (durations.getText().toString().trim().equals("30-45 min")) {
                        durationString = "30,45";
                    } else if (durations.getText().toString().trim().equals("45-60 min")) {
                        durationString = "45,60";
                    } else if (durations.getText().toString().trim().equals("60-75 min")) {
                        durationString = "60,75";
                    } else if (durations.getText().toString().trim().equals("75-120 min")) {
                        durationString = "75,120";
                    } else if (durations.getText().toString().trim().equals("Over 120 min")) {
                        durationString = "120,999";
                    } else {
                        durationString = "";
                    }


                    params.put("PlayId", "");
                    params.put("SearchString", searchBox.getText().toString() + "");
                    params.put("Actors", memberString);
                    params.put("Age", ages.getText().toString() + "");
                    params.put("Music", musics.getText().toString() + "");
                    params.put("Duration", durationString);


//                    Log.e("params", params + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                reader = API.callWebservicePost("http://api.danteater.dk/api/PlaySearch", params.toString());

                try{
                    Type listType = new TypeToken<List<BeanSearch>>() {
                    }.getType();
                    beanSearchList = new GsonBuilder()
                            .create().fromJson(reader, listType);
//                Log.e("search result size: ",beanSearchList.size()+"");

                    handleSearchResultData(beanSearchList);
                }catch(Exception e){}

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                    dialog.dismiss();
            }

        }.execute();


    }
    private void handleSearchResultData(final ArrayList<BeanSearch> beanSearchList) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


//                searchBox.setFocusable(false);
                searchBox.clearFocus();

//                Log.e("search result size: ",beanSearchList.size()+"");
                if(beanSearchList.size()>0) {
                    searchFilterView.setVisibility(View.GONE);
                    searchResultList.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    searchFilterView.setVisibility(View.GONE);
                    searchResultList.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
                searchResultList.setAdapter(new SearchResultAdapter(getActivity(),beanSearchList));

            }
        });
    }


    public class SearchResultAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<BeanSearch> beanSearchList;

        public SearchResultAdapter(Context context, ArrayList<BeanSearch> beanSearchList) {

            this.context = context;

            this.beanSearchList = beanSearchList;
        }

        public int getCount() {
            return beanSearchList.size();
        }

        public Object getItem(int position) {
            return beanSearchList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            WMTextView sResultTitle,sResultSubTitle,sResultParticipants,sResultMusics,sResultAge,sResultDuration;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_search_result, parent, false);
                holder = new ViewHolder();
                holder.sResultTitle = (WMTextView) convertView.findViewById(R.id.sResultTitle);
                holder.sResultSubTitle = (WMTextView) convertView.findViewById(R.id.sResultSubTitle);
                holder.sResultParticipants = (WMTextView) convertView.findViewById(R.id.sResultParticipants);
                holder.sResultMusics = (WMTextView) convertView.findViewById(R.id.sResultMusics);
                holder.sResultAge = (WMTextView) convertView.findViewById(R.id.sResultAge);
                holder.sResultDuration = (WMTextView) convertView.findViewById(R.id.sResultDuration);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.sResultTitle.setText(beanSearchList.get(position).Title);
            holder.sResultSubTitle.setText(beanSearchList.get(position).SubtitleShort);
            holder.sResultParticipants.setText(beanSearchList.get(position).Actors+" medvirkende");
            holder.sResultAge.setText(beanSearchList.get(position).Age+" år");
            holder.sResultDuration.setText(beanSearchList.get(position).Duration+" min");
            holder.sResultMusics.setText("Musik: "+beanSearchList.get(position).Music);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "search_result_play",0);
                    complexPreferences.putObject("searched_play",beanSearchList.get(position));
                    complexPreferences.commit();
                    Intent i=new Intent(getActivity(), PlayInfoActivity.class);
                    startActivity(i);
                }
            });
            return convertView;
        }

    }

    public class FilterCategoryAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> titleList;
        ArrayList<Integer> imageList;

        public FilterCategoryAdapter(Context context, ArrayList<String> titleList, ArrayList<Integer> imageList) {

            this.context = context;
            this.titleList = titleList;
            this.imageList = imageList;
        }

        public int getCount() {
            return titleList.size();
        }

        public Object getItem(int position) {
            return titleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {

            ImageView image;
            TextView txtTitle;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_search_filter_view, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.image = (ImageView) convertView.findViewById(R.id.filterImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setImageResource(imageList.get(position));
            holder.txtTitle.setText(titleList.get(position));
            return convertView;

        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CLICKED_POSITION = position;
        listDialog = new ListDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        listDialog.setCancelable(true);
        listDialog.setCanceledOnTouchOutside(true);
        listDialog.title(titleList.get(position));
        listDialog.setItems((ArrayList) listSubItems.get(position));
        listDialog.setSelectedListner(this);
        listDialog.show();
    }


    @Override
    public void selected(String value) {

        View v = filterCategoryList.getChildAt(CLICKED_POSITION);
        TextView tv = (TextView) v.findViewById(R.id.txtSelectedValue);
        tv.setVisibility(View.VISIBLE);
        if (value.equals("Alle")) {
            tv.setText("");
        } else {
            tv.setText(value);
        }
    }


}


