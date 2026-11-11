package com.takip.gui;

import com.takip.model.Film;
import com.takip.model.FilmYoneticisi;
import com.takip.model.KatalogEseri;
import com.takip.model.KatalogVeritabani;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Eser / Yapım Ekleme Formu (Modal Dialog)
 * 
 * Bu sınıf, sisteme yeni bir film veya dizi kaydı eklenmesini sağlayan
 * modern modal form penceresidir.
 * 
 * Yeni Güncellemeler (v3.9):
 * 1. Nötr / Boş Başlangıç Seçimleri:
 *    - Eser Türü (Film / Dizi) ComboBox'ı başlangıçta "Seçiniz..." ile açılır;
 *      hiçbir tür otomatik olarak seçilmez.
 *    - İzleme Durumu (İzledim / İzlenecek) ComboBox'ı başlangıçta "Seçiniz..." ile açılır;
 *      hiçbir durum varsayılan olarak seçili gelmez.
 * 2. Doğrulama ve Kayıt Engeli:
 *    - Tür seçilmemişse: "Lütfen yapım türünü (Film veya Dizi) seçiniz!" uyarısı verilir.
 *    - Durum seçilmemişse: "Lütfen izleme durumunu (İzledim veya İzlenecek) seçiniz!" uyarısı verilir.
 * 3. Form Sıfırlama (resetForm):
 *    - Form her açıldığında ve her başarılı kayıt sonrasında tüm alanlar
 *      otomatik olarak nötr/boş durumuna sıfırlanır.
 * 4. Puanlama (Yıldızlar) Durumu:
 *    - Durum henüz seçilmediğinde yıldızlar tamamen pasif (gri ve tıklanamaz) başlar.
 *    - Yalnızca "İzledim" seçildiğinde yıldız puanlama interaktifleşir.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 3.9 (Nötr Başlangıç ve Form Sıfırlama)
 */
