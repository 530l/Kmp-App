import SwiftUI
import Shared

@main
struct iOSApp: App {
    // 启动时初始化 Koin，依赖注入容器就绪
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            // 三个 Tab，每个独立 NavigationStack，互不影响
            TabView {
                NavigationStack { PokedexView() }
                    .tabItem { Label("图鉴", systemImage: "magnifyingglass") }
                NavigationStack { TypesView() }
                    .tabItem { Label("属性", systemImage: "star.fill") }
                NavigationStack { SettingsView() }
                    .tabItem { Label("设置", systemImage: "gearshape") }
            }
        }
    }
}
