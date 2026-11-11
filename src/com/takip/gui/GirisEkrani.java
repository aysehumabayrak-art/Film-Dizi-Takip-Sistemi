package com.takip.gui;

import com.takip.model.DosyaYoneticisi;
import com.takip.model.Kullanici;
import com.takip.model.KullaniciYoneticisi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Kimlik Doğrulama Ekranı
 * 
 * Bu sınıf, uygulamanın giriş kapısıdır. Kullanıcıların sisteme giriş yapmasını
 * (Authentication) ve yeni hesap oluşturmasını (Registration) sağlayan iki sekmeli
 * modern bir GUI arayüzüdür.
 * 
 * Yeni Özellikler (v5.5 - Oturum ve Hesap Kalıcılığı):
 * 1. Başlangıçta kayıtlı tüm kullanıcı hesapları (kullanicilar.txt) otomatik yüklenir.
 * 2. Yeni üye olan kullanıcılar kalıcı olarak saklanır, program yeniden açıldığında giriş yapabilirler.
 * 3. Başarılı giriş sonrasında kullanıcıya özel oturum ve izleme listesi (AnaPencere) yüklenir.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 5.5 (Hesap Kalıcılığı ve Oturum Doğrulama)
 */
public class GirisEkrani extends JFrame {

    private static final long serialVersionUID = 1L;

    // =========================================================================
    // RENK VE STİL SABİTLERİ
    // =========================================================================
    private static final Color RENK_ARKA_PLAN = new Color(245, 247, 250);
    private static final Color RENK_BEYAZ = Color.WHITE;
    private static final Color RENK_KENARLIK = new Color(222, 226, 230);
    private static final Color RENK_ANA_MAVI = new Color(25, 118, 210);   // #1976D2
    private static final Color RENK_KAYIT_YESIL = new Color(40, 167, 69); // #28A745

    // =========================================================================
    // BİLEŞENLER
    // =========================================================================
    private JTabbedPane tabbedPane;

    // Giriş Bileşenleri
    private JTextField txtGirisKullanici;
    private JPasswordField txtGirisSifre;
    private JButton btnGirisYap;

    // Kayıt Bileşenleri
    private JTextField txtKayitIsim;
    private JTextField txtKayitEmail;
    private JPasswordField txtKayitSifre;
    private JButton btnKayitOl;

    // =========================================================================
    // YAPICI METOT (CONSTRUCTOR)
    // =========================================================================
    public GirisEkrani() {
        // Kayıtlı kullanıcı hesaplarını diskten belleğe yükle
        DosyaYoneticisi.kullanicilariYukle();

        pencereAyarlariniYap();
        arayuzuOlustur();
    }

