import java.applet.*;		// Applet, AudioClip
import java.awt.*;			// Graphics, Image, Color, Font, MediaTracker, Button
import java.awt.event.*;	// ActionListener, ActionEvent, MouseListener, MouseEvent etc
import java.util.*;			// Date

public class Shooting extends Applet
		implements Runnable, ActionListener, MouseListener, MouseMotionListener {

	int GameWidth, GameHeight;						// �Q�[����ʃT�C�Y
	int Score = -1;									// �X�R�A�i�܂����Ă��Ȃ��ꍇ�j
	Thread thread = null;							// �X���b�h

	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	Image TitleImage;								// �^�C�g���C���[�W
	Image BackgroundImage[ ] = new Image[2];		// �w�i�C���[�W

	Image UFOImage[ ] = new Image[6];				// UFO��]�C���[�W

	Image TekiImage[ ] = new Image[5];				// �T��ނ̓G�C���[�W
	Image MissileImage[ ] = new Image[5];			// �T��ނ̃~�T�C���C���[�W
	Image ExplosionImage[ ] = new Image[6];			// �����C���[�W

	AudioClip MissileSound[ ] = new AudioClip[5];	// �~�T�C���̔��ˉ�
	AudioClip ExplosionSound[ ] = new AudioClip[5];	// ������

	Background background;							// �w�i�I�u�W�F�N�g
	UFO ufo;										// UFO�I�u�W�F�N�g
	TekiManeger tekiManeger;						// �G�}�l�[�W���i�G����j
	MissileManeger missileManeger;					// �~�T�C���}�l�[�W���i�~�T�C������j

	boolean ON = true, OFF = false;					// �_���萔
	boolean GameStart = OFF;						// �Q�[���X�^�[�g�t���O
	boolean GameOver = OFF;							// �Q�[���I�[�o�[
	int CloseCount = 0;								// ��ʂ��N���[�Y����J�E���g

	Button StartButton;								// �X�^�[�g�{�^��

	Font font = new Font("Courier", Font.BOLD, 20);	// �t�H���g��`

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		GameWidth = getSize( ).width;				// �Q�[����ʃT�C�Y
		GameHeight = getSize( ).height;

		WorkImage = createImage(GameWidth, GameHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );	// ��Ɨp�O���t�B�b�N�X�擾
		WorkGraphics.setFont(font);					// �t�H���g�ݒ�
		WorkGraphics.setColor(Color.white);			// �\���F

		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		StartButton.setBounds(GameWidth/2-30, GameHeight-40, 60, 30);

		DataLoad( );								// �f�[�^���[�h

		// �w�i�I�u�W�F�N�g
		background = new Background(BackgroundImage, GameWidth, GameHeight, this);
		// UFO�I�u�W�F�N�g
		ufo = new UFO(UFOImage, ExplosionImage, ExplosionSound,
						GameWidth, GameHeight, this);
		// �G�}�l�[�W��
		tekiManeger = 
			new TekiManeger(TekiImage, ExplosionImage, ExplosionSound, ufo, this);
		// �~�T�C���}�l�[�W��
		missileManeger = 
			new MissileManeger(MissileImage, MissileSound,	tekiManeger, ufo, this);

		tekiManeger.information(missileManeger);	// �G�}�l�[�W���Ƀ~�T�C�����

		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i�[�ǉ�
	}
	// �f�[�^���[�h ---------------------------------------------------------------------
	public void DataLoad( ) {
		for (int i= 0; i < 5; i++) {
			MissileSound[i] = getAudioClip(getCodeBase( ),
				"sound/missile" + i + ".au");		// �~�T�C���̔��ˉ�����
			ExplosionSound[i] = getAudioClip(getCodeBase( ),
				"sound/explosion" + i + ".au");		// ����������
		}

		MediaTracker mt = new MediaTracker(this);

		TitleImage = getImage(getCodeBase( ), "image/title.jpg");// �^�C�g���摜����
		mt.addImage(TitleImage, 1);					// ���f�B�A�g���b�J�ɃZ�b�g

		for (int i = 0; i < 2; i++) {				// �w�i�C���[�W����
			BackgroundImage[i] = getImage(getCodeBase( ), "image/tree" + i + ".gif");
			mt.addImage(BackgroundImage[i], 1);		// ���f�B�A�g���b�J�ɃZ�b�g
		}

		for (int i = 0; i < 6; i++) {				// ��]UFO�C���[�W����
			UFOImage[i] = getImage(getCodeBase( ), "image/ufo" + i + ".gif");
			mt.addImage(UFOImage[i], 1);			// ���f�B�A�g���b�J�ɃZ�b�g
		}

		for (int i = 0; i < 5; i++) {			  	// �G�ƃ~�T�C���C���[�W����
			TekiImage[i] = getImage(getCodeBase( ), "image/teki" + i + ".gif");
			mt.addImage(TekiImage[i], 1);
			MissileImage[i] = getImage(getCodeBase( ), "image/missile"+i+".gif");
			mt.addImage(MissileImage[i], 1);
		}

		for (int i = 0; i < 6; i++) {				// �����C���[�W����
			ExplosionImage[i] = getImage(getCodeBase( ), "image/explosion" + i + ".gif");
			mt.addImage(ExplosionImage[i], 1);		// ���f�B�A�g���b�J�ɃZ�b�g
		}

		try {
			mt.waitForAll( );						// ���͊����҂�
		} catch(InterruptedException e) {
		}
	}
	// �Q�[�������� ---------------------------------------------------------------------
	private void GameInitialize( ) {
		GameStart = ON;								// �Q�[���X�^�[�g���I��
		GameOver = OFF;								// �Q�[���I�[�o�[���I�t
		Score = 0;									// �X�R�A
		ufo.initialize( );							// UFO������
		WorkGraphics.setFont(font);					// �t�H���g�ݒ�
		WorkGraphics.setColor(Color.white);			// �\���F
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (GameStart == OFF) {						// �Q�[�����J�n����Ă��Ȃ��ꍇ
			// �^�C�g���`��
			WorkGraphics.drawImage(TitleImage, 0, 0, GameWidth, GameHeight, this);
			if (Score != -1)
				WorkGraphics.drawString("SCORE:" + Score, 5, 25);	// �X�R�A�\��
		}
		else if (GameOver == ON) {					// �Q�[���I�[�o�[�̏ꍇ
			// �^�C�g���`��
			WorkGraphics.drawImage(TitleImage, 0, 0, GameWidth, GameHeight, this);
			// ��ʂ���Ă���
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
				StartButton.setVisible(true);		// �X�^�[�g�{�^����\��
			}
		}
		else {										// �Q�[�����̏ꍇ
			gameprocess( );							// �Q�[���v���Z�X
		}

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �Q�[���v���Z�X -------------------------------------------------------------------
	public void gameprocess( ) {
		background.update(WorkGraphics);			// �w�i�X�V
		tekiManeger.update(WorkGraphics);			// �G�}�l�[�W���������̓G���X�V
		missileManeger.update(WorkGraphics);		// �~�T�C���}�l�[�W�����~�T�C���X�V
		ufo.update(WorkGraphics);					// UFO�X�V
		WorkGraphics.setColor(Color.white);			// �X�R�A�\��
		WorkGraphics.drawString("SCORE:" + Score, 5, 25);
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			try {
				thread.sleep(50);					// �X���b�h�X���[�v
			} catch (InterruptedException e){		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" " + e);				// ��O�G���[�\��
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
	}
	// �Q�[���I�[�o�[ -------------------------------------------------------------------
	public void gameover( ) {
		GameOver = ON;								// �Q�[���I�[�o�[
		CloseCount = 0;								// ��ʂ����J�E���g�p
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );
		if (button == StartButton) {				// �X�^�[�g�{�^���̏ꍇ
			StartButton.setVisible(false);			// �X�^�[�g�{�^�����B��
			GameInitialize( );						// �Q�[��������
		}
	}
	// MouseListener�C���^�[�t�F�[�X�̃��\�b�h��` --------------------------------------
    public void mousePressed(MouseEvent evt) {		// �}�E�X�_�E������
		if (GameStart == true)
			missileManeger.shoot( );				// �~�T�C������
	}
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
	// MouseMotionListener�C���^�[�t�F�[�X�̃��\�b�h��` --------------------------------
    public void mouseMoved(MouseEvent evt) {
		if (GameStart == true)
		ufo.MouseLocate(evt.getX( ), evt.getY( ));	// �}�E�X�̈ʒu��UFO�ɑ���
	}
    public void mouseDragged(MouseEvent evt) {
	}
}

