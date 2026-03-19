# 📘 BỘ CHUẨN TOÀN DIỆN ASH 2.0
# Agents + Skills + Hooks + Context Engineering + Brainstorming + OpenSpec + MCP
## Phiên Bản 2.0 • Cập Nhật: 2026-03-09

> **Nâng cấp từ ASH 1.0** — Bổ sung 4 trụ cột mới dựa trên xu hướng AI coding hiện đại nhất.
> Mỗi phần có: Là gì → Tại sao cần → Template → Ví dụ → Sai lầm cần tránh.

---

# MỤC LỤC

**NỀN TẢNG (từ ASH 1.0):**
1. [Tổng quan ASH 2.0](#-phần-0-tổng-quan-ash-20)
2. [AGENTS — Bộ não dự án](#-phần-1-agents--bộ-não-dự-án)
3. [SKILLS — Kỹ năng chuyên biệt (Agent Skills Open Standard)](#-phần-2-skills--kỹ-năng-chuyên-biệt)
4. [HOOKS — Bảo vệ & tự động hóa](#-phần-3-hooks--bảo-vệ--tự-động-hóa)

**MỚI TRONG 2.0:**
5. [CONTEXT ENGINEERING — Thiết kế thông tin cho AI](#-phần-4-context-engineering--thiết-kế-thông-tin)
6. [BRAINSTORMING — Bão não ý tưởng](#-phần-5-brainstorming--bão-não-ý-tưởng)
7. [OPENSPEC — Phát triển theo đặc tả](#-phần-6-openspec--phát-triển-theo-đặc-tả)
8. [MCP INTEGRATION — Kết nối công cụ bên ngoài](#-phần-7-mcp--kết-nối-công-cụ-bên-ngoài)

**TỔNG HỢP:**
9. [Cấu trúc thư mục ASH 2.0 hoàn chỉnh](#-phần-8-cấu-trúc-thư-mục-hoàn-chỉnh)
10. [Câu chuyện 60 ngày với ASH 2.0](#-phần-9-câu-chuyện-60-ngày)
11. [Lộ trình 8 tuần](#-phần-10-lộ-trình-8-tuần)
12. [FAQ ASH 2.0](#-phần-11-faq)

---
---

# 🔷 PHẦN 0: TỔNG QUAN ASH 2.0

## Từ 4 thành phần → 8 thành phần

```
┌──────────────────────────────────────────────────────────────────────┐
│                      DỰ ÁN CỦA BẠN (ASH 2.0)                      │
│                                                                      │
│  ═══════════ NỀN TẢNG (ASH 1.0) ═══════════                        │
│  🧠 AGENTS      🎯 SKILLS      🛡️ HOOKS      📚 KB                │
│  AI là ai?      AI làm gì?    AI bị giới    Bộ nhớ                 │
│  Theo rules     Theo steps    hạn thế nào?  dài hạn                │
│  nào?           nào?          Chặn/cho                              │
│                                phép gì?                              │
│                                                                      │
│  ═══════════ MỚI TRONG 2.0 ═══════════                             │
│  🧪 CONTEXT     💡 BRAIN-     📋 OPENSPEC    🔌 MCP                │
│  ENGINEERING    STORMING                     INTEGRATION             │
│  ──────────     ──────────    ──────────     ──────────              │
│  AI nhận        Bão não       Spec-driven   AI kết nối              │
│  thông tin      ý tưởng       development   công cụ bên             │
│  gì, khi nào,   trước khi     Định nghĩa    ngoài (DB,             │
│  định dạng      viết spec     trước, code   API, File...)           │
│  thế nào?       hay code      sau           qua giao thức           │
│                                              chuẩn                   │
│  ──────────     ──────────    ──────────     ──────────              │
│  "Kiến trúc     "Họp bàn      "Bản vẽ       "Ổ cắm                 │
│   sư thông      trước khi     trước khi     điện vạn                │
│   tin"           ra quyết      xây nhà"      năng"                   │
│                   định"                                              │
└──────────────────────────────────────────────────────────────────────┘
```

## Bảng so sánh 8 thành phần

| # | Thành phần | Là gì? | AI đọc khi nào? | File chính | Tạo khi nào? |
|---|-----------|--------|-----------------|-----------|-------------|
| 1 | **AGENTS** | Rules cho AI | TỰ ĐỘNG mỗi lần chat | `AGENTS.md` | Ngày 1 |
| 2 | **SKILLS** | Hướng dẫn từng bước (chuẩn quốc tế) | KHI CẦN (task phù hợp) | `SKILL.md` | Khi lặp task lần 2 |
| 3 | **HOOKS** | Cơ chế kiểm soát | TỰ ĐỘNG (chặn/cho phép) | `settings.json` + workflows | Khi AI sai/quên |
| 4 | **KB** | Bộ nhớ dài hạn | KHI CẦN (tra cứu) | `knowledge-base/*.md` | Mỗi ngày |
| 5 | 🆕 **CONTEXT ENG.** | Thiết kế thông tin cho AI | TRƯỚC mỗi task lớn | `context/` + `.context.md` | Khi task phức tạp |
| 6 | 🆕 **BRAINSTORMING** | Bão não ý tưởng | TRƯỚC khi viết spec/code | `brainstorms/*.md` | Khi cần khám phá ý tưởng |
| 7 | 🆕 **OPENSPEC** | Đặc tả trước khi code | TRƯỚC khi implement | `specs/*.md` | Khi tạo feature mới |
| 8 | 🆕 **MCP** | Kết nối công cụ ngoài | KHI CẦN (tool call) | `mcp-config.json` | Khi cần tích hợp |

## ASH 1.0 vs ASH 2.0 — Khác nhau ở đâu?

| Vấn đề | ASH 1.0 | ASH 2.0 |
|--------|---------|---------|
| AI nhận context thiếu/thừa | ❌ Chưa giải quyết | ✅ Context Engineering |
| Không biết làm gì, ý tưởng mơ hồ | ❌ Nhảy vào code luôn | ✅ Brainstorming (khám phá trước) |
| AI code không đúng ý → phải sửa nhiều | ❌ Chỉ có rules | ✅ OpenSpec (spec trước, code sau) |
| AI chỉ làm việc trong editor | ❌ Không kết nối ngoài | ✅ MCP (database, API, Git...) |
| Skills format khác nhau giữa tools | ❌ Mỗi tool 1 kiểu | ✅ Agent Skills Open Standard |
| Phải review code line-by-line | ❌ Không có cơ chế | ✅ Spec → Implement → Validate |

---
---

# 🧠 PHẦN 1: AGENTS — Bộ Não Dự Án

> *(Giữ nguyên từ ASH 1.0, bổ sung link Context Engineering)*

## 1.1. Là gì?

> **AGENTS = Tập hợp quy tắc mà AI PHẢI tuân theo khi làm việc trong dự án.**

Giống "Hiến Pháp Quốc Gia" — AI đọc TỰ ĐỘNG mỗi lần bạn chat.

## 1.2. Cấu trúc phân cấp (Hierarchical)

```
📁 project-root/
├── AGENTS.md               ← "Hiến pháp" — rules TOÀN DỰ ÁN
├── src/
│   ├── AGENTS.md            ← Rules riêng cho source code
│   └── ui/
│       └── AGENTS.md        ← Rules riêng cho UI
└── tests/
    └── AGENTS.md            ← Rules riêng testing
```

**Quy tắc:** File GẦN hơn → ưu tiên cao hơn. AI đọc từ GẦN ra XA.

## 1.3. Template chuẩn (Root AGENTS.md)

```markdown
# AGENTS.md — [TÊN DỰ ÁN]

## 1. Tổng Quan
- **Tên**: [MyShop — App bán hàng]
- **Nền tảng**: [Android]
- **Ngôn ngữ**: [Kotlin]
- **Trạng thái**: [MVP]

## 2. Tech Stack
- **UI**: [Jetpack Compose + Material 3]
- **Architecture**: [MVVM + Clean Architecture]
- **DI**: [Hilt]
- **Network**: [Retrofit]
- **Database**: [Room]

## 3. Quy Tắc BẮT BUỘC (DO)
- LUÔN viết spec trước khi implement (xem specs/)
- LUÔN dùng StateFlow cho UI state
- LUÔN handle Loading/Success/Error
- LUÔN ghi error-log sau khi fix bug

## 4. Điều CẤM (DON'T)
- KHÔNG code feature mới khi chưa có spec
- KHÔNG dùng !! (force unwrap)
- KHÔNG hardcode URL/secrets
- KHÔNG commit trực tiếp lên main

## 5. Context Rules (MỚI 2.0)
- LUÔN đọc specs/ trước khi implement feature
- LUÔN đọc knowledge-base/error-log.md trước khi fix bug
- KHI context dài → tóm tắt trước, chi tiết sau

## 6. Naming Conventions
| Loại | Quy tắc | Ví dụ |
|------|---------|-------|
| Screen | PascalCase | LoginScreen |
| ViewModel | +ViewModel | LoginViewModel |
| UseCase | Verb+ | GetProductsUseCase |
| Spec file | kebab-case | login-feature.spec.md |

## 7. Liên Kết
- Knowledge Base: `knowledge-base/`
- Specs: `specs/`
- MCP Config: `.mcp/config.json`
```

## 1.4. Bảng mapping đa công cụ

| AI Tool | File name | Nội dung |
|---------|-----------|----------|
| **Gemini** | `AGENTS.md` | Giống nhau |
| **Claude Code** | `CLAUDE.md` | Giống nhau |
| **Cursor** | `.cursor/rules/*.md` | Giống nhau |
| **Copilot** | `.github/copilot-instructions.md` | Giống nhau |
| **Windsurf** | `.windsurfrules` | Giống nhau |

> 💡 **Viết 1 lần → copy sang tất cả!**

---
---

# 🎯 PHẦN 2: SKILLS — Kỹ Năng Chuyên Biệt
## *(Nâng cấp: Agent Skills Open Standard — agentskills.io)*

## 2.1. Là gì?

> **SKILLS = Hướng dẫn từng bước cho 1 tác vụ cụ thể, tuân theo chuẩn quốc tế.**

**Tin tốt:** Từ 12/2025, `SKILL.md` đã trở thành **chuẩn mở** — được **OpenAI, Microsoft, GitHub, Google** cùng áp dụng. Bạn viết 1 skill → dùng được trên MỌI AI tool!

## 2.2. Mới trong 2.0: Chuẩn agentskills.io

### Progressive Disclosure (Tải từng phần)

```
AI khởi động:
  ① Chỉ đọc name + description (~100 tokens) — TẤT CẢ skills
       ↓
  Bạn yêu cầu task phù hợp:
  ② Đọc toàn bộ SKILL.md body (<5000 tokens) — SKILL liên quan
       ↓
  Cần thêm chi tiết:
  ③ Đọc scripts/, references/, assets/ — KHI CẦN
```

→ **Tiết kiệm token, AI nhanh hơn, chính xác hơn!**

### Cấu trúc thư mục Skill (Chuẩn quốc tế)

```
.agent/skills/
├── create-feature/
│   ├── SKILL.md              ← BẮT BUỘC (hướng dẫn chính)
│   ├── scripts/              ← Tùy chọn (script hỗ trợ)
│   │   └── scaffold.py
│   ├── references/           ← Tùy chọn (tài liệu tham khảo)
│   │   └── architecture.md
│   └── assets/               ← Tùy chọn (file mẫu)
│       └── template.kt
├── debug-error/
│   └── SKILL.md
└── write-spec/               ← MỚI 2.0: Skill viết spec
    └── SKILL.md
```

## 2.3. Template SKILL.md (Chuẩn agentskills.io)

```markdown
---
name: create-feature
description: >
  Tạo feature mới theo Clean Architecture. Sử dụng khi cần thêm
  tính năng mới bao gồm data layer, domain layer, và presentation layer.
  Bắt buộc phải có spec trước khi implement.
license: Apache-2.0
metadata:
  author: your-team
  version: "2.0"
---

# Create Feature

## Khi Nào Dùng
- Tạo feature mới (lần 2 trở đi)
- Cần quy trình chuẩn: spec → implement → test

## Điều Kiện Trước
1. Đọc `AGENTS.md` (root)
2. Đọc `knowledge-base/error-log.md`
3. **Kiểm tra:** Đã có spec trong `specs/` chưa?
   - CHƯA → Chạy skill `write-spec` trước
   - RỒI → Đọc spec → Tiếp tục

## Các Bước

### Bước 1: Đọc Spec
- Đọc file `specs/[feature-name].spec.md`
- Xác nhận scope và acceptance criteria

### Bước 2: Data Layer
- Tạo Entity, DAO/Repository interface
- Xem mẫu: [references/data-layer.md](references/data-layer.md)

### Bước 3: Domain Layer
- Tạo UseCase classes
- Inject Repository qua constructor

### Bước 4: Presentation Layer
- Tạo ViewModel (StateFlow + sealed UiState)
- Tạo Screen (@Composable)

### Bước 5: DI Module
- Register trong Hilt module

### Bước 6: Test
- Unit test cho UseCase + ViewModel
- Script kiểm tra: [scripts/run-tests.sh](scripts/run-tests.sh)

### Bước 7: Cập nhật Knowledge Base
- Ghi vào `knowledge-base/lesson.md`
- Nếu có lỗi → ghi `knowledge-base/error-log.md`

## Kết Quả Mong Đợi
- [ ] Feature hoạt động đúng spec
- [ ] Tất cả test pass
- [ ] Knowledge Base đã cập nhật

## Lưu Ý
- KHÔNG skip Bước 1 (đọc spec)
- KHÔNG tạo feature nếu chưa có spec
```

## 2.4. Quy tắc đặt tên Skill (Bắt buộc)

| Quy tắc | ✅ Đúng | ❌ Sai |
|---------|---------|-------|
| Chữ thường + gạch ngang | `create-feature` | `CreateFeature` |
| 1-64 ký tự | `debug-error` | `a-very-long-skill-name-that-exceeds-sixty-four-characters-limit` |
| Không bắt đầu/kết thúc bằng `-` | `code-review` | `-code-review` |
| Không có `--` liên tiếp | `write-test` | `write--test` |
| Tên thư mục = tên trong frontmatter | `name: create-feature` trong `create-feature/SKILL.md` | Tên khác nhau |

---
---

# 🛡️ PHẦN 3: HOOKS — Bảo Vệ & Tự Động Hóa

## 3.1. Là gì?

> **HOOKS = Cơ chế tự động can thiệp vào luồng Người ↔ AI.**

Giống bảo vệ + camera an ninh — tự động kiểm soát, bạn không cần canh.

## 3.2. Ba loại Hook

```
[Bạn gõ tin nhắn]
       ↓
  ╔═══════════════════╗
  ║ 1. SUBMIT HOOK    ║ ← AI tự đọc rules (AGENTS.md, styleguide)
  ╚═══════════════════╝
       ↓
  [AI xử lý + muốn chạy lệnh]
       ↓
  ╔═══════════════════╗
  ║ 2. TOOL HOOK      ║ ← Kiểm tra: lệnh này allow hay deny?
  ╚═══════════════════╝
       ↓
  [Cần chạy quy trình nhiều bước]
       ↓
  ╔═══════════════════╗
  ║ 3. WORKFLOW HOOK  ║ ← Chạy đúng quy trình, không bỏ bước
  ╚═══════════════════╝
```

## 3.3. Khi nào dùng?

| Tình huống | Loại Hook | Tạo gì |
|-----------|----------|--------|
| Bắt đầu project | Submit Hook | Tạo AGENTS.md (tự động đọc) |
| Muốn code style nhất quán | Submit Hook | Tạo `.gemini/styleguide.md` |
| AI chạy lệnh nguy hiểm | Tool Hook | Thêm deny vào `settings.json` |
| Quên bước quan trọng | Workflow Hook | Tạo `.agent/workflows/*.md` |
| Muốn quy trình build chuẩn | Workflow Hook | Tạo workflow `build.md` |

## 3.4. Template từng loại

### Hook 1: Submit Hook (AI TỰ ĐỘNG đọc)

Tạo file → AI tự đọc. Bạn KHÔNG cần làm gì thêm.

| File | Tool hỗ trợ |
|------|-------------|
| `AGENTS.md` | Gemini |
| `CLAUDE.md` | Claude Code |
| `.cursorrules` | Cursor |
| `.github/copilot-instructions.md` | Copilot |
| `.gemini/styleguide.md` | Gemini |

### Hook 2: Tool Hook (Allow/Deny lệnh)

Template `.gemini/settings.json`:
```json
{
  "permissions": {
    "allow": [
      "Read(**)",
      "Bash(gradle *)",
      "Bash(git status)",
      "Bash(git diff)",
      "Bash(git log *)"
    ],
    "deny": [
      "Bash(rm -rf *)",
      "Bash(del /s *)",
      "Bash(curl *)",
      "Bash(wget *)",
      "Edit(.env)",
      "Edit(*.lock)",
      "Edit(*.key)"
    ]
  }
}
```

**Giải thích:**
- `allow`: AI chạy TỰ DO, không hỏi ← lệnh an toàn
- `deny`: AI KHÔNG ĐƯỢC chạy, dù bạn cho phép ← lệnh nguy hiểm  
- Không trong allow/deny: AI phải HỎI bạn trước

### Hook 3: Workflow Hook (Quy trình nhiều bước)

Template `.agent/workflows/ten-workflow.md`:
```markdown
---
description: [Mô tả ngắn — hiện khi gõ /tên-workflow]
---

1. [Bước 1 mô tả]
// turbo                    ← AI tự chạy (an toàn)
[lệnh terminal]

2. [Bước 2 mô tả]
[lệnh terminal]            ← AI phải HỎI trước (quan trọng)

3. [Bước 3 mô tả]
// turbo
[lệnh terminal]
```

**Annotation `// turbo`:**
- CÓ `// turbo` → AI chạy TỰ ĐỘNG không hỏi (lệnh an toàn, read-only)
- KHÔNG có → AI phải HỎI trước khi chạy (lệnh thay đổi/tạo file)

## 3.5. Sai lầm cần tránh

| ❌ Sai | ✅ Đúng |
|-------|--------|
| Deny quá nhiều → AI không làm được gì | Chỉ deny lệnh THẬT SỰ nguy hiểm |
| Allow quá nhiều → AI tự tung tự tác | Chỉ allow lệnh read-only, build, git |
| Workflow quá dài (20 bước) | 5-7 bước là đủ |
| Quên annotation // turbo | Lệnh an toàn nên có turbo |

---
---

# 🧪 PHẦN 4: CONTEXT ENGINEERING — Thiết Kế Thông Tin
## 🆕 MỚI TRONG ASH 2.0

## 4.1. Là gì?

> **Context Engineering = Nghệ thuật thiết kế TOÀN BỘ hệ thống thông tin mà AI nhận được.**

Thay vì chỉ viết prompt tốt (Prompt Engineering), giờ bạn phải **thiết kế cả hệ thống thông tin** — AI nhận đúng thông tin, đúng lúc, đúng định dạng.

### Ví dụ đời thường

```
❌ Prompt Engineering (cũ):
   Bạn → "Fix bug login" → AI đoán bừa → sai 50%

✅ Context Engineering (mới):
   Bạn → "Fix bug login"
         AI tự động nhận:
         + AGENTS.md (rules dự án)
         + error-log.md (lỗi cũ tương tự)
         + login-feature.spec.md (spec gốc)
         + LoginViewModel.kt (code liên quan)
         → AI hiểu đầy đủ → đúng 95%
```

## 4.2. Năm chiến lược Context Engineering

### ① Selection (Chọn lọc) — Chọn đúng thông tin

```
❌ Nhồi hết code vào context:
   "Đây là toàn bộ 50 file của project, hãy fix bug login"

✅ Chọn đúng file liên quan:
   "Fix bug login. Context:
    - Spec: specs/login.spec.md
    - Code: LoginViewModel.kt, AuthRepository.kt
    - Lỗi cũ: error-log.md (entry #12)"
```

**Template `.context.md` cho task lớn:**
```markdown
# Context cho [Tên Task]

## Files cần đọc (theo thứ tự)
1. `specs/[feature].spec.md` — Đặc tả feature
2. `knowledge-base/error-log.md` — Lỗi liên quan
3. `src/[file1].kt` — Code chính
4. `src/[file2].kt` — Code phụ thuộc

## Files KHÔNG cần đọc
- Toàn bộ thư mục tests/ (chưa cần)
- Các module không liên quan

## Thông tin bổ sung
- [Bất kỳ context nào AI cần biết]
```

### ② Compression (Nén) — Chỉ giữ phần quan trọng

```
❌ Copy nguyên 200 dòng code vào prompt

✅ Tóm tắt:
   "LoginViewModel dùng StateFlow<LoginUiState>.
    Có 3 functions: login(), validateEmail(), resetPassword().
    Bug xảy ra ở login() dòng 45: quên handle timeout."
```

**Quy tắc nén:**
| Loại thông tin | Nén thế nào |
|---------------|-------------|
| Code dài | Chỉ giữ function signature + comment |
| Error log dài | Chỉ giữ 5 lỗi GẦN NHẤT liên quan |
| Architecture doc | Chỉ giữ diagram + layer names |
| Cả project | Chỉ giữ cây thư mục + AGENTS.md |

### ③ Ordering (Sắp xếp) — Thứ tự quan trọng

```
✅ Thứ tự đúng (quan trọng → chi tiết):

1. AGENTS.md          ← Rules chung (đọc trước)
2. Spec file          ← Bối cảnh feature
3. Error log          ← Lỗi liên quan
4. Source code         ← Code cụ thể (đọc sau)
```

> **Quy tắc:** Thông tin QUAN TRỌNG NHẤT → đặt ĐẦU TIÊN. AI chú ý phần đầu nhiều hơn.

### ④ Isolation (Cô lập) — Tách task lớn thành task nhỏ

```
❌ 1 prompt bao gồm:
   "Tạo cả feature login gồm UI, API, Database, Tests"

✅ Tách thành sub-tasks:
   Task 1: "Tạo Data Layer cho Login" (context: spec + data/)
   Task 2: "Tạo Domain Layer" (context: spec + domain/)
   Task 3: "Tạo UI Layer" (context: spec + ui/)
   Task 4: "Viết Tests" (context: tất cả layers)
```

### ⑤ Format (Định dạng) — AI đọc Markdown tốt nhất

```
❌ "dùng kotlin compose material3 mvvm hilt room retrofit"

✅ (Dùng Markdown cấu trúc rõ ràng)
    ## Tech Stack
    - **UI**: Jetpack Compose + Material 3
    - **Architecture**: MVVM
    - **DI**: Hilt
    - **Database**: Room
    - **Network**: Retrofit
```

**Quy tắc format:**
| Loại | Format tốt nhất |
|------|----------------|
| Rules | Bullet points (DO/DON'T) |
| Quy trình | Numbered steps |
| So sánh | Table |
| Cấu trúc | ASCII art / Tree |
| Code mẫu | Fenced code blocks |

## 4.3. Áp dụng Context Engineering vào ASH 2.0

```
                    Context Engineering
                    ────────────────────
                           │
              ┌────────────┼────────────┐
              ↓            ↓            ↓
          AGENTS.md    SKILL.md     specs/*.md
          (Selection   (Ordering    (Isolation
           + Format)    + Format)    + Compression)
              │            │            │
              ↓            ↓            ↓
          ┌───────────────────────────────────┐
          │  AI nhận ĐÚNG context, ĐÚNG lúc   │
          │  → Output chất lượng cao hơn      │
          └───────────────────────────────────┘
```

## 4.4. Sai lầm cần tránh

| ❌ Sai | ✅ Đúng |
|-------|--------|
| Nhồi hết vào 1 prompt | Chọn lọc file liên quan |
| Prompt dài 5000 từ | Tóm tắt, giữ < 2000 tokens |
| Không có thứ tự | Rules → Spec → Code |
| 1 task khổng lồ | Tách thành sub-tasks nhỏ |
| Viết dạng văn xuôi dài | Dùng Markdown rõ ràng |

---
---

# 💡 PHẦN 5: BRAINSTORMING — Bão Não Ý Tưởng
## 🆕 MỚI TRONG ASH 2.0

## 5.1. Là gì?

> **Brainstorming = Phương pháp tạo ra NHIỀU ý tưởng nhất có thể, KHÔNG đánh giá ngay, rồi chọn lọc sau.**

Giống "họp bàn trước khi ra quyết định" — nghĩ nhiều → chọn ít → làm sâu.

### Ví dụ đời thường

```
❌ Không brainstorm:
   Bạn: "Tạo feature login"
   AI: *code ngay* → không phù hợp nhu cầu
   Bạn: "Sai rồi, tôi muốn kiểu khác"
   → Lãng phí thời gian, code bỏ đi

✅ Có brainstorm:
   Bạn: "Brainstorm: có những cách login nào?"
   AI: "1. Email/Password  2. Social Login  3. Magic Link
        4. Biometric  5. Passkey  6. SMS OTP"
   Bạn: "OK, chọn 1 + 3 + 6 cho MVP"
   → Rõ ràng, đúng hướng từ đầu!
```

## 5.2. Bốn nguyên tắc cốt lõi

```
┌─────────────────────────────────────────────────────────┐
│              4 NGUYÊN TẮC BRAINSTORMING                 │
│                                                          │
│  ① SỐ LƯỢNG TRƯỚC                                      │
│     Càng nhiều ý tưởng càng tốt                         │
│     "Nghĩ 10 ý, chọn 2-3 tốt nhất"                     │
│                                                          │
│  ② KHÔNG PHÁN XÉT                                      │
│     Không nói "ý đó dở" khi đang brainstorm             │
│     Mọi ý đều được GHI LẠI                              │
│                                                          │
│  ③ XÂY TRÊN Ý KHÁC                                     │
│     Lấy 1 ý → mở rộng → kết hợp                        │
│     "Nếu thêm cái đó → thì sao nếu..."                 │
│                                                          │
│  ④ KHUYẾN KHÍCH Ý "ĐIÊN"                               │
│     Ý tưởng bất thường → đôi khi đột phá               │
│     "Nếu AI tự viết AGENTS.md thì sao?"                 │
└─────────────────────────────────────────────────────────┘
```

## 5.3. Năm kỹ thuật Brainstorming

### ① Mind Map (Sơ đồ tư duy)

Vẽ nhánh từ 1 ý trung tâm → mở rộng dần.

```
                    ┌─ Email/Password
                    ├─ Social Login (Google, Facebook)
        ┌─ Đăng nhập┤
        │           ├─ Magic Link
        │           └─ Biometric
Feature ┤
Login   ├─ Bảo mật ─┬─ 2FA
        │           ├─ Rate limiting
        │           └─ Session management
        │
        └─ UX ──────┬─ Remember me
                    ├─ Forgot password
                    └─ Error messages (tiếng Việt)
```

→ **Dùng khi:** Muốn khám phá MỌI khía cạnh của 1 feature/vấn đề.

### ② SCAMPER (7 câu hỏi sáng tạo)

| Chữ | Câu hỏi | Ví dụ cho ASH 2.0 |
|-----|---------|-------------------|
| **S**ubstitute | Thay thế bằng gì? | Thay Markdown bằng YAML cho config? |
| **C**ombine | Kết hợp với gì? | Gộp error-log + lesson thành 1 file? |
| **A**dapt | Điều chỉnh thế nào? | Làm phiên bản mini cho newbie? |
| **M**odify | Thay đổi gì? | Thêm version control cho AGENTS.md? |
| **P**ut to use | Dùng cho mục đích khác? | Dùng ASH cho dự án non-code? |
| **E**liminate | Bỏ bớt gì? | Bỏ MCP khỏi lộ trình newbie? |
| **R**everse | Đảo ngược thế nào? | AI tự đề xuất rules thay vì người viết? |

→ **Dùng khi:** Muốn cải tiến cái đã có.

### ③ "What If..." (Nếu... thì sao?)

Hỏi liên tục "Nếu X thì sao?" để phá giới hạn tư duy:

```
"Nếu app có 1 triệu users thì sao?"
  → Cần cache, CDN, database sharding

"Nếu không có internet thì sao?"
  → Cần offline mode, local storage

"Nếu user là người khiếm thị thì sao?"
  → Cần accessibility, screen reader support

"Nếu AI hiểu sai spec thì sao?"
  → Cần acceptance criteria CỤ THỂ hơn
```

→ **Dùng khi:** Tìm edge cases, tình huống chưa nghĩ tới.

### ④ Reverse Brainstorming (Brainstorm ngược)

Thay vì "Làm sao để THÀNH CÔNG?" → hỏi "Làm sao để THẤT BẠI?"
Rồi làm NGƯỢC LẠI.

```
"Làm sao để feature login THẤT BẠI?"
  ❌ Không validate email → ✅ PHẢI validate email
  ❌ Không có error message → ✅ PHẢI có error message rõ ràng
  ❌ Không giới hạn retry → ✅ PHẢI có rate limiting
  ❌ Lưu password plain text → ✅ PHẢI hash password
```

→ **Dùng khi:** Tìm lỗi tiềm ẩn, tạo checklist bảo mật.

### ⑤ Six Thinking Hats (6 chiếc mũ tư duy)

Nhìn vấn đề từ 6 góc khác nhau:

| Mũ | Góc nhìn | Câu hỏi cho feature |
|----|---------|---------------------|
| 🎩 **Trắng** | Dữ kiện | "User hiện tại là ai? Tech stack nào?" |
| 🎩 **Đỏ** | Cảm xúc | "User cảm thấy thế nào khi dùng?" |
| 🎩 **Vàng** | Lạc quan | "Lợi ích lớn nhất là gì?" |
| 🎩 **Đen** | Bi quan | "Rủi ro gì? Có thể sai ở đâu?" |
| 🎩 **Xanh lá** | Sáng tạo | "Có cách nào hoàn toàn mới không?" |
| 🎩 **Xanh dương** | Quản lý | "Timeline? Resources? Priority?" |

→ **Dùng khi:** Đánh giá toàn diện feature quan trọng.

## 5.4. Template phiên Brainstorming

### File `brainstorms/[tên-topic].brainstorm.md`

```markdown
# 💡 Brainstorm: [Tên Topic]
## Ngày: YYYY-MM-DD
## Kỹ thuật: [Mind Map / SCAMPER / What If / Reverse / 6 Hats]
## Trạng thái: [BRAINSTORMING → SELECTED → DONE]

## 1. Câu Hỏi Gốc
> [Câu hỏi cần brainstorm, ví dụ: "Có những cách nào để implement notification?"]

## 2. Ý Tưởng (KHÔNG lọc, ghi HẾT)
1. [Ý tưởng 1]
2. [Ý tưởng 2]
3. [Ý tưởng 3]
4. [Ý tưởng 4 — có thể "điên"]
5. [Ý tưởng 5]
...

## 3. Đánh Giá & Chọn Lọc

| # | Ý tưởng | Khả thi | Tác động | Thời gian | Chọn? |
|---|---------|---------|---------|-----------|-------|
| 1 | [Ý 1] | Cao | Cao | 2 ngày | ✅ |
| 2 | [Ý 2] | Trung bình | Cao | 5 ngày | ✅ |
| 3 | [Ý 3] | Thấp | Thấp | 1 ngày | ❌ |

## 4. Kết Luận
- **Chọn:** Ý tưởng #1 và #2
- **Lý do:** [Tại sao chọn]
- **Bước tiếp:** Viết spec cho ý đã chọn → `specs/[feature].spec.md`
```

## 5.5. Brainstorming trong luồng ASH 2.0

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ 💡 BRAINSTORM│ ──→ │ 📋 OPENSPEC  │ ──→ │ 💻 IMPLEMENT │ ──→ │ 📚 ARCHIVE   │
│  ──────────  │     │  ──────────  │     │  ──────────  │     │  ──────────  │
│  Khám phá    │     │  Viết spec   │     │  AI code     │     │  Lưu kết     │
│  ý tưởng     │     │  chi tiết    │     │  theo spec   │     │  quả vào KB  │
│  Chọn lọc    │     │  từ ý đã     │     │  đã duyệt   │     │  (bài học)   │
│              │     │  chọn        │     │              │     │              │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
   5-10 phút             5 phút              5 phút              2 phút
   CON NGƯỜI          CON NGƯỜI +AI       AI + CON NGƯỜI        TỰ ĐỘNG
```

**Quy tắc:** Brainstorm → Chọn → Spec → Code. ĐỪNG nhảy qua bước!

## 5.6. Khi nào dùng Brainstorming?

| Tình huống | Dùng Brainstorming? | Kỹ thuật nào? |
|-----------|--------------------|--------------| 
| Feature mới, chưa rõ approach | ✅ BẮT BUỘC | Mind Map |
| Cải tiến feature có sẵn | ✅ Nên dùng | SCAMPER |
| Tìm edge cases / rủi ro | ✅ Nên dùng | Reverse / What If |
| Đánh giá feature quan trọng | ✅ Nên dùng | 6 Hats |
| Fix bug nhỏ (biết rõ lỗi) | ❌ Không cần | — |
| Task lặp lại (đã có Skill) | ❌ Không cần | — |
| UI tweak nhỏ (đổi màu, font) | ❌ Không cần | — |

**Quy tắc vàng: Nếu bạn nói "Tôi chưa biết nên làm kiểu nào" → CẦN BRAINSTORM!**

## 5.7. Dùng AI để Brainstorm hiệu quả

### Prompt templates cho brainstorming với AI:

```markdown
## Mind Map prompt:
"Liệt kê TẤT CẢ khía cạnh của [feature X] theo dạng mind map.
Mỗi nhánh chính có ít nhất 3 nhánh con. Không lọc, tôi muốn NHIỀU."

## SCAMPER prompt:
"Áp dụng phương pháp SCAMPER cho [feature/component X].
Mỗi chữ S-C-A-M-P-E-R cho tôi 2 gợi ý cụ thể."

## What If prompt:
"Đặt 10 câu hỏi 'What If' cho [feature X].
Bao gồm cả tình huống bình thường VÀ extreme."

## Reverse prompt:
"Liệt kê 10 cách để [feature X] THẤT BẠI hoàn toàn.
Sau đó đảo ngược mỗi điểm thành best practice."

## 6 Hats prompt:
"Phân tích [feature X] qua 6 chiếc mũ tư duy.
Mỗi mũ cho tôi 3 nhận xét cụ thể."
```

## 5.8. Sai lầm cần tránh

| ❌ Sai | ✅ Đúng |
|-------|--------|
| Phán xét ý tưởng ngay lập tức | Ghi HẾT, đánh giá SAU |
| Brainstorm quá lâu (> 15 phút) | 5-10 phút là đủ cho 1 session |
| Chỉ brainstorm 1 mình | Hỏi AI + đồng nghiệp + cộng đồng |
| Không ghi lại kết quả | LUÔN lưu vào `brainstorms/*.md` |
| Brainstorm xong không hành động | Chọn lọc → Viết spec → Code |
| Brainstorm cho MỌI THỨ | Chỉ khi chưa rõ approach |

---
---

# 📋 PHẦN 6: OPENSPEC — Phát Triển Theo Đặc Tả
## 🆕 MỚI TRONG ASH 2.0

## 6.1. Là gì?

> **OpenSpec = Phương pháp "Viết spec trước, code sau" cho AI coding.**

Thay vì nói AI "tạo feature login" rồi sửa 10 lần → Viết spec chi tiết trước → AI code đúng 1 lần.

### Ví dụ đời thường

```
❌ Không có spec (Vibe Coding):
   Bạn: "Tạo màn login"
   AI: *tạo login cơ bản*
   Bạn: "Thêm remember me nữa"
   AI: *sửa* 
   Bạn: "Quên mất, cần validate email"
   AI: *sửa lại*
   Bạn: "Sai rồi, cần error message tiếng Việt"
   AI: *sửa lần nữa*
   → 4 lượt sửa, mất 30 phút, code lộn xộn

✅ Có spec (OpenSpec):
   Bước 1: Viết spec (5 phút)
   Bước 2: AI đọc spec → code 1 lần (5 phút)
   → 10 phút, code sạch, đúng 100%
```

## 6.2. Quy trình OpenSpec 3 bước

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│  PROPOSE │ ──→ │  IMPLEMENT   │ ──→ │   ARCHIVE    │
│  ────────│     │  ──────────  │     │  ──────────  │
│  Viết    │     │  AI code     │     │  Lưu spec    │
│  spec +  │     │  theo spec   │     │  vào KB      │
│  Review  │     │  đã duyệt   │     │  (bài học)   │
└──────────┘     └──────────────┘     └──────────────┘
   5 phút           5 phút              2 phút
   CON NGƯỜI        AI + CON NGƯỜI      TỰ ĐỘNG
```

### Bước 1: PROPOSE (Viết Spec)

```markdown
# Spec: [Tên Feature]
## Ngày: YYYY-MM-DD
## Trạng thái: [DRAFT → REVIEW → APPROVED → DONE → ARCHIVED]

## 1. Mô Tả
[Feature này làm gì, tại sao cần]

## 2. Scope
### Trong scope
- [Tính năng 1]
- [Tính năng 2]

### Ngoài scope
- [Tính năng KHÔNG làm trong lần này]

## 3. User Stories
- Là [vai trò], tôi muốn [hành động], để [mục đích]

## 4. Acceptance Criteria
- [ ] [Điều kiện 1 — cụ thể, đo được]
- [ ] [Điều kiện 2]
- [ ] [Điều kiện 3]

## 5. Technical Design
### Data Layer
- [Models, Repositories]

### Domain Layer
- [UseCases]

### Presentation Layer
- [Screens, ViewModels]

### Dependencies
- [Thư viện, API endpoints]

## 6. UI Mockup
[Mô tả hoặc link hình]

## 7. Error Cases
| Tình huống | Xử lý |
|-----------|-------|
| [Error 1] | [Hiện message gì] |
| [Error 2] | [Retry hay fail] |

## 8. Testing
- Unit tests: [Cần test gì]
- UI tests: [Cần test gì]
```

### Bước 2: IMPLEMENT

```
AI đọc spec → Kiểm tra Acceptance Criteria → Code từng phần:

1. Data Layer (theo spec section 5)
2. Domain Layer (theo spec section 5)
3. Presentation Layer (theo spec section 5 + 6)
4. Error handling (theo spec section 7)
5. Tests (theo spec section 8)

→ Mỗi phần PHẢI map về 1 mục trong spec
→ Khi code xong → check Acceptance Criteria
```

### Bước 3: ARCHIVE

```markdown
# Đã hoàn thành: [Tên Feature]
## Ngày hoàn thành: YYYY-MM-DD

## Kết quả
- [x] Tất cả Acceptance Criteria đạt
- [x] Tests pass

## Bài học
- [Điều gì đi tốt]
- [Điều gì cần cải thiện]

## Files đã tạo/sửa
- `src/ui/LoginScreen.kt` (new)
- `src/viewmodel/LoginViewModel.kt` (new)
```

## 6.3. Khi nào dùng OpenSpec?

| Tình huống | Dùng OpenSpec? |
|-----------|---------------|
| Feature mới > 3 files | ✅ BẮT BUỘC |
| Fix bug nhỏ (1-2 files) | ❌ Không cần |
| Refactor lớn | ✅ Nên dùng |
| Task lặp lại (đã có Skill) | ❌ Skill đủ rồi |
| Prototype nhanh (throwaway code) | ❌ Không cần |
| Feature quan trọng (payment, auth) | ✅ BẮT BUỘC |

**Quy tắc vàng: Nếu task ảnh hưởng > 3 files → VIẾT SPEC!**

## 6.4. Sai lầm cần tránh

| ❌ Sai | ✅ Đúng |
|-------|--------|
| Spec quá dài (10 trang) | 1-2 trang là đủ |
| Spec quá chung "tạo login" | Có Acceptance Criteria CỤ THỂ |
| Viết spec xong không review | BẮT BUỘC review trước khi code |
| Không archive spec sau khi xong | Lưu lại để tham khảo sau |
| Viết spec cho mọi thứ | Chỉ cho feature > 3 files |

---
---

# 🔌 PHẦN 7: MCP — Kết Nối Công Cụ Bên Ngoài
## 🆕 MỚI TRONG ASH 2.0

## 7.1. Là gì?

> **MCP (Model Context Protocol) = Giao thức chuẩn để AI kết nối với công cụ bên ngoài.**

Giống "ổ cắm điện vạn năng" — mọi thiết bị (AI tool) đều cắm vào được.

### Ví dụ đời thường

```
❌ Không có MCP:
   Bạn: "Query database lấy user"
   AI: "Tôi không truy cập được database. Đây là code SQL..."
   Bạn: *tự copy code → chạy → dán kết quả lại*

✅ Có MCP:
   Bạn: "Query database lấy user"
   AI: *tự kết nối DB qua MCP → chạy query → trả kết quả*
   → Không cần copy-paste!
```

## 7.2. MCP hoạt động thế nào?

```
┌──────────────┐          ┌──────────────┐
│   AI Agent   │ ───MCP──→│  MCP Server  │
│  (Claude,    │          │  ──────────  │
│   Gemini,    │          │  Database    │
│   Cursor...) │          │  File System │
│              │←──MCP────│  Git         │
│              │          │  API         │
└──────────────┘          └──────────────┘
     Client                   Server

MCP cung cấp 3 thứ:
  📦 Resources = Dữ liệu (DB records, files)
  🔧 Tools     = Hành động (query, write, deploy)
  💬 Prompts   = Template prompt có sẵn
```

## 7.3. Các MCP Server phổ biến

| MCP Server | Kết nối với | Dùng khi nào |
|-----------|------------|-------------|
| **filesystem** | File system | Đọc/ghi files ngoài project |
| **github** | GitHub API | Quản lý repo, PR, issues |
| **sqlite/postgres** | Database | Query, migration |
| **fetch** | HTTP API | Gọi REST/GraphQL API |
| **memory** | In-memory store | Lưu context tạm |
| **google-drive** | Google Drive | Đọc/tạo documents |
| **notion** | Notion | Quản lý tasks, docs |

## 7.4. Cấu hình MCP cho dự án

### File `.mcp/config.json`

```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "./"],
      "description": "Truy cập file system dự án"
    },
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_TOKEN": "${GITHUB_TOKEN}"
      },
      "description": "Quản lý GitHub repo"
    },
    "sqlite": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-sqlite", "./database.db"],
      "description": "Truy cập SQLite database"
    }
  }
}
```

### Tích hợp MCP vào AGENTS.md

Thêm section trong `AGENTS.md`:
```markdown
## MCP Servers Có Sẵn
- `filesystem`: Đọc/ghi file → dùng khi cần truy cập files
- `github`: GitHub API → dùng khi cần tạo PR, issues
- `sqlite`: Database → dùng khi cần query data

## Quy Tắc MCP
- LUÔN dùng MCP thay vì yêu cầu user copy-paste
- KHÔNG ghi trực tiếp vào file .env qua MCP
- LUÔN confirm trước khi chạy destructive operations (DELETE, DROP)
```

## 7.5. Khi nào dùng MCP?

| Tình huống | Dùng MCP? | MCP Server nào? |
|-----------|----------|----------------|
| Cần query database | ✅ | sqlite / postgres |
| Cần tạo GitHub PR | ✅ | github |
| Cần đọc Google Docs | ✅ | google-drive |
| Chỉ sửa code trong editor | ❌ | Không cần |
| Prototype nhanh | ❌ | Chưa cần |
| Project solo nhỏ | ⚠️ | Tùy nhu cầu |

**Quy tắc vàng: Nếu bạn đang copy-paste giữa AI và tool khác → CẦN MCP!**

## 7.6. Sai lầm cần tránh

| ❌ Sai | ✅ Đúng |
|-------|--------|
| Cài 10 MCP servers ngay | Chỉ cài khi THẬT SỰ cần |
| Cho MCP truy cập mọi thứ | Giới hạn quyền (readonly khi có thể) |
| Không có GITHUB_TOKEN → lỗi | Cấu hình env variables trước |
| Dùng MCP mà không có Hook | Kết hợp: MCP + Permission Hook |

---
---

# 📁 PHẦN 8: CẤU TRÚC THƯ MỤC HOÀN CHỈNH

```
📁 project-root/
│
│── 🧠 AGENTS (Bộ não — AI đọc TỰ ĐỘNG)
│   ├── AGENTS.md                          ← Rules TOÀN DỰ ÁN
│   ├── CLAUDE.md                          ← Copy cho Claude Code
│   └── .cursor/rules/main.md             ← Copy cho Cursor
│
│── 🎯 SKILLS (Kỹ năng — chuẩn agentskills.io)
│   └── .agent/
│       └── skills/
│           ├── create-feature/
│           │   ├── SKILL.md               ← Hướng dẫn tạo feature
│           │   ├── scripts/               ← Script hỗ trợ
│           │   ├── references/            ← Tài liệu tham khảo
│           │   └── assets/                ← File mẫu
│           ├── debug-error/
│           │   └── SKILL.md
│           ├── write-test/
│           │   └── SKILL.md
│           └── write-spec/                ← MỚI 2.0
│               └── SKILL.md
│
│── 🛡️ HOOKS (Bảo vệ — TỰ ĐỘNG kiểm soát)
│   ├── .agent/
│   │   └── workflows/
│   │       ├── build.md                   ← /build
│   │       ├── fix-error.md               ← /fix-error
│   │       └── new-feature.md             ← MỚI 2.0: OpenSpec flow
│   └── .gemini/
│       ├── settings.json                  ← Allow/Deny permissions
│       └── styleguide.md                  ← Code style
│
│── 💡 BRAINSTORMS (Bão não ý tưởng — MỚI 2.0)
│   └── brainstorms/
│       ├── login-approach.brainstorm.md   ← Brainstorm cách login
│       ├── notification-ideas.brainstorm.md ← Brainstorm notification
│       └── _archived/                     ← Brainstorms đã chọn xong
│
│── 📋 SPECS (Đặc tả — MỚI 2.0)
│   └── specs/
│       ├── login-feature.spec.md          ← Spec cho Login
│       ├── payment-feature.spec.md        ← Spec cho Payment
│       └── _archived/                     ← Specs đã hoàn thành
│           └── signup-feature.spec.md
│
│── 🧪 CONTEXT (Context Engineering — MỚI 2.0)
│   └── context/
│       └── templates/
│           ├── feature-context.md         ← Template context cho feature
│           ├── bugfix-context.md          ← Template context cho bugfix
│           └── refactor-context.md        ← Template context cho refactor
│
│── 🔌 MCP (Kết nối ngoài — MỚI 2.0)
│   └── .mcp/
│       └── config.json                    ← MCP servers config
│
│── 📚 KNOWLEDGE BASE (Bộ nhớ dài hạn)
│   └── knowledge-base/
│       ├── architecture.md                ← Kiến trúc
│       ├── lesson.md                      ← Tiến độ & patterns
│       ├── error-log.md                   ← Lỗi đã gặp
│       └── dev-notes.md                   ← Quyết định kỹ thuật
│
└── 📄 SOURCE CODE
    └── src/
        ├── AGENTS.md                      ← Rules riêng src/
        ├── data/
        ├── domain/
        └── ui/
            └── AGENTS.md                  ← Rules riêng ui/
```

---
---

# 📖 PHẦN 9: CÂU CHUYỆN 60 NGÀY

| Ngày | Chuyện gì xảy ra | Dùng gì | Tạo file gì |
|------|------------------|---------|-------------|
| **1 sáng** | Tạo project mới | 🧠 AGENTS | `AGENTS.md` |
| **1 chiều** | Tạo feature lần 1 | 🧠 AGENTS (đủ rồi) | — |
| **1 tối** | Gặp lỗi build | 📚 KB | `error-log.md` |
| **5** | Tạo feature lần 2 → sai pattern | 🎯 SKILL | `create-feature/SKILL.md` |
| **5** | AI xóa file nguy hiểm | 🛡️ HOOK | `settings.json` |
| **10** | Code review bị chê | 🧠 AGENTS | Thêm DO/DON'T |
| **15** | Fix bug mà quên ghi log | 🛡️ HOOK | `fix-error.md` workflow |
| **──** | **── ASH 1.0 xong! Bắt đầu 2.0 ──** | | |
| **18** | Feature mới, chưa biết approach | 💡 BRAINSTORM | `brainstorms/payment.brainstorm.md` |
| **20** | Chọn xong → viết spec | 📋 OPENSPEC | `specs/payment.spec.md` |
| **22** | Viết spec lần 2 → tạo skill | 🎯 SKILL | `write-spec/SKILL.md` |
| **25** | Context quá dài, AI bị lú | 🧪 CONTEXT | `context/templates/*.md` |
| **28** | Brainstorm cách refactor hệ thống | 💡 BRAINSTORM | `brainstorms/refactor.brainstorm.md` |
| **30** | Cần query DB mà phải copy-paste | 🔌 MCP | `.mcp/config.json` |
| **35** | Feature mới, dùng full ASH 2.0 | TẤT CẢ | Brainstorm → Spec → Code → KB |
| **40** | Refactor lớn, brainstorm + spec | 💡 + 📋 | Brainstorm + Spec + Context |
| **45** | Tích hợp GitHub qua MCP | 🔌 MCP | Thêm github server |
| **50** | Team member join | — | Review toàn bộ docs |
| **60** | Dự án vận hành mượt mà | 🎉 | — |

---
---

# 🔄 PHẦN 10: LỘ TRÌNH 8 TUẦN

## Phase 1: Nền tảng (Tuần 1-4 = ASH 1.0)

| Tuần | Làm gì | Thời gian |
|------|--------|-----------|
| **1** | Tạo `AGENTS.md` + `knowledge-base/` | 30 phút |
| **2** | Ghi error-log + lesson mỗi ngày | 5 phút/ngày |
| **3** | Tạo Skill đầu tiên | 15 phút |
| **4** | Tạo Hooks (permissions + workflow) | 10 phút |

## Phase 2: Nâng cấp (Tuần 5-8 = ASH 2.0)

| Tuần | Làm gì | Thời gian |
|------|--------|-----------|
| **5** | Brainstorm + viết Spec đầu tiên (OpenSpec) | 25 phút |
| **6** | Tạo Context templates | 15 phút |
| **7** | Cấu hình MCP Server đầu tiên | 15 phút |
| **8** | Full workflow: Brainstorm → Spec → Context → Skill → Code → KB | Practice! |

## Thói quen hàng ngày ASH 2.0

```
🌅 Bắt đầu code:
   → Không cần làm gì. AI tự đọc AGENTS.md

💡 Bắt đầu feature mới (chưa rõ approach):
   → BRAINSTORM trước (brainstorms/)
   → Chọn ý tưởng tốt nhất

📋 Bắt đầu feature mới (> 3 files):
   → VIẾT SPEC trước (specs/)
   → Review spec → Approve
   → Sau đó mới code

🧪 Task phức tạp:
   → Tạo context file (.context.md)
   → Chọn đúng files cần đọc
   → Chia thành sub-tasks

💻 Đang code:
   → Gặp lỗi → fix → GHI error-log (2 phút)
   → Quyết định A/B → GHI dev-notes (2 phút)

🔌 Cần dữ liệu ngoài:
   → Dùng MCP thay vì copy-paste

🌙 Kết thúc ngày:
   → GHI lesson.md (3 phút)
   → Cập nhật AGENTS.md nếu có rule mới
   → Archive specs + brainstorms đã hoàn thành

🔄 Khi task lặp lại lần 2:
   → TẠO SKILL mới (15 phút)
```

---
---

# ❓ PHẦN 11: FAQ ASH 2.0

### "Context Engineering nghe phức tạp, tôi newbie có cần học không?"
Tuần 1-4 CHƯA cần. Khi nào AI trả lời sai nhiều vì thiếu context → đó là lúc cần. Bắt đầu đơn giản: chỉ cần chọn đúng file liên quan khi hỏi AI.

### "Brainstorming có bắt buộc không?"
KHÔNG bắt buộc cho mọi task. Dùng khi bạn chưa rõ nên approach thế nào, hoặc feature quan trọng cần đánh giá nhiều góc. Fix bug nhỏ, UI tweak → không cần brainstorm.

### "OpenSpec có phải viết spec cho MỌI THỨ không?"
KHÔNG! Chỉ cho feature > 3 files hoặc feature quan trọng (payment, auth). Fix bug nhỏ, sửa UI → không cần spec.

### "MCP có bắt buộc không?"
KHÔNG bắt buộc. Dùng khi bạn thấy mình đang copy-paste giữa AI và tool khác (DB, GitHub...) quá nhiều. Nếu project nhỏ, chưa cần.

### "ASH 2.0 có thay thế ASH 1.0 không?"
KHÔNG thay thế, mà BỔ SUNG. ASH 1.0 vẫn là nền tảng. ASH 2.0 thêm 4 layer mới cho project phức tạp hơn.

### "Sao nhiều files thế? Có quá phức tạp không?"
Bạn KHÔNG CẦN tạo hết ngay. Tuần 1 chỉ cần `AGENTS.md` + `knowledge-base/`. Còn lại thêm DẦN DẦN khi cần.

### "Tôi dùng nhiều AI tools, ASH 2.0 dùng cho tool nào?"
TẤT CẢ! Đó là điểm mạnh:
- AGENTS.md → copy sang CLAUDE.md, .cursorrules
- SKILL.md → chuẩn quốc tế, mọi tool đều đọc
- MCP → hỗ trợ Claude, ChatGPT, VS Code, Cursor...

### "Thứ tự triển khai nên thế nào?"
```
Tuần 1-4: AGENTS → KB → SKILLS → HOOKS (nền tảng)
Tuần 5:   BRAINSTORMING + OPENSPEC (khi feature phức tạp)
Tuần 6:   CONTEXT ENGINEERING (khi AI trả lời sai)
Tuần 7:   MCP (khi cần tích hợp ngoài)
```

---
---

# 📊 BẢNG TỔNG KẾT ASH 2.0

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   BẠN GẶP GÌ?              →    LÀM GÌ?                 │
│                                                             │
│   ═══ NỀN TẢNG (ASH 1.0) ═══                             │
│   Bắt đầu project          →  🧠 Tạo AGENTS.md          │
│   Gặp lỗi                  →  📚 Ghi error-log           │
│   Xong task                 →  📚 Ghi lesson              │
│   Chọn tech A hay B         →  📚 Ghi dev-notes           │
│   Làm task LẦN 2            →  🎯 Tạo SKILL              │
│   Code review fail          →  🧠 Thêm DO/DON'T          │
│   AI chạy lệnh sai         →  🛡️ Thêm deny Hook         │
│   Quên bước quan trọng      →  🛡️ Tạo Workflow           │
│                                                             │
│   ═══ NÂNG CẤP (ASH 2.0) ═══                             │
│   Chưa biết approach nào    →  💡 BRAINSTORM trước       │
│   Feature phức tạp (>3 files) → 📋 Viết SPEC trước       │
│   AI trả lời sai/thiếu      →  🧪 Thiết kế CONTEXT       │
│   Copy-paste giữa AI & tool →  🔌 Cài MCP Server         │
│   Brainstorm lần 2          →  🎯 Tạo Skill brainstorm   │
│   Spec viết lần 2           →  🎯 Tạo Skill write-spec   │
│   Context bị dài             →  🧪 Nén + chọn lọc        │
│                                                             │
│   💡 Không chắc?                                          │
│   → GHI LẠI ĐÃ, phân loại SAU!                          │
│   → BẮT ĐẦU VỚI ASH 1.0, nâng cấp DẦN DẦN!            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

# 🔗 THAM KHẢO

| Nguồn | Link | Nội dung |
|-------|------|---------|
| Agent Skills Spec | [agentskills.io](https://agentskills.io/specification) | Chuẩn quốc tế cho SKILL.md |
| Anthropic Skills Repo | [github.com/anthropics/skills](https://github.com/anthropics/skills) | Kho skills mẫu |
| MCP Protocol | [modelcontextprotocol.io](https://modelcontextprotocol.io) | Giao thức kết nối AI |
| ASH 1.0 Guide | `master-agents-skills-hooks-guide.md` | Bộ chuẩn nền tảng |
