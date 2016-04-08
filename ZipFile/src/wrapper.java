
import java.io.File;

import com.rawa.ac.uk.BlockEnvironment;
import com.rawa.ac.uk.BlockInputs;
import com.rawa.ac.uk.BlockOutputs;


public class wrapper {

	public static void main(String[] args) throws Exception {
		MyService Service= new MyService();
		BlockInputs inputs=new BlockInputs();
		BlockOutputs outputs=new BlockOutputs();
		BlockEnvironment env=new BlockEnvironment();
		
		String OS=System.getProperty("os.name");
		String root=null;
		if(OS.substring(0, 3).matches("Win"))
			root="D:"+File.separator;
		else
			root=System.getProperty( "user.home" )+File.separator;
		
		//create blueprint dir
		File baseDir=new File(root+args[0]);
		if (!baseDir.exists())
		   baseDir.mkdir();		
		
	    //download blueprint to its dir	  	   
	  	File blueprint=new File(baseDir+File.separator+args[0]+".yaml");
	  	env.source = args[2]; //password	
		
		env.blueprint=args[0];  
		outputs.StoreLocation="in";//env.output_location(baseDir+File.separator+args[0]+".yaml", args[1]);
		System.out.println("store Location is "+outputs.StoreLocation);
				
		//creating block dir
		File blockDir=new File(root+baseDir.getName()+File.separator+args[1]);
		if (!blockDir.exists())
		    blockDir.mkdir();					
		 
		//local output dir for this block 
		outputs.dir=root+args[0]+File.separator+args[1];
		
		Service.execute(env, inputs, outputs);

	}

}
