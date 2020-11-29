package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.options.otherparam.Sys_OTH_PARAMSqlStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;

public class ShowSetItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		Sys_OTH_PARAMSqlStr sop=new Sys_OTH_PARAMSqlStr();
		String codesetid=(String)reqhm.get("codesetid");
		String selected=(String)reqhm.get("selected");
		selected = PubFunc.keyWord_reback(selected);
		String[] sqlstr=new String[4];
		String selStr="";		
		try {
			OtherParam op=new OtherParam(this.getFrameconn());
			String setid=getA01CodeSetID(dao);
			if(codesetid==null||codesetid.length()<1)
			{
				String codeid=op.getCodesetid();
				if(codeid!=null){
					if(selected!=null&&selected.length()>0)
					   sqlstr=sop.getCodeitem(selected.split("/")[0]);
					else if(setid!=null&&setid.length()>0)
					{
						sqlstr=sop.getCodeitem(setid);
					}else 
					{
						sqlstr=sop.getCodeitem(codeid);
					}
					   
					if(selected!=null&&selected.length()>0)
					  selStr=getA01Codelist(dao,selected);
					else
					{
						selStr=getA01Codelist(dao,setid);
					}
					Map myMap=this.getItem();
					hm.put("itemMap",myMap);
				}else{
					
					sqlstr=sop.getCodeitem(setid);
					selStr=getA01Codelist(dao,setid);
					hm.put("itemMap",new HashMap());
					this.getFormHM().put("view_check","false");
				}
				
			}else{
				sqlstr=sop.getCodeitem(codesetid);
				if(codesetid.equals(op.getCodesetid())){
					Map myMap=this.getItem();
//					System.out.println(myMap.size());
					hm.put("itemMap",myMap);
				}else{
					hm.put("itemMap",new HashMap());
				}
				selStr=getA01Codelist(dao,selected);
				reqhm.remove("codesetid");
			}
		    Map vmap=op.serachAtrr("/param/employ_type");
		    String valid="false";
		    if(vmap!=null){
				 valid=(String) vmap.get("valid");
		    }
			hm.put("itemvalid",valid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("selStr",selStr);
		hm.put("sql",sqlstr[0]);
		hm.put("where",sqlstr[1]);
		hm.put("column",sqlstr[2]);
		hm.put("orderby",sqlstr[3]);
		
	}
	public Map getItem() throws Exception{
		OtherParam op=new OtherParam(this.getFrameconn());
		boolean isCorrect=false;
		Map myMap=op.getEmployeeType();
		if(myMap.size()>0){
			for(Iterator it=myMap.keySet().iterator();it.hasNext();){
				String key=(String) it.next();
				Map mv=(Map) myMap.get(key);
				String table=(String) mv.get("table");
				String field=(String) mv.get("field");
				String[] tablestr=table.split(",");
				String[] fieldstr=field.split(",");
				List myList=new ArrayList();
				for(int i=0;i<tablestr.length;i++){
					String tid=tablestr[i];
					FieldSet fs=DataDictionary.getFieldSetVo(tid);
					if(fs!=null){
					String zhujiname=fs.getCustomdesc();				

					myList.add(zhujiname);
					for(int j=0;j<fieldstr.length;j++){
						String fi=fieldstr[j];
//						if(fi.startsWith(tid)){
							FieldItem fis=DataDictionary.getFieldItem(fi);
							if(fis!=null&&(fis.getFieldsetid()).equals(fs.getFieldsetid())){
							String finame=fis.getItemdesc();
							myList.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+finame);
							isCorrect=true;
							}
//						}else{
//							System.out.println(fi);
//						}
					}
					}
					
				}
				mv.put("value",myList);
				mv.remove("table");
				mv.remove("field");
			}
		}
		if(isCorrect)
			this.getFormHM().put("view_check","true");
		else
			this.getFormHM().put("view_check","false");
		return myMap;
	}
	public String  getA01Codelist(ContentDAO dao,String sel) throws GeneralException{
		String sql="select * from fielditem where fieldsetid='A01' and codesetid<>'0' and useflag='1' and itemid<>'E0122'";
		sql=sql+" and codesetid<>'UN' and codesetid<>'UM' order by displayid";
		List l=dao.searchDynaList(sql);
		StringBuffer sbSel=new StringBuffer();
		sbSel.append("<select name='codesetid' onchange='getcodesetid();'>");
		//添加单位、部门代码
		/* 移除 单位 部门 代码    bug 38630 wangb 20180705
		sbSel.append("<option value='UN/B0110'");
		if("UN/B0110".equals(sel))
			sbSel.append(" selected='selected' ");
		sbSel.append(">单位</option>");
		sbSel.append("<option value='UM/E0122' ");
		if("UM/E0122".equals(sel))
			sbSel.append(" selected='selected' ");
		sbSel.append(">部门</option>");
		*/
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			String code=(String) dynabean.get("codesetid");
			String itemid=(String)dynabean.get("itemid");
			if(sel!=null&&sel.indexOf("/")!=-1)
			{
				if((code+"/"+itemid).equalsIgnoreCase(sel)){
					sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"' selected='selected'>");
					sbSel.append(dynabean.get("itemdesc"));
					sbSel.append("</option>");
				}else{
					sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"'>");
					sbSel.append(dynabean.get("itemdesc"));
					sbSel.append("</option>");
				}
			}else if((code).equalsIgnoreCase(sel))
			{
				sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"' selected='selected'>");
				sbSel.append(dynabean.get("itemdesc"));
				sbSel.append("</option>");
			}
			else{
				sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"'>");
				sbSel.append(dynabean.get("itemdesc"));
				sbSel.append("</option>");
			}
		}
		
		sbSel.append("</select>");
		return sbSel.toString();
	}
	private String getA01CodeSetID(ContentDAO dao)
	{
		String sql="select * from constant where upper(constant)='SYS_INFO_PRIV'";
		
		String codesetid="";
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			String value="";
			if(rs.next())
			{
				value=rs.getString("str_value");
				if(value!=null&&value.length()>0)
				{
					String arr[]=value.split(",");
					if(arr!=null&&arr.length==2)
					{
						return arr[1];
					}
				}
			}
			sql="select * from fielditem where fieldsetid='A01' and codesetid<>'0' and useflag='1' and itemid<>'E0122'";
			sql=sql+" and codesetid<>'UN' and codesetid<>'UM'";		
			rs=dao.search(sql);
			if(rs.next())
			{
				codesetid=rs.getString("codesetid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return codesetid;
	}
}
