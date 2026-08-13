import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 我的页面：登录态 + 菜单
struct ProfileView: View {
    @StateViewModel var viewModel = ProfileViewModel(
        accountRepository: KoinDependencies().accountRepository
    )
    @State private var showLogin = false
    @State private var showLogoutAlert = false

    var body: some View {
        let state = viewModel.state

        ScrollView {
            VStack(spacing: 0) {
                // 用户信息区
                VStack(spacing: 12) {
                    Image(systemName: "person.circle.fill")
                        .font(.system(size: 64))
                        .foregroundColor(wanBlue)
                    Text(state.loggedIn ? state.username : "点击登录")
                        .font(.title3.weight(.semibold))
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 24)
                .background(Color(.secondarySystemBackground))
                .onTapGesture {
                    if !state.loggedIn { showLogin = true }
                }

                // 菜单
                if state.loggedIn {
                    Divider()
                    NavigationLink(value: "collections") {
                        menuRow(icon: "star.fill", title: "我的收藏")
                    }
                    .buttonStyle(.plain)
                    Divider()
                    Button {
                        showLogoutAlert = true
                    } label: {
                        menuRow(icon: "rectangle.portrait.and.arrow.right", title: "退出登录")
                    }
                    .buttonStyle(.plain)
                    Divider()
                }
            }
        }
        .navigationTitle("我的")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { viewModel.syncLoginState() }
        .sheet(isPresented: $showLogin) {
            // 登录页用 NavigationStack 包裹，方便 dismiss
            NavigationStack {
                LoginView(onSuccess: {
                    showLogin = false
                    viewModel.syncLoginState()
                })
            }
        }
        .alert("退出登录", isPresented: $showLogoutAlert) {
            Button("确定", role: .destructive) {
                viewModel.logout()
            }
            Button("取消", role: .cancel) {}
        } message: {
            Text("确定要退出登录吗？")
        }
    }

    private func menuRow(icon: String, title: String) -> some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(wanBlue)
                .frame(width: 24)
            Text(title)
                .foregroundColor(.primary)
            Spacer()
            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(Color(.systemBackground))
    }
}
