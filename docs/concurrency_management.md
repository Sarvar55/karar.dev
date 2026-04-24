# Veritabanı Tutarlılığı ve Eşzamanlılık (Concurrency) Yönetimi

Bu döküman, uygulamada verilerin aynı anda (eşzamanlı) güncellenmesi durumunda oluşabilecek veri kayıplarını ve bunları önleme yöntemlerini açıklar.

## 1. Yarış Durumu (Race Condition) Nedir?

Birden fazla kullanıcının aynı veriyi aynı anda değiştirmeye çalışması durumudur. Örneğin:
1.  **Karar A'nın** oy sayısı 100 olsun.
2.  **Kullanıcı 1** ve **Kullanıcı 2** aynı anda "Oy Ver" butonuna basar.
3.  Eğer önlem alınmazsa, her iki kullanıcı da değeri 100 olarak görür, 101'e yükseltir ve kaydeder.
4.  Sonuç: İki oy verilmesine rağmen toplam sayı 102 yerine 101 kalır.

---

## 2. Çözüm Yöntemleri

Uygulamamızda bu sorunu çözmek için iki ana yaklaşım kullanılır:

### A. Atomik Güncellemeler (Atomic Updates)
Veritabanına doğrudan "mevcut değerin üzerine ekle" komutu göndermektir.

**Örnek SQL:**
```sql
UPDATE decisions SET vote_count = vote_count + 1 WHERE id = :id;
```
*   **Nasıl Çalışır?** Veritabanı bu sorguyu işlerken ilgili satırı kilitler, işlemi yapar ve kilidi açar. İkinci sorgu bu sırada bekler ve güncel değer (101) üzerinden devam eder.
*   **Uygulama:** Projemizde `DecisionRepository` içindeki `@Modifying` sorguları bu yöntemi kullanır. Basit sayısal artışlar için en performanslı yöntemdir.

### B. İyimser Kilitleme (Optimistic Locking)
Verinin bir versiyon numarası ile takip edilmesidir.

**Süreç:**
1.  Tabloya bir `version` kolonu eklenir.
2.  Veri okunurken versiyonu ile birlikte okunur (örn: `version: 5`).
3.  Veri kaydedilirken veritabanına şu denir: `"Sadece versiyon hala 5 ise güncelle ve versiyonu 6 yap."`
4.  Eğer araya başka biri girmişse versiyon değişmiş olacağı için işlem reddedilir ve `ObjectOptimisticLockingFailureException` hatası fırlatılır.

---

## 3. Hangisi Ne Zaman Kullanılmalı?

| Durum | Tercih Edilen Yöntem |
| :--- | :--- |
| Sayaç artırma (Oy sayısı, izlenme sayısı vb.) | **Atomik Güncellemeler** |
| Karmaşık veri güncellemeleri (Yazı içeriği, kullanıcı profili vb.) | **Optimistic Locking** |

---

## 4. Uygulama Notları (Spring Boot / JPA)

Optimistic Locking uygulamak için Entity sınıflarına şu alan eklenmelidir:

```java
@Version
private Long version;
```

Bu hata alındığında sistemin çökmemesi için **Retry (Yeniden Deneme)** mekanizmaları kurgulanmalıdır.
