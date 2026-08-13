import SwiftUI
import Shared

@main
struct iOSApp: App {
    // 启动时初始化 Koin
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            // 五个 Tab，每个独立 NavigationStack
            TabView {
                NavigationStack {
                    HomeView()
                        .navigationDestination(for: WebDestination.self) { dest in
                            WebPage(url: dest.url, title: dest.title)
                        }
                }
                .tabItem { Label("首页", systemImage: "house") }

                NavigationStack {
                    SystemView()
                        .navigationDestination(for: WebDestination.self) { dest in
                            WebPage(url: dest.url, title: dest.title)
                        }
                }
                .tabItem { Label("体系", systemImage: "star") }

                NavigationStack {
                    WxView()
                        .navigationDestination(for: WebDestination.self) { dest in
                            WebPage(url: dest.url, title: dest.title)
                        }
                }
                .tabItem { Label("公众号", systemImage: "envelope") }

                NavigationStack {
                    SearchView()
                        .navigationDestination(for: WebDestination.self) { dest in
                            WebPage(url: dest.url, title: dest.title)
                        }
                }
                .tabItem { Label("搜索", systemImage: "magnifyingglass") }

                NavigationStack {
                    ProfileView()
                        .navigationDestination(for: WebDestination.self) { dest in
                            WebPage(url: dest.url, title: dest.title)
                        }
                        .navigationDestination(for: String.self) { value in
                            if value == "collections" {
                                CollectionView()
                                    .navigationDestination(for: WebDestination.self) { dest in
                                        WebPage(url: dest.url, title: dest.title)
                                    }
                            }
                        }
                }
                .tabItem { Label("我的", systemImage: "person") }
            }
            .tint(wanBlue)
        }
    }
}
