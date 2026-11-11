package com.takip.model;

import java.io.Serializable;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Eser / Yapım Modeli
 * 
 * Bu sınıf sistemde kayıt altına alınan her bir film veya dizi nesnesinin
 * temel verilerini ve özelliklerini temsil eden OOP (Nesne Tabanlı Programlama)
 * model sınıfıdır.
 * 
 * OOP İlkeleri ve Doğrulamalar:
 * - Kapsülleme (Encapsulation): Tüm alanlar kesin olarak 'private' tanımlanmıştır.
 * - Doğrulama (Validation): 'setPuan(int puan)' metodunda 0 ile 5 aralığı denetlenir;
 *   0'dan küçük değerler 0'a, 5'ten büyük değerler 5'e çekilir.
 * - Serileştirme Desteği: Nesne kalıcılığı için 'Serializable' arabirimi uygulanmıştır.
 * - Kolay durum geçiş metotları: durumuIzlendiYap(), durumuIzlenecekYap(), durumuNotrYap().
 * - Dinamik tür yardımcı metotları: isDizi(), isFilm(), getTurIsmi(), getTurIsmiBelirtmeHali().
 * 
 * @author Proje Geliştirme Ekibi
 * @version 6.0 (OOP Rubriği Tam Kapsülleme ve Doğrulama)
 */
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    // =========================================================================
    // ALANLAR (FIELDS / ATTRIBUTES - TÜMÜ PRIVATE)
    // =========================================================================

    /** Film veya dizinin adı */
    private String ad;

    /** Yapımın türü ("Film" veya "Dizi") */
    private String tur;

    /** Kullanıcının bu yapımı izleyip izlemediği bilgisi */
    private boolean izlendi;

    /** Kullanıcının bu yapımı sonra izlemek üzere listesine ekleyip eklemediği bilgisi */
    private boolean izlenecek;

    /** Kullanıcının yapıma verdiği puan (0 ile 5 arasında) */
    private int puan;

    /** Kullanıcının yapım hakkındaki kişisel değerlendirme notu */
    private String yorum;

    /** Yorumun sürprizbozan (spoiler) içerip içermediği bilgisi */
    private boolean spoilerMi;

    /** Film veya dizinin internetteki afiş/kapak görseli bağlantısı (URL) */
    private String afisUrl;

    // =========================================================================
    // YAPICI METOTLAR (CONSTRUCTORS)
    // =========================================================================

    /**
     * Varsayılan (Parametresiz) Yapıcı Metot.
     */
    public Film() {
        this("", "Film", false, false, 0, "", false, "");
    }

    /**
     * Keşfet Kataloğu için Nötr Yapım Yapıcı Metodu (Henüz kullanıcı listelerine eklenmemiş).
     */
    public Film(String ad, String tur, String afisUrl, String varsayilanYorum) {
        this(ad, tur, false, false, 0, varsayilanYorum, false, afisUrl);
    }

    /**
     * Form ve Geriye Dönük Uyumluluk Yapıcı Metodu.
     * Eğer izlendi true ise durum "İzlendi" olur; false ise kullanıcı formundan
     * eklendiği için doğrudan "İzlenecek" durumuna atanır.
     */
    public Film(String ad, String tur, boolean izlendi, int puan, String yorum, boolean spoilerMi, String afisUrl) {
        this(ad, tur, izlendi, !izlendi, puan, yorum, spoilerMi, afisUrl);
    }

    /**
     * Tüm alanları (izlendi ve izlenecek ayrı ayrı) parametre olarak alan tam yapıcı metot.
     */
    public Film(String ad, String tur, boolean izlendi, boolean izlenecek, int puan, String yorum, boolean spoilerMi, String afisUrl) {
        this.ad = (ad != null) ? ad.trim() : "";
        this.tur = (tur != null) ? tur.trim() : "Film";
        this.izlendi = izlendi;
        this.izlenecek = izlenecek;
        this.setPuan(puan); // 0-5 aralık doğrulaması setter üzerinden uygulanır
        this.yorum = (yorum != null) ? yorum.trim() : "";
        this.spoilerMi = spoilerMi;
        this.afisUrl = (afisUrl != null) ? afisUrl.trim() : "";
    }

    // =========================================================================
    // GETTER VE SETTER METOTLARI
    // =========================================================================

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = (ad != null) ? ad.trim() : "";
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = (tur != null) ? tur.trim() : "Film";
    }

    public boolean isIzlendi() {
        return izlendi;
    }

    public void setIzlendi(boolean izlendi) {
        this.izlendi = izlendi;
        if (izlendi) {
            this.izlenecek = false;
        }
    }

    public boolean isIzlenecek() {
        return izlenecek;
    }

    public void setIzlenecek(boolean izlenecek) {
        this.izlenecek = izlenecek;
        if (izlenecek) {
            this.izlendi = false;
        }
    }

    /**
     * Yapımın kullanıcının kişisel listelerinde olmayıp genel keşfet kataloğunda nötr olup olmadığını döner.
     */
    public boolean isNotr() {
        return !izlendi && !izlenecek;
    }

    public int getPuan() {
        return puan;
    }

    /**
     * Yapıma verilen puanı ayarlar. 
     * 0-5 aralık kontrolü (validation):
     * 0'dan küçük değerler 0, 5'ten büyük değerler 5 olarak sınırlandırılır.
     * 
     * @param puan Girilen puan değeri (0 - 5)
     */
    public void setPuan(int puan) {
        if (puan < 0) {
            this.puan = 0;
        } else if (puan > 5) {
            this.puan = 5;
        } else {
            this.puan = puan;
        }
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = (yorum != null) ? yorum.trim() : "";
    }

    public boolean isSpoilerMi() {
        return spoilerMi;
    }

    public void setSpoilerMi(boolean spoilerMi) {
        this.spoilerMi = spoilerMi;
    }

    public String getAfisUrl() {
        return afisUrl;
    }

    public void setAfisUrl(String afisUrl) {
        this.afisUrl = (afisUrl != null) ? afisUrl.trim() : "";
    }

    // =========================================================================
    // DURUM GEÇİŞ VE YÖNETİM METOTLARI (DİNAMİK ETKİLEŞİM)
    // =========================================================================

    /**
     * Yapımı kullanıcı listelerinden çıkarır ve ana katalogdaki nötr (keşfet) durumuna döndürür.
     */
    public void durumuNotrYap() {
        this.izlendi = false;
        this.izlenecek = false;
        this.puan = 0;
    }

    /**
     * Yapımı "İzlendi" durumuna geçirir; puan ve yorum bilgilerini atar.
     */
    public void durumuIzlendiYap(int puan, String yorum, boolean spoilerMi) {
        this.izlendi = true;
        this.izlenecek = false;
        this.setPuan(puan); // 0-5 doğrulama uygulanır
        if (yorum != null && !yorum.trim().isEmpty()) {
            this.yorum = yorum.trim();
        }
        this.spoilerMi = spoilerMi;
    }

    /**
     * Yapımı "Sonra İzlenecekler" listesine ekler.
     */
    public void durumuIzlenecekYap() {
        this.izlendi = false;
        this.izlenecek = true;
        this.puan = 0;
    }

    // =========================================================================
    // YARDIMCI DİNAMİK METİN METOTLARI
    // =========================================================================

    /**
     * Yapımın bir TV Dizisi olup olmadığını döner.
     */
    public boolean isDizi() {
        return "Dizi".equalsIgnoreCase(this.tur);
    }

    /**
     * Yapımın bir Sinema Filmi olup olmadığını döner.
     */
    public boolean isFilm() {
        return "Film".equalsIgnoreCase(this.tur);
    }

    /**
     * Türe göre "Film" veya "Dizi" ifadesini döner.
     */
    public String getTurIsmi() {
        return isDizi() ? "Dizi" : "Film";
    }

    /**
     * Türe göre Türkçe belirtme durum ekiyle ("filmi" / "diziyi") döner.
     */
    public String getTurIsmiBelirtmeHali() {
        return isDizi() ? "diziyi" : "filmi";
    }

    // =========================================================================
    // METİNSEL TEMSİL METODU (toString)
    // =========================================================================

    @Override
    public String toString() {
        String durumMetni = isIzlendi() ? "İzlendi (" + puan + "/5)" : (isIzlenecek() ? "İzlenecek" : "Keşfet (Nötr)");
        return "Yapım [" +
                "Ad='" + ad + '\'' +
                ", Tür='" + tur + '\'' +
                ", Durum=" + durumMetni +
                ", Spoiler=" + (spoilerMi ? "Evet" : "Hayır") +
                ", Afiş=" + (afisUrl.isEmpty() ? "Yok" : "Var") +
                ']';
    }
}
