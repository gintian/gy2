package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ResumeBrowsePDF;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ResumeBrowsePdfTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap   resumeBrowseSetMap=new HashMap();   //应聘者各子集里的信息集合
			HashMap   setShowFieldMap=new HashMap();      //子集显示 列 map
			
		
			String a0100 = (String)this.getFormHM().get("a0100");
			String persontype=(String)this.getFormHM().get("persontype");
			
			//if(persontype!=null){//安全平台改造,根据persontype是否为空判断,是否是由招聘外网进来 xucs 2014-10-21 无论是否是招聘外网,现在a0100都是加密的了
			if(a0100!=null && a0100.length() > 0 && !a0100.matches("^\\d+$"))
				a0100 = PubFunc.decrypt(a0100);
			//}
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbName=zpDbNameVo.getString("str_value");
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String resumeStateFieldIds="";
			if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
				resumeStateFieldIds=(String)map.get("resume_state");
			String status=bo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
			String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
			canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
			if(canPrintAdmissionCardStatus.indexOf((","+status+","))==-1)
			{
				
			}
			else
			{
				bo.setVisiablSeqField(true);
			}
			
			String workExperience=bo.getWorkExperience();
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			String value="";
			if("1".equals(isDefineWorkExperience))
				value=(String)this.getFormHM().get("workExperience");
			
			ArrayList list = bo.getZpFieldList();
			//招聘渠道，如果渠道为空则取发布招聘渠道的第一个
			String tem = (String)this.getFormHM().get("hireChannel");
			String hireChannel = StringUtils.isEmpty(tem) ? bo.getHireChannelFromTable() : tem;
			
			String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				hireChannel = bo.getCandidateStatus(candidate_status_itemId, a0100);
				String channelName = bo.getChannelName(candidate_status_itemId,a0100,"");
				this.getFormHM().put("channelName", channelName);
			}
			
			list=bo.getSetByWorkExprience(hireChannel);
			ArrayList fieldSetList=(ArrayList)list.get(0);
			for(int i=0;i<((ArrayList)list.get(0)).size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)((ArrayList)list.get(0)).get(i);
				String setID=(String)abean.get("fieldSetId");
				if("A01".equalsIgnoreCase(setID))
				{
					ArrayList resumeFieldList=bo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),0,(HashMap)list.get(1),a0100,dbName,"0");
					resumeBrowseSetMap.put(setID.toLowerCase(),resumeFieldList);
				}
				else
				{
					ArrayList showFieldList=bo.getShowFieldList(setID,(HashMap)list.get(2),(HashMap)list.get(1),0);  //取得简历子集 列表需显示的 列指标 集合
					ArrayList showFieldDataList=bo.getShowFieldDataList(showFieldList,a0100,setID,dbName);
					resumeBrowseSetMap.put(setID.toLowerCase(),showFieldDataList);
					setShowFieldMap.put(setID.toLowerCase(),showFieldList);
					
				}
			}
			ArrayList remarkList=getRemarkList(a0100,dbName);
			ArrayList zpPosList=getZpPosList(a0100,status);
			
			ResumeBrowsePDF bo1=new ResumeBrowsePDF(this.getFrameconn(),"0");
			bo1.setMap(map);
			bo1.setPersontype(persontype);
			bo1.setRemarkList(remarkList);
			bo1.setZpPosList(zpPosList);
			bo1.setSetShowFieldMap(setShowFieldMap);
			bo1.setResumeBrowseSetMap(resumeBrowseSetMap);
			bo1.setFieldSetList(fieldSetList);
			String outName=bo1.createPdf(a0100,dbName);
			this.getFormHM().put("outName",PubFunc.encrypt(outName));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public ArrayList getRemarkList(String a0100,String dbName)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	
		try
		{
			ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap  map=bo.getAttributeValues();
			String   codesetid=(String)map.get("resume_level");
			//if(codesetid==null||codesetid.equals(""))
			//	throw GeneralExceptionHandler.Handle(new Exception("没有设置评语等级代码！"));
			StringBuffer sql=new StringBuffer("");
			String isRemenberExamine="";
			String remenberExamineSet="";
			if(map!=null&&map.get("isRemenberExamine")!=null)
			{
				isRemenberExamine=(String)map.get("isRemenberExamine");
			}
			if(map!=null&&map.get("remenberExamineSet")!=null)
			{
				remenberExamineSet=(String)map.get("remenberExamineSet");
			}
			String title="";
			String content="";
			String commentuser="";
			String level1="";
			String commentdate="";
			HashMap infoMap=null;
			if(map!=null)
			{
				infoMap=(HashMap)map.get("infoMap");
				if(infoMap!=null&&infoMap.get("title")!=null)
					title=(String)infoMap.get("title");
				if(infoMap!=null&&infoMap.get("content")!=null)
					content=(String)infoMap.get("content");
				if(infoMap!=null&&infoMap.get("level")!=null)
					level1=(String)infoMap.get("level");
				if(infoMap!=null&&infoMap.get("comment_user")!=null)
					commentuser=(String)infoMap.get("comment_user");
				if(infoMap!=null&&infoMap.get("comment_date")!=null)
					commentdate=(String)infoMap.get("comment_date");
			}
			if("0".equals(isRemenberExamine)||isRemenberExamine==null|| "".equals(isRemenberExamine)||remenberExamineSet==null|| "".equals(remenberExamineSet))
			{
				RecordVo vo=new RecordVo("zp_comment_info");
				
			   /* sql.append("select zpi.title,zpi.comment_date,zpi.comment_user,zpi.content,");
			    if(vo.hasAttribute("level"))
			    	sql.append("level ");
			    else if(vo.hasAttribute("level_o"))
			    	sql.append("level_o");*/
				StringBuffer buf = new StringBuffer("info_id,a0100,title,content,comment_date,comment_user ");
				if(vo.hasAttribute("level"))
				{
					buf.append(" ,\"level\" as lev");
				}else if(vo.hasAttribute("level_o"))
				{
					buf.append(",level_o as lev");
				}
				sql.append(" select "+buf.toString());
			    sql.append(" from zp_comment_info zpi");
	            sql.append(" where   a0100='"+a0100+"' order by info_id");
			
				this.frowset=dao.search(sql.toString());
				SimpleDateFormat dataF=new SimpleDateFormat("yyyy-MM-dd");
				while(this.frowset.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("title",this.frowset.getString("title"));
					abean.set("date",dataF.format(this.frowset.getDate("comment_date")));
					abean.set("user",this.frowset.getString("comment_user"));
					
					String levelDesc="";
					if(codesetid!=null&&!"".equals(codesetid))
					{
						String level="";
						
						level=this.frowset.getString("lev");
						if(level!=null)
							levelDesc=AdminCode.getCodeName(codesetid,level);
					}
					abean.set("level",levelDesc);
					abean.set("content",Sql_switcher.readMemo(this.frowset,"content"));
					list.add(abean);
				}
			}else{
				sql.append(" select a."+title+" as title,a."+content+" as content,a."+commentuser+" as comment_user,");
	    		sql.append("a."+commentdate+" as comment_date ");
	    		sql.append(" from "+dbName+remenberExamineSet+" a ");
		    	sql.append(" where a.a0100='"+a0100+"'");
		    	this.frowset=dao.search(sql.toString());
				SimpleDateFormat dataF=new SimpleDateFormat("yyyy-MM-dd");
				FieldItem contentitem=DataDictionary.getFieldItem(content);
				while(this.frowset.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("title",this.frowset.getString("title")==null?"":this.frowset.getString("title"));
					if(this.frowset.getDate("comment_date")!=null)
				    	abean.set("date",dataF.format(this.frowset.getDate("comment_date")));
					else
						abean.set("date", "");
					abean.set("user",this.frowset.getString("comment_user")==null?"":this.frowset.getString("comment_user"));
					if(contentitem!=null&& "M".equalsIgnoreCase(contentitem.getItemtype()))
				    	abean.set("content",Sql_switcher.readMemo(this.frowset,"content"));
					else if(contentitem!=null&& "A".equalsIgnoreCase(contentitem.getItemtype()))
						abean.set("content",this.frowset.getString("content"));
					else
						abean.set("content",this.frowset.getString("content"));
					list.add(abean);
					
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
	
	
	public ArrayList getZpPosList(String a0100,String status)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowset=null;
		try
		{
			// author:dengcan
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.frameconn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String hireMajor="";
			if(map.get("hireMajor")!=null)
				hireMajor=(String)map.get("hireMajor");  //招聘专业指标
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			String sql="select zpt.zp_pos_id,zpt.thenumber,resume_flag,codeitem.codeitemdesc as cc,zpt.nbase, um.codeitemdesc as umc,un.codeitemdesc as unc,z03.Z0336 ";
			FieldItem fieldItem = DataDictionary.getFieldItem("Z0311","Z03");
			if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())) {
				sql += ",uk.codeitemdesc ";
			}
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem!=null&&hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
					hireMajorIsCode=true;
					sql+=","+hireMajor;
				}
			}
			//sql+=",zpt.zp_pos_id,organization.codeitemdesc ,zpt.thenumber,resume_flag,codeitem.codeitemdesc as cc,um.codeitemdesc as umc,un.codeitemdesc as unc  from zp_pos_tache zpt,z03,organization,(select * from codeitem where codesetid='36') codeitem"
			//		  +",(select codeitemid,codeitemdesc from organization where codesetid='UM') um,(select codeitemid,codeitemdesc from organization where codesetid='UN') un where zpt.zp_pos_id=z03.z0301 and z03.z0311=organization.codeitemid and z03.z0321=un.codeitemid and z03.z0325=um.codeitemid  and  zpt.resume_flag=codeitem.codeitemid  and   a0100='"+a0100+"' order by zpt.thenumber";
			sql+=" from zp_pos_tache zpt "
					+ "left join (select * from codeitem where codesetid='36') codeitem on zpt.resume_flag=codeitem.codeitemid "
					+ "left join z03 on z03.z0301=zpt.zp_pos_id "
					+ "left join (select codeitemid,codeitemdesc from organization where codesetid='UM') um on  z03.z0325=um.codeitemid "
					+ "left join (select codeitemid,codeitemdesc from organization where codesetid='UN') un on z03.z0321=un.codeitemid ";
			if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
				sql+= "left join (select codeitemid,codeitemdesc from organization where codesetid='@K') uk on z03.z0311=uk.codeitemid ";
			sql+=" where  a0100='"+a0100+"' order by zpt.thenumber";
			
			rowset=dao.search(sql);
			while(rowset.next())
			{
				String un="";
				if(rowset.getString("unc")!=null)
					un=rowset.getString("unc")+"/";
				String um="";
				if(rowset.getString("umc")!=null)
					um=rowset.getString("umc")+"/";
				String resumeflag=rowset.getString("resume_flag")==null?"":rowset.getString("resume_flag");
				String rstatus="";
				if(status!=null&&!"".equals(status)&& "12".equals(resumeflag)&&!"12".equals(status))
				{
					rstatus=AdminCode.getCodeName("36",status);
				}
				if(rstatus!=null&&!"".equals(rstatus))
					rstatus="("+rstatus+")";
				String value=rowset.getString("codeitemdesc");
				String z0336=rowset.getString("z0336");
				z0336=z0336==null?"02":z0336;
				if(hireMajor.length()>0&& "01".equalsIgnoreCase(z0336))
				{
					if(hireMajorIsCode)
					{
						value=rowset.getString(hireMajor);
						value=AdminCode.getCodeName(hireMajoritem.getCodesetid(), value);
					}
					else
						value=rowset.getString(hireMajor);
				}
				
				
				CommonData data1=new CommonData(rowset.getString("zp_pos_id"),un+um+value+" [第"+rowset.getInt("thenumber")+"志愿: "+rowset.getString("cc")+rstatus+"]");
				list.add(data1);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	
}
