import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Button, TextField
import java.awt.event.*;							// ActionListener, ActionEvent
import java.util.*;									// Calendar, TimeZone

public class Biorhythm extends Applet
					implements ActionListener { 	// ActionListener�C���^�[�t�F�[�X����
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int Days[ ] = { 0, 31, 28, 31, 30,  31, 30, 31, 31,  30, 31, 30, 31};	// �e���̓���
	int NowYear, NowMonth, NowDay;					// ���݂̔N����
	int BirthYear, BirthMonth, BirthDay;			// ���܂ꂽ�N����
	int TotalDays;									// ������
	Button DisplayButton;							// �\���{�^��
	TextField BirthYearTextfield, BirthMonthTextfield, BirthDayTextfield;

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		WorkImage = createImage(400, 240);			// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );	// ��Ɨp�O���t�B�b�N�X�擾
		WorkGraphics.setColor(new Color(0, 100, 0));// �o�C�I���Y���̃x�[�X��ʍ쐬
		WorkGraphics.fillRect(0, 0, 400, 20);
		WorkGraphics.setColor(new Color(0, 0, 0));
		WorkGraphics.fillRect(0, 20, 400, 201);
		WorkGraphics.setColor(new Color(0, 100, 0));
		WorkGraphics.fillRect(0, 221, 400, 20);

		BirthYearTextfield = new TextField(5);			// �N���͗p�e�L�X�g�t�B�[���h
		BirthMonthTextfield = new TextField(5);			// �����͗p�e�L�X�g�t�B�[���h
		BirthDayTextfield = new TextField(5);			// �����͗p�e�L�X�g�t�B�[���h
		BirthYearTextfield.setBackground(Color.yellow);	// �e�L�X�g�t�B�[���h�̔w�i�F
		BirthMonthTextfield.setBackground(Color.yellow);
		BirthDayTextfield.setBackground(Color.yellow);
		add(BirthYearTextfield);						// �e�L�X�g�t�B�[���h���A�v���b�g�ɕt��
		add(BirthMonthTextfield);
		add(BirthDayTextfield);

		BirthYearTextfield.setBounds(130, 3, 40, 15);	// �e�L�X�g�t�B�[���h�z�u�E�T�C�Y�ݒ�
		BirthMonthTextfield.setBounds(240, 3, 40, 15);
		BirthDayTextfield.setBounds(325, 3, 40, 15);

		WorkGraphics.setColor(new Color(255, 255, 255));// �e�^�C�g���`��
		WorkGraphics.drawString("Biorhythm", 3, 15);
		WorkGraphics.drawString("year", 100, 15);
		WorkGraphics.drawString("month", 200, 15);
		WorkGraphics.drawString("day", 300, 15);
		WorkGraphics.setColor(new Color(0, 255, 255));
		WorkGraphics.drawString("Physical", 10, 235);
		WorkGraphics.setColor(new Color(255, 255, 0));
		WorkGraphics.drawString("Sentiment", 90, 235);
		WorkGraphics.setColor(new Color(255, 255, 255));
		WorkGraphics.drawString("Intelligence", 170, 235);

		DisplayButton = new Button("Display");		// �\���{�^������
		add(DisplayButton);							// �A�v���b�g�Ƀ{�^����t��
		DisplayButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		DisplayButton.setBounds(340, 223, 50, 15);	// �{�^���̃T�C�Y�E�ʒu�̍Đݒ�

		// �{���̓��t
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		NowYear = date.get(Calendar.YEAR);
		NowMonth = date.get(Calendar.MONTH) + 1;
		NowDay   = date.get(Calendar.DATE);
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );

		if (button == DisplayButton){				// DisplayButton�������ꂽ�ꍇ
			String str = BirthYearTextfield.getText( );	// �N�̃e�L�X�g�t�B�[���h�̕�����
			int w = new Integer(str).intValue( );	// ������str�𐮐���
			BirthYear = w;							// ���܂ꂽ�N
			str = BirthMonthTextfield.getText( );	// ���̃e�L�X�g�t�B�[���h�̕�����
			w = new Integer(str).intValue( );		// ������str�𐮐���
			BirthMonth = w;							// ���܂ꂽ��
			str = BirthDayTextfield.getText( );		// ���̃e�L�X�g�t�B�[���h�̕�����
			w = new Integer(str).intValue( );		// ������str�𐮐���
			BirthDay = w;							// ���܂ꂽ��

			TotalDaysSyori( );						// ���܂�Ă��獡���܂ł̓���
			ScreenClear( );							// �o�C�I���Y����ʃN���A
			DisplayBiorhythm( );					// �o�C�I���Y���\��

			repaint( );								// �ĕ`��
		}
	}

	// ���܂�Ă���̓����v�Z -----------------------------------------------------------
	public void TotalDaysSyori( ) {					// ���܂�Ă��獡���܂ł̓���
		// ���܂ꂽ�N��1/1����a�����܂ł̓���
		int TotalDays1 = 0;
		Days[2] = 28 + LeapCheck(BirthYear);		// ���܂ꂽ�N��2���̓���
		for (int n = 1; n < BirthMonth; n++)
			TotalDays1 += Days[n];					// ���܂ꂽ���̑O���܂ł̓��������Z
		TotalDays1 += BirthDay;						// ���܂ꂽ�������Z

		// ���܂ꂽ�N��1/1���獡���܂ł̓���
		int TotalDays2 = 0;
		for (int n = BirthYear; n < NowYear; n++) {	// ���܂ꂽ�N�̑O�N�܂ł̓��������Z
			if (LeapCheck(n) == 1)					// n�N���[�N�̏ꍇ
				 TotalDays2 += 366;
			else
				 TotalDays2 += 365;
		}
		Days[2] = 28 + LeapCheck(NowYear);			// ���N��2���̓���
		for (int n = 1; n < NowMonth; n++)			// ���܂ꂽ���̑O���܂ł̓��������Z
			TotalDays2 += Days[n];
		TotalDays2 += NowDay;						// �����̓������Z

		// ���܂�Ă��獡���܂ł̓���
		TotalDays = TotalDays2 - TotalDays1;
	}
	// �[�N�`�F�b�N ---------------------------------------------------------------------
	int LeapCheck(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			return 1;
		else
			return 0;
	}
	// �o�C�I���Y����ʃN���A -----------------------------------------------------------
	public void ScreenClear( ) {
		WorkGraphics.setColor(new Color(0, 0, 0));
		WorkGraphics.fillRect(0, 20, 400, 201);

		// �P���P�ʂɃO���[�̏c��
		WorkGraphics.setColor(new Color(50, 50, 50));
		for (int x = 10; x <= 400; x += 10)
			WorkGraphics.drawLine(x, 20, x, 220);

		// �O���x���̐������Ɩ{����\�킷�c��
		WorkGraphics.setColor(new Color(255, 255, 255));
		WorkGraphics.drawLine(200, 20, 200, 220);
		WorkGraphics.drawLine(0, 120, 400, 120);
		WorkGraphics.drawString("+", 202, 30);
		WorkGraphics.drawString("-", 202, 220);
	}
	// �w��͈͕`�揈�� -----------------------------------------------------------------
	public void DisplayBiorhythm( ) {				// �o�C�I���Y���\��
		int startday = TotalDays - 20;				// 20���O����J�n

		for (int x = 1; x <= 400; x++) {			// 10pixels��1����
			// �g�́iPhysical�j
			int y = (int)(120 - 100 * Math.sin(6.28 * (startday % 23 + x / 10.0) / 23));
			WorkGraphics.setColor(new Color(0, 255, 255));
			WorkGraphics.drawLine(x, y, x, y);
			// ����(Sentiment�j
			y = (int)(120 - 100 * Math.sin(6.28 * (startday % 28 + x / 10.0) / 28));
			WorkGraphics.setColor(new Color(255, 255, 0));
			WorkGraphics.drawLine(x, y, x, y);
			// �m���iIntelligence�j
			y = (int)(120 - 100 * Math.sin(6.28 * (startday % 33 + x / 10.0) / 33));
			WorkGraphics.setColor(new Color(255, 255, 255));
			WorkGraphics.drawLine(x, y, x, y);
		}
		repaint( );
	}
}
