package com.example.music_xuzhaocheng.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_xuzhaocheng.R;
import com.example.music_xuzhaocheng.module.LrcLine;

import java.util.List;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder> {
    private List<LrcLine> lrcLines;
    private int highlightPosition = -1;

    public LyricsAdapter(List<LrcLine> lrcLines) {
        this.lrcLines = lrcLines;
    }

    @NonNull
    @Override
    public LyricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lyrics_line, parent, false);
        return new LyricsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LyricsViewHolder holder, int position) {
        LrcLine line = lrcLines.get(position);
        holder.lyricText.setText(line.getText());
        holder.lyricText.setTextColor(position == highlightPosition ? Color.YELLOW : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return lrcLines.size();
    }

    public void setHighlightPosition(int position) {
        if (highlightPosition != position) {
            int prev = highlightPosition;
            highlightPosition = position;
            if (prev >= 0) notifyItemChanged(prev);
            if (position >= 0) notifyItemChanged(position);
        }
    }

    static class LyricsViewHolder extends RecyclerView.ViewHolder {
        TextView lyricText;

        public LyricsViewHolder(@NonNull View itemView) {
            super(itemView);
            lyricText = itemView.findViewById(R.id.lyric_text);
        }
    }
}
