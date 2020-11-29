/**
 * 大字段类型 LiWeichao 2011-10-25 17:08:50
 * 多个主键 用,分割(主键须于值顺序对应)
 * types 主键类型<多个,号分割 对应主键> N=数字  A字符  (暂不考虑其他类型) 默认N
 * 返回字段为已加密
 */
package com.hjsj.hrms.transaction.train.trainexam;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ReadMemoHintTrans extends IBusiness {


	public void execute() throws GeneralException {
		String table = (String)this.getFormHM().get("table");//表名
		String column = (String)this.getFormHM().get("column");//查询字段
		String keys = (String) this.getFormHM().get("keys");//主键
		String values = (String)this.getFormHM().get("values");//条件
		values = values==null?"":values;
		values = PubFunc.decrypt(SafeCode.decode(values));
		String types = (String)this.getFormHM().get("types");//主键类型
		types = types==null?"":types;
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer("select ");
		sql.append(column);
		sql.append(" from ");
		sql.append(table);
		sql.append(" where ");
		String tmpkey[] = keys.split(",");
		String tmpvalue[] = values.split(",");
		String tmptype[] = types.split(",");
		for(int i=0; i<tmpkey.length; i++){
			if(i>0)
				sql.append(" and ");
			sql.append(tmpkey[i]);
			if("A".equalsIgnoreCase(tmptype[i]))
				sql.append("='"+tmpvalue[i]+"'");
			else
				sql.append("="+tmpvalue[i]);
		}
		//System.out.println(sql.toString());
		
		String content = "";
		try {
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next())
				content = this.frowset.getString(column);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content = content!=null ? content.replaceAll("\r\n", "<br/>"):content;
		this.getFormHM().put("content", SafeCode.encode(content));
	}
}

