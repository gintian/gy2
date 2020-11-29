package com.hjsj.hrms.businessobject.gz.voucher;

import com.hjsj.hrms.interfaces.gz.Financial_voucherXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 类名称:VoucherJounalBo
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-8-22 上午09:12:02 
 * 修改时间:xucs
 * 修改时间:2013-8-22 上午09:12:02
 * 修改备注:财务凭证分录业务类
 * @version
 *
 */
public class VoucherJounalBo {
	
	private ContentDAO dao;
	private UserView userView;
	public  VoucherJounalBo(Connection conn){
		dao=new ContentDAO(conn);
	}
	public  VoucherJounalBo(Connection conn,UserView uv){
		dao=new ContentDAO(conn);
		this.userView = uv;
	}
	/**
	 * 
	
	* @Title: getPIList
	
	* @Description:获取凭证的id号pn_id和凭证的类别interface_type 
	
	* @param conn
	* @param interface_type
	*                   // interface_type 凭证的类别 
	* @param pn_id 凭证id
	* 				   //  pn_id 凭证id
	* @param PIList 
	* 				  // PIList 用于存放 pn_id 和 interface_type
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getPIList() throws GeneralException{
		String sql="select * From gz_warrant order by pn_id";
		String pn_id="";
		String interface_type="";
		ArrayList PIList = new ArrayList();
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			while(rs.next()){
				pn_id=rs.getString("pn_id");
          	  String privflag = Financial_voucherXml.IsHavePriv(this.userView,rs.getString("b0110"));//1：没关系 2：包含（上级） 3：下级
        	  if("1".equals(privflag)){
        		  continue;
        	  }else{
        		  break;
        	  }
			}
			if("".equals(pn_id)||pn_id==null){
				return PIList;
			}
			PIList.add(pn_id);
			sql="select interface_type from gz_warrant where pn_id=' "+pn_id+"'";
			rs=dao.search(sql);
			while(rs.next()){
				interface_type=rs.getString(1);
				PIList.add(interface_type);
				return PIList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeResource(rs);
		}
		return PIList;
	}
	/**
	 * 
	
	* @Title: getListValue
	
	* @Description: 获取字符串list所对应的字符串内容
	
	* @param list 需要转换成字符串的list
	* @param Value list所对应的字符串   
	
	* @return String    
	
	* @throws
	 */
	public String getListValue(ArrayList list){
		if(list==null||list.size()==0){
			return "";
		}
		String Value="";
		for(int i=0;i<list.size();i++){
			String name=((String) list.get(i)).toLowerCase();
			name=name+",";
			Value=Value+name;
		}
		Value=Value.substring(0, Value.length()-1);
		return Value;
	}
	/**
	 * 
	
	* @Title: getList
	
	* @Description: 将字符串转换成list
	
	* @param Value
	*             // Value 字符串的内容
	* @param list 
	*            // list 字符串所等一应的list 
	
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getList(String Value){
		String[] Array=null;
		ArrayList list = new ArrayList();
		if(!("".equals(Value)||Value==null)){
			Array=Value.split(",");
			for(int i=0;i<Array.length;i++){
			    if(Array[i]==null||"".equals(Array[i])||"null".equals(Array[i])){
			        continue;
			    }
				if(list.contains(Array[i].toLowerCase())){
					continue;
				}
				list.add(Array[i].toLowerCase());
			}
		}
		return list;
	}
	/**
	 * 
	
	* @Title: getNone_field
	
	* @Description: 获得界面上不能显示的列
	
	* @param none_field 
	*                  // none_field:存放所有界面上不能显示的列 
	
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getNone_field(){
		ArrayList none_field = new ArrayList();
		none_field.add("pz_id".toUpperCase());
		none_field.add("voucher_date".toUpperCase());
		none_field.add("dbill_times".toUpperCase());
		none_field.add("dbill_date".toUpperCase());
		none_field.add("deptcode".toUpperCase());
		none_field.add("money".toUpperCase());
		none_field.add("ext_money".toUpperCase());
		none_field.add("pn_id".toUpperCase());
		none_field.add("fl_id".toUpperCase());
		none_field.add("c_type".toUpperCase());
		none_field.add("flseq".toUpperCase());
		none_field.add("gpseq".toUpperCase());
		return none_field;
	}
	/**
	 * 
	
	* @Title: getList
	
	* @Description: 获得要在界面上显示列的内容
	
	* @param sqlValue
	* 				//sqlValue 要从gz_warrantlist查询的字段内容
	* @param conn
	* 				// conn 数据库的连接
	* @param pn_id
	* 				// pn_id 财务凭证id
	* @param groupArray
	* 				// groupArray 分录分组指标中的指标     
	
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getList(String sqlValue,String pn_id,String[] groupArray)throws GeneralException{
		ArrayList list = new ArrayList();
		String sql="select "+sqlValue+" from gz_warrantlist where pn_id='"+pn_id+"' order by fl_id";
		RowSet rs = null;
		RowSet rst = null;
		HashMap tempMap =new HashMap();
		String[]tempArray=sqlValue.split(",");
		ArrayList tempList =new ArrayList();
		try {
			rs=dao.search(sql);
			int iList=0;
			while(rs.next()){
				HashMap listMap = new HashMap();
				for(int i=0;i<tempArray.length;i++){
					if("c_subject".equals(tempArray[i].toLowerCase())){
						String ccode=rs.getString(tempArray[i]);
						if(!("".equals(ccode)||ccode==null)){
							String codename=this.getCsubjectDesc(ccode);
							listMap.put(tempArray[i], codename);
							continue;
						}else{
							listMap.put(tempArray[i], null);
							continue;
						}
					}
					if("n_loan".equals(tempArray[i].toLowerCase())){
						String n_loan=rs.getString(tempArray[i]);
						if(!("".equals(n_loan)||n_loan==null)){
							
							tempMap.put(new Integer(iList), n_loan);
							listMap.put(tempArray[i], n_loan);
							continue;
						}else{
							listMap.put(tempArray[i], null);
							continue;
						}
					}
					if("seq".equals(tempArray[i].toLowerCase())){
						continue;
					}
					FieldItem item = DataDictionary.getFieldItem(tempArray[i].toLowerCase());//解决代码类在界面上显示的问题
					if(item!=null){
    				    if("A".equalsIgnoreCase(item.getItemtype())){
                            if(!("0".equalsIgnoreCase(item.getCodesetid())||"".equalsIgnoreCase(item.getCodesetid())||item.getCodesetid()==null)){
                                String itemdesc = getDesc(item.getCodesetid(),rs.getString(tempArray[i]));
                                listMap.put(tempArray[i], itemdesc);
                                continue;
                            }
                        }
					}
					listMap.put(tempArray[i], rs.getString(tempArray[i]));
				}
				if(!(groupArray==null||"".equals(groupArray))){
					ArrayList  temp = new ArrayList();
					for(int j=0;j<groupArray.length;j++){
						if(temp.contains(groupArray[j].toLowerCase())){
							continue;
						}
						temp.add(groupArray[j]);
						listMap.put(groupArray[j], "");
					}
				}
				iList++;
				listMap.put("seq", new Integer(rs.getInt("seq")));
				list.add(listMap);
			}
			tempList.add(list);
			tempList.add(tempMap);
			
			return tempList;
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rst);
		}
	}
	/**
	 * 
	
	* @Title: getNloanList
	
	* @Description: 对于借贷方向的处理
	
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getNloanList() throws GeneralException{
		ArrayList nloanList=new ArrayList();
		RowSet rs =null;
		try{
			nloanList.add("");
			nloanList.add(ResourceFactory.getProperty("gz.voucher.in"));
			nloanList.add(ResourceFactory.getProperty("gz.voucher.out"));
			String sql= "select distinct n_loan from GZ_WARRANTLIST ";
			rs=dao.search(sql);
			while(rs.next()){
				if(rs.getString("n_loan")==null||nloanList.contains(rs.getString("n_loan"))){
					continue;
				}
				nloanList.add(rs.getString("n_loan"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeResource(rs);
		}
		return nloanList;
		
	}
	/**
	 * 
	
	* @Title: getGroupList
	
	* @Description: 获取所有凭证分录中已经选择的分录分组指标
	
	* @param pn_id
	* 				// pn_id 凭证号
	* @param conn
	* 				// conn 数据库的连接
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList getGroupList(String pn_id){
		
		String sql="select c_group from gz_warrantlist where pn_id='"+pn_id+"'";
		String groupValue="";
		String[] groupArray=null;
		ArrayList groupList = new ArrayList();
		try {
			RowSet rs=dao.search(sql);
			while(rs.next()){				
				groupValue=rs.getString("c_group");
				if(!("".equals(groupValue)||groupValue==null)){
					groupArray=groupValue.split(",");
					for(int i=0;i<groupArray.length;i++){
						if(groupList.contains(groupArray[i])){
							continue;
						}
						groupList.add(groupArray[i].toLowerCase());
					}
				}
				else{
					continue;
				}
			}
			if(rs!=null){
				rs.close();
			}
			return groupList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	
	* @Title: getSalaryIdArray
	
	* @Description: 获取凭证所包含薪资类别的id
	
	* @param pn_id
	* 				// pn_id 凭证id
	* @param conn
	* 				// 数据库的连接
	* @return String[]    
	
	* @throws
	 */
	public String[] getSalaryIdArray(String pn_id){
		String sql="select c_scope from gz_warrant where pn_id='"+pn_id+"'";
		String salaryidValue="";
		String[] salaryidArray=null;
		try {
			RowSet rs=dao.search(sql);
			while(rs.next()){
				salaryidValue=rs.getString("c_scope");
			}
			if(rs!=null){
				rs.close();
			}
			if(!("".equals(salaryidValue)||salaryidValue==null)){
				salaryidArray=salaryidValue.split(",");
			}
			return salaryidArray;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}			
		return null;
	}
	/**
	 * 
	
	* @Title: getXmlArrayList
	
	* @Description: 获取要在界面上显示的字段
	
	* @param xmlArray
	* 				//保存在gz_wrrant 中的content 中的xml的内容
	* @param tgroup
	* 				// 分录分组指标中已经选择的指标
	* @return ArrayList 
	
	* @throws
	 */
	public ArrayList getXmlArrayList(String[] xmlArray,String tgroup){
		ArrayList xmlArrayList = new ArrayList();
		ArrayList tgroupList = new ArrayList();
		for(int i=0;i<xmlArray.length;i++){
			if(xmlArrayList.contains(xmlArray[i].toLowerCase())){
				continue;
			}
			FieldItem item = DataDictionary.getFieldItem(xmlArray[i].toLowerCase());
			if(!("".equals(item)||item==null)){
				if(!item.isVisible()){
					continue;
				}
			}
			xmlArrayList.add(xmlArray[i].toLowerCase());
		}
		ArrayList tempList = new ArrayList();
		tempList.add("PN_ID");
		tempList.add("PZ_ID");
		tempList.add("VOUCHER_DATE");
		tempList.add("DBILL_DATE");
		tempList.add("DBILL_TIMES");
		tempList.add("DEPTCODE");
		tempList.add("MONEY");
		tempList.add("EXT_MONEY");
		tempList.add("C_TYPE");
		ArrayList ltempList = new ArrayList();
		for(int i=0;i<tempList.size();i++){
			ltempList.add(((String)tempList.get(i)).toLowerCase());
		}
		tempList.addAll(ltempList);
		xmlArrayList.removeAll(tempList);
		if(!("".equals(tgroup)||tgroup==null)){
			String[] tgroupArray=tgroup.split(",");
			for(int i=0;i<tgroupArray.length;i++){
				tgroupList.add(tgroupArray[i]);
			}
		}			
		xmlArrayList.removeAll(tgroupList);
		xmlArrayList.add("seq");
		xmlArrayList=sortXMLList(xmlArrayList);
		return xmlArrayList;
	}
	/**
	 * 
	
	* @Title: sortXMLList
	
	* @Description: 对要显示在界面上的list字段进行排序
	
	* @param XMLList
	* 				//XMLList 要排序的list
	* @return ArrayList    
	
	* @throws
	 */
	public ArrayList sortXMLList(ArrayList XMLList){
		ArrayList tempList = new ArrayList();
		tempList.add("fl_id");
		tempList.add("c_mark");
		tempList.add("c_subject");
		tempList.add("fl_name");
		tempList.add("c_itemsql");
		tempList.add("c_extitemsql");
		tempList.add("c_group");
		tempList.add("c_where");
		tempList.add("n_loan");
		tempList.add("check_item");
		tempList.add("check_item_value");
		ArrayList xmlList = XMLList;
		ArrayList tList = new ArrayList();
		for(int i=0;i<tempList.size();i++){
			String ss = (String) tempList.get(i);
			if(xmlList.contains(ss.toLowerCase())||xmlList.contains(ss.toUpperCase())){
				continue;
			}
			tList.add(tempList.get(i));
		}
		
		xmlList.removeAll(tempList);
		tempList.removeAll(tList);
		tempList.addAll(xmlList);
		return tempList;
	}
	
