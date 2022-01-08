package sleepchild.everyfile;
import android.view.*;
import java.util.*;
import android.widget.*;
import java.io.*;
import android.view.ContextMenu.*;
import android.content.*;
import android.app.*;

public class SettingsAct extends BaseActivity
{
    public static final int requestCode = 827483;
    boolean sectionVisible=false;
    

    @Override
    public void init()
    {
        // TODO: Implement this method
        super.init();
        setContentView(R.layout.activity_settings);
    }
    
    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.settings_item_excludeitems:
                showSection(R.id.settings_section_excludes);
                initExcludeList();
                break;
            case R.id.settings_item_useownviewer:
                //showSection();
                break;
            case R.id.settings_item_about:
                // nothing
                break;
            default:
            //
            break;
        }
    }
    
    ListView excludeList;
    LAdapter excludeAdapter;
    private void initExcludeList(){
        if(excludeList==null){
            excludeAdapter = new LAdapter(ctx);
            excludeList = (ListView) vid(R.id.settings_list_excludeditems_list);
            excludeList.setAdapter(excludeAdapter);
        }
        //*
        excludeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View view, int pos, long p4)
                {
                    
                    File fl = (File) view.getTag();
                    mxint= fl;
                    /*
                    G.get().removeExcludedFile(fl);
                    updateExlist();
                    //*/
                    return false;
                }

            
        });
        //*/
        registerForContextMenu(excludeList);
        updateExlist();
    }
    
    void updateExlist(){
        if(excludeAdapter!=null){
            List<File> exc = G.get().getExcludedFilesList();
            excludeAdapter.update(exc);
        }
    }
    
    AddExcludeItemHandler aeih;
    public void addExcludeItem(View v){
        showSection(R.id.settings_section_excludes_chooser);
        //if(aeih==null){
            aeih = new AddExcludeItemHandler(this);
        //}
    }
    
    List<View> sectionStack = new ArrayList<>();
    
    public void showSection(int resid){
        //
        getActionBar().hide();
        View v = vid(resid);
        v.setVisibility(View.VISIBLE);
        sectionStack.add(0,v);
        sectionVisible=true;
    }
    
    public void hideVisibleSection(){
        if(!sectionStack.isEmpty()){
            View v = sectionStack.remove(0);
            v.setVisibility(View.GONE);
        }
        if(!sectionStack.isEmpty()){
            View v = sectionStack.get(0);
            initExcludeList();
            return;
        }
        sectionVisible=false;
        getActionBar().show();
    }

    File mxint = null;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if(v.equals(excludeList)){
            menu.add("remove").setActionView(v);
        }
        //menu.add("remove");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        String tag = item.getTitle().toString();
        View v = item.getActionView();
        
        switch(tag){
            case "edit":
                //
                break;
            case "remove":
                if(v.getId()==excludeList.getId()){
                    if(mxint!=null){
                        G.get().removeExcludedFile(mxint);
                        updateExlist();
                    }
                }
                mxint=null;//reset
                break;
        }
        return true;//super.onContextItemSelected(item);
    }
    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onBackPressed()
    {
        
        if(sectionVisible){
            if(aeih!=null && aeih.willHandleBack()){
                return;
            }
            hideVisibleSection();
        }else{
            super.onBackPressed();
        }
        
    }
    
}
