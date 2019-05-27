package com.cornelius.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebrityImages ;
    ArrayList<String> celebrityNames ;

    ImageView imageView ;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    int locationofanswer;
    int imageanswerselection;

    ArrayList<String> answers = new ArrayList<>();

    public void chooseCelebrity(){

        Random rand = new Random();

        ImageDownload task = new ImageDownload();
        Bitmap answerImage;

        answers.clear();

        imageanswerselection = rand.nextInt(celebrityNames.size())+1;

        try{

            answerImage = task.execute( (String) celebrityImages.get(imageanswerselection)).get();

            imageView.setImageBitmap(answerImage);

        }
        catch (Exception e){

            e.printStackTrace();
        }

        String wronganswer;

        locationofanswer = rand.nextInt(4);

        for(int i = 0;i <4 ;i++){

            if (i == locationofanswer){

                answers.add(celebrityNames.get(imageanswerselection));

            } else {

                wronganswer = celebrityNames.get(rand.nextInt(celebrityNames.size()));

                while(wronganswer == celebrityImages.get(imageanswerselection)){

                    wronganswer = celebrityNames.get(rand.nextInt(celebrityNames.size()));

                }

                answers.add(wronganswer);

            }

        }

        button1.setText(answers.get(0));
        button2.setText(answers.get(1));
        button3.setText(answers.get(2));
        button4.setText(answers.get(3));


    }

    public void chooseAnswer(View view ){

        if (view.getTag().toString().equals(Integer.toString(locationofanswer))){


            Toast.makeText(this, "You got it correct", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Wrong , it was " + celebrityNames.get(imageanswerselection) , Toast.LENGTH_SHORT).show();

        }

        chooseCelebrity();

    }

    public class ImageDownload extends AsyncTask<String , Void , Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection ) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }
            catch (Exception e){

                e.printStackTrace();

            }

            return null;
        }

    }

    public class CelebrityDownload extends AsyncTask<String , Void , String>{


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url ;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream in = httpURLConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        CelebrityDownload task = new CelebrityDownload();

        celebrityImages = new ArrayList<String>();
        celebrityNames = new ArrayList<String>();

        String result = null;

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

        } catch (ExecutionException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();
        }


        Log.i("result",result);

        Pattern p = Pattern.compile("src=\"(.*?)\"");
        Matcher m = p.matcher(result);

        while (m.find()){

            celebrityImages.add(m.group(1));

        }

        celebrityImages.remove(0);
        celebrityImages.remove(0);
        celebrityImages.remove(0);
        celebrityImages.remove(0);


        Log.i("array",celebrityImages.toString());

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(result);

        while (m.find()){

           celebrityNames.add(m.group(1));

        }

        Log.i("name array",celebrityNames.toString());

        chooseCelebrity();

    }
}
