package uz.context.noteapp_java.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import uz.context.noteapp_java.R;
import uz.context.noteapp_java.adapter.NoteListAdapter;
import uz.context.noteapp_java.database.RoomDB;
import uz.context.noteapp_java.itemclick.NotesClickListener;
import uz.context.noteapp_java.model.Notes;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    EditText searchView;
    FloatingActionButton fabAdd;
    SwipeRefreshLayout swipe;
    Notes selectedNote;
    View view;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linear_gone);
        recyclerView = findViewById(R.id.recycler_home);
        fabAdd = findViewById(R.id.fab_add);
        swipe = findViewById(R.id.swipe_refresh);
        searchView = findViewById(R.id.search_view);
        swipe.setOnRefreshListener(() -> {
            noteListAdapter.notifyDataSetChanged();
            swipe.setRefreshing(false);
        });
        view = swipe;

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecyclerView(notes);

        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            startActivityForResult(intent, 101);
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        isCheck();
    }

    private void filter(CharSequence newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toString().toLowerCase())
                    || singleNote.getNote().toLowerCase().contains(newText.toString().toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        noteListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
                isCheck();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Notes newNote = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(newNote.getID(), newNote.getTitle(), newNote.getNote());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
                isCheck();
            }
        }
    }

    private void updateRecyclerView(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(this, notes, notesClickListener);
        recyclerView.setAdapter(noteListAdapter);

    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
            isCheck();
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
            isCheck();
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.pin_p:
                if (selectedNote.isPinned()) {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    snackBar("Unpinned");
                } else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    snackBar("Pinned");
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete: {
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                noteListAdapter.notifyDataSetChanged();
                snackBar("Note Deleted!");
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.list_1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else if (item.getItemId() == R.id.grid_2) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        } else if (item.getItemId() == R.id.delete_all) {
            if (!notes.isEmpty()) {
                database.mainDAO().deleteAllData();
                notes.remove(selectedNote);
                snackBar("All Notes Deleted!");
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
       }
        return super.onOptionsItemSelected(item);
    }
    void snackBar(String str) {
        Snackbar snackbar = Snackbar.make(view,str, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
    void isCheck() {
        if (notes.isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }
}

