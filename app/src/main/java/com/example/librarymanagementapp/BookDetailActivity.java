package com.example.librarymanagementapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private TextView title, author, genre, summary, reviews;
    private ImageView imageView;
    private Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        title = findViewById(R.id.bookTitle);
        author = findViewById(R.id.bookAuthor);
        genre = findViewById(R.id.bookGenre);
        summary = findViewById(R.id.bookSummary);
        reviews = findViewById(R.id.bookReviews);
        imageView = findViewById(R.id.bookImage);

        book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            genre.setText(book.getGenre());
            summary.setText(book.getSummary());

            if (book.getImg() != null && !book.getImg().isEmpty()) {
                if (book.getImg().startsWith("http")) {
                    // Load image from URL using Picasso
                    Picasso.get().load(book.getImg()).into(imageView);
                } else {
                    // Decode Base64 string and set it to ImageView
                    byte[] decodedString = Base64.decode(book.getImg(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
            }

            List<Review> reviewList = book.getReviews();
            if (reviewList != null && !reviewList.isEmpty()) {
                StringBuilder reviewsText = new StringBuilder();
                for (Review review : reviewList) {
                    reviewsText.append("Reviewer: ").append(review.getReviewer()).append("\n");
                    reviewsText.append("Rating: ").append(review.getRating()).append("\n");
                    reviewsText.append("Comment: ").append(review.getComment()).append("\n\n");
                }
                reviews.setText(reviewsText.toString());
            } else {
                reviews.setText("Belum Ada Review");
            }
        }
    }
}
