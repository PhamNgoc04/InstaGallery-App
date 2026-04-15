# 📖 Bài Học Rút Ra (Lessons Learned)

*File này lưu trữ những "Patterns" (Mẫu thiết kế), "Best Practices" và những kinh nghiệm đúc kết được trong quá trình code dự án InstaGallery.*

## 1. Jetpack Compose Patterns
- **Unidirectional Data Flow (UDF)**: Trong Compose, UI rất "ngu", chỉ biết đọc trạng thái từ `UiState` của ViewModel thông qua `collectAsStateWithLifecycle()`. UI bắn sự kiện ngược lên ViewModel (Ví dụ: `onClick { viewModel.login() }`). ViewModel đổi State -> UI Recompose vẽ lại Loading/Error.
- **LaunchedEffect cho Side Effects**: Hiển thị `Toast` hay Navigation (chuyển màn hình) KHÔNG ĐƯỢC để trần trong luồng vẽ của Compose, phải bọc trong `LaunchedEffect(uiState.isSuccess)`. Phải có hàm `clearError()` gọi trong `LaunchedEffect` để dọn dẹp biến String rỗng, chống hiện Toast 2 lần khi xoay màn hình.

## 2. API & Network Patterns
- **Sử dụng `Resource` Wrapper:** Gói data trả về bằng class `Resource` -> Bắt buộc xử lý cả 3 state (Loading/Success/Error) trên UI. Sử dụng **Exhaustive `when`** với `sealed class` để ép developer phải define rẽ nhánh cho đủ 100% các trạng thái của Resource, không lo sót bug.
- **Serialization DTO lồng nhau**: Nếu Backend trả về cục Auth nằm trong object con thay vì nằm phẳng `"data": {"token": "..."}`, `AuthResponse.kt` phải định nghĩa lồng class `AuthDataDto` bên trong thay vì trải phẳng ra ngoài để Ktor ContentNegotiation cắn khớp.

## 3. Database & Local Storage Patterns
- **Lưu ý Local IP Android Emulator**: Gọi API trên thiết bị ảo không được gọi `127.0.0.1` (sẽ loopback vào chính máy ảo), phải đổi về IP Routing: `10.0.2.2`. Gắn quyền `usesCleartextTraffic` tại Manifest để mở HTTP.

## 4. Gỡ Lỗi Mạng & Công Cụ (Networking & Debugging)
- **Lỗi Mất Mạng trên Thiết Bị Thật (Physical Device)**: Máy ảo dùng được `10.0.2.2`, nhưng Máy Thật cắm cáp USB thì KHÔNG. Để Máy Thật gọi được Localhost của Window, phải dùng tuyệt chiêu **ADB Reverse** ép thông luồng cổng sạc USB:
  - B1: Chắc chắn URL trên API trỏ về `127.0.0.1:8080`.
  - B2: Khai báo `<domain includeSubdomains="true">127.0.0.1</domain>` vào file `network_security_config.xml` (Từ API 28+ Google cấm HTTP trơn).
  - B3: Cắm cáp, gõ lệnh Terminal: `adb reverse tcp:8080 tcp:8080`.
  - **🚨 Lỗi "more than one device/emulator"**: Nếu đang bật cả Máy Ảo + Cắm Máy Thật, lệnh trên sẽ sụp đổ vì đụng độ (ADB không biết đào hầm cho máy nào).
  - **Cách trị hỏa mù**: Gõ `adb devices` lấy dải Serial Number của máy thật (VD: *R58N85Y4BMZ*), sau đó gõ lệnh chỉ định đích danh: `adb -s R58N85Y4BMZ reverse tcp:8080 tcp:8080`. Vấn đề hoàn toàn được giải quyết.
  - **Tự động hóa ADB Reverse bằng Gradle**: Thêm task `adbReverse` vào `build.gradle.kts` + `afterEvaluate` hook `installDebug` → tự động chạy `adb reverse` mỗi lần bấm Run ▶️. Không cần gõ tay nữa!

## 5. Error Handling Patterns
- **Parse Error JSON từ Backend bằng Regex**: Khi Backend trả error dạng JSON `{"error":{"code":"...","message":"..."}}`, KHÔNG nên throw cả cục JSON thô lên UI. Dùng Regex đơn giản `"message"\s*:\s*"([^"]+)"` để extract message sạch, tránh tạo thêm DTO class thừa.
- **Compose `var` vs `val` cho mutableStateOf**: Khi khai báo biến input trong Composable, PHẢI dùng `var` (không phải `val`) với `remember { mutableStateOf("") }` để `onValueChange = { biến = it }` hoạt động được. Dùng `val` sẽ không gán lại được giá trị → TextField không bao giờ cập nhật.

29: ## 6. Compose Navigation Patterns
30: - **`popBackStack()` vs `navigate()`**: Khi quay lại màn hình trước (VD: Register quay về Login), dùng `popBackStack()` thay vì `navigate(Screen.Login.route)`. Lý do: `navigate()` thêm 1 LoginScreen mới lên stack → user bấm Back sẽ quay lại RegisterScreen (lạ). `popBackStack()` xóa RegisterScreen khỏi stack → sạch hơn.
31: 
32: ## 7. Testing Patterns
33: - **Test ViewModel với Coroutines**: ViewModel sử dụng `viewModelScope.launch` (chạy trên `Dispatchers.Main`). Khi chạy Unit Test trên máy tính (JVM), không có `Main` dispatcher thực của Android, sẽ gây crash `IllegalStateException`.
34:   - **Giải pháp**: Xây dựng một thư viện Rule nội bộ `TestWatcher` (thường đặt tên là `MainDispatcherRule`) gọi `Dispatchers.setMain(UnconfinedTestDispatcher())` ở hàm `starting()` và `resetMain()` ở hàm `finished()` để đánh lừa luồng xử lý.
35: - **Mocking cho Clean Architecture (MockK)**: Khi test ViewModel, tuyệt đối KHÔNG test xuyên xuống RepositoryImpl (tầng Data), mà phải dùng thư viện `MockK` để làm giả cái Interface của Repository (`coEvery { ... } returns ...`). Việc này đảm bảo ViewModel được test hoàn toàn cô lập với API Server hoặc thiết bị thật.