    private void pencereAyarlariniYap() {
        setTitle("Giriş Yap | Kişisel Film ve Dizi Takip Sistemi");
        setSize(450, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekran ortasında aç
        getContentPane().setBackground(RENK_ARKA_PLAN);
        setLayout(new BorderLayout());
    }

    private void arayuzuOlustur() {
        // 1. Üst Başlık Şeridi
        add(olusturHeaderPaneli(), BorderLayout.NORTH);

        // 2. Sekmeli Panel (Giriş Yap / Üye Ol)
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(RENK_BEYAZ);
        tabbedPane.setBorder(new EmptyBorder(10, 20, 15, 20));

        tabbedPane.addTab("Giriş Yap", olusturGirisSekmesi());
        tabbedPane.addTab("Üye Ol", olusturKayitSekmesi());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Alt Telif / Bilgi Şeridi
        add(olusturAltBilgiPaneli(), BorderLayout.SOUTH);

        // Enter tuşuna basıldığında aktif sekmeye göre buton çalışsın
        getRootPane().setDefaultButton(btnGirisYap);
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                getRootPane().setDefaultButton(btnGirisYap);
            } else {
                getRootPane().setDefaultButton(btnKayitOl);
            }
        });
    }

    private JPanel olusturHeaderPaneli() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblBaslik = new JLabel("Kişisel Film & Dizi Takip", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBaslik.setForeground(new Color(33, 37, 41));

        JLabel lblAlt = new JLabel("Lütfen devam etmek için giriş yapın veya kayıt olun.", SwingConstants.CENTER);
        lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlt.setForeground(new Color(108, 117, 125));

        JPanel metinPaneli = new JPanel(new GridLayout(2, 1, 0, 4));
        metinPaneli.setOpaque(false);
        metinPaneli.add(lblBaslik);
        metinPaneli.add(lblAlt);

        header.add(metinPaneli, BorderLayout.CENTER);
        return header;
    }

    // =========================================================================
    // A) GİRİŞ YAP SEKMESİ (SADELEŞTİRİLMİŞ)
    // =========================================================================
    private JPanel olusturGirisSekmesi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(RENK_BEYAZ);
        panel.setBorder(new EmptyBorder(25, 25, 20, 25));

        // 1. Kullanıcı Adı veya E-posta
        panel.add(olusturEtiket("Kullanıcı Adı veya E-posta:"));
        txtGirisKullanici = new JTextField();
        txtGirisKullanici.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGirisKullanici.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtGirisKullanici.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtGirisKullanici);
        panel.add(Box.createVerticalStrut(14));

        // 2. Şifre
        panel.add(olusturEtiket("Şifre:"));
        txtGirisSifre = new JPasswordField();
        txtGirisSifre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGirisSifre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtGirisSifre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtGirisSifre);
        panel.add(Box.createVerticalStrut(22));

        // 3. Giriş Yap Butonu
        btnGirisYap = new JButton("Giriş Yap");
        btnGirisYap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGirisYap.setBackground(RENK_ANA_MAVI);
        btnGirisYap.setForeground(Color.WHITE);
        btnGirisYap.setOpaque(true);
        btnGirisYap.setContentAreaFilled(true);
        btnGirisYap.setFocusPainted(false);
        btnGirisYap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGirisYap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnGirisYap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGirisYap.addActionListener(e -> girisIslemi());
        panel.add(btnGirisYap);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // =========================================================================
    // B) ÜYE OL SEKMESİ
    // =========================================================================
    private JPanel olusturKayitSekmesi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(RENK_BEYAZ);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. İsim
        panel.add(olusturEtiket("Kullanıcı Adı / İsim: *"));
        txtKayitIsim = new JTextField();
        txtKayitIsim.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKayitIsim.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtKayitIsim.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtKayitIsim);
        panel.add(Box.createVerticalStrut(10));

        // 2. E-posta
        panel.add(olusturEtiket("E-posta Adresi: *"));
        txtKayitEmail = new JTextField();
        txtKayitEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKayitEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtKayitEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtKayitEmail);
        panel.add(Box.createVerticalStrut(10));

        // 3. Şifre (EN FAZLA 12 KARAKTER SINIRLAMASI)
        panel.add(olusturEtiket("Şifre (En fazla 12 karakter): *"));
        txtKayitSifre = new JPasswordField();
        txtKayitSifre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKayitSifre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtKayitSifre.setAlignmentX(Component.LEFT_ALIGNMENT);

        // KRİTİK ŞİFRE KURALI: 12 KARAKTERDEN FAZLASINI ENGELLE VE UYARI VER
        ((AbstractDocument) txtKayitSifre.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int newLength = currentLength - length + (text != null ? text.length() : 0);

                if (newLength <= 12) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    JOptionPane.showMessageDialog(
                            GirisEkrani.this,
                            "Şifre en fazla 12 karakter olabilir!",
                            "Karakter Sınırı Uyarısı",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int newLength = currentLength + (string != null ? string.length() : 0);

                if (newLength <= 12) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    JOptionPane.showMessageDialog(
                            GirisEkrani.this,
                            "Şifre en fazla 12 karakter olabilir!",
                            "Karakter Sınırı Uyarısı",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        panel.add(txtKayitSifre);
        panel.add(Box.createVerticalStrut(18));

        // 4. Kayıt Ol Butonu
        btnKayitOl = new JButton("Üye Ol ve Kaydet");
        btnKayitOl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKayitOl.setBackground(RENK_KAYIT_YESIL);
        btnKayitOl.setForeground(Color.WHITE);
        btnKayitOl.setOpaque(true);
        btnKayitOl.setContentAreaFilled(true);
        btnKayitOl.setFocusPainted(false);
        btnKayitOl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKayitOl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnKayitOl.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKayitOl.addActionListener(e -> kayitIslemi());
        panel.add(btnKayitOl);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel olusturEtiket(String metin) {
        JLabel lbl = new JLabel(metin);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(73, 80, 87));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JPanel olusturAltBilgiPaneli() {
        JPanel alt = new JPanel(new FlowLayout(FlowLayout.CENTER));
        alt.setBackground(RENK_ARKA_PLAN);
        alt.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lbl = new JLabel("Güvenli Oturum & Hesap Yönetim Sistemi v1.1");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(140, 150, 160));
        alt.add(lbl);

        return alt;
    }

    // =========================================================================
    // İŞ MANTIKLARI VE ETKİLEŞİMLER
    // =========================================================================
    /**
     * Giriş yap butonuna basıldığında çalışır.
     */
    private void girisIslemi() {
        String kullaniciGirdisi = txtGirisKullanici.getText().trim();
        String sifre = new String(txtGirisSifre.getPassword()).trim();

        if (kullaniciGirdisi.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen kullanıcı adı / e-posta ve şifre alanlarını doldurunuz!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Kimlik Doğrulama
        Kullanici aktifKullanici = KullaniciYoneticisi.girisYap(kullaniciGirdisi, sifre);

        if (aktifKullanici != null) {
            // Giriş Başarılı -> Giriş ekranını kapat, kullanıcıya özel AnaPencere'yi aç
            dispose();
            SwingUtilities.invokeLater(() -> {
                AnaPencere anaPencere = new AnaPencere(aktifKullanici);
                anaPencere.setVisible(true);
            });
        } else {
            // Giriş Başarısız
            JOptionPane.showMessageDialog(
                    this,
                    "Kullanıcı adı/e-posta veya şifre hatalı!",
                    "Giriş Başarısız",
                    JOptionPane.ERROR_MESSAGE
            );
            txtGirisSifre.setText("");
            txtGirisSifre.requestFocus();
        }
    }

    /**
     * Kayıt ol butonuna basıldığında çalışır.
     */
    private void kayitIslemi() {
        String isim = txtKayitIsim.getText().trim();
        String email = txtKayitEmail.getText().trim();
        String sifre = new String(txtKayitSifre.getPassword()).trim();

        // 1. Boş Alan Doğrulaması
        if (isim.isEmpty() || email.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen tüm alanları eksiksiz doldurunuz!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Basit E-posta Kontrolü
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen geçerli bir e-posta adresi giriniz (Örn: ad@mail.com)!",
                    "Geçersiz E-posta",
                    JOptionPane.WARNING_MESSAGE
            );
            txtKayitEmail.requestFocus();
            return;
        }

        // 3. Şifre Sınır Kontrolü (12 Karakter)
        if (sifre.length() > 12) {
            JOptionPane.showMessageDialog(
                    this,
                    "Şifre en fazla 12 karakter olabilir!",
                    "Şifre Sınırı",
                    JOptionPane.WARNING_MESSAGE
            );
            txtKayitSifre.requestFocus();
            return;
        }

        // 4. Kullanıcıyı Kaydet (Belleğe ve Kalıcı Dosyaya)
        Kullanici yeniKullanici = new Kullanici(isim, email, sifre);
        String hata = KullaniciYoneticisi.kullaniciKaydet(yeniKullanici);

        if (hata != null) {
            JOptionPane.showMessageDialog(this, hata, "Kayıt Hatası", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kalıcı 'kullanicilar.txt' dosyasına yaz
        DosyaYoneticisi.kullaniciKaydet(yeniKullanici);

        // Başarılı Kayıt
        JOptionPane.showMessageDialog(
                this,
                "Hesabınız başarıyla oluşturuldu! Şimdi giriş yapabilirsiniz.",
                "Kayıt Başarılı",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Formu temizle ve Giriş sekmesine yönlendir
        txtKayitIsim.setText("");
        txtKayitEmail.setText("");
        txtKayitSifre.setText("");

        tabbedPane.setSelectedIndex(0); // Giriş Yap sekmesine geç
        txtGirisKullanici.setText(isim);
        txtGirisSifre.requestFocus();
    }
}
