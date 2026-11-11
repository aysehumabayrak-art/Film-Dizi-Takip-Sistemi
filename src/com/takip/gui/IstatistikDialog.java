package com.takip.gui;

import com.takip.model.FilmYoneticisi;
import com.takip.model.Kullanici;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Kişisel Film ve Dizi Takip Sistemi - Kullanıcı Profili ve İzleme İstatistikleri Dialog Penceresi
 * 
 * Bu sınıf, kullanıcının kişisel arşivindeki izleme verilerini ve alışkanlıklarını
 * "Yapım" odaklı modern kart tasarımıyla görselleştiren modal iletişim penceresidir.
 * 
 * Yapılan İyileştirmeler (v3.7):
 * - Tüm metinlerde "Eser" kelimesi yerine "Yapım" ifadesi kullanılmıştır:
 *   * "Toplam İzlenen Yapım"
 *   * "İzleme Listesindeki Yapımlar"
 *   * "Puanlanan yapımlar üzerinden"
 * 
 * @author Proje Geliştirme Ekibi
 * @version 3.7 (Yapım Odaklı Profil ve İstatistik Modülü)
 */
public class IstatistikDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color RENK_ARKA_PLAN = new Color(248, 250, 252);
    private static final Color RENK_KENARLIK = new Color(226, 232, 240);
    private static final Color RENK_BASLIK = new Color(15, 23, 42);
    private static final Color RENK_ALT_METIN = new Color(100, 116, 139);

    private final FilmYoneticisi filmYoneticisi;
    private final Kullanici aktifKullanici;

    public IstatistikDialog(JFrame parent, FilmYoneticisi filmYoneticisi, Kullanici aktifKullanici) {
        super(parent, "Kullanıcı Profili ve İzleme İstatistikleri", true);
        this.filmYoneticisi = filmYoneticisi;
        this.aktifKullanici = (aktifKullanici != null)
                ? aktifKullanici
                : new Kullanici("Kullanıcı", "", "");

        pencereAyarlariniYap(parent);
        arayuzuOlustur();
    }

    private void pencereAyarlariniYap(JFrame parent) {
        setSize(490, 520);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(RENK_ARKA_PLAN);
        setLayout(new BorderLayout());
    }

    private void arayuzuOlustur() {
        // 1. Üst Başlık Şeridi
        add(olusturHeaderPaneli(), BorderLayout.NORTH);

        // 2. Orta İstatistik Kartları Paneli
        add(olusturIcerikPaneli(), BorderLayout.CENTER);

        // 3. Alt Kapat Buton Şeridi
        add(olusturAltPaneli(), BorderLayout.SOUTH);
    }

    private JPanel olusturHeaderPaneli() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, RENK_KENARLIK),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel lblBaslik = new JLabel("Kullanıcı Profili ve İzleme İstatistikleri");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBaslik.setForeground(RENK_BASLIK);

        JLabel lblAlt = new JLabel("Sayın " + aktifKullanici.getIsim() + ", izleme alışkanlıklarınız ve arşiv özetiniz:");
        lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlt.setForeground(RENK_ALT_METIN);

        JPanel panelMetin = new JPanel(new GridLayout(2, 1, 0, 3));
        panelMetin.setOpaque(false);
        panelMetin.add(lblBaslik);
        panelMetin.add(lblAlt);

        header.add(panelMetin, BorderLayout.CENTER);
        return header;
    }

    private JPanel olusturIcerikPaneli() {
        JPanel panelGövde = new JPanel();
        panelGövde.setLayout(new BoxLayout(panelGövde, BoxLayout.Y_AXIS));
        panelGövde.setBackground(RENK_ARKA_PLAN);
        panelGövde.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Metrik Hesaplamaları
        int toplamIzlenen = filmYoneticisi.getIzlenenSayisi();
        int izlenenFilm = filmYoneticisi.getIzlenenFilmSayisi();
        int izlenenDizi = filmYoneticisi.getIzlenenDiziSayisi();
        int siradaBekleyen = filmYoneticisi.getIzlenecekSayisi();
        String sureMetni = filmYoneticisi.getTahminiIzlemeSuresiMetni();
        String ortalamaPuanMetni = filmYoneticisi.getOrtalamaPuanMetni();
        String tercihTur = filmYoneticisi.getEnCokTercihEdilenTur();

        // 1. Kart: İzlenen Yapım Sayısı
        panelGövde.add(olusturKart(
                "İzlenen Yapım Sayısı",
                toplamIzlenen + " Yapım",
                "(" + izlenenFilm + " Film, " + izlenenDizi + " Dizi)",
                new Color(25, 135, 84)
        ));
        panelGövde.add(Box.createVerticalStrut(10));

        // 2. Kart: Tahmini Toplam İzleme Süresi
        panelGövde.add(olusturKart(
                "Tahmini Toplam İzleme Süresi",
                sureMetni,
                "(Film: ~2 sa | Dizi: ~10 sa)",
                new Color(13, 110, 253)
        ));
        panelGövde.add(Box.createVerticalStrut(10));

        // 3. Kart: Ortalama Puan
        String puanGosterim = (!ortalamaPuanMetni.equals("-") ? "★ " : "") + ortalamaPuanMetni;
        panelGövde.add(olusturKart(
                "Ortalama Puanınız",
                puanGosterim,
                "Puanlanan yapımlar üzerinden",
                new Color(190, 120, 0)
        ));
        panelGövde.add(Box.createVerticalStrut(10));

        // 4. Kart: İzleme Listesindeki Yapımlar
        panelGövde.add(olusturKart(
                "İzleme Listesindeki Yapımlar",
                siradaBekleyen + " Yapım",
                "Sırada izlenmeyi bekliyor",
                new Color(217, 119, 6)
        ));
        panelGövde.add(Box.createVerticalStrut(10));

        // 5. Kart: En Çok Tercih Edilen Tür
        panelGövde.add(olusturKart(
                "En Çok Tercih Edilen Tür",
                tercihTur,
                "İzleme alışkanlıklarınıza göre",
                new Color(102, 16, 242)
        ));

        panelGövde.add(Box.createVerticalGlue());
        return panelGövde;
    }

    private JPanel olusturKart(String baslik, String anaDeger, String altAciklama, Color vurgulamaRengi) {
        JPanel kart = new JPanel(new BorderLayout(10, 0));
        kart.setBackground(Color.WHITE);
        kart.setBorder(new CompoundBorder(
                new LineBorder(RENK_KENARLIK, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        kart.setPreferredSize(new Dimension(430, 65));

        JPanel panelSol = new JPanel(new GridLayout(2, 1, 0, 2));
        panelSol.setOpaque(false);

        JLabel lblBaslik = new JLabel(baslik);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBaslik.setForeground(RENK_ALT_METIN);

        JLabel lblDeger = new JLabel(anaDeger);
        lblDeger.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDeger.setForeground(vurgulamaRengi != null ? vurgulamaRengi : RENK_BASLIK);

        panelSol.add(lblBaslik);
        panelSol.add(lblDeger);
        kart.add(panelSol, BorderLayout.CENTER);

        if (altAciklama != null && !altAciklama.isEmpty()) {
            JLabel lblAlt = new JLabel(altAciklama);
            lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblAlt.setForeground(new Color(120, 130, 140));
            kart.add(lblAlt, BorderLayout.EAST);
        }

        return kart;
    }

    private JPanel olusturAltPaneli() {
        JPanel alt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        alt.setBackground(Color.WHITE);
        alt.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, RENK_KENARLIK));

        JButton btnKapat = new JButton("Kapat");
        btnKapat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKapat.setBackground(new Color(241, 243, 245));
        btnKapat.setForeground(new Color(52, 58, 64));
        btnKapat.setFocusPainted(false);
        btnKapat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKapat.setPreferredSize(new Dimension(100, 34));
        btnKapat.setBorder(new CompoundBorder(
                new LineBorder(new Color(206, 212, 218), 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));
        btnKapat.addActionListener(e -> dispose());

        alt.add(btnKapat);
        return alt;
    }
}
