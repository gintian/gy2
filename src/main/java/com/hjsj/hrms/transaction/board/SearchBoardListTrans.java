/*
 * Created on 2005-5-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 */
public class SearchBoardListTrans extends IBusiness {

	private String annouce="";
	private String op="";
	private String selparam="";
	private String begintime="";
	private String endtime="";
	private String thistype="";
	private String type="";//是否是数据库中的取招聘对象0：不是1：是
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("opt");
		if(opt == null)
		    opt = (String)this.getFormHM().get("opt");
		String announcetype=(String)hm.get("announce");
		type = (String)hm.get("type");
		if(type == null)
			type = (String)this.getFormHM().get("type");
		hm.remove("type");
		annouce=announcetype;
		op=opt;
		this.getFormHM().put("opt", opt);
		this.getFormHM().put("annouce", announcetype);
		//获取模糊查询参数  jingq add 2014.5.12
		String seltype = (String) this.getFormHM().get("seltype");
		String param = (String) this.getFormHM().get("selparam");
		//处理特殊字符  jingq  add  2014.5.23
		param = SafeCode.decode(param);
		if(param!=null&&param.length()>0){
			param = param.replace("nbspa", "#");
			param = PubFunc.keyWord_reback(param);
			param = param.replace("quanjiao;hao", "；");
			selparam = param.replaceAll("\\\\", "\\\\\\\\");
			selparam = selparam.replaceAll("%", "\\\\%");
			selparam = selparam.replaceAll("_", "\\\\_");
			selparam = selparam.replaceAll("'", "''");
		}
		thistype = (String) this.getFormHM().get("thistype");
		if(!"1".equals(seltype)){
			thistype = "00";
		}
		String btime = (String) this.getFormHM().get("begintime");
		String etime = (String) this.getFormHM().get("endtime");
		if("04".equals(thistype)||"05".equals(thistype)){
			if("".equals(btime)&&!"".equals(etime)){
				begintime = "0001-01-01 00:00:00";
				endtime = etime+" 23:59:59";
			} else if("".equals(etime)&&!"".equals(btime)){
				endtime = "9999-12-31 23:59:59";
				begintime = btime+" 00:00:00";
			} else if(!"".equals(btime)&&!"".equals(etime)){
				begintime = btime+" 00:00:00";
				endtime = etime+" 23:59:59";
			} else {
				begintime = "";
				endtime = "";
			}
		}
		//界面需要的参数   jingq  add  2014.5.9
		param = SafeCode.encode(param);
		ArrayList list = getTypeList();
		this.getFormHM().put("typelist", list);
		this.getFormHM().put("thistype", thistype);
		this.getFormHM().put("selparam", param);
		this.getFormHM().put("begintime", btime);
		this.getFormHM().put("endtime", etime);
		this.getFormHM().put("seltype", "0");
		updateOtherFlagFirstInto();
	    SQLExecute();
	}
	/**
	 * Orcale执函数
	 *
	 */
	public void OrcaleExecute() throws GeneralException
	{
		String unitcode = getOperUnit();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,topic,content,createuser,createtime,period,approve,approveuser,approvetime from announce");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		ResultSet rs = null;
		try {
		     rs = dao.search("SELECT id,topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext,priority,unitcode FROM announce where (unitcode like '"+unitcode+"%' or unitcode is null) order by createtime desc");
			
			 while (rs.next()) {
				RecordVo vo = new RecordVo("announce");
				vo.setString("id", PubFunc.nullToStr(rs.getString("id")));

				String temp = PubFunc.nullToStr(rs.getString("topic"));
				
				if (temp == null || "".equals(temp)) {
					vo.setString("topic", "");
				} else {
					vo.setString("topic", temp.substring(0,temp.length()>10?10:temp.length())+"...");;
				}

				temp = PubFunc.nullToStr(Sql_switcher.readMemo(rs,"content"));
				if (temp == null || "".equals(temp)) {
					vo.setString("content", "...");
				} else {
					vo.setString("content", temp.substring(0,temp.length()>10?10:temp.length())+"...");
				}
				temp = PubFunc.nullToStr(rs.getString("createuser"));
				if (temp == null || "".equals(temp)) {
					vo.setString("createuser", "...");
				} else {
					vo.setString("createuser", temp);
				}
				temp = PubFunc.DoFormatDate(rs.getString("createtime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("createtime", "...");
				} else {

					vo.setString("createtime", temp);
				}

				int tempInt = rs.getInt("period");
				if (new Integer(tempInt) == null) {
					vo.setInt("period", 0);
				} else {
					vo.setInt("period", tempInt);
				}

				tempInt = rs.getInt("approve");
				if (new Integer(tempInt) == null || tempInt == 0) {
					vo.setString("approve", "否");
				} else {
					vo.setString("approve", "是");
				}

				temp = rs.getString("approveuser");
				if (temp == null || "".equals(temp)) {
					vo.setString("approveuser", "");
				} else {
					vo.setString("approveuser",temp);
				}

				temp = PubFunc.DoFormatDate(rs.getString("approvetime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("approvetime", "");
				} else {
					vo.setString("approvetime", temp);
				}
				temp = PubFunc.nullToStr(rs.getString("priority"));
				if (temp == null || "".equals(temp)) {
					vo.setString("priority", "");
				} else {
					vo.setString("priority", temp);
				}
				vo.setString("unitcode", this.frowset.getString("unitcode"));

				list.add(vo);
			}
			
			
			this.getFormHM().put("boardlist", list);
			this.getFormHM().put("unitcode", unitcode);//该用户的所属单位
		}
		catch(OutOfMemoryError error)
		{
			
			System.out.println("------>SearchBoardListTrans---->OutOfMemoryError-->");
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
            PubFunc.closeResource(rs);
		}
	}
	/**
	 * SQL执行函数
	 */
	public void SQLExecute() throws GeneralException
	{
		String unitcode = getOperUnit();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,topic,content,createuser,createtime,period,approve,approveuser,approvetime,priority,unitcode from announce where 1=1 ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		String id="";
		try 
		{
			if(this.op!=null){
				if("1".equalsIgnoreCase(this.op)&&this.annouce!=null&& "1".equalsIgnoreCase(this.annouce)){
					
					/*当有审批权限时，查出归属单位为当前用户管理范围的公告 guodd 2018-08-10*/
					if(this.userView.hasTheFunction("07050101")){
						String b0110 = this.userView.getManagePrivCodeValue();
						if(b0110.length()<1)
							b0110 = this.userView.getManagePrivCode();
						//b0110不等于UN时需要控制所属单位
						if(!"UN".equalsIgnoreCase(b0110)){
							strsql.append(" and ( ");
							if(b0110.length()>0){
								strsql.append(" b0110 like '"+b0110+"%' or ");
							}
							strsql.append(" b0110 is null ");
							if(Sql_switcher.searchDbServer()==1){
								strsql.append("or b0110='' ");
							}
							strsql.append(" ) ");
						}
						strsql.append(" and (flag=1 or flag is null ");
						if(Sql_switcher.searchDbServer()==1){
							strsql.append("or flag=''");
						}
						strsql.append(")");
					}else{
						strsql.append(" and (unitcode like '"+unitcode+"%' or unitcode is null) and  (flag=1 or flag is null ");
						if(Sql_switcher.searchDbServer()==1){
							strsql.append("or flag=''");
						}
						strsql.append(")");
					}
				}
				if("2".equalsIgnoreCase(this.op) && this.annouce!=null) {
					if(StringUtils.isNotBlank(type) && "1".equals(this.type)) {
						strsql.append(" and (unitcode like '"+unitcode+"%' or unitcode is null) and  other_flag='");//修改，现在根据other_flag来区别招聘对象
						strsql.append(this.annouce);
						strsql.append("'");
					}else {
						strsql.append(" and (unitcode like '"+unitcode+"%' or unitcode is null) and  flag=");
						strsql.append(this.annouce);
					}
				}
				//界面按钮查询   jingq  add   2014.5.14
				if(Sql_switcher.searchDbServer()==2){	//判断当前数据库类型    =2为Oracle
					if("01".equals(thistype)&&!"".equals(selparam)){
						strsql.append(" and topic like '%"+selparam+"%' escape '\\'");
					} else if("02".equals(thistype)&&!"".equals(selparam)){
						strsql.append(" and createuser like '%"+selparam+"%' escape '\\'");
					} else if("04".equals(thistype)&&!"".equals(begintime)){
						strsql.append(" and createtime between to_date('"+begintime+"','yyyy-MM-dd hh24:mi:ss') and to_date('"+endtime+"','yyyy-MM-dd hh24:mi:ss')");
					} else if("05".equals(thistype)&&!"".equals(begintime)){
						strsql.append(" and approvetime between to_date('"+begintime+"','yyyy-MM-dd hh24:mi:ss') and to_date('"+endtime+"','yyyy-MM-dd hh24:mi:ss')");
					}
				} else {
					selparam = selparam.replaceAll("\\[", "\\\\[");	//sqlserver中 [也为特殊字符，需要特殊处理
					if("01".equals(thistype)&&!"".equals(selparam)){
						strsql.append(" and topic like '%"+selparam+"%' escape '\\'");
					} else if("02".equals(thistype)&&!"".equals(selparam)){
						strsql.append(" and createuser like '%"+selparam+"%' escape '\\'");
					} else if("04".equals(thistype)&&!"".equals(begintime)){
						strsql.append(" and CONVERT(varchar(25),createtime,121) between '"+begintime+"' and '"+endtime+"'");
					} else if("05".equals(thistype)&&!"".equals(begintime)){
						strsql.append(" and CONVERT(varchar(25),approvetime,121) between '"+begintime+"' and '"+endtime+"'");
					}
				}
			} else{
				strsql.append(" and (unitcode like '"+unitcode+"%' or unitcode is null)");
			}
			strsql.append("  order by createtime desc");
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("announce");
				id=this.getFrowset().getString("id");
				if(this.op!=null&& "1".equalsIgnoreCase(op)){
					//有公告栏审批权限不走 资源权限，否则没法审批了
					if(!(this.userView.isHaveResource(IResourceConstant.ANNOUNCE,id)) && !this.userView.hasTheFunction("07050101"))
					{
								continue;
					}
				}
				vo.setString("id", this.getFrowset().getString("id"));

				String temp = this.getFrowset().getString("topic");
				if (temp == null || "".equals(temp)) {
					vo.setString("topic", "");
				} else {
					//vo.setString("topic", temp.substring(0,temp.length()>50?50:temp.length())+"...");;
					vo.setString("topic", temp);
				}

				temp = Sql_switcher.readMemo(this.getFrowset(),"content");
				if (temp == null || "".equals(temp)) {
					vo.setString("content", "...");
				} else {
					vo.setString("content", temp.substring(0,temp.length()>10?10:temp.length())+"...");
				}
				temp = this.getFrowset().getString("createuser");
				if (temp == null || "".equals(temp)) {
					vo.setString("createuser", "...");
				} else {
					vo.setString("createuser", this.getFrowset().getString(
							"createuser"));
				}
				temp = PubFunc.FormatDate(this.getFrowset().getDate("createtime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("createtime", "...");
				} else {

					vo.setDate("createtime",temp);
				}

				int tempInt = this.getFrowset().getInt("period");
				if (new Integer(tempInt) == null) {
					vo.setInt("period", 0);
				} else {
					vo.setInt("period", this.getFrowset().getInt("period"));
				}

				tempInt = this.getFrowset().getInt("approve");
				if (new Integer(tempInt) == null || tempInt == 0) {
					vo.setString("approve", "否");
				} else {
					vo.setString("approve", "是");
				}

				temp = this.getFrowset().getString("approveuser");
				if (temp == null || "".equals(temp)) {
					vo.setString("approveuser", "");
				} else {
					vo.setString("approveuser", this.getFrowset().getString(
							"approveuser"));
				}

				temp = PubFunc.FormatDate(this.getFrowset().getDate("approvetime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("approvetime", "");
				} else {
					vo.setDate("approvetime", temp);
				}
				temp = PubFunc.nullToStr(this.getFrowset().getString("priority"));
				if (temp == null || "".equals(temp)) {
					vo.setString("priority", "");
				} else {
					vo.setString("priority", temp);
				}
				String feng = this.frowset.getString("unitcode");
				vo.setString("unitcode", this.frowset.getString("unitcode"));

				list.add(vo);
			}

			this.getFormHM().put("boardlist", list);
			this.getFormHM().put("unitcode", unitcode);//该用户的所属单位
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	}
	
	/**
	 * db2执行函数
	 * @throws GeneralException
	 */
	public void db2Execute() throws GeneralException
	{
		String unitcode = getOperUnit();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,topic,content,createuser,createtime,period,approve,approveuser,approvetime from announce");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		ResultSet rs = null;
		try {
		    rs = dao.search("SELECT id,topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext,unitcode FROM announce where (unitcode like '"+unitcode+"%' or unitcode is null)");
			
			while (rs.next()) {
				RecordVo vo = new RecordVo("announce");
				vo.setString("id", PubFunc.nullToStr(rs.getString("id")));

				String temp = PubFunc.nullToStr(rs.getString("topic"));
				
				if (temp == null || "".equals(temp)) {
					vo.setString("topic", "");
				} else {
					vo.setString("topic", temp.substring(0,temp.length()>10?10:temp.length())+"...");;
				}

				temp = PubFunc.nullToStr(Sql_switcher.readMemo(rs,"content"));
				if (temp == null || "".equals(temp)) {
					vo.setString("content", "...");
				} else {
					vo.setString("content", temp.substring(0,temp.length()>10?10:temp.length())+"...");
				}
				temp = PubFunc.nullToStr(rs.getString("createuser"));
				if (temp == null || "".equals(temp)) {
					vo.setString("createuser", "...");
				} else {
					vo.setString("createuser", temp);
				}
				temp = PubFunc.DoFormatDate(rs.getString("createtime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("createtime", "...");
				} else {

					vo.setString("createtime", temp);
				}

				int tempInt = rs.getInt("period");
				if (new Integer(tempInt) == null) {
					vo.setInt("period", 0);
				} else {
					vo.setInt("period", tempInt);
				}

				tempInt = rs.getInt("approve");
				if (new Integer(tempInt) == null || tempInt == 0) {
					vo.setString("approve", "否");
				} else {
					vo.setString("approve", "是");
				}

				temp = rs.getString("approveuser");
				if (temp == null || "".equals(temp)) {
					vo.setString("approveuser", "");
				} else {
					vo.setString("approveuser",temp);
				}

				temp = PubFunc.DoFormatDate(rs.getString("approvetime"));
				if (temp == null || "".equals(temp)) {
					vo.setString("approvetime", "");
				} else {
					vo.setString("approvetime", temp);
				}
				vo.setString("unitcode", this.frowset.getString("unitcode"));

				list.add(vo);
			}
			
			this.getFormHM().put("boardlist", list);
			this.getFormHM().put("unitcode", unitcode);//该用户的所属单位
		}
		catch(OutOfMemoryError error)
		{
			System.out.println("------>SearchBoardListTrans---->OutOfMemoryError-->");
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
		    PubFunc.closeResource(rs);
		}
	}
	/*
	 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，得到用户所在单位（以前为：则查出管理范围所在的单位）。
	 * **/
	public String getOperUnit() throws GeneralException
	{/*
			String unit = "";
			String operOrg = this.userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
			{
				String[] temp = operOrg.split("`");
				String unitordepart = temp[0];
				if (unitordepart.substring(0, 2).equalsIgnoreCase("UN"))//如果是单位
					unit = unitordepart.substring(2);
				else
					unit = getUnit(unitordepart.substring(2));
			}
			else if((!this.userView.isSuper_admin()) && (operOrg.equalsIgnoreCase(""))) // 如果不是超级用户，且没有操作单位
			{
				String codePrefix = this.userView.getManagePrivCode();
				String codeid = this.userView.getManagePrivCodeValue();
				if(codePrefix.equalsIgnoreCase("UN"))
					unit = codeid;
				else
					unit = this.getUnit(codeid);
			}*/
		//统一同面板取所属单位一致 2013-12-20 xuj update
		String unit = getUnit(this.frameconn);
		return unit;		
	}
	
	/**
	 * 获取单位。
	 * @return
	 */
	public String getUnit(Connection conn){
		String unit = "";
		if(!userView.isSuper_admin()){//如果不是超级用户
			int userType = this.userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
			if(userType==4){//如果是自助用户
				unit = this.userView.getUserOrgId();//得到用户所在单位
			}else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
				unit = getOperUnit(conn);
			}
		}
		return unit;
	}
	
	/*
	 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
	 * **/
	public String getOperUnit(Connection conn) 
	{
			String unit = "";
			String operOrg = this.userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
			{
				String[] temp = operOrg.split("`");
				String unitordepart = temp[0];
				if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
					unit = unitordepart.substring(2);
				else//如果是部门
					unit = getUnit(unitordepart.substring(2),conn);
			}
			else if((!this.userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
			{
				String codePrefix = this.userView.getManagePrivCode();
				String codeid = this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codePrefix))//如果是单位
					unit = codeid;
				else//如果是部门
					unit = this.getUnit(codeid,conn);
			}
		return unit;		
	}
	
	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid,Connection conn){
		String unit = "";
		try{
			RowSet rs=null;
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				style = rs.getString("codesetid");
				unit = rs.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit,conn);
			
			if(rs!=null)
				rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
	

	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid) throws GeneralException{
		String unit = "";
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				style = this.frowset.getString("codesetid");
				unit = this.frowset.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit);
		}catch(SQLException e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return unit;
	}
	/**
	 * jingq  add   2014.5.9
	 * @Title: getTypeList   
	 * @Description: 下拉列表参数集合   
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	public ArrayList getTypeList(){
		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("00",ResourceFactory.getProperty("label.select"));
		list.add(temp);
		temp = new CommonData("01", ResourceFactory.getProperty("conlumn.board.topic"));
		list.add(temp);
		if ("11".equalsIgnoreCase(this.annouce))//培训新闻只有安主题查询
		    return list;
		
		temp = new CommonData("02", ResourceFactory.getProperty("conlumn.board.createuser"));
		list.add(temp);
//		temp = new CommonData("03", ResourceFactory.getProperty("conlumn.board.approveuser"));
//		list.add(temp);
		temp = new CommonData("04", ResourceFactory.getProperty("conlumn.board.createtime"));
		list.add(temp);
		temp = new CommonData("05", ResourceFactory.getProperty("conlumn.board.approvetime"));
		list.add(temp);
		return list;
	}
	
	/**
	 * 新增加other_flag字段，这里对于第一次进入的时候将flag为3（社招）、4（校招）的记录，在首次读取时(other_flag为空）自动将other_flag填写进02（社招）,01（校招）
	 * @Title: updateOtherFlagFirstInto   
	 * @Description:    
	 * @param  
	 * @return void    
	 * @throws
	 */
	public void updateOtherFlagFirstInto() {
		RowSet rs=null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select flag from announce where (flag = 3 or flag = 4) and other_flag is null");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sb.toString());
			if(rs.next()) {
				ArrayList list = new ArrayList();
				String flag = rs.getString("flag");
				String sql = "update announce set other_flag = ? where flag = ? and other_flag is null";
				if("3".equals(flag) || flag == "3") {
					list.add("02");
					list.add("3");
					dao.update(sql,list);
				}else{
					list.add("01");
					list.add("4");
					dao.update(sql,list);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
}