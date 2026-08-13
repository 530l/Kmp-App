import SwiftUI
import Kingfisher

// 详情页沉浸式 header：主属性色渐变背景越过状态栏，返回键浮左上，
// 居中大图 + 白色名字 + 属性胶囊（白底彩字，在彩色背景上对比清晰）。
struct DetailHeader: View {
    let name: String
    var artworkUrl: String?
    let types: [String]
    // 顶部安全区高度（状态栏），由外层 GeometryReader 读出传入，内容用它避开刘海/状态栏
    var topInset: CGFloat = 0
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        let base = typeColor(types.first ?? "normal")
        let gradient = LinearGradient(
            colors: [base, mix(base, .white, 0.35)],
            startPoint: .top,
            endPoint: .bottom
        )
        // 内容按安全区排版（不压状态栏），渐变背景单独 ignoresSafeArea 上溢到状态栏后
        VStack(spacing: 8) {
            HStack {
                Button { dismiss() } label: {
                    Image(systemName: "chevron.left")
                        .font(.title3.weight(.semibold))
                        .foregroundStyle(.white)
                }
                Spacer()
            }
            if let url = artworkUrl {
                KFImage(URL(string: url))
                    .resizable()
                    .scaledToFit()
                    .frame(height: 220)
            }
            Text(name.capitalized)
                .font(.system(size: 28, weight: .heavy))
                .foregroundStyle(.white)
            HStack(spacing: 8) {
                ForEach(types, id: \.self) { TypeChip(type: $0, onTint: true) }
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.top, topInset)
        .padding(.horizontal)
        .padding(.bottom, 24)
        .background(
            ZStack(alignment: .topTrailing) {
                gradient
                PokeballBackground()
                    .frame(width: 220, height: 220)
                    .padding(.trailing, -20)
            }
            .ignoresSafeArea(edges: .top)
        )
    }
}
