package com.pixbits.lib.ui.elements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ConcurrentModificationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class ProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	JLabel title;
	JLabel desc;
	JProgressBar progress;
	Runnable callback;
	
	private ProgressDialog(Frame parent, String title, Runnable cb)
	{
		super(parent, title);
		this.callback = cb;
		
		this.setUndecorated(true);

		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		progress = new JProgressBar();
		progress.setStringPainted(true);
		
		this.title = new JLabel(title);
		this.title.setFont(this.title.getFont().deriveFont(Font.BOLD));
		this.title.setHorizontalAlignment(SwingConstants.CENTER);

		
		desc = new JLabel("...");
		desc.setHorizontalAlignment(SwingConstants.CENTER);
		
		//progress.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		panel.add(progress, BorderLayout.CENTER);
		JPanel upperPanel = new JPanel(new GridLayout(2,1));
		upperPanel.add(this.title);
		upperPanel.add(desc);
		panel.add(upperPanel, BorderLayout.NORTH);
		
		if (callback != null)
		{
		  JButton cancelButton = new JButton("Cancel");
		  panel.add(cancelButton, BorderLayout.SOUTH);
		  cancelButton.addActionListener(e -> cb.run());
		}
		
		panel.setPreferredSize(new Dimension(400,100));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		this.add(panel);
		
		pack();
		this.setLocationRelativeTo(parent);
	}
	
	public ProgressDialog()
	{
		this(null, "", null);
	}

	public static class Manager
	{
	  private static Manager manager = null;
	  private ProgressDialog dialog = null;
	  
	  public Manager()
	  {
	    if (manager != null)
	      throw new IllegalStateException("ProgressDialog.Manager has been instantiated more than once");
	    Manager.manager = this;
	  }
	  
	  public void show(Frame parent, String title, final Runnable callback)
	  {
	    if (dialog != null)
	      throw new ConcurrentModificationException("Progress dialog reinit while it was already displayed");
	    
	    if (callback != null)
	      dialog = new ProgressDialog(parent, title, () -> { callback.run(); finished(); });
	    else
	      dialog = new ProgressDialog(parent, title, null);
	    
	    dialog.progress.setMaximum(100);
	    dialog.progress.setValue(0);
	    dialog.setVisible(true);
	  }
	  
	  public void update(SwingWorker<?,?> worker, String desc)
	  {
	    dialog.progress.setValue(worker.getProgress());
	    dialog.desc.setText(desc);
	  }
	  
	  public void update(float value, String desc)
	  {
	    int ivalue = (int)(value*100);
	    dialog.progress.setValue(ivalue);
	    dialog.progress.setString(String.format("%2.1f%%", value*100));
	    dialog.desc.setText(desc);
	  }
	  
	  public void finished()
	  {
	    dialog.dispose();
	    dialog = null;
	  }
	}
}
