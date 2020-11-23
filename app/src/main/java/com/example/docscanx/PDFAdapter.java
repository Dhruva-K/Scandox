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

public class PDFAdapter extends ArrayAdapter<File> {

    Context context;
    ArrayList<File> al_pdf;
    ViewHolder viewHolder;

    public PDFAdapter(Context context,ArrayList<File> al_pdf){
        super(context,R.layout.adapter_pdf,al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(al_pdf.size()>0)
        {
            return al_pdf.size();
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_filename = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.tv_filename.setText(al_pdf.get(position).getName());
        return view;
    }

    public class ViewHolder
    {
        TextView tv_filename;
    }


}
