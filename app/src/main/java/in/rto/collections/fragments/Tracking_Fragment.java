package in.rto.collections.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Tracking_Fragment extends Fragment {

    private static Context context;
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static LinearLayout ll_tablayout, ll_nothingtoshow;
    private static String user_id;
    private static UserSessionManager session;
    private static ProgressBar progressBar;
    private static ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        setEventListner();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tl_tabnames);
        ll_tablayout = rootView.findViewById(R.id.ll_tablayout);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        progressBar = rootView.findViewById(R.id.progressBar);
        viewPager.setOffscreenPageLimit(3);


        adapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    public static void setDefault() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);

            if (user_info == null) {
                if (Utilities.isNetworkAvailable(context)) {
                    new CheckCarIqUserRegistration().execute(user_id);
                } else {
                    ll_tablayout.setVisibility(View.GONE);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                }
            } else {
                ll_tablayout.setVisibility(View.VISIBLE);
                ll_nothingtoshow.setVisibility(View.GONE);
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupViewPager(ViewPager viewPager) {
        adapter.addFrag(new VehicleForTracking_Fragment(), "VEHICLES");
        adapter.addFrag(new LiveTracking_Fragment(), "LIVE");
        adapter.addFrag(new LastSeenTracking_Fragment(), "LAST SEEN");
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
                //  tab.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //   tab.getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    private static class CheckCarIqUserRegistration extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getuser"));
            param.add(new ParamsPojo("user_id", params[0]));
//            param.add(new ParamsPojo("user_id", "10"));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.USECARIQRAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {

                    ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
                    CarIqUserDetailsModel pojoDetails = new Gson().fromJson(result, CarIqUserDetailsModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        session.createCarIqSession(result);
                        ll_tablayout.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);

                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
                    } else {
                        ll_tablayout.setVisibility(View.GONE);
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
