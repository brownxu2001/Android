package com.example.myapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment3 extends Fragment {
    private RecyclerView rvQuestions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);


        initQuestionList(view);

        return view;
    }

    private void initQuestionList(View view) {
        rvQuestions = view.findViewById(R.id.rvQuestions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> questions = Arrays.asList(
                "忘记账号，如何找回？",
                "如何修改账号密码？",
                "账号锁定怎么办？",
                "如何绑定手机号？",
                "如何解绑已绑定设备？",
                "账号异常登录提醒",
                "如何注销？"
        );

        rvQuestions.setAdapter(new QuestionAdapter(questions));
    }

    private static class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
        private List<String> questions;

        public QuestionAdapter(List<String> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_question, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvQuestion.setText(questions.get(position));
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion;

            public ViewHolder(View itemView) {
                super(itemView);
                tvQuestion = itemView.findViewById(R.id.tvQuestion);
            }
        }
    }
}