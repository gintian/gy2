package com.hjsj.hrms.service.core.soap;

import com.hjsj.hrms.businessobject.sys.options.interfaces.ChangeInfoInterfaces;
import com.hjsj.hrms.interfaces.webservice.HrIssuanceInterf;
import com.hjsj.hrms.module.system.regothersys.SysRegBean;
import com.hjsj.hrms.module.system.regothersys.SysRegLogger;
import com.hjsj.hrms.service.core.util.ServiceType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * 
 * @author hssoft
 *
 */
public class SoapServiceBo {

	/**
	 * 日志对象
	 */
	private Category log = Category.getInstance(getClass().getName());
	
	private SysRegBean sysRegBean;
	
	/*public static String syscode = "1";
	public static String sysetoken = "2";
	public static String dynamicetoken = "3";*/
	
	private SysRegLogger regLogger;
	public SysRegLogger getRegLogger() {
		return regLogger;
	}
	
	/**
	 * SoapServiceBo构造方法
	 * @param opt 查询方式 
	 * 			1:通过sysCode(系统编码)查询
	 * 			2:通过sysetoken(认证标识)查询
	 * 			3:通过dynamicEtoken(动态认证码)查询
	 * 			其他情况通过sysetoken和dynamicEtoken查询
	 * @param identify
	 */
	public SoapServiceBo(String opt,String identify) {
		this.createBean(opt,identify);
	}

	/**
	 * 创建认证系统的实体类对象
	 * @param opt 查询方式 
	 * 			1:通过sysCode(系统编码)查询
	 * 			2:通过sysetoken(认证标识)查询
	 * 			3:通过dynamicEtoken(动态认证码)查询
	 * 			其他情况通过sysetoken和dynamicEtoken查询
	 * @param identify 唯一标识
	 */
	private void createBean(String opt,String identify) {
		if(sysRegBean==null) {
			String selSql = "";
			Connection conn = null;
			RowSet rs = null;
			List<String> valList = new ArrayList<String>();
			if("1".equals(opt)) {
				selSql = "SELECT syscode,sysetoken,valid,servicedetail,validateway,dynamicEtoken,etokencreatetime from t_sys_reg_services where sysCode = ?";
				valList.add(identify);
			}else if("2".equals(opt)) {
				selSql = "SELECT syscode,sysetoken,valid,servicedetail,validateway,dynamicEtoken,etokencreatetime from t_sys_reg_services where sysetoken = ?";
				valList.add(identify);
			}else if("3".equals(opt)) {
				selSql = "SELECT syscode,sysetoken,valid,servicedetail,validateway,dynamicEtoken,etokencreatetime from t_sys_reg_services where dynamicEtoken = ?";
				valList.add(identify);
			}else {
				selSql = "SELECT syscode,sysetoken,valid,servicedetail,validateway,dynamicEtoken,etokencreatetime from t_sys_reg_services where sysetoken = ? or dynamicEtoken = ?";
				valList.add(identify);
				valList.add(identify);
			}
			try {
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(selSql,valList);
				if(rs.next()) {
					sysRegBean = new SysRegBean();
					sysRegBean.setSyscode(rs.getString("syscode")!=null?rs.getString("syscode"):"");
					sysRegBean.setSysetoken(rs.getString("sysetoken")!=null?rs.getString("sysetoken"):"");
					sysRegBean.setValid(rs.getString("valid")!=null?rs.getString("valid"):"");
					sysRegBean.setServicedetail(rs.getString("servicedetail")!=null?rs.getString("servicedetail"):"");
					sysRegBean.setValidateway(rs.getString("validateway")!=null?rs.getString("validateway"):"");
					sysRegBean.setDynamicEtoken(rs.getString("dynamicEtoken")!=null?rs.getString("dynamicEtoken"):"");
					sysRegBean.setEtokencreatetime(rs.getTimestamp("etokencreatetime"));
					//若存在记录则记录的syscode一定存在,创建对应的日志对象
					regLogger = new SysRegLogger(sysRegBean.getSyscode());
				}
			} catch (GeneralException e) {
				e.printStackTrace();
				log.error("创建认证系统类:创建conn链接出错!");
				log.error(e);
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("创建认证系统类:执行查询sql出错!");
				log.error(e);
			}finally {
				PubFunc.closeDbObj(conn);
				PubFunc.closeDbObj(rs);
			}
		}
	}
	
