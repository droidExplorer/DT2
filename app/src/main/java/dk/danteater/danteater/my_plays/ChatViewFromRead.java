package dk.danteater.danteater.my_plays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import dk.danteater.danteater.Messages.MessagesForConversation;
import dk.danteater.danteater.Messages.ReadActivityForChat;
import dk.danteater.danteater.Play.AssignedUsers;
import dk.danteater.danteater.Play.Play;
import dk.danteater.danteater.Play.PlayLines;
import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;
import dk.danteater.danteater.customviews.HUD;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.model.API;
import dk.danteater.danteater.model.AdvancedSpannableString;
import dk.danteater.danteater.model.CallWebService;
import dk.danteater.danteater.model.ComplexPreferences;
import dk.danteater.danteater.model.DatabaseWrapper;


/**
 * Created by nirav on 15-10-2014.
 */
public class ChatViewFromRead extends BaseActivity {
    ListView listChat;
    private int lineNumber;
    private ChatAdapter chatAdapter;
    private ArrayList<MessagesForConversation> messagesForConversationArrayList=new ArrayList<MessagesForConversation>();
    static final int ITEM_TYPE_ME = 0;
    static final int ITEM_TYPE_SENDER = 1;
    private User currentUser;
    private HUD dialog_next;
    int plyIDAfterUpdate = 0;
    int playid = 0;
    Play ply;
    Play selectedPlay;
    PlayLines currentPlayLine;
    public static int STATE_CHAT = 3;
    private WMTextView btnSendMessage;
    private EditText etMessageValue;
    private HUD dialog;
    private String teacherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_for_read);
        messagesForConversationArrayList.clear();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ComplexPreferences complexPreferencesForPlayLine = ComplexPreferences.getComplexPreferences(ChatViewFromRead.this, "selected_playline", 0);
        currentPlayLine = complexPreferencesForPlayLine.getObject("current_playline", PlayLines.class);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this,"user_pref", 0);
        currentUser = complexPreferences.getObject("current_user", User.class);

        ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreference.getObject("selected_play",Play.class);

        if(currentUser.checkPupil(currentUser.getRoles())) {
            teacherName=getIntent().getStringExtra("to_user_id");
            txtHeader.setText(teacherName);
        }else {
            StringBuffer stringBuffer=new StringBuffer();
            for(AssignedUsers assignedUsers:currentPlayLine.assignedUsersList) {
                stringBuffer.append(assignedUsers.AssignedUserName+", ");
            }
            txtHeader.setText(stringBuffer.toString());
        }

        listChat = (ListView)findViewById(R.id.listViewChatForRead);
        btnSendMessage=(WMTextView)findViewById(R.id.btnSendMessageForRead);
        etMessageValue=(EditText)findViewById(R.id.etChatMessageForRead);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               messagesForConversationArrayList.add(new MessagesForConversation("",selectedPlay.OrderId,currentPlayLine.LineID,currentUser.getUserId(),"",currentPlayLine.LineID + "\n" +selectedPlay.Title.replace(" ","_")+"-"+currentPlayLine.RoleName+"-"+currentPlayLine.LineID.substring(currentPlayLine.LineID.lastIndexOf("-")+1)+"\n"+etMessageValue.getText().toString().trim()+"",0.0,""));

                chatAdapter=new ChatAdapter(ChatViewFromRead.this);
                listChat.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();

                if(currentUser.checkPupil(currentUser.getRoles())) {
                    // For Student
                    // call API createNewMessageAndSendToUser
                    createNewMessageAndSendToUser();
                } else {
                    //For Teacher
                    // call API createNewMessageAndSendToUser
                    for(AssignedUsers assignedUsers: currentPlayLine.assignedUsersList) {
                        createNewMessageAndSendToUser(assignedUsers);
                    }
                }

                etMessageValue.setText("");
            }
        });
    }


    private void createNewMessageAndSendToUser() { // For Pupil

        final JSONObject requestParams=new JSONObject();
        try {
            requestParams.put("OrderId", selectedPlay.OrderId+"");
            requestParams.put("LineId", currentPlayLine.LineID+"");
            requestParams.put("FromUserId", currentUser.getUserId()+"");
            requestParams.put("ToUserId", teacherName+"");
            //TODO
            requestParams.put("MessageText", currentPlayLine.LineID + "\n" + selectedPlay.Title.replace(" ", "_") + "-" + currentPlayLine.RoleName + "-" + currentPlayLine.LineID.substring(currentPlayLine.LineID.lastIndexOf("-" + 1)) + "\n" + etMessageValue.getText().toString().trim() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/Message", requestParams.toString());
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


            }
        }.execute();
    }


    private void createNewMessageAndSendToUser(AssignedUsers assignedUsers) { // For Teacher

        final JSONObject requestParams=new JSONObject();
        try {
            requestParams.put("OrderId", selectedPlay.OrderId+"");
            requestParams.put("LineId", currentPlayLine.LineID+"");
            requestParams.put("FromUserId", currentUser.getUserId()+"");
            requestParams.put("ToUserId", assignedUsers.getAssignedUserId()+"");
            requestParams.put("MessageText", currentPlayLine.LineID+"\n"+selectedPlay.Title.replace(" ","_")+"-"+currentPlayLine.RoleName+"-"+currentPlayLine.LineID.substring(currentPlayLine.LineID.lastIndexOf("-")+1)+"\n"+etMessageValue.getText().toString().trim()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/Message", requestParams.toString());
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


            }
        }.execute();
    }


    public class ChatAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ChatAdapter(Context context) {

            this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return messagesForConversationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {

            MessagesForConversation msg = messagesForConversationArrayList.get(position);

            if(msg.FromUserId.equalsIgnoreCase(currentUser.getUserId())){
                return ITEM_TYPE_ME;
            }else{
                return ITEM_TYPE_SENDER;
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            final MessagesForConversation msg = messagesForConversationArrayList.get(position);

            switch (type){

                case ITEM_TYPE_SENDER:


                    convertView = mInflater.inflate(R.layout.item_chat_left, parent,false);

                    WMTextView txtCircle = (WMTextView)convertView.findViewById(R.id.txtCircleChat);
                    txtCircle.setText(msg.FromUserId.toString().toUpperCase());

                    WMTextView txtChatData = (WMTextView)convertView.findViewById(R.id.txtChatData);
                    //TODO



                        String[] line = msg.MessageText.split(System.getProperty("line.separator"));
                    for(int i=0; i<line.length; i++ ) {
//                        Log.e("line value: ", line[i] + "");
                    }
                        String visibleLine = line[1] + "\n" + line[2];
                        AdvancedSpannableString spannableString = new AdvancedSpannableString(visibleLine);
                        String[] linearray = visibleLine.split(System.getProperty("line.separator"));
                        String firstLineData = linearray[0];
                        spannableString.setUnderLine(firstLineData);

                        txtChatData.setText(spannableString);

                    txtChatData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;

                case ITEM_TYPE_ME:

                    convertView = mInflater.inflate(R.layout.item_chat_right, parent,false);

                    WMTextView txtChatD = (WMTextView)convertView.findViewById(R.id.txtChatData);
                    //TODO

                        String[] lines = msg.MessageText.split(System.getProperty("line.separator"));
                    for(int i=0; i<lines.length; i++ ) {
//                        Log.e("line value: ", lines[i] + "");
                    }
                        String visibleLines = lines[1] + "\n" + lines[2];
                        final AdvancedSpannableString spannableStrin = new AdvancedSpannableString(visibleLines);
                        String[] linearrays = visibleLines.split(System.getProperty("line.separator"));
                        String firstLine = linearrays[0];
                        spannableStrin.setUnderLine(firstLine);
                        txtChatD.setText(spannableStrin);

                    txtChatD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Log.e("line id: ",msg.LineId.substring(0,msg.LineId.lastIndexOf("-"))+"");
                            lineNumber=Integer.parseInt(msg.LineId.substring(msg.LineId.lastIndexOf("-")+1));
//                            Log.e("line number in chat activity",lineNumber+" ");
                            Play play=new Play();
                            play.OrderId=msg.LineId.substring(0,msg.LineId.lastIndexOf("-"));
                            dialog_next = new HUD(ChatViewFromRead.this,android.R.style.Theme_Translucent_NoTitleBar);
                            dialog_next.title("Henter");
                            dialog_next.show();
                            gotoSpecificPage(play);
                        }
                    });
                    break;

            }

            return convertView;
        }


    }

    private void gotoSpecificPage(final Play play){
        DatabaseWrapper dbh = new DatabaseWrapper(ChatViewFromRead.this);
        dbh.openDataBase();
        boolean hasPlay = dbh.hasPlayWithPlayOrderIdText(play.OrderId);
        dbh.close();

        if(hasPlay == true){
            dbh.openDataBase();
            plyIDAfterUpdate = dbh.getPlayIdFromDBForOrderId(play.OrderId);
            dbh.close();
            SharedPreferences pre = getSharedPreferences("Plays", MODE_PRIVATE);
            SharedPreferences.Editor edi = pre.edit();
            edi.putInt("playid",plyIDAfterUpdate);
            edi.commit();
            gotoNextPage();
        }
        else{
//            Log.i("hasplay","false");
            // insert new play to db

            new CallWebService(API.link_retrievePlayContentsForPlayOrderId +play.OrderId,CallWebService.TYPE_JSONOBJECT) {

                @Override
                public void response(final String response) {

//                    Log.i("Response full play : ",""+response);
                    new AsyncTask<String,Integer,String>(){

                        @Override
                        protected String doInBackground(String... params) {

                            Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                            DatabaseWrapper db = new DatabaseWrapper(ChatViewFromRead.this);
                            db.openDataBase();
                            db.insertPlay(receivedPlay, false);
                            db.close();

                            return null;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);


                            SharedPreferences preferences = getSharedPreferences("Plays", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            String k = "PlayLatesteUpdateDate"+play.PlayId;
                            editor.putString(k,""+(int) (System.currentTimeMillis() / 1000));
                            editor.commit();

                            gotoNextPage();

                        }
                    }.execute();

                }

                @Override
                public void error(VolleyError error) {
                    dialog_next.dismiss();

                    Toast.makeText(ChatViewFromRead.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }.start();

        }

    }

    private void gotoNextPage() {

        new AsyncTask<String,Integer,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                SharedPreferences preferences = getSharedPreferences("Plays", MODE_PRIVATE);
                playid = preferences.getInt("playid",0);

            }

            @Override
            protected String doInBackground(String... params) {

                DatabaseWrapper dbh = new DatabaseWrapper(ChatViewFromRead.this);
                dbh.openDataBase();
                ply = dbh.retrievePlayWithId(playid);
                dbh.close();

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ChatViewFromRead.this, "mypref",0);
                complexPreferences.putObject("selected_play",ply);
                complexPreferences.commit();

                dialog_next.dismiss();
                Intent i1 = new Intent(ChatViewFromRead.this, ReadActivityForChat.class);
                i1.putExtra("currentState",STATE_CHAT);
                i1.putExtra("line_number",lineNumber);
                startActivity(i1);

            }
        }.execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
