package dk.danteater.danteater.model;

/**
 * Created by nirav on 16-09-2014.
 */
public class StateManager { //singleton class

    private static StateManager s = new StateManager();

    private StateManager() {
    }

    public static int playID = 0;
    public static StateManager getInstance() {

        return s;
    }

//    public static boolean isFinishRetrivingTeachersAndStudents=false;
//    public static boolean finishedRetrievingTeachers = false;
//    public static int numberOfClassesToBeRetrieved = 0;
//    public  static Group groupForTeacher = new Group();
//    public  static ArrayList<Group> classes = new ArrayList<Group>();
//    public static ArrayList<User> recordedAudios= new ArrayList<User>();
//    public static HashMap<String, ArrayList<User>> pupils=new HashMap<String, ArrayList<User>>();


//    public static void retriveSchoolClasses(final String seesionId, final String domain) {
//
//        JSONObject methodParams = new JSONObject();
//        JSONObject requestParams = new JSONObject();
//
//        try {
//            methodParams.put("session_id", seesionId);
//            methodParams.put("domain", domain);
//
//            requestParams.put("methodname", "listGroups");
//            requestParams.put("type", "jsonwsp/request");
//            requestParams.put("version", "1.0");
//            requestParams.put("args", methodParams);
//
//            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject jobj) {
//                    String res = jobj.toString();
////                    Log.e("response for retrive school classes...: ", res + "");
//
//                    BeanGroupInfo beanGroupInfo = new GsonBuilder().create().fromJson(res, BeanGroupInfo.class);
//                    BeanGroupResult beanGroupResult = beanGroupInfo.getBeanGroupResult();
//                    ArrayList<Group> groupArrayList = beanGroupResult.getGroupArrayList();
//                    classes.clear();
//                 //   pupils.clear();
//
//                    for (Group beanGroup : groupArrayList) {
//                        if (beanGroup.getGroupType().equals("classtype")) {
//                            classes.add(beanGroup);
//                            Log.e("group domain", beanGroup.getDomain() + "");
//                            numberOfClassesToBeRetrieved++;
//                            retriveMembers(seesionId, domain, beanGroup);
//                        }
//                    }
//
//                    Log.e("classes...: ", classes + "");
//
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError arg0) {
//                }
//            });
//            MyApplication.getInstance().addToRequestQueue(req);
//        } catch (JSONException je) {
//            je.printStackTrace();
//        }
//    }

//    public static void retriveSchoolTeachers(String seesionId, String domain) {
//        groupForTeacher.setGroupId("teacher");
//        recordedAudios.clear();
//        retriveMembers(seesionId, domain, groupForTeacher);
//    }

//    public static void retriveMembers(String seesionId, String domain, final Group group) {
//        JSONObject methodParams = new JSONObject();
//        JSONObject requestParams = new JSONObject();
//        try {
//            methodParams.put("session_id", seesionId);
//            methodParams.put("domain", domain);
//            methodParams.put("group_cn", group.getGroupId());
//            requestParams.put("methodname", "listGroupMembers");
//            requestParams.put("type", "jsonwsp/request");
//            requestParams.put("version", "1.0");
//            requestParams.put("args", methodParams);
//
//            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {
//
//                @Override
//                public void onResponse(JSONObject jobj) {
//
//                    String res = jobj.toString();
////                    Log.e("response for retrive school recordedAudios...: ", res + "");
//
//                    BeanGroupMemberInfo beanGroupMemberInfo = new GsonBuilder().create().fromJson(res, BeanGroupMemberInfo.class);
//                    BeanGroupMemberResult beanGroupMemberResult = beanGroupMemberInfo.getBeanGroupMemberResult();
//
//                    ArrayList<GroupMembers> groupMembersArrayList = beanGroupMemberResult.getGroupMembersArrayList();
//                    ArrayList<User> userArrayList = new ArrayList<User>();
//                    userArrayList.clear();
//                    for (GroupMembers beanGroupMembers : groupMembersArrayList) {
////                     Log.e("given name",beanGroupMembers.getGivenName()+" "+beanGroupMembers.getSn());
//
//                       userArrayList.add(new User(beanGroupMembers.getGivenName(), beanGroupMembers.getSn(), beanGroupMembers.getUid(), beanGroupMembers.getPrimaryGroup(), null, beanGroupMembers.getDomain()));
//                    }
//
//                    if (group.getGroupId().equals("teacher")) {
//
//                        recordedAudios.addAll(userArrayList);
//                        Log.e("recordedAudios:",recordedAudios+"");
//
//                        finishedRetrievingTeachers = true;
//                    } else {
//
//                        pupils.put(group.getGroupName().toString(), userArrayList);
//
//
//                        for(Map.Entry<String,ArrayList<User>> entry : pupils.entrySet()){
//
//                            Log.e("key: ",entry.getKey()+" ");
//                            Log.e("vlaues: ",entry.getValue()+"  ");
//
//
//                        }
//                        Log.e("pupils: ",pupils+"");
//
//                        numberOfClassesToBeRetrieved--;
//                    }
//                    if (finishedRetrievingTeachers && numberOfClassesToBeRetrieved == 0) {
//
//
//                    }
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError arg0) {
//                }
//            });
//            MyApplication.getInstance().addToRequestQueue(req);
//
//        } catch (JSONException je) {
//            je.printStackTrace();
//        }
//    }



}