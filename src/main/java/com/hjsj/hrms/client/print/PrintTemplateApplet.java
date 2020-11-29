package com.hjsj.hrms.client.print;

import com.hrms.struts.hessian.HessianApplet;

import javax.swing.*;
import java.awt.*;

public class PrintTemplateApplet extends HessianApplet {

	/**
	 * Constructor of the applet.
	 *
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 */
	public PrintTemplateApplet() throws HeadlessException {
		super();
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it is being reclaimed and that it should destroy
	 * any resources that it has allocated. The <code>stop</code> method
	 * will always be called before <code>destroy</code>. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform before it is
	 * destroyed. For example, an applet with threads would use the
	 * <code>init</code> method to create the threads and the
	 * <code>destroy</code> method to kill them. <p>
	 */
	@Override
    public void destroy() {
		// Put your code here
	}

	/**
	 * Returns information about this applet. An applet should override
	 * this method to return a <code>String</code> containing information
	 * about the author, version, and copyright of the applet. <p>
	 *
	 * @return  a string containing information about the author, version, and
	 * copyright of the applet.
	 */
	@Override
    public String getAppletInfo() {
		return "This is my default applet created by Eclipse";
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it has been loaded into the system. It is always
	 * called before the first time that the <code>start</code> method is
	 * called. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has initialization to perform. For example, an applet with
	 * threads would use the <code>init</code> method to create the
	 * threads and the <code>destroy</code> method to kill them. <p>
	 */
	@Override
    public void init() {
		// Put your code here
	 try
	 {
     this.setSize(new Dimension(107, 41));
		SwingUtilities.invokeAndWait(new Runnable(){
			@Override
            public void run(){
				url="http://192.192.100.102:8080";
				PrintTemplatePanel panel=new PrintTemplatePanel(url);  //超类中的url链接
				getContentPane().add(panel);
			}
		});
	 }
	 catch(Exception ex)
	 {
		 ex.printStackTrace();
	 }
		
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it should start its execution. It is called after
	 * the <code>init</code> method and each time the applet is revisited
	 * in a Web page. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform each time the Web
	 * page containing it is visited. For example, an applet with
	 * animation might want to use the <code>start</code> method to
	 * resume animation, and the <code>stop</code> method to suspend the
	 * animation. <p>
	 */
	@Override
    public void start() {
		// Put your code here
	}

	/**
	 * Called by the browser or applet viewer to inform
	 * this applet that it should stop its execution. It is called when
	 * the Web page that contains this applet has been replaced by
	 * another page, and also just before the applet is to be destroyed. <p>
	 *
	 * A subclass of <code>Applet</code> should override this method if
	 * it has any operation that it wants to perform each time the Web
	 * page containing it is no longer visible. For example, an applet
	 * with animation might want to use the <code>start</code> method to
	 * resume animation, and the <code>stop</code> method to suspend the
	 * animation. <p>
	 */
	@Override
    public void stop() {
		// Put your code here
	}
	
	public String HelloWorld(String name)
	{
		return "how are you!"+name;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
