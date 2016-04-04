package com.rawa.ac.uk;

//import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.pipeline.core.data.Data;
import org.pipeline.core.xmlstorage.XmlDataStore;

import com.connexience.api.StorageClient;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscFolder;
import com.connexience.api.model.EscUser;


public class BlockInputs {
	
	public String inputLocation=null;
	//method to deserialize input from file to Data
	public XmlDataStore Deserialize(String inputFile)
	{
		 XmlDataStore inputData=null;
	     FileInputStream fis = null;
	     ObjectInputStream in = null;
	      try {
	           fis = new FileInputStream(inputFile);
	           in = new ObjectInputStream(fis);
	           inputData = (XmlDataStore) in.readObject();
	           in.close();
	          } catch (Exception ex) {
	                   ex.printStackTrace();
	                   }
	      return inputData;
	}
	
	public ArrayList<String> FileDeserialize(String inputFile)
	{
		 ArrayList<String> inputData=null;
	     FileInputStream fis = null;
	     ObjectInputStream in = null;
	      try {
	           fis = new FileInputStream(inputFile);
	           in = new ObjectInputStream(fis);
	           inputData = (ArrayList<String>) in.readObject();
	           in.close();
	          } catch (Exception ex) {
	                   ex.printStackTrace();
	                   }
	      return inputData;
	}
	//get file ID from eSc storage
	public static String getFileID(String fileName,EscDocument[] docs )
	{//System.out.println(docs.length);
		for( int i=0; i<docs.length;i++)
			{if(fileName.equalsIgnoreCase(docs[i].getName()))
			
				return docs[i].getId();
			}
		return "no";
	}
	
	//get folder ID from eSc storage
	public static String getFolderID(String fileName,EscFolder[] folds )
	{//System.out.println(docs.length);
		for( int i=0; i<folds.length;i++)
			{if(fileName.equalsIgnoreCase(folds[i].getName()))
			
				return folds[i].getId();
			}
		return "no";
	}
	
	//download file from eSc storage block folder
	public File DownloadfromBlock(String input, String output) throws Exception
	{
		BlockEnvironment env=new BlockEnvironment();
		env.geteScCred();
		File file=new File("eSc.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String HOSTNAME=br.readLine();
		int PORT=8080;
		Boolean SECURE=false;
		String USERNAME=br.readLine();
		String PASSWORD=br.readLine();
		File localFile=null;
		System.out.println(input+"   "+output);
		File Input=new File(input);
		//File Output=new File(output);
		try {
			// Create a new storage client with username and password
			StorageClient client = new StorageClient(HOSTNAME,PORT,SECURE, USERNAME,PASSWORD);
			
			EscFolder homeFolder = client.homeFolder();
			EscFolder[] subFolders = client.listChildFolders(homeFolder.getId());
			//access blueprint folder
			System.out.println(Input.getParentFile().getParentFile().getName());
			String folder_ID=getFolderID(Input.getParentFile().getParentFile().getName(),subFolders);
			//String substring=input.substring(input.indexOf(File.separator)+1,input.length());
			subFolders = client.listChildFolders(folder_ID);
			
			//Access block folder
			System.out.println(Input.getParentFile().getName());
			folder_ID=getFolderID(Input.getParentFile().getName(),subFolders);
			
			EscDocument[] subDocs = client.folderDocuments(folder_ID);
			
			//access the specified file
			System.out.println(Input.getName());
			EscDocument doc = null;
			for(int i=0;i<subDocs.length;i++)
				if(subDocs[i].getName().matches(Input.getName()))
					doc=subDocs[i];
			
			//create local file		   	
			System.out.println(output+File.separator+Input.getName());
		    localFile = new java.io.File(output+File.separator+Input.getName());
		    //download the file from the server	
			client.download(doc, localFile);
			//client.deleteFolder(folder_ID);
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return localFile;
	}
	//call deserialize method to create XmlDataStore and convert it to Data 
	public Data getInputDataSet(String inputName) throws Exception {
		//DownloadfromBlock(inputName,inputName);
		Data ImportedData=new Data();
		System.out.println(inputName+".ser");
		//ImportedData.recreateObject(Deserialize(inputName+".ser"));
		
        return ImportedData;
	}
	
	//public Data new_getInputDataSet(String Block)
	{
		
	}
	
	public final List<String> getInputFiles(String inputName) throws Exception {
		
		String OS=System.getProperty("os.name");
		String root=null;
		String Temp=null;
		String output=(new File(inputName)).getParent();
		if(OS.substring(0, 2).matches("Wi"))
			root="D:"+File.separator;
		else
			root=System.getProperty( "user.home" )+File.separator;
		System.out.println("root "+root+"\n inputName "+inputName);
		List<String> inputs=FileDeserialize(inputName+".ser");
		
		List<String> files=new ArrayList<>();
		//System.out.println(inputs.get(0));
		for(int i=0;i< inputs.size();i++)
			files.add(inputs.get(i).toString());		
		
		for(int i=0;i<files.size();i++)
			if(files.get(i).substring(0,3).matches("eSc"))
			{
				//download from eSc     				
				Temp=files.get(i).substring(4,files.get(i).length());   //file path without suffix
				System.out.println("Temp "+Temp);
				BlockInputs in=new BlockInputs();
				Temp=in.DownloadfromBlock(Temp, output).getName(); 
				//store local file path in the arraylist
				files.set(i,output+File.separator+Temp);
				
			}
			else
			{
				files.set(i,files.get(i).substring(3,files.get(i).length()));
			}   		   	    
	        
		return files;
	}
	
}
