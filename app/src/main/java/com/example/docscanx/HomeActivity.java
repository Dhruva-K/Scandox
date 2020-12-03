package com.example.docscanx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> filelist = new ArrayList<File>();
    private ArrayList<File> UserSelection = new ArrayList<>();
    PDFAdapter obj_adapter;
    File dir;
    String name,fname;
    private static final int PERMISSION_CODE=1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri imageUri;
    private androidx.appcompat.view.ActionMode mActionMode;
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {

        lv_pdf = (ListView) findViewById(R.id.lv_pdf);

        //path to directory where pdfs are stored
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CamScannerCloudStorage");

        getfile(dir);

        lv_pdf.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_pdf.setMultiChoiceModeListener(modeListener);
        obj_adapter = new PDFAdapter(getApplicationContext(),filelist);
        lv_pdf.setAdapter(obj_adapter);


        //opening the pdf:
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),PdfActivity.class);
                intent.putExtra("position",i);
                startActivity(intent);

                Log.e("Position", i + "");
            }
        });
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();

        if(listFile != null && listFile.length > 0)
        {

            for(int i=0;i<listFile.length;i++) {
                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);
                } else
                    {
                    boolean booleanpdf = false;
                    //only .pdf files
                    if (listFile[i].getName().endsWith(".jpg.pdf")) {
                        for (int j = 0; j < filelist.size(); j++) {
                            if (filelist.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            }
                            else {

                            }
                            }

                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            filelist.add(listFile[i]);
                        }

                    }
                    }
            }
        }

        return filelist;
    }

    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        private int nr = 0;
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if(UserSelection.contains(filelist.get(position)))
            {
                UserSelection.remove(filelist.get(position));
                nr--;
            }
            else
            {
                UserSelection.add(filelist.get(position));
                nr++;
            }
            mode.setTitle(nr +" item selected");
            name = filelist.get(position).toString().replaceAll("file:///","");
            fname= filelist.get(position).getName();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            nr=0;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_main,menu);
            return true;

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch ((item.getItemId())) {
                case R.id.delete:
                    nr=0;
                    new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete this item")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    obj_adapter.removeItems(UserSelection);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    mode.finish();
                    return true;
                case R.id.share:
                       // String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CamScannerCloudStorage"+name;
                        File f1 = new File(name);
                            if(!f1.exists()){
                                Toast.makeText(HomeActivity.this,"File doesnt exist", Toast.LENGTH_SHORT).show();

                            }
                            /*Intent intentShare=new Intent(Intent.ACTION_SEND);
                            intentShare.setType("application/pdf");
                            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(name));
                            startActivity(Intent.createChooser(intentShare,"Share wih"));*/

                          /*  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri path= FileProvider.getUriForFile(HomeActivity.this,"com.example.docscanx.fileprovider",f1);
                                intent.putExtra(Intent.EXTRA_STREAM,path);
                            }
                            else{
                                intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f1));
                            }
                            intent.setType("application/pdf");
                            startActivity(intent);*/
                    MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
                    String ext = mimeTypeMap.getExtensionFromMimeType(fname);
                    String type = mimeTypeMap.getExtensionFromMimeType(ext);

                    if(type==null){
                        type="*/*";
                    }
                    try {
                        Intent intentShare =new Intent(Intent.ACTION_SEND);
                        intentShare.putExtra(Intent.EXTRA_TEXT, "Sharing file");

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            intentShare.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri path= FileProvider.getUriForFile(HomeActivity.this,"com.example.docscanx.fileprovider",f1);
                            intentShare.putExtra(Intent.EXTRA_STREAM,path);
                        }
                        else{
                            intentShare.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f1));
                        }
                        intentShare.setType("*/*");
                        startActivity(intentShare);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this,"No activity available",Toast.LENGTH_SHORT).show();
                    }


                    mode.finish();
                    return true;


                case R.id.rename:
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            HomeActivity.this);
                    alert.setTitle("Rename");

                    final EditText input = new EditText(HomeActivity.this);
                    alert.setView(input);


                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String srt1 = input.getEditableText().toString();
                            //update your listview here
                            input.setText(srt1);

                        }
                    });

                    alert.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();

                    mode.finish();
                    return true;
                default:
                    return false;

            }
        }
        @Override public void onDestroyActionMode(ActionMode mode) {

            }


    };







    public void check(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,PERMISSION_CODE);
            }
            else
            {
                OpenCamera();
            }
        }
        else
        {
            OpenCamera();
        }
    }
    private void OpenCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"from the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Intent i=new Intent(HomeActivity.this,ScanActivity.class);
            i.setData(imageUri);
            startActivity(i);
        }
    }
}