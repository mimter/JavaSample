import java.awt.*;		// Graphics, Image, Font, Color, Frame, Button, TextField
import java.awt.event.*;// WindowListener, WindowEvent
import java.io.*;		// FileInputStream, BufferedReader, InputStreamReader, IOException
import java.util.StringTokenizer;	// StringTokenizer
import java.util.Random;

class Cai extends Frame implements WindowListener, ActionListener {
	int FrameWidth = 1024, FrameHeight = 768;		// �t���[���T�C�Y

	Label UserIdLabel, PasswordLabel;				// �\�����x��
    TextField UserIdField = new TextField(30);		// ���[�UID���͈�
    TextField PasswordField = new TextField(30);	// �p�X���[�h���͈�
	Button LoginButton;								// ���O�C���{�^��
	String UserId;									// ���[�UID
	String Password;								// �p�X���[�h
	int LoginCount = 0;								// ���O�C����
	String CodedPassword = new String( );			// �Í������ꂽ�p�X���[�h

	String MondaiFile ="cai.txt";					// ���t�@�C��
	int MondaiMax = 1000;							// �w�K��萔(Max 1000)
	int Status[ ] = new int[MondaiMax];				// �w�K�󋵃e�[�u��
	int StatusPoint = 0;							// �w�K�󋵃|�C���g

	int PassBasis = 3;								// ���i��@3��A�������ō��i

	int StudyMax = 5;								// �w�K�T�C�Y�i�J��Ԃ��w�K�����萔�j
	int Study[ ] = new int[StudyMax];				// �w�K�e�[�u���i�w�K�ԍ��i�[�j
	int StudyPoint = 0;								// �w�K�|�C���g

	int LineMax = 20;								// �ő�i�[�s��
	String Mondai[][]
				= new String[StudyMax][LineMax+1];	// ���e�[�u���i��蕶���i�[�j
	int KaitougunMax = 30;							// �𓚌Q�̍ő吔
	int KakkoMondaiMax = 20;						// �i�@�j���̍ő吔

	String Contents[] = new String[LineMax];		// ��蕶
	String Kaitougun[] = new String[KaitougunMax];	// �𓚌Q
	int Seikai[] = new int[MondaiMax];				// ����ԍ�
	int LineNo;										// �s�J�E���^
	int	KakkoMondaiCount;							// �i�@�j���J�E���^
	int	KaitougunCount;								// �𓚌Q�J�E���^
	StringBuffer Delimiter = new StringBuffer( );	// ��؂蕶��
	TextField Kaitouran[] = new TextField[KakkoMondaiMax];// �𓚗�

	Button SaitenButton, StudyButton, EndButton;	// �e��{�^��
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	Color BackColor = new Color(0, 140, 140);		// �w�i�F

	FileInputStream MondaiFis;						// ���t�@�C�����̓X�g���[��
	BufferedReader MondaiBr;						// �o�b�t�@�[�h���̓X�g���[��

	boolean StudyFlag = false;						// �w�K���t���O�@�����͕s��
	boolean StudyFinishFlag = false;				// �w�K�����t���O

