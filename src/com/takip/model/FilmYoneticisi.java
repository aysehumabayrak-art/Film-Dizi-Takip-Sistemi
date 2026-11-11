package com.takip.model;

import java.util.ArrayList;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Veri Yönetim Sınıfı
 * 
 * Bu sınıf, sistemdeki film ve dizi nesnelerinin koleksiyonunu yöneten
 * servis/yönetici (Manager/Service) bileşenidir. Bellek içi (in-memory)
 * depolama için {@link ArrayList} veri yapısını kullanır.
 * 
 * Yeni Güncellemeler (v5.0):
 * - Keşfet / Genel Katalog Vitrini:
 *   Başlangıçtaki popüler 10 film ve dizi (Inception, Breaking Bad, Dune, Interstellar vb.)
 *   ana sayfada nötr (henüz kullanıcının listesine eklenmemiş) keşfet yapımları olarak listelenir.
 * - Kullanıcı Listeleri Ayrımı:
 *   * getIzlenenler(): Yalnızca kullanıcının [✓ İzledim] ile puanlayıp eklediği yapımları listeler.
 *   * getSonraIzlenecekler(): Yalnızca [➕ İzlemek İstiyorum] ile listeye eklenen yapımları listeler.
 *   * Başlangıçta kullanıcı listeleri boş olduğu için ilgili sekmeler zarif "Empty State" mesajlarını gösterir.
 * - Dinamik Metrik Hesaplamaları:
 *   Ortalama puan ve tahmini izleme süresi yalnızca kullanıcının bizzat izlediği yapımlar üzerinden hesaplanır.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 5.0 (Keşfet Vitrini ve Kullanıcı Listeleri Mimarisi)
 */
public class FilmYoneticisi {

    // =========================================================================
    // VERİ YAPISI
    // =========================================================================

    /**
     * Sistemde kayıtlı olan tüm film ve dizi nesnelerini (Keşfet kataloğu + kullanıcı kayıtları) tutan liste.
     */
    private ArrayList<Film> filmler;

    // =========================================================================
    // YAPICI METOT (CONSTRUCTOR)
    // =========================================================================

    public FilmYoneticisi() {
        this.filmler = new ArrayList<>();
    }

    // =========================================================================
    // YÖNETİM METOTLARI (CRUD İŞLEMLERİ)
    // =========================================================================

    public boolean filmEkle(Film film) {
        if (film == null) {
            return false;
        }
        return this.filmler.add(film);
    }

    public boolean filmSil(Film film) {
        if (film == null) {
            return false;
        }
        return this.filmler.remove(film);
    }

    public Film filmSil(int index) {
        if (index >= 0 && index < this.filmler.size()) {
            return this.filmler.remove(index);
        }
        return null;
    }

    /**
     * Ana sayfada sergilenen tüm yapımları (Genel Keşfet Kataloğu) döner.
     */
    public ArrayList<Film> getFilmler() {
        return this.filmler;
    }

    // =========================================================================
    // FİLTRELEME METOTLARI (KULLANICI LİSTELERİ & TÜRLER)
    // =========================================================================

    public ArrayList<Film> getSadeceFilmler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if ("Film".equalsIgnoreCase(f.getTur())) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    public ArrayList<Film> getSadeceDiziler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if ("Dizi".equalsIgnoreCase(f.getTur())) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    /**
     * Yalnızca kullanıcının bizzat "İzlendi" olarak işaretlediği ve puanladığı yapımları döner.
     */
    public ArrayList<Film> getIzlenenler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if (f.isIzlendi()) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    /**
     * Yalnızca durumu 'İzlendi' VE türü 'Film' olan yapımları listeler.
     */
    public ArrayList<Film> getIzlenenFilmler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if (f.isIzlendi() && "Film".equalsIgnoreCase(f.getTur())) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    /**
     * Yalnızca durumu 'İzlendi' VE türü 'Dizi' olan yapımları listeler.
     */
    public ArrayList<Film> getIzlenenDiziler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if (f.isIzlendi() && "Dizi".equalsIgnoreCase(f.getTur())) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    /**
     * Yalnızca kullanıcının "Sonra İzlenecekler" listesine eklediği yapımları döner.
     */
    public ArrayList<Film> getSonraIzlenecekler() {
        ArrayList<Film> sonuc = new ArrayList<>();
        for (Film f : filmler) {
            if (f.isIzlenecek()) {
                sonuc.add(f);
            }
        }
        return sonuc;
    }

