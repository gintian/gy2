
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryPersonTrans extends IBusiness {


	public void execute() throws GeneralException {

		HashMap rqhm=(HashMap)this.getFormHM().get("requestPamaHM");
		String selectname=(String)rqhm.get("selectname");
		String multimedia_file_flag="";
		selectname=PubFunc.keyWord_reback(selectname);
		selectname = com.hrms.frame.codec.SafeCode.decode(selectname).trim();
		if(selectname.contains("＇")){
			selectname=selectname.replaceAll("＇","''" );
		}
		ArrayList dblist=this.userView.getPrivDbList();
		if(dblist==null||dblist.size()<=0)
		{
			throw new GeneralException("没有人员库权限！");
		}
		//验证是否有A01主集权限  28065   wangb 20170602 
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);//获得所有权限的子集
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if("A01".equalsIgnoreCase(fieldset.getFieldsetid()))//是否有A01权限
			{
				multimedia_file_flag = fieldset.getMultimedia_file_flag();
				break;
			}
		}
		this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);//页面是否显示附件列 1 显示 

		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this
				.getFrameconn());
		String onlyname = sysbo.getCHKValue(
				Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		FieldItem item = DataDictionary.getFieldItem(onlyname);
		String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		FieldItem  pyItem  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
		ArrayList fieldlist = new ArrayList();
		/*页面显示添加人员库列 wangb 20170726*/
		FieldItem fielditem =new FieldItem();
		fielditem.setFieldsetid("A01");
		fielditem.setItemid("db");
		fielditem.setItemdesc("所在人员库");
		fielditem.setCodesetid("@@");
		fieldlist.add(fielditem);
		StringBuffer columns=new StringBuffer();
		/**
		 * 获取查询时要显示的人员指标  28065  wangb 20170602
		 * vo 读取constant表 constant字段值 INFO_PARAM 对应的记录 wangb 20170602
		 */ 
		RecordVo vo=ConstantParamter.getRealConstantVo("INFO_PARAM");
		String sql="";
		if(vo != null){
			if(vo.getString("str_value").toLowerCase()!=null 
					&&	vo.getString("str_value").toLowerCase().trim().length()>0 
					&&	vo.getString("str_value").toLowerCase().indexOf("xml")!=-1){
				Document doc=null;
				try {
					doc = PubFunc.generateDom(vo.getString("str_value").toLowerCase());//解析XML wangb 20170602 28065
					Element root = doc.getRootElement(); // 取得根节点 wangb 20170602 28065
					Element childnode=root.getChild("browser");//人员指标存储节点browser wangb 20170602 28065
					if(childnode == null || "".equalsIgnoreCase(childnode.getText())){//不存在该节点，没有任何指标时 31773 wangb 20170921
						/*默认显示 单位 部门 岗位 姓名 指标 wangb 20170921  31773*/
						sql+="B0110,E01A1,E0122,A0101,";
						columns.append(",B0110,E01A1,E0122,A0101");
						fieldlist.add(DataDictionary.getFieldItem("b0110"));
						fieldlist.add(DataDictionary.getFieldItem("e01a1"));
						fieldlist.add(DataDictionary.getFieldItem("e0122"));
						fieldlist.add(DataDictionary.getFieldItem("a0101"));
					}else{//配置显示指标
						String field=childnode.getText();
						String[] fields=field.split(",");
						for(int i=0 ; i<fields.length ; i++){
							if(fields[i]==null || "".equalsIgnoreCase(fields[i]))
								continue;
							
							FieldItem fi = DataDictionary.getFieldItem(fields[i].toLowerCase());
							if(fi == null || "0".equals(fi.getUseflag()))
							    continue;
							
							if(!"0".equals(this.userView.analyseFieldPriv(fields[i].toLowerCase()))){
								fieldlist.add(fi);//根据itemid值读取对应的指标项信息 wangb 20170602 28065
								sql+=fields[i]+",";
								columns.append(","+fields[i].toLowerCase());
							}
						}
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					GeneralExceptionHandler.Handle(ee);
				} 
			}
		}
		//没有设置人员列表指标时，默认显示下面的指标
        if(StringUtils.isEmpty(sql)) {
            sql += "b0110,e0122,a0101,";
            columns.append(",b0110,e0122,a0101");
            ArrayList<FieldItem> fieldItems = userView.getPrivFieldList("A01");
            for(int i = 0; i < fieldItems.size(); i++) {
                FieldItem fi = fieldItems.get(i);
                if("b0110".equals(fi.getItemid()) || "e0122".equals(fi.getItemid()) || "a0101".equals(fi.getItemid()))
                    fieldlist.add(fi);
                
            }
        }
		/*指标构库 wangb 20171016 32118*/
		if(item!=null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "1".equalsIgnoreCase(item.getUseflag())){
			columns.append(","+item.getItemid());
		}
		/*指标构库 wangb 20171016 32118*/
		if(pyItem!=null&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(this.userView.analyseFieldPriv(pyItem.getItemid()))&& "1".equalsIgnoreCase(pyItem.getUseflag())){
			columns.append(","+pyItem.getItemid());
		}
		StringBuffer strsql=new StringBuffer();

		String userbase=(String)dblist.get(0);
		strsql.append("select "); 
		strsql.append(sql);//查询在页面显示指标 wangb 20170602 28065
		strsql.append(userbase);
		strsql.append("a01.a0100 as a0100,");
		strsql.append("## as db,@@@ dbid,a0000");
		StringBuffer wheresql = new StringBuffer();
		/*指标构库，才能作为查询指标 wangb 20171016 32118 item.getUseflag().equalsIgnoreCase("1")*/
		if(item!=null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "1".equalsIgnoreCase(item.getUseflag())){
			if(strsql.indexOf(item.getItemid())==-1){//页面显示列表指标和查询条件指标不重复 wangb 20170602 28065
				strsql.append(","+item.getItemid());
			}
		}
		/*指标构库，才能作为查询指标 wangb 20171016 32118   pyItem.getUseflag().equalsIgnoreCase("1")*/
		if(pyItem!=null&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(this.userView.analyseFieldPriv(pyItem.getItemid()))&& "1".equalsIgnoreCase(pyItem.getUseflag())){
			if(strsql.indexOf(pyItem.getItemid())==-1){//页面显示列表指标和查询条件指标不重复 wangb 20170602 28065
				strsql.append(","+pyItem.getItemid());
			}
		}
		if(this.userView.isSuper_admin()){
			wheresql.append(" from "+userbase+"a01 where a0101 like '%"+selectname+"%'");
			/*指标构库，才能作为查询指标 wangb 20171016 32118 item.getUseflag().equalsIgnoreCase("1")*/
			if(item!=null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "1".equalsIgnoreCase(item.getUseflag())){
				wheresql.append(" or upper("+item.getItemid()+") like '%"+selectname+"%'");
			}
			/*指标构库，才能作为查询指标 wangb 20171016 32118   pyItem.getUseflag().equalsIgnoreCase("1")*/
			if(pyItem!=null&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(this.userView.analyseFieldPriv(pyItem.getItemid()))&& "1".equalsIgnoreCase(pyItem.getUseflag())){
				wheresql.append(" or upper("+pyItem.getItemid()+") like '%"+selectname+"%'");
			}
		}else{
			/*源代码：StringBuffer factor=new StringBuffer("A0101="+selectname+"%"); 
			 * 在=后面添加%号,解决非su用户不能用名查人员的问题【19698】
			 * guodd 2016-06-20
			 * */
			/*if(selectname.length()==0)
				selectname="%";*/
			StringBuffer factor=new StringBuffer("A0101=%"+selectname+"%");
			StringBuffer expr=new StringBuffer("1");
			boolean flag = false;
			/*指标构库，才能作为查询指标 wangb 20171016 32118 item.getUseflag().equalsIgnoreCase("1")*/
			if(item!=null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "1".equalsIgnoreCase(item.getUseflag())){
				factor.append("`"+item.getItemid()+"=%"+selectname+"%");
				expr.append("+2");
				flag=true;

			}
			/*指标构库，才能作为查询指标 wangb 20171016 32118   pyItem.getUseflag().equalsIgnoreCase("1")*/
			if(pyItem!=null&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(this.userView.analyseFieldPriv(pyItem.getItemid()))&& "1".equalsIgnoreCase(pyItem.getUseflag())){
				factor.append("`"+pyItem.getItemid()+"=%"+selectname+"%");
				if(flag)
					expr.append("+3");
				else
					expr.append("+2");
			}
			expr.append("|");
			//this.userView.getPrivExpression();
			wheresql.append(this.userView.getPrivSQLExpression(expr.toString()+factor.toString(),userbase, false, false, true, new ArrayList()));
			String tmp=wheresql.toString().toUpperCase();
			tmp=tmp.replaceAll(userbase.toUpperCase()+"A01.A0101", "upper("+userbase.toUpperCase()+"A01.A0101"+")");
			/*指标构库，才能作为查询指标 wangb 20171016 32118 item.getUseflag().equalsIgnoreCase("1")*/
			if(item!=null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "1".equalsIgnoreCase(item.getUseflag())){
				tmp=tmp.replaceAll(userbase.toUpperCase()+"A01."+onlyname.toUpperCase(), "upper("+userbase.toUpperCase()+"A01."+onlyname.toUpperCase()+")");
			}
			/*指标构库，才能作为查询指标 wangb 20171016 32118   pyItem.getUseflag().equalsIgnoreCase("1")*/
			if(pyItem!=null&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(this.userView.analyseFieldPriv(pyItem.getItemid()))&& "1".equalsIgnoreCase(pyItem.getUseflag())){
				tmp=tmp.replaceAll(userbase.toUpperCase()+"A01."+pinyin_field.toUpperCase(), "upper("+userbase.toUpperCase()+"A01."+pinyin_field.toUpperCase()+")");
			}
			wheresql.setLength(0);
			wheresql.append(tmp);
		}
		userbase=userbase.toUpperCase();
		String tmpsql =(strsql.append(wheresql)).toString().toUpperCase();
		StringBuffer sb = new StringBuffer();
		HashMap<String, String> dbMap=queryDbId();
		for(int n=0;n<dblist.size();n++){
			String tmpdbpre=(String)dblist.get(n);
			if(tmpdbpre.length()==3){
				if(sb.length()>0){
//					sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
					sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+tmpdbpre+"'").replaceAll("@@@", "'"+dbMap.get(tmpdbpre.toUpperCase())+"'"));//人员库 前面不拼接字符  wangb 20170726
				}else{
//					sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
					sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+tmpdbpre+"'").replaceAll("@@@", "'"+dbMap.get(tmpdbpre.toUpperCase())+"'"));//人员库 前面不拼接字符  wangb 20170726
				}
			}
		}
		wheresql.setLength(0);
		wheresql.append(sb.toString()+") tt");
		strsql.setLength(0);
		strsql.append("select a0100,");
		strsql.append(sql.substring(0, sql.length()-1));//查询在页面显示指标 wangb 20170602 28065
