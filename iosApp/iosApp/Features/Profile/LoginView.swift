import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

// 登录/注册页
struct LoginView: View {
    @StateViewModel var viewModel = LoginViewModel(
        repo: KoinDependencies().accountRepository
    )
    @State private var username = ""
    @State private var password = ""
    @State private var repassword = ""
    let onSuccess: () -> Void

    var body: some View {
        let state = viewModel.state
        let isLogin = state.mode == .login

        VStack(spacing: 16) {
            Spacer().frame(height: 32)

            Text(isLogin ? "欢迎回来" : "创建账号")
                .font(.title.weight(.bold))
                .frame(maxWidth: .infinity, alignment: .leading)

            // 用户名
            TextField("用户名", text: $username)
                .textFieldStyle(.roundedBorder)
                .submitLabel(.next)

            // 密码
            SecureField("密码", text: $password)
                .textFieldStyle(.roundedBorder)

            // 确认密码（注册模式）
            if !isLogin {
                SecureField("确认密码", text: $repassword)
                    .textFieldStyle(.roundedBorder)
            }

            // 错误信息
            if let error = state.error {
                Text(error)
                    .font(.caption)
                    .foregroundColor(wanRed)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            // 提交按钮
            Button {
                viewModel.submit(username: username, password: password, repassword: repassword)
            } label: {
                Group {
                    if state.loading {
                        ProgressView().tint(.white)
                    } else {
                        Text(isLogin ? "登录" : "注册")
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(wanBlue, in: RoundedRectangle(cornerRadius: 24))
                .foregroundColor(.white)
            }
            .disabled(state.loading)

            // 切换登录/注册
            Button {
                viewModel.switchMode(mode: isLogin ? .register : .login)
            } label: {
                Text(isLogin ? "没有账号？去注册" : "已有账号？去登录")
                    .font(.subheadline)
                    .foregroundColor(wanBlue)
            }

            Spacer()
        }
        .padding(.horizontal, 24)
        .navigationTitle(isLogin ? "登录" : "注册")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("取消") { onSuccess() }
            }
        }
        // 监听登录成功
        .onChange(of: state.success) { success in
            if success { onSuccess() }
        }
    }
}
