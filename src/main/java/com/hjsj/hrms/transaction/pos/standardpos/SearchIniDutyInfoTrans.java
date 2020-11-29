package com.hjsj.hrms.transaction.pos.standardpos;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class SearchIniDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String backdate = (String) this.getFormHM().get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		InfoUtils infoUtils=new InfoUtils();
		code=infoUtils.getCodeInifValue(this.getFrameconn(),this.userView);
	    this.getFormHM().put("code", code);
	    String privcode=this.userView.getManagePrivCode();
	    if(privcode==null||privcode.length()<=0)
	    	privcode="UN";
	    String codemess=AdminCode.getCodeName(privcode, code);
		this.getFormHM().put("codemess", codemess);
		this.getFormHM().put("isShowCondition", "none");
		/*Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel); */   	
		String fieldstr="h0100,codeitemdesc";
	    RecordVo vo= ConstantParamter.getRealConstantVo("SPOST_MAINSET_FIELD");
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
		this.getFormHM().put("selectfieldlist",infoUtils.selectField("4"));
		
		/****************是否显示岗位附件*************/
		String sql="select str_value from constant where upper(constant)='PS_CARD_ATTACH'";
		String value="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				value=this.frowset.getString("str_value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(value==null|| "".equals(value))
			 value="false";
	    this.getFormHM().put("ps_card_attach", value);
		
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		 ArrayList dblist=userView.getPrivDbList();
	        StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname where pre in (");
	        String userbase="";
	        if(dblist.size()>0){
	        	userbase=dblist.get(0).toString();      
	        }
	        else
	        	userbase="usr";
	        for(int i=0;i<dblist.size();i++)
	        {
	        	
	        	if(i!=0)
	                cond.append(",");
	            cond.append("'");
	            cond.append((String)dblist.get(i));
	            cond.append("'");
	        }
	        if(dblist.size()==0)
	            cond.append("''");
	        cond.append(")");
	        cond.append(" order by dbid");
	        /**应用库前缀过滤条件*/
	        cat.debug("-----userbase------>" + userbase);
	        this.getFormHM().put("userbase",userbase);
	        this.getFormHM().put("dbcond",cond.toString());
	        this.getFormHM().put("setprv",getEditSetPriv(infoSetList,"A01"));
	        this.getFormHM().put("backdate", backdate);
	}
	 /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        FieldItem fielditem=new FieldItem();
        fielditem.setItemid("codeitemdesc");
        fielditem.setCodesetid("");
        fielditem.setItemdesc("名称");
        fielditem.setItemtype("A");
        fielditem.setFieldsetid("codeitem");
        fielditem.setUseflag("1");
        fielditem.setItemlength(30);
        fielditem.setDisplaywidth(30);
        list.add(fielditem);
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
	                 list.add(item_0);
            	 }
            }
           
        }
        return list;
    }

    /**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,String setname)
	{
		String setpriv="1";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
	  return setpriv;	
	}
}
