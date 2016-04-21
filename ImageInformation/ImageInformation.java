import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color, Font, MediaTracker
import java.awt.image.PixelGrabber;					// PixelGrabber
import java.awt.event.*;							// MouseMotionListener, MouseEvent

public class ImageInformation extends Applet implements MouseMotionListener {
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	int Number;										// �f�[�^��
	Image image[ ] = new Image[30];					// �摜�C���[�W
	int X[ ] = new int[30];							// �摜�ʒu
	int Y[ ] = new int[30];
	int Width[ ] = new int[30];						// �摜�̃T�C�Y
	int Height[ ] = new int[30];
	String Text[ ] = new String[30];				// �\���e�L�X�g
	int MouseX, MouseY;								// �}�E�X�̈ʒu

	Image CheckImage;								// �`�F�b�N�C���[�W�̈�
	Graphics CheckGraphics;							// �`�F�b�N�O���t�B�b�N�X

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�T�C�Y
		AppletHeight = getSize( ).height;

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		WorkGraphics.setColor(new Color(255, 255, 255));	// ���F
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

		//�e��p�����[�^�擾�E�摜�f�[�^���́E
		MediaTracker mt = new MediaTracker(this);			// ���͊Ď����f�B�A�g���b�J�[
		Number = Integer.parseInt(getParameter("number"));	// �f�[�^��
		for (int i = 0; i < Number; i++) {
			String temp = getParameter("data" + i);
			X[i] = Integer.parseInt(temp.substring(0, 3));	// �摜�ʒu
			Y[i] = Integer.parseInt(temp.substring(4, 7));
			int p = temp.indexOf(",", 8);					// 8�����ڈȍ~�́u�C�v�̈ʒu
			String imagefilename = temp.substring(8, p);
			image[i] = getImage(getCodeBase( ), imagefilename);
			mt.addImage(image[i], 0);
			Text[i] = temp.substring(p + 1);
		}
		try {
			mt.waitForID(0);
		} catch (InterruptedException e) {
		}
		for (int i = 0; i < Number; i++) {			// �摜����Ɨ̈�ɕ`��
			Width[i] = image[i].getWidth(this);
			Height[i] = image[i].getHeight(this);
			WorkGraphics.drawImage(image[i], X[i], Y[i], this);	// �C���[�W�`��
		}

		CheckImage = createImage(1, 1);				// �s�N�Z���̃J���[�l�擾��Ɨ̈�
		CheckGraphics = CheckImage.getGraphics( );	// �`�F�b�N��ƃO���t�B�b�N

		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i�[�ǉ�	
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��

		for (int i = 0; i < Number; i++) {
			if (X[i] < MouseX && MouseX < X[i] + Width[i] &&	// �}�E�X���摜�̈��
				Y[i] < MouseY && MouseY < Y[i] + Height[i]) {

				// �|�C���g�̃J���[���
				int color = GetPixels(image[i], MouseX-X[i], MouseY-Y[i]);
				int alpha = (color >> 24) & 0xff; 	// �s�����x 0:�����`255�F�s����

				String message = "x="+MouseX+" y="+MouseY+" Alpha="+alpha;
				showStatus(message);

				if (alpha == 0xff) {						// �s�����C�摜������ꍇ
					int size = 12;							// �����T�C�Y
					g.setColor(new Color(0, 200, 255));		// ���b�Z�[�W�g
					g.fillRect(X[i], Y[i]+Height[i]+1, (Text[i].length( ))*size,size);
					g.setColor(new Color(0, 0, 0));
					g.setFont(new  Font("�l�r �S�V�b�N",Font.PLAIN,size));	// �t�H���g�ݒ�
					g.drawString(Text[i], X[i], Y[i]+Height[i]+size); 		// ���b�Z�[�W�`��
				}
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// ----------------------------------------------------------------------------------
	// �摜�C���[�Wimage�̓_(x, y)�̃J���[�l�擾
	public int GetPixels(Image image, int x, int y) {
		// �傫�ȃT�C�Y�̉摜���g���ƁCPixelGrabber�CgrabPixels( )�̏������x���Ȃ�
		// �T�C�Y��1�̃O���t�B�b�N�̈�ɑΏۃ|�C���g���������݁C���̉摜����������
		CheckGraphics.drawImage(image, -x, -y, this);

		int[ ] pixels = new int[1];
		PixelGrabber pg = new PixelGrabber(image, x, y, 1, 1, pixels, 0, 1);
        try  {
			pg.grabPixels( );
        }  catch  (InterruptedException  e)  {
        }
		return (pixels[0]);
	}
	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
    public void mouseMoved(MouseEvent evt) {
        MouseX = evt.getX( );						// �}�E�X�̌��݈ʒu��ۊ�
		MouseY = evt.getY( );
        repaint( );
    }
    public void mouseDragged(MouseEvent evt) { }
}
