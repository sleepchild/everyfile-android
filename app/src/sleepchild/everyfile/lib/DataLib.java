package sleepchild.everyfile.lib;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import sleepchild.everyfile.*;

public class DataLib
{
    private static volatile DataLib deft;
    
    private ExecutorService worker;
    
    private Set<ScanProgressListener> scanCallbacks = new HashSet<>();
    
    private List<File> mResults;
    
    ScanTask scanner;
    
    private volatile boolean mScanning = false;
    
    private DataLib(){
        //
        worker = Executors.newFixedThreadPool(2);
        mResults = Collections.synchronizedList(new LinkedList<>());
        scanCallbacks = new HashSet<>();
    }
    
    public static DataLib get(){
        DataLib inst = deft;
        if(inst==null){
            synchronized(DataLib.class){
                inst = DataLib.deft;
                if(inst==null){
                    inst = DataLib.deft = new DataLib();
                }
            }
        }
        return inst;
    }
    
    public void runTask(Runnable task){
        worker.submit(task);
    }
    
    public boolean isScanning(){
        return mScanning;
    }
    
    public synchronized void setScanning(boolean newValue){
        mScanning = newValue;
    }
    
    public void addScanResultListener(ScanProgressListener listener){
        scanCallbacks.add(listener);
    }
    
    public void removeScanResultListener(ScanProgressListener listener){
        scanCallbacks.remove(listener);
    }
    
    /*
       Reading 'mResults' while it is being populated can cause errors;
       
       we overcome this by making a copy of the current contents of mResults
       and then returning that copy.
       
       Important note: 
           Once this method returns mResults may continue to change
           meaning that xResult may NOT have the same contents as mResults when compared.
         
    //*/
    public synchronized List<File> getResults(){
        List<File> xResults = new ArrayList<>();
        //
        //*
        synchronized(mResults){
            xResults.clear();
            xResults.addAll(mResults);
            //Collections.copy(xResults, mResults);// this fails if mResults is being modified/populated.
            mResults.notifyAll();
        }
        //*/
        return xResults;
    }
    
    // whoever needs the results must handle sorting themselves;
    // peferably in a bg thread
    public boolean sorted = false;
    public void sortResults(){
        sorted = true;
        Utils.sort_bypath_09aZ(mResults);
    }
    
    public void scan(String directory, List<String> excludes){
        if(scanner!=null){
            scanner.setAbort();
        }
        
        sorted = false;
        
        synchronized(mResults){
            mResults.clear();
            mResults.notifyAll();
        }
        
        scanner = new ScanTask(directory, excludes);
        
        worker.submit(scanner);
    }
    
    public synchronized void addResults(List<File> more){
        synchronized(mResults){
            mResults.addAll(more);
            ///mResults.notifyAll();
        }
       // lock.notifyAll();
    }
    
    public void notifyScanStarted(ScanTask task){
        worker.submit(new Runnable(){
            public void run(){
                for(ScanProgressListener l : DataLib.get().scanCallbacks){
                    l.onScanStarted();
                }
            }
        });
    }
    
    public void notifyScanCompleted(ScanTask task){
        worker.submit(new Runnable(){
            public void run(){
                for(ScanProgressListener l : scanCallbacks){
                    l.onScanCompleted();
                }
            }
        });
    }
    
    public synchronized void notifyScanResultsUpdated(){
        for(ScanProgressListener l : scanCallbacks){
            l.onScanResultsUpdated(mResults);
        }
    }
    
    
    
    
}
