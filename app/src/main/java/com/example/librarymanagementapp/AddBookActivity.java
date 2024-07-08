package com.example.librarymanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtGenre, edtSummary;
    private Button btnSave, btnSelectImage;
    private ImageView imgBook;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final int PICK_IMAGE = 100;
    private static final int CAPTURE_IMAGE = 101;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        edtTitle = findViewById(R.id.edt_title);
        edtAuthor = findViewById(R.id.edt_author);
        edtGenre = findViewById(R.id.edt_genre);
        edtSummary = findViewById(R.id.edt_summary);
        btnSave = findViewById(R.id.btn_save);
        btnSelectImage = findViewById(R.id.btn_select_image);
        imgBook = findViewById(R.id.img_book);

        sharedPreferences = getSharedPreferences("LibraryApp", MODE_PRIVATE);
        gson = new Gson();

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pilihan untuk mengambil gambar dari galeri atau kamera
                CharSequence[] options = {"Pilih dari Galeri", "Ambil dengan Kamera"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddBookActivity.this);
                builder.setTitle("Pilih Gambar");
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE);
                    } else if (which == 1) {
                        dispatchTakePictureIntent();
                    }
                });
                builder.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString();
                String author = edtAuthor.getText().toString();
                String genre = edtGenre.getText().toString();
                String summary = edtSummary.getText().toString();

                if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || summary.isEmpty()) {
                    Toast.makeText(AddBookActivity.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                Book newBook = new Book();
                newBook.setId(UUID.randomUUID().toString()); // Set ID unik
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setGenre(genre);
                newBook.setSummary(summary);

                // Simpan gambar sebagai string base64
                Bitmap bitmap = ((BitmapDrawable) imgBook.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                String imgBase64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                newBook.setImg(imgBase64);

                String booksJson = sharedPreferences.getString("books", "");
                List<Book> books = new ArrayList<>();
                if (!booksJson.isEmpty()) {
                    Book[] savedBooks = gson.fromJson(booksJson, Book[].class);
                    for (Book book : savedBooks) {
                        books.add(book);
                    }
                }
                books.add(newBook);
                sharedPreferences.edit().putString("books", gson.toJson(books)).apply();

                Toast.makeText(AddBookActivity.this, "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddBookActivity.this, BookListActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                imgBook.setImageURI(selectedImage);
            }
        } else if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE) {
            File file = new File(currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            imgBook.setImageURI(uri);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.librarymanagementapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
