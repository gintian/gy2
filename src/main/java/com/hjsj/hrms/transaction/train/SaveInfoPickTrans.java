package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * <p>
 * Title:需求采集表第一次添加搜索与修改
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-13:17:01:52
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SaveInfoPickTrans extends IBusiness {
	
	ArrayList dynamicColDetail=new ArrayList();
	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo = (RecordVo) this.getFormHM().get("infoPickDetailTb");
		
		/**
		 * 得到明细添加动态字段
		 */
		 DoCodeBean addlist=new DoCodeBean();
		// this.getFormHM().put("infoAddList",addlist.getDynamicList(this.getFrameconn()));
		 ArrayList listtemp=addlist.getDynamicList(this.getFrameconn(),1);
		
		 /**
		  * 明细列表动态列
		  */
		 this.getFormHM().put("dynamicColDetail",listtemp);
			
		 dynamicColDetail=listtemp;
			

		if (vo == null) {

			return;
		}
		String flag = (String) this.getFormHM().get("judge");

		PreparedStatement ps = null;
		Connection con = null;

		if ("1".equals(flag))
		{

			/**
			 * 添加页面进行保存处理
			 */
			try {
				/**
				 * 取得需求调查表id
				 */
				ContentDAO dao = new ContentDAO(this.getFrameconn());

				String R16Id="";
				if(this.getFormHM().get("investigate")!=null)
				{
					R16Id=this.getFormHM().get("investigate").toString();
				}
				String factNum="0";
				if(this.getFormHM().get("factNum")!=null && !"".equals(this.getFormHM().get("factNum").toString()))
				{
					factNum=this.getFormHM().get("factNum").toString();
				}
				
				String pickTableName="";
				if(this.getFormHM().get("pickTableName")!=null)
				{
					pickTableName=this.getFormHM().get("pickTableName").toString();
				}
				//	取得需求采集表id
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String R1901 = idg.getId("R19.R1901");
				/**
				 * 需求调查表添加操作
				 */
				ArrayList infoAddList=(ArrayList)this.getFormHM().get("infoAddList");
				StringBuffer r19sb = new StringBuffer();
				if(infoAddList.size()<=0)
				{
					r19sb.append("insert into R19 (R1901,E0122,R1910,R1902,B0110,R1906,R1907,R1908,R1909) values(");
					r19sb.append("'");
					r19sb.append(R1901);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(PubFunc.nullToStr(this.userView.getUserDeptId()));
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(pickTableName);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(R16Id);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(this.userView.getUserOrgId());
					r19sb.append("',");
					r19sb.append("");
					r19sb.append(Sql_switcher.dateValue(DateStyle.getSystemTime()));
					r19sb.append(",");
					r19sb.append("'");
					r19sb.append("02");
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append("01");
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(this.userView.getUserFullName());
					r19sb.append("')");
				}
				else
				{
					r19sb.append("insert into R19 (R1901,E0122,R1910,R1902,B0110,R1906,R1907,R1908,R1909,");
					for(int i=0;i<infoAddList.size();i++)
					{
						BusifieldBean bsb=(BusifieldBean)infoAddList.get(i);
						if(i==infoAddList.size()-1)
						{
							r19sb.append(bsb.getItemid());
						}
						else
						{
							r19sb.append(bsb.getItemid());
							r19sb.append(",");
						}
					}
					r19sb.append(" ) values(");
					r19sb.append("'");
					r19sb.append(R1901);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(PubFunc.nullToStr(this.userView.getUserDeptId()));
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(pickTableName);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(R16Id);
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(this.userView.getUserOrgId());
					r19sb.append("',");
					r19sb.append("");
					r19sb.append(Sql_switcher.dateValue(DateStyle.getSystemTime()));
					r19sb.append(",");
					r19sb.append("'");
					r19sb.append("02");
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append("01");
					r19sb.append("',");
					r19sb.append("'");
					r19sb.append(this.userView.getUserFullName());
					r19sb.append("',");
					
					for(int i=0;i<infoAddList.size();i++)
					{
						BusifieldBean bsb=(BusifieldBean)infoAddList.get(i);
						if(i==infoAddList.size()-1)
						{
							if("N".equals(bsb.itemtype))
							{
								r19sb.append(PubFunc.NullToZero(bsb.getValue()));
								r19sb.append(")");
							}
							else
							{
								if("D".equals(bsb.itemtype))
								{
//									r19sb.append("'");
//									r19sb.append(PubFunc.FormatDate(bsb.getValue()));
//									r19sb.append("')");
									r19sb.append(Sql_switcher.dateValue(bsb.getValue()));
									r19sb.append(")");
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										r19sb.append("'");
										r19sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										r19sb.append("')");
									}
									else
									{
										r19sb.append("'");
										r19sb.append(bsb.getValue());
										r19sb.append("')");
									}
								}
							}
						}
						else
						{
							if("N".equals(bsb.itemtype))
							{
								r19sb.append(PubFunc.NullToZero(bsb.getValue()));
								r19sb.append(",");
							}
							else
							{
								if("D".equals(bsb.itemtype))
								{
//									r19sb.append("'");
//									r19sb.append(PubFunc.FormatDate(bsb.getValue()));
//									r19sb.append("',");
									r19sb.append(Sql_switcher.dateValue(bsb.getValue()));
									r19sb.append(",");
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										r19sb.append("'");
										r19sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										r19sb.append("',");
									}
									else
									{
										r19sb.append("'");
										r19sb.append(bsb.getValue());
										r19sb.append("',");
									}
								}
							}
						}
					}
				}
//				con = this.getFrameconn();
//				Statement st=con.createStatement();
//				st.executeUpdate(r19sb.toString());
				dao.update(r19sb.toString());
				/**
				 * 需求调查表明细添加操作
				 */
				String r22id="0";
				r22id=idg.getId("R22.R2202");
				ArrayList infoDetailAddList=(ArrayList)this.getFormHM().get("infoDetailAddList");
				StringBuffer r22sb=new StringBuffer();
				if(infoDetailAddList.size()<=0)
				{
					r22sb.append("insert into R22( R2201,R2202,R2206) values('");
					r22sb.append(R1901);
					r22sb.append("',");
					r22sb.append("'");
					r22sb.append(r22id);
					r22sb.append("',");
					r22sb.append("");
					r22sb.append(vo.getString("r2206"));
					r22sb.append(")");
				}
				else
				{
					r22sb.append("insert into R22( R2201,R2202,R2206,");
					for(int i=0;i<infoDetailAddList.size();i++)
					{
						BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(i);
						if(i==infoDetailAddList.size()-1)
						{
							r22sb.append(bsb.getItemid());
						}
						else
						{
							r22sb.append(bsb.getItemid());
							r22sb.append(",");
						}
					}
					r22sb.append(") values('");
					r22sb.append(R1901);
					r22sb.append("',");
					r22sb.append("'");
					r22sb.append(r22id);
					r22sb.append("',");
					r22sb.append("");
					r22sb.append(vo.getString("r2206"));
					r22sb.append(",");
					for(int j=0;j<infoDetailAddList.size();j++)
					{
						BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(j);
						if(j==infoDetailAddList.size()-1)
						{
							if("N".equals(bsb.itemtype))
							{
								r22sb.append(PubFunc.NullToZero(bsb.getValue()));
								r22sb.append(")");
							}
							else
							{
								if("D".equals(bsb.itemtype))
								{
//									r22sb.append("'");
//									r22sb.append(PubFunc.FormatDate(bsb.getValue()));
//									r22sb.append("')");
									r22sb.append(Sql_switcher.dateValue(bsb.getValue()));
									r22sb.append(")");
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										r22sb.append("'");
										r22sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										r22sb.append("')");
									}
									else
									{
										r22sb.append("'");
										r22sb.append(bsb.getValue());
										r22sb.append("')");
									}
								}
							}
						}
						else
						{
							if("N".equals(bsb.itemtype))
							{
								r22sb.append(PubFunc.NullToZero(bsb.getValue()));
								r22sb.append(",");
							}
							else
							{
								if("D".equals(bsb.itemtype))
								{
//									r22sb.append("'");
//									r22sb.append(PubFunc.FormatDate(bsb.getValue()));
//									r22sb.append("',");
									r22sb.append(Sql_switcher.dateValue(bsb.getValue()));
									r22sb.append(",");
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										r22sb.append("'");
										r22sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										r22sb.append("',");
									}
									else
									{
										r22sb.append("'");
										r22sb.append(bsb.getValue());
										r22sb.append("',");
									}
								}
							}
						}
					}
				}
				int num=0;
				num=dao.update(r22sb.toString());
