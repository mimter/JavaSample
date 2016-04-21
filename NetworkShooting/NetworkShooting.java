import java.net.*;			// Socket, ServerSocket
import java.io.*;			// InputStream, InputStreamReader, BufferedReader,
							// IOException, OutputStream, PrintStream
import java.awt.*;			// Frame, Graphics, Image, Color, Font, Toolkit, MediaTracker,
							// Button, TextField, Checkbox, CheckboxGroup
import java.awt.event.*;	// MouseListener, MouseEvent, MouseMotionListener, 
							// WindowEvent, WindowListener, ActionListener, ActionEvent
public class NetworkShooting extends Frame 
			implements Runnable, ActionListener, 
					   MouseListener, MouseMotionListener, WindowListener {
													// �e�C���^�[�t�F�[�X����
	Thread thread = null;							// �X���b�h
	Image BaseImage;								// ��{�Q�[����ʃC���[�W
	Image BackImage;								// �w�i��ʃC���[�W
	Image UFOImage[ ] = new Image[2];				// UFO�摜�C���[�W
	Image BakudanImage;								// ���e�摜�C���[�W
	Image ExplodeImage[ ] = new Image[5];			// �����C���[�W

	Graphics BaseGraphics;							// �x�[�X�O���t�B�b�N�X
	MediaTracker mt = new MediaTracker(this);		// �摜���͂��Ď����郁�f�B�A�g���b�J

	int UfoNo;										// ������UFO�ԍ�
	int UfoStatus[ ] = new int[10];					// �S�̂�UFO���
	int MyUfoStatus;								// ������UFO���
													// 1�F���݁C2 ���� 6 �܂ł͔������
	int Team[ ] = new int[10];						// �eUFO�̃`�[��
													// 0: Red team  1: Blue team
	int Existence[ ] = new int[2];					// �e�`�[���̑��ݓx����
	int MyTeam;										// ������UFO�̃`�[��
	int UfoMax = 10;								// UFO�̍ő吔
	int UfoX[ ] = new int[UfoMax];					// ���݂�UFO�̈ʒu
	int UfoY[ ] = new int[UfoMax];
	int UfoXBase[ ] = new int[UfoMax];				// �O���UFO�̈ʒu
	int UfoYBase[ ] = new int[UfoMax];
	int Direction[ ] = new int[UfoMax];				// UFO����̔��e���˕���
	int BakudanMax = 3;								// ���e��
	int GameInfoLen = 11 + BakudanMax * 7;			// 1�Q�[�����̒���
	int BakudanWay[ ][ ] = new int[UfoMax][BakudanMax];	// ��s���̔��e����
	int BakudanX[ ][ ] = new int[UfoMax][BakudanMax];	// ��s���̔��e�ʒu
	int BakudanY[ ][ ] = new int[UfoMax][BakudanMax];
	int BackImageWidth, BackImageHeight;			// �w�i�摜�T�C�Y
	int UFOImageWidth, UFOImageHeight;				// UFO�摜�T�C�Y
	int BakudanImageWidth, BakudanImageHeight;		// ���e�摜�T�C�Y
	int BakudanStatus = 0;							// ���e��
													//  0:����OK,1:����,2:���ˈʒu�ݒ�
	int FrameWidth = 500;							// �t���[����
	int FrameHeight = 500;							// �t���[������
	int UfoCount;									// UFO�̐�(MAX:10)

	TextField IpAddressField;
	CheckboxGroup CheckBoxPartGroup, CheckBoxTeamGroup;
	Checkbox CheckBoxAdmini, CheckBoxPlayer, CheckBoxRedTeam, CheckBoxBlueTeam;
	Button OkButton;
	String NextUserIpAddress;

	boolean AdministratorFlag;						// 0:Player, 1:Administrator

	boolean GameStartFlag = false;					// �Q�[���X�^�[�g�t���O
	boolean GameOverFlag = false;

	int port = 7000;								// �ʐM�|�[�g
	PrintStream NetOutput;							// ���M�p�X�g���[��
	BufferedReader NetInput;						// �C���^�[�t�F�[�X����
	String GameInformation;							// �Q�[�����
	String NewGameInformation;						// �V�Q�[�����
	String ReceiveInformation;						// ��M�Q�[�����
	String SendInformation;							// ���M�Q�[�����

	// ���C��----------------------------------------------------------------------------
	public static void main(String args[ ]) {
		NetworkShooting game;						// �N���X�I�u�W�F�N�g
		game = new NetworkShooting("Network shooting game");
		game.init( );								// ������
		game.start( );								// �X�^�[�g
		game.run( );								// �X���b�h�̎��s
		game.stop( );								// �I������
		game.ResultDisplay( );						// ���ʕ\��
	}
	// �t���[���쐬 ---------------------------------------------------------------------
	public NetworkShooting(String title) {
		setTitle(title);
	}
	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �t���[���ݒ�
		setBackground(Color.white);
		setForeground(Color.black);
		setSize(FrameWidth, FrameHeight);			// �t���[���T�C�Y�ݒ�
		show( );									// �t���[���\��

		BaseImage = createImage(FrameWidth, FrameHeight);
		BaseGraphics = BaseImage.getGraphics( );

		BackImage = Toolkit.getDefaultToolkit( ).getImage("./image/sky.jpg");
		mt.addImage(BackImage, 0);					// ���f�B�A�g���b�J�ɉ摜���Z�b�g
		for (int i = 0; i < 2; i++) {
			UFOImage[i] = 
				Toolkit.getDefaultToolkit( ).getImage("./image/ufo" + i + ".gif");
			mt.addImage(UFOImage[i], 0);			// ���f�B�A�g���b�J�ɉ摜���Z�b�g
		}
		BakudanImage = 
				Toolkit.getDefaultToolkit( ).getImage("./image/bakudan.gif");
		mt.addImage(BakudanImage, 0);				// ���f�B�A�g���b�J�ɉ摜���Z�b�g
		for (int i = 0; i < 5; i++) {
			ExplodeImage[i] = 
				Toolkit.getDefaultToolkit( ).getImage("./image/explosion" + i + ".gif");
			mt.addImage(ExplodeImage[i], 0);		// ���f�B�A�g���b�J�ɉ摜���Z�b�g
		}
		try {
			mt.waitForID(0);						// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			System.out.println(e);
		}
		BackImageWidth = BackImage.getWidth(this);			// �w�i�T�C�Y
		BackImageHeight = BackImage.getHeight(this);
		UFOImageWidth = UFOImage[0].getWidth(this);			// UFO�T�C�Y
		UFOImageHeight = UFOImage[0].getHeight(this);
		BakudanImageWidth = BakudanImage.getWidth(this);	// Bakudan�T�C�Y
		BakudanImageHeight = BakudanImage.getHeight(this);

		for (int i = 0; i < UfoMax; i++)
			UfoStatus[i] = -1;						// UFO�����݂��Ȃ��Ə����ݒ�

		OpeningDisplay( );
 	}
	// �Q�[���ݒ� -----------------------------------------------------------------------
	void OpeningDisplay( ) {
		setLayout(null);
		BaseGraphics.setColor(Color.black);
		BaseGraphics.setFont(new Font("TimesRoman", Font.BOLD, 30));
		BaseGraphics.drawString("Network Shooting Game", 52, 62);	// �e
		BaseGraphics.setColor(Color.red);
		BaseGraphics.drawString("Network Shooting Game", 50, 60);

		BaseGraphics.setColor(Color.black);
		BaseGraphics.setFont(new Font("TimesRoman", Font.BOLD, 12));
		BaseGraphics.drawString(
			"One administrator is necessary to play this game.", 60, 110);
		BaseGraphics.drawString(
			"The administrator is a starter, player, and checker of the gameover.", 
			60, 130);

		BaseGraphics.setColor(Color.black);
		BaseGraphics.setFont(new Font("TimesRoman", Font.BOLD, 18));
		BaseGraphics.drawString("Which part do you take in the game?", 80, 170);
		CheckBoxPartGroup = new CheckboxGroup( );
		CheckBoxAdmini = new Checkbox("Administrator", CheckBoxPartGroup, false);
		CheckBoxPlayer = new Checkbox("Player", CheckBoxPartGroup, true);
		add(CheckBoxAdmini);		CheckBoxAdmini.setBounds(100, 180, 100, 20);
		add(CheckBoxPlayer);		CheckBoxPlayer.setBounds(200, 180, 100, 20);

		BaseGraphics.drawString("Which team do you take?", 80, 230);
		CheckBoxTeamGroup = new CheckboxGroup( );
		CheckBoxRedTeam = new Checkbox("Red", CheckBoxTeamGroup, true);
		CheckBoxBlueTeam = new Checkbox("Blue", CheckBoxTeamGroup, false);
		add(CheckBoxRedTeam);		CheckBoxRedTeam.setBounds(100, 240, 100, 20);
		add(CheckBoxBlueTeam);		CheckBoxBlueTeam.setBounds(200, 240, 100, 20);

		BaseGraphics.drawString("What is the IP-address of next player ?", 80, 290);
		IpAddressField = new TextField(15);
		add(IpAddressField);	IpAddressField.setBounds(100, 300, 100, 20);

		OkButton = new Button("OK");
		add(OkButton);		OkButton.setBounds(80, 340, 50, 20);
		OkButton.addActionListener(this);			// ���X�i�[�ݒ�

 		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i�[�ǉ�
		addWindowListener(this);					// �E�B���h�E���X�i�[�t��

		repaint( );
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
	public void actionPerformed(ActionEvent evt) {	// �C�x���g����
		Button button = (Button)evt.getSource( );	// �C�x���g�\�[�X

		if (button == OkButton) {

			if(CheckBoxAdmini.getState( ) == true)
				AdministratorFlag = true;			// Administrator
			else
				AdministratorFlag = false;			// Player

			if(CheckBoxRedTeam.getState( ) == true)
				MyTeam = 0;							// Red team
			else
				MyTeam = 1;							// Blue team

			NextUserIpAddress = IpAddressField.getText( );

			remove(OkButton);
			remove(CheckBoxAdmini);
			remove(CheckBoxPlayer);
			remove(CheckBoxRedTeam);
			remove(CheckBoxBlueTeam);
			remove(IpAddressField);

			if (AdministratorFlag == true) {		// �Ǘ��҂̏ꍇ
				// �Ǘ��҂̏ꍇ ---------------------------------------------------------
				GameInformation = "";
				InitialGameInformationSet( );		// �����Q�[�����Z�b�g

				SendConnectProcess( );				// ���M�ڑ�
				SendGameInformation( );				// �Q�[����񑗐M

				ReceiveConnectProcess( );			// ��M�ڑ�

				// ��ʂ�1�T���āC�Q�[���Q���҂̃f�[�^����M
				ReceiveGameInformation( );			// �Q�[������M
				GameInformationMemorySet( );		// �Q�[�������������ɃZ�b�g
				NewGameInformation = GameInformation;	// �Q�[�����𑗐M���ɃZ�b�g
				SendGameInformation( );				// �Q�[����񑗐M
			}
			else {
				// �Q���ҁiPlayer�j�̏ꍇ -----------------------------------------------
				ReceiveConnectProcess( );			// ��M�ڑ�
				ReceiveGameInformation( );			// �Q�[��������
				InitialGameInformationSet( );		// �����Q�[�����Z�b�g

				SendConnectProcess( );				// ���M�ڑ�
				SendGameInformation( );				// �Q�[����񑗐M
			}
		GameStartFlag = true;						// �Q�[���X�^�[�g�t���O�I��
		}
	}
	// ���M�ڑ� -------------------------------------------------------------------------
	public void SendConnectProcess( ) {
		try {
			// �\�P�b�g�쐬
			Socket socket = new Socket(NextUserIpAddress, port);

			// �\�P�b�g����o�̓X�g���[�����쐬
			OutputStream outputstream = socket.getOutputStream( );
			// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
			NetOutput = new PrintStream(outputstream);

		} catch(Exception e) {						// �e���O����
			System.out.println("Send connect error : " + e);
		}
	}
	// ��M�ڑ� -------------------------------------------------------------------------
	public void ReceiveConnectProcess( ) {
		try {
			ServerSocket server_socket = new ServerSocket(port);

			// accept( )�ŃN���C�A���g����̐ڑ���҂�,socket���쐬
			Socket socket = server_socket.accept( );

			// �\�P�b�g������̓X�g���[�����쐬
			InputStream inputstream = socket.getInputStream( );
			// ���̓X�g���[������f�[�^���̓X�g���[���쐬
			NetInput = new BufferedReader(new InputStreamReader(inputstream));

		} catch(Exception e) {						// �e���O����
			System.out.println("Receive connect error : " + e);
		}
	}
	// �Q�[����񑗐M -------------------------------------------------------------------
	public void SendGameInformation( ) {
		SendInformation = NewGameInformation;
		try {
			NetOutput.println(SendInformation);
			NetOutput.flush( );
		} catch (Exception e) {						// �e���O����
			System.out.println("Send error!");
		}
	}
	// �Q�[������M -------------------------------------------------------------------
	public void ReceiveGameInformation( ) {
		// ���̓o�b�t�@�Ƃ��Ďg�p����ϐ��́C���݂͂̂Ŏg�p���邱�ƁB
		// ���̏������s���Ă���Œ��ɁC���͏����͓��ʂɍs����B
		// ���������āC���̏����̓r���Ő؂�ւ��\��������B
		// ���̓o�b�t�@�����ӂɂ���̂́C���̏ꏊ�݂̂Ƃ���B
		try {
			ReceiveInformation = NetInput.readLine( );
		} catch (IOException e) {					// ���o�͂ɑ΂����O����
			System.out.println("Receive error!");
		}
		GameInformation=ReceiveInformation;
	}

	//�@�Q�[�����(GameInformation)
	//�@/U010100200110020023003003200200/U1 �` /U2 �` /U3 �`
	//�@/U  : �f�[�^�̍ŏ���\��
	//�@0   : UFO�ԍ�
	//�@1   : UFO��ԁi�P�F�j���Ȃ�����C�Q����U�͔��j���)
	//�@0   : �`�[��
	//�@100 : UFO�̂w�ʒu
	//�@200 : �@�@�@�@�x�ʒu
	//�@1   : ���e�O�̔�s����
	//�@100 : �@�@�@�@�@�@�w�ʒu
	//�@200 : �@�@�@�@�@�@�x�ʒu
	//�@2   : ���e�P�̔�s����
	//�@300 : �@�@�@�@�@�@�w�ʒu
	//�@300 : �@�@�@�@�@�@�x�ʒu
	//�@3   : ���e�Q�̔�s����
	//�@200 : �@�@�@�@�@�@�w�ʒu
	//�@200 : �@�@�@�@�@�@�x�ʒu

	// �e����UFO�Ɣ��e�̏������Z�b�g--------------------------------------------------
	public void InitialGameInformationSet( ) {
		//  �Ō�ɓo�^�������[�U��UFO�f�[�^�̈ʒu
		int p = GameInformation.lastIndexOf("/U");
		if (p == -1)
			UfoNo = 0;
		else
			UfoNo = 
				new Integer(GameInformation.substring(p + 2, p + 3)).intValue( ) + 1;

		MyUfoStatus = 1;							// UFO���
		UfoStatus[UfoNo] = MyUfoStatus;				// UFO��Ԃ�z��ɃZ�b�g

		UfoX[UfoNo] = UfoNo * UFOImageWidth;

		Team[UfoNo] = MyTeam;
		if (MyTeam == 0) {							// Red Team
			Direction[UfoNo] = 5;					// South  ���e���˂̕���
			UfoX[UfoNo] = UFOImageWidth * (UfoNo+1);
			UfoY[UfoNo] = UFOImageHeight/2;
		}
		else {										// Blue Team
			Direction[UfoNo] = 1;					// North
			UfoX[UfoNo] = FrameWidth - UFOImageWidth * (UfoNo+1);
			UfoY[UfoNo] = FrameHeight - UFOImageHeight/2;
		}
		String ux = "00"+ String.valueOf(UfoX[UfoNo]);
		String uxp = ux.substring(ux.length( ) - 3);
		String uy = "00"+ String.valueOf(UfoY[UfoNo]);
		String uyp = uy.substring(uy.length( ) - 3);

		// �����Q�[�����쐬
		String InitialGameInformation = "/U"
							+ String.valueOf(UfoNo) 
							+ String.valueOf(MyUfoStatus)
							+ String.valueOf(MyTeam)
							+ String.valueOf(uxp)
							+ String.valueOf(uyp);
		for (int i = 0; i < BakudanMax; i++)		// �����Ƃw�x�̍��W�l
			InitialGameInformation = InitialGameInformation + "0000000";

		// �����Q�[�����ǉ�
		NewGameInformation = GameInformation + InitialGameInformation;
	}
	// �Q�[������UFO�Ɣ��e�̏��Z�b�g -------------------------------------------------
	public void GameInformationSet( ) {
		String ux = "00"+ String.valueOf(UfoX[UfoNo]);
		String uxp = ux.substring(ux.length( ) - 3);
		String uy = "00"+ String.valueOf(UfoY[UfoNo]);
		String uyp = uy.substring(uy.length( ) - 3);

		String NowGameInformation =
				"/U" + String.valueOf(UfoNo)
				+ String.valueOf(MyUfoStatus)
				+ String.valueOf(MyTeam)
				+ String.valueOf(uxp)
				+ String.valueOf(uyp);

		// MyUfoStatus�̒l��2����6�̏ꍇ�́CUFO�͔�����ԁC�����V�[����ω�������
		if (MyUfoStatus >= 2) {
			MyUfoStatus++;
			if (MyUfoStatus <= 6)
				UfoStatus[UfoNo] = MyUfoStatus;
			else
				UfoStatus[UfoNo] = MyUfoStatus = 0;	// ���S�ɔ������ď��������
		}

		for (int i = 0; i < BakudanMax; i++) {
			String mx = "00"+ String.valueOf(BakudanX[UfoNo][i]);
			String mxp = mx.substring(mx.length( ) - 3);
			String my = "00"+ String.valueOf(BakudanY[UfoNo][i]);
			String myp = my.substring(my.length( ) - 3);
			NowGameInformation = NowGameInformation
							+ String.valueOf(BakudanWay[UfoNo][i]) + mxp + myp;
		}

		String MyUFO = "/U" + String.valueOf(UfoNo);
		int p = GameInformation.indexOf(MyUFO);
		if (p != -1) {								// ���݂���ꍇ
			String mae = GameInformation.substring(0, p);
			String ato = GameInformation.substring(p + GameInfoLen);
			// ���݂̃Q�[�����Z�b�g
			NewGameInformation = mae + NowGameInformation + ato;
		}
		else										// �ʐM��C�f�[�^�����ő��݂��Ȃ��ꍇ
			// ���݂̃Q�[�����ǉ�
			NewGameInformation = GameInformation + NowGameInformation;
	}
	// �l�b�g�����荞�񂾃Q�[�������������ɃZ�b�g -----------------------------------
	public void GameInformationMemorySet( ) {
		for (int i = 0; i < UfoMax; i++) {
			String ufo = "/U" + String.valueOf(i);
			int p = GameInformation.indexOf(ufo);
			if (p != -1 && i != UfoNo && GameInformation.length( ) >= p+GameInfoLen) {
				// ���݂��C�������ȊO�̏�񂪂���ꍇ
				int n = new Integer(GameInformation.substring(
														p + 2, p + 3)).intValue( );
				UfoStatus[n]  = new Integer(GameInformation.substring(
														p +  3, p +  4)).intValue( );
				Team[n]  = new Integer(GameInformation.substring(
														p +  4, p +  5)).intValue( );
				UfoX[n] = new Integer(GameInformation.substring(
														p +  5, p +  8)).intValue( );
				UfoY[n] = new Integer(GameInformation.substring(
														p +  8, p + 11)).intValue( );
				int bp = p + 11;					// ���e�x�[�X�|�C���g
				for (int j = 0; j < 3; j++) {
					BakudanWay[n][j] = new Integer(
						GameInformation.substring(bp+7*j,   bp+7*j+1)).intValue( );
					BakudanX[n][j] = new Integer(
						GameInformation.substring(bp+7*j+1, bp+7*j+4)).intValue( );
					BakudanY[n][j] = new Integer(
						GameInformation.substring(bp+7*j+4, bp+7*j+7)).intValue( );
				}
			}
		}
		// ������UFO�Ƒ���UFO�̔��˂��ꂽ���e�̏Փ˃`�F�b�N
		// ���Ă�ꂽ���̃`�F�b�N
		// �͈͓��ł���΁CUFO�𔚔j������Ԃɂ���
		if (MyUfoStatus == 1) {						// ������UFO�������Ԃő��݂���ꍇ
			for (int i = 0; i < UfoMax; i++) {
				if (i != UfoNo && UfoStatus[i] != -1) {			// �����ȊOUFO�̔��e���
					for (int j = 0; j < 3; j++) {
						if (BakudanWay[i][j] != 0) {
 							// ��s����1�`8�@�Ɓ@���ŏ���9�̏ꍇ
							if (CrashCheck(	UfoX[UfoNo] + 5,	// UFO�̒��̕�
											UfoY[UfoNo] + 5,
											UFOImageWidth - 5,
											UFOImageHeight - 5,
											BakudanX[i][j],
											BakudanY[i][j],
											BakudanImageWidth,
											BakudanImageHeight) == 1)
								// ������UFO�̏�Ԓl�𔚔j�����l�Q�ɂ���
								MyUfoStatus = UfoStatus[UfoNo] = 2;
						}
					}
				}
			}
		}
		// �����̔��e�Ƒ���UFO�̏Փ˃`�F�b�N�i���Ă����̃`�F�b�N�j
		for (int i = 0; i < 3; i++) {
			if (BakudanWay[UfoNo][i] == 9)			// ���e���ŏ����̏ꍇ
				BakudanWay[UfoNo][i] = 0;			// ���e����

			if (BakudanWay[UfoNo][i] != 0) {		// ���˂���Ĕ��ł���ꍇ
				for (int j = 0; j < UfoMax; j++) {	// ����UFO�Ƃ̏Փ˃`�F�b�N
					// �������g�ȊO��UFO�������Ԃł���ꍇ
					if (j != UfoNo && UfoStatus[j] == 1) {
						// ���������˂������e��UFO�Ɍ��˂����ꍇ
						if (CrashCheck(	UfoX[j] + 5,		// UFO�̒��̕�
										UfoY[j] + 5,
										UFOImageWidth - 5,
										UFOImageHeight - 5,
										BakudanX[UfoNo][i],
										BakudanY[UfoNo][i],
										BakudanImageWidth,
										BakudanImageHeight) == 1)
							BakudanWay[UfoNo][i] = 9;		// ���e�̏��ŏ���
					}
				}
			}
		}
	}
	// ���e��������UFO�͈͓̔����`�F�b�N -----------------------------------------------
	public int CrashCheck( int ux, int uy, int uw, int uh, 
 						int mx, int my, int mw, int mh) {
		int cx[ ] = new int[4];
		int cy[ ] = new int[4];
		int crash = 0;				// ���蔻��(0:�������Ă��Ȃ��C1:��������)

		cx[0] = cx[3] = mx;   cx[1] = cx[2] = mx + mw;
		cy[0] = cy[1] = my;   cy[2] = cy[3] = my + mh;
		for (int i = 0; i < 4; i++) {
			// UFO�Ɣ��e���d�Ȃ����ꍇ�i�Փ˂����ꍇ�j
			if (cx[i] > ux && cx[i] < ux + uw && cy[i] > uy && cy[i] < uy + uh)
				crash = 1;
		}
		return crash;
	}
	// �X���b�h�ݒ�E�J�n ---------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);
		thread.start( );
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (BaseImage != null)				// BaseImage�������n�j�Őݒ肳��Ă���ꍇ
			g.drawImage(BaseImage, 0, 0, this);
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		int endswitch = 0;
		while (GameStartFlag == false) {
			try {
				thread.sleep(100);					// �X���b�h���X���[�v
			} catch(InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				System.out.println(e);
			}
		}

		MakeBackground( );
		repaint( );

		while(true) {

			ReceiveGameInformation( );				// �Q�[������M

			try {
				thread.sleep(50);					// ��M�҂��X���[�v
			} catch(InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				System.out.println(e);
			}

			// �l�b�g�����荞�񂾃Q�[�������������ɃZ�b�g
			GameInformationMemorySet( );

			if (ReceiveInformation.equals("Gameover")) {	// �Q�[���I�[�o�[�̏ꍇ
				NewGameInformation = "Gameover";
				if (AdministratorFlag == false)		// �Ǘ��҈ȊO��
					SendGameInformation( );			// �Q�[���I�[�o�[���𑗂�

				try {
					thread.sleep(3000);				// ���M����������܂�3�b�X���[�v
				} catch(InterruptedException e) {	// ���̃X���b�h�̊��荞�ݗ�O����
					System.out.println(e);
				}
				endswitch = 1;
			}
			else									// �Q�[���I�[�o�[�łȂ�
				GameInformationSet( );				// ���݂̎����̏����Z�b�g

			if (endswitch == 1)
				break;

			if (GameOverFlag == true && AdministratorFlag == true)
				NewGameInformation = "Gameover";

			// NetOutput�Ŏ��̃}�V���ɑ���
			SendGameInformation( );

			if (GameOverFlag == false && AdministratorFlag == true) {	
				// �A�h�~�j�̓Q�[���I�[�o�[�`�F�b�N
				Existence[0] = Existence[1] = 0;
				for (int i = 0; i < UfoMax; i++) { // �A�h�~�j�̏�񂾂��Ń`�F�b�N
					if (UfoStatus[i] == 1)
						Existence[Team[i]] = Existence[Team[i]] + 1;
				}
 				// �ǂ��炩�����݂��Ă��Ȃ��ꍇ
				if (Existence[0] == 0 || Existence[1] == 0) {
					GameOverFlag = true;
				}
			}

			UFOMoveProcess( );						// ������UFO�ړ�����
			BakudanMoveProcess( );					// �����̔��e�ړ�����
			GameScreenDisplay( );					// �Q�[����ʕ\��
		}
	}
	// ������UFO�ړ����� ---------------------------------------------------------------
	void UFOMoveProcess( ) {
		if (UfoX[UfoNo] < UfoXBase[UfoNo])
			UfoX[UfoNo] += 1;
		else if (UfoX[UfoNo] > UfoXBase[UfoNo])
			UfoX[UfoNo] -= 1;
		if (UfoY[UfoNo] < UfoYBase[UfoNo])
			UfoY[UfoNo] += 1;
		else if (UfoY[UfoNo] > UfoYBase[UfoNo])
			UfoY[UfoNo] -= 1;
	}
	// �����̔��e�ړ����� ---------------------------------------------------------------
	void BakudanMoveProcess( ) {
		for (int i = 0; i < BakudanMax; i++) {
			switch (BakudanWay[UfoNo][i]) {
				case 1:								// North
					BakudanY[UfoNo][i] -= 3;
					break;
				case 2:								// North East
					BakudanX[UfoNo][i] += 3;
					BakudanY[UfoNo][i] -= 3;
					break;
				case 3:								// East
					BakudanX[UfoNo][i] += 3;
					break;
				case 4:								// South East
					BakudanX[UfoNo][i] += 3;
					BakudanY[UfoNo][i] += 3;
					break;
				case 5:								// South
					BakudanY[UfoNo][i] += 3;
					break;
				case 6:								// South West
					BakudanX[UfoNo][i] -= 3;
					BakudanY[UfoNo][i] += 3;
					break;
				case 7:								// West
					BakudanX[UfoNo][i] -= 3;
					break;
				case 8:								// North West
					BakudanX[UfoNo][i] -= 3;
					BakudanY[UfoNo][i] -= 3;
					break;
			}

			if (BakudanX[UfoNo][i] < 0	// Frame�̊O�ɏo�����l���`�F�b�N
				|| BakudanX[UfoNo][i] > FrameWidth
				|| BakudanY[UfoNo][i] < 0
				|| BakudanY[UfoNo][i] > FrameHeight) {
					BakudanWay[UfoNo][i] = 0;
					BakudanX[UfoNo][i] = BakudanY[UfoNo][i] = 0;
			}
			
		}
	}
	// �Q�[����ʕ\�� -------------------------------------------------------------------
	public void GameScreenDisplay( ) {
		MakeBackground( );
		for (int UFO = 0; UFO < UfoMax; UFO++) {
			if (UfoStatus[UFO] == 1)
				BaseGraphics.drawImage(UFOImage[Team[UFO]], 
						UfoX[UFO]-UFOImageWidth/2, UfoY[UFO] -UFOImageHeight/2, this);
			else if (UfoStatus[UFO] >= 2 && UfoStatus[UFO] <= 6)
				BaseGraphics.drawImage(ExplodeImage[UfoStatus[UFO]-2], 
						UfoX[UFO]-UFOImageWidth/2, UfoY[UFO] -UFOImageHeight/2,
						UFOImageWidth, UFOImageHeight, this);

			if (UfoStatus[UFO] != -1) {
				for (int i = 0; i < BakudanMax; i++) {
					if (BakudanWay[UFO][i] >= 1 && BakudanWay[UFO][i] <= 8)
						BaseGraphics.drawImage(BakudanImage,
							BakudanX[UFO][i]-BakudanImageWidth/2,
							BakudanY[UFO][i] -BakudanImageHeight/2, this);
				}
			}
		}
		repaint( );
	}
	// �w�i�\�� -------------------------------------------------------------------------
	void MakeBackground( ) {
		
		for (int i = 0; i < FrameHeight / BackImageHeight + 1; i++)
			for (int j = 0; j < FrameWidth / BackImageWidth + 1; j++)
				BaseGraphics.drawImage(BackImage,
					j * BackImageWidth, i * BackImageHeight, this);
	}
	// MouseListener�C���^�t�F�[�X�̊e���\�b�h���` ------------------------------------
    public void mousePressed( MouseEvent evt ) {
		// ���{�^�� && �`�[�����Ԃ܂��͐�
		if ((evt.getModifiers( ) & evt.BUTTON1_MASK) != 0 
			&& BakudanStatus == 0 && MyUfoStatus == 1)
			BakudanStatus = 1;										// ���e����
		else if ((evt.getModifiers( ) & evt.BUTTON3_MASK) != 0) {	// �E�{�^��
			Direction[UfoNo]++;										// UFO�̉�]
			if (Direction[UfoNo] > 8)
				Direction[UfoNo] = 1;
		}
	}
    public void mouseReleased( MouseEvent evt ) {
		if (BakudanStatus == 1) {					// ���e���˂̏ꍇ
			for (int i = 0; i < BakudanMax; i++) {

				if (BakudanWay[UfoNo][i] == 0) {	// ���e����OK
					// ���e�̔��ˏ����ʒu�ݒ�
					BakudanX[UfoNo][i] = UfoX[UfoNo];
					BakudanY[UfoNo][i] = UfoY[UfoNo];
					// ���̃��[�`������BakudanWay���Ƀ`�F�b�N����ꍇ������̂�,
					// �����ł́ABakudanX Y �̐ݒ�̌�ŕ����ݒu����
					BakudanWay[UfoNo][i] = Direction[UfoNo];	// ���e�̕����ݒ�
					break;
				}
			}
			BakudanStatus = 0;						// ���e���ˏ���OK
		}
	}
    public void mouseClicked( MouseEvent evt ) {  }
    public void mouseEntered( MouseEvent evt ) {  }
    public void mouseExited( MouseEvent evt ) {  }
	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
    public void mouseMoved(MouseEvent evt) {
		UfoXBase[UfoNo] = evt.getX( );
		UfoYBase[UfoNo] = evt.getY( );
    }
    public void mouseDragged(MouseEvent evt) { }

	// ���ʕ\�� -------------------------------------------------------------------------
	public void ResultDisplay( ) {
		BaseGraphics.setFont(new Font("TimesRoman", Font.BOLD, 50));

		// �A�h�~�j���C�܂��̓v���C���[�ɂă`�F�b�N���s��
		// ���X�g�̊e���͈قȂ�C���ʓ_�́C���݂��Ă�����̏�Ԃɂ͂P������
		Existence[0] = Existence[1] = 0;
		for (int i = 0; i < UfoMax; i++) {
			if (UfoStatus[i] == 1)					// ���݂��Ă���ꍇ
				Existence[Team[i]] = 1;
		}
		if (Existence[0] == 1) {					// Red team ������
			BaseGraphics.setColor(Color.red);
			BaseGraphics.drawString("Red team win !!!", 50, 300);
		}
		else {										// Blue team ������
			BaseGraphics.setColor(Color.blue);
			BaseGraphics.drawString("Blue team win !!!", 50, 300);
		}
		repaint( );
	}
	// �X���b�h�I�� ---------------------------------------------------------------------
	public void stop( ) {
		thread = null;
	}
	// WindowListener�C���^�[�t�F�[�X�̊e���\�b�h���` ---------------------------------
	public void windowClosing(WindowEvent evt) {
		dispose( );  								// �t���[���̔p��
	}
	public void windowClosed(WindowEvent evt) {
		System.exit(1);								// �v���O�����I��
	}
	public void windowOpened(WindowEvent evt) { }
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }
}
