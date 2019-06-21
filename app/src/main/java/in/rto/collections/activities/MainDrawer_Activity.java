package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.squareup.picasso.Picasso;
//import com.aurelhubert.ahbottomnavigatibon.AHBottomNavigationViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.BotNavViewPagerAdapter;
import in.rto.collections.fragments.Fragment_RTO_Agent_Details;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class MainDrawer_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tv_name;
    private ImageView imv_profile;
    private Context context;
    private String name, photo,role;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationItem botNavMessage, botNavTemplates, botNavSignature, botNavMaterial, botNavUsers,botNavReminder,botNavInfo;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;
     private UserSessionManager session;
    private ImageView yimg_todolist, img_notifications;
    NavigationView navigationView;
    private String selcteddata ;
    public static ArrayList<String> faqCustomerLinksList=new ArrayList<>();
    public static ArrayList<String> faqAgentLinksList=new ArrayList<>();
    public static ArrayList<String> faqDealerLinksList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        tv_name = header.findViewById(R.id.tv_name);
        imv_profile = header.findViewById(R.id.imv_profile);
        navigationView.setNavigationItemSelectedListener(this);

        init();
        setUpToolbar();
        getSessionData();
        setEventHandler();
        setUpBottomNavigation();
        //getUpDrawerHeader();

        if (Utilities.isInternetAvailable(context)) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Please Check Internet Connection");
            builder.setIcon(R.drawable.ic_alert_red_24dp);
            builder.setTitle("Alert");
            builder.setCancelable(false);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(context, MainDrawer_Activity.class));
                    finish();
                }
            });
            AlertDialog alertD = builder.create();
            alertD.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSessionData();
    }

    private void init() {
        context = MainDrawer_Activity.this;
        new GetFaq().execute();
        session = new UserSessionManager(context);
       bottomNavigation = findViewById(R.id.bottom_navigation);
       view_pager = findViewById(R.id.view_pager);
       view_pager.setOffscreenPageLimit(4);
        selcteddata = getIntent().getStringExtra("linking");
        if(selcteddata == null){
            selcteddata = "deafult";
        }

    }

    private void getSessionData() {
       try {
           JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            name = json.getString("name");
            photo = json.getString("photo");
           role = json.getString("role_id");
           adapter = new BotNavViewPagerAdapter(getSupportFragmentManager(),role,selcteddata);
           view_pager.setAdapter(adapter);

       } catch (Exception e) {
            e.printStackTrace();
        }
        if(role.equals("3")) {
            navigationView.getMenu().removeItem(R.id.menu_masters);
        }
       getUpDrawerHeader();
   }

    private void setEventHandler() {
        img_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Notification_Activity.class));
            }
        });
