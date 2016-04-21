import java.applet.*;					// Applet, AudioClip
import java.awt.*;						// Graphics, Image, Color, MediaTracker
import java.awt.event.*;				// MouseListener, MouseMotionListener etc
import java.awt.image.*;				// MemoryImageSource, PixelGrabber

public class PuzzleMatch extends Applet implements MouseListener, MouseMotionListener {
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y

	int Number = 20;								// �s�[�X�̖���
	String FileName;								// �s�[�X�̃t�@�C����
	Image Piece[ ] = new Image[Number];				// �e�s�[�X�̉摜
	int PieceWidth, PieceHeight;					// �s�[�X�̃T�C�Y
	int PieceX[ ] = new int[Number];				// �s�[�X�̈ʒu
	int PieceY[ ] = new int[Number];
	int Priority[ ] = new int[Number];				// �s�[�X�̗D�揇�ʁi�ŉ���0�`11�j
	int ClickPieceNo;								// �N���b�N�����s�[�X�ԍ�
	int ClickDistanceX, ClickDistanceY;				// �N���b�N�ʒu�Ɖ摜�̕`��ʒu�̋���
	String BackgroundName = null;					// �w�i�t�@�C����
	Image Background;								// �w�i�摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	Image CheckImage;								// �`�F�b�N�C���[�W
	Graphics CheckGraphics;							// �`�F�b�N�O���t�B�b�N
	boolean MouseDownSw;							// �}�E�X�_�E���X�C�b�`
	AudioClip ClickSound;							// �N���b�N��
	int Pixels1[ ] = new int[200 * 200];			// �s�N�Z���f�[�^�ۊ�(200x200)
	int Pixels2[ ] = new int[200 * 200 * Number];	// �s�N�Z���f�[�^�ۊ�(200x200xNumber)

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;						// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;					// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		CheckImage = createImage(1, 1);						// �s�N�Z���J���[�l�擾��Ɨ̈�
		CheckGraphics = CheckImage.getGraphics( );			// �`�F�b�N��ƃO���t�B�b�N�X�擾

		// �p�����[�^�擾
		Number = Integer.parseInt(getParameter("number"));	// �s�[�X�̖���
		FileName = getParameter("filename");				// �s�[�X�̖��O
		BackgroundName = getParameter("background");		// �w�i

		// �f�[�^����
        ClickSound = getAudioClip(getCodeBase( ), "sound/click.au");

		// �摜���͂��Ď����郁�f�B�A�g���b�J����
		MediaTracker mediatracker = new MediaTracker(this);
		for (int i = 0; i < Number; i++) {					// �e�s�[�X�̉摜����
			Piece[i] = getImage(getCodeBase( ), FileName+i+".gif");
			mediatracker.addImage(Piece[i], 0);
			Priority[i] = i;								// �摜�ԍ��Z�b�g�@�D�揇�ʐݒ�
		}
		Background = getImage(getCodeBase( ), BackgroundName);	// �g�摜����
		mediatracker.addImage(Background, 0);
		try {
			mediatracker.waitForID(0);						// �摜���͂̊�����҂�
		} catch (InterruptedException e) {					// waitForID�ɑ΂����O����
			showStatus(" " + e);
		}

		PieceWidth = Piece[0].getWidth(this);				// �s�[�X�̕��ƍ���
		PieceHeight = Piece[0].getHeight(this);

		addMouseListener(this);								// �}�E�X���X�i�[�ǉ�
		addMouseMotionListener(this);						// �}�E�X���X�i�[�ǉ�

