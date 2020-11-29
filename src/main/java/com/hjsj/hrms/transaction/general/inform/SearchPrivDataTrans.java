/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:SearchPrivDataTrans</p> 
 *<p>Description:查询权限数据交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-15:下午02:25:00</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchPrivDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			String modleflag = (String)hm.get("modleflag");
			modleflag=modleflag!=null?modleflag:"0";
			hm.remove("modleflag");
			
			String vorg = (String)hm.get("vorg");
			vorg=vorg!=null?vorg:"1";
			hm.remove("vorg");

			/**库前缀列表*/
			ArrayList list=this.userView.getPrivDbList();
			if(list.size()==0)
				throw new GeneralException(ResourceFactory.getProperty("workbench.stat.noprivdbname"));
			ArrayList dblist=dbnameList(list);

			this.getFormHM().put("dblist",dblist);
			/**应用库前缀*/
			String dbname= (String)this.getFormHM().get("dbname");
			dbname=dbname!=null?dbname:"";
			boolean chk = true;
			for(int i=0;i<dblist.size();i++){
				CommonData data= (CommonData)dblist.get(i);
				if(data!=null){
					if(data.getDataValue().equalsIgnoreCase(dbname)){
						chk = false;
						break;
					}
				}
			}
			
			if(dbname.trim().length()<1||chk){
				CommonData data= (CommonData)dblist.get(0);
				dbname=(String)data.getDataValue();
			}
			this.getFormHM().put("dbname",dbname);
			/**表授权*/
			String inforflag = (String)hm.get("inforflag");
			inforflag=inforflag!=null&&inforflag.toString().length()>0?inforflag:"1";
			hm.remove("inforflag");
			
			String lies = (String)hm.get("lies");
			if(lies == null)
			    lies = modleflag;
			lies=lies!=null&&lies.toString().length()>0?lies:"0";
			hm.remove("lies");
			String train = "";
			if("2".equals(inforflag)){
				list=trainItemList();
				train = "train";
			}else
				list=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			if(list.size()==0)
				throw new GeneralException(ResourceFactory.getProperty("workbench.stat.noprivset"));
			ArrayList setlist=new ArrayList();	
			for(int i=0;i<list.size();i++)
			{
				FieldSet fieldset=(FieldSet)list.get(i);
				/**未构库不加进来*/
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
//				if(fieldset.getFieldsetid().equalsIgnoreCase("A00"))
//					continue;
				if(!"A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					ArrayList checklist=this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);

					if(checklist==null||checklist.size()<1)
						continue;
				}
				CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
				setlist.add(temp);
			}//for i loop end.
			Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
			String virtual_org=othparam.getValue(Sys_Oth_Parameter.VIRTUAL_ORG);
			if(virtual_org!=null&& "1".equals(virtual_org)&& "1".equals(vorg)&&!"2".equals(inforflag)){
				CommonData temp=new CommonData("t_vorg_staff","非职务任职情况");
				setlist.add(temp);
			}
			this.getFormHM().put("setlist",setlist);
			String setname=(String)this.getFormHM().get("setname");
			if("2".equals(inforflag)){
				if("1".equals(lies)){
				    if(setlist!=null&&setlist.size()>0)
				        setname=((CommonData)setlist.get(0)).getDataValue();
				    else
				        throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.nomainfield")));
				}else{
					setname="A01";
				}
			}else{
				setname="A01";
			}
			this.getFormHM().put("setname",setname);
			String codeitem=(String)hm.get("codeitem");
			codeitem=codeitem!=null&&codeitem.trim().length()>0?codeitem:"UN";
			hm.remove("codeitem");
			
			String visible="true";
			if("2".equals(inforflag)&& "a01".equalsIgnoreCase(setname)){
				visible="false";
			}
			String codeitemid="";
			FieldItem fielditem =  DataDictionary.getFieldItem(codeitem);
			if(fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())
					|| "UM".equalsIgnoreCase(fielditem.getCodesetid())
					|| "@K".equalsIgnoreCase(fielditem.getCodesetid()))){
				codeitemid=codeitem;
			}
			
			this.getFormHM().put("codeitemid", codeitemid);
			this.getFormHM().put("codeitem",codeitem);
			this.getFormHM().put("codeitemlist",codeItemList());
			this.getFormHM().put("inforflag",inforflag);
			this.getFormHM().put("viewbutton",visible);
			this.getFormHM().put("modleflag", modleflag);
			this.getFormHM().put("viewsearch", "0");
			this.getFormHM().put("t_vorg", vorg);
			this.getFormHM().put("train", train);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private ArrayList codeItemList(){
		ArrayList list = new ArrayList();
		ArrayList fieldlist = this.userView.getPrivFieldList("A01",Constant.USED_FIELD_SET);
		CommonData temp=new CommonData("UN",ResourceFactory.getProperty("general.inform.search.org"));
		list.add(temp);
		for(int i=0;i<fieldlist.size();i++){
			FieldItem item = ( FieldItem)fieldlist.get(i);
			if(item.isCode()){
				if("B0110".equalsIgnoreCase(item.getItemid())|| "E0122".equalsIgnoreCase(item.getItemid())
						|| "E01A1".equalsIgnoreCase(item.getItemid())){
					continue;
				}else{
					CommonData temp1=new CommonData(item.getItemid(),item.getItemdesc());
					list.add(temp1);
				}
			}
		}
		return list;
	}
	private ArrayList trainItemList(){
		ArrayList list = new ArrayList();
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String viewname = constantbo.getValue("subset");
		viewname=viewname!=null&&viewname.trim().length()>3?viewname:"";
		String arr[] = viewname.split(",");
		for(int i=0;i<arr.length;i++){
			String fieldsetid = arr[i];
			fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
			if(fieldsetid.trim().length()>0){
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				if(fieldset!=null)
					list.add(fieldset);
			}
		}
		return list;
	}
	private ArrayList dbnameList(ArrayList list){
		ArrayList dblist = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select * from dbname where Pre in(");
		String wheresql = "";
		for(int i=0;i<list.size();i++){
			String pre=(String)list.get(i);
			if(pre!=null&&pre.trim().length()>0){
				wheresql+="'"+pre+"',";
			}
		}
		wheresql =wheresql.substring(0,wheresql.length()-1);
		sqlstr.append(wheresql);
		sqlstr.append(") order by DbId");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				CommonData data=new CommonData(this.frowset.getString("Pre"),
						this.frowset.getString("DBName"));
				dblist.add(data);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dblist;
	}

}
