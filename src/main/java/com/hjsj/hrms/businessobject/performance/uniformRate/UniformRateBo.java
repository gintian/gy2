package com.hjsj.hrms.businessobject.performance.uniformRate;

import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:统一打分</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 23, 2008:4:56:55 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class UniformRateBo {
	private String planid;
	private String templateid;
	private Connection conn=null;
	private ArrayList slist;
	private String per_plan;
	
	
	public UniformRateBo(Connection a_con){
		this.conn=a_con;
	}
	public UniformRateBo(Connection a_con,String planid,String templateid){
		this.planid=planid;
		this.templateid=templateid;
		this.conn=a_con;
	}
	/**
	 * 对比更新
	 * @param a_con
	 * @param slist
	 * @param name
	 */
	public UniformRateBo(Connection a_con,ArrayList slist,String per_plan){
		this.slist=slist;
		this.per_plan=per_plan;
		this.conn=a_con;
	}
    /**判断定量统一打分指标对考核对象权限的类*/
    private Permission pointPrivBean=null;
    private UserView userView = null;
    
	public UniformRateBo(Connection a_con,ArrayList slist,String per_plan,UserView userView){
		this.slist=slist;
		this.per_plan=per_plan;
		this.conn=a_con;
		this.userView=userView;
		this.pointPrivBean=new Permission(this.conn,this.userView);
	}
	
	
	/**
	 * 取得模板下的指标列表
	 * @param templateid
	 * @return
	 */
	public ArrayList getPointList(String templateid)
	{	
		ArrayList list=new ArrayList();
		ResultSet rs=null;  
		
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select khzb.point_id,khzb.pointkind,khzb.Pointtype,khzb.status, pointname,khzb.pointctrl,mbys.score as MaxScore from per_template khmb,per_template_item mbxm,per_template_point mbys,per_point khzb where khmb.template_id=mbxm.template_id and mbxm.item_id=mbys.item_id and mbys.point_id=khzb.point_id and khmb.template_id='"+this.templateid+"' order by mbys.seq";          

		try {
			rs = dao.search(sql); 
		
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("point_id",rs.getString("point_id") );
				bean.set("pointname",rs.getString("pointname") );
				bean.set("MaxScore", rs.getString("MaxScore"));
				String pointctrl = Sql_switcher.readMemo(rs, "pointctrl");
				pointctrl=pointctrl==null?"":pointctrl;
//				HashMap xmlmap = PointCtrlXmlBo.getAttributeValues(pointctrl);
//				String rule = (String) xmlmap.get("computeRule");// rule=0|1|2|3 计分规则（录分｜简单｜分段｜排名）
//				rule=rule==null?"":rule;
//				if(!rule.equals("0"))//只显示录分指标
//					continue;				
				
				String Pointctrl =  rs.getString("Pointctrl");				
				String pointkind = rs.getString("pointkind");
				String Pointtype = rs.getString("Pointtype");
				String status = rs.getString("status");
				//只显示录分指标
				if ("1".equals(pointkind) && (status != null && "1".equals(status)) && (Pointtype == null || "0".equals(Pointtype)))
			    {
					HashMap map = PointCtrlXmlBo.getAttributeValues(Pointctrl);
					if (map.get("computeRule") == null || map.get("computeRule") != null && "0".equals((String) map.get("computeRule")))
						list.add(bean);				
			    }				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return list;
	}
	
	
	/**
	 * 取得前台	DataSet  filedList的数据  getList
	 * @param pointList
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getRateList(ArrayList pointList) throws GeneralException
	{
		ArrayList list=new ArrayList();
		Field field=null;
		
		ExamPlanBo bo = new ExamPlanBo(this.planid,this.conn);
		String object_type = bo.getPlanVo().getString("object_type");
		
		if(pointList!=null){
			field=new Field("id","id");
			field.setLength(10);
			field.setDatatype(DataType.INT); //id为整型;
			field.setReadonly(true);
			field.setVisible(false);//隐藏字段的,如果为false该字段在前台不显示;
			list.add(field);
			
			if("2".equals(object_type))
			{
				field=new Field("B0110","单位");
				field.setLength(50);
				field.setCodesetid("UN");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				field=new Field("E0122","部门");
				field.setLength(50);
				field.setCodesetid("UM");   //关联的中文信息
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				field=new Field("E01A1","职位");
				field.setLength(50);
				field.setCodesetid("@K");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				field=new Field("A0101","姓名");
				field.setLength(50);
				field.setCodesetid("0");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
			}else if("1".equals(object_type))
			{
				field=new Field("A0101",ResourceFactory.getProperty("org.performance.unorum"));
				field.setLength(50);
				field.setCodesetid("0");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
			}
			else if("3".equals(object_type))
			{
				field=new Field("B0110",ResourceFactory.getProperty("tree.unroot.undesc"));
				field.setLength(50);
				field.setCodesetid("UN");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
			}else if("4".equals(object_type))
			{
				field=new Field("E0122",ResourceFactory.getProperty("column.sys.dept"));
				field.setLength(50);
				field.setCodesetid("UM");   //关联的中文信息
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
			}
		}
		for(int i=0;i<pointList.size();i++){
			LazyDynaBean abean=(LazyDynaBean)pointList.get(i);
			abean.get("point_id");
			abean.get("pointname");
			field=new Field("C_"+(String)abean.get("point_id"),(String)abean.get("pointname"));
			field.setLength(50);
			field.setDatatype(DataType.FLOAT);  //数据是浮点型的数据;
			field.setReadonly(false);     //false代表可以更改数据;
			field.setDecimalDigits(4);    //我这数据都是浮点型的数据,就要用这个方法来限制数字的位数;
			list.add(field);
		}
		return list;
		
	}
	
	/**
	 *生成sql语句 
	 * @param planid
	 * @param templateid
	 * @return
	 */
	public String getSql(String planid,ArrayList pointList){
		StringBuffer buf=new StringBuffer("");
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("select id,B0110,E0122,E01A1,object_id,A0101 ");
			for(int i=0;i<pointList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)pointList.get(i);
				buf.append(",C_"+(String)abean.get("point_id"));
			}
			buf.append(" from  per_result_"+this.planid);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	public String getPer_result(String planid){
		StringBuffer buf = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		buf.append("per_result_"+this.planid);
		
		return buf.toString();
		
	}
	/*
	 * 
	 * 通过per_plan的id,取得考核模板表id
	 */
	
	public ArrayList getPer_plan(String per_plan){
		ArrayList list=new ArrayList();
		
		ResultSet rs=null; 
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select a.plan_id,template_id from per_plan a where plan_id="+this.per_plan;
		
		try {
			rs = dao.search(sql);
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("plan_id", rs.getString("plan_id"));
				bean.set("template_id",rs.getString("template_id"));
				list.add(bean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return list;
			
	}
	
	/*
	 *更新对比里再次调用考核模板SQL语句
	 */
	public ArrayList getPointListDB(ArrayList per_planList)
	{	
		ArrayList list=new ArrayList();
		ResultSet rs=null;
		
		ContentDAO dao = new ContentDAO(this.conn);
		String name="";
		if(per_planList!=null&&per_planList.size()>0)
		{
			LazyDynaBean abean=(LazyDynaBean)per_planList.get(0);
			abean.get("template_id");
			name=(String)abean.get("template_id");
		}
		String sql = "select khzb.point_id,khzb.pointname,(mbys.score*mbys.rank) as MaxScoreBD from per_template khmb,per_template_item mbxm,per_template_point mbys,per_point khzb where khmb.template_id=mbxm.template_id and mbxm.item_id=mbys.item_id and mbys.point_id=khzb.point_id and khmb.template_id='"+name+"' order by mbys.seq";          
		//System.out.println("KKKKKK+"+sql);
		try {
			rs = dao.search(sql); 
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("point_id",rs.getString("point_id") );
				bean.set("pointname",rs.getString("pointname") );
				bean.set("MaxScoreBD", rs.getString("MaxScoreBD"));
				list.add(bean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return list;
	}
	/*
	 * 更新对比
	 */
	public ArrayList getUpdateList(ArrayList slist,ArrayList pointListDB)throws GeneralException{
 		ArrayList list=new ArrayList();
		try
		{
			
			ContentDAO dao = new ContentDAO(this.conn);
			
			for(int j=0;j<slist.size();j++){
				RecordVo vo=(RecordVo)slist.get(j);
				
				for(int i=0;i<pointListDB.size();i++){
					LazyDynaBean abean=(LazyDynaBean)pointListDB.get(i);
					String point = (String)abean.get("point_id");
					String pointname = (String)abean.get("pointname");
					//在此加入判断定量统一打分指标对考核对象是否有权限的代码控制--待加 
					
					double tipfen =Double.parseDouble((String)abean.get("MaxScoreBD"));
					
					double value=vo.getDouble("c_"+point.toLowerCase());  //直接对比找出名字一样的分数;
					if(tipfen>0 && (value>tipfen || value<0) )
						throw GeneralExceptionHandler.Handle(new Exception("请录入分值范围内的数值！(0-"+tipfen+")"));
					else if(tipfen<0 && (value<tipfen || value>0))
						throw GeneralExceptionHandler.Handle(new Exception("请录入分值范围内的数值！("+tipfen+"-0)"));
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
	

}
