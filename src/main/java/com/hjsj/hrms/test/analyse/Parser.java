package com.hjsj.hrms.test.analyse;

import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 此处插入类型说明。 创建日期：(2006-6-26 14:13:03)
 * 
 * @author：周海茂
 */
class Parser extends java.awt.Frame {

	YksjParser yp = null;

	private Connection conn = null;

	private java.awt.Panel ivjContentsPane = null;

	IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private java.awt.Label ivjLabel1 = null;

	private java.awt.Label ivjLabel2 = null;

	private java.awt.Label ivjLabel3 = null;

	private java.awt.Label ivjLabel4 = null;

	private java.awt.Label ivjLabel5 = null;

	private java.awt.TextArea ivjTextAreaSQL = null;

	private java.awt.TextArea ivjTextAreaSQLS = null;

	private java.awt.TextArea ivjTextAreaUsedFields = null;

	private java.awt.TextArea ivjTextAreaUsedSets = null;

	private java.awt.Button ivjButtonTest = null;

	private java.awt.Choice ivjChoiceDataType = null;

	private java.awt.Choice ivjChoiceDB = null;

	private java.awt.Choice ivjChoiceObject = null;

	private java.awt.Choice ivjChoiceFieldItem = null;

	private java.awt.TextArea ivjTextAreaFSource = null;

	private java.awt.Choice ivjChoiceObjectNormal = null;

	private java.awt.Label ivjLabel6 = null;

	private java.awt.TextField ivjTextFieldReturn = null;

	private java.awt.TextArea ivjTextAreaError = null;

