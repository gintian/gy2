package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassObject;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 浏览基本班次
 * <p>Title:SearchClassTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 13, 2006 4:41:57 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SearchClassTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
	  String class_id=(String)this.getFormHM().get("class_id");
	  KqClassObject kqClassObject=new KqClassObject(this.getFrameconn());
	  kqClassObject.checkKqClassTable();//重构考勤班次表	 
	  RecordVo vo=new RecordVo("kq_class");
      String orgId ="";
      String orgName =""; 
      
	  if(class_id==null||class_id.length()<=0)
	  {
		  //class_id=getClassID();
		  this.getFormHM().put("class_vo",vo);
		  this.getFormHM().put("class_id",class_id);
		  this.getFormHM().put("save_flag","");
		  this.getFormHM().put("overlist",overTimeList());
		  this.getFormHM().put("orgName","");
		  this.getFormHM().remove("classType");
		  return;
	  }
	  String sql="select * from kq_class where class_id='"+class_id+"'";
	  
	  try
	  {
		  ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		  this.frowset=dao.search(sql);
		  if(this.frowset.next())
		  {
		     
			 vo.setString("class_id",this.frowset.getString("class_id"));
			 vo.setString("name",this.frowset.getString("name"));
			 vo.setString("onduty_card_1",this.frowset.getString("onduty_card_1"));
			 vo.setString("offduty_card_1",this.frowset.getString("offduty_card_1"));
			 vo.setString("onduty_card_2",this.frowset.getString("onduty_card_2"));
			 vo.setString("offduty_card_2",this.frowset.getString("offduty_card_2"));
			 vo.setString("onduty_card_3",this.frowset.getString("onduty_card_3"));
			 vo.setString("offduty_card_3",this.frowset.getString("offduty_card_3"));
			 vo.setString("onduty_card_4",this.frowset.getString("onduty_card_4"));
			 vo.setString("offduty_card_4",this.frowset.getString("offduty_card_4"));
			 
			 vo.setString("onduty_start_1",this.frowset.getString("onduty_start_1"));
			 vo.setString("onduty_1",this.frowset.getString("onduty_1"));			 
			 vo.setString("onduty_flextime_1",this.frowset.getString("onduty_flextime_1"));
			 vo.setString("be_late_for_1",this.frowset.getString("be_late_for_1"));
			 vo.setString("absent_work_1",this.frowset.getString("absent_work_1"));
			 vo.setString("onduty_end_1",this.frowset.getString("onduty_end_1"));
			 vo.setString("rest_start_1",this.frowset.getString("rest_start_1"));
			 vo.setString("rest_end_1",this.frowset.getString("rest_end_1"));
			 vo.setString("offduty_start_1",this.frowset.getString("offduty_start_1"));
			 vo.setString("leave_early_absent_1",this.frowset.getString("leave_early_absent_1"));
			 vo.setString("leave_early_1",this.frowset.getString("leave_early_1"));
			 vo.setString("offduty_1",this.frowset.getString("offduty_1"));
			 vo.setString("offduty_flextime_1",this.frowset.getString("offduty_flextime_1"));
			 vo.setString("offduty_end_1",this.frowset.getString("offduty_end_1"));
			 //2
			 vo.setString("onduty_start_2",this.frowset.getString("onduty_start_2"));
			 vo.setString("onduty_2",this.frowset.getString("onduty_2"));
			 vo.setString("onduty_flextime_2",this.frowset.getString("onduty_flextime_2"));
			 vo.setString("be_late_for_2",this.frowset.getString("be_late_for_2"));
			 vo.setString("absent_work_2",this.frowset.getString("absent_work_2"));
			 vo.setString("onduty_end_2",this.frowset.getString("onduty_end_2"));
			 vo.setString("rest_start_2",this.frowset.getString("rest_start_2"));
			 vo.setString("rest_end_2",this.frowset.getString("rest_end_2"));
			 vo.setString("offduty_start_2",this.frowset.getString("offduty_start_2"));
			 vo.setString("leave_early_absent_2",this.frowset.getString("leave_early_absent_2"));
			 vo.setString("leave_early_2",this.frowset.getString("leave_early_2"));
			 vo.setString("offduty_2",this.frowset.getString("offduty_2"));
			 vo.setString("offduty_flextime_2",this.frowset.getString("offduty_flextime_2"));
			 vo.setString("offduty_end_2",this.frowset.getString("offduty_end_2"));
			 //3
			 vo.setString("onduty_start_3",this.frowset.getString("onduty_start_3"));
			 vo.setString("onduty_3",this.frowset.getString("onduty_3"));
			 vo.setString("onduty_flextime_3",this.frowset.getString("onduty_flextime_3"));
			 vo.setString("be_late_for_3",this.frowset.getString("be_late_for_3"));
			 vo.setString("absent_work_3",this.frowset.getString("absent_work_3"));
			 vo.setString("onduty_end_3",this.frowset.getString("onduty_end_3"));
			 vo.setString("rest_start_3",this.frowset.getString("rest_start_3"));
			 vo.setString("rest_end_3",this.frowset.getString("rest_end_3"));
			 vo.setString("offduty_start_3",this.frowset.getString("offduty_start_3"));
			 vo.setString("leave_early_absent_3",this.frowset.getString("leave_early_absent_3"));
			 vo.setString("leave_early_3",this.frowset.getString("leave_early_3"));
			 vo.setString("offduty_3",this.frowset.getString("offduty_3"));
			 vo.setString("offduty_flextime_3",this.frowset.getString("offduty_flextime_3"));
			 vo.setString("offduty_end_3",this.frowset.getString("offduty_end_3"));
			 //4
			 vo.setString("onduty_start_4",this.frowset.getString("onduty_start_4"));
			 vo.setString("onduty_4",this.frowset.getString("onduty_4"));
			 vo.setString("onduty_flextime_4",this.frowset.getString("onduty_flextime_4"));
			 vo.setString("be_late_for_4",this.frowset.getString("be_late_for_4"));
			 vo.setString("absent_work_4",this.frowset.getString("absent_work_4"));
			 vo.setString("onduty_end_4",this.frowset.getString("onduty_end_4"));
			 vo.setString("rest_start_4",this.frowset.getString("rest_start_4"));
			 vo.setString("rest_end_4",this.frowset.getString("rest_end_4"));
			 vo.setString("offduty_start_4",this.frowset.getString("offduty_start_4"));
			 vo.setString("leave_early_absent_4",this.frowset.getString("leave_early_absent_4"));
			 vo.setString("leave_early_4",this.frowset.getString("leave_early_4"));
			 vo.setString("offduty_4",this.frowset.getString("offduty_4"));
			 vo.setString("offduty_flextime_4",this.frowset.getString("offduty_flextime_4"));
			 vo.setString("offduty_end_4",this.frowset.getString("offduty_end_4"));
			 //other
			 vo.setString("night_shift_start",this.frowset.getString("night_shift_start"));
			 vo.setString("night_shift_end",this.frowset.getString("night_shift_end"));
			 vo.setString("zeroflag",this.frowset.getString("zeroflag"));
			 vo.setString("domain_count",this.frowset.getString("domain_count"));
			 vo.setDouble("work_hours",this.frowset.getDouble("work_hours"));
			 orgId=this.frowset.getString("org_id");
			 orgId = "".equals(orgId)||orgId==null?"UN":orgId;
			 //linbz 所属部门优化为支持多个部门
			 String[] orglist = StringUtils.split(orgId, ",");
			 for(int i=0;i<orglist.length;i++){
				 String before = orglist[i].toString().substring(0,2);
	             String behind = orglist[i].toString().substring(2);
	             orgName  +=  AdminCode.getCodeName(before, behind) + ",";
			 }
			 if(orgName.indexOf(",") != -1){
				 orgName = orgName.substring(0, orgName.length()-1);
			 }
			 vo.setDouble("zero_absent",this.frowset.getDouble("zero_absent"));
			 vo.setDouble("one_absent",this.frowset.getDouble("one_absent"));
			 vo.setDouble("one_absent",this.frowset.getDouble("one_absent"));
			 
			 vo.setString("check_tran_overtime",this.frowset.getString("check_tran_overtime"));
			 vo.setInt("overtime_from",this.frowset.getInt("overtime_from"));
			 vo.setString("overtime_type",this.frowset.getString("overtime_type"));
			 
			 KqUtilsClass kqcl = new KqUtilsClass(this.getFrameconn(),this.userView);
			 // 取操作用户管理班次时的权限部门
			 String kqScope = kqcl.getKqClassManageCode();
			 
			 // 超级用户 或者 授权最顶级节点 UN 权限全部可编辑
			 if("UN".equalsIgnoreCase(kqScope)){
				 this.getFormHM().put("classType", "2");
			 }else{
				 if(StringUtils.isEmpty(orgId) || StringUtils.isEmpty(kqScope) 
	        			|| "UN".equalsIgnoreCase(orgId) || "null".equalsIgnoreCase(orgId)){
	        		// org_id等于空或UN 的班次为公共班次，不可编辑
	        		this.getFormHM().put("classType", "0");
				 }else{
	            	// 默认可编辑
	            	this.getFormHM().put("classType", "2");
	       			for(int j=0;j<orglist.length;j++){
	       				 String orgO = orglist[j];
	       				 if(orgO.substring(2).startsWith(kqScope.substring(2))){
	       					 // 该用户权限是本部门或者本部门的下属部门可以编辑
	       				 }else if(kqScope.substring(2).startsWith(orgO.substring(2))){
	       					 // 上级部门不可编辑
	       					 this.getFormHM().put("classType", "1");
	       					 break;
	       				 }else{
	       					 // 该班次是上级设置的多个同级部门不可编辑
	       					 this.getFormHM().put("classType", "1");
	       				 	}
       					}
				 }
			 }
			 
		  }
	  }catch(Exception e)
	  {
		e.printStackTrace();  
	  }
	  this.getFormHM().put("class_vo",vo);
	  this.getFormHM().put("class_id",class_id);
	  this.getFormHM().put("save_flag","");
	  this.getFormHM().put("overlist",overTimeList());
	  this.getFormHM().put("orgId",orgId);
	  this.getFormHM().put("orgName",orgName);
	}
    /**
     * 得到最小的ClassId
     * @return
     */
	public String getClassID()
	{
		String sql="select class_id from kq_class where class_id<>'0' order by class_id";
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		String class_id="1";
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				class_id=this.frowset.getString("class_id");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return class_id;
	}
	private ArrayList overTimeList()
	{
		ArrayList overlist=new ArrayList();
		CommonData datavo= new CommonData("-1","<自动判断>");
		overlist.add(datavo);
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		sql.append("SELECT codeitemid, codeitemdesc,parentid  FROM codeitem  where codesetid ='27'");
		sql.append(" and parentid like '1%'"); //取所有加班类型，不只是第一级的三类加班
		sql.append(" and codeitemid<>parentid");
		sql.append(" ORDER BY codeitemid");
		try
		{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{  
			   if(this.frowset.getString("codeitemid").length()>1)
			   {
				 datavo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				 overlist.add(datavo);
			   }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return overlist;
	}
}
