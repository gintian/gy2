/**
 * 
 */
package com.hjsj.hrms.transaction.general.sys;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author lenovo
 *<?xml version="1.0" encoding="GB2312"?>
  <rpc>
        <function_id>000000001</function_id>#后台交易功能号
        <type>0|1|2</type>#0: 执行语句 insert|delete|create, 1: select 语句, 2:带?参数的执行语句, 
                          #3: 同步UserView资源权限, 4:获取签章图片
        <sqls>
           <sql paramcount="3" paramtype="A`A`A" > #A:字符(值为Base64编码),N:数值,D:日期,M:大文本(值为Base64编码),L:二进制(值为Base64编码)
             sql语句
             <params>  #参数,可以有多组
               <p1></p1>
               <p2></p2>
               <p3></p3>
             </params>
           </sql>#语句列表,可以有多个SQL语句
           <sql>
             sql语句
             <isadd>1|0</isadd><restype></restype><resid></resid>#增加/删除UserView资源权限  
           </sql>
        </sqls>
  </rpc>
 */
public class XmlHttpExecutorTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap map=this.getFormHM();
		String type=(String)map.get("type");
		if(type==null|| "".equals(type))
			throw new GeneralException(ResourceFactory.getProperty(""));
		if("0".equals(type)){
			Connection conn = null;
				try{
					conn = AdminDb.getConnection();
					conn.setAutoCommit(false);
					ContentDAO dao=new ContentDAO(conn);
					ArrayList list=(ArrayList)map.get("sql");
					if(list==null||list.size()==0)
						throw new GeneralException(ResourceFactory.getProperty("error.hessian.sql"));
					list=PubFunc.getDecodeSQL(list);
					int[] updates=dao.batchUpdate(list);
					String tmp=Arrays.toString(updates);
					//String tmp=StringUtils.join(updates, ',');
					this.getFormHM().clear();				
					this.getFormHM().put("updates",tmp);
					try {
						conn.commit();
					} catch (SQLException e) {
						if(cat.isDebugEnabled())
							e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
				}catch(Exception ex){
					try {
						conn.rollback();
					} catch (SQLException e) {
						if(cat.isDebugEnabled())
							e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					if(cat.isDebugEnabled())
						ex.printStackTrace();
					throw GeneralExceptionHandler.Handle(ex);
				}finally{
					if(conn!=null)
						try {
							conn.setAutoCommit(true);
							conn.close();
						} catch (SQLException e) {
							if(cat.isDebugEnabled())
								e.printStackTrace();
						}
				}
		}else if("2".equals(type)){
				ArrayList list=(ArrayList)map.get("sql");
				HashMap paramHm=(HashMap)map.get("sqlparam");	
				ArrayList rmsglist = new ArrayList();//存储每条记录是操作成功的返回信息
				Connection conn = null;
				PreparedStatement psmt = null;
				DbSecurityImpl dbS = new DbSecurityImpl();
				try{
					conn = AdminDb.getConnection();
					conn.setAutoCommit(false);
					for(int i=0;i<list.size();i++)
					{
						String sql = (String)list.get(i);
						HashMap sqlHm=(HashMap)paramHm.get(String.valueOf(i));
						if(sqlHm!=null){//有参数的sql语句
							//String paramcount=(String)sqlHm.get("paramcount");
							String paramtype=(String)sqlHm.get("paramtype");
							String[] types=paramtype.split("`");
							ArrayList listvalues=(ArrayList)sqlHm.get("values"); //存取的对象也是ArrayList
							
							psmt = conn.prepareStatement(sql);
							dbS.open(this.frameconn, sql);
							for(int n=0;n<listvalues.size();n++){
								ArrayList values = (ArrayList)listvalues.get(n);
								for(int m=0;m<values.size();m++){
									String value = (String)values.get(m);
									//this.writeDate(value);
									if("A".equalsIgnoreCase(types[m])){

										/**
										 * websphere 64位编码串如果有+时,从前台传到后台时，转换后变成空格呢
										 * 再重新转回去. cmq changed at 20130204
										 */
										
										value=value.replaceAll(" ", "+");
										byte[] sourcearr = Base64.decodeBase64(value);
										value=new String(sourcearr,"GBK");
										psmt.setString(m+1, value);
									}else if("N".equalsIgnoreCase(types[m])){
										psmt.setObject(m+1, value);
									}else if("D".equalsIgnoreCase(types[m])){
										String format = "yyyy-MM-dd";
										if(value.length()>10)
											format = "yyyy-MM-dd HH:mm:ss";
										SimpleDateFormat sdf = new SimpleDateFormat(format);
										Date date = new Date(sdf.parse(value).getTime());
										psmt.setObject(m+1, date);
									}else if("M".equalsIgnoreCase(types[m])){

										value=value.replaceAll(" ", "+");
										byte[] sourcearr =Base64.decodeBase64(value);
										value=new String(sourcearr,"GBK");
										 /*if (value.length() < 2000)
											 value = StringUtils.rightPad(value, 2001, ' ');*/
										//Oracle9i的驱动下，setString 有2000字符长度的限制，将以下两行改完下方一行
								         //Reader clobReader = new StringReader(value);
								         //psmt.setCharacterStream(m + 1, clobReader, value.length());
										psmt.setObject(m + 1, value);
									}else if("L".equalsIgnoreCase(types[m])){

										value=value.replaceAll(" ", "+");
										byte[] sourcearr =Base64.decodeBase64(value);
										psmt.setBytes(m+1, sourcearr);
									}
									
								}
								psmt.addBatch();
							}
							int r[]=psmt.executeBatch();
							for(int index=0;index<r.length;index++){
								rmsglist.add(String.valueOf(r[index]));
							}
							
						}else{//无参数的sql语句
							psmt = conn.prepareStatement(sql);
							rmsglist.add(String.valueOf(psmt.executeUpdate()));
						}
						if(psmt!=null){
							try {
								psmt.close();// 避免Oracle报错：超出打开游标的最大数
							} catch (SQLException e) {
								if(cat.isDebugEnabled())
									e.printStackTrace();
								throw GeneralExceptionHandler.Handle(e);
							}
						}
					}
					conn.commit();
				}catch(Exception ex){
					try {
						conn.rollback();
					} catch (SQLException e) {
						if(cat.isDebugEnabled())
							e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					if(cat.isDebugEnabled())
						ex.printStackTrace();
					throw GeneralExceptionHandler.Handle(ex);
				}finally{
					try {
						dbS.close(this.frameconn);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if(psmt!=null)
						try {
							psmt.close();
						} catch (SQLException e) {
							if(cat.isDebugEnabled())
								e.printStackTrace();
						}
					if(conn!=null)
						try {
							conn.setAutoCommit(true);
							conn.close();
						} catch (SQLException e) {
							if(cat.isDebugEnabled())
								e.printStackTrace();
						}
					String tmp=Arrays.toString(rmsglist.toArray());
					this.getFormHM().clear();				
					this.getFormHM().put("updates",tmp);
				}
		}else if("3".equals(type)){  // 同步用户资源权限
            try{
                ArrayList reslist=(ArrayList)map.get("sql");
                if(reslist != null) {
                    reslist=PubFunc.getDecodeSQL(reslist);
                    for(int i=0;i<reslist.size();i++)
                    {
                        String res=(String)reslist.get(i);
                        if((res != null) && (res.length() > 0)) {
                            boolean isadd = "1".equals(getXmlValue(res, "isadd"));  // 增加或删除
                            int restype = Integer.parseInt(getXmlValue(res, "restype"));
                            String resid = getXmlValue(res, "resid");
                            if(resid.length()>0&&restype!=-1) {
                                if(isadd){  
                                    if(!userView.isHaveResource(restype,resid))
                                    {
                                        userView.addResourceMx(resid, restype);
                                    }
                                }
                                else {  // 删除
                                    
                                }
                            }
                        }
                    }
                }
            }catch(Exception e){
            	if(cat.isDebugEnabled())
            		e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }

		} else if("4".equals(type)){  //zxj 20190824 获取签章图片
            try{
                ArrayList params = (ArrayList)map.get("sql");
                if(params == null)
                    return;
                
                params = PubFunc.getDecodeSQL(params);
                
                for(int i=0;i<params.size();i++)
                {
                    String param = (String)params.get(i);
                    if((param == null) || (param.length() == 0))
                        continue;

                    String signatureId = getXmlValue(param, "signatureid");
                    if(StringUtils.isEmpty(signatureId))
                        continue;
                    
                    String userName = getXmlValue(param, "userName");
                    
                    getSignatureImage(userName, signatureId);
                }
            }catch(Exception e){
                if(cat.isDebugEnabled())
                    e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
		}else  //select 查询语句,返回记录集.
			try{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				ArrayList sqllist=(ArrayList)map.get("sql");				
				if(sqllist==null||sqllist.size()==0)
					throw new GeneralException(ResourceFactory.getProperty("error.hessian.sql"));	
				this.getFormHM().clear();
				sqllist=PubFunc.getDecodeSQL(sqllist);
				StringBuffer txt=new StringBuffer();
				
				for(int i=0;i<sqllist.size();i++)
				{
					String sql=(String)sqllist.get(i);
					RowSet rowset=dao.search(sql);
					txt.append(PubFunc.combineXml(rowset, true));
				}
				this.getFormHM().clear();
				this.getFormHM().put("rowset",txt.toString());
			}catch(Exception e){
				if(cat.isDebugEnabled())
					e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 取xml中节点值
	 * @param xml
	 * @param node
	 * @return
	 */
	private String getXmlValue(String xml, String node) {
	    String s=null;
        if(xml.indexOf("<"+node+">")!=-1&&xml.indexOf("</"+node+">")!=-1){
            s = xml.substring(xml.indexOf("<"+node+">")+("<"+node+">").length(),xml.indexOf("</"+node+">"));
        }
        if(s==null)
            s="";
	    return s;
	}

    /**
     * 获取签章图片并编码为base64返回给插件
     * @param userName 签章用户名
     * @param signatureId 签章id
     */
    private void getSignatureImage(String userName, String signatureId) {
        String base64Img = "";
        String imgType = "";        
        
        StringBuffer sql = new StringBuffer();
        sql.append("select ext_param from signature");
        sql.append(" WHERE username=?");
        
        ArrayList params = new ArrayList();
        params.add(userName);
        
        String xml = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql.toString(), params);
            if(this.frowset.next()) {
                xml = this.frowset.getString("ext_param");
                
                Document doc = PubFunc.generateDom(xml);
                
                Element root = doc.getRootElement(); 
                List childlist = root.getChildren();
                for(int i = 0; i<childlist.size(); i++) {
                    Element el = (Element) childlist.get(i);
                    if(signatureId.equalsIgnoreCase((el.getAttributeValue("MarkID")))) {
                        imgType = el.getAttributeValue("MarkType");
                        base64Img = el.getAttributeValue("MarkData");
                        break;
                    }
                }
 
                this.getFormHM().clear();               
                this.getFormHM().put("img", base64Img);
                this.getFormHM().put("imgtype", imgType);
            }
        } catch (Exception e) {
            if (cat.isDebugEnabled())
                e.printStackTrace();
        }
    }
}
