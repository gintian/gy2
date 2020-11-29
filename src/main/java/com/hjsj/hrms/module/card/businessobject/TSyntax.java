/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.module.card.businessobject;

import com.hjsj.hrms.valueobject.ykcard.StrResultView;
import com.hjsj.hrms.valueobject.ykcard.TNameView;
import com.hjsj.hrms.valueobject.ykcard.TTokenView;
import com.hjsj.hrms.valueobject.ykcard.TconstView;

import java.util.ArrayList;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TSyntax {
		//private static int INVALIDABC = 1; //无效的字符。
		private static int PLUSSY = 1; //'+'
		private static int MINUSSY = 2; //'-'
		private static int TIMESSY = 3; //'*'
		private static int DIVISY = 4; //'/'
		private static int LPARASY = 10; //'('
		private static int RPARASY = 11; //')'
		private static int IDEN = 20; //变量标识符
		//private static int STRCONST = 100; //字符串常量
		private static int NUMCONST = 105; //数值常量
		//private static int DATECONST = 107; //日期常量
		//private static int BOOLCONST = 110; //布尔常量
		public ArrayList m_tokelist = new ArrayList();
		public ArrayList m_constlist = new ArrayList();
		public ArrayList m_namelist = new ArrayList();
		private TTokenView m_curToken;
		private String numstr = "0123456789";
		private char old_ctype;
		private ArrayList bracketList = new ArrayList();//主要对左右括号进行记录，判断表达式是否正确
		int m_nToken;
		public String m_strResult;
		public TSyntax() {
		}
		public boolean Lexical(String m_strofDes) {
			boolean lexical;
			int nlen, nI, nJ;
			char ctype;
			TTokenView token;
			boolean bFlag;
			nlen = m_strofDes.length();
			bFlag = false;
			nI = 0;
			while (nI < nlen) {
				ctype = m_strofDes.toLowerCase().charAt(nI);
				//空格、回车疾换行#13#10
				switch (ctype) {
					case ' ' :
						{
							nI += 1;
							break;
						}
					case '(' : //左括号
						{
							token = new TTokenView();
							token.setNval(0);
							token.setNstyle(LPARASY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case ')' :
						{
							token = new TTokenView();
							token.setNval(0);
							token.setNstyle(RPARASY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case '+' :
						{
							token = new TTokenView ();
							token.setNval(0);
							token.setNstyle(PLUSSY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case '-' :
						{
							token = new TTokenView();
							token.setNval(0);
							token.setNstyle(MINUSSY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case '*' :
						{
							token = new TTokenView();
							token.setNval(0);
							token.setNstyle(TIMESSY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case '/' :
						{
							token = new TTokenView();
							token.setNval(0);
							token.setNstyle(DIVISY);
							m_tokelist.add(token);
							nI += 1;
							break;
						}
					case '0' :
					case '1' :
					case '2' :
					case '3' :
					case '4' :
					case '5' :
					case '6' :
					case '7' :
					case '8' :
					case '9' :
						{
							bFlag = false;
							for (nJ = nI; nJ < nlen; nJ++) {
								if (numstr.indexOf(m_strofDes.substring(nJ, nJ + 1))== -1) {
									if (m_strofDes.charAt(nJ) == '.' && (!bFlag)) {
										bFlag = true;
									} else {
										bFlag = false;
										break;
									}
								}

							}
							token = new TTokenView();
							token.setNval(FindConstList(m_strofDes.substring(nI, nJ)));
							token.setNstyle(NUMCONST);
							m_tokelist.add(token);
							nI = nJ;
							break;
						}
						/*case 'A'..'Z' ,'a'..'z'
						 * */
					case '@' : //@
						{
							if (nI == nlen) {
								lexical = false;
								nI += 1;
								break;
							}
							if (numstr.indexOf(m_strofDes.substring(nI + 1, nI + 2))!= -1) {
								for (nJ = nI + 1; nJ < nlen; nJ++) {
									if (numstr.indexOf(m_strofDes.substring(nJ, nJ + 1))!= -1)
										continue;
									else
										break;
								}
							} else {
								lexical = false;
								nI += 2;
								return lexical;
							}
							token = new TTokenView();
							token.setNval(FindVarName(m_strofDes.substring(nI, nJ)));
							token.setNstyle(IDEN);
							m_tokelist.add(token);
							nI = nJ;
							break;
						}
					default :
					{
						lexical = false;
						return lexical;
					}
				}
				lexical = this.checkFormula(ctype,nI,nlen);
				
				if(!lexical)
					return false;
				
				old_ctype = ctype;
			}
		   return true;
		}
		
		/**
		 * 进行判断这里进行（*，*），*+的判断，和左右括号的判断，是否对齐等
		 * @param ctype当前char
		 * @param nI最后一个长度
		 * @param nlen总长度
		 * @return
		 */
		private boolean checkFormula(char ctype, int nI,int nlen) {
			String needCheckType1 = "/,*,+,-";//*:且，+或
			String leftBracket = "(";
			String rightBracket = ")";
			boolean flag = true;
			//这里  (*，*)，*+，() 对这四种情况进行判断，这四种情况肯定是表达式不正确了
			if((needCheckType1.indexOf(old_ctype) != -1 && needCheckType1.indexOf(ctype) != -1)
					|| (leftBracket.indexOf(old_ctype) != -1 && needCheckType1.indexOf(ctype) != -1)
					|| (needCheckType1.indexOf(old_ctype) != -1 && rightBracket.indexOf(ctype) != -1)
					|| (leftBracket.indexOf(old_ctype) != -1 && rightBracket.indexOf(ctype) != -1)) {
				flag = false;
			}else if(leftBracket.equals(String.valueOf(ctype))) {//每有一个左括号，这样就能在集合里面添加一个，这样和右括号就能对应
				bracketList.add(ctype);
			}else if(rightBracket.equals(String.valueOf(ctype))) {
				if(bracketList.size() > 0) {//每有一个左括号就应该有一个右括号对应，这样最后得到的list.size()==0
					bracketList.remove(bracketList.size()-1);
				}else {
					flag = false;
				}
			}
			
			if(nI == nlen && bracketList.size() != 0) {//如果结束了，并且左右括号没有正确的对应，报错
				flag = false;
			}
			
			return flag;
		}
		
		public int FindConstList(String cConst) {
			int findconstlist = 0;
			int nI;
			TconstView constview1, constview2;
			boolean bflag;
			constview1 = new TconstView();
			constview2 = new TconstView();
			bflag = false;
			if (m_constlist.size() > 0) {
				for (nI = 0; nI < m_constlist.size(); nI++) {
					constview1 = (TconstView) m_constlist.get(nI);
					if (cConst == constview1.getCvalue()) {
						bflag = true;
						break;
					}
				}
				if (bflag) {
					findconstlist = nI;
				} else {
					constview2.setCvalue(cConst);
					m_constlist.add(constview2);
					findconstlist = nI;
				}
			} else {
				constview1.setCvalue(cConst);
				m_constlist.add(constview1);
				findconstlist = 0;
			}
			return findconstlist;
		}
//		public String Fisls(String sss){
//			String ss="";
//			return ss;
//			
//		}

		public int FindVarName(String cVar) {
			int nI;
			int findvarname = 0;
			TNameView nameview, nameview1;
			boolean bFlag;
			nameview = new TNameView();
			nameview1 = new TNameView();
			bFlag = false;
			if (m_namelist.size() > 0) {
				for (nI = 0; nI < m_namelist.size(); nI++) {
					nameview = (TNameView) m_namelist.get(nI);
					if (cVar == nameview.getCvalue()) {
						bFlag = true;
						break;
					}
				}
				if (bFlag) {
					findvarname = nI;
				} else {
					nameview1.setCname(cVar);
					m_namelist.add(nameview1);
					findvarname = nI;
				}
			} else {
				nameview.setCname(cVar);
				m_namelist.add(nameview);
				findvarname = 0;
			}
			return findvarname;
		}
		public void SetVariableValue(ArrayList fvalue) {
			int nI, nlen, nK;
			String cNo;
			TNameView vName;
			TRecParamView trecparam;
			ArrayList list=new ArrayList();
			boolean is=false;
			for (nI = 0; nI < m_namelist.size(); nI++) {
				vName = (TNameView) m_namelist.get(nI);
				cNo = vName.getCname();
				nlen = cNo.length();
				cNo = cNo.substring(1, nlen);
				nK = Integer.parseInt(cNo);				
				is=false;
				for (int j = 0; j < fvalue.size(); j++) {
					trecparam = (TRecParamView) fvalue.get(j);				
					if (trecparam.getNid() == nK && trecparam.isBflag()) {
						vName.setCvalue(trecparam.getFvalue());
						vName.setBflag(true);						
						m_namelist.set(nI, vName);						
						is=true;
						break;						
					}
				}				
			}			
		}
		public boolean DoWithProgram() {
			int nType = 0;
			m_nToken = -1;
			m_strResult = "";		
			m_strResult = DoWithFormula(nType);
			return true;
		}
		public boolean GetNextToken() {
			boolean result;
			m_nToken = m_nToken + 1;
			if (m_tokelist.size() > 0) {
				if (m_nToken < m_tokelist.size())
					result = true;
				else
					result = false;
			} else {
				result = false;
			}
			return result;
		}
		public String DoWithFormula(int nType) {
			boolean result;
			String strResult = "";
			int nop1, nop2;
			if (!GetNextToken()) {
				result = false;
				return strResult;
			}
			m_curToken = (TTokenView) m_tokelist.get(m_nToken);
			nop1 = m_curToken.getNstyle();
			StrResultView returnStr = DoWithTerms(nop1, strResult);
			nop1 = returnStr.getNtype();
			strResult = returnStr.getStrresult();

			if (!returnStr.isBl()) //表达式运算结束
				{
				result = false;
				//return strResult;                  //返回空好像不对		                 
			} else {
				if (nop1 == LPARASY && GetNextToken()) {
					//Application;
					result = false;
					return strResult;
				}
				if (nop1 == RPARASY) {
					//Application
					result = false;
					return strResult;
				}
				result = true;
				return strResult;
			}
			return strResult;
		}

		//===Add or Subtract two terms. Parament nType :符号类型strResult:还回结果
		public StrResultView DoWithTerms(int nType, String strResult) {
			boolean result;
			String strValue = "";
			int nop, nop1;
			StrResultView returnStr = DoWithFactors(nType, strResult);			
			strResult = returnStr.getStrresult();
			nType = returnStr.getNtype();
			if (!returnStr.isBl()) {
				result = false;
				returnStr.setBl(result);
				returnStr.setNtype(nType);
				returnStr.setStrresult(strResult);
				return returnStr;
			}
			if (m_nToken < m_tokelist.size()) {
				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nType = m_curToken.getNstyle();
				nop = nType;
			} else {
				result = false;
				returnStr.setBl(result);
				returnStr.setNtype(nType);
				returnStr.setStrresult(strResult);
				return returnStr;
			}
			while (nop == PLUSSY || nop == MINUSSY) {
				if (!GetNextToken()) {
					//Application;
					result = false;
					returnStr.setBl(result);
					returnStr.setNtype(nType);
					returnStr.setStrresult(strResult);
					return returnStr;
				}
				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nop1 = m_curToken.getNstyle();

				StrResultView returnStr1 = DoWithFactors(nop1, strValue);
				nop1 = returnStr1.getNtype();
				strValue = returnStr1.getStrresult();

				if (returnStr1.isBl()) {					
					StrResultView returnStrsub = Arith(nop, strResult, strValue);
					nop = returnStrsub.getNtype();
					strResult = returnStrsub.getStrresult();                    
					if (!returnStrsub.isBl()) {
						returnStr.setBl(false);
						returnStr.setNtype(nType);
						returnStr.setStrresult(strResult);
						return returnStr;
					}

					if (m_nToken < m_tokelist.size()) {
						m_curToken = (TTokenView) m_tokelist.get(m_nToken);
						nop1 = m_curToken.getNstyle();
					}
					nop = nop1;
					nType = nop;
					result = true;
				} else {
					result = false;
					returnStr.setBl(result);
					returnStr.setNtype(nType);
					returnStr.setStrresult(strResult);
					return returnStr;
				}
			}
			result = true;
			returnStr.setBl(result);
			returnStr.setNtype(nType);
			returnStr.setStrresult(strResult);
			return returnStr;

		}
		//===mutiply or divide two factors.
		public StrResultView DoWithFactors(int nType, String strResult) {
			boolean result;
			int nop1 = 0, nop2 = 0;
			String strValue = "";

			StrResultView returnStr = DoWithUnary(nType, strResult);			
			nType = returnStr.getNtype();
			strResult = returnStr.getStrresult();

			if (!returnStr.isBl()) {
				result = false;
				returnStr.setBl(result);
				returnStr.setNtype(nType);
				returnStr.setStrresult(strResult);
				return returnStr;
			}

			if (m_nToken < m_tokelist.size()) {
				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nType = m_curToken.getNstyle();
				nop1 = nType;
				//cmq changed
				if ((nop1 == 105) || (nop1 == 20)) //or(nop1==10) || (nop1==11)
					{
					//Application;
					result = false;
					returnStr.setBl(result);
					returnStr.setNtype(nType);
					returnStr.setStrresult(strResult);
					return returnStr;
				}
				//cmq end;
			}
			StrResultView st;
			StrResultView st1;
			while (nop1 == TIMESSY || nop1 == DIVISY) {
				if (!GetNextToken()) {
					//Application;
					result = false;
					returnStr.setBl(result);
					returnStr.setNtype(nType);
					returnStr.setStrresult(strResult);
					return returnStr;
				}

				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nop2 = m_curToken.getNstyle();
				st = DoWithUnary(nop2, strValue);
				nop2 = st.getNtype();
				strValue = st.getStrresult();

				if (st.isBl()) {

					st1 = Arith(nop1, strResult, strValue);
					nop1 = st1.getNtype();
					strResult = st1.getStrresult();

					///nop1=Arith(nop1,strResult,strValue);
					if (!st1.isBl()) {
						returnStr.setBl(false);
						returnStr.setNtype(nType);
						returnStr.setStrresult(strResult);
						return returnStr;
					}

					if (m_nToken < m_tokelist.size()) {
						m_curToken = (TTokenView) m_tokelist.get(m_nToken);
						nop2 = m_curToken.getNstyle();
					}
					nop1 = nop2;
					result = true;
				} else {
					result = false;
					returnStr.setBl(result);
					returnStr.setNtype(nType);
					returnStr.setStrresult(strResult);
					return returnStr;
				}

			}
			result = true;
			returnStr.setBl(result);
			returnStr.setNtype(nType);
			returnStr.setStrresult(strResult);
			return returnStr;
		}
		public StrResultView DoWithUnary(int nType, String strResult) {
			boolean result;
			int nop, nop1;
			//String strValue = "";
			nop = 0;
			m_curToken = (TTokenView) m_tokelist.get(m_nToken);
			nop1 = m_curToken.getNstyle();
			StrResultView st = new StrResultView();
			StrResultView st1;

			if ((nop1 == PLUSSY) || (nop1 == MINUSSY)) {
				nop = nop1;
				if (!GetNextToken()) {
					//Application.MessageBox(PCh;
					result = false;
					st.setBl(result);
					st.setNtype(nType);
					st.setStrresult(strResult);
					return st;
					//return strResult;
				}

				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nop1 = m_curToken.getNstyle();
			}			
			st1 = DoWithExpr(nop1, strResult);
			nop1 = st1.getNtype();
			strResult = st1.getStrresult();		
			if (st1.isBl()) {
				if (nop > 0) {
					///Unary过程的开始部分
					if (nop == MINUSSY) {
						switch (strResult.toLowerCase().charAt(0)) {
							case '-' :
								{
									strResult =
										strResult.substring(2, strResult.length());
									strResult = "-" + strResult;
									break;
								}
							case '+' :
								{
									strResult =
										strResult.substring(2, strResult.length());
									strResult = "-" + strResult;
									break;
								}
							default :
								{
									strResult = "-" + strResult;
								}
						}
						nType = 0;

					}
					if (nop == PLUSSY) {
						strResult = "+" + strResult;
						nType = 0;
					}
					////Unary过程的结束部分
					///// nType=nop;
				}
				result = true;
			} else {
				result = false;
			}
			st.setBl(result);
			st.setNtype(nType);
			st.setStrresult(strResult);
			return st;
		}
		//process parenthesized expression 处理（Expresion）
		public StrResultView DoWithExpr(int nType, String strResult) {
			boolean result;
			int nop;
			StrResultView st = new StrResultView();
			if (nType == LPARASY) {
				if (!GetNextToken()) {
					//Application.messagebox
					result = false;
					st.setBl(result);
					st.setNtype(nType);
					st.setStrresult(strResult);
					return st;
					//return strResult;
				}

				m_curToken = (TTokenView) m_tokelist.get(m_nToken);
				nop = m_curToken.getNstyle();				
				StrResultView st1 = DoWithTerms(nop, strResult);
				nop = st1.getNtype();
				strResult = st1.getStrresult();				
				if (!st1.isBl()) {
					result = false;
					st.setBl(result);
					st.setNtype(nType);
					st.setStrresult(strResult);
					return st;
				}

				if (m_nToken < m_tokelist.size()) {
					m_curToken = (TTokenView) m_tokelist.get(m_nToken);
					nop = m_curToken.getNstyle();
				} else {
					//Application.messagebox;
					result = false;
					st.setBl(result);
					st.setNtype(nType);
					st.setStrresult(strResult);
					return st;
				}
				if (nop != RPARASY) {
					//Application.;
					result = false;
					st.setBl(result);
					st.setNtype(nType);
					st.setStrresult(strResult);
					return st;
				}
				GetNextToken();
				result = true;
			} else {
				//DoWithExpr_bl=primitive(m_nToken,strResult);
				
				st = primitive(m_nToken, strResult);
				strResult = st.getStrresult();				
				result = st.isBl();
			}
			st.setBl(result);
			st.setNtype(nType);
			st.setStrresult(strResult);			
			return st;
		}
		public StrResultView primitive(int nType, String strResult) {
			StrResultView st = new StrResultView();
			boolean result;
			int nStyle, nV;
			TconstView constV;
			TNameView nameV;
			m_curToken = (TTokenView) m_tokelist.get(nType);
			nStyle = m_curToken.getNstyle();
			nV = m_curToken.getNval();			
			switch (nStyle) {
				case 20 :
					{
						nameV = new TNameView();
						nameV = (TNameView) m_namelist.get(nV);
						if (nameV.isBflag()) {
							strResult = nameV.getCvalue();								
							result = true;
						} else {
							//Application;
							result = false;
						}
						GetNextToken();
						break;
					}
				case 105 :
					{
						constV = (TconstView) m_constlist.get(nV);
						strResult = constV.getCvalue();
						GetNextToken();
						result = true;
						break;
					}
				default :
					{
						//Application;
						result = false;
					}
			}
			st.setNtype(nType);
			st.setStrresult(strResult);
			st.setBl(result);
			return st;
		}
		public StrResultView Arith(int nType, String strResult, String strSec) {
			boolean result;
			StrResultView st = new StrResultView();
			double fV1, fV2;
			fV1 = Double.parseDouble(strResult);
			fV2 = Double.parseDouble(strSec);			
			switch (nType) {
				case 1 :
					{
						fV1 = fV1 + fV2;
						strResult = String.valueOf(fV1);
						break;
					}
				case 2 :
					{
						fV1 = fV1 - fV2;
						strResult = String.valueOf(fV1);
						break;
					}
				case 3 :
					{

						fV1 = fV1 * fV2;
						strResult = String.valueOf(fV1);
						break;
					}
				case 4 :
					{
						try {
							if (fV2 != 0.0) {
								fV1 = fV1 / fV2;
							}
							else {
							    fV1=0;// 被除数为0，则返回0 wangrd  2013-01-04
							}
							
						} catch (Exception e) {
							//Application;
							result = false;
							st.setBl(result);
							st.setNtype(nType);
							st.setStrresult(strResult);
							return st;
						}
						strResult = String.valueOf(fV1);
						break;
					}
			}			
			result = true;
			st.setBl(result);
			st.setNtype(nType);
			st.setStrresult(strResult);
			return st;
		}
}
