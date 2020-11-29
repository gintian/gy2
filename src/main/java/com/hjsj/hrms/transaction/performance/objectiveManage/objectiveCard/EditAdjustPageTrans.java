package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description: 修改或删除 调整后的任务 </p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2008</p> 
 *@author dengcan
 *@version 4.2
 */
public class EditAdjustPageTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("operate");  //1:删除调整后的任务 2：编辑调整后的任务 3:新建调整任务
			String p0400=(String)hm.get("p0400");
			String model=(String)hm.get("model");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String adjustDesc=(String)this.getFormHM().get("adjustDesc");
			RecordVo vo=new RecordVo("p04");
			vo.setInt("p0400", Integer.parseInt(p0400));
			vo=dao.findByPrimaryKey(vo);
			
			LoadXml loadxml=new LoadXml(this.getFrameconn(),(String)this.getFormHM().get("planid"));
			Hashtable planParam=loadxml.getDegreeWhole();
			String taskAdjustNeedNew=(String)planParam.get("taskAdjustNeedNew");
			String TargetAllowAdjustAfterApprove=(String)planParam.get("TargetAllowAdjustAfterApprove");
			if("1".equals(opt))
			{
				vo.setInt("state",-1);
				vo.setInt("chg_type",3);
				vo.setString("p0425", adjustDesc);
				String adjustDate=(String)this.getFormHM().get("adjustDate");
				String[] temps=adjustDate.split("-");
				Calendar d=Calendar.getInstance();
				d.set(Calendar.YEAR, Integer.parseInt(temps[0]));
				d.set(Calendar.MONTH,Integer.parseInt(temps[1])-1);
				d.set(Calendar.DATE, Integer.parseInt(temps[2]));
				vo.setDate("p0427",d.getTime());
				vo.setString("p0424", this.userView.getDbname()+this.userView.getA0100()+"/"+this.userView.getUserFullName());
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
				vo.setDate("opttime", df.format(new Date()));
				vo.setString("optname", this.userView.getUserFullName());
				dao.updateValueObject(vo);
			}
			else if("2".equals(opt))
			{
				String before_value=(String)this.getFormHM().get("before_value");
				String after_value=(String)this.getFormHM().get("after_value");
				String pointContent=(String)this.getFormHM().get("pointContent");
				String adjustDate=(String)this.getFormHM().get("adjustDate");
				
				ArrayList adjustBeforePointList=(ArrayList)this.getFormHM().get("adjustBeforePointList");
				
				
				
				vo.setInt("state",-1);
				vo.setInt("chg_type",1);
				vo.setString("p0425", adjustDesc);
				vo.setString("p0407", pointContent);
				String status=(String)this.getFormHM().get("status");  //　０:分值模版  1:权重模版
				if("0".equals(status))
				{
					if("False".equalsIgnoreCase(taskAdjustNeedNew))
					{
						vo.setDouble("p0413", Double.parseDouble(before_value));
						vo.setDouble("p0421", Double.parseDouble(after_value));
					}
					else
					{
						vo.setDouble("p0413", Double.parseDouble(after_value));
						vo.setDouble("p0421", Double.parseDouble(after_value));
					}
				}
				else
				{
					if("False".equalsIgnoreCase(taskAdjustNeedNew))
					{
						vo.setDouble("p0415", Double.parseDouble(before_value)/100);
						vo.setDouble("p0423", Double.parseDouble(after_value)/100);
					}
					else
					{
						vo.setDouble("p0415", Double.parseDouble(after_value)/100);
						vo.setDouble("p0423", Double.parseDouble(after_value)/100);
					}
				}
				String[] temps=adjustDate.split("-");
				Calendar d=Calendar.getInstance();
				d.set(Calendar.YEAR, Integer.parseInt(temps[0]));
				d.set(Calendar.MONTH,Integer.parseInt(temps[1])-1);
				d.set(Calendar.DATE, Integer.parseInt(temps[2]));
				vo.setDate("p0427",d.getTime());
				
				if(adjustBeforePointList!=null&&adjustBeforePointList.size()>0)
				{
					LazyDynaBean abean=null;
					for(int i=0;i<adjustBeforePointList.size();i++)
					{
						abean=(LazyDynaBean)adjustBeforePointList.get(i);
						 String itemid=(String)abean.get("itemid");
						 String itemtype=(String)abean.get("itemtype");
						 String codesetid=(String)abean.get("codesetid");
						 String itemdesc=(String)abean.get("itemdesc");
						 String value=(String)abean.get("value");
						 String decimalwidth=(String)abean.get("decimalwidth");
						 String state=(String)abean.get("state");
						 String viewvalue=(String)abean.get("viewvalue");
						 if(value!=null&&value.length()>0)
						 {
							
								
							
							 if("score_org".equalsIgnoreCase(itemid))
							 {
								 if("root".equalsIgnoreCase(value))
									 value="";
								 if(viewvalue==null|| "".equals(viewvalue.trim()))
									 value="";
								 if(value.toUpperCase().indexOf("UM")!=-1||value.toUpperCase().indexOf("UN")!=-1||value.toUpperCase().indexOf("@K")!=-1)
							    	 vo.setString(itemid, value.substring(2));
								 else
									 vo.setString(itemid,value);
							 }
							 else  if("N".equalsIgnoreCase(itemtype))
							 {
									 if("0".equals(decimalwidth))
										 vo.setInt(itemid.toLowerCase(),Integer.parseInt(value));
									 else 
										 vo.setDouble(itemid.toLowerCase(),Double.parseDouble(value));
								
							 }
							 else if("A".equalsIgnoreCase(itemtype)|| "M".equalsIgnoreCase(itemtype))
							 {
								 if("A".equalsIgnoreCase(itemtype)&&codesetid!=null&&codesetid.length()!=0&&!"0".equals(codesetid)&&viewvalue.trim().length()==0)
									 value="";
								vo.setString(itemid.toLowerCase(),value);
							 }
							 else if("D".equalsIgnoreCase(itemtype))
							 {
								 Calendar a=Calendar.getInstance();
								 String[] temp=value.split("-");
								 a.set(Calendar.YEAR, Integer.parseInt(temp[0]));
								 a.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
								 a.set(Calendar.DATE, Integer.parseInt(temp[2]));
								 vo.setDate(itemid.toLowerCase(), a.getTime());
							 }
						 }else {
							 if("N".equalsIgnoreCase(itemtype))
							 {
								 if("0".equals(decimalwidth))
									 vo.setInt(itemid.toLowerCase(), 0);
								 else
									 vo.setDouble(itemid.toLowerCase(), 0);
							 }
							 else if("D".equalsIgnoreCase(itemtype))
							 {
								 Date a=null;
								 vo.setDate(itemid.toLowerCase(), a);
							 }
							 else if("A".equalsIgnoreCase(itemtype))
							 {
								 vo.setString(itemid.toLowerCase(), "");
							 }
							 else if("M".equalsIgnoreCase(itemtype))
							 {
								 vo.setString(itemid.toLowerCase(), "");
							 }
							 else
							 {
								 vo.setString(itemid.toLowerCase(), "");
							 }
						 }
						
					}
				}
				vo.setString("p0424", this.userView.getDbname()+this.userView.getA0100()+"/"+this.userView.getUserFullName());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
				vo.setDate("opttime", df.format(new Date()));
				vo.setString("optname", this.userView.getUserFullName());
				dao.updateValueObject(vo);
			}
			else if("3".equals(opt))
			{
				String before_value=(String)this.getFormHM().get("before_value");
				String after_value=(String)this.getFormHM().get("after_value");
				String pointContent=(String)this.getFormHM().get("pointContent");
				String adjustDate=(String)this.getFormHM().get("adjustDate");
				
				vo.setInt("state",-1);
				vo.setInt("chg_type",3);
				vo.setString("p0425", adjustDesc);
				String[] temps=adjustDate.split("-");
				Calendar d=Calendar.getInstance();
				d.set(Calendar.YEAR, Integer.parseInt(temps[0]));
				d.set(Calendar.MONTH,Integer.parseInt(temps[1])-1);
				d.set(Calendar.DATE, Integer.parseInt(temps[2]));
				vo.setDate("p0427",d.getTime());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
				vo.setDate("opttime", df.format(new Date()));
				vo.setString("optname", this.userView.getUserFullName());
				dao.updateValueObject(vo);
				int bf_p0400=vo.getInt("p0400");
				
				IDGenerator idg=new IDGenerator(2,this.getFrameconn());
				String id=idg.getId("P04.P0400");
				vo.setInt("p0400", Integer.parseInt(id));
				vo.setString("p0407", pointContent);
				vo.setInt("state",-1);
				vo.setInt("chg_type",1);
				vo.setInt("fromflag",vo.getInt("fromflag"));//调整需新建的，fromflag不变，
				vo.setInt("f_p0400", Integer.parseInt(p0400));
				vo.removeValue("p0425");
				vo.removeValue("p0427");
				String status=(String)this.getFormHM().get("status");  //　０:分值模版  1:权重模版
				if("0".equals(status))
				{
					vo.setDouble("p0413", Double.parseDouble(after_value));
				}
				else
				{
					vo.setDouble("p0415", Double.parseDouble(after_value)/100);
				}
				vo.setInt("processing_state",0);
				
				
				RecordVo plan_vo=new RecordVo("per_plan");
				plan_vo.setInt("plan_id",vo.getInt("plan_id"));
				plan_vo=dao.findByPrimaryKey(plan_vo);
				String object_id="";
				if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
					object_id=vo.getString("b0110");
				else
					object_id=vo.getString("a0100");
				int seq=getP0400Seq(String.valueOf(bf_p0400),object_id,vo.getInt("plan_id"),plan_vo);
				vo.setInt("seq",seq);
				vo.setString("p0424", this.userView.getDbname()+this.userView.getA0100()+"/"+this.userView.getUserFullName());
				vo.setDate("opttime", df.format(new Date()));
				vo.setString("optname", this.userView.getUserFullName());
				dao.addValueObject(vo);
				
			    if("True".equalsIgnoreCase((String)planParam.get("TaskSupportAttach"))) //任务支持附件上传
		        {
			    	dao.update("update per_article set task_id="+Integer.parseInt(id)+" where Article_type=3 and task_id="+bf_p0400);
			    	
		        }
				
			}
			
			
			if("True".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&("2".equals(model)|| "1".equals(model)))  //我的目标
			{
				String plan_id=vo.getString("plan_id");
				//String a0100=vo.getString("a0100");
				
				RecordVo plan_vo=new RecordVo("per_plan");
				plan_vo.setInt("plan_id",vo.getInt("plan_id"));
				plan_vo=dao.findByPrimaryKey(plan_vo);
				String object_id="";
				if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
				{
					object_id=vo.getString("b0110");
					
					ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.getUserView(),plan_id);
					
					LazyDynaBean un_functionaryBean=bo.getMainbodyBean(plan_id,object_id);
					if(un_functionaryBean!=null)
					{	
						String un_functionary=(String)un_functionaryBean.get("mainbody_id");
						if(this.userView.getA0100().equalsIgnoreCase(un_functionary))
						{
							dao.update("update per_object set trace_sp_flag='01' where sp_flag='03' and object_id='"+object_id+"' and plan_id="+plan_id);
							dao.update("update per_object set sp_flag='01' where sp_flag='03' and object_id='"+object_id+"' and plan_id="+plan_id);
							/**将考核主体的审批状态，全部清掉*/
				    		StringBuffer sql = new StringBuffer("");
				    		sql.append("update per_mainbody set sp_flag=null,sp_date=null where plan_id=");
				    		sql.append(plan_id+" and object_id='"+object_id+"'");
						}
					}
				}
				else
				{
					object_id=vo.getString("a0100");
					if(this.userView.getA0100().equalsIgnoreCase(object_id))
					{
						dao.update("update per_object set trace_sp_flag='01' where  sp_flag='03' and object_id='"+object_id+"' and plan_id="+plan_id);
						dao.update("update per_object set sp_flag='01' where  sp_flag='03' and object_id='"+object_id+"' and plan_id="+plan_id);
						/**将考核主体的审批状态，全部清掉*/
			    		StringBuffer sql = new StringBuffer("");
			    		sql.append("update per_mainbody set sp_flag=null,sp_date=null where plan_id=");
			    		sql.append(plan_id+" and object_id='"+object_id+"'");
					}
				}
				
				
			
			}
			/**执行计算指标*/
			ObjectCardBo bo = new ObjectCardBo(this.getFrameconn(),this.getUserView(),(String)this.getFormHM().get("planid"));
			bo.calcFormulaField(p0400);
						
			String object_id = (String)this.getFormHM().get("object_id");		    
		    String objectSpFlag = (String)this.getFormHM().get("objectSpFlag");	
		    bo.setObject_id(object_id);
		    // 修改员工已批的目标卡时程序将自动发送邮件给员工  JinChunhai 2013.03.19
		    if(objectSpFlag!=null && objectSpFlag.trim().length()>0 && "03".equalsIgnoreCase(objectSpFlag) && !object_id.equalsIgnoreCase(this.userView.getA0100()))
		    	bo.sendEmailObj(p0400,"edit",new ArrayList());
		    			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public int getP0400Seq(String p0400,String object_id,int plan_id,RecordVo plan_vo)
	{
		int seq=0;
		try
		{
			  
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			RowSet rowSet=dao.search("select seq from p04 where p0400="+p0400);
			if(rowSet.next())
			{
					if(rowSet.getString(1)!=null);
						seq=rowSet.getInt(1);
			}
			if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
			{
					dao.update("update p04 set seq="+Sql_switcher.isnull("seq","0")+"+1 where   b0110='"+object_id+"'  and plan_id="+plan_vo.getInt("plan_id")+" and "+Sql_switcher.isnull("seq","0")+">"+seq);
			}
			else
					dao.update("update p04 set seq="+Sql_switcher.isnull("seq","0")+"+1 where   a0100='"+object_id+"'  and plan_id="+plan_vo.getInt("plan_id")+" and "+Sql_switcher.isnull("seq","0")+">"+seq);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seq+1;
	}
}
