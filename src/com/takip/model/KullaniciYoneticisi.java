package com.takip.model;

import java.util.ArrayList;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Yönetim Servisi
 * 
 * Bu sınıf, sistemdeki kullanıcı kayıtlarını, benzersizlik doğrulamalarını
 * ve giriş kimlik doğrulama (authentication) işlemlerini yönetir.
 * Bellek içi (in-memory) static koleksiyon kullanır.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 1.0
 */
public class KullaniciYoneticisi {

    // =========================================================================
    // STATİK VERİ LİSTESİ
    // =========================================================================
    private static final ArrayList<Kullanici> kullaniciListesi = new ArrayList<>();

    // Varsayılan Demo Hesap Başlatıcı
    static {
        // Test ve sunum kolaylığı için varsayılan yönetici hesabı
        kullaniciListesi.add(new Kullanici("admin", "admin@mail.com", "1234"));
        kullaniciListesi.add(new Kullanici("ahmet", "ahmet@gmail.com", "pass123"));
    }

    // =========================================================================
    // YÖNETİM VE DOĞRULAMA METOTLARI
    // =========================================================================

    /**
     * Sisteme yeni bir kullanıcı kaydeder.
     * Kullanıcı adı veya e-posta adresinin daha önce alınıp alınmadığını denetler.
     * 
     * @param yeniKullanici Kaydedilecek {@link Kullanici} nesnesi
     * @return Kayıt sonucu mesajı (Başarılı ise null, hata varsa hata metni döner)
     */
    public static String kullaniciKaydet(Kullanici yeniKullanici) {
        if (yeniKullanici == null) {
            return "Geçersiz kullanıcı bilgisi!";
        }

        String isim = yeniKullanici.getIsim().trim();
        String email = yeniKullanici.getEmail().trim();

        // 1. İsim ve E-posta benzersizlik kontrolü
        for (Kullanici k : kullaniciListesi) {
            if (k.getIsim().equalsIgnoreCase(isim)) {
                return "Bu kullanıcı adı ('" + isim + "') zaten kullanılmaktadır!";
            }
            if (k.getEmail().equalsIgnoreCase(email)) {
                return "Bu e-posta adresi ('" + email + "') zaten kayıtlıdır!";
            }
        }

        // Listeye ekle
        kullaniciListesi.add(yeniKullanici);
        return null; // Hata yok, kayıt başarılı
    }

    /**
     * Kullanıcı giriş (kimlik doğrulama) işlemini yürütür.
     * Kullanıcı ister adı ('isim') ister 'email' adresi ile giriş yapabilir.
     * 
     * @param kullaniciAdiVeyaEmail Kullanıcı adı veya e-posta
     * @param sifre                 Girilen şifre
     * @return Giriş başarılı ise {@link Kullanici} nesnesi, başarısız ise null
     */
    public static Kullanici girisYap(String kullaniciAdiVeyaEmail, String sifre) {
        if (kullaniciAdiVeyaEmail == null || sifre == null) {
            return null;
        }

        String giris = kullaniciAdiVeyaEmail.trim();

        for (Kullanici k : kullaniciListesi) {
            boolean isimEslesiyor = k.getIsim().equalsIgnoreCase(giris);
            boolean emailEslesiyor = k.getEmail().equalsIgnoreCase(giris);

            if ((isimEslesiyor || emailEslesiyor) && k.getSifre().equals(sifre)) {
                return k; // Başarılı giriş
            }
        }

        return null; // Eşleşme bulunamadı
    }

    /**
     * Sistemdeki toplam kayıtlı kullanıcı sayısını döndürür.
     */
    public static int getKullaniciSayisi() {
        return kullaniciListesi.size();
    }
}
