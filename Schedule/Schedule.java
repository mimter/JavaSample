import java.awt.*;				// Frame, Image, Graphics, Toolkit
import java.awt.event.*;		// WindowListener, WindowEvent
import java.io.*;				// FileInputStream, BufferedReader,   InputStreamReader, IOException
import java.net.*;				// Socket
import java.util.*;				// Calendar, StringTokenizer

class Schedule extends Frame implements WindowListener, ActionListener {
	int FrameWidth = 500, FrameHeight = 600;		// �t���[���T�C�Y
	StringBuffer Delimiter = new StringBuffer( );	// ��؂蕶��
	int ContentsMAX = 18;							// ���e�̐��i6������23���j
	Label DateLabel;								// ���t
	Label HourLabel[ ] = new Label[ContentsMAX];	// ����
	Label Contents[ ] = new Label[ContentsMAX];		// ���e
	TextField DateField = new TextField(10);		// ���t�e�L�X�g�t�B�[���h
	TextField HourField = new TextField(3);			// ���ԃe�L�X�g�t�B�[���h
	TextField ContentField = new TextField(40);		// ���e�e�L�X�g�t�B�[���h

	int PreparationFlag = 0;						// �����t���O
	String SendMessage = null,						// ���M���b�Z�[�W
		   ReceiveMessage = null;					// ��M���b�Z�[�W

	Button BackButton, NextButton,					// �O���C�����{�^��
		   DispButton, InsertButton, 				// �\���E�o�^�{�^��
           DeleteButton, UpdateButton;				// �폜�E�X�V�{�^��

	Color BackColor = new Color(255, 210, 200);		// �w�i�F

