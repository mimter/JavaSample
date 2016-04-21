import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, Color
import java.awt.event.*;							// MouseListener, MouseEvent

public class Kingyo extends Applet implements Runnable, MouseListener {
	Image KingyoImage[ ] = new Image[16];			// 16�����̃C���[�W

	Thread thread = null;							// �X���b�h
	int Number = 50;								// ������
	int Direction[ ] = new int[Number];				// �e�����̕���
	int X[ ] = new int[Number];						// �e�����̈ʒu
	int Y[ ] = new int[Number];
	int MoveRadius = 4;								// �ړ����a

	int FoodNumber = 10;							// �a�̐�
	int Food[ ] = new int[FoodNumber];				// �a 0:���݂��Ȃ�,1:���݂���
	int FoodX[ ] = new int[FoodNumber];				// �a�̈ʒu
	int FoodY[ ] = new int[FoodNumber];

	int KingyoWidth, KingyoHeight;					// �C���[�W�̃T�C�Y
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y

	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// �p�����[�^�������
		Number = Integer.parseInt(getParameter("number"));	// �����̐�
		if (Number > 50)									// �ő吔�`�F�b�N
			Number = 50;

		MediaTracker  mt = new MediaTracker(this);			// ���f�B�A�g���b�J��`

		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���
		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		for (int i = 0; i < 16; i++) {						// ������16�����̉摜����
			KingyoImage[i] = getImage(getCodeBase( ), "image/kingyo"+(i+1)+".gif");
			mt.addImage(KingyoImage[i], 0);					// ���f�B�A�g���b�J�ɃC���[�W�Z�b�g
		}

		try {
			mt.waitForID(0);								// �C���[�W�摜�̓��͊�����҂�
		} catch(InterruptedException e) {
			showStatus(" "+e);
		}

		KingyoWidth = KingyoImage[0].getWidth(this);		// �����̕��ƍ���
		KingyoHeight = KingyoImage[0].getHeight(this);

		// �����̏�����
		for (int i = 0; i < Number; i++) {
			Direction[i] = (int)(Math.random( ) * 16);						// ����
			X[i] = (int)(Math.random( ) * (AppletWidth - KingyoWidth));		// �ʒu
			Y[i] = (int)(Math.random( ) * (AppletHeight - KingyoHeight));
		}
		// �a�̏�����
		for (int i = 0; i < FoodNumber; i++)
			Food[i] = 0;									// �a�������@�Ȃ����

