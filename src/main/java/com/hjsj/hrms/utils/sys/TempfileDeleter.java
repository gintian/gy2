package com.hjsj.hrms.utils.sys;

import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 9, 2005:10:21:28 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class TempfileDeleter implements HttpSessionBindingListener,
        Serializable {

    /** The temp names. */
    private List tempfileNames = new java.util.ArrayList();    
    /**
     * 
     */
    public TempfileDeleter() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void addTempFile(String filename) {
        this.tempfileNames.add(filename);
    }   
    
    public boolean isTempFileAvailable(String filename) {
        return (this.tempfileNames.contains(filename));
    }
    
    /* 
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0) {
        return ;
    }

    /* 
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0) {
        Iterator iter = this.tempfileNames.listIterator();
        //System.out.println("------>TempFileDeleter!");
        while (iter.hasNext()) {
            String filename = (String) iter.next();
            //System.out.println("----->"+filename);
            File file=null;
            try
            {
               //file = new File(SystemConfig.getProperty("tempfile.path"), filename);
                file = new File(System.getProperty("java.io.tmpdir"), filename);
            }
            catch(Exception ge)
            {
                ge.printStackTrace();
            }
            finally
            {
	            if (file.exists()) {
	                file.delete();
	            }
            }
        }
        return;

    }

}
