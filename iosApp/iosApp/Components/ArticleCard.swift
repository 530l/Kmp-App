import SwiftUI
import Shared

// 文章卡片：大厂风格的简洁信息流卡片
struct ArticleCard: View {
    let article: Article

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            // 标签行：新文章 + 分类
            if article.fresh || !article.tags.isEmpty || !article.displayChapter.isEmpty {
                HStack(spacing: 6) {
                    if article.fresh {
                        tagLabel("新", bg: wanRed)
                    }
                    ForEach(article.tags.prefix(1), id: \.name) { tag in
                        tagLabel(tag.name, outlined: true)
                    }
                    if !article.displayChapter.isEmpty {
                        Text(article.displayChapter)
                            .font(.caption2)
                            .foregroundColor(wanBlue)
                            .lineLimit(1)
                    }
                    Spacer()
                }
            }

            // 标题
            Text(article.title)
                .font(.system(size: 16, weight: .semibold))
                .lineLimit(2)

            // 描述
            if !article.desc.isEmpty {
                Text(article.desc)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }

            // 底栏：作者 + 时间
            HStack {
                Text(article.displayAuthor.isEmpty ? "未知作者" : article.displayAuthor)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
                Spacer()
                Text(article.niceDate)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding(.top, 4)
        }
        .padding(14)
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
    }

    // 小标签
    private func tagLabel(_ text: String, bg: Color = wanBlue, outlined: Bool = false) -> some View {
        Text(text)
            .font(.caption2)
            .foregroundColor(outlined ? wanBlue : .white)
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(
                outlined
                    ? RoundedRectangle(cornerRadius: 4).stroke(wanBlue.opacity(0.4))
                    : RoundedRectangle(cornerRadius: 4).fill(bg)
            )
    }
}
