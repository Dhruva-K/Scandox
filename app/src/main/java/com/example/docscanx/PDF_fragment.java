package com.example.docscanx;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.example.docscanx.Retrofit.IUploadApi;
import com.example.docscanx.Retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PDF_fragment extends Fragment {
    SendMessage SM;
    ListView lv_pdf;
    public static ArrayList<File> filelist = new ArrayList<File>();
    private ArrayList<File> UserSelection = new ArrayList<>();
    PDFAdapter obj_adapter;
    File dir;
    String name,fname;
    private androidx.appcompat.view.ActionMode mActionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pdf,container,false);


        lv_pdf = (ListView) view.findViewById(R.id.lv_pdf);

        //path to directory where pdfs are stored
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CamScannerCloudStorage");

        getfile(dir);

        lv_pdf.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_pdf.setMultiChoiceModeListener(modeListener);
        obj_adapter = new PDFAdapter(getActivity().getApplicationContext(),filelist);
        lv_pdf.setAdapter(obj_adapter);


        //opening the pdf:
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(),PdfActivity.class);
                intent.putExtra("position",i);
                startActivity(intent);

                Log.e("Position", i + "");
            }
        });
        return view;
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
                    if (listFile[i].getName().endsWith(".pdf")) {
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
                    new AlertDialog.Builder(getActivity())
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


                case R.id.ocr:
                    String postUrl="http://192.168.29.206:5000/";
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmap = BitmapFactory.decodeFile(name.replaceAll(".pdf",".jpg"), options);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    RequestBody postBodyImage = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", fname.replaceAll(".pdf",".jpg"), RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                            .build();


                    postRequest(postUrl, postBodyImage);
                    Intent i = new Intent(getActivity(), HomeActivity1.class);
                    startActivity(i);
                    mode.finish();
                    return true;
                default:
                    return false;

            }
        }
        @Override public void onDestroyActionMode(ActionMode mode) {

        }


    };
    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());


            }


            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                String ocr_text = response.body().string();
                try {
                    File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File gpxfile = new File(root, fname.replaceAll(".pdf",".txt"));
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(ocr_text);
                    writer.flush();
                    writer.close();
                    //Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                SM.sendData(ocr_text);
            }
        });
    }
    interface SendMessage{
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

}