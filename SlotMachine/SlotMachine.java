import java.applet.*;			// Applet, AudioClip
import java.awt.*;				// Graphics, Image, Color, MediaTracker, Button, Panel etc
import java.awt.event.*;		// ActionListener, ActionEvent

public class SlotMachine extends Applet implements Runnable, ActionListener {
								// Runnable ��ActionListener�C���^�[�t�F�[�X����
	CanvasWindows CanvasWindow[ ];					// �\���L�����o�X
	Button StopButton1, StopButton2, StopButton3;	// �X�g�b�v�{�^��
	Button StartButton;								// �X�^�[�g�{�^��
	int SlotMax = 5;								// 5��ނ̃p�l��
	Image image[ ] = new Image[SlotMax];			// �p�l���C���[�W
	int imageWidth, imageHeight;					// �C���[�W�̃T�C�Y
	Image panelImage;								// �p�l���C���[�W
	Graphics panelGraphics;							// �p�l���O���t�B�b�N
	Thread thread;									// �X���b�h
	boolean RotateFlag;								// ��]�t���O
	int Y[ ] = new int[3];							// ���������\���ʒu
	int Step[ ] = {30, 30, 30};						// �X�N���[���X�e�b�v
	int StopButton[ ] = {0, 0, 0};					// �X�g�b�v�{�^��
	AudioClip ClickSound, SeikaiSound;				// �N���b�N�T�E���h�C�����T�E���h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
    	ClickSound = getAudioClip(getCodeBase( ), "sound/click.au");	// �N���b�N��
    	SeikaiSound = getAudioClip(getCodeBase( ), "sound/seikai.au");	// ������

		setLayout(null);							// �R���|�[�l���g�̔z�u�����R�ݒ�

		Panel DisplayPanel = new Panel( );
		DisplayPanel.setLayout(new GridLayout(1, 3));

