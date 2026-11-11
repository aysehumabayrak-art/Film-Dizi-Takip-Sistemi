package com.takip.gui;

import com.takip.model.DosyaYoneticisi;
import com.takip.model.Film;
import com.takip.model.FilmYoneticisi;
import com.takip.model.Kullanici;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Ana Kullanıcı Arayüzü (GUI)
 * 
 * Bu sınıf, uygulamanın sekmeli/sayfalı (CardLayout Mimari) ana penceresidir.
 * 
 * Yeni Özellikler (v5.5 - Kullanıcı Bazlı Veri Kalıcılığı ve Oturum Mimarisi):
 * 1. Oturum & Veri Kalıcılığı (DosyaYoneticisi):
 *    - Her kullanıcı kendi adına özel dosya ('veriler_{kullaniciAdi}.txt') üzerinden izole çalışır.
 *    - Kullanıcı tekrar giriş yaptığında izledikleri, puanları, yorumları ve listeleri eksiksiz geri yüklenir.
 *    - İlk defa giriş yapan kullanıcılara popüler keşfet kataloğu atanır ve yeni profil dosyası oluşturulur.
 * 2. Çıkış Yap & Kapatma Anında Otomatik Kayıt:
 *    - [Çıkış Yap] butonuna basıldığında oturum verileri anında kaydedilir ve kullanıcı bilgilendirilir.
 *    - Pencerenin sağ üstündeki kırmızı "X" butonuna basıldığında 'windowClosing' yakalanarak veriler sessizce otomatik kaydedilir.
 * 3. Hızlı Kart Etkileşim Butonları:
 *    - [✓ İzledim]: Yıldız puanlama ve yorum modal penceresini açar; yapımı "İzlediklerim" listesine ekler.
 *    - [➕ İzlemek İstiyorum]: Tek tıkla yapımı "Sonra İzlenecekler" listesine ekler.
 * 4. Boş Liste (Empty State) ve Dinamik Rozet Yönetimi.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 5.5 (Veri Kalıcılığı ve Kullanıcı İzolasyonu)
 */
public class AnaPencere extends JFrame {

    private static final long serialVersionUID = 1L;

    // =========================================================================
    // KART TANIMLARI (CARDLAYOUT SABİTLERİ)
    // =========================================================================
    public static final String KART_TUMU = "TUMU";
    public static final String KART_IZLENEN_TUMU = "IZLENEN_TUMU";
    public static final String KART_IZLENEN_FILMLER = "IZLENEN_FILMLER";
    public static final String KART_IZLENEN_DIZILER = "IZLENEN_DIZILER";
    public static final String KART_IZLENECEKLER = "IZLENECEKLER";

    // =========================================================================
    // RENK VE STİL SABİTLERİ
    // =========================================================================
    private static final Color RENK_ARKA_PLAN = new Color(245, 247, 250);
    private static final Color RENK_KART_BEYAZ = Color.WHITE;
    private static final Color RENK_KENARLIK = new Color(222, 226, 230);

    private static final Color RENK_EKLE_BG = new Color(13, 110, 253);
    private static final Color RENK_EKLE_BORDER = new Color(11, 94, 215);

    private static final Color RENK_SEKME_AKTIF_BG = new Color(25, 118, 210);
    private static final Color RENK_SEKME_AKTIF_BORDER = new Color(21, 101, 192);
    private static final Color RENK_SEKME_AKTIF_FG = Color.WHITE;
    private static final Color RENK_SEKME_PASIF_BG = new Color(241, 243, 245);
    private static final Color RENK_SEKME_PASIF_FG = new Color(52, 58, 64);
    private static final Color RENK_SEKME_PASIF_BORDER = new Color(206, 212, 218);

    private static final Color RENK_SIL = new Color(220, 53, 69);
    private static final Color RENK_SPOILER_ARKAPLAN = new Color(255, 243, 205);
    private static final Color RENK_SPOILER_YAZI = new Color(133, 100, 4);
    private static final Color RENK_SPOILER_KENAR = new Color(255, 238, 186);

    // Afiş Boyut Sabitleri (100x140 px Sabit Poster Çerçevesi)
    private static final int AFIS_GENISLIK = 100;
    private static final int AFIS_YUKSEKLIK = 140;

    // =========================================================================
    // İŞ MANTIĞI VE OTURUM NESNELERİ
    // =========================================================================
    private final FilmYoneticisi filmYoneticisi;
    private final Kullanici aktifKullanici;

    // =========================================================================
    // GÖRÜNÜM VE CARDLAYOUT BİLEŞENLERİ
    // =========================================================================
    private CardLayout cardLayout;
    private JPanel panelKartKapsayici;
    private String aktifSekme = KART_TUMU;

    // 3 Ana Navigasyon Butonu ve Açılır Menü
    private JButton btnTumu;
    private JButton btnIzlediklerim;
    private JButton btnSonraIzlenecekler;
    private JPopupMenu popupIzlediklerim;

    private final Map<String, JPanel> listePanelleri = new HashMap<>();
    private JLabel lblDurumCubugu;

    // =========================================================================
    // YAPICI METOTLAR (CONSTRUCTORS)
    // =========================================================================
    public AnaPencere() {
        this(new Kullanici("admin", "admin@mail.com", "1234"));
    }

    public AnaPencere(Kullanici aktifKullanici) {
        this.aktifKullanici = (aktifKullanici != null)
                ? aktifKullanici
                : new Kullanici("Misafir", "misafir@mail.com", "");
        this.filmYoneticisi = new FilmYoneticisi();

        pencereAyarlariniYap();
        arayuzuInsaEt();

        // Kullanıcıya özel verileri diskten yükle veya ilk giriş ise yeni profil dosyası oluştur
        kullaniciVerileriniYukle();

        tumGorunumleriGuncelle();
    }

    public Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public FilmYoneticisi getFilmYoneticisi() {
        return filmYoneticisi;
    }

