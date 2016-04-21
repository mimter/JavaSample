import java.applet.*;								// Applet
import java.awt.*;									// Graphic, Image, Color, Font, FontMetrics

public class RainbowMessage extends Applet implements Runnable {
	Image WorkImage, RainbowImage;					// ��Ɨp�Ɠ��C���[�W
	Graphics WorkGraphics, RainbowGraphics;			// ��Ɨp�Ɠ��O���t�B�b�N�X
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	String Message;									// ���b�Z�[�W
	int MessageDispX, MessageDispY;					// ���b�Z�[�W�\���ʒu
	int RainbowDispX;								// ���\���ʒu
	Thread thread = null;							// �X���b�h

	// ���������� -----------------------------------------------------------------------
    public void init( ) {
		AppletWidth = getSize( ).width;							// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;						// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);		// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );				// ��Ɨp�O���t�B�b�N�擾

		RainbowImage = createImage(AppletWidth, AppletHeight);	// ���C���[�W�쐬
		RainbowGraphics = RainbowImage.getGraphics( );			// ���O���t�B�b�N�X�擾

		// �p�����[�^���e��f�[�^�ݒ�
		Message = getParameter("message");					// �\�����b�Z�[�W
		int size = Integer.parseInt(getParameter("size"));	// �����T�C�Y
		String fontname = getParameter("fontname");			// �t�H���g��
		Font font = new Font(fontname, Font.PLAIN, size);	// �t�H���g
		FontMetrics fontmetrics = getFontMetrics(font);		// �t�H���g���g���b�N�X�擾
		WorkGraphics.setFont(font);							// �t�H���g�ݒ�

		// �\���ʒu
		MessageDispX = (AppletWidth - fontmetrics.stringWidth(Message)) / 2;
		MessageDispY =
		 (AppletHeight + fontmetrics.getAscent( ) - fontmetrics.getDescent( ))/2;
		MakeRainbowImage( );						// ���쐬
		RainbowDispX = 0;							// ���\���ʒu
	}
	// ���̑э쐬 -----------------------------------------------------------------------
    protected void MakeRainbowImage( ) {
		float hue = 0;								// �F��
		for(int i = 0; i < AppletWidth; i++) {		// ����
			hue = (float)i / AppletWidth;			// �F����AppletWidth����i
			RainbowGraphics.setColor(Color.getHSBColor(hue, 1f, 1f));	// �F�ݒ�
			RainbowGraphics.fillRect(i, 0, 1, AppletHeight);
		}
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g) {
		RainbowDispX -= 1;							// ���̕\���ʒu������1�ړ�
		if (RainbowDispX < -AppletWidth)
			RainbowDispX = 0;
		// ���̃C���[�W��`��
		WorkGraphics.setPaintMode( );				// �㏑�����[�h�ݒ�
		WorkGraphics.drawImage(RainbowImage, RainbowDispX, 0, null);
		WorkGraphics.drawImage(RainbowImage, RainbowDispX + AppletWidth, 0, null);

		WorkGraphics.setColor(Color.black);								// ���F�ɐݒ�
		WorkGraphics.drawString(Message, MessageDispX, MessageDispY);	// ���b�Z�[�W�`��

		// ���̃C���[�W��`��
		WorkGraphics.setXORMode(Color.black);		// ��փ��[�h�ݒ�(���F)
		WorkGraphics.drawImage(RainbowImage, RainbowDispX, 0, null);
		WorkGraphics.drawImage(RainbowImage, RainbowDispX + AppletWidth, 0, null);

		g.drawImage(WorkImage, 0, 0, null);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ) {
        while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );
			try {
				thread.sleep(100);					// �X���b�h�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
			}
		}
	}
	// �`��X�V���� ---------------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h����
	}
}
