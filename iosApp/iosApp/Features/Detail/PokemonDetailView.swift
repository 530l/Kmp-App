import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 宝可梦详情：沉浸式属性色 header + 圆角内容区上盖（像底部 sheet）+ 能力 + 图鉴资料。
struct PokemonDetailView: View {
    let pokemonId: Int32
    // 和列表卡片共享的命名空间；id 与卡片图一致，详情根视图声明 zoom 目标
    var namespace: Namespace.ID? = nil
    @StateViewModel var viewModel = DetailViewModel(repository: KoinDependencies().pokemonRepository)

    var body: some View {
        Group {
            if let p = viewModel.pokemon {
                // GeometryReader 拿顶部安全区高度（状态栏）；ScrollView 自己 ignore 后内部子视图读不到，
                // 在这一层读出来传给 header，让它把返回箭头等推到状态栏下方，背景仍铺到顶。
                GeometryReader { proxy in
                    ScrollView {
                        VStack(spacing: 0) {
                            DetailHeader(
                                name: p.name,
                                artworkUrl: Self.artworkUrl(p.id),
                                types: (p.types as? [PokemonType] ?? []).map { $0.type.name },
                                topInset: proxy.safeAreaInsets.top
                            )
                            // 内容区：圆角从 header 下沿盖上来
                            VStack(spacing: 16) {
                                statsBlock(p)
                                if let s = viewModel.species {
                                    infoBlock(s, fallback: p.name)
                                }
                            }
                            .padding(.top, 20)
                            .padding(.bottom, 24)
                            .offset(y: -28)
                            .background(
                                Color(.systemBackground)
                                    .clipShape(RoundedRectangle(cornerRadius: 28))
                                    .offset(y: -28)
                            )
                        }
                    }
                }
                .ignoresSafeArea(edges: .top)
                .navigationBarHidden(true)
                .navigationBarBackButtonHidden(true)
                .lightStatusBar()
            } else {
                ProgressView().onAppear { viewModel.setId(id: pokemonId) }
            }
        }
        // 详情作为 zoom 转场目标：sourceID 和被点的卡片图配对
        .zoomDestination(pokemonId, in: namespace)
    }

    private static func artworkUrl(_ id: Int32) -> String {
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/\(id).png"
    }

    private func statsBlock(_ p: Pokemon) -> some View {
        SectionCard(title: "基础能力") {
            ForEach(p.stats as? [PokemonStat] ?? [], id: \.stat.name) { st in
                StatBar(label: st.stat.name, value: st.baseStat)
            }
            Text("身高 \(String(format: "%.1f", Double(p.height) / 10.0)) m · 体重 \(String(format: "%.1f", Double(p.weight) / 10.0)) kg")
                .font(.subheadline)
                .padding(.top, 4)
        }
        .padding(.horizontal, 16)
    }

    // 优先中文名和简介，没匹配到就回退英文
    private func infoBlock(_ s: PokemonSpecies, fallback: String) -> some View {
        let names = s.names as? [PokemonName] ?? []
        let genera = s.genera as? [PokemonGenus] ?? []
        let flavors = s.flavorTextEntries as? [PokemonFlavorText] ?? []
        let name = names.first { $0.language.name == "zh-Hans" }?.name
            ?? names.first { $0.language.name == "en" }?.name
            ?? fallback
        let genus = genera.first { $0.language.name == "zh-Hans" }?.genus
            ?? genera.first { $0.language.name == "en" }?.genus
        let flavor = flavors.first { $0.language.name == "zh-Hans" }?.flavorText
            ?? flavors.first { $0.language.name == "en" }?.flavorText

        return SectionCard(title: "图鉴资料") {
            Text(name).font(.body.weight(.medium))
            if let genus = genus { Text("分类：\(genus)").font(.subheadline) }
            if let flavor = flavor {
                Text(flavor.replacingOccurrences(of: "\n", with: " ")).font(.subheadline)
            }
        }
        .padding(.horizontal, 16)
    }
}
