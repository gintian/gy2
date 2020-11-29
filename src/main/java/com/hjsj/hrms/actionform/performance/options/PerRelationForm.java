package com.hjsj.hrms.actionform.performance.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:PerRelationForm.java</p>
 * <p> Description:考核关系</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-14 10:14:15</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class PerRelationForm extends FrameForm
{  
	
    ArrayList perObjects = new ArrayList();
    ArrayList perMainbodys = new ArrayList();
    ArrayList objectTypes = new ArrayList();  
    ArrayList bodyTypes = new ArrayList(); 
    ArrayList allBodyTypes = new ArrayList(); 
    ArrayList allObjectTypes = new ArrayList(); 
    
    private PaginationForm perObjectForm = new PaginationForm();
    private PaginationForm perMainbodyForm = new PaginationForm();
    private String a_code;
    private String paramStr;
    private String[] objectID = null;//删除时候用，用于复选框的标识
    private String[] mainbodyID = null;//删除时候用，用于复选框的标识
    private ArrayList khObjectList = new ArrayList();
    private String khObject;
    private ArrayList mainbodys = new ArrayList();//指定考核主体时候用
    private String khObjectCopyed;//复制主体时选中的考核对象
    private String left_fields[];
    private String right_fields[];
    private ArrayList leftlist = new ArrayList();
    private ArrayList selectedFieldList = new ArrayList();
    private String selfBodyId="";//本人主体类别ID
    private String objSelected;
    private String enableFlag;
    private HashMap joinedObjs = new HashMap();
    private ArrayList objectTypeList = new ArrayList(); // 对象类别
    
    @Override
    public void inPutTransHM()
    {	
    	this.getFormHM().put("objectTypeList",this.getObjectTypeList());
    	this.getFormHM().put("allObjectTypes",this.getAllObjectTypes());
    	this.getFormHM().put("allBodyTypes",this.getAllBodyTypes());
		this.getFormHM().put("perObjects",this.getPerObjects());
		this.getFormHM().put("perMainbodys",this.getPerMainbodys());
		this.getFormHM().put("objectTypes",this.getObjectTypes());
		this.getFormHM().put("bodyTypes",this.getBodyTypes());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("paramStr", this.getParamStr());
		this.getFormHM().put("objectID", this.getObjectID());
		this.getFormHM().put("mainbodyID", this.getMainbodyID());
		this.getFormHM().put("khObjectList", this.getKhObjectList());
		this.getFormHM().put("khObject", this.getKhObject());
		this.getFormHM().put("mainbodys", this.getMainbodys());
		this.getFormHM().put("khObjectCopyed", this.getKhObjectCopyed());
		this.getFormHM().put("left_fields", this.getLeft_fields());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("selectedFieldList", this.getSelectedFieldList());
		this.getFormHM().put("selfBodyId", this.getSelfBodyId());
		this.getFormHM().put("objSelected", this.getObjSelected());
		this.getFormHM().put("enableFlag", this.getEnableFlag());
		this.getFormHM().put("joinedObjs", this.getJoinedObjs());
    }

    @Override
    public void outPutFormHM()
    { 
	    this.setObjectTypeList((ArrayList) this.getFormHM().get("objectTypeList"));
	    this.setReturnflag((String)this.getFormHM().get("returnflag")); 
	    this.setAllObjectTypes((ArrayList)this.getFormHM().get("allObjectTypes"));
	    this.setAllBodyTypes((ArrayList)this.getFormHM().get("allBodyTypes"));
	    this.setJoinedObjs((HashMap)this.getFormHM().get("joinedObjs"));
		this.setPerObjects((ArrayList) this.getFormHM().get("perObjects"));
		this.setPerMainbodys((ArrayList) this.getFormHM().get("perMainbodys"));
		this.setObjectTypes((ArrayList) this.getFormHM().get("objectTypes"));
		this.setBodyTypes((ArrayList) this.getFormHM().get("bodyTypes"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		
		this.getPerMainbodyForm().setList((ArrayList) this.getFormHM().get("perMainbodys"));
		this.getPerObjectForm().setList((ArrayList) this.getFormHM().get("perObjects"));
		this.setParamStr((String)this.getFormHM().get("paramStr"));
		this.setObjectID((String[])this.getFormHM().get("objectID"));
		this.setMainbodyID((String[])this.getFormHM().get("mainbodyID"));
		this.setKhObject((String)this.getFormHM().get("khObject"));
		this.setKhObjectList((ArrayList) this.getFormHM().get("khObjectList"));
		this.setMainbodys((ArrayList) this.getFormHM().get("mainbodys"));
		this.setKhObjectCopyed((String)this.getFormHM().get("khObjectCopyed"));
		this.setLeftlist((ArrayList) this.getFormHM().get("leftlist"));
		this.setLeft_fields((String[]) this.getFormHM().get("left_fields"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setSelectedFieldList((ArrayList) this.getFormHM().get("selectedFieldList"));
		this.setSelfBodyId((String)this.getFormHM().get("selfBodyId"));
		this.setObjSelected((String)this.getFormHM().get("objSelected"));
		this.setEnableFlag((String)this.getFormHM().get("enableFlag"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/options/kh_relation".equals(arg0.getPath()) && arg1.getParameter("b_queryObj") != null)
		    {		
				if (this.perObjectForm.getPagination() != null)
				{
				    this.perObjectForm.getPagination().firstPage();
				}
		    }
		    if ("/performance/options/kh_relation/mainBodyList".equals(arg0.getPath()) && arg1.getParameter("b_queryBody") != null)
		    {		
				if (this.perMainbodyForm.getPagination() != null)
				{
				    this.perMainbodyForm.getPagination().firstPage();
				}
				/*
				 * 【6031】绩效管理/参数设置/考核关系，制定考核对象类别，每次都会显示刷新页面
				 * 绩效管理，参数设置，考核关系，选择考核对象类别后，使用ajax请求刷新下方页面，而本页面不刷新，
				 * 当点击下一页再返回本页时，考核对象类别未改变，数据库中数据已改变，但缓存中值未改，所以需要在
				 * form中手动改   jingq add 2014.12.30
				 */
				String typeid = arg1.getParameter("typeid");
				String objectid = arg1.getParameter("objectid");
				if(typeid!=null&&objectid!=null){
					ArrayList returnlist = new ArrayList();
					ArrayList list = this.perObjectForm.getList();
					for (int i = 0; i < list.size(); i++) {
						LazyDynaBean bean = (LazyDynaBean) list.get(i);
						if(bean.get("object_id").equals(objectid)){
							bean.set("obj_body_id", typeid);
						}
						returnlist.add(bean);
					}
					this.perObjectForm.setList(returnlist);
				}
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
    
    public ArrayList getObjectTypes()
    {
    
        return objectTypes;
    }

    public void setObjectTypes(ArrayList objectTypes)
    {
    
        this.objectTypes = objectTypes;
    }

    public ArrayList getPerMainbodys()
    {
    
        return perMainbodys;
    }

    public void setPerMainbodys(ArrayList perMainbodys)
    {
    
        this.perMainbodys = perMainbodys;
    }

    public ArrayList getPerObjects()
    {
    
        return perObjects;
    }

    public void setPerObjects(ArrayList perObjects)
    {
    
        this.perObjects = perObjects;
    }

    public String getA_code()
    {
    
        return a_code;
    }

    public void setA_code(String a_code)
    {
    
        this.a_code = a_code;
    }

    public PaginationForm getPerMainbodyForm()
    {
    
        return perMainbodyForm;
    }

    public void setPerMainbodyForm(PaginationForm perMainbodyForm)
    {
    
        this.perMainbodyForm = perMainbodyForm;
    }

    public PaginationForm getPerObjectForm()
    {
    
        return perObjectForm;
    }

    public void setPerObjectForm(PaginationForm perObjectForm)
    {
    
        this.perObjectForm = perObjectForm;
    }

    public String getParamStr()
    {
    
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }

    public String[] getMainbodyID()
    {
    
        return mainbodyID;
    }

    public void setMainbodyID(String[] mainbodyID)
    {
    
        this.mainbodyID = mainbodyID;
    }

    public String[] getObjectID()
    {
    
        return objectID;
    }

    public void setObjectID(String[] objectID)
    {
    
        this.objectID = objectID;
    }

    public String getKhObject()
    {
    
        return khObject;
    }

    public void setKhObject(String khObject)
    {
    
        this.khObject = khObject;
    }

    public ArrayList getKhObjectList()
    {
    
        return khObjectList;
    }

    public void setKhObjectList(ArrayList khObjectList)
    {
    
        this.khObjectList = khObjectList;
    }

    public ArrayList getMainbodys()
    {
    
        return mainbodys;
    }

    public void setMainbodys(ArrayList mainbodys)
    {
    
        this.mainbodys = mainbodys;
    }

    public String getKhObjectCopyed()
    {
    
        return khObjectCopyed;
    }

    public void setKhObjectCopyed(String khObjectCopyed)
    {
    
        this.khObjectCopyed = khObjectCopyed;
    }

    public String[] getLeft_fields()
    {
    
        return left_fields;
    }

    public void setLeft_fields(String[] left_fields)
    {
    
        this.left_fields = left_fields;
    }

    public ArrayList getLeftlist()
    {
    
        return leftlist;
    }

    public void setLeftlist(ArrayList leftlist)
    {
    
        this.leftlist = leftlist;
    }

    public String[] getRight_fields()
    {
    
        return right_fields;
    }

    public void setRight_fields(String[] right_fields)
    {
    
        this.right_fields = right_fields;
    }

    public ArrayList getSelectedFieldList()
    {
    
        return selectedFieldList;
    }

    public void setSelectedFieldList(ArrayList selectedFieldList)
    {
    
        this.selectedFieldList = selectedFieldList;
    }

    public ArrayList getBodyTypes()
    {
    
        return bodyTypes;
    }

    public void setBodyTypes(ArrayList bodyTypes)
    {
    
        this.bodyTypes = bodyTypes;
    }

    public String getSelfBodyId()
    {
    
        return selfBodyId;
    }

    public void setSelfBodyId(String selfBodyId)
    {
    
        this.selfBodyId = selfBodyId;
    }

    public String getObjSelected()
    {
    
        return objSelected;
    }

    public void setObjSelected(String objSelected)
    {
    
        this.objSelected = objSelected;
    }

    public String getEnableFlag()
    {
    
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag)
    {
    
        this.enableFlag = enableFlag;
    }

	public HashMap getJoinedObjs()
	{
		return joinedObjs;
	}

	public void setJoinedObjs(HashMap joinedObjs)
	{
		this.joinedObjs = joinedObjs;
	}

	public ArrayList getAllBodyTypes()
	{
		return allBodyTypes;
	}

	public void setAllBodyTypes(ArrayList allBodyTypes)
	{
		this.allBodyTypes = allBodyTypes;
	}

	public ArrayList getAllObjectTypes()
	{
		return allObjectTypes;
	}

	public void setAllObjectTypes(ArrayList allObjectTypes)
	{
		this.allObjectTypes = allObjectTypes;
	}

	public ArrayList getObjectTypeList() {
		return objectTypeList;
	}

	public void setObjectTypeList(ArrayList objectTypeList) {
		this.objectTypeList = objectTypeList;
	}
    
}
