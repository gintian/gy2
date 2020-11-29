package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


/**
 * <p>Title:IpAddrForm</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:5:10:58 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class IpAddrForm extends FrameForm {
    /**
     * 操作标识位，０００0 update ,1 new add
     * 
     */
    private String flag="0";
    /**当前页*/
    private int current=1;
    /**地址对象*/
    private RecordVo ip_vo=new RecordVo("ip_address");
    
    /**地址分页管理器*/
    private PaginationForm iplistform=new PaginationForm();
    
    /**
     * 
     */
    public IpAddrForm() {
        super();
        
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.getIplistform().setList((ArrayList)this.getFormHM().get("iplist"));
	    this.setIp_vo(((RecordVo)this.getFormHM().get("ip_vo"))); 
	    /**刷新禁用IP地址列表*/
	    this.getServlet().getServletContext().setAttribute("iplist",(ArrayList)this.getFormHM().get("addrlist") );
	    this.getServlet().getServletContext().setAttribute("iplist_v",(ArrayList)this.getFormHM().get("addrlist_v") );
	    /**重新定位到当前页*/
	    this.getIplistform().getPagination().gotoPage(current);
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("selectedlist",this.getIplistform().getSelectedList());
        this.getFormHM().put("ip_vo",this.getIp_vo());
	    this.getFormHM().put("flag",this.getFlag());        
    }

    public PaginationForm getIplistform() {
        return iplistform;
    }
    public void setIplistform(PaginationForm iplistform) {
        this.iplistform = iplistform;
    }

    public RecordVo getIp_vo() {
        return ip_vo;
    }
    public void setIp_vo(RecordVo ip_vo) {
        this.ip_vo = ip_vo;
    }
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**新建*/
        if("/system/security/ip_addr_manager".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
            this.getIp_vo().clearValues();            
            this.setFlag("1");
        }
        if("/system/security/ip_addr_maintenance".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null))
        {
            if(iplistform.getPagination()!=null)
            {
            	if("1".equals(this.flag))
            		iplistform.getPagination().lastPage();
                current=iplistform.getPagination().getCurrent(); 
            }
        }   
        if("/system/security/ip_addr_manager".equals(arg0.getPath())&&(arg1.getParameter("b_delete")!=null))
        {
            if(iplistform.getPagination()!=null)
            {
                current=iplistform.getPagination().getCurrent();
            }
        }         
        
        
        if("/system/security/ip_addr_maintenance".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            this.setFlag("0");
            if(iplistform.getPagination()!=null)
            {            
            	current=iplistform.getPagination().getCurrent();    
            }
        }
        return super.validate(arg0, arg1);
    }


}
