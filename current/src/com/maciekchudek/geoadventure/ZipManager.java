package com.maciekchudek.geoadventure;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipManager {

	public static List<String> unzipNames(InputStream is) throws Exception {
		ZipInputStream zin = new ZipInputStream(is);
		ZipEntry ze = null;
		List<String> sb = new ArrayList<String>();
		while ((ze = zin.getNextEntry()) != null) {
			sb.add(ze.getName());
		}

		return sb;
	}

	
	private static Reader getReaderFromInputStream(InputStream is){
		Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return new StringReader(s.hasNext() ? s.next() : "");
	}
	
	public static Reader[] getCSVReaders(InputStream is) throws IOException{
		Reader[] CSVs = new Reader[2];
		
		ZipInputStream zin = new ZipInputStream(is);
		ZipEntry ze = null;
		while ((ze = zin.getNextEntry()) != null) {
			String name = ze.getName(); 
			if(name.equals("meta.csv")){
				CSVs[0] = getReaderFromInputStream(zin);
			}
			if(name.equals("adventure.csv")){
				CSVs[1] = getReaderFromInputStream(zin);
			}
			zin.closeEntry();
		}
		zin.close();
		return CSVs;		
	}
	
	public static void unzipAssetFiles(InputStream is, String path) throws Exception {
		// create target location folder if not exist
		dirChecker(path);

		ZipInputStream zin = new ZipInputStream(is);
		ZipEntry ze = null;
		while ((ze = zin.getNextEntry()) != null) {
			// create dir if required while unzipping
			if (ze.isDirectory()) {
				dirChecker(path + ze.getName());
				zin.closeEntry();
			} else if (ze.getName().startsWith("assets/")){
				
				byte[] buffer = new byte[2048];

				FileOutputStream fout = new FileOutputStream(path + ze.getName());
				BufferedOutputStream bos = new BufferedOutputStream(fout, buffer.length);
				int size;
				while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, size);
				}
				// Close up shop..
				bos.flush();
				bos.close();

				fout.flush();
				fout.close();
				zin.closeEntry();				
			}
		}
		zin.close();
	}

	private static void dirChecker(String dir) {
		File f = new File(dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

}