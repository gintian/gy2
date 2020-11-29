package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 数据联动
* 
* 类名称：SearchCodeTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Mar 26, 2014 11:46:30 AM   
* 修改人：zhaoxg   
* 修改时间：Mar 26, 2014 11:46:30 AM   
* 修改备注：   
* @version    
*
 */
public class SearchCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		TempvarBo tempvarbo = new TempvarBo();
		
		String itemid = (String)hm.get("itemid");
		itemid = itemid!=null&&itemid.length()>0?itemid:"";
		
		
		StringBuffer str_value = new StringBuffer();
		ArrayList list = new ArrayList();				
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		String codesetid ="";
		if(fielditem==null){
		    fielditem=tempvarbo.getMidVariableList(this.frameconn,itemid,"0");			
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()||codesetid.trim().length()>0){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("UM".equalsIgnoreCase(codesetid)){
							sqlstr.append("select * from organization where codesetid='UM' or (codesetid='UN' and codeitemid in (select parentid from organization where codesetid='UM'))");
							sqlstr.append(" order by a0000");
						}
						else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
						}
						ContentDAO dao = new ContentDAO(this.frameconn);

						RowSet rs = null;
						rs = dao.search(sqlstr.toString());
						int x = 0;
						String y = "";
						String z = "";
						int t = 1;
						int num=0;
						while (rs.next()) {
							if(x==0){
								x=rs.getString("codeitemid").length();
								z=rs.getString("codeitemdesc");
								y=rs.getString("codeitemid");
								num=x;
								CommonData dataobj = new CommonData(rs.getString("codeitemid"),z);
								str_value.append("`"+y+"~"+z+"");
								list.add(dataobj);
							}else if(rs.getString("codeitemid").length()==x){//为了按层级显示部门，zhaoxg 2013-5-9
								x=rs.getString("codeitemid").length();
								z=rs.getString("codeitemdesc");
								for(int k=1;k<=x-num;k++){
									z = "  "+z;
								}
	
								y=rs.getString("codeitemid");
								str_value.append("`"+y+"~"+z+"");
								CommonData dataobj = new CommonData(rs.getString("codeitemid"),z);
								list.add(dataobj);
							}else if(rs.getString("codeitemid").length()>x&&rs.getString("codeitemid").substring(0, x).equals(y)){
								x=rs.getString("codeitemid").length();
								z=rs.getString("codeitemdesc");
								for(int k=1;k<=x-num;k++){
									z = "  "+z;
								}
					
								y=rs.getString("codeitemid");
								str_value.append("`"+y+"~"+z+"");
								CommonData dataobj = new CommonData(rs.getString("codeitemid"),z);
								list.add(dataobj);
								t++;
							}else{
								x=rs.getString("codeitemid").length();
								z=rs.getString("codeitemdesc");
								
								for(int k=1;k<=x-num;k++){
									z = "  "+z;
								}
								y=rs.getString("codeitemid");
								str_value.append("`"+y+"~"+z+"");
								CommonData dataobj = new CommonData(rs.getString("codeitemid"),z);
								list.add(dataobj);

							}
						}						
					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
				if("escope".equals(itemid)){
					dataobj = new CommonData("1","1"+":"+"离休人员");
					list.add(dataobj);
					dataobj = new CommonData("2","2"+":"+"退休人员");
					list.add(dataobj);
					dataobj = new CommonData("3","3"+":"+"内退人员");
					list.add(dataobj);
					dataobj = new CommonData("4","4"+":"+"遗嘱");
					list.add(dataobj);
				}
			}
			hm.put("codelist",list);
			hm.put("nulllist",new ArrayList());
			if(str_value.length()>0&&str_value.length()<10000){
				hm.put("str_value", SafeCode.encode(str_value.substring(1)));			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
