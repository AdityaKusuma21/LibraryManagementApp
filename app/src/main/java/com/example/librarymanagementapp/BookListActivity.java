package com.example.librarymanagementapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;

public class BookListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private static final String URL = "https://book-information-library.p.rapidapi.com/api/books/getall-books";
    private static final String API_KEY = "72dd2e1084msh8c95d68d95f2768p1b7732jsn96ec38b54a34";
    private static final String TAG = "BookListActivity";
    private Button btnAddBook;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList, this);  // Pastikan context diteruskan ke adapter
        recyclerView.setAdapter(bookAdapter);
        btnAddBook = findViewById(R.id.btn_add_book);
        sharedPreferences = getSharedPreferences("LibraryApp", MODE_PRIVATE);
        gson = new Gson();

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookListActivity.this, AddBookActivity.class));
            }
        });

        fetchBooks();
    }

    private void fetchBooks() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "book-information-library.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error fetching books: " + e.getMessage());
                runOnUiThread(() -> {
                    loadCachedBooks(); // Load cached books if API call fails
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray jsonArray = jsonObject.getJSONArray("books");

                        List<Book> apiBooks = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookObject = jsonArray.getJSONObject(i);
                            Book book = new Book();
                            book.setId(bookObject.getString("_id"));
                            book.setTitle(bookObject.getString("title"));
                            book.setAuthor(bookObject.getString("author"));
                            book.setGenre(bookObject.getString("genre"));
                            book.setSummary(bookObject.getString("summary"));
                            book.setImg(bookObject.getString("img_url"));  // Note the change in key name

                            List<Review> reviews = new ArrayList<>();
                            JSONArray reviewArray = bookObject.getJSONArray("reviews");
                            for (int j = 0; j < reviewArray.length(); j++) {
                                JSONObject reviewObject = reviewArray.getJSONObject(j);
                                Review review = new Review();
                                review.setId(reviewObject.getString("_id"));
                                review.setReviewer(reviewObject.getString("reviewer"));
                                review.setRating(reviewObject.getInt("rating"));
                                review.setComment(reviewObject.getString("comment"));
                                reviews.add(review);
                            }
                            book.setReviews(reviews);

                            apiBooks.add(book);
                        }

                        runOnUiThread(() -> {
                            bookList.clear();
                            bookList.addAll(apiBooks);
                            loadCachedBooks();
                            bookAdapter.notifyDataSetChanged();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        runOnUiThread(() -> {
                            loadCachedBooks(); // Load cached books if parsing fails
                        });
                    }
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    runOnUiThread(() -> {
                        loadCachedBooks(); // Load cached books if response is not successful
                    });
                }
            }
        });
    }

    private void loadCachedBooks() {
        String booksJson = sharedPreferences.getString("books", "");
        if (!booksJson.isEmpty()) {
            Book[] cachedBooks = gson.fromJson(booksJson, Book[].class);
            for (Book book : cachedBooks) {
                boolean exists = false;
                for (Book b : bookList) {
                    if (b.getId() != null && b.getId().equals(book.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    bookList.add(book);
                }
            }
            Collections.sort(bookList, (b1, b2) -> {
                if (b1.getId() == null || b2.getId() == null) {
                    return 0;
                }
                return b2.getId().compareTo(b1.getId());
            });
            bookAdapter.notifyDataSetChanged();
        }
    }
}