		// �摜���͂��Ď����郁�f�B�A�g���b�J�[�𐶐�
		MediaTracker mediatracker = new MediaTracker(this);
		for (int i = 0; i < SlotMax; i++) {
			image[i] = getImage(getCodeBase( ), "image/slot"+i+".gif");
			mediatracker.addImage(image[i], 0);		// �摜�����f�B�A�g���b�J�ɃZ�b�g
		}
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch (InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" "+e);
		}

		// �摜�̓��͂�����������ŁC�T�C�Y�𒲂ׂ�
		imageWidth = image[0].getWidth(this);		// �C���[�W�̕��ƍ���
		imageHeight = image[0].getHeight(this);

		// �`��p�l���C���[�W
		panelImage = createImage(imageWidth, imageHeight*(SlotMax+1));
		panelGraphics = panelImage.getGraphics( );
		panelGraphics.setColor(Color.black);
		for (int i = 0; i < SlotMax+1; i++) {		// 1���]���ɍ쐬
			if (i < SlotMax)
				panelGraphics.drawImage(image[i], 0, i*imageHeight, this);
			else									// ���X�g���ŏ��̃p�l���Ƃ���
				panelGraphics.drawImage(image[0], 0, i*imageHeight, this);
			panelGraphics.drawRect(0, i*imageHeight,
						imageWidth-1, imageHeight);
		}

		// �N���X�^�f�[�^�̔z��
		CanvasWindow = new CanvasWindows[3];
		for (int i = 0; i < 3; i++) {
			// �L�����o�X����
			CanvasWindow[i] = new CanvasWindows(panelImage);
			CanvasWindow[i].setSize(imageWidth, imageHeight);	// �L�����o�X�̃T�C�Y�ݒ�
			DisplayPanel.add(CanvasWindow[i]);		// �L�����o�X���p�l���ɕt��
			Y[i] = -i * imageHeight;				// �p�l���̏����ʒu
		}

		Panel StopPanel = new Panel( );				// �X�g�b�v�p�l������
		StopPanel.setLayout(new GridLayout(1, 3)); 	// �X�g�b�v�p�l���̃��C�A�E�g�ݒ�
		StopButton1 = new Button("Stop1");			// �X�g�b�v�{�^������
		StopButton2 = new Button("Stop2");
		StopButton3 = new Button("Stop3");
		StopPanel.add(StopButton1);					// �X�g�b�v�{�^����t��
		StopPanel.add(StopButton2);
		StopPanel.add(StopButton3);

		StartButton = new Button("Start");			// �X�^�[�g�{�^������

		add(DisplayPanel);							// �f�B�X�v���C�p�l�����A�v���b�g�t��
		add(StopPanel);								// �X�g�b�v�p�l�����A�v���b�g�ɕt��
  		add(StartButton);							// �X�^�[�g�{�^�����A�v���b�g�ɕt��

		// �f�B�X�v���C�p�l���̐ݒ�
		DisplayPanel.setBounds(0, 0, imageWidth*3, imageHeight);
		// �X�g�b�v�p�l���̐ݒ�
		StopPanel.setBounds(0, imageHeight, imageWidth*3, imageHeight/2);
		// �X�^�[�g�{�^���̐ݒ�
		StartButton.setBounds(0,imageHeight+imageHeight/2,imageWidth*3,imageHeight/2); 
		StopButton1.addActionListener(this);		// �{�^���Ƀ��X�i�[�Z�b�g
		StopButton2.addActionListener(this);
		StopButton3.addActionListener(this);
		StartButton.addActionListener(this);
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		// ����C�A�v���b�g��ɒ��ڕ`�悷����̂͂Ȃ��B���ׂăL�����o�X���ŕ`��B
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		RotateFlag = true;
		while (thread != null) {					// �X���b�h�����݂��Ă����
			while(RotateFlag == true) {				// ��]���Ă����
				for (int i = 0; i < 3; i++) {
					if (Y[i] <= -(SlotMax-1) * imageHeight)
						Y[i] = 0;
					if (Step[i] != 0) {
						CanvasWindow[i].DispPanel(Y[i]);	// �p�l���ʒu�ݒ�E�`��
						Y[i] -= Step[i];					// ���_�̂����W����Ɉړ�
					}
					if (StopButton[i] == 1 && Step[i] > 1)	// �X�g�b�v�{�^��������
						Step[i]--;							// ��]�ړ��X�e�b�v����
					if (Step[i] == 1 && (Y[i] % imageHeight) == 0)
						Step[i] = 0;						// ��؂̂Ƃ���ŃX�g�b�v
				}
				if (Step[0]+Step[1]+Step[2] == 0) {	// �O�̃p�l������~
					RotateFlag = false;				// ��~
					// �����`�F�b�N
				    if (Y[0] == Y[1] && Y[1] == Y[2])
						SeikaiSound.play( );
				}
				try {
					thread.sleep(100);				// �X���b�h��100�~���b�X���[�v
				} catch (InterruptedException e) {	// ���̃X���b�h�̊��荞�ݗ�O����
					showStatus(" "+e);
				}
			}
			try {
				thread.sleep(300);					// �X���b�h��300�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				showStatus(" "+e);
			}
		}
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	// ����C�A�v���b�g����repaint���Ȃ����߁C���̃��\�b�h�͕K�v�Ȃ�
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �A�v���b�g�I�� -------------------------------------------------------------------
	public void stop( )	{
		thread = null;								// �X���b�h�𖳌�
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �C�x���g����
		Button button = (Button)evt.getSource( );
		String label = button.getLabel( );
		ClickSound.play( );
		if (label.equals("Stop1"))					// Stop1�������ꂽ�ꍇ
			StopButton[0] = 1;
		else if (label.equals("Stop2"))				// Stop2�������ꂽ�ꍇ
			StopButton[1] = 1;
		else if (label.equals("Stop3"))				// Stop3�������ꂽ�ꍇ
			StopButton[2] = 1;
		else if (label.equals("Start")) {			// Start�{�^���������ꂽ�ꍇ
			for (int i = 0; i < 3; i++) {
				StopButton[i] = 0;					// �e�X�g�b�v�{�^���N���A
				Step[i] = 30;						// ��]�X�e�b�v��30
				RotateFlag = true;					// ��]�t���O�I��
			}
		}
	}
}
// ======================================================================================
// �L�����o�X�E�B���h�E�N���X
class CanvasWindows extends Canvas {
	Image image;
	int Y;
	// �R���X�g���N�^ -------------------------------------------------------------------
	public CanvasWindows(Image image) {
		this.image = image;
	}
	// �p�l���`�� -----------------------------------------------------------------------
	public void DispPanel(int Y) {
		this.Y = Y;
		repaint( );
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(image, 0, Y, this);
	}
}
