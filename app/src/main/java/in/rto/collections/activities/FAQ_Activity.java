package in.rto.collections.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.fragments.RtoAgent_Faq_Fragment;
import in.rto.collections.fragments.VehicleDealer_Faq_Fragment;
import in.rto.collections.fragments.customer_Faq_Fragment;

public class FAQ_Activity extends FragmentActivity {
    private Context context;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        init();
        setDefault();
        setupToolbar();
        setEventListner();
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tl_tabnames);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setOffscreenPageLimit(3);
    }

    protected void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("FAQ's");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setDefault() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        // tabLayout.getTabAt(0).setIcon(R.drawable.icon_birthdays);
        // tabLayout.getTabAt(1).setIcon(R.drawable.icon_anniversaries);
//        tabLayout.getTabAt(2).setIcon(R.drawable.icon_premium);

        // tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        // tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);
        //  tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.Battleship_Gray), PorterDuff.Mode.SRC_IN);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new RtoAgent_Faq_Fragment(), "RTO AGENT");
        adapter.addFrag(new VehicleDealer_Faq_Fragment(), "VEHICLE DEALER");
        adapter.addFrag(new customer_Faq_Fragment(), "CUSTOMER");
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


}
