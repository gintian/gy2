package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hjsj.hrms.interfaces.sys.CreateCodeXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 * 
 * Title:SearchcodeitemdescTrans.java
 * Description:
 * Company:hjsj
 * Create time:May 8, 2014:6:17:23 PM
 * @author zhaogd
 * @version 6.x
 */
public class SearchcodeitemdescTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2809167092806546121L;
	Integer leaf_only;
	public void execute() throws GeneralException {
		try {
			String codesetid=(String)this.getFormHM().get("codesetid");
			String codeitemid=(String)this.getFormHM().get("codeitemid");
			String flag = (String) this.getFormHM().get("flag");
			codeitemid = codeitemid == null || "undefined".equalsIgnoreCase(codeitemid) ? "" : codeitemid;
			//岗位培训分析：岗位查询  单位、部门、岗位需要全部显示
			String allcode=(String)this.getFormHM().get("allcode");
			allcode = allcode==null?"":allcode;
			String isfirstnode=(String)this.getFormHM().get("isfirstnode");
			isfirstnode = isfirstnode==null?"":isfirstnode;
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			name = name==null?"":name;
			//zgd 2014-7-17 通过定义变量needPrefix来控制是否需要前缀。 1为需要前缀；  
			String needPrefix=(String)this.getFormHM().get("needPrefix");
			needPrefix = needPrefix==null?"":needPrefix;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			CreateCodeXml xmlVO = new CreateCodeXml(codesetid,"");
			leaf_only = xmlVO.getSelectFlag(dao);
			String xmlsql = getQuerySql(codesetid,codeitemid,name,allcode);
			String itemdesc = "";
			String itemid = "";
			String setid = "";
			String fitemdesc = "";
			//岗位分析获取岗位
			HashMap map = new HashMap();
			if("ture".equalsIgnoreCase(flag))
				map = getTrainStation();
			
			ArrayList desclist = new ArrayList();
			this.frowset = dao.search(xmlsql);
			while(this.frowset.next()){
				CommonData objvo = new CommonData();
				itemdesc = (String)this.frowset.getString("codeitemdesc");
				itemid = (String)this.frowset.getString("codeitemid");
				setid = (String)this.frowset.getString("codesetid");
				if("ture".equalsIgnoreCase(flag)){
					if (! map.containsKey(itemid) && "@K".equalsIgnoreCase(codesetid)) {
						continue;
					}
				}
					
				if(codesetid!=null&&!"".equals(codesetid)){
					if(!"1_".equals(codesetid.substring(0, 2))&&!"@@".equalsIgnoreCase(codesetid)){
						if(!((String)this.frowset.getString("parentid")).equals(itemid)){//没有上一级不取父节点
							fitemdesc = getFitemDesc(codesetid,(String)this.frowset.getString("parentid"));//zgd 2014-5-9 有父节点的需要获取上一级
							itemdesc = fitemdesc +"/"+ itemdesc;
						}
					}
				}
				objvo.setDataName(itemdesc);
				if("1".equalsIgnoreCase(needPrefix)){
					objvo.setDataValue(setid+itemid);//zgd 2014-7-17 获取带前缀的codeitemid的值
				}else{
					objvo.setDataValue(itemid);
				}
				desclist.add(objvo);
			}
			this.getFormHM().put("namelist", desclist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getFitemDesc(String codesetid, String fitemid) {
		String sql = "";
		String itemdesc = "";
		if("55_1".equals(codesetid) || "55_2".equals(codesetid))
		    codesetid = "55";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		String upSetid = codesetid;
		boolean isOrg = false;
		try {
			if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){
				sql = "select * from organization where codeitemid = '"+fitemid+"'";
				isOrg = true;
			}else{
				sql = "select * from codeitem where codesetid = '"+codesetid+"' and codeitemid = '"+fitemid+"'";
			}
			rs = dao.search(sql);
			while(rs.next()){
				itemdesc = (String)rs.getString("codeitemdesc");
				upSetid = (String)rs.getString("codesetid");
			}
			
			
			if(!isOrg || "UN".equalsIgnoreCase(upSetid)){
				return itemdesc;
			}
			
			sql = "select codeitemid,codeitemdesc,"+Sql_switcher.length("codeitemid")+" lengths from organization where codesetid='UN' and codeitemid = "+Sql_switcher.substr("'"+fitemid+"'", "1", Sql_switcher.length("codeitemid"))+" order by lengths desc";
			rs = dao.search(sql);
			if(rs.next())
				itemdesc = rs.getString("codeitemdesc")+"/"+itemdesc;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return itemdesc;
	}

	private String getQuerySql(String codesetid, String codeitemid, String name, String allcode) {
		StringBuffer str=new StringBuffer();
		//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
    	String codesetidL = "";
		String flag = "";
		try {
			if (codesetid.indexOf("_") != -1) {
				codesetidL = codesetid;
				String[] codesetidLs=codesetidL.split("_");
				if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
					codesetid=codesetidLs[0];
					flag=codesetidLs[1];
				}
			}
			/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 start */
			String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
			/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 end */
			if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){
				if("UN".equalsIgnoreCase(codesetid))
				{
					str.append("select parentid,codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
					str.append(codesetid);
					str.append("'");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 start */
					str.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between start_date and end_date ");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 end */
				}
				else if("UM".equalsIgnoreCase(codesetid))
				{
					str.append("select parentid,codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
					str.append(codesetid);
					str.append("'");
					if("yes".equals(allcode)){
						str.append(" or codesetid = 'UN'");
					}
					str.append(")");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 start */
					str.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between start_date and end_date ");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 end */
				}  
				else if ("@K".equalsIgnoreCase(codesetid))
				{
					str.append("select parentid,codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
					str.append(codesetid);
					str.append("'");
					if("yes".equals(allcode)){
						str.append(" or codesetid = 'UN' or codesetid = 'UM'");
					}
					str.append(")");
					if("yes".equals(allcode) && !this.userView.isSuper_admin()){
					    String where = TrainCourseBo.getUnitIdByBusiWhere(userView);
					    where = where.replace("where", "and");
					    where = where.replaceAll("b0110", "codeitemid");
					    str.append(where);
					}
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 start */
					str.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between start_date and end_date ");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 end */
				}
				
				if(!"".equals(name)&&name!=null){
					str.append(" and (codeitemdesc like '%"+name+"%'");//zgd 2014-5-8 
					str.append(" or codeitemid like '%"+name+"%')");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 start */
					str.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between start_date and end_date ");
					/* 标识：1836 修改人员单位、部门、岗位时，按名称查询时，查询结果显示两次（问题原因为查出来了已经撤销的无效机构） xiaoyun 2014-6-5 end */
				}
				
			}else if (codesetid.length() > 1 && "1_".equals(codesetid.substring(0, 2))){
				String codeset = codesetid.replace("1_", "");
				String[] rel = relatingcode(codeset);
				str.append("select '" + codesetid + "' as codesetid,");
				str.append(rel[1] + " as codeitemid," + rel[2] + " as codeitemdesc,");
				str.append(rel[1] + " as childid from " + rel[0]);
				if(!userView.isSuper_admin()&&new DbWizard(this.getFrameconn()).isExistField(rel[0], "b0110", false)){
					str.append(TrainCourseBo.getUnitIdByBusiWhere(userView));
					str.append(" where 1=1");
					if(!"".equals(name)&&name!=null){
						str.append(" and ("+ rel[2] +" like '%"+name+"%' ");//zgd 2014-5-8 
						str.append(" or "+ rel[1] +" like '%"+name+"%') ");
					}
				}else if("r50".equalsIgnoreCase(rel[0])){//关联在线课程表须加条件
					str.append(" where r5022='04'");
					if(!userView.isSuper_admin()){
						String tmpwhere = TrainCourseBo.getLessonByBusiWhere(userView);
				    	if(tmpwhere.length()<5)
				    		tmpwhere="";
				    	str.append(tmpwhere);
					}
						
					
					if(!"".equals(name)&&name!=null){
						str.append(" and ("+ rel[2] +" like '%"+name+"%' ");//zgd 2014-5-8 
						str.append(" or "+ rel[1] +" like '%"+name+"%') ");
					}
				}else{
					if(!"".equals(name)&&name!=null){
						str.append(" where ("+ rel[2] +" like '%"+name+"%' ");//zgd 2014-5-8 
						str.append(" or "+ rel[1] +" like '%"+name+"%') ");
					}
				}
			}else if("@@".equalsIgnoreCase(codesetid)){//人员库
				str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname ");
				if(!"".equals(name)&&name!=null){
					str.append(" where (codeitemdesc like '%"+name+"%'");//zgd 2014-5-8 
					str.append(" or codeitemid like '%"+name+"%')");
				}
				//str.append(" order by dbid");//zgd 2014-5-8 
			}else{
				str.append("select parentid,codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
				str.append(codesetid);
				str.append("'");
				if(!"".equals(name)&&name!=null){
					str.append(" and (codeitemdesc like '%"+name+"%'");//zgd 2014-5-8 
					str.append(" or codeitemid like '%"+name+"%')");
				}
				if(leaf_only == 1){//add by xiegh on date 20180403 bug36344
					str.append(" and codeitemid not in ( select parentid from codeitem where parentid <> codeitemid ) ");
				}
				/* 标识：2342 员工管理：记录录入中修改人员信息时，按名称查询，无效的代码项也查询出来了 xiaoyun 2014-6-9 start */
				boolean isValidDate = getIsValidateFlag(codesetid);
				// 根据时间区间来判断
				if(isValidDate) {
					str.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between START_DATE and END_DATE"); 
				}else { // 直接根据是否有效来判断
					str.append(" and invalid=1");
				}
				/* 标识：2342 员工管理：记录录入中修改人员信息时，按名称查询，无效的代码项也查询出来了 xiaoyun 2014-6-9 end */
			}
			
			if("ALL".equals(codeitemid)){
				str.append(" and 1=1 ");
			}else if(codeitemid==null||"".equals(codeitemid)){
				if("64".equals(codesetid)||"65".equals(codesetid)){
					str.append(" and 1=2 ");
				}else{
					str.append(" and 1=1 ");
				}
			}else{
				if(codeitemid.indexOf("`")==-1){
					str.append(" and codeitemid like '");
					if(codeitemid.indexOf("UN")!=-1||codeitemid.indexOf("UM")!=-1){
						str.append(codeitemid.substring(2));
					}else{
						str.append(codeitemid);
					}
					str.append("%' ");
				}else{
					StringBuffer tempSql=new StringBuffer("");
					String[] temp=codeitemid.split("`");
					for(int i=0;i<temp.length;i++){
						if(temp.length==1){
							if("UN".equalsIgnoreCase(temp[i])){
								tempSql.append(" or 1=1 ");
							}else{
								tempSql.append(" or codeitemid like '"+temp[i].substring(2)+"%' ");
							}
						}else{
							tempSql.append(" or codeitemid like '"+temp[i].substring(2)+"%' ");
						}
					}
					str.append(" and ( "+tempSql.substring(3)+" ) ");
				}
			}
			
			if("55".equalsIgnoreCase(codesetid)) {//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
				if ("1".equalsIgnoreCase(flag))
					str.append(" and not exists(select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
				else if ("2".equalsIgnoreCase(flag)){
					str.append(" and exists(select 1 from r50 where R5022='04'"); 
					
					if(!this.userView.isSuper_admin()) {
						String where = TrainCourseBo.getLessonByBusiWhere(this.userView);
						str.append(where);
					}
					
					String backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
					str.append(" and "+Sql_switcher.year("R5030")+"*10000+"+Sql_switcher.month("R5030")+"*100+"+Sql_switcher.day("R5030")+"<="+backdate);
					str.append(" and "+Sql_switcher.year("R5031")+"*10000+"+Sql_switcher.month("R5031")+"*100+"+Sql_switcher.day("R5031")+">="+backdate);
					str.append(" and r50.codeitemid=codeitem.codeitemid)");
				}
				
				if("55_1".equalsIgnoreCase(codesetidL)||"55_2".equalsIgnoreCase(codesetidL))
					codesetid = codesetidL;
				str.append(" ORDER BY codeitemid ");
			}
			
			if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){
				str.append(" ORDER BY a0000,codeitemid ");
			}else if("@@".equalsIgnoreCase(codesetid)){
				str.append(" order by dbid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 检查该代码类是否记录历史
	 * @param codesetId
	 * @return
	 * @throws SQLException 
	 * @author xiaoyun 2014-6-9
	 */
	private boolean getIsValidateFlag(String codesetId) throws SQLException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		boolean flag = false;
		this.frowset = dao.search("select VALIDATEFLAG from codeset where codesetid='" + codesetId + "'");
		if(frowset.next()) {
			int validateFlag = frowset.getInt("VALIDATEFLAG");
			if(validateFlag == 1) {
				flag = true;
			}
		}
		return flag;
	}

	private String[] relatingcode(String codeset) {
		String[] rel = new String[3];
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    StringBuffer buf = new StringBuffer();
		    buf.append("select codetable,codevalue,codedesc from t_hr_relatingcode where");
		    buf.append(" codesetid='");
		    buf.append(codeset);
		    buf.append("'");
		    this.frowset = dao.search(buf.toString());
		    String codetable = "";
		    String codevalue = "";
		    String codedesc = "";
		    while (this.frowset.next())
		    {
			codetable = this.frowset.getString("codetable");
			codetable = codetable != null ? codetable : "";
			codevalue = this.frowset.getString("codevalue");
			codevalue = codevalue != null ? codevalue : "";
			codedesc = this.frowset.getString("codedesc");
			codedesc = codedesc != null ? codedesc : "";
		    }
		    rel[0] = codetable;
		    rel[1] = codevalue;
		    rel[2] = codedesc;
		} catch (SQLException e)
		{
		    e.printStackTrace();
		} 
		return rel;
	}
	/**
	 * 岗位分析获取岗位
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getTrainStation() throws GeneralException {
		boolean zFlag = false;
		HashMap map = new HashMap();
		ResultSet rset = null;
		TrainStationBo trainStationBo = new TrainStationBo();
		HashMap mapg = trainStationBo.getStationSett(this.frameconn);
		String postSetId = (String) mapg.get("post_setid");// 岗位培训子集编号
		String postCloumn = (String) mapg.get("post_coursecloumn");// 岗位培训子集中参培课程指标
		if (postSetId != null && postSetId.length() > 0 && postCloumn != null
				&& postCloumn.length() > 0) {
			zFlag = true;
		}

		StringBuffer strsql = new StringBuffer();
		try {
			if (zFlag) {
				strsql.append("select e01a1 from ");
				strsql.append(postSetId);
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)
					strsql.append(" where " + postCloumn + " is not null");
				else
					strsql.append(" where "
							+ Sql_switcher.isnull(postCloumn, "''") + "<>''");
				strsql.append(" group by e01a1");

				ContentDAO dao = new ContentDAO(this.frameconn);
				rset = dao.search(strsql.toString());
				while (rset.next()) {
					map.put(rset.getString("e01a1"), "");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			try {
				if (rset != null) {
					rset.close();
				}
			} catch (SQLException ee) {
				ee.printStackTrace();
			}
		}
		
		return map;
	}
}
