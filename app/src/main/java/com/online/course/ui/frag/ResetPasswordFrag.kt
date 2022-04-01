package com.online.course.ui.frag

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton

import com.online.course.R
import com.online.course.databinding.FragSignInBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.net.ApiService
import com.online.course.model.BaseResponse
import com.online.course.model.Data
import com.online.course.model.Login
import com.online.course.model.Response
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SignInPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abstract.UserAuthFrag
import com.online.course.ui.widget.LoadingDialog
import kotlin.math.log


class ResetPasswordFrag : UserAuthFrag() {

    private lateinit var mPresenter: Presenter.SignInPresenter
    private lateinit var mBinding: FragSignInBinding

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableLoginBtn()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1421
        private const val TAG = "SignInFrag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSignInBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        mGoogleBtn = mBinding.signInGoogleLayout
        mFacebookBtn = mBinding.signInFacebookLayout

        mBinding.signInLoginBtn.setOnClickListener(this)
        mBinding.signInSignUpBtn.setOnClickListener(this)
        mBinding.signInForgotPasswordBtn.setOnClickListener(this)
        mBinding.signInEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signInPasswordEdtx.addTextChangedListener(mInputTextWatcher)
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.signInLoginBtn -> {
                val username = mBinding.signInEmailPhoneEdtx.text.toString()
                val password = mBinding.signInPasswordEdtx.text.toString()


                val loginObj = Login()
                loginObj.username = username
                loginObj.password = password

                mPresenter.login(loginObj)
            }
        }
    }



    override fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.



            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog?.show(childFragmentManager, null)

        } catch (e: ApiException) {

        }
    }

    override fun getFacebookSignInCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result != null) {

                }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }
        }
    }

    fun onSuccessfulLogin(response : Data<Response>) {
        if (response.isSuccessful) {


            ApiService.createAuthorizedApiService(requireContext(), response.data!!.token)

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(App.SHOULD_REGISTER, !response.data!!.profileState.isNullOrEmpty())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            activity?.finish()
        } else {
            onErrorOccured(response)
        }
    }


    private fun enableDisableLoginBtn() {
        val username = mBinding.signInEmailPhoneEdtx.text.toString()
        val password = mBinding.signInPasswordEdtx.text.toString()
        val loginBtn = mBinding.signInLoginBtn

        if (username.isNotEmpty() && password.isNotEmpty()) {
            var checkForEmail = false

            for ((index, char) in username.withIndex()) {
                if (index == 0 && char == '+')
                    continue

                if (!char.isDigit()) {
                    checkForEmail = true
                    break
                }
            }

            if (checkForEmail) {
                val isValidEmail = username.length >= 3 && username.contains("@")
                        && username.contains(".")
                if (isValidEmail)
                    loginBtn.isEnabled = true
            } else {
                loginBtn.isEnabled = true
            }

        } else if (loginBtn.isEnabled) {
            loginBtn.isEnabled = false
        }
    }
}