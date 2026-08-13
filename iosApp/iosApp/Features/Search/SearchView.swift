import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 搜索页：搜索框 + 热搜词 + 结果列表
struct SearchView: View {
    @StateViewModel var viewModel = SearchViewModel(
        getHotKeys: KoinDependencies().getHotKeys,
        searchArticles: KoinDependencies().searchArticles
    )
    @State private var query = ""

    var body: some View {
        let state = viewModel.state

        VStack(spacing: 0) {
            // 搜索框
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.secondary)
                TextField("搜索文章…", text: $query)
                    .submitLabel(.search)
                    .onSubmit {
                        viewModel.setKeyword(value: query)
                        viewModel.search()
                    }
                if !query.isEmpty {
                    Button { query = ""; viewModel.reset() } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 24))
            .padding(12)

            // 内容区
            if !state.searched {
                // 初始态：热搜词
                if !state.hotKeys.isEmpty {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("热门搜索")
                                .font(.headline)
                            FlowLayout(spacing: 8) {
                                ForEach(state.hotKeys, id: \.id) { hotKey in
                                    Button {
                                        query = hotKey.name
                                        viewModel.search(keyword: hotKey.name)
                                    } label: {
                                        Text(hotKey.name)
                                            .font(.subheadline)
                                            .foregroundColor(wanBlue)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 6)
                                            .background(Color(.systemGray6), in: Capsule())
                                    }
                                }
                            }
                            .padding(.horizontal, 12)
                        }
                    }
                }
            } else {
                // 搜索结果
                ScrollView {
                    LazyVStack(spacing: 8) {
                        if state.loading {
                            LoadingView().frame(height: 200)
                        } else if state.results.isEmpty {
                            EmptyStateView(message: "未找到「\(state.keyword)」相关文章").frame(height: 200)
                        } else {
                            ForEach(state.results, id: \.id) { article in
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
            }
        }
        .navigationTitle("搜索")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func loadMoreIfNeeded(_ article: Article) {
        let results = viewModel.state.results
        guard !viewModel.state.loadingMore, !viewModel.state.finished else { return }
        if let lastIndex = results.indices.last,
           results[lastIndex].id == article.id || results[max(0, lastIndex - 3)].id == article.id {
            viewModel.loadMore()
        }
    }
}

// 简单的流式布局，热搜词标签换行
struct FlowLayout: Layout {
    var spacing: CGFloat = 8

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var x: CGFloat = 0, y: CGFloat = 0, rowHeight: CGFloat = 0
        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)
            if x + size.width > maxWidth {
                x = 0
                y += rowHeight + spacing
                rowHeight = 0
            }
            x += size.width + spacing
            rowHeight = max(rowHeight, size.height)
        }
        return CGSize(width: maxWidth, height: y + rowHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let maxWidth = bounds.width
        var x: CGFloat = bounds.minX, y: CGFloat = bounds.minY, rowHeight: CGFloat = 0
        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)
            if x + size.width > bounds.minX + maxWidth {
                x = bounds.minX
                y += rowHeight + spacing
                rowHeight = 0
            }
            subview.place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(size))
            x += size.width + spacing
            rowHeight = max(rowHeight, size.height)
        }
    }
}
