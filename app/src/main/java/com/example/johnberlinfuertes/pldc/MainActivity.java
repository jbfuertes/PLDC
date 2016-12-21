package com.example.johnberlinfuertes.pldc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView disclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disclaimer = (TextView)findViewById(R.id.disclaimer);
        disclaimer.setText(noticeText());
    }
    public void declineButtonClicked(View view){
        finish();
    }
    public void acceptButtonClicked(View view){
        Intent i = new Intent(this,WifiConnect.class);
        startActivity(i);
        finish();
    }
    public String noticeText(){
        return "Disclaimer: This Application is created for the sole purpose of " +
                "Education and Practice of the creator. Any misuse, Unauthorized " +
                "distribution, selling or any means of illigal use of the said project " +
                "will held only the user liable. If you agree with this and will use the app" +
                "press accept otherwise press decline button." +
                "Also note that only those default pldt password and wifi ssid can be hacked";
    }
}
