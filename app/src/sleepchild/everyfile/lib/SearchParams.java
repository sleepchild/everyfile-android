package sleepchild.everyfile.lib;

public class SearchParams
{
    public SearchParams(){}
    
    public SearchParams(String query, MATCH matchtype, LOOKUP lookuptype){
        this.query = query;
        this.match = matchtype;
        this.lookup = lookuptype;
    }
   
    public static final enum MATCH{
        CONTAINS_ALL,
        CONTAINS_ANY,
        STARTWITH,
        ENDSWITH,
        ;
    }
    
    public static final enum LOOKUP{
        FILE_NAME,
        FILE_PATH,
        BOTH,
        ;
    }
    
    public String query = null;
    
    private MATCH match = MATCH.CONTAINS_ALL;
    private LOOKUP lookup = LOOKUP.BOTH;
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return query;
    }
    
    public void setMatchType(MATCH match){
        this.match = match;
    }
    
    public MATCH getMatchType(){
        return match;
    }
    
    public void setLookupType(LOOKUP lookup){
        this.lookup = lookup;
    }
    
    public LOOKUP getLookupType(){
        return lookup;
    }
    
}
