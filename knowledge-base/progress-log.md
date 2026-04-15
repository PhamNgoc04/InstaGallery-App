# 📅 Nhật Ký Tiến Độ (Progress Log)

*File này dùng để lưu trữ tiến độ làm việc mỗi ngày, giúp AI và Developer nắm bắt chính xác bối cảnh (context) đang làm dở.*

---

## Ngày 14/03/2026 (Đã hoàn thành lúc 18:55)

### ✅ Đã làm được:
1. **Khởi tạo dự án & Git**: Init repo thành công, đẩy code lên nhánh `development` (repo: `PhamNgoc04/InstaGallery-Android`).
2. **Sửa lỗi UI/Resource**: Fix thành công lỗi `ic_launcher` bằng cách tạo Vector Asset và Image Asset chuẩn.
3. **Kiến trúc (Clean Architecture)**: Hoàn tất tạo 5 thư mục cốt lõi: `core`, `data`, `domain`, `presentation`, `di`.
4. **Hilt Dependency Injection**: Khai báo Application, cấu hình `MainActivity`, tạo module DI đầu tiên.
5. **Core State Management**: Xây dựng thành công class `Resource.kt`.
6. **Chuẩn hóa làm việc (ASH 2.0)**: Tạo file `AGENTS.md`.
7. **Cấu hình Network**: Thay thế Retrofit bằng Ktor Client, cài đặt `ContentNegotiation`, `Logging` thông qua `AppModule`.
8. **Network Error Handling**: Xây dựng `SafeApiCall.kt` bắt gọn các exception của Ktor.
9. **UI & State**: Xây dựng `UiState` interface và `BaseViewModel`.
10. **Compose Theme**: Cấu hình bảng màu chuẩn Instagram (Light/Dark mode), Typography chuẩn Material 3 tại `core/presentation/theme/`.

## Ngày 15/03/2026 (Hoàn thành Phase 3)

### ✅ Đã làm được (Phase 3 - Auth Logic):
1. **Local Storage (DataStore)**: Xây dựng thành công `AuthPreferences.kt` để lưu Token/UserId sử dụng `PreferencesDataStore` và `Flow`.
2. **Dependency Injection**: Cấu hình Hilt `@Module` và `@Binds` kết nối `AuthRepositoryImpl` với `AuthRepository`.
3. **Jetpack Compose UI & UDF**: 
   - Hoàn chỉnh `LoginScreen.kt` bám sát Figma.
   - Xây dựng `AuthViewModel` với `StateFlow` (`AuthUiState`).
   - Kết nối thành công vòng lặp UDF trong Compose bằng `collectAsStateWithLifecycle` và `LaunchedEffect`.
4. **Network Integration (Ktor + Serialization)**:
   - Dựng cấu trúc DTO (`AuthResponse`, `LoginRequest`).
   - Khắc phục lỗi Gradle Plugin Serialization 1.9.0 vs 1.9.23.
   - Refactor `AuthResponse` lồng ghép `AuthDataDto` để khớp với JSON lồng (`data: {...}`) từ Spring Boot Backend.
   - Tích hợp gọi API HTTP Local (`usesCleartextTraffic`, IP `10.0.2.2`). Đăng nhập thành công!

### ✅ Đã làm thêm (Buổi chiều - Fix Dialog Login Error):
5. **Fix lỗi Dialog sai mật khẩu (5 bugs trên 5 files)**:
   - `AuthRepositoryImpl.kt`: Tách riêng bước lấy `HttpResponse` thô → check `status.isSuccess()` TRƯỚC khi parse body → tránh `SerializationException`. Nếu 401 → throw `WrongPasswordException`.
   - `SafeApiCall.kt`: Thêm catch `WrongPasswordException` → trả mã `"wrong_password"` lên ViewModel.
   - `AppModule.kt`: Thêm `expectSuccess = false` để Ktor không tự throw exception cho 4xx/5xx.
   - `AuthViewModel.kt`: Bỏ check `password.length < 6`, để Backend quyết định đúng/sai qua 401. Sửa format emailError.
   - `LoginScreen.kt`: Tách 2 Dialog riêng biệt (Lỗi chung + Sai mật khẩu), bỏ comment `LaunchedEffect` navigation, xoá biến `showWrongPasswordDialog` thừa.
