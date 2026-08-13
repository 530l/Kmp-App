import SwiftUI

// 属性胶囊。在属性色背景上（彩色 header / 卡片）用白底彩字对比清晰，
// 在中性背景上用彩底白字。两端样式统一。
struct TypeChip: View {
    let type: String
    var onTint: Bool = false

    var body: some View {
        let c = typeColor(type)
        Text(type.capitalized)
            .font(.system(size: 12, weight: .bold))
            .foregroundStyle(onTint ? c : .white)
            .padding(.horizontal, 12)
            .padding(.vertical, 5)
            .background(onTint ? Color.white.opacity(0.92) : c, in: Capsule())
    }
}
