import java.applet.*;							// Applet
import java.awt.*;								// Graphics, Image, MediaTracker, Scrollbar
import java.awt.event.*;						// AdjustmentListener, AdjustmentEvent

public class MapScrollbar extends Applet 
			implements AdjustmentListener {		// AdjustmentListener�C���^�[�t�F�[�X����

	int	AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Image Map;										// �}�b�v�C���[�W
	int MapWidth, MapHeight;						// �}�b�v�̃T�C�Y
	int DispX, DispY;								// �}�b�v��`�悷��ʒu
	int	HscrollbarMax, VscrollbarMax;				// �e�X�N���[���o�[�̍ő�l
	Scrollbar Hscrollbar, Vscrollbar;				// �����o�[�C�����o�[

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		MediaTracker mediatracker = new MediaTracker(this);		// ���f�B�A�g���b�J����
		Map = getImage(getCodeBase( ), getParameter("name"));	// �摜�f�[�^����
		mediatracker.addImage(Map, 0);				// ���f�B�A�g���b�J�ɃZ�b�g
		try {
			mediatracker.waitForID(0);				// ���͂���������܂ő҂�
		} catch(InterruptedException e) {
		}

		MapWidth = Map.getWidth(this);				// �}�b�v�T�C�Y
		MapHeight = Map.getHeight(this);
		AppletWidth = getSize( ).width;				// �A�v���b�g�T�C�Y
		AppletHeight = getSize( ).height;
		HscrollbarMax = MapWidth - AppletWidth;		// �X�N���[��max
		VscrollbarMax = MapHeight - AppletHeight;

		setLayout(null);							// ���R�z�u
		// �X�N���[���o�[�쐬
		Hscrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, HscrollbarMax+1);
		Vscrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, VscrollbarMax+1);
		Hscrollbar.setBounds(20, 0, AppletWidth - 20, 20);	// �o�[�̍Đݒ�
		Vscrollbar.setBounds(0, 20, 20, AppletHeight - 20);
		add(Hscrollbar);							// �o�[���A�v���b�g�ɕt��
		add(Vscrollbar);
		Hscrollbar.addAdjustmentListener(this);		// �o�[�Ƀ��X�i�[�Z�b�g
		Vscrollbar.addAdjustmentListener(this);
		DispX = DispY = 0;							// �\���ʒu�����l�ݒ�
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(Map, DispX, DispY, this);
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	// AdjustmentListener�C���^�[�t�F�[�X�̃��\�b�h��` ---------------------------------
	public void adjustmentValueChanged(AdjustmentEvent evt) {	// �o�[�̕ω��L���b�`
		Scrollbar scrollbar = (Scrollbar)evt.getSource( );
		if (scrollbar == Hscrollbar)				// �����o�[�̏ꍇ
			DispX = -Hscrollbar.getValue( );		// ���������ł̒n�}�̕`��ʒu
		else if (scrollbar == Vscrollbar)			// �����o�[�̏ꍇ
			DispY = -Vscrollbar.getValue( );		// ���������ł̒n�}�̕`��ʒu
		repaint( );
	}
}
