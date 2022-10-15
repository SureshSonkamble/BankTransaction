package com.sonkamble.personal_transaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    Toolbar toolbar;
    EditText edt_mob;
    EditText edit_user_name,edit_user_mob,edit_opbal;
    String str_uname,str_pass,stud_name,stud_id,str_user,str_mob;
    Button btn_login;
    Button btn_save;
    TextView txt_web;
    ImageView btn_cancel;
    AlertDialog dialog;
    ProgressDialog progressDoalog;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    String user,mob,opbal;
    String login_url= "https://codingseekho.in/APP/EXP/user_login.php";
    String user_url= "https://codingseekho.in/APP/EXP/add_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("USER Login");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        sessionManager = new SessionManager(this);
        sp_pi_login = getSharedPreferences("PI", MODE_PRIVATE);
        editor_sp_pi_login = sp_pi_login.edit();
        if (sessionManager.isLoggedIn()) {
            String user=sp_pi_login.getString("name",null);
            String id=sp_pi_login.getString("id",null);
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            i.putExtra("name",user);
            i.putExtra("id",id);
            startActivity(i);
            finish();
        }

        /*txt_web=(TextView) findViewById(R.id.txt_web);
        txt_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("http://sonkamble.com/"));
                startActivity(i);
            }
        });*/
        edt_mob=(EditText)findViewById(R.id.edt_mob);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_mob.getText().toString().equals(""))
                {
                    Toast.makeText(Login.this, "Mobile Number Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    login();
                }
            }
        });

        TextView signUp_text = findViewById(R.id.signUp_text);
        signUp_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user_popup();
            }
        });
    }

    void login()
    {
        str_mob = edt_mob.getText().toString();
        progressDoalog = new ProgressDialog(Login.this);
        progressDoalog.setMessage("Login....");
        progressDoalog.show();
        // progressbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
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
                            stud_name= postobject.getString("name");
                            stud_id= postobject.getString("id");
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("STUD_DATA", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("sname",stud_name);
                            editor.putString("sid",stud_id);
                            editor.commit();
                            sessionManager.createLoginSession(stud_name, edt_mob.getText().toString());
                            Toast.makeText(getApplicationContext(), "Login Successfull.", Toast.LENGTH_SHORT).show();
                            editor_sp_pi_login.putString("email", stud_name);
                            editor_sp_pi_login.commit();

                            Intent i = new Intent(Login.this, MainActivity.class);
                            i.putExtra("name", stud_name);
                            i.putExtra("id", stud_id);
                            startActivity(i);
                            finish();

                        } else if (status.equals("400")) {
                            // english_poemList.clear();
                            Toast.makeText(getApplicationContext(), "Invalid Credentials." + status, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No dat found ... please try again", Toast.LENGTH_SHORT).show();
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
                    param.put("mobile", str_mob);
                return param;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestque(stringRequest);

    }
    public void add_user_popup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_user, null);
        edit_user_name = (EditText) alertLayout.findViewById(R.id.edit_user_name);
        edit_user_mob = (EditText) alertLayout.findViewById(R.id.edit_user_mob);
        edit_opbal = (EditText) alertLayout.findViewById(R.id.edit_opbal);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=edit_user_name.getText().toString();
                mob=edit_user_mob.getText().toString();
                opbal=edit_opbal.getText().toString();
                if(user.equals("")||mob.equals("")||opbal.equals(""))
                {
                    Toast.makeText(Login.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, user_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response != null) {
                                    // progressbar.setVisibility(View.INVISIBLE);
                                    //progressDoalog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    JSONObject postobject = jsonObject.getJSONObject("posts");
                                    String status = postobject.getString("status");
                                    //String client_status = postobject.getString("client_status");
                                    if (status.equals("200")) {
                                        Intent i=new Intent(getApplicationContext(),Login.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(getApplicationContext(), "Registered Successfully..!!", Toast.LENGTH_SHORT).show();
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
                            param.put("uname",user);
                            param.put("mobile",mob);
                            param.put("opbal",opbal);
                            return param;
                        }
                    };

                    MySingleton.getInstance(Login.this).addToRequestque(stringRequest);
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

        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
}
