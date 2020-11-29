package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SaveExamineesTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList datalist=(ArrayList) this.getFormHM().get("savedata");
		
		StringBuffer sql = new StringBuffer();		
		StringBuffer where = new  StringBuffer();
		String tablename = "z63";//由于只能修改z63子集下的数据，这里固定为z63
		try {
			String itemstr = "nbase,A0100,A0101,Z0321,Z0325,Z0301,Z0351,Z6301";
			String itemAdd = "a0100_e,nbase_e,z0301_e";
			ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
			ArrayList z63_column = getColumn(tablename,itemstr);
			for(int i=0;i<datalist.size();i++){
				StringBuffer str = new  StringBuffer();
				sql.setLength(0);
				where.setLength(0);
				sql.append("update "+tablename+" set ");
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				String items="";
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					items+=key+",";
					if(z63_column.indexOf(key) != -1 || itemAdd.indexOf(key.toString().toLowerCase()) != -1) {//如果是在z63子集中的数据，则修改
						Object val = entry.getValue();
						if("id".equals(key) ||"MVP_Data_Key".equals(key)){//这几个字段不会被页面编辑
							continue;
						}
		
						if("a0100_e".equalsIgnoreCase(key.toString())){//这几个字段是条件
							where.append(" and a0100 =?");
							wherelist.add(PubFunc.decrypt(val.toString()));
						}else if("nbase_e".equalsIgnoreCase(key.toString())){
							where.append(" and upper(nbase) =?");
							wherelist.add(PubFunc.decrypt(val.toString()).toUpperCase());
						}else if("z0301_e".equalsIgnoreCase(key.toString())){
							where.append(" and z0301 =?");
							wherelist.add(PubFunc.decrypt(val.toString()));
						}else {
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
							FieldItem fiem = (FieldItem)DataDictionary.getFieldItem(key.toString());		
							if(fiem!=null&&fiem.getCodesetid()!=null&&!"0".equals(fiem.getCodesetid())&&val.toString()!=null&&val.toString().length()>0){
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
				}
				list.addAll(wherelist);
				sql.append(str.substring(1));
				sql.append(" where ");
				sql.append(where.substring(4));
				dao.update(sql.toString(), list);
			}
		}catch(Exception e) {
			e.printStackTrace();
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
	private Timestamp getTimeStamp(String dateStr,String format, String formatS) {
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
	
	/**获取tableName表中业务字典里所有相关列
	 * @param tableName
	 * @param itemStr
	 * @return
	 * @throws GeneralException
	 */
    private ArrayList getColumn(String tableName,String itemStr) throws GeneralException{
		ArrayList columnList = new ArrayList();
		try {
			ArrayList fieldList = DataDictionary.getFieldList(tableName,Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item = (FieldItem) fieldList.get(i);
				String colunmName = item.getItemid();
				if (item != null && "1".equals(item.getState()) && "1".equals(item.getUseflag()) && itemStr.indexOf(colunmName) == -1)
					columnList.add(colunmName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return columnList;
    }
}
