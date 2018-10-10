package s.com.videoapp.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import s.com.videoapp.R;

public class Utils {

	/*public static API callApi() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(API.class);
    }*/
    static ProgressDialog progressDialog;

    public static void showProgress(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public static void showProgress(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        else
            Log.i("Dialog", "already dismissed");
    }
	public static void serverError(Activity activity, int code) {
        dismissProgress();
        String message = "";
        switch (code) {
            case 400:
                message = "400 - Bad Request";
                break;
            case 401:
                message = "401 - Unauthorized";
                break;
            case 404:
                message = "404 - Not Found";
                break;
            case 500:
                message = "500 - Internal Server Error";
                break;
            default:
                message = activity.getString(R.string.server_error);
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
    public static void showAlert(final Activity activity, String title,
                                 String message) {
        new AlertDialog.Builder(activity).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showAlert(Activity activity, String message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .create()
                .show();
    }

    public static void showAlert(final Activity activity, String message, final boolean finish) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (finish) {
                            activity.finish();
                        }
                    }
                })
                .create()
                .show();
    }

    public static void internetAlert(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Please check internet connection.")
                .setPositiveButton("Ok", null)
                .create()
                .show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isValidEmail(EditText email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    public static void selectDate(Activity activity, final Button btnDate) {
        new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                btnDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DATE, dayOfMonth);
                //calendar.getTimeInMillis() + "";
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void selectTime(Activity activity, final Button btnTime) {
        new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                btnTime.setText(selectedHour + ":" + selectedMinute);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false).show(); //is24HourView
    }

    public static boolean isEmpty(View view) {
        if (view instanceof EditText) {
            if (((EditText) view).getText().toString().length() == 0) {
                return true;
            }
        } else if (view instanceof Button) {
            if (((Button) view).getText().toString().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void showListDialog(Activity activity, final Button btnShow, final ArrayList<String> list) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, list);
        //pass custom layout with single textview to customize list
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnShow.setText(list.get(which));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void hideKB(Activity activity, View view) {
        //View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}