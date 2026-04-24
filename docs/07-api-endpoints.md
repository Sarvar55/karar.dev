# API Endpoints

## 🔌 Tam Endpoint Listesi

| Endpoint | Method | Controller | Açıklama |
|----------|--------|------------|----------|
| `/api/v1/auth/register` | POST | AuthController | Kullanıcı kaydı |
| `/api/v1/users` | GET | RegularUserController | Tüm kullanıcıları listele |
| `/api/v1/users/{id}` | GET | RegularUserController | Kullanıcı getir |
| `/api/v1/users/{id}` | PUT | RegularUserController | Kullanıcı güncelle |
| `/api/v1/users/{id}` | DELETE | RegularUserController | Kullanıcı sil |
| `/api/v1/decisions` | GET | DecisionController | Tüm kararları listele |
| `/api/v1/decisions/{id}` | GET | DecisionController | Karar getir |
| `/api/v1/decisions/user/{userId}` | GET | DecisionController | Kullanıcı kararları |
| `/api/v1/decisions/regret-level/{level}` | GET | DecisionController | Seviyeye göre filtrele |
| `/api/v1/decisions/tag/{tagId}` | GET | DecisionController | Tag'e göre kararları filtrele |
| `/api/v1/decisions` | POST | DecisionController | Karar oluştur |
| `/api/v1/decisions/{id}` | PUT | DecisionController | Karar güncelle |
| `/api/v1/decisions/{id}` | DELETE | DecisionController | Karar sil |
| `/api/v1/comments` | GET | CommentController | Tüm yorumları listele |
| `/api/v1/comments/{id}` | GET | CommentController | Yorum getir |
| `/api/v1/comments/decision/{decisionId}` | GET | CommentController | Kararın yorumlarını getir |
| `/api/v1/comments/user/{userId}` | GET | CommentController | Kullanıcının yorumlarını getir |
| `/api/v1/comments` | POST | CommentController | Yorum oluştur |
| `/api/v1/comments/{id}` | PUT | CommentController | Yorum güncelle |
| `/api/v1/comments/{id}` | DELETE | CommentController | Yorum sil |
| `/api/v1/votes` | GET | VoteController | Tüm oyları listele |
| `/api/v1/votes/{id}` | GET | VoteController | Oy getir |
| `/api/v1/votes/decision/{decisionId}` | GET | VoteController | Kararın oylarını getir |
| `/api/v1/votes/user/{userId}` | GET | VoteController | Kullanıcının oylarını getir |
| `/api/v1/votes/decision/{decisionId}/count` | GET | VoteController | Oy sayısı ve kullanıcı durumu |
| `/api/v1/votes/check` | GET | VoteController | Kullanıcı oy durumu kontrolü |
| `/api/v1/votes` | POST | VoteController | Karara oy ver |
| `/api/v1/votes/{id}` | DELETE | VoteController | Oyu ID ile sil |
| `/api/v1/votes` | DELETE | VoteController | Oyu kullanıcı/karar ile sil (unvote) |
| `/api/v1/tags` | GET | TagController | Tüm tagleri listele |
| `/api/v1/tags/{id}` | GET | TagController | Tag getir |
| `/api/v1/tags` | POST | TagController | Tag oluştur |
| `/api/v1/tags/{id}` | PUT | TagController | Tag güncelle |
| `/api/v1/tags/{id}` | DELETE | TagController | Tag sil |

---

**Son Güncelleme**: 2026-04-24
