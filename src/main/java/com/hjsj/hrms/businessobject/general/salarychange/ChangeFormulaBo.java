package com.hjsj.hrms.businessobject.general.salarychange;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class ChangeFormulaBo {
	/**
	 * 根据模版编号tableid字段和dao获取公式table
	 * @param tableid //模版id
	 * @param dao 
	 * @return String 
	 * @throws SQLException
	 */
	public String tableTemp(String tableid,ContentDAO dao){
		StringBuffer tabletemp = new StringBuffer();
		tabletemp.append("<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTable\">");
		tabletemp.append("<tr class=\"fixedHeaderTr1\"><td width=\"10%\" align=\"center\" class=\"TableRow\" nowrap>&nbsp;</td>");
		tabletemp.append("<td width=\"70%\" align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("workdiary.message.formula.group.name")+"</td>");
		tabletemp.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("workdiary.message.effective.stats")+"</td></tr>");
		String sqlstr = "select id,flag,chz,formula,cfactor from gzAdj_formula where tabid="+tableid+" order by nsort";
		try {
			RowSet rs = dao.search(sqlstr);
			int n=1;
			while(rs.next()){
				String id= rs.getString("id");
				tabletemp.append("<tr>");
				tabletemp.append("<td class=\"RecordRow\" align=\"center\" onclick=\"tr_bgcolor(");
				tabletemp.append(id);
				tabletemp.append(");setId(");
				tabletemp.append(id);	
				tabletemp.append(");\" nowrap>"+n+"</td>");
				tabletemp.append("<td class=\"RecordRow\" onclick=\"tr_bgcolor(");
				tabletemp.append(id);
				tabletemp.append(");setId(");
				tabletemp.append(id);	
				tabletemp.append(");\">");
				tabletemp.append("<div id=\"view_"+id+"\" ondblclick=\"alertName("+id+")\">");
				tabletemp.append(rs.getString("chz"));
				tabletemp.append("</div><div id=\"hide_"+id+"\" style=\"display:none\">");
				tabletemp.append("<input type=\"text\" name=\"value_");
				tabletemp.append(id+"\" value=\""+rs.getString("chz"));
				tabletemp.append("\" size=\"25\" onblur=\"onLeave("+id+",this)\">");
				tabletemp.append("</div></td>"); 
				tabletemp.append("<td class=\"RecordRow\" align=\"center\" onclick=\"tr_bgcolor(");
				tabletemp.append(id);
				tabletemp.append(");setId(");
				tabletemp.append(id);	
				tabletemp.append(");\" nowrap>");
				if(rs.getInt("flag")==1){
					tabletemp.append("<input type=\"checkbox\" onclick=\"alertFlag("+id+",this);\" name=\""+id+"\" value=\"1\" checked>");
				}else{
					tabletemp.append("<input type=\"checkbox\" onclick=\"alertFlag("+id+",this);\" name=\""+id+"\" value=\"0\">");
				}
				tabletemp.append("</td></tr>");
				n++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tabletemp.append("</table>");
		return tabletemp.toString();
	}
	/**
	 * 根据模版编号tableid字段和项目id删除项目
	 * @param tableid //模版id
	 * @param id //项目id
	 * @param dao 
	 * @return void 
	 * @throws SQLException
	 */
	public void delTemp(String tableid,String id,ContentDAO dao){
		String sqlstr = "delete from gzAdj_formula where tabid="+tableid+" and id="+id;
		try {
			dao.update(sqlstr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 根据项目公式item和项目list获取项目table
	 * @param item //公式
	 * @param list //项目list
	 * @return String 
	 * @throws SQLException
	 */
	public String itemTable(String item,ArrayList itemlist){
		StringBuffer tabletemp = new StringBuffer();
		tabletemp.append("<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTable1\" style='border-left:0px;border-right:0px;'>");
		tabletemp.append("<tr class=\"fixedHeaderTr1\"><td align=\"center\" class=\"TableRow\" nowrap style='border-left:0px;border-right:0px;'>"+ResourceFactory.getProperty("org.maip.comp.project")+"</td></tr>");
		if(item.length()>1){
			for(int i=0;i<itemlist.size();i++){ 
				CommonData dataobj = (CommonData)itemlist.get(i);
				String itemid = i+"_"+dataobj.getDataValue()+"_2";
				tabletemp.append("<tr>");
				tabletemp.append("<td class=\"RecordRow\" height=\"30\" style='border-left:0px;border-right:0px;' nowrap>");
				tabletemp.append("<div id=\"div"+i+"__2\">");
				tabletemp.append(optionStr(itemid,dataobj.getDataName()));
				tabletemp.append("</div>");
				tabletemp.append("</td></tr>");
			}
		}
		tabletemp.append("</table>");
		return tabletemp.toString();
	}
	/**
	 * 根据项目公式item获取项目
	 * @param item //公式
	 * @param list //变化前项目list
	 * @return ArrayList 
	 */
	public ArrayList itemList(ContentDAO dao,String item,ArrayList list,String tabid,String ids){
		ArrayList itemlist = new ArrayList();
		String itemarr="";
		if(item!=null&&item.length()>1){
			String arr[] = item.split("`");
			for(int i=0;i<arr.length;i++){
				String id = arr[i].substring(0,arr[i].indexOf("="));
				String arr_item[] = id.split("_");
				if(arr_item.length==2){
					String itemid = arr_item[0];
					for(int j=0;j<list.size();j++){
						CommonData dataobj = (CommonData)list.get(j);
						if(itemid.equalsIgnoreCase(dataobj.getDataValue())){
							itemlist.add(dataobj);
							itemarr+=arr[i]+"`";
							break;
						}
					}
				}
			}
		}
		if(itemarr.length()!=item.length()){//把导入的模板中的一些脏数据（未构库的指标）删除掉 郭峰
		
			alertItem(dao,tabid,ids,itemarr);
		}
			
		return itemlist;
	}
	/**
	 * 
	* <p>Description：人事异动-根据项目公式item获取项目 </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-12 下午03:10:58
	 */
	public ArrayList itemListFormula(ContentDAO dao,String item,ArrayList list,String tabid,String ids,String[] fields){
		ArrayList itemlist = new ArrayList();
		String itemarr="";
		if(item!=null&&item.length()>1){
			String arr[] = item.split("`");
			for(int i=0;i<arr.length;i++){
				String id = arr[i].substring(0,arr[i].indexOf("="));
				id=id.replace("START_DATE", "START*DATE");
				id=id.replace("start_date", "start*date");
				String arr_item[] = id.split("_");
				if(arr_item.length==3){
					String itemid = arr_item[1];
					boolean isHave = false;
					for(int j=0;j<list.size();j++){
						CommonData dataobj = (CommonData)list.get(j);
						HashMap itemmap = new HashMap();
						if(itemid.equalsIgnoreCase(dataobj.getDataValue())){
							isHave = true;
							itemmap.put(fields[0], itemid+":"+dataobj.getDataName());
							itemmap.put(fields[1], itemid);
							itemmap.put(fields[2], arr_item[0]);
							itemlist.add(itemmap);
							itemarr+=arr[i]+"`";
							break;
						}
					}
					if(!isHave) {
						HashMap itemmap = new HashMap();
						itemmap.put(fields[0], itemid+":未知指标");
						itemmap.put(fields[1], itemid);
						itemmap.put(fields[2], arr_item[0]);
						itemlist.add(itemmap);
						itemarr+=arr[i]+"`";
					}
				}
			}
		}
		if(itemarr.length()!=item.length()){//把导入的模板中的一些脏数据（未构库的指标）删除掉 郭峰
		
			alertItem(dao,tabid,ids,itemarr);
		}
			
		return itemlist;
	}
	/**
	 * 根据项目公式item获取项目中文名称
	 * @param list //变化前项目list
	 * @return ArrayList 
	 */
	public String itemStr(ArrayList list){
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<list.size();i++){
			CommonData dataobj = (CommonData)list.get(i);
			buf.append(dataobj.getDataName()+",");
		}
		return buf.toString();
	}
	/**
	 * 根据项目公式list组成tr,td
	 * @param itemid  项目id
	 * @param desc 项目中文名称
	 * @return String 
	 */
	public String optionStr(String itemid,String desc){
		StringBuffer optionstr = new StringBuffer();
		optionstr.append("<input type=\"text\" name=\"");
		optionstr.append(itemid);
		optionstr.append("\" value=\"");
		optionstr.append(desc);
		optionstr.append("\" onclick=\"");
		optionstr.append("onSelects('");
		optionstr.append(itemid);
		optionstr.append("');\" style=\"width:200px;text-align:right\" class='TEXT4' >");
		return optionstr.toString();
	}
	/**
	 * 保存公式
	 * @param dao 
	 * @param tableid //模版id
	 * @param name //模版名称
	 * @param formula //公式 
	 * @param cfactor //公式条件
	 * @return void 
	 */
	public String saveItem(ContentDAO dao,String tableid,String chz,String formula,String cfactor,Connection conn){
		int id = 1;
		try
		{
			StringBuffer strsql = new StringBuffer();
			int dbflag = Sql_switcher.searchDbServer();
			DbWizard db = new DbWizard(conn);
			switch (dbflag) {
				case Constant.MSSQL:
//					strsql.append("alter table ");
//					strsql.append(name);
//					strsql.append(" add xxx int identity(1,1)");
					break;
				default:
					if (!isSequence(dbflag,conn)) {
						//db.execute("drop sequence xxx");
						strsql.append("create sequence gzAdj_FormulaSeq_Id increment by 1 start with 1");
					}
					break;
			}
			if(strsql.length()!=0) {
                db.execute(strsql.toString());
            }
			
			formula=formula.replaceAll("'", "\"");
			int nsort = nsortMax(dao,tableid);
			RowSet rs =null;
			if(Constant.MSSQL==Sql_switcher.searchDbServer()){
				RecordVo gzadjVo=new RecordVo("gzAdj_formula");
				gzadjVo.setInt("tabid",Integer.parseInt(tableid));
				gzadjVo.setInt("nsort",nsort);
				gzadjVo.setInt("flag",1);
				gzadjVo.setString("chz",chz);
				gzadjVo.setString("formula",formula);
				gzadjVo.setString("cfactor",cfactor); 
			    dao.addValueObject(gzadjVo);
				 
			}else if(Constant.ORACEL==Sql_switcher.searchDbServer()){
				 
					StringBuffer buf = new StringBuffer();
					buf.append("INSERT INTO gzAdj_Formula(id,tabid,Flag,nSort,chz,cexpr) VALUES(");  //,formula
					buf.append("gzAdj_FormulaSeq_Id.NextVal,");
					buf.append(tableid+",1,");
					buf.append(nsort+",'");
					buf.append(chz+"','')");
				//	buf.append(formula+"')");
					dao.update(buf.toString()); 
					rs = dao.search("select max(Id) as id from gzAdj_Formula where flag=1 and nsort="+nsort+" and tabid="+tableid+" and chz='"+chz+"'");
					if(rs.next())
					{
						RecordVo gzadjVo=new RecordVo("gzAdj_formula");
						gzadjVo.setInt("id",rs.getInt("id"));
						gzadjVo.setInt("tabid",Integer.parseInt(tableid));
						gzadjVo=dao.findByPrimaryKey(gzadjVo);
						gzadjVo.setString("formula",formula);
						gzadjVo.setString("cfactor", cfactor);
						dao.updateValueObject(gzadjVo); 
					} 
				 
			}else if(Constant.DB2==Sql_switcher.searchDbServer()){  
					StringBuffer buf = new StringBuffer();
					buf.append("INSERT INTO gzAdj_Formula(id,tabid,Flag,nSort,chz,cfactor,cexpr,formula) VALUES(");
					buf.append("NextVal FOR gzAdj_FormulaSeq_Id,");
					buf.append(tableid+",1,");
					buf.append(nsort+",'");
					buf.append(chz+"','");
					buf.append(cfactor+"','");
					buf.append("','");
					buf.append(formula+"')");
					dao.update(buf.toString());
				 
			} 
			
				rs = dao.search("select max(Id) as id from gzAdj_Formula where tabid="+tableid+"");
				if(rs.next()){
					id = rs.getInt("id");
				}
				id=id>0?id:1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id+"";
	}
	public boolean isSequence(int dbflag,Connection conn)
	{
		boolean flag=false;
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			if(dbflag==Constant.ORACEL){
				rowSet=dao.search("select   sequence_name   from   user_sequences   where lower(sequence_name)='gzadj_formulaseq_id'");
				if(rowSet.next()) {
                    flag=true;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}
	/**
	 * 
	 * @param dao
	 * @param tabid
	 * @param groupId
	 * @param cHz
	 * @return
	 * @Description: 人事异动-计算公式-编辑公式组
	 * @author gaohy 
	 * @date Feb 25, 2016 6:41:52 PM 
	 * @version V7x
	 */
	public String updateItem(ContentDAO dao,String tabid,String groupId,String cHz){
		String flag="";
		try {
			int result=dao.update("update gzAdj_Formula set cHz='"+cHz+"'where TabId='"+tabid+"' and Id='"+groupId+"'");
			if(result==1){
				flag="OK";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
    public String updateValidState(ContentDAO dao,String tabid,String groupId,String State){
        String flag="";
        try {
            int result=dao.update("update gzAdj_Formula set flag='"+State
                    +"'where TabId='"+tabid+"' and Id='"+groupId+"'");
            if(result==1){
                flag="OK";
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;
    }
	
	/**
	 * 生产select代码
	 * @param desc 项目
	 * @return String 
	 */
	public String selectStr(String desc){
		StringBuffer optionstr = new StringBuffer();
		optionstr.append("<select id=\"selectid\" name=\"selectid\" style=\"width:200px;\"");
		optionstr.append(" onchange=\"changeSelect();\" onblur=\"onLeave();\">");
		optionstr.append("<option value=\"new\"></option>");
		if(desc!=null&&desc.length()>0){
			String arr[] = desc.split("`");
			for(int i=0;i<arr.length;i++){
				if(arr[i].trim().length()>0){
					String item_arr[] = arr[i].split(":");
					if(item_arr.length==2){
						optionstr.append("<option value=\"");
						optionstr.append(item_arr[0].toUpperCase());
						optionstr.append("\">");
						optionstr.append(item_arr[1]);
						optionstr.append("</option>");
					}
				}
			}
		}
		optionstr.append("</select>");
		return optionstr.toString();
	}
	/**
	 * 修改公式
	 * @param dao 
	 * @param tableid //模版id
	 * @param name //模版名称
	 * @param formula //公式 
	 * @param cfactor //公式条件
	 * @return void 
	 */
	public String alertItem(ContentDAO dao,String tableid,String id,String formula,String cfactor){
		String info= "no";
		formula=formula.replaceAll("'", "\"");
/*		StringBuffer sqlstr = new StringBuffer(); 
		sqlstr.append("update gzAdj_formula set Formula='");
		sqlstr.append(formula);
		sqlstr.append("',cFactor='");
		sqlstr.append(cfactor);
		sqlstr.append("' where TabId=");
		sqlstr.append(tableid);
		sqlstr.append(" and Id=");
		sqlstr.append(id);
*/
		formula=formula.replace("START*DATE", "START_DATE");
		formula=formula.replace("start*date", "start_date");
		try {
			RecordVo vo=new RecordVo("gzadj_formula");
			vo.setInt("id",Integer.parseInt(id));
			vo.setInt("tabid",Integer.parseInt(tableid));
			vo=dao.findByPrimaryKey(vo);
			vo.setString("formula",formula);
			vo.setString("cfactor",cfactor);
			dao.updateValueObject(vo);
			
	//		dao.update(sqlstr.toString());
			info= "ok";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	/**
	 * 修改公式名称
	 * @param dao 
	 * @param tableid //模版id
	 * @param name //模版名称
	 * @param formula //公式 
	 * @param cfactor //公式条件
	 * @return void 
	 */
	public String alertItemName(ContentDAO dao,String tableid,String id,String chz){
		String info= "no";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update gzAdj_formula set cHz='");
		sqlstr.append(chz);
		sqlstr.append("' where TabId=");
		sqlstr.append(tableid);
		sqlstr.append(" and Id=");
		sqlstr.append(id);

		try {
			dao.update(sqlstr.toString());
			info= "ok";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	/**
	 * 修改公式（不能用sql语句直接更新，因为那样sql语句超过4000个字符时oracle库会报错。）
	 * @param dao 
	 * @param tableid //模版id
	 * @param name //模版名称
	 * @param formula //公式 
	 * @param cfactor //公式条件
	 * @return void 
	 */
	public String alertItem(ContentDAO dao,String tableid,String id,String formula){
		String info= "no";
		try {
			RecordVo vo = new RecordVo("gzAdj_formula");
			vo.setInt("id", Integer.parseInt(id));
			vo.setInt("tabid", Integer.parseInt(tableid));
			vo = dao.findByPrimaryKey(vo);
			vo.setString("formula", formula);
			dao.updateValueObject(vo);
			info= "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	/**
	 * 获取gzAdj_formula表中当前模版最大nsort号
	 * @param dao 
	 * @param tableid //模版id
	 * @return String 
	 */
	public int nsortMax(ContentDAO dao,String tableid){
		int nsort = 1;
		String sqlstr = "select max(nsort) as nsort from gzAdj_formula where tabid="+tableid;

		try {
			RowSet rs = dao.search(sqlstr);
			if(rs.next()){
				if(rs.getString("nsort")!=null) {
                    nsort=rs.getInt("nsort")+1;
                }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nsort;
	}
	/**
	 * 获取gzAdj_formula表中当前获取公式和计算条件号
	 * @param dao 
	 * @param tableid //模版id
	 * @return String 
	 */
	public String[] getItem(ContentDAO dao,String tableid,String id,ArrayList itemlist){
		String[] itemFactor = new String[4];
		String formula = "";
		String cfactor = "";
		StringBuffer item = new StringBuffer();
		StringBuffer item2 = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer();
		
		sqlstr.append("select formula,cfactor,chz "); 
		sqlstr.append("from gzAdj_formula where ");
		sqlstr.append("tabid="+tableid);
		sqlstr.append(" and id="+id);
		RowSet rs =null;
		try {
			if(tableid!=null&&tableid.length()>0&&id!=null&id.length()>0){//tableid和id不为空才执行sql
				rs = dao.search(sqlstr.toString());
			}
			if(rs.next()){
				formula=rs.getString("formula");
				cfactor=rs.getString("cfactor");
				cfactor=cfactor!=null?cfactor:"";
				itemFactor[2]=rs.getString("chz");
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(formula!=null&&formula.length()>0){
			String arr[] = formula.split("`");
			int index=0;
			for(int i=0;i<arr.length;i++){
			    //过滤 已经删除的指标 wangrd 2014-01-07
		        String itemid = arr[i].substring(0,arr[i].indexOf("="));
		        itemid=itemid.replace("START_DATE", "START*DATE");
                String arr_item[] = itemid.split("_");
                if(arr_item.length==2){
                    itemid = arr_item[0];
                    boolean isHave = false;
                    for(int j=0;j<itemlist.size();j++){
                        CommonData dataobj = (CommonData)itemlist.get(j);
                        if(itemid.equalsIgnoreCase(dataobj.getDataValue())){
                        	isHave = true;
                            item.append(index+"_"+arr[i]+"`");
                            item2.append(arr[i]+"`");
                            index++;
                            break;
                        }
                    }
                    if(!isHave) {//业务模板不包含此指标
                    	item.append(index+"_"+arr[i]+"`");
                    	item2.append(arr[i]+"`");
                		index++;
                    }
                }
				
			}
		}
		itemFactor[0]=item.toString();
		itemFactor[1]=cfactor;
		itemFactor[3]=item2.toString();
		
		return itemFactor;
	}
	/**
	 * 根据项目公式item项目重组
	 * @param item //公式
	 * @return String 
	 */
	public String itemStr(String item){
		String itemdesc = "";
		String arr[] = item.split("`");
		for(int i=0;i<arr.length;i++){
			itemdesc += arr[i].substring(arr[i].indexOf("_")+1,arr[i].length())+"`";
		}
		return itemdesc;
	}
}
