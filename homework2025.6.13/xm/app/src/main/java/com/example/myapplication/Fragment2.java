package com.example.myapplication;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fragment2 extends Fragment {
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private List<ListItem> items = new ArrayList<>();
    private Random random = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);


        initListData();


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListAdapter(items);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnAddItem).setOnClickListener(v -> addItem());

        view.findViewById(R.id.btnRemoveItem).setOnClickListener(v -> removeItem());

        return view;
    }

    private void initListData() {

        String[] actions = {"AAA", "BBB", "CCC", "DDD", "EEE"};

        for (int i = 0; i < 5; i++) {
            int actionIndex = random.nextInt(actions.length);
            items.add(new ListItem(
                    "游戏数据: " + i,
                    actions[actionIndex]
            ));
        }

        items.add(new ListItem("additem", "ADDED"));
        items.add(new ListItem("游戏表", "点击了button:7"));
    }

    private void addItem() {
        String[] actions = {"AAA", "BBB", "CCC", "DDD", "EEE"};
        int actionIndex = random.nextInt(actions.length);
        int newId = items.size();

        items.add(new ListItem("游戏数据: " + newId, actions[actionIndex]));
        adapter.notifyItemInserted(items.size() - 1);
        recyclerView.scrollToPosition(items.size() - 1);
    }

    private void removeItem() {
        if (items.size() > 0) {
            int position = random.nextInt(items.size());
            items.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(getContext(), "删没了", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ListItem {
        private String text;
        private String actionText;

        public ListItem(String text, String actionText) {
            this.text = text;
            this.actionText = actionText;
        }

        public String getText() {
            return text;
        }

        public String getActionText() {
            return actionText;
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private List<ListItem> items;

        public ListAdapter(List<ListItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ListItem item = items.get(position);
            holder.tvText.setText(item.getText());
            holder.btnAction.setText(item.getActionText());


            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(getContext(), "点击: " + item.getText(), Toast.LENGTH_SHORT).show();
            });

            holder.btnAction.setOnClickListener(v -> {
                Toast.makeText(getContext(), "点击了button: " + position + " - " + item.getActionText(),
                        Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAvatar;
            TextView tvText;
            Button btnAction;

            public ViewHolder(View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.ivAvatar);
                tvText = itemView.findViewById(R.id.tvText);
                btnAction = itemView.findViewById(R.id.btnAction);

                GradientDrawable avatarBg = new GradientDrawable();
                avatarBg.setShape(GradientDrawable.OVAL);
                avatarBg.setColor(Color.parseColor("#FF5722"));
                ivAvatar.setBackground(avatarBg);

                GradientDrawable btnBg = new GradientDrawable();
                btnBg.setCornerRadius(dpToPx(4));
                btnBg.setColor(Color.parseColor("#2196F3"));
                btnAction.setBackground(btnBg);
                btnAction.setTextColor(Color.WHITE);
            }

            private int dpToPx(int dp) {
                float density = getResources().getDisplayMetrics().density;
                return Math.round(dp * density);
            }
        }
    }
}