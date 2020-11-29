package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ExcecuteGradeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String object_id="";
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		if("0".equals((String)hm.get("b_grade")))
		{
			object_id=(String)hm.get("object_id");
		}
		else
		{
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedlist");
			for(Iterator t=selectedList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				/**这里面的a0100是加密的,所以解密回来**/
				object_id=(String)abean.get("a0100");
				object_id = PubFunc.decrypt(object_id);
			}
		}
		
		try
		{
			String z0127="";
			
			String employTypeFiled="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
			HashMap map = bo2.getAttributeValues();
			if (map != null && map.get("hire_object") != null)//招聘对象指标 获得招聘对象指标
				employTypeFiled = (String) map.get("hire_object");
			if(employTypeFiled==null|| "".equals(employTypeFiled))
				throw GeneralExceptionHandler.Handle(new Exception("参数设置模块没有设置招聘对象指标！"));
			/**获得招聘配置的人员库**/
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbName=zpDbNameVo.getString("str_value");
            ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
            String resume_state_field = (String) map.get("resume_state");//获得配置的简历状态指标
			int advanceFlag=testTemplatAdvance.size();
			String hireState="";
			String sql="select z03."+employTypeFiled+",z0301,"+dbName+"A01."+resume_state_field+" hirestate from zp_pos_tache,z03,"+dbName+"A01 where zp_pos_tache.zp_pos_id=z03.z0301  and zp_pos_tache.resume_flag='12'   and zp_pos_tache.a0100='"+object_id+"' and ";
			sql=sql+dbName+"A01.a0100='"+object_id+"'";
			this.frowset=dao.search(sql);//要操作人的招聘渠道 这个人简历状态是已选
			String z0301="";
			if(this.frowset.next()){
				z0127=this.frowset.getString(1);
				//z0301=Sql_switcher.readMemo(frowset, "z0301");  
				z0301=this.frowset.getString("z0301");
				hireState=this.frowset.getString("hirestate");
			}
			if(z0127==null|| "".equals(z0127))
				throw GeneralExceptionHandler.Handle(new Exception("职位需求没有设置招聘对象！"));
			
			String scoreFlag="";//标志着是  混合打分还是标度打分
			String template_id="";//为每个职位定义的测评表  因为高级测评的测评表ID是存在于参数配置中的xml中的因此将这个字段提前
			String status="0";//权重分值标识 0：分值  1：权重
			String titleName="";
			String currentAndvance="0";
			if(advanceFlag>0){//配置了高级测评方式
				for(int i=0;i<testTemplatAdvance.size();i++){
					 HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
					 String hire_obj_code=(String) advanceMap.get("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
					 String interview=(String) advanceMap.get("interview");//得到面试状态也就是简历的状态 1：初试 2：复试
					 if(hire_obj_code.equalsIgnoreCase(z0127)&&interview.equalsIgnoreCase(hireState)){
						 template_id=(String) advanceMap.get("templateId");//得到模版号
						 scoreFlag=(String)advanceMap.get("mark_type");//是否采用复杂打分
						 currentAndvance="1";
						 break;
					 }
				}
			}
			if(template_id.trim().length()<=0){
				HashMap scorFlagmap=bo2.getMarkTypebyHireObjectCode();
				//这里先去查询有没有为岗位设置测评表
				DemandCtrlParamXmlBo DemandCtrlParamXmlBo = new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
				scoreFlag =DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "type");//=2混合，=1标度
				template_id = DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "id");//模板号
				//如果没有为岗位设置测评表取招聘管理配置参数中设置的测评表
		        if(template_id==null|| "".equals(template_id.trim())|| "#".equals(template_id.trim())){
				    template_id=(String)map.get("testTemplateID_"+z0127);
		        	scoreFlag=(String)scorFlagmap.get(z0127);
		        }
			}

		  this.frowset=dao.search("select * from per_template where template_id='"+template_id+"'");
		  if(this.frowset.next()){
				status=this.frowset.getString("status");
				titleName=this.frowset.getString("name");
		  }
			
		  SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn);
		  singleGradeBo.setCurrentAndvance(currentAndvance);//设置当前人员当前测评阶段当前招聘渠道是否设置了高级测评方式
		  singleGradeBo.setHireState(hireState);
		  object_id=object_id+"/1";
		  String[] tt=object_id.split("/");
			
		  ArrayList lists=getGradeUserList(tt[0]);
		  String mainBodyID=this.userView.getDbname()+this.getUserView().getUserId();
		  if("0".equals((String)hm.get("b_grade"))){
		    	mainBodyID=(String)hm.get("mainbodyID");
		  }else{
		    if("block".equals((String)lists.get(1)))
		    {
		    	CommonData date=(CommonData)((ArrayList)lists.get(0)).get(0);
		    	mainBodyID=date.getDataValue();
		    	
		    }
		  }
		    mainBodyID = mainBodyID.toUpperCase(); // 大写，用于在页面下拉框选中正确的主体 by 刘蒙
		    String id=mainBodyID.substring(3);//考官人员编号
		    String name="";//考官登录名