6. **Git**: Đổi tên nhánh `feature/auth-logic` → `feature/auth-login` (cả local và remote).
7. **Cấu hình ADB**: Chạy `adb reverse tcp:8080 tcp:8080` để test trên máy thật qua USB. SDK path: `D:\Android\SDK`.

### 🚧 Việc tiếp theo cho Phase 4 (Main Feed):
- **Cụ thể**:
  1. Tạo Navigation Graph (Chuyển từ LoginScreen sang HomeScreen thành công).
  2. Dựng Layout chuẩn cho HomeScreen (Bottom Navigation, Top Bar).
  3. Cấp quyền truy cập Internet và Ảnh (Permissions).

---
*(Bất cứ khi nào bạn nghỉ, hãy yêu cầu mình cập nhật file này nhé!)*

---

## Ngày 19/03/2026 (Bổ sung Timber + Review context)

### ✅ Đã làm được:
1. **Timber Logging**: Tích hợp `timber:5.0.1` vào toàn bộ luồng Auth:
   - `InstaGalleryApplication.kt`: `Timber.plant(DebugTree())`
   - `SafeApiCall.kt`: Log cho mọi case (success, 401, 4xx, 5xx, timeout, serialization)
   - `AuthRepositoryImpl.kt`: Log từng bước login flow
   - `AuthViewModel.kt`: Log state transitions
   - `AppModule.kt`: Ktor HTTP logger chuyển sang `Timber.tag("KtorHTTP")`

## Ngày 20/03/2026 (Hoàn thành chức năng Đăng ký — Phase 3.5)

### ✅ Đã làm được (Auth Register):
1. **Domain Layer**: Thêm hàm `register()` vào interface `AuthRepository.kt` (5 tham số: email, username, passwordHash, fullName, userType).
2. **Data Layer (DTO)**: Tạo `RegisterRequest.kt` với `@Serializable`, field `userType` mặc định `"CLIENT"`.
3. **Data Layer (Repository)**: Implement `register()` trong `AuthRepositoryImpl.kt`:
   - Gọi `POST /api/v1/auth/register` qua Ktor.
   - Parse error JSON từ Backend bằng Regex để lấy `message` sạch (thay vì trả cả cục JSON thô).
   - Timber log chi tiết cho debug.
4. **Presentation Layer**:
   - `RegisterUiState.kt`: Data class với error fields cho 5 ô (fullName, username, email, password, confirmPassword).
   - `RegisterViewModel.kt`: `validateInput()` kiểm tra đầy đủ (rỗng, định dạng email, password >= 6 ký tự, confirmPassword khớp, username không có khoảng trắng). Hàm `register()` gọi API qua Repository.
   - `RegisterScreen.kt`: UI hoàn chỉnh theo design Figma gồm: Avatar placeholder (vòng tròn + badge camera), 5 OutlinedTextField, Role selector (2 OutlinedCard: Nhiếp ảnh gia / Khách hàng), Gradient Button, link "Đã có tài khoản? Đăng nhập".
   - **Dialog lỗi API**: AlertDialog với style gradient giống LoginScreen, hiện message lỗi sạch từ Backend.
5. **Navigation**: 
   - `Screen.kt`: Đã có sẵn `Screen.Register` route.
   - `AppNavGraph.kt`: Thêm composable route cho RegisterScreen, kết nối `onRegisterSuccess` → `popBackStack()` (quay về Login).
   - `LoginScreen.kt`: Kết nối nút "Register" + nút "Tạo tài khoản mới" (trong error dialog) → navigate sang RegisterScreen.
