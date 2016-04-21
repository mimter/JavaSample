import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, MediaTracker
import java.awt.image.*;							// MemoryImageSource, PixelGrabber

public class Kaleidoscope extends Applet implements Runnable {

	Image WorkImage1, WorkImage2;					// ��Ɨp�C���[�W
	Graphics WorkGraphics1, WorkGraphics2;			// ��Ɨp�O���t�B�b�N�X
	Image MirrorImage0, MirrorImage1, MirrorImage2, MirrorImage3;
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int ImageWidth, ImageHeight;					// �摜�̕��ƍ���
	Thread thread = null;							// �X���b�h
	int[ ]  Pixels1  =  new  int[100  *  100];		// �s�N�Z���f�[�^�ۊ�(100 x 100)
	int[ ]  Pixels2  =  new  int[100  *  100];
	int[ ]  Pixels3  =  new  int[100  *  100];

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̃T�C�Y�擾
		AppletHeight = getSize( ).height;
		// ��1�i�K�p��ƃC���[�W�̈�ݒ�
		WorkImage1 = createImage(AppletWidth, AppletHeight);// ��Ɨp�C���[�W�쐬
		WorkGraphics1 = WorkImage1.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
		// ��2�i�K�p��ƃC���[�W�̈�ݒ�
		WorkImage2 = createImage(AppletWidth, AppletHeight);// ��Ɨp�C���[�W�쐬
		WorkGraphics2 = WorkImage2.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		MediaTracker mediatracker = new MediaTracker(this);		// ���f�B�A�g���b�J����
		String imagefile = getParameter("image");
		MirrorImage0 = getImage(getCodeBase( ), imagefile);		// �摜�f�[�^����
		mediatracker.addImage(MirrorImage0, 0);		// �摜�����f�B�A�g���b�J�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch (InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}

		ImageWidth = MirrorImage0.getWidth(this);	// �C���[�W�̕��ƍ���
		ImageHeight = MirrorImage0.getHeight(this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage2, 0, 0, this);		//��2�i�K�̍�ƃC���[�W��`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		int dispX = 0, dispY = 0;					// �\���ʒu
		int moveX = -1, moveY = -1;					// �ړ��X�e�b�v

		while(thread != null) {						// �X���b�h�����݂��Ă����
			try {
				thread.sleep(100);					// �X���b�h�X���[�v
			} catch (InterruptedException e){		// ���荞�ݗ�O����
				showStatus(" "+e);
			}

			MirrorProcess(dispX, dispY);			// �~���[����

			dispX += moveX;							// x�������̉摜�̕\���ʒu�ړ�
			if (dispX <= -(ImageWidth - 100) || dispX >= 0)		// �\���ʒu���͈͊O�̏ꍇ
				moveX *= (-1);						// x�����̈ړ��X�e�b�v�𔽓]
			dispY += moveY;							// y�������̉摜�̕\���ʒu�ړ�
			if (dispY == -(ImageHeight - 100) || dispY == 0)	// �\���ʒu���͈͊O�̏ꍇ
				moveY *= (-1);						// y�����̈ړ��X�e�b�v�𔽓]

			repaint( );
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �~���[���� -----------------------------------------------------------------------
	public void MirrorProcess(int dispX, int dispY) {
		// ������1�i�K�̍�ƃO���t�B�b�N�X�ɕ`��
		WorkGraphics1.drawImage(MirrorImage0, dispX, dispY, this);

		// ���悩��s�N�Z��������荞��
		GetPixels(MirrorImage0, -dispX, -dispY, 100, 100, Pixels1);
		// ���E�Ώ̂ɂȂ�悤�Ƀf�[�^�����ւ���
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100 / 2; j++) {
				int temp = Pixels1[i*100 + j];
				Pixels1[i*100 + j] = Pixels1[i*100 + 100 - 1 - j];
				Pixels1[i*100 + 100 - 1 - j] = temp;
			}
		}
		// �ҏW���������z��̃s�N�Z����񂩂�C���[�W���쐬
		MirrorImage1 = createImage(new MemoryImageSource(100, 100, Pixels1, 0, 100));
		// �쐬�����C���[�W���1�i�K�̍�ƃO���t�B�b�N�X�̉E��ɕ`��
		WorkGraphics1.drawImage(MirrorImage1, 100, 0, this);

		// �쐬�����E��̉摜����s�N�Z��������荞��
		GetPixels(MirrorImage1, 0, 0, 100, 100, Pixels2);
		// �㉺�Ώ̂ɂȂ�悤�Ƀf�[�^�����ւ���
		for (int i = 0; i < 100 / 2; i++) {
			for (int j = 0; j < 100; j++) {
				int temp = Pixels2[i*100 + j];
				Pixels2[i*100 + j] = Pixels2[(100 - 1 - i)*100 + j];
				Pixels2[(100 - 1 - i)*100 + j] = temp;
			}
		}
		// �ҏW���������z��̃s�N�Z����񂩂�C���[�W���쐬
		MirrorImage2 = createImage(new MemoryImageSource(100, 100, Pixels2, 0, 100));
		// �쐬�����C���[�W���1�i�K�̍�ƃO���t�B�b�N�X�̉E���ɕ`��
		WorkGraphics1.drawImage(MirrorImage2, 100, 100, this);

		// �쐬�����E���̉摜����s�N�Z��������荞��
		GetPixels(MirrorImage2, 0, 0, 100, 100, Pixels3);
		// ���E�Ώ̂ɂȂ�悤�Ƀf�[�^�����ւ���
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100 / 2; j++) {
				int temp = Pixels3[i*100 + j];
				Pixels3[i*100 + j] = Pixels3[i*100 + 100 - 1 - j];
				Pixels3[i*100 + 100 - 1 - j] = temp;
			}
		}
		// �ҏW���������z��̃s�N�Z����񂩂�C���[�W���쐬
		MirrorImage3 = createImage(new MemoryImageSource(100, 100, Pixels3, 0, 100));
		// �쐬�����C���[�W���1�i�K�̍�ƃO���t�B�b�N�X�̍����ɕ`��
		WorkGraphics1.drawImage(MirrorImage3, 0, 100, this);

		// ��1�i�K�̍�ƃC���[�W���2�i�K�̍�ƃO���t�B�b�N�X�ɕ`��
		WorkGraphics2.drawImage(WorkImage1, 0, 0, this);
	}
	// �摜�C���[�W�̊e�s�N�Z�������擾 -----------------------------------------------
	public void GetPixels(Image img, int x, int y, int w, int h, int pixels[ ]) {
		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
						// (�摜�C���[�W�C�J�n�ʒu���C���C��, �����C�z��C�i�[�ʒu�C����)
        try  {
			pg.grabPixels( );						// �s�N�Z�����捞��
        }  catch  (InterruptedException  e)  {		// grabPixels( )�ɑ΂����O����
            showStatus(" "+e);
        }
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
	}
}
