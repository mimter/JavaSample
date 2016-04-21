import java.applet.*;						// Applet
import java.awt.*;							// Graphics, Image, Color, Font, FontMetrics

public class WaveMessage extends Applet implements Runnable {
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int	AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	String Message;									// ���b�Z�[�W
	Thread thread;									// �X���b�h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		Message = getParameter("message");			// �p�����[�^��胁�b�Z�[�W�擾

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
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
		int angle = 0;								// �ŏ��̕����̏����p�x
		int step = 30;								// �e�����̕\���p�x�Ԋu
		Font font = new Font("TimesRoman", Font.PLAIN, 40);	// �t�H���g
		FontMetrics fontmetrics = getFontMetrics(font);		// �t�H���g���g���b�N�X
		int haba =									// �㉺��
		 (AppletHeight- (fontmetrics.getAscent( )+ fontmetrics.getDescent( ))) / 2;
		int base = haba + fontmetrics.getAscent( );	// �\���x�[�X���C���̃|�C���g	
		WorkGraphics.setFont(font);					// �t�H���g�ݒ�
        while (thread != null) {					// �X���b�h�����݂��Ă����
			WorkGraphics.setColor(Color.white);		// �w�i�𔒐F�œh��Ԃ�
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
			WorkGraphics.setColor(Color.black);		// �`��F�����F
			angle = (angle + step) % 360;			// �ŏ��̕����̕\�������p�x
			for (int i = 0; i < Message.length( ); i++) {	// �����������[�v
				int x = AppletWidth / Message.length( ) * i;
				int y = (int)(base + haba * Math.sin((angle + step * i) * 3.14 / 180));
				// i�Ԗڂ̕����\��
				WorkGraphics.drawString(Message.substring(i, i + 1), x, y);
			}
			repaint( );								// �ĕ`��
			try {
				thread.sleep(200);					// 200�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);					// �G���[���X�e�[�^�X�o�[�ɕ\��
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
