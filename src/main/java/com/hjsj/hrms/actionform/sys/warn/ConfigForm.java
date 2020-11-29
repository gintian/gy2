package com.hjsj.hrms.actionform.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.transaction.sys.warn.DomainTool;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Title:WarnSettingForm
 * Description:
 * Company:hjsj
 * create time:Jun 6, 2006:5:10:58 PM
 * @author zhouhaimao
 * @version 1.0
 *  
 */
public class ConfigForm extends FrameForm implements IConstant {

	/** XML 解析器 */
	ConfigCtrlInfoVO xmlCtrlVo = null;   //预警控制
    /** 当前页 */
    private int current = 1;

    /** 操作标识位，0 update ,1 new add*/
    private String flag = Key_Flag_Update;
    
    /** 地址分页管理器 */
    private PaginationForm pageListForm = new PaginationForm();

    /** 地址对象 */
    private DynaBean dynaBean = new LazyDynaBean();
    
    
    //预警显示动态列
    private String strsql;
    private String columns;
    private ArrayList columnList = new ArrayList();
    private String order="";
    //预警结果显示查询使用
    private String dbPre; //人员库前缀
    private ArrayList dblist=new ArrayList(); //人员库列表
    private String wid; //预警ID号
    private ArrayList perlist=new ArrayList();
    private String bs_tree;
    private String select_id;
    private ArrayList tenplatelist=new ArrayList();
    private String tenplateId="";
    private String edition;//版本控制
    private String uplevel;
    private String warntype;
    private String encodeSql;
    private ArrayList tranfieldsetlist=new ArrayList();
    private String fieldItemclumn="";
    
    //liuy 2014-8-19  修改预警提示详情返回  begin
    private String returnvalue="";//控制预警提示详情返回
    public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	//liuy 2014-8-19  修改预警提示详情返回  end
	
	public ArrayList getTranfieldsetlist() {
		return tranfieldsetlist;
	}

	public void setTranfieldsetlist(ArrayList tranfieldsetlist) {
		this.tranfieldsetlist = tranfieldsetlist;
	}

	public String getWarntype() {
		return warntype;
	}

