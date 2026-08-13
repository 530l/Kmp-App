import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 首页：置顶 + 文章列表，快滚到底自动加载
struct HomeView: View {
    @StateViewModel var viewModel = HomeViewModel(
        repo: KoinDependencies().articleRepository
    )

    var body: some View {
        let state = viewModel.state

        ScrollView {
            LazyVStack(spacing: 8) {
                if state.loading {
                    LoadingView()
                        .frame(height: 300)
                } else if let error = state.error {
                    ErrorView(message: error) { viewModel.refresh() }
                        .frame(height: 300)
                } else {
                    // 置顶文章
                    ForEach(state.topArticles, id: \.id) { article in
                        NavigationLink(value: WebDestination(url: article.link, title: article.title)) {
                            ArticleCard(article: article)
                        }
                        .buttonStyle(.plain)
                    }
                    // 普通文章
                    ForEach(state.articles, id: \.id) { article in
                        NavigationLink(value: WebDestination(url: article.link, title: article.title)) {
                            ArticleCard(article: article)
                        }
                        .buttonStyle(.plain)
                        .onAppear { loadMoreIfNeeded(article) }
                    }
                    // 底部
                    if state.loadingMore { LoadingFooter() }
                    if state.finished { EndFooter() }
                }
            }
            .padding(12)
        }
        .navigationTitle("玩 Android")
        .navigationBarTitleDisplayMode(.inline)
    }

    // 快滚到底自动加载
    private func loadMoreIfNeeded(_ article: Article) {
        let articles = viewModel.state.articles
        guard !viewModel.state.loadingMore, !viewModel.state.finished else { return }
        if let lastIndex = articles.indices.last,
           articles[lastIndex].id == article.id || articles[max(0, lastIndex - 3)].id == article.id {
            viewModel.loadMore()
        }
    }
}

// WebView 导航目标，用结构体做 route
struct WebDestination: Hashable {
    let url: String
    let title: String
}
