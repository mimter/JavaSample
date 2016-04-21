import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Font

public class ElectricBulletinBoardV extends Applet implements Runnable {
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int	AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	String Message[ ] = new String[10];				// ���b�Z�[�W
	int	MessageNumber;								// ���b�Z�[�W�̐�
	int	MessageCount;								// ���b�Z�[�W�\���J�E���g
	int	MessageLength;								// ���b�Z�[�W�̒���
	int FontHeight;									// �����̍���
	int FontSize;									// �����̃T�C�Y
	int	Step;										// ���b�Z�[�W�̈ړ��Ԋu
	int	DispPoint;									// ���b�Z�[�W�̐����\���ʒu
	boolean PrepareFlag = false;					// �����t���O
	Thread thread = null;							// �X���b�h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		MessageNumber = Integer.parseInt(getParameter("number"));	// ���b�Z�[�W��
		for (int i = 0; i < MessageNumber; i++)						// ���b�Z�[�W�擾
			Message[i] = getParameter("message" + i);
		Step = AppletWidth / 20;									// ���b�Z�[�W�ړ��Ԋu
		FontSize = AppletWidth;										// �t�H���g�T�C�Y
		Font font = new Font("�l�r �S�V�b�N", Font.PLAIN, FontSize);// �t�H���g�ݒ�
		WorkGraphics.setFont(font);
		FontMetrics fm = getFontMetrics(font);				// �t�H���g���g���b�N�X
		FontHeight = fm.getAscent( ) + fm.getDescent( );	// �����̍���
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (PrepareFlag == false)					// �������ł��Ă��Ȃ��ꍇ
			return;
		WorkGraphics.setColor(Color.black);						// �w�i�F
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);	// �w�i�h��Ԃ�
		WorkGraphics.setColor(new Color(0, 255, 0));			// ���b�Z�[�W�̐F
		// ���b�Z�[�W�̕����������[�v(1�����Â`��)
		for (int n = 0; n < Message[MessageCount].length( ); n++) {
			WorkGraphics.drawString(Message[MessageCount].substring(n, n+1), 
									0, DispPoint + n * FontHeight);
		}
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		MessageCount = 0;							// ���b�Z�[�W�\���J�E���g
		MessageLength = (Message[MessageCount].length( ) - 1) * FontSize;// ���b�Z�[�W��
		DispPoint = AppletHeight + FontHeight;		// �\���ʒu�ݒ�
		PrepareFlag = true;							// �����t���O��ON
        while (thread != null) {					// �X���b�h�����݂��Ă����
			try {
				thread.sleep(50);
			} catch(InterruptedException e) {
			}
			DispPoint -= Step;						// �`��ʒu�����step�ړ�
			if (DispPoint < -MessageLength) {		// ���b�Z�[�W����O�ɏo���ꍇ
				DispPoint = AppletHeight + FontHeight;	// �\���ʒu�ݒ�
				MessageCount++;						// ���̃��b�Z�[�W
				if (MessageCount >= MessageNumber)	// ���b�Z�[�W���𒴂����ꍇ
					MessageCount = 0;				// �ŏ��̃��b�Z�[�W��
				MessageLength = (Message[MessageCount].length( ) - 1) * FontSize;
													// ���b�Z�[�W��
			}
			repaint( );
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
