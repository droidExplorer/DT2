package dk.danteater.danteater.tab_share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dk.danteater.danteater.R;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.my_plays.ShareActivityForPerform;
import dk.danteater.danteater.my_plays.SharedUser;


public class FragmentPupils extends Fragment {
    //    private Menu menu;
    private boolean isSelectAll = false;
    private ListView listStudents;
    private static final String ARG_POSITION = "position";
    private static final String ARG_CLASS_NAME = "class_name";
    private int position;
    private String className;
    //    StateManager stateManager=StateManager.getInstance();
    HashMap<String, ArrayList<User>> pupils;
    ArrayList<User> pupilsList;
    public static FragmentPupils newInstance(int position,String clName) {

        FragmentPupils fragment = new FragmentPupils();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putString(ARG_CLASS_NAME,clName);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  if(ShareActivityForPerform.isSharedforPerformChanged=true){
            ShareActivityForPerform.menu.getItem(0).getIcon();

        }*/
        position = getArguments().getInt(ARG_POSITION);
        className= getArguments().getString(ARG_CLASS_NAME);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_fragment_pupils, container, false);
        listStudents=(ListView) convertView.findViewById(R.id.listStudentsShare);
        return convertView;
    }


    @Override
    public void onResume() {
        super.onResume();

        try {

            pupils = ShareActivityForPerform.pupils;

            if (pupils != null) {
                pupilsList = pupils.get(className);

                Collections.sort(pupilsList, User.nameComparator);

                for (int i = 0; i < pupilsList.size(); i++) {
                    User u = pupilsList.get(i);

                    if (u.getFirstName().equalsIgnoreCase("Hele")) {
                        pupilsList.remove(u);
                    }
                }

                if (pupilsList.size() > 0) {
                    User uHele = new User();
                    uHele.setFirstName("Hele");
                    uHele.setLastName("klassen");
                    pupilsList.add(0, uHele);
                }
                ArrayList<String> studentNameList = new ArrayList<String>();

                for (int i = 0; i < pupilsList.size(); i++) {
                    studentNameList.add("" + pupilsList.get(i).getFirstName() + " " + pupilsList.get(i).getLastName());

                }

                ArrayAdapter adap = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, studentNameList);
                listStudents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listStudents.setAdapter(adap);


                for (int i = 0; i < pupilsList.size(); i++) {
                    User user = pupilsList.get(i);
                    for (SharedUser u : ShareActivityForPerform.sharedTeachersAndStudents) {
                        if (u.userId.equalsIgnoreCase(user.getUserId())) {
                            listStudents.setItemChecked((i), true);
                            ShareFragment.studentSharedList.add(user);
                        }
                    }
                }

                listStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {

                            ShareActivityForPerform.menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_selected));
                            ShareActivityForPerform.menu.getItem(0).setEnabled(true);
                            ShareActivityForPerform.isSharedforPerformChanged = true;

                            if (isSelectAll == true) {

                                for (int i = 0; i < pupilsList.size(); i++) {

                                    listStudents.setItemChecked((i), false);
                                }
                                ShareFragment.studentSharedList.removeAll(pupilsList);

                                isSelectAll = false;

                            } else {

                                for (int i = 0; i < pupilsList.size(); i++) {


                                    ShareFragment.studentSharedList.add(pupilsList.get(i));
                                    listStudents.setItemChecked((i), true);
                                }


                                isSelectAll = true;


                            }


                        } else {


                            ShareActivityForPerform.menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_selected));
                            ShareActivityForPerform.menu.getItem(0).setEnabled(true);
                            ShareActivityForPerform.isSharedforPerformChanged = true;
                            if (ShareFragment.studentSharedList.contains(pupilsList.get(position))) {
                                ShareFragment.studentSharedList.remove(pupilsList.get(position));
                                listStudents.setItemChecked(0, false);
                            } else {
                                ShareFragment.studentSharedList.add(pupilsList.get(position));
                            }

                        }
                    }
                });
            }
        }catch (Exception e){}
    }


}
