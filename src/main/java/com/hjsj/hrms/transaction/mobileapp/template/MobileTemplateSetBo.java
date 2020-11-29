package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.transaction.mobileapp.template.util.Mcell;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.valueobject.UserView;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 类名称:TemplateSetBo
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-11-28 下午02:40:34 
 * 修改人:xucs
 * 修改时间:2013-11-28 下午02:40:34
 * 修改备注:
 * @version
 *
 */
public class MobileTemplateSetBo extends Mcell{
   
	private static final long serialVersionUID = 1L;
	
	private UserView userview = null;
    private Connection conn = null;
    private int Chgstate;
    private boolean subflag =false;
    private boolean yneed = false;
    private String xml_param;
    private String sub_domain_id="";
    private ArrayList suTableList = new ArrayList();
    private RecordVo varVo;
    private String fields;
    /**单位、部门、岗位是否按管理范围控制*/
	private boolean bLimitManagePriv=false;
    public boolean isbLimitManagePriv() {
		return bLimitManagePriv;
	}

	public void setbLimitManagePriv(boolean bLimitManagePriv) {
		this.bLimitManagePriv = bLimitManagePriv;
	}

	/**宽度及高度,整个表格的*/
    private Rectangle rect=new Rectangle(0,0,0,0);
    
    public MobileTemplateSetBo (Connection conn,UserView userview){
        this.conn=conn;
        this.userview=userview;
    }
    
    public String getFields() {
        return fields;
    }
    public void setFields(String fields) {
        this.fields = fields;
    }
    public RecordVo getVarVo() {
        return varVo;
    }
    public void setVarVo(RecordVo varVo) {
        this.varVo = varVo;
    }
    public int getChgstate() {
        return Chgstate;
    }
    public void setChgstate(int chgstate) {
        Chgstate = chgstate;
    }
    public boolean isSubflag() {
        return subflag;
    }
    public void setSubflag(boolean subflag) {
        this.subflag = subflag;
    }
    public boolean isYneed() {
        return yneed;
    }
    public void setYneed(boolean yneed) {
        this.yneed = yneed;
    }
    public String getXml_param() {
        return xml_param;
    }
    public void setXml_param(String xmlParam) {
        xml_param = xmlParam;
    }
    public String getSub_domain_id() {
        return sub_domain_id;
    }
    public void setSub_domain_id(String subDomainId) {
        sub_domain_id = subDomainId;
    }
    public Rectangle getRect() {
        return rect;
    }
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
    public ArrayList getSuTableList() {
        return suTableList;
    }
    public void setSuTableList(ArrayList suTableList) {
        this.suTableList = suTableList;
    }

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
    
    
}
