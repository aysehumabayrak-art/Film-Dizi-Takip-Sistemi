package com.takip.model;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Katalog Eseri Modeli
 * 
 * Bu sınıf, hazır arama kataloğundaki öneri kayıtlarını (film ve dizi)
 * temsil eden veri modelidir.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 1.0
 */
public class KatalogEseri {

    private String ad;
    private String tur; // "Film" veya "Dizi"
    private String afisUrl; // İsteğe bağlı hazır afiş bağlantısı

    public KatalogEseri() {
        this("", "Film", "");
    }

    public KatalogEseri(String ad, String tur) {
        this(ad, tur, "");
    }

    public KatalogEseri(String ad, String tur, String afisUrl) {
        this.ad = ad;
        this.tur = tur;
        this.afisUrl = (afisUrl != null) ? afisUrl : "";
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getAfisUrl() {
        return afisUrl;
    }

    public void setAfisUrl(String afisUrl) {
        this.afisUrl = afisUrl;
    }

    /**
     * Kullanıcı arayüzünde (JList, JComboBox vb.) gösterilecek standart metin biçimi.
     * Örnek: "The Matrix [Film]" veya "Stranger Things [Dizi]"
     */
    @Override
    public String toString() {
        return ad + " [" + tur + "]";
    }
}
