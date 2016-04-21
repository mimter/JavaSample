import java.applet.*;			// Applet, AudioClip
import java.awt.*;				// Graphics, Image, Color, Font, Button etc
import java.awt.event.*;		// ActionListener, ActionEvent, KeyListener, KeyEvent

public class DropBlock extends Applet
					implements Runnable, KeyListener, ActionListener {
    Thread thread;									// �X���b�h
	int Block[ ][ ][ ] =							// �u���b�N�̌`��f�[�^
		{
			{	{0, 0, 0},
				{0, 1, 0},
				{1, 1, 1}
			},
			{	{0, 1, 0},
				{0, 1, 0},
				{0, 1, 0}
			},		
			{	{0, 0, 0},
				{0, 0, 1},
				{1, 1, 1}
			},		
			{	{0, 0, 0},
				{1, 0, 0},
				{1, 1, 1}
			}
		};

	int VPoint,										// ��������(vertical)�̈ʒu
		HPoint,										// ��������(horizontal)�̈ʒu
		Number;										// �u���b�N�ԍ�
	boolean EventFlag;								// �C�x���g�����t���O
	int StageWidth = 20;							// �X�e�[�W�̕�(Max:20)
	int StageHeight = 20;							// �X�e�[�W�̍���(Max:20)
	int Stage[ ][ ]
		= new int[StageHeight][StageWidth];			// �X�e�[�W�̓��e
	Image PanelImage[ ] = new Image[2];				// �p�l���C���[�W
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int OK = 1;										// OK�l
	int Count;										// �_��
	int Size;										// �p�l���T�C�Y
	AudioClip MoveSound, RotateSound, DropSound;	// �T�E���h�i�ړ����C��]���C�������j

	Button StartButton;								// �X�^�[�g�{�^��
	boolean GameFlag = false;						// true:�Q�[���J�n	false:�Q�[���I��

	Font font = new Font("System", Font.PLAIN, 20);
	FontMetrics fontmetttrics = getFontMetrics(font);

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �p�����[�^�擾
		StageWidth = Integer.parseInt(getParameter("StageWidth"));		// �X�e�[�W�̉���
		StageHeight = Integer.parseInt(getParameter("StageHeight"));	// �X�e�[�W�̏c�s

        MoveSound = getAudioClip(getCodeBase( ), "sound/click.au");		// ���E�ړ���
        RotateSound = getAudioClip(getCodeBase( ), "sound/kaiten.au");	// ��]��
        DropSound = getAudioClip(getCodeBase( ), "sound/drop.au");		// ������

		// �摜���͂��Ď����郁�f�B�A�g���b�J�[����
		MediaTracker mediatracker = new MediaTracker(this);
		for (int i = 0; i < 2; i++) {				// �u���b�N�Ɣw�i�摜����
			PanelImage[i] = getImage(getCodeBase( ), "image/panel"+i+".gif");
			mediatracker.addImage(PanelImage[i], 0);// ���f�B�A�g���b�J�ɃZ�b�g
		}

		try {
			mediatracker.waitForID(0);				// ���f�B�A�g���b�J�̓��͊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}

		Size = PanelImage[0].getWidth(this);		// Panel�T�C�Y
		// ��ƃC���[�W�̈�m��
		WorkImage = createImage(StageWidth * Size, (StageHeight + 1) * Size);
		WorkGraphics = WorkImage.getGraphics( );
		WorkGraphics.setFont(font);

		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�ǉ�
		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton.setBounds((StageWidth * Size - 60) / 2,
							  (StageHeight * Size - 30) / 2,
							  60, 30);				// �{�^���̈ʒu�ƃT�C�Y�ݒ�

		addKeyListener(this);						// �L�[���X�i�[�ǉ�
		StageDisp( );								// �X�e�[�W�`��
	}
	// �Q�[�������� ---------------------------------------------------------------------
	private void GameInitial( ) {
		Count = 0;									// �_���J�E���g	
		EventFlag = false;							// �L�[�C�x���g�t���O
        VPoint = -2;								// �J�n�|�C���g����ʂ̏�ɂ���
		HPoint = StageWidth / 2 - 1;				// �J�n�|�C���g�𒆉��ɂ���
		Number = (int)(Math.random( )*4);			// �����_���Ƀu���b�N�ԍ�(0�`3)����
		GameFlag = true;							// �Q�[���X�^�[�g�t���O���I��
		requestFocus( );							// ���̓t�H�[�J�X��v��
		StageClear( );								// �X�e�[�W���N���A
		StageDisp( );								// �X�e�[�W��`��
	}
	// �X�e�[�W�N���A -------------------------------------------------------------------
	private void StageClear( ) {
		for (int i = 0; i < StageHeight; i++)		// �X�e�[�W�c����
			for (int j = 0; j < StageWidth; j++)	// ������
				Stage[i][j] = 0;					// �z��X�e�[�W���O�N���A
	}
	// �X�e�[�W�`�� ---------------------------------------------------------------------
	public void StageDisp( ) {
		for (int i = 0; i < StageHeight; i++)		// �X�e�[�W�c����
			for (int j = 0; j < StageWidth; j++)	// ������
				WorkGraphics.drawImage(PanelImage[Stage[i][j]],j*Size, i*Size, this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
    public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
    }
	// �u���b�N�\�� ---------------------------------------------------------------------
    public void BlockDisp(int sw) { 				// 0:�w�i  1:�u���b�N
		for (int i = 0; i < 3; i++)					// �c����
			for (int j = 0; j < 3; j++)				// ������
				if (Block[Number][i][j] == 1)		// �u���b�N�̏ꍇ && VPoint + i >= 0)
					WorkGraphics.drawImage(PanelImage[sw],
						(HPoint +j)*Size, (VPoint + i)*Size, this);
    }
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g) {
		// ���_�\��
		WorkGraphics.setColor(new Color(131, 147, 202));
		WorkGraphics.fillRect(0, StageHeight * Size, StageWidth * Size, Size);
		WorkGraphics.setColor(new Color(255, 255, 255));
		String information = Count + " Point";
		int MessageLength = fontmetttrics.stringWidth(information);
		WorkGraphics.drawString(information,		// ���_�\���̉E�[�����낦��
			(StageWidth * Size - MessageLength) / 2, (StageHeight + 1) * Size - 5);

		// �X�e�[�W�\��
		g.drawImage(WorkImage, 0, 0, this);
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ) {
		while(thread != null) {						// �X���b�h�����݂��Ă����
			syori( );								// �u���b�N��������
			repaint( );
			try {
				if (Count > 400)					// 400�_�ȏ�̓Q�[���X�s�[�h�����
					thread.sleep(100);
				else
					thread.sleep(500 - Count);		// �Q�[���X�s�[�h�����X�ɑ�������
	        } catch (InterruptedException e) {		// sleep�ɑ΂��Ă̊��荞�ݗ�O����
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
	// �u���b�N���� ---------------------------------------------------------------------
	private void syori( ) {
		if (GameFlag == true) {						// �Q�[�����̏ꍇ
			if (EventFlag == false) {				// �L�[��������Ă��Ȃ��ꍇ
				//  ����ɗ��������ꍇ�̃`�F�b�N
				if (BlockCheck(HPoint, VPoint + 1) == OK) {  // �Ԃ���Ȃ��ꍇ
					BlockDisp(0);					// �O��ʒu�N���A
					VPoint++;						// ����ɗ���
					BlockDisp(1);					// �V�����ʒu�Ńu���b�N�\��
					repaint( );
				}
				else {								// �Ԃ���ꍇ

					boolean HitFlagInStage = true;	// �X�e�[�W���łԂ������Ɖ���
					// �u���b�N�����݂̃X�e�[�W�̈ʒu�ɔz�u
					for (int i = 0; i < 3; i++)	{
						for (int j = 0; j < 3; j++) {
							if (Block[Number][i][j] == 1) {
								if(VPoint + i >= 0)	// ��������������̏ꍇ
									Stage[VPoint+i][HPoint+j] = 1;
								else {				// �X�e�[�W�O�̏ꍇ
									GameFlag = false;				// �Q�[���I�[�o�[
									StartButton.setVisible(true);	// �X�^�[�g�{�^���\��
									HitFlagInStage = false;// �X�e�[�W���̏Փ˃t���O�N���A
									break;
								}
							}
						}
					}

					if (HitFlagInStage == true) {		// �X�e�[�W���łԂ������ꍇ
						LineCheck( );					// ���C���`�F�b�N
						// �V�����u���b�N�쐬
						VPoint = -3;					// ���������̈ʒu�̓X�e�[�W�̊O
						HPoint = StageWidth / 2 - 1;	// ���������̈ʒu�̓X�e�[�W�̒���
						Number = (int)(Math.random( )*4);// �����_���Ƀu���b�N(0�`3)����
					}
				}
			}
        }
		EventFlag = false;							// �C�x���g�t���O���N���A
		// EventFlag��false�ł���΁C�L�[���͂��Ȃ������Ƃ������Ƃ�\��
	}
	// ���C���`�F�b�N -------------------------------------------------------------------
	public void LineCheck( ) {
		// ���݂̃u���b�N�̃x�[�X�|�C���g�{�Q����i�������CStageHeight�|�P�ȉ��j
		// ������Ƀ��C���𒲂ׂău���b�N�����邩�`�F�b�N
		int sp = VPoint + 2;
		if (sp > StageHeight - 1)
			sp = StageHeight - 1;

		for (int i = sp; i > 0; i--) {
			// 1���C���`�F�b�N
			int cnt = 0;
			for (int j = 0; j < StageWidth; j++)
				cnt += Stage[i][j];

			if (cnt == 0)							// ���ׂăX�y�[�X�̏ꍇ
				break;

			if (cnt == StageWidth) {
				Count += cnt;						// ���_�J�E���g
				int flag = 1;

				// 1���C�����ׂău���b�N�̏ꍇ
				// ���̍s�̏�̃��C�������ɗ��Ƃ�
				// ���ׂăX�y�[�X�̃��C���𗎂Ƃ����ꍇ�́C���Ƃ��������I��
				int p, sw;
				for (p = i; p > 0 && flag == 1; p--) {
					flag = 0;						// 1���C�����ׂăX�y�[�X�ł���Ɖ���
					for (int j = 0; j < StageWidth; j++) {
						Stage[p][j] = Stage[p-1][j];
						if (Stage[p][j] == 1) {		// �u���b�N���������ꍇ
							sw = 1;
							flag = 1;  				// ����ɏ���J��Ԃ�
						}
						else
							sw = 0;
						WorkGraphics.drawImage(PanelImage[sw], j*Size, p*Size, this);
					}
				}

				if (p == 0) {
					// 1�i�ڂ܂Ń`�F�b�N���ăX���C�h�����ꍇ,�ŏ�i�ɃX�y�[�X������
					for (int j = 0; j < StageWidth; j++) {
						Stage[0][j] = 0;
						WorkGraphics.drawImage(PanelImage[0], j * Size, p * Size, this);
					}
				}
			i++;		// �P���C�����Ƃ����̂�,�ēx�������C�����`�F�b�N����
			}
		}
	}
	// KeyListener�C���^�t�F�[�X�̊e���\�b�h���` --------------------------------------
	public void keyTyped(KeyEvent evt) { }
	public void keyReleased(KeyEvent evt) {  }
	public void keyPressed(KeyEvent evt) {
		EventFlag = true;							// �L�[�C�x���g�t���O�I��
		switch (evt.getKeyCode( )) {
	        case KeyEvent.VK_UP    : RotateSound.play( ); kaiten( ); break; // ��L�[
		    case KeyEvent.VK_DOWN  : DropSound.play( ); Drop( );   break; 	// ���L�[
	        case KeyEvent.VK_LEFT  : MoveSound.play( ); Left( );   break; 	// ���L�[
		    case KeyEvent.VK_RIGHT : MoveSound.play( ); Right( );  break;	// �E�L�[
		}
	}
	// ��]���� -------------------------------------------------------------------------
	public void kaiten( ) {
		int work[ ][ ] = new int [3][3];
		int temp[ ][ ] = new int [3][3];

		BlockDisp(0);								// �u���b�N����
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				temp[i][j] = Block[Number][i][j];	// �ۊ�
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				work[j][3 - i - 1] = Block[Number][i][j];// ��Ɨ̈�ɉ�]�Z�b�g
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				Block[Number][i][j] = work[i][j];	// ��]�������e���Z�b�g
		if (BlockCheck(HPoint, VPoint) == OK) {		// �u���b�N���u���邩�`�F�b�N
		}
		else {										// �u���Ȃ��ꍇ
			for (int i = 0; i < 3; i++)				// ���ɖ߂�
				for (int j = 0; j < 3; j++)
					Block[Number][i][j] = temp[i][j];
		}
		BlockDisp(1);								// �u���b�N�\��
		repaint( );									// �ĕ`��
	}
	// �u���b�N���� ---------------------------------------------------------------------
	public void Drop( ) {
		while (BlockCheck(HPoint, VPoint + 1) == OK) {	// �P���Ƀu���b�N���u����ꍇ
			BlockDisp(0);							// ���݂̈ʒu�̃u���b�N������
			VPoint++;								// ���������̈ʒu��1����
			BlockDisp(1);							// �u���b�N��\��
			repaint( );								// �ĕ`��
		}
	}
	// �u���b�N�`�F�b�N -----------------------------------------------------------------
	public int BlockCheck(int hp, int vp) {
		int flag = 1;
		for (int i = 0; i < 3 && flag == 1; i++) {
			for (int j = 0; j < 3 && flag == 1; j++) {
				// �����u���b�N���X�e�[�W�̃u���b�N�ƂԂ������ꍇ
				if (vp + i >= 0 && vp + i < StageHeight &&
					hp + j >= 0 && hp + j < StageWidth) {
					if (Stage[vp + i][hp + j] == 1 && Block[Number][i][j] == 1)
						flag = 0;
				}

				// �����u���b�N���X�e�[�W�̏�O�ɏo���ꍇ
				if (Block[Number][i][j] == 1 &&
					(hp + j < 0 || hp + j > StageWidth - 1 ||
					 vp + i > StageHeight - 1) )
					flag = 0;
			}
		}
		return(flag);
	}
	// �E�ړ����� -----------------------------------------------------------------------
	public void Right( ) {
		if (BlockCheck(HPoint + 1, VPoint) == OK) {	// �E�Ɉړ��ł���ꍇ
			BlockDisp(0);							// �u���b�N����
			HPoint++;								// ���������E�Ɉړ�
			BlockDisp(1);							// �u���b�N�\��
			repaint( );								// �ĕ`��
		}
	}
	// ���ړ����� -----------------------------------------------------------------------
	public void Left( ) {
		// ���ɍs�����Ƃ��ɃX�e�[�W�̃u���b�N�ƂԂ���Ȃ����`�F�b�N
		// �u���b�N����O�ɏo�Ȃ����`�F�b�N
		if (BlockCheck(HPoint - 1, VPoint) == OK) {	// ���Ɉړ��ł���ꍇ
			BlockDisp(0);							// �u���b�N����
			HPoint--;								// �����������Ɉړ�
			BlockDisp(1);							// �u���b�N�\��
			repaint( );								// �ĕ`��
		}
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �A�N�V�����C�x���g����
		Button bt = (Button)evt.getSource( );
		if (bt == StartButton) {					// �X�^�[�g�{�^���������ꂽ�ꍇ
			StartButton.setVisible(false);			// �X�^�[�g�{�^�����B��
			GameInitial( );							// �Q�[������������
		}
	}
}