public class FilmEkleDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color RENK_ARKA_PLAN = new Color(248, 249, 250);
    private static final Color RENK_KART_BEYAZ = Color.WHITE;
    private static final Color RENK_KENARLIK = new Color(222, 226, 230);
    private static final Color RENK_ANA = new Color(13, 110, 253);

    // Yıldız Puanlama Renkleri
    private static final Color RENK_YILDIZ_DOLU = new Color(245, 166, 35); // Parlak altın sarısı
    private static final Color RENK_YILDIZ_BOS = new Color(200, 200, 200);   // Açık gri

    private final FilmYoneticisi filmYoneticisi;
    private final AnaPencere anaPencere;
    private boolean kayitBasarili = false;

    // Otomatik Arama Bileşenleri
    private JTextField txtArama;
    private JLabel lblAramaBilgi;
    private JPopupMenu popupOneriler;
    private DefaultListModel<KatalogEseri> listModelOneriler;
    private JList<KatalogEseri> listOneriler;
    private JScrollPane scrollOneriler;
    private boolean programatikMetinDegisimi = false;

    // Standart Form Alanları (Nötr Başlatılan Seçim Bileşenleri)
    private JTextField txtFilmAdi;
    private JComboBox<String> cmbTur;
    private JTextField txtAfisUrl;
    private JComboBox<String> cmbDurum;

    // Modern Yıldız Puanlama Bileşenleri
    private JButton[] yildizButonlari;
    private JLabel lblPuanGosterge;
    private int seciliPuan = 0;

    // Yorum ve Yorum Yapan Alanları
    private JTextField txtYorumYapan;
    private JTextArea txtYorum;
    private JCheckBox chkSpoiler;

    public FilmEkleDialog(AnaPencere ebeveyn, FilmYoneticisi filmYoneticisi) {
        super(ebeveyn, "Yeni Film / Dizi Ekle", true);
        this.anaPencere = ebeveyn;
        this.filmYoneticisi = filmYoneticisi;

        dialogAyarlariniYap();
        arayuzuOlustur();
        resetForm(); // Form başlangıçta tamamen nötr ve boş başlasın
    }

    private void dialogAyarlariniYap() {
        setSize(540, 750);
        setResizable(false);
        setLocationRelativeTo(anaPencere);
        getContentPane().setBackground(RENK_ARKA_PLAN);
        setLayout(new BorderLayout());
    }

    private void arayuzuOlustur() {
        // Üst Başlık Şeridi
        JPanel panelBaslik = new JPanel(new BorderLayout());
        panelBaslik.setBackground(Color.WHITE);
        panelBaslik.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblBaslik = new JLabel("Yeni Film veya Dizi Kaydı");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBaslik.setForeground(new Color(33, 37, 41));

        JLabel lblAciklama = new JLabel("Hazır katalogdan aratabilir veya bilgileri serbestçe elle doldurabilirsiniz.");
        lblAciklama.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAciklama.setForeground(new Color(108, 117, 125));

        JPanel panelMetinler = new JPanel(new GridLayout(2, 1, 0, 3));
        panelMetinler.setOpaque(false);
        panelMetinler.add(lblBaslik);
        panelMetinler.add(lblAciklama);
        panelBaslik.add(panelMetinler, BorderLayout.CENTER);
        add(panelBaslik, BorderLayout.NORTH);

        // Orta Form Gövdesi (Kaydırılabilir Panel)
        JPanel panelGövde = new JPanel();
        panelGövde.setLayout(new BoxLayout(panelGövde, BoxLayout.Y_AXIS));
        panelGövde.setBackground(RENK_KART_BEYAZ);
        panelGövde.setBorder(new EmptyBorder(16, 24, 16, 24));

        // =====================================================================
        // 0. AKILLI KATALOG ARAMA VE OTOMATİK DOLDURMA ALANI
        // =====================================================================
        JPanel panelAramaKarti = new JPanel();
        panelAramaKarti.setLayout(new BoxLayout(panelAramaKarti, BoxLayout.Y_AXIS));
        panelAramaKarti.setBackground(new Color(241, 247, 255));
        panelAramaKarti.setBorder(new CompoundBorder(
                new LineBorder(new Color(186, 216, 250), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        panelAramaKarti.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAramaKarti.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        JLabel lblAramaBaslik = new JLabel("Hazır Katalogdan Ara ve Seç (Otomatik Doldur):");
        lblAramaBaslik.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAramaBaslik.setForeground(new Color(13, 110, 253));
        lblAramaBaslik.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAramaKarti.add(lblAramaBaslik);
        panelAramaKarti.add(Box.createVerticalStrut(6));

        txtArama = new JTextField();
        txtArama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtArama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtArama.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtArama.setToolTipText("Film veya dizi adı yazın (Örn: Inception, Succession, Chernobyl...)");
        panelAramaKarti.add(txtArama);

        lblAramaBilgi = new JLabel("İpucu: Seçtiğiniz eserin adı, türü ve afiş bağlantısı aşağıdaki forma otomatik aktarılır.");
        lblAramaBilgi.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAramaBilgi.setForeground(new Color(100, 116, 139));
        lblAramaBilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAramaKarti.add(Box.createVerticalStrut(4));
        panelAramaKarti.add(lblAramaBilgi);

        panelGövde.add(panelAramaKarti);
        panelGövde.add(Box.createVerticalStrut(14));

        // Arama Öneri Listesi ve Popup Menüsü
        listModelOneriler = new DefaultListModel<>();
        listOneriler = new JList<>(listModelOneriler);
        listOneriler.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listOneriler.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOneriler.setSelectionBackground(new Color(225, 239, 254));
        listOneriler.setSelectionForeground(new Color(13, 110, 253));

        listOneriler.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KatalogEseri) {
                    KatalogEseri k = (KatalogEseri) value;
                    String rozet = k.getTur().equalsIgnoreCase("Dizi") ? "Dizi" : "Film";
                    String rozetRenk = k.getTur().equalsIgnoreCase("Dizi") ? "#0d6efd" : "#6f42c1";
                    label.setText(String.format("<html><div style='padding:3px;'><b>%s</b> &nbsp;<span style='color:%s; font-size:10px;'>[%s]</span></div></html>",
                            k.getAd(), rozetRenk, rozet));
                    label.setBorder(new EmptyBorder(4, 8, 4, 8));
                }
                return label;
            }
        });

        scrollOneriler = new JScrollPane(listOneriler);
        scrollOneriler.setBorder(new LineBorder(new Color(186, 216, 250), 1));
        scrollOneriler.getVerticalScrollBar().setUnitIncrement(14);

        popupOneriler = new JPopupMenu();
        popupOneriler.setFocusable(false);
        popupOneriler.setBorder(BorderFactory.createEmptyBorder());
        popupOneriler.add(scrollOneriler);

        // Canlı Arama Dinleyicisi
        txtArama.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onAramaMetniDegisti();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onAramaMetniDegisti();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onAramaMetniDegisti();
            }
        });

        // Klavye Gezinme Dinleyicisi
        txtArama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupOneriler.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        int index = listOneriler.getSelectedIndex() + 1;
                        if (index < listModelOneriler.getSize()) {
                            listOneriler.setSelectedIndex(index);
                            listOneriler.ensureIndexIsVisible(index);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int index = listOneriler.getSelectedIndex() - 1;
                        if (index >= 0) {
                            listOneriler.setSelectedIndex(index);
                            listOneriler.ensureIndexIsVisible(index);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        KatalogEseri secilen = listOneriler.getSelectedValue();
                        if (secilen != null) {
                            katalogEseriniFormaDoldur(secilen);
                            e.consume();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        popupOneriler.setVisible(false);
                        e.consume();
                    }
                }
            }
        });

        // Öneri Tıklama Dinleyicisi
        listOneriler.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                KatalogEseri secilen = listOneriler.getSelectedValue();
                if (secilen != null) {
                    katalogEseriniFormaDoldur(secilen);
                }
            }
        });

        // =====================================================================
        // 1. Film / Dizi Adı
        // =====================================================================
        panelGövde.add(olusturEtiket("Film / Dizi Adı: *"));
        txtFilmAdi = new JTextField();
        txtFilmAdi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFilmAdi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtFilmAdi.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(txtFilmAdi);
        panelGövde.add(Box.createVerticalStrut(10));

        // =====================================================================
        // 2. Eser Türü (Nötr Başlatma: 'Seçiniz...', Zorunlu Seçim)
        // =====================================================================
        panelGövde.add(olusturEtiket("Eser Türü: *"));
        cmbTur = new JComboBox<>(new String[]{"Seçiniz...", "Film", "Dizi"});
        cmbTur.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbTur.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbTur.setSelectedIndex(0); // Başlangıçta boş/nötr gelsin
        panelGövde.add(cmbTur);
        panelGövde.add(Box.createVerticalStrut(10));

        // =====================================================================
        // 3. Afiş Görsel Bağlantısı (URL)
        // =====================================================================
        panelGövde.add(olusturEtiket("Afiş Görsel Bağlantısı (URL):"));
        txtAfisUrl = new JTextField();
        txtAfisUrl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAfisUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtAfisUrl.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtAfisUrl.setToolTipText("İsteğe bağlı: İnternetteki resim linkini yapıştırabilirsiniz");
        panelGövde.add(txtAfisUrl);

        JLabel lblAfisBilgi = new JLabel("(İsteğe bağlı: İnternetteki resim linkini yapıştırabilirsiniz)");
        lblAfisBilgi.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAfisBilgi.setForeground(new Color(120, 130, 140));
        lblAfisBilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(lblAfisBilgi);
        panelGövde.add(Box.createVerticalStrut(10));

        // =====================================================================
        // 4. İzleme Durumu (Nötr Başlatma: 'Seçiniz...', Zorunlu Seçim)
        // =====================================================================
        panelGövde.add(olusturEtiket("İzleme Durumu: *"));
        cmbDurum = new JComboBox<>(new String[]{"Seçiniz...", "İzledim", "İzlenecek"});
        cmbDurum.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDurum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbDurum.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbDurum.setSelectedIndex(0); // Başlangıçta hiçbir durum seçili olmasın
        panelGövde.add(cmbDurum);
        panelGövde.add(Box.createVerticalStrut(10));

        // =====================================================================
        // 5. MODERN İNTERAKTİF YILDIZ PUANLAMA BİLEŞENİ
        // =====================================================================
        panelGövde.add(olusturEtiket("Puanınız (1 - 5 Yıldız):"));
        JPanel panelYildizlar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelYildizlar.setOpaque(false);
        panelYildizlar.setAlignmentX(Component.LEFT_ALIGNMENT);

        yildizButonlari = new JButton[5];
        for (int i = 0; i < 5; i++) {
            final int puanDegeri = i + 1;
            JButton btn = new JButton("\u2606");
            btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 26));
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

            // Tıklama ile Kalıcı Puan Seçimi (Yalnızca "İzledim" seçiliyse)
            btn.addActionListener(e -> {
                if (cmbDurum.getSelectedIndex() == 1) {
                    puanAyarla(puanDegeri);
                }
            });

            // Fare Üzerine Gelme (Hover) Dinleyicisi
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn.isEnabled() && cmbDurum.getSelectedIndex() == 1) {
                        yildizlariGuncelle(puanDegeri);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn.isEnabled() && cmbDurum.getSelectedIndex() == 1) {
                        yildizlariGuncelle(seciliPuan);
                    }
                }
            });

            yildizButonlari[i] = btn;
            panelYildizlar.add(btn);
        }

        // Seçilen Puan Gösterge Etiketi
        lblPuanGosterge = new JLabel("Durum seçilmedi");
        lblPuanGosterge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPuanGosterge.setForeground(new Color(140, 150, 160));
        lblPuanGosterge.setBorder(new EmptyBorder(0, 10, 0, 0));
        panelYildizlar.add(lblPuanGosterge);

        panelGövde.add(panelYildizlar);
        panelGövde.add(Box.createVerticalStrut(10));

        // Başlangıçta izleme durumu "Seçiniz..." olduğu için yıldızlar devre dışıdır
        yildizlariAktiflestir(false);

        // İzleme durumu değiştiğinde yıldızları dinamik aktif/pasif yap
        cmbDurum.addActionListener(e -> {
            int secilenIndex = cmbDurum.getSelectedIndex();
            if (secilenIndex == 1) { // "İzledim"
                yildizlariAktiflestir(true);
            } else { // "Seçiniz..." (0) veya "İzlenecek" (2)
                yildizlariAktiflestir(false);
            }
        });

        // =====================================================================
        // 6. YORUMU YAPAN (İSİM) ALANI
        // =====================================================================
        String aktifIsim = (anaPencere != null && anaPencere.getAktifKullanici() != null)
                ? anaPencere.getAktifKullanici().getIsim()
                : "Kullanıcı";

        panelGövde.add(olusturEtiket("Yorumu Yapan (İsim):"));
        txtYorumYapan = new JTextField();
        txtYorumYapan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtYorumYapan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtYorumYapan.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtYorumYapan.setText(aktifIsim);
        txtYorumYapan.setToolTipText("İsteğe bağlı: Boş bırakılırsa oturum açan kullanıcı '" + aktifIsim + "' kullanılır.");
        panelGövde.add(txtYorumYapan);

        JLabel lblYapanBilgi = new JLabel("(İsteğe bağlı: Boş bırakılırsa varsayılan olarak '" + aktifIsim + "' kullanılır)");
        lblYapanBilgi.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblYapanBilgi.setForeground(new Color(120, 130, 140));
        lblYapanBilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(lblYapanBilgi);
        panelGövde.add(Box.createVerticalStrut(10));

        // =====================================================================
        // 7. KULLANICI YORUMU ALANI
        // =====================================================================
        panelGövde.add(olusturEtiket("Kullanıcı Yorumu:"));
        txtYorum = new JTextArea(3, 20);
        txtYorum.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtYorum.setLineWrap(true);
        txtYorum.setWrapStyleWord(true);
        JScrollPane scrollYorum = new JScrollPane(txtYorum);
        scrollYorum.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollYorum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        panelGövde.add(scrollYorum);
        panelGövde.add(Box.createVerticalStrut(8));

        // 8. Spoiler Kutusu
        chkSpoiler = new JCheckBox("Bu yorum sürprizbozan (spoiler) içeriyor");
        chkSpoiler.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSpoiler.setOpaque(false);
        chkSpoiler.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGövde.add(chkSpoiler);

        JScrollPane scrollGövde = new JScrollPane(panelGövde);
        scrollGövde.setBorder(null);
        scrollGövde.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollGövde, BorderLayout.CENTER);

        // Alt Butonlar Şeridi
        JPanel panelAlt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panelAlt.setBackground(Color.WHITE);
        panelAlt.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, RENK_KENARLIK));

        JButton btnIptal = new JButton("İptal");
        btnIptal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnIptal.setFocusPainted(false);
        btnIptal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIptal.setPreferredSize(new Dimension(90, 34));
        btnIptal.addActionListener(e -> dispose());

        JButton btnKaydet = new JButton("Listeye Ekle");
        btnKaydet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKaydet.setBackground(RENK_ANA);
        btnKaydet.setForeground(Color.WHITE);
        btnKaydet.setFocusPainted(false);
        btnKaydet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKaydet.setPreferredSize(new Dimension(130, 34));
        btnKaydet.addActionListener(e -> kaydetVeKapat());

        panelAlt.add(btnIptal);
        panelAlt.add(btnKaydet);
        add(panelAlt, BorderLayout.SOUTH);
    }

    private void onAramaMetniDegisti() {
        if (programatikMetinDegisimi) {
            return;
        }
        SwingUtilities.invokeLater(this::katalogdaAraVeOner);
    }

    private void katalogdaAraVeOner() {
        String aranan = txtArama.getText().trim();
        if (aranan.isEmpty()) {
            popupOneriler.setVisible(false);
            lblAramaBilgi.setText("İpucu: Seçtiğiniz eserin adı, türü ve afiş bağlantısı aşağıdaki forma otomatik aktarılır.");
            lblAramaBilgi.setForeground(new Color(100, 116, 139));
            return;
        }

        List<KatalogEseri> eslesenler = KatalogVeritabani.ara(aranan);
        if (eslesenler.isEmpty()) {
            popupOneriler.setVisible(false);
            lblAramaBilgi.setText("Katalogda eşleşen bulunamadı (Aşağıdan serbestçe manuel ekleyebilirsiniz).");
            lblAramaBilgi.setForeground(new Color(180, 83, 9));
            return;
        }

        listModelOneriler.clear();
        for (KatalogEseri e : eslesenler) {
            listModelOneriler.addElement(e);
        }
        listOneriler.setSelectedIndex(0);

        int genislik = txtArama.getWidth();
        if (genislik <= 0) {
            genislik = 440;
        }
        int yukseklik = Math.min(eslesenler.size() * 32 + 6, 180);

        scrollOneriler.setPreferredSize(new Dimension(genislik, yukseklik));
        popupOneriler.pack();

        if (txtArama.isShowing()) {
            popupOneriler.show(txtArama, 0, txtArama.getHeight() + 2);
            txtArama.requestFocusInWindow();
        }
    }

    private void katalogEseriniFormaDoldur(KatalogEseri secilen) {
        if (secilen == null) {
            return;
        }

        programatikMetinDegisimi = true;
        txtArama.setText(secilen.getAd());
        programatikMetinDegisimi = false;

        // 1. Film / Dizi Adını Otomatik Doldur
        txtFilmAdi.setText(secilen.getAd());

        // 2. Eser Türünü Otomatik Seç ("Film" veya "Dizi")
        if ("Dizi".equalsIgnoreCase(secilen.getTur())) {
            cmbTur.setSelectedItem("Dizi");
        } else if ("Film".equalsIgnoreCase(secilen.getTur())) {
            cmbTur.setSelectedItem("Film");
        }

        // 3. Varsa Hazır Afiş Bağlantısını Metin Kutusuna Doldur
        if (secilen.getAfisUrl() != null && !secilen.getAfisUrl().trim().isEmpty()) {
            txtAfisUrl.setText(secilen.getAfisUrl().trim());
        } else {
            txtAfisUrl.setText("");
        }

        // Öneri Popup'ını Kapat
        popupOneriler.setVisible(false);

        // Kullanıcıya Başarı Geri Bildirimi
        lblAramaBilgi.setText("'" + secilen.getAd() + "' [" + secilen.getTur() + "] seçildi, forma aktarıldı.");
        lblAramaBilgi.setForeground(new Color(25, 135, 84));
    }

    private JLabel olusturEtiket(String metin) {
        JLabel lbl = new JLabel(metin);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(73, 80, 87));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /**
     * Tıklanan puan değerini kalıcı olarak formun durumuna atar ve yıldızları günceller.
     */
    private void puanAyarla(int puan) {
        this.seciliPuan = puan;
        yildizlariGuncelle(puan);
    }

    /**
     * Yıldız butonlarının metin ve renk durumunu verilen puana göre anlık yeniler.
     * Hover ve seçim işlemlerinde ortak kullanılır.
     */
    private void yildizlariGuncelle(int puan) {
        for (int i = 0; i < yildizButonlari.length; i++) {
            if (i < puan) {
                yildizButonlari[i].setText("★");
                yildizButonlari[i].setForeground(RENK_YILDIZ_DOLU);
            } else {
                yildizButonlari[i].setText("☆");
                yildizButonlari[i].setForeground(RENK_YILDIZ_BOS);
            }
        }

        if (lblPuanGosterge != null) {
            int durumIdx = (cmbDurum != null) ? cmbDurum.getSelectedIndex() : 0;
            if (durumIdx == 1) { // "İzledim"
                if (puan > 0) {
                    lblPuanGosterge.setText(puan + " / 5");
                    lblPuanGosterge.setForeground(new Color(190, 120, 0));
                } else {
                    lblPuanGosterge.setText("Puan seçilmedi");
                    lblPuanGosterge.setForeground(new Color(140, 150, 160));
                }
            } else if (durumIdx == 2) { // "İzlenecek"
                lblPuanGosterge.setText("İzlenmedi (Puansız)");
                lblPuanGosterge.setForeground(new Color(150, 160, 170));
            } else { // "Seçiniz..." (0)
                lblPuanGosterge.setText("Durum seçilmedi");
                lblPuanGosterge.setForeground(new Color(150, 160, 170));
            }
        }
    }

    /**
     * "İzlendi" durumuna göre yıldız butonlarının tıklanabilirliğini ayarlar.
     * Pasif durumdayken yıldızlar grileşir ve tıklanamaz hale gelir.
     */
    private void yildizlariAktiflestir(boolean aktif) {
        for (JButton btn : yildizButonlari) {
            btn.setEnabled(aktif);
            btn.setCursor(aktif ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        }

        if (!aktif) {
            yildizlariGuncelle(0);
        } else {
            yildizlariGuncelle(seciliPuan);
        }
    }

    /**
     * Formdaki tüm seçimleri ve metin alanlarını nötr/boş başlangıç durumuna sıfırlar.
     */
    public void resetForm() {
        if (txtArama != null) {
            programatikMetinDegisimi = true;
            txtArama.setText("");
            programatikMetinDegisimi = false;
        }
        if (popupOneriler != null && popupOneriler.isVisible()) {
            popupOneriler.setVisible(false);
        }
        if (lblAramaBilgi != null) {
            lblAramaBilgi.setText("İpucu: Seçtiğiniz eserin adı, türü ve afiş bağlantısı aşağıdaki forma otomatik aktarılır.");
            lblAramaBilgi.setForeground(new Color(100, 116, 139));
        }
        if (txtFilmAdi != null) {
            txtFilmAdi.setText("");
        }
        if (cmbTur != null) {
            cmbTur.setSelectedIndex(0); // Nötr 'Seçiniz...'
        }
        if (txtAfisUrl != null) {
            txtAfisUrl.setText("");
        }
        if (cmbDurum != null) {
            cmbDurum.setSelectedIndex(0); // Nötr 'Seçiniz...'
        }

        seciliPuan = 0;
        yildizlariAktiflestir(false);

        String aktifIsim = (anaPencere != null && anaPencere.getAktifKullanici() != null)
                ? anaPencere.getAktifKullanici().getIsim()
                : "Kullanıcı";
        if (txtYorumYapan != null) {
            txtYorumYapan.setText(aktifIsim);
        }
        if (txtYorum != null) {
            txtYorum.setText("");
        }
        if (chkSpoiler != null) {
            chkSpoiler.setSelected(false);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            resetForm();
            kayitBasarili = false;
        }
        super.setVisible(b);
    }

    private void kaydetVeKapat() {
        String ad = txtFilmAdi.getText().trim();

        // 1. Film / Dizi Adı Doğrulaması
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen film veya dizi adını boş bırakmayınız!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE
            );
            txtFilmAdi.requestFocus();
            return;
        }

        // 2. Yapım Türü Doğrulaması (Zorunlu Seçim)
        if (cmbTur.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen yapım türünü (Film veya Dizi) seçiniz!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE
            );
            cmbTur.requestFocus();
            return;
        }

        // 3. İzleme Durumu Doğrulaması (Zorunlu Seçim)
        if (cmbDurum.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lütfen izleme durumunu (İzledim veya İzlenecek) seçiniz!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE
            );
            cmbDurum.requestFocus();
            return;
        }

        String tur = (String) cmbTur.getSelectedItem();
        String afisUrl = txtAfisUrl.getText().trim();
        
        // ComboBox seçimi: 1 -> "İzledim" (true), 2 -> "İzlenecek" (false)
        boolean izlendi = (cmbDurum.getSelectedIndex() == 1);
        int puan = izlendi ? this.seciliPuan : 0;

        // Kullanıcı Yorumu ve Yorum Yapan Kişi Bilgisi
        String hamYorum = txtYorum.getText().trim();
        String yorumYapan = txtYorumYapan.getText().trim();

        // Eğer kullanıcı adı boş bırakılmışsa oturum açan aktif kullanıcıyı kullan
        if (yorumYapan.isEmpty()) {
            yorumYapan = (anaPencere != null && anaPencere.getAktifKullanici() != null)
                    ? anaPencere.getAktifKullanici().getIsim()
                    : "Kullanıcı";
        }

        String nihaiYorum;
        if (hamYorum.isEmpty()) {
            nihaiYorum = "";
        } else {
            // Doğal isim formatında "Ad Soyad: Yorum Metni" olarak biçimlendir
            if (hamYorum.startsWith(yorumYapan + ":")) {
                nihaiYorum = hamYorum;
            } else {
                nihaiYorum = yorumYapan + ": " + hamYorum;
            }
        }

        boolean spoilerMi = chkSpoiler.isSelected();

        // Yeni Film / Dizi Nesnesi Oluşturma
        Film yeniFilm = new Film(ad, tur, izlendi, puan, nihaiYorum, spoilerMi, afisUrl);
        filmYoneticisi.filmEkle(yeniFilm);
        kayitBasarili = true;

        String turBelirtme = tur.equalsIgnoreCase("Dizi") ? "adlı dizi" : "adlı film";
        JOptionPane.showMessageDialog(
                this,
                "'" + ad + "' " + turBelirtme + " başarıyla arşivinize eklendi!",
                "Kayıt Başarılı",
                JOptionPane.INFORMATION_MESSAGE
        );

        resetForm();
        dispose();
    }

    @Override
    public void dispose() {
        if (popupOneriler != null && popupOneriler.isVisible()) {
            popupOneriler.setVisible(false);
        }
        super.dispose();
    }

    public boolean isKayitBasarili() {
        return kayitBasarili;
    }
}
