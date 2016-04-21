import java.applet.*;								// Applet, AudioClip
import java.awt.*;									// Graphics, Image, Color
import java.awt.event.*;							// MouseListener, MouseEvent

public class Piano extends Applet implements MouseListener {
    String KeyName[ ] = {"C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5", 	//�e�L�[�̖��O
						 "C4S", "D4S", "F4S", "G4S", "A4S"};			 	//���t�@�C����
	int Point[ ][ ] = new int[13][4];				// ���Ղ̈ʒu
	int Keyboard[ ] = new int[13];					// ���Ղ̏��  0:�����Ă��Ȃ��@1:����
    AudioClip Sound[ ] = new AudioClip[13];			// ��
	int AppletWidth, AppletHeight;					// �A�v���b�g�T�C�Y
	int PreHitKey = 0;								// �O�񉟂�����������
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	// ���������� -----------------------------------------------------------------------
    public void init( ) {
		for (int i = 0; i < 13; i++)				// ���f�[�^����
	        Sound[i] = getAudioClip(getCodeBase( ), "sound/"+KeyName[i]+".au");
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕��ƍ���
		AppletHeight = getSize( ).height;
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
		addMouseListener(this);								// �}�E�X���X�i�[�ǉ�

		// ���Ղ̍��W�C���C����
		// C4
		Point[0][0] = (int)(AppletWidth *  0 / 40.0);  Point[0][1] = 0;
		Point[0][2] = (int)(AppletWidth *  5 / 40.0);  Point[0][3] = AppletHeight;
		// D4
		Point[1][0] = (int)(AppletWidth *  5 / 40.0);  Point[1][1] = 0;
		Point[1][2] = (int)(AppletWidth *  5 / 40.0);  Point[1][3] = AppletHeight;
		// E4
		Point[2][0] = (int)(AppletWidth * 10 / 40.0);  Point[2][1] = 0;
		Point[2][2] = (int)(AppletWidth *  5 / 40.0);  Point[2][3] = AppletHeight;
		// F4
		Point[3][0] = (int)(AppletWidth * 15 / 40.0);  Point[3][1] = 0;
		Point[3][2] = (int)(AppletWidth *  5 / 40.0);  Point[3][3] = AppletHeight;
		// G4
		Point[4][0] = (int)(AppletWidth * 20 / 40.0);  Point[4][1] = 0;
		Point[4][2] = (int)(AppletWidth *  5 / 40.0);  Point[4][3] = AppletHeight;
		// A4
		Point[5][0] = (int)(AppletWidth * 25 / 40.0);  Point[5][1] = 0;
		Point[5][2] = (int)(AppletWidth *  5 / 40.0);  Point[5][3] = AppletHeight;
		// B4
		Point[6][0] = (int)(AppletWidth * 30 / 40.0);  Point[6][1] = 0;
		Point[6][2] = (int)(AppletWidth *  5 / 40.0);  Point[6][3] = AppletHeight;
		// C5
		Point[7][0] = (int)(AppletWidth * 35 / 40.0);  Point[7][1] = 0;
		Point[7][2] = (int)(AppletWidth *  5 / 40.0);  Point[7][3] = AppletHeight;
		// C4S
		Point[8][0] = (int)(AppletWidth *  2 / 40.0);  Point[8][1] = 0;
		Point[8][2] = (int)(AppletWidth *  4 / 40.0);
		Point[8][3] = (int)(AppletHeight * 0.6);
		// D4S
		Point[9][0] = (int)(AppletWidth *  9 / 40.0);  Point[9][1] = 0;
		Point[9][2] = (int)(AppletWidth *  4 / 40.0);
		Point[9][3] = (int)(AppletHeight * 0.6);
		// F4S
		Point[10][0] = (int)(AppletWidth * 17 / 40.0);  Point[10][1] = 0;
		Point[10][2] = (int)(AppletWidth *  4 / 40.0);
		Point[10][3] = (int)(AppletHeight * 0.6);
		// G4S
		Point[11][0] = (int)(AppletWidth * 23 / 40.0);  Point[11][1] = 0;
		Point[11][2] = (int)(AppletWidth *  4 / 40.0);
		Point[11][3] = (int)(AppletHeight * 0.6);
		// A4S
		Point[12][0] = (int)(AppletWidth * 29 / 40.0);  Point[12][1] = 0;
		Point[12][2] = (int)(AppletWidth *  4 / 40.0);
		Point[12][3] = (int)(AppletHeight * 0.6);
	}
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g) {
		hitdraw( );									// ���Օ`��
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �Ō��`�� -------------------------------------------------------------------------
	void hitdraw( ) {
		for (int i = 0; i < 13; i++) {				// �����Ղ���`��
			if (Keyboard[i] == 1)					// ���Ղ��������ꍇ
				WorkGraphics.setColor(new Color(255, 255, 153));
			else {
				if(i >= 8)
					WorkGraphics.setColor(Color.black);	// �����Ղ̏ꍇ
				else
					WorkGraphics.setColor(Color.white);	// �����Ղ̏ꍇ
			}
			WorkGraphics.fillRect(Point[i][0], Point[i][1], Point[i][2], Point[i][3]);
			WorkGraphics.setColor(Color.black);
			WorkGraphics.drawRect(Point[i][0], Point[i][1],
								  Point[i][2]-1, Point[i][3]-1);
		}
	}
	// MouseListener�C���^�[�t�F�[�X������-----------------------------------------------
 	// �s�A�m�̂悤�ɁC1��̃}�E�X�_�E���ɑ΂��āC�P��炷
	public void mousePressed(MouseEvent evt) {
		int mouseX = evt.getX( );					// �N���b�N�����}�E�X�̈ʒu
		int mouseY = evt.getY( );
		for (int i = 12; i >= 0; i--) {				// �������Ղ���`�F�b�N
			if (mouseX >= Point[i][0] && mouseX <= Point[i][0] + Point[i][2]
			 && mouseY >= Point[i][1] && mouseY <= Point[i][1] + Point[i][3]) {
				Keyboard[i] = 1;					// ���������ՂɂP���Z�b�g
				Sound[i].play( );					// �P��炷
				PreHitKey = i;						// ����̈ʒu��O��̈ʒu�Ƃ��ċL��
				break;
			}
		}
        repaint( );									// �ĕ`��
    }
	public void mouseReleased(MouseEvent evt) {
		Keyboard[PreHitKey] = 0;					// �O�񉟂������Ղ��N���A
        repaint( );									// �ĕ`��
	}
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
}
