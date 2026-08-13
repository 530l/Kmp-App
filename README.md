# 玩 Android (KMP)

基于 [wanandroid 开放 API](https://www.wanandroid.com/blog/show/2) 的 Kotlin Multiplatform 应用，Android 端使用 Jetpack Compose + Navigation 3，iOS 端使用 SwiftUI。

## 技术栈

- **KMP** — 共享业务逻辑层（data / domain / presentation）
- **Ktorfit** — 注解式 HTTP 接口（类似 Retrofit）
- **Koin 4** — 依赖注入
- **UseCase 模式** — 每个 UseCase 独立 class，`operator fun invoke`，解耦 ViewModel 与 Repository
- **Navigation 3** — 每个 feature 独立管理路由（NavKey + EntryProviderScope 扩展）
- **Coil3 / Kingfisher** — 图片加载
- **kotlinx.serialization** — JSON 序列化

## 功能

- 首页文章流 + 置顶
- 体系/分类
- 公众号
- 搜索（热搜词推荐）
- 登录/注册 + 收藏

## 架构

```
shared/commonMain/
  data/         Model · API · Repository
  domain/       UseCase（每个一个 class，operator fun invoke）
  presentation/ ViewModel
  di/           Koin 模块
  storage/      SessionManager (expect/actual)
```

```
androidApp/
  feature/
    home/navigation/HomeNavigation.kt   — NavKey + EntryProviderScope.homeEntries()
    system/navigation/...               — 每个 feature 独立路由
    ...
  navigation/App.kt                     — 顶层组合所有 feature 的 entryProvider
```
