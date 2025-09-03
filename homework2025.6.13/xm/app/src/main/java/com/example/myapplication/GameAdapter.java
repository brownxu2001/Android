/*
package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.graphics.Typeface; // 保留
import android.text.TextUtils;    // 保留
import java.util.Locale;

import android.graphics.Typeface; // 添加缺失导入
import android.text.TextUtils;    // 添加缺失导入
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;            // 修正导入
import java.util.Locale;          // 添加缺失导入

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<GameEntity> games; // 明确类型声明

    // 构造函数参数必须与创建时传递的类型一致
    public GameAdapter(List<GameEntity> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实现缺失的抽象方法
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameEntity game = games.get(position);

        // 使用Typeface实现加粗
        holder.nameTextView.setTypeface(null, Typeface.BOLD);
        holder.nameTextView.setText(game.gameName);

        // 使用TextUtils实现单行省略
        holder.introTextView.setSingleLine(true);
        holder.introTextView.setEllipsize(TextUtils.TruncateAt.END);
        holder.introTextView.setText(game.introduction);

        // 使用Locale格式化评分
        holder.scoreTextView.setText(String.format(Locale.getDefault(), "%.1f分", game.score));
        holder.playerCountTextView.setText(game.playNumFormat);
    }

    // 实现缺失的getItemCount方法
    @Override
    public int getItemCount() {
        return games != null ? games.size() : 0;
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        // 包含所有视图组件
        TextView nameTextView;
        TextView introTextView;
        TextView scoreTextView;
        TextView playerCountTextView;

        public GameViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_game_name);
            introTextView = itemView.findViewById(R.id.tv_game_intro);
            scoreTextView = itemView.findViewById(R.id.tv_game_score);
            playerCountTextView = itemView.findViewById(R.id.tv_game_players);
        }
    }
}*/
