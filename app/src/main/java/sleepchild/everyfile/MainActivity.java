package sleepchild.everyfile;

import android.widget.TextView;
import android.widget.ListView;
import java.util.List;
import java.util.ArrayList;
import android.widget.ProgressBar;
import java.io.File;
import android.widget.AdapterView;
import android.view.View;
import android.os.Handler;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.text.*;
import java.util.concurrent.*;
import java.util.*;

public class MainActivity extends BaseActivity{

	TextView tvLog, tvlog2;
	EditText searchbar;
	ListView listviewAll, listviewSearch;
    RelativeLayout searchContainer;
	LAdapter adapterAll;
	List<File> fileList = new ArrayList<>();
	List<File> searchList = new ArrayList<>();
	//ProgressBar progress;
    //Timer tik = new Timer();
    Handler h = new Handler();
    ExecutorService worker = Executors.newSingleThreadExecutor();
    
    @Override
    public void init(){
		getPermissions();
	}
	
	public void initV(){
		G.get().ma = this;
        setContentView(R.layout.activity_main);
        //
        tvLog = (TextView) findViewById(R.id.tvlog);
       // progress = (ProgressBar) findViewById(R.id.activity_mainProgressBar);
		//
        searchContainer = (RelativeLayout) findViewById(R.id.activity_main_searchbar);
		searchbar = (EditText) findViewById(R.id.activity_main_searchinput);
		searchbar.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
                {
                    // TODO: Implement this method
                }

                @Override
                public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
                {
                    String text = searchbar.getText().toString();
                    if(!text.isEmpty()){
                        getSearchResults(text);
                    }
                }

                @Override
                public void afterTextChanged(Editable p1)
                {
                    // TODO: Implement this method
                }
        });
		adapterAll = new LAdapter(this);
		listviewAll = (ListView) findViewById(R.id.list_all);
		listviewAll.setAdapter(adapterAll);
        listviewAll.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p1, View view, int p3, long p4)
            {
                File f = (File) view.getTag();
                if(!f.isDirectory()){
                    openFile(f);
                }
            }
        });
        startIndexingService();
        //
    }
    
    void getSearchResults(final String text){
        //
        worker.submit(new Runnable(){
            public void run(){
                final List<File> result = new ArrayList<>();
                Set<File> hash = new HashSet<>();
                
                String[] p = text.split(" ");
                for(File fl : fileList){
                    String fn = fl.getName().toLowerCase();
                    int irt=0;// track which iteration we're at
                    
                    // this loop begins a test to check if this file contains ALL the words submitted
                    // if it does, it will be added to the final results;
                    // 
                    for(String t : p){
                        if(irt==0){
                            // for the first iteration, we simply add the file if contains the first param
                            if(fn.contains(t)){
                                hash.add(fl);
                            }
                        }else if(irt>0){
                            if(hash.contains(fl)){
                                // file passed a previous iteration so we test it some more.
                                if(!fn.contains(t)){
                                    // we reach this point because this file (fl) does not contain this word (t) and hence fails the test.
                                    // we remove/exclude it from further testing and consequently the final result
                                    hash.remove(fl);
                                    // move on to next file.
                                    break;
                                }
                            }else{
                                // at this point, this file failed a previous match so we skip further checks
                                // and move on to the next file by breaking out of this inner loop
                                break;
                            }
                        }
                        irt++;
                    }
                }
                
                // set to list conversion
                // find a better way?
                for(File fl : hash){
                    result.add(fl);
                }
                
                h.postDelayed(new Runnable(){
                    public void run(){
                        adapterAll.update(result);
                        log(result.size()+" results");
                    }
                },1);
            }
        });
    }
    
	public void updateList(final List<File> list){
		h.postDelayed(new Runnable(){
			@Override
			public void run()
			{
                //long diff = tik.stop();
                adapterAll.update(list);
				fileList = list;
				log(fileList.size()+" items ");//, in "+diff+"ms (aprox)");
				//progress.setVisibility(View.GONE);
			}
		}, 0);
	}
	
	
	String msg;
	public void log(Object text){
		msg = text.toString();
		h.postDelayed(new Runnable(){
			@Override
			public void run()
			{
				tvLog.setText(msg);
			}
		}, 0);
	}
	
	public void log(int num){
		log(""+num);
	}
	
	public void po(Intent i){
		//
		log(i);
		startActivity(i);
	}
	
	public void openFileHandle(File fl){
		try{
		/*
		Intent myIntent = new Intent(Intent.ACTION_VIEW);
		myIntent.setDataAndType(Uri.fromFile(fl));
		Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
		startActivity(j);
		//*/
		//open_file(fl);
		openFile(fl);
		}catch(Exception e){
			toast(e.getMessage());
		}
	}
	
	public void open_file(File file) {
		try{
		//File path = new File(getFilesDir(), "dl");
		//File file = new File(filename);

		// Get URI and MIME type of file
		//Uri uri = FileProvider.getUriForFile(this, App.PACKAGE_NAME + ".fileprovider", file);
		String mime = "*/*";//getContentResolver().getType(uri);

		// Open file with user selected app
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), mime);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(intent);
		}catch(Exception e){
			toast(e.getMessage());
		}
	}
	
	public void openFile(File url) {

		Uri uri = Uri.fromFile(url);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
			// Word document
			intent.setDataAndType(uri, "application/msword");
		} else if (url.toString().contains(".pdf")) {
			// PDF file
			intent.setDataAndType(uri, "application/pdf");
		} else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
			// Powerpoint file
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		} else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
			// Excel file
			intent.setDataAndType(uri, "application/vnd.ms-excel");
		} else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
			// WAV audio file
			intent.setDataAndType(uri, "application/x-wav");
		} else if (url.toString().contains(".rtf")) {
			// RTF file
			intent.setDataAndType(uri, "application/rtf");
		} else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
			// WAV audio file
			intent.setDataAndType(uri, "audio/x-wav");
		} else if (url.toString().contains(".gif")) {
			// GIF file
			intent.setDataAndType(uri, "image/gif");
		} else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
			// JPG file
			intent.setDataAndType(uri, "image/jpeg");
		} else if (url.toString().contains(".txt")) {
			// Text file
			intent.setDataAndType(uri, "text/plain");
		} else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
			// Video files
			intent.setDataAndType(uri, "video/*");
		} else {
			//if you want you can also define the intent type for any other file
			//additionally use else clause below, to manage other unknown extensions
			//in this case, Android will show all applications installed on the device
			//so you can choose which application to use
			intent.setDataAndType(uri, "*/*");
		}
        
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);

	}
	
	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf(".") + 1);
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();

		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
        menu.add("search").setIcon(R.drawable.ic_search_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("filter");//.setIcon();
		menu.add("settings");
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String tag = item.getTitle().toString();
        switch(tag){
            case "search":
                //startIndexingService();
                showSearcher();
                break;
            case "filter":
                //
                break;
            case "settings":
                Intent si = new Intent(ctx, SettingsAct.class);
                startActivityForResult(si, SettingsAct.requestCode);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    boolean isSearchBarVisible=false;
    private void showSearcher(){
        getActionBar().hide();
        searchContainer.setVisibility(View.VISIBLE);
        //listviewSearch.setVisibility(View.VISIBLE);
        isSearchBarVisible=true;
        searchbar.requestFocus();
        showKeyboard(searchbar);
       // adapterSearch.update(fileList);
    }
    
    void hideSearchBar(){
        getActionBar().show();
        searchContainer.setVisibility(View.GONE);
        //listviewSearch.setVisibility(View.GONE);
        
        isSearchBarVisible=false;
        searchbar.setText("");
        searchList.clear();
        adapterAll.update(fileList);
        log(fileList.size()+ " items");
    }
    
	private void startIndexingService(){
        //tik.start();
       // progress.setVisibility(View.VISIBLE);
		Intent idxIntent = new Intent(MainActivity.this, IndexingService.class);
		idxIntent.setAction("update");
		startService(idxIntent);
	}
	
	public void toast (String text){
		Toast.makeText(ctx,text,300).show();
	}
	
	public void getPermissions(){
		int grant = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(grant != PackageManager.PERMISSION_GRANTED){
			String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
			requestPermissions(perms,523);
		}else{
			initV();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		int grant = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(grant == PackageManager.PERMISSION_GRANTED){
			initV();
		}else{
			toast("permission required!");
            finish();
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO: Implement this method
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SettingsAct.requestCode){
            startIndexingService();
        }
    }
    

    @Override
    public void onBackPressed()
    {
        if(isSearchBarVisible){
            hideSearchBar();
        }else{
            super.onBackPressed();
        }
        
    }
    //
}
