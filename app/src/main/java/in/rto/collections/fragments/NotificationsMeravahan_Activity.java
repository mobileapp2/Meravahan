package in.rto.collections.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.GetNotificationListAdapter;
import in.rto.collections.models.NotificationPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class NotificationsMeravahan_Activity extends Fragment {
    private static Context context;
    private static RecyclerView rv_productlist;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private static LinearLayout ll_parent;
    //    private static ShimmerFrameLayout shimmer_view_container;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;
    private static ArrayList<NotificationPojo> notificationInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifi_meravahan, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_productlist = rootView.findViewById(R.id.rv_productlist);
        ll_parent = rootView.findViewById(R.id.ll_parent);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_productlist.setLayoutManager(layoutManager);

        notificationInfoList = new ArrayList<>();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetNotificationsInfoList().execute(user_id);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_productlist.setVisibility(View.GONE);
        }
    }

    private void setEventHandlers() {

//        rv_productlist.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                NotificationPojo notificationDetails = notificationInfoList.get(position);
//                Intent intent = new Intent(context, ViewNotification_Activity.class);
//                intent.putExtra("notificationDetails", notificationDetails);
//                startActivity(intent);
//            }
//        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetNotificationsInfoList().execute(user_id);
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_productlist.setVisibility(View.GONE);
                }
            }
        });
    }

    public static class GetNotificationsInfoList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_productlist.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getNotifications"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
//                shimmer_view_container.stopShimmer();
//                shimmer_view_container.setVisibility(View.GONE);
                rv_productlist.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    notificationInfoList = new ArrayList<>();
                    rv_productlist.setAdapter(new GetNotificationListAdapter(context, notificationInfoList));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                NotificationPojo summary = new NotificationPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                summary.setId(jsonObj.getString("id"));
                                summary.setMessage(jsonObj.getString("message"));
                                summary.setImage(jsonObj.getString("image"));
                                summary.setImageurl(jsonObj.getString("imageURL"));
                                summary.setSenderName(jsonObj.getString("name"));
                                summary.setCreated_at(jsonObj.getString("send_at"));
                                notificationInfoList.add(summary);
                            }
                            if (notificationInfoList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_productlist.setVisibility(View.GONE);
                            } else {
                                rv_productlist.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rv_productlist.setAdapter(new GetNotificationListAdapter(context, notificationInfoList));
                        }
                    } else {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_productlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





}
