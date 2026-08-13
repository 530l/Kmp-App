import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 属性列表页：每行一个属性圆点 + 名字，点进去看相克关系。
struct TypesView: View {
    @StateViewModel var viewModel = TypesViewModel(repository: KoinDependencies().pokemonRepository)

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(viewModel.types, id: \.name) { res in
                    NavigationLink(value: res.id) {
                        HStack(spacing: 16) {
                            Circle()
                                .fill(typeColor(res.name))
                                .frame(width: 32, height: 32)
                            Text(res.name.capitalized)
                                .font(.title3.weight(.medium))
                                .foregroundStyle(.primary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.footnote)
                                .foregroundStyle(.tertiary)
                        }
                        .padding(.horizontal, 20)
                        .padding(.vertical, 16)
                    }
                    .buttonStyle(.plain)
                    Divider().padding(.leading, 68)
                }
            }
        }
        .navigationTitle("属性")
        .navigationDestination(for: Int32.self) { TypeDetailView(typeId: $0) }
    }
}
