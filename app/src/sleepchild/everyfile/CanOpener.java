package sleepchild.everyfile;
import java.io.*;
import android.content.*;
import android.net.*;

public class CanOpener
{
    public static void open(Context ctx, File fl){
        Intent i = new Intent(Intent.ACTION_VIEW);
        String p = fl.getAbsolutePath();
        
        
        // audio files
        if(ends(p, ".mp3",".wma",".wav",".acc")){
            i.setDataAndType(Uri.fromFile(fl), "audio/*");
        }
        // videos
        else if(ends(p,".mp4",".avi",".ogg",".3gp")){
            i.setDataAndType(Uri.fromFile(fl), "video/*");
        }
        // images
        else if(ends(p,".jpg",".png", ".jpeg",".bmp", ".gif",".webp")){
            i.setDataAndType(Uri.fromFile(fl), "image/*");
        }
        // common plain texts
        else if(ends(p,
          //
          ".txt",".html",".md",".css",".js",".less",".scss"
          ,".java", ".c", ".cpp",".h",".lua",".python"
          ,".xml",".svg"
          ,".md5"
        )){
            i.setDataAndType(Uri.fromFile(fl), "text/*");
        }
        
        // archives
        else if(ends(p,".zip",".gz",".tar",".jar")){
            i.setDataAndType(Uri.fromFile(fl), "application/*");
        }
        
        // everything else
        // this will show all available apps and let the user choose
        else{
            i.setDataAndType(Uri.fromFile(fl), "*/*");
        }
        
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ctx.startActivity(i);
    }
    
    
    /*
    /
    */
    private static boolean ends(String path, String... m){
        if(m!=null){
            for(String t : m){
                if(path.toLowerCase().endsWith(t.toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
