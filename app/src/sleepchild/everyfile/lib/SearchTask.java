package sleepchild.everyfile.lib;
import java.io.*;
import java.util.*;
import android.graphics.*;

public class SearchTask implements Runnable
{
    List<File> dlist = new ArrayList<>();
    List<File> results = new ArrayList<>();
    
    // todo: better search refinement using SearchParms
    SearchParams params;
    
    String query;
    SearchResultListener srl;
    
    /*
        todo: find a more eficient way to cancel a task
        @param mAbort
        
        A flag(sort of) to determine if this task should continue and send results;
        
        All methods must continually check if this changed to true and must return immediatley.
    */
    boolean mAbort = false;
    
    public SearchTask(String params, List<File> search_in, SearchResultListener l){
        this.dlist = search_in;
        this.query = params.toLowerCase();
        this.srl = l;
    }
    
    public synchronized void abort(){
        mAbort = true;
    }

    @Override
    public void run(){
        // todos: 
        if(query==null){
           // log error, quietly exit.
           return;
        }
        
        boolean ok = searchContainsAll(false);
        
        //searchContainsAny(false);
        
        if(!mAbort){
            if(srl!=null){
                srl.onSearchResult(results);
            }
        }
    }
    
    /*
        
    */
    private boolean searchContainsAll(boolean inorder){
        //
        String[] parts = query.split(" ");
        if(parts!=null){
            
            // 
            for (File fl : dlist){
                String fn = fl.getName().toLowerCase();
                String fp = fl.getAbsolutePath().toLowerCase();
                
                // track iterations of the inner loop
                int i = 0;
                
                if(mAbort){
                    return false;
                }
                
                for(String q : parts){
                    
                    if(mAbort){
                        return false;
                    }
                    
                    /*
                       here we simply add all the file that match the first q to the results
                     */
                    if(i==0){
                        if(fn.contains(q) || fp.contains(q)){
                            results.add(fl);
                        }
                    }
                   
                    /*
                      when this is true it means the file passed the above check and was added to the results;
                        and also that parts array has more than one element; i.e parts.length > 1 == true;
                      we now test the next 'q' parts;
                    */
                    else if(results.contains(fl)){
                        //
                        if(!fn.contains(q)){ // todo: we can do better here.
                            if(!fp.contains(q)){
                                /*  if we reach this point, then the filename AND filepath does not
                                    contain the nth(q) and is thus removed from the results;
                                */
                                results.remove(fl);
                                
                                /*
                                    these break statements do help with performance..
                                    we move on to the next file
                                */
                                break;
                            }
                        }
                    }else{
                        //  failed above tests
                        //  break and move on to next file
                        break;
                    }
                    i++;
                }
            }
            
        }else{
            // idealy this should never have to run,
            // but we gotta have a failsafe.
            for(File fl : dlist){
                if(mAbort){
                    return false;
                }
                String fn = fl.getName().toLowerCase();
                String fp = fl.getAbsolutePath().toLowerCase();
                
                if(mAbort){
                    return false;
                }
                
                if(fn.contains(query) || fp.contains(query)){
                    results.add(fl);
                }
            }
            return true;
        }
        //*/
        
        return true;
         
    }
    
    /*
    
    */
    private void searchContainsAny(boolean inorder){
        
        for( File fl : dlist){
            if(mAbort){
                return;
            }
            if(fl.getName().toLowerCase().contains(query)){
                results.add(fl);
            }
        }
    }
    
    
    /*
    
    */
    private void searchStarswith(){
        //
    }
    
    
    /*
    
    */
    private void searchEndswith(){
        //
    }
    
    
}
