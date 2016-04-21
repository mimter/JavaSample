import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, MediaTracker

public class Snow extends Applet implements Runnable {
	Thread thread;									// �X���b�h
	Image SnowImage;								// ��C���[�W
	Image BackImage;								// �w�i�C���[�W
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int SnowWidth, SnowHeight;						// ��̕��ƍ���
	int MAX = 500;									// �ݒ�\�Ȑ�̍ő吔
	boolean SnowFlag[ ] = new boolean[MAX];			// ��̃t���O
	int SnowMax = 100;								// ��̍ő�f�t�H���g��(Max500)
	int SnowX[ ] = new int[MAX];					// ��̈ʒu
	int SnowY[ ] = new int[MAX];
	int SnowSpeed[ ] = new int[MAX];				// ��̗�����X�s�[�h
	int SnowYure[ ] = new int[MAX];					// ��̉��h��
	int Speed, Yure;								// ��̃X�s�[�h�Ɖ��h��
	int SnowAppear = 2000;							// ��̔����m�� SnowAppear����1�`1
	int SleepTime = 50;								// �X���b�h�̃X���[�v�^�C��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		// �p�����[�^
		SnowMax = Integer.parseInt(getParameter("max"));	// ��̍ő吔
		if (SnowMax > MAX)									// �z��̍ő�܂�
			SnowMax = MAX;
		Speed = Integer.parseInt(getParameter("speed"));	// ��̃X�s�[�h
		Yure = Integer.parseInt(getParameter("yure"));		// ��̉��h��

		SnowImage = getImage(getCodeBase( ), getParameter("snowimage"));// ��̃C���[�W
		BackImage = getImage(getCodeBase( ), getParameter("backimage"));// �w�i�C���[�W

		// ���͉摜�����f�B�A�g���b�J�ɃZ�b�g
		MediaTracker mediatracker = new MediaTracker(this);		// ���f�B�A�g���b�J
		mediatracker.addImage(BackImage, 0);
		mediatracker.addImage(SnowImage, 0);
		try {
			mediatracker.waitForID(0);				// �摜�̓��͂���������܂ő҂�
		}
		catch (InterruptedException e) {
			showStatus(" "+e);
		}

		// �摜�̕��ƍ���
		SnowWidth = SnowImage.getWidth(this);		// ��̕�
		SnowHeight = SnowImage.getHeight(this);		// ��̍���

		for (int i = 0; i < SnowMax; i++)			// ��̏�����
			SnowFlag[i] = false;
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			//��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			Making( );								// ��̍~��V�[�����쐬
			repaint( );
			try {
				thread.sleep(SleepTime);			// �X���b�h���X���[�v
			} catch (InterruptedException e) {
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;
	}
	// �V�[���쐬 -----------------------------------------------------------------------
	void Making( ) {
		if (BackImage == null) {					// �w�i�摜���Ȃ��ꍇ�͔w�i�����ŕ`��
			WorkGraphics.setColor(Color.black);
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		}
		else
			WorkGraphics.drawImage(BackImage, 0, 0, this);	// �w�i�摜�`��

		for (int p = 0; p < SnowMax; p++) {			// ��̐��������[�v
			if (SnowFlag[p] == true) {				// ��̔������I���̏ꍇ
				// �X�s�[�h�Ɖ��h����l�����Ĉʒu���v�Z
				int x = (int)(SnowX[p] + 
						Math.sin((SnowY[p]+SnowSpeed[p])*3.14/180) * SnowYure[p]);
				int y = SnowY[p];
				SnowY[p] += SnowSpeed[p];			// ������ɗ��Ƃ�
				if (SnowY[p] > AppletHeight)		// �A�v���b�g��ʂ�艺�ɗ������ꍇ
					SnowFlag[p] = false;			// �������

				WorkGraphics.drawImage(SnowImage, x, y, this);	// ���`��
			}
			else
				SnowMake(p);						// �V������̈ʒu���̌v�Z������
		}
	}
	// ��쐬 ---------------------------------------------------------------------------
	void SnowMake(int p) {
		if ((int)(Math.random( ) * SnowAppear) == 0) {
			// ���SnowAppear���̂P�̊m���Ń����_���ɔ���
			SnowX[p] = AppletWidth * p / SnowMax;				// ���������ʒu
			SnowY[p] = -(int)(Math.random( ) * 100);			// �����ʒu��������
			SnowSpeed[p] = Speed + (int)(Math.random( ) * 5);	// ������X�s�[�h
			SnowYure[p] = (int)(Math.random( ) * Yure);			// ��̉��h��
			SnowFlag[p] = true;									// ��̔����t���O�I��
			if (SnowAppear > 1)						// �����m���v�Z�p�̒l���P����̏ꍇ
				SnowAppear--;						// ��̔����m������������
		}
	}
}