		MouseDownSw = false;
		Shake( );											// �V�F�C�N
		repaint( );
	}
	// �V�F�C�N���� ---------------------------------------------------------------------
	public void Shake( ) {							// �z�u�ꏊ�������_���ɂ���
		for (int i = 0; i < Number; i++) {
			PieceX[i] = (int)(Math.random( )*(AppletWidth - PieceWidth));
			PieceY[i] = (int)(Math.random( )*(AppletHeight - PieceHeight));
		}
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (BackgroundName == null) {				// �w�i�摜�̎w�肪�Ȃ��ꍇ
			WorkGraphics.setColor(Color.white);		// �`������F�œh��Ԃ�
			WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);
		}
		else {
			WorkGraphics.drawImage(Background, 0, 0, this);		// �g�\��
		}

		// �e�s�[�X��\��
		for (int i = 0; i < Number; i++) {
			int p = Priority[i];					// �D�揇��
			WorkGraphics.drawImage(Piece[p], PieceX[p], PieceY[p], this);
		}
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
	public void mousePressed(MouseEvent evt) {
		int mouseX = evt.getX( );
		int mouseY = evt.getY( );

		// ���̈ʒu�Ƀs�[�X�����邩�ǂ����̃`�F�b�N�C���X�g����s��
		for (int i = Number - 1; i >= 0 && MouseDownSw == false; i--) {
			int p = Priority[i];
			if (mouseX >= PieceX[p] && mouseX < PieceX[p] + PieceWidth 
				&& mouseY >= PieceY[p] && mouseY < PieceY[p] + PieceHeight) {

				// �|�C���g�̃J���[���
				int color = GetPixels(Piece[p], mouseX-PieceX[p], mouseY-PieceY[p]);
				int alpha = (color >> 24) & 0xff; 	// �s�����x�@0:�����`255:�s����

				if (alpha == 0xff) {				// �s�����C�摜������ꍇ
					if ((evt.getModifiers( ) & InputEvent.BUTTON1_MASK) != 0)// ���{�^��
						MouseDownSw = true;			// �h���b�O�̊J�n�X�C�b�`
					// �}�E�X�̈ʒu�Ɖ摜�̕`��ʒu�Ƃ̋���
					ClickDistanceX = mouseX - PieceX[p];
					ClickDistanceY = mouseY - PieceY[p];
					// �`�揇�ʁE�`�F�b�N�D�揇�ʍĐݒ� ----------------------------------
					int tempNo = p;
					for (int n = i + 1; n < Number; n++) {
						Priority[n-1] = Priority[n];
					}
					// �N���b�N�����s�[�X�����X�g�Ɉړ�
					ClickPieceNo = Priority[Number - 1] = tempNo;
					ClickSound.play( );

					// �E�{�^���̏ꍇ�C�摜����]���āC�\���ʒu��ύX
					if ((evt.getModifiers( ) & InputEvent.BUTTON3_MASK) != 0){ 
						// ��]�摜�쐬
						// ���悩��s�N�Z��������荞��
						GetPixelsSetArray(Piece[p], 0, 0,
										  PieceWidth, PieceHeight, Pixels1);

						// �E��]�ɂȂ�悤�Ƀf�[�^�����ւ���
						// ���ꂼ��̉摜�쐬�p�̔z����m�ۂ��Ă����K�v����
						for (int tate = 0; tate < PieceHeight; tate++) {
							for (int yoko = 0; yoko < PieceWidth; yoko++) {
								Pixels2[200 * 200 * p
									 + PieceWidth * yoko + PieceWidth - tate - 1]
									= Pixels1[PieceWidth * tate + yoko];
							}
						}
						// �ҏW���������z��̃s�N�Z����񂩂�C���[�W���쐬
						Piece[p] = createImage(new MemoryImageSource(
							PieceWidth, PieceHeight, Pixels2, 200*200*p, PieceWidth));

						// ��]��̕`��ʒu�Ƃ̋���
						int TempClickDistanceX = ClickDistanceX;
						ClickDistanceX = PieceHeight - ClickDistanceY;
						ClickDistanceY = TempClickDistanceX;

						// ��]��̕`��ʒu
						PieceX[p] = mouseX - ClickDistanceX;
						PieceY[p] = mouseY - ClickDistanceY;
					}
					break;
				}
			}
		}
		repaint( );
	}
	// �摜�C���[�W�̊e�s�N�Z�������擾 -----------------------------------------------
	public void GetPixelsSetArray(Image img, int x, int y, int w, int h, int pixels[]) {
		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
						// (�摜�C���[�W�C�J�n�ʒu���C���C��, �����C�z��C�i�[�ʒu�C����)
        try  {
			pg.grabPixels( );						// �s�N�Z�����捞��
        }  catch  (InterruptedException  e)  {		// grabPixels( )�ɑ΂����O����
            showStatus(" "+e);
        }
	}
	// ----------------------------------------------------------------------------------
	// �摜�C���[�Wimage�̓_(x, y)�̃J���[�l�擾
	public int GetPixels(Image image, int x, int y) {
		// �傫�ȃT�C�Y�̉摜���g���ƁCPixelGrabber�CgrabPixels( )�̏������x���Ȃ�
		// �T�C�Y��1�̃O���t�B�b�N�̈�ɑΏۃ|�C���g���������݁C���̉摜����������
		CheckGraphics.drawImage(image, -x, -y, this);

		int[ ] pixels = new int[1];
		PixelGrabber pg = new PixelGrabber(image, x, y, 1, 1, pixels, 0, 1);
        try  {
			pg.grabPixels( );
        }  catch  (InterruptedException  e)  {
        }
		return (pixels[0]);
	}
	public void mouseReleased(MouseEvent evt) {
		if (MouseDownSw == true) {
			MouseDownSw = false;					// �}�E�X�_�E���X�C�b�`���I�t
		}
	}
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
	// MouseMotionListener�C���^�[�t�F�[�X������ ----------------------------------------
    public void mouseDragged(MouseEvent evt) {
		if (MouseDownSw == true) {
			int mouseX = evt.getX( );
			int mouseY = evt.getY( );
			// �摜�`��ʒu�@���@�}�E�X�ʒu �| �N���b�N���̋���
			PieceX[ClickPieceNo] = mouseX - ClickDistanceX;
			PieceY[ClickPieceNo] = mouseY - ClickDistanceY;
			repaint( );
		}
	}
    public void mouseMoved(MouseEvent evt) { }
}
