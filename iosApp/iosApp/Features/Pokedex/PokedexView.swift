import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 图鉴首页：自适应属性色卡片网格 + 搜索，快滚到底自动拉下一页，点卡片进详情。
struct PokedexView: View {
    @StateViewModel var viewModel = PokedexViewModel(repository: KoinDependencies().pokemonRepository)
    @State private var query = ""
    // 卡片图 ↔ 详情的共享元素转场命名空间，列表源和详情目标都用它
    @Namespace private var artworkNamespace

    private let columns = [GridItem(.adaptive(minimum: 150), spacing: 12)]

    // 直接读 ViewModel 的当前值；native-coroutines + StateViewModel 在数据变化时自动刷新
    private var items: [PokemonListItem] { viewModel.visibleItems }
    private var paging: PagedList { viewModel.pokedex }

    var body: some View {
        ScrollView {
            if items.isEmpty {
                ProgressView().padding(.top, 80)
            } else {
                LazyVGrid(columns: columns, spacing: 12) {
                    ForEach(items, id: \.id) { item in
                        NavigationLink(value: item.id) {
                            PokeCard(item: item, namespace: artworkNamespace)
                        }
                        .buttonStyle(.plain)
                        // 快滚到底就自动拉下一页（最后 6 张里任意一张出现都触发）
                        .onAppear { loadMoreIfNeeded(currentId: item.id) }
                    }
                    if paging.loading {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .gridCellColumns(columns.count)
                    }
                    if paging.finished {
                        Text("共 \(items.count) 只")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .gridCellColumns(columns.count)
                    }
                }
                .padding(12)
            }
        }
        .searchable(text: $query, prompt: "搜索宝可梦")
        .onChange(of: query) { newValue in viewModel.setQuery(value: newValue) }
        .navigationDestination(for: Int32.self) {
            PokemonDetailView(pokemonId: $0, namespace: artworkNamespace)
        }
        .navigationTitle("Pokédex")
    }

    private func loadMoreIfNeeded(currentId: Int32) {
        guard !paging.loading, !paging.finished else { return }
        if let lastIndex = items.indices.last,
           items[lastIndex - min(5, items.count - 1)].id == currentId || items[lastIndex].id == currentId {
            viewModel.loadMore()
        }
    }
}
