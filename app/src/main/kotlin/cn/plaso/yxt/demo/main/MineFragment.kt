package cn.plaso.yxt.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import cn.plaso.yxt.demo.R
import cn.plaso.yxt.demo.SessionStore
import cn.plaso.yxt.demo.databinding.FragmentMineBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 「我的」。SDK 没有公开的我的页面，自己展示会话信息。 */
class MineFragment : Fragment() {

    private var binding: FragmentMineBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentMineBinding.inflate(inflater, container, false)
        binding = viewBinding
        viewBinding.btnLogout.setOnClickListener { confirmLogout() }
        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun render() {
        val viewBinding = binding ?: return
        val form = SessionStore.loadForm()
        val expireAt = SessionStore.tokenExpireAt()
        val expireText = if (expireAt <= 0L) {
            "-"
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(expireAt))
        }
        viewBinding.tvProfile.text = listOf(
            getString(R.string.mine_login_name) to form.loginName.ifEmpty { "-" },
            getString(R.string.mine_user_type) to form.userType.displayName,
            getString(R.string.mine_env) to "${form.environment.displayName} (${form.environment.host})",
            getString(R.string.mine_app_id) to form.appId.ifEmpty { "-" },
            getString(R.string.mine_expire) to expireText
        ).joinToString("\n\n") { (title, value) -> "$title：$value" }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.logout) { _, _ ->
                (activity as? MainActivity)?.logout()
            }
            .show()
    }
}