//				num=st.executeUpdate(r22sb.toString());
				 /**
				  * 重新清value属性的值
				  */
				 DoCodeBean addlist2=new DoCodeBean();
				 this.getFormHM().put("infoDetailAddList",addlist2.getDynamicList(this.getFrameconn(),1));
				/**
				 * 添加成功
				 */
				if(num>0)
				{
					this.getFormHM().put("firstFlag","0");  //第一次进入标识
					this.getFormHM().put("r19id",R1901);
					this.getFormHM().put("newr19id",R1901);
					((RecordVo)this.getFormHM().get("infoPickDetailTb")).clearValues();
					this.getFormHM().put("first_date",new DateStyle());
					doDailList(R1901);
					this.getFormHM().put("pickTableName","");
				    this.getFormHM().put("factNum","");
				}
				else
				{
					this.getFormHM().put("firstFlag","1");
					this.getFormHM().put("r19id","0");
					this.getFormHM().put("pickInfoDetaillst", new ArrayList());
					((RecordVo)this.getFormHM().get("infoPickDetailTb")).clearValues();
					this.getFormHM().put("first_date",new DateStyle());
					this.getFormHM().put("pickTableName","");
				    this.getFormHM().put("factNum","");
				}
				
			} catch (Exception sqle) {
				this.getFormHM().put("pickInfoDetaillst", new ArrayList());
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			}
		}
	}
	/**
	 * 得到明细列表
	 * @param r19id
	 */
	public void doDailList(String r19id) throws GeneralException 
	{
			String sql = "select * from R22 where R2201='"+r19id+"'";
			StringBuffer strsql = new StringBuffer();
			strsql.append(sql);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList list = new ArrayList();
			try {
				  this.frowset = dao.search(strsql.toString());
				/*
				 * 得到采集表对象列表
				 */
				 /**
			       * 判断是否是存在列
			       */
			      ArrayList lst=new ArrayList();
			      for(int i=0;i<dynamicColDetail.size();i++)
		          {
		          	BusifieldBean busb=(BusifieldBean)dynamicColDetail.get(i);
		          	if(this.getFrowset().findColumn(busb.getItemid())>0)
		          	{
		          		lst.add(busb);
		          	}
		          }
			      dynamicColDetail=lst;
				
				  while (this.frowset.next()) {
					
					DynaBean vo=new LazyDynaBean();
					
					vo.set("r2202",PubFunc.nullToStr(this.frowset.getString("r2202")));
					vo.set("r2201",PubFunc.nullToStr(this.frowset.getString("r2201")));
					vo.set("r2206",PubFunc.nullToStr(this.frowset.getString("r2206")));
					
					for(int i=0;i<dynamicColDetail.size();i++)
			          {
			          	BusifieldBean busb=(BusifieldBean)dynamicColDetail.get(i);
			        	if("D".equals(busb.getItemtype()))
			      		{
			        		vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid())));
			      		}
			        	else
			        	{
			        		vo.set(busb.getItemid(),PubFunc.nullToStr(this.getFrowset().getString(busb.getItemid())));
			        	}
			          }			  	
					list.add(vo);
				}
				this.getFormHM().put("pickInfoDetaillst", list);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);				
			} 
		}

}