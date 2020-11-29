package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

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
 * 类名称：SaveSalaryTableTrans 
 * 类描述：保存薪资发放数据 
 * 创建人：zhaoxg
 * 创建时间：Jun 25, 2015 2:22:38 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 25, 2015 2:22:38 PM
 * 修改备注： 
 * @version
 */
public class SaveSalaryTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList datalist=(ArrayList) this.getFormHM().get("savedata");
			//String tablename = (String) this.getFormHM().get("tablekey");
//			tablename = PubFunc.decrypt(SafeCode.decode(tablename));
			String tablename="";
			String tablekey= (String) this.getFormHM().get("tablekey");
			String [] stritem=tablekey.split("_");
			if(StringUtils.isNotBlank(tablekey)&&stritem.length>1){
				tablename=new SalaryTemplateBo(this.frameconn, Integer.parseInt(stritem[stritem.length-1]), this.userView).getGz_tablename();
			}
			if(StringUtils.isBlank(tablename))
				return;
			
			StringBuffer sql = new StringBuffer();		
			StringBuffer str = new  StringBuffer();
			StringBuffer where = new  StringBuffer();
			ArrayList valueList = new ArrayList();
			String noeditStr = ",a0000_e,add_flag,changestate,nbase,appprocess,sp_flag,a00z2,a00z3,a0000,a0100,,e0122_o,b0110_o,dbid,sp_flag2,record_internalid,";//这几个字段不会被页面编辑
			sql.append("update "+tablename+" set ");
			
			ArrayList taxMxList=new ArrayList();
			String updateTAX="update gz_tax_mx set a00z0=?,a00z1=? where salaryid="+Integer.parseInt(stritem[stritem.length-1])+" and a00z0=? and a00z1=? and upper(nbase)=? and a0100=?";
			for(int i=0;i<datalist.size();i++){
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				
				String a00z0_o="";
				String a00z1_o="";
				String a00z0="";
				String a00z1="";
				String a0100="";
				String nbase="";
				
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if("id".equals(key) || noeditStr.indexOf(","+key.toString().toLowerCase()+",")!=-1||"MVP_Data_Key".equals(key)){//这几个字段不会被页面编辑
						continue;
					}

					if("a00z0".equalsIgnoreCase(key.toString())||"a00z1".equalsIgnoreCase(key.toString())){
						if(i==0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						if("a00z0".equalsIgnoreCase(key.toString())){
							a00z0=val.toString();
							Date date = DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd");
							list.add(date);
						}else{
							a00z1=val.toString();
							list.add(val.toString());
						}
					}else if("a00z01".equalsIgnoreCase(key.toString())||"a00z11".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append(key.toString().substring(0, key.toString().length()-1));
							where.append(" =? ");
						}
						if("a00z01".equalsIgnoreCase(key.toString())){
							a00z0_o=val.toString();
							Date date = DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd");
							wherelist.add(date);
						}else{
							a00z1_o=val.toString();
							wherelist.add(val.toString());
						}
					}else if("a0100_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("a0100");
							where.append(" =? ");
						}
						a0100=PubFunc.decrypt(val.toString());
						wherelist.add(PubFunc.decrypt(val.toString()));
					}else if("nbase1_e".equalsIgnoreCase(key.toString())){
						if(i==0){
							where.append("and ");
							where.append("upper(nbase)");
							where.append(" =? ");
						}	
						nbase=PubFunc.decrypt(val.toString()).toUpperCase();
						wherelist.add(PubFunc.decrypt(val.toString()).toUpperCase());
					}else{
						if(i==0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						if("A01Z0".equalsIgnoreCase(key.toString())){//停发标识
							list.add(val==null||val.toString().length()==0?val.toString():(val.toString()).split("`")[0]);
							continue;
						}
						
						FieldItem fiem = (FieldItem)DataDictionary.getFieldItem(key.toString());		
						if(fiem!=null&&fiem.getCodesetid()!=null&&!"0".equals(fiem.getCodesetid())&&val!=null&&val.toString().length()>0){
								if(val.toString().split("`").length!=2)//当格式不正确时，置空
									val="";		
							list.add(val==null||val.toString().length()==0?val.toString():(val.toString()).split("`")[0]);
						}else if(fiem!=null&& "D".equals(fiem.getItemtype())){
							Timestamp timestamp = null;
							if(val!=null&&StringUtils.isNotBlank(val.toString())){
								String dateStr = val.toString();
								//进行时间格式化，可以保存到时分秒
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
								list.add(val==null||val.toString().length()==0?null:Integer.parseInt(val.toString()));
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
				}		
				
				
				if(a00z0_o.indexOf(a00z0)==-1||!a00z1_o.equals(a00z1))
				{
					ArrayList taxValueList=new ArrayList();
					Date date = DateUtils.getSqlDate(a00z0.toString(),"yyyy-MM-dd");
					taxValueList.add(date);
					taxValueList.add(a00z1.toString());
					date = DateUtils.getSqlDate(a00z0_o.toString(),"yyyy-MM-dd");
					taxValueList.add(date);
					taxValueList.add(a00z1_o.toString());
					taxValueList.add(nbase);
					taxValueList.add(a0100);
					taxMxList.add(taxValueList);
				}
				
				
			}
			dao.batchUpdate(sql.toString(), valueList);
			if(taxMxList.size()>0) //归属日期和归属次数如被改动得同步更新税表记录。
			{
				dao.batchUpdate(updateTAX, taxMxList);
			}
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
