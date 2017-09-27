package kg.com.severelectro;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import kg.com.severelectro.models.Abonent;
import kg.com.severelectro.models.Chinameters;
import kg.com.severelectro.models.Counterr;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ListChinaOffActivity extends AppCompatActivity {
    ListView listView2;
    CustomArrayAdapter customArrayAdapter = null;
    Chinameters[] chinameters = null;
    ProgressBar progressBar2 = null;
    TextView textView34 = null;
    AsyncTaskForListView asyncTaskForListView = null;
    String ipStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_china_off);
        listView2 = (ListView) findViewById(R.id.listView4);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar4);
        textView34 = (TextView) findViewById(R.id.textView36);
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(openFileInput("ip.txt"),"UTF-8"));
            ipStr = bf.readLine();
            if(ipStr!=null){
                bf.close();
            }else{
                Intent intent = new Intent(getApplicationContext(),IpPortActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(ListChinaOffActivity.this,"Файл с IP:PORT не найден",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(ListChinaOffActivity.this,"Ошибка чтения Файла с IP:PORT",Toast.LENGTH_SHORT).show();
        }
        boolean bool= isOnline();
        if(bool){
            asyncTaskForListView = new AsyncTaskForListView();
            asyncTaskForListView.execute();
        }else{
            Toast.makeText(ListChinaOffActivity.this,"Нет подключения к интернету",Toast.LENGTH_SHORT).show();
        }
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListChinaOffActivity.this,OnAbonent.class);

                if(chinameters[i].getRegion()!=null&&chinameters[i].getRegion()!=""&&chinameters[i].getRegion().length()!=0){
                    intent.putExtra("nregion",chinameters[i].getRegion());
                }
                if(chinameters[i].getMeter_no() !=null&&chinameters[i].getMeter_no() !=""&&chinameters[i].getMeter_no().length()!=0){
                    intent.putExtra("ccounter",chinameters[i].getMeter_no());
                }
                if(chinameters[i].getMeter_type()!=null&&chinameters[i].getMeter_type()!=""&&chinameters[i].getMeter_type().length()!=0){
                    intent.putExtra("ccounter2",chinameters[i].getMeter_type());
                }
                if(chinameters[i].getType_mvalue()!=null&&chinameters[i].getType_mvalue()!=""&&chinameters[i].getType_mvalue().length()!=0){
                    intent.putExtra("ccounter20",chinameters[i].getType_mvalue());
                }
                if(chinameters[i].getCust_name()!=null&&chinameters[i].getCust_name()!=""&&chinameters[i].getCust_name().length()!=0){
                    intent.putExtra("cspot2", chinameters[i].getCust_name());
                }
                if(chinameters[i].getCust_code()!=null&&chinameters[i].getCust_code()!=""&&chinameters[i].getCust_code().length()!=0){
                    intent.putExtra("cspot", chinameters[i].getCust_code());
                }
                if(chinameters[i].getCust_adr1()!=null&&chinameters[i].getCust_adr1()!=""&&chinameters[i].getCust_adr1().length()!=0){
                    intent.putExtra("address1", chinameters[i].getCust_adr1());
                }
                if(chinameters[i].getCust_adr2()!=null&&chinameters[i].getCust_adr2()!=""&&chinameters[i].getCust_adr2().length()!=0){
                    intent.putExtra("address2", chinameters[i].getCust_adr2());
                }

                startActivity(intent);
            }
        });
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            boolean bool= isOnline();
            if(bool){
                textView34.setVisibility(textView34.GONE);
                listView2.setVisibility(listView2.GONE);
                progressBar2.setVisibility(progressBar2.VISIBLE);
                new AsyncTaskForListView().execute();
            }else{
                Toast.makeText(ListChinaOffActivity.this,"Нет подключения к интернету",Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(progressBar2.GONE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class AsyncTaskForListView extends AsyncTask<Void,Void,Void> {
        ArrayList fio = new ArrayList();
        OkHttpClient client = SingletonOkHttp.getClient();
        JSONArray jsonArray = null;
        JSONObject obb = null;
        JSONObject jsonSend = null;
        String url = ipStr+"/getuserdata/getList";
        @Override
        protected void onPreExecute() {
            progressBar2.setVisibility(progressBar2.VISIBLE);
            textView34.setVisibility(textView34.GONE);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                jsonSend = new JSONObject("{'f':0,'l':100}");
            } catch (JSONException e) {
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonSend.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try {
                Log.e("jsonSend===>",jsonSend.toString());
                Log.e("request===>",request.toString());
                Response response = client.newCall(request).execute();
                jsonArray = new JSONArray(response.body().string());
            } catch (IOException e) {
            } catch (JSONException e) {
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int lengthJsonArray = 0 ;
            String ssss = null;
            if(jsonArray!=null) {
                lengthJsonArray = Integer.valueOf(String.valueOf(jsonArray.length()));
                if(lengthJsonArray!=0){
                    fio = new ArrayList(lengthJsonArray);
                    chinameters = new Chinameters[lengthJsonArray];
                    try {

                        for(int i =0;i<= lengthJsonArray;i++){
                            obb = jsonArray.getJSONObject(i);
                            Chinameters china = new Chinameters();
                            if (obb.getString("region")!=null&&obb.getString("region")!=""&&obb.getString("region").length() != 0) {
                                china.setRegion(obb.getString("region"));
                            }else{
                                china.setRegion("null");
                            }
                            if (obb.getString("command")!=null&&obb.getString("command")!=""&&obb.getString("command").length() != 0) {
                                china.setCommand(obb.getString("command"));
                            }else{
                                china.setCommand("null");
                            }
                            if (obb.getString("meter_type")!=null&&obb.getString("meter_type")!=""&&obb.getString("meter_type").length()!=0){
                                china.setMeter_type(obb.getString("meter_type"));
                            }else{
                                china.setMeter_type("null");
                            }
                            if(obb.getString("type_mvalue") !=null&&obb.getString("type_mvalue")!=""&&obb.getString("type_mvalue").length()!=0){
                                china.setType_mvalue(obb.getString("type_mvalue"));
                            }else{
                                china.setType_mvalue("null");
                            }
                            if (obb.getString("cust_name")!=null&&obb.getString("cust_name")!=""&&obb.getString("cust_name").length()!=0){
                                china.setCust_name(obb.getString("cust_name"));
                            }else{
                                china.setCust_name("null");
                            }
                            if (obb.getString("cust_code")!=null&&obb.getString("cust_code")!=""&&obb.getString("cust_code").length()!=0) {
                                china.setCust_code(obb.getString("cust_code"));
                            }else{
                                china.setCust_code("null");
                            }
                            if (obb.getString("cust_adr1")!=null&&obb.getString("cust_adr1")!=""&&obb.getString("cust_adr1").length()!=0){
                                china.setCust_adr1(obb.getString("cust_adr1"));
                            }else{
                                china.setCust_adr1("null");
                            }
                            if(obb.getString("cust_adr2")!=null&&obb.getString("cust_adr2")!=""&&obb.getString("cust_adr2").length()!=0){
                                china.setCust_adr2(obb.getString("cust_adr2"));
                            }else{
                                china.setCust_adr2("null");
                            }
                            chinameters[i] = china;
                            if(chinameters[i].getCust_name()!=null&&chinameters[i].getCust_name()!=""){
                                ssss = chinameters[i].getCust_name();
                            }else{
                                ssss = "null";
                            }
                            fio.add(ssss);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        customArrayAdapter = new CustomArrayAdapter(ListChinaOffActivity.this,fio);
                        listView2.setAdapter(customArrayAdapter);
                        jsonArray = null;
                    }else{
                        Toast.makeText(ListChinaOffActivity.this,"нет данных для отображения",Toast.LENGTH_SHORT).show();
                        progressBar2.setVisibility(progressBar2.GONE);
                        listView2.setVisibility(listView2.GONE);
                        textView34.setVisibility(textView34.VISIBLE);
                }
                }
            if(!(progressBar2.getVisibility()==View.GONE&&listView2.getVisibility()==View.GONE&&textView34.getVisibility()==View.VISIBLE)){
                progressBar2.setVisibility(progressBar2.GONE);
                textView34.setVisibility(textView34.GONE);
                listView2.setVisibility(listView2.VISIBLE);

            }
            }
        }
    }

