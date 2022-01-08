package sleepchild.everyfile;
import android.widget.*;
import android.view.*;
import java.util.*;
import java.io.*;
import android.content.*;
//import android.view.View.*;
import android.net.*;

public class LAdapter extends BaseAdapter
{
	List<File> fileList = new ArrayList<>();
	Context ctx;
	LayoutInflater lInflator;
	
	public LAdapter(Context ctx){
		this.ctx = ctx;
		lInflator = LayoutInflater.from(ctx);
		//
	}
	
	public void update(List<File> newList){
		fileList = newList;
		notifyDataSetChanged();
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
		File fl = fileList.get(pos);
		view = lInflator.inflate(R.layout.list_item,null,false);
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
		view.setTag(fl);
		return view;
	}
    
}
