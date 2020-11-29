package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 资格评审_专家选择控件检索
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class RandomSelectionFastSearchTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("schemeConfig");// 已选方案
		String state = (String)this.getFormHM().get("state");// 1：抽选 2：撤选
		ArrayList w0101List = (ArrayList)this.getFormHM().get("w0101List");
		HashMap schemeConfig = PubFunc.DynaBean2Map(bean);
		int seq = (Integer)this.getFormHM().get("seq");
		
		try {
			StringBuilder datasql = new StringBuilder();

			String tablesql = userView.getHm().get("random_selection_tablesql").toString();
			if("1".equals(state)){//专家抽选
				tablesql = tablesql.replace("{state}", "not in");
			} else if("2".equals(state)){//撤选
				tablesql = tablesql.replace("{state}", "in");
				
			}
			Iterator iter = schemeConfig.entrySet().iterator();
			int i=0;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String schemeid = entry.getKey().toString();
				int num = Integer.parseInt(entry.getValue().toString());
				String itemSql = this.getRandomSql(tablesql, schemeid, num, w0101List, seq,state);//每项sql
				
				if(StringUtils.isNotEmpty(itemSql)){
					if(i > 0){//第一次不加or
						datasql.append(" union all ");//可以重复，后面会统一去重
					}
					datasql.append(itemSql);
				}
				i++;
			}
			
			// 去重
			StringBuilder sql = new StringBuilder();
			// 没有重复的数据
			sql.append(" select * from (");
			sql.append(datasql);
			sql.append(" ) kk ");
			sql.append(" where W0101 not in ( ");
			sql.append(" select W0101 from ( ");
			sql.append(" select w0101,COUNT(w0101) as sum1 from ("+datasql+") ll group by w0101) ooo ");
			sql.append(" where  sum1>1 ");
			sql.append(" ) ");
			
			sql.append(" union ");
			// 重复的数据，只拿第一条
			int db_type = Sql_switcher.searchDbServer();//数据库类型
			if(db_type == 1){//sql server
				
				sql.append(" select top 1 * from  (");
				sql.append(datasql);
				sql.append(" ) kk ");
				sql.append(" where W0101 in ( ");
				sql.append(" select W0101 from ( ");
				sql.append(" select w0101,COUNT(w0101) as sum1 from ("+datasql+") ll group by w0101) ooo ");
				sql.append(" where  sum1>1 ");
				sql.append(" ) ");
			} else if(db_type == 2){//oracle
				sql.append(" select * from  (");
				sql.append(datasql);
				sql.append(" ) kk ");
				sql.append(" where W0101 in ( ");
				sql.append(" select W0101 from ( ");
				sql.append(" select w0101,COUNT(w0101) as sum1 from ("+datasql+") ll group by w0101) ooo ");
				sql.append(" where  sum1>1 ");
				sql.append(" ) ");
				sql.append("and rownum < 2 ");
			}

			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get("random_selection_00001");
			catche.setTableSql(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 获取条件sql
	 * @param schemeid：方案id
	 * @param num：人数
	 * @param state 1抽选  2撤选
	 * @return
	 */
	private String getRandomSql(String tablesql, String schemeid, int num, ArrayList w0101List, int seq,String state){
		StringBuilder sql = new StringBuilder();
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			
			//String[] exceptStr = {"flag","start_date","end_date","b0110"};//特殊字段：《评委会专家表》中存在、《专家表》中不存在的字段

			ArrayList list = new ArrayList();
			String tmpSql = "select expression,conditem From t_sys_table_query_plan where Query_plan_id=? ";
			list.add(schemeid);
			rs = dao.search(tmpSql.toString(), list);
			
			StringBuilder dataSql = new StringBuilder(tablesql);
			if (rs.next()) {
				String expression = rs.getString("expression");
				String conditem = rs.getString("conditem");
				
				if(!StringUtils.isEmpty(expression) && !StringUtils.isEmpty(conditem)){//自定义检索方案
					// 解析表达式并获得sql语句
					HashMap<String, FieldItem> map = new HashMap<String, FieldItem>();
					
					ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
					for(int i=0; i<fieldList.size(); i++){
						FieldItem item = (FieldItem)fieldList.get(i);		
						String itemid = item.getItemid();//字段id
						map.put(itemid, item);
					}
					
					FieldItem item = new FieldItem();
					item.setItemid("fixed_member");
					item.setUseflag("1");
					item.setItemtype("A");
					item.setCodesetid("45");
					item.setItemdesc("固定成员");
					item.setFieldsetid("");
					item.setItemlength(1);
					item.setVarible(1);
					map.put("fixed_member", item);
					FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(expression)) ,PubFunc.keyWord_reback(SafeCode.decode(conditem)), userView.getUserName(), map);
					dataSql.append(" and ");
					dataSql.append(parser.getSingleTableSqlExpression("data").replaceAll("data.", ""));
					
				}
			}
			
			StringBuilder selectSql = new StringBuilder();
			//程序兼容 ： bug 31388   专家抽取时，不考虑固定成员列  2017-09-08
			if("1".equals(state) && dataSql.indexOf("fixed_member")!=-1) {
				int index1 = dataSql.indexOf("fixed_member");
				int index2 = dataSql.indexOf(")", index1);
				String tsql = dataSql.toString().substring(0, index1)+"1=2"+dataSql.toString().substring(index2);
				
				selectSql.append(tsql);
			}else {
				selectSql = new StringBuilder(dataSql);
			}
			if(w0101List.size() > 0){// 排除已有数据
				String notInStr = "";
				for(int i=0; i<w0101List.size(); i++){
					MorphDynaBean bean = (MorphDynaBean)w0101List.get(i);
					HashMap data = PubFunc.DynaBean2Map(bean);
					String w0101 = PubFunc.decrypt((String)data.get("w0101"));
					notInStr += "'"+w0101+"',";
				}
				notInStr = notInStr.substring(0, notInStr.length()-1);
				selectSql.append(" and w01.w0101 not in( "+ notInStr + ") ");
			}
			
			StringBuilder str = new StringBuilder();
			ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				if("w0101".equals(itemid))
					continue;
				str.append(itemid+",");
			}
			str.append("w0101,");//防止w0101被隐藏
		
			int db_type = Sql_switcher.searchDbServer();//数据库类型
			sql.append("select * from ( ");
			StringBuffer randSql = new StringBuffer();
			if(db_type == 1){//sql server
				randSql.append("select top "+num +" w0101 ");
				randSql.append("from ");
				randSql.append("(");
				randSql.append(selectSql);
				randSql.append(") RAND");
				randSql.append(" order by NEWID()");
			} else if(db_type == 2){//oracle
				randSql.append("select w0101 ");
				randSql.append("from ");
				randSql.append("(");
				randSql.append(selectSql);
				randSql.append(" order by dbms_random.value ) RAND ");
			}
			//查询出随机的专家号
			if(db_type == 2){
				randSql.append("where rownum < "+(num+1)+" ");
			}
			rs = dao.search(randSql.toString());
			String inStr = "";
			while(rs.next()) {
				inStr+="'"+rs.getString("w0101")+"',";
			}
			sql.append("select "+str+"'"+schemeid+"' as schemeid, '"+seq+"' as seq ");
			sql.append("from ");
			sql.append("("+selectSql+") d where ");
			if(inStr.length()>0) {
				sql.append("d.w0101 in (");
				sql.append(inStr.substring(0, inStr.length()-1) + ")");
			}else {
				sql.append("1=2");
			}
			sql.append(" ) YYY ");
			
			if(w0101List.size() > 0){// 加上前台传过来的数据
				
				for(int i=0; i<w0101List.size(); i++){
					MorphDynaBean bean = (MorphDynaBean)w0101List.get(i);
					HashMap data = PubFunc.DynaBean2Map(bean);
					String w0101 = PubFunc.decrypt((String)data.get("w0101"));
					String scheme_id = (String)data.get("schemeid");
					String seqs = (String)data.get("seq");
					
					sql.append(" union ");
					sql.append("select "+str+"'"+scheme_id+"' as schemeid, '"+seqs+"' as seq ");
					sql.append(" from ( ");
					sql.append(tablesql + " and w01.w0101 in( "+ w0101 + ") ");
					sql.append(" ) TT_"+i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return sql.toString();
	}
}
