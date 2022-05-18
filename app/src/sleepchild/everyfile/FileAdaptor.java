package sleepchild.everyfile;
import android.widget.*;
import android.view.*;
import java.io.*;
import java.util.*;

public class FileAdaptor extends BaseAdapter
{
    MainActivity act;
    LayoutInflater inflator;
    
    List<File> fileslist = new ArrayList<>();
    
    public FileAdaptor(MainActivity ctx){
        act = ctx;
        inflator = ctx.getLayoutInflater();
        
    }
    
    public void update(List<File> list){
//        for(File fl : list){
//            if(fl!=null){
//               // fileslist.add(fl);
//            }else{
//                // we need to report this!
//            }
//        }
        fileslist = list;
        //Utils.sort_bypath_09aZ(fileslist);
        //
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return fileslist.size();
    }

    @Override
    public Object getItem(int pos){
        return fileslist.get(pos);
    }

    @Override
    public long getItemId(int p1){
        return p1;
    }

    @Override
    public View getView(int pos, View v, ViewGroup p3){
        
        final File fl = fileslist.get(pos);
        
        v = inflator.inflate(R.layout.file_adaptor, null, false);
        v.setOnClickListener(new View.OnClickListener(){
            public void onClick(View a){
                if(ocl!=null){
                    ocl.onFileItemClicked(fl);
                }
            }
        });
        
        TextView title = fv(v,R.id.file_adaptor_title);
        title.setText(fl.getName());
        
        TextView path = fv(v, R.id.file_adaptor_path);
        path.setText(fl.getAbsolutePath());
        
        ImageView icon = fv(v, R.id.file_adaptor_icon);
        if(fl.isDirectory()){
            icon.setBackgroundResource(R.drawable.ic_folder);
        }
        
        return v;
    }
    
    <T extends View> T fv(View root, int resid){
        return (T) root.findViewById(resid);
    }
    
    private OnFileItemClickListener ocl;
    
    public void setOnListItemClickListener(OnFileItemClickListener l){
        ocl = l;
    }
    
    public interface OnFileItemClickListener{
        public void onFileItemClicked(File fl);
        public void onListItemLongClick();
        public void onListItemMoreClicked();
    }
    
    
}
