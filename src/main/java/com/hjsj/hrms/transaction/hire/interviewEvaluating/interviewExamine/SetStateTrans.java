package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.PreparedStatement;
import java.util.*;

public class SetStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedlist");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String state=(String)hm.get("state");
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();
		PreparedStatement ps=null;
		String resume_state_field="";
		RowSet rs=null;
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
			HashMap map0 = bo2.getAttributeValues();
			if (map0 != null && map0.get("resume_state") != null)
				resume_state_field = (String) map0.get("resume_state");
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer a0100s=new StringBuffer("");
			for(Iterator t=selectedList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				a0100s.append(",'"+ PubFunc.decrypt((String)abean.get("a0100"))+"'");
			}
			
			if(a0100s.length()==0)
				return;
			if(!"del".equals(state))
			{
				dao.update("update "+dbname+"A01 set "+resume_state_field+"='"+state+"' where A0100 in ("+a0100s.substring(1)+")");
				if("32".equals(state)|| "31".equals(state))  //如果状态为复试或初试，则将 z05（面试安排信息表里的相关数据初始化）
				{
					/**开始查询z05中涉及到高级测评列，这写字段的数据不能清空，予以保留**/
					 ArrayList testTemplatAdvance=(ArrayList) map0.get("testTemplatAdvance");//高级测评的相关参数
					 ArrayList columnList= new ArrayList();//存放列名
					 for(int i=0;i<testTemplatAdvance.size();i++){
						 HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
						 String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
						 columnList.add(score_item);
					 }
					 String sql="select * from z05 where A0100 in("+a0100s.substring(1)+")";
					 rs=dao.search(sql);
					 HashMap dataMap=new HashMap();
					 while(rs.next()){
						 String a0100=rs.getString("A0100");
						 ArrayList valueList=new ArrayList();
						 for(int i=0;i<columnList.size();i++){
							 HashMap tempMap=new HashMap();
							 String columnName=(String) columnList.get(i);
							 
							 FieldItem item=DataDictionary.getFieldItem(columnName);
							 int decmacWidth=item.getDecimalwidth();
							 String columnValue="";
							 if(decmacWidth==0){
								columnValue=String.valueOf(rs.getInt(columnName));
							 }else{
								 columnValue=String.valueOf(rs.getDouble(columnName));
							 }
							 tempMap.put(columnName, columnValue);
							 valueList.add(tempMap);
						 }
						 dataMap.put(a0100, valueList);
					 }
					dao.delete("delete from Z05 where A0100 in ("+a0100s.substring(1)+")",new ArrayList());
					ArrayList recordList=new ArrayList();
					for(Iterator t=selectedList.iterator();t.hasNext();)
					{
						 LazyDynaBean abean=(LazyDynaBean)t.next();
						 String temp=PubFunc.decrypt((String)abean.get("a0100"));
						 RecordVo vo=new RecordVo("Z05");	
						 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
						 String id=idg.getId("Z05.Z0501");		
						 vo.setString("z0501",id);
						 vo.setString("a0100",temp);
						 vo.setString("state","21");
			             vo.setDate("z0515",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));//简历状态修改时间 2014-05-28
			             ArrayList valueList=(ArrayList) dataMap.get(temp);
			             for(int i=0;i<valueList.size();i++){
			            	 HashMap tempMap=(HashMap) valueList.get(i);
			            	 Set keySet=tempMap.keySet();
			            	 for(Iterator it=keySet.iterator();it.hasNext();){
			            		 String columnName=(String) it.next();
			            		 String columnValue=(String) tempMap.get(columnName);
			            		 FieldItem item=DataDictionary.getFieldItem(columnName);
								 int decmacWidth=item.getDecimalwidth();
								 if(decmacWidth==0){
										vo.setInt(columnName.toLowerCase(), Integer.parseInt(columnValue));
									 }else{
										 vo.setDouble(columnName.toLowerCase(), Double.parseDouble(columnValue));
									 }
			            	 }
							 
			             }
						 recordList.add(vo);
					}
					dao.addValueObject(recordList);
				}
				else if("41".equals(state))  //拟录用
				{
					// 加入拟录用字段z0515，值为当前时间,且过滤掉状态为21的记录 by 刘蒙
					dao.update("update z05 set state='21',Z0515=" + Sql_switcher.sqlNow() + " where A0100 in ("+a0100s.substring(1)+") and state <> '21'");
					
				}
				else if("13".equals(state))    //未通过
				{
					dao.update("update zp_pos_tache set resume_flag='13' where resume_flag='12' and  A0100 in ("+a0100s.substring(1)+")");
					dao.delete("delete from z05 where a0100 in ("+a0100s.substring(1)+") ",new ArrayList());
					clearPersonExamRecord(a0100s);
				}
			}
			else   //删除
			{
				clearPersonExamRecord(a0100s);
				dao.delete("delete from zp_pos_tache where a0100 in ("+a0100s.substring(1)+") ",new ArrayList());
				dao.delete("delete from z05 where a0100 in ("+a0100s.substring(1)+") ",new ArrayList());
				dao.update("update "+dbname+"A01 set "+resume_state_field+"='10' where A0100 in ("+a0100s.substring(1)+")");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(ps!=null)
					ps.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	
	
	
	
	public void clearPersonExamRecord(StringBuffer a0100s)
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			HashSet templateIDSet=new HashSet();
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			for(Iterator  i=map.keySet().iterator();i.hasNext();)   {   
		            String keyValue=(String)i.next();
		            if(keyValue.indexOf("testTemplateID_")!=-1)
		            {
		            	templateIDSet.add((String)map.get(keyValue));
		            }
			}
			
			dao.delete("delete from zp_test_template where A0100 in ("+a0100s.substring(1)+")",new ArrayList());
			for(Iterator t=templateIDSet.iterator();t.hasNext();)
			{
				String templateID=(String)t.next();
				dao.delete("delete from zp_test_result_"+templateID+" where A0100 in ("+a0100s.substring(1)+")",new ArrayList());	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	

}
