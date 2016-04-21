import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color

public class Hunsui extends Applet implements Runnable{
    Thread thread = null;							// �X���b�h
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int RunSpeed = 90;								// ���s�X�s�[�h

	// �e��ݒ�
	int AngleRange = 10;							// ���������ɑ΂��Ă̔��ˊp�x��
	int FireSpeed = 90;								// ���˃X�s�[�h�f�t�H���g�l

	int XP, YP;										// ���˂̈ʒu
	int MAX = 500;									// ���H�̍ő吔
	int X[ ] = new int[MAX];						// ���݈ʒu
	int Y[ ] = new int[MAX];

	double Angle[ ] = new double[MAX];				// ���ˊp�x�i���W�A��) 
	double Speed[ ] = new double[MAX];				// ���˃X�s�[�h
	double V0x[ ] = new double[MAX];				// ���������̑��x
	double V0y[ ] = new double[MAX];				// ���������̑��x
	int Process[ ] = new int[MAX];					// �v���Z�X�i�㏸�C�����j
	double Time[ ] = new double[MAX];				// �o�ߎ���

	double Rad = Math.PI / 180;						// ���W�A��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾
	
		// �e��p�����[�^����
		RunSpeed = Integer.parseInt(getParameter("runspeed")); 		// ���s�X�s�[�h
		if (RunSpeed > 99)
			RunSpeed = 99;
		MAX = Integer.parseInt(getParameter("max")); 				// ���ː���
		if (MAX > 500)
			MAX = 500;
		AngleRange = Integer.parseInt(getParameter("anglerange"));	// ���ˊp�x��
		FireSpeed = Integer.parseInt(getParameter("firespeed"));	// ���˃X�s�[�h

		// ������
		XP = AppletWidth / 2;						// ���˂̈ʒu
		YP = AppletHeight;
		for (int i = 0; i < MAX; i++)
			Process[i] = i;							// �ŏ��������ɔ�яo���悤�ɐݒ�
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
		Making( );
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ){
        while(thread != null){						// �X���b�h�����݂��Ă����
            try{
               thread.sleep(1000 - RunSpeed * 10);	// �X���b�h�X���[�v
            }catch(InterruptedException e){			// ���̃X���b�h�̊��荞�ݗ�O����
			}
        	repaint( );								// �ĕ`��
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
	// �V�[���쐬 -----------------------------------------------------------------------
	void Making( ) {
		for (int n = 0; n < MAX; n++) {
			if (Process[n] == 0) {								// ���ˏ����i�K
				Angle[n] = 90 - AngleRange / 2 + (int)(Math.random( )* AngleRange);
																// ���ˊp�x
				Speed[n] = FireSpeed + (int)(Math.random( ) * 10);	// ���˃X�s�[�h
				X[n] = AppletWidth / 2;							// ����̈ʒu��ۊ�
				Y[n] = AppletHeight;
				V0x[n] = Speed[n] * Math.cos(Angle[n] * Rad);	// �������������x
				V0y[n] = Speed[n] * Math.sin(Angle[n] * Rad); 	// �������������x
				Time[n] = 0;									// �o�ߎ��ԃN���A
				Process[n] = 1;									// ���̃v���Z�X�i�K
			}
			else if (Process[n] == 1) {							// ��s�i�K
				if(Y[n] <= AppletHeight) {						// �\���͈͓�
					Time[n] += 0.1;								// ���ԃJ�E���g
					int xt = (int)(V0x[n] * Time[n]);			// t�b�㐅�������̈ʒu
					int yt = (int)(V0y[n] * Time[n]
									- 9.8/2*Time[n]*Time[n]);	// t�b�㐂�������̈ʒu
					X[n] = XP + xt;								// t�b��̈ʒu
					Y[n] = YP - yt;
					WorkGraphics.setColor(Color.white);
					WorkGraphics.fillRect(X[n], Y[n], 2, 2);
				}
				else {
					Process[n] = 0;								// ���̒i�K�ɃZ�b�g
				}
			}
			else {
				// �ŏ��������ɔ�яo���悤�ɏ���
				Process[n]++;
				if (Process[n] > MAX)
					Process[n] = 0;					// �n�߂ď����i�K�Ƃ���
			}
		}
	}
}
 