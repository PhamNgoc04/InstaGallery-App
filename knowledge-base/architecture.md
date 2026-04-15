# 🏗️ Kiến Trúc Hệ Thống (Architecture)

*File này làm tài liệu tham khảo nhanh cho AI về toàn bộ cấu trúc kiến trúc của dự án InstaGallery.*

## 1. Clean Architecture Layers
- **Core**: Chứa các class cơ sở (`BaseViewModel`), Utilities (`Resource`, Extensions), Theme, và Constants.
- **Data**: Chứa DTOs (Data Transfer Objects), Remote (Ktor/Retrofit), Local (Room, DataStore), và Implementation của các Repository.
- **Domain**: Nòng cốt của ứng dụng. Chứa Models (Entities nguyên thủy), Repository Interfaces, và UseCases. HOÀN TOÀN KHÔNG CHỨA logic của Android Framework.
- **Presentation**: Chứa Màn hình UI (Jetpack Compose), ViewModels (StateFlow), UI States, và Navigation.
- **DI (Dependency Injection)**: Chứa các Modules của Hilt cung cấp Instances cho toàn bộ app (`AppModule`, `NetworkModule`, `DatabaseModule`).

## 2. Luồng Dữ Liệu (Data Flow) Đơn Hướng (UDF)
UI (Gửi Intent/Event) -> ViewModel (Xử lý logic, gọi UseCase) -> UseCase (Xử lý nghiệp vụ) -> Repository Interface -> Repository Impl (Lấy data từ Network hoặc Local DB) -> Trả về `Resource<T>` -> ViewModel (Cập nhật StateFlow) -> UI (Recompose).

## 3. Libraries Cốt Lõi
- DI: Dagger Hilt
- Network: Ktor Client
- Async/Reactive: Kotlin Coroutines & Flow
- Image Loading: Coil
- DB: Room Database
- Local Storage: Jetpack DataStore
