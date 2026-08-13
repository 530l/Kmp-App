import SwiftUI
import Shared
import Kingfisher

// 列表卡片：主属性色渐变背景 + 半透明精灵球水印 + 居中大图 + 白字编号和名字。
// primaryType 还没预取到时回退中性灰，加载后自动变属性色。
struct PokeCard: View {
    let item: PokemonListItem
    // 共享元素转场的命名空间；从 PokedexView 传进来，nil 时关闭转场
    var namespace: Namespace.ID? = nil

    var body: some View {
        let base = item.primaryType.map { typeColor($0) } ?? Color(.systemGray5)
        let gradient = LinearGradient(
            colors: [base, mix(base, .white, 0.3)],
            startPoint: .top,
            endPoint: .bottom
        )
        ZStack(alignment: .topTrailing) {
            gradient
            PokeballBackground()
                .frame(width: 110, height: 110)
                .padding(.trailing, -10)
            VStack(spacing: 4) {
                // 图片标记为 zoom 转场源：点进详情时这张图放大飞过去
                KFImage(URL(string: item.artworkUrl))
                    .resizable()
                    .scaledToFit()
                    .frame(height: 110)
                    .zoomSource(item.id, in: namespace)
                Text(String(format: "#%03d", item.id))
                    .font(.system(size: 11))
                    .foregroundStyle(.white.opacity(0.85))
                Text(item.name.capitalized)
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(.white)
            }
            .frame(maxWidth: .infinity)
            .padding(12)
        }
        .clipShape(RoundedRectangle(cornerRadius: 20))
    }
}
