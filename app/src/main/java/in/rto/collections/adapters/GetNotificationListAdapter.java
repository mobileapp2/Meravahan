package in.rto.collections.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.ViewNotification_Activity;
import in.rto.collections.activities.ViewRTODetails_Activity;
import in.rto.collections.models.NotificationPojo;
import in.rto.collections.models.ProductInfoListPojo;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class GetNotificationListAdapter extends RecyclerView.Adapter<GetNotificationListAdapter.MyViewHolder> {

    private List<NotificationPojo> resultArrayList;
    private Context context;
    private File file, notificationPicFolder;
    public GetNotificationListAdapter(Context context, List<NotificationPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        notificationPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Vehicle_dealer");
        if (!notificationPicFolder.exists())
            notificationPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    @Override
    public GetNotificationListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_list_row, parent, false);
        GetNotificationListAdapter.MyViewHolder myViewHolder = new GetNotificationListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GetNotificationListAdapter.MyViewHolder holder, final int position) {
        NotificationPojo notificationPojo = new NotificationPojo();
        notificationPojo = resultArrayList.get(position);
        String DateTime = changeDateFormat("yyyy-MM-dd HH:MM:SS",
                "dd-MM-yyyy HH:MM",notificationPojo.getCreated_at());
        holder.tv_name.setText(notificationPojo.getMessage());
        holder.tv_send_by.setText(notificationPojo.getSenderName());
        holder.tv_send_at.setText(DateTime);
        if (!notificationPojo.getImage().equals("")) {
            holder.imv_product.setVisibility(View.VISIBLE);
            holder.imv_download.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(notificationPojo.getImageurl())
                    .into(holder.imv_product);
        }else{
            holder.imv_product.setVisibility(View.GONE);
            holder.imv_download.setVisibility(View.GONE);
        }

        final NotificationPojo finalNotificationPojo = notificationPojo;
        holder.gotoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewNotification_Activity.class);
                intent.putExtra("notificationDetails", finalNotificationPojo);
                context.startActivity(intent);
            }
        });
        holder.imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!finalNotificationPojo.getImage().equals("")) {
                    URL url = null;
                    try {
                        url = new URL(finalNotificationPojo.getImageurl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap b = null;
                    try {
                        b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            b, "Title", null);
                    Uri imageUri = Uri.parse(path);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("*/*");
                    share.putExtra(Intent.EXTRA_TEXT, finalNotificationPojo.getMessage());
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    context.startActivity(Intent.createChooser(share, "Share !"));
                }else{
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, finalNotificationPojo.getMessage());
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    context.startActivity(Intent.createChooser(share, "Share !"));
                }
            }
        });
        holder.imv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetNotificationListAdapter.DownloadDocument().execute(finalNotificationPojo.getImageurl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name,tv_send_by,tv_send_at;
        private ImageView imv_product,imv_share,imv_download;
        CardView ll_mainlayout;
        LinearLayout gotoview;
        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_send_at = view.findViewById(R.id.send_at);
            tv_send_by = view.findViewById(R.id.send_by);
            imv_product = view.findViewById(R.id.imv_product);
            imv_share = view.findViewById(R.id.imv_share);
            imv_download = view.findViewById(R.id.imv_download);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            gotoview = view.findViewById(R.id.gotoview);
        }
    }
    public class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context, R.style.CustomDialogTheme);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Downloading Document");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(notificationPicFolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

}

