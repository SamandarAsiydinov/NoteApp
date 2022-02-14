package uz.context.noteapp_java.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uz.context.noteapp_java.model.Notes;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Notes> getAll();

    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID = :id")
    void update(int id, String title, String notes);

    @Delete
    void delete(Notes notes);

    @Query("DELETE FROM notes")
    void deleteAllData();

    @Query("UPDATE notes SET pinned = :pin WHERE ID = :id")
    void pin(int id, boolean pin);
}
