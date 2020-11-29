package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChangeFiledItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String itemDesc = (String)this.getFormHM().get("itemDesc");
		if(itemDesc == null || "".equals(itemDesc)){
			this.getFormHM().put("info","error");
		}
		
		//System.out.println(itemDesc);
		
		String codesetid = this.checkItem(itemDesc);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(codesetid!=null&&codesetid.trim().length()>0&&!"0".equals(codesetid)){
			try {
				ArrayList list = new ArrayList();
	
				CommonData dataobj1 = new CommonData();
				dataobj1 = new CommonData("","");
				list.add(dataobj1);
				
				String table = "";
				if("@K".equals(codesetid)|| "UM".equals(codesetid)|| "UN".equals(codesetid)){
					table = "organization";
				}else{
					table = "codeitem";
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				String sql="select codeitemid,codeitemdesc  from "+table+"  where (codesetid ='"+codesetid+"'" ;
				//tianye update 支持关联部门的指标也可以选择单位
				if("UM".equals(codesetid)){
					sql+= " or codesetid ='UN'";
				}
				
				if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				{
					sql+=") and " + Sql_switcher.dateValue(backdate)
			     			+ " between start_date and end_date";
					
					String codeitemid = this.userView.getUnitIdByBusi("4");
					if(StringUtils.isNotEmpty(codeitemid))
						codeitemid = PubFunc.getTopOrgDept(codeitemid);
					
					if(!this.userView.isSuper_admin() && !"UN`".equalsIgnoreCase(codeitemid)) {
						String[] codeitemids = codeitemid.split("`");
						sql += " and (";
						for(int i = 0; i < codeitemids.length; i++) {
							String itemid = codeitemids[i];
							sql += "codeitemid like '" + itemid.substring(2) + "%' or ";
						}
						
						if(sql.endsWith("or "))
							sql = sql.substring(0, sql.length() - 3);
						
						sql += ")";
					}
					
					sql=sql+(" ORDER BY a0000,codeitemid ");
				}else if(!"@@".equalsIgnoreCase(codesetid))
				{
					//guodd 2015-09-08 
					//普通代码类，查询validateflag：1控制起止日期 0 通过有效无效（invalid）控制
					RecordVo vo = new RecordVo("codeset");
					vo.setString("codesetid", codesetid);
					vo = dao.findByPrimaryKey(vo);
					if(1 == vo.getInt("validateflag")){//判断日期
						sql+=") and " + Sql_switcher.dateValue(backdate)
					     	+ " between start_date and end_date";
					}else{//是否有效
						sql+=") and invalid=1 ";
					}
					//chenxg 复杂查询显示代码类没有层级显示，统一全部显示出来按 a0000,codeitemid排序显示顺序错乱，
					//暂时将代码项的显示顺序按照codeitemid，a0000排序，暂时不支持显示代码项调整顺序 2016-12-05
					sql=sql+(" ORDER BY codeitemid,a0000");
				}
			
			
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					CommonData dataobj = new CommonData();
					String id = this.getFrowset().getString("codeitemid");
					String desc = this.getFrowset().getString("codeitemdesc");
					dataobj = new CommonData(id,"("+id+")"+desc);
					list.add(dataobj);
				}
				
				this.getFormHM().put("info",list);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}else{
			this.getFormHM().put("info","error");
		}
		
		
	}

	/**
	 * 判断指标是否是代码型
	 * @param itemid
	 * @return
	 */
	public String checkItem(String itemDesc){
		String warntype=(String)this.getFormHM().get("warntype");
		if(warntype==null||warntype.length()<=0)
			warntype="0";
		String b = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if("3".equals(warntype))
		{
			String sql="select codesetid  from t_hr_busifield where itemdesc='"+itemDesc+"'";
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					b = this.frowset.getString("codesetid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else
		{
			if(itemDesc!=null&&("单位".equals(itemDesc)|| "单位名称".equals(itemDesc)))
				return "UN";
			if(itemDesc!=null&&("部门".equals(itemDesc)|| "部门名称".equals(itemDesc)))
				return "UM";
			// jazz 42832 岗位名称也要特殊处理
			if(itemDesc!=null&&("职位".equals(itemDesc)|| "职位名称".equals(itemDesc)|| "岗位".equals(itemDesc)|| "岗位名称".equals(itemDesc)))
				return "@K";
			
			// 传参方式，防止sql注入
			ArrayList params = new ArrayList();
			params.add(itemDesc);
			
			String sql="select codesetid  from fielditem where itemdesc=?";
			try {
				this.frowset = dao.search(sql, params);
				if(this.frowset.next()){
					b = this.frowset.getString("codesetid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return b;
	}
}
