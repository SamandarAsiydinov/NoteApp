package uz.context.noteapp_java.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uz.context.noteapp_java.R;
import uz.context.noteapp_java.itemclick.NotesClickListener;
import uz.context.noteapp_java.model.Notes;

public class NoteListAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    List<Notes> list;
    NotesClickListener listener;

    public NoteListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list, parent, false);
        return new NotesViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.textTitle.setText(list.get(position).getTitle());
        holder.textTitle.setSelected(true);
        holder.textNotes.setText(list.get(position).getNote());
        holder.textDate.setText(list.get(position).getDate());
        holder.textDate.setSelected(true);

        if (list.get(position).isPinned()) {
            holder.imagePin.setImageResource(R.drawable.ic_baseline_push_pin_24);
        } else {
            holder.imagePin.setImageResource(0);
        }
        int colorCode = getRandomColor();
        holder.notesContainer.setCardBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));
        holder.notesContainer.setOnClickListener(view -> {
            listener.onClick(list.get(holder.getAdapterPosition()));
        });
        holder.notesContainer.setOnLongClickListener(view -> {
            listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notesContainer);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Notes> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    private int getRandomColor() {
        List<Integer> colorsCode = new ArrayList<>();
        colorsCode.add(R.color.color1);
        colorsCode.add(R.color.color2);
        colorsCode.add(R.color.color3);
        colorsCode.add(R.color.color4);
        colorsCode.add(R.color.color5);
        colorsCode.add(R.color.color6);
        colorsCode.add(R.color.color7);
        colorsCode.add(R.color.color8);
        colorsCode.add(R.color.color9);
        colorsCode.add(R.color.light);

        Random random = new Random();
        int randomColor = random.nextInt(colorsCode.size());
        return colorsCode.get(randomColor);
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView notesContainer;
    TextView textTitle, textNotes, textDate;
    ImageView imagePin;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notesContainer = itemView.findViewById(R.id.notes_container);
        textDate = itemView.findViewById(R.id.textView_date);
        textTitle = itemView.findViewById(R.id.textView_title);
        textNotes = itemView.findViewById(R.id.textView_notes);
        imagePin = itemView.findViewById(R.id.imageView_pin);
    }
}
