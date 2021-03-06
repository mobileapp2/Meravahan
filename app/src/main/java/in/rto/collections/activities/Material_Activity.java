package in.rto.collections.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.activities.DL_Information_Activity;
import in.rto.collections.activities.Tem_Registration_Activity;
import in.rto.collections.models.InformationListPojo;
import in.rto.collections.utilities.UserSessionManager;


public class Material_Activity extends Activity implements View.OnClickListener{

    public LinearLayout ll_parent;
    private Context context;
    private CardView first, second, third, fourth, five, six, seven, eight, nine, ten, eleven, twele, thirteen, fourteen;
    private CardView first1, second1, third1, fourth1, five1, six1, seven1, eight1, nine1;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;
    private ArrayList<InformationListPojo> informationListPojos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        init();
        setUpToolbar();
    }

    private void init() {
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        ten = findViewById(R.id.ten);
        eleven = findViewById(R.id.eleven);
        twele = findViewById(R.id.twele);
        thirteen = findViewById(R.id.thirteen);
        fourteen = findViewById(R.id.fourteen);

        first1 = findViewById(R.id.first1);
        second1 = findViewById(R.id.second1);
        third1 = findViewById(R.id.third1);
        fourth1 = findViewById(R.id.fourth1);
        five1 = findViewById(R.id.five1);
        six1 = findViewById(R.id.six1);
        seven1 = findViewById(R.id.seven1);


        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        ten.setOnClickListener(this);
        eleven.setOnClickListener(this);
        twele.setOnClickListener(this);
        thirteen.setOnClickListener(this);
        fourteen.setOnClickListener(this);

        first1.setOnClickListener(this);
        second1.setOnClickListener(this);
        third1.setOnClickListener(this);
        fourth1.setOnClickListener(this);
        five1.setOnClickListener(this);
        six1.setOnClickListener(this);
        seven1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first:
                Intent i = new Intent(context, Tem_Registration_Activity.class);
                i.putExtra("type", "1");
                startActivity(i);
                break;
            case R.id.second:
                Intent second = new Intent(context, Tem_Registration_Activity.class);
                second.putExtra("type", "2");
                startActivity(second);
                break;
            case R.id.third:
                Intent third = new Intent(context, Tem_Registration_Activity.class);
                third.putExtra("type", "3");
                startActivity(third);
                break;
            case R.id.fourth:
                Intent fourth = new Intent(context, Tem_Registration_Activity.class);
                fourth.putExtra("type", "4");
                startActivity(fourth);
                break;
            case R.id.five:
                Intent five = new Intent(context, Tem_Registration_Activity.class);
                five.putExtra("type", "5");
                startActivity(five);
                break;
            case R.id.six:
                Intent six = new Intent(context, Tem_Registration_Activity.class);
                six.putExtra("type", "6");
                startActivity(six);
                break;
            case R.id.seven:
                Intent seven = new Intent(context, Tem_Registration_Activity.class);
                seven.putExtra("type", "7");
                startActivity(seven);
                break;
            case R.id.eight:
                Intent eight = new Intent(context, Tem_Registration_Activity.class);
                eight.putExtra("type", "8");
                startActivity(eight);
                break;
            case R.id.nine:
                Intent nine = new Intent(context, Tem_Registration_Activity.class);
                nine.putExtra("type", "9");
                startActivity(nine);
                break;
            case R.id.ten:
                Intent ten = new Intent(context, Tem_Registration_Activity.class);
                ten.putExtra("type", "10");
                startActivity(ten);
                break;
            case R.id.eleven:
                Intent eleven = new Intent(context, Tem_Registration_Activity.class);
                eleven.putExtra("type", "14");
                startActivity(eleven);
                break;
            case R.id.twele:
                Intent twele = new Intent(context, Tem_Registration_Activity.class);
                twele.putExtra("type", "11");
                startActivity(twele);
                break;
            case R.id.thirteen:
                Intent thirteen = new Intent(context, Tem_Registration_Activity.class);
                thirteen.putExtra("type", "12");
                startActivity(thirteen);
                break;
            case R.id.fourteen:
                Intent fourteen = new Intent(context, Tem_Registration_Activity.class);
                fourteen.putExtra("type", "13");
                startActivity(fourteen);
                break;

            case R.id.first1:
                Intent i1 = new Intent(context, DL_Information_Activity.class);
                i1.putExtra("type", "1");
                startActivity(i1);
                break;
            case R.id.second1:
                Intent second1 = new Intent(context, DL_Information_Activity.class);
                second1.putExtra("type", "2");
                startActivity(second1);
                break;
            case R.id.third1:
                Intent third1 = new Intent(context, DL_Information_Activity.class);
                third1.putExtra("type", "3");
                startActivity(third1);
                break;
            case R.id.fourth1:
                Intent fourth1 = new Intent(context, DL_Information_Activity.class);
                fourth1.putExtra("type", "4");
                startActivity(fourth1);
                break;
            case R.id.five1:
                Intent five1 = new Intent(context, DL_Information_Activity.class);
                five1.putExtra("type", "5");
                startActivity(five1);
                break;
            case R.id.six1:
                Intent six1 = new Intent(context, DL_Information_Activity.class);
                six1.putExtra("type", "6");
                startActivity(six1);
                break;
            case R.id.seven1:
                Intent seven1 = new Intent(context, DL_Information_Activity.class);
                seven1.putExtra("type", "7");
                startActivity(seven1);
                break;

        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Information");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
