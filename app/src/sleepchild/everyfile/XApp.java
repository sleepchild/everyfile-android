package sleepchild.everyfile;

import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.io.IOException;
import android.os.Build;

public class XApp extends Application
{
    private static Context ctx;
    private Thread.UncaughtExceptionHandler prevHandler;
    
    @Override
    public void onCreate(){
        super.onCreate();
        SearchService.start(getApplicationContext());
        XApp.ctx = getApplicationContext();
        
        prevHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
                @Override
                public void uncaughtException(Thread t, Throwable ex){
                    //
                    log(ex);
                    //
                    if(prevHandler!=null){
                        prevHandler.uncaughtException(t, ex);
                    }else{
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }
            });

    }

    private void log(Throwable ex){
        if(ex==null){
            return;
        }
        String dirp = "/sdcard/.sleepchild/everyfile/logs/crash/";
        new File(dirp).mkdirs();

        String dd = new Date().toLocaleString()
            .replace(" ","_")
            .replace(",","_")
            .replace("__","_").toLowerCase();

        String msg="sleepchild.everyfile: crash log\n\n";
        msg+= dd+"\n\n";

        // device info
        msg+="device info\n________________\n\n";
        msg+= "device: "+Build.DEVICE+"\n";
        msg+= "brand: "+Build.BRAND+"\n";
        msg+= "manufacturer: "+Build.MANUFACTURER+"\n";
        msg+= "model: "+Build.MODEL+"\n";
        msg+= "os: "+Build.VERSION.BASE_OS+"\n";
        msg+= "release: "+Build.VERSION.RELEASE+"\n";
        msg+= "sdk: "+Build.VERSION.SDK+"\n";
        msg+= "abi: "+Build.CPU_ABI;


        // start log
        msg+="\n\n\nstart log\n________\n________\n\n";

        msg += ex.getMessage()+"\n\n";

        if(ex.getStackTrace()!=null){
            for(StackTraceElement st : ex.getStackTrace()){
                msg += st.toString()+"\n";
            }
        }

        if(ex.getCause()!=null){
            msg+="\n\n________________________\n\n";

            Throwable mx = ex.getCause();

            msg += mx.getMessage()+"\n\n";

            if(mx.getStackTrace()!=null){
                for(StackTraceElement st : mx.getStackTrace()){
                    msg+=st.toString()+"\n";
                }
            }
        }

        msg+= "\n\n________\n________\nend";

        if(!msg.isEmpty()){

            String fpath = dirp + "crash-" + dd + ".log";
            try{
                FileOutputStream o = new FileOutputStream(fpath);
                o.write(msg.getBytes());
                o.flush();
                o.close();
            }catch (IOException e){}
        }

    }

}
