import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, MediaTracker
import java.awt.event.*;							// MouseListener, MouseEvent

public class MapScrollClick extends Applet implements MouseListener, Runnable {
	Image Map;										// �n�}�C���[�W
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	int MapWidth, MapHeight;						// �摜�̃T�C�Y
	int DispX, DispY;								// �\���ʒu
	int MoveX=0, MoveY=0;							// �ړ��X�e�b�v
	Thread thread = null;							// �X���b�h
	boolean MoveFlag = false;						// �ړ��t���O

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		MediaTracker mediatracker = new MediaTracker(this);	// ���͊Ď����f�B�A�g���b�J
		Map = getImage(getCodeBase( ), "image/map.gif");
		mediatracker.addImage(Map, 0);				// ���f�B�A�g���b�J�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);						// ��O�����G���[�\��
		}

		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���
		MapWidth = Map.getWidth(this);				// �}�b�v�̕�
		MapHeight = Map.getHeight(this);			// �}�b�v�̍���

		DispX = AppletWidth / 2 - MapWidth / 2;		// �}�b�v�̒��S���A�v���b�g�̒��S��
		DispY = AppletHeight / 2 - MapHeight / 2;

		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ){
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		DispX += MoveX;								// �\���ʒu���ړ�
		DispY += MoveY;
		// �n�}���A�v���b�g�����ׂĂɕ\������Ȃ��ꍇ
		if (DispX > 0 || DispX < (AppletWidth - MapWidth) ||
			DispY > 0 || DispY < (AppletHeight - MapHeight)){
			DispX -= MoveX;							// �ړ��������A�߂�
			DispY -= MoveY;
			MoveX = MoveY = 0;						// �ړ���~
			MoveFlag = false;
		}
		g.drawImage(Map, DispX, DispY, this);
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ){
		while (thread != null) {					// �X���b�h�����݂��Ă����
			try{
				thread.sleep(100);					// �X���b�h�X���[�v
			}catch(InterruptedException e){			// �X���b�h�̊��荞�ݗ�O����
				showStatus(" " + e);				// ��O�����G���[�\��
			}
			repaint( );								// �ĕ`��
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ){
		thread = null;								// �X���b�h����
	}
	// MouseListener�C���^�[�t�F�[�X������----------------------------------------------
	public void mousePressed(MouseEvent evt) {
		int mouseX = evt.getX( );					// �}�E�X�̈ʒu
		int mouseY = evt.getY( );
		if (MoveFlag == false) {					// �Î~���Ă���ꍇ
			MoveFlag = true;						// �ړ��J�n
			// ��ʂ�9�������āC8�����Ɉړ�
			if (mouseX < AppletWidth / 3)			// �������N���b�N�����ꍇ
				MoveX = +1;							// �摜�̈ړ����E����
			else if(mouseX > AppletWidth / 3 * 2)	// �E�����N���b�N�����ꍇ
				MoveX = -1;							// �摜�̈ړ���������
			else
				MoveX = 0;
			if (mouseY < AppletHeight / 3)			// �㑤���N���b�N�����ꍇ
				MoveY = +1;							// �摜�̈ړ���������
			else if(mouseY > AppletHeight / 3 * 2)	// �������N���b�N�����ꍇ
				MoveY = -1;							// �摜�̈ړ����㑤��
			else
				MoveY = 0;
		}
		else {										// �����Ă���ꍇ
			MoveFlag = false;						// ��~
			MoveX = MoveY = 0;						// �ړ���0
		}
		repaint( );									// �ĕ`��
	}
	public void mouseClicked(MouseEvent evt) { }
	public void mouseReleased(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
}
