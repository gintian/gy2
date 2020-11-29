package com.hjsj.hrms.service.core.soap;

/**
 * 集成注册服务Http请求Bo类
 * @author cuibl
 *
 */
public class HrSoapService {

	/**
	 * 获取注册系统的动态认证码
	 * @param sysCode 注册系统编码
	 * @return
	 */
	public String getSysEtoken(String sysCode) {
		String result = "";

		SoapServiceBo bo = new SoapServiceBo("1", sysCode);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取动态认证码:");
		}
		result = bo.getSysEtoken(sysCode);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取动态认证码结束。");
		}
		return result;
	}
	

	/**
	 * 获取有变动（新增、删除、更新）的机构信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */
	public String getOrgInfo(String sysEtoken, String sqlWhere) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息:");
		}
		result = bo.getOrgInfo(sysEtoken, sqlWhere);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息结束。");
		}
		return result;
	}
	/**
	 * 获取的机构信息数据,按sql条件查询
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */
	 public String getPostInfo(String sysEtoken, String flag) {
			String result = "";
			SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
			if (bo.getRegLogger() != null) {
				bo.getRegLogger().start("获取岗位变动信息:");
			}
			result = bo.getPostInfo(sysEtoken, flag);
			if (bo.getRegLogger() != null) {
				bo.getRegLogger().start("获取岗位变动信息结束。");
			}
			return result;
		}
	

	/**
	 * 获取的人员信息数据,按sql条件查询
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */

	public String getUserInfo(String sysEtoken, String sqlWhere) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息:");
		}
		result = bo.getUserInfo(sysEtoken, sqlWhere);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息结束。");
		}
		return result;
	}
	

	
	/**
	 * 更新数据同步状态
	 * @param sysEtoken 认证标识
	 * @param xml 需要处理的数据
	 * @param onlyFiled 唯一标识(默认为unique_id)
	 * @param sysFlag 系统标识，由eHR系统提供如AD,OA(默认是flag)
	 * @param type "ORG" 代表机构，"HR"代表人员，"POST"代表岗位
	 * @return XML格式的处理状态
	 */
	public String updInfoState(String sysEtoken,String xml,String onlyFiled,String sysFlag,String type) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新数据同步状态:");
		}
		result = bo.updInfoState(sysEtoken, xml, onlyFiled, sysFlag, type);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新数据同步状态结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的公告信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getBoardXml(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取公告信息:");
		}
		result = bo.getBoardXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取公告信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的待办信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getMatterXml(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取待办信息:");
		}
		result = bo.getMatterXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取待办信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的常用统计信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getStaticsXml(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取常用统计信息:");
		}
		result = bo.getStaticsXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取常用统计信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的报表信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getReportXml(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取报表信息:");
		}
		result = bo.getReportXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取报表信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的预警信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getWarnXml(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息:");
		}
		result = bo.getWarnXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取考勤（请假、加班、公出）报批信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getKqInfoXml(String sysEtoken,String userName){
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取考勤报批信息:");
		}
		result = bo.getKqInfoXml(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取考勤报批信息结束。");
		}
		return result;
		
	}
	
	/**
	 * 更新信息集
	 * @param sysEtoken 认证码
	 * @param type 流程标志
	 * @param xml XML格式的数据,详情查看白皮书
	 * @return
	 */
	public String syncProcess(String sysEtoken,String type,String xml) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新子集信息:");
		}
		result = bo.syncProcess(sysEtoken, type, xml);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新子集信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取用户标识
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	public String getUserEtoken(String sysEtoken,String userName) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取用户标识:");
		}
		result = bo.getUserEtoken(sysEtoken, userName);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取用户标识结束。");
		}
		return result;
	}
	
	/**
	 * 获取年假已休、可休天数
	 * @param sysEtoken 认证码
	 * @param xml XML格式的数据
	 * @return Xml格式的数据其中包含年假假期（可休、已休天数）
	 */
	public String getHolidayMsg(String sysEtoken,String xml) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取年假已休、可休天数:");
		}
		result = bo.getHolidayMsg(sysEtoken, xml);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取年假已休、可休天数结束。");
		}
		return result;
	}
	
	/**
	 * 更新年假天数
	 * @param sysEtoken 认证码
	 * @param xml XML格式的数据
	 * @return 
	 */
	public String updateHolidays(String sysEtoken,String xml) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新年假天数:");
		}
		result = bo.updateHolidays(sysEtoken, xml);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新年假天数结束。");
		}
		return result;
	}
	
	/**
	 * 获取所有机构信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @return
	 */
	
	/*  基本不用 屏蔽 wangrd 20190528
	public String getAllOrg(String sysEtoken) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构信息:");
		}
		result = bo.getAllOrg(sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构信息结束。");
		}
		return result;
	}
	*/
	
	/**
	 * 获取有变动（新增、删除、更新）的机构信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param flag 视图表状态标识，传递空串时默认为”flag”
	 * @return
	 */
	/*  基本不用 屏蔽 wangrd 20190528
	public String getChangeOrg(String sysEtoken, String flag) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息:");
		}
		result = bo.getChangeOrg(sysEtoken, flag);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息结束。");
		}
		return result;
	}
	*/
	
	
	/**
	 * 获取所有人员信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @return
	 */
	/*  基本不用 屏蔽 wangrd 20190528
	public String getAllUser(String sysEtoken) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员信息:");
		}
		result = bo.getAllUser(sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员信息结束。");
		}
		return result;
	}
	*/
	
	/**
	 * 获取有变动（新增、删除、更新）的人员信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param flag 视图表状态标识，传递空串时默认为”flag”
	 * @return
	 */
	/*  基本不用 屏蔽 wangrd 20190528
	public String getChangeUser(String sysEtoken, String flag) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息:");
		}
		result = bo.getChangeUser(sysEtoken, flag);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息结束。");
		}
		return result;
	}
	*/
	
	/**
	 * 获取所有岗位信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @return
	 */
/*基本不用 屏蔽 wangrd 20190528
  	public String getAllPost(String sysEtoken) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位信息:");
		}
		result = bo.getAllPost(sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位信息结束。");
		}
		return result;
	}*/
	
	/**
	 * 获取有变动（新增、删除、更新）的岗位信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param flag 视图表状态标识，传递空串时默认为”flag”
	 * @return
	 */
/*  基本不用 屏蔽 wangrd 20190528
 * 	public String getChangePost(String sysEtoken, String flag) {
		String result = "";
		SoapServiceBo bo = new SoapServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位变动信息:");
		}
		result = bo.getChangePost(sysEtoken, flag);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位变动信息结束。");
		}
		return result;
	}*/
	
}
