package kg.com.severelectro;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kg.com.severelectro.models.Abonent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button btn = null,btn2 = null;
    AlertDialog.Builder builder = null;
    AlertDialog alert = null;
    ProgressBar pbar = null;
    TextView textAlert = null;
    LinearLayout containerLinear = null;
    String pathToShrift = "fonts/calibril.ttf";
    Typeface typefacen = null;
    EditText editText4 = null;
    String ss1 = null;
    StringBuilder ss3 = new StringBuilder();
    StringBuilder ss4 = new StringBuilder();
    JSONArray jsonArray = null;
    JSONObject obb = null;
    JSONObject json = new JSONObject();
    ListView listview = null;
    CustomArrayAdapter customArrayAdapter = null;
    ArrayList fio = null;
    Abonent [] abonent = null;
    Spinner spinner = null;
    RadioGroup radioGroup = null;
    int cnt;
    String raion = null;
    String str4serch = null;
    TextView txt63 = null;
    TextView txt64 = null;
    String ipStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(openFileInput("ip.txt"),"UTF-8"));
             ipStr = bf.readLine();
            if(ipStr!=null&&ipStr!=null){
                bf.close();
            }else{
                Intent intent = new Intent(getApplicationContext(),IpPortActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this,"Файл с IP:PORT не найден",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,"Ошибка чтения Файла с IP:PORT",Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        typefacen = Typeface.createFromAsset(getAssets(), pathToShrift);
        listview = (ListView) findViewById(R.id.listView);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(MainActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.activity_search, null);
                editText4 = (EditText) view1.findViewById(R.id.editText4);
                spinner = (Spinner) view1.findViewById(R.id.spinner);
                btn = (Button) view1.findViewById(R.id.button2);
                btn2 = (Button) view1.findViewById(R.id.button3);
                pbar = (ProgressBar) view1.findViewById(R.id.progressBar);
                textAlert = (TextView) view1.findViewById(R.id.textView5);
                txt63 = (TextView) view1.findViewById(R.id.textView63);
                txt64 = (TextView) view1.findViewById(R.id.textView64);
                containerLinear = (LinearLayout) view1.findViewById(R.id.containerLinear);
                radioGroup = (RadioGroup) view1.findViewById(R.id.radio);
                final ArrayAdapter<?> arrayAdapterM = ArrayAdapter.createFromResource(MainActivity.this, R.array.raions, android.R.layout.simple_spinner_item);
                arrayAdapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapterM);
                builder.setView(view1);
                alert = builder.create();
                alert.show();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        raion = String.valueOf(arrayAdapterM.getItem(i));
                        txt63.setText("Поиск в районе " + raion + " ");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.radioButton:
                                str4serch = "adres";
                                txt64.setText("по адресу");
                                break;
                            case R.id.radioButton2:
                                str4serch = "fio";
                                txt64.setText("по Ф.И.О");
                                break;
                            case R.id.radioButton3:
                                str4serch = "lschet";
                                txt64.setText("по Л.Счету");
                                break;

                        }
                    }
                });


            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentForAbonentInformation();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void searchAlert(View view) {

        String sear = editText4.getText().toString();
        if(sear!=null&&sear!=""&&sear.length()!=0){
            boolean bool = isOnline();
        if(bool){


             ss1 = editText4.getText().toString();
            char ch;
            for (int i = 0; i < ss1.length(); i++) {
                ch = ss1.charAt(i);
                ss3.append(ss1.charAt(i));
                if (ch == ' ') {
                    for (int k = i += 1; k < ss1.length(); k++) {
                        ss4.append(ss1.charAt(k));
                        if (k == ss1.length()) {
                            break;
                        }
                    }
                    break;
                }
            }if (str4serch != null && str4serch.length() != 0 && str4serch != "" && raion != null && raion != "" && raion.length() != 0) {
                try {
                    //json.put("nregion", raion);
                    if (str4serch.equals("lschet")) {
                        json.put("cspot", ss1);
                    }
                    if (str4serch.equals("fio")) {
                        json.put("cspot2", ss1);
                    }
                    if (str4serch.equals("adres")) {
                        if (ss4.length() == 0) {
                            json.put("address1", ss3.toString());
                        } else if (ss4.length() != 0) {
                            json.put("address1", ss3.toString());
                            json.put("address2", ss4.toString());
                        }
                    }

                } catch (JSONException e) {
                }
                containerLinear.setVisibility(containerLinear.GONE);
                pbar.setVisibility(pbar.VISIBLE);
                textAlert.setVisibility(textAlert.VISIBLE);
                btn2.setVisibility(btn2.VISIBLE);
                btn.setVisibility(btn.GONE);
            new AsyncOkHttp().execute();
        }else{
                Toast.makeText(MainActivity.this,"Выберите категорию",Toast.LENGTH_SHORT).show();
            }
    }else{
            Toast.makeText(MainActivity.this,"Нет связи с интернетом",Toast.LENGTH_SHORT).show();
         }
    }else{
            Toast.makeText(MainActivity.this,"Введите текст для поиска",Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteAlert(View view) {
        alert.dismiss();
    }
    public void IntentForAbonentInformation(){
        Intent intent = new Intent(MainActivity.this,ForAbonentInformationActivity.class);

        if(abonent[cnt].getCcounter()!=null&&abonent[cnt].getCcounter()!=""&& abonent[cnt].getCcounter().length()!=0){
            intent.putExtra("ccounter",abonent[cnt].getCcounter());
        }else{
            intent.putExtra("ccounter","null");
        }
        if(abonent[cnt].getCcounter20()!=null&&abonent[cnt].getCcounter20()!="" &&abonent[cnt].getCcounter20().length()!=0){
            intent.putExtra("ccounter20",abonent[cnt].getCcounter());
        }else{
            intent.putExtra("ccounter20","null");
        }
        if(abonent[cnt].getCcounter2()!=null&&abonent[cnt].getCcounter2()!="" &&abonent[cnt].getCcounter2().length()!=0){
            intent.putExtra("ccounter2",abonent[cnt].getCcounter());
        }else{
            intent.putExtra("ccounter2","null");
        }
        if(abonent[cnt].getCspot()!=null&&abonent[cnt].getCspot()!=""&&abonent[cnt].getCspot().length()!= 0){
            intent.putExtra("cspot",abonent[cnt].getCspot());
        }else{
            intent.putExtra("cspot","null");
        }
        if(abonent[cnt].getCspot2()!=null&&abonent[cnt].getCspot2()!=""&&abonent[cnt].getCspot2().length() !=0){
            intent.putExtra("cspot2",abonent[cnt].getCspot2());
        }else{
            intent.putExtra("cspot2","null");
        }
        if(abonent[cnt].getAddress1()!=null&&abonent[cnt].getAddress1()!=""&& abonent[cnt].getAddress1().length()!=0){
            intent.putExtra("address1", abonent[cnt].getAddress1());
        }else{
            intent.putExtra("address1","null");
        }
        if(abonent[cnt].getAddress2()!=null&&abonent[cnt].getAddress2()!=""&& abonent[cnt].getAddress2().length()!=0){
            intent.putExtra("address2", abonent[cnt].getAddress2());
        }else{
            intent.putExtra("address2","null");
        }
        if(abonent[cnt].getNamitype()!=null&&abonent[cnt].getNamitype()!=""&&abonent[cnt].getNamitype().length()!=0){
            intent.putExtra("namitype", abonent[cnt].getNamitype());
        }else{
            intent.putExtra("namitype","null");
        }
        if(abonent[cnt].getCstation()!=null&&abonent[cnt].getCstation()!=""&&abonent[cnt].getCstation().length()!=0){
            intent.putExtra("cstation", abonent[cnt].getCstation());
        }else{
            intent.putExtra("cstation","null");
        }
        if(abonent[cnt].getNregion()!=null&&abonent[cnt].getNregion()!=""&&abonent[cnt].getNregion().length()!=0){
            intent.putExtra("nregion",abonent[cnt].getNregion());
        }else{
            intent.putExtra("nregion", "null");
        }
     startActivity(intent);
    }



class AsyncOkHttp extends AsyncTask<Void,Void,Void>{

        OkHttpClient client = new OkHttpClient();
    @Override
    protected Void doInBackground(Void... voids) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json.toString());
        Request request = new Request.Builder()
                .url(ipStr+ "/getuserdata/search")
                .post(body)
                .build();
        Log.e("json", json.toString());
        Log.e("request", request.toString());
        try {
            Response response = client.newCall(request).execute();
            jsonArray  = new JSONArray(response.body().string());
            if(jsonArray!=null){
                Log.e("JSONaRRAY========>", jsonArray.toString());
            }
        } catch (IOException e) {
        } catch (JSONException e) {
            attention = "attention";
            publishProgress();
        }

        return null;
    }
    String attention = null;
    @Override
    protected void onProgressUpdate(Void... values) {
        if(attention.equals("attention")){
            Toast.makeText(MainActivity.this,"нет данных для отображения",Toast.LENGTH_SHORT).show();
        }else{
            alert.dismiss();
            ss1 = null;
            ss3.delete(0,ss3.length());
            ss4.delete(0, ss4.length());
            Toast.makeText(MainActivity.this,"Выберите категорию",Toast.LENGTH_SHORT).show();
            attention = "";

        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        alert.dismiss();
        int lengthJsonArray = 0 ;
        String ssss = null;
        if(jsonArray!=null) {
            lengthJsonArray = Integer.valueOf(String.valueOf(jsonArray.length()));
        }
        if(lengthJsonArray!=0){
            try {
                fio = new ArrayList(lengthJsonArray);
                abonent = new Abonent[lengthJsonArray];
                for(int i =0;i<= lengthJsonArray;i++){
                    obb = jsonArray.getJSONObject(i);
                    Abonent a = new Abonent();
                    if(obb.getString("cspot")!=null&&obb.getString("cspot")!=""&&obb.getString("cspot").length()!=0){
                        a.setCspot(obb.getString("cspot"));
                    }
                    if (obb.getString("cspot2")!=null&&obb.getString("cspot2")!=""&&obb.getString("cspot2").length()!=0){
                        a.setCspot2(obb.getString("cspot2"));
                    }
                    if (obb.getString("address1")!=null&&obb.getString("address1")!=""&&obb.getString("address1").length()!=0){
                        a.setAddress1(obb.getString("address1"));
                    }
                    if (obb.getString("address2")!=null&&obb.getString("address2")!=""&&obb.getString("address2").length()!=0){
                        a.setAddress2(obb.getString("address2"));
                    }
                    if (obb.getString("namitype")!=null&&obb.getString("namitype")!=""&&obb.getString("namitype").length()!=0){
                        a.setNamitype(obb.getString("namitype"));
                    }
                    if (obb.getString("cstation")!=null&&obb.getString("cstation")!=""&&obb.getString("cstation").length()!=0){
                        a.setCstation(obb.getString("cstation"));
                    }
                    if (obb.getString("nregion")!=null&&obb.getString("nregion")!=""&&obb.getString("nregion").length()!=0){
                        a.setNregion(obb.getString("nregion"));
                    }
                    if (obb.getString("ccounter")!=null&&obb.getString("ccounter")!=""&&obb.getString("ccounter").length()!=0){
                        a.setCcounter(obb.getString("ccounter"));
                    }
                    if (obb.getString("ccounter2")!=null&&obb.getString("ccounter2")!=""&&obb.getString("ccounter2").length()!=0){
                        a.setCcounter2(obb.getString("ccounter2"));
                    }
                    if (obb.getString("ccounter20")!=null&&obb.getString("ccounter20")!=""&&obb.getString("ccounter20").length()!=0){
                        a.setCcounter20(obb.getString("ccounter20"));
                    }
                    abonent[i] = a;

                    Log.e("abonItyi",abonent[i].getCspot2());
                    if(abonent[i].getCspot2()!=null&&abonent[i].getCspot2()!=""){
                        ssss = abonent[i].getCspot2();
                    }else{
                        ssss = "null";
                    }
                    fio.add(ssss);
                    Log.e("ssss", ssss);
                }
            } catch (JSONException e) {
            }


            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,fio);
            listview.setAdapter(customArrayAdapter);
            jsonArray = null;
        }else{
            Toast.makeText(MainActivity.this,"нет данных для отображения",Toast.LENGTH_SHORT).show();
        }
        ss1 = "";
        ss3.delete(0,ss3.length());
        ss4.delete(0, ss4.length());
                try {
                    json.put("cspot", null);
                    json.put("cspot2", null);
                    json.put("address1", null);
                    json.put("address2", null);
                }catch(Exception ex){}
            }
        }
























    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),IpPortActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.off_china) {
            intent = new Intent(MainActivity.this,ListChinaOffActivity.class);
            startActivity(intent);
        }else if (id == R.id.off_add) {
            intent = new Intent(MainActivity.this,ListAddOffActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
