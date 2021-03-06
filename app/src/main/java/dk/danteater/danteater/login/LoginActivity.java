package dk.danteater.danteater.login;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mvnordic.mviddeviceconnector.DeviceSecurity;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import dk.danteater.danteater.Play.Play;
import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;
import dk.danteater.danteater.app.MyApplication;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.guide.GuideStartup;
import dk.danteater.danteater.model.ComplexPreferences;
import dk.danteater.danteater.model.DatabaseWrapper;
import dk.danteater.danteater.model.SessionReceiver;
import dk.danteater.danteater.model.StateManager;
import dk.danteater.danteater.my_plays.DrawerActivity;
import dk.danteater.danteater.my_plays.ReadActivityFromPreview;


public class LoginActivity extends BaseActivity {

    public  DeviceSecurity m_device_security=null;
    boolean shouldShowLoginView;
    private LinearLayout loginView, noAccessView;
    private RelativeLayout noNetworkView;
    boolean isTeacherOrAdmin;
    private WMTextView txtBottomLabel;
    User user;
    private DatabaseWrapper dbHelper;
    WMTextView txtTryAgain,txtContinueWithLastOpen;
    String session_id;
    Timer timer;
    Play selectedPlay;
    JSONObject request_params;
    JSONObject request_params2; //for tryToLogin2 method
    StateManager stateManager = StateManager.getInstance();



  public String aaccess_identifier = "product.ios.da.intowords";
 //   public String aaccess_identifier = "product.ios.ml.theaterapp.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtHeader.setText("Login");
        dbHelper=new DatabaseWrapper(LoginActivity.this);
        // Alternative Views
        loginView = (LinearLayout) findViewById(R.id.LoginView);
        noAccessView = (LinearLayout) findViewById(R.id.noAccessView);
        noNetworkView = (RelativeLayout) findViewById(R.id.noNetworkView);

        txtBottomLabel = (WMTextView) findViewById(R.id.txtBottomLabel);
        txtTryAgain = (WMTextView) findViewById(R.id.txtTryAgain);
        txtContinueWithLastOpen=(WMTextView) findViewById(R.id.txtContinueWithLastOpen);
        // get login value from setting activity
        // true- automatic login
        // false- manual login
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        shouldShowLoginView = preferences.getBoolean("shouldShowLoginView", false);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showLineNumber",true);
        editor.commit();

        editor.putBoolean("showComments",true);
        editor.commit();