//		    String ssql="select OperUser.UserName  from OperUser,UsrA01 where OperUser.A0100=UsrA01.A0100 and OperUser.A0100='"+id+"'";
//		    ResultSet rs=null;
//		    rs=dao.search(ssql);
//		    String name="";
//		    while(rs.next()){
//		    	name=rs.getString("UserName");
//		    }

		    String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
		    sql="select * from "+mainBodyID.substring(0,3)+"A01 where A0100='"+id+"'";
		    this.frowset=dao.search(sql);
		    while(this.frowset.next()){
		    	name=this.frowset.getString(loguser);
		    }
		    if(name==null){
		    	throw GeneralExceptionHandler.Handle(new Exception("考官没有设置登录名!"));
		    }
		    UserView userView1=new UserView(name,this.getFrameconn());
		    String privPoint="";
			if(userView1.canLogin(false))
				privPoint=this.getFieldPriv(dao,userView1, template_id, mainBodyID);
//		    String privPoint = singleGradeBo.anaysePrivPoint(mainBodyID, "4");//有权限的指标
////		    String privPoint="";		    
//			UserView userView1=new UserView(name,this.getFrameconn());
//		    if(userView1.canLogin(false))
//		    {
//		    }
//	    	if(userView1.isSuper_admin()){
//	    		privPoint="";
//	    	}else{
//	    		if(privPoint.equals("")){
//	    			 privPoint="*****随便加的";
//	    		}
//	    	}
	        singleGradeBo.setPrivPointStr(privPoint);
		    singleGradeBo.setFromType("2");
		    ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,status,mainBodyID.substring(3),tt[0],tt[1],titleName,scoreFlag);		   
		    
		    
		    this.getFormHM().put("gradeUserList",(ArrayList)lists.get(0));
		    this.getFormHM().put("isSelfGrade",(String)lists.get(1));
		    
		    this.getFormHM().put("gradeHtml",(String)list.get(0));		  
		    this.getFormHM().put("isNull",(String)list.get(2));
		    this.getFormHM().put("scoreflag",(String)list.get(3));
		    if(((String)list.get(4)).length()>0)
		    	this.getFormHM().put("dataArea",((String)list.get(4)).substring(1));		   
		    this.getFormHM().put("mainBodyId",mainBodyID);
		    this.getFormHM().put("templateId",template_id);
		    this.getFormHM().put("z0127",z0127);
	
		    this.getFormHM().put("lay",(String)list.get(9));
		    this.getFormHM().put("status",status);
		    this.getFormHM().put("object_id",object_id);
		    if(advanceFlag<=0){//如果不是高级测评方式
		        this.getFormHM().put("hireState", "0");//如果不是高级测评方式,interview的状态设置成0
		    }else{
		        this.getFormHM().put("hireState", hireState);//如果是高级测评方式,interview的状态设置成当前人员的状态
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		

	}
	/**
	 * 获得考官对应考核模板的指标权限
	 * @param dao
	 * @param userView
	 * @param template_id
	 * @param mainBodyID
	 * @return
	 */
	
	private String getFieldPriv(ContentDAO dao,UserView userView,String template_id,String mainBodyID){
		String privPoints="";
		String sql="select point_id from per_template_point where item_id in(select item_id from  per_template_item where template_id='"+template_id+"')";
		String point_id="";
		try {
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				point_id=this.frowset.getString("point_id");
				if(userView.isSuper_admin()||userView.isHaveResource(IResourceConstant.KH_FIELD,point_id)){
					privPoints+=point_id+",";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(privPoints.length()>0){
			privPoints=privPoints.substring(0, privPoints.length()-1);
		}
		return privPoints;
	}
	public ArrayList getGradeUserList(String objectID) throws GeneralException
	{
		ArrayList list=new ArrayList();
		ArrayList gradeUserList=new ArrayList();
		String    isSelfGrade="block";                 //block:替人打分  none：自己打分
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search("select * from z05 where a0100='"+objectID+"'");
			StringBuffer a0100=new StringBuffer("");
			String z0505="";
			String z0507="";
			if(this.frowset.next())
			{
				z0505=this.frowset.getString("z0505");
				z0507=this.frowset.getString("z0507");
			}
			if(z0505!=null&&z0505.trim().length()>1)
			{
				for(int i=0;i<z0505.split(",").length;i++)
				{
					a0100.append(",'"+z0505.split(",")[i]+"'");
				}
				
			}
			if(z0507!=null&&z0507.trim().length()>1)
			{
				for(int i=0;i<z0507.split(",").length;i++)
				{
					a0100.append(",'"+z0507.split(",")[i]+"'");
				}
				
			}
			
			if(a0100.length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("没有设置考官"));
			this.frowset=dao.search("select pre from dbname");
        	HashMap tmap = new HashMap();
        	HashMap amap = new HashMap();
        	while(this.frowset.next())
        	{
        		tmap.put(this.frowset.getString(1).toUpperCase(), "1");
        	}
        	if(z0505!=null&&!"".equals(z0505))
        	{
        	String[] aa_z0505=z0505.split(",");
			for(int index = 0;index < aa_z0505.length;index++)
			{
				if(aa_z0505[index]==null|| "".equals(aa_z0505[index]))
					continue;
				if(tmap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
				{
					if(amap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
					{
						String t= (String)amap.get(aa_z0505[index].substring(0,3).toUpperCase());
						t+=","+aa_z0505[index].substring(3);
						amap.put(aa_z0505[index].substring(0,3).toUpperCase(), t);
					}
					else
					{
						amap.put(aa_z0505[index].substring(0,3).toUpperCase(), ","+aa_z0505[index].substring(3));
					}
				}
				else
				{
					String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
					t+=","+aa_z0505[index];
					amap.put("USR", t);
				}
			}
        	}
			if(z0507!=null&&!"".equals(z0507))
			{
				String[] aa_z0507=z0507.split(",");
				for(int index = 0;index < aa_z0507.length;index++)
				{
					if(aa_z0507[index]==null|| "".equals(aa_z0507[index]))
						continue;
					if(tmap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
					{
						if(amap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
						{
							String t= (String)amap.get(aa_z0507[index].substring(0,3).toUpperCase());
							t+=","+aa_z0507[index].substring(3);
							amap.put(aa_z0507[index].substring(0,3).toUpperCase(), t);
						}
						else
						{
							amap.put(aa_z0507[index].substring(0,3).toUpperCase(), ","+aa_z0507[index].substring(3));
						}
					}
					else
					{
						String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
						t+=","+aa_z0507[index];
						amap.put("USR", t);
					}
				}
			}
			Set keySet = amap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key = (String)t.next();
				String str=(String)amap.get(key);
				if(str!=null&&str.length()>0)
				{
					str="'"+str.substring(1).replaceAll(",", "','")+"'";
					this.frowset=dao.search("select a0100,a0101 from "+key+"A01 where a0100 in ("+str+")");
					while(this.frowset.next())
					{
						//map.put(key.toUpperCase()+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
						if(this.frowset.getString("a0100").equalsIgnoreCase(this.getUserView().getUserId()))
							isSelfGrade="none";
						gradeUserList.add(new CommonData(key+this.frowset.getString("a0100"), this.frowset.getString("a0101")));
					}
				}
			}
			/*this.frowset=dao.search("select a0100,a0101 from usra01 where a0100 in ("+a0100.substring(1)+")");
			while(this.frowset.next())
			{
				if(this.frowset.getString("a0100").equalsIgnoreCase(this.getUserView().getUserId()))
					isSelfGrade="none";
				gradeUserList.add(new CommonData(this.frowset.getString("a0100"), this.frowset.getString("a0101")));
			}
			*/
			list.add(gradeUserList);
			list.add(isSelfGrade);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	public static void main(String[] arg)
	{
		String sss="000005,000007,";
		System.out.println(sss.split(",").length);
		
	}
	

}
