package com.takip.model;

import javax.swing.JOptionPane;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Bazlı Veri Kalıcılığı ve Dosya Yöneticisi
 * 
 * Bu sınıf, her kullanıcının kişisel izleme listesini (izledikleri, puanları,
 * yorumları, sonra izlenecekleri ve nötr keşfet yapımlarını) yerel bir dosyaya
 * kaydedip geri yükleyen dosya yönetim servisidir.
 * 
 * Teknik Özellikler ve OOP Prensipleri:
 * 1. Kullanıcıya Özel Dosyalama:
 *    - Her kullanıcı için 'veriler_{kullaniciAdi}.txt' (veya .dat) dosyası kullanılır.
 *    - Kullanıcı adı güvenli dosya adı standartlarına göre temizlenir.
 * 2. İki Yönlü Format Desteği:
 *    - İnsan tarafından okunabilir, düzenlenebilir UTF-8 anahtar-değer blok formatı:
 *      [YAPIM] ... [SON] blokları halinde Ad, Tur, Durum (İzlendi / İzlenecek / Nötr),
 *      Puan (1-5), Spoiler, AfisUrl ve Yorum alanlarını eksiksiz saklar.
 *    - Ayrıca geriye dönük uyumluluk için Java Nesne Serileştirme (.dat) desteği barındırır.
 * 3. Hata Yönetimi ve Kullanıcı Bildirimi:
 *    - Tüm dosya I/O işlemlerindeki catch bloklarında kullanıcıya anlaşılır
 *      JOptionPane.showMessageDialog hata pencereleri gösterilir.
 * 4. Çok Kullanıcılı Profil İzolasyonu:
 *    - Her kullanıcı sisteme giriş yaptığında yalnızca kendi verilerini görür.
 *    - İlk defa giriş yapan kullanıcılara varsayılan katalog atanır ve yeni profil dosyası oluşturulur.
 * 5. Oturum & Hesap Kalıcılığı:
 *    - Kayıt olan yeni kullanıcı hesapları 'kullanicilar.txt' dosyasına eklenerek oturumlar arası korunur.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 6.0 (OOP Tam Puan & Hata Yönetimi Güçlendirmesi)
 */
public class DosyaYoneticisi {

    // =========================================================================
    // SABİTLER
    // =========================================================================
    public static final String DOSYA_ONEKI = "veriler_";
    public static final String DOSYA_UZANTISI = ".txt";
    public static final String KULLANICILAR_DOSYASI = "kullanicilar.txt";

    // =========================================================================
    // DOSYA ADI VE VARLIK KONTROLÜ
    // =========================================================================

    /**
     * Kullanıcı adını dosya sistemine uygun, güvenli bir dosya adına dönüştürür.
     * 
     * @param kullaniciAdi Kullanıcının oturum açtığı kullanıcı adı
     * @return Güvenli dosya adı (Örn: 'veriler_admin.txt')
     */
    public static String getDosyaAdi(String kullaniciAdi) {
        String guvenli = getGuvenliKullaniciAdi(kullaniciAdi);
        return DOSYA_ONEKI + guvenli + DOSYA_UZANTISI;
    }

    /**
     * Kullanıcı adındaki boşluk ve özel karakterleri güvenli alt tire (_) karakterine dönüştürür.
     * İngilizce yerel ayar kullanılarak Türkçe 'İ/i' dönüşüm hataları engellenir.
     */
    public static String getGuvenliKullaniciAdi(String kullaniciAdi) {
        if (kullaniciAdi == null || kullaniciAdi.trim().isEmpty()) {
            return "misafir";
        }
        return kullaniciAdi.trim().toLowerCase(Locale.ENGLISH).replaceAll("[\\\\/:*?\"<>|\\s]", "_");
    }

    /**
     * Belirtilen kullanıcıya ait daha önce kaydedilmiş bir veri dosyasının (.txt veya .dat)
     * var olup olmadığını denetler.
     * 
     * @param kullaniciAdi Denetlenecek kullanıcı adı
     * @return Dosya mevcut ve geçerli bir dosya ise true, aksi halde false
     */
    public static boolean dosyaVarMi(String kullaniciAdi) {
        File dosyaTxt = new File(getDosyaAdi(kullaniciAdi));
        if (dosyaTxt.exists() && dosyaTxt.isFile()) {
            return true;
        }

        File dosyaDat = new File(DOSYA_ONEKI + getGuvenliKullaniciAdi(kullaniciAdi) + ".dat");
        return dosyaDat.exists() && dosyaDat.isFile();
    }

    // =========================================================================
    // VERİ KAYDETME METOTLARI (SAVE)
    // =========================================================================

