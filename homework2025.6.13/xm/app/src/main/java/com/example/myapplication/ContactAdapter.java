package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> contacts;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            phone = itemView.findViewById(R.id.tv_phone);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(contacts.get(position).getName());
        holder.phone.setText(contacts.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void updateContacts(List<Contact> newContacts) {
        contacts = newContacts;
        notifyDataSetChanged();
    }
}