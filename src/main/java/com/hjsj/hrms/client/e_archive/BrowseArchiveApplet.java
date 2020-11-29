/**
 * 
 */
package com.hjsj.hrms.client.e_archive;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.hessian.HessianApplet;
import com.hrms.struts.hessian.HessianExecutor;
import com.hrms.struts.valueobject.TransInfoView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:浏览电子档案</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:上午11:16:27</p> 
 *@author cmq
 *@version 4.0
 */
public class BrowseArchiveApplet extends HessianApplet {

	private JPanel jContentPane = null;
	private JToolBar jToolBar = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	//private ScrollPicturePanel scrollPicturePanel = new ScrollPicturePanel();
	private HessianExecutor executor =new HessianExecutor(url);// 注意超类中属性url  //  @jve:decl-index=0:
	private JPanel jPanel = null;
	private JComboBox jComboBox = null;
	/**tiff文件图形象对象列表*/
	private ArrayList pagelist;  //  @jve:decl-index=0:
	private JScrollPane jScrollPane = null;
	private JLabel jLabel = null;
	private JMenuBar jJMenuBar = null;
	private String name;  //  @jve:decl-index=0:
	private float xscale=0.5f;
	private float yscale=0.5f;
	public BrowseArchiveApplet() {

	}

	private void createGUI()
	{
		this.setSize(new Dimension(519, 226));
        this.setContentPane(getJContentPane());
	}
	/**
	 * This method initializes this
	 * 
	 */
	@Override
    public void init() {
     try
     {
        super.init();

		this.setSize(new Dimension(519, 226));
        this.setContentPane(getJContentPane());

        /*
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
            	createGUI();
            }
        });*/
		String filename=getParameter("filename");
		//url="http://192.192.100.102:8080";
		//filename="\0001~9999\1004\10040101.Tif";
		//executor=new HessianExecutor(url);
		findArchiveFile(filename);
     }
     catch(Exception ex)
     {
    	 ex.printStackTrace();
     }
	}

	/**
	 * 取得后台电子档案文件
	 * @param filename
	 */
	private void findArchiveFile(String filename)
	{
		try
		{
			TransInfoView infoview=new TransInfoView();
			infoview.setWorkFlowId("10100960101");
			filename=SafeCode.decode(filename);
			infoview.getFormHM().put("filename", filename);
			HashMap map=execute(infoview);
			
			/*
	        filename="D:\\tomcat5.5\\temp\\e_archive51304.tif";
            int len;
            FileInputStream in=new FileInputStream(new File(filename));
            byte buff[] = new byte[1024];			
            ByteArrayOutputStream out = new ByteArrayOutputStream(); 
            while((len   =   in.read(buff))!=-1){ 
            	out.write(buff, 0, len); 
            }
            byte[] ss=out.toByteArray();	        
            out.close();
			*/
	        byte[] ss=(byte[])map.get("content");
			ByteArrayInputStream   bytein   = new ByteArrayInputStream(ss);   
			pagelist=PubFunc.splitRenderedImage(bytein, "tiff");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<pagelist.size();i++)
			{
				buf.setLength(0);
				JComboBox combobox=this.getJComboBox();
				buf.append("第");
				buf.append(i+1);
				buf.append("页");
				combobox.addItem(buf.toString());
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar(), BorderLayout.NORTH);
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.add(getJButton());
			jToolBar.add(getJButton1());
			jToolBar.add(getJPanel());
		}
		return jToolBar;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("放大");
			jButton.addActionListener(new java.awt.event.ActionListener()
			{
				@Override
                public void actionPerformed(java.awt.event.ActionEvent e)
				{
					try
					{
						JComboBox bobox=getJComboBox();
						int idx=bobox.getSelectedIndex();
						if(idx==-1)
							return;
						BufferedImage bufferimage=PubFunc.renderedToBuffered((RenderedImage)pagelist.get(idx),"PNG");
						//bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,400,100,1f,1f);
						xscale=xscale+0.1f;
						yscale=yscale+0.1f;
						bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,700,400,xscale,yscale);
						ImageIcon icon=new ImageIcon(bufferimage);
						// picturpnl.getDavid();
						jLabel.setIcon(icon);

					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("缩小");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				@Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
				  try
				  {
						JComboBox bobox=getJComboBox();
						int idx=bobox.getSelectedIndex();
						if(idx==-1)
							return;
						BufferedImage bufferimage=PubFunc.renderedToBuffered((RenderedImage)pagelist.get(idx),"PNG");
						//bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,400,100,1f,1f);
						xscale=xscale-0.1f;
						yscale=yscale-0.1f;
						bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,700,400,xscale,yscale);
						ImageIcon icon=new ImageIcon(bufferimage);
						// picturpnl.getDavid();
						jLabel.setIcon(icon);

				  }
				  catch(Exception ex)
				  {
					  ex.printStackTrace();
				  }
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJComboBox(), BorderLayout.WEST);
		}
		return jPanel;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.addItemListener(new java.awt.event.ItemListener() {
				@Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
					JComboBox bobox=getJComboBox();
					int idx=bobox.getSelectedIndex();
					if(idx==-1)
						return;
					BufferedImage bufferimage=PubFunc.renderedToBuffered((RenderedImage)pagelist.get(idx),"PNG");
					//bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,400,100,1f,1f);
					bufferimage=PubFunc.addWaterMarker("北京高法开放档案", bufferimage,700,400,xscale,yscale);
					ImageIcon icon=new ImageIcon(bufferimage);
					// picturpnl.getDavid();
					jLabel.setIcon(icon);
				}
			});
		}
		return jComboBox;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jLabel = new JLabel();
			jLabel.setText("");
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(jLabel);
		}
		return jScrollPane;
	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
