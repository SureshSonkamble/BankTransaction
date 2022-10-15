package com.sonkamble.personal_transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinearLayout add_credit,add_debit,view_transaction;
    Button btn_save;
    ImageView btn_cancel,img_logout;
    EditText edit_exp_amt,edit_note;
    TextView txt_opbal,edit_date,txt_ttl_credit,txt_ttl_debit,txt_ttl_cpd,txt_ttl_cmd;
        AlertDialog dialog;
        ProgressBar progressBar;
        ProgressDialog progressDoalog;
    String exp_amt,exp_date,exp_note,Temp_date,Query_date, OPBAL;
    String opbal_url="https://codingseekho.in/APP/EXP/opbal_list.php?uid=";
    String credit_url="https://codingseekho.in/APP/EXP/insert_credit.php";
    String debit_url="https://codingseekho.in/APP/EXP/insert_debit.php";
    String json_url="https://codingseekho.in/APP/EXP/transaction_list.php?uid=";
   // String uid="1";
    int opbal=0;
    int expbal=0;
    int c_ttl=0;
    int d_ttl=0;
    int cl_ttl=0;
    int mYear, mMonth, mDay;
    SessionManager sessionManager;
    SharedPreferences sp;
    String user, name, uid,str_user;
    String formattedDate, str_month="",str_day,systemDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.pg);
        view_transaction=(LinearLayout) findViewById(R.id.view_transaction);

        img_logout=(ImageView) findViewById(R.id.img_logout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.exit);
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logoutUser();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
      //  txt_ttl_clbal=(TextView) findViewById(R.id.txt_ttl_clbal);
        txt_ttl_cpd=(TextView) findViewById(R.id.txt_ttl_cpd);
        txt_ttl_cmd=(TextView) findViewById(R.id.txt_ttl_cmd);
        txt_ttl_credit=(TextView) findViewById(R.id.txt_ttl_credit);
        txt_ttl_debit=(TextView) findViewById(R.id.txt_ttl_debit);
        txt_opbal=(TextView) findViewById(R.id.txt_opbal);

        sessionManager = new SessionManager(this);
        //------------------------User Session------------------------------------------
        Bundle b = getIntent().getExtras();
        try {
            user = b.getString("name");
            uid = b.getString("id");

        } catch (Exception e) {
        }

        if (user == null) {
            // Toast.makeText(getApplicationContext(),"User Id Null...",Toast.LENGTH_LONG).show();
        } else {
            sp = this.getSharedPreferences("PI", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", name);
            editor.putString("id", uid);
            editor.commit();
        }
        SharedPreferences sp = getSharedPreferences("STUD_DATA", MODE_PRIVATE);
        name = sp.getString("sname", "");
        uid = sp.getString("sid", "");

        add_credit=(LinearLayout)findViewById(R.id.add_credit);
        add_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                credit_popup_form();
            }
        });
        add_debit=(LinearLayout)findViewById(R.id.add_debit);
        add_debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debit_popup_form();
            }
        });
        load_opbal();
      /*  android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

            }
        }, 3000);*/
        view_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent i=new Intent(getApplicationContext(),Trns_Activity.class);
              startActivity(i);
             // finish();
            }
        });
        load_data();
    }
    public void credit_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_credit, null);

        edit_date = (TextView) alertLayout.findViewById(R.id.edit_date);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edit_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                Query_date=Temp_date;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        edit_note = (EditText) alertLayout.findViewById(R.id.edit_note);
        edit_exp_amt = (EditText) alertLayout.findViewById(R.id.edit_exp_amt);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exp_date=edit_date.getText().toString();
                exp_note=edit_note.getText().toString();
                exp_amt=edit_exp_amt.getText().toString();
                if(exp_date.equals("")||exp_note.equals("")||exp_amt.equals(""))
                {
                    Toast.makeText(MainActivity.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, credit_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response != null) {
                                    // progressbar.setVisibility(View.INVISIBLE);
                                    progressDoalog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    JSONObject postobject = jsonObject.getJSONObject("posts");

                                    String status = postobject.getString("status");
                                    //String client_status = postobject.getString("client_status");
                                    if (status.equals("200")) {
                                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(getApplicationContext(), "Credited Successfully..!!", Toast.LENGTH_SHORT).show();
                                    } else if (status.equals("404")) {
                                        // english_poemList.clear();
                                        Toast.makeText(getApplicationContext(), "Error:" + status, Toast.LENGTH_LONG).show();

                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "No data found ... please try again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> param=new HashMap<String, String>();
                            param.put("uid",uid);
                            param.put("date",Query_date);
                            param.put("note",exp_note);
                            param.put("debit","0");
                            param.put("credit",exp_amt);
                            return param;
                        }
                    };

                    MySingleton.getInstance(MainActivity.this).addToRequestque(stringRequest);
                    dialog.dismiss();
                }

            }
        });

        btn_cancel = (ImageView) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void debit_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_debit, null);

        edit_date = (TextView) alertLayout.findViewById(R.id.edit_date);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edit_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                Query_date=Temp_date;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        edit_note = (EditText) alertLayout.findViewById(R.id.edit_note);
        edit_exp_amt = (EditText) alertLayout.findViewById(R.id.edit_exp_amt);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exp_date = edit_date.getText().toString();
                exp_note = edit_note.getText().toString();
                exp_amt = edit_exp_amt.getText().toString();
                expbal = Integer.parseInt(exp_amt);
                if (expbal > opbal) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.warning);
                    builder.setMessage("Debit Amount Can Not Be Greater Than Current Balance..!");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else {
                    if (exp_date.equals("") || exp_note.equals("") || exp_amt.equals("")) {
                        Toast.makeText(MainActivity.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, debit_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response != null) {
                                        // progressbar.setVisibility(View.INVISIBLE);
                                        progressDoalog.dismiss();
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        JSONObject postobject = jsonObject.getJSONObject("posts");
                                        String status = postobject.getString("status");
                                        //String client_status = postobject.getString("client_status");
                                        if (status.equals("200")) {
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(i);
                                            finish();
                                            Toast.makeText(getApplicationContext(), "Debited Successfully..!!", Toast.LENGTH_SHORT).show();
                                        } else if (status.equals("404")) {
                                            // english_poemList.clear();
                                            Toast.makeText(getApplicationContext(), "Error:" + status, Toast.LENGTH_LONG).show();

                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "No data found ... please try again", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                error.printStackTrace();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> param = new HashMap<String, String>();
                                param.put("uid", uid);
                                param.put("date", Query_date);
                                param.put("note", exp_note);
                                param.put("debit", exp_amt);
                                param.put("credit", "0");
                                return param;
                            }
                        };

                        MySingleton.getInstance(MainActivity.this).addToRequestque(stringRequest);
                        dialog.dismiss();
                    }
                }
            }
        });

        btn_cancel = (ImageView) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void load_opbal()
    {
        opbal=0;
        expbal=0;
        {   progressDoalog = new ProgressDialog(MainActivity.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, opbal_url+uid, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressDoalog.dismiss();
                    //Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                    try
                    {
                        if(response != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject postobject = jsonObject.getJSONObject("posts");
                            String status = postobject.getString("status");
                            if (status.equals("200")) {
                             //   Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  U_ID = c.getString("U_ID");
                                        OPBAL = c.getString("OPBAL");
                                        opbal=Integer.parseInt(c.getString("OPBAL"));
                                        txt_opbal.setText(OPBAL);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
        }
    }
    public void load_data()
    {
        {   /*progressDoalog = new ProgressDialog(MainActivity.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();*/

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, json_url+uid, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.VISIBLE);
                   // progressDoalog.dismiss();
                    //Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                    try
                    {
                        if(response != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject postobject = jsonObject.getJSONObject("posts");
                            String status = postobject.getString("status");
                            if (status.equals("200")) {
                                // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {

                                        c_ttl=c_ttl+Integer.parseInt(c.getString("CREDIT"));
                                        d_ttl=d_ttl+Integer.parseInt(c.getString("DEBIT"));
                                        cl_ttl=cl_ttl+Integer.parseInt(c.getString("CLBAL"));
                                        txt_ttl_credit.setText(""+c_ttl);
                                        txt_ttl_debit.setText(""+d_ttl);
                                        int cpd=c_ttl+d_ttl;
                                        txt_ttl_cpd.setText(""+cpd);
                                        int cmd=c_ttl-d_ttl;
                                        txt_ttl_cmd.setText(""+cmd);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){}
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.warning);
        builder.setMessage("Are You Sure You Want To Exit..!");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }
}