	class IvjEventHandler implements java.awt.event.ItemListener,
			java.awt.event.MouseListener, java.awt.event.WindowListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == Parser.this.getChoiceDB()) {
				connEtoC2(e);
			}
			if (e.getSource() == Parser.this.getChoiceObject())
				connEtoC3(e);
			if (e.getSource() == Parser.this.getChoiceDataType())
				connEtoC4(e);
			if (e.getSource() == Parser.this.getChoiceFieldItem())
				connEtoC5(e);
		};

		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == Parser.this.getButtonTest())
				connEtoC7(e);
		};

		public void mouseEntered(java.awt.event.MouseEvent e) {
		};

		public void mouseExited(java.awt.event.MouseEvent e) {
		};

		public void mousePressed(java.awt.event.MouseEvent e) {
		};

		public void mouseReleased(java.awt.event.MouseEvent e) {
		};

		public void windowActivated(java.awt.event.WindowEvent e) {
		};

		public void windowClosed(java.awt.event.WindowEvent e) {
		};

		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == Parser.this)
				connEtoC1(e);
		};

		public void windowDeactivated(java.awt.event.WindowEvent e) {
		};

		public void windowDeiconified(java.awt.event.WindowEvent e) {
		};

		public void windowIconified(java.awt.event.WindowEvent e) {
		};

		public void windowOpened(java.awt.event.WindowEvent e) {
		};
	};

	/**
	 * Parser 构造子注解。
	 */
	public Parser() {
		super();
		initialize();
	}

	/**
	 * Parser 构造子注解。
	 * 
	 * @param title
	 *            java.lang.String
	 */
	public Parser(String title) {
		super(title);
	}

	/**
	 * Comment
	 */
	public void choiceDataType_ItemStateChanged(
			java.awt.event.ItemEvent itemEvent) {
		return;
	}

	/**
	 * Comment
	 */
	public void choiceDB_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
		System.out.println(getChoiceDB().getSelectedItem());
		return;
	}

	/**
	 * Comment
	 */
	public void choiceDB_ItemStateChanged1(java.awt.event.ItemEvent itemEvent) {
		System.out.println(getChoiceDB().getSelectedItem());
		return;
	}

	/**
	 * Comment
	 */
	public void choiceFieldItem_ItemStateChanged(
			java.awt.event.ItemEvent itemEvent) {
		String strTemp = getChoiceFieldItem().getSelectedItem();
		getTextAreaFSource().append(strTemp.substring(0, strTemp.indexOf(":")));
		return;
	}

	/**
	 * Comment
	 */
	public void choiceObject_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
		return;
	}

	/**
	 * connEtoC1: (Parser.window.windowClosing(java.awt.event.WindowEvent) -->
	 * Parser.dispose()V)
	 * 
	 * @param arg1
	 *            java.awt.event.WindowEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC1(java.awt.event.WindowEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.dispose();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC2: (ChoiceDB.item.itemStateChanged(java.awt.event.ItemEvent) -->
	 * Parser.choiceDB_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * 
	 * @param arg1
	 *            java.awt.event.ItemEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC2(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.choiceDB_ItemStateChanged(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC3: (ChoiceObject.item.itemStateChanged(java.awt.event.ItemEvent)
	 * --> Parser.choiceObject_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * 
	 * @param arg1
	 *            java.awt.event.ItemEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC3(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.choiceObject_ItemStateChanged(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC4:
	 * (ChoiceDataType.item.itemStateChanged(java.awt.event.ItemEvent) -->
	 * Parser.choiceDataType_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * 
	 * @param arg1
	 *            java.awt.event.ItemEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC4(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.choiceDataType_ItemStateChanged(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC5:
	 * (ChoiceFieldItem.item.itemStateChanged(java.awt.event.ItemEvent) -->
	 * Parser.choiceFieldItem_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * 
	 * @param arg1
	 *            java.awt.event.ItemEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC5(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.choiceFieldItem_ItemStateChanged(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC7: (ButtonTest.mouse.mouseClicked(java.awt.event.MouseEvent) -->
	 * Parser.buttonTest_MouseClicked(Ljava.awt.event.MouseEvent;)V)
	 * 
	 * @param arg1
	 *            java.awt.event.MouseEvent
	 */
	/* 警告：此方法将重新生成。 */
	private void connEtoC7(java.awt.event.MouseEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.buttonTest_MouseClicked(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * 返回 Button1 特性值。
	 * 
	 * @return java.awt.Button
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Button getButtonTest() {
		if (ivjButtonTest == null) {
			try {
				ivjButtonTest = new java.awt.Button();
				ivjButtonTest.setName("ButtonTest");
				ivjButtonTest.setBounds(692, 528, 46, 20);
				ivjButtonTest.setLabel("Test");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjButtonTest;
	}

	/**
	 * 返回 Choice21 特性值。
	 * 
	 * @return java.awt.Choice
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Choice getChoiceDataType() {
		if (ivjChoiceDataType == null) {
			try {
				ivjChoiceDataType = new java.awt.Choice();
				ivjChoiceDataType.setName("ChoiceDataType");
				ivjChoiceDataType.setBounds(640, 5, 127, 40);
				// user code begin {1}
				ivjChoiceDataType.addItem("N");
				ivjChoiceDataType.addItem("A");
				ivjChoiceDataType.addItem("D");
				ivjChoiceDataType.addItem("L");
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjChoiceDataType;
	}

	/**
	 * 返回 Choice1 特性值。
	 * 
	 * @return java.awt.Choice
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Choice getChoiceDB() {
		if (ivjChoiceDB == null) {
			try {
				ivjChoiceDB = new java.awt.Choice();
				ivjChoiceDB.setName("ChoiceDB");
				ivjChoiceDB.setBounds(68, 5, 94, 40);
				// user code begin {1}

				ivjChoiceDB.addItem("SQL Server");
				ivjChoiceDB.addItem("Oracle");
				ivjChoiceDB.addItem("DB2");
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjChoiceDB;
	}

	/**
	 * 返回 Choice3 特性值。
	 * 
	 * @return java.awt.Choice
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Choice getChoiceFieldItem() {
		if (ivjChoiceFieldItem == null) {
			try {
				ivjChoiceFieldItem = new java.awt.Choice();
				ivjChoiceFieldItem.setName("ChoiceFieldItem");
				ivjChoiceFieldItem.setBounds(11, 41, 756, 36);
				// user code begin {1}
				ivjChoiceFieldItem.addItem("A0107:性别(AX)");
				ivjChoiceFieldItem.addItem("A0102:出生日期(D)");
				ivjChoiceFieldItem.addItem("A0405:学历(FD)");
				ivjChoiceFieldItem.addItem("A5508:级别工资(N)");
				ivjChoiceFieldItem.addItem("A5520:变动数(N)");
				ivjChoiceFieldItem.addItem("A0114:年龄(N)");
				ivjChoiceFieldItem.addItem("B0430:是(N)");
				ivjChoiceFieldItem.addItem("B0102:单位性质(BB)");
				ivjChoiceFieldItem.addItem("B0107:成立时间(D)");
				ivjChoiceFieldItem.addItem("B0401:拔款数(n)");
				ivjChoiceFieldItem.addItem("P0123:岗位名称(A)");
				ivjChoiceFieldItem.addItem("P0105:岗位类型(EE)");
				ivjChoiceFieldItem.addItem("P0401:岗位条件(FF)");
				ivjChoiceFieldItem.addItem("按职位统计人数");
				ivjChoiceFieldItem.addItem("A0107=\"1\":sex");
				ivjChoiceFieldItem.addItem("K0202:最高岗位工资");
				ivjChoiceFieldItem.addItem("K0201:最低岗位工资");
				ivjChoiceFieldItem
						.addItem("A0107=\"1\" and A0405=\"21\":male and bach");

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjChoiceFieldItem;
	}

	/**
	 * 返回 Choice2 特性值。
	 * 
	 * @return java.awt.Choice
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Choice getChoiceObject() {
		if (ivjChoiceObject == null) {
			try {
				ivjChoiceObject = new java.awt.Choice();
				ivjChoiceObject.setName("ChoiceObject");
				ivjChoiceObject.setBounds(454, 3, 127, 40);
				// user code begin {1}
				ivjChoiceObject.addItem("forPerson");
				ivjChoiceObject.addItem("forPosition");
				ivjChoiceObject.addItem("forDepartment");
				ivjChoiceObject.addItem("forUnit");
				ivjChoiceObject.addItem("forParty");
				ivjChoiceObject.addItem("forWorkParty");
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjChoiceObject;
	}

	/**
	 * 返回 ChoiceObjectNormal 特性值。
	 * 
	 * @return java.awt.Choice
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Choice getChoiceObjectNormal() {
		if (ivjChoiceObjectNormal == null) {
			try {
				ivjChoiceObjectNormal = new java.awt.Choice();
				ivjChoiceObjectNormal.setName("ChoiceObjectNormal");
				ivjChoiceObjectNormal.setBounds(248, 5, 127, 40);
				// user code begin {1}
				ivjChoiceObjectNormal.addItem("forNormal");
				ivjChoiceObjectNormal.addItem("forSearch");
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjChoiceObjectNormal;
	}

	/**
	 * 返回 ContentsPane 特性值。
	 * 
	 * @return java.awt.Panel
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Panel getContentsPane() {
		if (ivjContentsPane == null) {
			try {
				ivjContentsPane = new java.awt.Panel();
				ivjContentsPane.setName("ContentsPane");
				ivjContentsPane.setLayout(null);
				ivjContentsPane.setBackground(java.awt.SystemColor.scrollbar);
				getContentsPane().add(getChoiceDB(), getChoiceDB().getName());
				getContentsPane().add(getLabel1(), getLabel1().getName());
				getContentsPane().add(getChoiceObject(),
						getChoiceObject().getName());
				getContentsPane().add(getChoiceDataType(),
						getChoiceDataType().getName());
				getContentsPane().add(getLabel2(), getLabel2().getName());
				getContentsPane().add(getChoiceFieldItem(),
						getChoiceFieldItem().getName());
				getContentsPane().add(getTextAreaFSource(),
						getTextAreaFSource().getName());
				getContentsPane().add(getTextAreaSQL(),
						getTextAreaSQL().getName());
				getContentsPane().add(getTextAreaSQLS(),
						getTextAreaSQLS().getName());
				getContentsPane().add(getLabel3(), getLabel3().getName());
				getContentsPane().add(getLabel4(), getLabel4().getName());
				getContentsPane().add(getLabel5(), getLabel5().getName());
				getContentsPane().add(getTextAreaUsedSets(),
						getTextAreaUsedSets().getName());
				getContentsPane().add(getTextAreaUsedFields(),
						getTextAreaUsedFields().getName());
				getContentsPane().add(getButtonTest(),
						getButtonTest().getName());
				getContentsPane().add(getChoiceObjectNormal(),
						getChoiceObjectNormal().getName());
				getContentsPane().add(getTextFieldReturn(),
						getTextFieldReturn().getName());
				getContentsPane().add(getLabel6(), getLabel6().getName());
				getContentsPane().add(getTextAreaError(),
						getTextAreaError().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjContentsPane;
	}

	/**
	 * 返回 Label1 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel1() {
		if (ivjLabel1 == null) {
			try {
				ivjLabel1 = new java.awt.Label();
				ivjLabel1.setName("Label1");
				ivjLabel1.setText("数据库");
				ivjLabel1.setBounds(11, 15, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel1;
	}

	/**
	 * 返回 Label2 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel2() {
		if (ivjLabel2 == null) {
			try {
				ivjLabel2 = new java.awt.Label();
				ivjLabel2.setName("Label2");
				ivjLabel2.setText("SQL");
				ivjLabel2.setBounds(11, 188, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel2;
	}

	/**
	 * 返回 Label3 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel3() {
		if (ivjLabel3 == null) {
			try {
				ivjLabel3 = new java.awt.Label();
				ivjLabel3.setName("Label3");
				ivjLabel3.setText("SQLS");
				ivjLabel3.setBounds(413, 188, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel3;
	}

	/**
	 * 返回 Label4 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel4() {
		if (ivjLabel4 == null) {
			try {
				ivjLabel4 = new java.awt.Label();
				ivjLabel4.setName("Label4");
				ivjLabel4.setText("UsedSets");
				ivjLabel4.setBounds(11, 368, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel4;
	}

	/**
	 * 返回 Label5 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel5() {
		if (ivjLabel5 == null) {
			try {
				ivjLabel5 = new java.awt.Label();
				ivjLabel5.setName("Label5");
				ivjLabel5.setText("UsedFields");
				ivjLabel5.setBounds(413, 368, 70, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel5;
	}

	/**
	 * 返回 Label6 特性值。
	 * 
	 * @return java.awt.Label
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.Label getLabel6() {
		if (ivjLabel6 == null) {
			try {
				ivjLabel6 = new java.awt.Label();
				ivjLabel6.setName("Label6");
				ivjLabel6.setText("return");
				ivjLabel6.setBounds(456, 523, 50, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLabel6;
	}

	/**
	 * 返回 TextAreaError 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaError() {
		if (ivjTextAreaError == null) {
			try {
				ivjTextAreaError = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaError.setName("TextAreaError");
				ivjTextAreaError.setBounds(11, 526, 343, 61);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextAreaError;
	}

	/**
	 * 返回 TextArea1 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaFSource() {
		if (ivjTextAreaFSource == null) {
			try {
				ivjTextAreaFSource = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaFSource.setName("TextAreaFSource");
				ivjTextAreaFSource.setBounds(11, 77, 756, 110);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextAreaFSource;
	}

	/**
	 * 返回 TextArea2 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaSQL() {
		if (ivjTextAreaSQL == null) {
			try {
				ivjTextAreaSQL = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaSQL.setName("TextAreaSQL");
				ivjTextAreaSQL.setBounds(11, 213, 354, 139);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjTextAreaSQL;
	}

	/**
	 * 返回 TextArea3 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaSQLS() {
		if (ivjTextAreaSQLS == null) {
			try {
				ivjTextAreaSQLS = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaSQLS.setName("TextAreaSQLS");
				ivjTextAreaSQLS.setBounds(413, 213, 354, 139);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjTextAreaSQLS;
	}

	/**
	 * 返回 TextArea5 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaUsedFields() {
		if (ivjTextAreaUsedFields == null) {
			try {
				ivjTextAreaUsedFields = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaUsedFields.setName("TextAreaUsedFields");
				ivjTextAreaUsedFields.setBounds(413, 388, 289, 120);
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextAreaUsedFields;
	}

	/**
	 * 返回 TextArea4 特性值。
	 * 
	 * @return java.awt.TextArea
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextArea getTextAreaUsedSets() {
		if (ivjTextAreaUsedSets == null) {
			try {
				ivjTextAreaUsedSets = new java.awt.TextArea("", 0, 0,
						java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
				ivjTextAreaUsedSets.setName("TextAreaUsedSets");
				ivjTextAreaUsedSets.setBounds(11, 388, 343, 120);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextAreaUsedSets;
	}

	/**
	 * 返回 TextFieldReturn 特性值。
	 * 
	 * @return java.awt.TextField
	 */
	/* 警告：此方法将重新生成。 */
	private java.awt.TextField getTextFieldReturn() {
		if (ivjTextFieldReturn == null) {
			try {
				ivjTextFieldReturn = new java.awt.TextField();
				ivjTextFieldReturn.setName("TextFieldReturn");
				ivjTextFieldReturn.setBounds(518, 524, 103, 20);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTextFieldReturn;
	}

	/**
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
		System.out.println("--------- 未捕捉到的异常 ---------");
		exception.printStackTrace(System.out);

	}

	/**
	 * 初始化连接
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	/* 警告：此方法将重新生成。 */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		this.addWindowListener(ivjEventHandler);
		getChoiceDB().addItemListener(ivjEventHandler);
		getChoiceObject().addItemListener(ivjEventHandler);
		getChoiceDataType().addItemListener(ivjEventHandler);
		getChoiceFieldItem().addItemListener(ivjEventHandler);
		getButtonTest().addMouseListener(ivjEventHandler);
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("Parser");
			setLayout(new java.awt.BorderLayout());
			setSize(785, 639);
			setTitle("语法分析器测试程序");
			add(getContentsPane(), "Center");
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * 启动应用程序。
	 * 
	 * @param args
	 *            命令行自变量数组
	 */

	/**
	 * 启动应用程序。
	 * 
	 * @param args
	 *            命令行自变量数组
	 */
	public static void main(java.lang.String[] args) {

		// 在此处插入用来启动应用程序的代码。
		Parser pWindow = new Parser();
		pWindow.show();

	}

	/**
	 * Comment Test按钮点击事件处理
	 */
	public void buttonTest_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
		getTextAreaSQL().setText("");
		getTextAreaSQLS().setText("");
		getTextAreaUsedFields().setText("");
		getTextAreaUsedSets().setText("");
		getTextFieldReturn().setText("");
		getTextAreaError().setText("");

		initParser();
		yp.setModeFlag(getChoiceObjectNormal().getSelectedIndex());
		yp.setDBType(getChoiceDB().getSelectedIndex() + 1);
		// 无变量输入时的解析方法
		try {
			ContentDAO dao = new ContentDAO(conn);
			yp.run(getTextAreaFSource().getText(), null, "targetField",
					"targetTable", dao, "(select lzy from best)", conn, "N", 10,1, "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// yp.run(getTextAreaFSource().getText(),true);

		// 显示whereCond
		getTextAreaSQL().setText(yp.getSQL());

		// 显示SQLS（包含建库脚本）
		ArrayList alSqls = yp.getSQLS();
		for (int i = 0; i < alSqls.size(); i++) {
			getTextAreaSQLS().append((String) alSqls.get(i));
			getTextAreaSQLS().append("\n");
		}

		// 显示返回值
		getTextFieldReturn().setText(yp.getResultString());

		// 显示使用到的库
		ArrayList alTemp = yp.getUsedSets();
		for (int i = 0; i < alTemp.size(); i++) {
			getTextAreaUsedSets().append((String) alTemp.get(i));
		}

		// 显示用到的指标
		java.util.Iterator it = yp.getMapUsedFieldItems().keySet().iterator();
		while (it.hasNext()) {
			FieldItem fi = (FieldItem) yp.getMapUsedFieldItems().get(it.next());
			getTextAreaUsedFields().append(fi.getItemid() + ",");
			getTextAreaUsedFields().append("\n");
		}
	}

	public void initParser() {
	

			// 模拟登录用户信息
			UserView userView = null;
			ResultSet rs=null;
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				conn = DriverManager
						.getConnection(
								"jdbc:sqlserver://192.192.100.102:1433;databaseName=ykchr;",
								"yksoft", "yksoft1919");
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search("select * from fielditem");
				
				System.out.println("）");
				rs.next() ;
				rs.next() ;
				rs.next() ;
				rs.next() ;
                  // System.out.println(rs.getString("itemdesc"));
				
                   userView = new UserView("su", conn);
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			int dataType = -1;
			// public static int INT=5;
			// public static int FLOAT=6;
			// public static int STRVAforLUE=7;
			// public static int LOGIC=8;
			// public static int DATEVALUE=9;
			// public static int NULLVALUE=10;
			// public static int ODDVAR=11;
			if (getChoiceDataType().getSelectedIndex() == 0) {
				dataType = 6;
			}
			if (getChoiceDataType().getSelectedIndex() == 1) {
				dataType = 7;
			}
			if (getChoiceDataType().getSelectedIndex() == 2) {
				dataType = 9;
			}
			if (getChoiceDataType().getSelectedIndex() == 3) {
				dataType = 8;
			}
			// 构建新的解析器
			yp = new YksjParser(userView, getFieldItems(),
					getChoiceObjectNormal().getSelectedIndex(),
					dataType, getChoiceObject()
							.getSelectedIndex(), "Ht", "USR");

			// 原构造函数在系统内部默认设定（配置文件中的配置值）
			// setDBType(Sql_switcher.searchDbServer());// 设置数据库类型
			// 此处因测试工具的需要不能使用默认值，实际使用中无需设置该值
			yp.setDBType(getChoiceDB().getSelectedIndex());

			// 设置待解析字符串文本
			// 因为构造函数
		

	}

	private ArrayList getFieldItems() {
		ArrayList alReturn = new ArrayList();

		FieldItem f = new FieldItem("A0107", "A0107");
		f.setItemid("A0107");// cFldName
		f.setItemdesc("性别");// cHZ
		f.setItemtype("A");// cFldtype
		f.setFieldsetid("A01");// cSetName
		f.setCodesetid("AX");// cCodeid
		alReturn.add(f);

		f = new FieldItem("A0102", "A0102");
		f.setItemid("A0102");// cFldName
		f.setItemdesc("出生日期");// cHZ
		f.setItemtype("D");// cFldtype
		f.setFieldsetid("A01");// cSetName
		f.setCodesetid("");// cCodeid
		alReturn.add(f);

		f = new FieldItem("A0405", "A0405");
		f.setItemid("A0405");// cFldName
		f.setItemdesc("学历");// cHZ
		f.setItemtype("A");// cFldtype
		f.setFieldsetid("A04");// cSetName
		f.setCodesetid("FD");// cCodeid
		alReturn.add(f);

		f = new FieldItem("A5508", "A5508");
		f.setItemid("A5508");// cFldName
		f.setItemdesc("级别工资");// cHZ
		f.setItemtype("N");// cFldtype
		f.setFieldsetid("A55");// cSetName
		f.setCodesetid("");// cCodeid
		alReturn.add(f);

		f = new FieldItem("A0114", "A0114");
		f.setItemid("A0114");// cFldName
		f.setItemdesc("年龄");// cHZ
		f.setItemtype("N");// cFldtype
		f.setFieldsetid("A01");// cSetName
		f.setCodesetid("");// cCodeid
		alReturn.add(f);

		f = new FieldItem("B0401", "B0401");
		f.setItemid("B0401");// cFldName
		f.setItemdesc("拔款数");// cHZ
		f.setItemtype("n");// cFldtype
		f.setFieldsetid("B04");// cSetName
		f.setCodesetid("DD");// cCodeid
		alReturn.add(f);

		f = new FieldItem("A5520", "A5520");
		f.setItemid("A5520");// cFldName
		f.setItemdesc("变动数");// cHZ
		f.setItemtype("N");// cFldtype
		f.setFieldsetid("A55");// cSetName
		f.setCodesetid("");// cCodeid
		alReturn.add(f);

		f = new FieldItem("B0102", "B0102");
		f.setItemid("B0102");// cFldName
		f.setItemdesc("单位性质");// cHZ
		f.setItemtype("A");// cFldtype
		f.setFieldsetid("B01");// cSetName
		f.setCodesetid("BB");// cCodeid
		alReturn.add(f);

		FieldItem fielditem = new FieldItem("Q03", "Q03");
		fielditem.setFieldsetid("Q03");
		fielditem.setItemdesc("每日工时");
		fielditem.setItemid("8.0");
		fielditem.setItemtype("N");
		fielditem.setDecimalwidth(2);
		// fielditem.setVar(1);
		alReturn.add(fielditem);

		fielditem = new FieldItem("Q03", "Q03");
		fielditem.setFieldsetid("Q03");
		fielditem.setItemdesc("职务工资");
		fielditem.setItemid("A3300");
		fielditem.setItemtype("N");
		fielditem.setDecimalwidth(2);
		// fielditem.setVar(1);
		alReturn.add(fielditem);
		fielditem = new FieldItem("ffff", "ffff");
		fielditem.setFieldsetid("B3320");
		fielditem.setItemdesc("医疗（包干）");
		fielditem.setItemid("8.0");
		fielditem.setItemtype("N");
		fielditem.setDecimalwidth(2);
		// fielditem.setVar(1);
		alReturn.add(fielditem);

		fielditem = new FieldItem("K0201", "K0201");
		fielditem.setFieldsetid("K02");
		fielditem.setItemdesc("最低岗位工资");
		fielditem.setItemid("K0201");
		fielditem.setItemtype("N");
		fielditem.setDecimalwidth(2);
		// fielditem.setVar(1);
		alReturn.add(fielditem);

		fielditem = new FieldItem("k0202", "k0202");
		fielditem.setFieldsetid("K02");
		fielditem.setItemdesc("最高岗位工资");
		fielditem.setItemid("K0202");
		fielditem.setItemtype("N");
		fielditem.setDecimalwidth(2);
		// fielditem.setVar(1);
		alReturn.add(fielditem);

		fielditem = new FieldItem("B0107", "B0107");
		fielditem.setFieldsetid("B01");
		fielditem.setItemdesc("成立时间");
		fielditem.setItemid("B0107");
		fielditem.setItemtype("D");
		alReturn.add(fielditem);
		String str = "\"";
		
		fielditem = new FieldItem("P0123", "P0123");
		fielditem.setFieldsetid("p01");
		fielditem.setItemdesc("成立时间");
		fielditem.setItemid("P0123");
		fielditem.setItemtype("A");
		alReturn.add(fielditem);
		
		fielditem = new FieldItem("P0123", "P0123");
		fielditem.setFieldsetid("p01");
		fielditem.setItemdesc("岗位名称");
		fielditem.setItemid("P0123");
		fielditem.setItemtype("A");
		alReturn.add(fielditem);
//		String str = "\"";
		// fielditem.setVar(1);
		// alReturn.add(fielditem);
		// Field := TMenu.Create;
		// Field.cFldName :='B0107';
		// Field.cHz := '成立时间';
		// Field.cFldtype := 'D';
		// Field.cSetName := 'B01';
		// Field.cCodeid :='';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);

		// +岗位工资+租房补贴+消费津贴+月奖+医疗（包干）+伙食补助

		// Field := TMenu.Create;
		// Field.cFldName :='B0102';
		// Field.cHz := '单位性质';
		// Field.cFldtype := 'A';
		// Field.cSetName := 'B01';
		// Field.cCodeid :='BB';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);
		//
		//
		// Field := TMenu.Create;
		// Field.cFldName :='B0107';
		// Field.cHz := '成立时间';
		// Field.cFldtype := 'D';
		// Field.cSetName := 'B01';
		// Field.cCodeid :='';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);
		//
		// Field := TMenu.Create;
		// Field.cFldName :='E0122';
		// Field.cHz := '部门';
		// Field.cFldtype := 'A';
		// Field.cSetName := 'A01';
		// Field.cCodeid :='UM';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);
		//
		// Field := TMenu.Create;
		// Field.cFldName :='P0123';
		// Field.cHz := '岗位名称';
		// Field.cFldtype := 'A';
		// Field.cSetName := 'K01';
		// Field.cCodeid :='';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);
		//
		// Field := TMenu.Create;
		// Field.cFldName :='P0105';
		// Field.cHz := '岗位类型';
		// Field.cFldtype := 'A';
		// Field.cSetName := 'K01';
		// Field.cCodeid :='EE';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);
		//
		// Field := TMenu.Create;
		// Field.cFldName :='P0401';
		// Field.cHz := '岗位条件';
		// Field.cFldtype := 'A';
		// Field.cSetName := 'K04';
		// Field.cCodeid :='FF';
		// Field.nIsVar := 0;
		// myParser.Fields.Add(Field);

		return alReturn;
	}
}
