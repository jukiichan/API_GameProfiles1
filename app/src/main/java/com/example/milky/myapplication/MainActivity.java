package com.example.milky.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {


    public static  Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream

    public static  InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

    //AsyncTask, który miał wykonywać operację pobierania obrazu
    //z linku w tle, aby uniknąć wykonywania operacji w głównym wątku
    public class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            System.out.println("doInBackground");
            Bitmap bitmap = null;
            bitmap = downloadImage(params[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            System.out.println("bitmap" + bitmap);
        }
    }


    //inny sposób pobierania obrazu z linku
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    //obiekt gracza
    public class Gracz{
        public String nick;
        public String level;
        public String record;
        public String avatar;

        public Gracz() {

        }

        public Gracz(String nick, String level, String record, String avatar){
            this.nick = nick;
            this.level = level;
            this.record = record;
            this.avatar = avatar;
        }
    }

    //zmienne dla obiektu gracza
    private String nick3;
    private String level3;
    private String record3;
    private String avatar;

    //metoda tworzaca za kazdym otworzeniem aplikacji
    //czterech graczy w bazie danych (dla pewności, że struktura się nie zmieniła)
    private void writePlayers(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        for(Integer i=1;i<5;i++){
            nick3 = "gracz"+i;
            level3 = "1"+i;
            record3 = "22"+i;
            avatar = "https://i.imgur.com/4SaFLvC.png";
            Gracz gracz = new Gracz(nick3, level3, record3, avatar);

            mDatabase.child("players").child(nick3).setValue(gracz);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final EditText player_input = (EditText) findViewById(R.id.player_input);
        final TextView display_nick = (TextView) findViewById(R.id.nick);
        final TextView display_level = (TextView) findViewById(R.id.level_value);
        final TextView display_record = (TextView) findViewById(R.id.record_value);
        final ImageView display_avatar = (ImageView) findViewById(R.id.avatar);
        final ImageView display_sig = (ImageView) findViewById(R.id.sig);

        writePlayers();

        final Button button3 = (Button) findViewById(R.id.button3);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //co sie dzieje po kliknieciu buttonu "GET PROFILE"

                //pobierz nazwe z pola
                final String playername = String.valueOf(player_input.getText());

                //instancja bazy danych
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRefLevel = database.getReference("players/"+playername+"/level");
                myRefLevel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String level_todisplay = (String) dataSnapshot.getValue();
                        display_level.setText(level_todisplay);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

                DatabaseReference myRefRecord = database.getReference("players/"+playername+"/record");
                myRefRecord.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String record_todisplay = (String) dataSnapshot.getValue();
                        display_record.setText(record_todisplay);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

                DatabaseReference myRefAvatar = database.getReference("players/"+playername+"/avatar");
                myRefAvatar.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String avatar_link = (String) dataSnapshot.getValue();

                        //próby pobrania obrazka z linku

//                        InputStream is = null;
//                        try {
//                            is = (InputStream) new URL(avatar_link).getContent();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Drawable d = Drawable.createFromStream(is, "src");
//                        display_avatar.setImageDrawable(d);

                        //new AsyncGettingBitmapFromUrl().execute(avatar_link);

                        //display_avatar.setImageBitmap();

                        //display_avatar.setImageDrawable(LoadImageFromWebOperations(avatar_link));
                        //button3.setText(avatar_link);
                        //display_avatar.setImageDrawable(avatar_todisplay);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

                display_nick.setText(playername);

            }
        });


        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //klikniecie buttonu 'odbierz'

                //odebranie z db
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
