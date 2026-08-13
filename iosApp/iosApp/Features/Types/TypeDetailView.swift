import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 属性相克详情：沉浸式 header（无图）+ 各相克关系块。
struct TypeDetailView: View {
    let typeId: Int32
    @StateViewModel var viewModel = TypeDetailViewModel(repository: KoinDependencies().pokemonRepository)

    var body: some View {
        if let d = viewModel.typeDetail {
            let dr = d.damageRelations
            ScrollView {
                VStack(spacing: 0) {
                    DetailHeader(name: d.name, artworkUrl: nil, types: [d.name])
                    VStack(alignment: .leading, spacing: 16) {
                        relationSection("攻击时双倍伤害", dr.doubleDamageTo)
                        relationSection("攻击时伤害减半", dr.halfDamageTo)
                        relationSection("攻击时无效", dr.noDamageTo)
                        relationSection("受到双倍伤害", dr.doubleDamageFrom)
                        relationSection("受到伤害减半", dr.halfDamageFrom)
                    }
                    .padding(16)
                    .offset(y: -28)
                    .background(
                        Color(.systemBackground)
                            .clipShape(RoundedRectangle(cornerRadius: 28))
                            .offset(y: -28)
                    )
                }
            }
            .ignoresSafeArea(edges: .top)
            .navigationBarHidden(true)
            .navigationBarBackButtonHidden(true)
        } else {
            ProgressView().onAppear { viewModel.setId(id: typeId) }
        }
    }

    private func relationSection(_ title: String, _ members: [NamedApiResource]) -> some View {
        SectionCard(title: title) {
            if members.isEmpty {
                Text("—").foregroundStyle(.secondary)
            } else {
                LazyVGrid(
                    columns: [GridItem(.adaptive(minimum: 80), spacing: 8)],
                    alignment: .leading,
                    spacing: 8
                ) {
                    ForEach(members, id: \.name) { res in
                        TypeChip(type: res.name)
                            .frame(maxWidth: .infinity)
                    }
                }
            }
        }
    }
}