6. **Gradle ADB Auto-Reverse**: Thêm task `adbReverse` vào `build.gradle.kts`, tự động chạy `adb reverse tcp:8080 tcp:8080` mỗi khi install app lên máy thật (không cần chạy tay nữa).

### ✅ Đã làm thêm (Buổi chiều - System Splash & Unit Test):
7. **System UI (Android 12+ Splash)**: Cấu hình `themes.xml` (ẩn icon mặc định, set màu nền trắng trùng Compose) để tích hợp mượt mà với Splash Screen tùy chỉnh.
8. **Splash Screen UI**: Loại bỏ background gradient & animation fade-in thừa, chỉ giữ lại đúng 1 logo hiển thị tĩnh để giống hệt giao diện Splash của Instagram gốc.
9. **Unit Testing (Foundation)**: Thêm dependencies `MockK`, `Coroutines Test`, `Turbine` vào Gradle; tạo boilerplate `MainDispatcherRule.kt`; viết 3 Test Cases cho `SplashViewModelTest.kt`, giả lập (mocking) thành công `AuthRepository` độc lập hoàn toàn với API & Local Database. Mọi test đều PASS xanh lè.
10. **Học thuật (Clean Architecture)**: Hoàn thành giải thích quy trình chuẩn luồng chảy DTO → Domain Model → Repository → ViewModel → UI thực tế sinh động.

### 🚧 Việc tiếp theo cho Phase 4 (Main Feed):
- **Cụ thể**:
  1. Dựng Layout chuẩn cho HomeScreen (Scaffold + Bottom Navigation + Top Bar).
  2. Tạo BottomNavigationBar (5 tabs: Home, Search, Add Post, Reels, Profile).
  3. Hiển thị Feed giả (LazyColumn + Post items).
  4. Cấp quyền truy cập Ảnh (Permissions).

---

## Ngày 21/03/2026 (Xây dựng PostDetailScreen UI — Phase 4.5)

### ✅ Đã làm được (Post Detail UI + Navigation):
1. **Domain Layer**: Tạo `Comment.kt` data class tại `domain/post/model/` — các field: commentId, userId, username, userAvatar, content, createdAt, likeCount, isLiked.
2. **Domain Layer (Cập nhật)**: Thêm 2 field `isLiked: Boolean = false` và `isSaved: Boolean = false` vào `Post.kt` (giá trị mặc định để không ảnh hưởng code cũ).
3. **Presentation Layer (UiState)**: Tạo `PostDetailUiState.kt` chứa post, comments, isLoading, error.
4. **Presentation Layer (UI Components)**:
   - `CommentItem.kt`: Avatar tròn viền vàng `#FFD700` + username bold + nội dung + "Trả lời" + icon 👍 xanh Facebook `#1877F2` với số like.
   - `PostDetailScreen.kt`: Màn hình hoàn chỉnh gồm:
     - TopBar: nút Back + "Bài đăng" + icon Flag (report).
     - Post Header: avatar viền gradient cam + username + thời gian + nút Share tròn hồng.
     - Post Image: full-width `AsyncImage`.
     - Action Bar: ♥ (toggle like/đen) + 💬 + ↗ + 🔖 (toggle save).
     - Likes count + Caption (full, không giới hạn dòng).
     - Comments section: header "Bình luận (N)" + danh sách `CommentItem`.
     - Bottom Bar: `OutlinedTextField` bo tròn "Viết bình luận..." + nút Send.
5. **Navigation**:
   - `Screen.kt`: Thêm `PostDetail` route với tham số `{postId}` + hàm `createRoute(postId)`.
   - `PostItem.kt`: Thêm callback `onPostClick: () -> Unit` + `.clickable`.
   - `HomeScreen.kt`: Truyền `onPostClick` lambda navigate sang PostDetail.
   - `AppNavGraph.kt`: Thêm composable route PostDetail với `navArgument("postId")` + data giả tạm.