// ======================================================================================
// �w�i�N���X
class Background extends Applet {
	Applet applet;									// �\���A�v���b�g(�t���[��)
	int GameWidth, GameHeight;						// �Q�[����ʃT�C�Y

	static Image Tree[] = new Image[2];				// �؂̉摜
	int TreeWidth, TreeHeight;						// �؂̃T�C�Y
	int TreeMax = 100;								// �؂̐�
	int TreeType[] = new int[TreeMax];				// �؂̃^�C�v
	Point TreePoint[] = new Point[TreeMax]; 		// �؂̈ʒu

	int MissyuCount = 0;							// ���W�J�E���g
	int MissyuMax = 0;								// ���W�ő吔
	int MissyuKankaku = 0;							// ���W�Ԋu

	int ScrollStep = 3;								// �X�N���[���X�e�b�v

	// �R���X�g���N�^ ------------------------------------------------------------------
	public Background(Image BackgroundImage[ ],int GameWidth, int GameHeight,
						 Applet applet) {

		Tree = BackgroundImage;						// �؂̃C���[�W
		this.GameWidth = GameWidth;					// �Q�[����ʃT�C�Y
		this.GameHeight = GameHeight;
		this.applet = applet;						// �A�v���b�g

		TreeWidth = Tree[0].getWidth(applet);		// �؂̕�
		TreeHeight = Tree[0].getHeight(applet);		// �؂̍���

		MakeTreePoint( );							// �؂̈ʒu�ݒ�
	}
	// �؂̈ʒu�ݒ� -------------------------------------------------------------------
	// UFO�̐i�s�����͏�����C��ʃX�N���[���͋t�̉�����
	void MakeTreePoint( ) {
		for (int i = 0; i < TreeMax; i++) {
			TreePoint[i] = new Point( );			// Point�N���X�̃I�u�W�F�N�g����
			TreePoint[i].x = (int)(Math.random() * GameWidth);	// �����_���ɉ������ݒ�
			TreePoint[i].y = GameHeight - i * TreeHeight;		// �����������ɍ쐬
			TreeType[i] = (int)(Math.random() * 2);				// �����_���Ƀ^�C�v�ݒ�
		}
	}

	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		MoveScreen( );								// ��ʈړ�����
		paint(g);									// paint�݂̂Ń`���c�L�h�~
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.setColor(new Color(99,222,57));			// �����̐F�œh��Ԃ�
		g.fillRect(0, 0, GameWidth, GameHeight);

