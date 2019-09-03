package com.example.supportelderly;


public class Medicine {
    private int id;
    private String dose;
    private String frequency;
    private byte[] image;
    private String nameMedicine;

    public Medicine(String nameOfMedicine, String dose, String frequency, byte[] image, int id) {
        this.nameMedicine = nameOfMedicine;
        this.dose = dose;
        this.frequency = frequency;
        this.image = image;
        this.id = id;
    }


    public void setName(String name) {
        this.nameMedicine = name;
    }

    public String getDose() {
        return dose;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfMedicine() {
        return nameMedicine;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setDose(String price) {
        this.dose = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
