package sleepchild.everyfile;

import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.io.*;
import sleepchild.view.*;
import sleepchild.everyfile.lib.*;

public class MainActivity extends BaseActivity implements 
  ScanProgressListener,
  View.OnClickListener,
  SearchPanel.OnSearchPanelCloseListener,
  FileAdaptor.OnFileItemClickListener
{
    ListView list4;
    FileAdaptor adaptor;
    LoaderIV loader;
    View lroot;
    TextView tvItemSize;
    List<String> excludes = new ArrayList<>();
    
    Handler handle;
    
    SearchPanel searchpanel;
    
    SearchService server;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
        
        //DataLib.get().addScanResultListener(this);
       // startScan();
       
    }
    
    void init(){
        //
        handle = new Handler(Looper.getMainLooper());
        
        lroot = findView(R.id.loader_root);
        loader = findView(R.id.loader);
        
        adaptor = new FileAdaptor(this);
        adaptor.setOnListItemClickListener(this);
        
        list4 = findView(R.id.list4);
        list4.setDividerHeight(0);
        list4.setDivider(null);
        list4.setAdapter(adaptor);
        
        tvItemSize = findView(R.id.filet);
        
        setClickable(R.id.btn_more);
        setClickable(R.id.btn_refresh);
        setClickable(R.id.btn_start_search);
        
        searchpanel = new SearchPanel(this, adaptor);
        //
    }
    
    
    void setClickable(int resid){
        findViewById(resid).setOnClickListener(this);
    }
    
    private void startScan(){
        getExcludes();
        DataLib.get().scan("/sdcard/", excludes);
    }
    
    void getExcludes(){
        excludes.clear();
        excludes = Utils.getExcludes(getApplicationContext());
    }
    
    void updateList(){
        List<File> flist = Utils.copy(DataLib.get().getResults());
        adaptor.update(flist);
    }
    
    Runnable updateListTask = new Runnable(){
        public void run(){
            if(DataLib.get().isScanning()){
                updateList();
                handle.postDelayed(updateListTask, 500); 
            }else{
                handle.removeCallbacks(updateListTask);
                DataLib.get().runTask(sortlist);
            }
        }
    };
    
    void startUPD(){
        handle.postDelayed(updateListTask,100);
    }
    
    void stopUPD(){
        handle.removeCallbacks(updateListTask);
    }

    @Override
    public void onFileItemClicked(File fl)
    {
        if(fl.isFile()){
            CanOpener.open(ctx, fl);
        }else{
            //
        }
    }

    @Override
    public void onListItemLongClick()
    {
        // TODO: Implement this method
    }

    @Override
    public void onListItemMoreClicked()
    {
        // TODO: Implement this method
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch(id){
            case R.id.btn_more:
                //
                break;
            case R.id.btn_refresh:
                startScan();
                break;
            case R.id.btn_start_search:
                searchpanel.show(DataLib.get().getResults());
                break;
        }
    }

    @Override
    public void onSearchPanelClosed()
    {
        // TODO: Implement this method
    }

    @Override
    public void onScanStarted(){ 
        runOnUiThread(new Runnable(){
           public void run(){
               Utils.show(lroot);
               loader.start();
               //
               toast("Scan Started");
               startUPD();
           }
        });
    }

    @Override
    public void onScanCompleted(){
        //
        runOnUiThread(new Runnable(){
            public void run(){
                stopUPD();
                DataLib.get().runTask(sortlist);
                //
                toast("Scan Completed");
                //
                Utils.hide(lroot);
                loader.stop();
                //
            }
        });
    }
    
    /*
      Important: run this in a bg thread;
      
      This task sorts the results from DataLib::scan and then updates the adaptor
      *
      The results from DataLib::scan are never sorted automatically due to peformance reasons;
      !this can be visibly aparent in larger lists;
      *
      thus we must manually call DataLib::sortResults; we only need to do this once and recomended to do so after ::OnScanCompleted
      
    */
    Runnable sortlist = new Runnable(){
        public void run(){
            if(!DataLib.get().sorted){
                DataLib.get().sortResults(); 
            }
            
            final List<File> flist = Utils.copy(DataLib.get().getResults());
            
            runOnUiThread(new Runnable(){
                public void run(){
                    if(!searchpanel.isOpen()){
                        adaptor.update(flist);
                        tvItemSize.setText(""+flist.size());
                    }
                }
            });
            
        }
    };
    
    @Override
    public void onScanResultsUpdated(final List<File> files){
        runOnUiThread(new Runnable(){
            public void run(){
                tvItemSize.setText(""+files.size());
            }
        }); 
    }
    
    public void none(View v){}

    @Override
    protected void onPause(){
        //
        Utils.hide(lroot);
        loader.stop();
        DataLib.get().removeScanResultListener(this);
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        DataLib.get().addScanResultListener(this);
        //
        startUPD();
    }

    @Override
    public void onBackPressed(){
        if(searchpanel.isOpen()){
            searchpanel.close();
        }else{
            super.onBackPressed();
        }
        
    }
    
    
}
