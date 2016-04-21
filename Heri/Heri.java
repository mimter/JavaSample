import java.applet.*;
import java.awt.*;			// Graphic, Image, Color, Font, Canvas, Label, 
							// BorderLayout, Toolkit, MediaTracker, GraphicsConfiguration
import java.awt.event.*;	// MouseListener, MouseEvent
import javax.media.j3d.*;	//Transform3D, TransformGroup, Canvas3D, BranchGroup, View, GraphicsContext3D
import javax.vecmath.Vector3d;
import com.sun.j3d.loaders.lw3d.Lw3dLoader;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Heri extends Applet implements Runnable, MouseListener {

	String DataFilename[ ] = { "heri.lws",			// Lightwave�ō쐬�����w���R�v�^�[
							   "map.lws"};			// Lightwave�ō쐬�����n�`
	Thread thread = null;							// �X���b�h
	int Score = 0;									// �X�R�A
	Image MeterPanel, Direction;					// ���[�^�̃p�l���摜�C�����C���[�W

	Canvas3D GameCanvas = null;						// �Q�[���L�����o�X
	Label InformationLabel;							// ���\�����x��

	MeterCanvas metercanvas;						// ���[�^�L�����o�X

	static int FrameWidth = 800, FrameHeight = 600;	// �t���[���̕��ƍ���

	int Kanseihokan = 20;							// ������ԉ�
	int KanseihokanCount = 0;						// ������ԉ񐔃J�E���g
	int preparation  = 0;							// �����t���O
	boolean GameStartFlag = false;					// �Q�[���X�^�[�g�t���O

	double LocalXRotation = 0;						// ���[�J���̉�]�p�x
	double LocalYRotation = 0;
	double GlobalXRotation = 0;						// �O���[�o���̉�]�p�x
	double GlobalYRotation = 0;

	double ViewX = 0, ViewY = 50, ViewZ = 0;		// �r���[�|�C���g�����ݒ�

	double StarX[ ] = { -5000,  5000,  5000,  -5000,    0};	// map.lws����star.lwo�̈ʒu
	double StarY[ ] = {   100,   100,   100,    100,  100};
	double StarZ[ ] = {  5000,  5000, -5000,  -5000,    0};

	boolean HitFlag[ ] = { false, false, false, false, false };	// star�q�b�g�t���O

    GraphicsContext3D graphicsContext3D = null;		// GraphicsContext3D�I�u�W�F�N�g

	// ���_�ݒ�p------------------------------------------------------------------------
	TransformGroup ViewTransform = null;
	Transform3D ViewTrans = new Transform3D( );
	Transform3D ViewTrans_pos = new Transform3D( );
	Transform3D ViewTrans_rotx = new Transform3D( );
	Transform3D ViewTrans_roty = new Transform3D( );

	// ���C������ -------------------------------------------------------------------------
    public static void main(String args[ ]) {
		new MainFrame(new Heri( ), FrameWidth, FrameHeight);
    }

	// �R���X�g���N�^ -------------------------------------------------------------------
    public Heri( ) {
 		MediaTracker  mt = new MediaTracker(this);	// ���f�B�A�g���b�J����
		MeterPanel = Toolkit.getDefaultToolkit( ).getImage("MeterPanel.jpg");
		Direction = Toolkit.getDefaultToolkit( ).getImage("Direction.jpg");
		mt.addImage(MeterPanel, 0);					// �摜�����f�B�A�g���b�J�ɃZ�b�g
		mt.addImage(Direction, 0);					// �摜�����f�B�A�g���b�J�ɃZ�b�g
		try {
			mt.waitForID(0);						// �摜���͂̊�����҂�
		} catch(InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);					// ��O�G���[�\��
		}

		// Lightwave3D�̃��[�_�\�z�C�RD�f�[�^����
		Loader lw3dLoader1 = new Lw3dLoader(Loader.LOAD_ALL);
		Scene loaderScene_heri = null;
		try {
		    loaderScene_heri = lw3dLoader1.load(DataFilename[0]);
		}
		catch (Exception e) {
		    System.err.println("Exception loading " + DataFilename[0] + " : " + e);
		    System.exit(1);
		}

		// Lightwave3D�̃��[�_�\�z�C�RD�f�[�^����
		Loader lw3dLoader2 = new Lw3dLoader(Loader.LOAD_ALL);
		Scene loaderScene_map = null;
		try {
		    loaderScene_map = lw3dLoader2.load(DataFilename[1]);
		}
		catch (Exception e) {
		    System.err.println("Exception loading " + DataFilename[1] + " : " + e);
		    System.exit(1);
		}

		// Construct the applet canvas
		setLayout(new BorderLayout( ));
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		GameCanvas = new Canvas3D(config);
		add("Center", GameCanvas);

		// Create a basic universe setup and the root of our scene
		SimpleUniverse universe = new SimpleUniverse(GameCanvas);
		BranchGroup sceneRoot = new BranchGroup( );

		// Change the back clip distance; the default is small for
		// some lw3d worlds
		View theView = universe.getViewer( ).getView( );			// ���_
		theView.setBackClipDistance(50000f);

		// ���_�ݒ�----------------------------------------------------------------------
		ViewTrans.rotY(Math.PI / 180.0);
		ViewTrans_pos.setTranslation(new Vector3d(ViewX, ViewY, ViewZ));
		ViewTrans.mul(ViewTrans_pos);
		ViewTransform = universe.getViewingPlatform( ).getViewPlatformTransform( );
		ViewTransform.setTransform(ViewTrans);

		// ------------------------------------------------------------------------------
		TransformGroup sceneTransform = new TransformGroup( );
		TransformGroup sceneTransform_heri = new TransformGroup( );
		TransformGroup sceneTransform_map = new TransformGroup( );

		// �w����] ---------------------------------------------------------------------
		Transform3D transform3D_heri = new Transform3D( );
		transform3D_heri.rotY(Math.PI / 180);
		Transform3D transform3D_heri_pos = new Transform3D( );
		transform3D_heri_pos.setTranslation(new Vector3d(0.0, 50.0, -50.0));
		transform3D_heri.mul(transform3D_heri_pos);
		sceneTransform_heri.setTransform(transform3D_heri);
		sceneTransform_heri.addChild(loaderScene_heri.getSceneGroup( ));

		// Map --------------------------------------------------------------------------
		sceneTransform_map.addChild(loaderScene_map.getSceneGroup( ));

		// ------------------------------------------------------------------------------
		sceneRoot.addChild(sceneTransform);
		sceneRoot.addChild(sceneTransform_heri);
		sceneRoot.addChild(sceneTransform_map);
	
		universe.addBranchGraph(sceneRoot);

		GameCanvas.addMouseListener(this);			// �}�E�X���X�i�[�ǉ�

		thread = new Thread(this);
		thread.start( );

		setLayout(new BorderLayout( ));				// Frame�̃��C�A�E�g�ݒ�

		InformationLabel = new Label("          ");		// ���x���I�u�W�F�N�g����
		InformationLabel.setBackground(Color.yellow);	// ���x���̔w�i�F�ݒ�

		// ���[�^�[�L�����o�X�쐬
		metercanvas = new MeterCanvas(MeterPanel, Direction);	// �L�����o�X����
		metercanvas.setSize(800, 100);				// �L�����o�X�T�C�Y�Đݒ�
		metercanvas.canvas_make(1);					// �L�����o�X�̐�������

		// �t���[���ɔz�u
		add ("North", InformationLabel);			// ���x�����A�v���b�g�ɕt��
		add("Center", GameCanvas);
		add("South", metercanvas);					// �L�����o�X���A�v���b�g�ɕt��

		preparation = 1;							// ����OK
	}
	// �����_�[ -------------------------------------------------------------------------
	public void render( ) {
		if (preparation == 0)						// �������܂��̏ꍇ
			return;

		if (GameStartFlag == false) {				// �Q�[�����n�܂��Ă��Ȃ��ꍇ
			metercanvas.meter_disp(ViewX, ViewY, ViewZ, 0, 0, 0); 	// �p�l���\��
			return;
		}

		// �����[�|�C���g�v�Z
		double move = 5;							// �P�ʈړ�����
		double yw = move * Math.sin(GlobalXRotation);
		double xw = (-1) * Math.sin(GlobalYRotation) * Math.sqrt(move * move - yw * yw);
		double zw = Math.cos(GlobalYRotation) * Math.sqrt(move * move - yw * yw);
		ViewX += xw;
		ViewY += yw;
		ViewZ -= zw;

		// �O���[�o���̉�]�����@���W�A�����p�x�ɕϊ�
		int x_rot = (int)(GlobalXRotation * 180 / 3.14);
		int y_rot = (int)(GlobalYRotation * 180 / 3.14);

		String str = "[" + x_rot + ", " + y_rot + "]";
		InformationLabel.setText(str);

		// ���[�^�L�����o�X�ɒl�𑗂�C�p�l���\��
		metercanvas.meter_disp(ViewX, ViewY, ViewZ, x_rot, y_rot, Score); 

		if (GameStartFlag == false)					// �Q�[�����n�܂��Ă��Ȃ��ꍇ
			return;

		// �ʒu��ݒ�
		ViewTrans.setTranslation(new Vector3d(ViewX, ViewY, ViewZ));

		// �O���[�o�����W�ł̐i�s����
		GlobalXRotation += LocalXRotation / Kanseihokan;
		GlobalYRotation += LocalYRotation / Kanseihokan;

		// ���[���h�r���[�ɕ�Ԃ����p�x��ݒ�
//		ViewTrans_rotx.rotX(LocalXRotation / Kanseihokan);		// X���ɑ΂��Ẳ�]
		ViewTrans_roty.rotY(LocalYRotation / Kanseihokan);		// Y���ɑ΂��Ẳ�]

//		ViewTrans.mul(ViewTrans_rotx);				// X���̉�]�ݒ�
		ViewTrans.mul(ViewTrans_roty);				// Y���̉�]�ݒ�

		ViewTransform.setTransform(ViewTrans);

		// ������ԏ���
		KanseihokanCount--;

		if (KanseihokanCount == 0) {				// �܂������ɐi��
			LocalXRotation = LocalYRotation = 0;	// X�������Y���̉�]�Ȃ�
			KanseihokanCount = Kanseihokan;			// �J��Ԃ�
		}
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {
			render( );
			checkCollision(new Vector3d(ViewX, ViewY, ViewZ));
 			try {
				thread.sleep(50);					// �X���b�h��n�~���b�X���[�v
        	} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
 			}
		}
	}
	// ���蔻�� -------------------------------------------------------------------------
	void checkCollision(Vector3d pst) {
		double width = 10;
		for (int i = 0; i < 5; i++) {
			if ( Math.abs(pst.x - StarX[i]) < width
				&& Math.abs(pst.y - StarY[i]) < width
				&& Math.abs(pst.z - StarZ[i]) < width ) {
				HitFlag[i] = true;
			}
			else {
				if (HitFlag[i] == true) {
					Score++;
					Toolkit.getDefaultToolkit( ).beep( );	// �r�[�v��
				}
				HitFlag[i] = false;
			}
		}
	}
	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
    public void mousePressed(MouseEvent evt) {
		// �N���b�N�����ʒu��frame�̒��S����̋���
		int x = evt.getX( ) - FrameWidth / 2;
		int y = evt.getY( ) - FrameHeight / 2;

		// ����1000�iframe�̉����̔����j�̃x�N�g���̐�ɖڕW�_������Ƃ���
		// �O���ɐi��ł��邱�Ƃ��l�������
		// ���̎��_�ł̂w���y�тx���̃��[�J���ȉ�]��90�x����|90�x�ł���
		// ��ʏ�̃��[�J�����W�ł̐i�s����
		LocalXRotation = Math.asin((double)y / 1000.0) * (-1);
		LocalYRotation = Math.asin((double)x / Math.sqrt(1000.0 * 1000.0 - y * y)) * (-1);

		GameStartFlag = true;
		KanseihokanCount = Kanseihokan;
	}
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
}

