import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Color, Font, FontMetrics

public class TapeMessage extends Applet implements Runnable {
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int	AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int	DispPoint;									// �����\���ʒu
	int	StringHeight;								// �����̍���
	String Message;									// ���b�Z�[�W
	Thread thread = null;							// �X���b�h
	Font font;										// �t�H���g
	FontMetrics fm;									// �t�H���g���g���b�N�X

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕��ƍ���
		AppletHeight = getSize( ).height;
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
		Message = getParameter("message");					// MESSAGE�p�����[�^�擾
		font = new Font("TimesRoman", Font.PLAIN, 20);		// �t�H���g�쐬
		WorkGraphics.setFont(font);							// �`��t�H���g�Z�b�g
		fm = getFontMetrics(font);							// �t�H���g���g���b�N�X�擾
		StringHeight = fm.getAscent( ) + fm.getDescent( );	// �����̍���
		setBackground(Color.white);							// �w�i�𔒐F
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		WorkGraphics.setColor(Color.white);			// �A�v���b�g�̐F�ݒ�
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);		// �A�v���b�g����
		WorkGraphics.setColor(Color.gray);							// �e�[�v�̉e�̐F�ݒ�
		int StringLength = fm.stringWidth((Message.substring(0, DispPoint)));
		WorkGraphics.fillRect(3, 3, StringLength, StringHeight);	// �e�[�v�̉e�`��
		WorkGraphics.setColor(Color.yellow);						// �e�[�v�̐F�ݒ�
		WorkGraphics.fillRect(0, 0,	StringLength, StringHeight); 	// �e�[�v�`��
		WorkGraphics.setColor(Color.black);							// �����̐F�ݒ�
		WorkGraphics.drawString(Message.substring(0, DispPoint), 0, StringHeight);
													// Message��DispPoint�������\��
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s���� -----------------------------------------------------------------
	public void run( ) {
		DispPoint = 0;								// �����\���ʒu�N���A
		while(thread != null) {		 				// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			if (DispPoint == Message.length( )) {	// ���b�Z�[�W�̒����ɂȂ����ꍇ
				try {
					thread.sleep(3000);				// ���b�Z�[�W�����ׂĕ\����C3�b��~
				} catch (InterruptedException e) {	// ���̃X���b�h�̊��荞�ݗ�O����
					showStatus(" "+e);
				}
				DispPoint = 0;						// �����\���ʒu�N���A
			}
			else
				DispPoint++;						// ���̕�����\���ł���悤�ɂ���
			try {
				thread.sleep(300);					// 1�����\�����Ƃ�300�~���b��~
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
