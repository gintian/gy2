package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.IAcceptSMS;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-5-20 18:18:45 PM</p>
 * @author duml
 * @version 1.0
 * 
 */
public class ReceiveMessageBo implements IAcceptSMS  {
	Connection conn = null;
	RowSet rs=null;
	Statement st=null;
	public ReceiveMessageBo(){
		
	}
	
	@Override
    public void acceptSMS(LazyDynaBean bean){
		try {
			this.conn=AdminDb.getConnection();
		} catch (GeneralException e1) {
			e1.printStackTrace();
		}
		String nbase="";
		String interviewingRevertItemid="";
		ContentDAO dao=new ContentDAO(this.conn);
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
		HashMap map;
		String param="";
		try {
			RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
			nbase=vo.getString("str_value");
			map = parameterXMLBo.getAttributeValues();
			if(map!=null&&map.get("interviewing_itemid")!=null)
			{
				interviewingRevertItemid=(String)map.get("interviewing_itemid");
				RecordVo sms_vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
		        if(sms_vo==null) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
				}
		        param=sms_vo.getString("str_value");
		        if(param==null|| "".equals(param)) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
				}
			}
			if(interviewingRevertItemid!=null&&interviewingRevertItemid.length()!=0){       
			FieldItem item=DataDictionary .getFieldItem(interviewingRevertItemid, "a01");
			String codesetid="";
			if(item!=null){
				codesetid=item.getCodesetid();
			}
			StringBuffer sql=new StringBuffer();
			sql.append("select * from codeitem where codesetid='");
			sql.append(codesetid);
			sql.append("'");
			HashMap fieldsMap=new HashMap();
			String num="";
			String content="";
			this.rs=dao.search(sql.toString());
				while(rs.next()){
					fieldsMap.put(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
				}
				if(bean!=null){
					num=(String)bean.get("sender");
					content=(String)bean.get("text");
					String code="";
					if(content!=null&&content.length()!=0){
						String tem=content.substring(0,2);
						if("zp".equalsIgnoreCase(tem)){
							code=content.substring(3);
							if(fieldsMap.get(code)!=null){
								sql.setLength(0);
								sql.append("update ");
								sql.append(nbase);
								sql.append("A01 set ");
								sql.append(interviewingRevertItemid);
								sql.append("='");
								sql.append(code);
								sql.append("' where ");
								sql.append(param);
								sql.append("='");
								sql.append(num);
								sql.append("'");
								dao.update(sql.toString());
							}
						}
					}
				}
				}else{
					
				}
			}catch(Exception e){
				
			}finally{
				
					try {
						if(this.rs!=null){
							rs.close();
						}
						if(this.conn!=null){
							this.conn.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
			
	
	}
}
