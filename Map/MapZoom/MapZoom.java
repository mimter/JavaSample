import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, MediaTracker, Color
import java.awt.event.*;							// MouseListener, MouseMotionListener etc

public class MapZoom extends Applet implements MouseListener, MouseMotionListener {

	Image Map;										// �n�}�摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int MapWidth, MapHeight;						// �n�}�̕��ƍ���
	int MouseX, MouseY;								// �}�E�X�̈ʒu
	int DispX, DispY;								// �\���ʒu
	double Per;										// �\���{��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J����
		Map = getImage(getCodeBase( ), "image/map.gif");	// �n�}�摜����
		mediatracker.addImage(Map, 0);				// ���f�B�A�g���b�J�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);					// ��O�����G���[�\��
		}

		AppletWidth = getSize( ).width;				// �A�v���b�g�T�C�Y
		AppletHeight = getSize( ).height;
		MapWidth = Map.getWidth(this);				// �}�b�v�T�C�Y
		MapHeight = Map.getHeight(this);
		Per = (double)AppletWidth / MapWidth;		// �����{��
		DispX = DispY = 0;							// �\���ʒu

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�擾

		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i
		repaint( );
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		WorkGraphics.setColor(Color.black);
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

		WorkGraphics.drawImage(Map, DispX, DispY,
			(int)(Per*MapWidth), (int)(Per*MapHeight), this);

		WorkGraphics.drawLine(MouseX, 0, MouseX, AppletHeight);	// �������C���`��
		WorkGraphics.drawLine(0, MouseY, AppletWidth, MouseY);	// �������C���`��

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W��`��
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// MouseListener�C���^�[�t�F�[�X������-----------------------------------------------
	public void mousePressed(MouseEvent evt) {
		MouseX = evt.getX( );						// �}�E�X�̈ʒu
		MouseY = evt.getY( );

       evt.consume( );								// �C�x���g����

 		if ((evt.getModifiers( ) & InputEvent.BUTTON1_MASK) != 0) {			// ���{�^��
			// �g��@�Y�[���C��
			DispX = (int)(MouseX - (MouseX - DispX) * (Per + 0.1) / Per);
			DispY = (int)(MouseY - (MouseY - DispY) * (Per + 0.1) / Per);
			Per += 0.1;
		}
 		else if ((evt.getModifiers( ) & InputEvent.BUTTON3_MASK) != 0) {	// �E�{�^��
			// �k���@�Y�[���A�E�g
			DispX = (int)(MouseX - (MouseX - DispX) * (Per - 0.1) / Per);
			DispY = (int)(MouseY - (MouseY - DispY) * (Per - 0.1) / Per);
			Per -= 0.1;
		} 
		repaint( );
	}
	public void mouseClicked(MouseEvent evt) { }
	public void mouseReleased(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
	public void mouseMoved(MouseEvent evt) {
		MouseX = evt.getX( );						// ���݂̃}�E�X�̈ʒu
		MouseY = evt.getY( );
		repaint( );
	}
	public void mouseDragged(MouseEvent evt) { }
}
