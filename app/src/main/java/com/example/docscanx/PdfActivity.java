package com.example.docscanx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

        PDFView pdfView;
        Integer pageNumber = 0;
        String pdfFileName;
        int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        init();
    }

    private void init() {
        pdfView = (PDFView)findViewById(R.id.pdfview);
        position = getIntent().getIntExtra("position", -1);
        displayFromSdCard();
    }

    private void displayFromSdCard() {
        pdfFileName = HomeActivity.filelist.get(position).getName();
        //file by position and get name to show on top
        Toast.makeText(this,pdfFileName,Toast.LENGTH_SHORT).show();

        pdfView.fromFile(HomeActivity.filelist.get(position))
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
    pageNumber = page;
    setTitle(String.format("%s %s / %s",pdfFileName,page +1 , pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(),"-");
    }

    private void printBookmarksTree(List<PdfDocument.Bookmark> tableOfContents, String s) {
        for(PdfDocument.Bookmark b:tableOfContents)
        {
            if(b.hasChildren())
            {
                printBookmarksTree(b.getChildren(), s+"-");
            }
        }
    }
}