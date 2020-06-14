package com.example.phrm;

class Medicalmodel{

    private String doc_date,medical_documents,imageUrl,short_description;
    private String timestamp;

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDoc_date() {
        return doc_date;
    }

    public void setDoc_date(String doc_date) {
        this.doc_date = doc_date;
    }

    public String getMedical_documents() {
        return medical_documents;
    }

    public void setMedical_documents(String medical_documents) {
        this.medical_documents = medical_documents;
    }

    public Medicalmodel(String doc_date, String medical_documents, String imageUrl, String short_description,String timestamp) {
        this.doc_date = doc_date;
        this.medical_documents = medical_documents;
        this.imageUrl = imageUrl;
        this.short_description = short_description;
        this.timestamp = timestamp;
    }

    public Medicalmodel() {
    }
}
