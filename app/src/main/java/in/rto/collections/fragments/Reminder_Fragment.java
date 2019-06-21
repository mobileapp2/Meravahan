package in.rto.collections.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class Reminder_Fragment extends Fragment {
    private Context context;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private UserSessionManager session;
    private String user_id, role;
    ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventListner();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tl_tabnames);
        viewPager = rootView.findViewById(R.id.viewPager);
//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
//        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(Color.GRAY);
//        drawable.setSize(1, 1);
//        linearLayout.setDividerPadding(10);
//        linearLayout.setDividerDrawable(drawable);
        viewPager.setOffscreenPageLimit(3);
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

    private void setDefault() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        if (role.equals("3")) {
            tabLayout.setVisibility(View.GONE);
            // tabLayout.getTabAt(0).setIcon(R.drawable.icon_premium);
            // tabLayout.getTabAt(1).setIcon(R.drawable.icon_premium);
            //tabLayout.getTabAt(2).setIcon(R.drawable.icon_premium);
            // tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
            // tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
            // tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);

        } else {
            // tabLayout.getTabAt(0).setIcon(R.drawable.icon_premium);
            //tabLayout.getTabAt(1).setIcon(R.drawable.icon_birthdays);
            //tabLayout.getTabAt(2).setIcon(R.drawable.icon_anniversaries);

            //tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            //tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
            //tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (role.equals("3")) {
            adapter.addFrag(new PremiumDueSecond_Fragment(), "VEHICLE REMINDERS");
            //adapter.addFrag(new DealerPremiumDue_Fragment(), "Dealer Premium");
            //adapter.addFrag(new AgentPremiumDue_Fragmen(), "Agent Premium");
        } else {
            adapter.addFrag(new PremiumDue_Fragment(), "VEHICLE REMINDERS");
            adapter.addFrag(new Birthday_Fragment(), "BIRTHDAY");
            adapter.addFrag(new Anniversary_Fragment(), "ANNIVERSARY");
        }
        viewPager.setAdapter(adapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventListner() {

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = adapter.getItem(tab.getPosition());
                if (fragment != null) {
                    fragment.onResume();
                }
                //  tab.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //tab.getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
