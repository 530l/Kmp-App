import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 我的收藏页
struct CollectionView: View {
    @StateViewModel var viewModel = CollectionViewModel(
        getCollections: KoinDependencies().getCollections
    )

    var body: some View {
        let state = viewModel.state

        ScrollView {
            LazyVStack(spacing: 8) {
                if state.loading {
                    LoadingView().frame(height: 300)
                } else if let error = state.error {
                    ErrorView(message: error) { viewModel.refresh() }.frame(height: 300)
                } else if state.articles.isEmpty {
                    EmptyStateView(message: "还没有收藏文章").frame(height: 300)
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
        .navigationTitle("我的收藏")
        .navigationBarTitleDisplayMode(.inline)
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
