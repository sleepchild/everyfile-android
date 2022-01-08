package sleepchild.everyfile;

import android.app.*;
import android.os.*;
import android.content.*;
import java.util.*;
import java.io.*;
import android.widget.*;

public class IndexingService extends IntentService
{
	String dirp;
	MainActivity ma;
	List<File> fList = new ArrayList<>();
	List<String> exclude = new ArrayList<>();
	
	public IndexingService(){
		super("idx");
	}

	@Override
	protected void onHandleIntent(Intent p1)
	{
		ma = G.get().ma;
		dirp = Environment.getExternalStorageDirectory().getAbsolutePath()
			+"/";
		getExcludeList();
		preRun();
	}
	
	public void preRun(){
		List<File> xlist = Arrays.asList(new File(dirp).listFiles());
        listOrdered(xlist);
		//
		List<File> fl1 = new ArrayList<>();
		List<File> fl2 = new ArrayList<>();
        List<File> fl3 = new ArrayList<>();
		//*
        int l = xlist.size()/3;
        int l2 = l+l;
        
        int c = 1;
		for(File f : xlist){
            if(c<l){
                fl1.add(f);
            }else if(c<l2){
                fl2.add(f);
            }else{
                fl3.add(f);
            }
            //
            c++;
        }
        //*/
		ma.log("updating index...");
		new getFilesTask(fl1).exclude(exclude).execute();
		new getFilesTask(fl2).exclude(exclude).execute();
        new getFilesTask(fl3).exclude(exclude).execute();
	}
	
	/*
	public int i=1;
	public List<File> scanDir(String path){
		List<File> slist = new ArrayList<>();
		for(File xfl : new File(path).listFiles()){
			if(!isExcluded(xfl)){
				ma.log("updating.. "+i);
				i++;
				slist.add(xfl);
				if(xfl.isDirectory()){
					for(File cfl : scanDir(xfl.getAbsolutePath())){
						slist.add(cfl);
					}
				}
			}
		}
		return slist;
	}
	//*/
	
	public boolean isExcluded(File fl){
		for(String ex : exclude){
			if(ex.equals(fl.getAbsolutePath())){
				return true;
			}
		}
		return false;
	}
	
	public void after(){
		if(fList!=null || !fList.isEmpty()){
			ma.log( fList.size()+" items");
			ma.updateList(fList);
		}
		
	}
	
	public int taskNum = 0;
	public void addData(List<File> fdata){
		//*
		synchronized(fList){
			for(File afl : fdata){
				fList.add(afl);
			}
			//log(fList.size());
			taskNum++;
		  if(taskNum==3){
				after();
			}
		}	
		//*/
		
	}
	
    //todo: get from prefs
	private void getExcludeList(){
        for(File fl : G.get().getExcludedFilesList()){
            exclude.add(fl.getAbsolutePath());
        }
		//exclude.add(dirp+".aide");
		//exclude.add(dirp+"Android");
        //exclude.add(dirp+"AppProjects");
	}
    
    void listOrdered(List<File> list){
        Collections.sort(list, new Comparator<File>(){
                @Override
                public int compare(File p1, File p2)
                {
                    return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                }
        });
    }
	
	class getFilesTask extends AsyncTask<Void, Void, Boolean>
	{
		List<File> inlist;
		List<File> outList=new ArrayList<>();
		List<String> excl = new ArrayList<>();
		
		getFilesTask(List<File> list){
			inlist = list;
		}
		
		public getFilesTask exclude(List<String> eList){
			excl = eList;
			return this;
		}

		@Override
		protected Boolean doInBackground(Void[] p1)
		{
			for(File fx : inlist){
				if(!isExclude(fx)){
					outList.add(fx);
					if(fx.isDirectory()){
						scan(fx);
					}
				}
			}
			return outList!=null;
		}
		
		public void scan(File fd){
			for(File fl : fd.listFiles()){
				outList.add(fl);
				if(fl.isDirectory()){
					scan(fl);
				}
			}
		}
		
		public boolean isExclude(File fl){
			for(String ex : excl){
				if(ex.equals(fl.getAbsolutePath())){
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			if(result){
				addData(outList);
			}else{
				ma.log("err");
			}
		}

		
	}
	
	
}
