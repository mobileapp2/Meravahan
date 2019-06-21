package in.rto.collections.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.Master_Bank;
import in.rto.collections.models.BranchPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.MyViewHolder> {
    private List<BranchPojo> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String user_id;


    public BranchAdapter(Context context, List<BranchPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public BranchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_single, parent, false);
        BranchAdapter.MyViewHolder myViewHolder = new BranchAdapter.MyViewHolder(view);
        return myViewHolder;
    }


    public void onBindViewHolder(final BranchAdapter.MyViewHolder holder, final int position) {
        BranchPojo branchDetails = new BranchPojo();
        branchDetails = resultArrayList.get(position);
        final BranchPojo finalbranchDetails = branchDetails;

        holder.tv_namealias.setText(branchDetails.getName());

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edt_branch = new EditText(context);
                edt_branch.setHint("Branch*");

                final EditText edt_address = new EditText(context);
                edt_address.setHint("Address");
                edt_branch.setText(finalbranchDetails.getName());
                edt_address.setText(finalbranchDetails.getAddress());
                edt_branch.setSelection(finalbranchDetails.getName().length());
                float dpi = context.getResources().getDisplayMetrics().density;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Edit Branch");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isInternetAvailable(context)) {
                            new BranchAdapter.EditBranch().execute(edt_branch.getText().toString().trim(), edt_address.getText().toString().trim(),
                                    user_id,
                                    finalbranchDetails.getId());
                        } else {
                            Utilities.showMessageString(context, "Please Check Internet Connection");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Utilities.isInternetAvailable(context)) {
                            new BranchAdapter.DeleteBranch().execute(finalbranchDetails.getId());
                        } else {
                            Utilities.showMessageString(context, "Please Check Internet Connection");
                        }
                    }
                });

                final AlertDialog alertD = builder.create();
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edt_branch);
                ll.addView(edt_address);
                //alertD.setView(ll);
                alertD.setView(ll, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                //alertD.setView(edt_alias, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
                edt_branch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_namealias;
        CardView ll_mainlayout;
        View view1;

        public MyViewHolder(View view) {
            super(view);
            tv_namealias = view.findViewById(R.id.tv_namealias);
            view1 = view.findViewById(R.id.view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
        }
    }

    public class EditBranch extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "updateBranch");
            obj.addProperty("branch", params[0]);
            obj.addProperty("address", params[1]);
            obj.addProperty("user_id", params[2]);
            obj.addProperty("id", params[3]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        // new Clients_Fragment.GetClientList().execute(user_id);
                        new Master_Bank.GetBankList().execute(user_id);
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteBranch extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteBranch");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        new Master_Bank.GetBankList().execute(user_id);
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

