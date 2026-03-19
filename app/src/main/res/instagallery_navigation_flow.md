# 🗺️ InstaGallery — Luồng Điều Hướng

## 1. Auth Flow

```mermaid
flowchart LR
    A["01 Splash"] --> B["02 Login"]
    B -->|"Quên MK"| D["04 ForgotPassword"]
    D -->|"Quay lại"| B
    B -->|"Đăng ký"| C["03 Register"]
    C -->|"Đăng nhập"| B
    C -->|"OK ✅"| E["05 Location"]
    B -->|"Login OK ✅"| F["06 Feed 🏠"]
    E -->|"Bật/Bỏ qua"| F
```

## 2. Home + Explore + Create

```mermaid
flowchart TD
    F["06 Feed 🏠"] -->|"Feed trống"| F2["07 FeedEmpty"]
    F2 -->|"Khám phá"| EX["08 Explore 🔍"]
    F -->|"Tap bài"| PD["10 PostDetail"]
    F -->|"Tap avatar"| UP["16 UserProfile"]
    F -->|"Tap 💬"| MS["19 Messages"]

    EX -->|"Tap search"| SR["09 Search"]
    EX -->|"Tap ảnh"| PD
    SR -->|"Tap user"| UP
    SR -->|"Tap post"| PD

    CR1["11 CreatePost 1"] -->|"Upload"| CR2["12 CreatePost 2"]
    CR2 -->|"Tiếp theo"| CR3["13 CreatePost 3"]
    CR3 -->|"Đăng bài ✅"| F
```

## 3. Profile + Settings

```mermaid
flowchart TD
    MP["15 MyProfile 👤"] -->|"⚙️"| ST["21 Settings"]
    MP -->|"Chỉnh sửa"| EP["22 EditProfile"]
    MP -->|"Tap Followers"| FL["18 Followers"]
    MP -->|"Tap bài"| PD["10 PostDetail"]

    ST -->|"Edit Profile"| EP
    ST -->|"Đổi MK"| CP["23 ChangePassword"]
    ST -->|"Ngôn ngữ"| CL["24 ChangeLanguage"]
    ST -->|"Đăng xuất 🚪"| LG["02 Login"]

    PD -->|"⋮ Sửa"| EDT["17 EditPost"]
```

## 4. Social + Chat

```mermaid
flowchart TD
    UP["16 UserProfile"] -->|"Follow"| UP
    UP -->|"Nhắn tin 💬"| CD["20 ChatDetail"]
    UP -->|"Đặt lịch 📅"| CB["26 CreateBooking"]
    UP -->|"Followers"| FL["18 Followers"]
    UP -->|"Tap bài"| PD["10 PostDetail"]

    FL -->|"Tap user"| UP
    MS["19 Messages"] -->|"Tap chat"| CD
    PD -->|"Tap avatar"| UP

    NF["14 Notifications 🔔"] -->|"❤️ like"| PD
    NF -->|"👤 follow"| UP
    NF -->|"📅 booking"| BL["27 BookingList"]
```

## 5. Booking Flow

```mermaid
flowchart LR
    PL["25 PhotographerList"] -->|"Tap"| UP["16 UserProfile"]
    UP -->|"Đặt lịch"| CB["26 CreateBooking"]
    CB -->|"OK ✅"| BL["27 BookingList"]
    BL -->|"Tap"| BD["BookingDetail"]
    BD -->|"Nhắn tin"| CD["20 ChatDetail"]
    BD -->|"Hoàn thành"| RT["28 Rating ⭐"]
```

---

## Bottom Navigation

```
┌──────────┬──────────┬──────────┬──────────┬──────────┐
│ 🏠 Home  │ 🔍 Find  │ ➕ Post  │ 🔔 Notif │ 👤 Me    │
│ 06 Feed  │ 08 Expl  │ 11~13    │ 14 Notif │ 15 Prof  │
└──────────┴──────────┴──────────┴──────────┴──────────┘
```

## Bảng chi tiết

| Từ | Hành động | Đến |
|---|---|---|
| 01 Splash | Tự chuyển 2s | 02 Login |
| 02 Login | Login OK | 06 Feed |
| 02 Login | "Quên MK?" | 04 ForgotPassword |
| 02 Login | "Đăng ký" | 03 Register |
| 03 Register | Đăng ký OK | 05 Location |
| 05 Location | Bật/Bỏ qua | 06 Feed |
| 06 Feed | Tap bài | 10 PostDetail |
| 06 Feed | Tap avatar | 16 UserProfile |
| 06 Feed | Tap 💬 | 19 Messages |
| 07 FeedEmpty | "Khám phá" | 08 Explore |
| 08 Explore | Tap search | 09 Search |
| 08 Explore | Tap ảnh | 10 PostDetail |
| 09 Search | Tap user | 16 UserProfile |
| 10 PostDetail | Tap avatar | 16 UserProfile |
| 10 PostDetail | ⋮ Sửa | 17 EditPost |
| 11→12→13 | Tạo bài | 06 Feed |
| 14 Notif | Tap like | 10 PostDetail |
| 14 Notif | Tap follow | 16 UserProfile |
| 14 Notif | Tap booking | 27 BookingList |
| 15 MyProfile | ⚙️ | 21 Settings |
| 15 MyProfile | "Chỉnh sửa" | 22 EditProfile |
| 15 MyProfile | Followers | 18 Followers |
| 16 UserProfile | "Nhắn tin" | 20 ChatDetail |
| 16 UserProfile | "Đặt lịch" | 26 CreateBooking |
| 18 Followers | Tap user | 16 UserProfile |
| 19 Messages | Tap chat | 20 ChatDetail |
| 21 Settings | Edit Profile | 22 EditProfile |
| 21 Settings | Đổi MK | 23 ChangePassword |
| 21 Settings | Ngôn ngữ | 24 ChangeLanguage |
| 21 Settings | Đăng xuất | 02 Login |
| 26 CreateBooking | OK ✅ | 27 BookingList |
| 27 BookingList | Tap | BookingDetail |
| BookingDetail | Hoàn thành | 28 Rating |