	// �{���̓��t
	Calendar DispDay = Calendar.getInstance( );		// �\�����t
	int Year, Month, Day;							// �N�C���C��

	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	// ���C������ -----------------------------------------------------------------------
	public static void main(String args[ ]) {
		Schedule frame = new Schedule("Schedule");	// �t���[���쐬
		frame.init( );								// �t���[��������
	}
	// �R���X�g���N�^ -------------------------------------------------------------------
	public Schedule(String title) {
		setTitle(title);							// �t���[���Ƀ^�C�g���ݒ�
	}
	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �t���[���ݒ�
		setSize(FrameWidth, FrameHeight);			// �t���[���T�C�Y�ݒ�
		show( );									// �t���[���\��

		addWindowListener(this);					// �E�B���h�E���X�i�[�ǉ�

		// ��Ɨp�O���t�B�b�N�̈�ݒ�i�t���[���̍쐬��ɍs�����Ɓj
		WorkImage = createImage(FrameWidth, FrameHeight);
		WorkGraphics = WorkImage.getGraphics( );
		WorkGraphics.setFont(new  Font("�l�r ����", Font.PLAIN, 20)); 	// �t�H���g�ݒ�

		setLayout(null);							// ���R���C�A�E�g

		// ��ʐݒ� --------------------------------------------
		DateLabel = new Label("yyyy/mm/dd(***)");				// ���x������
		add(DateLabel);											// �t��
		DateLabel.setBounds((FrameWidth - 200)/2, 30, 200, 30);	// �z�u�E�T�C�Y�ݒ�
		DateLabel.setFont(new Font("TimesRoman", Font.BOLD, 30));// �t�H���g�ݒ�
		DateLabel.setBackground(BackColor);						// �w�i�F�ݒ�

		// ���E���{�^���ݒ� ------------------------------------
		add(BackButton = new Button("��"));						// �{�^�������E�t��
		BackButton.setFont(new  Font("System", Font.PLAIN, 20));// �t�H���g�ݒ�
		BackButton.setBounds(10, 30, 30, 30);					// �z�u�E�T�C�Y�ݒ�
		BackButton.addActionListener(this);						// ���X�i�[�ǉ�

		add(NextButton = new Button("��"));						// �{�^�������E�t��
		NextButton.setFont(new  Font("System", Font.PLAIN, 20));// �t�H���g�ݒ�
		NextButton.setBounds(FrameWidth - 40, 30, 30, 30);		// �z�u�E�T�C�Y�ݒ�
		NextButton.addActionListener(this);						// ���X�i�[�ǉ�

		// ���e�\���̈�ݒ� ------------------------------------
		int BasePoint = 80;										// �ォ��80pixels
		for (int i = 0; i < ContentsMAX; i++) {					// 6������23����18��
			if (i + 6 < 10)
				HourLabel[i] = new Label(" " + (i+6));			// �������x��
			else
				HourLabel[i] = new Label("" + (i+6));
			add(HourLabel[i]);	HourLabel[i].setBounds(30, BasePoint + i * 20, 20, 20);
			HourLabel[i].setFont(new Font("TimesRoman", Font.BOLD, 20));
			HourLabel[i].setBackground(BackColor);

			Contents[i] = new Label("                              ");	// ���e���N���A
			Contents[i].setFont(new  Font("System", Font.PLAIN, 16));	// �t�H���g�ݒ�
			if (i % 2 == 0)												// ���݂ɐF�ω�
			    Contents[i].setBackground(new Color(255, 250, 200));	// �w�i�ݒ�
			else
			    Contents[i].setBackground(new Color(255, 255, 255));	// �w�i�ݒ�
			add(Contents[i]);											// �t��
			Contents[i].setBounds(50, BasePoint + i * 20, 400, 20);		// �z�u
		}

		// ���͗̈�ݒ�
		DateField.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		DateField.setBackground(new Color(100, 250, 250));			// �w�i�F
		add(DateField);												// �t���[���ɕt��
		DateField.setBounds(50, 460, 110, 30);						// �z�u�E�T�C�Y�ݒ�
		DateField.setText("yyyy/mm/dd");							// �N����
		DateField.addActionListener(this);							// ���X�i�[�t��

		HourField.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		HourField.setBackground(new Color(100, 250, 250));			// �w�i�F
		add(HourField);												// �t���[���ɕt��
		HourField.setBounds(200, 460, 50, 30);						// �z�u�E�T�C�Y�ݒ�
		HourField.setText("Hour");									// ����
		HourField.addActionListener(this);							// ���X�i�[�t��

		ContentField.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		ContentField.setBackground(new Color(150, 250, 250));		// �w�i�F
		add(ContentField);											// �t���[���ɕt��
		ContentField.setBounds(50, 500, 400, 30);					// �z�u�E�T�C�Y�ݒ�
		ContentField.setText("Content");							// ���e
		ContentField.addActionListener(this);						// ���X�i�[�t��

		// �\���E�ǉ��E�X�V�E�폜�{�^��
		add(DispButton = new Button("Disp"));						// �\���{�^�������t��
		DispButton.setFont(new  Font("System", Font.PLAIN, 20));	// �t�H���g�ݒ�
		DispButton.setBounds(66, 550, 80, 30);						// �z�u�E�T�C�Y�ݒ�
		DispButton.addActionListener(this);							// ���X�i�[�t��

		add(InsertButton = new Button("Insert"));					// �ǉ��{�^�������t��
		InsertButton.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		InsertButton.setBounds(162, 550, 80, 30);					// �z�u�E�T�C�Y�ݒ�
		InsertButton.addActionListener(this);						// ���X�i�[�t��

		add(DeleteButton = new Button("Delete"));					// �폜�{�^�������t��
		DeleteButton.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		DeleteButton.setBounds(258, 550, 80, 30);					// �z�u�E�T�C�Y�ݒ�
		DeleteButton.addActionListener(this);						// ���X�i�[�t��

		add(UpdateButton = new Button("Update"));					// �X�V�{�^�������t��
		UpdateButton.setFont(new  Font("System", Font.PLAIN, 20)); 	// �t�H���g�ݒ�
		UpdateButton.setBounds(354, 550, 80, 30);					// �z�u�E�T�C�Y�ݒ�
		UpdateButton.addActionListener(this);						// ���X�i�[�t��

		// ------------------------------------------------------------------------------
		Delimiter.append('#');						// ���o���p�f���~�^�쐬

		PreparationFlag = 1;						// �����t���O
		repaint( );

		YearMonthDay( );							// �{��DispDay�̔N����
		DateLabel.setText(NowDay( )+NowWeek( ));	// �N����(�j��)�����x���ɐݒ�

		SendMessage = "Select#" + NowDay( ) + "#";	// Select#yyyy/mm/dd#
		System.out.println(SendMessage);
		SendProcess( );								// �T�[�o�ɖ{���̏��̓��͂�v��

		SetMessage( );								// �T�[�o����̏���\��
	}
	// ���b�Z�[�W�ݒ� -------------------------------------------------------------------
	// ReceiveMessage�̓��e���e�L�X�g�t�B�[���h�ɐݒ�
	// ReceiveMessage = "1999/11/6# # # # # #Meeting# # # # # # # # # # # # ";
	void SetMessage( ) {
		int p1, p2;														// �|�C���g
		p1 = ReceiveMessage.indexOf("/", 0);							// �ŏ���/�̈ʒu
		Year = Integer.parseInt(ReceiveMessage.substring(0, p1));		// �N
		p2 = ReceiveMessage.indexOf("/", p1+1);							// ����/�̈ʒu
		Month = Integer.parseInt(ReceiveMessage.substring(p1+1, p2));	// ��
		p1 = p2;
		p2 = ReceiveMessage.indexOf("#", p1+1);							// ���̈ʒu
		Day = Integer.parseInt(ReceiveMessage.substring(p1+1, p2));		// ��

		DateLabel.setText(NowDay( )+NowWeek( ));	// ���t(�j��)�\��

		// Delimiter ��؂蕶�����ŋ�؂�C6������23���܂ł̓��e�����o��
		int c = 0;
		StringTokenizer st =
			new StringTokenizer(ReceiveMessage.substring(p2), Delimiter.toString( ));
		while  (st.hasMoreTokens( ))  {				// �g�[�N��������ԌJ��Ԃ�
			String str = st.nextToken( );			// ���̃g�[�N�������o��
			Contents[c].setText(str);				// ���e�\�����x���ɐݒ�
			c++;
		}
	}
	// �N���� ---------------------------------------------------------------------------
	void YearMonthDay( ) {
		Year = DispDay.get(DispDay.YEAR);			// �N
		Month = DispDay.get(DispDay.MONTH) + 1;		// ��
		Day = DispDay.get(DispDay.DAY_OF_MONTH);	// ��
	}
	// ���t�𕶎��� ---------------------------------------------------------------------
	String NowDay( ) {
		DispDay.set(Year, Month - 1, Day);			// ���t�Z�b�g
		YearMonthDay( );							// DispDay��Year,Month,Day
		return Year+"/"+Month+"/"+Day;
	}
	// �j����Ԃ� -----------------------------------------------------------------------
	String NowWeek( ) {
        String Weeks[ ] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		DispDay.set(Year, Month - 1, Day);			// ���t�Z�b�g
		return "("+Weeks[DispDay.get(Calendar.DAY_OF_WEEK)-1]+")";
	}
	// SendMessage���T�[�o�ɑ��� --------------------------------------------------------
	void SendProcess( ) {
		try {
			String ip = "192.168.1.1";				// �T�[�o��IP�A�h���X
			int port = 8003;						// ����M�|�[�g�ԍ�

			// �\�P�b�g�쐬
			Socket socket = new Socket(ip, port);	// IP�A�h���X�ƃ|�[�g�ԍ��w��

			// �\�P�b�g������̓X�g���[�����쐬
			InputStream inputstream = socket.getInputStream( );
			// ���̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_�쐬
			BufferedReader netinput 
 				= new BufferedReader(new InputStreamReader(inputstream));

			// �\�P�b�g����o�̓X�g���[�����쐬
			OutputStream outputstream = socket.getOutputStream( );
			// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
			PrintStream netoutput = new PrintStream(outputstream);
			// �R���\�[�����̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_���쐬
			BufferedReader consoleinput  =
				new BufferedReader(new InputStreamReader(System.in));

			// �T�[�o�ɖ��߂𑗂�
			netoutput.println(SendMessage);
			netoutput.flush( );						// �o�b�t�@���f�[�^�������I�ɏo��

			// �T�[�o���������M
			try {
				ReceiveMessage = netinput.readLine( );	// 1�s���̃��b�Z�[�W����
			} catch (IOException  e) {					// ���o�͂ɑ΂����O����
				System.out.println("read error");
			}
		} catch(Exception e) {						// �e���O����
			System.out.println(e);
		}
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (PreparationFlag == 1) {					// �����t���O��ON�̏ꍇ
			WorkGraphics.setColor(BackColor);
			WorkGraphics.fillRect(0, 0, FrameWidth, FrameHeight);
			g.drawImage(WorkImage, 0, 0, this);		// ��ƃC���[�W�\��
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
 	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h��` -------------------------------------
 	public void actionPerformed(ActionEvent evt) {
		Button button = (Button)evt.getSource( );
		if (button == BackButton || button == NextButton) {	// �O���E���̓��̏ꍇ
			if (button == BackButton)
				Day--;								// �O��
			else
				Day++;								// ����
			DispDay.set(Year, Month - 1, Day);		// DispDay�̓��t�Z�b�g
			YearMonthDay( );						// Year�CMonth�CDay���擾
			DateLabel.setText(NowDay( )+NowWeek( ));	// ���t(�j��)�\��
			SendMessage = "Select#" + NowDay( ) +"#";	// �w����̏��
			SendProcess( );							// �T�[�o�ɑ��M�C����уT�[�o�����M
			SetMessage( );							// �T�[�o����̏����e�L�X�g�t�B�[���h�ɐݒ�
		}
		else if (button == DispButton) {			// �\��
			SendMessage = "Select#" + DateField.getText( ) + "#";
			SendProcess( );							// �T�[�o�ɑ��M�C����уT�[�o�����M
			SetMessage( );							// �T�[�o����̏����e�L�X�g�t�B�[���h�ɐݒ�
		}
		else if (button == InsertButton) {			// �ǉ��o�^
			SendMessage = "Insert#" + DateField.getText( ) + "#" 
					+ HourField.getText( ) + "#" + ContentField.getText( ) + "#";
			SendProcess( );							// �T�[�o�ɑ��M�C����уT�[�o�����M
			SetMessage( );							// �T�[�o����̏����e�L�X�g�t�B�[���h�ɐݒ�
		}
		else if (button == DeleteButton) {			// �폜
			SendMessage = "Delete#" + DateField.getText( ) + "#" 
					+ HourField.getText( ) + "#" + ContentField.getText( ) + "#";
			SendProcess( );							// �T�[�o�ɑ��M�C����уT�[�o�����M
			SetMessage( );							// �T�[�o����̏����e�L�X�g�t�B�[���h�ɐݒ�
		}
		else if (button == UpdateButton) {			// �X�V
			SendMessage = "Update#" + DateField.getText( ) + "#" 
					+ HourField.getText( ) + "#" + ContentField.getText( ) + "#";
			SendProcess( );							// �T�[�o�ɑ��M�C����уT�[�o�����M
			SetMessage( );							// �T�[�o����̏����e�L�X�g�t�B�[���h�ɐݒ�
		}
	}
	// WindowListener�C���^�[�t�F�[�X�̊e���\�b�h���` ---------------------------------
	public void windowOpened(WindowEvent evt) { }
	public void windowClosing(WindowEvent evt) {
		dispose( );  								// �t���[���̔p��
	}
	public void windowClosed(WindowEvent evt) {
		System.exit(0);								// �v���O�����I��
	}
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }
}
