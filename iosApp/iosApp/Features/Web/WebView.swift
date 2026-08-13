import SwiftUI
import WebKit

// 文章详情 WebView
struct WebView: UIViewRepresentable {
    let url: String

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.load(URLRequest(url: URL(string: url)!))
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}
}

// WebView 容器页面，带标题栏
struct WebPage: View {
    let url: String
    let title: String

    var body: some View {
        WebView(url: url)
            .navigationTitle(title.isEmpty ? "加载中…" : title)
            .navigationBarTitleDisplayMode(.inline)
    }
}
