import java.applet.*;					// Applet
import java.awt.*;						// Graphics, Image, Color, MediaTracker
import java.awt.event.*;				// MouseListener, MouseEvent, MouseMotionListener

public class MapScrollDrag extends Applet
							implements MouseListener, MouseMotionListener {

	int	AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Image WorkImage;								// ��ƃC���[�W
	Graphics WorkGraphics;							// ��ƃO���t�B�b�N�X
	Image Map;										// �}�b�v
	int	MapWidth, MapHeight;						// �}�b�v�̃T�C�Y
	int DispX, DispY;								// �`��ʒu
	int	DistanceX, DistanceY;						// �}�E�X�Ɖ摜�Ƃ̋���
	boolean DragFlag;								// �h���b�O�t���O

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J����
		Map = getImage(getCodeBase( ), "image/map.gif");	// �摜�C���[�W��荞��
		mediatracker.addImage(Map, 0);				// ���f�B�A�g���b�J�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch (InterruptedException e) {			// waitForID�ɑ΂����O����
		}

		AppletWidth = getSize( ).width;				// �A�v���b�g�T�C�Y
		AppletHeight = getSize( ).height;
		MapWidth = Map.getWidth(this);				// �}�b�v�̕��ƍ���
		MapHeight = Map.getHeight(this);
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		DispX = AppletWidth / 2 - MapWidth / 2;		// �����\���ʒu�i�����\���j
		DispY = AppletHeight / 2 - MapHeight / 2;
		DragFlag = false;							// �h���b�O�t���O�I�t
		repaint( );
		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);				// �}�E�X���[�V�������X�i�[�ǉ�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		WorkGraphics.setColor(Color.black);
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		WorkGraphics.drawImage(Map, DispX, DispY, this);

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// MouseListener�C���^�[�t�F�[�X������----------------------------------------------
	public void mousePressed(MouseEvent evt) {
		int mouseX = evt.getX( );					// �}�E�X�̈ʒu
		int mouseY = evt.getY( );
		// �N���b�N�����ʒu���}�b�v�͈͓̔��̏ꍇ
		if (mouseX >= DispX && mouseX < DispX + MapWidth
		 && mouseY >= DispY && mouseY < DispY + MapHeight) {
			DragFlag = true;						// �h���b�O�J�n
			DistanceX = mouseX - DispX;				// �摜�̕\���ʒu�ƃ}�E�X�Ƃ̋���
			DistanceY = mouseY - DispY;
		}
	}
	public void mouseReleased(MouseEvent evt) {
		if (DragFlag == true) {						// �h���b�O�̏ꍇ�̂�
			DragFlag = false;						// �h���b�O�I��
			int mouseX = evt.getX( );				// �}�E�X�̈ʒu
			int mouseY = evt.getY( );
			// �摜�`��ʒu=���}�E�X�̈ʒu - ���΋���
			DispX = mouseX - DistanceX;
			DispY = mouseY - DistanceY;
		}
	}
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
	public void mouseDragged(MouseEvent evt) {
		if (DragFlag == true) {						// �h���b�O���̏ꍇ
			int mouseX = evt.getX( );				// �}�E�X�̈ʒu
			int mouseY = evt.getY( );
			DispX = mouseX - DistanceX;				// �摜�̕`��ʒu
			DispY = mouseY - DistanceY;
			repaint( );								// �ĕ`��
		}
	}
	public void mouseMoved(MouseEvent evt) { }
}