6. **Git**: Tạo nhánh `feature/post-detail`, commit & push thành công lên GitHub.

### 🐛 Lỗi đã gặp & Fix:
- `TextFieldDefaults.outlinedTextFieldColors()` → **DEPRECATED** trong Material 3 → sửa thành `OutlinedTextFieldDefaults.colors()`.
- `OutlinedTextField` không có tham số `contentPadding` → xóa dòng đó.
- `PostItem.onPostClick(postId)` → `postId` không tồn tại trong `PostItem` → đổi callback thành `() -> Unit`.
- `post.location != null` → `location` kiểu `String` (không nullable) → đổi thành `post.location.isNotBlank()`.

### 🚧 ~~Việc tiếp theo~~ → Đã hoàn thành ngày 24/03 (xem bên dưới)

---

## Ngày 24/03/2026 (Tích hợp API cho PostDetailScreen — Phase 4.5 tiếp)

### ✅ Đã làm được (PostDetail API Integration):
1. **Data Layer (DTO)**: Tạo `PostDetailResponse.kt` — wrapper class `@Serializable` chứa `status: String` + `data: PostDto` (tái sử dụng `PostDto` từ `FeedResponse.kt`, không tạo mới).
2. **Domain Layer**: Thêm hàm `getPostById(postId: Int): Post` vào interface `PostRepository.kt`.
3. **Data Layer (Repository)**: Implement `getPostById()` trong `PostRepositoryImpl.kt` — gọi `GET /api/v1/posts/$postId` với Bearer token, parse `PostDetailResponse`, convert `.data.toDomain()`.
4. **Presentation Layer (ViewModel)**: Viết lại `PostDetailViewModel.kt` từ đầu:
   - **Bỏ UseCase layer** (over-engineering cho giai đoạn này), gọi `PostRepository` trực tiếp.
   - Inject `SavedStateHandle` + `PostRepository`.
   - `postId: Int` lấy từ `savedStateHandle`.
   - `loadPostDetail()` dùng `safeApiCall` → update `_uiState` (Loading/Success/Error).
5. **Presentation Layer (UI)**: Refactor `PostDetailScreen.kt`:
   - Đổi tham số từ `post: Post, comments: List<Comment>` → `uiState: PostDetailUiState`.
   - Thêm `when` block xử lý 3 trạng thái: Loading (`CircularProgressIndicator`), Error (`Text` đỏ), Data (LazyColumn giữ nguyên UI cũ).
6. **Navigation**: Cập nhật `AppNavGraph.kt` — xóa data giả, thay bằng `hiltViewModel()` + `collectAsStateWithLifecycle()`.

### 📝 Quyết định thiết kế:
- **Bỏ UseCase layer**: ViewModel gọi trực tiếp Repository. Lý do: logic đơn giản (chỉ fetch data), thêm UseCase là over-engineering. Khi nào logic phức tạp hơn sẽ thêm lại.
- **Dùng `safeApiCall` one-shot**: Set `isLoading = true` trước khi gọi, không xử lý `Resource.Loading` trong `when` (vì `safeApiCall` chỉ trả `Success`/`Error`).

### 🚧 Việc tiếp theo:
- **Cụ thể**:
  1. Build & test trên thiết bị thật với Backend đang chạy.
  2. Tích hợp API load comments.
  3. Tích hợp chức năng Like/Unlike, Save/Unsave bài viết.
  4. Tích hợp chức năng gửi bình luận.

---

## Ngày 07/04/2026 (Hiển thị Nested Comments & Cấu hình Đa thiết bị)