		for (int i = TreeMax-1; i >= 0; i--)		// ����i��j�̖؂���`�� 
			g.drawImage(Tree[TreeType[i]], TreePoint[i].x, TreePoint[i].y, this);

	}
	// ��ʈړ����� ---------------------------------------------------------------------
	void MoveScreen( ) {
		// �O���i���j�̖؂��珇�Ɉړ����ăQ�[����ʓ��̏ꍇ�́CInputPoint�̈ʒu�ɓ����
		int InputPoint = 0;									// �Z�b�g����ʒu
		for (int i = 0; i < TreeMax; i++) {					// �O���i���j�̖؂���
			TreePoint[i].y += ScrollStep;					// �؂��ړ�
			if (TreePoint[i].y <= GameHeight) {				// �Q�[����ʂ͈͓̔�
				TreePoint[InputPoint].x = TreePoint[i].x;	// InputPoint�̈ʒu�ɓ����
				TreePoint[InputPoint].y = TreePoint[i].y;
				TreeType[InputPoint] = TreeType[i];
				InputPoint++;								// �����ʒu�����炷
			}
		}

		// InputPoint�̈ʒu��TreeMax�͈͓̔��̏ꍇ�C���̈ʒu�ɐV�����؂��Z�b�g
		for (int i = InputPoint; i < TreeMax; i++) {
			MissyuCount++;
			if (MissyuCount > MissyuMax) { 	// ���W���𒴂����ꍇ�C�V���ɖ��W�����ݒ�
				MissyuCount = 0;									// ���W�J�E���g�ݒ�
				MissyuMax = (int)(Math.random() * TreeMax);			// ���W���ݒ�
				MissyuKankaku = (int)(Math.random() * TreeHeight);	// ���W�Ԋu�ݒ�
			}
			TreePoint[i].x = (int)(Math.random() * GameWidth);		// �����_���������ݒ�
			TreePoint[i].y = TreePoint[i-1].y - (int)(Math.random() * MissyuKankaku);
												// �O�̖؂̈ʒu��胉���_���Ɍ��ɃZ�b�g
			if (TreePoint[i].y > -TreeHeight)	// �ˑR��ʂɕ`�悳���ꍇ
				TreePoint[i].y = -GameHeight/2 - (int)(Math.random() * MissyuKankaku);
												// ��ʂɕ`�悳��Ȃ��ʒu�ɐݒ�
			TreeType[i] = (int)(Math.random() * 2);					// �؂̃^�C�v�ݒ�
		}
	}
}

