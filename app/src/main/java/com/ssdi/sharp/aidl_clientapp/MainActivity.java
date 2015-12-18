package com.ssdi.sharp.aidl_clientapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssdi.sharp.aidl_additionservice.IMyAdditionAidlInterface;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.etValue1)
    EditText etValue1;
    @Bind(R.id.etValue2)
    EditText etValue2;
    @Bind(R.id.btnAdd)
    Button btnAdd;
    @Bind(R.id.tvResult)
    TextView tvResult;

    protected IMyAdditionAidlInterface addServiceAidl;
    ServiceConnection addServiceConnection;
    private Context mContext;
    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mContext = MainActivity.this;

        initConnection();

    }

    private void initConnection() {

        addServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Toast.makeText(mContext, "ServiceConnected..", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding is done - Service connected");

                addServiceAidl = IMyAdditionAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(mContext, "ServiceDisconnected..", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding - Service disconnected");

                addServiceAidl = null;
            }
        };

        if (addServiceAidl == null) {
            Intent in = new Intent();
            in.setAction("service.Calculator");
            bindService(in, addServiceConnection, Service.BIND_AUTO_CREATE);
        }

    }

    @OnClick(R.id.btnAdd)
    void submitAddButton(View view) {
        // Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "btnAddClicked!!");

        String value1 = etValue1.getText().toString();
        String value2 = etValue2.getText().toString();

        //value1 == null || value2 == null ||
        if (value1.trim().equals("") || value2.trim().equals("")) {
            Toast.makeText(mContext, "Plz provide values for add!!", Toast.LENGTH_SHORT).show();
            return;
        }

        int num1 = Integer.parseInt(value1);
        int num2 = Integer.parseInt(value2);

        try {
            int sum = addServiceAidl.add(num1, num2);
            tvResult.setText(String.valueOf(sum));
            Toast.makeText(this, "Addition result is: " + sum, Toast.LENGTH_SHORT).show();
            Log.d("IRemote", "Binding - Add operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(addServiceConnection);
    }
}
