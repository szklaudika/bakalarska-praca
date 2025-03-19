package com.example.zapisnik;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String text;  // Sem zvyknete dávať zobrazený text (napr. "PPL (Angličtina)...")
    private String date;  // Nové pole na uloženie dátumu

    // Konštruktor pre header
    public ListItem(int type, String text) {
        this.type = type;
        this.text = text;
    }

    // Konštruktor pre item (detail + dátum)
    public ListItem(int type, String date, String text) {
        this.type = type;
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
}