    // =========================================================================
    // İSTATİSTİK VE SAYAÇ METOTLARI (KULLANICIYA ÖZEL)
    // =========================================================================

    public int getToplamSayisi() {
        return this.filmler.size();
    }

    public int getFilmTuruSayisi() {
        return getSadeceFilmler().size();
    }

    public int getDiziTuruSayisi() {
        return getSadeceDiziler().size();
    }

    public int getIzlenenSayisi() {
        return getIzlenenler().size();
    }

    public int getIzlenenFilmSayisi() {
        return getIzlenenFilmler().size();
    }

    public int getIzlenenDiziSayisi() {
        return getIzlenenDiziler().size();
    }

    public int getIzlenecekSayisi() {
        return getSonraIzlenecekler().size();
    }

    /**
     * Yalnızca kullanıcının izlediği ve puan verdiği (puan > 0) yapımların aritmetik ortalama puanını hesaplar.
     */
    public double getOrtalamaPuan() {
        ArrayList<Film> izlenenler = getIzlenenler();
        int toplamPuan = 0;
        int puanlananSayisi = 0;

        for (Film f : izlenenler) {
            if (f.getPuan() > 0) {
                toplamPuan += f.getPuan();
                puanlananSayisi++;
            }
        }

        if (puanlananSayisi == 0) {
            return 0.0;
        }

        return (double) toplamPuan / puanlananSayisi;
    }

    /**
     * Ortalama puanı kullanıcı dostu metin formatında döner (Örn: "4.8 / 5.0" veya "-").
     */
    public String getOrtalamaPuanMetni() {
        double ortalama = getOrtalamaPuan();
        if (ortalama <= 0.0) {
            return "-";
        }
        return String.format(java.util.Locale.US, "%.1f / 5.0", ortalama);
    }

    /**
     * Kullanıcının izlediği yapımların tahmini toplam izleme süresini dakika cinsinden hesaplar.
     * Kabul: Her izlenen Film için 120 dakika (2 saat), her izlenen Dizi için 600 dakika (10 saat).
     */
    public int getTahminiToplamIzlemeDakikasi() {
        int filmDakika = getIzlenenFilmSayisi() * 120;
        int diziDakika = getIzlenenDiziSayisi() * 600;
        return filmDakika + diziDakika;
    }

    /**
     * Tahmini izleme süresini saat ve dakika olarak biçimlendirir (Örn: "36 Saat 0 Dakika").
     */
    public String getTahminiIzlemeSuresiMetni() {
        int toplamDakika = getTahminiToplamIzlemeDakikasi();
        int saat = toplamDakika / 60;
        int dakika = toplamDakika % 60;
        return saat + " Saat " + dakika + " Dakika";
    }

    /**
     * Kullanıcının izlediği yapımlar arasında en çok tercih ettiği türü analiz eder.
     */
    public String getEnCokTercihEdilenTur() {
        int filmSayisi = getIzlenenFilmSayisi();
        int diziSayisi = getIzlenenDiziSayisi();

        if (filmSayisi == 0 && diziSayisi == 0) {
            return "Henüz Veri Yok";
        }

        if (filmSayisi > diziSayisi) {
            return "Film (Ağırlıklı)";
        } else if (diziSayisi > filmSayisi) {
            return "Dizi (Ağırlıklı)";
        } else {
            return "Dengeli (Film & Dizi)";
        }
    }

    public void tumunuTemizle() {
        this.filmler.clear();
    }

    // =========================================================================
    // GENEL KEŞFET KATALOĞU (NÖTR BAŞLANGIÇ LİSTESİ)
    // =========================================================================

