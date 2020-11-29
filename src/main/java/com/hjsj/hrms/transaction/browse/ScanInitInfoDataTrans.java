package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanInitInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		
		ArrayList dbaselist=userView.getPrivDbList();    
		if(dbaselist==null||dbaselist.size()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有授权人员库！","",""));
		InfoUtils infoUtils=new InfoUtils();
		String cardid=infoUtils.searchCard("1",this.getFrameconn());
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String num_per=sysbo.getValue(Sys_Oth_Parameter.NUM_PER_PAGE);
		if(num_per==null||num_per.length()<=0)
            num_per="21";		
		String code=(String)this.getFormHM().get("code");		
		if(code==null||code.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("得到机构单元编号为空，错误！"));
		String sql="select codesetid,parentid from organization where codeitemid='"+code+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String kind=(String)hm.get("kind");
		hm.remove("kind");
		String orgtype="org";		
		String parentid="";
		String codesetid="";		
		boolean isOrg=false;
		if(!"H".equalsIgnoreCase(kind)){
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				codesetid=this.frowset.getString("codesetid");
				parentid=this.frowset.getString("parentid");
				kind=infoUtils.getKindFormCodeSetId(codesetid);
				orgtype="org";
				isOrg=true;
			}
			if(!isOrg)
			{
				sql="select codesetid,parentid from vorganization where codeitemid='"+code+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					codesetid=this.frowset.getString("codesetid");
					kind=infoUtils.getKindFormCodeSetId(codesetid);
					parentid=this.frowset.getString("parentid");
					orgtype="vorg";					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("parentid", parentid);
		this.getFormHM().put("codesetid", codesetid);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("code", code);
		}				
		this.getFormHM().put("num_per_page",num_per);		
		this.getFormHM().put("cardid",cardid);
		this.getFormHM().put("isShowCondition", "none");
		this.getFormHM().put("scanfieldlist",infoUtils.selectField("1","a0101",this.getFrameconn()));  			
		this.getFormHM().put("querylike", "0");
		this.getFormHM().put("query", "");
		this.getFormHM().put("orglike", "0");
		  //System.out.println(wheresql.toString());
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
		String codemess=AdminCode.getCodeName("UN", code);
		 if(codemess==null||codemess.length()<=0)
		   {
			   CodeItem codeitem=AdminCode.getCode("UM", code,Integer.parseInt(uplevel));
			   if(codeitem!=null)
			      codemess=codeitem.getCodename();
		   } 
		if(codemess==null||codemess.length()<=0)
		{
			   CodeItem codeitem=AdminCode.getCode("@K", code,Integer.parseInt(uplevel));
			   if(codeitem!=null)
			      codemess=codeitem.getCodename();
		 }  
		this.getFormHM().put("codemess", codemess);	
		String unit_code_field="";	
	    RecordVo vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");
	    String unit_code_value="";
	    
	    if(vo!=null)
	    {
	    	unit_code_field=vo.getString("str_value");
	    	if(unit_code_field!=null&&unit_code_field.length()>0)
	    	{
	    		FieldItem item=DataDictionary.getFieldItem(unit_code_field);
	    		if(item!=null)
	    		{
	    			sql="select "+item.getItemid()+" from b01 where b0110='"+code+"'";	    			
	    			try {
						this.frowset=dao.search(sql);
						if(this.frowset.next())
						{
							unit_code_value=this.frowset.getString(item.getItemid());
							if(!"0".equals(item.getCodesetid()))
							{
								unit_code_field=AdminCode.getCodeName(item.getCodesetid(), unit_code_field);
							}
						}
							
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
	    }	    
	    this.getFormHM().put("unit_code_mess", unit_code_value);
	    ArrayList dblist=userView.getPrivDbList();  
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase=(String)this.getFormHM().get("userbase");
        if(userbase==null||userbase.length()<=0)
        {
        	if(dblist.size()>0){
            	userbase=dblist.get(0).toString();      
            }
            else
            	userbase="usr";
        }        
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
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("orgtype", orgtype);
	}
	

}
