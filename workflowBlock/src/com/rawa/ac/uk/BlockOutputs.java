package com.rawa.ac.uk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.io.*;
import org.pipeline.core.xmlstorage.XmlStorageException;

import com.connexience.api.StorageClient;
import com.connexience.api.model.EscDocumentVersion;
import com.connexience.api.model.EscFolder;
import com.connexience.api.model.EscUser;

public class BlockOutputs {
    public String dir;
    public String StoreLocation;
    
	
    public void serialize(Object obj, String fileName)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
 
        fos.close();
    }
	
	public String Upload(String filename) throws Exception
	{
		BlockEnvironment env=new BlockEnvironment();
		File file=new File("eSc.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String HOSTNAME=br.readLine();
		int PORT=8080;
		Boolean SECURE=false;
		String USERNAME=br.readLine();
		String PASSWORD=br.readLine();
		String path=null;
		try {
			// Create a new storage client with username and password
			StorageClient client = new StorageClient(HOSTNAME,PORT,SECURE, USERNAME,PASSWORD);
			
			File fileToUpload = new java.io.File(filename);
			System.out.println("file name  "+filename);
			EscFolder folder = client.homeFolder();
			EscFolder[] folders = client.listChildFolders(folder.getId());
			EscFolder baseDir;
			EscFolder subdir=null;
			String Dir=dir.substring(dir.indexOf(File.separator)+1, dir.length());
			String ID=BlockInputs.getFolderID(fileToUpload.getParentFile().getParentFile().getName(),folders);//Dir.substring(0, Dir.indexOf(File.separator)),folders );
			System.out.println("blueprint dir  "+fileToUpload.getParentFile().getParentFile().getName());
			//System.out.println("ID= "+ID.length());
			if(ID.matches("no"))
			{
				baseDir = client.createChildFolder(folder.getId(), fileToUpload.getParentFile().getParentFile().getName());
				subdir=client.createChildFolder(baseDir.getId(), fileToUpload.getParentFile().getName());
			}
			else
			{
				
				EscFolder[] subFolders = client.listChildFolders(ID);
				//System.out.println(subFolders.length);
				String subID=BlockInputs.getFolderID(fileToUpload.getParentFile().getName(), subFolders);
				//System.out.println("ID="+subID.length());
				if((subID.matches("no"))||(subFolders.length==0))
				{
					subdir=client.createChildFolder(ID, fileToUpload.getParentFile().getName());
					
				}
				else
					if(subID.length()>0)
					{
						for(int i=0;i<subFolders.length;i++)
							if(subFolders[i].getId()==subID)
								subdir=subFolders[i];
					}
					
			}
			System.out.println("block folder"+subdir.getName());
			EscDocumentVersion version = client.upload(subdir, fileToUpload);
			path=version.getDownloadPath();			
					
        	}
		catch (Exception e){
			e.printStackTrace();
		}
		return path;
	}
	public final void setOutputDataSet(String outputName, Data outputData) throws Exception
	{
		//serialize Data and store it in a file
		
		serialize(outputData.storeObject(),dir+File.separator+outputName+".ser");
		if (StoreLocation.matches("out")||StoreLocation.matches("both"))
		   Upload(dir+File.separator+outputName+".ser");
				
	}
	
	public final void setOutputFile(String outputName, File file) throws Exception  {
		
		
		ArrayList<String> Files= new ArrayList<>();
		System.out.println(StoreLocation);
		//adding files to the list
		if(StoreLocation.matches("in")||StoreLocation.matches("both"))
		     Files.add("FS"+File.separator+file.getPath());
		System.out.println("in outputs   "+file);
		
		if(StoreLocation.matches("out")||StoreLocation.matches("both"))
		{
			System.out.println(file.getPath().substring(file.getPath().indexOf(File.separator)+1,file.getPath().length()));
			
			Files.add("eSc"+File.separator+file.getPath().substring(file.getPath().indexOf(File.separator)+1,file.getPath().length()));
			
			Upload(file.getPath());
		}
		
		//serialize the list of files
		serialize(Files,dir+File.separator+outputName+".ser");
		if (StoreLocation.matches("out")||StoreLocation.matches("both"))
		    Upload(dir+File.separator+outputName+".ser");
		
	}
	
	
}