    /**
     * Ana sayfada vitrin olarak sergilenecek 10 adet popüler film ve diziyi yükler.
     * Başlangıçta tüm yapımlar NÖTR (izlendi = false, izlenecek = false) olarak başlar.
     * Böylece "İzlediklerim" ve "Sonra İzlenecekler" sekmeleri başlangıçta boştur.
     */
    public void varsayilanVerileriYukle() {
        this.filmler.clear();

        // 1. Dune: Part Two (Film, Nötr/Keşfet)
        filmEkle(new Film(
                "Dune: Part Two",
                "Film",
                false, // izlendi
                false, // izlenecek
                0,     // puan
                "Sinemada izlemek için sabırsızlanıyorum, ilk filmi çok beğenmiştim.",
                false,
                "https://cdn.sinemalar.com/images/movie/274150/gallery/dune-2-1708088443.jpg"
        ));

        // 2. Breaking Bad (Dizi, Nötr/Keşfet)
        filmEkle(new Film(
                "Breaking Bad",
                "Dizi",
                false,
                false,
                0,
                "Dizi tarihinin en kusursuz finaline sahip başyapıtı. Karakter gelişimleri ders niteliğinde.",
                true,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsaU70LeqrcOhtLvFbajq9O2ro87I7T-hZd91evM8ElQ&s=10"
        ));

        // 3. Inception (Film, Nötr/Keşfet)
        filmEkle(new Film(
                "Inception",
                "Film",
                false,
                false,
                0,
                "Christopher Nolan'dan rüya içinde rüya konseptli muazzam bir bilim kurgu başyapıtı. Müzikler olağanüstü.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrrFOFA31IxsW8YTci1RxsdapEdUHace1q7tLb43EE0w&s=10"
        ));

        // 4. Severance (Dizi, Nötr/Keşfet)
        filmEkle(new Film(
                "Severance",
                "Dizi",
                false,
                false,
                0,
                "İş ve özel hayat ayrımını konu alan distopik gizem dizisi.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3g_n3G9-mp9z9vFv2OdhrdizmB6Bf4P6oo6FPQKTDzg&s=10"
        ));

        // 5. Interstellar (Film, Nötr/Keşfet)
        filmEkle(new Film(
                "Interstellar",
                "Film",
                false,
                false,
                0,
                "Uzay-zaman bükülmesi ve baba-kız ilişkisini harika bağlayan bir başyapıt.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRN6MBU9VxzNxqU0gzzOsgDR0Mpxn4_6BDHIzD-Xc8YaQ&s=10"
        ));

        // 6. The Dark Knight (Film, Nötr/Keşfet)
        filmEkle(new Film(
                "The Dark Knight",
                "Film",
                false,
                false,
                0,
                "Joker performansıyla sinema tarihinin en iyi çizgi roman uyarlaması.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3PhCcXO0pyxx4bXbkEJbCmPNQNj7nmFjdN1gPy0JCsw&s=10"
        ));

        // 7. Fight Club (Film, Nötr/Keşfet)
        filmEkle(new Film(
                "Fight Club",
                "Film",
                false,
                false,
                0,
                "Kült bir klasik, sonundaki ters köşesiyle sinema efsanesi.",
                true,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxnNDrJNgxA5hZPfuhD6Shb7wmNdvJ_lJe90KTII7vbQ&s=10"
        ));

        // 8. Stranger Things (Dizi, Nötr/Keşfet)
        filmEkle(new Film(
                "Stranger Things",
                "Dizi",
                false,
                false,
                0,
                "80'ler nostaljisi ve fantastik gizem örgüsü çok sürükleyici.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrPls9rZtuIWtDvESGz-JtttBHQE2O__xbeluI6oUADQ&s=10"
        ));

        // 9. Chernobyl (Dizi, Nötr/Keşfet)
        filmEkle(new Film(
                "Chernobyl",
                "Dizi",
                false,
                false,
                0,
                "Gerilimi her saniye hissettiren muazzam bir mini dizi.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-EoF0OqwnI4ivHEhphaEd9sVrUxMsI5dsIkKRZoA7sg&s=10"
        ));

        // 10. Succession (Dizi, Nötr/Keşfet)
        filmEkle(new Film(
                "Succession",
                "Dizi",
                false,
                false,
                0,
                "Güç ve aile çatışmasını anlatan çok övülen bir yapım.",
                false,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFnbkrKBEJQGyNwpzto8ju7maYPQze_DJa7ktZZcsFSw&s=10"
        ));
    }
}
