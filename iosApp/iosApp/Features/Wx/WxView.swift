import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 公众号页：公众号 Tab + 文章列表
struct WxView: View {
    @StateViewModel var viewModel = WxViewModel(
        getWxAccounts: KoinDependencies().getWxAccounts,
        getWxArticles: KoinDependencies().getWxArticles
    )

    var body: some View {
        let state = viewModel.state

        ScrollView {
            LazyVStack(spacing: 8) {
                if state.loading {
                    LoadingView().frame(height: 300)
                } else if let error = state.error {
                    ErrorView(message: error) { viewModel.refresh() }.frame(height: 300)
                } else {
                    ForEach(state.articles, id: \.id) { article in
                        NavigationLink(value: WebDestination(url: article.link, title: article.title)) {
                            ArticleCard(article: article)
                        }
                        .buttonStyle(.plain)
                        .onAppear { loadMoreIfNeeded(article) }
                    }
                    if state.loadingMore { LoadingFooter() }
                    if state.finished { EndFooter() }
                }
            }
            .padding(12)
        }
        .navigationTitle("公众号")
        .navigationBarTitleDisplayMode(.inline)
        .safeAreaInset(edge: .top) {
            // 公众号横滑选择条
            if !state.accounts.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(state.accounts, id: \.id) { account in
                            let selected = state.selectedId == account.id
                            Text(account.name)
                                .font(.caption)
                                .foregroundColor(selected ? .white : wanBlue)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(
                                    Capsule().fill(selected ? wanBlue : wanBlue.opacity(0.1))
                                )
                                .onTapGesture { viewModel.selectAccount(id: account.id) }
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                }
                .background(Color(.systemBackground))
            }
        }
    }

    private func loadMoreIfNeeded(_ article: Article) {
        let articles = viewModel.state.articles
        guard !viewModel.state.loadingMore, !viewModel.state.finished else { return }
        if let lastIndex = articles.indices.last,
           articles[lastIndex].id == article.id || articles[max(0, lastIndex - 3)].id == article.id {
            viewModel.loadMore()
        }
    }
}
