
import java.io.File;

import com.rawa.ac.uk.BlockEnvironment;
import com.rawa.ac.uk.BlockInputs;
import com.rawa.ac.uk.BlockOutputs;


public class wrapper {

	public static void main(String[] args) throws Exception {
		
		//args[0]:blueprint name, args[1]:block note template		
		
		MyService Service= new MyService();
		BlockInputs inputs=new BlockInputs();
		BlockEnvironment env=new BlockEnvironment();
		
		String OS=System.getProperty("os.name");
		String root=null;
		if(OS.substring(0, 3).matches("Win"))
			root="D:"+File.separator;
		else
			root=System.getProperty( "user.home" )+File.separator;
		
		//creating blueprint baseDir if not exists
		File baseDir=new File(root+args[0]);
		if (!baseDir.exists())
		{ 
			baseDir.mkdir();		    
		    
		}	
		//getting source file name from block node template
		String source= args[2];//env.get_property(root+args[0]+File.separator+args[0]+".yaml", args[1], "SourceFolder");
		File tmp=new File(source);
		env.source=root+args[0]+File.separator+tmp.getName();
		System.out.println(env.source);
		env.blueprint=args[0];  
		
		//creating block dir
		File blockDir=new File(root+args[0]+File.separator+args[1]);
		if (!blockDir.exists())
		    blockDir.mkdir();		
		
		BlockOutputs outputs=new BlockOutputs(); 
		
		outputs.dir=root+env.blueprint+File.separator+args[1];
		outputs.StoreLocation="in";
		Service.execute(env, inputs, outputs);
		
	}

}

