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

import java.util.ArrayList;

/**
 * Title:保存需求采集表添加的明细 
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SaveAddInfoPickDetailTrans extends IBusiness {

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo = (RecordVo) this.getFormHM().get("infoPickDetailov");
		if (vo == null) {
			return;
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {

			String flag = (String) this.getFormHM().get("judge");
			/**
			 * 添加需求采集明细
			 */
			if ("3".equals(flag)) {

				//	取得需求采集表id
				
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String R1901 = this.getFormHM().get("r19id").toString();
				
				/**
				 * 需求调查表明细添加操作
				 */
				
				String r22id = "0";
				r22id = idg.getId("R22.R2202");
				ArrayList infoDetailAddList=(ArrayList)this.getFormHM().get("infoDetailAddList");
				StringBuffer r22sb=new StringBuffer();
				
				
				/*
				vo.setString("r2201",R1901);
				vo.setString("r2202",  r22id);
				vo.setString("r2205", first_date.getDataStringToDate());
				dao.addValueObject(vo);
				*/
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
					for(int i=0;i<infoDetailAddList.size();i++)
					{
						BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(i);
						if(i==infoDetailAddList.size()-1)
						{
							if("N".equals(bsb.itemtype))
							{
								r22sb.append(bsb.getValue());
								r22sb.append(")");
							}
							else
							{
								if("D".equals(bsb.itemtype))
								{
									r22sb.append(Sql_switcher.dateValue(bsb.getValue()));
									r22sb.append(")");
//									r22sb.append("'");
//									r22sb.append(PubFunc.FormatDate(bsb.getValue()));
//									r22sb.append("')");
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
//				 num=st.executeUpdate(r22sb.toString());
//				 if(st!=null)st.close();
				 /**
				  * 重新清value属性的值
				  */
				 DoCodeBean addlist=new DoCodeBean();
				 this.getFormHM().put("infoDetailAddList",addlist.getDynamicList(this.getFrameconn(),1));
			}
			/**
			 * 修改需求采集明细
			 */
			if ("4".equals(flag)) {
				
				ArrayList infoDetailAddList=(ArrayList)this.getFormHM().get("infoDetailAddList");
				String r2206=vo.getString("r2206");
				StringBuffer sb=new StringBuffer();
				if(infoDetailAddList.size()<=0)
				{
					sb.append("update R22 set R2206=");
					sb.append(r2206);
					sb.append(" where R2202='");
					sb.append(vo.getString("r2202"));
					sb.append("'");
				}
				else
				{
					sb.append("update R22 set R2206=");
					sb.append(r2206);
					sb.append(",");
					for(int i=0;i<infoDetailAddList.size();i++)
					{
						BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(i);
						if(i==infoDetailAddList.size()-1)
						{
							if("N".equals(bsb.getItemtype()))
							{
								sb.append(bsb.getItemid());
								sb.append("=");
								sb.append(PubFunc.NullToZero(bsb.getValue()));
							}
							else
							{
								if("D".equals(bsb.getItemtype()))
								{
									sb.append(bsb.getItemid());
									sb.append("=");
									sb.append(Sql_switcher.dateValue(bsb.getValue()));
//									sb.append("='");
//									sb.append(PubFunc.FormatDate(bsb.getValue()));
//									sb.append("'");
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										sb.append(bsb.getItemid());
										sb.append("='");
										sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										sb.append("'");
									}
									else
									{
										sb.append(bsb.getItemid());
										sb.append("='");
										sb.append(bsb.getValue());
										sb.append("'");
									}
								}
							}
						}
						else
						{
							if("N".equals(bsb.getItemtype()))
							{
								sb.append(bsb.getItemid());
								sb.append("=");
								sb.append(PubFunc.NullToZero(bsb.getValue()));
								sb.append(",");
							}
							else
							{
								if("D".equals(bsb.getItemtype()))
								{
									sb.append(bsb.getItemid());
//									sb.append("='");
//									sb.append(PubFunc.FormatDate(bsb.getValue()));
//									sb.append("',");
									sb.append("=");
									sb.append(Sql_switcher.dateValue(bsb.getValue()));
									sb.append(",");									
								}
								else
								{
									if("0".equals(bsb.getCodesetid()))
									{
										sb.append(bsb.getItemid());
										sb.append("='");
										sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
										sb.append("',");
										
									}
									else
									{
										sb.append(bsb.getItemid());
										sb.append("='");
										sb.append(bsb.getValue());
										sb.append("',");
									}
								
								}
							}
						}
					}
					sb.append(" where R2202='");
					sb.append(vo.getString("r2202"));
					sb.append("'");
				}
				dao.update(sb.toString());
				 /**
				  * 重新清value属性的值
				  */
				 DoCodeBean addlist=new DoCodeBean();
				 this.getFormHM().put("infoDetailAddList",addlist.getDynamicList(this.getFrameconn(),1));
				 this.getFormHM().put("judge","3");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		} finally {
			((RecordVo)this.getFormHM().get("infoPickDetailTb")).clearValues();
			this.getFormHM().put("first_date",new DateStyle());
		}

	}

}