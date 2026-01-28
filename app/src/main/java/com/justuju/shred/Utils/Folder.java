package com.justuju.shred.Utils;

import java.util.ArrayList;

public class Folder {
    private  String path;
    private  String FolderName;
    private int numberOfFiles = 0;
    private int firstPic;
    private long FolderSize = 0;
    private ArrayList<PictureFacer> ListOfFiles = new ArrayList<PictureFacer>();

    public Folder(){

    }

    public Folder(String path, String folderName) {
        this.path = path;
        FolderName = folderName;
    }

    public void addFile(PictureFacer file)
    {
        ListOfFiles.add(file);
    }

    public ArrayList<PictureFacer> getListOfFiles()
    {
        return ListOfFiles;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public void addpics(){
        this.numberOfFiles++;
    }

    public int getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(int firstPic) {
        this.firstPic = firstPic;
    }

    public long getFolderSize() {
        return FolderSize;
    }

    public void setFolderSize(long FolderSize) {
        this.FolderSize += FolderSize;
    }
}
