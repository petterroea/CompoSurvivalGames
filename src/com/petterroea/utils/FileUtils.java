package com.petterroea.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileUtils {
	public static String getMd5(File file)
	{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(file);				
			byte[] buffer = new byte[8192];
			int read = 0;
			while( (read = is.read(buffer)) > 0) {
				md.update(buffer, 0, read);
			}	
			byte[] digest = md.digest();
			String res = "";
			String tmp = "";
			for (int i = 0; i < digest.length; i++) {
				tmp = (Integer.toHexString(0xFF & digest[i]));
				if (tmp.length() == 1) {
					res += "0" + tmp;
				} else {
					res += tmp;
				}
			}
			res = URLEncoder.encode(res, "UTF-8");
			return res;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String readFile(File f)
	{
		String[] contents = new String[0];
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(f));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	        return sb.toString();
	    } catch(Exception e) {
	        
	    }
	    return "";
	}
	public static String readResource(InputStream is)
	{
		String[] contents = new String[0];
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	        return sb.toString();
	    } catch(Exception e) {
	        
	    }
	    return "";
	}
	public static void writeFile(File f, String[] lines)
	{
		try {
			PrintWriter out = new PrintWriter(f.getAbsolutePath());
			for(int i = 0; i < lines.length; i++)
			{
				out.println(lines[i]);
				out.flush();
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void writeFile(File f, String lines)
	{
		writeFile(f, lines.split("\n"));
	}
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	/**
	 * Copies a file
	 * @param sourceFile The source file
	 * @param destFile The destination file
	 * @throws IOException if something goes wrong
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	    	destFile.getParentFile().mkdirs();
	        destFile.createNewFile();
	    }
	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
