package uz.context.noteapp_java.itemclick;

import androidx.cardview.widget.CardView;
import uz.context.noteapp_java.model.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
