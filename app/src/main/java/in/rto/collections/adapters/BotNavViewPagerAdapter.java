package in.rto.collections.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.rto.collections.fragments.Client_Fragment;
import in.rto.collections.fragments.Fragment_Banker_Vehicle_Details;
import in.rto.collections.fragments.Fragment_Customer;
import in.rto.collections.fragments.Fragment_Other_Vehicle_Details;
import in.rto.collections.fragments.Fragment_RTO_Agent_Details;
import in.rto.collections.fragments.Fragment_settings;
import in.rto.collections.fragments.Fragment_vehicle_dealer_details;
import in.rto.collections.fragments.Reminder_Fragment;
import in.rto.collections.fragments.Tracking_Fragment;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class BotNavViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;
    private String role;
    private UserSessionManager session;
    private String user_id;
    private String selectData;

    public BotNavViewPagerAdapter(FragmentManager fm, String role_id, String selectDefault) {
        super(fm);
        role = role_id;
        selectData = selectDefault;
        fragments.clear();
        if (role.equals("1")) {
            fragments.add(new Client_Fragment());
            fragments.add(new Fragment_RTO_Agent_Details());
            fragments.add(new Reminder_Fragment());
            fragments.add(new Fragment_settings());
        } else if (role.equals("2")) {
            fragments.add(new Client_Fragment());
            fragments.add(new Fragment_vehicle_dealer_details());
            fragments.add(new Reminder_Fragment());
            fragments.add(new Fragment_settings());
        } else if (role.equals("4")) {
            fragments.add(new Client_Fragment());
            fragments.add(new Fragment_Banker_Vehicle_Details());
            fragments.add(new Reminder_Fragment());
            fragments.add(new Fragment_settings());
        } else if (role.equals("6")) {
            fragments.add(new Client_Fragment());
            fragments.add(new Fragment_Other_Vehicle_Details());
            fragments.add(new Reminder_Fragment());
            fragments.add(new Fragment_settings());
        } else {
            fragments.add(new Fragment_Customer());
            fragments.add(new Reminder_Fragment());
            fragments.add(new Fragment_settings());
//            fragments.add(new Material_Fragment());
            fragments.add(new Tracking_Fragment());
        }

        // getSessionData();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
            role = json.getString("role_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

}