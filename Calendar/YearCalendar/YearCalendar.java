import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Font, Button, TextField
import java.awt.event.*;							// ActionListener, ActionEvent
import java.util.*;									// Calendar, TimeZone

public class YearCalendar extends Applet implements ActionListener {
	int Days[ ] = { 0, 31, 28, 31, 30, 31, 30, 31, 31,	30, 31, 30, 31};	// �e���̓���
	int Year;
	TextField YearTextfield;						// �N�e�L�X�g�t�B�[���h
	Button DisplayButton, LeftButton, RightButton;	// �e��{�^��
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int Holiday[ ] = {								// �x���ݒ�
		 101, 		// ���U
		 100, 		// ���l�̓�	------- ����
		 211, 		// �����L�O��
		 300, 		// �t���̓�	------- ����
		 429, 		// �݂ǂ�̓�
		 503, 		// ���@�L�O��
		 504, 		// �����̋x��
		 505, 		// �q���̓�
		 720, 		// �C�̓�
		 915, 		// �h�V�̓�
		 900, 		// �H���̓�	------- ����
		1000, 		// �̈�̓�	------- ����
		1103, 		// �����̓�
		1123, 		// �ΘJ���ӂ̓�
		1223  		// �V�c�a����
		};

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		WorkImage = createImage(800, 500);			// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );	// ��Ɨp�O���t�B�b�N�X�擾
		Font font = new Font("Courier", Font.PLAIN, 12);	// �t�H���g�ݒ�
		WorkGraphics.setFont(font);							//�t�H���g�ݒ�

		setLayout(null);							// ���R���C�A�E�g�ݒ�

		WorkGraphics.setColor(new Color(0, 100, 0));// �w�b�_�[�쐬
		WorkGraphics.fillRect(0, 0, 800, 20);
		WorkGraphics.setColor(new Color(255, 255, 255));
		WorkGraphics.drawString("Calendar", 40, 15);

		WorkGraphics.setColor(Color.white);			// �J�����_�[�g
		WorkGraphics.fillRect(0, 20, 800, 480);

		YearTextfield = new TextField(5);			// �N�e�L�X�g�t�B�[���h
		YearTextfield.setBackground(Color.yellow);
		add(YearTextfield);
		YearTextfield.setBounds(370, 3, 35, 15);

		DisplayButton = new Button("Display");		// �\���{�^��
		add(DisplayButton);							// �{�^�����A�v���b�g�ɒǉ�
		DisplayButton.setBounds(430, 3, 50, 15);	// �{�^���̔z�u�Ƒ傫���ݒ�
		DisplayButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�

		LeftButton = new Button("<<");				// �O�N�\���{�^��
		add(LeftButton);
		LeftButton.setBounds(5, 3, 30, 15);			// �{�^���̔z�u�Ƒ傫���ݒ�
		LeftButton.addActionListener(this);			// �{�^���Ƀ��X�i�[�ǉ�
		RightButton = new Button(">>");				// ���N�\���{�^��
		add(RightButton);
		RightButton.setBounds(765, 3, 30, 15);		// �{�^���̔z�u�Ƒ傫���ݒ�
		RightButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�

