package com.example.notes_app;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddNoteActivity extends AppCompatActivity{
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    boolean isEditable = false;
    String fileName = "";
    String tempNote = "";
    EditText editNote;
    EditText editTtl;
    Button save, delete;
    FloatingActionButton back;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        editNote = findViewById(R.id.noteEdit);
        editTtl = findViewById(R.id.titleEdit);
        save = findViewById(R.id.savebutton);
        delete = findViewById(R.id.deletebutton);
        back = findViewById(R.id.backbutton);
        toolbar = findViewById(R.id.toolbar);

        save.setOnClickListener(this::onClick);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("filename");
            editTtl.setText(fileName);
        }
        eventID = 1;
        readFile();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddNoteActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String path = getExternalFilesDir(null) + "/proyek01.kominfo";
                                        File file = new File(path, editTtl.getText().toString());
                                        file.delete();
                                        startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventID = 2;
                new AlertDialog.Builder(AddNoteActivity.this)
                        .setTitle("Save Note")
                        .setMessage("Are you sure you want to save?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        update();
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savebutton:
                eventID = 2;
                if (!tempNote.equals(editNote.getText().toString())) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkStorage()) {
                            ConfirmStgDialog();
                        }
                    } else {
                        ConfirmStgDialog();
                    }
                }
                break;
        }
    }

    public boolean checkStorage() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (eventID == 1) {
                        readFile();
                    } else {
                        ConfirmStgDialog();
                    }
                }
                break;
        }
    }

    void readFile() {
        String path = getExternalFilesDir(null) + "/proyek01.kominfo";
        File file = new File(path, editTtl.getText().toString());
        if (file.exists()) {
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line = br.readLine();

                while (line != null) {
                    text.append(line);
                    text.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
            tempNote = text.toString();
            editNote.setText(text.toString());
        } else {
            path = getExternalFilesDir(null) + "/proyek01.kominfo";
            File parent = new File(path);
            parent.mkdir();
            File f = new File(path, editTtl.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(editNote.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void update() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        String path = getExternalFilesDir(null) + "/proyek01.kominfo";
        File parent = new File(path);
        if (parent.exists()) {
            File file = new File(path, editTtl.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(editNote.getText().toString());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            parent.mkdir();
            File file = new File(path, editTtl.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(editNote.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
        onBackPressed();
    }

    void ConfirmStgDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Save Note")
                .setMessage("Are you sure you want to save?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                update();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    public void onBackPressed() {
        if (!tempNote.equals(editNote.getText().toString())) {
            ConfirmStgDialog();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