### ✅ Đã làm được:
1. **Domain & Data Layer (DTO)**: Nhận diện cấu trúc Lazy Loading bình luận con từ Backend. Cập nhật `Comment.kt` và `CommentDto` thêm `parentId`, `replyCount` và tái cấu trúc lưu trữ dạng đệ quy con.
2. **UI (Compose)**: Refactor `CommentItem.kt` áp dụng cấu trúc **UI đệ quy (Recursive UI)** để thụt lề và bật/tắt danh sách câu trả lời mượt mà, đúng chuẩn Instagram.
3. **Setup Môi trường Đa thiết bị**: 
   - Thử nghiệm truy cập mạng LAN thực (`192.168.x.x`), xử lý cặn kẽ sự cố chặn Firewalls, cấu hình `network_security_config.xml` vượt lỗi HTTP Cleartext traffic và lỗi mạng cách ly AP Isolation.
   - Vô hiệu hóa thành công task `adbReverse` tự động trong `build.gradle.kts` khi cắm phát sinh >2 thiết bị.
   - Ứng dụng thành công **mô hình 1 Máy Ảo + 1 Máy Thật song song** kết nối chung 1 Backend bằng `127.0.0.1` qua ADB Port Forwarding chỉ định Device ID.

### 🚧 Việc tiếp theo:
- Hoàn thiện luồng Call API lấy comments con (Load Replies) dựa vào `parentId` khi bấm chữ "Xem x câu trả lời".
- Code Form gửi bình luận POST API.

---
*(Bất cứ khi nào bạn nghỉ, hãy yêu cầu mình cập nhật file này nhé!)*

---

## Ngày 15/04/2026 (Triển khai hệ thống WebSockets & Chat Architecture)

### ✅ Đã làm được (Chat Feature - 5 Phases Enterprise Architecture):
1. **Phase 1 (Core Foundation):**
   - Thiết lập Domain Models `Conversation`, `Message` và Enum `MessageStatus` (`SENDING`, `SENT`, `DELIVERED`, `READ`, `FAILED`).
   - Khởi tạo DTO giao tiếp REST/WebSocket: `ConversationDto`, `MessageDto`.
2. **Phase 2 (Offline-First với Room Database):**
   - Config Room DB ở `build.gradle.kts` và `AppDatabase`.
   - Khởi tạo Entities (`ConversationEntity`, `MessageEntity`) cùng `ChatDao` lấy dữ liệu bằng `Flow`.
   - Thiết lập `ChatRepositoryImpl` theo chuẩn Single Source of Truth (dữ liệu luôn đổ từ Room ra tránh giật lag khi tải mạng).
3. **Phase 3 & 4 (Advanced UI/UX & Navigation):**
   - **ConversationListScreen**: Tích hợp giao diện **Skeleton Loading**, list hội thoại hỗ trợ Badge (chưa đọc), hiển thị avatar crop tròn.
   - **ChatDetailScreen**:
     - Cấu hình `Modifier.imePadding()` fix triệt để lỗi đè khung hình khi bật bàn phím.
     - Phân nhóm ngày tháng dạng **Date Header**.
     - Thêm Component `TypingIndicatorBubble` xuất hiện khi đối phương đang gõ chữ.
     - Bóng tin nhắn `MessageBubble` báo lỗi đỏ khi `FAILED` hoặc mờ khi `SENDING`.
   - Đổi "Notifications" thành "Messages" tại `BottomNavBar`. Tạo tuyến đường kết nối `ChatList` và `ChatDetail` ở `AppNavGraph.kt`.
4. **Phase 5 (WebSockets Real-time):**
   - Thiết lập WebSockets Plugin vào Ktor `AppModule`.
   - Đóng gói logic WebSocket vào Component độc lập `ChatSocketService` (chạy ngầm, dùng `SharedFlow`), đấu thẳng vào `ChatRepositoryImpl` để cắm dữ liệu xuống DB.

### 🚧 Việc tiếp theo:
- Hook luồng ChatSocketService vào Backend thật khi Spring Cloud Gateway update các Endpoints WebSockets.
- Thử nghiệm gửi Media qua luồng Chat.
