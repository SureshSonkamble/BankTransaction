package com.sonkamble.personal_transaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trns_Activity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> post_arryList;
    ProgressBar progressBar;
    ProgressDialog progressDoalog;
    vehical_recyclerAdapter demo_recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager_demo;
    private RecyclerView recyclerView_demo;
    String delete_url="https://codingseekho.in/APP/EXP/delete_test.php";
  //  String json_url="https://codingseekho.in/APP/EXP/search.php?search=";
    String json_url="https://codingseekho.in/APP/EXP/search.php?uid=";
   // String json_url="https://codingseekho.in/APP/EXP/transaction_list.php?uid=";
    int c_ttl=0;
    int d_ttl=0;
    int cl_ttl=0;
    int bal=0;
    int flag=0;
    TextView credit_ttl,debit_ttl,clbal_ttl;
    String tid,uid;
    String max_tid="";
    String user_tid="";
    String search_word="";
    SearchView grid_searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction__history);
        clbal_ttl=(TextView) findViewById(R.id.clbal_ttl);
        debit_ttl=(TextView) findViewById(R.id.debit_ttl);
        credit_ttl=(TextView) findViewById(R.id.credit_ttl);
        progressBar=(ProgressBar)findViewById(R.id.pg);
        post_arryList = new ArrayList<HashMap<String, String>>();
        SharedPreferences sp = getSharedPreferences("STUD_DATA", MODE_PRIVATE);
        uid = sp.getString("sid", "");
        recyclerView_demo=(RecyclerView)findViewById(R.id.recycler_vehical);
        //--------for linear layout--------------
        layoutManager_demo = new LinearLayoutManager(Trns_Activity.this, RecyclerView.VERTICAL, false);
        recyclerView_demo.setLayoutManager(layoutManager_demo);
        //---------for grid layout--------------
        // recyclerView_demo.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        //------------------------------------------
        demo_recyclerAdapter=new vehical_recyclerAdapter(Trns_Activity.this,post_arryList);
        recyclerView_demo.setAdapter(demo_recyclerAdapter);
        //--------- search ------------
        grid_searchView=(SearchView)findViewById(R.id.grid_searchView);
        grid_searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                search_word="";
                load_data(search_word);
            }
        });
        grid_searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search_word="";
                load_data(search_word);
                return false;
            }
        });

        grid_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search_word="";
                if (newText.length() >0)
                {   search_word=newText;
                    //new FetchSearchResult().execute();
                    load_data( search_word);
                }
                else  if (TextUtils.isEmpty(newText)){
                    search_word="";
                    load_data(search_word);
                }
                else
                { }
                return false;
            }
        });
        //********************
        load_data(search_word);
       // load_data();
    }
    public void load_data(String search_word)
    {    c_ttl=0;
         d_ttl=0;
         cl_ttl=0;
         bal=0;
         max_tid="";
        flag=0;
         credit_ttl.setText("");
         debit_ttl.setText("");
         clbal_ttl.setText("");
        {  /* progressDoalog = new ProgressDialog(Trns_Activity.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();*/
            json_url=json_url+uid+"&search="+search_word;
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, json_url, null, new Response.Listener<JSONObject>() {
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
                                post_arryList.clear();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  T_ID = c.getString("T_ID");
                                        String  U_ID = c.getString("U_ID");
                                        String  DATE = c.getString("DATE");
                                        String  NOTE = c.getString("NOTE");
                                        String  DEBIT = c.getString("DEBIT");
                                        String CREDIT=c.getString("CREDIT");
                                        String CLBAL=c.getString("CLBAL");
                                        map.put("T_ID", T_ID);
                                        map.put("U_ID", U_ID);
                                        map.put("DATE", DATE);
                                        map.put("NOTE", NOTE);
                                        map.put("DEBIT", DEBIT);
                                        map.put("CREDIT", CREDIT);
                                        map.put("CLBAL", CLBAL);

                                        post_arryList.add(map);
                                        c_ttl=c_ttl+Integer.parseInt(c.getString("CREDIT"));
                                        d_ttl=d_ttl+Integer.parseInt(c.getString("DEBIT"));
                                        cl_ttl=cl_ttl+Integer.parseInt(c.getString("CLBAL"));
                                        credit_ttl.setText(""+c_ttl);
                                        debit_ttl.setText(""+d_ttl);
                                        clbal_ttl.setText(""+cl_ttl);
                                        max_tid=c.getString("T_ID");
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
            MySingleton.getInstance(Trns_Activity.this).addToRequestque(jsonObjectRequest);
        }
        if (demo_recyclerAdapter != null) {
            demo_recyclerAdapter.notifyDataSetChanged();
            System.out.println("Adapter " + demo_recyclerAdapter.toString());
        }
    }
    public class vehical_recyclerAdapter extends RecyclerView.Adapter<vehical_recyclerAdapter.DemoViewHolder>
    {
        Context context;
        ArrayList<HashMap<String, String>> img_list;

        public vehical_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> quans_list) {
            this.img_list = quans_list;
            this.context = context;
        }
        @Override
        public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trns_list, parent, false);
            DemoViewHolder ViewHolder = new DemoViewHolder(view);
            return ViewHolder;
        }

        @Override
        public void onBindViewHolder(DemoViewHolder merchantViewHolder, final int position)
        {
            merchantViewHolder.txt_d1.setText(img_list.get(position).get("DATE"));
            merchantViewHolder.txt_d2.setText(img_list.get(position).get("NOTE"));
            merchantViewHolder.txt_d3.setText(img_list.get(position).get("CREDIT"));
            merchantViewHolder.txt_d4.setText(img_list.get(position).get("DEBIT"));
            merchantViewHolder.txt_d5.setText(img_list.get(position).get("CLBAL"));
            if(!img_list.get(position).get("CREDIT").equals("0"))
            {    flag=1;
                merchantViewHolder.txt_d3.setTextColor(Color.rgb(0, 102, 0));
            }
            if(!img_list.get(position).get("DEBIT").equals("0"))
            {   flag=2;
                merchantViewHolder.txt_d4.setTextColor(Color.rgb(204, 51, 0));
            }
            merchantViewHolder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uid=img_list.get(position).get("U_ID");
                    tid=img_list.get(position).get("T_ID");
                    bal=Integer.parseInt(img_list.get(position).get("CREDIT"))+Integer.parseInt(img_list.get(position).get("DEBIT"));
                //    Toast.makeText(context, "tid: "+tid, Toast.LENGTH_SHORT).show();
                    if(max_tid.equals(tid)) {
                        delete_alert();
                    }
                    else {
                        delete_warn();
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return img_list.size();
        }

        public class DemoViewHolder extends RecyclerView.ViewHolder
        {     LinearLayout lin;
              TextView txt_d1,txt_d2,txt_d3,txt_d4,txt_d5,txt_d6;
            public DemoViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d1 = (TextView) itemView.findViewById(R.id.txt_d1);
                this.txt_d2 = (TextView) itemView.findViewById(R.id.txt_d2);
                this.txt_d3 = (TextView) itemView.findViewById(R.id.txt_d3);
                this.txt_d4 = (TextView) itemView.findViewById(R.id.txt_d4);
                this.txt_d5 = (TextView) itemView.findViewById(R.id.txt_d5);

            }
        }
    }
    void delete_warn(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Trns_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.fail);
        builder.setMessage("Are You Not Allowed To Delete In Between Records?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    void delete_alert(){
         AlertDialog.Builder builder = new AlertDialog.Builder(Trns_Activity.this, R.style.AppCompatAlertDialogStyle);
         builder.setTitle("Alert");
         builder.setIcon(R.drawable.fail);
         builder.setMessage("Are You Sure,You Want To Delete?");
         builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 if(post_arryList.size()>1) {
                     delete();
                 }
                 else {
                     delete_warn_alert();
                 }
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
    void delete()
    {
         StringRequest stringRequest=new StringRequest(Request.Method.POST, delete_url, new Response.Listener<String>() {
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
                             Intent i=new Intent(getApplicationContext(),MainActivity.class);
                             startActivity(i);
                             finish();
                             Toast.makeText(getApplicationContext(), "Deleted Successfully..!!", Toast.LENGTH_SHORT).show();
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
                 param.put("tid",tid);
                 param.put("bal",""+bal);
                 param.put("flag",""+flag);
                 return param;
             }
         };

         MySingleton.getInstance(Trns_Activity.this).addToRequestque(stringRequest);
     }
    void delete_warn_alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Trns_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.fail);
        builder.setMessage("Are You Not Allowed To Delete last Record..!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }
}
