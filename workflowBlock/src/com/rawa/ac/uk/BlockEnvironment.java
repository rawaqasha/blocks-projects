package com.rawa.ac.uk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import com.connexience.api.StorageClient;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscFolder;

public class BlockEnvironment {
	
	public String source;
	public String OutputFileName;
	public Map<String,String> block;
	public String blueprint;
	public String BlockName;
	
	public Map getBlockTemplate(String ServiceTemplate, String BlockNodeTemplate) throws Exception
	{
		final Yaml yaml = new Yaml();
        Reader reader = null;
        
        reader = new FileReader(ServiceTemplate);
		Map<String, String> map = (Map<String, String>) yaml.load(reader);
		String st="node_templates";
        Object temp=map.get(st);; //get the node templates with all info 
        Map m= (Map) temp;
        Set s = m.keySet();       //get node templates as keys
        java.util.Iterator ir = s.iterator();
  	    while (ir.hasNext()) 
  	       {
  	    	 String key = (String) ir.next();
  	    	
  	    	 if ( key.equals(BlockNodeTemplate))
  	    		{ 
  	    		  Object value = m.get(key);
  	    		  map = (Map) value;
  	    		  break;
  	    		}
  	    		 
  	       }
		return map;
	}
	
	public Map get_value(Map<String,String> map,String key)
	{
		if(!map.containsKey(key))
			return null;
		Object temp=map.get(key);  
        Map m= (Map) temp;
        return m;
	}
	
	public String get_property(String serviceTemplate, String block, String property) throws Exception
	{
		String st="notblock";
		
		Map m=get_value(getBlockTemplate(serviceTemplate, block),"properties");
		if(m!=null)
		{	
		//System.out.println(block+"::::"+m);
		if(m.containsKey(property))
		{
			st= (String)m.get(property);
		    return st;
		}
		}
		return st;
	}
	public boolean IsBlock(String blueprint, String block) throws Exception
	{
		//System.out.println(get_property(blueprint, block, "service_type"));
		String st = get_property(blueprint, block, "service_type");
		//System.out.println(st);
		if(!(st.matches("block")))
			return false;
		return true;
	}
	
	public String output_location(String ServiceTemplate, String block) throws Exception
	{
		final Yaml yaml = new Yaml();
		System.out.println(ServiceTemplate+"  "+block);
	     Reader reader = null;
	     try {
	          reader = new FileReader(ServiceTemplate);
	          Map<String, String> map = (Map<String, String>) yaml.load(reader); //blueprint
	          boolean in=false,out=false;
	          String st="";
	          
	          Map Block=getBlockTemplate(ServiceTemplate, block);
	          //System.out.println(Block);
	          ArrayList Relations =(ArrayList)Block.get("relationships");
	          System.out.println(((Map)Relations.get(0)).get("target"));
	          String host=(String) ((Map)Relations.get(0)).get("target");
	          System.out.println(host);
	          Map nodes=get_value(map,"node_templates"); //get node templates for the blocks
	          
	          Set s = nodes.keySet();
	          Iterator ir = s.iterator();
	  	      while (ir.hasNext())
	  	      { //while
	  	    	String item= (String) ir.next(); 	  	       
	  	    	if (IsBlock(ServiceTemplate, item ) && (! item.matches(block)))
	  	          { //first if
	  	    	    System.out.println("block "+item);
	  	    		Map node=(Map)nodes.get(item);
	  	    		Relations =(ArrayList)node.get("relationships");
	  	    		//System.out.println(Relations);
	  	    		for (int i=0;i<Relations.size();i++)  	    			  	    			
	  	    			{
	  	    			  //System.out.println(Relations.get(i));
	  	    			  if(((String) ((Map)Relations.get(i)).get("type")).matches("cloudify.relationships.contained_in"))	  	    				 
	  	    				{
	  	    				   if(((String) ((Map)Relations.get(i)).get("target")).matches(host))	  	    				 	
	  	    					  { int j;
	  	    					   for( j=i+1;j<Relations.size();j++)	  	    					  
	  	    						if(((String) ((Map)Relations.get(j)).get("type")).matches("block_link"))
	  		  	    				   if(((String) ((Map)Relations.get(j)).get("target")).matches(block))
	  		  	    				   { in=true;   break; }	
	  	    					   if(j<Relations.size())
	  	    						   break;
	  	    					  }
	  	    				}
	  	    			else	  	    			
	  	    			   if(((String) ((Map)Relations.get(i)).get("type")).matches("block_link"))
	  	    			     if(((String) ((Map)Relations.get(i)).get("target")).matches(block))
	  	    					{
	  	    					  out=true; break;
	  	    					}  	
	  	    			}//for
	  	    		} //first if
	  	          
	  	      } //while
	  	      
	  	    if(in)
	  	      st= "in";
	  	    if(out)
	  	      st= "out";
	  	    if(in&&out)
	  	      st= "both";
	  	    return st;
	  	   } //try
	     catch (final FileNotFoundException fnfe) {
	        System.err.println("We had a problem reading the YAML from the file because we couldn't find the file." + fnfe);
	        } finally {
	           if (null != reader) {
	            try {
	                reader.close();
	            } catch (final IOException ioe) {
	                System.err.println("We got the following exception trying to clean up the reader: " + ioe);
	            }
	        }
	      
	    }
	     return null;
	}

