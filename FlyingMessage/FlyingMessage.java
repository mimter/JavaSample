import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Font, FontMetrics

public class FlyingMessage extends Applet implements Runnable {
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int	AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	int MessageNumber;								// ���b�Z�[�W�̐�
	String Message[ ] = new String[10];				// ���b�Z�[�WMAX10
	String FontName;								// �t�H���g��
	Thread thread = null;							// �X���b�h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̉���
		AppletHeight = getSize( ).height;			// �A�v���b�g�̏c��

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�̈�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		// �p�����[�^����f�[�^����
		MessageNumber = Integer.parseInt(getParameter("number"));	// ���b�Z�[�W��
		for (int i = 0; i < MessageNumber; i++)				// ���b�Z�[�W�����[�v
			Message[i] = getParameter("message" + i);		// �e�L�X�g�̃p�����[�^�捞��
		FontName = getParameter("font");					// �t�H���g��
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
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
		int x = 0, y = 0;							// �\���ʒu
		double step = 0.0;							// �ړ��Ԋu
		int sw = 1;									// �ړ�����  sw  -1:���ړ� +1:�E�ړ�
		int count = 0;								// ���b�Z�[�W�\���J�E���g
		int fontsize = 1;							// �t�H���g�T�C�Y
		Font font;									// �t�H���g
		FontMetrics fontmetrics = null;				// �t�H���g���g���b�N�X

        while (thread != null) {					// �X���b�h�����݂��Ă����
			WorkGraphics.setColor(Color.black);		// �`��F�����F�ɐݒ�
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
			if (fontsize < AppletHeight -10) {
				// �t�H���g�T�C�Y���A�v���b�g�g�ɕ\���ł���T�C�Y��菬�����ꍇ
				font = new Font(FontName, Font.PLAIN, fontsize);	// �t�H���g�ݒ�
				fontmetrics = getFontMetrics(font);	// �t�H���g���g���b�N�X�擾
				WorkGraphics.setFont(font);			// �t�H���g�ݒ�
				// ���b�Z�[�W�������ɕ\�������悤�ɕ\���ʒu�ݒ�
				x = (AppletWidth - fontmetrics.stringWidth(Message[count])) / 2;
				y = AppletHeight / 2 
					+ (fontmetrics.getAscent( ) - fontmetrics.getDescent( )) / 2;
				fontsize += 2;						// �t�H���g�T�C�Y+2
			}
			else {	// sw�����������Ƀ��b�Z�[�W�������I�Ɉړ�������
				x += (int)(step * sw);				// sw  -1:���ړ�   +1:�E�ړ�
				step += 1.0;						// �ړ��X�s�[�h���X�ɃA�b�v
			}

			WorkGraphics.setColor(Color.white);		// �`��F�𔒐F�ɐݒ�
			WorkGraphics.drawString(Message[count], x, y);	// ���b�Z�[�W�\��

			repaint( );								// �ĕ`��

			// ���b�Z�[�W����ʍ��O�ɏo���ꍇ���C�܂��̓��b�Z�[�W����ʉE�O�ɏo���ꍇ
			if (x < - fontmetrics.stringWidth(Message[count]) || x > AppletWidth) {
				count++;							// ���̃��b�Z�[�W��
				if (count >= MessageNumber)			// �Ō�̃��b�Z�[�W�̎�
					count = 0;						// �ŏ��̃��b�Z�[�W
				fontsize = 1;						// �ŏ��t�H���g�T�C�Y�ɐݒ�
				step = 0;							// �ړ��X�e�b�v0�N���A
				sw *= -1;							// �ړ����������݂ɕύX
			}

			try {
				Thread.sleep(100);					// 100�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
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
}
