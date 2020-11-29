/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Owner
 *
 */
public class SearchBrowseFieldsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		//HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		ArrayList dbaselist=userView.getPrivDbList();    
		if(dbaselist==null||dbaselist.size()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有授权人员库！","",""));
		DbWizard dbWizard =new DbWizard(this.frameconn);
		String tablename=userbase+"A01";
		if(!dbWizard.isExistTable(tablename,false))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","人员信息集未构库！","",""));
		}
		
		String personsort=(String)this.getFormHM().get("personsort");
		String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String strwhere="";	
		String kind="";
		String code="";
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by a0000");
		if(!userView.isSuper_admin())
		{
	           String expr="1";
	           String factor="";
	           code=userView.getManagePrivCodeValue();
			if("UN".equals(userView.getManagePrivCode()))
			{
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`B0110=`";
				  expr="1+2";
				}
			}
			else if("UM".equals(userView.getManagePrivCode()))
			{
				factor="E0122="; 
			    kind="1";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`E0122=`";
				  expr="1+2";
				}
			}
			else if("@K".equals(userView.getManagePrivCode()))
			{
				factor="E01A1=";
				kind="0";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`E01A1=`";
				  expr="1+2";
				}
			}
			else
			{
				 expr="1+2";
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`B0110=`";
			}			
			 ArrayList fieldlist=new ArrayList();
		        try
		        {        
			        
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0)
					   strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
		            StringBuffer sql=new StringBuffer();
		            sql.append("from "+userbase+"A01 where a0100 in(select "+userbase+"A01.a0100 "+strwhere+")");
		            if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
			    	{
		            	sql.append(" or "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where "+part_unit+"='"+this.userView.getManagePrivCodeValue()+"')");
			    	}
		            strwhere=sql.toString();
		            //System.out.println(strwhere);
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
	
		}else{
			StringBuffer wheresql=new StringBuffer();
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 where (1=1 ");
			if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0)
			{
				wheresql.append("  and (");
			    wheresql.append(personsortfield + "='" + personsort + "')");
			}		
			wheresql.append(")");			
			strwhere=wheresql.toString();
		}
		//System.out.println("strwhere" + strwhere);
		StringBuffer columns=new StringBuffer();
		String setprv=getEditSetPriv("A01");
		this.getFormHM().put("setprv",setprv);
		StringBuffer strsql=new StringBuffer();
		strsql.append("select "+userbase+"A01.a0000 a0000,");
	    strsql.append(userbase);
		strsql.append("A01.A0100");
		columns.append("A0100");
//	    strsql.append(userbase);
//		strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName");
//		columns.append("A0100,B0110,E0122,E01A1,A0101,UserName");
		
	
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		
//		fieldstr=fieldstr.replaceFirst(","," ");
		this.getFormHM().put("browsefields","");
		if(fieldstr!=null&&fieldstr.length()>0){
			fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
			if(fieldstr!=null&&fieldstr.length()>0){
				fieldstr = fieldstr /*+ ",a0101"*/;
				strsql.append(fieldstr);
				columns.append(fieldstr);
			}else
			{
				fieldstr=",B0110,E0122,E01A1,A0101,UserName";
				strsql.append(",B0110,E0122,E01A1,A0101,UserName");
				columns.append(",B0110,E0122,E01A1,A0101,UserName");
			}
		
		}else{
			fieldstr=",B0110,E0122,E01A1,A0101,UserName";
			strsql.append(",B0110,E0122,E01A1,A0101,UserName");
			columns.append(",B0110,E0122,E01A1,A0101,UserName");
		}
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String flag=(String)hm.get("flag");
    	
    	ArrayList infoFieldList=new ArrayList();
    	GzDataMaintBo gzDataMaintBo=new GzDataMaintBo(this.getFrameconn());
		/*if("infoself".equalsIgnoreCase(flag))			
		{
			infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性		
			
		}
	    else
		{	
			infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性	    	
		}*/
    	FieldSet fieldset=DataDictionary.getFieldSetVo("A01");
    	infoFieldList=gzDataMaintBo.fieldItemList(fieldset);
		ArrayList fields=new ArrayList();
