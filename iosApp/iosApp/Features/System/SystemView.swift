import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 体系页：二级分类 Tab + 文章列表
struct SystemView: View {
    @StateViewModel var viewModel = SystemViewModel(
        getSystemTree: KoinDependencies().getSystemTree,
        getChapterArticles: KoinDependencies().getChapterArticles
    )

    var body: some View {
        let state = viewModel.state
        let leaves = state.tree.flatMap { $0.children }

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
        .navigationTitle("体系")
        .navigationBarTitleDisplayMode(.inline)
        .safeAreaInset(edge: .top) {
            // 二级分类横滑选择条
            if !leaves.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(leaves, id: \.id) { chapter in
                            let selected = state.selectedChapter?.id == chapter.id
                            Text(chapter.name)
                                .font(.caption)
                                .foregroundColor(selected ? .white : wanBlue)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(
                                    Capsule().fill(selected ? wanBlue : wanBlue.opacity(0.1))
                                )
                                .onTapGesture { viewModel.selectChapter(chapter: chapter) }
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
