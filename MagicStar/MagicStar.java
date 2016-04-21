import java.applet.*;								// Applet, AudioClip
import java.awt.*;									// Graphics, Image, Color
import java.awt.event.*;							// MouseListener, MouseMotionListener, MouseEvent

public class MagicStar extends Applet
			implements MouseListener, MouseMotionListener, Runnable{
			// �}�E�X���X�i�[�C�}�E�X���[�V�������X�i�[�C�����i�u������
    Thread thread = null;							// �X���b�h
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���

	int N = 50;										// ���̐�
	int Size[ ] = new int[N];						// �e���̑傫��
	int X[ ] = new int[N];							// ���̈ʒu
	int Y[ ] = new int[N];
	float Hue[ ] = new float[N];					// ���̐F��

	float HueStep = 0.01f;							// ���쐬�̐F���ω��f�t�H���g�l
	int Radius = 30;								// ���쐬�̃f�t�H���g�ő唼�a
	int Range = 60;									// ���쐬�̃f�t�H���g�ő唭���͈�

	int	MouseX, MouseY;								// �}�E�X�̈ʒu

	Image WorkImage;								// ��ƃC���[�W
	Graphics WorkGraphics;							// ��ƃO���t�B�b�N�X

    String Key[ ] = {	"C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5"}; //�e�L�[�̖��O
    AudioClip Sound[ ] = new AudioClip[8];			// �e�L�[�ɑ΂���T�E���h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �p�����[�^����
		Radius = Integer.parseInt(getParameter("radius"));		// ���쐬�̍ő唼�a
		Range = Integer.parseInt(getParameter("range"));		// ���쐬�̍ő唭���͈�
		HueStep = new Float(getParameter("step")).floatValue( );// �F���ω��l

		// ���f�[�^����
		for (int i = 0; i < 8; i++)								// ���f�[�^����
	        Sound[i] = getAudioClip(getCodeBase( ), "sound/"+Key[i]+".au");

		AppletWidth = getSize( ).width;							// �A�v���b�g�̃T�C�Y
		AppletHeight = getSize( ).height;
		WorkImage = createImage(AppletWidth, AppletHeight);		// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );				// ��Ɨp�O���t�B�b�N�X�擾

		addMouseListener(this);									// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);							// �}�E�X���[�V�������X�i�[�ǉ�
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
    public void start( ){
        thread = new Thread(this);					// �X���b�h����
        thread.start( );							// �X���b�h�J�n
    }
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g){
		WorkGraphics.setColor(Color.black);			// �`��F�����F�ɐݒ�
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);	// �N���A
		for (int i = 0; i < N; i++) {				// ���̐��J��Ԃ�
			if (Size[i] > 0) {						// ���̑傫��������ꍇ
    			// 2�̃��C���ŊO�����璆�S�Ɍ������ĕ`��
				for (int p = Size[i]; p > 0; p--) {	// �O�����璆�S�Ɍ������Đ��쐬
					// �F�ݒ�i�O�����璆�S�֖��x�𖾂邭����j
					WorkGraphics.setColor(
						Color.getHSBColor(Hue[i], 1f, (float)(Size[i] - p) / Size[i]));
					// �Q�̃��C���ŏ\���`��
					WorkGraphics.drawLine(X[i]  ,Y[i]-p, X[i]  , Y[i]+p);
					WorkGraphics.drawLine(X[i]-p,Y[i]  , X[i]+p, Y[i]  );
				}
				Size[i]--;							// ���̃T�C�Y������������
			}
		}
		g.drawImage(WorkImage, 0, 0, this); 		// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ){
        while(thread != null){						// �X���b�h�����݂��Ă����
			MakeStar( );							// ���쐬
            try{
               thread.sleep(50);					// �X���b�h�X���[�v
            }catch(InterruptedException e){			// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
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
	// �X�^�[�쐬 -----------------------------------------------------------------------
	void MakeStar(  ) {
		for (int i = 0; i < N; i++) {
			if (Size[i] <= 0) {
				Size[i] = (int)(Math.random( ) * Radius + 1);				// �傫��
				X[i] = MouseX - Range + (int)(Math.random( ) * Range * 2);	// ����x�ʒu
				Y[i] = MouseY - Range + (int)(Math.random( ) * Range * 2);	// ����y�ʒu
				Hue[i] += HueStep;					// �F���ω�
				if (Hue[i] > 1)
					Hue[i] = 0;
			}
		}
	}
	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
    public void mousePressed(MouseEvent evt) {
        int p = evt.getX( ) / (AppletWidth / 8);	// �N���b�N���������ˋ�̌��Ղ̂��Ԗ�
		if (p >= 0 && p < 8)						// 1�I�N�^�[�u���Ȃ��
			Sound[p].play( );						// ���̉��K��炷
	}
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
    public void mouseMoved(MouseEvent evt) {
		MouseX = evt.getX( );						// �}�E�X�̌��݈ʒu��ۊ�
		MouseY = evt.getY( );
    }
    public void mouseDragged(MouseEvent evt) { }
}
