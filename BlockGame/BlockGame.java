import java.applet.*;			// Applet, AudioClip
import java.awt.*;				// Graphics, Image, Color, MediaTracker
import java.awt.event.*;		// KeyListener, KeyEvent, MouseListener, MouseEvent

public class BlockGame extends Applet 
		implements Runnable, KeyListener, ActionListener {

	Thread thread = null;							// �A�j���[�V�����p�X���b�h
	Image BallImage, BlockImage, RacketImage;		// �{�[���C�u���b�N�C���P�b�g�C���[�W
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int BallX, BallY,								// �{�[���̈ʒu
		BallPreX, BallPreY,							// �{�[���̑O��ʒu
		BallXStep, BallYStep,						// �{�[���̈ړ��X�e�b�v�l
		BallSize;									// �{�[���T�C�Y
	int BlockWidth, BlockHeight;					// �u���b�N�̕��ƍ���
	int RacketWidth, RacketHeight;					// ���P�b�g�T�C�Y
	int AppletWidth, AppletHeight;					// �A�v���b�g�T�C�Y
	int Score;										// �X�R�A
	int RacketX, RacketY;							// ���P�b�g�̈ʒu
	int BlockX, BlockY;								// �u���b�N�\���ʒu
	int BlockRow, BlockColumn;						// �u���b�N�̍s��
	int Block[ ][ ] = new int[20][30];				// �u���b�N�̊m�F�p�z��
	int BallCheckX[ ] = new int[8];					// �{�[���`�F�b�N�|�C���g
	int BallCheckY[ ] = new int[8];
	int HantenX[ ] = new int[8];					// ���]�v�Z�l
	int HantenY[ ] = new int[8];
	int	KeyDownSw;									// KeyDown�����X�C�b�`
	int RaketStep = 10;								// ���P�b�g�ړ��l
	int PreRacketHit;								// �O�񃉃P�b�g�Փ˒l
	boolean GameStartSw = false;					// �Q�[���X�^�[�g�X�C�b�`
	MediaTracker mt = new MediaTracker(this);		// �摜���͊Ď����f�B�A�g���b�J����
    AudioClip HitSound;								// �q�b�g�T�E���h
	int BallCount = 3;								// �{�[���̐�
	int BallSpeed = 1;								// �{�[���̃X�s�[�h
	int ClearCount = 0;								// �N���A��
	int SleepTime = 10, WaitTime = 1000;			// �X���[�v�^�C��

	Button StartButton;								// �X�^�[�g�{�^��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �p�����[�^�擾
		BlockX = Integer.parseInt(getParameter("BlockX"));			// �u���b�N�̕\���ʒu
		BlockY = Integer.parseInt(getParameter("BlockY"));
		BlockRow = Integer.parseInt(getParameter("BlockRow"));		// �u���b�N�̍s��
		BlockColumn = Integer.parseInt(getParameter("BlockColumn"));// �u���b�N�̗�

		addKeyListener(this);						// �L�[���X�i�[�ǉ�

		//�@�T�E���h�Ɖ摜�f�[�^����
		//�@�{�[���͓���GIF�ŁC�T�C�Y�͊�ł��邱��  ���R�F���E�ƃZ���^�[
		//�@�u���b�N�ƃ��P�b�g�͋�`�ł��邱��
        HitSound = getAudioClip(getCodeBase( ), "sound/hit.au");		// �N���b�N��

		BallImage = getImage(getCodeBase( ), getParameter("ball"));		// �{�[���摜
		mt.addImage(BallImage, 0);					// �摜�����f�B�A�g���b�J�ɐݒ�
		BlockImage = getImage(getCodeBase( ), getParameter("block"));	// �u���b�N�摜
		mt.addImage(BlockImage, 0);
		RacketImage = getImage(getCodeBase( ), getParameter("racket"));	// ���P�b�g�摜
		mt.addImage(RacketImage, 0);
		try {
			mt.waitForID(0);						// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);					// �G���[�\��
		}

		BallSize = BallImage.getWidth(this);		// �{�[���T�C�Y
		BlockWidth = BlockImage.getWidth(this);		// �u���b�N�T�C�Y
		BlockHeight = BlockImage.getHeight(this);
		RacketWidth = RacketImage.getWidth(this);	// ���P�b�g�T�C�Y
		RacketHeight = RacketImage.getHeight(this);
		AppletWidth = getSize( ).width;				// �A�v���b�g�T�C�Y
		AppletHeight = getSize( ).height;

		WorkImage = createImage(AppletWidth, AppletHeight); // ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		StartButton.setBounds(AppletWidth/2-30, AppletHeight-40, 60, 30);

		RacketX = (AppletWidth - RacketWidth) / 2;	// ���P�b�g�̈ʒu
		RacketY = AppletHeight - RacketHeight * 2;

		Score = 0;									// �X�R�A
		CheckPointMake( );							// �{�[���̎��͂̃`�F�b�N�|�C���g�쐬
		GameScreenDisplay( );						// �Q�[����ʕ`��
		BallSet(1);									// �{�[�������ʒu�ݒ�
	}
	// �{�[���̊O���|�C���g�y�є��]�v�Z�l�ݒ� -------------------------------------------
	public void CheckPointMake( ) {					// �{�[�����͂W�_����
		int r = BallSize / 2;						// ���a
		int w = (int)(r * Math.sin(45 * 3.14 / 180));		// ���~sin(45)
		//�@BallCheckX, BallCheckY�F���ΓI�Ȉʒu�CHantenX,HantenY�F�Փˎ��̔��]�v�Z�l
		BallCheckX[0]=r;         BallCheckY[0]=0;        HantenX[0]=1; HantenY[0]=-1;
		BallCheckX[1]=r+w;       BallCheckY[1]=r-w;      HantenX[1]=-1; HantenY[1]=-1;
		BallCheckX[2]=BallSize-1;BallCheckY[2]=r;        HantenX[2]=-1; HantenY[2]=1;
		BallCheckX[3]=r+w;       BallCheckY[3]=r+w;      HantenX[3]=-1; HantenY[3]=-1;
		BallCheckX[4]=r;         BallCheckY[4]=BallSize-1;HantenX[4]=1; HantenY[4]=-1;
		BallCheckX[5]=r-w;       BallCheckY[5]=r+w;      HantenX[5]=-1;HantenY[5]=-1;
		BallCheckX[6]=0;         BallCheckY[6]=r;        HantenX[6]=-1; HantenY[6]=1;
		BallCheckX[7]=r-w;       BallCheckY[7]=r-w;      HantenX[7]=-1; HantenY[7]=-1;
	}
	// �Q�[����ʕ`�� -------------------------------------------------------------------
	public void GameScreenDisplay( ) {
		WorkGraphics.setPaintMode( );				// �㏑�����[�h

		// �g�`��
		WorkGraphics.setColor(Color.white);			// ���F�œh��Ԃ�
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		WorkGraphics.setColor(Color.black);			// �O�g���쐬
		WorkGraphics.drawRect(0, 0, AppletWidth - 1, AppletHeight - 1);

		// �u���b�N�`��
		for (int i = 0; i < BlockRow; i++) {		// �c�����@�s
			for (int j = 0; j < BlockColumn; j++) {	// �������@��
				WorkGraphics.drawImage(BlockImage,	// ��ƃO���t�B�b�N�Ƀu���b�N�`��
					BlockX + BlockWidth * j, 
					BlockY + BlockHeight * i, this);
				Block[i][j] = 1;					// �z��Ƀu���b�N�Z�b�g
			}
		}

		// ���P�b�g�`��
		WorkGraphics.drawImage(RacketImage,RacketX, RacketY, this);

		WorkGraphics.setXORMode(Color.white);		// �d�˕`���w�i�F�����[�h
	}
	// �{�[���̐ݒ� ---------------------------------------------------------------------
	public void BallSet(int UseFlag) {
		if (UseFlag == 1)							// �V�����{�[����K�v�Ƃ���ꍇ
			BallCount--;							// �{�[���̐������炷
		if (BallCount < 0)							// �{�[�����Ȃ��Ȃ����ꍇ
			return;

		BallX = BallPreX = (AppletWidth - BallSize) / 2;	// �{�[���̌��݋y�ёO��ʒu
		BallY = BallPreY = BlockY + BlockHeight * BlockRow;
		BallSpeed = ClearCount + 1;					// �{�[���X�s�[�h
		if (BallSpeed > 5)
			BallSpeed = 5;
		BallXStep = 1;								// ���������̈ړ��X�s�[�h
		if ((int)(Math.random( ) * 2) == 0)			// �����_���ɕ��������E�ɐݒ�
			BallXStep = -1;
		BallYStep = 1;								// ���������̈ړ��X�s�[�h

		WorkGraphics.drawImage(BallImage, BallX, BallY, this);	// �{�[���`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
        while (thread != null && BallCount >= 0) {	// �X���b�h�ƃ{�[�����܂�����ꍇ
			if (GameStartSw) {						// �Q�[���X�^�[�g�X�C�b�`��true
				try {
					repaint( );						// �ĕ`�� -> update( )
					thread.sleep(SleepTime - BallSpeed + WaitTime);	// �X���b�h�X���[�v
					if (WaitTime > 0)				// �ŏ������̑҂�����
						WaitTime = 0;				// �{�[���������Α҂����Ԃ�0�ɂ���
				}
				catch (InterruptedException e) {	// sleep�ɑ΂����O����
				}
				KeyDownSw = 0;						// KeyDown�����X�C�b�`�s�� 
				BallProcess( );						// �{�[������
				KeyDownSw = 1;						// KeyDown�����X�C�b�`��
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g�I�� -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h�𖳌�
	}
	// �{�[���̉^������ -----------------------------------------------------------------
	public void BallProcess( ) {
		// setXORMode(Color.white)�̐ݒ�ɂ���āC�㏑���ŏ���
		WorkGraphics.drawImage(BallImage, BallPreX, BallPreY, this);	// �{�[������

		// �O��̈ʒu�Ɉړ��ʂ������ă{�[���̈ʒu���Đݒ�
		BallX = BallPreX + BallXStep;				// �{�[���̈ʒu�Đݒ�
		BallY = BallPreY + BallYStep;

		int HitSw = 0;								// �Փ˃X�C�b�`���O�ɉ��ݒ�
		// �{�[�������E�̘g����O�ɏo�Ă��Ȃ����`�F�b�N  
		if (BallX < 1 || BallX > AppletWidth - BallSize) {
			BallXStep = -BallXStep;					// X�����̈ړ��ʂ𔽓]
			HitSw = 1;								// �Փ˃X�C�b�`��ON�ɐݒ�
		}
		// �{�[������̘g����O�ɏo�Ă��Ȃ����`�F�b�N
		if (BallY < 1) {
			BallYStep = -BallYStep;					// Y�����̈ړ��ʂ𔽓]
			HitSw = 1;								// �Փ˃X�C�b�`��ON�ɐݒ�
		}
		// ���̃`�F�b�N
		if (BallY > AppletHeight - BallSize) {
			BallSet(1);								// �V�����{�[�����Z�b�g
			return;
		}

		int RacketHit = 0;
		// �{�[�������i�s�����ꍇ�̃u���b�N�`�F�b�N
		for (int i = 0; i < 8; i++) {				// �{�[���̎��͂W�_�𒲂ׂ�
			int xw = BallX + BallCheckX[i];			// �{�[���̎��͂W�_�̈ʒu
			int yw = BallY + BallCheckY[i];

			// �u���b�N�͈͓̔�
			if (xw >= BlockX && xw < BlockX + BlockWidth * BlockColumn
			 && yw >= BlockY && yw < BlockY + BlockHeight * BlockRow) {

				// xw,yw�ɊY������u���b�N�̔z��ԍ�(xp,yp)
				int xp = (xw - BlockX) / BlockWidth;
				int yp = (yw - BlockY) / BlockHeight;
				if (Block[yp][xp] == 1) {				// �u���b�N������ꍇ
					Block[yp][xp] = 0;					// �z����Ńu���b�N������
					// setXORMode(Color.white)�̐ݒ�ɂ���āC�㏑���ŏ���
					WorkGraphics.drawImage(BlockImage,	// �O���t�B�b�N��Ńu���b�N������
						BlockX + BlockWidth * xp,
						BlockY + BlockHeight * yp, this);

					BallXStep = BallXStep * HantenX[i];	// �{�[���̈ړ��ϗʂ��C��
					BallYStep = BallYStep * HantenY[i];
					HitSw = 1;							// �Փ˃X�C�b�`�I��
					Score++;							// �X�R�A�J�E���g
					showStatus("Score = " + Score);		// �X�R�A�\��

					// �u���b�N�����ׂĂԂ����ꍇ
					if (Score > 0 && Score % (BlockRow * BlockColumn) == 0) {
						WaitTime = 1000;				// �X���b�h�̑҂����Ԑݒ�
						ClearCount++;					// �N���A��
						GameScreenDisplay( );			// �V�����Q�[����ʕ`��
						BallSet(0);						// ���݂̃{�[���ʒu�ݒ�
						return;
					}
				}
			}

			// �{�[���̎��͂W�_�����P�b�g�����̃`�F�b�N
			// �Փ˂����ꍇ�C�Փ˔͈͂���E�o����܂ł̏Փ˂͖�������
			if (RacketX <= xw && xw <= RacketX+RacketWidth-1  
 			 && RacketY <= yw && yw <= RacketY + RacketHeight-1) {
				RacketHit = 1;							// ���P�b�g�q�b�g���I��
				if (PreRacketHit == 0) {				// 1�O���Փ˂��Ă��Ȃ�
					HitSw = 1;							// �Փ˃X�C�b�`�I��
					BallXStep = BallXStep * HantenX[i];	// �Փˌ�̈ړ��l�ݒ�
					BallYStep = BallYStep * HantenY[i];
					break;
				}
			}
		}
		PreRacketHit = RacketHit;					// ���P�b�g�Փ˂�ۊ�

		if (HitSw == 1) { 							// �Փ˂����ꍇ
			HitSound.play( );						// �Ԃ�������
			BallX = BallPreX;  BallY = BallPreY;	// �ՓˑO�̈ʒu�ɖ߂�
		}

		WorkGraphics.drawImage(BallImage, BallX, BallY, this);	// �{�[���\��
		BallPreX = BallX;		BallPreY = BallY;	// ����̈ʒu�ۊ�
	}
	// KeyListener�C���^�t�F�[�X�̊e���\�b�h��` ----------------------------------------
	public void keyTyped(KeyEvent evt) { }
	public void keyReleased(KeyEvent evt) {  }
	public void keyPressed(KeyEvent evt) {
        evt.consume( );								// �C�x���g������
		if (KeyDownSw == 0)							// KeyDown�����s��
			return;

		// setXORMode(Color.white)�̐ݒ�ɂ���āC�㏑���ŏ���
		WorkGraphics.drawImage(RacketImage, RacketX, RacketY, this);	// ���P�b�g����

		int RacketXw = RacketX;						// ���P�b�g�̈ʒu�ۊ�
		int RacketYw = RacketY;
		switch (evt.getKeyCode( )) {
			case KeyEvent.VK_UP	:								// ��̃L�[����
				if (RacketY > BlockY + BlockHeight * BlockRow)	// ��ړ����
					RacketY -= RaketStep;						// ���P�b�g����Ɉړ�
				break;
			case KeyEvent.VK_DOWN:								// ���̃L�[����
				if (RacketY < AppletHeight - RacketHeight * 2)	// ���ړ�����
					RacketY += RaketStep;						// ���P�b�g�����Ɉړ�
					break;
			case KeyEvent.VK_LEFT:								// ���̃L�[����
				if (RacketX > 0)								// ���ړ����E
					RacketX -= RaketStep;						// ���P�b�g���Ɉړ�
			 	break;
			case KeyEvent.VK_RIGHT:								// �E�̃L�[����
				if (RacketX < AppletWidth - RacketWidth)	  	// �E�ړ����E
					RacketX += RaketStep;						// ���P�b�g�E�Ɉړ�
				break;
		}

		// ���P�b�g���ړ�������Ƀ{�[�������邩����
		int flag = 0;								// �`�F�b�N�t���O
		// �{�[���̎��͂W�_�Ƀ��P�b�g�����邩�`�F�b�N
		for (int i = 0; i < 8 && flag == 0; i++) {	// �t���O��0�̏ꍇ�C8�_���ׂ�
			int xw = BallX + BallCheckX[i];			// �{�[���̎���8�_�̊e�ʒu
			int yw = BallY + BallCheckY[i];
			// ���P�b�g�͈͓̔��Ƀ{�[�������邩�`�F�b�N
			if (RacketX <= xw && xw <= RacketX + RacketWidth - 1
				&& RacketY <= yw && yw <= RacketY + RacketHeight - 1)
				flag = 1;							// ����ꍇ�t���O���I��
		}

		if (flag == 1) {	// ���P�b�g���ړ������Ƃ���Ƀ{�[��������ꍇ
			HitSound.play( );						// �q�b�g�T�E���h
			RacketX = RacketXw;  RacketY = RacketYw;// ���̈ʒu�ɖ߂�
		}

		WorkGraphics.drawImage(RacketImage, RacketX, RacketY, this);	// ���P�b�g�\��

		repaint( );
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );
		if (button == StartButton) {				// �X�^�[�g�{�^���̏ꍇ
			StartButton.setVisible(false);			// �X�^�[�g�{�^�����B��
			GameStartSw = true;						// �Q�[���X�^�[�g
			requestFocus( );						// ���̓t�H�[�J�X��v��
		}
	}
}
