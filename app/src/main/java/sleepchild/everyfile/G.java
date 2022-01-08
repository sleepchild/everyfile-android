package sleepchild.everyfile;
import java.util.*;
import java.io.*;
import android.os.*;
import android.content.*;


public class G{
	
	public MainActivity ma;
	
	public static volatile G deftInstance;
	
	public G(){}
	
	public static G get(){
		G instance = deftInstance;
		if(instance==null){
			synchronized(G.class){
				instance = G.deftInstance;
				if(instance==null){
					instance = G.deftInstance = new G();
				}
			}
		}
		return instance;
	}
    
    String excludesFilePath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/.sleepchild/everyting/excludes.txt";
    private Set<File> excludeFiles = new HashSet<>();
    public void addToExcludeFiles(File fl){
        excludeFiles.add(fl);
        
    }
    
    public void removeExcludedFile(File fl){
        excludeFiles.remove(fl);
        saveExludes();
    }
    
    public List<File> getExcludedFilesList(){
        List<File> o = new ArrayList<>();
        for(File f : getExcludedFiles()){
            o.add(f);
        }
        return o;
    }
    
    public Set<File> getExcludedFiles(){
        if(excludeFiles.isEmpty()){
            try{
                
                BufferedReader br = new BufferedReader(new FileReader(excludesFilePath));
                String s = "";
                while( (s = br.readLine()) != null){
                   excludeFiles.add(new File(s)); 
                }
                br.close();
            }catch(IOException e){}
        }
        return excludeFiles;
    }
    
    public void saveExludes(){
        
        try
        {
            File ef = new File(excludesFilePath);
            //if(!ef.exists()){
                ef.getParentFile().mkdirs();
            //}

            String data = "";
            for(File fl : excludeFiles){
                data += fl.getAbsolutePath() + "\n";
            }
            
            FileOutputStream out = new FileOutputStream(ef);
            out.write(data.getBytes());
            out.flush();
            out.close();
            
        }
        catch (FileNotFoundException e)
        {}catch (IOException e){}
    }
    
}
