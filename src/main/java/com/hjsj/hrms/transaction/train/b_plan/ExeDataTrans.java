package com.hjsj.hrms.transaction.train.b_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 将读入内存的数据持久化到数据库
 * @author xujian
 *Apr 24, 2012
 */
public class ExeDataTrans extends IBusiness {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		int num=0;
		try{
			Object[] maps=(Object[])this.getFormHM().get("maps");
			String isupdate =(String)this.getFormHM().get("isupdate");
			isupdate=isupdate==null|| "".equals(isupdate)?"1":isupdate;
			String updatestr = (String)this.getFormHM().get("updatestr");
			updatestr=updatestr!=null?updatestr:"";
			HashMap fieldMap = (HashMap)maps[0];
			if(fieldMap==null)
				return;
			ArrayList valueList = (ArrayList)maps[1];
			if(valueList==null||valueList.size()==0){
				return;
			}
			ArrayList keyList =(ArrayList)maps[3];
			String primarykey=(String)keyList.get(0);
			StringBuffer a0100sb=(StringBuffer)maps[4];//用于判断库中是否已存在
			if(keyList==null||keyList.size()==0)
				return;
			ContentDAO dao = new ContentDAO(this.frameconn);
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			RecordVo vo=null;
			int size=valueList.size();
			boolean flag = this.initI9999(size, dao);
				for(int i=0;i<size;i++){
					
					HashMap valueMap=(HashMap)valueList.get(i);
					String keyvalue= (String)valueMap.get(primarykey);
					if(keyvalue==null|| "".equals(keyvalue)){
						continue;
					}
						if(a0100sb.toString().indexOf(keyvalue)!=-1)//库中已存在
							continue;
						vo = new RecordVo("r25");
						for(Iterator it=valueMap.keySet().iterator();it.hasNext();){
							String itemid=(String)it.next();
							FieldItem item = DataDictionary.getFieldItem(itemid);
							if("D".equals(item.getItemtype())){
								String value=(String)valueMap.get(itemid);
								if(value==null||"".equals(value)){
									continue;
								}
								if(!"```".equals(value)){
									vo.setDate(itemid, value);
								}
							}else{
								String value=(String)valueMap.get(itemid);
								if(value==null||"".equals(value)){
									continue;
								}
								if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid())){
									value=splitString(value, item.getItemlength());
								}
								if(!"```".equals(value)){
									vo.setString(itemid, value);
								}
							}
						}
						vo.setString("r2509", "01");
						vo.setString("r2509", "01");
						if(flag){
							vo.setInt("i9999", size-i);
						}
						String priFldValue = idg.getId("R25.R2501");
						vo.setString("r2501", priFldValue);
					num+=dao.addValueObject(vo);
				}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("num", String.valueOf(num));
		}
	}

	
	private String splitString(String source, int len)
	  {
	    byte[] bytes = source.getBytes();
	    int bytelen = bytes.length;
	    int j = 0;
	    int rlen = 0;
	    if (bytelen <= len)
	      return source;

	    for (int i = 0; i < len; ++i)
	    {
	      if (bytes[i] < 0)
	        ++j;
	    }
	    if (j % 2 == 1)
	      rlen = len - 1;
	    else
	      rlen = len;
	    byte[] target = new byte[rlen];
	    System.arraycopy(bytes, 0, target, 0, rlen);
	    String dd = new String(target);
	    return dd;
	  }
	private boolean initI9999(int num,ContentDAO dao){
		DbWizard dbw=new DbWizard(this.getFrameconn());
	    //如果存在i9999字段,首先将所有该字段非空记录加num,再将要保存的记录该字段设为1
		boolean flag=dbw.isExistField("r25", "i9999", false);
	    if(flag){
	    	String sql = "update r25 set i9999=i9999+"+num+" where i9999 is not null";
		    try {
				dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    return flag;
	}
}
