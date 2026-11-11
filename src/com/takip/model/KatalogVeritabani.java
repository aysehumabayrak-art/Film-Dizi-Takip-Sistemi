package com.takip.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Hazır Film ve Dizi Kataloğu
 * 
 * Bu sınıf, kullanıcının yeni kayıt eklerken hızlı arama ve otomatik doldurma
 * yapabilmesi için popüler 30+ film ve diziyi barındıran statik katalog veritabanıdır.
 * 
 * Özellikler:
 * - Türkçe/İngilizce büyük/küçük harf duyarsız (case-insensitive) arama motoru.
 * - 'İ-i', 'I-ı' gibi Türkçe karakter uyumsuzluklarını önleyen harf normalizasyonu.
 * - Seçilen popüler eserler için doğrudan çalışan yüksek kaliteli afiş bağlantıları.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 1.0
 */
public class KatalogVeritabani {

    /**
     * Sistemde hazır olarak sunulan film ve dizi kataloğu listesi.
     */
    private static final List<KatalogEseri> KATALOG = new ArrayList<>();

    static {
        // =====================================================================
        // POPÜLER FİLMLER (16 ADET)
        // =====================================================================
        KATALOG.add(new KatalogEseri(
                "Inception",
                "Film",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrrFOFA31IxsW8YTci1RxsdapEdUHace1q7tLb43EE0w&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Interstellar",
                "Film",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRN6MBU9VxzNxqU0gzzOsgDR0Mpxn4_6BDHIzD-Xc8YaQ&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "The Dark Knight",
                "Film",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3PhCcXO0pyxx4bXbkEJbCmPNQNj7nmFjdN1gPy0JCsw&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Fight Club",
                "Film",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxnNDrJNgxA5hZPfuhD6Shb7wmNdvJ_lJe90KTII7vbQ&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "The Matrix",
                "Film",
                "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Pulp Fiction",
                "Film",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "The Godfather",
                "Film",
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "The Shawshank Redemption",
                "Film",
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Oppenheimer",
                "Film",
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Dune: Part Two",
                "Film",
                "https://cdn.sinemalar.com/images/movie/274150/gallery/dune-2-1708088443.jpg"
        ));
        KATALOG.add(new KatalogEseri(
                "Forrest Gump",
                "Film",
                "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Gladiator",
                "Film",
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "The Lord of the Rings: The Fellowship of the Ring",
                "Film",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Spider Man Into The Spider Verse",
                "Film",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRo1VppUKcW40Nk0jSmXOACJM-a7KFJabQuOa26LEnmWA&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Whiplash",
                "Film",
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Parasite",
                "Film",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=300"
        ));

        // =====================================================================
        // POPÜLER DİZİLER (16 ADET)
        // =====================================================================
        KATALOG.add(new KatalogEseri(
                "Breaking Bad",
                "Dizi",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsaU70LeqrcOhtLvFbajq9O2ro87I7T-hZd91evM8ElQ&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Severance",
                "Dizi",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3g_n3G9-mp9z9vFv2OdhrdizmB6Bf4P6oo6FPQKTDzg&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Game of Thrones",
                "Dizi",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Stranger Things",
                "Dizi",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrPls9rZtuIWtDvESGz-JtttBHQE2O__xbeluI6oUADQ&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Chernobyl",
                "Dizi",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-EoF0OqwnI4ivHEhphaEd9sVrUxMsI5dsIkKRZoA7sg&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "The Sopranos",
                "Dizi",
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Succession",
                "Dizi",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFnbkrKBEJQGyNwpzto8ju7maYPQze_DJa7ktZZcsFSw&s=10"
        ));
        KATALOG.add(new KatalogEseri(
                "Dark",
                "Dizi",
                "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Better Call Saul",
                "Dizi",
                "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Sherlock",
                "Dizi",
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "The Last of Us",
                "Dizi",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Black Mirror",
                "Dizi",
                "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Fargo",
                "Dizi",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "Arcane",
                "Dizi",
                "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "True Detective",
                "Dizi",
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=300"
        ));
        KATALOG.add(new KatalogEseri(
                "The Wire",
                "Dizi",
                "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=300"
        ));
    }

    /**
     * Verilen anahtar kelimeyi içeren tüm film ve dizileri büyük/küçük harf
     * duyarsız ve Türkçe karakter uyumlu olarak filtreleyip döner.
     * 
     * @param kelime Kullanıcının arama kutusuna yazdığı metin
     * @return Eşleşen {@link KatalogEseri} nesnelerinin listesi
     */
    public static List<KatalogEseri> ara(String kelime) {
        List<KatalogEseri> sonuc = new ArrayList<>();
        if (kelime == null || kelime.trim().isEmpty()) {
            return sonuc;
        }

        String arananNormalize = harfNormalizeEt(kelime);

        for (KatalogEseri eser : KATALOG) {
            String eserAdiNormalize = harfNormalizeEt(eser.getAd());
            if (eserAdiNormalize.contains(arananNormalize)) {
                sonuc.add(eser);
            }
        }

        return sonuc;
    }

    /**
     * Tüm hazır kataloğu değiştirilemez (read-only) liste olarak döner.
     * 
     * @return Tüm katalog listesi
     */
    public static List<KatalogEseri> getTumKatalog() {
        return Collections.unmodifiableList(KATALOG);
    }

    /**
     * Türkçe ve İngilizce büyük/küçük harf dönüşüm hatalarını ('İ-i', 'I-ı' vb.)
     * engelleyen güçlü normalizasyon yardımcı metodu.
     * 
     * @param metin İşlenecek ham metin
     * @return Karşılaştırmaya hazır küçük harfli normalize metin
     */
    private static String harfNormalizeEt(String metin) {
        if (metin == null) {
            return "";
        }
        return metin.trim()
                .replace('İ', 'i')
                .replace('I', 'ı')
                .toLowerCase(Locale.forLanguageTag("tr-TR"))
                .replace('ı', 'i')
                .replace('ğ', 'g')
                .replace('ü', 'u')
                .replace('ş', 's')
                .replace('ö', 'o')
                .replace('ç', 'c');
    }
}
