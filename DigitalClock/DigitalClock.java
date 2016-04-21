import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, MediaTracker
import java.util.*;									// Calendar, TimeZone

public class DigitalClock extends Applet implements Runnable {
	Thread thread = null;
	Image Digit[ ] = new Image[11];					// �摜�f�[�^(0�`9,:)
	int DigitWidth, DigitHeight;					// �摜�f�[�^�̕��ƍ���
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕��ƍ���
		AppletHeight = getSize( ).height;
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J�[����
		for(int i = 0; i < 11; i++) {				// �摜�f�[�^�����
			Digit[i] = getImage(getCodeBase( ), "image/" + i + ".gif");
			mediatracker.addImage(Digit[i], 1);		// ���f�B�A�g���b�J�[�ɓ��͉摜��ݒ�
		}
		try {
			mediatracker.waitForID(1);				// �摜���͂���������܂ő҂�
		} catch (InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}
		DigitWidth = Digit[0].getWidth(this);		// �摜�T�C�Y
		DigitHeight = Digit[0].getHeight(this);
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
        while (thread != null) {					// �X���b�h�����݂��Ă����
			DispTime( );							// �����`��
			repaint( );								// �ĕ`��
			try{
				thread.sleep(100);					// �X���[�v
			} catch(InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// ���ԕ`�� -------------------------------------------------------------------------
	void DispTime( ) {
		// �J�����_�[�I�u�W�F�N�g�쐬
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		int hour = date.get(Calendar.HOUR);			// ���ݎ����̎��Ԏ擾
		int minute = date.get(Calendar.MINUTE);		// ���ݎ����̕��擾
		int second = date.get(Calendar.SECOND);		// ���ݎ����̕b�擾

		// ��ƃO���t�B�b�N�ɕ`��
		WorkGraphics.setColor(Color.white);
		WorkGraphics.fillRect(0, 0, DigitWidth*8, DigitHeight);
		int h1 = hour / 10,							// ���Ԃ�10�̌�
			h2 = hour % 10;							// ���Ԃ�1�̌�
		WorkGraphics.drawImage(Digit[h1],             0, 0, this);
		WorkGraphics.drawImage(Digit[h2], DigitWidth*1, 0, this);
		WorkGraphics.drawImage(Digit[10], DigitWidth*2, 0, this);
		int m1 = minute / 10,						// ����10�̌�
			m2 = minute % 10;						// ����1�̌�
		WorkGraphics.drawImage(Digit[m1], DigitWidth*3, 0, this);
		WorkGraphics.drawImage(Digit[m2], DigitWidth*4, 0, this);
		WorkGraphics.drawImage(Digit[10], DigitWidth*5, 0, this);
		int s1 = second / 10,						// �b��10�̌�
			s2 = second % 10;						// �b��1�̌�
		WorkGraphics.drawImage(Digit[s1], DigitWidth*6, 0, this);
		WorkGraphics.drawImage(Digit[s2], DigitWidth*7, 0, this);
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
			thread = null;							// �X���b�h�𖳌�
	}
}
