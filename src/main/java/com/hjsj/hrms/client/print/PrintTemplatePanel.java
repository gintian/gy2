/**
 * 
 */
package com.hjsj.hrms.client.print;

import com.hjsj.hrms.client.card.CardModel;
import com.hjsj.hrms.client.card.CardView;
import com.hrms.struts.hessian.HessianExecutor;
import com.hrms.struts.valueobject.TransInfoView;

import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007-1-12:9:25:36</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PrintTemplatePanel extends JPanel implements ActionListener {
	/**WEB应用的地址http://192.192.100.101:8080*/
	private String url;
    private JComboBox combox;
    private CardView cardview;
    private CardModel cardmodel;
	/**
	 * @param arg0
	 * @param arg1
	 * @param url
	 */
//	public PrintTemplatePanel(LayoutManager arg0, boolean arg1, String url) {
//		super(arg0, arg1);
//		this.url = url;
//	}
	
	public PrintTemplatePanel(String url) 
	{
		super(new BorderLayout());
		this.url = url;	
		/**模型及视图*/
		cardview=new CardView();
		cardmodel=new CardModel();
		cardmodel.addObserver(cardview);
		createGui();
	}
	/**
	 * 根据模板传过来的参数
	 *
	 */
	private void createGui()
	{
		JToolBar toolbar=new JToolBar();
		JButton btn=new JButton("上页");
		btn.addActionListener(this);
		btn.setActionCommand("prepage");
		toolbar.add(btn);
		
		btn=new JButton("下页");
		btn.setActionCommand("nextpage");
		btn.addActionListener(this);
		toolbar.add(btn);
		btn=new JButton("设置");
		btn.addActionListener(this);
		toolbar.add(btn);
		btn=new JButton("当前页");
		btn.addActionListener(this);
		toolbar.add(btn);
		btn=new JButton("当前人");
		btn.addActionListener(this);
		toolbar.add(btn);
		btn=new JButton("全部");
		btn.addActionListener(this);
		toolbar.add(btn);
		String[] objlist={"aa","bb","cc"};
		combox=new JComboBox(objlist);
		combox.setSelectedIndex(0);
		//combox.setPreferredSize(new Dimension(50,20));
		combox.setMaximumSize(new Dimension(100,40));
		
		toolbar.add(combox);
	
		this.add(toolbar,BorderLayout.NORTH);
		
		/**模板分页*/
		JTabbedPane tabbedpanel=new JTabbedPane();
		
		tabbedpanel.setLayout(new GridLayout(1,1));
		JComponent panel1=makePanel("");
		tabbedpanel.addTab("first",/*panel1*/this.cardview);
		
		JComponent panel2=makePanel("");
		tabbedpanel.addTab("second",panel2);
		tabbedpanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedpanel.setTabPlacement(JTabbedPane.BOTTOM);
		this.add(tabbedpanel);
		
	}
	/**
	 * 创建选项卡
	 * @param title
	 * @return
	 */
	private JComponent makePanel(String title)
	{
		JPanel panel=new JPanel(false);
		JLabel label=new JLabel(title);
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1,1));
		panel.add(label);
		panel.setBorder(BorderFactory.createEmptyBorder());
		return panel;
	}
	/**
	 * 创建图标
	 * @param path
	 * @return
	 */
	private ImageIcon createImageIcon(String path)
	{
		URL imgurl=this.getClass().getResource(path);
		if(imgurl!=null)
			return new ImageIcon(imgurl);
		else
			return null;
	}

	@Override
    public void actionPerformed(ActionEvent arg0) {
		String command=arg0.getActionCommand();
		
        HessianExecutor executor = new HessianExecutor(url);// 注意超类中属性url
        
		if("prepage".equalsIgnoreCase(command))
		{
			TransInfoView tranview=new TransInfoView();
			tranview.setWorkFlowId("0000000001");
			ArrayList list=new ArrayList();
			list.add(arg0.getActionCommand());
			list.add("myfirst applet transition class");
			tranview.getFormHM().put("list",list);
			try
			{
				HashMap map=executor.execute(tranview);
				ArrayList resultlist=(ArrayList)map.get("result");
				System.out.println("------>after run transition.....");
				for(int i=0;i<resultlist.size();i++)
				{
					System.out.println("=====>"+resultlist.get(i));
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
        
		}
		else if("nextpage".equalsIgnoreCase(command))
		{
	        StringBuffer strsql = new StringBuffer();
	        strsql.append("select * from codeitem");
	        try
	        {
	        	RowSet rset=executor.search(strsql.toString());
	        	while(rset.next())
	        	{
	                combox.addItem(rset.getString("codeitemid") + ":" + rset.getString("codeitemdesc"));
	                System.out.println("-->" + rset.getString("codeitemid") + ":" + rset.getString("codeitemdesc"));
	        	}

	        }
	        catch(Exception ex)
	        {
	            ex.printStackTrace();
	        }			
		}
		else
		{
			
		}
		System.out.println("--->="+arg0.getActionCommand());
	}
	
	
}
