package com.takip;

import com.takip.gui.GirisEkrani;

import javax.swing.*;

/**
 * Uygulamanın Başlangıç (Entry Point) Sınıfı.
 * 
 * Sistem başlatıldığında doğrudan ana ekranı değil; kimlik doğrulama,
 * kullanıcı kaydı ve güvenli oturum açma işlemlerini yöneten
 * {@link GirisEkrani}'nı açar.
 * 
 * @author Proje Geliştirme Ekibi
 * @version 2.4
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // İşletim sisteminin yerel Look & Feel temasını uygula
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            // Giriş ve Kayıt ekranını başlat
            GirisEkrani girisEkrani = new GirisEkrani();
            girisEkrani.setVisible(true);
        });
    }
}
