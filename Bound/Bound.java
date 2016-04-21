import java.applet.*;	// Applet
import java.awt.*;		// Graphics, Image, Color, MediaTracker, Button, Label, Scrollbar
import java.awt.event.*;// ActionListener, AdjustmentListener, ActionEvent, AdjustmentEvent
						// ItemListener, ItemEvent

public class Bound extends Applet 
		implements Runnable, ActionListener, AdjustmentListener, ItemListener {
 		// Runnable, ActionListener, AdjustmentListener�CItemListener�C���^�[�t�F�[�X����
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	Image Ball;										// �{�[���C���[�W
	Button StartButton;								// ���˃X�^�[�g�{�^��
	Scrollbar SpeedBar, AngleBar;					// �X�s�[�h�o�[�C�p�x�o�[
	Checkbox LocusCheckbox;							// �O�Ճ`�F�b�N�{�b�N�X
	int AppletWidth, AppletHeight;					// �A�v���b�g�̕��ƍ���
	int BallRadius;									// �{�[���̔��a
	double Speed = 60;								// ���ˑ��x	�f�t�H���g60
	double Angle = 80;								// ���ˊp�x �f�t�H���g80�x
	Thread thread;									// �X���b�h

	double t;										// ����
	double BasePoint;								// �o�E���h�̊�_
	double Vx, Vy;									// �����������x�C�����������x
	double PreVy;									// �O��̐����������x
	double X, Y, PreX, PreY;						// �{�[���̈ʒu�C�O��̈ʒu
	double E;										// ���˂�����W��

	boolean FireSw = false;							// ���˃X�C�b�`

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		ScreenClear( );								// ��ʃN���A

		MediaTracker  mt = new MediaTracker(this);	// ���f�B�A�g���b�J����
		Ball = getImage(getCodeBase( ), "image/ball.gif");
		mt.addImage(Ball, 0);						// ���f�B�A�g���b�J�ɃZ�b�g
		try {
			mt.waitForID(0);						// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);					// ��O�G���[�\��
		}
		// �摜�̓��͂�����������ŁC�T�C�Y�𒲂ׂ�
		BallRadius = Ball.getWidth(this) / 2;		// �{�[���̔��a
		X = Y = BallRadius;							// �{�[���̈ʒu

		StartButton = new Button("START");			// �X�^�[�g�{�^������
		add(StartButton);							// �{�^�����A�v���b�g�ɕt��
		StartButton.addActionListener(this);		// �{�^���Ƀ��X�i�[�t��

		Label LabelSpeed = new Label("���x");
		Label LabelAngle = new Label("�p�x");

		// �X�N���[���o�[����
		SpeedBar = new Scrollbar(Scrollbar.HORIZONTAL, (int)Speed, 0, 0, 100);
		AngleBar = new Scrollbar(Scrollbar.HORIZONTAL, (int)Angle, 0, 0, 90);
		SpeedBar.addAdjustmentListener(this);		// �o�[�Ƀ��X�i�[�t��
		AngleBar.addAdjustmentListener(this);
		add(LabelSpeed);							// ���x�����A�v���b�g�ɕt��
		add(SpeedBar);								// �X�s�[�h�o�[���A�v���b�g�ɕt��
		add(LabelAngle);
		add(AngleBar);
		// �O�Ճ`�F�b�N�{�b�N�X
		LocusCheckbox = new Checkbox("�O��");		// �O�Ճ{�b�N�X�I�u�W�F�N�g����
		add(LocusCheckbox);							// �O�Ճ`�F�b�N�{�b�N�X��t��
		LocusCheckbox.addItemListener(this);		// �O�Ճ`�F�b�N�{�b�N�X�Ƀ��X�i�[�ǉ� 
		LocusCheckbox.setState(false);				// �O�Ճ`�F�b�N�{�b�N�X�I�t

		setLayout(null);							// ���C�A�E�g�����R�ݒ�
		StartButton.setBounds(10, 10, 50, 20);		// �{�^���̈ʒu�ƃT�C�Y���Đݒ�
		LabelSpeed.setBounds(70, 10, 30, 20);		// ���x���̈ʒu�ƃT�C�Y���Đݒ�
		SpeedBar.setBounds(100, 10, 100, 20);		// �X�s�[�h�o�[�̈ʒu�ƃT�C�Y���Đݒ�
		LabelAngle.setBounds(210, 10, 30, 20);		// ���x���̈ʒu�ƃT�C�Y���Đݒ�
		AngleBar.setBounds(240, 10, 100, 20);		// �p�x�̈ʒu�ƃT�C�Y���Đݒ�
		LocusCheckbox.setBounds(350, 10, 48, 20);	// �O�Ճ`�F�b�N�{�b�N�X�̈ʒu�ƃT�C�Y

		repaint( );
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// ��ʃN���A -----------------------------------------------------------------------
	public void ScreenClear( ) {
		WorkGraphics.setColor(Color.white);
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		WorkGraphics.setColor(Color.black);
		WorkGraphics.drawRect(0, 0, AppletWidth-1, AppletHeight-1);
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (LocusCheckbox.getState( ) == false)		// �O�Ճ`�F�b�N���I�t�̏ꍇ
			ScreenClear( );							// ��ʃN���A
		// �{�[����`��
		WorkGraphics.drawImage(Ball, (int)X - BallRadius,
						(int)(AppletHeight - Y - BallRadius), this);
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while(thread != null) {						// �X���b�h�����݂��Ă����
			try {
				thread.sleep(50);					// �X���b�h���w��~���b�X���[�v
			} catch (InterruptedException ex) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+ex);
			}
			if (FireSw == true) {					// ���˃{�^���@�I���̏ꍇ
				t = t + 0.1;						// ���Ԃ�i�߂�
				X = Vx * t + BasePoint;						// t���Ԍ��x�����ʒu
				Y = Vy * t - 9.8 / 2 * t * t + BallRadius;	// t���Ԍ��y�����ʒu

				if(Y > BallRadius) {				// �{�[���̒��S���{�[���̔��a����
					PreX = X;  PreY = Y;			// ����̈ʒu��O��̈ʒu�Ƃ��ĕۊ�
				}
				else {
					Vy = (Vy - 9.8 * t) * E * (-1);	// �o�E���h���̏����x
					t = 0;							// �V������s�̂��߂Ɏ��Ԃ��N���A
					BasePoint = X;					// ���̕����^���̊�_

					if(Math.abs(PreVy - Vy) > 0.1)	// �͂��ގ��̑��x�`�F�b�N
						PreVy = Vy;
					else							// �w�肵���l�����������Ȃ����ꍇ
						FireSw = false;				// ��~�@���˃X�C�b�`�@�I�t
				}

				if (X > AppletWidth + BallRadius)	// �A�v���b�g�̉E�[����o���ꍇ
					FireSw = false;					// ��~�@���˃X�C�b�`�@�I�t
				if (FireSw == false) {
					X = Y = BallRadius;
					try {
						thread.sleep(3000);				// �X���b�h���w��~���b�X���[�v
					} catch (InterruptedException ex) {	// ���̃X���b�h�̊��荞�ݗ�O����
						showStatus(" "+ex);
					}
				}
			}
			repaint( );
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		thread = null;								// �X���b�h�𖳌���
	}
 	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �{�^���A�N�V����
		Button button = (Button)evt.getSource( );
		if(button == StartButton) { 				// ���˃X�^�[�g�{�^���̏ꍇ
			t = 0;									// ����
			BasePoint = BallRadius;					// �o�E���h�̊�_
			Vx = Speed * Math.cos(Angle * 3.14 / 180);	// �����������x
			Vy = Speed * Math.sin(Angle * 3.14 / 180);	// �����������x
			PreVy = 0;								// �O��̐����������x���ݒ�
			E = 0.8;								// ���˕Ԃ�W��
			FireSw = true;							// ���˃X�C�b�`���I��
			ScreenClear( );							// ��ʃN���A
		}
	}
	// AdjustmentListener�C���^�[�t�F�[�X�̃��\�b�h��` ---------------------------------
	public void adjustmentValueChanged(AdjustmentEvent evt) {	// �o�[�̕ω��L���b�`
		Scrollbar scrollbar = (Scrollbar)evt.getSource( );
		if (scrollbar == SpeedBar)					// �X�s�[�h�o�[�̏ꍇ
			Speed = (double)SpeedBar.getValue( );	// �X�s�[�h�o�[�̒l�擾
		else if (scrollbar == AngleBar)				// �p�x�o�[�̏ꍇ
			Angle = (double)AngleBar.getValue( );	// �p�x�o�[�̒l���擾

		showStatus("Speed=" + Speed +   " Angle=" + Angle);
	}
	// ItemListener�C���^�[�t�F�[�X�̃��\�b�h���`
	public void itemStateChanged(ItemEvent evt) {	// ���ڕύX�C�x���g 
	}
}
