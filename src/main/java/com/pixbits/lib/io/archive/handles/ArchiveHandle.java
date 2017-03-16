package com.pixbits.lib.io.archive.handles;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.pixbits.lib.io.archive.ArchiveFormat;
import com.pixbits.lib.io.archive.ExtractionCanceledException;
import com.pixbits.lib.io.archive.support.ArchiveExtractCallback;
import com.pixbits.lib.io.archive.support.ArchiveExtractPipedStream;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class ArchiveHandle extends Handle
{
  private Path file;
  public final int indexInArchive;
  public final String internalName;
  public final ArchiveFormat format;
  public final long size;
  public final long compressedSize;
  public final long crc;
  
  private IInArchive archive;
  
  public ArchiveHandle(Path file, ArchiveFormat format, String internalName, Integer indexInArchive, long size, long compressedSize, long crc)
  {
    this.file = file.normalize();
    this.internalName = internalName;
    this.indexInArchive = indexInArchive;
    this.format = format;  
    this.size = size;
    this.compressedSize = compressedSize;
    this.archive = null;
    this.crc = crc;
  }
    
  protected IInArchive open()
  {
    if (archive != null)
      return archive;
    
    try
    {      
      RandomAccessFileInStream rfile = new RandomAccessFileInStream(new RandomAccessFile(file.toFile(), "r"));
      return SevenZip.openInArchive(null, rfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
  
  @Override public final boolean isArchive() { return true; }
  
  @Override public Path file() { return file; }
  @Override public String fileName() { return internalName; }
  
  @Override public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
  @Override public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
  @Override public String plainInternalName() { return internalName.substring(0, internalName.toString().lastIndexOf('.')); }
  @Override public String getInternalExtension() { return internalName.substring(internalName.toString().lastIndexOf('.')+1); }
  
  @Override public long size() { return size; }
  @Override public long compressedSize() { return compressedSize; }
  @Override public long crc() { return crc; }
  
  

  public boolean renameInternalFile(String newName)
  {       
    return false;
  }
  
  @Override
  public void relocate(Path file)
  {
    this.file = file;
  }
  
  @Override
  public Handle relocateInternal(String internalName)
  {
    return null;//new Zip7Handle(file, internalName);
  }
  
  @Override
  public InputStream getInputStream() throws IOException
  {
    final IInArchive archive = open();    
    final ArchiveExtractPipedStream stream = new ArchiveExtractPipedStream(archive, indexInArchive);
    final ArchiveExtractCallback callback = new ArchiveExtractCallback(stream); 
    
    Runnable r = () -> {
      //System.out.println("Extract Thread Started");
      try
      {
        archive.extract(new int[] { indexInArchive }, false, callback);
        callback.close();
      }
      catch (ExtractionCanceledException e)
      {
        
      }
      catch (IOException e)
      {
        System.err.println(String.format("Exception while extracting file %s from archive %s (index: %d)", 
            internalName, file.getFileName().toString(), indexInArchive)); 
        
        e.printStackTrace();
      }
      //System.out.println("Extract Thread Stopped");
    };
    
    new Thread(r).start();
 
    return stream.getInputStream();
  }

  
}