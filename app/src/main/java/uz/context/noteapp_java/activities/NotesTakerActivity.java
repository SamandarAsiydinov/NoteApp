package uz.context.noteapp_java.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import uz.context.noteapp_java.R;
import uz.context.noteapp_java.model.Notes;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editTitle, editNote;
    ImageView imageSave, imageBack;
    Notes notes;
    TextView titleText;

    boolean isOnlNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        editNote = findViewById(R.id.editText_notes);
        editTitle = findViewById(R.id.editText_title);
        imageSave = findViewById(R.id.imageView_save);
        imageBack = findViewById(R.id.back_image);
        titleText = findViewById(R.id.title_text);
        imageBack.setOnClickListener(view -> {
            finish();
        });
        notes = new Notes();

        try {
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            editTitle.setText(notes.getTitle());
            editNote.setText(notes.getNote());
            titleText.setText(notes.getTitle());
            isOnlNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageSave.setOnClickListener(view -> {
            String title = editTitle.getText().toString().trim();
            String description = editNote.getText().toString().trim();

            if (description.isEmpty()) {
                Snackbar snackbar = Snackbar.make(view, "Please enter some notes!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();

            if (!isOnlNote) {
                notes = new Notes();
            }
            notes.setTitle(title);
            notes.setNote(description);
            notes.setDate(simpleDateFormat.format(date));

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }
}