//        img_todolist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, TodoList_Activity.class));
//            }
//        });
    }

    private void getUpDrawerHeader() {
       tv_name.setText(name);

        if (!photo.equals("")) {
           Picasso.with(context)
                    .load(photo)
                    .placeholder(R.drawable.icon_userprofile)
                    .into(imv_profile);
        }

       Picasso.with(context).setLoggingEnabled(true);
    }

    private void setUpBottomNavigation() {

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#ffffff"));

        bottomNavigation.setAccentColor(Color.parseColor("#1d89c9"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        bottomNavigation.setForceTint(true);

        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);



        if(role.equals("1")){

            botNavMessage = new AHBottomNavigationItem("Client", R.drawable.client_new, R.color.Gunmetal);
            botNavSignature = new AHBottomNavigationItem("Vehicle Details", R.drawable.vehicle_new, R.color.Gunmetal);
            botNavUsers = new AHBottomNavigationItem("Settings", R.drawable.settings_new, R.color.Gunmetal);
            botNavReminder = new AHBottomNavigationItem("Reminder",R.drawable.reminder_new, R.color.Gunmetal);
            // Add items

            bottomNavigation.addItem(botNavMessage);
            bottomNavigation.addItem(botNavSignature);
            bottomNavigation.addItem(botNavReminder);
            bottomNavigation.addItem(botNavUsers);

            if(selcteddata.equals("linking")){
                view_pager.setCurrentItem(1, true);
                bottomNavigation.setCurrentItem(1);
            }

        }
        else if(role.equals("2")){
            botNavMessage = new AHBottomNavigationItem("Client", R.drawable.client_new, R.color.Gunmetal);
            botNavTemplates = new AHBottomNavigationItem("Vehicle Details", R.drawable.vehicle_new, R.color.Gunmetal);
            botNavUsers = new AHBottomNavigationItem("Settings", R.drawable.settings_new, R.color.Gunmetal);
            botNavReminder = new AHBottomNavigationItem("Reminder",R.drawable.reminder_new, R.color.Gunmetal);
            // Add items

                bottomNavigation.addItem(botNavMessage);
                bottomNavigation.addItem(botNavTemplates);
                bottomNavigation.addItem(botNavReminder);
                bottomNavigation.addItem(botNavUsers);
            if(selcteddata.equals("linking")){
                view_pager.setCurrentItem(1, true);
                bottomNavigation.setCurrentItem(1);
            }

        }  else if(role.equals("4")){
            botNavMessage = new AHBottomNavigationItem("Client", R.drawable.client_new, R.color.Gunmetal);
            botNavTemplates = new AHBottomNavigationItem("Loan Details", R.drawable.vehicle_new, R.color.Gunmetal);
            botNavUsers = new AHBottomNavigationItem("Settings", R.drawable.settings_new, R.color.Gunmetal);
            botNavReminder = new AHBottomNavigationItem("Reminder",R.drawable.reminder_new, R.color.Gunmetal);
            // Add items

            bottomNavigation.addItem(botNavMessage);
            bottomNavigation.addItem(botNavTemplates);
            bottomNavigation.addItem(botNavReminder);
            bottomNavigation.addItem(botNavUsers);
            if(selcteddata.equals("linking")){
                view_pager.setCurrentItem(1, true);
                bottomNavigation.setCurrentItem(1);
            }

        }else if(role.equals("6"))
        {
            botNavMessage = new AHBottomNavigationItem("Client", R.drawable.client_new, R.color.Gunmetal);
            botNavTemplates = new AHBottomNavigationItem("Vehicle Details", R.drawable.vehicle_new, R.color.Gunmetal);
            botNavUsers = new AHBottomNavigationItem("Settings", R.drawable.settings_new, R.color.Gunmetal);
            botNavReminder = new AHBottomNavigationItem("Reminder",R.drawable.reminder_new, R.color.Gunmetal);
            // Add items

            bottomNavigation.addItem(botNavMessage);
            bottomNavigation.addItem(botNavTemplates);
            bottomNavigation.addItem(botNavReminder);
            bottomNavigation.addItem(botNavUsers);
           /* if(selcteddata.equals("linking")){
                view_pager.setCurrentItem(1, true);
                bottomNavigation.setCurrentItem(1);
            }*/
        }
        else{
            botNavMaterial = new AHBottomNavigationItem("Vehicle", R.drawable.client_new, R.color.Gunmetal);
            botNavUsers = new AHBottomNavigationItem("Settings", R.drawable.settings_new, R.color.Gunmetal);
            botNavReminder = new AHBottomNavigationItem("Reminder",R.drawable.reminder_new, R.color.Gunmetal);
            botNavInfo = new AHBottomNavigationItem("Information",R.drawable.information_new, R.color.Gunmetal);

            bottomNavigation.addItem(botNavMaterial);
            bottomNavigation.addItem(botNavReminder);
            bottomNavigation.addItem(botNavUsers);
            bottomNavigation.addItem(botNavInfo);
        }
        // Create items

        //view_pager.setCurrentItem(2,true);



        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

              view_pager.setCurrentItem(position, true);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(role.equals("1")) {

            if (id == R.id.menu_profile) {
                startActivity(new Intent(context, Profile_Activity.class));
            }else if (id == R.id.menu_pro) {
                startActivity(new Intent(context, SelectPremiumPlan_Activity.class));
            }
            else if (id == R.id.legal_info)
            {
                startActivity(new Intent(context, LegalInfo_Activity.class));
            }
            else if(id == R.id.menu_notification){
                startActivity(new Intent(context, Notification_Activity.class));
            }else if (id == R.id.menu_masters) {
             startActivity(new Intent(context, Masters_Activity.class));
            }else if (id == R.id.menu_contact)
            {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }else if (id == R.id.menu_faq)
            {
                startActivity(new Intent(context, FAQ_Activity.class));
            }
            else if (id == R.id.menu_logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }else if(role.equals("2")){
            if (id == R.id.menu_profile) {
                startActivity(new Intent(context, Profile_Activity.class));
            }else if (id == R.id.menu_pro) {
                startActivity(new Intent(context, SelectPremiumPlan_Activity.class));
            }else if(id == R.id.menu_notification){
                startActivity(new Intent(context, Notification_Activity.class));
            } else if (id == R.id.menu_masters) {
               startActivity(new Intent(context, Masters_Activity.class));
            }
            else if (id == R.id.legal_info)
            {
                startActivity(new Intent(context, LegalInfo_Activity.class));
            }
            else if (id == R.id.menu_contact)
            {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }else if (id == R.id.menu_faq)
            {
                startActivity(new Intent(context, FAQ_Activity.class));
            }
            else if (id == R.id.menu_logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }else if(role.equals("4")){
            if (id == R.id.menu_profile) {
                startActivity(new Intent(context, Profile_Activity.class));
            }else if (id == R.id.menu_pro) {
                startActivity(new Intent(context, SelectPremiumPlan_Activity.class));
            } else if(id == R.id.menu_notification){
                startActivity(new Intent(context, Notification_Activity.class));
            }else if (id == R.id.menu_masters) {
                startActivity(new Intent(context, Masters_Activity.class));
            }
            else if (id == R.id.legal_info)
            {
                startActivity(new Intent(context, LegalInfo_Activity.class));
            }
            else if (id == R.id.menu_contact)
            {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }else if (id == R.id.menu_faq)
            {
                startActivity(new Intent(context, FAQ_Activity.class));
            }else if (id == R.id.menu_logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }else if(role.equals("6")){
            if (id == R.id.menu_profile) {
                startActivity(new Intent(context, Profile_Activity.class));
            }else if (id == R.id.menu_pro) {
                startActivity(new Intent(context, SelectPremiumPlan_Activity.class));
            }else if(id == R.id.menu_notification){
                startActivity(new Intent(context, Notification_Activity.class));
            } else if (id == R.id.menu_masters) {
                startActivity(new Intent(context, Masters_Activity.class));
            }else if (id == R.id.menu_contact)
            {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }else if (id == R.id.menu_faq)
            {
                startActivity(new Intent(context, FAQ_Activity.class));
            }
            else if (id == R.id.legal_info)
            {
                startActivity(new Intent(context, LegalInfo_Activity.class));
            }
            else if (id == R.id.menu_logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }else {
            if (id == R.id.menu_profile) {
                startActivity(new Intent(context, Profile_Activity.class));
            }else if (id == R.id.menu_pro) {
                startActivity(new Intent(context, SelectPremiumPlan_Activity.class));
            }  else if(id == R.id.menu_notification){
                startActivity(new Intent(context, Notification_Activity.class));
            }else if (id == R.id.menu_masters) {
                startActivity(new Intent(context, Masters_Activity.class));
            }
            else if (id == R.id.menu_contact)
            {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }else if (id == R.id.menu_faq)
            {
                startActivity(new Intent(context, FAQ_Activity.class));
            }
            else if (id == R.id.legal_info)
            {
                startActivity(new Intent(context, LegalInfo_Activity.class));
            }
            else if (id == R.id.menu_logout) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      img_notifications = findViewById(R.id.img_notifications);
      // img_todolist = findViewById(R.id.img_todolist);
        DrawerLayout drawer = findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public class GetFaq extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getFAQ"));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.FAQAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
               // pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        faqCustomerLinksList=new ArrayList<>();
                        faqAgentLinksList=new ArrayList<>();
                         faqDealerLinksList=new ArrayList<>();

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                JSONObject jsonObj = jsonarr.getJSONObject(i);

                                if (!jsonObj.getString("faqImageUrl").equals("")) {
                                    if (jsonObj.getString("role").equals("1"))
                                    {
                                        String link= "https://meravahan.in/images/faqImages/rto_agents/" + jsonObj.getString("faqImageUrl");
                                        faqAgentLinksList.add(link);

                                    }else if (jsonObj.getString("role").equals("2"))
                                    {
                                        String link= "https://meravahan.in/images/faqImages/dealers/" + jsonObj.getString("faqImageUrl");
                                        faqDealerLinksList.add(link);

                                    }else if (jsonObj.getString("role").equals("3"))
                                    {
                                        String link= "https://meravahan.in/images/faqImages/customers/" + jsonObj.getString("faqImageUrl");
                                        faqCustomerLinksList.add(link);

                                    }

                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
