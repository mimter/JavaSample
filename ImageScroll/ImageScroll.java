import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, MediaTracker

public class ImageScroll extends Applet implements Runnable {
	Thread thread = null;							// �X���b�h
	int DispX;										// �����\���ʒu
	int DispN;										// ���[�ɕ\������摜�ԍ�
	int Number;										// �摜��
	int	Size;										// �摜���T�C�Y
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Image image[ ] = new Image[30];					// �摜�f�[�^��max30
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g�g���b�J����

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �A�v���b�g�T�C�Y
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
		// �p�����[�^����
		Number = Integer.parseInt(getParameter("number"));	// �摜��
		// �f�[�^����
		for (int i = 0; i < Number; i++) {			// �摜�ǂݍ���
			image[i] = getImage(getCodeBase( ), getParameter("image" + i));
			mediatracker.addImage(image[i], 0);		// ���f�B�A�g���b�J�ɃZ�b�g
		}
		// �����ݒ�
		DispX = 0;									// �\���X�^�[�g�|�C���g
		DispN = 0;									// �\���摜�ԍ�
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		// ���f�B�A�g���b�J���������Ă��Ȃ��ꍇ�i�摜���͂��������Ă��Ȃ��ꍇ�j
	    if (mediatracker.statusID(0, true) != MediaTracker.COMPLETE) {
			g.drawString("Now loading ... ", 20, 20);
			return;
		}

		Size = image[0].getWidth(this);				// �摜���T�C�Y
		DispX -= 2;									// �摜������2�|�C���g�ړ�
		// ���[�̕\���摜���A�v���b�g�̉�ʂ��獶�ɏo���ꍇ
		if (DispX <= -Size) {
			DispX = 0;								// �����\���ʒu���O
			DispN++;								// �\������摜�����̉摜�ɂ���
			if (DispN >= Number)					// �ŏI�摜�𒴂����ꍇ
				DispN = 0;							// �ŏ��̉摜�ɂ���
		}

		// �\���g���ɓ���摜�݂̂�\��
		for (int i = 0; i < Number && DispX + i * Size < AppletWidth; i++)
			WorkGraphics.drawImage(
				image[(DispN + i) % Number], DispX + i * Size + 1, 0, this);
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while(thread != null) {						// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			try {
				thread.sleep(100);					// �X���b�h��100�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
			}
		}
		thread = null;
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
	}
}
