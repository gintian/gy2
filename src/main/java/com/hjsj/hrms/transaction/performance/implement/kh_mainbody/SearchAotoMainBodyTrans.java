package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchMainBodyTrans.java</p>
 * <p>Description:考核实施/指定考核主体/查找考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-08-12 10:45:24</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchAotoMainBodyTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String type = (String)hm.get("type");
    	String scopestr="";
    	if(type!=null&& "save".equals(type))
    	{
    		scopestr=(String)hm.get("scopestr");
    		hm.remove("type");
    	}
		String plan_id = (String)this.getFormHM().get("planid");
		if(plan_id==null||plan_id.trim().length()==0)
			throw GeneralExceptionHandler.Handle(new GeneralException("获得不到计划id！")); 
	
		//提示信息：没有设置主体范围或者条件
		HashMap setinfo = new HashMap();
		ArrayList setaotolist = this.getChildList(plan_id,type,scopestr,setinfo);
		this.getFormHM().put("setaotolist", setaotolist);
		String setinfomation ="";
		if(setinfo!=null&&setinfo.get("1")!=null)
			setinfomation = (String)setinfo.get("1");
		this.getFormHM().put("setinfomation", setinfomation);
    }
    
    private ArrayList getChildList(String plan_id,String type,String scopestr,HashMap setinfo)
    {

		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
	
		String strSql ="";
		    strSql = "Select P.cexpr cexpr1,P.cond cond1,P.scope scope1, B.* FROM per_plan_body P, per_mainbodyset B WHERE P.body_id = B.body_id and plan_id =" + plan_id + "  ORDER BY b.seq ";
		try
		{
	
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    this.frowset = dao.search(strSql);
		    int i =0;
		    String names ="";
		    while (this.frowset.next())
		    {
		    	RecordVo vo = new RecordVo("per_mainbodyset");
		    	RecordVo vo2 = new RecordVo("per_plan_body");
				vo.setString("body_id", this.frowset.getString("body_id"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setString("body_type", this.frowset.getString("body_type"));
				String  cond1 = Sql_switcher.readMemo(this.frowset, "cond1");
				String scope1 = this.frowset.getString("scope1");
				String cond ="";
				String cexpr ="";
				String  scope ="";
				if(cond1==null||cond1.trim().length()==0)
				{
					cond = Sql_switcher.readMemo(this.frowset, "cond");
					cexpr = Sql_switcher.readMemo(this.frowset, "cexpr");
					scope = this.frowset.getString("scope");
				}else
				{
					 cond = Sql_switcher.readMemo(this.frowset, "cond1");
					 cexpr = Sql_switcher.readMemo(this.frowset, "cexpr1");
					 scope = this.frowset.getString("scope1");
				}
				if(scope1==null||cond1.trim().length()==0)
				{
					scope = this.frowset.getString("scope");
				}else
				{
					scope = this.frowset.getString("scope1");
				}
				cond = cond==null?"":cond;
				cexpr = cexpr==null?"":cexpr;
				
				vo.setString("cond", SafeCode.encode(cond));
				vo.setString("cexpr", cexpr);	
//				if(scope!=null){
//				if(scope.equals("1")){
//					scope ="单位"; 
//				}else if (scope.equals("2")){
//					scope ="上级部门"; 
//				}else if (scope.equals("3")){
//					scope ="本部"; 
//				}
//				}else{
//					scope="";
//				}
				
				if(type!=null&& "save".equals(type)&&!"5".equals(this.frowset.getString("body_id")))
				{
					vo2.setString("body_id", this.frowset.getString("body_id"));
					vo2.setString("plan_id", plan_id);
					vo2 = dao.findByPrimaryKey(vo2);
					vo2.setString("cond", cond);
					vo2.setString("cexpr", cexpr);
//					if(scopestr!=null&&scopestr.indexOf("`")!=-1){
//						String temp[] = scopestr.split("`");
//						vo2.setString("scope", temp[i]);
//						vo.setString("scope", temp[i]);
//					}else{
						vo.setString("scope", scope);
//					}
					i++;
					list2.add(vo2);
				}else
				{
					vo.setString("scope", scope);
				}
				if(!"5".equals(this.frowset.getString("body_id")))
				{
					if(scope1!=null&&("-1".equals(scope1)|| "0".equals(scope1)))
						scope1="";
					if(scope!=null&&("-1".equals(scope)|| "0".equals(scope)))
						scope="";
				    if((cond1==null||cond1.length()==0)&&(scope1==null||scope1.length()==0))
				    {
				    	if((cond!=null&&cond.length()>0)||(scope!=null&&scope.length()>0))
				    	{//自动更新
				    		vo2.setString("body_id", this.frowset.getString("body_id"));
							vo2.setString("plan_id", plan_id);
							vo2 = dao.findByPrimaryKey(vo2);
							vo2.setString("cond", cond);
							vo2.setString("cexpr", cexpr);
							vo2.setString("scope", scope);
							list2.add(vo2);
				    	}else
				    	{//提示信息
				    		names+=this.frowset.getString("name")+",";
				    	}
				    }
				}
				list.add(vo);
				
		    }
		    if(list2.size()>0)
		    	dao.updateValueObject(list2);
		    if(names.length()>0)
		    	setinfo.put("1", names);
		    	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	
		return list;
    }
}
