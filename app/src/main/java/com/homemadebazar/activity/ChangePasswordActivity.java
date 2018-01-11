package com.homemadebazar.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.homemadebazar.R;
import com.homemadebazar.model.BaseModel;
import com.homemadebazar.model.UserModel;
import com.homemadebazar.network.HttpRequestHandler;
import com.homemadebazar.network.api.ApiCall;
import com.homemadebazar.network.apicall.ChangePasswordApiCall;
import com.homemadebazar.util.Constants;
import com.homemadebazar.util.DialogUtils;
import com.homemadebazar.util.SharedPreference;
import com.homemadebazar.util.Utils;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    @Override
    protected void initUI() {
        userModel = SharedPreference.getUserModel(ChangePasswordActivity.this);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

    }

    @Override
    protected void initialiseListener() {
        findViewById(R.id.btn_update_profile).setOnClickListener(this);
    }

    @Override
    protected void setData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_profile:
                if (isValid())
                    changePassword();
                break;
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(etCurrentPassword.getText().toString().trim())) {
            DialogUtils.showAlert(ChangePasswordActivity.this, "Please enter current password.");
            return false;
        } else if (TextUtils.isEmpty(etNewPassword.getText().toString().trim())) {
            DialogUtils.showAlert(ChangePasswordActivity.this, "Please enter new password.");
            return false;
        } else if (TextUtils.isEmpty(etConfirmPassword.getText().toString().trim())) {
            DialogUtils.showAlert(ChangePasswordActivity.this, "Please enter confirm password.");
            return false;
        } else if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString().trim())) {
            DialogUtils.showAlert(ChangePasswordActivity.this, "New password and confirm password doesnot match.");
            return false;
        }
        return true;
    }

    private void changePassword() {
        try {
            final ChangePasswordApiCall apiCall = new ChangePasswordApiCall(userModel.getUserId(), etCurrentPassword.getText().toString().trim(), etNewPassword.getText().toString().trim());
            HttpRequestHandler.getInstance(this.getApplicationContext()).executeRequest(apiCall, new ApiCall.OnApiCallCompleteListener() {

                @Override
                public void onComplete(Exception e) {
                    if (e == null) { // Success
                        try {
                            BaseModel baseModel = apiCall.getBaseModel();
                            if (baseModel.getStatusCode() == Constants.ServerResponseCode.SUCCESS) {
                                DialogUtils.showAlert(ChangePasswordActivity.this, baseModel.getStatusMessage());
                            } else {
                                DialogUtils.showAlert(ChangePasswordActivity.this, baseModel.getStatusMessage());
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else { // Failure
                        Utils.handleError(e.getMessage(), ChangePasswordActivity.this, null);
                    }
                }
            });
        } catch (Exception e) {
            Utils.handleError(e.getMessage(), ChangePasswordActivity.this, null);
        }
    }

}