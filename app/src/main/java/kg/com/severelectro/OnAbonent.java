package kg.com.severelectro;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import kg.com.severelectro.models.Abonent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OnAbonent extends AppCompatActivity {
    JSONObject json = null;
    JSONObject jsonResult = null;
    String cspot = null;
    String cspot2 = null;
    String address1 = null;
    String address2 = null;
    String namitype = null;
    String cstation = null;
    String nregion = null;
    String ccounter = null;
    String ccounter2 = null;
    String ccounter20 = null;

    TextView txt1 = null;
    TextView txt2 = null;
    TextView txt3 = null;
    TextView txt4 = null;
    TextView txt8 = null;
    TextView txt9 = null;
    TextView txt10 = null;
    TextView txt11 = null;
    TextView txt12 = null;
    TextView txt13 = null;
    String pathToShrift = "fonts/calibril.ttf";
    Typeface typefacen = null;
    String urlOff = null;
    String urlOn = null;
    String ipStr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_abonent);

        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(openFileInput("ip.txt"),"UTF-8"));
            ipStr = bf.readLine();
            if(ipStr!=null){
                bf.close();
                urlOff = ipStr+"/getuserdata/off";
                urlOn = ipStr+"/getuserdata/on";
            }else{
                Intent intent = new Intent(getApplicationContext(),IpPortActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(OnAbonent.this,"Файл с IP:PORT не найден",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(OnAbonent.this,"Ошибка чтения Файла с IP:PORT",Toast.LENGTH_SHORT).show();
        }
        cspot = getIntent().getStringExtra("cspot");
        cspot2 = getIntent().getStringExtra("cspot2");
        address1 = getIntent().getStringExtra("address1");
        address2 = getIntent().getStringExtra("address2");
        namitype = getIntent().getStringExtra("namitype");
        cstation = getIntent().getStringExtra("cstation");
        nregion = getIntent().getStringExtra("nregion");
        ccounter = getIntent().getStringExtra("ccounter");
        ccounter2 = getIntent().getStringExtra("ccounter2");
        ccounter20 = getIntent().getStringExtra("ccounter20");

        typefacen = Typeface.createFromAsset(getAssets(), pathToShrift);
        txt1 = (TextView) findViewById(R.id.textView9);
        txt2 = (TextView) findViewById(R.id.textView16);
        txt3 = (TextView) findViewById(R.id.textView17);
        txt4 = (TextView) findViewById(R.id.textView18);
        txt8 = (TextView) findViewById(R.id.textView22);
        txt9 = (TextView) findViewById(R.id.textView23);
        txt10 = (TextView) findViewById(R.id.textView24);
        txt11 = (TextView) findViewById(R.id.textView4);
        txt12 = (TextView) findViewById(R.id.textView60);
        txt13 = (TextView) findViewById(R.id.textView61);

        txt1.setText(cspot);
        txt2.setText(cspot2);
        txt3.setText(address1);
        txt4.setText(address2);
        txt8.setText(namitype);
        txt9.setText(cstation);
        txt10.setText(nregion);
        txt11.setText(ccounter);
        txt12.setText(ccounter2);
        txt13.setText(ccounter20);

        txt1.setTypeface(typefacen);
        txt2.setTypeface(typefacen);
        txt3.setTypeface(typefacen);
        txt4.setTypeface(typefacen);
        txt8.setTypeface(typefacen);
        txt9.setTypeface(typefacen);
        txt10.setTypeface(typefacen);
        txt11.setTypeface(typefacen);
        txt12.setTypeface(typefacen);
        txt13.setTypeface(typefacen);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    AlertDialog alert = null;
    public void sendData2(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(OnAbonent.this);
        builder.setMessage("подключить абонент ?")
                .setTitle("Подключение абонета")
                .setCancelable(true)
                .setPositiveButton("подключить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isOnline()) {
                            new AsyncOkHttpOn().execute();
                        }else {
                            Toast.makeText(OnAbonent.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                            new StoppingAlert().execute();
                        }

                    }
                })
                .setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(OnAbonent.this, "Отмена", Toast.LENGTH_SHORT).show();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    class StoppingAlert extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            alert.dismiss();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            alert.dismiss();
        }
    }
    class AsyncOkHttpOn extends AsyncTask<Void,Void,Void> {
        String str = null;
        OkHttpClient client = SingletonOkHttp.getClient();
        ProgressDialog progressDialog= null;
        String sst1 = null;
        String sst2 = null;
        String sst3 = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OnAbonent.this);
            progressDialog.setTitle("Подключение абонента...");
            progressDialog.setMessage("отправка данных...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(sst1!=null){
                Toast.makeText(OnAbonent.this,sst1,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else if(sst2!=null){
                Toast.makeText(OnAbonent.this,sst2,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else if(sst3!=null){
                Toast.makeText(OnAbonent.this,sst3,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            json = new JSONObject();
            try {
                json.put("cspot", cspot);
            }catch(Exception ex){}
            RequestBody body2 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request2 = new Request.Builder()
                    .url(urlOn)
                    .post(body2)
                    .build();
            try {
                Response response = client.newCall(request2).execute();
                jsonResult = new JSONObject(response.body().string());
                if(jsonResult!=null){
                    str = jsonResult.getString("result");
                    if(str!=null){
                        Log.e("результат-подключаемого",str);
                    }
                }
            } catch (IOException e) {
            } catch (JSONException e) {
            }
            if(str!=null&&str.equals("200")){
                sst1 = "Абонент успешно подключен";
                publishProgress();
            }else if(str!=null&&str.equals("300")){
                sst2 = "Абонент уже подключен";
                publishProgress();
            }else if(str!=null&&str.equals("100")){
                sst3 = "Абонент не может быть подключен";
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new StoppingAlert().execute();
            if(str!=null&&str.length()!=0) {
                progressDialog.dismiss();
            }

        }
    }
}
