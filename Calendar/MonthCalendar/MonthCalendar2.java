import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color
import java.awt.event.*;							// ActionListener, ActionEvent
import java.util.*;									// Calendar, TimeZone

public class MonthCalendar2 extends Applet
			 		implements ActionListener {	// ActionListener�C���^�[�t�F�[�X������

	int Year, Month;								// �N�E��
	Button  LastYearButton,							// ��N�̂��̌��`��{�^��
			LastMonthButton,						// �O���`��{�^��
			NextYearButton,							// ���N�̂��̌��`��{�^��
			NextMonthButton;						// ���̌��`��{�^��

	int Holiday[ ] = {
		 101, // ���U
		 100, // ���l�̓�	------- ��2���j��
		 211, // �����L�O��
		 300, // �t���̓�	------- �v�Z
		 429, // �݂ǂ�̓�
		 503, // ���@�L�O��
		 504, // �����̋x��
		 505, // �q���̓�
		 720, // �C�̓�		------- ��3���j��
		 915, // �h�V�̓�
		 900, // �H���̓�	------- �v�Z
		1000, // �̈�̓�	------- ��2���j��
		1103, // �����̓�
		1123, // �ΘJ���ӂ̓�
		1223  // �V�c�a����
		};

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		setLayout(null);							// ���C�A�E�g�����R�ݒ�ɂ���

		LastYearButton = new Button("<<");			// �O�N�{�^������
		NextYearButton = new Button(">>");			// ���N�{�^������
		LastMonthButton = new Button("<");			// �O���{�^������
		NextMonthButton = new Button(">");			// �����{�^������
		add(LastYearButton);						// �e�{�^�����A�v���b�g�ɕt��
		add(NextYearButton);
		add(LastMonthButton);
		add(NextMonthButton);

		LastYearButton.addActionListener(this);		// �e�{�^���Ƀ��X�i�[�ǉ�
		NextYearButton.addActionListener(this);
		LastMonthButton.addActionListener(this);
		NextMonthButton.addActionListener(this);

		LastYearButton.setBounds(10, 5, 30, 30);	// �{�^���̔z�u�ƃT�C�Y�ݒ�
		NextYearButton.setBounds(280, 5,30, 30);	// setBounds (x, y, w, h)
		LastMonthButton.setBounds(50, 5, 30, 30);
		NextMonthButton.setBounds(240, 5,30, 30);

		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		Year = date.get(Calendar.YEAR);		 		// ���݂̔N�擾
		Month = date.get(Calendar.MONTH) + 1;		// ���݂̌��擾 (0:1���C1�F2���C�`�C11:12���j
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		int Days[ ] = { 0, 31, 28, 31, 30,   31, 30, 31, 31,  30, 31, 30, 31 };
        String week[ ] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

		if ((Year % 4 == 0 && Year % 100 != 0) || Year % 400 == 0)	// �[�N
			Days[2] = 29;							// 2���̓����C��
		else
 			Days[2] = 28;							// ���N

		// �w�i�\��
		g.setColor(new Color(235, 150, 0));
		g.fillRect(0, 0, 320, 240);					// �w�i�g����
		g.setColor(new Color(255, 255, 255));
		g.fillRect(17, 55, 287, 175);				// �J�����_�[�\����
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, 319, 239);					// �O�g

		// �e�t���ŁC�N�^���\��
		for (int i = 0; i <= 1; i++) {
			if (i == 0)								// i=0�̂Ƃ��ɍ��F�ŉe�\��
				g.setColor(Color.black);
			else									// i=1�̂Ƃ��ɔ��F�őO�ʕ����\��
				g.setColor(Color.white);

			Font f = new Font("Courier", Font.BOLD, 30);	// �t�H���g��`
			g.setFont(f);									// �t�H���g�ݒ�

			if (Month < 10)							// 1���̏ꍇ�C�\���ʒu�ݒ�
				g.drawString(Year + "/"  + Month, 107 - i, 30);
			else									// 2���̏ꍇ
				g.drawString(Year + "/"  + Month, 100 - i, 30);
		}

		// �j���`��
		g.setColor(Color.black);
		Font f = new Font("TimesRoman", Font.PLAIN, 12);	// �t�H���g��`
		g.setFont(f);										// �t�H���g�ݒ�
		for (int i = 0; i < 7; i++)
			g.drawString(week[i], 33 + i * 40, 50);

		// �g�쐬
		g.setColor(new Color(255, 230, 0));
		for (int i = 0; i <= 5; i++)
			for (int j = 1; j <= 7; j++)
				g.drawRect(j * 40 - 20, i * 28 + 58, 40, 28);

		// �\���t�H���g�ݒ�
		f = new Font("Courier", Font.BOLD, 30);
		g.setFont(f);

		// �j���v�Z
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(Year, Month - 1, 1);				// set(Year, Month, Date)
													// �������́C0:1��,1:2��,�E,11:12��
		int col = date.get(Calendar.DAY_OF_WEEK);	// �j�� 1:���C2:���C3:�΁C�E�E�E�C7:�y

		// �J�����_�[�̓��쐬
		HolidaySyori( );								// �x������					
		int row = 1;
		int hurikae = 0;								// �U�փN���A				
		for (int day = 1; day <= Days[Month]; day++) {
			if (col == 1 || hurikae == 1)				// ���j�����܂��͐U�ւ̏ꍇ	
				g.setColor(Color.red);
			else if (col == 7)							// �y�j���̏ꍇ
				g.setColor(Color.blue);
			else										// �����̏ꍇ
				g.setColor(Color.black);

			// �j���@���@�U�֋x��													
			hurikae = 0;								// �U�փN���A				
			for (int h = 0; h < Holiday.length; h++) {	// �j���`�F�b�N				
				if (Month * 100 + day == Holiday[h]) {	// �j���̏ꍇ				
					g.setColor(Color.red);											
					if (col == 1)						// ���j�̏ꍇ				
						hurikae = 1;					// �U�փZ�b�g				
					break;															
				}																	
			}																		

			if (day < 10)								// ���t��1���̏ꍇ
				g.drawString(day + " ", col * 40 - 5, row * 28 + 55);
			else										// ���t��2���̏ꍇ
				g.drawString(day + " ", col * 40 - 15, row * 28 + 55);
			col++;
			if (col > 7) {
				row++;									// ���̍s��
				col = 1;								// �j������j����
			}
		}

	}
	// �x������ -------------------------------------------------------------------------
	void HolidaySyori( ) {
		// ���l�̓��@1���̑�2���j
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		date.set(Year, 1 - 1, 1);				 	// 1��1���ɃZ�b�g
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
		// �C�̓��@�V���̑�3���j
		date.set(Year, 7 - 1, 1);					// 7��1���ɃZ�b�g
		col = date.get(Calendar.DAY_OF_WEEK);		// �j���v�Z 1:���C2:���C3:�΁C�E�E7:�y
		count = 0;
		for (int c = 1; c <= 30; c++) {
			if (col == 2) {							// ���j���̏ꍇ
				count++;
				if (count == 3) {					// ��3���j��
					Holiday[8] = 7 * 100 + c;
					break;
				}
			}
			col++;									// ���̗j���ɃZ�b�g
			if (col > 7)							// �y�j���̎���
				col = 1;							// ���j��
		}
		// �̈�̓��@10���̑�2���j
		date.set(Year, 10 - 1, 1);					// 10��1���ɃZ�b�g
		col = date.get(Calendar.DAY_OF_WEEK);		// �j���v�Z 1:���C2:���C3:�΁C�E�E7:�y
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
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );
		if (button == LastYearButton)				// �{�^���̃��x�����O�N�̏ꍇ
			Year--;
		if (button == NextYearButton)				// �{�^���̃��x�������N�̏ꍇ
			Year++;
		if (button == LastMonthButton)				// �{�^���̃��x�����O���̏ꍇ
			Month--;
		if (button == NextMonthButton)				// �{�^���̃��x���������̏ꍇ
			Month++;
		if (Month < 1) {							// 1���̑O�̌�
			Year--;									// �O�N��12���ɂ���
			Month = 12;	
		}
		if (Month > 12) {							// 12���̎��̌�
			Year++;									// ���N��1���ɂ���
			Month = 1;
		}
		repaint( );									// �ĕ`��
	}
}