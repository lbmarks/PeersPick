package com.example.kienenwayrynen.myapplication.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.kienenwayrynen.myapplication.R;
import com.example.kienenwayrynen.myapplication.model.Graph;
import com.example.kienenwayrynen.myapplication.view.PathImageView;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class RouteFinder extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_finder);
        Spinner routeStartSpinner = (Spinner) findViewById(R.id.route_start_spinner);
        Spinner routeEndSpinner = (Spinner) findViewById(R.id.route_end_spinner);
        String[] testArray = {"Annex",
                "Clark College Building (VCCW)",
                "Classroom Building (VCLS)",
                "Dengerink Administration Building (VDEN)\n" + "Cafeteria",
                "Engineering & Computer\n" + "Science Building (VECS)",
                "Facilities Operations Building (VFO)",
                "Firstenburg Student Commons (VFSC)",
                "Library Building (VLIB)",
                "McClaskey Building (VMCB)\n" + "Child Development Program",
                "Multimedia Classroom Building (VMMC)",
                "Physical Plant Building (VPP)\n" + "Parking Services",
                "Science & Engineering Building (VSCI)",
                "Student Services Center (VSSC)\n" + "Admissions, Bookstore,\n" +
                "Financial Aid, Visitor’s Center",
                "Undergraduate Building (VUB)"};
        ArrayAdapter<CharSequence> routeStartAdapter =
                new ArrayAdapter<CharSequence>(this,android.R.layout.simple_list_item_1, testArray);
        ArrayAdapter<CharSequence> routeEndAdapter =
                new ArrayAdapter<CharSequence>(this,android.R.layout.simple_list_item_1, testArray);
        routeStartSpinner.setAdapter(routeStartAdapter);
        routeEndSpinner.setAdapter(routeEndAdapter);

        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("sample_graph",
                        "raw", getPackageName()));

        final Graph sampleGraph = new Graph(ins);
        final PathImageView img = (PathImageView) findViewById(R.id.path_img);



        img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Bitmap topresent = convertToMutable(BitmapFactory.decodeResource(getResources(), R.drawable.map));
                float scalex = topresent.getWidth()/(float)600 ;
                float scaleY = topresent.getHeight()/(float)364;
                System.out.println("scalex:" + scalex + " scaley:" + scaleY + " bitw:" + topresent.getWidth() + " bith:" + topresent.getHeight());
                List<Point> path = sampleGraph.findPath(12, 22, 0,0, scalex, scaleY);
                img.setMap(topresent, path);
            }
        });

    }

    private static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            System.gc();

            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            imgIn.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();

            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgIn;
    }


}
