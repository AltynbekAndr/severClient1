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
import kg.com.severelectro.models.Counterr;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ListAddOffActivity extends AppCompatActivity {
    ListView listView2;
    CustomArrayAdapter customArrayAdapter = null;
    Counterr [] counterr = null;
    ProgressBar progressBar2 = null;
    TextView textView34 = null;
    AsyncTaskForListView asyncTaskForListView = null;
    String ipStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_add_off);
        listView2 = (ListView) findViewById(R.id.listView2);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        textView34 = (TextView) findViewById(R.id.textView34);
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
            Toast.makeText(ListAddOffActivity.this,"Файл с IP:PORT не найден",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(ListAddOffActivity.this,"Ошибка чтения Файла с IP:PORT",Toast.LENGTH_SHORT).show();
        }
        boolean bool= isOnline();
        if(bool){
            asyncTaskForListView = new AsyncTaskForListView();
            asyncTaskForListView.execute();
        }else{
            Toast.makeText(ListAddOffActivity.this,"Нет подключения к интернету",Toast.LENGTH_SHORT).show();
        }
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListAddOffActivity.this,OnAbonent.class);
                if(counterr[i].getLs()!=null&&counterr[i].getLs()!=""&&counterr[i].getLs().length()!=0){
                   intent.putExtra("cspot",counterr[i].getLs());
                }
                if(counterr[i].getRaion()!=null&&counterr[i].getRaion()!=""&&counterr[i].getRaion().length()!=0){
                    intent.putExtra("nregion",counterr[i].getRaion());
                }
                if(counterr[i].getFio_customer()!=null&&counterr[i].getFio_customer()!=""&&counterr[i].getFio_customer().length()!=0){
                    intent.putExtra("cspot2",counterr[i].getFio_customer());
                }
                if(counterr[i].getCounters()!=null&&counterr[i].getCounters()!=""&&counterr[i].getCounters().length()!=0){
                    intent.putExtra("ccounter",counterr[i].getCounters());
                }
                if(counterr[i].getAdress()!=null&&counterr[i].getAdress()!=""&&counterr[i].getAdress().length()!=0){
                    intent.putExtra("address1", counterr[i].getAdress());
                }
                if(counterr[i].getDom()!=null&&counterr[i].getDom()!=""&&counterr[i].getDom().length()!=0){
                    intent.putExtra("address2", counterr[i].getDom());
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
                Toast.makeText(ListAddOffActivity.this,"Нет подключения к интернету",Toast.LENGTH_SHORT).show();
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
        String url = ipStr+"/getuserdata/getList2";
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
                counterr = new Counterr[lengthJsonArray];
                try {
                    for(int i =0;i<= lengthJsonArray;i++){
                        obb = jsonArray.getJSONObject(i);
                        Counterr count = new Counterr();
                        if (obb.getString("comand")!=null&&obb.getString("comand")!=""&&obb.getString("comand").length()!=0){
                            count.setComand(obb.getString("comand"));
                        }else{
                            count.setComand("null");
                        }
                        if(obb.getString("ls")!=null&&obb.getString("ls")!=""&&obb.getString("ls").length()!=0){
                            count.setLs(obb.getString("ls"));
                        }else{
                            count.setLs("null");
                        }
                        if(obb.getString("raion")!=null&&obb.getString("raion")!=""&&obb.getString("raion").length()!=0){
                            count.setRaion(obb.getString("raion"));
                        }else{
                            count.setRaion("null");
                        }
                        if (obb.getString("fio_customer")!=null&&obb.getString("fio_customer")!=""&&obb.getString("fio_customer").length() != 0) {
                            count.setFio_customer(obb.getString("fio_customer"));
                        }else{
                            count.setFio_customer("null");
                        }
                        if (obb.getString("counters")!=null&&obb.getString("counters")!=""&&obb.getString("counters").length()!=0){
                            count.setCounters(obb.getString("counters"));
                        }else{
                            count.setCounters("null");
                        }
                        if (obb.getString("adress")!=null&&obb.getString("adress")!=""&&obb.getString("adress").length()!=0){
                            count.setAdress(obb.getString("adress"));
                        }else{
                            count.setAdress("null");
                        }
                        if(obb.getString("dom")!=null&&obb.getString("dom")!=""&&obb.getString("dom").length()!=0) {
                            count.setDom(obb.getString("dom"));
                        }else{
                            count.setDom("null");
                        }
                        if(obb.getString("users")!=null&&obb.getString("users")!=""&&obb.getString("users").length()!=0){
                            count.setUsers(obb.getString("users"));
                        }else{
                            count.setUsers("null");
                        }
                        if(obb.getString("timesend")!=null&&obb.getString("timesend")!=""&&obb.getString("timesend").length()!=0){
                            count.setTimesend(obb.getString("timesend"));
                        }else{
                            count.setTimesend("null");
                        }
                        counterr[i] = count;
                        if(counterr[i].getFio_customer()!=null&&counterr[i].getFio_customer()!=""){
                            ssss = counterr[i].getFio_customer();
                        }else{
                            ssss = "null";
                        }
                        fio.add(ssss);
                    }
                } catch (JSONException e) {
                }
                    customArrayAdapter = new CustomArrayAdapter(ListAddOffActivity.this,fio);
                    listView2.setAdapter(customArrayAdapter);
                    jsonArray = null;
                }else{
                     Toast.makeText(ListAddOffActivity.this,"нет данных для отображения",Toast.LENGTH_SHORT).show();
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