	// ���C������ -----------------------------------------------------------------------
	public static void main(String args[ ]) {
		Cai frame = new Cai("Cai");					// �t���[���쐬
		frame.init( );								// ����������
	}
	// �R���X�g���N�^ -------------------------------------------------------------------
	public Cai(String title) {
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
		setBackground(BackColor);

		// ���O�C����ʗp�f�t�h ---------------------------------------------------------
		setLayout(null);							// ���R���C�A�E�g�ݒ�
        UserIdLabel = new Label("UserID");
		UserIdLabel.setForeground(Color.white);		// �����J���[�ݒ�
		UserIdLabel.setFont(new Font("courier", Font.BOLD, 20));// ���͕����̃t�H���g�ݒ�
		UserIdLabel.setBounds(FrameWidth/2-150, FrameHeight*1/4, 150, 30);
		add(UserIdLabel);

		UserIdField.setBackground(Color.white);		// �w�i�J���[�ݒ�
		UserIdField.setFont(new Font("courier", Font.BOLD, 20));
		UserIdField.setBounds(FrameWidth/2, FrameHeight*1/4, 200, 30);
		add(UserIdField);
		UserIdField.requestFocus( );				// ���̓t�H�[�J�X��v��

        PasswordLabel = new Label("Password");
		PasswordLabel.setForeground(Color.white);	// �����J���[�ݒ�
		PasswordLabel.setFont(new Font("courier", Font.BOLD, 20));
		PasswordLabel.setBounds(FrameWidth/2-150, FrameHeight*2/4, 150, 30);
		add(PasswordLabel);

		PasswordField.setBackground(Color.white);	// �w�i�J���[�ݒ�
		PasswordField.setFont(new Font("courier", Font.BOLD, 20));
		PasswordField.setBounds(FrameWidth/2, FrameHeight*2/4, 200, 30);
		add(PasswordField);
		PasswordField.setEchoChar('*');

		add(LoginButton = new Button("���O�C��"));
		LoginButton.setFont(new  Font("System", Font.PLAIN, 20));
		LoginButton.setBounds((FrameWidth - 100)/2, FrameHeight*3/4, 100, 30);
		LoginButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�

		// �w�K��ʗp�f�t�h -------------------------
		add(StudyButton = new Button("�@�w�K�@"));
		StudyButton.setFont(new  Font("System", Font.PLAIN, 20));
		StudyButton.setBounds(50, 50, 100, 30);
		StudyButton.addActionListener(this);
		StudyButton.setVisible(false);

		add(SaitenButton = new Button("�@�̓_�@"));
		SaitenButton.setFont(new  Font("System", Font.PLAIN, 20));
		SaitenButton.setBounds((FrameWidth - 100) / 2, 50, 100, 30);
		SaitenButton.addActionListener(this);
		SaitenButton.setVisible(false);

		add(EndButton = new Button("�@�I���@"));
		EndButton.setFont(new  Font("System", Font.PLAIN, 20));
		EndButton.setBounds(FrameWidth - 150, 50, 100, 30);
		EndButton.addActionListener(this);
		EndButton.setVisible(false);

		// �𓚗��ݒ� -------------------------
		for (int i = 0; i < KakkoMondaiMax; i++) {
			Kaitouran[i] = new TextField(10);
			Kaitouran[i].setFont(new  Font("System", Font.PLAIN, 20));
		    Kaitouran[i].setBackground(new Color(200, 220, 200));
			add(Kaitouran[i]);
			Kaitouran[i].addActionListener(this);
			Kaitouran[i].setBounds(FrameWidth - 100, 127 + i * 30, 50, 20);
			Kaitouran[i].setVisible(false);
		}
	}
	// ���O�C���`�F�b�N���� -------------------------------------------------------------
	public void LoginCheck( ) {
		LoginCount++;								// ���O�C���J�E���g

		UserId = UserIdField.getText( );			// ���[�UID�擾
		Password = PasswordField.getText( );		// �p�X���[�h�擾

		// ���͂����p�X���[�h���烉���_�������̃V�[�h�쐬
		long seed = 0;								// �����_�������V�[�h�쐬
		for (int i = 0; i < Password.length( ); i++)
			seed += (long)(Password.charAt(i));
		Random rand = new Random(seed);				// �����W�F�l���[�^����
		// �p�X���[�h����쐬���������_�������V�[�h�𗘗p����
		// ���[�UID���Í������ꂽ�p�X���[�h�ɕϊ�
		CodedPassword = "";							// �Í����p�X���[�h������
		for (int i = 0; i < UserId.length( ); i++) {
			char c = (char)(UserId.charAt(i) + rand.nextInt( ) % 20);
			CodedPassword += String.valueOf(c);
		}

		// �w�K�t�@�C���w���[�UID�x�𒲂ׂ�
         File file = new File(UserId);				// �t�@�C���I�u�W�F�N�g����

         if (file.exists( ) == true) {				// �w�K�t�@�C���w���[�UID�x������ꍇ
	        try {
				// ���̓X�g���[��fis���쐬
				FileInputStream fis = new FileInputStream(UserId);
				// ���̓X�g���[������o�C�i���`���œ��͂��邽�߂̃f�[�^���̓X�g���[���쐬
				DataInputStream dis = new DataInputStream(fis);

				String InputPassword = new String( );
				// �쐬���ꂽ�Í����p�X���[�h��UserId�Ɠ�������
				for (int i = 0; i < UserId.length( ); i++)
					InputPassword += String.valueOf(dis.readChar( ));	// �p�X���[�h����

				if (CodedPassword.compareTo(InputPassword) == 0) {		// ��v����ꍇ
					// ���̃f�[�^���炷�ׂĂ���͂��Ċw�K�󋵃e�[�u���ɕۑ�����
					try {
						MondaiMax = 0;								// ��萔�O�N���A
						while (true) {
				        	Status[MondaiMax] = dis.readInt( );		// �w�K�󋵓���
							MondaiMax++;							// ��萔�J�E���g
						}
					} catch(EOFException e) {		// �t�@�C���̏I�[�ɒB�����ꍇ�̏���
					}
					StudyFlag = true;				// �w�K����
					ScreenClear( );					// ��ʃN���A
					StudyStart( );					// �w�K�X�^�[�g
				}
				else {								// ��v���Ȃ��ꍇ
					// ���͈���N���A
					UserIdField.setText("");
					PasswordField.setText("");
					UserIdField.requestFocus( );	// ���̓t�H�[�J�X��v��

					// �ēx���[�UID�ƃp�X���[�h����͂���
					if (LoginCount == 3) {			// 3��ԈႦ���ꍇ�C�I��
						dis.close( );				// ���̓X�g���[�����N���[�Y
						fis.close( );				// ���̓X�g���[�����N���[�Y
						dispose( );					// �t���[���̔p��
						System.exit(0);				// �v���O�����I��
					}
				}
				dis.close( );						// ���̓X�g���[�����N���[�Y
				fis.close( );						// ���̓X�g���[�����N���[�Y
		    } catch(IOException e) {				// ���o�͂ɑ΂����O����
	            System.err.println(e);
    		}
		}
		else {		// �w�K�t�@�C���w���[�UID�x���Ȃ��ꍇ�C�V�K���[�U
			StudyFlag = true;						// �w�K����
			MondaiCount( );							// ��萔�J�E���g
			ScreenClear( );							// ��ʃN���A
			StudyStart( );							// �w�K�X�^�[�g
		}
	}
	// �w�K�e�X�g������ -----------------------------------------------------------------
	void MondaiCount( ) {
		MondaiMax = 0;								// ��萔�O�N���A
        try {
			// ���̓X�g���[��fis���쐬
			FileInputStream fis = new FileInputStream(MondaiFile);
			// ���̓X�g���[������s�P�ʂɓ��͂��邽�߂̃o�b�t�@�h���[�_�쐬
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			try {
				String str;
				while ((str = br.readLine( )) != null) {// �s�P�ʂɓ���
					if (str.compareTo("�^") == 0)	// �e�X�g�̋�؂�`�F�b�N
						MondaiMax++;				// ��萔�J�E���g�A�b�v
				}
		    } catch(IOException e) {				// ���o�͂ɑ΂����O����
	            System.err.println(e);
    		}
            br.close( );							// �o�b�t�@�h���[�_���N���[�Y
			fis.close( );							// ���̓X�g���[�����N���[�Y
        } catch (IOException e) {					// ���o�͂ɑ΂����O����
            System.err.println(e);
        }
	}
	// �w�K�J�n���� ---------------------------------------------------------------------
	public void StudyStart( ) {
		// �w�K�f�t�h�@�\��
		StudyButton.setVisible(true);
		SaitenButton.setVisible(true);
		EndButton.setVisible(true);

		Delimiter.append('�C');						// �𓚌Q�̎��o���p�f���~�^�ǉ�

		// ���t�@�C���I�[�v��
		try {
			MondaiFis = new FileInputStream(MondaiFile);
			MondaiBr = new BufferedReader(new InputStreamReader(MondaiFis));
	    } catch(FileNotFoundException e) {			// ���o�͂ɑ΂����O����
            System.err.println(e);
   		}

		// �w�K�e�[�u���i�w�K�ԍ��i�[)��-1�ŃN���A
		for (int i = 0; i < StudyMax; i++)
			Study[i] = -1;

		study( );									// �w�K

	}
	// �w�K -----------------------------------------------------------------------------
	void study( ) {
		int line = 0;								// ��蕶�̍s�J�E���g

		if (Study[StudyPoint] == -1) {				// �w�K�ԍ����o�^����Ă��Ȃ��ꍇ
			// ���w�K�̖���T��
			boolean foundflag = false;				// ���w�K�̖�茟�o�t���O
			do {
				// �w�K�������̖�蕶���N���A
				for(int i = 0; i < LineMax; i++)
					Mondai[StudyPoint][i] = "";

				// ��蕶���w�K�ɃZ�b�g
				line = 0;
				try {
					String str;
					while ((str = MondaiBr.readLine( )) != null) {  // �s�P�ʂɓ���
						Mondai[StudyPoint][line++] = str;	// ��蕶���Z�b�g
						if (str.compareTo("�^") == 0)		// ���̋�؂�`�F�b�N
							break;
					}
			    } catch(IOException e) {			// ���o�͂ɑ΂����O����
    			}
				// ��蕶�̃t�@�C�����V�[�P���V�����t�@�C���ł��邽��
				// �w�K�󋵂Ɋ֌W�Ȃ����͂���
				if (line > 0) {						// ��蕶����͂ł����ꍇ
					if(Status[StatusPoint] < PassBasis) {// �w�K�󋵂����i������̏ꍇ
						Study[StudyPoint] = StatusPoint; // �w�K�󋵔ԍ����w�K�e�[�u����
						foundflag = true;			// ���B���̖���������
					}
					StatusPoint++;					// �w�K�󋵔ԍ����J�E���g�A�b�v
				}

				// ���͂ł��������B���̖�肪������Ȃ���
			} while (line > 0 && foundflag == false);

			// --------------------------------------------------------------------------

			if (line == 0) {						// ��肪�Ȃ������ꍇ
				// �Ō�܂Œ��ׂ����C���i�������������Ȃ�����
				// ���̏ꍇ�C���݊w�K����Study���ō��i����������邩���ׂ�
				// ����ł��Ȃ��ꍇ�́C�w�K�����Ƃ���

				int StudyPointKeep = StudyPoint;	// ���݂�StudyPoint���o���Ă���
				do {
					StudyPoint++;					// ���𒲂ׂ�
					if (StudyPoint == StudyMax)		// ���X�g�̎��͐擪
						StudyPoint = 0;
					if (Study[StudyPoint] != -1)	// �w�K�ԍ����o�^����Ă���ꍇ
						break;
				} while (StudyPoint != StudyPointKeep);	// �ŏ���StudyNoPoint�łȂ��ꍇ

				if (StudyPoint == StudyPointKeep) {	// �ŏ���StudyPoint�̏ꍇ
					// �w�K�C��
					StudyFinishFlag = true;
					studysave( );					// �w�K���e��ۑ�
					ScreenClear( );					// ��ʃN���A
					WorkGraphics.drawString("�S�w�K���i�@�C��",
											FrameWidth/2-100, FrameHeight/2);
					repaint( );						// �ĕ`��
					return;
				}
			}
		}

		// ��蕶�\��
		MondaibunDisp( );

		// �𓚗��\��
		KaitouranDisp( );

		repaint( );
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (StudyFlag == true) {					// �w�K�t���O���I���̏ꍇ
			g.drawImage(WorkImage, 0, 0, this);		// ��ƃC���[�W�\��
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// ��ʃN���A -----------------------------------------------------------------------
	void ScreenClear( ) {
		// ���O�C���f�t�h��\��
		UserIdLabel.setVisible(false);
		PasswordLabel.setVisible(false);
	    UserIdField.setVisible(false);
		PasswordField.setVisible(false);
		LoginButton.setVisible(false);
		// �w�K�f�t�h��\��
		StudyButton.setVisible(false);
		SaitenButton.setVisible(false);
		EndButton.setVisible(false);
		for (int i = 0; i < KakkoMondaiMax; i++)
			Kaitouran[i].setVisible(false);
		// ��ʂ�w�i�F�ŃN���A
		WorkGraphics.setColor(BackColor);
		WorkGraphics.fillRect(0, 0, FrameWidth, FrameHeight);
		WorkGraphics.setColor(new Color(255, 255, 255));
	}
	// ��蕶��\�� ---------------------------------------------------------------------
	void MondaibunDisp( ) {
		// �w�i�F�ŉ�ʃN���A
		WorkGraphics.setColor(BackColor);
		WorkGraphics.fillRect(0, 0, FrameWidth, FrameHeight);
		WorkGraphics.setColor(new Color(255, 255, 255));

		LineNo = 0;									// �s�ԍ�
		KakkoMondaiCount = 0;						// �i�@�j��萔
		KaitougunCount = 0;							// �𓚌Q�̐�

		for (int line=0; line < LineMax && KakkoMondaiCount < KakkoMondaiMax; line++){

			String str = Mondai[StudyPoint][line];	// StudyPoint�̖���line�s�̕�
			if (str.compareTo("�^") == 0)			// ��؂�̏ꍇ 
				break;								// ���[�v�E�o
			if (str.compareTo("") == 0) {			// �k���̏ꍇ
			LineNo++;								// �\���s�ԍ����J�E���g�A�b�v
				continue;							// ��΂�
			}

			// str�̒��Ɂi�@�����邩�`�F�b�N
			int n, p;
			Contents[LineNo] = " ";					// ��蕶�̊e�s��������
			n = 0;									// �ҏW�J�n�ʒun
			while ((p = str.indexOf("�i", n)) >= 0) {	// n�Ԗڂ���( ��T��
			    String str2 = str.substring(n, p);	// n�Ԗڂ���( �̎�O�܂ł����o��

				Contents[LineNo] = Contents[LineNo]
								 + str2 + "  ( " + (KakkoMondaiCount+1) + " )  ";
				n = str.indexOf("�j", p) + 1;		// ) ��T��

				StringTokenizer st = new StringTokenizer(str.substring(p+1, n-1), 
														Delimiter.toString( ));

				int FirstSetFlag = 1;				// �擪�̐������Z�b�g���邽�߂̃t���O
				while  (st.hasMoreTokens( ))  {		// �g�[�N��������ԌJ��Ԃ�
					String s = st.nextToken( );		// ���̃g�[�N�������o��
					int flag = 0;					// ����𓚃t���O
					for (int i = 0; i < KaitougunCount; i++) // �o�^����Ă���𓚌Q�𒲍�
						if (s.compareTo(Kaitougun[i]) == 0) {	// �o�^����Ă���ꍇ
							if (FirstSetFlag == 1) {// ( )���̉𓚌Q�̐擪�̏ꍇ
								Seikai[KakkoMondaiCount] = i;	// ����ԍ����Z�b�g
								FirstSetFlag = 0;	// �������Z�b�g�ςƂ���
							}
							flag = 1;				// �o�^��
						}
					if (flag == 0) {				// �o�^����Ă��Ȃ��ꍇ
						if (FirstSetFlag == 1) {	// ( )���̉𓚌Q�̐擪�̏ꍇ
							Seikai[KakkoMondaiCount] = KaitougunCount;// ����ԍ����Z�b�g
							FirstSetFlag = 0;		// �������Z�b�g�ςƂ���
						}
						Kaitougun[KaitougunCount] = s;	// �𓚌Q�ɃZ�b�g
						KaitougunCount++;			// �𓚌Q�̐����J�E���g�A�b�v
					}
				}
				KakkoMondaiCount++;					// �i�@�j��萔�J�E���g�A�b�v
			}
			if (p == -1) {							// �i�@�̌��o���Ȃ��Ȃ����ꍇ
				// n�Ԗڂ���Ō�܂ł��蕶�ɉ�����
				Contents[LineNo] += str.substring(n);
			}

			// ��蕶�\��
			WorkGraphics.drawString(Contents[LineNo], 50, LineNo*24 + 120);
			LineNo++;								// �\���s�ԍ����J�E���g�A�b�v
		}
		// �𓚌Q�������_���Ɍ����C�����ɐ���ԍ����ύX ---------------------------------
		for (int i = 0; i < KaitougunCount; i++) {
			// �𓚌Q��i�Ԗڂ�w�Ԗڂ�����
			int w = (int)(Math.random( )*KaitougunCount);
			String strtemp = Kaitougun[i];
			Kaitougun[i] = Kaitougun[w];
			Kaitougun[w] = strtemp;

			// �����ɂ��Ƃ�������΁C���ꂼ��̒l��ς���
			for (int n = 0; n < KakkoMondaiCount; n++) {
				if (Seikai[n] == i)
					Seikai[n] = w;
				else if (Seikai[n] == w)
					Seikai[n] = i;
			}
		}
		// �𓚌Q�\�� -------------------------------------------------------------------
		LineNo++;
		WorkGraphics.drawString("�𓚌Q", 50, LineNo*24 + 120);	// ��蕶�\��
		LineNo++;
		int Column = 0;								// �\���J����
		for (int i = 0; i < KaitougunCount; i++) {
			String KaitouStr = "("+(i+1)+")"+Kaitougun[i];

			// �𓚌Q�\��
			WorkGraphics.drawString(KaitouStr, 50+Column*18, LineNo*24 + 120);

			Column = Column + KaitouStr.length( );
			if (Column > 30) {						// ���ɕ\������ő啶�����ȏ�̏ꍇ
				LineNo++;							// �\���s�����̍s�Ƃ���
				Column = 0;							// �\���J�������O�N���A
			}
		}
	}
	// �𓚗��\�� -----------------------------------------------------------------------
	void KaitouranDisp( ) {
		WorkGraphics.drawString("�𓚗�", FrameWidth - 150, 120);

		for (int i = 0; i < KakkoMondaiCount; i++) {	// �i�@�j���̐��\��
			WorkGraphics.drawString("(" + (i+1) + ")",
					 FrameWidth - 150, i*30 + 145);	// (1)�`�\��
			Kaitouran[i].setText("");				// ���e�N���A
			Kaitouran[i].setVisible(true);			// �\��
		}
		for (int i = KakkoMondaiCount; i < KakkoMondaiMax; i++) {	// �c��͔�\��
			Kaitouran[i].setVisible(false);
		}

		Kaitouran[0].requestFocus( );				// ���̓t�H�[�J�X��v��
	}
	// �̓_ -----------------------------------------------------------------------------
	void saiten( ) {
		// �̓_���ʕ\�����w�i�F�ŃN���A
		WorkGraphics.setColor(BackColor);
		WorkGraphics.fillRect(960, 100, 1000, 600);
		WorkGraphics.setColor(new Color(255, 255, 255));

		boolean PerfectFlag = true;					// �S�␳���t���O�I���@���ݒ�

		for (int i = 0; i < KakkoMondaiCount; i++) {// �i�@�j���̐����[�v
			int value;
	        try {
				// ���͂����𓚗��̒l�𐮐���
				value = Integer.parseInt(Kaitouran[i].getText( ));
	        } catch (NumberFormatException e) {		// �����ȊO�̏ꍇ
    	        value = 0;							// ����0�Ƃ���
        	}
			if (value == Seikai[i]+1)				// �\����(1)�`����Ƃ������߁C�{�P
				WorkGraphics.drawString("��", FrameWidth - 40, i*30 + 145);	// ����
			else {
				WorkGraphics.drawString("�~ "+(Seikai[i]+1),
											  FrameWidth - 40, i*30 + 145);	// �s����
				PerfectFlag = false;				// �S�␳���t���O�@�I�t
			}
		}
		if (PerfectFlag == true) {					// �S�␳���̏ꍇ
			Status[Study[StudyPoint]]++;			// �w�K�J�E���g�A�b�v
			if (Status[Study[StudyPoint]] >= PassBasis)	// ���i��ɒB�����ꍇ
				Study[StudyPoint] = -1;				// �w�K�ԍ����N���A
		}
		else
			Status[Study[StudyPoint]] = 0;			// �w�K�J�E���g�N���A

		StudyPoint++;								// �w�K�ԍ������ɂ���
		if (StudyPoint == StudyMax)					// ���X�g�̎��̏ꍇ
			StudyPoint = 0;							// �w�K�ԍ���擪�ɂ���

		repaint( );									// �̓_���ʂ��ĕ`��
	}
	// �w�K���e���L�^ -------------------------------------------------------------------
	void studysave( ) {
        try {
			// �w�K�t�@�C���ɑ΂��ďo�̓X�g���[�����쐬
            FileOutputStream fos = new FileOutputStream(UserId);
			// �o�̓X�g���[���Ƀo�C�i���`���ŏo�͂��邽�߂̃f�[�^�o�̓X�g���[�����쐬
            DataOutputStream dos = new DataOutputStream(fos);

			dos.writeChars(CodedPassword);			// �Í������ꂽ�p�X���[�h�o��

	        for (int i = 0; i < MondaiMax; i++)
	            dos.writeInt(Status[i]);			// �w�K�󋵏o��

			dos.close( );							// �f�[�^�o�̓X�g���[�����N���[�Y

		} catch (IOException e) {					// ���o�͂ɑ΂����O����
			System.err.println(e);
		}
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h��` -------------------------------------
 	public void actionPerformed(ActionEvent evt) {
		Button button = (Button)evt.getSource( );

		if (button == LoginButton) {				// ���O�C��
			LoginCheck( );							// ���O�C���`�F�b�N
		}
		if (button == StudyButton) {				// �w�K
			study( );
		}
		if (button == SaitenButton) {				// �̓_
			saiten( );
		}
		if (button == EndButton) {					// �I��
			studysave( );							// �w�K�L�^
			dispose( );  							// �t���[���̔p��
			System.exit(0);							// �v���O�����I��
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