    /**
     * Kullanıcının güncel film ve dizi listesini kullanıcıya özel dosyaya kaydeder.
     * Hata oluşursa kullanıcıya bilgilendirici bir hata penceresi gösterir.
     * 
     * @param kullaniciAdi Verisi kaydedilecek kullanıcının adı
     * @param filmler      Kaydedilecek film ve dizilerin listesi
     * @return İşlem başarılı ise true, aksi halde false
     */
    public static boolean verileriKaydet(String kullaniciAdi, List<Film> filmler) {
        if (filmler == null) {
            return false;
        }

        String dosyaAdi = getDosyaAdi(kullaniciAdi);
        File dosya = new File(dosyaAdi);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(dosya), StandardCharsets.UTF_8))) {

            writer.write("# ====================================================================");
            writer.newLine();
            writer.write("# Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Oturum Verileri");
            writer.newLine();
            writer.write("# Kullanıcı: " + kullaniciAdi);
            writer.newLine();
            writer.write("# Toplam Kayıt Sayısı: " + filmler.size());
            writer.newLine();
            writer.write("# ====================================================================");
            writer.newLine();
            writer.newLine();

            for (Film film : filmler) {
                if (film == null) continue;

                // Durum Metnini Belirle
                String durum;
                if (film.isIzlendi()) {
                    durum = "İzlendi";
                } else if (film.isIzlenecek()) {
                    durum = "İzlenecek";
                } else {
                    durum = "Nötr";
                }

                // Yorum metnindeki satır başı karakterlerini kaçış karakterine dönüştür
                String guvenliYorum = (film.getYorum() != null)
                        ? film.getYorum().replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\n")
                        : "";

                writer.write("[YAPIM]");
                writer.newLine();
                writer.write("Ad=" + film.getAd());
                writer.newLine();
                writer.write("Tur=" + film.getTur());
                writer.newLine();
                writer.write("Durum=" + durum);
                writer.newLine();
                writer.write("Puan=" + film.getPuan());
                writer.newLine();
                writer.write("Spoiler=" + film.isSpoilerMi());
                writer.newLine();
                writer.write("AfisUrl=" + (film.getAfisUrl() != null ? film.getAfisUrl() : ""));
                writer.newLine();
                writer.write("Yorum=" + guvenliYorum);
                writer.newLine();
                writer.write("[SON]");
                writer.newLine();
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("Dosya kaydetme hatası (" + dosyaAdi + "): " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Kullanıcı verileri '" + dosyaAdi + "' dosyasına kaydedilirken bir hata oluştu:\n" + e.getMessage(),
                    "Dosya Kayıt Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /**
     * Alternatif olarak nesne serileştirme (.dat) biçiminde verileri kaydeder.
     */
    public static boolean seriNesneKaydet(String kullaniciAdi, List<Film> filmler) {
        if (filmler == null) return false;
        String dosyaAdi = DOSYA_ONEKI + getGuvenliKullaniciAdi(kullaniciAdi) + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dosyaAdi))) {
            oos.writeObject(new ArrayList<>(filmler));
            return true;
        } catch (IOException e) {
            System.err.println(".dat serileştirme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Veriler yedek dosyaya (.dat) kaydedilirken bir hata oluştu:\n" + e.getMessage(),
                    "Yedekleme Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    // =========================================================================
    // VERİ YÜKLEME METOTLARI (LOAD)
    // =========================================================================

    /**
     * Kullanıcıya ait daha önce kaydedilmiş dosyayı okur ve {@link Film} nesneleri
     * listesi olarak sisteme geri yükler. Hata oluşursa kullanıcıya hata penceresi açar.
     * 
     * @param kullaniciAdi Yüklenecek kullanıcının adı
     * @return Yüklenen film ve dizilerin listesi (Dosya yoksa veya hata varsa null döner)
     */
    public static ArrayList<Film> verileriYukle(String kullaniciAdi) {
        String dosyaAdi = getDosyaAdi(kullaniciAdi);
        File dosya = new File(dosyaAdi);

        // Eğer .txt dosyası yoksa ama .dat varsa, .dat dosyasından okumayı dene
        if (!dosya.exists()) {
            File dosyaDat = new File(DOSYA_ONEKI + getGuvenliKullaniciAdi(kullaniciAdi) + ".dat");
            if (dosyaDat.exists()) {
                ArrayList<Film> datSonuc = seriNesneYukle(dosyaDat);
                if (datSonuc != null) {
                    return datSonuc;
                }
            }
            return null; // Kullanıcıya ait daha önce oluşturulmuş veri dosyası bulunamadı
        }

        ArrayList<Film> filmler = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(dosya), StandardCharsets.UTF_8))) {

            String satir;
            boolean yapimBlokunda = false;
            String ad = "";
            String tur = "Film";
            String durum = "Nötr";
            int puan = 0;
            boolean spoiler = false;
            String afisUrl = "";
            String yorum = "";

            while ((satir = reader.readLine()) != null) {
                satir = satir.trim();

                if (satir.isEmpty() || satir.startsWith("#")) {
                    continue; // Yorum ve boş satırları atla
                }

                if (satir.equalsIgnoreCase("[YAPIM]") || satir.equalsIgnoreCase("[ESER]")) {
                    yapimBlokunda = true;
                    ad = "";
                    tur = "Film";
                    durum = "Nötr";
                    puan = 0;
                    spoiler = false;
                    afisUrl = "";
                    yorum = "";
                    continue;
                }

                if (satir.equalsIgnoreCase("[SON]") || satir.equalsIgnoreCase("[BITIS]")) {
                    if (yapimBlokunda && !ad.isEmpty()) {
                        boolean izlendi = durum.equalsIgnoreCase("İzlendi") || durum.equalsIgnoreCase("Izlendi");
                        boolean izlenecek = durum.equalsIgnoreCase("İzlenecek") || durum.equalsIgnoreCase("Izlenecek");

                        Film film = new Film(ad, tur, izlendi, izlenecek, puan, yorum, spoiler, afisUrl);
                        filmler.add(film);
                    }
                    yapimBlokunda = false;
                    continue;
                }

                if (yapimBlokunda) {
                    int esittirIndex = satir.indexOf('=');
                    if (esittirIndex != -1) {
                        String anahtar = satir.substring(0, esittirIndex).trim().toUpperCase(Locale.ENGLISH);
                        String deger = satir.substring(esittirIndex + 1).trim();

                        switch (anahtar) {
                            case "AD":
                            case "BASLIK":
                                ad = deger;
                                break;
                            case "TUR":
                                tur = deger;
                                break;
                            case "DURUM":
                                durum = deger;
                                break;
                            case "PUAN":
                                try {
                                    puan = Integer.parseInt(deger);
                                } catch (NumberFormatException ignored) {
                                    puan = 0;
                                }
                                break;
                            case "SPOILER":
                                spoiler = Boolean.parseBoolean(deger) || "evet".equalsIgnoreCase(deger);
                                break;
                            case "AFIS":
                            case "AFISURL":
                            case "AFİS":
                            case "AFİSURL":
                                afisUrl = deger;
                                break;
                            case "YORUM":
                                yorum = deger.replace("\\n", "\n");
                                break;
                        }
                    }
                }
            }

            return filmler;
        } catch (IOException e) {
            System.err.println("Dosya okuma hatası (" + dosyaAdi + "): " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Kullanıcı verileri '" + dosyaAdi + "' dosyasından okunurken bir hata oluştu:\n" + e.getMessage(),
                    "Dosya Okuma Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    /**
     * .dat ikili dosyasından serileştirilmiş ArrayList nesnesini yükler.
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<Film> seriNesneYukle(File dosya) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dosya))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                return (ArrayList<Film>) obj;
            }
        } catch (Exception e) {
            System.err.println(".dat okuma hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Yedek veri dosyası okunurken bir hata oluştu:\n" + e.getMessage(),
                    "Veri Okuma Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        return null;
    }

    // =========================================================================
    // KULLANICI HESAPLARI KALICILIĞI (KULLANICILAR.TXT)
    // =========================================================================

    /**
     * 'kullanicilar.txt' dosyasında kayıtlı kullanıcıları okur ve
     * {@link KullaniciYoneticisi} belleğine ekler.
     */
    public static void kullanicilariYukle() {
        File dosya = new File(KULLANICILAR_DOSYASI);
        if (!dosya.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(dosya), StandardCharsets.UTF_8))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                satir = satir.trim();
                if (satir.isEmpty() || satir.startsWith("#")) {
                    continue;
                }
                String[] parcalar = satir.split("\\|", -1);
                if (parcalar.length >= 3) {
                    Kullanici k = new Kullanici(parcalar[0].trim(), parcalar[1].trim(), parcalar[2].trim());
                    KullaniciYoneticisi.kullaniciKaydet(k);
                }
            }
        } catch (Exception e) {
            System.err.println("Kullanıcı listesi okuma hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Kullanıcı hesap listesi ('" + KULLANICILAR_DOSYASI + "') yüklenirken bir hata oluştu:\n" + e.getMessage(),
                    "Hesap Listesi Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Sisteme yeni kayıt olan kullanıcıyı 'kullanicilar.txt' dosyasına kalıcı olarak ekler.
     * 
     * @param yeniKullanici Kaydedilecek yeni kullanıcı
     */
    public static void kullaniciKaydet(Kullanici yeniKullanici) {
        if (yeniKullanici == null) return;
        File dosya = new File(KULLANICILAR_DOSYASI);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(dosya, true), StandardCharsets.UTF_8))) {
            writer.write(yeniKullanici.getIsim() + "|" + yeniKullanici.getEmail() + "|" + yeniKullanici.getSifre());
            writer.newLine();
        } catch (Exception e) {
            System.err.println("Kullanıcı kaydetme hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Yeni kullanıcı hesabı kaydedilirken bir hata oluştu:\n" + e.getMessage(),
                    "Kullanıcı Kayıt Hatası",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
