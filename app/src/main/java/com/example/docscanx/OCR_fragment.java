package com.example.docscanx;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OCR_fragment extends Fragment {
    TextView responseText;
    ListView txt_ocr;
    public static ArrayList<File> filelist1 = new ArrayList<File>();
    private ArrayList<File> UserSelection1 = new ArrayList<>();
    OCR_Adapter ocr_adapter;
    File dir1;
    String name,fname;
    private androidx.appcompat.view.ActionMode mActionMode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ocr,container,false);
        txt_ocr= (ListView)view.findViewById(R.id.txt);
        dir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Notes");
        getfile(dir1);

        txt_ocr.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        txt_ocr.setMultiChoiceModeListener(modeListener);
        ocr_adapter = new OCR_Adapter(getActivity().getApplicationContext(),filelist1);
        txt_ocr.setAdapter(ocr_adapter);

        txt_ocr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(),OCR_Text.class);
                intent.putExtra("position",i);
                startActivity(intent);

                Log.e("Position", i + "");
            }
        });

        return view;
    }

    public ArrayList<File> getfile(File dir1) {
        File listFile[] = dir1.listFiles();

        if(listFile != null && listFile.length > 0)
        {

            for(int i=0;i<listFile.length;i++) {
                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);
                } else
                {
                    boolean booleanpdf = false;
                    //only .pdf files
                    if (listFile[i].getName().endsWith(".txt")) {
                        for (int j = 0; j < filelist1.size(); j++) {
                            if (filelist1.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            }
                            else {

                            }
                        }

                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            filelist1.add(listFile[i]);
                        }

                    }
                }
            }
        }

        return filelist1;
    }
    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        private int nr = 0;
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if(UserSelection1.contains(filelist1.get(position)))
            {
                UserSelection1.remove(filelist1.get(position));
                nr--;
            }
            else
            {
                UserSelection1.add(filelist1.get(position));
                nr++;
            }
            mode.setTitle(nr +" item selected");
            name = filelist1.get(position).toString().replaceAll("file:///","");
            fname= filelist1.get(position).getName();
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
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete this item")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ocr_adapter.removeItems(UserSelection1);
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
                        Toast.makeText(getActivity(),"File doesnt exist", Toast.LENGTH_SHORT).show();

                    }

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
                            Uri path= FileProvider.getUriForFile(getActivity(),"com.example.docscanx.fileprovider",f1);
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
                        Toast.makeText(getActivity(),"No activity available",Toast.LENGTH_SHORT).show();
                    }


                    mode.finish();
                    return true;
                default:
                    return false;

            }
        }
        @Override public void onDestroyActionMode(ActionMode mode) {

        }


    };

}
