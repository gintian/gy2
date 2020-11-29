package com.hjsj.hrms.businessobject.sys.fieldsubset;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:指标集维护</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 18, 2008:5:10:01 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SubSetBo {
	private Connection conn = null;

	public SubSetBo(Connection a_con) {
		this.conn = a_con;
	}

	public ArrayList getsubsetList(String infor) {
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		String retname="";
		String retspre="";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select classname,classpre from informationclass where classpre= '"+infor+"'";
			RowSet rowSet = dao.search(sql.toString());
			while(rowSet.next()){
				da = new CommonData();
				retname=rowSet.getString("classname");
				retspre = rowSet.getString("classpre");
				da.setDataName(retname);
				da.setDataValue(retspre);
				list.add(da);
			}
//			if (infor.equals("A")) {
//				da = new CommonData();
//				da.setDataName(ResourceFactory.getProperty("kjg.title.personnel"));
//				da.setDataValue("A");
//				list.add(da);
//			}
//			if (infor.equals("B")) {
//				da = new CommonData();
//				da.setDataName(ResourceFactory.getProperty("kjg.title.unit"));
//				da.setDataValue("B");
//				list.add(da);
//			}
//			if (infor.equals("K")) {
//				da = new CommonData();
//				da.setDataName(ResourceFactory.getProperty("kjg.title.job"));
//				da.setDataValue("K");
//				list.add(da);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}
	/*
	 * 子集最大的值算法
	 * 添加dev_flag  jingq upd 
	 * 2015.01.27
	 * 
	 * guodd update 2015-08-05 算法改变
	 */
	public String getcodevalue(ArrayList subsetList,String dev_flag) {
		String ret = "";
		String strResult = "";
        RowSet rs = null;
		try {
			CommonData da = new CommonData();
			ContentDAO dao = new ContentDAO(this.conn);
			da = (CommonData) subsetList.get(0);
			String codeV = da.getDataValue();
			ArrayList list = new ArrayList();
			if (codeV != null && !"".equals(codeV)) {
	//代码生成规则改变，注掉此处 guodd 2015-08-05		
//				RowSet rowSet = dao
//						.search("SELECT MAX(fieldSetId) AS AAA FROM fieldset WHERE LEFT(fieldSetId,1) = '"+ codeV + "' ");
//				RowSet rowSet = dao
//						.search("SELECT MAX(fieldSetId) AS AAA FROM fieldset WHERE "+Sql_switcher.left("fieldSetId", 1)+" = '"+ codeV + "' ");
//				RowSet rowSet = dao
//						.search("select fieldSetId,(select MAX(fieldSetId) from fieldSet where fieldSetId like '"+codeV+"%') as AAA from fieldSet where fieldSetId like '"+codeV+"%'");
//				while(rowSet.next()){
//					list.add(rowSet.getString(1));
//					ret = rowSet.getString(2);
//				}
				
				//guodd 2015-08-05 
				//xus 20/4/14 达梦数据库处理
				String sql = "";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字
						sql = "select fieldsetid,(select max(regexp_substr(FIELDSETID, '"+codeV+"[0-9][A-Z]')) from fieldset ) maxvalue " +
								"from (SELECT fieldsetid, regexp_substr(FIELDSETID, '"+codeV+"[0-9][[:alnum:]]') as reg FROM fieldset) sourceData " +
								"where reg is not NULL order by fieldsetid DESC";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z
						sql = "select fieldsetid,(select max(regexp_substr(FIELDSETID, '"+codeV+"[A-W][A-Z]')) from fieldset ) maxvalue " +
								"from (SELECT fieldsetid, regexp_substr(FIELDSETID, '"+codeV+"[A-W][A-Z]') as reg FROM fieldset) sourceData " +
								"where reg is not NULL order by fieldsetid DESC";
					}
				}else{
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字
						sql = "select fieldsetid,(select max(fieldsetid)  from fieldset where  fieldsetid like '"+codeV+"[0-9][A-Z]' ) maxvalue from fieldset where  fieldsetid like '"+codeV+"[0-9]%' order by fieldsetid desc";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z
						sql = "select fieldsetid,(select max(fieldsetid)  from fieldset where  fieldsetid like '"+codeV+"[A-W][A-Z]' ) maxvalue from fieldset where fieldsetid like '"+codeV+"[A-W][A-Z]' order by fieldsetid desc ";
					}
				}
				rs = dao.search(sql);
				while(rs.next()){
					list.add(rs.getString("fieldsetid"));
				    ret = rs.getString("maxvalue");
				}
				if(ret==null && !list.isEmpty()) {
                    ret = list.get(0).toString();
                }
				strResult = toAdd(ret,codeV,dev_flag,list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return strResult;
		
	}

	/*
	 * 验证子集代码是否存在
	 */
	public boolean checkcode(String code){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			String sql = "select fieldSetId from fieldset where fieldSetId = '"+code+"'";
			rs= dao.search(sql);
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return flag;
		
	}
	/*
	 * 验证子集名称是否存在
	 */
	public boolean checkname(String name){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			String sql = "select fieldSetId from fieldset where fieldSetDesc = '"+name+"'";
			rs= dao.search(sql);
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return flag;
		
	}
	/*
	 * displayorder字段顺序
	 */
	public int initial(String cDX){
		String ret="";
		int re = 0;
		String cdx = cDX.substring(0,1);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
		String sql = "SELECT MAX(displayorder) AS AAA FROM fieldset WHERE fieldSetId like '"+cdx+"%'";
		rs= dao.search(sql);
		if (rs.next()) {
            ret = rs.getString(1);
        }
			if(ret==null||"null".equals(ret))
			{
				re=0;
			}else{
				re = Integer.parseInt(ret)+1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return re;
		
	}
	/*
	 * displayid字段顺序,指标顺序;
	 */
	public int initorder(String cDX){
		String ret="";
		int re = 0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
		String sql = "SELECT MAX(displayid) AS AAA FROM fielditem WHERE fieldsetid= '"+cDX+"'";
		rs= dao.search(sql);
		if (rs.next()) {
            ret = rs.getString(1);
        }
		if(ret==null||ret==""){
			ret="1";
			re = Integer.parseInt(ret);
		}else{
		re = Integer.parseInt(ret)+1;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return re;
		
	}
	/**
	 * 插入子集
	 * @param name 子集名称
	 * @param qobj 子集类型（变化类型）
	 * @param cDX  子集fieldsetid
	 * @param cdx  displayorder
	 * @param initid 变化子集时，向fielditem中插入z0、z1时的displayid值
	 * @param multimedia_file_flag 是否支持多媒体
	 * @param explain 子集解释 guodd 2018-04-24
	 */
	public void setmuster(String name,String qobj,String cDX,int cdx,int initid, String multimedia_file_flag,String explain){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("insert into fieldset(fieldsetId,Fieldsetdesc,changeFlag,UseFlag,ModuleFlag,displayorder,Customdesc,multimedia_file_flag,fieldsetmemo)");
			sql.append(" values(?,?,?,?,?,?,?,?,?)");
//			sql.append(cDX);
//			sql.append("','");
//			sql.append(name);
//			sql.append("','");
//			sql.append(qobj);
//			sql.append("','");
//			sql.append(0);
//			sql.append("','");
//			sql.append("11111111111111111111");
//			sql.append("','");
//			sql.append(cdx);
//			sql.append("','");
//			sql.append(name);
//			sql.append("','");
//			sql.append(multimedia_file_flag);
//			sql.append("')");
			
			ArrayList setValues = new ArrayList();
			setValues.add(cDX);
			setValues.add(name);
			setValues.add(qobj);
			setValues.add(0);
			setValues.add("11111111111111111111");
			setValues.add(cdx);
			setValues.add(name);
			setValues.add(multimedia_file_flag);
			setValues.add(explain);
			
			
			if("1".equals(qobj)|| "2".equals(qobj)){
				StringBuffer sql1 = new StringBuffer();
				StringBuffer sql2 = new StringBuffer();
				sql1.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,Decimalwidth,Codesetid,Expression,Itemmemo,Displaywidth,state,Reserveitem,Auditingformula,AuditingInformation)");
				sql1.append(" values('");
				sql1.append(initid);
				sql1.append("','");
				sql1.append(cDX);
				sql1.append("','");
				sql1.append(cDX+"Z0");
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append("11111111111111111111");
				sql1.append("','");
				sql1.append("D");
				sql1.append("','");
				sql1.append("年月标识");
				sql1.append("','");
				sql1.append(10);
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append(" ");
				sql1.append("','");
				sql1.append(14);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append(1);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("')");
				
				sql2.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,Decimalwidth,Codesetid,Expression,Itemmemo,Displaywidth,state,Reserveitem,Auditingformula,AuditingInformation)");
				sql2.append(" values('");
				sql2.append(initid+1);
				sql2.append("','");
				sql2.append(cDX);
				sql2.append("','");
				sql2.append(cDX+"Z1");
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append("11111111111111111111");
				sql2.append("','");
				sql2.append("N");
				sql2.append("','");
				sql2.append("次数");
				sql2.append("','");
				sql2.append(3);
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append(" ");
				sql2.append("','");
				sql2.append(10);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append(1);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("')");
				dao.insert(sql1.toString(), new ArrayList());
				dao.insert(sql2.toString(), new ArrayList());
			}
			dao.insert(sql.toString(), setValues); 
			
			FieldSet fieldset = new FieldSet(cDX);
	        fieldset.setFieldsetid(cDX.toUpperCase());
	        fieldset.setFieldsetdesc(name.replaceAll("\"","“"));
	        fieldset.setCustomdesc(name.replaceAll("\"","“"));
	        fieldset.setUseflag("0");
	        fieldset.setChangeflag(qobj);
	        fieldset.setDisplayorder(cdx);
	        fieldset.setMultimedia_file_flag(multimedia_file_flag);
	        fieldset.setExplain(explain);
			DataDictionary.addFieldSet(cDX.toUpperCase(), fieldset);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DataDictionary.refresh();
		}
	}
	
	/*
	 * 根据dev_flag生成子集代号
	 * jingq upd
	 * 2015.01.27
	 * guodd update 2015-08-05
	 */
	private int index = 0;
	public String toAdd(String str,String codeV,String dev_flag,ArrayList list){
		String newCode = createNewCode(str,codeV,dev_flag);//计算新代码
		if(newCode==null){//如果为null，代表最大代码已经被占用，寻找中间是否有空缺
			newCode = findEmptyCodePostion(codeV,dev_flag,list);
		}
		return newCode;
	}
	
	/**
	 * 根据规则生成新代码
	 * guodd 2015-08-05
	 * @param souceCode
	 * @param codePrefix
	 * @param dev_flag
	 * @return
	 */
	public String createNewCode(String souceCode,String codePrefix,String dev_flag){
		String newCode = null;
		//默认是生成子集代码
		
		//倒数第二位下标值
		int fstIndex = 1;
		//倒数第一位下标值
		int scdIndex = 2;
		//如果前缀是三位说明需要生成的是指标代码
		if(codePrefix.length()==3){
			fstIndex = 3;
			scdIndex = 4;
		}
			
		if (souceCode == null || souceCode == "") {
			if("1".equals(dev_flag)){
				newCode = codePrefix+"01";//开发模式 如果没有最大值，说明没有子集（基本不可能存在这种状况）。从02开始 改为01 开始  wangb 20180428 bug 35905
			}else{
				newCode = codePrefix+"AA";//用户模式 从AA开始
			}
		}else{
			char buf[] = souceCode.toCharArray();
			//开发模式  子集后两位范围为先从00~99开始，然后再0A~9Z
			if("1".equals(dev_flag)){
				if(buf[fstIndex]=='9' && buf[scdIndex]=='9'){//如果最大值是99，下一个是0A
					buf[fstIndex] ='0';
				    buf[scdIndex] = 'A';
				}else if(buf[fstIndex]=='9' && buf[scdIndex]=='Z'){//如果 最大值是9Z；说明已经是范围内最大值了,不能再直接加了
					return null;
				}else if(buf[scdIndex]<'9' || (buf[scdIndex]>='A' && buf[scdIndex]<'Z')){//如果最后一位是0~8 或者a~y，最后一位直接+1
					buf[scdIndex] = (char)(buf[scdIndex]+1);
				}else if(buf[scdIndex]=='Z'){//如果最后一位是Z，倒数第二位+1，倒数第一位从A开始
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = 'A';
				}else if(buf[scdIndex]=='9'){//如果最后一位是9，倒数第二位+1，倒数第一位从0开始
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = '0';
				}
					
					
			}else{//用户模式为前缀+两位字母，倒数第二位不能为x、y、z
				if(buf[fstIndex]=='W' && buf[scdIndex]=='Z'){//倒数第二位为w并且倒数第一位为z说明用户指标使用范围到最大值，不能再直接加了
					return null;
				}else if(buf[scdIndex]<'Z'){ //如果倒数第一位不为Z，第三位直接+1
					buf[scdIndex] = (char)(buf[scdIndex]+1);
				}else{//此种情况是 倒数第一位为Z，将倒数第二位+1，倒数第一位为初始值A
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = 'A';
				}
			}
			newCode = String.valueOf(buf);
		}
		
		return newCode;
	}
	
	
	/**
	 * 根据规则生成业务字典新代码  指标代码规则：子集名+2位 （数字或字母）
	 * wangb 2017-06-07
	 * @param souceCode
	 * @param codePrefix 子集名
	 * @param dev_flag
	 * @return
	 */
	public String createBuCode(String souceCode,String codePrefix,String dev_flag){
		String newCode = null;
		//默认是生成子集代码
		
		//倒数第二位下标值   
		int fstIndex = codePrefix.length();
		//倒数第一位下标值
		int scdIndex = codePrefix.length()+1;
		if (souceCode == null || souceCode == "") {
			if("1".equals(dev_flag)){
				newCode = codePrefix+"02";//开发模式 如果没有最大值，说明没有子集（基本不可能存在这种状况）。从02开始
			}else{
				newCode = codePrefix+"AA";//用户模式 从AA开始
			}
		}else{
//			char buf[] = souceCode.toCharArray();
			char buf[] = souceCode.toUpperCase().toCharArray();//小写全部转成大写的 wangb 20170915 31427
			//开发模式  子集后两位范围为先从00~99开始，然后再0A~9Z
			if("1".equals(dev_flag)){
				if(buf[fstIndex]=='9' && buf[scdIndex]=='9'){//如果最大值是99，下一个是0A
					buf[fstIndex] ='0';
				    buf[scdIndex] = 'A';
				}else if(buf[fstIndex]=='9' && buf[scdIndex]=='Z'){//如果 最大值是9Z；说明已经是范围内最大值了,不能再直接加了
					return null;
				}else if(buf[scdIndex]<'9' || (buf[scdIndex]>='A' && buf[scdIndex]<'Z')){//如果最后一位是0~8 或者a~y，最后一位直接+1
					buf[scdIndex] = (char)(buf[scdIndex]+1);
				}else if(buf[scdIndex]=='Z'){//如果最后一位是Z，倒数第二位+1，倒数第一位从A开始
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = 'A';
				}else if(buf[scdIndex]=='9'){//如果最后一位是9，倒数第二位+1，倒数第一位从0开始
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = '0';
				}
			}else{//用户模式为前缀+两位字母，倒数第二位不能为x、y、z
				if(buf[fstIndex]=='W' && buf[scdIndex]=='Z'){//倒数第二位为w并且倒数第一位为z说明用户指标使用范围到最大值，不能再直接加了
					return null;
				}else if(buf[scdIndex]<'Z'){ //如果倒数第一位不为Z，第三位直接+1
					buf[scdIndex] = (char)(buf[scdIndex]+1);
				}else{//此种情况是 倒数第一位为Z，将倒数第二位+1，倒数第一位为初始值A
					buf[fstIndex] = (char)(buf[fstIndex]+1);
					buf[scdIndex] = 'A';
				}
			}
			newCode = String.valueOf(buf);
		}
		
		return newCode;
	}
	
	/**
	 * 寻找空缺
	 * guodd 2015-08-05
	 * @param codePrefix 前缀
	 * @param dev_flag  
	 * @param list
	 * @return
	 */
	private String findEmptyCodePostion(String codePrefix,String dev_flag,ArrayList list){
		String newCode = "";
		 if ("1".equals(dev_flag)) {
			newCode = codePrefix + "02";
		 }else{
			newCode = codePrefix + "AA";
		 }
		
		 while(list.contains(newCode)){//如果存在，说明被占用，生成新的代码，再次判断
			list.remove(newCode);//去掉已经存在的
			newCode =  createNewCode(newCode,codePrefix,dev_flag);//生成新的代码
			if(newCode==null) {
                break;
            }
		 }
		 
		 return newCode;
	}
	
	
	public static boolean isNumeric(String str){
		   for(int i=str.length();--i>=0;){
		      int chr=str.charAt(i);
		      if(chr<47 || chr>57) {
                  return false;
              }
		   }
		   return true;
		}
	/*
	 * 更新数据 code,qobj,name,custom
	 * 添加更新子集解释指标（explain） guodd 2018-04-24
	 */
	public void getamend(String code,String qobj,String name,String custom, String multimedia_file_flag,String explain){
	
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList value = new ArrayList();
			value.add(name);
			value.add(custom);
			value.add(qobj);
			value.add(multimedia_file_flag);
			value.add(explain);
			value.add(code);
			dao.update("update fieldset set fieldSetDesc=?,customdesc=?,changeFlag=?,multimedia_file_flag=?,fieldsetmemo=? where fieldSetId=?",value);
		
			//更新数据字典
			FieldSet fs = DataDictionary.getFieldSetVo(code.toUpperCase());
			if(fs!=null){
				fs.setCustomdesc(custom);
				fs.setFieldsetdesc(name);
				fs.setChangeflag(qobj);
				fs.setMultimedia_file_flag(multimedia_file_flag);
				fs.setExplain(explain);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*
	 * 更新子集;0=一般 1=按月 2=按年;插入Z0或Z1两个指标
	 */
	public void initid(String code,String qobj,int initid) throws SQLException{
		ContentDAO dao = new ContentDAO(this.conn);
		//A01时 不能删除A01Z0(停发标识)。 guodd 2016-07-09
		if("0".equals(qobj) && !"A01".equalsIgnoreCase(code)){
			StringBuffer buf = new StringBuffer();
			buf.append("delete from fielditem where ");
			buf.append("itemid= '"+code+"Z0' or itemid= '"+code+"Z1'");
			try {
				dao.delete(buf.toString(), new ArrayList());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("1".equals(qobj)|| "2".equals(qobj)){
			StringBuffer sql1 = new StringBuffer();
			StringBuffer sql2 = new StringBuffer();
			RowSet rs = null;
			String srs = null;
			int nState = 0;//1:有Z0    2:有Z1  3:有Z0和Z1
			boolean bRet = true;
			String sql="select itemid from fielditem where itemid='"+code+"Z0' or itemid='"+code+"Z1'";
			rs = dao.search(sql);
			if(rs.next()&&bRet ==true){
				 srs =rs.getString(1);
				 if(srs.equals(code+"Z0")){
					 if(nState == 0) {
                         nState = 1;
                     } else if(nState == 2) {
                         nState = 3;
                     }
				 }else if(srs.equals(code+"Z1")){
					 if(nState == 1) {
                         nState = 3;
                     } else if(nState == 0) {
                         nState = 2;
                     }
				 }
				 while(rs.next()){
					 srs =rs.getString(1);
					 if(srs.equals(code+"Z0")){
						 if(nState == 0) {
                             nState = 1;
                         } else if(nState == 2) {
                             nState = 3;
                         }
					 }else {
						 if(nState == 1) {
                             nState = 3;
                         } else if(nState == 0) {
                             nState = 2;
                         }
					 }
				 }
			}else{
				sql1.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,Decimalwidth,Codesetid,Expression,Itemmemo,Displaywidth,state,Reserveitem,Auditingformula,AuditingInformation)");
				sql1.append(" values('");
				sql1.append(initid);
				sql1.append("','");
				sql1.append(code);
				sql1.append("','");
				sql1.append(code+"Z0");
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append("11111111111111111111");
				sql1.append("','");
				sql1.append("D");
				sql1.append("','");
				sql1.append("年月标识");
				sql1.append("','");
				sql1.append(10);
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append(0);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append(14);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append(1);
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("','");
				sql1.append("NULL");
				sql1.append("')");
				
				sql2.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,Decimalwidth,Codesetid,Expression,Itemmemo,Displaywidth,state,Reserveitem,Auditingformula,AuditingInformation)");
				sql2.append(" values('");
				sql2.append(initid+1);
				sql2.append("','");
				sql2.append(code);
				sql2.append("','");
				sql2.append(code+"Z1");
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append("11111111111111111111");
				sql2.append("','");
				sql2.append("N");
				sql2.append("','");
				sql2.append("次数");
				sql2.append("','");
				sql2.append(3);
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append(0);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append(10);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append(1);
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("','");
				sql2.append("NULL");
				sql2.append("')");
				dao.insert(sql1.toString(), new ArrayList());
				dao.insert(sql2.toString(), new ArrayList());
				DataDictionary.refresh();
			}
		}
	}
	/*
	 * 修改检查子集构库前名字;
	 */
	public boolean checkupfieldname(String code,String fieldname){
		boolean flag=false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "select fieldsetdesc from fieldset where fieldsetdesc = '"+fieldname+ "' and fieldsetid <>'"+code+"'";
			rs= dao.search(sql);
			while(rs.next()){
				flag=true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		
		return flag;
	}
/*
 * 修改检查子集构库后名字
 */
	public boolean checkupcustom(String code,String custom){
		boolean flag = false;
		ContentDAO dao= new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sqls = "select customdesc from fieldset where customdesc= '"+custom+"' and fieldsetid <>'"+code+"'";
			rs = dao.search(sqls);
			while(rs.next()){
				flag = true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return flag;
	}
}
