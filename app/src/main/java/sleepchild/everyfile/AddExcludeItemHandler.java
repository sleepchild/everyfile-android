package sleepchild.everyfile;
import android.content.Context;
import android.widget.*;
import android.view.*;
import java.util.*;
import java.io.*;
import android.graphics.*;
import android.os.*;

public class AddExcludeItemHandler
{
    SettingsAct ctx;
    ListView pickerView;
    PAdapter pAdapter;
    Button done;
    boolean isSelecting=false;
    boolean isVisible=false;
    Set<PickableFile> pickedList = new HashSet<>();
    List<PickableFile> backstack = new ArrayList<>();
    
    String dirp = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    
    AddExcludeItemHandler(SettingsAct ctx){
        this.ctx = ctx;
        this.isVisible=true;
        pickerView =(ListView) ctx.vid(R.id.settings_list_excludeditems_choose);
        pAdapter = new PAdapter(ctx);
        pickerView.setAdapter(pAdapter);
        pickerView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> p1, View view, int p3, long p4)
                {
                    PickableFile fl = (PickableFile) view.getTag();
                    if(fl.isDirectory()){
                        if(isSelecting){
                            togglePicked(view);
                        }else{
                            PickableFile p = new PickableFile(fl.getParent());
                            backstack.add(0,p);
                            pAdapter.update(fl.listFilez());
                        }
                    }else{//is file
                        togglePicked(view);
                    }
                }
        });
        pickerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View view, int p3, long p4)
                {
                    //PickableFile fl = (PickableFile) view.getTag();
                    togglePicked(view);
                    return true;
                }
        });
        done = (Button) ctx.vid(R.id.activity_settings_addexcludeitem_done);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //
                for(PickableFile fl : pickedList){
                    G.get().addToExcludeFiles(new File(fl.getAbsolutePath()));
                }
                G.get().saveExludes();
                close();
            }  
        });
        
        pAdapter.update(new PickableFile(dirp).listFilez());
    }
    
    private void togglePicked(View view){
        PickableFile pfl = (PickableFile) view.getTag();
        if(pfl.isPicked){
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            pickedList.remove(pfl);
        }else{
            view.setBackgroundColor(Color.parseColor("#50345345"));
            pickedList.add(pfl);
        }
        pfl.isPicked=!pfl.isPicked;
        
        if(pickedList.isEmpty()){
            clearSelection();
        }else{
            isSelecting=true;
        }
    }
    
    public void close(){
        clearSelection();
        isVisible=false;
        isSelecting=false;
        ctx.hideVisibleSection();
        backstack.clear();
    }
    
    public void clearSelection(){
        isSelecting=false;
        pickedList.clear();
        pAdapter.clearSelected();
        
    }
    
    public boolean willHandleBack(){
        if(isSelecting){
            clearSelection();
            return true;
        }else if(!backstack.isEmpty()){
            pAdapter.update(backstack.remove(0).listFilez());
            return true;
        }
        return false;
    }
    
    class PickableFile extends File{
        public boolean isPicked=false;
        String path;
        PickableFile(String path){
            super(path);
            this.path = path;
        }

        
        public List<PickableFile> listFilez()
        {
            List<PickableFile> rflies = new ArrayList<>();
            for(File fl : listFiles()){
                PickableFile pfl = new PickableFile(fl.getAbsolutePath());
                rflies.add(pfl);
            }
            return rflies;
        }
        
        
    }
    
    class PAdapter extends BaseAdapter{
        List<PickableFile> fileList = new ArrayList<>();
        LayoutInflater inf;
        
        PAdapter(Context ctx){
            inf = LayoutInflater.from(ctx);
        }

        
        public void update(List<PickableFile> newList){
            fileList = newList;
            listOrdered(fileList);
            notifyDataSetChanged();
        }
        
        void listOrdered(List<PickableFile> list){
            Collections.sort(list, new Comparator<File>(){
                    @Override
                    public int compare(File p1, File p2)
                    {
                        return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
                    }
                });
        }
        
        public void clearSelected(){
            for(PickableFile fl : fileList){
                fl.isPicked=false;
            }
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount()
        {
            // TODO: Implement this method
            return fileList.size();
        }

        @Override
        public Object getItem(int p1)
        {
            return fileList.get(p1);
        }

        @Override
        public long getItemId(int p1)
        {
            return p1;
        }
        

        @Override
        public View getView(int pos, View view, ViewGroup p3)
        {
            PickableFile fl = fileList.get(pos);
            view = inf.inflate(R.layout.list_item,null,false);
            TextView name = (TextView) view.findViewById(R.id.item_name);
            TextView path = (TextView) view.findViewById(R.id.item_path);
            name.setText(fl.getName());
            path.setText(fl.getAbsolutePath());
            ImageView ivey = (ImageView) view.findViewById(R.id.list_itemImageView);
            if(fl.isDirectory()){
                ivey.setImageResource(R.drawable.ic_folder);
            }else{
                ivey.setImageResource(R.drawable.ic_file);
            }
            if(fl.isPicked){
                view.setBackgroundColor(Color.parseColor("#50345345"));
            }
            view.setTag(fl);
            return view;
        }
        
    }
    
    //LAdapter pAdapter = new LAdapter(){};
}