// ======================================================================================
// UFO�N���X
class UFO extends Applet {
	Applet applet;									// �\���Ώہi�A�v���b�g�E�t���[���j
	int GameWidth, GameHeight;						// �Q�[����ʃT�C�Y
	static Image UFOImage[ ] = new Image[6];		// UFO�C���[�W
	int Width, Height;								// UFO�̕��ƍ���
	int X, Y;										// UFO�̒��S�ʒu
	int Current;									// UFO�̕\���ԍ�
	int Step = 3;									// UFO�̈ړ��X�e�b�v
	int UfoCount = 5;								// UFO�̐�
	int explosion = -1;								// UFO�������(-1,0,1,�E�E,5)
	int MouseX, MouseY;								// �}�E�X�̈ʒu
	Image ExplosionImage[ ] = new Image[6];			// �����C���[�W
	AudioClip ExplosionSound[ ] = new AudioClip[5];	// ������
	Shooting shooting;								// Shooting�e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public UFO(Image UFOImage[ ], Image ExplosionImage[ ], AudioClip ExplosionSound[ ],
				int GameWidth, int GameHeight, Applet applet) {
		this.UFOImage = UFOImage;					// UFO�C���[�W
		this.ExplosionImage = ExplosionImage;		// �����C���[�W
		this.ExplosionSound = ExplosionSound;		// ������
		this.GameWidth = GameWidth;					// �Q�[����ʃT�C�Y
		this.GameHeight = GameHeight;
		this.applet = applet;						// �A�v���b�g
		Width = UFOImage[0].getWidth(applet);		// UFO�̕�
		Height = UFOImage[0].getHeight(applet);		// UFO�̍���
		shooting = (Shooting)applet;				// Shooting�e�N���X
	}
	// �C�j�V�����C�Y -------------------------------------------------------------------
	public void initialize( ) {
		UfoCount = 5;								// UFO�̐�
		X = GameWidth / 2;							// UFO�̏����ʒu
		Y = GameHeight - Height / 2;
		Current = 0;								// UFO�̕\���ԍ�
		Step = 3;									// UFO�̈ړ��X�e�b�v
		explosion = -1;								// UFO�������(-1,0,1,�E�E,5)
	}
	// �}�E�X�̈ʒu ---------------------------------------------------------------------
	public void MouseLocate(int MouseX, int MouseY) {
		this.MouseX = MouseX;						// �}�E�X�̈ʒu
		this.MouseY = MouseY;
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		Current++;									// UFO�A�j���[�V�����\���ԍ�
		if (Current >= 6)							// �\����0�`5
			Current = 0;							// �ŏ��̉摜�ԍ�
		move( );									// UFO�ړ�
		paint(g);									// UFO�`��
	}
	// �ړ����� -------------------------------------------------------------------------
	public void move( ) {
		if (Math.abs(X - MouseX) > Step) {			// �}�E�X�Ƃ̋������ړ��X�e�b�v����
			if (X < MouseX)							// �}�E�X��荶���̏ꍇ
				X += Step;							// �E�Ɉړ�
			else if (X > MouseX)					// �}�E�X���E���̏ꍇ
				X -= Step;							// ���Ɉړ�
		}
		if (Math.abs(Y - MouseY) > Step) {			// �}�E�X�Ƃ̋������ړ��X�e�b�v����
			if (Y < MouseY)							// �}�E�X���㑤�̏ꍇ
				Y += Step;							// ���Ɉړ�
			else if (Y > MouseY)					// �}�E�X��艺���̏ꍇ
				Y -= Step;							// ��Ɉړ�
		}
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (explosion >= 0 && explosion <= 5) {		// �����i�K
			if (explosion == 0)						// �����̍ŏ�
				ExplosionSound[0].play( );			// ������
			g.drawImage(ExplosionImage[explosion],
						X - Width, Y - Height, Width*2, Height*2, applet);
			explosion++;							// ���j��Ԑi�s
			if (explosion > 5) {					// �����I��
				UfoCount--;							// UFO�̎c�萔����
				if (UfoCount == 0)
					shooting.gameover( );			// �Q�[���I�[�o�[
				explosion = -1;						// �������Ă��Ȃ����
			}
		}
		g.drawImage(UFOImage[Current], X - Width / 2, Y - Height / 2, this);// UFO�`��
		for (int i = 0; i < UfoCount; i++)			// UFO�c��`��
			g.drawImage(UFOImage[0], i * Width, GameHeight - Height/2,
							 Width/2, Height/2, this);
	}
}

