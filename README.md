<p align="center">
  <img src="docs/Logo_InstaGallery.svg" width="100" alt="InstaGallery Logo"/>
</p>

<h1 align="center">📸 InstaGallery</h1>

<p align="center">
  <b>Nền tảng chia sẻ ảnh chuyên nghiệp dành cho nhiếp ảnh gia & người yêu nhiếp ảnh</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose"/>
  <img src="https://img.shields.io/badge/Architecture-MVVM-FF6B35" alt="MVVM"/>
  <img src="https://img.shields.io/badge/DI-Hilt-2196F3" alt="Hilt"/>
  <img src="https://img.shields.io/badge/API-REST-009688" alt="REST API"/>
</p>

---

## 📋 Giới Thiệu

**InstaGallery** là ứng dụng Android chia sẻ ảnh chuyên nghiệp, được phát triển trong khuôn khổ **Đồ án tốt nghiệp**. Ứng dụng cung cấp đầy đủ các tính năng từ đăng bài, tương tác xã hội, nhắn tin, đến đặt lịch chụp ảnh với nhiếp ảnh gia chuyên nghiệp.

### 🎯 Mục tiêu dự án
- Xây dựng ứng dụng Android hoàn chỉnh với kiến trúc **MVVM + Clean Architecture**
- Tích hợp **RESTful API** backend (Spring Boot + MySQL)
- Áp dụng các công nghệ Android hiện đại: **Jetpack Compose**, **Hilt DI**, **Coroutines**, **Flow**
- Thiết kế UI/UX đẹp mắt, trải nghiệm người dùng mượt mà

---

## ✨ Tính Năng Chính

### 🔐 Xác Thực & Bảo Mật
- Đăng nhập / Đăng ký tài khoản
- Quên mật khẩu (gửi email reset)
- Đổi mật khẩu
- Token-based Authentication (JWT)

### 📸 Bài Đăng & Chia Sẻ
- Tạo bài đăng mới với ảnh từ Gallery/Camera
- Chỉnh sửa & xóa bài đăng
- News Feed với lazy loading
- Chi tiết bài đăng với ảnh full-screen

### ❤️ Tương Tác Xã Hội
- Like / Unlike bài đăng (real-time)
- Bình luận với Bottom Sheet UI
- Follow / Unfollow người dùng
- Xem danh sách Followers & Following

### 👤 Hồ Sơ Cá Nhân
- Trang cá nhân với thống kê (Bài đăng, Followers, Following)
- Chỉnh sửa hồ sơ (avatar, bio, thông tin cá nhân)
- Xem hồ sơ người dùng khác

### 🔍 Khám Phá & Tìm Kiếm
- Trang Khám phá với tag trending
- Tìm kiếm theo: Người dùng, Bài đăng, Tags
- Photo grid layout

### 🔔 Thông Báo
- Thông báo like, comment, follow, booking
- Đánh dấu đã đọc / Đọc tất cả

### 💬 Nhắn Tin
- Danh sách cuộc hội thoại
- Chat real-time với giao diện bubble
- Hỗ trợ emoji & gửi ảnh

### 📅 Đặt Lịch Chụp Ảnh
- Danh sách nhiếp ảnh gia (lọc theo chuyên môn, giá, vị trí)
- Đặt lịch chụp ảnh (chọn ngày, thời lượng, chi tiết)
- Quản lý lịch đặt (Chờ duyệt, Xác nhận, Hoàn thành, Hủy)
- Đánh giá & nhận xét sau buổi chụp

### ⚙️ Cài Đặt
- Chỉnh sửa hồ sơ cá nhân
- Đổi mật khẩu
- Đổi ngôn ngữ (8 ngôn ngữ)
- Đăng xuất

---

## 🏗️ Kiến Trúc & Công Nghệ

### Kiến trúc: MVVM + Clean Architecture

```
┌─────────────────────────────────────────────┐
│                  UI Layer                    │
│   Jetpack Compose + Navigation Component    │
├─────────────────────────────────────────────┤
│               ViewModel Layer               │
│     StateFlow + Coroutines + LiveData       │
├─────────────────────────────────────────────┤
│              Repository Layer               │
│         Business Logic + Caching            │
├─────────────────────────────────────────────┤
│               Data Layer                    │
│       Retrofit + OkHttp + Gson              │
└─────────────────────────────────────────────┘
```

### Tech Stack

| Layer | Công nghệ |
|-------|----------|
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (Dagger) |
| **Networking** | Retrofit 2 + OkHttp 4 |
| **Image Loading** | Coil Compose |
| **Async** | Kotlin Coroutines + Flow |
| **Navigation** | Navigation Compose |
| **Serialization** | Gson + Kotlin Serialization |
| **Auth** | JWT Token + SharedPreferences |
| **Build** | Gradle (Kotlin DSL) |

### Backend (Không nằm trong repo này)