	public void setWarntype(String warntype) {
		this.warntype = warntype;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public ArrayList getTenplatelist() {
		return tenplatelist;
	}
    public ArrayList emailtemplateList=new ArrayList();
	public void setTenplatelist(ArrayList tenplatelist) {
		this.tenplatelist = tenplatelist;
	}

	public String getSelect_id() {
		return select_id;
	}

	public void setSelect_id(String select_id) {
		this.select_id = select_id;
	}

	public String getBs_tree() {
		return bs_tree;
	}

	public void setBs_tree(String bs_tree) {
		this.bs_tree = bs_tree;
	}

	public ArrayList getPerlist() {
		return perlist;
	}

	public void setPerlist(ArrayList perlist) {
		this.perlist = perlist;
	}
  
	/*
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
    	
        getFormHM().put(Key_List_SelectedVo, getPageListForm().getSelectedList());//选中列表
        getFormHM().put(Key_RecorderVo, getDynaBean());      
        getFormHM().put(Key_Flag, getFlag()); //操作标识      
        getFormHM().put(Key_HrpWarn_Ctrl_VO, getXmlCtrlVo()); //预警控制对象
        
        //清空显示列表SQL语句        
        this.getFormHM().put("strsql","");
        //this.getFormHM().put("dbPre", this.getDbPre());
        this.getFormHM().put("returnvalue",this.getReturnvalue());
    }

    /*
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
    	
        getPageListForm().setList((ArrayList) this.getFormHM().get( Key_List_Query_FormVo ));
        
        this.setEncodeSql((String)this.getFormHM().get("encodeSql"));
        setDynaBean(((DynaBean) this.getFormHM().get( Key_RecorderVo )));
        
        setXmlCtrlVo( (ConfigCtrlInfoVO)getDynaBean().get(Key_HrpWarn_Ctrl_VO) );
        this.setPerlist((ArrayList)this.getFormHM().get("perlist"));

        /** 重新定位到当前页 */
        //getPageListForm().getPagination().gotoPage(current);
        
        //预警显示动态列
        setStrsql((String)this.getFormHM().get("strsql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setColumnList((ArrayList)this.getFormHM().get("columnList"));
        this.setOrder((String)this.getFormHM().get("order"));
        //预警显示查询
        this.setDblist((ArrayList)this.getFormHM().get("dblist"));
        this.setWid((String)this.getFormHM().get("wid"));
        this.setBs_tree((String)this.getFormHM().get("bs_tree"));
        this.setSelect_id((String)this.getFormHM().get("select_id"));
        this.setTenplatelist((ArrayList)this.getFormHM().get("tenplatelist"));
        this.setEmailtemplateList((ArrayList)this.getFormHM().get("emailtemplateList"));
        this.setEdition((String)this.getFormHM().get("edition"));
        //this.setDbPre((String)this.getFormHM().get("dbPre"));
        this.setUplevel((String)this.getFormHM().get("uplevel"));
        this.setWarntype((String)this.getFormHM().get("warntype"));
        this.setTranfieldsetlist((ArrayList)this.getFormHM().get("tranfieldsetlist"));     
        this.setFlag((String)this.getFormHM().get("flag"));
        this.setFieldItemclumn((String)this.getFormHM().get("fieldItemclumn"));
        this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
    }

   
    /**
     * 控制程序业务逻辑 flag
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        /*if(this.getPagination()!=null)
            this.getPagination().firstPage();*///?

        if(mapping.getPath().equals(Key_URL_Manage)&&request.getParameter(Key_Button_Add)!=null){
        	
        	//System.out.println("新建预警条件........................");
        
        	DynaBean newBean = new LazyDynaBean();
        	this.setDynaBean( newBean );
        	
        	UserView uv = (UserView) request.getSession().getAttribute(WebConstant.userView);
        	String strDomain = "UN";
        	if( !uv.isSuper_admin()){//非管理员
        		strDomain =strDomain+ uv.getUserOrgId();
        		//uv.getManagePrivCode()+uv.getManagePrivCodeValue();
        	}
        	
        	
        	// 只在新建的时候保存b0110，因为超级用户可能修改单位的设置，但此时不能修改为超级用户的单位
        	// 所以只是一次性新增的时候设置该值
        	newBean.set(Key_HrpWarn_FieldName_Org,strDomain);
        	newBean.set(Key_HrpWarn_FieldName_Warntyp, "0");
        	ConfigCtrlInfoVO ctrlVo = new ConfigCtrlInfoVO("");
        	newBean.set(Key_HrpWarn_Ctrl_VO,ctrlVo);
        	ctrlVo.setStrRule("0");//新增时默认为简单
        	ctrlVo.setStrDomain(strDomain);
        	DomainTool tool = new DomainTool();
        	newBean.set(Key_Domain_Names, tool.getDomainNames(ctrlVo.getStrDomain()));
        	
            this.setFlag("1");
            
        }
        
        
        if(mapping.getPath().equals(Key_URL_Maintenace)&&(request.getParameter(Key_Button_Save)!=null))
        {
            if( getPageListForm().getPagination()!=null)
            {
            	if("1".equals(this.flag))
            		getPageListForm().getPagination().firstPage();
                //current=getPageListForm().getPagination().getCurrent(); 
            	//current=1;
            }
        }   
        if(mapping.getPath().equals(Key_URL_Manage)&&(request.getParameter(Key_Button_Delete)!=null))
        {
            if(getPageListForm().getPagination()!=null)
            {
                current=getPageListForm().getPagination().getCurrent();
            }
            
        }         
        
        if("/system/warn/result_manager".equals(mapping.getPath())&&(request.getParameter("b_query")!=null))
        {
            if(getPageListForm().getPagination()!=null)
            {
                current=getPageListForm().getPagination().getCurrent();
            }
            /*this.setDbPre("");
            this.getFormHM().put("dbPre", "");*/
            //if(this.getPagination()!=null)
    	         // this.getPagination().firstPage();//?
            if(request.getParameter("warn_wid")!=null&&this.getPagination()!=null){
            	this.getPagination().firstPage();
            }
        }   
        if("/system/warn/config_manager".equals(mapping.getPath())&&(request.getParameter("b_query")!=null))
        {
            if(getPageListForm().getPagination()!=null)
            {
                //current=getPageListForm().getPagination().getCurrent();
                //getPageListForm().getPagination().firstPage();
            	//【7253】修改预警设置返回携带参数noreset=1，用于判断是否重新定位到首页  jingq upd 2015.01.30
            	String noreset = request.getParameter("noreset");
            	if(!"1".equals(noreset)){
            		current=1;
            		getPageListForm().getPagination().gotoPage(current);
            	}
            }
           
        } 
        if("/system/warn/myresult_manager".equals(mapping.getPath())&&(request.getParameter("b_query")!=null))
        {
            if(this.getPagination()!=null)
            {
                //current=getPageListForm().getPagination().getCurrent();
                //getPageListForm().getPagination().firstPage();
                current=1;
                getPagination().setCurrent(current);
            }
           
        } 
        if(mapping.getPath().equals(Key_URL_Maintenace)&&request.getParameter(Key_Button_Query)!=null)
        {
            this.setFlag("0");
            if(getPageListForm().getPagination()!=null)
            {            
            	current=getPageListForm().getPagination().getCurrent();    
            }
        }
        return super.validate(mapping, request);        
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @param pageListForm
     *            The pageListForm to set.
     */
    public void setPageListForm(PaginationForm pageListForm) {
        this.pageListForm = pageListForm;
    }

    /**
     * @param recordVO
     *            The recordVO to set.
     */
    public void setDynaBean(DynaBean dynaBean) {
        this.dynaBean = dynaBean;
    }

	public ConfigCtrlInfoVO getXmlCtrlVo() {
		if( xmlCtrlVo == null ){
			xmlCtrlVo = new ConfigCtrlInfoVO( (String)dynaBean.get( Key_HrpWarn_FieldName_CtrlInf ));			
		}
		return xmlCtrlVo;
	}

	public void setXmlCtrlVo(ConfigCtrlInfoVO xmlCtrlVo) {
		this.xmlCtrlVo = xmlCtrlVo;
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		getXmlCtrlVo().setStrEmail("false");
		getXmlCtrlVo().setStrMobile("false");
		getXmlCtrlVo().setStrEveryone("false");
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public ArrayList getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}

	/**
     * creator
     */
    public ConfigForm() {
        super();

    }

	public String getFlag() {
        return flag;
    }

    /**
     * @return Returns the pageListForm.
     */
    public PaginationForm getPageListForm() {
        return pageListForm;
    }

    /**
     * @return Returns the recordVO.
     */
    public DynaBean getDynaBean() {
        return dynaBean;
    }
	
    
    public String getDbPre() {
		return dbPre;
	}

	public void setDbPre(String dbPre) {
		this.dbPre = dbPre;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getTenplateId() {
		return tenplateId;
	}

	public void setTenplateId(String tenplateId) {
		this.tenplateId = tenplateId;
	}

	public ArrayList getEmailtemplateList() {
		return emailtemplateList;
	}

	public void setEmailtemplateList(ArrayList emailtemplateList) {
		this.emailtemplateList = emailtemplateList;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEncodeSql() {
		return encodeSql;
	}

	public void setEncodeSql(String encodeSql) {
		this.encodeSql = encodeSql;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFieldItemclumn() {
		return fieldItemclumn;
	}

	public void setFieldItemclumn(String fieldItemclumn) {
		this.fieldItemclumn = fieldItemclumn;
	}

	
}