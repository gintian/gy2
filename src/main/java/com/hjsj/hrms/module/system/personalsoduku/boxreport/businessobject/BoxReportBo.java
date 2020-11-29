package com.hjsj.hrms.module.system.personalsoduku.boxreport.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* <p>Title:BoxReportBo </p>
* <p>Description: 盒式报表操作bo</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 9, 2015 5:06:32 PM
 */
public class BoxReportBo {
	Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public BoxReportBo(){
    	
    }
    
    public BoxReportBo(Connection conn){
    	this.conn = conn;
    }
    
    public BoxReportBo(Connection conn, UserView userview){
    	this.conn = conn;
        this.userview = userview;
    }
    
    /**
     * 获取表头 渲染
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnList() {
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        try {
            // 报表编号
            ColumnsInfo idColumn = getColumnsInfo("box_id", "报表编号", 80, "N");
            idColumn.setLocked(true);
            columnTmp.add(idColumn);

            // 报表名称
            ColumnsInfo cassetteName = getColumnsInfo("name", "报表名称", 180, "A"); 
            if (userview.isSuper_admin() || userview.hasTheFunction("3001H03")){
            	cassetteName.setRendererFunc("boxreport_me.boxReportInfo");
            }
            cassetteName.setLocked(true);
            columnTmp.add(cassetteName);

            // 数据来源
            ColumnsInfo dataSource = getColumnsInfo("data_from", "数据来源", 150, "A");
            columnTmp.add(dataSource);

            // 时间维度指标
            ColumnsInfo timeDimension = getColumnsInfo("time_dim_field", "时间维度指标", 150, "A");
            columnTmp.add(timeDimension);
            
            // 横向指标
            ColumnsInfo lateralIndex = getColumnsInfo("h_field", "横向指标", 180, "A");
            columnTmp.add(lateralIndex);

            // 横向坐标描述
            ColumnsInfo lateralDesc = getColumnsInfo("h_field_desc", "横向坐标描述", 180, "A");
            columnTmp.add(lateralDesc);

            // 纵向指标
            ColumnsInfo longitudinalIndex = getColumnsInfo("v_field", "纵向指标", 150, "A");
            columnTmp.add(longitudinalIndex);

            // 纵向坐标描述
            ColumnsInfo longitudinalDesc = getColumnsInfo("v_field_desc", "纵向坐标描述", 146, "A");
            columnTmp.add(longitudinalDesc);

            // 分析区间
            ColumnsInfo analysisInterval = getColumnsInfo("time_dim_type", "分析区间", 94, "A");
            analysisInterval.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(analysisInterval);

            // 人员范围
            ColumnsInfo personnelRange = getColumnsInfo("static_ids", "人员范围", 94, "A");
            personnelRange.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(personnelRange);
            
            ColumnsInfo status = getColumnsInfo("status", "状态", 94, "A");
            status.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(status);
            
            ColumnsInfo qnaction = new ColumnsInfo();
			qnaction.setColumnDesc("操作");
			qnaction.setRendererFunc("boxreport_me.actionRender");
			qnaction.setColumnWidth(150);
			qnaction.setTextAlign("center");
			columnTmp.add(qnaction);

        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
        return columnTmp;
    }
    /**
     * 初始化ColumnsInfo对象
     * @param columnId
     * @param columnDesc
     * @param columnWidth
     * @param type
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位

        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
    /**
     * 获取按钮
     * @return
     */
	public ArrayList getButtonList() {
		ArrayList buttonList  = new ArrayList();
		if (userview.isSuper_admin() || userview.hasTheFunction("3001H01")){
			buttonList.add(newButton("新建", null, "boxreport_me.addBoxReport", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("3001H02")){
			buttonList.add(newButton("删除", null, "boxreport_me.delBoxReport", null, "true"));
		}
		return buttonList;
	}
	/**
	 * 生成按钮
	 * @param text
	 * @param id
	 * @param handler
	 * @param icon
	 * @param getdata
	 * @return
	 */
    private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if (icon != null)
            button.setIcon(icon);
        if (id != null)
            button.setId(id);
        return button;
    }
	/**
	 * 得到横纵坐标值
	 * @param index
	 * @param datasource
	 * @return
	 */
	public HashMap getIndexMap(String index, String datasource) {
		HashMap map = new HashMap();
		RowSet rst = null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			if(!"".equals(index)){
				String sql = "select e.itemdesc,e.itemlength,e.codesetid,(select count(*) from codeitem f where f.codesetid=e.codesetid and f.codeitemid=f.parentid and f.invalid='1') numbe from t_hr_busifield e where e.fieldsetid='"+datasource+"' and e.itemid='"+index+"'";
				rst = dao.search(sql);
				while(rst.next()){
					String itemdesc = rst.getString("itemdesc");
					String itemlength = rst.getString("itemlength");
					String number = rst.getString("numbe");
					map.put("itemid", index);
					map.put("itemdesc", itemdesc);
					map.put("itemlength", itemlength);
					map.put("number", number);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeDbObj(rst);
		}
		return map;
	}
	/**
	 * 得到人员范围条件
	 * @param personnel_range
	 * @return
	 */
	public ArrayList<HashMap> getpersonalList(String personnel_range) {
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		RowSet rst = null;
		RowSet rst1 = null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			if(personnel_range!=null){
				String [] perarry = personnel_range.split(",");
				String ids = "";
				for(int i=0;i<perarry.length;i++){
					String per = perarry[i];
					ids+="'"+per+"'"+",";
				}
				ids = ids.substring(0,ids.length()-1);
				String sql = "select id,name,infokind,flag from sname where id in("+ids+") and infokind='1'";
				
				rst = dao.search(sql);
				while(rst.next()){
					ArrayList<HashMap<String, String>> leglist = new ArrayList<HashMap<String, String>>();
					HashMap map = new HashMap();
					String id = String.valueOf(rst.getInt("id"));
					String name = rst.getString("name");
					String infokind = rst.getString("infokind");
					String sflag = rst.getString("flag");
					map.put("id",id);
					map.put("name", name);
					map.put("sflag", sflag);
					String lsql = "select * from SLegend where id='"+id+"'";
					rst1 = dao.search(lsql);
					while(rst1.next()){
						HashMap<String, String> lmap = new HashMap<String, String>();
						String flag = rst1.getString("flag");
						String legend = rst1.getString("legend");
						String lexpr = rst1.getString("lexpr");
						String factor = rst1.getString("factor");
						lmap.put("legend", legend);
						lmap.put("flag", flag);
						lmap.put("lexpr", lexpr);
						lmap.put("factor", factor);
						lmap.put("infokind", infokind);
						leglist.add(lmap);
					}
					map.put("slegend", leglist);
					list.add(map);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeDbObj(rst);
			 PubFunc.closeDbObj(rst1);
		}
		return list;
	}
	/**
	 * 得到横纵指标、时间维度下拉值
	 * @param datasource
	 * @return
	 */
	public ArrayList<HashMap<String, String>> gethzindex(String datasource) {
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		RowSet rst = null;
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("select e.ItemId,e.itemdesc,e.itemlength,e.itemtype,e.codesetid,(select count(*) from ");
			sb.append("codeitem f where f.codesetid=e.codesetid and f.codeitemid=f.parentid and f.invalid='1') numbe ");
			sb.append("from (select distinct c.ItemId,c.itemdesc,c.itemlength,c.itemtype,c.codesetid from ( ");
			sb.append("select a.* from t_hr_busifield a,t_hr_busitable b where a.FieldSetId=b.FieldSetId ");
			sb.append("and b.FieldSetId='"+datasource+"' ");
			sb.append("and (( a.itemtype='A' and a.codesetid<>'0') or a.itemtype='D' ) ");
			sb.append(") c ,codeitem d where c.codesetid=d.codesetid and d.invalid='1' or c.itemtype='D') e");
			rst = dao.search(sb.toString());
			while(rst.next()){
				HashMap<String,String> map = new HashMap<String,String>();
				String itemid = rst.getString("itemid");
				String itemdesc = rst.getString("itemdesc");
				String itemtype = rst.getString("itemtype");
				String itemlength = rst.getString("itemlength");
				String number = rst.getString("numbe");
				map.put("itemid", itemid);
				map.put("itemdesc", itemdesc);
				map.put("itemtype", itemtype);
				map.put("itemlength", itemlength);
				map.put("number", number);
				list.add(map);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				 PubFunc.closeDbObj(rst);
			}
		
		return list;
	}
	/**
	 * 得到九宫格数据
	 * @param lateral_index
	 * @param longitudinal_index
	 * @param data_source
	 * @param sodukusql
	 * @param whereList
	 * @param codewhereList
	 * @param cassetteid
	 * @param datafrom
	 * @param datelist
	 * @param time_dimension
	 * @return
	 */
	public ArrayList getSoduku(String lateral_index, String longitudinal_index, String data_source ,String sodukusql ,
			ArrayList whereList ,ArrayList codewhereList,String cassetteid,
			String datafrom,ArrayList datelist,String time_dimension,ArrayList dbarr) {
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rst = null;
		RowSet rst1 = null;
		ArrayList listso = new ArrayList();
		try {
			//刷新业务字典
//			DBMetaModel dbmodel=new DBMetaModel(conn);
//			dbmodel.reloadTableModel("t_hr_busifield");
//			dbmodel.reloadTableModel("t_hr_busitable");
			DataDictionary.refresh();
			FieldItem lateralitem = DataDictionary.getFieldItem(lateral_index);
			String lateral =  lateralitem.getFieldsetid();//横向指标前缀
			
			FieldItem longitudinalitem = DataDictionary.getFieldItem(longitudinal_index);
			String longitudinal =  longitudinalitem.getFieldsetid();//纵向指标前缀
			
			String latcode=getCodeItem(lateral_index,data_source,dao,"ASC");//横坐标指标项分项

			String longcode = getCodeItem(longitudinal_index,data_source,dao,"DESC");//纵坐标指标项分项
			String sql = "";
			String basicsql = "";
			if("".equals(sodukusql)){
				if(userview.isSuper_admin()){//超级用户
					if("".equals(time_dimension)){
						sql= " select a.a0101,a.a0100,a.B0110,(select bb.codeitemdesc from organization bb where bb.codeitemid=a.e0122) as e0122,(select cc.codeitemdesc from organization cc where cc.codeitemid=a.e01a1) as e01a1,a.nbase,a."+lateral_index+",a."+longitudinal_index+" ";
					}
					else{
						sql= " select a.a0101,a.a0100,a.B0110,(select bb.codeitemdesc from organization bb where bb.codeitemid=a.e0122) as e0122,(select cc.codeitemdesc from organization cc where cc.codeitemid=a.e01a1) as e01a1,a.nbase,a."+lateral_index+",a."+longitudinal_index+",a."+time_dimension+" ";
					}
					sql+=" from "+data_source+" a where 1=1 ";
					basicsql=sql;
					if(!datelist.isEmpty()){//时间维度   临时添加第一次运行默认显示当前选择的时间分析区间所对应的当前时间的统计结果
						String datewheresql = getDatewheresql(datelist,time_dimension);
						sql+=" and "+datewheresql;
					}
				}else{//普通用户
					String db = "";
					String codesstr = "";
					String wheresql = "";
					String _code = "";
					String percode = "";
					codesstr = this.userview.getManagePrivCodeValue();//管理范围(能查看的人员)
					_code = this.userview.getManagePrivCode();
					percode = this.userview.getCondpriv().toString();//人员范围高级设置
					if(dbarr.size()>0){//人员库范围有
						if("".equals(time_dimension)){
							sql= " select a.a0101,a.a0100,a.B0110,(select bb.codeitemdesc from organization bb where bb.codeitemid=a.e0122) as e0122,(select cc.codeitemdesc from organization cc where cc.codeitemid=a.e01a1) as e01a1,a.nbase,a."+lateral_index+",a."+longitudinal_index+" ";
						}
						else{
							sql= " select a.a0101,a.a0100,a.B0110,(select bb.codeitemdesc from organization bb where bb.codeitemid=a.e0122) as e0122,(select cc.codeitemdesc from organization cc where cc.codeitemid=a.e01a1) as e01a1,a.nbase,a."+lateral_index+",a."+longitudinal_index+",a."+time_dimension+" ";
						}
						sql+=" from "+data_source+" a ";
						
						for(Object dbs:dbarr){
							db +="'"+dbs+"',"; 
						}
						db = db.substring(0,db.length()-1);
						switch(Sql_switcher.searchDbServer()){
						 case Constant.MSSQL:
							  sql+=" where a.nbase in ("+db+") ";
							  break;
						 case Constant.ORACEL:
							  sql+=" where lower(a.nbase) in ("+db+") ";
							  break;
						}
						
						//人员范围权限也有
						if(_code!=null&&!"".equals(_code)){
							
							if(_code.startsWith("UN")){
								wheresql +=" and a.b0110 like '"+codesstr+"%' " ;
							}
							else if(_code.startsWith("UM")){
								wheresql +=" and a.e0122 like '"+codesstr+"%' " ;
							}
							else if(_code.startsWith("@K")){
								wheresql +=" and a.e01a1 like '"+codesstr+"%' " ;
	
							}
							sql+=wheresql;
							basicsql=sql;
							if(!datelist.isEmpty()){//时间维度  临时添加第一次运行默认显示当前选择的时间分析区间所对应的当前时间的统计结果
								String datewheresql = getDatewheresql(datelist,time_dimension);
								sql+=" and "+datewheresql;
							}
						}
						else{
							return listso;
						}
						
					}else{
						return listso;
					}
				}
				
			}else{
//				sql+="select b.* FROM  ("+sodukusql+") b ";
				if(!whereList.isEmpty()&&codewhereList.isEmpty()&&datelist.isEmpty()){//只选择人员范围
					String persql = getPersql(lateral_index,longitudinal_index,sodukusql,whereList,time_dimension);
					sql+=persql;
				}
				else if(!codewhereList.isEmpty()&&whereList.isEmpty()&&datelist.isEmpty()){//只选择组织范围
					sql+=sodukusql+" and (";
					String codesql = "";
					for(int s=0;s<codewhereList.size();s++){
						String ss = (String)codewhereList.get(s);
						codesql+=ss+" or ";
					}
					codesql=codesql.substring(0,codesql.length()-3);
					sql+=codesql+" ) ";
				}
				else if(!datelist.isEmpty()&&codewhereList.isEmpty()&&whereList.isEmpty()){//只选时间维度
					String datewheresql = getDatewheresql(datelist,time_dimension);
					sql+=sodukusql+" and "+datewheresql;
				}
				else if(codewhereList.size()>0&&whereList.size()>0&&datelist.isEmpty()){//组织范围和人员范围
					String persql = getPersql(lateral_index,longitudinal_index,sodukusql,whereList,time_dimension);
					sql+=persql+" and (";
					String codesql = "";
					for(int s=0;s<codewhereList.size();s++){
						String ss = (String)codewhereList.get(s);
						codesql+=ss+" or ";
					}
					codesql=codesql.substring(0,codesql.length()-3);
					sql+=codesql+" ) ";
				}
				else if(!codewhereList.isEmpty()&&whereList.isEmpty()&&!datelist.isEmpty()){//组织和时间
					String datewheresql = getDatewheresql(datelist,time_dimension);
					sql+=sodukusql+" and (";
					String codesql = "";
					for(int s=0;s<codewhereList.size();s++){
						String ss = (String)codewhereList.get(s);
						codesql+=ss+" or ";
					}
					codesql=codesql.substring(0,codesql.length()-3);
					sql+=codesql+" ) ";
					sql+=" and "+datewheresql;
				}
				else if(codewhereList.isEmpty()&&!whereList.isEmpty()&&!datelist.isEmpty()){//人员范围和时间
					String persql = getPersql(lateral_index,longitudinal_index,sodukusql,whereList,time_dimension);
					String datewheresql = getDatewheresql(datelist,time_dimension);
					sql+=persql+" and "+datewheresql;
				}
				else if(!codewhereList.isEmpty()&&!whereList.isEmpty()&&!datelist.isEmpty()){//全部选择
					String persql = getPersql(lateral_index,longitudinal_index,sodukusql,whereList,time_dimension);
					String datewheresql = getDatewheresql(datelist,time_dimension);
					sql+=persql+" and (";
					String codesql = "";
					for(int s=0;s<codewhereList.size();s++){
						String ss = (String)codewhereList.get(s);
						codesql+=ss+" or ";
					}
					codesql=codesql.substring(0,codesql.length()-3);
					sql+=codesql+" ) and "+datewheresql ;
				}
				else{
					sql = sodukusql;
				}
				basicsql=sodukusql;
			}

			if(!"".equals(basicsql)){
				basicsql = PubFunc.encrypt(basicsql);
			}
			latcode = latcode.replaceAll("'", "");
			String [] latarr = latcode.split(",");
			
			longcode = longcode.replaceAll("'", "");
			String [] longarr = longcode.split(",");
			
			rst = dao.search(sql);
			ArrayList lists = new ArrayList();
			while(rst.next()){
				HashMap maps = new HashMap();
				String a0100 = rst.getString("a0100");
				String nbase = rst.getString("nbase");
				String a0101 = rst.getString("a0101");
				String e0122 = rst.getString("e0122");
				String e01a1 = rst.getString("e01a1");
				String late = rst.getString(lateral_index);//'01','02','03','04'
				String longte = rst.getString(longitudinal_index);//'1','2','3','9'
				maps.put("a0100",a0100);
				maps.put("nbase",nbase);
				maps.put("a0101",a0101);
				maps.put("e0122",e0122);
				maps.put("e01a1",e01a1);
				maps.put("late",late);
				maps.put("longte",longte);
				lists.add(maps);
			}
			
			//xus 20/4/22 vfs获取人员照片
			insertFileIdInList(lists,dao,rst);
			
			int num = 0;
			for(int i=0;i<latarr.length;i++){
				for(int j=0;j<longarr.length;j++){
					HashMap map = new HashMap();
					String x = latarr[i];
					String y = longarr[j];
					ArrayList list = new ArrayList();
					String element_desc = "";
					String titlesql = "select description from t_sys_box_cell where h_field_value='"+x+"' and v_field_value='"+y+"' and box_id='"+cassetteid+"'";
					rst1 = dao.search(titlesql);
					while(rst1.next()){
						element_desc = rst1.getString("description");
					}
					for(int k=0;k<lists.size();k++){
						HashMap maps = (HashMap)lists.get(k);
						ArrayList<String> perlist = new ArrayList<String>();
						String a0100 = (String)maps.get("a0100");
						String nbase = (String)maps.get("nbase");
						String e0122 = (String)maps.get("e0122");;//姓名
						String e01a1 = (String)maps.get("e01a1");;//单位
						String a0101 = (String)maps.get("a0101");;//部门
						String late = (String)maps.get("late");//'01','02','03','04'
						String longte = (String)maps.get("longte");//'1','2','3','9'
						String fileid = (String)maps.get("fileid");
						if(x.equals(late)&&y.equals(longte)){
							String carda0100 = "~" + SafeCode.encode(PubFunc.convertTo64Base(a0100));
							perlist.add(a0100);
							perlist.add(PubFunc.encrypt(a0100));
							perlist.add(carda0100);
							perlist.add(nbase);
							perlist.add(PubFunc.encrypt(nbase));
							perlist.add(a0101);
							perlist.add(e0122);
							perlist.add(e01a1);
							perlist.add(fileid);
							list.add(perlist);
						}
					}
					map.put("xy", x+"_"+y);
					map.put("xylist", list);
					map.put("title", element_desc);
					int size = list.size();
					num+=size;
					listso.add(map);
					}
			}
			listso.add(num);
			listso.add(basicsql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeDbObj(rst);
			 PubFunc.closeDbObj(rst1);
		}
		return listso;
	}
	
	/**
	 * xus 获取人员照片fileid
	 * @param list
	 * @param dao2
	 * @param rst
	 */
	private void insertFileIdInList(ArrayList list, ContentDAO dao2, RowSet rst) {
		if(list.isEmpty())
		{
			return;
		}
		
		StringBuffer fileSql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		HashMap<String,ArrayList> nbaseMap = new HashMap<String,ArrayList>();
		ArrayList values = new ArrayList();
		HashMap<String,HashMap<String,String>> totmap = new HashMap<String, HashMap<String,String>>();
		
		try {
			fileSql.append(" select nbase,a0100,path from ( select upper(nbase) nbase,upper(a0100) a0100,path from hr_multimedia_file where class = 'P' ) t "); 
			
			/** 遍历list 拼接查询语句 */
			for(Object o : list) {
				HashMap map = (HashMap)o;
				String a0100 = (String) map.get("a0100");
				String nbase = (String) map.get("nbase");
				if(StringUtils.isBlank(a0100) || StringUtils.isBlank(nbase))
				{
					continue;
				}
				if(whereSql.length() > 0) 
				{
					whereSql.append(" or ");
				}
				else 
				{
					whereSql.append(" where ");
				}
				whereSql.append(" ( nbase = ? and a0100 = ? ) ");
				values.add(nbase.toUpperCase());
				values.add(a0100.toUpperCase());
			}
			fileSql.append(whereSql.toString());
			
			rst = dao2.search(fileSql.toString(),values);
			while(rst.next()) {
				String dbNbase  = rst.getString("nbase");
				String dbA0100  = rst.getString("a0100");
				String dbFileid  = rst.getString("path");
				
				//获取头像图片fileid
				String[] imgs = dbFileid.split(",");
				if(imgs.length >1)
				{
					dbFileid = imgs[1];
				}
				else
				{
					dbFileid = "";
				}
				
				if(totmap.containsKey(dbNbase)) 
				{
					if(!totmap.get(dbNbase).containsKey(dbA0100) || StringUtils.isBlank(totmap.get(dbNbase).get(dbA0100)))
					{
						totmap.get(dbNbase).put(dbA0100, dbFileid);
					}
				}
				else 
				{
					HashMap<String,String> a0100ToFileidMap = new HashMap<String, String>(); 
					a0100ToFileidMap.put(dbA0100, dbFileid);
					totmap.put(dbNbase,a0100ToFileidMap);
				}
		
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		/** 将人员照片放到集合中 */
		for(Object o : list) 
		{
			HashMap map = (HashMap)o;
			String a0100 = (String) map.get("a0100");
			String nbase = (String) map.get("nbase");
			if(!totmap.containsKey(nbase) || !totmap.get(nbase).containsKey(a0100)) 
			{
				map.put("fileid", "");
			}
			else 
			{
				map.put("fileid", totmap.get(nbase).get(a0100));
			}
		}
	}

	/**
	 * 得到人员范围sql
	 * @param lateral_index
	 * @param longitudinal_index
	 * @param sodukusql
	 * @param whereList
	 * @return
	 */
	private String getPersql(String lateral_index, String longitudinal_index,
			String sodukusql, ArrayList whereList ,String time_dimension) {
		String sql = "";
		if("".equals(time_dimension)){
			sql+=" select c.a0101,c.a0100,c.B0110,c.e0122,c.e01a1,c.nbase,c."+lateral_index+",c."+longitudinal_index+" from ("+sodukusql+") c where exists (select 1 from (";
		}else{
			sql+=" select c.a0101,c.a0100,c.B0110,c.e0122,c.e01a1,c.nbase,c."+lateral_index+",c."+longitudinal_index+",c."+time_dimension+" from ("+sodukusql+") c where exists (select 1 from (";
		}
		String nbaseSql = "";
		for(int m=0;m<whereList.size();m++){
			String nsql = (String)whereList.get(m);
			if(!"".equals(nbaseSql)){
				nbaseSql+=" union all "+nsql;
			}else{
				nbaseSql=nsql;
			}
		}
		sql+=nbaseSql+" )  b where b.A0100=c.A0100)";
		return sql;
	}
	/**
	 * 得到日期时间sql
	 * @param datelist
	 * @param time_dimension
	 * @return
	 */
	private String getDatewheresql(ArrayList datelist ,String time_dimension) {
		String sql = "";
		String year = "";
		String quarter = "";
		String month = "";
		String montharr ="";
		MorphDynaBean mdyear = (MorphDynaBean)datelist.get(0);
		year = (String)mdyear.get("year");
		MorphDynaBean mdquar = (MorphDynaBean)datelist.get(1);
		quarter = (String)mdquar.get("quarter");
		MorphDynaBean mdmon = (MorphDynaBean)datelist.get(2);
		month = (String)mdmon.get("month");
		if(!"".equals(quarter)){
			if("1".equals(quarter)){
				montharr = "'01','02','03'";
			}
			else if("2".equals(quarter)){
				montharr = "'04','05','06'";
			}
			else if("3".equals(quarter)){
				montharr = "'07','08','09'";
			}
			else if("4".equals(quarter)){
				montharr = "'10','11','12'";
			}
		}
		if(!"".equals(year)&&"".equals(quarter)&&"".equals(month)){//年
			switch(Sql_switcher.searchDbServer()){
			 case Constant.MSSQL:
				 sql= " datepart(yyyy,"+time_dimension+")='"+year+"'";
				  break;
			 case Constant.ORACEL:
				 sql=" to_char("+time_dimension+",'yyyy')='"+year+"'";
				  break;
			}
		}
		else if(!"".equals(year)&&!"".equals(quarter)){//年季
			switch(Sql_switcher.searchDbServer()){
			 case Constant.MSSQL:
				 sql=" datepart(mm,"+time_dimension+") in ("+montharr+") and datepart(yyyy,"+time_dimension+")='"+year+"'";
				  break;
			 case Constant.ORACEL:
				 sql=" to_char("+time_dimension+",'mm') in ("+montharr+") and to_char("+time_dimension+",'yyyy')='"+year+"'";
				  break;
			}
		}
		else if(!"".equals(year)&&!"".equals(month)){//年月
			switch(Sql_switcher.searchDbServer()){
			 case Constant.MSSQL:
				 sql=" datepart(mm,"+time_dimension+")='"+month+"' and datepart(yyyy,"+time_dimension+")='"+year+"'";
				  break;
			 case Constant.ORACEL:
				 sql=" to_char("+time_dimension+",'mm') ='"+month+"' and to_char("+time_dimension+",'yyyy')='"+year+"'";
				  break;
			}
		}
		return sql;
	}

//	/**
//	 * 获得中间sql
//	 * @param firstname
//	 * @param index
//	 * @param code
//	 * @return
//	 */
//	private String getMiddleSql(String firstname, String index,String code,String flag) {
//		StringBuffer sb = new StringBuffer();
//		if(firstname.startsWith("A")){
//			if(firstname.startsWith("A01")){
//				sb.append("(select "+index+" from UsrA01 b where a.a0100 = b.a0100 and b."+index+" in("+code+")) as "+flag+", ");
//			}else{
//				sb.append("(select "+index+" from Usr"+firstname+" b where a.A0100=b.A0100 and b."+index+" in("+code+")");
//				sb.append(" and b.I9999=(select MAX(b7.I9999) from Usr"+firstname+" b7 where a.A0100=b7.A0100)) as "+flag+", ");
//			}
//		}
//		else if(firstname.startsWith("B")){
//			if(firstname.startsWith("B01")){
//				sb.append("(select "+index+" from B01 b where a.B0110 = b.B0110 and b."+index+" in("+code+")) as "+flag+", ");
//			}else{
//				sb.append("(select "+index+" from "+firstname+" b where a.B0110 = b.B0110 and b."+index+" in("+code+")");
//				sb.append(" and b.I9999=(select MAX(b7.I9999) from "+firstname+" b7 where a.B0110=b7.B0110)) as "+flag+", ");
//			}
//		}
//		else if(firstname.startsWith("K")){
//			if(firstname.startsWith("K01")){
//				sb.append("(select "+index+" from K01 b where a.E01A1 = b.E01A1 and b."+index+" in("+code+")) as "+flag+", ");
//			}else{
//				sb.append("(select "+index+" from "+firstname+" b where a.E01A1 = b.E01A1 and b."+index+" in("+code+")");
//				sb.append(" and b.I9999=(select MAX(b7.I9999) from "+firstname+" b7 where a.E01A1=b7.E01A1)) as "+flag+", ");
//			}
//		}
//		return sb.toString();
//	}

	/**
	 * 获取横纵坐标指标分项
	 * @param index
	 * @param data_source
	 * @param dao
	 * @return
	 */
	public String getCodeItem(String index, String data_source,ContentDAO dao,String flag) {
		RowSet rst = null;
		String latcode="";
		try {
			String latsql = "select a.codeitemid from codeitem a,codeset b where a.codesetid=b.CodeSetId and a.codeitemid=a.parentid and a.invalid='1' and  b.CodeSetId=(select c.codesetid from t_hr_busifield c where c.FieldSetId='"+data_source+"' and c.ItemId='"+index+"') order by a.codeitemid "+flag;
			rst = dao.search(latsql);
			while(rst.next()){
				String codeitemid = rst.getString("codeitemid");
				latcode+="'"+codeitemid+"',";
			}
			if(!"".equals(latcode)){
				latcode = latcode.substring(0, latcode.length()-1);
			}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				 PubFunc.closeDbObj(rst);
			}
		return latcode;
	}

	public ArrayList getcodeMap(String index, String data_source,ContentDAO dao, String flag) {
		RowSet rst = null;
		ArrayList lists = new ArrayList();
		try {
			String latsql = "select a.codeitemid,a.codeitemdesc from codeitem a,codeset b where a.codesetid=b.CodeSetId and a.codeitemid=a.parentid and a.invalid='1' and  b.CodeSetId=(select c.codesetid from t_hr_busifield c where c.FieldSetId='"+data_source+"' and c.ItemId='"+index+"') order by a.a0000 "+flag+" ,a.codeitemid "+flag;
			rst = dao.search(latsql);
			while(rst.next()){
				HashMap map = new HashMap();
				String codeitemid = rst.getString("codeitemid");
				String codeitemdesc = rst.getString("codeitemdesc");
				map.put("codeitemid", codeitemid);
				map.put("codeitemdesc", codeitemdesc);
				lists.add(map);
			}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				 PubFunc.closeDbObj(rst);
			}
		return lists;
	}

	/**
	 * 得到库前缀
	 * @param data_source
	 * @return
	 */
	public ArrayList getDbnamelist(String data_source,ContentDAO dao) {
		RowSet rst = null;
		ArrayList list = new ArrayList();
		try {
			String sql = "select nbase from "+data_source+" group by nbase";
			rst = dao.search(sql);
			while(rst.next()){
				String nbase = rst.getString("nbase");
				list.add(nbase);
			}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				 PubFunc.closeDbObj(rst);
			}
		return list;
	}

	
}
