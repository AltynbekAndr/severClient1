package kg.com.severelectro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IpPortActivity extends Activity {
    String sIp = null;
    EditText editIp = null;
    TextView txt63 = null;
    Button b = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_port);
        editIp = (EditText) findViewById(R.id.editText6);
        try {
            BufferedReader bf3 = new BufferedReader(new InputStreamReader(openFileInput("ip.txt"),"UTF-8"));
            String s3 = bf3.readLine();
            if(s3!=null){
                bf3.close();
                editIp.setText(s3);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
    public void writeIpPort(View view){
        sIp = editIp.getText().toString();

        if((sIp!=null&&sIp!=""&&sIp.length()>=14)){
        try {
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(openFileOutput("ip.txt",MODE_PRIVATE),"UTF-8"));
            bf.write(sIp);
            bf.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(IpPortActivity.this, "Ошибка при создании файлов для IP:PORT", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(IpPortActivity.this,"Ошибка при записи данных для файлов с IP:PORT",Toast.LENGTH_SHORT).show();
        }
            AlertDialog.Builder builder = new AlertDialog.Builder(IpPortActivity.this);
            View view1 = getLayoutInflater().inflate(R.layout.custom_alert2, null);
            txt63 = (TextView) view1.findViewById(R.id.textView72);
            b = (Button) view1.findViewById(R.id.button8);
            txt63.setText(sIp);
            builder.setView(view1);
            AlertDialog alert = builder.create();
            alert.show();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
    }else{
            Toast.makeText(IpPortActivity.this,"Введенные данные не корректны",Toast.LENGTH_SHORT).show();
        }
}
}
