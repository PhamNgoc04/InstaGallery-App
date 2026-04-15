# 🐛 Sổ Lỗi Kỹ Thuật (Error Log)

*File này ghi lại những lỗi (Bugs / Crashes / Exceptions) khó chịu đã gặp và cách chúng ta tìm ra giải pháp để FIX (với mục đích AI tương lai không mất thời gian mổ xẻ lại các lỗi cũ).*

## Format:
- **Ngày**: `[YYYY-MM-DD]` | **Feature**: `[Tên Tính Năng]`
- **Hiện tượng**: `[Ví dụ: Crash app ngay khi mở màn hình Login]`
- **Logcat (snippet)**:
```
[Dán một đoạn vài dòng Logcat lỗi đỏ vào đây]
```
- **Nguyên nhân cốt lõi**: `[Giải thích tại sao lỗi xảy ra]`
- **Cách Fix**: `[Mô tả cách sửa, code snippet...]`

---

## Danh Sách Lỗi Đã Giải Quyết

- **Ngày**: `2026-03-14` | **Feature**: `App Launch`
- **Hiện tượng**: App build không được, báo đỏ rực ở Manifest.
- **Logcat**: `AAPT: error: resource mipmap/ic_launcher not found`
- **Nguyên nhân cốt lõi**: Do Import thẳng file SVG cực kỳ phức tạp (không phải chuẩn Android Vector) vào thư mục drawable rồi set làm `ic_launcher` trong Manifest. Hệ thống Gradle không tự convert được SVG ra launcher mimpap.
- **Cách Fix**: Dùng Image Asset Tool trong Android Studio: Tạo 1 Vector Drawable (từ SVG local) -> Sau đó tạo Image Asset `ic_launcher` (Foreground là file Vector vừa tạo, Background màu trắng). Run lại app bay lỗi.

- **Ngày**: `2026-03-20` | **Feature**: `Git Version Control`
- **Hiện tượng**: Không thể `git merge` qua lại giữa các branch, báo lỗi `overwritten by merge` do xung đột `.gradle` file khóa.
- **Logcat**:
  ```
  error: Your local changes to the following files would be overwritten by merge:
          .gradle/8.6/checksums/checksums.lock
  Please commit your changes or stash them before you merge.
  ```
- **Nguyên nhân cốt lõi**: Do thư mục build tự động `.gradle` và cài đặt IDE `.idea` vô tình bị Git theo dõi. Khi build ở nhánh A, file đổi, nhảy sang nhánh B đòi merge sẽ vấp phải file rác này.
- **Cách Fix**: 
  1. Tạm thời: Chạy `git stash --include-untracked` để giấu đi -> `git merge` nhánh cần -> `git stash drop` để ném bỏ file rác.
  2. Vĩnh viễn: Đảm bảo `.gradle/` và `.idea/` bắt buộc phải có mặt trong file `.gitignore` gốc.

- **Ngày**: `2026-03-21` | **Feature**: `PostDetailScreen UI`
- **Hiện tượng**: Preview không render, màn hình trắng hoàn toàn.
- **Logcat**:
  ```
  None of the following candidates is applicable:
  No parameter with name 'contentPadding' found.
  ```
- **Nguyên nhân cốt lõi**: Dùng `TextFieldDefaults.outlinedTextFieldColors()` — hàm đã bị xóa trong Material 3. Ngoài ra `OutlinedTextField` không có tham số `contentPadding`.
- **Cách Fix**:
  1. Đổi `TextFieldDefaults.outlinedTextFieldColors(...)` → `OutlinedTextFieldDefaults.colors(...)`.
  2. Xóa dòng `contentPadding = PaddingValues(...)`.

- **Ngày**: `2026-03-21` | **Feature**: `Navigation PostItem → PostDetail`
- **Hiện tượng**: Lỗi đỏ compile: `Unresolved reference: postId` trong `PostItem.kt`.
- **Logcat**:
  ```
  e: PostItem.kt: (70, 34): Unresolved reference: postId
  ```
- **Nguyên nhân cốt lõi**: Khai báo `onPostClick: (Int) -> Unit` và gọi `onPostClick(postId)` trong `PostItem`, nhưng `PostItem` KHÔNG có biến `postId`.
- **Cách Fix**: Đổi callback thành `onPostClick: () -> Unit`. Ở HomeScreen bọc lambda: `onPostClick = { onPostClick(post.postId) }`.

- **Ngày**: `2026-03-21` | **Feature**: `PostDetailScreen UI`
- **Hiện tượng**: Compiler warning: `Condition 'post.location != null' is always true`.
- **Nguyên nhân cốt lõi**: Field `location` kiểu `String` (non-nullable), check `!= null` vô nghĩa.
- **Cách Fix**: Đổi thành `if (post.location.isNotBlank())`.

