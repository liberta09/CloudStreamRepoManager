# ⚡ CloudStream Repo Manager (v1.0.0)

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material3-yellow.svg)
![Theme](https://img.shields.io/badge/Theme-Cyberpunk%202077-pink.svg)
![Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)

**CloudStream Repo Manager**, CloudStream Android uygulaması için geliştirilmiş fütüristik **Cyberpunk 2077** temalı, merkezi bulut senkronizasyonlu ve tek tıkla eklenti yükleme destekli gelişmiş eklenti kataloğu yöneticisidir.

---

## 📥 APK İndirme Bağlantıları (Bulut Depolama / Google Drive)

Uygulama APK dosyalarını yüksek hızlı ve güvenli bulut depolama servisleri üzerinden indirebilirsiniz:

* 📱 **[Kullanıcı Sürümü APK İndir (Google Drive)](https://drive.google.com/file/d/1_CloudStreamRepoManager_User_v1.0.0/view?usp=sharing)**
  * *Açıklama:* Tüm CloudStream kullanıcıları için tasarlanmış **Sade, Güvenli ve Değiştirilemez** sürümdür.
* 👑 **[Admin (Yönetici) Sürümü APK İndir (Google Drive)](https://drive.google.com/file/d/1_CloudStreamRepoManager_Admin_v1.0.0/view?usp=sharing)**
  * *Açıklama:* Merkezi repo listesini düzenleme, silme, ekleme ve tüm cihazlara yayınlama yetkisine sahip yönetici sürümüdür.

---

## 🚀 Sürüm Notları (v1.0.0 - İlk Resmi Sürüm)

* 🎨 **Cyberpunk 2077 Teması:** `CyberYellow (#FCEE09)`, `CyberCyan (#00F0FF)`, `CyberPink (#FF0055)` ve koyu arka plan tonlarıyla hazırlanan modern Jetpack Compose arayüzü.
* 📦 **11 Hazır Türkçe Repo Kataloğu:** PLT Stream, Kraptor CS-TR, Manitux, WioSpor, SafakStream, Neoser, BTVault, CloudStreamHub, Dr-Octagon, CS Kraptor Aytzey ve Nik CloudStream repoları preloaded olarak dahil edilmiştir.
* ☁️ **Merkezi Bulut Senkronizasyonu (`central_repos.json`):** Uygulama her açıldığında doğrulanmış en güncel bulut verisini çeker ve önbelleği senkronize eder.
* 🔗 **1 Tıkla CloudStream'e Aktarım:** `cloudstreamrepo://` özel derin bağlantı (deep link) protokolü ile eklentileri tek dokunuşla CloudStream'e yükler.
* 🛠️ **Oynatıcı Çözücü Desteği (Extractors):** Vidmoly, Doodstream, Filemoon gibi video oynatıcı çözücülerini yükleyen özel araç.
* 🔐 **Güvenli Admin Paneli & SHA Kontrolü:** PIN şifreli ve GitHub SHA çakışması korumalı bulut yayınlama motoru.
* 📺 **Android TV & Kumanda Uyumluluğu:** TV kumandası odaklanma (Focus Highlight) ve Tam Ekran Siber Açılış Ekranı (Splash Screen) desteği.

---

## 📖 Kurulum Talimatları (Telefon & Android TV)

### 1. Android Telefon ve Tablet Kurulumu:
1. Yukarıdaki **Google Drive** indirme bağlantısına tıklayarak `CloudStreamRepoManager_Kullanici_Surumu.apk` dosyasını telefonunuza indirin.
2. İndirdiğiniz `.apk` dosyasına tıklayın.
3. Eğer uyarı çıkarsa: **`Ayarlar ➔ Bilinmeyen Kaynaklardan Uygulama Yüklemeye İzin Ver`** seçeneğini aktif edin.
4. **Yükle** butonuna basarak kurulumu tamamlayın.

### 2. Android TV & TV Box Kurulumu:
1. TV cihazınıza Google Play Store üzerinden **Send Files to TV** veya **Downloader** uygulamasını kurun.
2. Bilgisayarınızdan veya telefonunuzdan indirdiğiniz APK dosyasını TV'ye gönderin.
3. TV'deki bir dosya yöneticisi (örneğin *AnExplorer* veya *FX File Explorer*) ile `.apk` dosyasını çalıştırıp kurun.

---

## ⚡ CloudStream'e Repo Nasıl Aktarılır?

1. **CloudStream Repo Manager** uygulamasını açın.
2. Listeden yüklemek istediğiniz reponun altındaki **`⚡ CLOUDSTREAM'E AKTAR`** butonuna basın.
3. Cihazınızdaki **CloudStream** uygulaması otomatik olarak açılacaktır.
4. Ekrana gelen **"Bu Eklenti Reposu Eklensin mi?"** uyarısında **"Onayla / Yükle"** seçeneğini seçin.
5. Film, dizi veya TV yayınlarının açılmaması durumunda ana ekrandaki **`🛠️ OYNATMA / LİNK ÇÖZÜCÜ`** butonuna basarak Extractors eklentisini de yükleyin.

---

## 👑 Yönetici (Admin) Paneli Kullanımı

1. Admin Sürümü APK'sını kurduktan sonra sağ üstteki **`🔑 ADMIN GİRİŞİ`** butonuna tıklayın.
2. **Admin PIN Kodu:** `1907` giriniz.
3. Dilediğiniz repoları ekleyin, düzenleyin veya silin.
4. Tüm değişiklikleri kullanıcılara yayınlamak için ana ekrandaki **`💾 GITHUB'A KAYDET`** butonuna basınız.

---

## 📄 Lisans & Katkıda Bulunma

Bu proje açık kaynaklıdır ve **MIT Lisansı** ile lisanslanmıştır. Projeye katkıda bulunmak veya hata bildirmek için [GitHub Repository Issues](https://github.com/liberta09/CloudStreamRepoManager/issues) sayfasını kullanabilirsiniz.
