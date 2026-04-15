# 📓 Ghi Chú Quyết Định Kỹ Thuật (Dev Notes & ADR)

*File này giống như sổ tay của riêng bạn (Developer) và Tech Lead (AI). Dùng để ghi lại tại sao ngày xưa ta lại chọn công nghệ A thay vì công nghệ B.*

## Danh Sách Quyết Định

### 1. Chọn Ktor Client thay vì Retrofit (Dự kiến)
- **Ngày quyết định**: 2026-03-14
- **Lý do chọn**: 
  - Ktor Client được viết 100% bằng Kotlin (Multiplatform ready), hỗ trợ Coroutines Native, cấu hình cực kỳ gọn nhẹ bằng DSL thay vì phải dùng Annotation lằng nhằng như Retrofit.
  - Phù hợp với định hướng hệ sinh thái Kotlin thuần của Android hiện đại.
- **Hệ quả**: Tất cả API calls trong Data Layer sẽ dùng `HttpClient` của Ktor.

### 2. Chọn Hilt thay vì Koin
- **Ngày quyết định**: Trước 2026-03-14
- **Lý do chọn**: Hilt là chuẩn DI (Dependency Injection) chính thức của Google, được Android Studio support tận răng sinh code lúc Compile Time (Compile-time safety), performance tốt hơn lúc Runtime so với Koin.
- **Hệ quả**: Tất cả classes phải được gắn `@Inject`, Modules gắn `@Module`.

### 3. Khóa phiên bản Activity Compose và Lifecycle Runtime
- **Ngày quyết định**: 2026-03-15
- **Sự cố**: Khi build app bằng Gradle, xảy ra xung đột: `androidx.activity:activity-compose:1.9.0` và `androidx.lifecycle:lifecycle-runtime-compose:2.10.0` ép buộc dự án phải compile với **Android SDK 35 (Android 15)** và cấu hình **AGP > 8.6.0**.
- **Giải pháp**: Dự án đang chạy Native SDK 34 (Android 14) + AGP 8.4.1. Quyết định **hạ cấp** thư viện thay vì nâng cấp SDK toàn dự án để tránh bất ổn định hiện tại:
  - `androidx.activity:activity-compose:1.8.2`
  - `androidx.lifecycle:lifecycle-runtime-compose:2.7.0`

### 4. Tách RegisterViewModel riêng thay vì mở rộng AuthViewModel
- **Ngày quyết định**: 2026-03-20
- **Lý do chọn**: `AuthViewModel` đã chứa logic login phức tạp (validate, login, clearError, wrong_password handling). Nếu nhồi thêm register vào sẽ vi phạm nguyên tắc **Single Responsibility** và làm file phình to, khó maintain.
- **Hệ quả**: Tạo `RegisterViewModel.kt` riêng + `RegisterUiState.kt` riêng trong package `presentation/auth/register/`. Cả 2 ViewModel đều inject cùng `AuthRepository` interface.

### 5. Tự động ADB Reverse qua Gradle Task
- **Ngày quyết định**: 2026-03-20
33: - **Sự cố**: Mỗi lần test trên máy thật phải chạy tay `adb reverse tcp:8080 tcp:8080`, rất hay quên dẫn đến lỗi `ConnectException`.
34: - **Giải pháp**: Thêm Gradle task `adbReverse` vào `build.gradle.kts`, hook vào `installDebug` bằng `finalizedBy`. Mỗi lần bấm Run ▶️ sẽ tự động forward port. Có `try-catch` nên không crash build khi không cắm USB.
35: 
36: ### 6. Cấu hình Splash Screen kiểu tĩnh (Static Logo)
37: - **Ngày quyết định**: 2026-03-20
38: - **Sự cố**: Dev thường thêm animation fade-in vào Splash Compose. Nhưng trên Android 12+, hệ điều hành đã tự mở 1 cái System Splash trước khi app kịp load. Việc này tạo ra một "độ chớp trắng" gián đoạn khó chịu.
39: - **Giải pháp**: Gán thẳng Logo của app lên System Splash qua `themes.xml` (`windowSplashScreenAnimatedIcon`). Sau đó, gỡ BỎ HOÀN TOÀN animation bên trong Jetpack Compose. 
40: - **Kết quả**: App mở lên cực kì nguyên bản (Native) hệt Instagram.

### 7. Bỏ UseCase layer, ViewModel gọi Repository trực tiếp
- **Ngày quyết định**: 2026-03-24
- **Sự cố**: `PostDetailViewModel` cũ inject 6 UseCase chưa tồn tại → build fail.
- **Giải pháp**: Bỏ UseCase. ViewModel inject `PostRepository` trực tiếp, bọc trong `safeApiCall`.
- **Lý do**: Logic đơn giản (chỉ fetch data). UseCase lúc này là over-engineering. Khi logic phức tạp hơn sẽ thêm lại.
- **Hệ quả**: ViewModel gọn ~40 dòng, ít dependency, dễ test.

### 8. Tái sử dụng DTO thay vì tạo mới
- **Ngày quyết định**: 2026-03-24
- **Giải pháp**: `PostDetailResponse` chỉ là wrapper nhỏ (`status` + `data: PostDto`), tái sử dụng `PostDto` từ `FeedResponse.kt`.
- **Lý do**: Tránh code trùng lặp. Nếu Backend đổi field, chỉ cần sửa 1 chỗ (`PostDto`).

### 9. Vô hiệu hóa "adbReverse" Task tự động trong Gradle
- **Ngày quyết định**: 2026-04-07
- **Sự cố**: Script tự chạy `adb reverse` trước bị báo lỗi `adb.exe: more than one device/emulator` khi Developer mở >1 thiết bị (vừa dev máy áo, vừa test máy thật).
- **Giải pháp**: Comment vô hiệu khóa vòng lặp `finalizedBy("adbReverse")` trong `app/build.gradle.kts`.
- **Hệ quả**: Quyền quyết định thông port `reverse` sẽ được coder thao tác tĩnh bằng Terminal khi thiết lập môi trường dev riêng. Code base luôn an toàn và sạch sẽ tiến trình.
