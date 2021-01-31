package com.example.docscanx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class OCR_Adapter extends ArrayAdapter<File> {

    Context context;
    ArrayList<File> al_txt;
    OCR_Adapter.ViewHolder viewHolder;
    OCR_Adapter.ViewHolder renameHolder;
    TextView filename;

    String file_path;

    public OCR_Adapter(Context context,ArrayList<File> al_txt){
        super(context,R.layout.adapter_ocr,al_txt);
        this.context = context;
        this.al_txt = al_txt;
    }

    public String getFile_path() {

        return file_path;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(al_txt.size()>0)
        {
            return al_txt.size();
        }
        else
        {
            return 1;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {
        if(view== null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_ocr,parent,false);
            viewHolder = new OCR_Adapter.ViewHolder();
            viewHolder.tv_filename = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (OCR_Adapter.ViewHolder)view.getTag();
        }
        viewHolder.tv_filename.setText(al_txt.get(position).getName());
        return view;
    }
    public void removeItems(ArrayList<File> filelist){
        for(File item: filelist){
            al_txt.remove(item);
        }
        notifyDataSetChanged();
    }



    public class ViewHolder
    {
        TextView tv_filename;
    }


}
