# 重构方案：双端统一 skydoves 属性色风格 + 专业包结构

## 决策（已确认）
- 列表 + 详情全属性色驱动（预取主属性，复用 pokemonCache，详情页零重复请求）
- iOS 开启 Xcode16 文件夹自动同步，重组为专业结构
- 参考 skydoves 不照搬：不引它的 Rainbow/TransformationLayout 等库，属性色渐变 + 精灵球水印用各平台原生 Canvas/Brush 实现

---

## 一、shared 数据层重构（单一数据源 + 预取 + 包重组）

**1. 属性色单一数据源** — 新增 `data/model/TypeColors.kt`：
```kotlin
object PokemonTypeColors {
    fun argb(type: String): Long  // 18 色 ARGB Long，双端唯一来源
}
```
双端删掉各自维护的 18 色表（Android `Color.kt` 的 TypeNormal…Fairy、iOS `typeColor()` 的 switch），全部改读这里。Android 加 `Long.toColor()`、iOS 加 `Color(argb:)` 扩展做平台转换。彻底消除「各管各的」。

**2. `PokemonListItem` 加主属性**：
```kotlin
data class PokemonListItem(id, name, artworkUrl, val primaryType: String? = null)
```

**3. Repository 预取主属性**（关键）：
- `loadMore()` 拉到新 item 后，对每个新 item 启动后台协程 `getPokemon(id).types.firstOrNull()?.type?.name`
- 用 `Semaphore(8)` 限流并发（一页 24 个，最多 8 并发，对 PokeAPI 友好）
- 取到后 update `_list` flow 对应 item 的 `primaryType`（卡片颜色渐进出现）
- 复用 `pokemonCache`，进详情页时零重复请求

**4. 包重组**（名实相符，分层清晰）：
```
shared/commonMain/.../com/jetbrains/kmpapp/
  data/model/      PokeApiModels.kt + TypeColors.kt + PokemonListItem.kt(从repository拆出)
  data/remote/     PokeApi.kt
  data/repository/ PokemonRepository.kt
  presentation/    4个ViewModel（从 screens/ 改名——VM 不是 screen）
  di/              Koin.kt
shared/iosMain/    di/KoinDependencies.kt（路径随上调整）
```
同步改所有 import、Koin 绑定、`KoinDependencies`。

---

## 二、统一设计语言（双端各自实现，视觉完全一致）

| 组件 | 规范 |
|---|---|
| **PokeCard**(列表) | 圆角20 + 阴影；背景=主属性色垂直渐变(色→色+白30%)；背景半透明精灵球水印(白12%)；居中大图130dp；底部白字 #001 + Name |
| **DetailHeader** | 主属性色渐变铺到状态栏后(沉浸式 edge-to-edge / ignoresSafeArea top)；返回键浮左上(半透明白圆)；居中大图280dp + 白色大 Name + TypeChip 横排；下方白色圆角 card(top 28dp) 上盖如 bottom-sheet |
| **TypeChip** | 属性色背景上 → 白底彩字胶囊(对比清晰)；中性背景上 → 彩底白字胶囊 |
| **StatBar** | 全圆角 track(高8)；fill 按数值分段着色 <50 红 / 50-89 黄 / ≥90 绿(一眼看强弱)；左 stat 名右数值 |
| **SectionCard** | 表面色圆角16，包裹 stats / 图鉴资料 |
| **PokeballBackground** | 各平台 Canvas 画精灵球(上红下白中黑横线+中圆)，白12% alpha，作卡片/header 装饰水印 |

---

## 三、Android 重组 + 重写

包结构（feature-based + design system 分离）：
```
androidApp/.../com/jetbrains/kmpapp/
  MainActivity.kt, MuseumApp.kt
  navigation/        App.kt（Nav3 + 3Tab + NavKey）
  ui/theme/          Color.kt(仅品牌色+Long.toColor), Theme.kt, Type.kt(新增排版)
  ui/components/     PokeCard, TypeChip, StatBar, DetailHeader, SectionCard, PokeballBackground
  feature/
    pokedex/         PokedexScreen.kt
    detail/          PokemonDetailScreen.kt
    types/           TypesScreen.kt, TypeDetailScreen.kt
    settings/        SettingsScreen.kt
```
- 抽 `components` 消除当前两处复制粘贴的 TypeChip、重复的 header 脚手架
- 各 feature 屏幕用 components 重写为 skydoves 风格（属性色卡片 + 渐变沉浸 header）
- 顺手修正 Tab 图标语义（Pokedex 用 pokéball/book、Types 用属性相关）

---

## 四、iOS 重组 + 重写

**第一步：改 pbxproj 开启 `fileSystemSynchronizedGroups`**（一次性改造，build 验证不破；之后增删文件零改 pbxproj）。

包结构：
```
iosApp/iosApp/
  iOSApp.swift
  Theme/            Color.swift(品牌+typeColor读shared), Typography.swift
  Components/       TypeChip, StatBar, PokeCard, DetailHeader, SectionCard, PokeballBackground
  Features/
    Pokedex/        PokedexView.swift
    Detail/         PokemonDetailView.swift
    Types/          TypesView.swift, TypeDetailView.swift
    Settings/       SettingsView.swift
  KMPObservableViewModel.swift(保留glue)
```
- `Color.swift` 的 `typeColor()` 改读 shared `PokemonTypeColors.argb()` 转 SwiftUI Color
- 各屏幕用 Components 重写；**补 iOS 列表无限滚动**（当前 iOS 只显示第一页24个，未接 `loadMore`，对齐 Android）
- 统一 loading 态、`.cornerRadius` → `.clipShape(RoundedRectangle)` 等

---

## 五、执行顺序 & 验证
1. **shared 重构** → `./gradlew :shared:compileKotlinIosSimulatorArm64 :shared:compileDebugLibrary` 通过
2. **iOS pbxproj 改造** → xcodebuild 通过（先确认不破坏现有 build）
3. **Android 重写** → gradle 通过 + 模拟器截图 5 屏
4. **iOS 重写** → xcodebuild 通过 + 模拟器截图（Pokedex 列表 + 详情）
5. **双端冒烟**，对比双端视觉一致性

## 范围与原则
- 保持简洁：不加 domain/use-case 层，不加新依赖，精灵球用 Canvas 画不引资源
- KMP 隔离不变：shared 零 UI，属性色作为「数据」放 shared，平台端只做 Color 转换
- 注释仅写「为什么」，大白话