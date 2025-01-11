package com.example.library;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private EditText titleInput, authorInput;
    private ListView bookList;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        titleInput = findViewById(R.id.editTextTitle);
        authorInput = findViewById(R.id.editTextAuthor);
        Button addButton = findViewById(R.id.buttonAdd);
        bookList = findViewById(R.id.listViewBooks);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        loadBooks();
    }

    private void addBook() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        database.execSQL("INSERT INTO books (title, author) VALUES (?, ?)", new Object[]{title, author});
        titleInput.setText("");
        authorInput.setText("");
        loadBooks();
    }

    private void loadBooks() {
        Cursor cursor = database.rawQuery("SELECT _id, title, author FROM books", null);

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this,
                    R.layout.book_item,
                    cursor,
                    new String[]{"title", "author"},
                    new int[]{R.id.textViewTitle, R.id.textViewAuthor},
                    0);
            bookList.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        database.close();
        dbHelper.close();
        super.onDestroy();
    }
}

class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDatabaseHelper(MainActivity context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE books (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }
}