# Veritabanı Şeması

## 🗄️ Entity İlişki Diyagramı

```
┌─────────────────────┐     ┌─────────────────────┐
│   RegularUser       │     │   CompanyUser       │
├─────────────────────┤     ├─────────────────────┤
│ id (UUID, PK)       │     │ id (UUID, PK)       │
│ email               │     │ email               │
│ password            │     │ password            │
│ username            │     │ companyName         │
│ role                │     │ role                │
│ createdAt           │     │ createdAt           │
│ updatedAt           │     │ updatedAt           │
└──────────┬──────────┘     └─────────────────────┘
           │
           │ 1:N
           ▼
┌─────────────────────┐     ┌─────────────────────┐
│      Decision       │     │       Vote          │
├─────────────────────┤     ├─────────────────────┤
│ id (UUID, PK)       │◄────┤ id (UUID, PK)       │
│ title               │ 1:N │ decision_id (FK)    │
│ why                 │     │ user_id (FK)          │
│ alternative         │     │ voteType              │
│ regretLevel         │     └─────────────────────┘
│ voteCount           │
│ user_id (FK)        │
└──────────┬──────────┘
           │
           │ 1:N
           ▼
┌─────────────────────┐
│      Comment        │
├─────────────────────┤
│ id (UUID, PK)       │
│ content             │
│ decision_id (FK)    │
│ user_id (FK)        │
└─────────────────────┘

┌─────────────────────┐     ┌─────────────────────┐
│    DecisionTag      │     │        Tag          │
│   (Junction Table)  │     ├─────────────────────┤
├─────────────────────┤     │ id (UUID, PK)       │
│ decision_id (PK,FK) │◄────┤ name                │
│ tag_id (PK,FK)      │────►└─────────────────────┘
└─────────────────────┘
```

## 📋 Entity Açıklamaları

| Entity | Açıklama |
|--------|----------|
| **User** | Abstract temel kullanıcı sınıfı |
| **RegularUser** | Bireysel kullanıcı (username ile) |
| **CompanyUser** | Kurumsal kullanıcı (companyName ile) |
| **Decision** | Kullanıcıların paylaştığı kararlar |
| **Tag** | Karar kategorileri |
| **DecisionTag** | Decision-Tag çoka çok ilişki tablosu |
| **Vote** | Kararlara verilen oylar |
| **Comment** | Kararlara yapılan yorumlar |

---

**Son Güncelleme**: 2026-04-24
