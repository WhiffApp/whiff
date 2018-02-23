package com.app.whiff.whiff;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImportFileFragment extends Fragment {


    public ImportFileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View imView = inflater.inflate(R.layout.fragment_import_file, container, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
        }

        Button b = imView.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(getActivity())
                        .withRequestCode(1000)
                        .withFilter(Pattern.compile(".*\\.apk$"))
                        .withFilterDirectories(true)
                        .withHiddenFiles(true)
                        .start();
            }
        });


        return imView;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        }

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
          switch(requestCode){
              case 1001: if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                             Toast.makeText(getActivity(),"Permission granted!", Toast.LENGTH_SHORT).show();
                         else
                             Toast.makeText(getActivity(), "Permission not granted!", Toast.LENGTH_SHORT).show();
              }
        }
    }

