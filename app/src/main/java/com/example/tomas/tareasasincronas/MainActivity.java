package com.example.tomas.tareasasincronas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=(Button) findViewById(R.id.btnBloquea);
        btn2=(Button) findViewById(R.id.btnHilo);
        btn3=(Button) findViewById(R.id.btnAsinkTask);
        btn4=(Button) findViewById(R.id.btnNotificacion);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBloquea:
                for(int i=0;i<10;i++){
                    UnSegundo();
                }
                Toast.makeText(MainActivity.this,"Tarea larga finalizada",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnHilo:
                Hilos();
                break;
            case R.id.btnAsinkTask:
                EjemploAsinkTask ejemploAsinkTask=new EjemploAsinkTask();
                ejemploAsinkTask.execute();
                break;
            case R.id.btnNotificacion:
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com/index.html"));
                PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);

                //Construimos la notificación
                NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
                builder.setSmallIcon(android.R.drawable.ic_notification_clear_all);
                builder.setContentTitle("Notificación básica");
                builder.setContentText("Aprender más android");
                builder.setSubText("Pulsa para abrir la web");
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_name));

                //Enviamos la notificación
                NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0,builder.build());




                break;
            default:
                break;
        }
    }

    private void UnSegundo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void Hilos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    UnSegundo();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"Tarea larga finalizada",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private class EjemploAsinkTask extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMax(100);
            progressBar.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            for(int i=1;i<=10;i++){
                UnSegundo();
                publishProgress(i*10);
                if(isCancelled()){
                    break;
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0].intValue());

        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(getBaseContext(),"Tarea larga finalizada",Toast.LENGTH_SHORT).show();

            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getBaseContext(),"Tarea larga cancelada",Toast.LENGTH_SHORT).show();

        }

    }

}