// ======================================================================================
// �~�T�C���}�l�[�W���N���X
class MissileManeger extends Applet {
	Applet applet;									// �\���A�v���b�g(�t���[��)
	static Image MissileImage[ ] = new Image[5];	// �~�T�C���C���[�W
	private Missile missile[ ];						// �~�T�C���I�u�W�F�N�g
	int MissileMax = 10;							// �~�T�C���̐�
	int Kind;										// �~�T�C���̎��
	AudioClip MissileSound[ ] = new AudioClip[5];	// �~�T�C���̔��ˉ�
	Shooting shooting;								// Shooting�e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public MissileManeger(Image MissileImage[ ], AudioClip MissileSound[ ],
			 TekiManeger tekiManeger, UFO ufo, Applet applet) {
		this.MissileImage = MissileImage;			// �~�T�C���C���[�W
		this.MissileSound = MissileSound;			// �~�T�C���̔��ˉ�
		this.applet = applet;						// �A�v���b�g

		shooting = (Shooting)applet;				// Shooting�e�N���X

		missile = new Missile[MissileMax];			// �~�T�C���I�u�W�F�N�g����
		for (int i = 0; i < MissileMax; i++) {
			missile[i] = new Missile(MissileImage, tekiManeger, ufo, applet);
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		for (int i = 0; i < MissileMax; i++) {
			if (missile[i].Status == true) {		// �~�T�C������
				missile[i].move( );					// �~�T�C���ړ�
				missile[i].paint(g);				// �~�T�C���`��
			}
		}
	}
	// �~�T�C������ ---------------------------------------------------------------------
	public void shoot( ) {
		for (int i = 0; i < MissileMax; i++) {
			if (missile[i].Status == false) {		// �~�T�C�������˂���Ă��Ȃ��ꍇ
				missile[i].shoot( );				// �~�T�C������
				MissileSound[missile[i].Kind].play( );	// �~�T�C�����ˉ�
				if (shooting.Score > 0)				// �_��������ꍇ
					shooting.Score--;				// �~�T�C�����˂ɂ����_
				break;
			}
		}
	}
	// �~�T�C�����݃`�F�b�N -------------------------------------------------------------
	public boolean existcheck( ) {
		boolean flag = false;
		for (int i = 0; i < MissileMax; i++) {
			if (missile[i].Status == true) {		// �~�T�C�����˒�
				flag = true;						// ���݃t���O�@�I��
				break;
			}
		}
		return flag;
	}	
}

