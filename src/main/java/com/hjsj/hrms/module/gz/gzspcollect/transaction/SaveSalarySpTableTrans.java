package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SaveSalarySpTableTrans 
 * 类描述： 薪资审批明细保存
 * 创建人：zhaoxg
 * 创建时间：Dec 28, 2015 1:53:58 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 28, 2015 1:53:58 PM
 * 修改备注： 
 * @version
 */
public class SaveSalarySpTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList datalist=(ArrayList) this.getFormHM().get("savedata");
			String tablekey = (String) this.getFormHM().get("tablekey");
			String salaryid = tablekey.split("_")[1];//修改现在传过来的是salaryspdetail_XX这种的sunjian 2017-06-26
			String tablename = "salaryhistory";
			StringBuffer sql = new StringBuffer();
			StringBuffer str = new  StringBuffer();
			StringBuffer where = new  StringBuffer();
			ArrayList valueList = new ArrayList();
			String noeditStr = ",add_flag,changestate,nbase,appprocess,sp_flag,a00z2,a00z3,a0000,a0100,,e0122_o,b0110_o,dbid,sp_flag2,record_internalid,";//这几个字段不会被页面编辑
			sql.append("update "+tablename+" set ");
			for(int i=0;i<datalist.size();i++){
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if("id".equals(key) || noeditStr.indexOf(","+key.toString().toLowerCase()+",")!=-1){//这几个字段不会被页面编辑
						continue;
					}
					if("a00z0".equalsIgnoreCase(key.toString())||"a00z1".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append(key.toString());
							where.append(" =? ");
						}
						if("a00z0".equalsIgnoreCase(key.toString())){
							Date date = DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd");
							wherelist.add(date);
						}else{
							wherelist.add(val.toString());
						}
					}else if("a0100_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("a0100");
							where.append(" =? ");
						}
						wherelist.add(PubFunc.decrypt(val.toString()));
					}else if("nbase1_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("upper(nbase)");
							where.append(" =upper(?) ");
						}						
						wherelist.add(PubFunc.decrypt(val.toString()));
					}else if("MVP_DATA_KEY".equalsIgnoreCase(key.toString())){
						continue;
					}else{
						if(i==0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						FieldItem fiem = (FieldItem)DataDictionary.getFieldItem(key.toString());
						if(fiem!=null&&fiem.getCodesetid()!=null&&!"0".equals(fiem.getCodesetid())&&val!=null&&val.toString()!=null&&val.toString().length()>0){
							list.add(val==null||val.toString().length()==0?val.toString():val.toString().split("`")[0]);
						}else if(fiem!=null&& "D".equals(fiem.getItemtype())){
							Timestamp timestamp = null;
							if(val!=null&&StringUtils.isNotBlank(val.toString())){
								String dateStr = val.toString();
								if(dateStr.length() == 19) 
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
								}
								else if(dateStr.length() == 16) 
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm");
								}
								else if(dateStr.length() == 7)
								{	
									timestamp = getTimeStamp(dateStr, "yyyy.MM", "yyyy-MM");
								}
								else if(dateStr.length()==4)
								{
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
									timestamp = new Timestamp(sdf.parse(dateStr).getTime());
								}
								else
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd", "yyyy-MM-dd");
								}
								list.add(timestamp);
							}else{
								list.add(timestamp);
							}
						}else if(fiem!=null&& "N".equals(fiem.getItemtype())){
							if(fiem.getDecimalwidth()==0){
								list.add(val.toString().length()==0?null:Integer.parseInt(val.toString()));
							}else{
								String value = PubFunc.DoFormatDecimal(val==null||val.toString().length()==0?"":val.toString(), fiem.getDecimalwidth());
								list.add(value.length()==0?null:PubFunc.parseDouble(value));
							}
						}else{
							list.add(val==null||val.toString().length()==0?"":val.toString());
						}
					}
				}
				list.addAll(wherelist);
				valueList.add(list);
				if(i==0){
					sql.append(str.substring(1));
					sql.append(" where ");
					sql.append(where.substring(3));
					sql.append(" and salaryid='"+salaryid+"'");
				}
			}
			dao.batchUpdate(sql.toString(), valueList);
			
			//------------------------------更新临时表数据------------------------------------
			String noStr = ",add_flag,userflag,sp_flag,curr_user,appuser,appprocess,";//历史表多余临时表的字段
			where = new  StringBuffer(" 1=1 ");
			str.setLength(0);
			for(int i=0;i<datalist.size();i++){
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				valueList = new ArrayList();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if("id".equals(key) || noeditStr.indexOf(","+key.toString().toLowerCase()+",")!=-1){//这几个字段不会被页面编辑
						continue;
					}
					if("a00z0".equalsIgnoreCase(key.toString())||"a00z1".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append(key.toString());
							where.append(" =? ");
						}
						if("a00z0".equalsIgnoreCase(key.toString())){
							Date date = DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd");
							wherelist.add(date);
						}else{
							wherelist.add(val.toString());
						}
					}else if("a0100_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("a0100");
							where.append(" =? ");
						}
						wherelist.add(PubFunc.decrypt(val.toString()));
					}else if("nbase1_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("upper(nbase)");
							where.append(" =upper(?) ");
						}						
						wherelist.add(PubFunc.decrypt(val.toString()));
					}else if("MVP_DATA_KEY".equalsIgnoreCase(key.toString())||noStr.indexOf(","+key.toString().toLowerCase()+",")!=-1){
						continue;
					}else{
						if(i==0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						FieldItem fiem = (FieldItem)DataDictionary.getFieldItem(key.toString());
						if(fiem!=null&&fiem.getCodesetid()!=null&&!"0".equals(fiem.getCodesetid())&&val!=null&&val.toString().length()>0){
							list.add(val==null||val.toString().length()==0?val.toString():val.toString().split("`")[0]);
						}else if(fiem!=null&& "D".equals(fiem.getItemtype())){
							Timestamp timestamp = null;
							if(val!=null&&StringUtils.isNotBlank(val.toString())){
								String dateStr = val.toString();
								if(dateStr.length() == 19) 
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
								}
								else if(dateStr.length() == 16) 
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm");
								}
								else if(dateStr.length() == 7)
								{	
									timestamp = getTimeStamp(dateStr, "yyyy.MM", "yyyy-MM");
								}
								else if(dateStr.length()==4)
								{
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
									timestamp = new Timestamp(sdf.parse(dateStr).getTime());
								}
								else
								{
									timestamp = getTimeStamp(dateStr, "yyyy.MM.dd", "yyyy-MM-dd");
								}
								list.add(timestamp);
							}else{
								list.add(timestamp);
							}
						}else if(fiem!=null&& "N".equals(fiem.getItemtype())){
							if(fiem.getDecimalwidth()==0){
								list.add(val.toString().length()==0?null:Integer.parseInt(val.toString()));
							}else{
								String value = PubFunc.DoFormatDecimal(val==null||val.toString().length()==0?"":val.toString(), fiem.getDecimalwidth());
								list.add(value.length()==0?null:PubFunc.parseDouble(value));
							}
						}else{
							list.add(val==null||val.toString().length()==0?"":val.toString());
						}
					}
				}
				list.addAll(wherelist);
				valueList.add(list);
				String _sql = "select * from  "+tablename+" where "+where + " and salaryid='"+salaryid+"'";
				RowSet rs = dao.search(_sql,wherelist);
				if(rs.next()){
					sql.setLength(0);
					sql.append("update "+rs.getString("userflag")+"_salary_"+salaryid+" set ");
					sql.append(str.substring(1));
					sql.append(" where ");
					sql.append(where);
					dao.batchUpdate(sql.toString(), valueList);
				}
			}
			//----------------------------------------end------------------------------------------------------
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("hinttext",e.toString());
		}
	}

	/**
	 * 获取格式化的时间
	 * @Title: getTimeStamp   
	 * @Description:    
	 * @param @return 
	 * @return Timestamp    
	 * @throws
	 */
	public Timestamp getTimeStamp(String dateStr,String format, String formatS) {
		Timestamp timestamp = null;
		try {
			if(dateStr.indexOf("-")<0) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				timestamp = new Timestamp(sdf.parse(dateStr).getTime());
			} else { 
				SimpleDateFormat sdf = new SimpleDateFormat(formatS);
				timestamp = new Timestamp(sdf.parse(dateStr).getTime());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
}