	public ArrayList maintainJounal(String xmlValue,String[] ValueArray) throws GeneralException{
	    ArrayList maintainList = new ArrayList();//要维护的指标列表
        ArrayList digestList = new ArrayList();//科目列表
        ArrayList voucherList = new ArrayList();//借贷方向列表
        ArrayList codeList = new ArrayList();//代码类所对应的列表
        ArrayList markList = new ArrayList();//摘要所对应的列表
	    try{
	        
	         if(!(xmlValue==null||"".equals(xmlValue))){
	             String[] xmlArray=xmlValue.split(",");
	             for(int i=0;i<xmlArray.length;i++){
	                 LazyDynaBean JounalBean = new LazyDynaBean();
	                 FieldItem item = DataDictionary.getFieldItem(xmlArray[i].toLowerCase());
	                 
	                 if("A".equalsIgnoreCase(item.getItemtype())){/**对字符型数据的处理 其中科目和借贷方向比较特殊 A 中包含了 代码型指标的处理**/
	                     if("c_subject".equalsIgnoreCase(item.getItemid())){
	                         //此处单独处理科目
	                         digestList=getDigestList();
	                         JounalBean.set("filedtype", item.getItemtype());
	                         JounalBean.set("filedid", item.getItemid().toLowerCase());
	                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
	                         JounalBean.set("codesetid", item.getCodesetid());
	                         JounalBean.set("list", digestList);
	                         
	                     }else if("N_LOAN".equalsIgnoreCase(item.getItemid())){
	                         //此处单独处理借贷方向
	                         voucherList=getNloanList();
	                         JounalBean.set("filedtype", item.getItemtype());
	                         JounalBean.set("filedid", item.getItemid().toLowerCase());
	                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
	                         JounalBean.set("codesetid", item.getCodesetid());
	                         JounalBean.set("list", voucherList);
	                     }else if("c_mark".equalsIgnoreCase(item.getItemid())){
	                         //此处单独处理摘要
	                         markList=getCmarkList();
	                         JounalBean.set("filedtype", item.getItemtype());
	                         JounalBean.set("filedid", item.getItemid().toLowerCase());
	                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
	                         JounalBean.set("codesetid", item.getCodesetid());
	                         JounalBean.set("list", markList);
	                     }
	                     else if(!("0".equalsIgnoreCase(item.getCodesetid())||"".equalsIgnoreCase(item.getCodesetid())||item.getCodesetid()==null)){
	                         codeList=getCodeList(item.getCodesetid());
	                         JounalBean.set("filedtype", item.getItemtype());
	                         JounalBean.set("filedid", item.getItemid().toLowerCase());
	                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
	                         JounalBean.set("codesetid", item.getCodesetid());
	                         JounalBean.set("list", codeList);
	                     }else{
	                         JounalBean.set("filedtype", item.getItemtype());
	                         JounalBean.set("filedid", item.getItemid().toLowerCase());
	                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
	                         JounalBean.set("codesetid", item.getCodesetid());
	                     }
	                     if(ValueArray!=null){
	                         if(!("0".equalsIgnoreCase(item.getCodesetid())||"".equalsIgnoreCase(item.getCodesetid())||item.getCodesetid()==null)){
	                             String desc = getDesc(item.getCodesetid(),ValueArray[i]);
	                             JounalBean.set("realvalue", ValueArray[i]);
	                             JounalBean.set("textvalue",desc);
	                         }else if("c_subject".equalsIgnoreCase(item.getItemid())){
	                             String desc = this.getCsubjectDesc(ValueArray[i]);
	                             if("".equals(desc)){
	                                 JounalBean.set("textvalue","");  
	                             }else{
	                                 JounalBean.set("textvalue",ValueArray[i]+":"+desc);    
	                             }
	                             JounalBean.set("realvalue", ValueArray[i]);
	                         }else{
	                             if(ValueArray[i]==null){
	                                 JounalBean.set("textvalue","");
	                             }else {
	                                 JounalBean.set("textvalue",ValueArray[i]);
	                             }
	                             
	                         }
	                     }else{
	                         JounalBean.set("textvalue", ""); 
	                     }
	                 }else{
	                	 /**************xiegh****20170614****之前对于非A类型的指标只是塞了value其他没有做处理********/
	                     JounalBean.set("filedtype", item.getItemtype());
                         JounalBean.set("filedid", item.getItemid().toLowerCase());
                         JounalBean.set("desc", item.getItemdesc().toLowerCase());
                         JounalBean.set("codesetid", item.getCodesetid());
                         /****************************************************************************/
	                     if(ValueArray!=null){
	                         if(ValueArray[i]==null){
                                 JounalBean.set("textvalue","");
                             }else {
                                 JounalBean.set("textvalue",ValueArray[i]);
                             }
                         }else{
                             JounalBean.set("textvalue", ""); 
                         }
	                 }
	                 maintainList.add(JounalBean);
	             }
	         } 
	    }catch(Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	    return maintainList;
	}
	public ArrayList getDigestList() throws GeneralException{
	    RowSet rs = null;
	    try{
	        ArrayList voucherList = new ArrayList();
	        String sql = "select ccode,ccode_name,igrade from gz_code order by ccode";
	        CommonData temp = new CommonData("", "");
	        voucherList.add(temp);
	        ArrayList tempList = new ArrayList();
	        rs=dao.search(sql);
	        while (rs.next()) {
	            boolean contaninFlag = true;
	            int m=0;
	            if("".equals(rs.getString("ccode"))||rs.getString("ccode")==null||rs.getInt("igrade")<1||"null".equals(rs.getString("ccode"))){
	                continue;
	            }
	            if(1<rs.getInt("igrade")){
	                for(int i=0;i<tempList.size();i++){
	                    String ss=(String) tempList.get(i);
	                    if((rs.getString("ccode").contains(ss))){
	                        m=m+1;
	                    }
	                }
	                
	                if((m+1)<(rs.getInt("igrade"))){
	                    contaninFlag=false;
	                }
	            }
	            if(contaninFlag){
	                String ss ="";
	                for(int i=0;i<m;i++){
	                    ss=ss+"　";
	                }
	                temp = new CommonData(rs.getString(1), ss+rs
	                        .getString(1)
	                        + ":"+rs.getString(2));
	                tempList.add(rs.getString("ccode"));
	                voucherList.add(temp);
	            }
	        } 
	        return voucherList;
	    }catch(Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	    finally{
	        if(rs!=null){
	            try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
	        }
	    }
	    
	}
	public ArrayList getCodeList(String codesetid) throws GeneralException{
	    ArrayList codeList = new ArrayList();
	    CommonData temp=new CommonData("","");
	    String sql="select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid order by codeitemid";
	    if("@k".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)){
	        sql="select codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"' and codeitemid=parentid order by codeitemid";
	    }
	    RowSet rs = null;
	    String descstr="　";
	    try{
	        rs=dao.search(sql);
	        while(rs.next()){
	            temp=new CommonData((rs.getObject(1)).toString(),rs.getObject(2).toString());
	            codeList.add(temp);
	            findChild(codesetid,rs.getString(1),codeList,descstr);
	        }
	        return codeList;
	    }catch(Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
	    finally{
	        if(rs!=null){
	            try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
	        }
	    }
	}
	public void findChild(String codesetid,String codeitemid,ArrayList codeList,String descstr) throws GeneralException{
	    descstr=descstr+"　";
	    CommonData temp=new CommonData("","");
        String sql="select codeitemid,codeitemdesc from codeitem where parentid='"+codeitemid+"' and codesetid='"+codesetid+"' and parentid <> codeitemid order by codeitemid";
        if("@k".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)){
            sql="select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='"+codesetid+"' and parentid <> codeitemid order by codeitemid";
        }
        RowSet rs = null;
        try{
            rs=dao.search(sql);
            while(rs.next()){
                temp=new CommonData((rs.getObject(1)).toString(),descstr+rs.getObject(2).toString());
                codeList.add(temp);
                findChild(codesetid,rs.getString(1),codeList,descstr);
                descstr="　";
            }
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public ArrayList getCmarkList() throws GeneralException{
	    ArrayList markList = new ArrayList();
	    String sql="select distinct c_mark from gz_warrantlist";
	    RowSet rs = null;
	    try{
	        rs=dao.search(sql);
	        while (rs.next()) {
                if (rs.getString(1) == null
                        || "".equals(rs.getString(1))) {
                    continue;
                }
                markList.add(rs.getString(1));
            }
	        return markList;
	    }catch(Exception e){
	        e.printStackTrace();
	        GeneralExceptionHandler.Handle(e);
	    }
	   
	    finally{
	        if(rs!=null){
	            try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	        }
	    }
	    return markList;
	}
	
	public String getDesc(String codesetid,String codeitemid) throws GeneralException{
	    String desc="";
	    String sql="select codeitemdesc from codeitem where codeitemid='"+codeitemid+"' and codesetid='"+codesetid+"'";
        if("@k".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)){
            sql="select codeitemdesc from organization where codeitemid='"+codeitemid+"' and codesetid='"+codesetid+"'";
        }
        RowSet rs = null;
        try{
            rs=dao.search(sql);
            while(rs.next()){
                desc=rs.getString(1);
            }
            return desc;
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public String getCsubjectDesc(String ccode){
	    String codename="";
	    RowSet rs = null;
	    if(!("".equals(ccode)||ccode==null)){
            String sql1="select ccode_name from gz_code where Upper(ccode)='"+ccode.toUpperCase()+"'";   
            try {
                rs=dao.search(sql1);
                if(rs.next()){
                    codename=rs.getString(1);
                 }
                return codename;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally{
                if(rs!=null){
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            
	    }
	    return codename;
	   
	}
	public ArrayList getContianList()  throws GeneralException{
	    RowSet rs=null;
	    ArrayList contianList = new ArrayList();//判断要维护的指标itemid  是否在GZ_WARRANTLIST业务字典中
        String sql="select itemid from t_hr_BusiField where fieldsetid='GZ_WARRANTLIST'";
        try{
            rs = dao.search(sql);
            while(rs.next()){
                contianList.add(rs.getString(1).toLowerCase());
            }  
        }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
        finally{
           PubFunc.closeResource(rs);
        }
        return contianList;
	    
	}
}
