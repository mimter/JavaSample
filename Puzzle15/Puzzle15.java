import java.applet.*;								// Applet, AudioClip
import java.awt.*;									// Graphics, Image, Color, MediaTracker
import java.awt.event.*;							// MouseListener, MouseEvent

public class Puzzle15 extends Applet implements MouseListener, Runnable {
	Image panel[ ] = new Image[16];					// �p�l���摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int Table[ ][ ] = new int[4][4];				// �摜�ԍ��i�[�e�[�u��
	AudioClip ClickSound, SeikaiSound;				// �N���b�N���C������
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	Thread thread = null;							// �X���b�h
	int ClickTate, ClickYoko;						// �N���b�N�p�l���ʒu
	int SpaceTate, SpaceYoko;						// �X�y�[�X�p�l���ʒu
	int MovePoint;									// ����|�C���g
	boolean MoveFlag;								// ����t���O

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
        ClickSound = getAudioClip(getCodeBase( ), "sound/click.au");	// �N���b�N��
        SeikaiSound = getAudioClip(getCodeBase( ), "sound/seikai.au");	// ������

		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J����
		for (int i = 0; i < 16; i++) {
			panel[i] = getImage(getCodeBase( ), "image/panel"+i+".gif");
			mediatracker.addImage(panel[i], 0);		// �摜�����f�B�A�g���b�J�ɃZ�b�g
		}
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		}
		catch (InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}

		Shake( );									// �p�l���������_���ɍ�����
		DispPanel( );								// �p�l���\��
		MoveFlag = false;							// ����t���O
		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
	}
	// �V�F�C�N���� ---------------------------------------------------------------------
	public void Shake( ) {							// �p�l���̔z�u�������_���ɂ���
		int count = 1;
		for (int tate = 0; tate < 4; tate++)		// �p�l���̔ԍ������ɃZ�b�g
			for (int yoko = 0; yoko < 4; yoko++)
				Table[tate][yoko] = count++;
		Table[3][3] = 0;							// �E���̃p�l���͔��F

		// ��������t�ɕ����Ă������@
		// �󂢂Ă��鏊����l���������_���ɑI��Ō���
		int tate1 = 3, yoko1 = 3;
		int tate2 = 0, yoko2 = 0;
		for (int c = 1; c <= 100; c++) {			// 100������i���ۂ�100�ȉ��j
			int w = (int)(Math.random( ) * 4);		// 0�`3�̗�������
			switch (w) {
				case 0: // ��ƌ���
					tate2 = tate1 - 1;  yoko2 = yoko1; break;
				case 1: // ���ƌ���
					tate2 = tate1 + 1;  yoko2 = yoko1; break;
				case 2: // �E�ƌ���
					tate2 = tate1;      yoko2 = yoko1 + 1; break;
				case 3: // ���ƌ���
					tate2 = tate1;      yoko2 = yoko1 - 1; break;
			}
			if (tate2 >= 0 && tate2 <= 3 && yoko2 >= 0 && yoko2 <= 3) { // �͈͓�
				Change(tate1, yoko1,  tate2, yoko2);
				tate1 = tate2;
				yoko1 = yoko2;
			}
		}
	}
	// �p�l���`�� -----------------------------------------------------------------------
	void DispPanel( ) {
		// �S�p�l����`��
		for (int tate = 0; tate < 4; tate++)
			for (int yoko = 0; yoko < 4; yoko++)
					WorkGraphics.drawImage(panel[Table[tate][yoko]],
											yoko*50, tate*50, this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (MoveFlag == true) {						// ����t���O���I���@���쒆
			MovePoint += 5;							// �p�l���ړ�
			if (MovePoint > 50) {					// �p�l���T�C�Y���ړ������ꍇ
				MoveFlag = false;					// ������~�@�t���O�I�t
				Change(ClickTate, ClickYoko, SpaceTate, SpaceYoko);

				if (Check( ) == true) {				// �`�F�b�N���Ă��ׂĐ����̏ꍇ
					SeikaiSound.play( );			// ������
					WorkGraphics.setColor(Color.red);	// �ԐF�łn�j�ƕ`��
					WorkGraphics.drawString("OK", 170, 180);
				}
			}
			else {									// �ړ��͈͓̔�
				// �ړ����Ă��镔���̂ݕ`��
				// �p�l�����ړ�����Q�ӏ��ɃX�y�[�X�`��
				WorkGraphics.drawImage(panel[Table[SpaceTate][SpaceYoko]],
										ClickYoko*50, ClickTate*50, this);
				WorkGraphics.drawImage(panel[Table[SpaceTate][SpaceYoko]],
										SpaceYoko*50, SpaceTate*50, this);
				// �N���b�N�����ړ��p�l���`��
				WorkGraphics.drawImage(panel[Table[ClickTate][ClickYoko]],
					ClickYoko * 50 + (SpaceYoko - ClickYoko) * MovePoint,
					ClickTate * 50 + (SpaceTate - ClickTate) * MovePoint, this);
			}
		}
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			try {
				thread.sleep(50);					// �X���b�h���X���[�v
			} catch (InterruptedException e){		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" " + e);				// ��O�G���[�\��
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g�I�� -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
	}

	// �p�l���������� -------------------------------------------------------------------
	public void Change(int tate1, int yoko1, int tate2, int yoko2) {	// ��������
		int w = Table[tate1][yoko1];				// �󔒈ʒu�ƃN���b�N�����p�l��������
		Table[tate1][yoko1] = Table[tate2][yoko2];
		Table[tate2][yoko2] = w;
	}
	// �����`�F�b�N ---------------------------------------------------------------------
	public boolean Check( ) {						// �S�p�l���ʒu�������Ă��邩�`�F�b�N
		int count = 1;								// �����`�F�b�N�p�J�E���g 
		boolean flag = true;						// �����t���O��true�ɉ��ݒ�
		for (int tate = 0; flag == true && tate < 4; tate++) {
			for (int yoko = 0; flag == true && yoko < 4; yoko++) {
				if (tate == 3 && yoko == 3)
					break;
				if (Table[tate][yoko] != count)		// ������ꍇ�C�t���O��false�ɂ���
					flag = false;
				count++;
			}
		}
		return flag;
	}
	// MouseListener�C���^�[�t�F�[�X������-----------------------------------------------
    public void mousePressed(MouseEvent evt) {
		if (MoveFlag == true)						// �����Ă���r���̏ꍇ
			return;

        evt.consume( );								// �C�x���g������

		// �N���b�N���ꂽ2�����z���̃p�l���ʒu
		int yoko = evt.getX( ) / 50;				// �O���t�B�b�N���W�ʒu��
		int tate = evt.getY( ) / 50;				// 2�����z��̈ʒu�ɕϊ�

		// �㉺���E�ɋ󂫂����邩�`�F�b�N
		// ����΁C�N���b�N�����p�l�������̕����Ɉړ�
		ClickTate = tate; ClickYoko = yoko;			// �N���b�N�����p�l���̈ʒu
		MovePoint = 0;								// ���[�u�|�C���g���N���A
		if (tate - 1 >= 0 && Table[tate - 1][yoko] == 0)				// ��`�F�b�N
			{ MoveFlag = true; SpaceTate = tate - 1; SpaceYoko = yoko; }
		else if (tate + 1 <= 3 && Table[tate + 1][yoko] == 0)			// ���`�F�b�N
			{ MoveFlag = true; SpaceTate = tate + 1; SpaceYoko = yoko; }
		else if (yoko - 1 >= 0 && Table[tate][yoko - 1] == 0)			// ���`�F�b�N
			{ MoveFlag = true; SpaceTate = tate; SpaceYoko = yoko - 1; }
		else if (yoko + 1 <= 3 && Table[tate][yoko + 1] == 0)			// �E�`�F�b�N
			{ MoveFlag = true; SpaceTate = tate; SpaceYoko = yoko + 1; }
	
		ClickSound.play( );							// �N���b�N��
	}
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
} 