        // automatic login is on
        if (shouldShowLoginView == true) {
            startLoginTimer();
//            Log.i("Login Flow : ","Already LoggedIn goto next page");

//            Log.e("state manager","called ....... ");
            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
            startActivity(i);
            finish();

        } else {
//            Log.i("Login Flow : ","Fresh Login");
            // automatic login is off
            proceedLogin();
        }
    }

    private void proceedLogin() {

        m_device_security = new DeviceSecurity(this);
        m_device_security.addDeviceSecurityListener(device_security_listener);
        m_device_security.setApplicationCountryCode("IntoWords_dk");
        m_device_security.excludeLoginGroup("company");
        m_device_security.excludeLoginGroup("private");

        // if automatic login is off, release the device registration
        if (shouldShowLoginView == false) {

            m_device_security.releaseDeviceRegistration();

        }
        // try to login
        if (isConnected()) {

            // TODO change to login with android
            m_device_security.doLogin(aaccess_identifier, R.id.fragment_layout);
            loginView.setVisibility(View.VISIBLE);
            noAccessView.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.GONE);

        } else {
            m_device_security.doLogin(aaccess_identifier, R.id.fragment_layout);
            loginView.setVisibility(View.GONE);
            noAccessView.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.GONE);
        }
    }


    private DeviceSecurity.DeviceSecurityListener device_security_listener = new DeviceSecurity.DeviceSecurityListener() {

        @Override
        public void onMVIDResponseReady(MVIDResponse response) {

            if (response.has_access == false) {

                m_device_security.releaseDeviceRegistration();

            }

            session_id = m_device_security.getMVSessionID(response.access_identifier);

            if (session_id == "" || session_id == null) {

                m_device_security.releaseDeviceRegistration();
                // no internet connection
                // shows no network view
                txtTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected()) {
                            m_device_security.doLogin(aaccess_identifier, R.id.fragment_layout);
                            loginView.setVisibility(View.VISIBLE);
                            noAccessView.setVisibility(View.GONE);
                            noNetworkView.setVisibility(View.GONE);
                        }
                    }
                });

                txtContinueWithLastOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "mypref", 0);
                            selectedPlay = complexPreferences.getObject("selected_play", Play.class);
                            if (selectedPlay == null) {
//                                Log.e("selected play", "false");
                                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                alert.setTitle("Fejl");
                                alert.setMessage("Stykket kunne ikke findes på det device. ");
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            } else {
                                Intent i1 = new Intent(LoginActivity.this, ReadActivityFromPreview.class);
                                i1.putExtra("currentState",1);
                                i1.putExtra("isFromLogin",true);
                                startActivity(i1);
                            }
                    }
                });

                showDialog("Intet netværk", "Login-serveren kunne ikke kontaktes. Tjek venligst dine netværksindstillinger, og prøv at logge ind igen.");
                loginView.setVisibility(View.GONE);
                noAccessView.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.VISIBLE);
            } else {
                loginView.setVisibility(View.VISIBLE);
                noAccessView.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.GONE);
                // save session id in shared preferences
                SharedPreferences preferences = getSharedPreferences("session_id", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("session_id", session_id);
                editor.commit();

                startBackServiceForLIve(session_id);

                // post webservice
                callWebServicePost(session_id, response);
            }
        }

        private void callWebServicePost(String session_id, final MVIDResponse mvid_response) {

            JSONObject params = new JSONObject();
             request_params = new JSONObject();
            try {
                params.put("session_id", session_id);
                params.put("lookup_primary_group", true);
                params.put("extract_userroles", true);

                request_params.put("methodname", "whoami");
                request_params.put("type", "jsonwsp/request");
                request_params.put("version", "1.0");
                request_params.put("args", params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/UserService/jsonwsp", request_params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {
                    String res = jobj.toString();
//                    Log.e("response: ", res + "");
                    handlePostsList(res, mvid_response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });

            MyApplication.getInstance().addToRequestQueue(req);
        }
    };

    private void startBackServiceForLIve(String session_id) {

        Intent intent = new Intent(getApplicationContext(), SessionReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this,12345,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 30000; // 30 seconds
        AlarmManager alarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);



    }

    private void handlePostsList(final String response, final DeviceSecurity.DeviceSecurityListener.MVIDResponse mvid_response) {

        runOnUiThread(new Runnable() {
            public void run() {
                try {

                    Intent intent = null;
                    BeanUserResult beanCustomerInfo = new GsonBuilder().create().fromJson(response, BeanUserResult.class);
                    BeanUserInfo beanUserInfo = beanCustomerInfo.getBeanUserResult();
                    user = beanUserInfo.getUser();

//                    Log.e("user_id: ", user.getUserId() + "");
//                    Log.e("first_name: ", user.getFirstName() + "");
//                    Log.e("last_name: ", user.getLastName() + "");
//                    Log.e("primary_group: ", user.getPrimaryGroup() + "");
//                    Log.e("roles: ", user.getRoles() + "");
//                    Log.e("domain: ", user.getDomain() + "");

                    isTeacherOrAdmin = user.checkTeacherOrAdmin(user.getRoles());

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }

                //store current user and domain in shared preferences
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                complexPreferences.putObject("current_user", user);
                complexPreferences.commit();

                // if logged in user is teacher or admin and has no access
                if (!mvid_response.has_access && isTeacherOrAdmin) {

                    loginView.setVisibility(View.GONE);
                    noAccessView.setVisibility(View.VISIBLE);
                    noNetworkView.setVisibility(View.GONE);


                    txtBottomLabel.setText(Html.fromHtml("Ring til MV-Nordic på 6591 8022 eller klik <a href=\"https://www.mv-nordic.com/dk/produkter/teater-aftale\\\">her</a> for at læse mere."));
                    txtBottomLabel.setMovementMethod(LinkMovementMethod.getInstance());
                } else {

                    loginView.setVisibility(View.VISIBLE);
                    noAccessView.setVisibility(View.GONE);
                    noNetworkView.setVisibility(View.GONE);

                    startLoginTimer();
//                    stateManager.retriveSchoolClasses(session_id, user.getDomain());
//                    stateManager.retriveSchoolTeachers(session_id, user.getDomain());
                    // check user is login first time or not
                    if(isTeacherOrAdmin){
                        SharedPreferences preferences = getSharedPreferences("show_tutorial",MODE_PRIVATE);
                        String lastLoginUsers=preferences.getString("last_login_users", "");

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("last_login_users", lastLoginUsers+"*"+user.getUserId());
                        editor.commit();

                        boolean showtutorial=false;
                        if((!lastLoginUsers.contains(user.getUserId().toString()))){
                            showtutorial=true;
                        }

                        // go to next screen
                        if (isFirstTime() || showtutorial) { // show guide pages
                            Intent i = new Intent(LoginActivity.this, GuideStartup.class);
                            startActivity(i);
                            finish();
                        } else {    // show home screen
                            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            }
        });
    }

    private void startLoginTimer() {

        timer=new Timer();
       // stopLoginTimer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tryToLogin2();
            }
        },0,1000*60*10);

    }

    private void stopLoginTimer() {

        timer.cancel();
    }

    private void tryToLogin2() {

        JSONObject params = new JSONObject();
        request_params2 = new JSONObject();
        try {
            SharedPreferences preferences = getSharedPreferences("session_id", MODE_PRIVATE);
            String sessionId=preferences.getString("session_id","");
//            Log.e("==================================================",sessionId);
            params.put("session_id", sessionId);
            request_params2.put("methodname", "keepAlive");
            request_params2.put("type", "jsonwsp/request");
            request_params2.put("version", "1.0");
            request_params2.put("args", params);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/UserService/jsonwsp", request_params2, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {
                String res = jobj.toString();
//                Log.e("????????????  response continue: ", res + "");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        MyApplication.getInstance().addToRequestQueue(req);


    }


    // check user is login first time or not
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("run_before",MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }


    private void showDialog( String title,String message ) {

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_device_security.releaseDeviceRegistration();
            }
        });
        alert.show();
    }
}
