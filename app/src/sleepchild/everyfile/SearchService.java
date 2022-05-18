package sleepchild.everyfile;
import android.app.*;
import android.os.*;
import android.content.*;
import sleepchild.everyfile.lib.*;

public class SearchService extends Service
{
    public static final String CMD_START = "sleepchills.everyfile.service.s3archservic3.cmd.start" ;

    @Override
    public IBinder onBind(Intent p1){
        return null;
    }

    public static void start(Context ctx){
        Intent i = new Intent(ctx, SearchService.class);
        i.setAction(CMD_START);
        ctx.startService(i);
    }
    
    @Override
    public void onCreate(){
        super.onCreate();
        //M.get().server = this;
        //Utils.toast(this, "catfish");
        DataLib.get().scan("/sdcard/", Utils.getExcludes(getApplicationContext()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // TODO: Implement this method
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        //M.get().server = null;
        super.onDestroy();
    }
    
    
    
    
    
    
}
