package com.example.uisadmin;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdfActicvty extends AppCompatActivity {

    private CardView addPdf;
    private TextView pdfTextview;
    private EditText pdfTitle;
    private String title;
    private Button uploadPdfBtn;
    private String pdfName;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private final int REQ = 1;
    private Uri pdfData;
    private ProgressDialog pd;

    String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf_acticvty);


        pd = new ProgressDialog(this);

        addPdf = findViewById(R.id.addPdf);
        pdfTitle = findViewById(R.id.pdfTitle);
        uploadPdfBtn = findViewById(R.id.uploadPdfBtn);
        pdfTextview = findViewById(R.id.pdfTextview);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdfTitle.getText().toString();
                if (title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }else if (pdfData == null){
                    Toast.makeText(UploadPdfActicvty.this, "Please Select The PDF To Upload", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void uploadPdf() {
        pd.setTitle("Please Wait...");
        pd.setMessage("Uploading PDF");
        pd.show();
        StorageReference reference = storageReference.child("pdf/"+ pdfName+ "-"+System.currentTimeMillis()+ ".pdf");
        reference.putFile(pdfData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        uploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UploadPdfActicvty.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadData(String valueOf) {
        String uniqueKey = databaseReference.child("pdf").push().getKey();

        HashMap data = new HashMap();

        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdfActicvty.this, "PDF Uploaded Successfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdfActicvty.this, "Failed TO Upload PDF", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQ);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK) {
            pdfData = data.getData();

            if (pdfData.toString().startsWith("content://")) {
                Cursor cursor = null;
                cursor = UploadPdfActicvty.this.getContentResolver().query(pdfData, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (displayNameIndex != -1) {
                            pdfName = cursor.getString(displayNameIndex);
                        } else {
                            // Handle the case where DISPLAY_NAME column doesn't exist
                            pdfName = "Unknown.pdf";
                        }
                    }
                    cursor.close(); // Don't forget to close the cursor when done with it
                } else {
                    // Handle the case where the cursor is null
                    pdfName = "Unknown.pdf";
                }
            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.toString()).getName();
            }
            pdfTextview.setText(pdfName);
        }
    }

}