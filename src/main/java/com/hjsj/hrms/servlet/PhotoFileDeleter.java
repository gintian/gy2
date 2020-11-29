package com.hjsj.hrms.servlet;


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
 * <p>create time:Jun 15, 2005:3:55:11 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PhotoFileDeleter implements HttpSessionBindingListener,Serializable {

    /**
     * 
     */
    public PhotoFileDeleter() {
        super();
        // TODO Auto-generated constructor stub
    }
    /** The temp names. */
    private List tempfileNames = new java.util.ArrayList();    
    /**
     * 
     */

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
        while (iter.hasNext()) {
            String filename = (String) iter.next();
            File file=null;
            file = new File(System.getProperty("java.io.tmpdir"), filename);               
	        if (file.exists())
	            file.delete();
        }
        return;

    }



}
