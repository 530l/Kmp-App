import SwiftUI

// iOS 18+ 的「共享元素式」zoom 转场：源视图标 matchedTransitionSource，
// 目标视图声明 navigationTransition(.zoom)，运行时按 id + namespace 配对。
// 低版本静默降级为普通 push，不影响功能。namespace 传 nil 也等同降级。
extension View {
    @ViewBuilder
    func zoomSource(_ id: AnyHashable, in namespace: Namespace.ID?) -> some View {
        if #available(iOS 18, *), let namespace {
            self.matchedTransitionSource(id: id, in: namespace)
        } else {
            self
        }
    }

    @ViewBuilder
    func zoomDestination(_ id: AnyHashable, in namespace: Namespace.ID?) -> some View {
        if #available(iOS 18, *), let namespace {
            self.navigationTransition(.zoom(sourceID: id, in: namespace))
        } else {
            self
        }
    }

    /// 沉浸式彩色 header：状态栏文字调浅色（白），深色属性背景上也能看清。
    @ViewBuilder
    func lightStatusBar() -> some View {
        if #available(iOS 16, *) {
            self.toolbarColorScheme(.dark, for: .navigationBar)
        } else {
            self
        }
    }
}
