import java.applet.*;										// Applet
import java.awt.*;											// Graphics, Image, Color
import java.util.*;											// Calendar, TimeZone
import java.lang.Math;										// sin, cos

public class AnalogClock extends Applet 
						 implements Runnable {
	Thread thread = null;									// �X���b�h�錾
	Image WorkImage;										// ��Ɨp�C���[�W
	Graphics WorkGraphics;									// ��Ɨp�O���t�B�b�N�X
	int CenterX, CenterY;									// ���v�̒��S
	int Radius;												// ���v�̔��a
	int AppletWidth, AppletHeight;							// �A�v���b�g�̕��ƍ���

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕��ƍ���
		AppletHeight = getSize( ).height;

		WorkImage = createImage(AppletWidth, AppletHeight); // ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		// ���v�̈ʒu�Ɣ��a
		CenterX = AppletWidth / 2;
		CenterY = AppletHeight / 2;
		if (AppletWidth < AppletHeight)						// �c���C�Z���������v�̔��a
			Radius = (int)(AppletWidth / 2 * 0.8);			// ���v�̔��a
		else
			Radius = (int)(AppletHeight / 2 * 0.8);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);							// �X���b�h����
		thread.start( );									// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);					// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
        while (thread != null) {							// �X���b�h�����݂��Ă����
			DispTime( );									// ���ԕ`��
			repaint( );  									// �ĕ`��
			try{
				thread.sleep(100);							// �X���[�v
			} catch(InterruptedException e) {				// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);							// �u���E�U�̃X�e�[�^�X�o�[�ɕ\��
			}
		}
	}
	// �`��X�V�����Ē�` --------------------------------------------------------------
	public void update(Graphics g) {						// �f�t�H���g��update���Ē�`
		paint(g);											// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// ���ԕ`�� -------------------------------------------------------------------------
	void DispTime( ) {
		// ��ƃO���t�B�b�N�X�ɕ`��
		WorkGraphics.setColor(Color.white);
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

		// �p�x��6�x�Âi60����360�x�C1����6�x)
 		for (int kakudo = 0; kakudo < 360; kakudo += 6) {
			// �ڐ������ŕ\��
			double RD = kakudo * Math.PI / 180;				// �p�x�����W�A���ɕϊ�
			int x1 = CenterX + (int)(Math.sin(RD) * Radius);// �ڐ���̊O���̓_�̈ʒu
			int y1 = CenterY - (int)(Math.cos(RD) * Radius);
			int radius2;
			if (kakudo % 30 == 0)							// 5������
	 			radius2 = Radius - 8;						// ����8�̖ڐ���
			else
	 			radius2 = Radius;							// ����0�̖ڐ���i�܂�_)

			int x2 = CenterX + (int)(Math.sin(RD) * radius2);	// �ڐ���̓����̓_�̈ʒu
			int y2 = CenterY - (int)(Math.cos(RD) * radius2);
			WorkGraphics.setColor(Color.black);
	 		WorkGraphics.drawLine(x1, y1, x2, y2);			// �ڐ��胉�C��������
		}

		// ���݂̎���
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("JST"));
		int hour = date.get(Calendar.HOUR);					// ���ݎ����̎��Ԏ擾
		int minute = date.get(Calendar.MINUTE);				// ���ݎ����̕��擾
		int second = date.get(Calendar.SECOND);				// ���ݎ����̕b�擾

		// ���Ԃ̐j�`��
		MakeHand(CenterX, CenterY,
				 hour * 30 + minute / 60.0 * 30, Radius * 0.8, 1.2, Color.black);
		// ���̐j�`��
		MakeHand(CenterX, CenterY, minute * 6.0, Radius * 0.9, 0.7, Color.gray);

		// �b�̐j�`��
		double RD = second * 6 * Math.PI / 180;					// �b�̊p�x�����W�A���ɕϊ�
		int sx = CenterX + (int)(Math.sin(RD) * Radius * 0.9);	// �b�̐j�̐�[�̈ʒu
		int sy = CenterY - (int)(Math.cos(RD) * Radius * 0.9);
		WorkGraphics.setColor(Color.red);
		WorkGraphics.drawLine(CenterX, CenterY, sx, sy);
	}
	//  �����j��`�悷�郁�\�b�h --------------------------------------------------------
	void MakeHand(int cx, int cy,
				  double kakudo, double nagasa, double hutosa, Color color) {
		int px[ ] = new int[4];
		int py[ ] = new int[4];
		double RD = kakudo * Math.PI / 180;
		px[0] = cx + (int)(Math.sin(RD) * nagasa);
		py[0] = cy - (int)(Math.cos(RD) * nagasa);
		for (int i = 1; i < 4; i++) {
			RD = (kakudo + i * 90) * Math.PI / 180;
			px[i] = cx + (int)(Math.sin(RD) * nagasa * hutosa / 10);
			py[i] = cy - (int)(Math.cos(RD) * nagasa * hutosa / 10);
		}
		WorkGraphics.setColor(color);
		WorkGraphics.fillPolygon(px, py, 4);
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( )	{
		thread = null;										// �X���b�h����
	}
}
