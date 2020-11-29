package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SearchIniDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		InfoUtils infoUtils=new InfoUtils();
		//code=infoUtils.getCodeInifValue(this.getFrameconn(),this.userView);
		/*
	    this.getFormHM().put("code", code);
	    String privcode=this.userView.getManagePrivCode();
	    if(privcode==null||privcode.length()<=0)
	    	privcode="UN";
	    String codemess=AdminCode.getCodeName(privcode, code);
		this.getFormHM().put("codemess", codemess);*/
		this.getFormHM().put("isShowCondition", "none");
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);    	
		String pos_code_field="";		
		String fieldstr="";
	    RecordVo vo= ConstantParamter.getRealConstantVo("POS_CODE_FIELD");//岗位代码
	    if(vo!=null)
	    {
	    	pos_code_field=vo.getString("str_value");	    	
	    }
	    if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field)){
	    	String fieldarr[]=pos_code_field.split(",");
    		if(fieldarr!=null)
    		{
    			for(int i=0;i<fieldarr.length;i++)
    			{
    				String field=fieldarr[i];
    				if(fieldstr.indexOf(field)==-1){
    					 FieldItem fi = DataDictionary.getFieldItem(field);
    					if(fi!=null && "1".equalsIgnoreCase(fi.getUseflag()))
    						if(fieldstr.length()>1){
    							fieldstr=fieldstr+","+field;
    						}else{
    							fieldstr=field;
    						}
    				}
    			}
    			if(fieldstr.length()>4)
    				fieldstr=fieldstr+",";
    		}
	    }
	    fieldstr=fieldstr+"e01a1,e0122";
	    String ps_c_level_code="";
	    vo= ConstantParamter.getRealConstantVo("PS_C_CODE");//岗位级别代码
	    if(vo!=null)
	    {
	    	ps_c_level_code=vo.getString("str_value");	    	
	    }
	    if(ps_c_level_code!=null&&ps_c_level_code.length()>0&&!"#".equals(ps_c_level_code))
	    {
	    	ArrayList list=DataDictionary.getFieldList("k01", Constant.USED_FIELD_SET);
		    for(int i=0;list !=null && i<list.size();i++)
		    {
		    	FieldItem item=(FieldItem)list.get(i);
		    	String setid=item.getCodesetid();		    	
		    	if(setid!=null&&ps_c_level_code.equalsIgnoreCase(setid))
		    	{
		    		fieldstr=fieldstr+","+item.getItemid();
		    	}
		    }
	    }
	   // System.out.println(fieldstr);
	   // System.out.println(ps_c_level_code);
	    vo= ConstantParamter.getRealConstantVo("POST_MAINSET_FIELD");
	   // System.out.println(fieldstr);
	    if(vo!=null)
	    {
	    	String post_field=vo.getString("str_value");
	    	if(post_field!=null&&post_field.length()>0)
	    	{
	    		String fieldarr[]=post_field.split(",");
	    		if(fieldarr!=null)
	    		{
	    			for(int i=0;i<fieldarr.length;i++)
	    			{
	    				String field=fieldarr[i];
	    				if(fieldstr.indexOf(field)==-1){
	    					FieldItem item = DataDictionary.getFieldItem(field);
	    					if(item!=null&&item.getUseflag().length()>0&&!"0".equals(item.getUseflag()))
	    						fieldstr=fieldstr+","+field;
	    				}
	    			}
	    		}
	    		
	    	}
	    }	    
	    ArrayList fieldList=splitField(fieldstr);
		if(fieldList!=null)
		  this.getFormHM().put("fieldList", fieldList);
		else
		  this.getFormHM().put("fieldList", new ArrayList());
	    String cardID="";
	    vo= ConstantParamter.getRealConstantVo("ZP_POS_TEMPLATE");
	    if(vo!=null)
	    {
	    	cardID=vo.getString("str_value");	
	    	if(cardID==null||cardID.length()<=0|| "#".equals(cardID))
	    		cardID="-1";
	    }
	    this.getFormHM().put("fieldstr", fieldstr);
	    this.getFormHM().put("cardID", cardID);
	    this.getFormHM().put("orglike", "1");
		this.getFormHM().put("querylike", "0");
		this.getFormHM().put("query", "");
		this.getFormHM().put("selectfieldlist",infoUtils.selectField("3"));  
	}
	 /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone(); 
            	 if(item.getUseflag().length()>0&&!"0".equals(item.getUseflag())){
	            	 if("e01a1".equalsIgnoreCase(item_0.getItemid()))
	            		 item_0.setItemdesc("岗位名称");
	                 list.add(item_0);
            	 }
            }
           
        }
        return list;
    }

}
