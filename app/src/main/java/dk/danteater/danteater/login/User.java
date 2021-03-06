package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by nirav on 12-09-2014.
 */
public class User  {

    // Dhruvil

    @SerializedName("givenName")
    private String firstName;
    @SerializedName("sn")
    private String lastName;
    @SerializedName("uid")
    private String userId;
    @SerializedName("primaryGroup")
    private String primaryGroup;
    @SerializedName("roles")
    private ArrayList<String> roles;
    @SerializedName("domain")
    private String domain;

    public User(String firstName, String lastName, String userId, String primaryGroup, ArrayList<String> roles, String domain) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.primaryGroup = primaryGroup;
        this.roles = roles;
        this.domain = domain;
    }

    public User() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(String primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }


    public boolean checkTeacherOrAdmin(ArrayList<String> roles) {
        boolean isTeacher=false;
        if(roles==null){
            return false;
        }
        if(roles.size()==0){
            return false;
        }

        for(String name: roles) {

            if((name.equalsIgnoreCase("teacher")) || (name.equalsIgnoreCase("admin")) ) {
                isTeacher=true;
                break;
            }
        }
        return isTeacher;
    }


    public boolean checkPupil(ArrayList<String> roles) {
        boolean isPupil=false;
        if(roles==null){
            return false;
        }
        if(roles.size()==0){
            return false;
        }

        for(String name: roles) {

            if((name.equalsIgnoreCase("pupil")) ) {
                isPupil=true;
                break;
            }
        }
        return isPupil;
    }

    /*Comparator for sorting the list by  Name*/
    public static Comparator<User> nameComparator = new Comparator<User>() {

        public int compare(User u1, User u2) {

            String name1 = u1.getFirstName().toUpperCase()+" "+u1.getLastName().toUpperCase();
            String name2 = u2.getFirstName().toUpperCase()+" "+u2.getLastName().toUpperCase();

            //ascending order
            return name1.compareTo(name2);

        }};
}