- **Ngày**: `2026-03-24` | **Feature**: `PostDetail API Integration`
- **Hiện tượng**: Lỗi đỏ compile: `Unresolved reference: savedStateHandle` + `Initializer type mismatch: expected 'Int', actual 'MatchGroup'`.
- **Logcat**:
  ```
  e: PostDetailViewModel.kt: Unresolved reference 'savedStateHandle'
  e: PostDetailViewModel.kt: Initializer type mismatch: expected 'Int', actual 'MatchGroup'
  ```
- **Nguyên nhân cốt lõi**: Khi refactor constructor để bỏ UseCase, vô tình xóa luôn `savedStateHandle: SavedStateHandle` khỏi constructor. Nhưng property `postId` ở dòng dưới vẫn gọi `savedStateHandle.get<Int>("postId")` → Kotlin không tìm thấy biến.
- **Cách Fix**: Thêm lại `savedStateHandle: SavedStateHandle` vào constructor (KHÔNG cần `private val` vì chỉ dùng 1 lần khi init). Lưu ý: luôn giữ `savedStateHandle` khi dùng `SavedStateHandle` ở bất kỳ đâu trong ViewModel.

- **Ngày**: `2026-03-24` | **Feature**: `PostDetail API Integration`
- **Hiện tượng**: Uncomment code cũ thay vì viết mới → build 6 lỗi `Unresolved reference` cho UseCase classes.
- **Logcat**:
  ```
  e: Unresolved reference: GetPostDetailUseCase
  e: Unresolved reference: GetPostCommentsUseCase
  e: Unresolved reference: LikePostUseCase (...)
  ```
- **Nguyên nhân cốt lõi**: Code cũ (comment out) dùng 6 UseCase class chưa bao giờ được tạo. Uncomment = import 6 class không tồn tại.
- **Cách Fix**: KHÔNG uncomment code cũ. Viết mới hoàn toàn, inject `PostRepository` trực tiếp thay vì UseCase. Bài học: code comment out là **tham khảo cấu trúc**, không phải code production.

- **Ngày**: `2026-03-24` | **Feature**: `PostDetail API Integration`
- **Hiện tượng**: Import sai path `Resource`: `com.example.instagallery.core.domain.model.Resource` (không tồn tại).
- **Nguyên nhân cốt lõi**: Code cũ (comment out) dùng path sai. Path đúng là `com.example.instagallery.core.utils.Resource`.
- **Cách Fix**: Kiểm tra import path thực tế trong project trước khi dùng. Dùng `Alt+Enter` trong Android Studio để IDE tự resolve đúng path.

- **Ngày**: `2026-04-07` | **Feature**: `Local LAN API Testing`
- **Hiện tượng**: Không thể gọi API bằng máy thật qua Wi-Fi dù IP đúng, báo Exception từ chối kết nối. Lỗi này xảy ra TRƯỚC KHI request đi.
- **Logcat**:
  ```
  java.net.ConnectException: Failed to connect to /192.168.0.105:8080
  AuthVM: State → ERROR: Không có kết nối mạng, vui lòng kiểm tra lại.
  ```
- **Nguyên nhân cốt lõi**: Cấu hình `Cleartext HTTP` không sai nữa, nhưng bị bọc bởi Tường Lửa hệ điều hành (Windows Defender Firewall chặn port 8080) hoặc Router Wi-Fi bật tính năng Wireless/AP Isolation cấm các thiết bị P2P.
- **Cách Fix**: Ưu tiên sử dụng ADB Port Forwarding: giữ vững `127.0.0.1` trong code, cắm cáp USB và dùng lệnh `adb reverse tcp:8080 tcp:8080`. Dữ liệu sẽ đi xuyên cáp vật lý, qua mặt hoàn toàn Wi-Fi LAN.

- **Ngày**: `2026-04-07` | **Feature**: `Multi-Device ADB Reverse`
- **Hiện tượng**: App văng đỏ báo lỗi Gradle Build task ADBReverse khi đang kiểm thử với 2 máy hoạt động (>1 máy).
- **Logcat**:
  ```
  adb.exe: more than one device/emulator
  ```
- **Nguyên nhân cốt lõi**: Script Gradle trong `build.gradle.kts` dùng lệnh `adb reverse` không điểm danh số seri. Giữa lúc mở Máy Ảo + Cắm Máy thật, ADB bị ép chạy không rõ định tuyến tới Device nào nên nó khóa lệnh để an toàn.
- **Cách Fix**: Tắt `finalizedBy("adbReverse")` trong Gradle. Thực thi ADB ngầm qua tay tại Terminal bằng cờ `-s` đính kèm Serial Number lấy từ lệnh `adb devices`: ví dụ `adb -s emulator-5554 reverse tcp:8080 tcp:8080`.
