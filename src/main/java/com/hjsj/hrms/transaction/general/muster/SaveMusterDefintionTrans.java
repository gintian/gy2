/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.MusterParamterVo;
import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SaveMusterDefintionTrans</p>
 * <p>Description:保存花名册定义</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-15:14:16:05</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveMusterDefintionTrans extends IBusiness {

	private ArrayList transList(ArrayList fieldlist)
	{
		ArrayList list=new ArrayList();
		StringBuffer format=new StringBuffer();
		format.append("############");

		for(int i=0;i<fieldlist.size();i++)
		{
			Field item=(Field)fieldlist.get(i);
			Field temp=(Field)item.clone();
			String fieldname=temp.getName();
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname.substring(0,5));
			if(fielditem!=null)
			{
				if("N".equals(fielditem.getItemtype()))
				{
					int ndec=temp.getDecimalDigits();
					if(ndec>0)
					{
						temp.setDatatype(DataType.FLOAT);						
						format.setLength(ndec);
						temp.setFormat("####."+format.toString());
					}
					else
						temp.setDatatype(DataType.INT);							
				}	
				else if("D".equals(fielditem.getItemtype()))
				{
					temp.setDatatype(DataType.DATE);
					temp.setFormat("yyyy.MM.dd");
				}	
				else
				{
					temp.setDatatype(DataType.STRING);
				}
			}
			list.add(temp);
		}
		return list;
	}

	public void execute() throws GeneralException {
	  try
	  {
		HashMap hm=(HashMap)this.getFormHM();
		String[] musterfields=(String[])hm.get("musterfields");
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)hm.get("inforkind");
		if(infor_kind==null|| "".equals(infor_kind))
			infor_kind="1";
		if(musterfields==null||musterfields.length==0)
		{
			throw new GeneralException("",ResourceFactory.getProperty("error.muster.notdefined"),"","");
		}
		/**定义参数,保存花名册结构*/
		String history=(String)hm.get("history");
		history=history!=null&&history.trim().length()>0?history:"0";
		
		String repeat_mainset=(String)hm.get("repeat_mainset");
		repeat_mainset=repeat_mainset!=null&&repeat_mainset.trim().length()>0?repeat_mainset:"0";
		
		
		String returncheck=(String)hm.get("returncheck");
		returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"0";
		this.getFormHM().put("returncheck",returncheck);
		
		if(!"1".equals(infor_kind))
			history="0";
		if("0".equals(history))
			repeat_mainset="0";

	//	String[] ddd=(String[])hm.get("sortfields");
		
		String mustertype = (String)hm.get("mustertype");
		mustertype=mustertype!=null?mustertype:"";
		
		String mustername = (String)hm.get("mustername");
		mustername=mustername!=null?mustername:"";
		
		String sortitem = (String)hm.get("sortitem");
		sortitem=sortitem!=null?sortitem:"";
		hm.remove("sortitem");
		
		MusterParamterVo para_vo=new MusterParamterVo();
		para_vo.setUsername(this.userView.getUserName());
		para_vo.setMusterfields(musterfields);
		para_vo.setInfor_kind(infor_kind);
		para_vo.setMustername((String)hm.get("mustername"));
		para_vo.setSortfields((String[])hm.get("sortfields"));
		para_vo.setUsed_flag((String)hm.get("used_flag"));
		para_vo.setMustertype(mustertype);
		para_vo.setSortitem(sortitem);
		hm.remove("mustername");
		hm.remove("sortfields");
		hm.remove("used_flag");
		hm.remove("mustertype");
		para_vo.setHistory(history);
	
		//MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.getUserView());
		RecordVo lname_vo=musterbo.saveMuster(para_vo);
		/**用查询结果,生成花名册数据*/
		String dbpre=(String)hm.get("dbpre");
		if(dbpre==null)
			dbpre="";
		
		String repeat = "False";
		if("1".equals(repeat_mainset))
			repeat = "True";

		if("1".equals(infor_kind)){
			MusterXMLStyleBo xmltylebo = new MusterXMLStyleBo(this.getFrameconn(),lname_vo.getString("tabid"));
			xmltylebo.setParamValue(MusterXMLStyleBo.Param,"repeat_mainset",repeat);
			xmltylebo.saveSetValue();
		}
		/**创建花名册*/
		String tabname=null;
		if(musterbo.createMusterTempTable(infor_kind,dbpre,lname_vo.getString("tabid"),this.userView.getUserName(),history))
			tabname=musterbo.getTableName(infor_kind,dbpre,lname_vo.getString("tabid"),this.userView.getUserName());
		this.getFormHM().put("mustername",tabname);
		/**把花名册指标-->显示的数据格式*/

		ArrayList fieldlist=musterbo.getFieldlist();
		ArrayList itemlist = new ArrayList();
		ArrayList checkitemlist = new ArrayList();
		boolean checkitemid = false;
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			Field field = (Field)fieldlist.get(i);
			if("A0100".equals(field.getName()))
				field.setVisible(false);
//			if(field.getName().equals("B0110")
//						||field.getName().equals("E01A1"))
//					field.setVisible(false);
			if(!"recidx".equalsIgnoreCase(field.getName())&& "0".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setReadonly(true);
			if("recidx".equalsIgnoreCase(field.getName()))
				field.setReadonly(true);
			if(!musterbo.haveDataInMuster(tabname,field.getName()))
				checkitemid = true;
			field.setSortable(false);
			checkitemlist.add(field.getName());
			FieldItem fielditem = DataDictionary.getFieldItem(field.getName());
			if(fielditem!=null&& "D".equalsIgnoreCase(fielditem.getItemtype()))
			{
	    		if(fielditem.getItemlength()==10)
	    			column.append(","+field.getName());
		    	else if(fielditem.getItemlength()==7)
		    	{
		    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    		{
		    			column.append(","+Sql_switcher.substr(field.getName(), "0", "7")+" as "+field.getName());
		    		}else
		        		column.append(","+Sql_switcher.substr(field.getName(), "0", "8")+" as "+field.getName());
		    	}else if(fielditem.getItemlength()==4){
		    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    		{
		    			column.append(","+Sql_switcher.substr(field.getName(), "0", "4")+" as "+field.getName());
		    		}else
		    	    	column.append(","+Sql_switcher.substr(field.getName(), "0", "5")+" as "+field.getName());
		    	}else
		    		column.append(","+field.getName());
			}
			else
			{
				column.append(","+field.getName());
			}
			itemlist.add(field);
		}
		this.getFormHM().put("fieldlist",itemlist);
		this.getFormHM().put("coumsize",fieldlist.size()+"");
		String countPerson = "";
		if(infor_kind!=null&& "1".equals(infor_kind)){
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search("select count(A0100) from "+tabname+" group by A0100");
			int i=0;
			while(rs.next()){
				i++;
			}
			countPerson="(总人数:"+i+"人)";
		}
		//String sql="select * from "+tabname+" order by recidx";
		String sql="select "+column.toString().substring(1)+" from "+tabname+" order by recidx";
		this.getFormHM().put("sql",sql);
		this.getFormHM().put("countStr",countPerson);
		this.getFormHM().put("currid",lname_vo.getString("tabid"));
		cat.debug("muster_fields="+musterfields.toString());
		
		
		/**chenmengqing added at 20080202*/
		//mustername="<div style=\"position:absolute;top:40;left:540;WIDTH:500;WORD-BREAK:break-all;\">"+mustername+"</div>";
		mustername = mustername;
		
		this.getFormHM().put("mustertitle",mustername);
		this.getFormHM().put("moduleflag",moduleflag(mustertype));
		this.getFormHM().put("infor_Flag",infor_kind);
		this.getFormHM().put("save_flag","save");
		this.getFormHM().put("checkflag","0");
		this.getFormHM().put("condlist",musterbo.getUsuallyCondList(infor_kind,this.userView));
		this.getFormHM().put("sortitem",musterbo.getOrderFieldStr(lname_vo.getString("tabid")));
		if(hm.get("a_tabid")!=null)
			hm.remove("a_tabid");
		
		/**创建时保存*/
		if(!(this.userView.isSuper_admin() && "1".equals(this.userView.getGroupId())))
		{
			UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(lname_vo.getString("tabid"),this.userView,IResourceConstant.MUSTER);
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }
	}
	private String moduleflag(String type){
		String moduleflag="";
		if(type!=null&&type.trim().length()>0){
			moduleflag="0"+type;
		}
		return moduleflag;
	}
}
