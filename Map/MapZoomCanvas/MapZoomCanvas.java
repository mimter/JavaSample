import java.applet.*;				// Applet
import java.awt.*;					// Graphics, Image, MediaTracker, Color
import java.awt.*;					// Graphics , addMouseListener, addMouseMotionListener
import java.awt.event.*;			// MouseListener, MouseMotionListener, MouseEvent

// �}�b�v�Y�[���L�����o�X�N���X ---------------------------------------------------------
public class MapZoomCanvas extends Applet {
	MapCanvas mapcanvas;
	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J����
		Image Map = getImage(getCodeBase( ), "image/map.gif");
		mediatracker.addImage(Map, 0);
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);					// ��O�����G���[�\��
		}

		Image WorkImage = createImage(200, 200);	// ��Ɨp�C���[�W�쐬
		mapcanvas = new MapCanvas(Map, WorkImage);	// �n�}�\���p�̃L�����o�X����
		mapcanvas.setSize(200, 200);				// �L�����o�X�̃T�C�Y�ݒ�

		setLayout(null);							// ���R���C�A�E�g
		add(mapcanvas);								// �L�����o�X���A�v���b�g�ɕt��
		mapcanvas.setBounds(50, 50, 200, 200);		// �L�����o�X�̈ʒu�T�C�Y�ݒ�
	}
}
// --------------------------------------------------------------------------------------
// �}�b�v�L�����o�X�N���X
class MapCanvas extends Canvas implements MouseListener, MouseMotionListener {
	Image Map;										// �n�}�C���[�W
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int CanvasWidth, CanvasHeight;					// �L�����o�X�T�C�Y
	int MapWidth, MapHeight;						// �}�b�v�̃T�C�Y	
	int MouseX, MouseY;								// �}�E�X�̈ʒu
	int DispX, DispY;								// �}�b�v�̕`��ʒu
	double Per;										// �`��{��

	// �R���X�g���N�^ -------------------------------------------------------------------
	public MapCanvas(Image map, Image workimage) {
		Map = map;
		WorkImage = workimage;

		CanvasWidth = workimage.getWidth(this);		// �L�����o�X�C���[�W�T�C�Y
		CanvasHeight = workimage.getHeight(this);
		MapWidth = Map.getWidth(this);				// �}�b�v�̕��ƍ���
		MapHeight = Map.getHeight(this);
		Per = (double)CanvasWidth / MapWidth;		// �����{��
		DispX = DispY = 0;							// �}�b�v�`��ʒu

		WorkGraphics = WorkImage.getGraphics( );	// ��Ɨp�O���t�B�b�N�擾

		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i�[�ǉ�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		WorkGraphics.setColor(Color.black);
		WorkGraphics.fillRect(0, 0, CanvasWidth, CanvasHeight);	// �w�i�`��

		WorkGraphics.drawImage(Map, DispX, DispY,
			(int)(Per * MapWidth), (int)(Per * MapHeight), this);

		WorkGraphics.drawLine(MouseX, 0, MouseX, CanvasHeight);	// �������C���`��
		WorkGraphics.drawLine(0, MouseY, CanvasWidth, MouseY);	// �������C���`��

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W��`��
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// MouseListener�C���^�[�t�F�[�X������-----------------------------------------------
	public void mousePressed(MouseEvent evt) {
		MouseX = evt.getX( );						// ���݂̃}�E�X�̈ʒu
		MouseY = evt.getY( );
		evt.consume( );								// �C�x���g����
 		if ((evt.getModifiers( ) & InputEvent.BUTTON1_MASK) != 0) {			// ���{�^��
			// �g��  �Y�[���C��
			DispX = (int)(MouseX - (MouseX - DispX) * (Per + 0.1) / Per);
			DispY = (int)(MouseY - (MouseY - DispY) * (Per + 0.1) / Per);
			Per += 0.1;								// �{���A�b�v
		}
 		else if ((evt.getModifiers( ) & InputEvent.BUTTON3_MASK) != 0) {	// �E�{�^��
			// �k���@�Y�[���A�E�g
			DispX = (int)(MouseX - (MouseX - DispX) * (Per - 0.1) / Per);
			DispY = (int)(MouseY - (MouseY - DispY) * (Per - 0.1) / Per);
			Per -= 0.1;								// �{���_�E��
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
