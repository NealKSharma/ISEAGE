package com.iseage.wms.Label;

public class Label {
    private String first_name;
    private String last_name;
    private String barcode;
    private String address;

    public Label() {}

    public Label(String first_name, String last_name, String address, String barcode) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.barcode = barcode;
    }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
}