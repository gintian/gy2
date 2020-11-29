package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddOrEditFieldTrans.java</p>
 * <p>Description:新建或编辑指标</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class AddOrEditFieldTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			//【2913】绩效管理：新建指标，定量指标计算公式，切换指标类别时，界面总一闪一闪的    jingq add 2014.12.10
			if(map==null){
				map = this.getFormHM();
			}
			KhFieldBo bo = new KhFieldBo(this.getFrameconn(),this.userView);
			String kpiTarget=(String)map.get("kpiTarget");
			map.remove("kpiTarget");
			String tabid=(String)map.get("tabid");
			if(kpiTarget!=null && kpiTarget.trim().length()>0 && "selecTarget".equalsIgnoreCase(kpiTarget))
			{
				/**用来判断页面显示哪个页签*/
				this.getFormHM().put("tabid",tabid);
				String kpiTargetType = (String)this.getFormHM().get("kpiTargetType");
				this.getFormHM().put("kpiTarget_idList", bo.getKpiTarget_idList(kpiTargetType));
				return;
			}
			
			String type=(String)map.get("type");
			String fieldnumber=(String)map.get("point_id");
			fieldnumber=fieldnumber.trim();
			CheckPrivSafeBo cpbo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean flag = cpbo.isHaveItemPriv(fieldnumber);
			if(!flag){
				return;
			}
			String pointsetid = (String)map.get("pointsetid");
		    String fieldname="";
		    String pointkind="0";
	        String fieldvlidflag="1";
			String description="";
			String proposal="";
			String visible="3";
		    String status="";
			String pointtype="0";
			String hiddennumber="";
			String display="";
			String ltype="1";
			String add_type="1";
			String minus_type="0";
			String rule="0";
			String add_value="";
			String minus_value="";
			String add_score="";
			String minus_score="";
			String add_valid="0";
			String minus_valid="0";
			String convert="0";	
			String kh_content="";
			String formula = "";    // 定义的指标公式
			String gd_principle="";
			
			ArrayList newgradeList = null;
			String rulePointid="";
			String xfieldnumber="";
			String yxb0110 = "";
			String b0110 = "";
			yxb0110 = KhFieldTree.getyxb0110(userView,this.getFrameconn());
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			
			String sql = "select b0110 from per_pointset where pointsetid ='"+pointsetid+"'";
			RowSet rs = null;
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				b0110 = rs.getString("b0110");
			}
			
			int yxb0110le = yxb0110.length();
			int b0110le = b0110.length();
			if(yxb0110le<b0110le)
				yxb0110le = yxb0110.length();
			else 
				yxb0110le = b0110.length();
		   if(!b0110.substring(0,yxb0110le).equals(yxb0110)&&!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())&&!"hjsj".equalsIgnoreCase(b0110)){
				throw GeneralExceptionHandler.Handle(new Exception("您没有该指标分类的编辑权限！"));
		   }
		   
			if("1".equals(type))//new
			{	
				
				fieldnumber=bo.getNextSeq("point_id", "per_point");
				newgradeList=bo.getGradeTemplateList(subsys_id);
				rulePointid="XXXXPPPP";
			}
			else//edit
			{
				
				rulePointid=fieldnumber;
				newgradeList=bo.getFieldGradeBr(fieldnumber,subsys_id);
				LazyDynaBean bean =bo.getFieldInfoById(fieldnumber);
				hiddennumber = (String)bean.get("point_id");
				if("3".equals(type))
				{
					xfieldnumber=bo.getNextSeq("point_id", "per_point");
					fieldname=(String)bean.get("pointname")+"(复件)";
					fieldname = PubFunc.keyWord_reback(fieldname);
					
				}else
				{
					fieldname=(String)bean.get("pointname");
					fieldname = PubFunc.keyWord_reback(fieldname);
				}
				pointkind=(String)bean.get("pointkind");
				fieldvlidflag=(String)bean.get("validflag");
				description=(String)bean.get("description");
				description = PubFunc.keyWord_reback(description);
				proposal=(String)bean.get("proposal");
				proposal = PubFunc.keyWord_reback(proposal);
				visible=(String)bean.get("visible");
				/* smk 2015.11.30 能力素质指标合并显示标度、指标显示，此处为兼容以前版本，visible为2时强转为1 */
				visible=visible==null|| "".equals(visible)?"3":visible;
				if ("35".equalsIgnoreCase(subsys_id)&& "2".equalsIgnoreCase(visible))
					visible = "1";
				status=(String)bean.get("status");
				pointtype=(String)bean.get("pointtype");
				kh_content=(String)bean.get("kh_content");
				kh_content = PubFunc.keyWord_reback(kh_content);
				formula=(String)bean.get("formula");
				formula = PubFunc.keyWord_reback(formula);
				gd_principle=(String)bean.get("gd_principle");
				gd_principle = PubFunc.keyWord_reback(gd_principle);
				StandardItemBo SIB = new StandardItemBo(this.getFrameconn());
				HashMap ruleMap = SIB.getRuleValue(fieldnumber);
				if(ruleMap!=null&& "1".equals(status)&& "1".equals(pointkind))
				{
					if(ruleMap.get("convert")!=null)
					{
						convert=(String)ruleMap.get("convert");
					}
					if(ruleMap.get("computeType")!=null)
					{
						ltype=(String)ruleMap.get("computeType");
					}
					if(ruleMap.get("computeRule")!=null)
					{
						rule=(String)ruleMap.get("computeRule");
					}
					if(ruleMap.get("addValid")!=null)
					{
						add_valid=(String)ruleMap.get("addValid");
					}
					if(ruleMap.get("addType")!=null)
					{
						add_type=(String)ruleMap.get("addType");
					}
					if(ruleMap.get("addValue")!=null)
					{
						add_value=(String)ruleMap.get("addValue");
					}
					if(ruleMap.get("addScore")!=null)
					{
						add_score=(String)ruleMap.get("addScore");
					}
					if(ruleMap.get("minusValid")!=null)
					{
						minus_valid=(String)ruleMap.get("minusValid");
					}
					if(ruleMap.get("minusType")!=null)
					{
						minus_type=(String)ruleMap.get("minusType");
					}
					if(ruleMap.get("minusValue")!=null)
					{
						minus_value=(String)ruleMap.get("minusValue");
					}
					if(ruleMap.get("minusScore")!=null)
					{
						minus_score=(String)ruleMap.get("minusScore");
					}
					if("0".equals(pointtype))
					{
						if("1".equals(rule))
	    		     	{
	    		     		if("1".equals(ltype))
	    		     		{
	    				        if(add_value!=null&&!"".equals(add_value))
	    				        {
	    				        	add_value=Double.parseDouble(add_value)*100+"";
	    			    	    }
	    				        if(minus_value!=null&&!"".equals(minus_value))
	    			    	    {
	    				        	minus_value=Double.parseDouble(minus_value)*100+"";
	    			    	    }
	    		     		}	    		     		
	    		     	}
					}
				}				
			}
			this.getFormHM().put("gd_principle",gd_principle);
			this.getFormHM().put("kh_content", kh_content);
