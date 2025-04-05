package com.example.zapisnik;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String text;  // Displayed text (e.g., "PPL (Angličtina)...")
    private String date;  // For example, formatted expiry date
    private int certificateId; // New field for the certificate's unique ID

    // Constructor for header items
    public ListItem(int type, String text) {
        this.type = type;
        this.text = text;
    }

    // Constructor for certificate items with certificateId, date, and text.
    public ListItem(int type, int certificateId, String date, String text) {
        this.type = type;
        this.certificateId = certificateId;
        this.date = date;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return certificateId;
    }
}
