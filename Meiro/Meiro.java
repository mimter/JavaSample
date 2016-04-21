import java.applet.*;		// Applet, AudioClip
import java.awt.*;			// Graphics, Image, Color, MediaTracker, Button
import java.awt.event.*;	// KeyListener, KeyEvent, ActionListener, ActionEvent

public class Meiro extends Applet implements  Runnable, ActionListener, KeyListener {

	int GameWidth, GameHeight;						// �Q�[����ʃT�C�Y
	Thread thread = null;							// �X���b�h
	Image WorkImage, MeiroImage;					// ��Ɨp�Ɩ��H�C���[�W
	Graphics WorkGraphics, MeiroGraphics;			// ��Ɨp�Ɩ��H�O���t�B�b�N�X

	Image TitleImage;								// �^�C�g���C���[�W
	Image PenguinImage[ ] = new Image[8];			// �y���M���̕��s�摜
	Image LionImage[ ] = new Image[8];				// ���C�I���̕��s�摜
	Image block, takara, egg;						// �ǂƕ󕨂Ƃ��܂�

	int TateMax = 25;								// ���H�̑傫���i��j
	int YokoMax = 25;
	int Map[ ][ ] = new int[TateMax][YokoMax];		// ���H�̃}�b�v�f�[�^
	int Size;										// �ǂ̃T�C�Y�i���̉摜�������T�C�Y�j

	int DispBaseX, DispBaseY;						// �\���x�[�X�|�W�V����	

	Penguin penguin;								// �y���M���I�u�W�F�N�g
	int LionMax = 5;								// ���C�I���̐�
	Lion lion[ ] = new Lion[LionMax];				// ���C�I���I�u�W�F�N�g

	boolean ON = true, OFF = false;					// �_���萔
	boolean GameStart = OFF;						// �Q�[���X�^�[�g�t���O
	boolean GameClear = OFF;						// �Q�[���N���A
	boolean GameOver = OFF;							// �Q�[���I�[�o�[
	int CloseCount = 0;								// ��ʂ��N���[�Y����J�E���g

	Button StartButton;								// �X�^�[�g�{�^��

	AudioClip BGMSound;								// BGM

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		GameWidth = getSize( ).width;				// �Q�[����ʃT�C�Y
		GameHeight = getSize( ).height;

		WorkImage = createImage(GameWidth, GameHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );		// ��Ɨp�O���t�B�b�N�X�擾

		DataLoad( );								// �f�[�^���[�h

		Size = block.getWidth(this);				// �ǃT�C�Y

		MeiroImage = createImage(YokoMax * Size, TateMax * Size);	// ���H�C���[�W�쐬
		MeiroGraphics = MeiroImage.getGraphics( );	// ���H�O���t�B�b�N�X�擾

		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		StartButton.setBounds(GameWidth/2-30, GameHeight-40, 60, 30);

		// �y���M������
		penguin = new Penguin(PenguinImage, Size, this);
		// ���C�I������
		for (int i = 0; i < LionMax; i++)
			lion[i] = new Lion(LionImage, Size, penguin, this);

