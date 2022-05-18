package sleepchild.view;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.os.*;
import android.graphics.*;
import sleepchild.everyfile.*;
import java.util.*;

// custom loading animation
public class LoaderIV extends ImageView
{
    Handler handle = new Handler();
    float sx=1.2f;
    
    boolean running=false;
    
    RectF bounds = new RectF(0,0,0,0);
    
    Paint gpaint, spaint;
    
    float cx=0;
    boolean lr=true;

    public LoaderIV(Context ctx){
        super(ctx);
        init();
    }

    public LoaderIV(Context ctx, AttributeSet a){
        super(ctx,a);
        init();
    }

    public LoaderIV(Context ctx, AttributeSet a, int d){
        super(ctx,a, d);
        init();
    }
    //
    void init(){
        
        gpaint = new Paint();
        gpaint.setColor(Color.parseColor("#ffffff"));
        gpaint.setStyle(Paint.Style.STROKE);
        gpaint.setStrokeWidth(5);
        
        spaint = new Paint();
        spaint.setColor(Color.parseColor("#50000000"));
        
    }

    public void start(){
        if(running){
            return;
        }
        running=true;
        handle.post(task);
        handle.post(task2);
        
    }

    public void stop(){
        handle.removeCallbacks(task);
        handle.removeCallbacks(task2);
        running = false;
    }
    
    //scale animation
    Runnable task2 = new Runnable(){
        public void run(){
            animate().scaleX(sx).scaleY(sx).setDuration(300).start();
            if(sx==1f){
                sx=1.2f;
            }else{
                sx=1f;
            }
            handle.postDelayed(task2,300);
        }
    };

    // magnifier glass animation
    Runnable task = new Runnable(){
        public void run(){
            if(lr){
                cx += 10;
            }else{
                cx -= 10;
            }

            if(cx>(bounds.width()-100)){
                lr = false;
            }else if(cx < 50){
                lr = true;
            }

            invalidate();
            
            handle.postDelayed(task,50);
        }
    };

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        float cy = bounds.centerY()-(bounds.centerY()/2)+20;
        
        //glass scope
        canvas.drawCircle(cx, cy,25, spaint);
        
        // magnifying glass
        canvas.drawCircle(cx, cy,25, gpaint);
        
        // handle
        canvas.drawLine(cx+25,cy+25,cx+50,cy+50, gpaint);
        
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        bounds = new RectF(0,0,w,h);
        
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    

}
