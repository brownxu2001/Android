package com.example.myapplication;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.ContentProviderOperation;

import android.widget.Toast;


public class Fragment7 extends Fragment {
    private ContactAdapter adapter;
    private List<Contact> contacts = new ArrayList<>();
    private static final int REQUEST_CONTACTS_PERMISSION = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_7, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_load).setOnClickListener(v -> checkContactsPermission());

        view.findViewById(R.id.btn_add).setOnClickListener(v -> showAddContactDialog());

        return view;
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS_PERMISSION);
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        Cursor cursor = requireActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        if (cursor != null) {

            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

            if (nameIndex >= 0 && phoneIndex >= 0 && idIndex >= 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    long id = cursor.getLong(idIndex);
                    contacts.add(new Contact(id, name != null ? name : "Unknown",
                            phone != null ? phone : ""));
                }
            } else {
                Log.e("Fragment7", "无效的列索引");
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);

        builder.setView(dialogView)
                .setTitle("添加联系人")
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String phone = etPhone.getText().toString();
                    if (!name.isEmpty() && !phone.isEmpty()) {
                        addNewContact(name, phone);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void addNewContact(String name, String phone) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            requireActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            loadContacts();
            Toast.makeText(requireContext(), "添加成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "添加失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        }
    }
}