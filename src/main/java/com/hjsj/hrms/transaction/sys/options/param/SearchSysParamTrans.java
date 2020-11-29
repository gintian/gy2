package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.options.otherparam.Sys_OTH_PARAMSqlStr;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchSysParamTrans  extends IBusiness{
	
	private ArrayList getOperationList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rset= null;
		try
		{
			String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
			StringBuffer buf=new StringBuffer();
			buf.append("select operationcode,operationname from operation where ");
			buf.append(Sql_switcher.length("operationcode"));
			buf.append("=4 and operationtype=10 and "+staitic_+"=1");
			rset=dao.search(buf.toString());
			CommonData vo = new CommonData();
			vo.setDataName("");
			vo.setDataValue("");
			list.add(vo);
			while(rset.next())
			{
				vo = new CommonData();
				vo.setDataName(rset.getString("operationname"));
				vo.setDataValue(rset.getString("operationcode"));
				list.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeResource(rset);
		}
		return list;
	}
	/**
	 * 取得人员子集列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getSetList()throws GeneralException
	{
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    try
	    {
	        fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		    for(int i=0;i<fieldsetlist.size();i++)
		    {
		      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
		      if("A00".equals(fieldset.getFieldsetid())|| "A01".equals(fieldset.getFieldsetid()))
		    	  continue;
		      CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
	          list.add(dataobj);
		    }
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }
	    return list;
	}
	/**
     * @throws GeneralException  
     * @Description: 获取证件类型可选列表
     * @return list
     * @throws 
    */
    private ArrayList getIdTypeList() throws GeneralException {
    	ArrayList<CommonData> list = new ArrayList<CommonData>();
        try {
        	ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("A01", 1);
        	CommonData data = new CommonData("","请选择…");
			list.add(data);
			for (FieldItem fieldItem : fieldList) {
				String codesetid = fieldItem.getCodesetid();
        		if("A".equals(fieldItem.getItemtype())&& !"0".equals(codesetid)&&!"UM".equals(codesetid)&&!"UN".equals(codesetid)&&!"@K".equals(codesetid)) {
					data = new CommonData(fieldItem.getItemid(),fieldItem.getItemid().toUpperCase()+ ":" +fieldItem.getItemdesc());
					list.add(data);
				}
			}
        } 
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
	public void execute()throws GeneralException
	{
		HashMap hm=this.getFormHM();
		ContentDAO dao =new ContentDAO(this.getFrameconn());
//		 Sys_Oth_Parameter sys_param=new Sys_Oth_Parameter();
//		 String xmlContent=sys_param.search_SYS_PARAMETER(this.getFrameconn());
//		 String typeid=sys_param.getUnittype(xmlContent);
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String typeid=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		 String setname=sysbo.getValue(Sys_Oth_Parameter.GOBROADSUBSET,"setname");
		 String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");
		 String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
		 String id_type = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE);
		 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		 String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
		 String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		 String transfer = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"transfer");
		 String combine = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"combine");
		 String bolish = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"bolish");
		 String delete = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"delete");
		 if(chk==null)
			 chk="";
		 else
			 chk=chk.toLowerCase();
		 if(onlyname==null)
			 onlyname = "";
		 else
			 onlyname=onlyname.toLowerCase();
		 if(chkvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 uniquenessvalid="";
		 StringBuffer sql = new StringBuffer("select tabid,name from template_table where tabid in (-1,");
		 if(transfer==null|| "".equals(transfer)){
			 transfer="";
		 }else{
			 sql.append(transfer+",");
		 }
		 if(combine==null|| "".equals(combine))
			 combine="";
		 else
			 sql.append(combine+",");
		 if(bolish==null|| "".equals(bolish))
			 bolish="";
		 else
			 sql.append(bolish+",");
		 if(delete==null|| "".equals(delete))
			 delete="";
		 else
			 sql.append(delete+",");
		 this.getFormHM().put("goboardset",setname);
		 this.getFormHM().put("operationcode",operationcode);
		 this.getFormHM().put("chk",chk);
		 this.getFormHM().put("idType",id_type);
		 this.getFormHM().put("idTypeList",this.getIdTypeList());
		 this.getFormHM().put("onlyname",onlyname);
		 this.getFormHM().put("transfer", transfer);
		 this.getFormHM().put("combine", combine);
		 this.getFormHM().put("bolish", bolish);
		 this.getFormHM().put("delete", delete);
		 try {
			this.frowset = dao.search(sql.substring(0, sql.length()-1)+")");
			HashMap map = new HashMap();
			while(this.frowset.next()){
				String name=this.frowset.getString("name");
				String tableid=String.valueOf(this.frowset.getInt("tabid"));
				map.put(new Integer(tableid), name);
			}
				if(!"".equals(transfer)){
					this.formHM.put("transferview", (String)map.get(new Integer(transfer)));
				}else{
					this.formHM.put("transferview", "");
				}
				if(!"".equals(combine)){
					this.formHM.put("combineview", (String)map.get(new Integer(combine)));
				}else{
					this.formHM.put("combineview", "");
				}
				if(!"".equals(bolish)){
					this.formHM.put("bolishview", (String)map.get(new Integer(bolish)));
				}else{
					this.formHM.put("bolishview", "");
				}
				if(!"".equals(delete)){
					this.formHM.put("deleteview", (String)map.get(new Integer(delete)));
				}else{
					this.formHM.put("deleteview", "");
				}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 String chkcheck="",uniquenesscheck="";

		 if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
			 chkcheck="";
		 }
		 else{
			 chkcheck="checked";
		 }
		 if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
			 uniquenesscheck="";
		 }
		 else{
			 uniquenesscheck="checked";
		 }
		 this.getFormHM().put("chkcheck",chkcheck);
		 this.getFormHM().put("uniquenesscheck",uniquenesscheck);

		 ArrayList list=new ArrayList();
		 //CommonData vo = null;
		 LazyDynaBean bean = null;
		 //vo = new CommonData("","");
	     for(int i=1;i<4;i++)//把军队和其它，去掉
		 {
			 //vo = new CommonData();
	    	 bean = new LazyDynaBean();
	    	 bean.set("name",ResourceFactory.getProperty("sys.options.param.descript"+i));
	    	 bean.set("value",i+"");
	    	 if(typeid.indexOf(i+"")!=-1)
	    		 bean.set("check","checked");
	    	 else
	    		 bean.set("check","");
 			 //vo.setDataName(ResourceFactory.getProperty("sys.options.param.descript"+i));
 			 //vo.setDataValue(i+"");
 			 list.add(bean);
		 }
	     Sys_OTH_PARAMSqlStr sop=new Sys_OTH_PARAMSqlStr();
	     String bycardnosrc="";
	     String bycardnobirth="";
	     String bycardnoage="";
	     String bycardnovalid="";
	     String cardflag="0";
	     String bycardnoax="";
	     
	     String byworkdest="";
	     String byworksrc="";
	     String byworkvalid="";
	     String workflag="0";
	     
	     String byorgdest="";
	     String byorgsrc="";
	     String byorgvalid="";
	     String orgflag="0";
	     try {
			OtherParam op=new OtherParam(this.getFrameconn());
			Map cardMap=op.serachAtrr("/param/formual[@name='bycardno']");
			Map workMap=op.serachAtrr("/param/formual[@name='bywork']");
			Map orgMap=op.serachAtrr("/param/formual[@name='byorg']");
			if(cardMap!=null&&cardMap.size()==6){
				bycardnosrc=(String) cardMap.get("src");
				bycardnobirth=(String) cardMap.get("birthday");
			    bycardnoage=(String) cardMap.get("age");
			    String tempv=(String) cardMap.get("valid");
			    bycardnoax=(String) cardMap.get("ax");
			    if("true".equalsIgnoreCase(tempv)){
			    	cardflag="1";
			    }
//			    bycardnovalid=(String) cardMap.get("valid");
			}
			if(workMap!=null&&workMap.size()==4){
				byworkdest=(String) workMap.get("dest");
			    byworksrc=(String) workMap.get("src");
			    String tempv=(String) workMap.get("valid");
			    if("true".equalsIgnoreCase(tempv)){
			    	workflag="1";
			    }
//			    byworkvalid=(String) workMap.get("valid");
			}
			if(orgMap!=null&&orgMap.size()==4){
				byorgdest=(String) orgMap.get("dest");
			    byorgsrc=(String) orgMap.get("src");
			    String tempv=(String)orgMap.get("valid");
			    if("true".equalsIgnoreCase(tempv)){
			    	orgflag="1";
			    }
//			    byorgvalid=(String) orgMap.get("valid");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 this.getFormHM().put("typeid",typeid);
		 this.getFormHM().put("type_list",list);
		 this.getFormHM().put("codelist",getOperationList());
		 this.getFormHM().put("setlist",getSetList());
		 
		 hm.put("bycardnosrc",bycardnosrc);
		 hm.put("bycardnobirth",bycardnobirth);
		 hm.put("bycardnoage",bycardnoage);
		 hm.put("bycardnovalid",bycardnovalid);
		 hm.put("bycardnoax",bycardnoax);
		 
		 hm.put("byworkdest",byworkdest);
		 hm.put("byworksrc",byworksrc);
		 hm.put("byworkvalid",byworkvalid);
		 
		 hm.put("byorgdest",byorgdest);
		 hm.put("byorgsrc",byorgsrc);
		 hm.put("byorgvalid",byorgvalid);
		 
		 hm.put("cardflag",cardflag);
		 hm.put("workflag",workflag);
		 hm.put("orgflag",orgflag);
		 try{
		 hm.put("srclist",sop.getSrc(dao));
		 hm.put("destlist",sop.getDestBirth(dao));
		 hm.put("agelist",sop.getAge(dao));
		 hm.put("axlist",sop.getAx(dao));
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 /**
		  * 身份证
		  */
		 GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
		 ArrayList chklist = gf.getUsedFieldBySetNameTrans("A01",this.userView);
		 hm.put("chklist",chklist);
		 
		 
	}

}