// --------------------------------------------------------------------------------------
// MeterCanvas �N���X
class MeterCanvas extends Canvas{
	int X, Y, Z;
	int RX, RY;
	int SCORE;
	int MAKE_FLAG = 0;
	Image MeterPanel, Direction;
	Image WorkImage = null, WorkDirection;			// ��Ɨp�C���[�W
	Graphics wg = null, wd = null;					// ��Ɨp�O���t�B�b�N�X
	// �R���X�g���N�^ -------------------------------------------------------------------
	public MeterCanvas (Image meterpanel, Image direction) {
		setBackground(Color.white);					// �L�����o�X�̔w�i�F
		MeterPanel = meterpanel;					// ����p�l���̃C���[�W
		Direction = direction;						// �����p�l���̃C���[�W
		X = Y = Z = 0;								// X�EY�EZ�����W�l
		RX = RY = 0;								// X���EY���ɑ΂����]
		SCORE = 0;									// �X�R�A
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		if (MAKE_FLAG == 1) {
			// �Ăяo��������setsize��������ŁCcreateImage���s�� 
			WorkImage = createImage(800, 100);
			wg = WorkImage.getGraphics( );	
			WorkDirection = createImage(100, 40);
			wd = WorkDirection.getGraphics( );
			MAKE_FLAG = 2;							// ����	
			return;
		}

		// ���[�^�[�p�l��
		wg.drawImage(MeterPanel, 0, 0, this);

		// �������[�^�[
		int w = RY % 360;
		if (w > 0)
			w = -(360 - w);
		wd.drawImage(Direction, w, 0, this);
		wg.drawImage(WorkDirection, 30, 40, 100, 40, this);
		wg.setColor(new Color(255,  0,  0));
		wg.drawLine(80, 35, 80, 85);				// �����j

		// ���x���[�^ 170,50
 		wg.setFont(new  Font("Courier", Font.BOLD, 20));
		wg.setColor(new Color(255,  255,  255));
		wg.drawString(Y + " m", 170, 70);

		// �s�b�`�p�x 290, 50
		wg.drawString(RX + "��", 290, 70);

		// ���݈ʒu 410 , 530
		wg.drawString("" + X, 410, 70);
		wg.drawString("" + Z, 530, 70);

		// �X�R�A 700, 50
		wg.setColor(new Color(255,  0,  0));
		wg.drawString("" + SCORE, 700, 70);

		// ��ƃC���[�W���L�����o�X�ɕ`��
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
	}
	// �`��X�V�����Ē�` ---------------------------------------------------------------
	public void update(Graphics g) {				// �f�t�H���g��update���Ē�`
		paint(g);									// �w�i�F��ʃN���A�폜�Cpaint�̂�
	}
	void canvas_make(int make_flag) {
		MAKE_FLAG = make_flag;
	}
	// ���[�^�`�� -----------------------------------------------------------------------
	void meter_disp(double x, double y, double z, int rx, int ry, int Score) {
		X = (int)x; Y = (int)y; Z = (int)z; RX = rx; RY = ry; SCORE = Score;
		repaint( );									// ���[�^�[�`��
	}
}