		addMouseListener(this);								// �}�E�X���X�i�[�ǉ�
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
			Swimming( );							// �j������
			repaint( );								// �ĕ`��
			try {
				Thread.sleep(100);					// �X���b�h�X���[�v
			} catch (InterruptedException e){
				showStatus(" "+e);
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h�𖳌�
	}
	// �j������ -------------------------------------------------------------------------
	void Swimming( ) {
		WorkGraphics.setColor(Color.white);
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

		// �a����
		WorkGraphics.setColor(Color.black);
		for (int i = 0; i < FoodNumber; i++) {
			if (Food[i] == 1)
				WorkGraphics.fillOval(FoodX[i], FoodY[i], 3, 3);	// �a�\��
		}

		// ��������
		for (int i = 0; i < Number; i++) {

			// ���������݌����Ă�������ɐi�߂�     (3.14 * 2 / 16)  360�x��16����
			double radian = Direction[i] * 3.14 * 2 / 16;			// �����̕����p�x
			int xp = X[i] + (int)(Math.sin(radian) * MoveRadius);	// �����̈ʒu
			int yp = Y[i] + (int)(-Math.cos(radian) * MoveRadius);
			// ��O�ɏo�Ȃ��悤�Ƀ`�F�b�N
			if (xp >= 0 && xp <= AppletWidth - KingyoWidth &&
				yp >= 0 && yp <= AppletHeight - KingyoHeight) {
				X[i] = xp;
				Y[i] = yp;
			}

			WorkGraphics.drawImage(KingyoImage[Direction[i]], xp, yp, this);

			// �܂��a�����邩�`�F�b�N
			int foodFlag = 0;
			for (int e = 0; e < FoodNumber; e++) {
				if (Food[e] == 1)
					foodFlag = 1;					// �a������
			}

			// ���̕����Z�b�g
			if (foodFlag == 0) {
				// �a���Ȃ��ꍇ�C�����𔭐����āC�O�F���C�Por�Q�F�܂������C�R�F�E
				int w = (int)(Math.random( )*4);
				if (w == 0)
					Direction[i]--;
				if (w == 3)
					Direction[i]++;
				if (Direction[i] < 0)
					Direction[i] = 15;
				if (Direction[i] > 15)
					Direction[i] = 0;
			}
			else {
				// �a������ꍇ
				// �����̌��݂̏ꏊ����ł��߂��a��T��
				int minDistance = 9999;
				int nearPoint = -1;
				int distance;
				for (int p = 0; p < FoodNumber; p++) {
					if (Food[p] == 1) {
						double temp = (X[i] - FoodX[p]) * (X[i] - FoodX[p])
									+ (Y[i] - FoodY[p]) * (Y[i] - FoodY[p]);
						if (temp > 0)	// ���̏ꍇ�́C���s���v�Z
							distance = (int)(Math.sqrt(temp));
						else
							distance = 0;
						if (minDistance > distance) {
							minDistance = distance;
							nearPoint = p;
						}
					}
				}

				// ���C�܂������C�E�̂ǂ���ɍs���������������Z���Ȃ邩���ׂ�
				minDistance = 9999;
				int bestDirection = -1;
				int tempDirection = -1;
				for (int p = -1; p <= 1; p++) {
					tempDirection = Direction[i] + p;
					if (tempDirection < 0)
						tempDirection = 15;
					if (tempDirection > 15)
						tempDirection = 0;
					radian = tempDirection * 3.14 * 2 / 16;		// �����̕����p�x
												// (3.14 * 2 / 16)��360�x��16���������l
					xp = X[i] + (int)(Math.sin(radian) * MoveRadius);
					yp = Y[i] + (int)(-Math.cos(radian) * MoveRadius);
					if (xp >= 0 && xp <= AppletWidth - KingyoWidth &&
						yp >= 0 && yp <= AppletHeight - KingyoHeight) {

						double temp = (xp - FoodX[nearPoint]) * (xp - FoodX[nearPoint])
									+ (yp - FoodY[nearPoint]) * (yp - FoodY[nearPoint]);
						if (temp > 0)	// ���̏ꍇ�́C���s���v�Z
							distance = (int)(Math.sqrt(temp));
						else
							distance = 0;
						if (minDistance > distance) {
							minDistance = distance;
							bestDirection = tempDirection;
						}
					}
				}
				if (bestDirection != -1)			// �ŒZ�������������ꍇ
					Direction[i] = bestDirection;
				else
					Direction[i] = tempDirection;

				// �a�������̉摜���̏ꍇ�C�a���폜
				if (X[i] <= FoodX[nearPoint] && FoodX[nearPoint] <= X[i] + KingyoWidth &&
					Y[i] <= FoodY[nearPoint] && FoodY[nearPoint] <= Y[i] + KingyoHeight)
					Food[nearPoint] = 0;
			}
		}
	}
	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
    public void mousePressed(MouseEvent evt) {
       	int mouseX = evt.getX( );					// �}�E�X�̈ʒu�ۊ�
		int	mouseY = evt.getY( );

		// �a���܂�
		for (int i = 0; i < FoodNumber; i++) {
			if (Food[i] == 0) {
				Food[i] = 1;
				// �a�̈ʒu  �}�E�X���N���b�N�����ʒu�𒆐S�ɔ��a50�͈̔͂Ƀ����_���ɂ܂�
				FoodX[i] = mouseX + (int)(Math.random( ) * 100 - 50);
				FoodY[i] = mouseY + (int)(Math.random( ) * 100 - 50);
				// �~�`�Ƀ����_����
				if (FoodX[i] <= 0 || FoodX[i] >= AppletWidth ||	// �͈͊O�̏ꍇ
					FoodY[i] <= 0 || FoodY[i] >= AppletHeight)
					Food[i] = 0;
			}
		}
	}
    public void mouseClicked(MouseEvent evt) { }
	public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
}
