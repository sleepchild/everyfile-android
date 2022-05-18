package sleepchild.everyfile.lib;
import java.util.*;
import java.io.*;

public interface ScanProgressListener{
    public void onScanStarted();
    public void onScanResultsUpdated(List<File> list);
    public void onScanCompleted();
}
