import java.applet.*;								// Applet, AudioClip
import java.awt.*;									// Graphics, Image, MediaTracker, Color
import java.awt.event.*;							// MouseListener, MouseEvent

public class PanelMatch extends Applet implements Runnable, MouseListener {
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	Image panel[ ] = new Image[9];					// �p�l���̃C���[�W
	int Size;										// �p�l���T�C�Y
	int Table[ ][ ] = new int[4][4];				// �p�l���̉摜�ԍ����i�[�����e�[�u��
	int Status[ ][ ] = new int[4][4];				// �p�l���̏�Ԃ��i�[�����e�[�u��
	int OpenPanel[ ][ ] = new int[2][2];			// �I�[�v�������ʒu��ۊ�
	int Count;										// �߂�������
	int Seikaisu;									// ����
	int GameFlag;									// �Q�[���t���O
	Thread thread = null;							// �X���b�h
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
    AudioClip ClickSound, SeikaiSound;				// �N���b�N�T�E���h�C�����T�E���h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�擾
        ClickSound = getAudioClip(getCodeBase( ), "sound/click.au");		// �N���b�N��
        SeikaiSound = getAudioClip(getCodeBase( ), "sound/seikai.au");	// ������

		MediaTracker mediatracker = new MediaTracker(this);		// ���f�B�A�g���b�J
		for (int n = 0; n < 9; n++) {				// �p�l���摜����
			panel[n] = getImage(getCodeBase( ), getParameter("panel" + n));
			mediatracker.addImage(panel[n], 0);		// �摜�����f�B�A�g���b�J�ɃZ�b�g
		}
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
		}
		Size = panel[0].getWidth(this);				// �p�l���T�C�Y

		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�
		shake( );									// �e�p�l���̈ʒu�������_���ɐݒ�
		Count = 0;									// 2���Z�b�g�ŗ��Ԃ��Ƃ��̃J�E���g
		Seikaisu = 0;								// �����p�l����
		GameFlag = 1;								// �Q�[���t���O
	}
	// �V�F�C�N���� ---------------------------------------------------------------------
	public void shake( ) {
		// PanelNumber��8��ނ̃p�l���̒l1�`8���Z�b�g
		int n = 1;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				Table[i][j] = n;
				Table[i+2][j] = n++;
			}
		}

		// �����_���ɕ��ёւ���
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int ip = (int)(Math.random( )*4);
				int jp = (int)(Math.random( )*4);
				int w = Table[i][j];				// 3�X�e�b�v�Ō���
				Table[i][j] = Table[ip][jp];
				Table[ip][jp] = w;
			}
		}

		// ��ԃZ�b�g
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				Status[i][j] = 0;

		// �\��
		WorkGraphics.setColor(Color.black);			// ��ƃO���t�B�b�N�X�ɕ`��
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		for (int i = 0; i < 4; i++)					// �B���p�l���`��
			for (int j = 0; j < 4; j++)
				WorkGraphics.drawImage(panel[0], j * Size, i * Size, this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			try {
				thread.sleep(200);					// �X���b�h200�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
			}

			if (Count == 2) {						// 2���ڂ𗠕Ԃ����Ƃ�
				if (Table[OpenPanel[0][0]] [OpenPanel[0][1]]		// ��v�����ꍇ
					== Table[OpenPanel[1][0]] [OpenPanel[1][1]]) {
					SeikaiSound.play( );			// ������
					Seikaisu++;						// �����p�l���̎�ނ̐�
					if (Seikaisu == 8)
						thread = null;				// �X���b�h�𖳌��ɂ��āC�Q�[���I��
				}
				else {								// ��v���Ȃ������ꍇ
					try {
						thread.sleep(2000);			// �X���b�h���Q�b�ԃX���[�v
					} catch (InterruptedException e) {	// ���̃X���b�h�̊��荞�ݗ�O����
						showStatus(" "+e);
					}
					for (int n = 0; n < 2; n++) {	// �B���p�l���ɖ߂�
						WorkGraphics.drawImage(panel[0],
							OpenPanel[n][1] * Size, OpenPanel[n][0] * Size, this);
						Status[OpenPanel[n][0]][ OpenPanel[n][1]] = 0;	// ���ɖ߂�
					}
					repaint( );						// �ĕ`��
				}
				Count = 0;							// ���Ԃ��J�E���g���N���A
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
	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
    public void mouseReleased(MouseEvent evt) {
        evt.consume( );								// �C�x���g������
		if (Count < 2) {							// count��0��1�̏ꍇ
			int j = evt.getX( ) / Size;				// �N���b�N�ʒu��z��̈ʒu�ɕϊ�
			int i = evt.getY( ) / Size;
			if (Status[i][j] == 0) {				// ���Ԃ��Ă���ꍇ
				Status[i][j] = 1;					// �\�ɂ���
				OpenPanel[Count][0] = i;			// �I�[�v�������ʒu��ۊ�
				OpenPanel[Count][1] = j;
				// �I�[�v�������ʒu�̉摜��`��
				WorkGraphics.drawImage(panel[Table[i][j]],
										j * Size, i * Size, this);
				WorkGraphics.drawRect(j * Size, i * Size, Size, Size);
				ClickSound.play( );					// �N���b�N��
				repaint( );							// �A�v���b�g��ʂ��ĕ`��
				Count++;							// ���Ԃ��J�E���g��+1
			}
		}
	}
    public void mouseClicked(MouseEvent evt) { }
    public void mousePressed(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
}
