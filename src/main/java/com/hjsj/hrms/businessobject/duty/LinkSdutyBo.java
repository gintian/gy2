package com.hjsj.hrms.businessobject.duty;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.transaction.dutyinfo.SaveDutyInfoTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class LinkSdutyBo {
	
   Document doc;
   String dutyid;  // h0100
   String dutydesc;  //源岗位名称
   String orgid;  //b0110
   String linkdutyitemid;//岗位中存岗位体系的指标，PS_C_JOB 参数中获取
   Connection conn;
   String linktag="1";// 1:不存在，直接插入。2：update原纪录
   String code;
   String sqltag; //覆盖（over）还是追加
   int i9999=1;


   int grade;
   ResultSet rs = null;
   Statement sta = null;
//   public LinkSdutyBo(String dutyid,String orgid,Connection conn){
//	   this.dutyid = dutyid;
//	   this.orgid = orgid;
//	   this.conn = conn;
//	   
//   }
   
   public LinkSdutyBo(String xml,String linkdutyitemid,String sqltag,Connection conn){
	    createDOC(xml);
	    this.linkdutyitemid = linkdutyitemid;
	    this.sqltag = sqltag;
	    this.conn = conn;
   }
   
  

private void createDOC(String xml){
		try {
			if("#".equals(xml)) {
                this.doc = new Document();
            } else {
                this.doc = PubFunc.generateDom(xml);
            }
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
   
public String checkDOC(){
	try{
		
		String xpath = "/params/rec[@source = 'H01']";
		XPath reportPath = XPath.newInstance(xpath);
		List childlist = reportPath.selectNodes(doc);
		
		if(childlist.size()<1){
			return "0";
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	return "1";
}
	
	/**
	 * 用于覆盖原有引入基准岗位获取code wusy
	 * @param dutyid
	 * @param dutydesc
	 * @param orgid
	 * @return
	 */
	public String getcode(String dutyid,String dutydesc,String orgid){
		this.linktag = "1";
	    this.dutyid = dutyid;
	    this.dutydesc = dutydesc;
	    this.orgid = orgid;
	    this.code = getcode();
	   String num = checklinktag();
	   if("2".equals(this.linktag)){
		   return num;
	   }
	   String sql = "select codeitemid from organization where parentid='"+this.orgid+"' and parentid<>codeitemid order by codeitemid desc";
	   String codeitemid="";
	   try {
		   sta = conn.createStatement(); 
		   rs = sta.executeQuery(sql);
		   while(rs.next()){
			   codeitemid = rs.getString("codeitemid");
			   break;
		   }
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   
	    AddOrgInfo addOrgInfo=new AddOrgInfo();
	    num=addOrgInfo.GetNext(codeitemid,this.orgid);
	    return num;
	}

   public void linksdtuy(String dutyid,String dutydesc,String orgid) throws SQLException, GeneralException{
	   this.linktag = "1";
	    this.dutyid = dutyid;
	    this.dutydesc = dutydesc;
	    this.orgid = orgid;
	    this.code = getcode();
	    //getgrade(this.orgid);
	   Element recE = null;
	   String source = null;
	   String target = null;
	   Element fieldE = null;
	   String[] fields = {};
	   
	ContentDAO dao = new ContentDAO(this.conn);
	ArrayList sqlList = new ArrayList();
	try{
		String xpath = "/params/rec";
		XPath reportPath = XPath.newInstance(xpath);
		List childlist = reportPath.selectNodes(doc);
		
		Iterator ite = childlist.iterator();
		
		if("1".equals(linktag)){
			
			String sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
            "',i9999,title,ole,'K',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and flag='K'";
			
			sqlList.add(sql);
			
			while(ite.hasNext()){
				recE = (Element)ite.next();
				source = recE.getAttributeValue("source");
				target = recE.getAttributeValue("target");
				fieldE = recE.getChild("field");
				fields = fieldE.getText().split(",");
				
				if("H01".equals(source.toUpperCase())) {
					for (String field : fields) {
						String[] linkitems = field.split("=");
						if(StringUtils.isNotBlank(linkdutyitemid)&&linkdutyitemid.equalsIgnoreCase(linkitems[1])){
							throw GeneralExceptionHandler.Handle(new Exception("岗位参数设置中\"所属岗位体系指标\"所设置指标不可设置为基准岗位对应指标！"));
						}
					}

					initMainSet();
				}
				
				
				if("H00".equals(source.toUpperCase())){
					
					for(int i=0;i<fields.length;i++){
						String type[] = fields[i].split("=");
						if(type.length!=2) {
                            continue;
                        }
						 sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
			              "',i9999,title,ole,'"+type[1]+"',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and flag='"+type[0]+"'";
						 sqlList.add(sql);
					}
					
					continue;
				}
				
				if(checkFields(source,target,fields))//检查指标是否存在。预防 库结构改变 却没有改参数情况
                {
                    continue;
                }
				
				sqlList.add(insertSetinfosql(fields,source,target));
			}
			/*数据视图数据同步 规则  基准岗位引入 先 organization表 后 岗位表  数据视图岗位视图数据才同步过去    wang 20190102*/
			new SaveDutyInfoTrans(this.conn).addOrgData(this.orgid, this.code, this.dutydesc, null, this.grade+"", "1");
			dao.batchUpdate(sqlList);
			
		}else{
			 if("over".equals(sqltag)){
				 
				 String sql = "delete k00 where k00.e01a1='"+this.orgid+this.code+"'";
				 sqlList.add(sql);
					
					sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
		              "',i9999,title,ole,'K',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and flag='K'";
					sqlList.add(sql);  
				 
				    while(ite.hasNext()){
						recE = (Element)ite.next();
						source = recE.getAttributeValue("source");
						target = recE.getAttributeValue("target");
						fieldE = recE.getChild("field");
						fields = fieldE.getText().split(",");
						
						
						
						
						if("H00".equals(source.toUpperCase()) ){
							
							for(int i=0;i<fields.length;i++){
								String type[] = fields[i].split("=");
								if(type.length<2) {
                                    continue;
                                }
								sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
					              "',i9999,title,ole,'"+type[1]+"',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and flag='"+type[0]+"'";
								sqlList.add(sql);
							}
							
							continue;
						}
						
						
					    if(checkFields(source,target,fields))//检查指标是否存在。预防 库结构改变 却没有改参数情况
                        {
                            continue;
                        }
					    
						if("01".equals(target.substring(1))){
							String sqls=createMainSetSql(fields,source,target,this.orgid+this.code);
							sqlList.add(sqls);
						}else{
							String sqls = "delete "+target+" where e01a1='"+this.orgid+this.code+"'";
							sqlList.add(sqls);
							sqlList.add(insertSetinfosql(fields,source,target));
						}
					}
					
					dao.batchUpdate(sqlList);
			 }else{
				 String sql = "";
				 HashMap fieldmap = new HashMap();
				   while(ite.hasNext()){
					    recE = (Element)ite.next();
						source = recE.getAttributeValue("source");
						target = recE.getAttributeValue("target");
						fieldE = recE.getChild("field");
						fields = fieldE.getText().split(",");
						
						if("H00".equals(source.toUpperCase()) ){
							
                            for(int i=0;i<fields.length;i++){
                            	String type[] = fields[i].split("=");
                            	if(type.length<2) {
                                    continue;
                                }
								fieldmap.put(type[0], type[1]);
							}
							continue;
						}
						
						if(checkFields(source,target,fields))//检查指标是否存在。预防 库结构改变 却没有改参数情况
                        {
                            continue;
                        }
						
						if("01".equals(target.substring(1))){
						    sql=createMainSetSql(fields,source,target,this.orgid+this.code);
							dao.update(sql);
						}else{
							List volist = insertSetData(fields, source, target);
							dao.addValueObject(volist);
						}
				   }
				   
				   if("H00".equals(source.toUpperCase())){
					   
					    int i9999 = MaxI9999(this.orgid+this.code,"k00");
					    boolean flag=true;
					    sql="select '1' from k00 where e01a1='"+this.orgid+this.code+"' and flag='K'";
					    rs = dao.search(sql);
					    if(rs.next()) {
                            flag=false;
                        }
					    sql = "select i9999,flag from h00 where h0100='"+this.dutyid+"'";
					    rs = dao.search(sql);
					    while(rs.next()){
					    	i9999++;
					    	
					    	if("K".equals(rs.getString("flag")) && flag){
					    		sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
					              "',"+i9999+",title,ole,'K',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and flag='K'";
					    		sqlList.add(sql);
					    	}
					    	if(!"K".equals(rs.getString("flag"))){
						    	sql = "insert into k00(e01a1,i9999,title,ole,flag,ext,createtime,createusername) select '"+this.orgid+this.code+
					             "',"+i9999+",title,ole,'"+fieldmap.get(rs.getString("flag"))+"',ext,createtime,createusername from h00 where h0100='"+this.dutyid+"' and i9999="+rs.getInt("i9999");
						    	sqlList.add(sql);
					    	}
					    }
					    
				   }
				   
				   
				   dao.batchUpdate(sqlList);
			 }
		}
	 }catch (JDOMException e) {
		   e.printStackTrace();
	}finally{
		clearUp();
		//System.gc(); wangb 20170801 13833  请求垃圾回收 耗时长 不用了
	}
   }
   
   private String insertSetinfosql(String[] fields,String source,String target) {
	   
	        StringBuffer sql = new StringBuffer();
	        
	        	sql.append("insert into ");
		    	sql.append(target+"(");
		    	
		    	for(int i=0;i<fields.length;i++){
		    		if(fields[i] == null) {
                        continue;
                    }
		    		String[] linkitems = fields[i].split("=");
		    		sql.append(linkitems[1]);
		    		sql.append(",");
		    	}
		    	
		    	
		    	if("h01".equals(source.toLowerCase())){
		    		sql.append(linkdutyitemid+",");
		    		sql.append("e0122,");
		    	}else{
		    		sql.append("i9999,");
		    	}
		    	
		    	sql.append("e01a1");
		    	sql.append(") select ");
		    	
		    	for(int j=0;j<fields.length;j++){
		    		if(fields[j] == null) {
                        continue;
                    }
		    		String[] linkitems = fields[j].split("=");
		    		sql.append(linkitems[0]);
		    		sql.append(",");
		    	}
		    	
		    	if("h01".equals(source.toLowerCase())){
		    		sql.append("'"+this.dutyid+"',");
		    	    sql.append("'"+this.orgid+"',");
		    	}else{
		    		sql.append("i9999,");
		    	}
		    	
		    	sql.append("'"+this.orgid+this.code+"'");
		    	
		    	sql.append(" from "+source);
		    	sql.append(" where h0100=");
		    	sql.append("'"+this.dutyid+"'");
	        	
		return sql.toString() ;
   }
   
   public String createMainSetSql(String[] fields,String source,String target,String e01a1){
	   StringBuffer sql = new StringBuffer();
	   
	   
	   if(Sql_switcher.searchDbServer() == 1){
		   sql.append("update "+target+" set ");
		   for(int i=0;i<fields.length;i++){
			   if(fields[i] == null) {
                   continue;
               }
	   		String[] linkitems = fields[i].split("=");
	   		sql.append(linkitems[1]+"="+linkitems[0]);
	   		sql.append(",");
	   	   }
		   
		   sql.deleteCharAt(sql.length()-1);
		   sql.append(" from "+source+" where ");
		   sql.append(this.linkdutyitemid+" = h0100 and e01a1='"+e01a1+"'");
	   }else{
		   sql.append("update "+target+" set(");
		   StringBuffer where = new StringBuffer(" (select ");
		   for(int i=0;i<fields.length;i++){
			   if(fields[i] == null) {
                   continue;
               }
	   		String[] linkitems = fields[i].split("=");
	   		sql.append(linkitems[1]+" ,");
	   		where.append(linkitems[0]+" ,");
	   	   }
		   
		   sql.deleteCharAt(sql.length()-1);
		   where.deleteCharAt(where.length()-1);
		   where.append(" from "+source+" where h0100='"+this.dutyid+"')");
		   sql.append(") = "+where);
		   sql.append(" where e01a1='"+e01a1+"'");
	   }
	   
	   return sql.toString();
   }
   
   public List insertSetData(String[] fields,String source,String target) throws SQLException{
	   
	   int i9999 = MaxI9999(this.orgid+this.code, target);
	     
	   List volist = new ArrayList();
	   
	    	String sql = "select * from "+source+" where h0100='"+this.dutyid+"'";
	    	ContentDAO dao = new ContentDAO(conn);
	    	rs = dao.search(sql);
	        while(rs.next()){
	        	i9999++;
	        	RecordVo vo = new RecordVo(target);
				   for(int i=0;i<fields.length;i++){
					   if(fields[i] == null) {
                           continue;
                       }
					   String[] linkitems = fields[i].split("=");
					   vo.setObject(linkitems[1].toLowerCase(), rs.getObject(linkitems[0].toUpperCase()));
				   }

				   vo.setObject("i9999", new Integer(i9999));

				   vo.setObject("e01a1", this.orgid+this.code);
				volist.add(vo);
	        }
	    return volist;
   }
   
   private String getcode(){
	   String num = ""; 
	   if("over".equalsIgnoreCase(this.sqltag)) {
	       num = checklinktag();
	       if("2".equals(this.linktag)){
	           return num;
	       }
	   }        
	   String sql = "select codeitemid from organization where parentid='"+this.orgid+"' and parentid<>codeitemid order by codeitemid desc";
	   String codeitemid="";
	   try {
	       ContentDAO dao = new ContentDAO(conn);
		   rs = dao.search(sql);
		   while(rs.next()){
			   codeitemid = rs.getString("codeitemid");
			   break;
		   }
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   
	    AddOrgInfo addOrgInfo=new AddOrgInfo();
	    num=addOrgInfo.GetNext(codeitemid,this.orgid);
	    return num;
   }
   
   public String checklinktag(){
	   String codeitemid="";
	   try {
	       ContentDAO dao = new ContentDAO(conn);
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   
		   String sql = "select o.codeitemid from organization o inner join k01 k on o.codeitemid=k.e01a1 where o.parentid=? and k."
			            +this.linkdutyitemid+"=? and o.parentid<>o.codeitemid and "+Sql_switcher.dateValue(sdf.format(new Date()))
			            +"  between o.start_date and o.end_date";
		 
		   ArrayList values = new ArrayList();
		   values.add(this.orgid);
		   values.add(this.dutyid);
		   rs = dao.search(sql, values);
		   if(rs.next()){
			   codeitemid = rs.getString("codeitemid");
			   this.linktag="2";
		   }else {
               return "";
           }
       } catch (Exception e) {
		   e.printStackTrace();
       }	
	   return codeitemid.substring(this.orgid.length());
   }
   
   public void getgrade(String orgid) {
	   String sql = "select grade from organization where codeitemid=?";
	   int grade = 0;
	   ContentDAO dao = new ContentDAO(conn);
	   try {
	       ArrayList values = new ArrayList();
		   values.add(orgid);
		   rs = dao.search(sql, values);
		   if(rs.next()) {
               grade = rs.getInt("grade");
           }
 	   } catch (SQLException e) {
           e.printStackTrace();
	   }
   
	   this.grade = grade+1;
  }
   
   public boolean checkFields(String source,String target,String[] fields){
	   FieldSet sourceSet = DataDictionary.getFieldSetVo(source);
	   FieldSet targetSet = DataDictionary.getFieldSetVo(target);
	   if(sourceSet == null || targetSet == null || "0".equals(sourceSet.getUseflag()) || "0".equals(targetSet.getUseflag())) {
           return true;
       } else{
		   for(int i=0;i<fields.length;i++){
			   String[] linkitems = fields[i].split("=");
			   FieldItem sourceItem = DataDictionary.getFieldItem(linkitems[0]);
			   FieldItem targetItem = DataDictionary.getFieldItem(linkitems[1]);
			   if(sourceItem == null || targetItem == null || "0".equals(sourceItem.getUseflag()) || "0".equals(targetItem.getUseflag())) {
                   fields[i]=null;
               }
		   }
	   }
	   return false;
   }
   
   public int MaxI9999(String codeitemid,String table){
	    String sql = "select max(i9999) i9999 from "+table+" where e01a1='"+codeitemid+"'";
	    int i9999=0;
	    try{
	       ContentDAO dao = new ContentDAO(conn);
	       rs = dao.search(sql);
	       if(rs.next()) {
               i9999 = rs.getInt("i9999");
           }
	       
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return i9999;
   }
   
   public void clearUp(){
	   try {
		   if(rs!=null) {
               rs.close();
           }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	   System.gc();
   }
   
   public String getLinktag() {
		return linktag;
	}
   
   public void initMainSet(){
	   ContentDAO dao = new ContentDAO(this.conn);
	   try {
		String sql="select '1' from H01 where h0100='"+this.dutyid+"'";
		this.rs = dao.search(sql);
		if(!rs.next()){
			sql = " insert into H01(h0100) values('"+this.dutyid+"')";
			dao.update(sql);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
   }
}
