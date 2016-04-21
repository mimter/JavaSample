import java.applet.*;		// Applet, AudioClip
import java.awt.*;			// Graphics, Image, Color, MediaTracker, Button
import java.awt.event.*;	// KeyListener, KeyEvent, ActionListener, ActionEvent

public class MakeRoad extends Applet
						implements KeyListener, Runnable, ActionListener {

	Image Ufo;										// UFO�摜
    Image panel[ ] = new Image[7];					// 7��ނ̃p�l���摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	int SpaceRow, SpaceColumn;						// �X�y�[�X�̍s�Ɨ�
	int Base;										// �\���x�[�X�|�C���g
	int UfoX, UfoY,									// UFO�̃O���t�B�b�N��ł̈ʒu
        UfoRow, UfoColumn, 							// UFO�̃p�l����ł̍s�Ɨ�
        UfoStepCount,       						// �e�p�l�����ł̃X�e�b�v�J�E���g
		UfoInX,										// UFO������Ƃ���X����  1:��  -1:��
		UfoInY,										// UFO������Ƃ���Y����  1:��  -1:��
		UfoSize;									// UFO�T�C�Y

	int Map[ ][ ] = new int[4][4];					// �}�b�v

	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int PanelSize;									// �p�l���T�C�Y
	int Speed = 10;									// �Q�[���X�s�[�h(10 �`�@100)

	boolean GameStartFlag = false;					// �Q�[���X�^�[�g�t���O
    boolean GameOverFlag = true;					// �Q�[���I�[�o�[�t���O
	boolean GameClearFlag = false;					// �Q�[���N���A�t���O

	Thread thread = null;							// �X���b�h

	Button StartButton;								// �X�^�[�g�{�^��

    AudioClip BGM, GameoverSound;					// BGM�ƃQ�[���I�[�o�[�T�E���h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
        BGM = getAudioClip(getCodeBase( ), "sound/loop.au");			// �a�f�l
        GameoverSound = getAudioClip(getCodeBase( ), "sound/gong.au");	// �Q�[���I�[�o�[�T�E���h

		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		imageLoad( );								// �摜�f�[�^����

		mapset( );									// �}�b�v�쐬

		UfoInitial( );								// UFO�̏�����

		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		StartButton.setBounds(Base-60, Base-30, 60, 30);	// �{�^���̔z�u�ƃT�C�Y�ݒ�

		addKeyListener(this);						// ���X�i�[�ǉ�
		requestFocus( );							// �L�[���̓t�H�[�J�X��v��
	}
	// �摜���[�h���� -------------------------------------------------------------------
	public void imageLoad( ) {
		MediaTracker  mt = new MediaTracker(this);	// �摜���͂��Ď����郁�f�B�A�g���b�J
		for (int i = 0; i < 7; i++) {				// 7��ނ̃p�l���摜����
			panel[i] = getImage(getCodeBase( ), "image/panel"+i+".gif");
			mt.addImage(panel[i], 0);				// ���f�B�A�g���b�J �ɃC���[�W�Z�b�g
		}
		Ufo = getImage(getCodeBase( ), "image/Ufo.gif");// UFO�摜����
		mt.addImage(Ufo, 0);						// ���f�B�A�g���b�J �ɃC���[�W�Z�b�g

		try {
			mt.waitForID(0);						// �C���[�W�摜�̓��͊�����҂�
		} catch(InterruptedException e) {
			showStatus(" "+e);
		}

		PanelSize = PanelSize = panel[0].getWidth(this);	// �p�l���T�C�Y
        Base = (AppletWidth - PanelSize*4)/2;		// �\����{�ʒu �p�l���𒆉��ɕ\��
		UfoSize = Ufo.getWidth(this);				// UFO�T�C�Y
	}
	// �}�b�v�쐬 -----------------------------------------------------------------------
	public void mapset( ) {
		int p = 0;
		for (int r = 0; r < 4; r++)					// �sRow�������[�v
			for (int c = 0; c < 4; c++)				// ��Column�������[�v
				Map[r][c] = p++ / 2; 				// �摜�ԍ��ݒ�
				
		Map[3][2] = (int)(Math.random( )*7);		// �����_���ɂ����ꖇ�ǉ�
		Map[3][3] = -1;								// ��
		SpaceRow = SpaceColumn = 3;					// �󔒂̈ʒu�i�sRow �� ��Column�j
		// �����_���Ƀ}�X�̃p�l�������ւ���
		for (int r = 0; r < 4; r++) {				// �s�����Ƀ��[�v
			for (int c = 0; c < 4; c++) {			// ������Ƀ��[�v
				int rp = (int)(Math.random( )*4);	// �����_���s�|�C���g�@0 �` 3
				int cp = (int)(Math.random( )*4);	// �����_����|�C���g�@0 �` 3
				// �X�^�[�g�n�_�ƃS�[���n�_�͌������Ȃ�
				if (!(r+c == 0 || rp+cp == 0 || r*c == 9 || rp*cp == 9)) {
					int temp = Map[r][c];			// ��������
					Map[r][c] = Map[rp][cp];
					Map[rp][cp] = temp;
				}
			}
		}
    }
	// UFO�̏����� ----------------------------------------------------------------------
	private void UfoInitial( ) {
		// UFO�̃O���t�B�b�N��ł̈ʒu
		UfoX = Base - UfoSize / 2;
		UfoY = Base + PanelSize / 2 - UfoSize / 2;
		// UFO�̃p�l����ł̈ʒu
        UfoRow = UfoColumn = 0;
		// UFO���e�p�l���ɓ����Ă���̏����^�C���J�E���g���O�N���A
        UfoStepCount = 0;
		// UFO���p�l���ɓ�������@�E����
		UfoInX = 1;
		UfoInY = 0;
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this); 					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g) {
		WorkGraphics.setColor(Color.black);						// �w�i�F
        WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);	// ��Ɨ̈��h��Ԃ�

        WorkGraphics.drawImage(panel[0], Base-PanelSize, Base, this);// �����̃p�l���`��
		// �}�b�v���e��`��
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (Map[r][c] != -1)				// �󔒂łȂ��ꍇ
					// �p�l����`��
			     	WorkGraphics.drawImage(panel[Map[r][c]],
										Base+c*PanelSize, Base+r*PanelSize, this);
			}
		}
		WorkGraphics.setColor(Color.white);
		WorkGraphics.drawString("�S�[��",
					 4*PanelSize+Base + 15, 3*PanelSize+Base - 5);	// �S�[���\��
        WorkGraphics.drawImage(panel[0], 4*PanelSize+Base, 3*PanelSize+Base, this);	
        WorkGraphics.drawImage(Ufo, UfoX, UfoY, this);				// UFO�`��

		WorkGraphics.setColor(Color.yellow);
		if (GameStartFlag == true && GameClearFlag == true)		// �Q�[���N���A�`�F�b�N
			WorkGraphics.drawString("Game CLear", Base, Base+PanelSize*4+10);
		if (GameStartFlag == true && GameOverFlag == true)		// �Q�[���I�[�o�[�`�F�b�N
			WorkGraphics.drawString("Game over", Base, Base+PanelSize*4+10);
      
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ) {
		while(thread != null) {						// �X���b�h�����݂��Ă����
			if (GameStartFlag == true && GameOverFlag == false)	// �Q�[���t���O�I��
				move( );							// UFO�ړ�����
			try {
				thread.sleep(300 - Speed);			// �X���b�h�X���[�v
			} catch(InterruptedException evt) {
			}
			repaint( );								// �ĕ`��
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g�I�� -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h���k���ɂ��Ė����ɂ���
		BGM.stop( );								// �a�f�l�I��
	}
	// �ړ����� -------------------------------------------------------------------------
    private void move( ) {
		if (UfoRow == 3 && UfoColumn == 4) {		// �o���ɓ��� �Q�[���N���A
			gameclearProcess( );					// �Q�[���N���A����
			return;
		}
		else if (UfoRow < 0 || UfoRow > 3 || UfoColumn < 0 || UfoColumn > 3) {
			// �}�b�v�����яo�����ꍇ�C�Q�[���I�[�o�[
			gameoverProcess( );						// �Q�[���I�[�o�[����
			return;
		}
		// �p�l�� - or +  ������� ��   --------------------------------------------------
		if ((Map[UfoRow][UfoColumn] == 0 || Map[UfoRow][UfoColumn] == 2)




				&& UfoInX == 1 && UfoInY == 0) {	// ������� ��
			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= PanelSize)			// �p�l���͈͓̔��̏ꍇ
				UfoX=UfoX+1;						// �w�����Ɂ{�P
			if (UfoStepCount == PanelSize) {		// �p�l���̒[�ɓ��B
				UfoColumn++;						// �E�̃p�l���Ɉړ�
				UfoInX = 1;							// ���̃p�l���ɓ�������@��
				UfoInY = 0;	
				UfoStepCount = 0;					// �X�e�b�v�J�E���g���O�N���A
			}
		}
		// �p�l�� - or +  ������� ��   --------------------------------------------------
		else if ((Map[UfoRow][UfoColumn] == 0 || Map[UfoRow][UfoColumn] == 2)




				&& UfoInX == -1 && UfoInY == 0) {	// ������� ��
			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= PanelSize)			// �p�l���͈͓̔��̏ꍇ
				UfoX=UfoX-1;						// �w������-�P
			if (UfoStepCount == PanelSize) {		// �p�l���̒[�ɓ��B
				UfoColumn--;						// ���̃p�l���Ɉړ�
				UfoInX = -1;						// ���̃p�l���ɓ�������@��
				UfoInY = 0;	
				UfoStepCount = 0;					// �X�e�b�v�J�E���g���O�N���A
			}
		}
		// �p�l�� | or +  ������� ��   --------------------------------------------------
		else if ((Map[UfoRow][UfoColumn] == 1 || Map[UfoRow][UfoColumn] == 2)




				&& UfoInX == 0 && UfoInY == 1) {	// ������� ��
			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= PanelSize)			// �p�l���͈͓̔��̏ꍇ
				UfoY=UfoY+1;						// Y������+�P
			if (UfoStepCount == PanelSize) {		// �p�l���̒[�ɓ��B
				UfoRow++;							// ���̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = 1;	
				UfoStepCount = 0;					// �X�e�b�v�J�E���g���O�N���A
			}
		}
		// �p�l�� | or +  ������� ��   --------------------------------------------------
		else if ((Map[UfoRow][UfoColumn] == 1 || Map[UfoRow][UfoColumn] == 2)




				&& UfoInX == 0 && UfoInY == -1) {	// ������� ��
			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= PanelSize)			// �p�l���͈͓̔��̏ꍇ
				UfoY=UfoY-1;						// Y����-�P
			if (UfoStepCount == PanelSize) {		// �p�l���̒[�ɓ��B
				UfoRow--;							// ��̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = -1;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g���O�N���A
			}
		}
		// �p�l�� �~�`�E��  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 3 && UfoInX == 1 && UfoInY == 0) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoX=UfoX+1;						// �w�����{�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX+1;						// �w�����{�P
				UfoY=UfoY+1;						// �x�����{�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoY=UfoY+1;						// �x�����{�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoRow++;							// UFO�����̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = 1;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`�E��  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 3 && UfoInX == 0 && UfoInY == -1) {




			UfoStepCount++;							// �X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoY=UfoY-1;						// �x�����\�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX-1;						// �w�����\�P
				UfoY=UfoY-1;						// �x�����\�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoX=UfoX-1;						// �w�����\�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoColumn--;						// UFO�����̃p�l���Ɉړ�
				UfoInX = -1;						// ���̃p�l���ɓ�������@��
				UfoInY = 0;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`�E��  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 4 && UfoInX == 0 && UfoInY == 1) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoY=UfoY+1;						// �x�����{�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX-1;						// �w�����\�P
				UfoY=UfoY+1;						// �x�����{�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoX=UfoX-1;						// �w�����\�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoColumn--;						// UFO�����̃p�l���Ɉړ�
				UfoInX = -1;						// ���̃p�l���ɓ�������@��
				UfoInY = 0;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`�E��  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 4 && UfoInX == 1 && UfoInY == 0) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoX=UfoX+1;						// �w�����{�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX+1;						// �w�����{�P
				UfoY=UfoY-1;						// �x�����\�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoY=UfoY-1;						// �x�����\�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoRow--;							// UFO����̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = -1;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`����  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 5 && UfoInX == 0 && UfoInY == 1) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoY=UfoY+1;						// �x�����{�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX+1;						// �w�����{�P
				UfoY=UfoY+1;						// �x�����{�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoX=UfoX+1;						// �w�����{�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoColumn++;						// UFO���E�̃p�l���Ɉړ�
				UfoInX = 1;							// ���̃p�l���ɓ�������@��
				UfoInY = 0;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`����  	���������  --------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 5 && UfoInX == -1 && UfoInY == 0) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoX=UfoX-1;						// �w�����\�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX-1;						// �w�����\�P
				UfoY=UfoY-1;						// �x�����\�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoY=UfoY-1;						// �x�����\�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoRow--;							// UFO����̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = -1;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`����  	���������	--------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 6 && UfoInX == 0 && UfoInY == -1) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoY=UfoY-1;						// �x�����\�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX+1;						// �w�����{�P
				UfoY=UfoY-1;						// �x�����\�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoX=UfoX+1;						// �w�����{�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoColumn++;						// UFO���E�̃p�l���Ɉړ�
				UfoInX = 1;							// ���̃p�l���ɓ�������@��
				UfoInY = 0;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		// �p�l�� �~�`����  	������� ��	--------------------------------------------
		else if (Map[UfoRow][UfoColumn] == 6 && UfoInX == -1 && UfoInY == 0) {




			UfoStepCount++;							// �p�l�����̃X�e�b�v�J�E���g
			if (UfoStepCount <= 11)					// �X�e�b�v�J�E���g11�܂�
				UfoX=UfoX-1;						// �w�����\�P
			else if (UfoStepCount <= 31) {			// �X�e�b�v�J�E���g12�`31�܂�
				UfoX=UfoX-1;						// �w�����\�P
				UfoY=UfoY+1;						// �x�����{�P
			}
			else if (UfoStepCount <= 41) {			// �X�e�b�v�J�E���g32�`41�܂�
				UfoY=UfoY+1;						// �x�����{�P
			}
			if (UfoStepCount == 41) {				// �p�l���̒[�ɓ��B
				UfoRow++;							// UFO�����̃p�l���Ɉړ�
				UfoInX = 0;							// ���̃p�l���ɓ�������@��
				UfoInY = 1;
				UfoStepCount = 0;					// �X�e�b�v�J�E���g�O�N���A
			}
		}
		else										// ��������ƃp�l������v���Ȃ��ꍇ
			gameoverProcess( );						// �Q�[���I�[�o�[����
	}
	// �Q�[���N���A���� -----------------------------------------------------------------
	private void gameclearProcess( ) { 
			GameClearFlag = true;					// �Q�[���N���A
			BGM.stop( );							// BGM�X�g�b�v
			if (Speed < 100)						// �X�s�[�h��100��菬�����ꍇ
				Speed += 10;						// �X�s�[�h�A�b�v
	}
	// �Q�[���I�[�o�[���� ---------------------------------------------------------------
	private void gameoverProcess( ) {	
			GameOverFlag = true;					// �Q�[���I�[�o�[�t���O���I��
			BGM.stop( );							// BGM�X�g�b�v
			GameoverSound.play( );					// �Q�[���I���S���O
	}

	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button bt = (Button)evt.getSource( );
		if (bt == StartButton) {					// �X�^�[�g�{�^��
			if (GameOverFlag == true || GameClearFlag == true)
				// �Q�[���I�[�o�[�܂��̓Q�[���N���A�̏ꍇ
				gameStart( );						// �Q�[���X�^�[�g
		}
		requestFocus( );							// �L�[���̓t�H�[�J�X��v��
	}
	// �Q�[���J�n���� -------------------------------------------------------------------
	private void gameStart( ) {
		GameOverFlag = false;						// �Q�[���I�[�o�[�t���O�ݒ�
		GameClearFlag = false;						// �Q�[���N���A�t���O�ݒ�
		GameStartFlag = true;						// �Q�[���X�^�[�g�t���O�ݒ�
		mapset( );									// �V�����}�b�v�ݒ�
		UfoInitial( );								// UFO�ď�����
		requestFocus( );							// �L�[���̓t�H�[�J�X��v��
		BGM.loop( );								// �a�f�l�X�^�[�g
	}
   	// KeyListener�C���^�t�F�[�X�̊e���\�b�h���` --------------------------------------
	public void keyPressed(KeyEvent evt) {
		switch (evt.getKeyCode( )) {	
		    case KeyEvent.VK_DOWN  :				// ���L�[
                change(SpaceRow+1, SpaceColumn);	// ���ƌ���
                break;
	        case KeyEvent.VK_UP    :				// ��L�[
                change(SpaceRow-1, SpaceColumn);	// ��̃p�l���ƌ���
                break;
		    case KeyEvent.VK_RIGHT : 				// �E�L�[
                change(SpaceRow, SpaceColumn+1);	// �E�̃p�l���ƌ���
                break;
	        case KeyEvent.VK_LEFT  : 				// ���L�[
                change(SpaceRow, SpaceColumn-1);	// ���̃p�l���ƌ���
                break;
	    }
		repaint( );									// �ĕ`��
	}
	public void keyReleased(KeyEvent evt) { }
	public void keyTyped(KeyEvent evt) { }
	// �^�[�Q�b�g�̃p�l�����X�y�[�X�Ɉړ� -----------------------------------------------
	private void change(int tpR, int tpC) {
		// �^�[�Q�b�g�̍s�Ɨ�UFO�̍s�Ɨ�łȂ��CMap�͈͓̔��ł����
		if (!(tpR == UfoRow && tpC == UfoColumn)
		 && tpR >= 0 && tpR <= 3 && tpC >= 0 && tpC <= 3) {
			int temp = Map[tpR][tpC];				// �^�[�Q�b�g�̃p�l���ƃX�y�[�X������
			Map[tpR][tpC] = Map[SpaceRow][SpaceColumn];
			Map[SpaceRow][SpaceColumn] = temp;
			SpaceRow = tpR;							// �^�[�Q�b�g�ʒu���X�y�[�X�ʒu�ɐݒ�
			SpaceColumn = tpC;
		}
	}
}