		addKeyListener(this);						// ���X�i�[�ǉ�
		requestFocus( );							// �L�[���̓t�H�[�J�X��v��
	}
	// �f�[�^���[�h ---------------------------------------------------------------------
	public void DataLoad( ) {
		BGMSound = getAudioClip(getCodeBase( ), "sound/bgm.au");

		MediaTracker mt = new MediaTracker(this);
		TitleImage = getImage(getCodeBase( ), "image/title.jpg");// �^�C�g���摜����
		mt.addImage(TitleImage, 0);					// ���f�B�A�g���b�J�ɃZ�b�g

		for (int i = 0; i < 8; i++) {				// 8��ނ̕��s�摜
			PenguinImage[i] = getImage(getCodeBase( ), "image/penguin"+i+".gif");
													// �y���M�����s�摜



			mt.addImage(PenguinImage[i], 0);
			LionImage[i] = getImage(getCodeBase( ), "image/lion"+i+".gif");
													// ���C�I�����s�摜



			mt.addImage(LionImage[i], 0);
		}
		block = getImage(getCodeBase( ), "image/block.gif");	// �ǉ摜


		mt.addImage(block, 0);
		takara = getImage(getCodeBase( ), "image/takara.gif");	// �󕨉摜


		mt.addImage(takara, 0);
		egg = getImage(getCodeBase( ), "image/egg.gif");		// ���܂��摜

		mt.addImage(egg, 0);

		try {
			mt.waitForID(0);						// �C���[�W�摜�̓��͊�����҂�
		} catch(InterruptedException e) {
			showStatus(" "+e);
		}
	}
	// �Q�[�������� ---------------------------------------------------------------------
	private void GameInitialize( ) {
			MakeMeiro( );							// ���H�쐬
			DispBaseX = DispBaseY = 0;				// �\����{�ʒu
			GameStart = ON;							// �Q�[���X�^�[�g���I��
			GameClear = OFF;						// �Q�[���N���A���I�t
			GameOver = OFF;							// �Q�[���I�[�o�[���I�t
			penguin.initialize( );					// �y���M��������
			for (int i = 0; i < LionMax; i++)
				lion[i].intialize ( );				// ���C�I������������
			BGMSound.loop( );						// BGM
			StartButton.setVisible(OFF);			// �X�^�[�g�{�^�����B��
			requestFocus( );						// �L�[���̓t�H�[�J�X��v��  �{�^���̔�\���̌�
	}
	// ���H�}�b�v�쐬 -------------------------------------------------------------------
	private void MakeMeiro( ) {
		// �}�b�v���󔒂ŃN���A
		for (int Tate = 0; Tate < TateMax; Tate++)
			for (int Yoko = 0; Yoko < YokoMax; Yoko++)
				Map[Tate][Yoko] = 0;				// �󔒃N���A
		// �O���̕Ǎ쐬
		for (int i = 0; i < YokoMax; i++) {			// ���Ǎ쐬
			Map[0][i] = 1;							// ��̒i�̉��̕�
			Map[TateMax-1][i] = 1;					// ���̒i�̉��̕�
		}
		for (int i = 0; i < TateMax; i++) {			// �c��
			Map[i][0] = 1; 							// ���̏c��
			Map[i][YokoMax-1] = 1;					// �E�̏c��
		}
		// �K��̓��ǂƘA������ǂ��쐬
		for (int Yoko = 2; Yoko < YokoMax - 2; Yoko += 2) {
			for (int Tate = 2; Tate < TateMax - 2; Tate += 2) {
				Map[Tate][Yoko] = 1;				// �K��̓���

				// ���ǂ���4�����ɕǂ�u��
				// ���̂P�@�u���ʒu�ɕǂ�����΁C�u����Ƃ���T���Ēu��
				//       ���̏ꍇ�́C��Ɉ�ʂ肵���Ȃ����H���ł�������܂��B
				// �@�@�@����������Ɓu�E��@�ŕK����������H�v�ƂȂ�܂��B
				int flag = 0;						// �u���邩�̃`�F�b�N�t���O
				while (flag == 0) {					// �ǂ��u���Ȃ��ԁC�J��Ԃ�
					// �l���̕����������_���ɔ����@0:��  1:�E  2:��  3:��
					int w = (int)(Math.random( )*4);

					switch  (w) {
						case 0: if (Map[Tate-1][Yoko] == 0) {// ������̈ʒu���󔒂̏ꍇ
									Map[Tate-1][Yoko] = 1;		// �ǂ�u��
									flag = 1;					// �u�����Ƃ��ł���
								}
							    break;
						case 1: if (Map[Tate][Yoko+1] == 0) {// �E�����̈ʒu���󔒂̏ꍇ
									Map[Tate][Yoko+1] = 1;		// �ǂ�u��
									flag = 1;					// �u�����Ƃ��ł���
								}
							    break;
						case 2: if (Map[Tate+1][Yoko] == 0) {// �������̈ʒu���󔒂̏ꍇ
									Map[Tate+1][Yoko] = 1;		// �ǂ�u��
									flag = 1;					// �u�����Ƃ��ł���
								}
							    break;
						case 3: if (Map[Tate][Yoko-1] == 0) {// �������̈ʒu���󔒂̏ꍇ
									Map[Tate][Yoko-1] = 1;		// �ǂ�u��
									flag = 1;					// �u�����Ƃ��ł���
								}
							    break;
					}
				}
				/* ======================================================================
				// ���̂Q�@�l�������_���ɕǂ�u���B�Q�d�ɒu���ꍇ��������B
				// �@�@�@���̏ꍇ�́C��Ԃ����܂�C���ʂ肩�̖��H���ł���B
				// �l���̕����������_���ɔ����@0:��  1:�E  2:��  3:��
				int w = (int)(Math.random( )*4);
				switch  (w) {
					case 0: Map[Tate-1][Yoko] = 1;	// ������̈ʒu��ǂɂ���
						    break;
					case 1: Map[Tate][Yoko+1] = 1;	// �E�����̈ʒu��ǂɂ���
						    break;
					case 2: Map[Tate+1][Yoko] = 1;	// �������̈ʒu��ǂɂ���
						    break;
					case 3: Map[Tate][Yoko-1] = 1;	// �������̈ʒu��ǂɂ���
						    break;
				}
			========================================================================== */
			}
		}

		// �󕨂�u��
		Map[TateMax-2][YokoMax-2] = 2;				// ���H�̉E���ɕ󕨂�u��

		// ���H�摜�쐬
		MeiroGraphics.setColor(Color.white);		// ���F�œh��Ԃ�
		MeiroGraphics.fillRect(0, 0, YokoMax*Size, TateMax*Size);
		for (int Tate = 0; Tate < TateMax; Tate++) {		// �c�������[�v
			for (int Yoko = 0; Yoko < YokoMax; Yoko++) {	// ���������[�v
				if (Map[Tate][Yoko] == 1)			// �ǂ̏ꍇ�@�Ǖ`��
					MeiroGraphics.drawImage(block, Yoko*Size, Tate*Size, this);
				else if (Map[Tate][Yoko] == 2)		// �󕨂̏ꍇ�@�󕨕`��
					MeiroGraphics.drawImage(takara, Yoko*Size, Tate*Size, this);
			}
		}
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (GameStart == OFF)						// �Q�[�����X�^�[�g����Ă��Ȃ��ꍇ
			// �^�C�g���`��
			WorkGraphics.drawImage(TitleImage, 0, 0, GameWidth, GameHeight, this);
		else if (GameOver == ON) {					// �Q�[���I�[�o�[
			// �^�C�g���`��
			WorkGraphics.drawImage(TitleImage, 0, 0, GameWidth, GameHeight, this);
			// ��ʂ���Ă�������
			if (!(GameWidth - CloseCount*2 <= 0
				|| GameHeight - CloseCount*2 <= 0)) {
				// �N���b�v�̈�ݒ�
				WorkGraphics.clipRect(CloseCount, CloseCount,
					GameWidth - CloseCount*2, GameHeight - CloseCount*2);

				gameprocess( );						// �Q�[���v���Z�X

				WorkGraphics = WorkImage.getGraphics( ); // �ēx�O���t�B�b�N�X�擾
				CloseCount += 3;					// ���Ă������𑝂₷
			}
			else {									// ��ʂ̃N���[�Y����
				GameStart = OFF;					// �Q�[���X�^�[�g�@�I�t
				StartButton.setVisible(ON);			// �X�^�[�g�{�^����\��
			}
		}
		else {										// �Q�[����
			gameprocess( );							// �Q�[���v���Z�X
		}
		WorkGraphics.setColor(Color.black);
		WorkGraphics.drawRect(0, 0, GameWidth-1, GameHeight-1);	// �O�g�쐬

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �Q�[���v���Z�X -------------------------------------------------------------------
	public void gameprocess( ) {
			WorkGraphics.drawImage(MeiroImage, DispBaseX, DispBaseY, this);	// ���H���
			penguin.update(WorkGraphics);			// �y���M���`��
			if (GameClear == OFF) {					// �Q�[���N���A���Ă��Ȃ�
				for (int i = 0; i < LionMax; i++)
					lion[i].paint(WorkGraphics);	// ���C�I���`��
			}
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			if (GameClear == OFF) {					// �Q�[���N���A���Ă��Ȃ�
				for (int i = 0; i < LionMax; i++)
					lion[i].move( );				// ���C�I���ړ�
			}
			try {
				thread.sleep(100);					// �X���b�h�X���[�v : �Q�[���X�s�[�h
													// �܂胉�C�I���̃X�s�[�h
			} catch (InterruptedException e){		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" " + e);				// ��O�G���[�\��
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �Q�[���I�[�o�[ -------------------------------------------------------------------
	public void gameover( ) {
		GameOver = ON;								// �Q�[���I�[�o�[
		CloseCount = 0;								// ��ʂ����J�E���g�p
		BGMSound.stop( );							// BGM�I��
	}
	// �Q�[���N���A ---------------------------------------------------------------------
	public void gameclear( ) {
		GameClear = ON;								// �Q�[���N���A
		StartButton.setVisible(ON);					// �X�^�[�g�{�^�����B��
		BGMSound.stop( );							// BGM�I��
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
		BGMSound.stop( );							// BGM�I��
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );
		if (button == StartButton) {				// �X�^�[�g�{�^���̏ꍇ
			GameInitialize( );						// �Q�[��������
		}
	}
	// KeyListener�C���^�t�F�[�X�̊e���\�b�h���` --------------------------------------
	public void keyPressed(KeyEvent evt) {
		if (GameOver == ON)							// �Q�[���I�[�o�[�̏ꍇ
			return;
		// �y���M���𓮂���
		switch (evt.getKeyCode( )) {				// ���̓L�[�̒l�ŕ���
		    case KeyEvent.VK_DOWN  :				// ���L�[
				// �Ⴄ�����������Ă���Ƃ��́C�����]��
				if (penguin.Status != 0)			// �������������Ă��Ȃ��ꍇ
					penguin.Status = 0;				// �y���M���̕������������ɂ���
				// ���̐^���ŁC�����ǂłȂ���ΐi��
				else if (penguin.X % Size == 0
					 && Map[(penguin.Y+Size)/Size][penguin.X/Size] != 1) {
					penguin.Y += penguin.Step;		// ���ɐi��
					// �\���x�[�X�|�C���g��ω�������
					// y�����̕\����ʒu���܂�������ɍs�����Ƃ��ł��C
					// ����ʒu���猻�݂̈ʒuY���A�v���b�g��2����1���傫���ꍇ
					if (DispBaseY > -TateMax*Size+GameHeight
					 && penguin.Y + DispBaseY > GameHeight / 2)
						DispBaseY -= penguin.Step;	// �\����ʒu��Step����Ɉړ�
													// ���̂��Ƃɂ���Ė��H�̉������\��
				}
				break;
	        case KeyEvent.VK_UP    :				// ��L�[
				if (penguin.Status != 1)			// ������������Ă��Ȃ��ꍇ
					penguin.Status = 1;				// �y���M���̕�����������ɂ���
				// ���̐^���ŁC�オ�ǂłȂ���ΐi��
				else if (penguin.X % Size == 0 &&
						Map[(penguin.Y-penguin.Step)/Size][penguin.X/Size] != 1) {
					penguin.Y -= penguin.Step;		// ��ɐi��
					// �\���x�[�X�|�C���g��ω�������
					if (DispBaseY < 0 && penguin.Y < TateMax*Size - GameHeight / 2)
						DispBaseY += penguin.Step;	// �\����ʒu��Step�����Ɉړ�
				}
				break;
		    case KeyEvent.VK_RIGHT : 				// �E�L�[
				if (penguin.Status != 2)			// �E�����������Ă��Ȃ��ꍇ
					penguin.Status = 2;				// �y���M���̕������E�����ɂ���
				// ���̐^���ŁC�E���ǂłȂ���ΐi��
				else if (penguin.Y % Size == 0 && 
						Map[penguin.Y/Size][(penguin.X+Size)/Size] != 1) { 
					penguin.X += penguin.Step;		// �E�ɐi��
					// �\���x�[�X�|�C���g��ω�������
					if (DispBaseX > -YokoMax*Size+GameWidth
					 && penguin.X + DispBaseX > GameWidth / 2)
						DispBaseX -= penguin.Step;	// �\����ʒu��Step�����Ɉړ�
				}
				break;
	        case KeyEvent.VK_LEFT  : 				// ���L�[
				if (penguin.Status != 3)			// �������������Ă��Ȃ��ꍇ
					penguin.Status = 3;				// �y���M���̕������������ɂ���
				// ���̐^���ŁC�����ǂłȂ���ΐi��
				else if (penguin.Y % Size == 0 &&
						Map[penguin.Y/Size][(penguin.X-penguin.Step)/Size] != 1) {
					penguin.X -= penguin.Step;		// ���ɐi��
					// �\���x�[�X�|�C���g��ω�������
					if (DispBaseX < 0 && penguin.X < YokoMax*Size - GameWidth / 2)
						DispBaseX += penguin.Step;	// �\����ʒu��Step���E�Ɉړ�
				}
				break;
	    }
		repaint( );
	}
	public void keyReleased(KeyEvent evt) { }
	public void keyTyped(KeyEvent evt) { }
}

// ======================================================================================
// �y���M���N���X
class Penguin extends Applet {
	Applet applet;									// �\���A�v���b�g
	static Image PenguinImage[ ] = new Image[6];	// �y���M���C���[�W
	int X, Y;										// �y���M���̈ʒu
	int Step;										// �ړ��X�e�b�v
	int Status;										// ��ԁ@0��  1��  2��  3��
	int Change;										// �A�j���[�V�����ω��p
	int Size;										// ���H�̕ǃT�C�Y

	Meiro meiro;									// �e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public Penguin(Image PenguinImage[ ], int Size, Applet applet) {
		this.PenguinImage = PenguinImage;			// �y���M���C���[�W
		this.applet = applet;						// �A�v���b�g
		this.Size = Size;
		meiro = (Meiro)applet;						// �e�N���X
	}
	// �C�j�V�����C�Y -------------------------------------------------------------------
	public void initialize( ) {
		X = Y = Size;								// �y���M���̏����ʒu
		Status = 0;									// ��ԁ@0��  1��  2��  3��
		Change = 0;									// �A�j���[�V�����ω��p
		Step = Size / 10;							// �X���[�Y�ړ��Ԋu(Size �ǃT�C�Y)
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		Change++;									// �y���M���A�j���[�V�����̏�Ԃ�ω�

 		// ��Ɨp�O���t�B�b�N�Ƀy���M���`��
		g.drawImage(PenguinImage[Status * 2 + Change % 2],
					X + meiro.DispBaseX, Y + meiro.DispBaseY, this);

		// ���܂����}�b�v�ɓo�^���C���H�O���t�B�b�N�ɂ��܂���`��
		if (X % Size == 0 && Y % Size == 0 && meiro.Map[Y / Size][X / Size] == 0) {
			// ���H�̊e�u���b�N�̂��傤�ǂ̈ʒu�ŉ����Ȃ��ꍇ
			meiro.Map[Y / Size][X / Size] = 3;
			meiro.MeiroGraphics.drawImage(meiro.egg,
								 X / Size * Size, Y / Size * Size, applet);
		}

		// �󕨂ɓ���
		if (meiro.Map[Y / Size][X / Size] == 2)		// �󕨂ɓ���
			meiro.gameclear( );						// �Q�[���N���A
	}
}

// ======================================================================================
// ���C�I���N���X
class Lion {
	Applet applet;									// �A�v���b�g
	static Image LionImage[ ] = new Image[8];		// ���C�I���C���[�W
	int X, Y;										// ���C�I���̈ʒu
	int Status;										// ��ԁ@0��  1��  2��  3��
	int Change;										// �A�j���[�V�����ω��p
	int Step;										// �ړ��X�e�b�v
	int Size;										// �ǃT�C�Y
	Meiro meiro;									// �e�N���X
	Penguin penguin;

	// �R���X�g���N�^ -------------------------------------------------------------------
	public Lion(Image LionImage[ ], int Size, Penguin penguin, Applet applet) {
		this.LionImage = LionImage;					// ���C�I���C���[�W
		this.Size = Size;							// �ǃT�C�Y
		this.penguin = penguin;						// �y���M��
		this.applet = applet;						// �A�v���b�g
		meiro = (Meiro)applet;						// �e�N���X
	}
	// �C�j�V�����C�Y -------------------------------------------------------------------
	public void intialize( ) {
		X = (meiro.YokoMax - 2) * Size;				// ���C�I���̈ʒu�@�󕨂Ɠ����ʒu
		Y = (meiro.TateMax - 2) * Size;
		Status = (int)(Math.random( )*4);			// ���C�I���̏��  0�k  1�� 2��  3��
		Step = Size / 10;							// �X���[�Y�ړ��Ԋu(Size �ǃT�C�Y)
		Change = 0;									// �A�j���[�V�����ω��p
	}
	// �ړ����� -------------------------------------------------------------------------
	public void move( ) {
		int flag = 0;								// �s���邩�ǂ����̃`�F�b�N�p�t���O
		switch (Status) {							// 0��  1��  2��  3��
			case 0:	// 0���@���C�I���͉�����
				// 0��  2��  3��  �����ꂩ�i�s�\�����ׂ�
				// ���C�I���̃O���t�B�b�N��̈ʒu��z���ł̈ʒu�ɕϊ����ĕǂ̃`�F�b�N
				if (meiro.Map[(Y+Size)/Size][X/Size] != 1	// �����ǂłȂ�
				 || meiro.Map[Y/Size][(X+Size)/Size] != 1	// �E���ǂłȂ�
				 || meiro.Map[Y/Size][(X-Step)/Size] != 1)	// �����ǂłȂ�
					{
					// �i�s�����̉����ǂłȂ��C�ړ����z��̂P�}�X�̓r���i�K�̏ꍇ
					if (meiro.Map[(Y+Size)/Size][X/Size] != 1
					 && Y % Size != 0) {
						flag = 1;					// �s����t���O���I��
						Y += Step;					// �������ɐi��
						Status = 0;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// ���݃��C�I���͉������������Ă���̂ŉ��������ɔ��f
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y+Size)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y += Step;					// �������ɐi��
						Status = 0;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// �E�����ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X+Size)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X += Step;					// �E�����ɐi��
						Status = 2;					// ���C�I���̏�Ԃ��E�����ɂ���
					}
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X-Step)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X -= Step;					// �������ɐi��
						Status = 3;					// ���C�I���̏�Ԃ��������ɂ���
					}
					while (flag == 0) {				// �܂��s�����������܂��Ă��Ȃ��ꍇ
						// ���������ǂłȂ������_���m��2����1��OK�̏ꍇ
						if (meiro.Map[(Y+Size)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y += Step;				// �������ɐi��
							Status = 0;				// ���C�I���̏�Ԃ��������ɂ���
						}
						// �E�������ǂłȂ������_���m��2����1��OK�̏ꍇ
						else if (meiro.Map[Y/Size][(X+Size)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X += Step;				// �E�����ɐi��
							Status = 2;				// ���C�I���̏�Ԃ��E�����ɂ���
						}
						// ���������ǂłȂ������_���m��2����1��OK�̏ꍇ
						else if (meiro.Map[Y/Size][(X-Step)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X -= Step;				// �������ɐi��
							Status = 3;				// ���C�I���̏�Ԃ��������ɂ���
						}
					}
				}
				else {			// 0��  2��  3��  ������i�s�ł��Ȃ��ꍇ�@1��	�o�b�N
					flag = 1;						// �s����t���O���I��
					Y -= Step;						// ������ɐi��
					Status = 1;						// ���C�I���̏�Ԃ�������ɂ���
				}
				break;

			case 1:	// 1���@���C�I���͏����
				// 1��  2��  3��  �����ꂩ�i�s�\�����ׂ�
				if (meiro.Map[(Y-Step)/Size][X/Size] != 1	// �オ�ǂłȂ�
				 || meiro.Map[Y/Size][(X+Size)/Size] != 1	// �E���ǂłȂ�
				 || meiro.Map[Y/Size][(X-Step)/Size] != 1)	// �����ǂłȂ�
					{
					// �i�s�����̏オ�ǂłȂ��C�ړ����z��̂P�}�X�̓r���i�K�̏ꍇ
					if (meiro.Map[(Y-Step)/Size][X/Size] != 1
					 && Y % Size != 0) {
						flag = 1;					// �s����t���O���I��
						Y -= Step;					// ������ɐi��
						Status = 1;					// ���C�I���̏�Ԃ�������ɂ���
					}
					// ���݃��C�I���͏�����������Ă���̂ŏ�������ɔ��f
					// ������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y-Step)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y -= Step;					// ������ɐi��
						Status = 1;					// ���C�I���̏�Ԃ�������ɂ���
					}
					// �E�����ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X+Size)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X += Step;					// �E�����ɐi��
						Status = 2;					// ���C�I���̏�Ԃ��E�����ɂ���
					}
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X-Step)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X -= Step;					// �������ɐi��
						Status = 3;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// �i�s��������܂�Ȃ��ꍇ
					while (flag == 0) {
						// ��������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						if (meiro.Map[(Y-Step)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y -= Step;				// ������ɐi��
							Status = 1;				// ���C�I���̏�Ԃ�������ɂ���
						}
						// �E�������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[Y/Size][(X+Size)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X += Step;				// �E�����ɐi��
							Status = 2;				// ���C�I���̏�Ԃ��E�����ɂ���
						}
						// ���������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[Y/Size][(X-Step)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X -= Step;				// �������ɐi��
							Status = 3;				// ���C�I���̏�Ԃ��������ɂ���
						}
					}
				}
				else {	// 0��	��E���E�ɍs���Ȃ��ꍇ�C�o�b�N����
					flag = 1;						// �s����t���O���I��
					Y += Step;						// �������ɐi��
					Status = 0;						// ���C�I���̏�Ԃ��������ɂ���
				}
				break;

			case 2:	// 2���@���C�I���͉E���� 
				// 2��  0���@1���@�����ꂩ�i�s�\�����ׂ�
				if (meiro.Map[Y/Size][(X+Size)/Size] != 1		// �E���ǂłȂ�
				 || meiro.Map[(Y+Size)/Size][X/Size] != 1		// �����ǂłȂ�
				 || meiro.Map[(Y-Step)/Size][X/Size] != 1) {	// �オ�ǂłȂ�
					// �i�s�����̉E���ǂłȂ��C�ړ����z��̂P�}�X�̓r���i�K�̏ꍇ
					if (meiro.Map[Y/Size][(X+Size)/Size] != 1
					 && X % Size != 0) {
						flag = 1;					// �s����t���O���I��
						X += Step;					// �E�����ɐi��
						Status = 2;					// ���C�I���̏�Ԃ��E�����ɂ���
					}
					// �E�����ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X+Size)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X += Step;					// �E�����ɐi��
						Status = 2;					// ���C�I���̏�Ԃ��E�����ɂ���
					}
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y+Size)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y += Step;					// �������ɐi��
						Status = 0;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// ������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y-Step)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y -= Step;					// ������ɐi��
						Status = 1;					// ���C�I���̏�Ԃ�������ɂ���
					}
					// �i�s��������܂�Ȃ��ꍇ
					while (flag == 0) {
						// �E�������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						if (meiro.Map[Y/Size][(X+Size)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X += Step;				// �E�����ɐi��
							Status = 2;				// ���C�I���̏�Ԃ��E�����ɂ���
						}
						// ���������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[(Y+Size)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y += Step;				// �������ɐi��
							Status = 0;				// ���C�I���̏�Ԃ��������ɂ���
						}
						// ��������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[(Y-Step)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y -= Step;				// ������ɐi��
							Status = 1;				// ���C�I���̏�Ԃ�������ɂ���
						}
					}
				}
				else {	// 3���@�@�E�E�㉺�ɍs���Ȃ��ꍇ�C�o�b�N����
					flag = 1;						// �s����t���O���I��
					X -= Step;						// �������ɐi��
					Status = 3;						// ���C�I���̏�Ԃ��������ɂ���
				}
				break;

			case 3:	// 3���@���C�I���͍�����
				// 3��  0��  1���@�����ꂩ�i�s�\�����ׂ�
				if (meiro.Map[Y/Size][(X-Step)/Size] != 1		// �����ǂłȂ�
				 || meiro.Map[(Y+Size)/Size][X/Size] != 1		// �����ǂłȂ�
				 || meiro.Map[(Y-Step)/Size][X/Size] != 1) {	// �オ�ǂłȂ�
					// �i�s�����̍����ǂłȂ��C�ړ����z��̂P�}�X�̓r���i�K�̏ꍇ
					if (meiro.Map[Y/Size][(X-Step)/Size] != 1
					 && X % Size != 0) {
						flag = 1;					// �s����t���O���I��
						X -= Step;					// �������ɐi��
						Status = 3;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[Y/Size][(X-Step)/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						X -= Step;					// �������ɐi��
						Status = 3;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// �������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y+Size)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y += Step;					// �������ɐi��
						Status = 0;					// ���C�I���̏�Ԃ��������ɂ���
					}
					// ������ɂ��܂�������ꍇ
					else if (meiro.Map[(Y-Step)/Size][X/Size] == 3) {
						flag = 1;					// �s����t���O���I��
						Y -= Step;					// ������ɐi��
						Status = 1;					// ���C�I���̏�Ԃ�������ɂ���
					}
					// �i�s��������܂�Ȃ��ꍇ
					while (flag == 0) {
						// ���������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						if (meiro.Map[Y/Size][(X-Step)/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							X -= Step;				// �������ɐi��
							Status = 3;				// ���C�I���̏�Ԃ��������ɂ���
						}
						// ���������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[(Y+Size)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y += Step;				// �������ɐi��
							Status = 0;				// ���C�I���̏�Ԃ��������ɂ���
						}
						// ��������ǂłȂ��C����2����1�̊m���Ń����_��������0�̏ꍇ
						else if (meiro.Map[(Y-Step)/Size][X/Size] != 1
						 && (int)(Math.random( )*2) == 0) {
							flag = 1;				// �s����t���O���I��
							Y -= Step;				// ������ɐi��
							Status = 1;				// ���C�I���̏�Ԃ�������ɂ���
						}
					}
				}
				else {	// 2���@���E�㉺�ɍs���Ȃ��ꍇ�C�o�b�N����
					flag = 1;						// �s����t���O���I��
					X += Step;						// �E�����ɐi��
					Status = 2;						// ���C�I���̏�Ԃ��E�����ɂ���
				}
				break;
			
		}
		Change++;									// �A�j���[�V�����ω��p

		if (meiro.Map[Y/Size][X/Size] == 3) {		// ���܂��̏ꍇ
			meiro.Map[Y/Size][X/Size] = 0;			// �}�b�v�ォ�炽�܂�������
			meiro.MeiroGraphics.setColor(Color.white);	// ���F�œh��Ԃ�
			meiro.MeiroGraphics.fillRect(X/Size*Size, Y/Size*Size, Size, Size);
		}
		// �y���M���Ɠ����ʒu�̏ꍇ
		if (penguin.X / Size == X / Size && penguin.Y / Size == Y / Size
			&& meiro.GameOver == false)
			meiro.gameover( );						// �Q�[���I�[�o�[
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(LionImage[Status * 2 + Change % 2], 
					meiro.DispBaseX + X, meiro.DispBaseY + Y, applet);
	}
}
