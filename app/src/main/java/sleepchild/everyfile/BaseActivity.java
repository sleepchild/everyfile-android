package sleepchild.everyfile;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.widget.*;
import android.view.inputmethod.*;
import android.content.*;

public class BaseActivity extends Activity
{
    Context ctx;
    InputMethodManager imm;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //
        ctx = getApplicationContext();
        super.onCreate(savedInstanceState);
        
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        init();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    
    public void init(){
        // implement
    }
    
    public void showKeyboard(View v){
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
    
    public void toast(String msg){
        Toast.makeText(ctx, msg, 500).show();
    }
    
    public View vid(int resid){
        return findViewById(resid);
    }    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
}
