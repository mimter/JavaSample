import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color

public class Hanabi extends Applet implements Runnable{
    Thread thread = null;							// �X���b�h
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int sleeptime = 30;								// �X���[�v�^�C���i���s�X�s�[�h)

	int AngleRange = 50;							// �ł��グ�p�x�i���������ɑ΂��āj
	int SpeedMax = 90;								// �ł��グ�X�s�[�h
	int ExplosionSpeed = 50;						// �����X�s�[�h
	int XP, YP;										// ���˂̈ʒu
	int HMAX = 30;									// �ԉ΂̐�
	int TMAX = 200;									// �ԉ΂̒��̋ʐ�
	int LEN = 4;									// �΂̒���
	int SIZE = 1;									// �ʂ̃T�C�Y
	int COUNT = 30;									// �ԉ΂̔������ԃJ�E���g
	int X[ ][ ] = new int[HMAX][TMAX];				// �ԉ΂̋ʂ̈ʒu
	int Y[ ][ ] = new int[HMAX][TMAX];
	double Angle[ ][ ] = new double[HMAX][TMAX];	// ���ˊp�x�i���W�A��) 
	double Speed[ ][ ] = new double[HMAX][TMAX];	// ���˃X�s�[�h
	double V0x[ ][ ] = new double[HMAX][TMAX];		// ���������̏����x	
	double V0y[ ][ ] = new double[HMAX][TMAX];		// ���������̏����x
	int Process[ ] = new int[HMAX];					// �ԉ΂̃v���Z�X�i�����C��s�C�����j
	int Time[ ] = new int[HMAX];					// ���ԃJ�E���g
	float Hue[ ] = new float[HMAX];					// �ԉ΂̐F
	double Rad = Math.PI / 180;						// ���W�A��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;								// �A�v���b�g�̃T�C�Y
		AppletHeight = getSize( ).height;
		WorkImage = createImage(AppletWidth, AppletHeight);			// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );					// ��Ɨp�O���t�B�b�N�X�擾
		WorkGraphics.setColor(Color.black);							// �`��F�����F�ɐݒ�
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);		// ��ʃN���A

		// �e��p�����[�^����
		AngleRange = Integer.parseInt(getParameter("anglerange"));	// �������ˊp�x��
		SpeedMax = Integer.parseInt(getParameter("speedmax"));		// ���˃X�s�[�h
		HMAX = Integer.parseInt(getParameter("max"));				// ���ː���
		if (HMAX > 30)
			HMAX = 30;

		// �ԉ΃v���Z�X��0�N���A�i���˂��Ă��Ȃ��i�K)
		for (int i = 0; i < HMAX; i++)
			Process[i] = 0;							// 0:�����C1:���ˁC2:�����C3�`30����
		// ���ˈʒu
		XP = AppletWidth / 2;
		YP = AppletHeight;
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
    public void start( ){
        thread = new Thread(this);					// �X���b�h����
        thread.start( );							// �X���b�h�J�n
    }
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g){
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ){
        while(thread != null){						// �X���b�h�����݂��Ă����
			MakingHanabi( );						// �ԉ΍쐬
            try{
               thread.sleep(sleeptime);				// �w��~���b�X���b�h�X���[�v
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
	// �ԉ΍쐬 -------------------------------------------------------------------------
	void MakingHanabi( ) {
		for (int n = 0; n < HMAX; n++) {
			if (Process[n] == 0) {					// �ԉ΂̏����i�K
				Angle[n][0] = 90 - AngleRange/2 + (int)(Math.random( )*AngleRange);
																		// ���ˊp�x
				Speed[n][0] = SpeedMax + (int)(Math.random( )*20);		// �ԉ΂̃X�s�[�h
				X[n][0] = AppletWidth / 2;								// �ʒu
				Y[n][0] = AppletHeight;
				V0x[n][0] = Speed[n][0] * Math.cos(Angle[n][0] * Rad);	// �����xx
				V0y[n][0] = Speed[n][0] * Math.sin(Angle[n][0] * Rad);	// �����xy
				Time[n] = 0;											// ���ԃJ�E���g
				Process[n] = 1;											// ���˒i�K
				Hue[n] = (float)Math.random( );							// �ԉ΂̐F
			}
			else if (Process[n] == 1) {					// �ԉΔ�s�i�K
				double nowtime = Time[n] / 10.0;		// ���ԃJ�E���g�����ԂɕύX
				if(V0y[n][0] - 9.8 * nowtime >= 10.0) {	// �����X�s�[�h��10�ȏ�̏ꍇ
					WorkGraphics.setColor(Color.black);	// �O��ʒu����
					WorkGraphics.drawLine(X[n][0], Y[n][0], X[n][0], Y[n][0]);
					Time[n] += 1;						// ���ԃJ�E���g
					nowtime = Time[n] / 10.0;			// ���ԃJ�E���g�����ԂɕύX
					int xt = (int)(V0x[n][0] * nowtime);
					int yt = (int)(V0y[n][0] * nowtime - 9.8/2 * nowtime * nowtime);
					X[n][0] = XP + xt;					// ���ˈʒu����̔�s�������Z
					Y[n][0] = YP - yt;
					WorkGraphics.setColor(Color.white);	// �ԉΕ`��
					WorkGraphics.fillRect(X[n][0], Y[n][0], 1, 1);
				}
				else {				// �����X�s�[�h��10���x���Ȃ����ꍇ�C�����Z�b�g
					WorkGraphics.setColor(Color.black);	// �O��ʒu����
					WorkGraphics.drawLine(X[n][0], Y[n][0], X[n][0], Y[n][0]);
					Process[n] = 2;						// �����i�K�ɃZ�b�g
					Time[n] = 0;						// �����X�^�[�g�@���ԃN���A
					for (int i = 1; i < TMAX; i++) {	// �e�ʂ̐ݒ�
						Angle[n][i] = (int)(Math.random( ) * 360);			// ���ˊp�x
						Speed[n][i] = (int)(ExplosionSpeed * Math.random( ));// �X�s�[�h
						X[n][i] = X[n][0];									// �ʒu
						Y[n][i] = Y[n][0];
						V0x[n][i] = Speed[n][i] * Math.cos(Angle[n][i] * Rad);// �����xx
						V0y[n][i] = Speed[n][i] * Math.sin(Angle[n][i] * Rad);// �����xy
					}
				}
			}
			else if (Process[n] >= 2) {					// �����i�K
				Time[n] += 1;							// ���ԃJ�E���g

				for (int w = 1; w < TMAX; w++) {		// �ʂ̐���������
					if (Time[n] >= LEN) {				// �΂̒������O�̈ʒu������
						double backtime = (Time[n] - LEN) / 10.0;	// �΂̒������O�̎���
						WorkGraphics.setColor(Color.black);
						WorkGraphics.fillRect(X[n][0] + (int)(V0x[n][w] * backtime),
						 Y[n][0] - (int)(V0y[n][w] * backtime - 9.8/2 * backtime * backtime),
						 SIZE, SIZE);
					}
					double nowtime = Time[n] / 10.0;	// ���ԃJ�E���g�����Ԃɕϊ�
					int xt = (int)(V0x[n][w] * nowtime);// �ʒu�v�Z
					int yt = (int)(V0y[n][w] * nowtime - 9.8/2 * nowtime * nowtime);
					X[n][w] = X[n][0] + xt;				// �����ʒu����̔�s�������Z
					Y[n][w] = Y[n][0] - yt;
					if (COUNT - Process[n] > LEN)	{	// �ݒ肵���΂̒������傫���ꍇ
						if (Process[n] < COUNT / 2)		// �w��񐔂̔����܂ł͍ő喾�x
							WorkGraphics.setColor(Color.getHSBColor(Hue[n], 1f, 1f));
						else							// �㔼�͖��x�����X�ɗ��Ƃ�
							WorkGraphics.setColor(Color.getHSBColor(Hue[n], 1f,
								1f * (COUNT - Process[n]) / (COUNT / 2)));
					}
					else
						WorkGraphics.setColor(Color.getHSBColor(Hue[n], 1f, 0));// ���F
					WorkGraphics.fillRect(X[n][w], Y[n][w],	SIZE, SIZE);	// �΂�`��
				}
				Process[n]++;						// �v���Z�X��ω�
				if (Process[n] > COUNT)				// �w��i�K�𒴂����ꍇ
					Process[n] = 0;					// �ŏ��̒i�K�ɖ߂�
			}
		}
	}
}
