package com.takip.model;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Modeli
 * 
 * Bu sınıf, sisteme kayıt olan ve giriş yapan kullanıcıların
 * kimlik ve hesap bilgilerini temsil eden model sınıfıdır.
 * Kapsülleme (Encapsulation) prensiplerine tam uyumludur.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 1.0
 */
public class Kullanici {

    // =========================================================================
    // ALANLAR (FIELDS)
    // =========================================================================
    private String isim;
    private String email;
    private String sifre;

    // =========================================================================
    // YAPICI METOTLAR (CONSTRUCTORS)
    // =========================================================================
    public Kullanici() {
        this.isim = "";
        this.email = "";
        this.sifre = "";
    }

    public Kullanici(String isim, String email, String sifre) {
        this.isim = isim;
        this.email = email;
        this.sifre = sifre;
    }

    // =========================================================================
    // GETTER VE SETTER METOTLARI
    // =========================================================================
    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    @Override
    public String toString() {
        return "Kullanici [İsim=" + isim + ", E-posta=" + email + "]";
    }
}