//
		String[] f=fieldstr.split(",");
		for(int i=0;i<f.length;i++){
			for(int j=0;j<infoFieldList.size();j++){
				FieldItem fieldItem_O=(FieldItem)infoFieldList.get(j);
				FieldItem fieldItem=(FieldItem)fieldItem_O.clone();
				fieldItem.setDisplaywidth(fieldItem.getDisplaywidth()*12);					
				//if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
				if(!"0".equals(this.userView.analyseFieldPriv(fieldItem.getItemid())))
				{
					if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
					{
						fields.add(fieldItem);
					}
				}				
			}
		}
//		
		
		
//		for(int i=0;i<infoFieldList.size();i++)
//		{
//			FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
//			if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
//			{
//				if(fieldstr.toUpperCase().indexOf(fieldItem.getItemid().toUpperCase())!=-1)
//				{
//					fields.add(fieldItem);
//				}
//			}
//		}
		
		this.getFormHM().put("browsefields",fields);		
	    this.getFormHM().put("strsql",strsql.toString());
	    
		this.getFormHM().put("cond_str",strwhere); 		
		this.getFormHM().put("code",code);
		this.getFormHM().put("where_n", "");
		this.getFormHM().put("orgtype", "");
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());
		this.getFormHM().put("columns",columns.toString());	
		this.getFormHM().put("personsort", personsort);
		String cardid=searchCard("1");
		this.getFormHM().put("cardid",cardid);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String num_per=sysbo.getValue(Sys_Oth_Parameter.NUM_PER_PAGE);
		if(num_per==null||num_per.length()<=0)
            num_per="21";
		this.getFormHM().put("num_per_page",num_per);
		String roster=sysbo.getValue(Sys_Oth_Parameter.COMMON_ROSTER);
		roster=roster!=null&&roster.trim().length()>0&&!"#".equals(roster)?roster:"no";
		if(!this.userView.isSuper_admin()){
			String temp=this.userView.getResourceString(4);
			if(temp.indexOf(roster)<0){
				roster="no";
			}
		}
		this.getFormHM().put("roster",roster); 
		String mustername = "";
		/*if(!roster.equalsIgnoreCase("no"))
			mustername = inportData(userbase,roster,userView.getManagePrivCodeValue());*/
		/*if(mustername==null||mustername.length()<=0)
			this.getFormHM().put("roster","no"); */
		this.getFormHM().put("mustername",mustername);
		//人员分类条件
		List condlist=getCondList(sysbo);
		this.getFormHM().put("condlist", condlist);
		this.getFormHM().put("stock_cond", "");
	}
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(String setname)
	{
		String setpriv=userView.analyseTablePriv(setname);
		return setpriv;
	}
	 /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    private String searchCard(String infortype)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
    private String  inportData(String dbpre,String thetabid,String a_code){
    	String mustername = "";
    	MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
    	try {
			if(musterbo.createMusterTempTable("1",dbpre,thetabid,this.userView.getUserName(),"0",a_code,""))
				mustername=musterbo.getTableName("1",dbpre,thetabid,this.userView.getUserName());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    this.frowset=dao.search(" select 1 from  lname where tabid="+thetabid);
			if(!this.frowset.next())
				mustername="";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return mustername;
    }
    /**人员分类 常用查询*/
    private List getCondList(Sys_Oth_Parameter sysbo){
    	List condlist=new ArrayList();
    	try {
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
			String gquery_cond = sysbo.getValue(Sys_Oth_Parameter.GQUERY_COND,"value");
			if(gquery_cond==null)
				gquery_cond="";
			if(gquery_cond.trim().length()<=0)
				return condlist;
			String[] gquery_conds=gquery_cond.split(",");
			if(gquery_conds.length<=0||gquery_conds[0].trim().length()<1)
				return condlist;
			StringBuffer sql=new StringBuffer();
			StringBuffer wheresql=new StringBuffer();
			sql.append("select id,name from lexpr where id in(");
			for (int i = 0; i < gquery_conds.length; i++) {
				if(gquery_conds[i]!=null&&gquery_conds[i].length()>0){
					if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,gquery_conds[i])))
	                	continue;
					wheresql.append(gquery_conds[i]+",");
				}
			}
			if(wheresql.length()>0)
				   wheresql.setLength(wheresql.length()-1);
				else
				   return condlist;
			sql.append(wheresql.toString()+")");
			sql.append(" order by norder");
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{	
				CommonData cd = new CommonData(this.frowset.getString("id"),this.frowset.getString("name"));
				condlist.add(cd);
			}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
		return condlist;
    }
}
