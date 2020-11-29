package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.drbean.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 一件创建中间库表
 * @author caoqy
 *
 */
public class CreateMidDbTableTrans extends IBusiness {


	@Override
	public void execute() throws GeneralException {
		Connection con = null;
		DRParserXmlBean ParserXmlBean = null;
		try {
			ArrayList<HashMap> tableList = null;// 中间库需创建的表
			ArrayList<HashMap> rowList = null;// 中间库表需创建的列
			//数据库配置
			String operationtype = (String) this.getFormHM().get("type");// 测试数据库连接还是保存方案
			String dbname = (String) this.getFormHM().get("dbname");// 数据库名
			String dbtype = (String) this.getFormHM().get("dbtype");// 数据库类型1oracle,2sqlserver
			String dburl = (String) this.getFormHM().get("dburl");// 数据库链接
			String dbusername = (String) this.getFormHM().get("dbusername");// 数据库用户名
			String password = (String) this.getFormHM().get("password");// 密码
			String port = (String) this.getFormHM().get("port");// 端口号
			String testDbUrl = "";//jdbc驱动连接url
			if ("2".equalsIgnoreCase(dbtype)) {// mssql
				testDbUrl = "jdbc:sqlserver://" + dburl + ":" + port + ";databaseName=" + dbname;
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} else if ("1".equalsIgnoreCase(dbtype)) {
				testDbUrl = "jdbc:oracle:thin:@"+dburl+":"+port+":"+dbname;
				Class.forName("oracle.jdbc.driver.OracleDriver");
			}
			con = DriverManager.getConnection(testDbUrl, dbusername, password);//获取中间库连接
			//获取解析的bean
			String xml = this.getConstantXml();
	        JAXBContext context = JAXBContext.newInstance(DRParserXmlBean.class);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        ParserXmlBean  = (DRParserXmlBean) unmarshaller.unmarshal(new StringReader(xml));
			String flag = this.createMidTable(con,ParserXmlBean);
			this.getFormHM().put("flag", flag);
		}catch (Exception e) {
			this.getFormHM().put("flag", "fail");
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(con);
		}
	}
	/**
	 * 创建中间库表
	 * @param con
	 * @param tableList
	 * @param rowList
	 * @return
	 */
	private String createMidTable(Connection con,DRParserXmlBean ParserXmlBean) {
		String flag = "success";
		DbWizard dbWizard = new DbWizard(con);
		try {
			if (dbWizard.isExistTable("T_EXG_ORG", false)) {
				dbWizard.dropTable("T_EXG_ORG");
			}
			Table table_exg_org = new Table("T_EXG_ORG");
			//  唯一标识码
			Field temp1=new Field("guidkey","唯一标识码");
			temp1.setNullable(false);
			temp1.setKeyable(true);
			temp1.setDatatype(DataType.STRING);
		    temp1.setLength(38);		
		    table_exg_org.addField(temp1);
		    
		    //机构编码
			Field temp2=new Field("orgid","机构编码");
			temp2.setNullable(false);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.STRING);
		    temp2.setLength(50);		
		    table_exg_org.addField(temp2);
		    
			//  机构名称
			Field temp3=new Field("orgdesc","机构名称");
			temp3.setNullable(false);
			temp3.setKeyable(false);
			temp3.setDatatype(DataType.STRING);
		    temp3.setLength(50);		
		    table_exg_org.addField(temp3);
		    
			//  机构标识
			Field temp4=new Field("codesetid","机构标识");
			temp4.setNullable(false);
			temp4.setKeyable(false);
			temp4.setDatatype(DataType.STRING);
		    temp4.setLength(2);//UN单位，UM部门，@K岗位		
		    table_exg_org.addField(temp4);
		    
			//  上级机构编码
			Field temp5=new Field("parentid","上级机构编码");
			temp5.setNullable(false);
			temp5.setKeyable(false);
			temp5.setDatatype(DataType.STRING);
		    temp5.setLength(50);		
		    table_exg_org.addField(temp5);
		    
			//  机构有效日期
			Field temp6=new Field("start_date","机构有效日期");
			temp6.setNullable(true);
			temp6.setKeyable(false);
			temp6.setDatatype(DataType.DATE);
		    table_exg_org.addField(temp6);
		    
			//  机构失效日期
			Field temp7=new Field("end_date","机构失效日期");
			temp7.setNullable(true);
			temp7.setKeyable(false);
			temp7.setDatatype(DataType.DATE);
		    table_exg_org.addField(temp7);
		    
			//  修改日期
			Field temp8=new Field("modtime","修改时间");
			temp8.setNullable(false);
			temp8.setKeyable(false);
			temp8.setDatatype(DataType.DATETIME);
		    table_exg_org.addField(temp8);
		    
			//  删除标志
			Field temp9=new Field("delstate","删除标志");
			temp9.setNullable(true);
			temp9.setKeyable(false);
			temp9.setDatatype(DataType.INT);
		    temp9.setLength(4);		
		    table_exg_org.addField(temp9);
		    
			//  接收状态
			Field temp10=new Field("acceptstate","接收状态");
			temp10.setNullable(true);
			temp10.setKeyable(false);
			temp10.setDatatype(DataType.INT);
		    table_exg_org.addField(temp10);
		    
			//  机构顺序
			Field temp11=new Field("seq","机构顺序");
			temp11.setNullable(true);
			temp11.setKeyable(false);
			temp11.setDatatype(DataType.INT);
		    temp11.setLength(4);		
		    table_exg_org.addField(temp11);
		    dbWizard.createTable(table_exg_org);
			//增加t_photo照片表
			DRParamBean param = ParserXmlBean.getParam();
			String reportPhoto = param.getReportPhoto();
			if ("TRUE".equalsIgnoreCase(reportPhoto)){
				if (dbWizard.isExistTable("T_PHOTO", false)) {
					dbWizard.dropTable("T_PHOTO");
				}
				Table table = new Table("T_PHOTO");
				Field field = new Field("emp_id", "人员主键");
				field.setDatatype(DataType.STRING);
				field.setLength(38);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				field = new Field("photo", "照片");
				field.setDatatype(DataType.BLOB);
				field.setNullable(true);
				table.addField(field);
				field = new Field("ext", "照片后缀");
				field.setDatatype(DataType.STRING);
				field.setLength(10);
				field.setNullable(false);
				table.addField(field);
				field = new Field("modtime", "更新时间");
				field.setDatatype(DataType.DATETIME);
				field.setNullable(false);
				table.addField(field);
				field = new Field("delstate", "删除标志");
				field.setNullable(true);
				field.setKeyable(false);
				field.setDatatype(DataType.INT);
				field.setLength(4);
				table.addField(field);
				field = new Field("acceptstate", "接收状态");
				field.setNullable(true);
				field.setKeyable(false);
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbWizard.createTable(table);
			}
		    DRFieldSetBean set = ParserXmlBean.getFieldSet();
	        List tableList = set.getSetList();
			for (int i = 0; i < tableList.size(); i++) {
				DRSetBean setBean = (DRSetBean) tableList.get(i);
				String name = setBean.getSetid();
				String firstName = name.substring(0,1).toUpperCase();//A B K
				if (dbWizard.isExistTable("T_" + name, false)) {
					dbWizard.dropTable("T_" + name);
				}
				// 重新创建
				Table table = new Table("T_" + name);
				//添加固定指标
				// 唯一标识码
				Field tempguid = new Field("guidkey", "唯一标识码");
				tempguid.setNullable(false);
				tempguid.setKeyable(true);
				tempguid.setDatatype(DataType.STRING);
				tempguid.setLength(38);
				table.addField(tempguid);
				
				// 更新时间
				Field tempmodtime = new Field("modtime", "更新时间");
				tempmodtime.setNullable(false);
				tempmodtime.setKeyable(false);
				tempmodtime.setDatatype(DataType.DATETIME);
				table.addField(tempmodtime);
				
				//  接收状态
				Field tempacceptstate=new Field("acceptstate","接收状态");
				tempacceptstate.setNullable(true);
				tempacceptstate.setKeyable(false);
				tempacceptstate.setDatatype(DataType.INT);
			    tempacceptstate.setLength(4);		
			    table.addField(tempacceptstate);
			    
				// 删除标志 
				Field tempdelstate = new Field("delstate", "删除标志");
				tempdelstate.setNullable(true);
				tempdelstate.setKeyable(false);
				tempdelstate.setDatatype(DataType.INT);
				tempdelstate.setLength(4);
				table.addField(tempdelstate);
				
				if("A01".equalsIgnoreCase(name)){
					// 所在单位
					Field tempb0110 = new Field("b0110", "所在单位");
					tempb0110.setNullable(false);
					tempb0110.setKeyable(false);
					tempb0110.setDatatype(DataType.STRING);
					tempb0110.setLength(30);
					table.addField(tempb0110);
					// 所在部门
					Field tempe0122 = new Field("e0122", "所在部门");
					tempe0122.setNullable(true);
					tempe0122.setKeyable(false);
					tempe0122.setDatatype(DataType.STRING);
					tempe0122.setLength(30);
					table.addField(tempe0122);
					// 所在岗位
					Field tempe01a1 = new Field("e01a1", "所在部门");
					tempe01a1.setNullable(true);
					tempe01a1.setKeyable(false);
					tempe01a1.setDatatype(DataType.STRING);
					tempe01a1.setLength(30);
					table.addField(tempe01a1);
				}else if(!"A01".equalsIgnoreCase(name)&&"A".equalsIgnoreCase(firstName)){
					// 人员主键
					Field tempemp_id = new Field("emp_id", "人员主键");
					tempemp_id.setNullable(false);
					tempemp_id.setKeyable(false);
					tempemp_id.setDatatype(DataType.STRING);
					tempemp_id.setLength(38);
					table.addField(tempemp_id);
					
					// 子集记录顺序号
					Field tempi9999 = new Field("i9999", "子集记录顺序号");
					tempi9999.setNullable(false);
					tempi9999.setKeyable(false);
					tempi9999.setDatatype(DataType.INT);
					tempi9999.setLength(4);
					table.addField(tempi9999);
				}else if("B01".equalsIgnoreCase(name)){
					// 机构编码
					Field temporgid = new Field("orgid", "机构编码");
					temporgid.setNullable(false);
					temporgid.setKeyable(false);
					temporgid.setDatatype(DataType.STRING);
					temporgid.setLength(30);
					table.addField(temporgid);
				}else if(!"B01".equalsIgnoreCase(name)&&"B".equalsIgnoreCase(firstName)){
					// 机构编码
					Field temporgid = new Field("orgid", "机构编码");
					temporgid.setNullable(false);
					temporgid.setKeyable(false);
					temporgid.setDatatype(DataType.STRING);
					temporgid.setLength(30);
					table.addField(temporgid);
					// 子集记录顺序号
					Field tempi9999 = new Field("i9999", "子集记录顺序号");
					tempi9999.setNullable(false);
					tempi9999.setKeyable(false);
					tempi9999.setDatatype(DataType.INT);
					tempi9999.setLength(4);
					table.addField(tempi9999);
				}else if("K01".equalsIgnoreCase(name)){
					// 岗位编码
					Field temppostid = new Field("postid", "岗位编码");
					temppostid.setNullable(false);
					temppostid.setKeyable(false);
					temppostid.setDatatype(DataType.STRING);
					temppostid.setLength(30);
					table.addField(temppostid);
				}else if(!"K01".equalsIgnoreCase(name)&&"K".equalsIgnoreCase(firstName)){
					// 机构编码
					Field temppostid = new Field("postid", "岗位编码");
					temppostid.setNullable(false);
					temppostid.setKeyable(false);
					temppostid.setDatatype(DataType.STRING);
					temppostid.setLength(30);
					table.addField(temppostid);
					// 子集记录顺序号
					Field tempi9999 = new Field("i9999", "子集记录顺序号");
					tempi9999.setNullable(false);
					tempi9999.setKeyable(false);
					tempi9999.setDatatype(DataType.INT);
					tempi9999.setLength(4);
					table.addField(tempi9999);
				}
				DRFieldItemBean fieldItem = ParserXmlBean.getFieldItem();
		        List columnList = fieldItem.getItemList();
				for (int j = 0; j < columnList.size(); j++) {
					DRItemBean bean =  (DRItemBean) columnList.get(j);
					String setid = bean.getSetid();
					if (name.equalsIgnoreCase(setid)) {
						String itemid = bean.getItemid();// 字段名
						if("A01".equalsIgnoreCase(setid)) {
							if("b0110".equalsIgnoreCase(itemid)||"e0122".equalsIgnoreCase(itemid)||"e01a1".equalsIgnoreCase(itemid)){
								continue;
							}
						}else if("B01".equalsIgnoreCase(setid)) {
							if("orgid".equalsIgnoreCase(itemid)){
								continue;
							}
						}else if("K01".equalsIgnoreCase(setid)) {
							if("postid".equalsIgnoreCase(itemid)){
								continue;
							}
						}
						String itemdesc = bean.getItemdesc();// 字段描述
						String itemtype = bean.getItemtype();// 字段类型
						String itemlength = bean.getItemlength();// 字段长度
						String mustfill = bean.getMustfill();// 是否必填
						String uniq = bean.getUniq();// 是否唯一
						int dataType = 0;
						boolean mustfield = true;
						if ("A".equalsIgnoreCase(itemtype)) {
							dataType = DataType.STRING;
							itemlength = "200";
						} else if ("D".equalsIgnoreCase(itemtype)) {
							dataType = DataType.DATE;
						} else if ("N".equalsIgnoreCase(itemtype)) {
							dataType = DataType.INT;
						} else if ("M".equalsIgnoreCase(itemtype)) {
							dataType = DataType.CLOB;
						} else if ("true".equalsIgnoreCase(mustfill)) {
							mustfield = false;
						}
						Field temp = new Field(itemid, itemdesc);
						temp.setNullable(mustfield);
						temp.setKeyable(false);
						temp.setDatatype(dataType);
						temp.setLength(Integer.parseInt(itemlength));
						table.addField(temp);
					}
				}
				dbWizard.createTable(table);
			}
		} catch (Exception e) {
			flag = "fail";
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 获取需要的xml
	 * @return
	 */
	private String getConstantXml() {
		String str_value = "";
		StringBuffer sql = new StringBuffer(
				"SELECT str_value from Constant where Constant = 'BS_ASYN_PLAN_S' ");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql.toString());
			if (rs.next()) {
				str_value = rs.getString("str_value");
			}
		} catch (Exception e) {
			PubFunc.closeDbObj(rs);
		}
		return str_value;
	}
}