## 8. Material 3 Compose Pitfalls
- **`TextFieldDefaults.outlinedTextFieldColors()` DEPRECATED**: Trong Material 3, hàm này đã bị xóa. Phải dùng `OutlinedTextFieldDefaults.colors(...)` thay thế. Lỗi này sẽ khiến Preview không render được (compile fail).
- **`OutlinedTextField` không có `contentPadding`**: Khác với `TextField`, `OutlinedTextField` trong M3 không hỗ trợ tham số `contentPadding`. Muốn điều chỉnh padding bên trong phải dùng cách khác (VD: `textStyle`, hoặc bọc trong `Box` với padding).
- **Domain Model KHÔNG dùng `@Serializable`**: Annotation `@Serializable` thuộc thư viện `kotlinx.serialization` → chỉ dùng ở tầng Data (DTO). Domain Model phải sạch, không phụ thuộc thư viện nào.

## 9. Navigation With Arguments
- **Route với tham số**: Khai báo route dạng `"screen/{paramName}"`, dùng `navArgument("paramName") { type = NavType.IntType }` trong composable. Lấy giá trị bằng `it.arguments?.getInt("paramName")`.
- **Helper function `createRoute()`**: Tạo hàm tiện ích trong sealed class `Screen` để build route cụ thể: `fun createRoute(postId: Int) = "post_detail_screen/$postId"`. Tránh hardcode string ở nhiều nơi.
- **Callback `onPostClick` trong PostItem**: Khi cần navigate từ item trong list, truyền callback `() -> Unit` xuống Composable con. PostItem KHÔNG cần biết `postId` — việc đó xử lý ở tầng cha (HomeScreen) nơi có access vào `post.postId`.

## 10. ViewModel Loading State Pattern
- **`safeApiCall` là one-shot**: Hàm `safeApiCall` chỉ trả `Resource.Success` hoặc `Resource.Error`, KHÔNG bao giờ trả `Resource.Loading`. Do đó phải set `isLoading = true` **TRƯỚC** khi gọi `safeApiCall`, không xử lý `Resource.Loading` trong `when(result)`.
- **Khác với Flow-based pattern**: Nếu Repository trả `Flow<Resource<T>>` thì Loading được emit qua stream. Nhưng với suspend one-shot, Loading phải set thủ công.
- **`SavedStateHandle` trong constructor**: Khi ViewModel nhận argument từ Navigation, Hilt tự inject `SavedStateHandle`. Lấy giá trị bằng `savedStateHandle.get<Int>("key")`. Lưu ý: `savedStateHandle` KHÔNG cần `private val` vì chỉ dùng 1 lần khi init.

## 11. DTO Reuse Pattern
- **Tái sử dụng DTO đã có**: Khi Backend trả cùng structure data ở nhiều endpoint (VD: `PostDto` dùng cho cả Feed lẫn PostDetail), chỉ cần tạo wrapper class mới (`PostDetailResponse`) chứa `PostDto`, KHÔNG copy lại fields.

## 12. Recursive UI & Lazy Loading Comments
- **Lazy Loading Model**: Backend hệ thống tin nhắn hoặc mạng xã hội rất hiếm khi lồng toàn bộ bình luận con vào danh sách JSON gốc vì gây lag mạng. Thay vào đó, chúng chỉ nhả về 1 mảng comments cha phẳng, kèm theo `parentId` và `replyCount`. UI phải vẽ thành đệ quy và cấu trúc List rỗng chờ để fetch (Lazy load) khi người dùng chủ động mở xem nhánh con.
- **Recursive Compose**: Trong React hay Compose, hàm Gọi Hàm (gọi lại chính Component đó `CommentItem(comment = child)`) trong `Column` là cách đơn giản và thanh lịch nhất để vẽ một "Cây Gia Phả" (Tree UI) như Bảng Comment phân nhánh có chia thụt lề `padding` tự tăng tiến.

## 13. Advanced Networking (Multi-Device ADB Reverse)
- Khi test API Local, không phải lúc nào Wi-Fi IPv4 LAN cũng chạy trơn tru vì vướng phải Windows Firewall hoặc Cục Modem bật AP Isolation cấm client ping lẫn nhau.
- **Hack thông minh**: Giữ cứng `127.0.0.1:8080` ở cả Máy Thật và Máy Ảo, sau đó cắm cáp gõ lệnh định rõ Serial Number (Lọc ID từ `adb devices`):
  - Emulator: `adb -s emulator-5554 reverse tcp:8080 tcp:8080`
  - Real Device: `adb -s [SERIAL_NO] reverse tcp:8080 tcp:8080`
  Đường cấp ống USB sẽ đào thành hệ thống ống thông lập tức cho mọi số lượng thiết bị, chạy ổn định, nhanh tuyệt đối và miễn nhiễm với Router Network.
