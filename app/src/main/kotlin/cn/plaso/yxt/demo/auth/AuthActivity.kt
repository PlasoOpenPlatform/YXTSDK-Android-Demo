package cn.plaso.yxt.demo.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cn.plaso.yxt.demo.R
import cn.plaso.yxt.demo.databinding.ActivityAuthBinding
import cn.plaso.yxt.demo.main.MainActivity
import kotlinx.coroutines.launch

/** 授权页：服务器环境、AppId、SignKey、用户名、用户类型。 */
class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val environments = ServerEnvironment.entries
        val types = UserType.entries
        binding.spEnvironment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("") + environments.map { "${it.displayName} (${it.host})" }
        )
        binding.spUserType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("") + types.map { it.displayName }
        )
        binding.etAppId.setText("")
        binding.etSignKey.setText("")
        binding.etLoginName.setText("")
        binding.spEnvironment.setSelection(0)
        binding.spUserType.setSelection(0)
        binding.spEnvironment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val environment = environments.getOrNull(position - 1) ?: return
                viewModel.selectEnvironment(environment)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.btnStart.setOnClickListener {
            viewModel.login(
                environments.getOrNull(binding.spEnvironment.selectedItemPosition - 1),
                binding.etAppId.text.toString(),
                binding.etSignKey.text.toString(),
                binding.etLoginName.text.toString(),
                types.getOrNull(binding.spUserType.selectedItemPosition - 1)
            )
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.btnStart.isEnabled = state !is AuthUiState.Loading
                    binding.btnStart.setText(
                        if (state is AuthUiState.Loading) R.string.auth_loading else R.string.auth_start
                    )
                    if (state is AuthUiState.Failed) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = state.message
                        Toast.makeText(this@AuthActivity, state.message, Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvError.visibility = View.GONE
                    }
                    if (state is AuthUiState.Success) {
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
