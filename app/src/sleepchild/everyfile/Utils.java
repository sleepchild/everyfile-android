package sleepchild.everyfile;

import java.io.*;
import java.util.*;
import android.content.*;
import android.widget.*;
import android.view.*;

public class Utils
{
    public static void sort_bypath_09aZ(List<File> list){
        Collections.sort(list, new Comparator<File>(){
            @Override
            public int compare(File p1, File p2){
                return p1.getAbsolutePath().compareToIgnoreCase(p2.getAbsolutePath());
            }
        });
    }
    
    public static void sort_byname_09aZ(List<File> list){
        Collections.sort(list, new Comparator<File>(){
            @Override
            public int compare(File p1, File p2){
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
    }
    
    public static long getTime(){
        return new Date().getTime();
    }
    
    public static void toast(Context ctx, String msg){
        Toast.makeText(ctx,msg,500).show();
    }
    
    public static void show(View v){
        v.setVisibility(View.VISIBLE);
    }
    
    public static void hide(View v){
        v.setVisibility(View.GONE);
    }
    
    public static List<String> getExcludes(Context ctx){
        List<String> elist = new ArrayList<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("/sdcard/.sleepchild/everyfile/files/excludes.txt"));
            String line = "";
            while( (line = br.readLine()) != null){
                if(line!=null && !line.isEmpty()){
                    elist.add(line);
                }
            }
            br.close();
        }
        catch (FileNotFoundException e)
        {}catch(IOException ioe){}
        finally{
            if(br!=null){try{br.close();}catch(IOException ioe){}}
        }
        return elist;
    }
    
    public static List<File> copy(List<File> src){
        List<File> dest = new ArrayList<>();
        //Collections.copy(dest, src);
        
        return src;
    }
    
    
}
