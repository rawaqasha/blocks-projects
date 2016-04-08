import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;




import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import com.rawa.ac.uk.BlockEnvironment;
import com.rawa.ac.uk.BlockInputs;
import com.rawa.ac.uk.BlockOutputs;
import com.rawa.ac.uk.workflowBlock;


public class MyService implements workflowBlock {
    /**
     * This field refers to property 'Copy Input' defined the blueprint
     */
    private final static String Prop_COPY_INPUT = "Copy Input";

    /**
     * This fields refer to input ports defined in the blueprint
     */
    private final static String Input_INPUT_1 = "input-1";
    
      
    /**
     * This field refers to output port 'file-3' defined the blueprint
     */
    private final static String Output_OUTPUT_1 = "output-1";


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
    public void execute(BlockEnvironment env, BlockInputs inputs, BlockOutputs outputs) throws Exception
    {
    	ArrayList<String> input1=(ArrayList)inputs.getInputFiles(outputs.dir+File.separator+Input_INPUT_1);
    	    	
    	File file1=new File(input1.get(0));
    	System.out.println(file1.getPath());        
    	/*old method without password
        FileOutputStream fos = new FileOutputStream(file1.getParent()+File.separator+"zipfile.zip");
        
        ZipOutputStream zos = new ZipOutputStream(fos);
        //add a new Zip Entry to the ZipOutputStream
        //ZipEntry ze = new ZipEntry(file1.getName());
        zos.putNextEntry(ze);
        //read the file and write to ZipOutputStream
        FileInputStream fis = new FileInputStream(file1);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
         
        //Close the zip entry to write to zip file
        zos.closeEntry();
        //Close resources
       zos.close();
        fis.close();
        fos.close();*/
        try {
            //This is name and path of zip file to be created
            ZipFile zipFile = new ZipFile(file1.getParent()+File.separator+"zipfile.zip");
             
            //Add files to be archived into zip file
            ArrayList<File> filesToAdd = new ArrayList<File>();
            filesToAdd.add(file1);
           
             
            //Initiate Zip Parameters which define various properties
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to default compression         
            
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
             
            //Set the encryption flag to true
            parameters.setEncryptFiles(true);
             
            //Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
             
            //AES_STRENGTH_128 - For both encryption and decryption
            //AES_STRENGTH_192 - For decryption only
            //AES_STRENGTH_256 - For both encryption and decryption        
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
             
            //Set password
            parameters.setPassword(env.source);
             
            //Now add files to the zip file
            zipFile.addFiles(filesToAdd, parameters);
        } 
        catch (ZipException e) 
        {
            e.printStackTrace();
        }
        File outFile = new File(file1.getParent()+File.separator+"zipfile.zip");
        System.out.println("block output "+outFile);
        
    	outputs.setOutputFile(Output_OUTPUT_1, outFile);
       		
    }
    
    /*
     * This code is called once when all of the data has passed through the block. 
     * It should be used to cleanup any resources that the block has made use of.
     */
    public void postExecute(BlockEnvironment env) throws Exception
    {
        
    }

}