	/**
	 * 更新信息集
	 * @param etoken 认证码
	 * @param type 流程标志
	 * @param xml XML格式的数据,详情查看白皮书
	 * @return
	 */
	public String syncProcess(String etoken, String type, String xml) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return syncProcessReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return syncProcessReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.PROCESS.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("更新信息集服务未启用");
					return syncProcessReturnMess("更新信息集服务未启用");
				}
				break;
			}
		}
		SoapSyncProcessBo processBo = new SoapSyncProcessBo();
		result = processBo.syncProcess(type, xml);
		regLogger.start("更新信息集结果："+result);
		return result;
	}
	
	/**
	 * 更新年假天数
	 * @param etoken 认证码
	 * @param xml XML格式的数据
	 * @return 
	 */
	public String updateHolidays(String etoken, String xml) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return holidaysReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return holidaysReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.UPD_HOLIDAY.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("更新年假天数服务未启用");
					return holidaysReturnMess("更新年假天数服务未启用");
				}
				break;
			}
		}
		SoapHolidayBo holidayBo = new SoapHolidayBo(regLogger);
		result = holidayBo.updateHolidays(etoken,xml);
		regLogger.start("更新年假天数成功，返回数据内容："+result);
		return result;
	}

	/**
	 * 获取年假已休、可休天数
	 * @param etoken 认证码
	 * @param xml XML格式的数据
	 * @return Xml格式的数据其中包含年假假期（可休、已休天数）
	 */
	public String getHolidayMsg(String etoken, String xml) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return holidaysReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return holidaysReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.GET_HOLIDAY.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取年假已休、可休天数服务未启用");
					return holidaysReturnMess("获取年假已休、可休天数服务未启用");
				}
				break;
			}
		}
		SoapHolidayBo holidayBo = new SoapHolidayBo(regLogger);
		result = holidayBo.getHolidayMsg(etoken,xml);
		regLogger.start("获取年假已休、可休天数成功，数据内容："+result);
		return result;
	}

	//TODO
	/**
	 * 获取用户标识
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return 加密串/空串
	 */
	public String getUserEtoken(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			//return "HR系统中服务列表出现问题";
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.USERETOKEN.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取用户标识服务未启用");
					//return "获取用户标识服务未启用";
					return returnMessLog("获取用户标识服务未启用",1,"");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"","false",conn);
				if (userView != null) {
					result = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName + "," + userView.getPassWord()));
					result = returnMessLog(result, 0, "");
				}else {
					//result = "用户不存在";
					result = returnMessLog("用户不存在",1,"");
				}
			} else {
				result = returnMessLog("数据池连接没有成功,请联系HR人员",1,"");;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取用户标识出错:");
			log.error(e);
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取用户标识成功，返回数据内容："+result);
		return result;
	}

	/**
	 * 获取考勤（请假、加班、公出）报批信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getKqInfoXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.KQINFO.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取考勤报批信息服务未启用");
					return hrIssuanceReturnMess("获取考勤报批信息服务未启用");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrkqContent(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取考勤报批信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 返回当前人员的预警信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getWarnXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.WARN.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取预警信息服务未启用");
					return hrIssuanceReturnMess("获取预警信息服务未启用");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrSysWarn(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取预警信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 返回当前人员的报表信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getReportXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.REPORT.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取报表信息服务未启用");
					return hrIssuanceReturnMess("获取报表信息服务未启用");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrReportContent(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取报表信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 返回当前人员的常用统计信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getStaticsXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.STATICS.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取常用统计信息服务未启用");
					return hrIssuanceReturnMess("获取常用统计信息服务未启用");
				}
				break;
			}
		}
		Connection conn = null;
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrStatics(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取常用统计信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 返回当前人员的待办信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getMatterXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.MATTER.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取待办信息服务未启用");
					return hrIssuanceReturnMess("获取待办信息服务未启用");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();		
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrMatterContent(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取待办信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 返回当前人员的公告信息
	 * @param etoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getBoardXml(String etoken, String userName) {
		String result = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return hrIssuanceReturnMess(errorMsg);
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return hrIssuanceReturnMess("HR系统中服务列表出现问题");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.BOARD.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取公告信息服务未启用");
					return hrIssuanceReturnMess("获取公告信息服务未启用");
				}
				break;
			}
		}
		HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			if (conn != null) {
				UserView userView = hrIssuanceInterf.getSetView(userName,"", "false", conn);
				if (userView != null) {
					result = hrIssuanceInterf.getHrBoardContent(conn);
				} else {
					return hrIssuanceReturnMess("用户不存在");
				}
			} else
				return hrIssuanceReturnMess("数据池连接没有成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取公告信息成功，数据内容："+result);
		return result;
	}

	/**
	 * 更新数据同步状态
	 * @param etoken 认证标识
	 * @param xml 需要处理的数据
	 * @param onlyFiled 唯一标识(默认为unique_id)
	 * @param sysFlag 系统标识，由eHR系统提供如AD,OA(默认是flag)
	 * @param type "ORG" 代表机构，"HR"代表人员，"POST"代表岗位
	 * @return XML格式的处理状态
	 */
	public String updInfoState(String etoken, String xml, String onlyFiled, String sysFlag, String type) {
		boolean isCorrect = false;
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		//唯一标识为空串时置为默认值
		if(StringUtils.isBlank(onlyFiled)) {
			onlyFiled = "unique_id";
		}
		//标识字段为空串时置为默认值
		if(StringUtils.isBlank(sysFlag)) {
			sysFlag = "flag";
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		if ("HR".equalsIgnoreCase(type)) {
			for (Map<String, String> map : serviceList) {
				String method = map.get("serviceMethod");
				if(ServiceType.EMP.equalsIgnoreCase(method)) {
					String valid = map.get("valid");
					if(!"1".equals(valid)) {
						regLogger.start("获取人员信息服务未启用");
						return returnMessLog("获取人员信息服务未启用",1,"");
					}
					break;
				}
			}
		} else if ("ORG".equalsIgnoreCase(type)) {
			for (Map<String, String> map : serviceList) {
				String method = map.get("serviceMethod");
				if(ServiceType.ORG.equalsIgnoreCase(method)) {
					String valid = map.get("valid");
					if(!"1".equals(valid)) {
						regLogger.start("获取机构信息服务未启用");
						return returnMessLog("获取机构信息服务未启用",1,"");
					}
					break;
				}
			}
		} else if ("POST".equalsIgnoreCase(type)) {
			for (Map<String, String> map : serviceList) {
				String method = map.get("serviceMethod");
				if(ServiceType.POST.equalsIgnoreCase(method)) {
					String valid = map.get("valid");
					if(!"1".equals(valid)) {
						regLogger.start("获取岗位信息服务未启用");
						return returnMessLog("获取岗位信息服务未启用",1,"");
					}
					break;
				}
			}
		}else {
			regLogger.start("type值填写错误");
			return returnMessLog("type值填写错误",1,"");
		}
		Connection conn = null;
		ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
		try {
			conn = AdminDb.getConnection();
			isCorrect = infoInter.returnSynchroXml(conn, xml, onlyFiled, sysFlag, type);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("同步信息时发生错误！", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		if (isCorrect) {
			regLogger.start("更新数据同步状态成功");
			return returnMessLog("更新数据同步状态成功", 0, "");
		} else {
			regLogger.start("更新数据同步状态失败,失败信息为:"+infoInter.getErrorMess());
			return returnMessLog("更新数据同步状态失败", 1, infoInter.getErrorMess());
		}
	}
	

	
	public String getUserInfo(String etoken,String whereStr) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.EMP.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取人员信息服务未启用");
					return returnMessLog("获取人员信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		if(!checkWhere(whereStr)) {
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		whereScope = parseWhere(sqlscope, "fields");
		String whereSql = "";
		if(StringUtils.isNotBlank(whereStr)) {
			whereSql = whereStr;
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeUsers(conn, whereSql);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取人员变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取人员变动信息成功，数据内容："+result);
		return result;
	}
	
	
	/**
	 * 按查询条件返回机构数据
	 * @param etoken
	 * @param sqlWhere  sql语句条件，一般按修改日期查询变化数据。
	 * @return XML格式的机构信息数据
	 */
	public String getOrgInfo(String etoken,String whereStr) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}

		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.ORG.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取机构信息服务未启用");
					return returnMessLog("获取机构信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		if(!checkWhere(whereStr)) {
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		whereScope = parseWhere(sqlscope, "org_fields");
		String whereSql = "";
		if(StringUtils.isNotBlank(whereStr)) {
			whereSql = whereStr;
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeOrganizations(conn, whereStr);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取机构变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取机构变动信息成功，数据内容："+result);
		return result;
	}
	

	
	/**
	 * 返回有变动（新增、删除、更新）的岗位信息数据
	 * @param etoken
	 * @param whereStr  sql条件
	 * @return XML格式的岗位信息数据
	 */
	public String getPostInfo(String etoken,String whereStr) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.POST.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取岗位信息服务未启用");
					return returnMessLog("获取岗位信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		if(!checkWhere(whereStr)) {
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		whereScope = parseWhere(sqlscope, "post_fields");
		String whereSql = "";
		if(StringUtils.isNotBlank(whereStr)) {
			whereSql = whereStr;
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangePost(conn, whereSql);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取岗位变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取岗位变动信息成功，数据内容："+result);
		return result;
	}
	
	/**
	 * 获取注册系统的动态认证码
	 * @param sysCode 注册系统编码
	 * @return XML格式的数据
	 */
	public String getSysEtoken(String sysCode) {
		String result = "";
		String checkResult = this.checkSyscode(sysCode);
		StringBuffer strxml = new StringBuffer();
		if("0".endsWith(checkResult)) {
			return returnMessLog("系统编码不存在",1,"");
		}
		if("3".endsWith(checkResult)) {
			regLogger.start("未设置动态认证");
			return returnMessLog("未设置动态认证,不能申请动态码",1,"");
		}else if("2".endsWith(checkResult)) {
			regLogger.start("未启用该系统");
			return returnMessLog("未启用该系统",1,"");
		}
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();//创建UUID
		Date date = new Date();
		Timestamp timestamp = DateUtils.getTimestamp(date);//生成当前时间的时间戳
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String updateSql = "UPDATE t_sys_reg_services SET dynamicEtoken =?,etokencreatetime =? WHERE syscode = ?";
			List<Object> values = new ArrayList<Object>();
			values.add(uuid);
			values.add(timestamp);
			values.add(sysCode);
			dao.update(updateSql, values);
			strxml.append("<syscode>"+ uuid +"</syscode>");
		} catch (GeneralException e) {
			e.printStackTrace();
			log.error("创建动态认证标识:创建conn链接出错!");
			log.error(e);
			return returnMessLog("HR系统数据库连接出现问题,请查看HR日志",1,"");
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("创建动态认证标识:执行更新sql出错!");
			log.error(e);
			return returnMessLog("申请动态认证码出现问题,请查看HR日志",1,"");
		}finally {
			PubFunc.closeDbObj(conn);
		}
		//regLogger.start("动态码为:"+uuid);
		sysRegBean = null;//更新数据后,将对象置为空
		result = returnMessLog("",2,strxml.toString());
		regLogger.start("获取动态认证码成功，数据内容："+result);
		return result;
	}
	
	/**
	 * 验证系统是否启用
	 * @return 0:未注册系统,1:成功,2:系统未启用
	 */
	private String validateValid() {
		String result = "0";
		if(sysRegBean != null) {
			String valid = sysRegBean.getValid();
			if("1".equals(valid)) {
				result = "1";
			}else {
				result = "2";
			}
		}
		return result;
	}

	/**
	 * 验证动态码的时间是否过期
	 * @return 空串:动态码认证成功,0:未注册系统,1:启用动态码但用静态码请求,2:系统未启用,3:静态码认证成功,4:动态码已过期,5:未申请动态码
	 */
	private String validateDynamic(String etoken) {
		//验证系统是否启用
		String validate = this.validateValid();
		if(!"1".equals(validate)) {
			return validate;
		}
		String result = "3";
		String validateway = sysRegBean.getValidateway();
		if("1".equals(validateway)) {
			if(!StringUtils.equals(etoken, sysRegBean.getDynamicEtoken())) {
				return "1";
			}
			if(sysRegBean.getEtokencreatetime()!=null) {
				long createTime = sysRegBean.getEtokencreatetime().getTime();
				long time = System.currentTimeMillis()-createTime;
				if(time<1800000) {//默认30分钟,30*60*1000
					result = "";
				}else {
					result = "4";
				}
			}else {
				result = "5";
			}
		}
		return result;
	}

	/**
	 * 获取错误信息并记录日志
	 * @param validate 
	 * @return 
	 */
	private String getErrorMsgAndLog(String validate,String etoken) {
		// 空串:动态码认证成功,0:未注册系统,1:启用动态码但用静态码请求,2:系统未启用,3:静态码认证成功,4:动态码已过期,5:未申请动态码
		String result = "";
		if("".equals(validate)) {
			return result;
		}
		int code = Integer.parseInt(validate);
		switch (code) {
		case 0:
			//regLogger.start("认证标识错误");
			result = "认证标识错误";
			break;
		case 1:
			String dynamicEtoken = sysRegBean.getDynamicEtoken();
			if(!dynamicEtoken.equals(etoken)) {
				regLogger.start("请使用动态码");
				result = "请使用动态码";
			}
			break;
		case 2:
			regLogger.start("系统未启用");
			result = "系统未启用";
			break;
		/*case 3:
			//一般不会出现这种情况
			String sysetoken = sysRegBean.getSysetoken();
			if(!sysetoken.equals(etoken)) {
				regLogger.start("认证标识错误");
				result = "认证标识错误";
			}
			break;*/
		case 4:
			regLogger.start("动态码已过期");
			result = "动态码已过期";
			break;
		case 5:
			//一般不会走这段代码
			regLogger.start("未申请动态码");
			result = "未申请动态码";
			break;
		default:
			return result;
		}
		return result;
	}

	/**
	 * 校验系统编码是否存在
	 * @param syscode 系统编码
	 * @return 0:系统编码不存在,1:成功,2:系统未启用,3:未设置动态认证
	 */
	private String checkSyscode(String syscode) {
		String result = "0";
		/*if(sysRegBean == null) {
			this.createBean(SoapServiceBo.syscode, syscode);
		}*/
		if(sysRegBean != null) {
			//regLogger = new SysRegLogger(syscode);
			if("0".equals(sysRegBean.getValid())) {
				result = "2";
			}else {
				String validateway = sysRegBean.getValidateway();
				if("1".equals(validateway)) {
					result = "1";
				}else if("0".equals(validateway)){
					result = "3";
				}
			}
		}
		return result;
	}
	
	/**
	 * 解析定义的where语句
	 * @param c_expr
	 * @param node
	 * @return
	 * @throws GeneralException
	 */
	private String parseWhere(String c_expr,String node){
		String result = "";
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		c_expr=SafeCode.decode(c_expr);
		Connection conn = null;
		String[] parseResult = this.getFields(node);
		try {
			conn = AdminDb.getConnection();
			if (c_expr != null && c_expr.length() > 0) {
			    ArrayList fieldlist = new ArrayList();
				//ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				//fieldlist.addAll(alUsedFields);
				FieldItem nbase_0 = new FieldItem();
				nbase_0.setItemid("nbase_0");
				nbase_0.setItemdesc("人员库前缀");
				nbase_0.setItemtype("A");
				nbase_0.setItemlength(3);
				nbase_0.setDecimalwidth(0);
				nbase_0.setCodesetid("0");
				fieldlist.add(nbase_0);
				
				FieldItem b0110_0 = new FieldItem();
				b0110_0.setItemid("b0110_0");
				b0110_0.setItemdesc("机构编码");
				b0110_0.setItemtype("A");
				b0110_0.setItemlength(50);
				b0110_0.setDecimalwidth(0);
				b0110_0.setCodesetid("0");
				fieldlist.add(b0110_0);
				
				FieldItem e0122_0 = new FieldItem();
				e0122_0.setItemid("e0122_0");
				e0122_0.setItemdesc("部门编码");
				e0122_0.setItemtype("A");
				e0122_0.setItemlength(50);
				e0122_0.setDecimalwidth(0);
				e0122_0.setCodesetid("0");
				fieldlist.add(e0122_0);
				
				FieldItem e01a1_0 = new FieldItem();
				e01a1_0.setItemid("e01a1_0");
				e01a1_0.setItemdesc("岗位编码");
				e01a1_0.setItemtype("D");
				e01a1_0.setItemlength(50);
				e01a1_0.setDecimalwidth(0);
				e01a1_0.setCodesetid("0");
				fieldlist.add(e01a1_0);
				
				FieldItem sDate = new FieldItem();
				sDate.setItemid("sDate");
				sDate.setItemdesc("更新时间");
				sDate.setItemtype("D");
				sDate.setItemlength(10);
				sDate.setDecimalwidth(0);
				sDate.setCodesetid("0");
				fieldlist.add(sDate);
				for (int i = 0; i < parseResult.length; i++) {
					FieldItem fieldItem = DataDictionary.getFieldItem(parseResult[i]);
					fieldlist.add(fieldItem);
				}
				//创建UserView对象
				UserView userView = new UserView("su", conn);
				//创建解析类
				YksjParser yp = new YksjParser(userView, fieldlist, YksjParser.forNormal, YksjParser.LOGIC
						, YksjParser.forNormal,"Ht", "");
				yp.setCon(conn);
				//解析sql
				yp.run_where(c_expr);
				/*String sql = yp.getSQL();
				System.out.println(sql);*/
				//result = " ( "+yp.getSQL()+" ) ";
				result = yp.getSQL();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(conn);
		}
		return result;
	}
	
	/**
	 * 获取同步的字段名
	 * @param node org_fields,post_fields,fields
	 * @return 数组对象
	 */
	private String[] getFields(String node) {
		Connection conn = null;
		String result[] = null;
		String str = "";
		try {
			String sql = "select Str_Value from Constant where Constant = 'SYS_EXPORT_VIEW'";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while(rs.next()) {
				str = rs.getString("Str_Value");
			}
		} catch (Exception e) {
			log.error("获取Constant表中数据出错:");
			log.error(e);
		}finally {
			PubFunc.closeDbObj(conn);
		}
		if(str.length()>0) {
			try {
				Document doc = PubFunc.generateDom(str);
				XPath xpath =null;
				xpath = XPath.newInstance("/root/"+node);
				Element ele = (Element) xpath.selectSingleNode(doc);
				result = ele.getText().split(",");
			} catch (Exception e) {
				log.error("解析 "+node+" 时出错:");
				log.error(e);
			}
		}
		return result;
	}
	
	/**
	 * 解析serviceXML并将生成的List返回
	 * @param str
	 * @return
	 */
	private ArrayList<Map<String,String>> parseServiceDetail(String str) {
		ArrayList<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		try {
			Document doc = PubFunc.generateDom(str);
			XPath xpath =null;
			xpath = XPath.newInstance("/services/service");
			List<Element> selectNodes = xpath.selectNodes(doc);
			for (Element element : selectNodes) {
				Map<String, String> map = new HashMap<String, String>();
				Attribute attribute = element.getAttribute("id");
				String value = attribute.getValue();
				String valid = element.getChild("valid").getText();
				map.put("serviceMethod", value);
				map.put("valid", valid);
				if(ServiceType.ORG.equalsIgnoreCase(value) || ServiceType.POST.equalsIgnoreCase(value) || ServiceType.EMP.equalsIgnoreCase(value)) {
					String sqlscope = element.getChild("sqlscope").getText();
					map.put("sqlscope", sqlscope);
				}
				resultList.add(map);
			}
		} catch (Exception e) {
			log.error("解析XML出错!");
			log.error(e);
		}
		return resultList;
	}
	
	/**
	 * 返回XML格式的信息提示或错误信息
	 * @param mess 提示信息
	 * @param flag 1:错误,0:信息
	 * @param errorElementStr
	 * @return 拼接后的XML
	 */
	private String returnMessLog(String mess, int flag, String errorElementStr) {
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version='1.0' encoding=\"GBK\"?>");
		strxml.append("<hr version=\"5.0\">");
		strxml.append("<title>人力资源系统</title>");
		strxml.append("<language>zh-cn</language>");
		if (flag == 0)
			strxml.append("<mess>" + mess + "</mess>");
		else if (flag == 1)
			strxml.append("<error>" + mess + "</error>");
		if (errorElementStr != null && errorElementStr.length() > 0)
			strxml.append(errorElementStr);
		strxml.append("</hr>");
		return strxml.toString();
	}
	
	/**
	 * syncProcess方法返回XML格式的错误信息
	 * @param mess 提示信息
	 * @param flag 1:错误,0:信息
	 * @return 拼接后的XML
	 */
	private String syncProcessReturnMess(String mess) {
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version='1.0' encoding=\"GBK\"?>");
		strxml.append("<root>");
		strxml.append("<error>" + mess + "</error>");
		strxml.append("</root>");
		return strxml.toString();
	}
	
	/**
	 * hrpIssuance类中方法返回XML格式的错误信息
	 * @param mess 提示信息
	 * @return 拼接后的XML
	 */
	private String hrIssuanceReturnMess(String mess) {
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version='1.0' encoding=\"GBK\"?>");
		strxml.append("<rss version=\"2.0\">");
		strxml.append("<channel>");
		strxml.append("<title>人力资源系统</title>");
		//strxml.append("<link></link>");
		strxml.append("<description>集团人力资源系统</description>");
		strxml.append("<language>zh-cn</language>");
		strxml.append("<error>"+mess+"</error>");
		strxml.append("</channel>");
		strxml.append("</rss>");
		return strxml.toString();
	}
	
	/**
	 * hrpIssuance类中方法返回XML格式的错误信息
	 * @param mess 提示信息
	 * @return 拼接后的XML
	 */
	private String holidaysReturnMess(String mess) {
		StringBuffer str = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		str.append("<ehr>");
		str.append("<info>false</info>");
		str.append("<error>"+mess+"</error>");
		str.append("</ehr>");
		return str.toString();
	}
	
	private boolean checkWhere(String where)
	{
		String str = where.toLowerCase();
	    if (str.indexOf("select") > -1 && str.indexOf("from") > -1) return false;   
	    if (str.indexOf("where") > -1 && str.indexOf("=") > -1) return false;   
	    if (str.indexOf("update") > -1 && str.indexOf("set") > -1) return false;   
	    if (str.indexOf("delete") > -1 && str.indexOf("from") > -1) return false;   
	    if (str.indexOf("insert") > -1 && str.indexOf("into") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("exec") > -1 && str.indexOf("master") > -1) return false;   
	    if (str.indexOf("exec") > -1 && str.indexOf("xp_cmdshell") > -1) return false;   
	  
         return true;
	}
	
	
	/**
	 * 返回所有机构信息数据
	 * @param etoken
	 * @return XML格式的机构信息数据
	 */
/*	public String getAllOrg(String etoken) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.ORG.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取机构信息服务未启用");
					return returnMessLog("获取机构信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		whereScope = parseWhere(sqlscope, "org_fields");
		//org_fields
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeOrganizations(conn, whereScope);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取机构信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取机构信息成功，数据内容："+result);
		return result;
	}*/
	
	/**
	 * 返回所有机构信息数据
	 * @param etoken
	 * @return XML格式的机构信息数据
	 */
/*	public String getChangeOrg(String etoken,String flag) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.ORG.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取机构信息服务未启用");
					return returnMessLog("获取机构信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		whereScope = parseWhere(sqlscope, "org_fields");
		String whereSql = "";
		if("".equals(flag) || "flag".equals(flag)) {
			whereSql = "flag !=0 ";
		}else { 
			whereSql = flag +" !=0 ";
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeOrganizations(conn, whereSql);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取机构变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取机构变动信息成功，数据内容："+result);
		return result;
	}*/
	
	
	/**
	 * 返回所有岗位信息数据
	 * @param etoken
	 * @return XML格式的岗位信息数据
	 */
/*	public String getAllPost(String etoken) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.POST.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取岗位信息服务未启用");
					return returnMessLog("获取岗位信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		whereScope = parseWhere(sqlscope, "post_fields");
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangePost(conn, whereScope);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取岗位信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取岗位信息成功，数据内容："+result);
		return result;
	}
	*/
	/**
	 * 返回有变动（新增、删除、更新）的岗位信息数据
	 * @param etoken
	 * @return XML格式的岗位信息数据
	 */
/*	public String getChangePost(String etoken,String flag) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.POST.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取岗位信息服务未启用");
					return returnMessLog("获取岗位信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}

		whereScope = parseWhere(sqlscope, "post_fields");
		String whereSql = "";
		if("".equals(flag) || "flag".equals(flag)) {
			whereSql = "flag !=0 ";
		}else { 
			whereSql = flag +" !=0 ";
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangePost(conn, whereSql);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取岗位变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取岗位变动信息成功，数据内容："+result);
		return result;
	}
	*/
	
	/**
	 * 返回所有人员信息数据
	 * @param etoken
	 * @return XML格式的人员信息数据
	 */
/*	public String getAllUser(String etoken) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.EMP.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取人员信息服务未启用");
					return returnMessLog("获取人员信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		whereScope = parseWhere(sqlscope, "fields");
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeUsers(conn, whereScope);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取人员信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取人员信息成功，数据内容："+result);
		return result;
	}*/
	
	/**
	 * 返回有变动（新增、删除、更新）的人员信息数据
	 * @param etoken
	 * @return XML格式的人员信息数据
	 */
/*	public String getChangeUser(String etoken,String flag) {
		String result = "";
		String whereScope = "";
		String sqlscope = "";
		String validate = this.validateDynamic(etoken);
		String errorMsg = this.getErrorMsgAndLog(validate,etoken);
		if(!"".equals(errorMsg)) {
			return returnMessLog(errorMsg,1,"");
		}
		String servicedetail = sysRegBean.getServicedetail();
		if(StringUtils.isBlank(servicedetail)) {
			regLogger.start("错误:服务列表为空");
			return returnMessLog("HR系统中服务列表出现问题",1,"");
		}
		ArrayList<Map<String, String>> serviceList = this.parseServiceDetail(servicedetail);
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.EMP.equalsIgnoreCase(method)) {
				String valid = map.get("valid");
				if(!"1".equals(valid)) {
					regLogger.start("获取人员信息服务未启用");
					return returnMessLog("获取人员信息服务未启用",1,"");
				}
				sqlscope = map.get("sqlscope");
				break;
			}
		}
		whereScope = parseWhere(sqlscope, "fields");
		String whereSql = "";
		if("".equals(flag) || "flag".equals(flag)) {
			whereSql = "flag !=0 ";
		}else { 
			whereSql = flag +" !=0 ";
		}
		if(StringUtils.isNotBlank(whereScope)) {
			whereSql = whereSql + " and " + whereScope;
		}
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
			result = infoInter.getWhereChangeUsers(conn, whereSql);
		} catch (Exception e) {
			e.printStackTrace();
			return returnMessLog("获取人员变动信息错误", 1, "");
		} finally {
			PubFunc.closeDbObj(conn);
		}
		regLogger.start("获取人员变动信息成功，数据内容："+result);
		return result;
	}*/
	
}