// ======================================================================================
// �~�T�C���N���X
class Missile {
	Applet applet;									// �A�v���b�g
	static Image MissileImage[ ] = new Image[5];	// �~�T�C���C���[�W
	int Width, Height;								// �~�T�C���̕��ƍ���
	int Kind;										// �~�T�C���̎��
	int X, Y;										// �~�T�C���̈ʒu
	int Step = 3;									// �~�T�C���̈ړ��X�e�b�v
	boolean Status;									// �~�T�C���̐������
	TekiManeger tekiManeger;						// �G�}�l�[�W��
	UFO ufo;										// UFO�N���X�̃I�u�W�F�N�g
	Shooting shooting;								// Shooting�e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public Missile(Image MissileImage[ ],TekiManeger tekiManeger,
					UFO ufo, Applet applet) {
		this.MissileImage = MissileImage;			// �~�T�C���C���[�W
		this.applet = applet;						// �A�v���b�g
		this.tekiManeger = tekiManeger;				// �G�}�l�[�W���N���X
		this.ufo = ufo;								// UFO�N���X�̃I�u�W�F�N�g
		Status = false;								// ���˂���Ă��Ȃ����
		shooting = (Shooting)applet;				// Shooting�e�N���X
	}
	// ���ˏ��� -------------------------------------------------------------------------
	public void shoot( ) {
		Kind = tekiManeger.Kind;					// ���˂̎�ށF�G�̎��
		X = ufo.X;									// ���ˈʒu�@�FUFO�̈ʒu
		Y = ufo.Y;
		Width = MissileImage[Kind].getWidth(applet);// �G�̕��ƍ���
		Height = MissileImage[Kind].getHeight(applet);
		Status = true;								// �~�T�C���g�p���@����
	}
	// �ړ����� -------------------------------------------------------------------------
	public void move( ) {
		if (Y > -Height)							// �A�v���b�g���ɕ\�������͈͓�
			Y -= Step;								// ���������ړ�
		else
			Status = false;							// �~�T�C����O�ց@����

		for (int i = 0; i < 5; i++) {				// �G�Ƃ̐ڐG����
			if (tekiManeger.teki[i].Status == true	// �G���A�v���b�g���ɑ���
				&& tekiManeger.teki[i].Kind == Kind) { // �G�ƃ~�T�C�����������
				// �G�Ƃ̐ڐG�`�F�b�N
				if (Math.abs(X - tekiManeger.teki[i].X)
						< Width/2 + tekiManeger.teki[i].Width/2
				 && Math.abs(Y - tekiManeger.teki[i].Y) 
						< Height/2 + tekiManeger.teki[i].Height/2) {
					tekiManeger.teki[i].explosion = 0;	// ���j�J�n
					Status = false;					// �~�T�C������
					shooting.Score += 10;			// �������_
				}
			}
		}
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(MissileImage[Kind], X - Width / 2, Y - Height / 2, applet);
	}
}

