import SwiftUI

// 设置页：外观 / 关于，用 SectionCard 统一外观。
struct SettingsView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                SectionCard(title: "外观") {
                    row("主题", "跟随系统（亮 / 暗自动切换）")
                }
                SectionCard(title: "关于") {
                    row("数据来源", "PokeAPI · pokeapi.co")
                    row("技术栈", "KMP · SwiftUI · NavigationStack · Kingfisher")
                    row("图片素材", "PokeAPI Sprites")
                }
            }
            .padding(16)
        }
        .navigationTitle("设置")
    }

    private func row(_ key: String, _ value: String) -> some View {
        HStack {
            Text(key)
            Spacer()
            Text(value)
                .foregroundStyle(.secondary)
                .font(.footnote)
                .multilineTextAlignment(.trailing)
        }
    }
}
