🏦 Banking System (Java OOP Implementation)
Bu proje, temel Nesne Yönelimli Programlama (OOP) prensiplerini kullanarak geliştirilmiş bir Banka Yönetim Sistemi simülasyonudur. Sistem; farklı hesap türlerini yönetme, hesaplar arası para transferi, risk değerlendirmesi ve işlem geçmişi takibi gibi özellikleri içerir.

🚀 Öne Çıkan Özellikler
Çoklu Hesap Desteği: Cari (Current), Tasarruf (Saving) ve Vadeli (Fixed Deposit) hesap türleri.

Dinamik İşlem Yönetimi: Dosyadan okunan verilerle otomatik hesap oluşturma ve transfer işlemlerini gerçekleştirme.

Gelişmiş İşlem Mantığı:

Current Account: Esnek limitli ek hesap (overdraft) desteği.

Saving Account: Minimum bakiye kontrolü ve ceza sistemi.

Fixed Deposit: Vade tarihi takibi ve erken çekim cezası hesaplama.

Risk Analizi: Her hesap türü için bakiyeye ve işlem sıklığına göre dinamik risk değerlendirmesi.

🛠 Kullanılan OOP Prensipleri
Bu proje, yazılım geliştirme süreçlerindeki temel prensipleri uygulamalı olarak gösterir:

Interface (Arayüz): IOperations arayüzü ile tüm hesaplar için ortak metodlar (Transfer, Deposit, Risk Evaluation) tanımlanmıştır.

Polymorphism (Çok Biçimlilik): Farklı hesap nesneleri IOperations referansı üzerinden yönetilerek kodun esnekliği artırılmıştır.

Encapsulation (Kapsülleme): Hesap verileri private alanlarda saklanmış, erişim kontrollü metodlarla sağlanmıştır.

Abstraction (Soyutlama): Karmaşık bankacılık işlemleri, kullanıcıya basit bir arayüz üzerinden sunulmuştur.