// ======================================================================================
// �G�}�l�[�W���N���X
class TekiManeger extends Applet {
	Applet applet;									// �\���Ώہi�A�v���b�g�E�t���[��)
	Image TekiImage[ ] = new Image[5];				// �G�C���[�W
	Teki teki[ ];									// �G�I�u�W�F�N�g
	int Width, Height;								// �G�̕��ƍ���
	int TekiMax = 5;								// �G�̐�
	int X, Y;										// �G�̒��S�ʒu
	int Step;										// �ړ��X�e�b�v
	int Kind;										// �G�̎��
	int Pattern;									// �G�̃p�^�[��
	long AttackTime;								// �U���J�n����
	MissileManeger missileManeger;					// �~�T�C���}�l�[�W���̃I�u�W�F�N�g
	Shooting shooting;								// Shooting�e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public TekiManeger(Image TekiImage[ ], Image ExplosionImage[ ],
						AudioClip ExplosionSound[ ], UFO ufo , Applet applet) {

		this.TekiImage = TekiImage;					// �G�C���[�W
		this.applet = applet;						// �A�v���b�g
		teki = new Teki[TekiMax];					// �G�I�u�W�F�N�g����
		for (int i = 0; i < TekiMax; i++) {
			teki[i] = new Teki(TekiImage, ExplosionImage, ExplosionSound, ufo, applet);
		}
		shooting = (Shooting)applet;				// Shooting�e�N���X
	}
	// �~�T�C�����擾 -----------------------------------------------------------------
	public void information(MissileManeger missileManeger) {
		this.missileManeger = missileManeger;		// �~�T�C���}�l�[�W���I�u�W�F�N�g
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		boolean TekiExist = false;					// �G�̑��݃t���O
		for (int i = 0; i < TekiMax; i++) {
			if (teki[i].Status == true) {			// �G����
				teki[i].move( );					// �G�ړ�
				teki[i].paint(g);					// �G�`��
				TekiExist = true;					// �G����
			}
		}

		// �G���~�T�C�������݂��Ȃ��ꍇ
		if (TekiExist == false && missileManeger.existcheck( ) == false) {
			Kind = (int)(Math.random( ) * TekiMax);		// �G�̎��
			Width = TekiImage[Kind].getWidth(applet);	// �G�̕��ƍ���
			Height = TekiImage[Kind].getHeight(applet);

			Date now = new Date( );						// �U�����Ԑݒ�
			long nowtime = now.getTime( );				// ���ݎ���
			Pattern = (int)(Math.random( ) * 6);		// �U���p�^�[��
			for (int i = 0; i < TekiMax; i++) {			// �܂̓G�𐶐�
				X = shooting.GameWidth / (TekiMax + 1) * (i + 1);
				Y = - Height * 2;						// �G�̈ʒu
				Step = (int)(Math.random( ) * 5) + 3;	// �ړ��X�e�b�v
				// �U���p�^�[���ōU���J�n���Ԑݒ�
				switch (Pattern) {
					case 0 :	// �������i(���ׂ�5�b��ɔ��i)
							AttackTime = nowtime + 1000 * 5;
							break;
					case 1 :	// �����珇�ɔ��i(5,6,7,����b�㔭�i)
							AttackTime = nowtime + 1000 * (5 + i);
							break;
					case 2 : 	// �E���珇�ɔ��i(5,6,7,����b�㔭�i)
							AttackTime = nowtime + 1000 * (5 + (4 -i));
							break;
					case 3 :	// �������珇�ɔ��i(5,6,7,����b�㔭�i)
							AttackTime = nowtime + 1000 * (5 + Math.abs(2 - i));
						 	break;
					case 4 : 	// �[���珇�ɔ��i(5,6,7,����b�㔭�i)
							AttackTime = nowtime + 1000 * (5 + 2 - Math.abs(2 - i)); 
						 	break;
					case 5 : 	// �����_���ɔ��i
							AttackTime = nowtime + 1000 * (5 + (int)(Math.random( ) * 5));
						 	break;
				}
				teki[i].initialize(Kind, X, Y, AttackTime, Step);	// �G�̏���������
			}
		}
	}
}

