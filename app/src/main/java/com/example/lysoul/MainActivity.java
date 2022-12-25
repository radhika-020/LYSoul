package com.example.lysoul;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    // This method is used for grant permission
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                       // Toast.makeText(MainActivity.this, "Run time permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs=fetchSongs(Environment.getExternalStorageDirectory());
                        String items[] =new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent =new Intent(MainActivity.this, PlaySong.class);
                                String currentSong=listView.getItemAtPosition(i).toString();
                                intent.putExtra("songList",mySongs);
                                intent.putExtra("currentSong",currentSong);
                                intent.putExtra("position",i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    // This method is worked if we denied the permission
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    // This method is used if we denied the permission and open it again it again ask for the permission
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    /*
    private boolean checkPermission(){
        if(SDK_INT>= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else{
            int write= ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
            int read= ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);

            return write== PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED
        }
    }*/
    // Abstract implementation
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] song=file.listFiles();
        // Recursive implementation
        if(song!=null){
            for(File myFile : song){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3")&& !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }

            }
        }
        return arrayList;
    }
}