    /**
     * Pencere ayarlarını yapılandırır ve 'X' kapatma butonuna güvenli otomatik kayıt dinleyicisi bağlar.
     */
    private void pencereAyarlariniYap() {
        setTitle("Kişisel Film ve Dizi Takip Sistemi - Kullanıcı: " + aktifKullanici.getIsim());
        setSize(1140, 760);
        setMinimumSize(new Dimension(980, 640));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(RENK_ARKA_PLAN);

        // Pencerenin sağ üstündeki kırmızı "X" (kapatma) butonuna basıldığında otomatik kaydet
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pencereyiKapatVeKaydet();
            }
        });
    }

    /**
     * Aktif kullanıcının daha önce kaydedilmiş kişisel verilerini yükler.
     * Eğer kullanıcının daha önce oluşturulmuş bir veri dosyası yoksa (ilk giriş),
     * varsayılan popüler kataloğu yükler ve kullanıcı için yeni bir profil veri dosyası oluşturur.
     */
    private void kullaniciVerileriniYukle() {
        String kullaniciAdi = (aktifKullanici != null) ? aktifKullanici.getIsim() : "admin";

        if (DosyaYoneticisi.dosyaVarMi(kullaniciAdi)) {
            ArrayList<Film> kayitliFilmler = DosyaYoneticisi.verileriYukle(kullaniciAdi);
            if (kayitliFilmler != null && !kayitliFilmler.isEmpty()) {
                filmYoneticisi.tumunuTemizle();
                for (Film f : kayitliFilmler) {
                    filmYoneticisi.filmEkle(f);
                }
            } else {
                filmYoneticisi.varsayilanVerileriYukle();
                DosyaYoneticisi.verileriKaydet(kullaniciAdi, filmYoneticisi.getFilmler());
            }
        } else {
            // İlk giriş: varsayılan 10 adet nötr kataloğu yükle ve dosyayı ilk kez oluştur
            filmYoneticisi.varsayilanVerileriYukle();
            DosyaYoneticisi.verileriKaydet(kullaniciAdi, filmYoneticisi.getFilmler());
        }
    }

    /**
     * Aktif kullanıcının güncel izleme ve katalog verilerini yerel dosyaya kaydeder.
     * 
     * @param sessizMi true ise kullanıcıya diyalog kutusu göstermez (arka plan otomatik kaydı)
     * @return Kayıt başarılı ise true, aksi halde false
     */
    public boolean verileriKaydet(boolean sessizMi) {
        if (aktifKullanici == null || filmYoneticisi == null) {
            return false;
        }

        String kullaniciAdi = aktifKullanici.getIsim();
        boolean basarili = DosyaYoneticisi.verileriKaydet(kullaniciAdi, filmYoneticisi.getFilmler());

        if (basarili && !sessizMi) {
            JOptionPane.showMessageDialog(
                    this,
                    "Oturum verileriniz başarıyla kaydedildi.",
                    "Oturum Kaydedildi",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        return basarili;
    }

    /**
     * Pencerenin sağ üstündeki kırmızı "X" butonuna basıldığında verileri sessizce otomatik
     * kaydeder ve uygulamayı sonlandırır.
     */
    private void pencereyiKapatVeKaydet() {
        verileriKaydet(true); // Sessizce arka planda güvenle kaydet
        dispose();
        System.exit(0);
    }

    private void arayuzuInsaEt() {
        setLayout(new BorderLayout());

        // 1. ADIM: CardLayout ve Kart Kapsayıcıyı Öncelikli Başlat
        cardLayout = new CardLayout();
        panelKartKapsayici = new JPanel(cardLayout);
        panelKartKapsayici.setOpaque(false);
        panelKartKapsayici.setBorder(new EmptyBorder(15, 20, 10, 20));

        // 5 Görünüm Kartını Oluştur
        olusturSekmeKarti(KART_TUMU);
        olusturSekmeKarti(KART_IZLENEN_TUMU);
        olusturSekmeKarti(KART_IZLENEN_FILMLER);
        olusturSekmeKarti(KART_IZLENEN_DIZILER);
        olusturSekmeKarti(KART_IZLENECEKLER);

        // 2. ADIM: Üst Alan (Header + 3 Butonlu Sade Navigasyon Çubuğu)
        JPanel panelUstKombine = new JPanel(new BorderLayout());
        panelUstKombine.add(olusturHeaderPaneli(), BorderLayout.NORTH);
        panelUstKombine.add(olusturNavigasyonPaneli(), BorderLayout.SOUTH);
        add(panelUstKombine, BorderLayout.NORTH);

        // 3. ADIM: Orta Alan (CardLayout Panelini Merkeze Ekle)
        add(panelKartKapsayici, BorderLayout.CENTER);

        // 4. ADIM: Alt Alan (Status Bar)
        add(olusturDurumCubuguPaneli(), BorderLayout.SOUTH);

        // 5. ADIM: Başlangıç Sekme Seçimi
        sekmeSec(KART_TUMU);
    }

    private JPanel olusturHeaderPaneli() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JLabel lblBaslik = new JLabel("🎬 Film & Dizi Keşfet ve Arşiv Sistemi");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBaslik.setForeground(new Color(33, 37, 41));

        JLabel lblAlt = new JLabel("Popüler kataloğu keşfedin, izlediklerinizi puanlayın ve izleme listenizi yönetin.");
        lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlt.setForeground(new Color(108, 117, 125));

        JPanel panelMetin = new JPanel(new GridLayout(2, 1, 0, 2));
        panelMetin.setOpaque(false);
        panelMetin.add(lblBaslik);
        panelMetin.add(lblAlt);
        header.add(panelMetin, BorderLayout.WEST);

        // Kullanıcı Karşılama, İstatistik ve Çıkış Butonları
        JPanel panelKullanici = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelKullanici.setOpaque(false);

        JLabel lblHosgeldin = new JLabel("Kullanıcı: " + aktifKullanici.getIsim());
        lblHosgeldin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHosgeldin.setForeground(new Color(33, 37, 41));
        lblHosgeldin.setBackground(new Color(241, 243, 245));
        lblHosgeldin.setOpaque(true);
        lblHosgeldin.setBorder(new CompoundBorder(
                new LineBorder(new Color(222, 226, 230), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        panelKullanici.add(lblHosgeldin);

        // [Profil / İstatistikler] Butonu
        JButton btnIstatistik = new JButton("📊 Profil / İstatistikler");
        btnIstatistik.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIstatistik.setBackground(new Color(238, 242, 255));
        btnIstatistik.setForeground(new Color(67, 56, 202));
        btnIstatistik.setFocusPainted(false);
        btnIstatistik.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIstatistik.setBorder(new CompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnIstatistik.addActionListener(e -> {
            IstatistikDialog dialog = new IstatistikDialog(this, filmYoneticisi, aktifKullanici);
            dialog.setVisible(true);
        });
        panelKullanici.add(btnIstatistik);

        // [Çıkış Yap] Butonu
        JButton btnCikisYap = new JButton("Çıkış Yap");
        btnCikisYap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCikisYap.setBackground(new Color(254, 242, 242));
        btnCikisYap.setForeground(RENK_SIL);
        btnCikisYap.setFocusPainted(false);
        btnCikisYap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCikisYap.setBorder(new CompoundBorder(
                new LineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnCikisYap.addActionListener(e -> oturumuKapat());
        panelKullanici.add(btnCikisYap);

        header.add(panelKullanici, BorderLayout.EAST);
        return header;
    }

    /**
     * Sağ üstteki [Çıkış Yap] butonuna basıldığında oturumu otomatik kaydeder,
     * kullanıcıya bildirim gösterir ve Giriş Ekranına döner.
     */
    private void oturumuKapat() {
        int secim = JOptionPane.showConfirmDialog(
                this,
                "Oturumunuzu kapatmak ve giriş ekranına dönmek istediğinize emin misiniz?",
                "Oturumu Kapat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (secim == JOptionPane.YES_OPTION) {
            // 1. Aktif kullanıcının güncel listesini otomatik kaydet ve bildirim ver
            verileriKaydet(false);

            // 2. Ana pencereyi kapat ve Giriş Ekranını aç
            dispose();
            SwingUtilities.invokeLater(() -> {
                GirisEkrani giris = new GirisEkrani();
                giris.setVisible(true);
            });
        }
    }

    // =========================================================================
    // SADELEŞTİRİLMİŞ NAVİGASYON PANELİ (3 ANA BUTON + AÇILIR MENÜ)
    // =========================================================================
    private JPanel olusturNavigasyonPaneli() {
        JPanel navBar = new JPanel(new BorderLayout(15, 0));
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel panelSekmeler = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelSekmeler.setOpaque(false);

        // 1. [Tümü / Ana Sayfa] Butonu
        btnTumu = olusturNavButon("Tümü / Ana Sayfa");
        btnTumu.addActionListener(e -> sekmeSec(KART_TUMU));
        panelSekmeler.add(btnTumu);

        // 2. [İzlediklerim ▼] Açılır Menülü (Dropdown) Buton
        popupIzlediklerim = olusturIzlediklerimPopup();
        btnIzlediklerim = olusturNavButon("İzlediklerim ▼");
        btnIzlediklerim.addActionListener(e -> {
            popupIzlediklerim.show(btnIzlediklerim, 0, btnIzlediklerim.getHeight() + 4);
        });
        panelSekmeler.add(btnIzlediklerim);

        // 3. [Sonra İzlenecekler] Butonu
        btnSonraIzlenecekler = olusturNavButon("Sonra İzlenecekler");
        btnSonraIzlenecekler.addActionListener(e -> sekmeSec(KART_IZLENECEKLER));
        panelSekmeler.add(btnSonraIzlenecekler);

        navBar.add(panelSekmeler, BorderLayout.CENTER);

        // Sağ Köşe: [+ Yeni Film / Dizi Ekle] Butonu
        JButton btnYeniEser = new JButton("+ Yeni Film / Dizi Ekle");
        btnYeniEser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnYeniEser.setBackground(new Color(34, 197, 94));
        btnYeniEser.setForeground(Color.WHITE);
        btnYeniEser.setOpaque(true);
        btnYeniEser.setContentAreaFilled(true);
        btnYeniEser.setFocusPainted(false);
        btnYeniEser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYeniEser.setBorder(new CompoundBorder(
                new LineBorder(new Color(22, 163, 74), 1, true),
                new EmptyBorder(9, 18, 9, 18)
        ));

        btnYeniEser.addActionListener(e -> yeniEserEkleDialogunuAc());
        navBar.add(btnYeniEser, BorderLayout.EAST);

        return navBar;
    }

    /**
     * [İzlediklerim ▼] butonu için modern açılır menüyü (JPopupMenu) oluşturur.
     */
    private JPopupMenu olusturIzlediklerimPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);
        popup.setBorder(new CompoundBorder(
                new LineBorder(new Color(206, 212, 218), 1, true),
                new EmptyBorder(4, 4, 4, 4)
        ));

        JMenuItem itemTumu = olusturMenuItem("Tüm İzlediklerim", KART_IZLENEN_TUMU);
        JMenuItem itemFilmler = olusturMenuItem("İzlenen Filmler", KART_IZLENEN_FILMLER);
        JMenuItem itemDiziler = olusturMenuItem("İzlenen Diziler", KART_IZLENEN_DIZILER);

        popup.add(itemTumu);
        popup.add(itemFilmler);
        popup.add(itemDiziler);

        return popup;
    }

    private JMenuItem olusturMenuItem(String baslik, String kartAdi) {
        JMenuItem item = new JMenuItem(baslik);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(Color.WHITE);
        item.setForeground(new Color(33, 37, 41));
        item.setBorder(new EmptyBorder(8, 14, 8, 14));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.addActionListener(e -> sekmeSec(kartAdi));
        return item;
    }

    private JButton olusturNavButon(String baslik) {
        JButton btn = new JButton(baslik);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        butonPasifAyarla(btn);
        return btn;
    }

    private void sekmeSec(String kartAdi) {
        this.aktifSekme = kartAdi;

        butonPasifAyarla(btnTumu);
        butonPasifAyarla(btnIzlediklerim);
        butonPasifAyarla(btnSonraIzlenecekler);

        if (kartAdi.equals(KART_TUMU)) {
            butonAktifAyarla(btnTumu);
            btnIzlediklerim.setText("İzlediklerim ▼");
        } else if (kartAdi.equals(KART_IZLENECEKLER)) {
            butonAktifAyarla(btnSonraIzlenecekler);
            btnIzlediklerim.setText("İzlediklerim ▼");
        } else if (kartAdi.equals(KART_IZLENEN_TUMU)) {
            butonAktifAyarla(btnIzlediklerim);
            btnIzlediklerim.setText("Tüm İzlediklerim ▼");
        } else if (kartAdi.equals(KART_IZLENEN_FILMLER)) {
            butonAktifAyarla(btnIzlediklerim);
            btnIzlediklerim.setText("İzlenen Filmler ▼");
        } else if (kartAdi.equals(KART_IZLENEN_DIZILER)) {
            butonAktifAyarla(btnIzlediklerim);
            btnIzlediklerim.setText("İzlenen Diziler ▼");
        }

        filtreliListeyiGuncelle(kartAdi);

        if (cardLayout != null && panelKartKapsayici != null) {
            cardLayout.show(panelKartKapsayici, kartAdi);
        }
    }

    private void butonAktifAyarla(JButton btn) {
        if (btn == null) return;
        btn.setBackground(RENK_SEKME_AKTIF_BG);
        btn.setForeground(RENK_SEKME_AKTIF_FG);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new CompoundBorder(
                new LineBorder(RENK_SEKME_AKTIF_BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
    }

    private void butonPasifAyarla(JButton btn) {
        if (btn == null) return;
        btn.setBackground(RENK_SEKME_PASIF_BG);
        btn.setForeground(RENK_SEKME_PASIF_FG);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorder(new CompoundBorder(
                new LineBorder(RENK_SEKME_PASIF_BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
    }

    private void olusturSekmeKarti(String kartAdi) {
        JPanel icerikPaneli = new JPanel();
        icerikPaneli.setLayout(new BoxLayout(icerikPaneli, BoxLayout.Y_AXIS));
        icerikPaneli.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(icerikPaneli);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        listePanelleri.put(kartAdi, icerikPaneli);
        panelKartKapsayici.add(scrollPane, kartAdi);
    }

    private void filtreliListeyiGuncelle(String kartAdi) {
        JPanel panel = listePanelleri.get(kartAdi);
        if (panel == null) return;

        panel.removeAll();

        ArrayList<Film> filtrelenmisListe;
        String bosDurumIkon;
        String bosDurumBaslik;
        String bosDurumAciklama;

        switch (kartAdi) {
            case KART_IZLENEN_TUMU:
                filtrelenmisListe = filmYoneticisi.getIzlenenler();
                bosDurumIkon = "📭";
                bosDurumBaslik = "Henüz izlenmiş bir yapım bulunmuyor.";
                bosDurumAciklama = "Ana sayfadaki yapımlardan [✓ İzledim] butonuna basarak veya yukarıdaki '+ Yeni Film / Dizi Ekle' butonuyla izlediklerinizi ekleyebilirsiniz.";
                break;
            case KART_IZLENEN_FILMLER:
                filtrelenmisListe = filmYoneticisi.getIzlenenFilmler();
                bosDurumIkon = "🎬";
                bosDurumBaslik = "İzlenmiş film yok.";
                bosDurumAciklama = "";
                break;
            case KART_IZLENEN_DIZILER:
                filtrelenmisListe = filmYoneticisi.getIzlenenDiziler();
                bosDurumIkon = "📺";
                bosDurumBaslik = "İzlenmiş dizi yok.";
                bosDurumAciklama = "";
                break;
            case KART_IZLENECEKLER:
                filtrelenmisListe = filmYoneticisi.getSonraIzlenecekler();
                bosDurumIkon = "📌";
                bosDurumBaslik = "İzlemek için kaydedilen yapım yok.";
                bosDurumAciklama = "Daha sonra izlemek istediğiniz film ve dizileri ana sayfadaki [➕ İzlemek İstiyorum] butonunu kullanarak listenize ekleyebilirsiniz.";
                break;
            case KART_TUMU:
            default:
                filtrelenmisListe = filmYoneticisi.getFilmler();
                bosDurumIkon = "📭";
                bosDurumBaslik = "Arşivinizde henüz kayıtlı film veya dizi bulunmamaktadır.";
                bosDurumAciklama = "Yukarıdaki '+ Yeni Film / Dizi Ekle' butonunu kullanarak ilk yapımınızı ekleyebilirsiniz.";
                break;
        }

        if (filtrelenmisListe.isEmpty()) {
            panel.add(Box.createVerticalStrut(70));
            panel.add(olusturBosDurumPaneli(bosDurumIkon, bosDurumBaslik, bosDurumAciklama));
            panel.add(Box.createVerticalGlue());
        } else {
            for (int i = filtrelenmisListe.size() - 1; i >= 0; i--) {
                Film film = filtrelenmisListe.get(i);
                JPanel kart = olusturFilmKarti(film);
                panel.add(kart);
                panel.add(Box.createVerticalStrut(12));
            }
            panel.add(Box.createVerticalGlue());
        }

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Boş liste durumunda ekranın ortasında gösterilen zarif ve modern Empty State paneli.
     */
    private JPanel olusturBosDurumPaneli(String ikon, String baslik, String aciklama) {
        JPanel kapsayici = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        kapsayici.setOpaque(false);
        kapsayici.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelKutu = new JPanel();
        panelKutu.setLayout(new BoxLayout(panelKutu, BoxLayout.Y_AXIS));
        panelKutu.setBackground(new Color(248, 249, 250)); // Açık gri zemin
        panelKutu.setBorder(new CompoundBorder(
                new LineBorder(new Color(222, 226, 230), 1, true),
                new EmptyBorder(34, 48, 34, 48)
        ));
        panelKutu.setPreferredSize(new Dimension(620, (aciklama != null && !aciklama.trim().isEmpty()) ? 180 : 150));
        panelKutu.setMaximumSize(new Dimension(700, (aciklama != null && !aciklama.trim().isEmpty()) ? 200 : 170));

        JLabel lblIkon = new JLabel(ikon);
        lblIkon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        lblIkon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelKutu.add(lblIkon);
        panelKutu.add(Box.createVerticalStrut(14));

        JLabel lblBaslik = new JLabel(baslik);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBaslik.setForeground(new Color(51, 65, 85)); // Soluk gri / antrasit renk
        lblBaslik.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelKutu.add(lblBaslik);

        if (aciklama != null && !aciklama.trim().isEmpty()) {
            panelKutu.add(Box.createVerticalStrut(8));
            JLabel lblAciklama = new JLabel(aciklama);
            lblAciklama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblAciklama.setForeground(new Color(100, 116, 139)); // Soluk gri renk
            lblAciklama.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelKutu.add(lblAciklama);
        }

        kapsayici.add(panelKutu);
        return kapsayici;
    }

    /**
     * Film veya Dizi kartını üretir.
     * 
     * Sağ panelde (EAST):
     * - Nötr ise: [✓ İzledim] ve [➕ İzlemek İstiyorum] butonları
     * - İzlendi ise: [✓ İzlendi (X/5)] rozeti, [⭐ Puanı Düzenle] ve [✕ Listeden Çıkar] butonları
     * - İzlenecek ise: [📌 Listede] rozeti, [✓ Şimdi İzledim] ve [✕ Listeden Çıkar] butonları
     */
    private JPanel olusturFilmKarti(Film film) {
        JPanel kart = new JPanel(new BorderLayout(14, 0));
        kart.setBackground(RENK_KART_BEYAZ);
        kart.setBorder(new CompoundBorder(
                new LineBorder(RENK_KENARLIK, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        kart.setPreferredSize(new Dimension(850, 160));
        kart.setMinimumSize(new Dimension(650, 160));
        kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // =====================================================================
        // SOL ALAN (WEST): 100x140 AFİŞ POSTER BİLEŞENİ
        // =====================================================================
        JPanel panelWest = new JPanel(new BorderLayout());
        panelWest.setOpaque(false);
        panelWest.setPreferredSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));
        panelWest.setMinimumSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));
        panelWest.setMaximumSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));

        JLabel lblAfis = new JLabel();
        lblAfis.setPreferredSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));
        lblAfis.setMinimumSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));
        lblAfis.setMaximumSize(new Dimension(AFIS_GENISLIK, AFIS_YUKSEKLIK));
        lblAfis.setHorizontalAlignment(SwingConstants.CENTER);
        lblAfis.setVerticalAlignment(SwingConstants.CENTER);
        lblAfis.setBorder(new LineBorder(new Color(222, 226, 230), 1, true));

        varsayilanYerTutucuAyarla(lblAfis);

        if (film.getAfisUrl() != null && !film.getAfisUrl().trim().isEmpty()) {
            afisiAsenkronYukle(film.getAfisUrl().trim(), lblAfis);
        }

        panelWest.add(lblAfis, BorderLayout.CENTER);
        kart.add(panelWest, BorderLayout.WEST);

        // =====================================================================
        // SAĞ ALAN (EAST): HIZLI ETKİLEŞİM VE YÖNETİM BUTONLARI
        // =====================================================================
        JPanel panelEast = new JPanel();
        panelEast.setLayout(new BoxLayout(panelEast, BoxLayout.Y_AXIS));
        panelEast.setOpaque(false);
        panelEast.setPreferredSize(new Dimension(175, AFIS_YUKSEKLIK));
        panelEast.setMinimumSize(new Dimension(175, AFIS_YUKSEKLIK));
        panelEast.setMaximumSize(new Dimension(175, AFIS_YUKSEKLIK));
        panelEast.setBorder(new EmptyBorder(2, 6, 2, 0));

        if (film.isNotr()) {
            // DURUM 1: NÖTR (KEŞFET KATALOĞU)
            JButton btnIzledim = new JButton("✓ İzledim");
            btnIzledim.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnIzledim.setBackground(new Color(236, 253, 245));
            btnIzledim.setForeground(new Color(4, 120, 87));
            btnIzledim.setBorder(new CompoundBorder(
                    new LineBorder(new Color(167, 243, 208), 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            btnIzledim.setFocusPainted(false);
            btnIzledim.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnIzledim.setMaximumSize(new Dimension(165, 34));
            btnIzledim.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnIzledim.setToolTipText("Bu yapımı izledim olarak işaretle ve puanla");
            btnIzledim.addActionListener(e -> hizliDegerlendirmeDialogunuAc(film));

            JButton btnIzlemekIstiyorum = new JButton("➕ İzlemek İstiyorum");
            btnIzlemekIstiyorum.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnIzlemekIstiyorum.setBackground(new Color(239, 246, 255));
            btnIzlemekIstiyorum.setForeground(new Color(29, 78, 216));
            btnIzlemekIstiyorum.setBorder(new CompoundBorder(
                    new LineBorder(new Color(191, 219, 254), 1, true),
                    new EmptyBorder(6, 10, 6, 10)
            ));
            btnIzlemekIstiyorum.setFocusPainted(false);
            btnIzlemekIstiyorum.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnIzlemekIstiyorum.setMaximumSize(new Dimension(165, 34));
            btnIzlemekIstiyorum.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnIzlemekIstiyorum.setToolTipText("Sonra izlenecekler listenize ekleyin");
            btnIzlemekIstiyorum.addActionListener(e -> {
                film.durumuIzlenecekYap();
                verileriKaydet(true); // Değişikliği anında arka planda kaydet
                tumGorunumleriGuncelle();
                JOptionPane.showMessageDialog(
                        this,
                        "'" + film.getAd() + "' sonra izlenecekler listenize eklendi.",
                        "Listeye Eklendi",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });

            panelEast.add(Box.createVerticalGlue());
            panelEast.add(btnIzledim);
            panelEast.add(Box.createVerticalStrut(8));
            panelEast.add(btnIzlemekIstiyorum);
            panelEast.add(Box.createVerticalGlue());

        } else if (film.isIzlendi()) {
            // DURUM 2: İZLENDİ (KULLANICI DEĞERLENDİRDİ)
            JLabel lblRozet = new JLabel("✓ İzlendi (" + film.getPuan() + "/5)");
            lblRozet.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblRozet.setForeground(new Color(21, 128, 61));
            lblRozet.setBackground(new Color(220, 252, 231));
            lblRozet.setOpaque(true);
            lblRozet.setHorizontalAlignment(SwingConstants.CENTER);
            lblRozet.setBorder(new CompoundBorder(
                    new LineBorder(new Color(187, 247, 208), 1, true),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            lblRozet.setMaximumSize(new Dimension(165, 28));
            lblRozet.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btnDuzenle = new JButton("⭐ Puanı Düzenle");
            btnDuzenle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnDuzenle.setBackground(new Color(248, 250, 252));
            btnDuzenle.setForeground(new Color(71, 85, 105));
            btnDuzenle.setBorder(new CompoundBorder(
                    new LineBorder(new Color(203, 213, 225), 1, true),
                    new EmptyBorder(4, 6, 4, 6)
            ));
            btnDuzenle.setFocusPainted(false);
            btnDuzenle.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDuzenle.setMaximumSize(new Dimension(165, 26));
            btnDuzenle.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnDuzenle.addActionListener(e -> hizliDegerlendirmeDialogunuAc(film));

            JButton btnKaldir = new JButton("✕ Listeden Çıkar");
            btnKaldir.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnKaldir.setBackground(new Color(254, 242, 242));
            btnKaldir.setForeground(new Color(220, 38, 38));
            btnKaldir.setBorder(new CompoundBorder(
                    new LineBorder(new Color(254, 202, 202), 1, true),
                    new EmptyBorder(4, 6, 4, 6)
            ));
            btnKaldir.setFocusPainted(false);
            btnKaldir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnKaldir.setMaximumSize(new Dimension(165, 26));
            btnKaldir.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnKaldir.addActionListener(e -> listedenCikar(film));

            panelEast.add(Box.createVerticalGlue());
            panelEast.add(lblRozet);
            panelEast.add(Box.createVerticalStrut(6));
            panelEast.add(btnDuzenle);
            panelEast.add(Box.createVerticalStrut(6));
            panelEast.add(btnKaldir);
            panelEast.add(Box.createVerticalGlue());

        } else if (film.isIzlenecek()) {
            // DURUM 3: İZLENECEKLER LİSTESİNDE
            JLabel lblRozet = new JLabel("📌 Listede");
            lblRozet.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblRozet.setForeground(new Color(180, 83, 9));
            lblRozet.setBackground(new Color(254, 243, 199));
            lblRozet.setOpaque(true);
            lblRozet.setHorizontalAlignment(SwingConstants.CENTER);
            lblRozet.setBorder(new CompoundBorder(
                    new LineBorder(new Color(253, 230, 138), 1, true),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            lblRozet.setMaximumSize(new Dimension(165, 28));
            lblRozet.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btnSimdiIzledim = new JButton("✓ Şimdi İzledim");
            btnSimdiIzledim.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSimdiIzledim.setBackground(new Color(236, 253, 245));
            btnSimdiIzledim.setForeground(new Color(4, 120, 87));
            btnSimdiIzledim.setBorder(new CompoundBorder(
                    new LineBorder(new Color(167, 243, 208), 1, true),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            btnSimdiIzledim.setFocusPainted(false);
            btnSimdiIzledim.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnSimdiIzledim.setMaximumSize(new Dimension(165, 28));
            btnSimdiIzledim.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSimdiIzledim.addActionListener(e -> hizliDegerlendirmeDialogunuAc(film));

            JButton btnKaldir = new JButton("✕ Listeden Çıkar");
            btnKaldir.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnKaldir.setBackground(new Color(254, 242, 242));
            btnKaldir.setForeground(new Color(220, 38, 38));
            btnKaldir.setBorder(new CompoundBorder(
                    new LineBorder(new Color(254, 202, 202), 1, true),
                    new EmptyBorder(4, 6, 4, 6)
            ));
            btnKaldir.setFocusPainted(false);
            btnKaldir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnKaldir.setMaximumSize(new Dimension(165, 26));
            btnKaldir.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnKaldir.addActionListener(e -> listedenCikar(film));

            panelEast.add(Box.createVerticalGlue());
            panelEast.add(lblRozet);
            panelEast.add(Box.createVerticalStrut(6));
            panelEast.add(btnSimdiIzledim);
            panelEast.add(Box.createVerticalStrut(6));
            panelEast.add(btnKaldir);
            panelEast.add(Box.createVerticalGlue());
        }

        kart.add(panelEast, BorderLayout.EAST);

        // =====================================================================
        // ORTA ALAN (CENTER): BİLGİ VE YORUMLAR
        // =====================================================================
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
        panelCenter.setOpaque(false);
        panelCenter.setBorder(new EmptyBorder(2, 6, 2, 6));

        // 1. Satır: Başlık ve Tür Rozeti
        JPanel panelBaslikSatiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBaslikSatiri.setOpaque(false);
        panelBaslikSatiri.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBaslik = new JLabel(film.getAd());
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBaslik.setForeground(new Color(33, 37, 41));
        panelBaslikSatiri.add(lblBaslik);

        JLabel lblTurRozet = new JLabel(film.getTurIsmi());
        lblTurRozet.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (film.isDizi()) {
            lblTurRozet.setForeground(new Color(13, 110, 253));
            lblTurRozet.setBackground(new Color(231, 241, 255));
            lblTurRozet.setBorder(new CompoundBorder(
                new LineBorder(new Color(182, 212, 255), 1, true),
                new EmptyBorder(2, 8, 2, 8)
            ));
        } else {
            lblTurRozet.setForeground(new Color(102, 16, 242));
            lblTurRozet.setBackground(new Color(243, 232, 255));
            lblTurRozet.setBorder(new CompoundBorder(
                new LineBorder(new Color(216, 187, 255), 1, true),
                new EmptyBorder(2, 8, 2, 8)
            ));
        }
        lblTurRozet.setOpaque(true);
        panelBaslikSatiri.add(lblTurRozet);
        panelCenter.add(panelBaslikSatiri);

        panelCenter.add(Box.createVerticalStrut(4));

        // 2. Satır: Durum ve Puan
        JPanel panelBilgiSatiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBilgiSatiri.setOpaque(false);
        panelBilgiSatiri.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDurum = new JLabel();
        lblDurum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (film.isIzlendi()) {
            lblDurum.setText("Durum: ✓ İzlendi");
            lblDurum.setForeground(new Color(22, 163, 74));
        } else if (film.isIzlenecek()) {
            lblDurum.setText("Durum: 📌 Sonra İzlenecek");
            lblDurum.setForeground(new Color(217, 119, 6));
        } else {
            lblDurum.setText("Durum: Keşfet (Listede Değil)");
            lblDurum.setForeground(new Color(100, 116, 139));
        }
        panelBilgiSatiri.add(lblDurum);

        if (film.isIzlendi() && film.getPuan() > 0) {
            JLabel lblPuan = new JLabel("Puan: " + yildizMetniUret(film.getPuan()) + " (" + film.getPuan() + "/5)");
            lblPuan.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblPuan.setForeground(new Color(217, 119, 6));
            panelBilgiSatiri.add(lblPuan);
        }

        panelCenter.add(panelBilgiSatiri);
        panelCenter.add(Box.createVerticalStrut(4));

        // 3. Satır: Kullanıcı Değerlendirmesi / Yorum Alanı
        String yorumMetni = (film.getYorum() != null && !film.getYorum().trim().isEmpty())
                ? film.getYorum().trim()
                : "Bu " + (film.isDizi() ? "dizi" : "film") + " için henüz bir kullanıcı yorumu girilmemiş.";

        JTextArea txtYorumGorunumu = new JTextArea(yorumMetni);
        txtYorumGorunumu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtYorumGorunumu.setForeground(new Color(73, 80, 87));
        txtYorumGorunumu.setLineWrap(true);
        txtYorumGorunumu.setWrapStyleWord(true);
        txtYorumGorunumu.setEditable(false);
        txtYorumGorunumu.setOpaque(false);

        JScrollPane scrollYorum = new JScrollPane(txtYorumGorunumu);
        scrollYorum.setBorder(null);
        scrollYorum.setOpaque(false);
        scrollYorum.getViewport().setOpaque(false);
        scrollYorum.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollYorum.setPreferredSize(new Dimension(500, 48));
        scrollYorum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        // Spoiler Mantığı
        if (film.isSpoilerMi() && film.getYorum() != null && !film.getYorum().trim().isEmpty()) {
            JPanel panelSpoilerKapsayici = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panelSpoilerKapsayici.setOpaque(false);
            panelSpoilerKapsayici.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnSpoilerGoster = new JButton("⚠️ Bu yorum sürprizbozan (spoiler) içeriyor. Görüntülemek için tıklayın.");
            btnSpoilerGoster.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnSpoilerGoster.setBackground(RENK_SPOILER_ARKAPLAN);
            btnSpoilerGoster.setForeground(RENK_SPOILER_YAZI);
            btnSpoilerGoster.setBorder(new CompoundBorder(
                    new LineBorder(RENK_SPOILER_KENAR, 1, true),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            btnSpoilerGoster.setFocusPainted(false);
            btnSpoilerGoster.setCursor(new Cursor(Cursor.HAND_CURSOR));

            scrollYorum.setVisible(false);

            btnSpoilerGoster.addActionListener(e -> {
                btnSpoilerGoster.setVisible(false);
                scrollYorum.setVisible(true);
                panelCenter.revalidate();
                panelCenter.repaint();
            });

            panelCenter.add(btnSpoilerGoster);
            panelCenter.add(scrollYorum);
        } else {
            panelCenter.add(scrollYorum);
        }

        kart.add(panelCenter, BorderLayout.CENTER);
        return kart;
    }

    /**
     * Kullanıcının yapımı kişisel listesinden çıkarıp genel keşfet kataloğuna nötr olarak döndürmesini sağlar.
     */
    private void listedenCikar(Film film) {
        String turIsmiBelirtme = film.getTurIsmiBelirtmeHali(); // "filmi" veya "diziyi"
        int secim = JOptionPane.showConfirmDialog(
                this,
                "'" + film.getAd() + "' adlı " + turIsmiBelirtme + " kişisel listenizden çıkarmak istediğinize emin misiniz?\n(Yapım genel keşfet kataloğunda korunacaktır)",
                "Listeden Çıkar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (secim == JOptionPane.YES_OPTION) {
            film.durumuNotrYap();
            verileriKaydet(true); // Değişikliği anında dosyaya kaydet
            tumGorunumleriGuncelle();
            JOptionPane.showMessageDialog(
                    this,
                    "'" + film.getAd() + "' kişisel listenizden çıkarıldı ve keşfet durumuna alındı.",
                    "Bilgi",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * [✓ İzledim] butonuna basıldığında açılan modern, küçük Puanlama ve Yorum modal penceresi.
     */
    private void hizliDegerlendirmeDialogunuAc(Film film) {
        JDialog dialog = new JDialog(this, "Yapımı Değerlendir - " + film.getAd(), true);
        dialog.setSize(440, 420);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Üst Başlık Paneli
        JPanel panelBaslik = new JPanel(new GridLayout(2, 1, 0, 4));
        panelBaslik.setBackground(Color.WHITE);
        panelBaslik.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblSoru = new JLabel("Bu yapımı izlediniz! Kaç puan verirsiniz?");
        lblSoru.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSoru.setForeground(new Color(30, 41, 59));

        JLabel lblEser = new JLabel(film.getAd() + " (" + film.getTurIsmi() + ")");
        lblEser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEser.setForeground(new Color(13, 110, 253));

        panelBaslik.add(lblSoru);
        panelBaslik.add(lblEser);
        dialog.add(panelBaslik, BorderLayout.NORTH);

        // Orta Gövde
        JPanel panelGövde = new JPanel();
        panelGövde.setLayout(new BoxLayout(panelGövde, BoxLayout.Y_AXIS));
        panelGövde.setBackground(Color.WHITE);
        panelGövde.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Yıldız Puanlama
        JLabel lblYildizBaslik = new JLabel("Puanınız (1 - 5 Yıldız): *");
        lblYildizBaslik.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblYildizBaslik.setForeground(new Color(71, 85, 105));
        lblYildizBaslik.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(lblYildizBaslik);
        panelGövde.add(Box.createVerticalStrut(6));

        JPanel panelYildizlar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelYildizlar.setOpaque(false);
        panelYildizlar.setAlignmentX(Component.LEFT_ALIGNMENT);

        final Color RENK_YILDIZ_DOLU = new Color(255, 180, 0); // Canlı altın sarısı
        final Color RENK_YILDIZ_BOS = new Color(210, 215, 220);  // Açık gri
        final Font FONT_YILDIZ = new Font("Segoe UI Symbol", Font.PLAIN, 26);

        final int[] seciliPuan = {film.getPuan() > 0 ? film.getPuan() : 5};
        JButton[] yildizButonlari = new JButton[5];
        JLabel lblPuanGosterge = new JLabel(seciliPuan[0] + " / 5");
        lblPuanGosterge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPuanGosterge.setForeground(new Color(217, 119, 6));
        lblPuanGosterge.setBorder(new EmptyBorder(0, 10, 0, 0));

        java.util.function.Consumer<Integer> yildizlariYenile = (gorunenPuan) -> {
            for (int j = 0; j < 5; j++) {
                if (j < gorunenPuan) {
                    yildizButonlari[j].setText("\u2605"); // Dolu altın yıldız
                    yildizButonlari[j].setForeground(RENK_YILDIZ_DOLU);
                } else {
                    yildizButonlari[j].setText("\u2606"); // Boş gri yıldız
                    yildizButonlari[j].setForeground(RENK_YILDIZ_BOS);
                }
            }
            if (gorunenPuan > 0) {
                lblPuanGosterge.setText(gorunenPuan + " / 5");
            } else {
                lblPuanGosterge.setText("Puan seçilmedi");
            }
        };

        for (int i = 0; i < 5; i++) {
            final int puanDegeri = i + 1;
            JButton btn = new JButton("\u2605");
            btn.setFont(FONT_YILDIZ);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            btn.setPreferredSize(new Dimension(38, 38));
            btn.setMinimumSize(new Dimension(38, 38));
            btn.setMaximumSize(new Dimension(38, 38));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(puanDegeri + " Yıldız");

            // Tıklama ile kalıcı puan seçimi
            btn.addActionListener(e -> {
                seciliPuan[0] = puanDegeri;
                yildizlariYenile.accept(seciliPuan[0]);
            });

            // Hover (Fare Üzerine Gelme / Çekilme)
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    yildizlariYenile.accept(puanDegeri);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    yildizlariYenile.accept(seciliPuan[0]);
                }
            });

            yildizButonlari[i] = btn;
            panelYildizlar.add(btn);
        }
        panelYildizlar.add(lblPuanGosterge);
        panelGövde.add(panelYildizlar);
        yildizlariYenile.accept(seciliPuan[0]);

        panelGövde.add(Box.createVerticalStrut(12));

        // Yorum Alanı
        JLabel lblYorumBaslik = new JLabel("Kısa Değerlendirmeniz / Yorumunuz (İsteğe bağlı):");
        lblYorumBaslik.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblYorumBaslik.setForeground(new Color(71, 85, 105));
        lblYorumBaslik.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(lblYorumBaslik);
        panelGövde.add(Box.createVerticalStrut(6));

        JTextArea txtYorum = new JTextArea(3, 20);
        txtYorum.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtYorum.setLineWrap(true);
        txtYorum.setWrapStyleWord(true);
        if (film.getYorum() != null && !film.getYorum().trim().isEmpty()) {
            txtYorum.setText(film.getYorum());
        }

        JScrollPane scrollYorum = new JScrollPane(txtYorum);
        scrollYorum.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollYorum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        panelGövde.add(scrollYorum);
        panelGövde.add(Box.createVerticalStrut(8));

        JCheckBox chkSpoiler = new JCheckBox("Bu yorum sürprizbozan (spoiler) içeriyor");
        chkSpoiler.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSpoiler.setOpaque(false);
        chkSpoiler.setSelected(film.isSpoilerMi());
        chkSpoiler.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(chkSpoiler);

        dialog.add(panelGövde, BorderLayout.CENTER);

        // Alt Butonlar
        JPanel panelAlt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelAlt.setBackground(new Color(248, 250, 252));
        panelAlt.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, RENK_KENARLIK));

        JButton btnIptal = new JButton("İptal");
        btnIptal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnIptal.setFocusPainted(false);
        btnIptal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIptal.setPreferredSize(new Dimension(85, 32));
        btnIptal.addActionListener(e -> dialog.dispose());

        JButton btnKaydet = new JButton("Kaydet");
        btnKaydet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKaydet.setBackground(new Color(13, 110, 253));
        btnKaydet.setForeground(Color.WHITE);
        btnKaydet.setFocusPainted(false);
        btnKaydet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKaydet.setPreferredSize(new Dimension(100, 32));

        btnKaydet.addActionListener(e -> {
            String hamYorum = txtYorum.getText().trim();
            String isim = aktifKullanici.getIsim();
            String nihaiYorum;
            if (hamYorum.isEmpty()) {
                nihaiYorum = "";
            } else if (hamYorum.startsWith(isim + ":")) {
                nihaiYorum = hamYorum;
            } else {
                nihaiYorum = isim + ": " + hamYorum;
            }

            film.durumuIzlendiYap(seciliPuan[0], nihaiYorum, chkSpoiler.isSelected());
            verileriKaydet(true); // Değerlendirmeyi anında dosyaya kaydet
            tumGorunumleriGuncelle();
            dialog.dispose();

            JOptionPane.showMessageDialog(
                    this,
                    "'" + film.getAd() + "' başarıyla izlenenler listenize eklendi ve puanlandı!",
                    "Değerlendirme Kaydedildi",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        panelAlt.add(btnIptal);
        panelAlt.add(btnKaydet);
        dialog.add(panelAlt, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void varsayilanYerTutucuAyarla(JLabel lblAfis) {
        lblAfis.setIcon(null);
        lblAfis.setOpaque(true);
        lblAfis.setBackground(new Color(241, 243, 245));
        lblAfis.setText("<html><center style='color:#6c757d; font-family: Segoe UI;'><b style='font-size:12px;'>Görsel Yok</b></center></html>");
    }

    private Image resmiIndir(String urlString) {
        try {
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            try (InputStream in = conn.getInputStream()) {
                return javax.imageio.ImageIO.read(in);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void afisiAsenkronYukle(String urlStr, JLabel lblAfis) {
        SwingWorker<ImageIcon, Void> yukleyici = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                Image orjinalResim = resmiIndir(urlStr);
                if (orjinalResim != null) {
                    Image olcekliResim = orjinalResim.getScaledInstance(AFIS_GENISLIK, AFIS_YUKSEKLIK, Image.SCALE_SMOOTH);
                    return new ImageIcon(olcekliResim);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon afisIkonu = get();
                    if (afisIkonu != null) {
                        lblAfis.setText("");
                        lblAfis.setIcon(afisIkonu);
                        lblAfis.revalidate();
                        lblAfis.repaint();
                    } else {
                        varsayilanYerTutucuAyarla(lblAfis);
                    }
                } catch (Exception ignored) {
                    varsayilanYerTutucuAyarla(lblAfis);
                }
            }
        };

        yukleyici.execute();
    }

    private String yildizMetniUret(int puan) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= puan ? "★" : "☆");
        }
        return sb.toString();
    }

    private void yeniEserEkleDialogunuAc() {
        FilmEkleDialog dialog = new FilmEkleDialog(this, filmYoneticisi);
        dialog.setVisible(true);

        if (dialog.isKayitBasarili()) {
            verileriKaydet(true); // Yeni eklenen eseri anında dosyaya kaydet
            tumGorunumleriGuncelle();
        }
    }

    public void tumGorunumleriGuncelle() {
        int toplam = filmYoneticisi.getToplamSayisi();
        int izlenenler = filmYoneticisi.getIzlenenSayisi();
        int izlenenFilmler = filmYoneticisi.getIzlenenFilmSayisi();
        int izlenenDiziler = filmYoneticisi.getIzlenenDiziSayisi();
        int izlenecekler = filmYoneticisi.getIzlenecekSayisi();

        lblDurumCubugu.setText(String.format(
                "  Katalog Vitrini: %d | İzlenenler: %d (Film: %d, Dizi: %d) | Sonra İzlenecekler: %d",
                toplam, izlenenler, izlenenFilmler, izlenenDiziler, izlenecekler
        ));

        filtreliListeyiGuncelle(aktifSekme);
    }

    private JPanel olusturDurumCubuguPaneli() {
        JPanel panelDurum = new JPanel(new BorderLayout());
        panelDurum.setBackground(Color.WHITE);
        panelDurum.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, RENK_KENARLIK),
                new EmptyBorder(6, 15, 6, 15)
        ));

        lblDurumCubugu = new JLabel("Hazır");
        lblDurumCubugu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDurumCubugu.setForeground(new Color(108, 117, 125));
        panelDurum.add(lblDurumCubugu, BorderLayout.WEST);

        JLabel lblSurum = new JLabel("v5.5 (Veri Kalıcılığı ve Kullanıcı İzolasyonu)");
        lblSurum.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSurum.setForeground(new Color(173, 181, 189));
        panelDurum.add(lblSurum, BorderLayout.EAST);

        return panelDurum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new AnaPencere().setVisible(true);
        });
    }
}
