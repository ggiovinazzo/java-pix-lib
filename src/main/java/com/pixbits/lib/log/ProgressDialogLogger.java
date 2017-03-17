package com.pixbits.lib.log;

import javax.swing.SwingUtilities;

import com.pixbits.lib.ui.elements.ProgressDialog;

class ProgressDialogLogger implements ProgressLogger
{
  final static private ProgressDialog.Manager manager = new ProgressDialog.Manager();
  
  float lastProgress;
  String progressMessage;
   
  private boolean running = false;
  private Thread dialogThread = null;
  private Runnable dialogUpdater = () -> {
    try
    {
      while (running)
      {
        Thread.sleep(50);
        SwingUtilities.invokeLater(() -> { 
          if (running) manager.update(lastProgress, progressMessage); 
        });
      }
    }
    catch (InterruptedException e)
    {
      manager.finished();
    }
  };
  
  @Override public void startProgress(Log type, String message)
  {
    try
    {
      lastProgress = 0;
      SwingUtilities.invokeAndWait(() -> manager.show(null, message, null));
      dialogThread = new Thread(dialogUpdater);
      running = true;
      dialogThread.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @Override synchronized public void updateProgress(float percent, String message)
  {
    lastProgress = percent;
    progressMessage = message;
  }
  
  @Override public void endProgress()
  {
    running = false;
    SwingUtilities.invokeLater(manager::finished);
  }
}