package sleepchild.everyfile;
import java.io.*;
import java.util.*;
import android.app.*;
import android.widget.GridLayout.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.text.*;
import sleepchild.everyfile.lib.*;
import java.util.concurrent.*;
import android.os.*;

public class SearchPanel implements 
    View.OnClickListener, 
    TextWatcher,
    SearchResultListener
{
    List<File> mList = new ArrayList<>();
    FileAdaptor adaptor;
    
    EditText etInput;
    TextView tvFs;
    
    ExecutorService worker;
    SearchTask stask;
    int ctid = 0;
    Handler handle = new Handler();
    
    View root;
    
    MainActivity act;
    
    public SearchPanel(MainActivity a, FileAdaptor adaptor){
        act = a;
        this.adaptor = adaptor;
        root = a.findView(R.id.searcbar);
        
        a.findViewById(R.id.searchpanel_cleartext_close).setOnClickListener(this);
        
        etInput = (EditText) a.findViewById(R.id.searchpanel_et_input);
        etInput.addTextChangedListener(this);
        
        tvFs = (TextView) a.findViewById(R.id.filet);
        
        //
        worker = Executors.newFixedThreadPool(1);
        
    }
    
    public void show(List<File> list){
        mList.clear();
        mList.addAll(list);
        //
        act.show(root);
        
        root.postDelayed(new Runnable(){
            public void run(){
                act.showKeyboard(etInput);
            }
        },50);
    }
    
    private void updateAdapter(List<File> flist){
        adaptor.update(flist);
        tvFs.setText(""+flist.size());
    }
    
    private void clearOrClose(){
        if(etInput.getText().toString().isEmpty()){
            // exit
            act.hide(root);
            //
        }else{
            etInput.setText("");
        }
    }
    
    

    @Override
    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4){}
    
    @Override
    public void onTextChanged(CharSequence txt, int p2, int p3, int p4)
    {
        String q = etInput.getText().toString();
        interupt();
        if(q.isEmpty()){
            updateAdapter(mList);
        }else{
            stask = new SearchTask(q, mList, this);
            worker.submit(stask);
        }
    }

    @Override
    public void onSearchResult(final List<File> result)
    {
        handle.postDelayed(new Runnable(){
            public void run(){
                updateAdapter(result);
            }
        },1);
    }

    @Override
    public void afterTextChanged(Editable p1){}
    
    @Override
    public void onClick(View v){
        
        int id = v.getId();
        switch(id){
            case R.id.searchpanel_cleartext_close:
                clearOrClose();
                break;
                
        }
    } 
    
    // stop the previous searches from running & sending their resuts
    private void interupt(){
        if(stask!=null){
            stask.abort();
            //worker.shutdownNow();  // this is bad.
            //worker = Executors.newSingleThreadExecutor();
        }
    }
    
    public boolean isOpen(){
        return root.getVisibility() == View.VISIBLE;
    }
    
    public void close(){
        act.hide(root);
        adaptor.update(mList);
    }
    
    public interface OnSearchPanelCloseListener{
        public void onSearchPanelClosed();
    }
}