//		strsql.append(",a0101,db ");
		strsql.append(",db ");// a0101 指标重复或页面不设置 sql语句报错  wangb 20170628 29121
		StringBuffer orderby = new StringBuffer();
		orderby.append(" order by ");
		orderby.append("dbid,a0000");
		columns.append(",a0100,db");
		this.getFormHM().put("columns", columns.toString());
		String flag=(String)this.getFormHM().get("flag");
		if(flag!=null&& "13".equals(flag))
		{
			this.getFormHM().put("nbaselist", dblist);

		}
		this.getFormHM().put("distinct","");
		this.getFormHM().put("cond_sql",strsql.toString());

		this.getFormHM().put("cond_str",wheresql.toString()); 
		this.getFormHM().put("fieldlist",fieldlist);

		/**浏览信息用的卡片*/
		this.getFormHM().put("tabid", searchCard("1")); 	    
		this.getFormHM().put("order_by",orderby.toString());
	}  
	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i];
	}
	
	/***
	 * 查询人员库编号 列表显示人员信息按照人员库id编号查询 changxy 20170920
	 * **/
	private HashMap<String, String> queryDbId(){
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		HashMap<String, String> dbMap=new HashMap<String, String>();
		try {
			String dbid="";
			rs=dao.search("SELECT dbid,upper(pre) dbname from dbname ");//where  upper(pre)='"+dbname.toUpperCase()+"'
			while(rs.next()){
				dbMap.put(rs.getString("dbname"), rs.getInt("dbid")+"");
			}
			return dbMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据信息群类别，查询定义的登记表格号
	 * @param infortype =1人员 =2单位 3=职位 
	 * @return
	 */
	private String searchCard(String infortype)
	{
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		if(uplevel==null||uplevel.length()==0)
			uplevel="0";
		this.getFormHM().put("uplevel", uplevel);	
		String cardid="-1";
		try
		{
			if("1".equalsIgnoreCase(infortype))
			{
				cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			}
			if("2".equalsIgnoreCase(infortype))
			{
				cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			}
			if("3".equalsIgnoreCase(infortype))
			{
				cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			}
			if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				cardid="-1";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return cardid;
	}

}
