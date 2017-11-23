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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    ProgressBar progressBar;
    String urlconexion="http://ep00.epimg.net/rss/elpais/portada.xml";
    TextView noticias;
    Conexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=(Button) findViewById(R.id.btnBloquea);
        btn2=(Button) findViewById(R.id.btnHilo);
        btn3=(Button) findViewById(R.id.btnAsinkTask);
        btn4=(Button) findViewById(R.id.btnNotificacion);
        btn5=(Button) findViewById(R.id.btnHttp);
        noticias = (TextView) findViewById(R.id.txtNoticias);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);

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
            case R.id.btnHttp:
                noticias.setText("");
                conexion=new Conexion();
                conexion.execute();
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

    public class Conexion extends AsyncTask<Void,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            String salida="";
            int i=0,j=0;
            try {
                URL url=new URL(urlconexion);//url de donde obtenemos la info
                //Abrimos la conexión
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent","Mozilla/5.0" +
                        " (Linux;Android 1.5; es-ES) Ejemplo Http");

                int respuesta = connection.getResponseCode();
                if(respuesta==HttpURLConnection.HTTP_OK){
                    BufferedReader lector=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String linea = lector.readLine();
                    while(linea!=null){
                        if(linea.indexOf("<title><![CDATA[")>=0){
                            i=linea.indexOf("<title>")+16;
                            j=linea.indexOf("</title>")-3;
                            salida +=linea.substring(i,j);
                            salida +="\n-----------\n";
                        }
                        linea=lector.readLine();
                    }
                    lector.close();
                }else{
                    salida="Página no encontrada";
                }
                connection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return salida;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            noticias.setText(s);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


    }



}

