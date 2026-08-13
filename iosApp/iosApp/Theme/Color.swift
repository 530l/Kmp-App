import SwiftUI
import Shared

// 18 种属性色唯一来源在 shared 的 PokemonTypeColors，iOS 只做 SwiftUI Color 转换，
// 避免和 Android 各维护一份色表导致双端颜色漂移。
extension Color {
    init(typeArgb argb: Int64) {
        let v = UInt64(bitPattern: argb)
        self.init(
            .sRGB,
            red: Double((v >> 16) & 0xFF) / 255.0,
            green: Double((v >> 8) & 0xFF) / 255.0,
            blue: Double(v & 0xFF) / 255.0,
            opacity: Double((v >> 24) & 0xFF) / 255.0
        )
    }
}

func typeColor(_ name: String) -> Color {
    Color(typeArgb: PokemonTypeColors.shared.argb(type: name))
}

// 品牌主色：精灵球红
let pokeRed = Color(red: 0.933, green: 0.082, blue: 0.082)

// 颜色线性插值：卡片 / header 的渐变需要「主色 → 主色提亮」，靠它算出过渡色
func mix(_ a: Color, _ b: Color, _ t: Double) -> Color {
    let ua = UIColor(a), ub = UIColor(b)
    var ar: CGFloat = 0, ag: CGFloat = 0, ab: CGFloat = 0, aa: CGFloat = 0
    var br: CGFloat = 0, bg: CGFloat = 0, bb: CGFloat = 0, ba: CGFloat = 0
    ua.getRed(&ar, green: &ag, blue: &ab, alpha: &aa)
    ub.getRed(&br, green: &bg, blue: &bb, alpha: &ba)
    return Color(
        red: Double(ar + (br - ar) * t),
        green: Double(ag + (bg - ag) * t),
        blue: Double(ab + (bb - ab) * t)
    )
}
