package s.com.videoapp.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import s.com.videoapp.R;
import s.com.videoapp.databinding.ActivityLoginBinding;
import s.com.videoapp.utils.StoreUserData;
import s.com.videoapp.utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    Activity activity;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activity = this;
        storeUserData = new StoreUserData(activity);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnLogin) {
            if (Utils.isEmpty(binding.edEmail)) {
                Utils.showAlert(activity, "Please enter email-id.");
            } else if (!Utils.isValidEmail(binding.edEmail)) {
                Utils.showAlert(activity, "Please enter valid email-id.");
            } else if (Utils.isEmpty(binding.edPassword)) {
                Utils.showAlert(activity, "Please enter password.");
            } else if (!Utils.isOnline(activity)) {
                Utils.internetAlert(activity);
            } else {
                login();
            }
        }
    }

    public void login() {
        Utils.showProgress(activity);

    }
}
