package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * <p>BatchSaveFieldTrans.java</p>
 * <p>Description:批量另存指标</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2012-9-19</p> 
 * @author xuz
 * @version 1.0
 */
public class BatchSaveFieldTrans  extends IBusiness {

	public void execute() throws GeneralException {
		RowSet rs=null;
		RowSet rs2 = null;
		try
		{
		String point_id = (String)this.getFormHM().get("point_id");
		String pointsetid = (String)this.getFormHM().get("pointsetid");
		String subsys_id = (String)this.getFormHM().get("subsys_id");
		String b0110 = "";
		KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
		KhFieldBo boo = new KhFieldBo(this.getFrameconn(),this.userView);
		String sql2="select b0110 from per_pointset where pointsetid='"+pointsetid+"'";

		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		
		int seq = bo.getMaxId("per_point", "seq");
		String [] point = point_id.replaceAll("／", "/").split("/");
		String sql ="";
	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		rs2 = dao.search(sql2);
		while(rs2.next())
		{
			b0110 =rs2.getString("b0110");
		}
		String point_idx ="";
		String isgs="1";
		UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
		
		
		String hql="";
		RowSet ros=null;
		String str="";
		StringBuffer msg=new StringBuffer("");;
		for(int i =0;i<point.length ;i++)
		{   
			point_id = point[i];
			hql="select Gradecode from per_grade where Point_id='"+point_id+"'";
			ros=dao.search(hql);
			while(ros.next()){
				String Gradecode=ros.getString("Gradecode");
				str+=Gradecode+",";
			}
			
		}
		String[] ss=str.split(",");
		for(int i=0;i<ss.length;i++){
			String ssql = " select grade_template_id from "+per_comTable+" where grade_template_id='"+ss[i]+"'";
			ros = dao.search(ssql);
			if(ros.next())
			{
				continue;
			}
			else
			{
				msg.setLength(0);
				msg.append(ResourceFactory.getProperty("kh.field.yz_code")+"!");
				break;
			}
		}
		if("".equals(msg.toString())){//如果没有错误
	//		String yxb0110 = KhFieldTree.getyxb0110(this.userView,this.getFrameconn());
	//		if(!b0110.equals(yxb0110)&&!this.userView.isSuper_admin()&&!this.userView.getGroupId().equals("1")&&!b0110.equalsIgnoreCase("hjsj")){
	//			isgs="2";//没有另存的权限
	//			
	//			this.getFormHM().put("isgs",isgs);
	//			}
	//		else{
			//对每一个指标进行储存
			for(int i =0;i<point.length ;i++)
			{   
				point_idx=bo.getNextSeq("point_id", "per_point");
				point_id = point[i];
				if(!(this.userView.isSuper_admin())&&!"1".equals(this.userView.getGroupId()))
				{
					
					user_bo.saveResource(point_idx,this.userView,IResourceConstant.KH_FIELD);//新增指标是增加维护的权限
				}
				 sql="select pointname from per_point where point_id='"+point_id+"'";
				 
				this.frowset=dao.search(sql);
				String pointname ="";
				while(this.frowset.next()){
					pointname =this.frowset.getString("pointname")+"〔复件〕";
					
				}
			    bo.FieldSaveAs(point_id, point_idx, seq, pointname,pointsetid);
			    seq++;
			    
			    
			    
			    sql2 ="select * from per_grade where point_id='"+point_id+"'";
			    rs=dao.search(sql2);
				
			    while(rs.next()){
					
					int grade_id = new Integer(boo.getMaxNextId("per_grade","grade_id")).intValue();
					//grade_id =this.frowset.getString("grade_id");
					bo.ScaleFieldSave(point_id, point_idx, String.valueOf(grade_id),rs.getString("grade_id"));
				}
			}
		}	

		this.getFormHM().put("isgs",isgs);
		this.getFormHM().put("msg",msg.toString());
		
		
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
