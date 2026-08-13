import SwiftUI

// 精灵球轮廓水印：卡片和详情 header 放一个半透明精灵球当装饰，用 Canvas 画，不引图片资源。
struct PokeballBackground: View {
    var color: Color = .white
    var alpha: Double = 0.15

    var body: some View {
        Canvas { ctx, size in
            let cx = size.width / 2
            let cy = size.height / 2
            let r = min(size.width, size.height) / 2 * 0.9
            let stroke = r * 0.13
            let col = color.opacity(alpha)

            // 外圈
            ctx.stroke(circle(cx: cx, cy: cy, r: r), with: .color(col), lineWidth: stroke)
            // 中间横线
            var line = Path()
            line.move(to: CGPoint(x: cx - r, y: cy))
            line.addLine(to: CGPoint(x: cx + r, y: cy))
            ctx.stroke(line, with: .color(col), lineWidth: stroke)
            // 中圈
            ctx.stroke(circle(cx: cx, cy: cy, r: r * 0.3), with: .color(col), lineWidth: stroke)
            // 中心点
            ctx.fill(circle(cx: cx, cy: cy, r: r * 0.16), with: .color(Color.white.opacity(alpha * 0.6)))
        }
    }

    private func circle(cx: CGFloat, cy: CGFloat, r: CGFloat) -> Path {
        Path(ellipseIn: CGRect(x: cx - r, y: cy - r, width: r * 2, height: r * 2))
    }
}
