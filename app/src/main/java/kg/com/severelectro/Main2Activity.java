package kg.com.severelectro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {
    Button b = null;
    EditText editpassword = null;
    EditText editname = null;
    EditText editIp = null;
    ProgressDialog progressDialog = null;
    String spassword = null;
    String sname = null;
    String sUrl = null;
    String pathToShrift = "fonts/calibril.ttf";
    Typeface typefacen = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        b = (Button) findViewById(R.id.button);
        editpassword = (EditText) findViewById(R.id.editText);
        editname = (EditText) findViewById(R.id.editText2);
        editIp = (EditText) findViewById(R.id.editText3);
        typefacen = Typeface.createFromAsset(getAssets(), pathToShrift);
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(openFileInput("name.txt"),"UTF-8"));
            BufferedReader bf3 = new BufferedReader(new InputStreamReader(openFileInput("ip.txt"),"UTF-8"));
            String s = bf.readLine();
            String s3 = bf3.readLine();
            if(s!=null&&s3!=null){
                bf.close();
                bf3.close();
                editname.setText(s);
                editIp.setText(s3);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(typefacen);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setTypeface(typefacen);


    }



    public void myIntent(View view){
        spassword = editpassword.getText().toString();
         sname = editname.getText().toString();
        sUrl = editIp.getText().toString();
       boolean bool = isOnline();
        if(bool){

            if((spassword!=""&&spassword!=null&&spassword.length()!=0)&&(sname!=""&&sname!=""&&sname.length()!=0)
                &&
               (sUrl!=null&&sUrl!=""&&sUrl.length()>=14)){

                MyAsync myAsync = new MyAsync();
                myAsync.execute();

            }else{
                Toast.makeText(getApplicationContext(),"введенные данные не корректны", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Нет связи с интернетом", Toast.LENGTH_SHORT).show();
        }

    };



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    class MyAsync extends AsyncTask<Void,Void,Void> {
        String str = null;
        JSONObject json=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Main2Activity.this);
            progressDialog.setTitle("Проверка данных...");
            progressDialog.setMessage("Ждите...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(final Void... voids) {

            OkHttpClient client = SingletonOkHttp.getClient();
            try {
                json = new JSONObject("{'name':"+sname+",'password':"+spassword+"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json.toString());
            Request request = new Request.Builder()
                    .url(sUrl  + "/getuserdata/signin")
                    .post(body)
                    .build();
            Log.e("json========>",json.toString());
            Log.e("request========>", request.toString());
            try {
                Response response = client.newCall(request).execute();
                str = response.body().string();
                if(str!=null){
                    Log.e("str========>",str.toString());
                }
            } catch (IOException e) {
            }


            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            if(str!=null){
                try {
                json = new JSONObject(str.toString());
            } catch (JSONException e) {
            }
            try {
                str = json.getString("result");
            } catch (JSONException e) {
            }
            progressDialog.dismiss();
            if(str.equals("200")){
                try {
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(openFileOutput("name.txt",MODE_PRIVATE),"UTF-8"));
                    BufferedWriter bf3 = new BufferedWriter(new OutputStreamWriter(openFileOutput("ip.txt",MODE_PRIVATE),"UTF-8"));
                    bf.write(editname.getText().toString());
                    bf3.write(editIp.getText().toString());
                    bf.close();
                    bf3.close();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }


            }else{
                Toast.makeText(Main2Activity.this,"Введенные данные не найдены в базе", Toast.LENGTH_SHORT).show();

            }}else{
                progressDialog.dismiss();
                Toast.makeText(Main2Activity.this,"Сервер не отвечает либо проблема со связью",Toast.LENGTH_SHORT).show();

            }
            super.onPostExecute(aVoid);
            return;
        }
    }


}