| Công nghệ | Mô tả |
|------------|--------|
| **Framework** | Spring Boot 3.x |
| **Database** | MySQL 8.0 |
| **Auth** | Spring Security + JWT |
| **API** | RESTful API |
| **Storage** | File System / Cloud Storage |

---

## 📁 Cấu Trúc Dự Án

```
app/src/main/java/com/codewithngoc/instagallery/
├── 📂 data/
│   ├── InstaGalleryApi.kt          # Retrofit API interface (28+ endpoints)
│   ├── InstaGallerySession.kt      # Session management (JWT)
│   ├── 📂 model/                   # Data classes & API models
│   ├── 📂 repository/              # Repository pattern
│   └── 📂 remote/                  # API response wrappers
├── 📂 di/
│   └── AppModule.kt                # Hilt dependency injection
├── 📂 ui/
│   ├── 📂 features/
│   │   ├── 📂 auth/                # Login, Register, Forgot Password
│   │   ├── 📂 homefeed/            # Home Feed, Post Detail, Comments, Likes
│   │   ├── 📂 explore/             # Explore & Discovery
│   │   ├── 📂 search/              # Search (Users, Posts, Tags)
│   │   ├── 📂 notifications/       # Notifications
│   │   ├── 📂 messages/            # Conversations & Chat
│   │   ├── 📂 profile/             # My Profile, Settings, Edit Profile
│   │   ├── 📂 userprofile/         # Other User's Profile
│   │   ├── 📂 followers/           # Followers & Following
│   │   ├── 📂 booking/             # Photographer List, Create Booking, Rating
│   │   ├── 📂 newpost/             # Create & Edit Post
│   │   └── 📂 news/                # News Feed
│   ├── 📂 navigation/
│   │   └── NavRoutes.kt            # 28 screen routes
│   └── 📂 theme/                   # App theme & colors
└── MainActivity.kt                  # Entry point
```

---

## 📱 Màn Hình (28 screens)

| # | Nhóm | Màn hình | Mô tả |
|---|------|----------|-------|
| 1 | Auth | Splash | Màn hình khởi động với animation |
| 2 | Auth | Welcome | Màn hình chào mừng |
| 3 | Auth | Đăng nhập | Email + mật khẩu |
| 4 | Auth | Đăng ký | Form đăng ký đầy đủ |
| 5 | Auth | Quên mật khẩu | Reset qua email |
| 6 | Feed | Trang chủ | News feed với lazy loading |
| 7 | Feed | Chi tiết bài đăng | Ảnh full + comments |
| 8 | Feed | Tạo bài đăng | Chọn ảnh + caption + location |
| 9 | Feed | Sửa bài đăng | Edit caption, media |
| 10 | Feed | Tin tức | News articles |
| 11 | Explore | Khám phá | Photo grid + trending tags |
| 12 | Explore | Tìm kiếm | 3 tab: Users, Posts, Tags |
| 13 | Social | Thông báo | Like, comment, follow, booking |
| 14 | Social | Hồ sơ cá nhân | Stats + posts grid |
| 15 | Social | Hồ sơ người khác | Follow/Message/Book buttons |
| 16 | Social | Followers/Following | 2 tab với search |
| 17 | Chat | Danh sách tin nhắn | Conversations + unread |
| 18 | Chat | Chi tiết chat | Bubble UI + send |
| 19 | Booking | Nhiếp ảnh gia | Filter + photographer cards |
| 20 | Booking | Đặt lịch chụp | Calendar + pricing |
| 21 | Booking | Lịch đặt chụp | Status filter + booking list |
| 22 | Booking | Đánh giá | Star rating + review |
| 23 | Settings | Cài đặt | Menu settings |
| 24 | Settings | Chỉnh sửa hồ sơ | Edit all fields |
| 25 | Settings | Đổi mật khẩu | 3 password fields |
| 26 | Settings | Ngôn ngữ | 8 languages |
| 27 | Settings | Đăng xuất | Confirmation dialog |
| 28 | Settings | Sửa bài đăng (Profile) | Edit from profile |

---

## 🚀 Cài Đặt & Chạy

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17+
- Android SDK 34

### Bước chạy

```bash
# 1. Clone repository
git clone https://github.com/PhamNgoc04/InstaGallery-App.git

# 2. Mở project trong Android Studio

# 3. Sync Gradle dependencies

# 4. Cấu hình API base URL trong AppModule.kt
# (Mặc định: http://10.0.2.2:8080/api/)

# 5. Build & Run trên emulator hoặc thiết bị thật
```

---

## 👨‍💻 Tác Giả

| Thông tin | Chi tiết |
|-----------|----------|
| **Họ tên** | Phạm Ngọc |
| **GitHub** | [@PhamNgoc04](https://github.com/PhamNgoc04) |
| **Dự án** | InstaGallery — Ứng dụng chia sẻ ảnh chuyên nghiệp |

---

## 📄 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

<p align="center">
  <b>⭐ Nếu thấy hữu ích, hãy cho project một Star!</b>
</p>
