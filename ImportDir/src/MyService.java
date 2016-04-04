import java.awt.List;
import java.io.File;




import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import com.connexience.server.util.WildcardUtils;
import com.rawa.ac.uk.BlockEnvironment;
import com.rawa.ac.uk.BlockInputs;
import com.rawa.ac.uk.BlockOutputs;
import com.rawa.ac.uk.workflowBlock;


public class MyService implements workflowBlock{
	 /**
     * This field refers to property 'Copy Input' defined in service.xml
     */
    //private final static String Prop_COPY_INPUT = "Copy Input";

    /**
     * This field refers to input port 'input-1' defined in service.xml
     */
    //private final static String Input_INPUT_1 = "input-1";
    
    /**
     * This field refers to output port 'output-1' defined in service.xml
     */
    private final static String Output_OUTPUT_1 = "imported-files";


    /**
     * This method is called when block execution is first started. It should be
     * used to setup any data structures that are used throughout the execution
     * lifetime of the block.
     */
    public void preExecute(BlockEnvironment env) throws Exception
    {
        
    }

    /**
     * This code is used to perform the actual block operation. It may be called
     * multiple times if data is being streamed through the block. It is, however, 
     * guaranteed to be called at least once and always after the preExecute
     * method and always before the postExecute method;
     */
    //
   	
    
    public void execute(BlockEnvironment env, BlockInputs inputs, BlockOutputs outputs) throws Exception
    {
    	String OS=System.getProperty("os.name");
    	String root = null;
		if(OS.substring(0, 3).matches("Win"))
			root = "D:" + File.separator ;
		else
			root = System.getProperty( "user.home" ) + File.separator ;
    	
		File folder = new File(env.source);
		System.out.println("Folder Path is "+folder.getPath());
    	String recursive = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName, "Recursive");
        String wildcard = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"Wildcard");
        String importToSubdir = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"ImportToSubDirectory");
        String subDirName = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"SubDirectoryName");
        String keepDirStructure = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"KeepDirStructure");
        String orderByName = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"OrderByName");
        String orderByTimestamp = env.get_property(root+env.blueprint+File.separator+env.blueprint+".yaml", env.BlockName,"OrderByTimestamp");
        
        ArrayList<String> Files = new ArrayList<String>() ;
                
        importDirectory(folder, wildcard, recursive, Files, keepDirStructure, orderByName, orderByTimestamp);
        serialize(Files,outputs.dir+File.separator+Output_OUTPUT_1+".ser");
        
	}
    public void serialize(Object obj, String fileName)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
 
        fos.close();
    }
    private void importDirectory(File folder, String pattern, String recursive, ArrayList<String> contents, String keepDirStructure, String orderByName, String orderByTimestamp) throws Exception {
       
    	System.out.println("Folder Path is "+folder.getPath());
        File[] files = new File(folder.getPath()).listFiles();
        System.out.println("No.of Files is "+files.length);
        for (File file : files)
            if (file.isFile()) {
                contents.add("FS"+File.separator+file.toString());
            }
        
        if (orderByName.equals("true")) {
            Collections.sort(contents, new Comparator<String>() {
                public int compare(String documentRecord, String documentRecord1) {
                    return documentRecord.compareTo(documentRecord1);
                }

			
            });
            
    
            }
        }
    
    /*
     * This code is called once when all of the data has passed through the block. 
     * It should be used to cleanup any resources that the block has made use of.
     */
    public void postExecute(BlockEnvironment env) throws Exception
    {
        
    }

}