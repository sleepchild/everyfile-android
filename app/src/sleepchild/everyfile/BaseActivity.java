package sleepchild.everyfile;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.content.SharedPreferences;

public class BaseActivity extends Activity {

    public Context ctx;
    private InputMethodManager imm;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        ctx = getApplicationContext();
        prefs = ctx.getSharedPreferences("unique_preference_name",ctx.MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }
    
    <T extends View> T findView(int resid){
        return (T) findViewById(resid);
    }
    
    public void startActivity(Class<?> clazz){
        startActivity(new Intent(this, clazz));
    }
    
    public void startActivityForResult(Class<?> clazz, int requestCode){
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    public void toast(String msg){
        Toast.makeText(ctx, msg, 500).show();
    }
    
    public void show(View v){
        v.setVisibility(View.VISIBLE);
    }
    
    public void hide(View v){
        v.setVisibility(View.GONE);
    }

    public void showKeyboard(View v){
        v.requestFocus();
        imm.showSoftInput(v,1);
    }
    
    //@Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {
        // TODO: Implement this method
    }
}
