package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject.ChangeCompareBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:ReducePersonTrans.java</p>
 * <p>Description>:薪资发放_变动比对_减少人员</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 18, 2016 11:00:51 AM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class ReducePersonTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		// 薪资类别编号
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String flag = (String) this.getFormHM().get("flag");//1:取column 2：取store
		try {
			ChangeCompareBo changeCompareBo = new ChangeCompareBo(this.getFrameconn(), this.userView);// 工具类
			// 减少人员临时表
			String tableName="t#"+this.userView.getUserName()+"_gz_Dec";
			if("1".equals(flag)){
				// 不需要的项目
				String exceptStr = ",state,a0100,a0000,a01z0,";
				HashMap map = changeCompareBo.getColumn(changeCompareBo.REDUCE_PERSON, tableName, exceptStr);
				this.getFormHM().put("fields", map.get("fields"));
				this.getFormHM().put("column", map.get("column"));
			}else if("2".equals(flag)){
				int page = Integer.parseInt((String)this.formHM.get("page"));
				int limit = Integer.parseInt((String)this.formHM.get("limit"));
				String sort = (String) this.formHM.get("sort");
				String order = changeCompareBo.getOrderby(sort,tableName);
				ArrayList list = new ArrayList();
				ContentDAO dao = new ContentDAO(this.frameconn);
				String columnStr = changeCompareBo.getColumnStr(tableName);
				String sql = "select "+tableName+".*,'reduce' as tabletype from "+tableName+" "+order;
				String[] str = columnStr.split(",");
				RowSet rs = dao.search(sql,limit,page);
				while(rs.next()){
					HashMap map = new HashMap();
					for(int i=0;i<str.length;i++){
						if("STATE".equalsIgnoreCase(str[i])){
							map.put(str[i].toUpperCase(), "1".equals(rs.getString("STATE"))?"checked":"");//1:选中 0：未选中
						}else if("dbname".equalsIgnoreCase(str[i])){
							map.put(str[i].toUpperCase(), AdminCode.getCodeName("@@", rs.getString("dbname")));//人员库单独处理
							map.put(str[i].toUpperCase()+"1", rs.getString("dbname"));
						}else{
							FieldItem item=DataDictionary.getFieldItem(str[i]);
							if(item != null){
								if("N".equals(item.getItemtype()))
									map.put(str[i].toUpperCase(), PubFunc.round(rs.getString(str[i].toUpperCase()), item.getDecimalwidth()));
								else if("D".equals(item.getItemtype()))
									map.put(str[i].toUpperCase(), PubFunc.DateStringChangeValue(rs.getDate(str[i].toUpperCase())==null ? null : rs.getDate(str[i].toUpperCase()).toString()));
								else if("A".equalsIgnoreCase(item.getItemtype()) && "UM".equalsIgnoreCase(item.getCodesetid())){
									map.put(str[i].toUpperCase(), changeCompareBo.getDepartName(rs.getString(str[i].toUpperCase())));
								}else
									map.put(str[i].toUpperCase(), item.getCodesetid()!=null&&!"0".equals(item.getCodesetid())?AdminCode.getCodeName(item.getCodesetid(), rs.getString(str[i].toUpperCase())):rs.getString(str[i].toUpperCase()));
							}else{
								map.put(str[i].toUpperCase(), rs.getString(str[i].toUpperCase()));
							}
						}
					}
					map.put("TABLETYPE", rs.getString("tabletype".toUpperCase()));
					list.add(map);
				}
				int totalCount = 0;//总条数
				this.frowset = dao.search("select count(*) as ncount from "+tableName);
				if(this.frowset.next()){
					totalCount = this.frowset.getInt("ncount");
				}
				this.getFormHM().put("data", list);
				this.getFormHM().put("totalCount", totalCount);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}