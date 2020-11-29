
/*
 * Created on 2005-7-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.param;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FriendForm extends FrameForm {

		/**新建及编辑功能控制*/
        private String flag="0";
        private FormFile file = null;
        String userAdmin="false";
	    /**友情链接对象*/
	    private RecordVo friendvo=new RecordVo("hr_friend_website");
	    /**友情链接对象列表*/
	    private PaginationForm friendForm=new PaginationForm();     
	    public FormFile getFile() {
			return this.file;
		}

		public void setFile(FormFile file) {
			this.file = file;
		}
		private String fname;

		public String getFname() {
			return this.fname;
		}

		public void setFname(String fname) {
			this.fname = fname;
		}
	    @Override
        public void outPutFormHM() {
	        this.setFriendvo((RecordVo)this.getFormHM().get("friendTb"));
	        this.getFriendForm().setList((ArrayList)this.getFormHM().get("friendlist"));
	        this.setUserAdmin(Boolean.toString(this.userView.isSuper_admin()));
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
		    this.getFormHM().put("selectedlist",(ArrayList)this.getFriendForm().getSelectedList());
	        this.getFormHM().put("friendvo",this.getFriendvo());
	        this.getFormHM().put("friendTb",this.getFriendvo());
	        this.getFormHM().put("flag",this.getFlag());  
	        this.getFormHM().put("userAdmin",this.getUserAdmin());
	        this.getFormHM().put("file",this.getFile());
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	    public PaginationForm getFriendForm() {
	        return friendForm;
	    }
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	    public void setFriendForm(PaginationForm friendForm) {
	        this.friendForm = friendForm;
	    }
	    /**
	     * @return Returns the consulantvo.
	     */
	    public RecordVo getFriendvo() {
	        return friendvo;
	    }
	    /**
	     * @param proposevo The consulantvo to set.
	     */
	    public void setFriendvo(RecordVo friendvo) {
	        this.friendvo = friendvo;
	    }
	    /* 
	     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
        public void reset(ActionMapping arg0, HttpServletRequest arg1) {
	        super.reset(arg0, arg1);
	    }
	    /* 
	     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	   
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	        /**新建*/
	        if("/selfservice/param/friend".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	            this.setFlag("1");
	            this.getFormHM().put("flag", this.getFlag());
	            this.getFriendvo().clearValues();
	        }
	        /**编辑*/
	        if("/selfservice/param/addfriend".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setFlag("0");
	            this.getFormHM().put("flag", this.getFlag());
	        }        
	        return super.validate(arg0, arg1);
	    }
	    public String getFlag() {
	        return flag;
	    }
	    
	    /**
	     * @param flag The flag to set.
	     */
	   
	    public void setFlag(String flag) {
	        this.flag = flag;
	    }
		/**
		 * @return 返回 userAdmin。
		 */
		public String getUserAdmin() {
			return userAdmin;
		}
		/**
		 * @param userAdmin 要设置的 userAdmin。
		 */
		public void setUserAdmin(String userAdmin) {
			this.userAdmin = userAdmin;
		}

	
}
