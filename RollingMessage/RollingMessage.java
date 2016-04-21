import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Font, MediaTracker

public class RollingMessage extends Applet implements Runnable {
	Image BackImage;								// �w�i�C���[�W
	Image WorkImage; 								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int	AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int BackImageWidth, BackImageHeight;			// �C���[�W�̕��ƍ���
	int	SleepTime;									// �X���[�v�^�C��
	String Message;									// ���b�Z�[�W
	Thread thread = null;							// �X���b�h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�̈�m��
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		// �p�����[�^���e��f�[�^�ݒ�
		Message = getParameter("Message");		// ���b�Z�[�W�̃p�����[�^�擾
		SleepTime = Integer.parseInt(getParameter("SleepTime"));	// �X���[�v�^�C��
		String imagefile = getParameter("BackImage");		// �C���[�W�t�@�C�����擾
		BackImage = getImage(getCodeBase( ), imagefile);	// �摜�C���[�W�捞��
		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J�[����
		mediatracker.addImage(BackImage, 0);		// ���f�B�A�g���b�J�[�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}
		BackImageWidth = BackImage.getWidth(this);	// �摜�C���[�W�̕��ƍ���
		BackImageHeight = BackImage.getHeight(this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		Font font = new Font("TimesRoman", Font.PLAIN, 10);	// �t�H���g�ݒ�
		WorkGraphics.setColor(Color.black);			// ��ƃO���t�B�b�N�X�̕\���F�����F
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight); // ���F�ɓh��Ԃ�
		WorkGraphics.setColor(Color.white);			// ��ƃO���t�B�b�N�X�̕\���F�𔒐F
		int startAngle = 0;							// �`��J�n�p�x
        while (thread != null) {					// �X���b�h�����݂��Ă����
			startAngle += 5;						// �`��J�n�p�x��+5���ς���
		 	WorkGraphics.setColor(Color.black);		// �w�i�����ŕ`��
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight); 
		 	WorkGraphics.setColor(Color.white);		// ���b�Z�[�W�̕`��F
			for (int n = 1; n <= 2; n++) {			// �����ƕ\���̕`��
				int DispPoint = 0;					// �`�敶���̈ʒu�@�擪�ɃZ�b�g
				for (int angle = startAngle; angle > 0; angle -= 5) {
					if ((n == 1 && ((angle % 360 < 90) || (angle % 360 > 270)))
													// 1��ڂ́C�����̕�����`��
					 ||	(n == 2 && ((angle % 360 >= 90) && (angle % 360 <= 270)))) {
													// 2��ڂ́C�\���̕�����`��
						float vs = (float)Math.sin(angle * 3.14 / 180);
						float vc = (float)Math.sin((angle - 90)* 3.14 / 180);
						WorkGraphics.drawString(
							Message.substring(DispPoint, DispPoint  + 1), 
							AppletWidth / 2 + (int)(vs * AppletWidth / 2 * 0.8),
							AppletHeight / 2 + (int)(vc * AppletHeight / 2 * 0.2));
					}
					DispPoint++;					// ���̕����ɐݒ�
					if (DispPoint >= Message.length( ))	// �Ō�̕����ɂȂ����ꍇ
						break;
				}
				if (n == 1)							// 1��ڂ͉摜�C���[�W�`��
					WorkGraphics.drawImage(BackImage,
										(AppletWidth - BackImageWidth) / 2,
										(AppletHeight - BackImageHeight) / 2, this);
			}
			repaint( );								// �ĕ`��
			try {
				Thread.sleep(SleepTime);			// �X���b�hsleeptime�~���b�X���[�v
			} catch (InterruptedException evt) {	// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" " + evt);
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
