package sleepchild.everyfile.lib;
import java.util.concurrent.*;
import java.util.*;
import sleepchild.everyfile.*;
import java.io.*;

public class ScanTask implements Runnable
{
    ExecutorService worker;
    
    int MAX_THREAD_COUNT = 4;
    
    boolean mabort = false;
    
    long tid;
    
    String mPath;
    
    List<SubTask> mTasks = new ArrayList<>();
    
    List<String> excludes = new ArrayList<>();
    
    
    public ScanTask(String path, List<String> excludes){
        tid = Utils.getTime();
        mPath = path;
        this.excludes = excludes;
        worker = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
        //
    }
    
    public synchronized void setAbort(){
        try{
            worker.shutdownNow();
        }catch(Exception e){}
        mabort = true;
    }
    
    public boolean abort(){
        return mabort;
    }

    @Override
    public void run(){
        DataLib.get().setScanning(true);
        DataLib.get().notifyScanStarted(this);
        //
        List<File> mres = new ArrayList<>();
        
        File dir = new File(mPath);
        for(File fl : dir.listFiles()){
            boolean skip = false;
            for(String ex : excludes){
                if( fl.getAbsolutePath().equalsIgnoreCase(ex)){
                    skip = true;
                    break;
                }
            }
            
            if(skip){
                skip = false;
                continue;
            }
            
            mres.add(fl);
            if(fl.isDirectory()){
                addTask( new SubTask(this, fl, excludes) );
            }
        }
        
        addResults(mres);
        
        if(!abort()){
            DataLib.get().notifyScanResultsUpdated();
        } 
        
        if(completed() && !abort()){
            DataLib.get().notifyScanCompleted(this);
        }
        
    }
    
    public synchronized void addTask(SubTask tsk){
        if(abort()){
            return;
        }
        synchronized(mTasks){
            mTasks.add(tsk);
            mTasks.notifyAll();
        }
        
        worker.submit(tsk);
    }
    
    public synchronized boolean completed(){
        return mTasks.isEmpty();
    }
    
    public synchronized void removeSub(SubTask stk){
        synchronized(mTasks){
            mTasks.remove(stk);
            //mTasks.notifyAll();//
        }
    }
    
    public synchronized void addResults(List<File> flist){
        if(abort()){
            return;
        }
        
        //todo: merge the following to a single call
        // e.g DataLib.get().addResultsAndNotify(flist)
        DataLib.get().addResults(flist);
        DataLib.get().notifyScanResultsUpdated();

        if(completed() && !abort()){
            DataLib.get().setScanning(false);
            DataLib.get().notifyScanCompleted(this);
        }
        
    }
    
    private class SubTask implements Runnable{
        File sfl;
        ScanTask tsk;
        List<File> sres = new ArrayList<>();
        List<String> excl;
        
        public SubTask(ScanTask tsk, File fl, List<String> ex){
            this.tsk = tsk;
            this.sfl = fl;
            this.excl = ex;
        }

        @Override
        public void run(){
             scandir(sfl);
             tsk.removeSub(this);
             tsk.addResults(sres);
        }
        
        void scandir(File dirp){
            boolean skip = false;
            if(dirp.isDirectory()){
                for(File fl : dirp.listFiles()){
                    for(String xc : excl){
                        if(fl.getAbsolutePath().equalsIgnoreCase(xc)){
                            skip = true;
                            break;
                        }
                    }
                    if(skip){
                        skip = false;
                        continue;
                    }
                    sres.add(fl);
                   
                    if(fl.isDirectory()){
                        //scandir(fl);
                        tsk.addTask(new SubTask(tsk, fl, excl));
                    }
                }
            }
        }
 
    }
    
    
}
