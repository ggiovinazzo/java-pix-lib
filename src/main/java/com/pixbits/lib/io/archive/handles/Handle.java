package com.pixbits.lib.io.archive.handles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.pixbits.lib.io.archive.Compressible;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.digest.DigestableCRC;

public abstract class Handle implements DigestableCRC, Compressible, VerifierEntry
{
  @Override
  public abstract String toString();
  
  public abstract Path path();
  public abstract String relativePath();
  public abstract String fileName();
  
  public abstract String internalName();
  public abstract String plainName();
  public abstract String plainInternalName();
  public abstract void relocate(Path file);
  public abstract Handle relocateInternal(String internalName);
  public abstract boolean isArchive();
  
  public String getExtension() {
    String filename = path().getFileName().toString();
    int lastdot = filename.lastIndexOf('.');
    return lastdot != -1 ? filename.substring(lastdot+1) : "";
  }
  
  public abstract String getInternalExtension();
  public abstract InputStream getInputStream() throws IOException;
  
  /*
   * @return crc returns crc32 for handle, this operation caches the value
   */
  public abstract long crc();
  
  /**
   * @return size in bytes of the handle
   */
  public abstract long size();
  
  /**
   * @return compressed size in bytes of the handle, corresponds to <code>size()</code> for binary handles
   */
  public abstract long compressedSize();
}