//			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("fieldname", fieldname);
			this.getFormHM().put("pointkind", pointkind==null|| "".equals(pointkind)?"0":pointkind);
			this.getFormHM().put("fieldvlidflag", fieldvlidflag==null|| "".equals(fieldvlidflag)?"1":fieldvlidflag);
			this.getFormHM().put("description", description);
			this.getFormHM().put("proposal", proposal);
			this.getFormHM().put("visible", visible);
			this.getFormHM().put("status", status);
			this.getFormHM().put("pointtype", pointtype);
			if("3".equals(type))
			{
				this.getFormHM().put("fieldnumber", xfieldnumber);
			}else
			{
				this.getFormHM().put("fieldnumber", fieldnumber);
			}
			
			this.getFormHM().put("hiddennumber", hiddennumber);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("display", "0".equals(pointkind)?"display:none;":"display:block;");
			this.getFormHM().put("type",type);
			this.getFormHM().put("newgradeList",newgradeList);
			this.getFormHM().put("isClose","2");
			this.getFormHM().put("isrefresh", "1");
			this.getFormHM().put("ltype",ltype);
			this.getFormHM().put("add_type",add_type);
			this.getFormHM().put("minus_type", minus_type);
			this.getFormHM().put("rule",rule);
			this.getFormHM().put("add_value",add_value);
			this.getFormHM().put("minus_value", minus_value);
			this.getFormHM().put("add_score",add_score);
			this.getFormHM().put("minus_score",minus_score);
			this.getFormHM().put("add_valid",add_valid);
			this.getFormHM().put("minus_valid",minus_valid);
			this.getFormHM().put("convert",convert);
			this.getFormHM().put("rulePointid", rulePointid);
			/**用来判断页面显示哪个页签*/
			this.getFormHM().put("tabid",tabid==null?"1":tabid);
			
			
			/**定义指标公式  2011.08.05  JinChunhai */
			this.getFormHM().put("computeFormula", formula);
			
			String kpiTargetType = (String)this.getFormHM().get("kpiTargetType");
//			String kpiTarget_id = (String)this.getFormHM().get("kpiTarget_id");			
			this.getFormHM().put("kpiTargetTypeList", bo.getKpiTargetTypeList());
			this.getFormHM().put("kpiTarget_idList", bo.getKpiTarget_idList(kpiTargetType));
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
