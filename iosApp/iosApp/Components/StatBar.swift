import SwiftUI

// 能力条：圆角 track + 按数值分段着色（弱 / 中 / 强），一眼看出强弱。
struct StatBar: View {
    let label: String
    let value: Int32

    var body: some View {
        let ratio = min(max(Double(value) / 255.0, 0), 1)
        HStack(spacing: 6) {
            Text(label)
                .font(.system(size: 12))
                .frame(width: 96, alignment: .leading)
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    Capsule().fill(Color(.systemGray5))
                    Capsule().fill(statColor(value)).frame(width: geo.size.width * ratio)
                }
            }
            .frame(height: 8)
            Text("\(value)")
                .font(.system(size: 12, weight: .semibold))
                .frame(width: 36, alignment: .trailing)
        }
    }

    // 弱红、中黄、强绿
    private func statColor(_ v: Int32) -> Color {
        if v < 50 { return Color(red: 0.925, green: 0.333, blue: 0.333) }
        if v < 90 { return Color(red: 0.984, green: 0.722, blue: 0.251) }
        return Color(red: 0.357, green: 0.725, blue: 0.392)
    }
}
