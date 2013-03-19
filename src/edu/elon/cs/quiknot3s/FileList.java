/**Copyright 2013 Evan Walmer, Miles Camp, Dillon Lynch, Clyde Zuber, Elliot Wehmueller
 * quiknote3s.FileList
 */
package edu.elon.cs.quiknot3s;

import java.util.Comparator;

/**
 * Object Class that holds all the information quiknot3s needs 
 * to utilize from the audio files
 * @author Team Socrat3s
 *
 */
public class FileList implements Comparable<FileList>{

	private String fileName;
	private String uri;
	
	public FileList(String fileName, String uri){
		this.fileName = fileName;
		this.uri = uri;
	}
	
	public String getUri(){
		return uri;
	}
	
	@Override
	public String toString(){
		return fileName;
	}

	public String getFileName() {
		return fileName;
	}
	

	@Override
	public int compareTo(FileList another) {
		
		return this.getFileName().compareTo(another.getFileName());
	}

}