  public String input_store(String ServiceTemplate, String block) throws Exception 
   {
	final Yaml yaml = new Yaml();
    Reader reader = null;
    try {
    	 Map<String,String> blockTemplate=getBlockTemplate(ServiceTemplate, block);
    	 ArrayList relation = (ArrayList) ((Object)blockTemplate.get("relationships"));
    	 for(int i=0; i<relation.size();i++)
	        {
    		 Map r= (Map)relation.get(i);
	         String blockType=((String)getBlockTemplate(ServiceTemplate,(String)r.get("target")).get("type"));
	       
	         if(((String)r.get("type")).matches("cloudify.relationships.depends_on")&&blockType.matches("WFblock"))
	           {
	            Map m=getBlockTemplate(ServiceTemplate,(String)r.get("target"));
	        
	            if(((String)get_value(blockTemplate,"properties").get("container_ID")).matches((String)get_value(m,"properties").get("container_ID")))
	           	   return "in";
	            else
	               return "out";
	           }
	         }
	
  	    } //try
     catch (final FileNotFoundException fnfe) 
        {
         System.err.println("We had a problem reading the YAML from the file because we couldn't find the file." + fnfe);
        } 
     finally
        {
         if (null != reader)
            {
             try {
                  reader.close();
                 }
             catch (final IOException ioe) {
                System.err.println("We got the following exception trying to clean up the reader: " + ioe);
              }
            }
        }
    return "null";
   }
	

  public String DownloadBlueprint(String blueprint) throws Exception
	{
	    //get eSc credentials
	    geteScCred();
	    File file=new File("eSc.txt");
	    BufferedReader br = new BufferedReader(new FileReader(file));
		String HOSTNAME=br.readLine();//"192.168.56.101";
		int PORT=8080;
		Boolean SECURE=false;
		String USERNAME=br.readLine();//"rawa_qasha@yahoo.com";
		String PASSWORD=br.readLine();//"123";
		File localFile=null;
		try {
			// Create a new storage client with username and password
			StorageClient client = new StorageClient(HOSTNAME,PORT,SECURE, USERNAME,PASSWORD);
			
			EscFolder homeFolder = client.homeFolder();
			EscFolder[] subFolders = client.listChildFolders(homeFolder.getId());
			
			String folder_ID=getFolderID(blueprint,subFolders);
					
			EscDocument[] subDocs = client.folderDocuments(folder_ID);
				
			//download the file from the server
			//BlockInputs.getFileID(blueprint, subDocs));
		   	EscDocument doc = client.getDocument(subDocs[0].getId());
		   	
		   	String OS=System.getProperty("os.name");
			String root=null;
			if(OS.substring(0, 3).matches("Win"))
				root="D:"+File.separator;
			else
				root=System.getProperty( "user.home" )+File.separator;
		   	
		   	File dir=new File(root+blueprint);
		   	if(!dir.exists())
		   	    dir.mkdir();
		   	localFile=new File(root+blueprint+File.separator+blueprint+".yaml");
		    
			client.download(doc, localFile);
		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return localFile.getPath();
	}
  
  public static String getFolderID(String fileName,EscFolder[] folds )
	{//System.out.println(docs.length);
		for( int i=0; i<folds.length;i++)
			{if(fileName.equalsIgnoreCase(folds[i].getName()))
			
				return folds[i].getId();
			}
		return null;
	}
  
  public void geteScCred() throws Exception
  {
	  String link = "https://raw.githubusercontent.com/rawaqasha/Data/master/eSc.txt";
      String            fileName = "eSc.txt";
      URL               url  = new URL( link );
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      Map< String, List< String >> header = http.getHeaderFields();
      while( isRedirected( header )) {
         link = header.get( "Location" ).get( 0 );
         url    = new URL( link );
         http   = (HttpURLConnection)url.openConnection();
         header = http.getHeaderFields();
      }
      InputStream  input  = http.getInputStream();
      byte[]       buffer = new byte[4096];
      int          n      = -1;
      OutputStream output = new FileOutputStream( new File( fileName ));
      while ((n = input.read(buffer)) != -1) {
         output.write( buffer, 0, n );
      }
      output.close();
  }
  private static boolean isRedirected( Map<String, List<String>> header ) {
      for( String hv : header.get( null )) {
         if(   hv.contains( " 301 " )
            || hv.contains( " 302 " )) return true;
      }
      return false;
   }
 }