// --------------------------------------------------------------------------------------
// �G�N���X
class Teki {
	Applet applet;									// �A�v���b�g
	static Image TekiImage[ ] = new Image[5];		// �G�C���[�W
	int Width, Height;								// �G�̕��ƍ���
	int Kind;										// �G�̎��
	int X, Y;										// �G�̒��S�ʒu
	int Step;										// �G�̈ړ��X�e�b�v
	boolean Status;									// �G�̐������
	long AttackTime;								// �U���J�n����
	Image ExplosionImage[ ] = new Image[6];			// �����C���[�W
	int explosion;									// ���j�J�E���g
	AudioClip ExplosionSound[ ] = new AudioClip[5];	// ������
	UFO ufo;										// UFO�N���X�̃I�u�W�F�N�g
	Shooting shooting;								// Shooting�e�N���X

	// �R���X�g���N�^ -------------------------------------------------------------------
	public Teki(Image TekiImage[ ], Image ExplosionImage[ ], AudioClip ExplosionSound[ ],
			UFO ufo, Applet applet) {
		this.TekiImage = TekiImage;					// �G�C���[�W
		this.ExplosionImage = ExplosionImage;		// �����C���[�W
		this.ExplosionSound = ExplosionSound;		// ������
		this.ufo = ufo;								// UFO�N���X
		this.applet = applet;						// �A�v���b�g
		Status = false;								// �G����
		shooting = (Shooting)applet;				// Shooting�e�N���X
	}
	// ���������� -----------------------------------------------------------------------
	public void initialize(int Kind, int X, int Y, long AttackTime, int Step) {
		this.Kind = Kind;							// �G�̎��
		this.X = X;									// �G�̒��S�ʒu
		this.Y = Y;
		this.AttackTime = AttackTime;				// �U���J�n����
		this.Step = Step;							// �U���ړ��X�e�b�v
		Status = true;								// �G�̐������
		explosion = -1;								// ���j�J�E���g
		Width = TekiImage[Kind].getWidth(applet);	// �G�̕��ƍ���
		Height = TekiImage[Kind].getHeight(applet);
	}
	// �ړ����� -------------------------------------------------------------------------
	public void move( ) {
		Date now = new Date( );						// �U�����Ԑݒ�
		long nowtime = now.getTime( );				// ���ݎ���
		if (Y < Height)								// �X�^�[�g���C���ɒB���Ă��Ȃ��ꍇ
			Y += Step;								// ���������ړ�
		if (nowtime > AttackTime) {					// �U�����ԂɒB�����ꍇ
			Y += Step;								// ���������ړ�
			if (X < ufo.X)							// UFO��荶��
				X += 1;								// �E�Ɉړ�
			else if (X > ufo.X)						// UFO���E��
				X -= 1;								// ���Ɉړ�
		}
		if (Y > shooting.GameHeight + Height)		// �Q�[����ʂ̊O�ɏo���ꍇ
			Status = false;							// �G���݂���
		// UFO�Ƃ̐ڐG����
		if (Math.abs(X - ufo.X) < Width/2 + ufo.Width/2
		 && Math.abs(Y - ufo.Y) < Height/2 + ufo.Height/2) {
			ufo.explosion = 0;						// UFO���j�J�n
			Status = false;							// �G���݂��� ����
		}
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(TekiImage[Kind], X - Width / 2, Y - Height / 2, applet);

		if (explosion >= 0 && explosion <= 5) {		// ������Ԃ̏ꍇ
			if (explosion == 0)						// �����̍ŏ�
				ExplosionSound[Kind].play( );		// ������
			g.drawImage(ExplosionImage[explosion],
						X - Width, Y - Height, Width*2, Height*2, applet);
			explosion++;							// ������Ԑi�s
			if (explosion > 5) {					// �����I��
				Status = false;						// �G���݂��� ����
			}
		}
	}
}
