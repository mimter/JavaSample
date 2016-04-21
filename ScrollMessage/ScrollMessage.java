import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, MediaTracker

public class ScrollMessage extends Applet implements Runnable {
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	String BackImageFile = null;					// �w�i�摜�t�@�C����
	Image BackImage;								// �w�i�C���[�W
	Image Parts[ ] = new Image[6];					// �g�C���[�W
	int PartsSize;									// �g�T�C�Y
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	String Message[ ] = new String[20];				// ���b�Z�[�W
	int MessageNumber;								// ���b�Z�[�W��
	int Step = 1;									// ���b�Z�[�W�̃X�N���[���Ԋu
	int DispPoint;									// ���b�Z�[�W���������\���ʒu
	int Size;										// �t�H���g�T�C�Y
	int RowHeight;									// �s�̍���
	Color color;									// �����̐F
	MediaTracker mediatracker = new MediaTracker(this);		// ���f�B�A�g���b�J
	Thread thread = null;							// �X���b�h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);		// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );				// ��Ɨp�O���t�B�b�N�X�擾

		// �p�����[�^���f�[�^����
		String str = getParameter("color");						// color�p�����[�^�擾
		int p1 = str.indexOf(",");								// �ŏ��́u�C�v�̈ʒu����
		int red = Integer.parseInt(str.substring(0, p1));		// �ŏ��́u�C�v�܂ł̒l
		int p2 = str.indexOf(",", p1+1);						// ���́u�C�v�̈ʒu����
		int green = Integer.parseInt(str.substring(p1+1, p2));	// ���̒l
		int blue = Integer.parseInt(str.substring(p2+1));		// �Ō�̒l
		color = new Color(red, green, blue);					// �����̐F�ݒ�

		MessageNumber = Integer.parseInt(getParameter("number"));	// ���b�Z�[�W��
		for (int i = 0; i < MessageNumber; i++)			// ���b�Z�[�W����
			Message [i] = getParameter("message" + i);
		BackImageFile = getParameter("backimage");		// �w�i�摜
		if (BackImageFile != null) {
			BackImage = getImage(getCodeBase( ), BackImageFile);// �w�i�摜����
			mediatracker.addImage(BackImage, 0);	// ���f�B�A�g���b�J�ɃZ�b�g
		}
		for (int i = 0; i < 6; i++) {
			Parts[i] = getImage(getCodeBase( ), "image/part"+i+".jpg");	// �g�摜
			mediatracker.addImage(Parts[i], 0);		// ���f�B�A�g���b�J�ɃZ�b�g
		}
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {
		}
		PartsSize = Parts[0].getWidth(this);		// �g�T�C�Y

		Size = Integer.parseInt(getParameter("size"));			// ������𐮐���
		Font font = new Font("�l�r �S�V�b�N", Font.PLAIN, Size);// �t�H���g�ݒ�
		WorkGraphics.setFont(font);
		FontMetrics fm = getFontMetrics(font);					// �t�H���g���g���b�N�X
		RowHeight = (int)((fm.getAscent( ) + fm.getDescent( ))*1.5);// �s�̍��� ����1.5�{
		DispPoint = AppletHeight + RowHeight;		// ���b�Z�[�W�\�������ʒu�ݒ�
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (BackImageFile != null)					// �w�i�摜���w�肵�Ă����ꍇ
			WorkGraphics.drawImage(BackImage, 0, 0, this);	// �w�i�摜�`��
		else {
			WorkGraphics.setColor(Color.white);		// �w�i�𔒂��h��
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		}
		WorkGraphics.setColor(color);				// ���b�Z�[�W�̕`��F
		for (int i = 0; i < MessageNumber; i++) 	// ���b�Z�[�W��\��
			WorkGraphics.drawString(Message[i], PartsSize, DispPoint + i * RowHeight);
		if (DispPoint + (MessageNumber - 1) * RowHeight < 0)	// ���X�g�̕\���ʒu����O
			DispPoint = AppletHeight + RowHeight;	// �\���ʒu���A�v���b�g�̉��ɐݒ�
		else
			DispPoint -= Step;						// �\���ʒu��step����ɏグ��

		if (BackImageFile == null) {				// �w�i�摜���Ȃ��ꍇ�C�g�쐬
			for (int i = 0; i < AppletWidth; i += PartsSize) {
				WorkGraphics.drawImage(Parts[4], i, 0, this);
				WorkGraphics.drawImage(Parts[4], i, AppletHeight - PartsSize, this);
			}
			for (int i = 0; i < AppletHeight; i += PartsSize) {
				WorkGraphics.drawImage(Parts[5], 0, i, this);
				WorkGraphics.drawImage(Parts[5], AppletWidth - PartsSize, i, this);
			}
			WorkGraphics.drawImage(Parts[0], 0, 0, this);
			WorkGraphics.drawImage(Parts[1], AppletWidth - PartsSize, 0, this);
			WorkGraphics.drawImage(Parts[2], 0, AppletHeight - PartsSize, this);
			WorkGraphics.drawImage(Parts[3],
 				AppletWidth - PartsSize, AppletHeight - PartsSize, this);
		}

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );								// update( )�����s
			try {
				Thread.sleep(100);					// �w��~���b�X���[�v
			} catch (InterruptedException e) {		// ���荞�ݓ��̗�O����
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