		Calendar date = Calendar.getInstance( );
		Year = date.get(Calendar.YEAR);				// ���݂̔N���擾
		YearTextfield.setText(String.valueOf(Year));// �e�L�X�g�t�B�[���h�ɔN�ݒ�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		LeapYearCheck(Year);						// �[�N����
		HolidaySyori( );							// �x������
		Display( );									// �J�����_�[�`�揈��
		g.drawImage(WorkImage, 0, 0, this);
	}
	// �`��X�V���� ---------------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// 1�N�`�揈�� ----------------------------------------------------------------------
	void Display( ) {
		WorkGraphics.setColor(Color.white);
		WorkGraphics.fillRect(0, 20, 800, 480);

		for (int i = 0; i < 3; i++)					// �c3�s
			for (int j = 0; j < 4; j++)				// ��4��
				MakeCalendar(Year, i * 4 + j + 1, i, j);	// �e���̃J�����_�[�`��
	}
	// �[�N���� -------------------------------------------------------------------------
	void LeapYearCheck(int Year) {					// �[�N����
		if (Year % 4 == 0 && Year % 100 != 0 || Year  % 400 == 0)
			Days[2] = 29;							// �Q���̓�������
		else
			Days[2] = 28;
	}
	// �x������ -------------------------------------------------------------------------
	void HolidaySyori( ) {
		// ���l�̓��@1���̑�2���j
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(Year, 1 - 1, 1);					// 1��1���ɃZ�b�g
		int col = date.get(Calendar.DAY_OF_WEEK);	// �j���v�Z 1:���C2:���C3:�΁C�E�E7:�y
		int count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// ���j���̏ꍇ
				count++;
				if (count == 2) {					// ��2���j��
					Holiday[1] = 1 * 100 + c;
					break;
				}
			}
			col++;									// ���̗j���ɃZ�b�g
			if (col > 7)							// �y�j���̎���
				col = 1;							// ���j��
		}
		// �̈�̓��@10���̑�2���j
		date.set(Year, 10 - 1, 1);					// 10��1���ɃZ�b�g
		col = date.get(Calendar.DAY_OF_WEEK) % 7;	// �j���v�Z
		count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// ���j���̏ꍇ
				count++;
				if (count == 2) {					// ��2���j��
					Holiday[11] = 10 * 100 + c;
					break;
				}
			}
			col++;									// ���̗j���ɃZ�b�g
			if (col > 7)							// �y�j���̎���
				col = 1;							// ���j��
		}

		// �t���̓�   03/??
		Holiday[3] = 3*100 + (int)(20.8431+0.242194*(Year-1980)-(int)((Year-1980)/4)); 

		// �H���̓�   09/??
		Holiday[10] = 9*100 + (int)(23.2488+0.242194*(Year-1980)-(int)((Year-1980)/4));

	}
	// �w��N�E���̃J�����_�[�쐬 -------------------------------------------------------
	void MakeCalendar(int year, int month, int y, int x) {
        String weeks[ ] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		x = x * 200 + 24;							// �`��ʒu�ݒ�
		y = y * 160 + 30;

		WorkGraphics.setColor(Color.black);			// �e���̃w�b�_�[�쐬
		WorkGraphics.drawString("" + month, x + 72, y);
		WorkGraphics.setColor(Color.red);
		WorkGraphics.drawString(weeks[0], x, y+15);
		WorkGraphics.setColor(Color.black);
		for (int i = 1; i <= 5; i++)
			WorkGraphics.drawString(weeks[i], x + i * 24, y + 15);
		WorkGraphics.setColor(Color.blue);
		WorkGraphics.drawString(weeks[6], x + 6 * 24, y + 15);
		WorkGraphics.setColor(Color.black);
		WorkGraphics.drawLine(x, y + 18, x + 7 * 24 - 6, y + 18);

		// �e���̍ŏ��̗j���v�Z
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(year, month - 1, 1);				// Year/Month/1�ɃZ�b�g
		int w = date.get(Calendar.DAY_OF_WEEK);		// �j���v�Z 1:���C2:���C3:�΁C�E�E7:�y

		y = y + 35;									// �c�����̕\���ʒu�ݒ�
		int hurikae = 0;							// �U�փN���A
		for (int d = 1; d <= Days[month]; d++) {
			// �j���ŃJ���[�ݒ�
			if (w == 1 || hurikae == 1)				// ���j���܂��͐U�֋x��
				WorkGraphics.setColor(Color.red);
			else if (w == 7)						// �y�j��
				WorkGraphics.setColor(Color.blue);
			else									// ���j��������j��
				WorkGraphics.setColor(Color.black);

			// �j���@���@�U�֋x��
			hurikae = 0;								// �U�փN���A
			for (int h = 0; h < Holiday.length; h++) {	// �j���`�F�b�N
				if (month * 100 + d == Holiday[h]) {	// �j���̏ꍇ
					WorkGraphics.setColor(Color.red);
					if (w == 1)							// ���j�̏ꍇ
						hurikae = 1;					// �U�փZ�b�g
					break;
				}
			}

			if (d < 10)								// �\���������P��
				WorkGraphics.drawString("" + d, x + w * 24 - 15, y);	// �\���ʒu�ݒ�
			else									// �\���������Q��
				WorkGraphics.drawString("" + d, x + w * 24 - 21, y);

			w++;
			if (w > 7) {							// �y�j���̎��̏ꍇ
				w = 1;								// ���j���ɐݒ�
				y += 14;
			}
		}
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h��` -------------------------------------
 	public void actionPerformed(ActionEvent evt) {
		Button button = (Button)evt.getSource( );
		if (button == DisplayButton) {				// �w��N�`��
			String str = YearTextfield.getText( );	// �e�L�X�g�t�B�[���h�̒l�擾
			Year = new Integer(str).intValue( );	// �����𐮐���
		}
		else if (button == LeftButton)				// �O�N�`��
			Year--;
		else if (button == RightButton)				// ���N�`��
			Year++;
		YearTextfield.setText(String.valueOf(Year));// �e�L�X�g�t�B�[���h�ɒl�ݒ�
		repaint( );									// �ĕ`��
	}
}
