import java.applet.*;								// Applet
import java.awt.*;									// Graphic, Image, Color, Font etc

public class Neon extends Applet implements Runnable{
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
    Thread thread = null;							// �X���b�h
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y

	// �d�� -----------------------------------------------------------------------------
	int DenkyuSize = 20;							// �d���T�C�Y
	int DenkyuKazu = 0;								// �d����
	int DenkyuX[ ] = new int[200];					// �d���ʒu
	int DenkyuY[ ] = new int[200];
	int DenkyuDisp[ ] = new int[200];				// �d����� 1:�_�� 0:����
	int DenkyuProcess = 0;							// �d���v���Z�X
	int DenkyuSyoriCount = 0;						// �d�������J�E���g

	// �u���� ---------------------------------------------------------------------------
	int KeikoutouSize = 5;							// �u�������T�C�Y
	int KeikoutouKazu = 0;							// �u������
	int KeikoutouX[ ] = new int[200];				// �u�����ʒu
	int KeikoutouY[ ] = new int[200];
	int KeikoutouDisp[ ] = new int[200];			// �u������� 1:�_�� 0:����
	int KeikoutouProcess = 0;						// �u�����v���Z�X
	int KeikoutouSyoriCount = 0;					// �u���������J�E���g

	// ���� -----------------------------------------------------------------------------
	String Message;									// ���b�Z�[�W�̕���
	String Moji[ ] = new String[50];				// 1�������i�[����z��
	int Mojisu = 0;									// �����̐�
	int MojiX[ ] = new int[50];						// �����̈ʒu
	int MojiY[ ] = new int[50];
	int MojiDisp[ ] = new int[50];					// �����̏�� 1:�_�� 0:����
	int MojiProcess = 0;							// �����v���Z�X
	int MojiSyoriCount = 0;							// ���������J�E���g

	// ���������� -----------------------------------------------------------------------
    public void init( ){
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��Ɨp�O���t�B�b�N�X�擾

		Message = getParameter("message");			// �p�����[�^��胁�b�Z�[�W�擾

	// �d���̃Z�b�g -----------------------------------------------------------------
		DenkyuKazu = 0;
		// ��̍�����z�u
		for (int i = 0; i <= AppletWidth - DenkyuSize; i += DenkyuSize) {
			DenkyuX[DenkyuKazu] = i;
			DenkyuY[DenkyuKazu] = 0;
			DenkyuDisp[DenkyuKazu] = 0;
			DenkyuKazu++;
		}
		// �E�̏ォ��z�u
		for (int i = DenkyuSize; i <= AppletHeight - DenkyuSize; i += DenkyuSize) {
			DenkyuX[DenkyuKazu] = AppletWidth - DenkyuSize;
			DenkyuY[DenkyuKazu] = i;
			DenkyuDisp[DenkyuKazu] = 0;
			DenkyuKazu++;
		}
		// �E�����獶�֔z�u
		for (int i = AppletWidth - DenkyuSize * 2; i >= 0; i -= DenkyuSize) {
			DenkyuX[DenkyuKazu] = i;
			DenkyuY[DenkyuKazu] = AppletHeight - DenkyuSize;
			DenkyuDisp[DenkyuKazu] = 0;
			DenkyuKazu++;
		}
		// ���������֔z�u
		for (int i = AppletHeight - DenkyuSize * 2; i >= DenkyuSize; i -= DenkyuSize) {
			DenkyuX[DenkyuKazu] = 0;
			DenkyuY[DenkyuKazu] = i;
			DenkyuDisp[DenkyuKazu] = 0;
			DenkyuKazu++;
		}

		// �u�����̃Z�b�g ---------------------------------------------------------------
		KeikoutouKazu = 0;
		for (int i = DenkyuSize; i < AppletWidth - DenkyuSize; i += KeikoutouSize * 2) {
			KeikoutouX[KeikoutouKazu] = i;
			KeikoutouY[KeikoutouKazu] = DenkyuSize;
			KeikoutouDisp[KeikoutouKazu] = 0;
			KeikoutouKazu++;
		}

		// ���� -------------------------------------------------------------------------
		int MojiSize = (AppletWidth - DenkyuSize*2) / Message.length( );
		// System Helvetica  TimesRoman  Courier  Dialog  Symbol '�l�r ����'
		Font font = new Font("System", Font.BOLD, MojiSize);
		WorkGraphics.setFont(font);
		FontMetrics fontmetrics = getFontMetrics(font);	// �t�H���g���g���b�N�X�擾
		int MojiHeight = fontmetrics.getAscent( ); 		// + fm.getDescent( );//�����̍���
		Mojisu = 0;
		for (int i = 0; i < Message.length( ); i++) {
			Moji[Mojisu] = Message.substring(i, i+1);// ������1�������Z�b�g
			MojiX[Mojisu] = DenkyuSize*2 + i * MojiSize;
			MojiY[Mojisu] = (AppletHeight + MojiHeight) / 2;
			MojiDisp[Mojisu] = 0;
			Mojisu++;
		}
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�J�n
	}
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g){
		if (WorkGraphics == null)
			return;

		// �S�ʃN���A
		WorkGraphics.setColor(new Color(0, 0, 0));
		WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

		// �d���`��
		for (int i = 0; i < DenkyuKazu; i++) {
			if (DenkyuDisp[i] == 1) {				// �_��
				for (int r = DenkyuSize; r > 0; r-=2) {
					WorkGraphics.setColor(new Color(250 - r*9, 250 - r*9, 0));
					WorkGraphics.fillOval(DenkyuX[i] + (DenkyuSize - r) / 2,
										 DenkyuY[i] + (DenkyuSize - r) / 2, r, r);
				}
			}
			else {									// ����
				for (int r = DenkyuSize; r > 0; r-=2) {
					WorkGraphics.setColor(new Color(125 - r*5, 125 - r*5, 0));
					WorkGraphics.fillOval(DenkyuX[i] + (DenkyuSize - r) / 2,
										 DenkyuY[i] + (DenkyuSize - r) / 2, r, r);
				}
			}
		}

		// �u�����`��
		for (int i = 0; i < KeikoutouKazu; i++) {
			if (KeikoutouDisp[i] == 1)				// �_��
				WorkGraphics.setColor(new Color(0, 100, 250));
			else									// ����
				WorkGraphics.setColor(new Color(0, 0, 80));
			WorkGraphics.fillRect(KeikoutouX[i], KeikoutouY[i],
									KeikoutouSize, AppletHeight - 20*2);
		}

		// �����`��
		for (int i = 0; i < Mojisu; i++) {
			if (MojiDisp[i] == 1)					// �_��
				WorkGraphics.setColor(new Color(240, 240, 240));
			else									// ����
				WorkGraphics.setColor(new Color(30, 30, 30));
			WorkGraphics.drawString(Moji[i], MojiX[i], MojiY[i]);
		}

		g.drawImage(WorkImage, 0, 0, this);			// �A�v���b�g�̃O���t�B�b�N�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
    public void run( ){
        while(thread != null){						// �X���b�h�����݂��Ă����
			DenkyuShori( );							// �d���`�揈��
			KeikoutouShori( );						// �u�����`�揈��
			MojiShori( );							// �����`�揈��
			repaint( );								// �ĕ`��
            try{
               thread.sleep(200);					// �X���b�h���w��~���b�X���[�v
            }catch(InterruptedException e){}
        }
    }
	// �d������ -------------------------------------------------------------------------
    void DenkyuShori( ){
		switch (DenkyuProcess) {
			case 0:	DenkyuRotation(30);	break;		// �d�����[�e�[�V�����_��
			case 1:	DenkyuFlush(5);		break;		// �d���t���b�V��
			default: DenkyuProcess = 0;
		}
	}
	// �d����]�_�� ---------------------------------------------------------------------
	void DenkyuRotation(int kaisu) {
		for (int i = 0; i < DenkyuKazu; i++) {
			if ((i + DenkyuSyoriCount) % 8 == 0)	// 8�����ɏ���
				DenkyuDisp[i] = 0;					// ����
			else
				DenkyuDisp[i] = 1;					// �_��
		}
		DenkyuSyoriCount++;							// �d�������J�E���g
		if (DenkyuSyoriCount == kaisu) {			// �w��񐔂ɂȂ�܂�
			DenkyuProcess++;						// ���̃v���Z�X��
			DenkyuSyoriCount = 0;					// �d�������J�E���g�N���A
		}
	}
	// �d���t���b�V�� -------------------------------------------------------------------
	void DenkyuFlush(int kaisu) {
		for (int i = 0; i < DenkyuKazu; i++) {
			if (DenkyuSyoriCount % 2 == 0)			// 2���1��
				DenkyuDisp[i] = 0;					// ����
			else
				DenkyuDisp[i] = 1;					// �_��
		}
		DenkyuSyoriCount++;							// �d�������J�E���g
		if (DenkyuSyoriCount == kaisu) {			// �w��񐔂ɂȂ�܂�
			DenkyuProcess++;						// ���̃v���Z�X��
			DenkyuSyoriCount = 0;					// �d�������J�E���g�N���A
		}
	}
	// �u�������� -----------------------------------------------------------------------
    void KeikoutouShori( ){
		switch (KeikoutouProcess) {
			case 0:	KeikoutouSideDisp(1);	break;	// �T�C�h����_��
			case 1:	KeikoutouSideDisp(0);	break;	// �T�C�h�������
			case 2:	KeikoutouLeftDisp(1);	break;	// ������_��
			case 3: KeikoutouLeftDisp(0);	break;	// ���������
			case 4: KeikoutouFlush(5);		break;	// �t���b�V��
			case 5: KeikoutouRightDisp(1);	break;	// �E����_��
			case 6: KeikoutouFlush(3);		break;	// �t���b�V��
			default: KeikoutouProcess = 0;
		}
	}
	// �u����������_���E���� -----------------------------------------------------------
	void KeikoutouLeftDisp(int sw) {
		KeikoutouDisp[KeikoutouSyoriCount] = sw;	// 1:�_���@0:����
		KeikoutouSyoriCount++;						// �u���������J�E���g
		if (KeikoutouSyoriCount == KeikoutouKazu) {	// �u�����̐��܂�
			KeikoutouProcess++;						// ���̃v���Z�X��
			KeikoutouSyoriCount = 0;				// �u���������J�E���g�N���A
		}
	}
	// �u�����E����_���E���� -----------------------------------------------------------
	void KeikoutouRightDisp(int sw) {
		KeikoutouDisp[KeikoutouKazu - KeikoutouSyoriCount - 1] = sw;	// 1:�_���@0:����
		KeikoutouSyoriCount++;						// �u���������J�E���g
		if (KeikoutouSyoriCount == KeikoutouKazu) {	// �u�����̐��܂�
			KeikoutouProcess++;						// ���̃v���Z�X��
			KeikoutouSyoriCount = 0;				// �u���������J�E���g�N���A
		}
	}
	// �u�����T�C�h����_���E���� -------------------------------------------------------
	void KeikoutouSideDisp(int sw) {
		KeikoutouDisp[KeikoutouSyoriCount] = sw;						// ������
		KeikoutouDisp[KeikoutouKazu - KeikoutouSyoriCount - 1] = sw;	// �E����
		KeikoutouSyoriCount++;						// �u���������J�E���g
		if (KeikoutouSyoriCount == (KeikoutouKazu / 2)) {	// �����̔����܂�
			KeikoutouProcess++;						// ���̃v���Z�X��
			KeikoutouSyoriCount = 0;				// �u���������J�E���g�N���A
		}
	}
	// �u�����t���b�V�� -----------------------------------------------------------------
	void KeikoutouFlush(int kaisu) {
		for (int i = 0; i < KeikoutouKazu; i++) {
			if (KeikoutouSyoriCount % 2 == 0) 		// 2���1��
				KeikoutouDisp[i] = 1;				// �_��
			else
				KeikoutouDisp[i] = 0;				// ����
		}
		KeikoutouSyoriCount++;						// �u���������J�E���g
		if (KeikoutouSyoriCount == kaisu * 2) {		// �_���E�������s������2�{
			KeikoutouProcess++;						// ���̃v���Z�X��
			KeikoutouSyoriCount = 0;				// �u���������J�E���g�N���A
		}
	}

	// �������� -------------------------------------------------------------------------
    void MojiShori( ){
		switch (MojiProcess) {
			case 0:	MojiLeftDisp(1);	break;		// ������_��
			case 1:	MojiLeftDisp(0);	break;		// ���������
			case 2:	MojiFlush(5);		break;		// �����t���b�V��
			default: MojiProcess = 0;
		}
	}
	// ����������_���E���� -------------------------------------------------------------
	void MojiLeftDisp(int sw) {
		MojiDisp[MojiSyoriCount] = sw;				// 1:�_���@0:����
		MojiSyoriCount++;							// ���������J�E���g
		if (MojiSyoriCount == Mojisu) {				// �w��񐔂܂�
			MojiProcess++;							// ���̃v���Z�X��
			MojiSyoriCount = 0;						// ���������J�E���g�N���A
		}
	}
	// �����t���b�V�� -------------------------------------------------------------------
	void MojiFlush(int kaisu) {
		for (int i = 0; i < Mojisu; i++) {
			if (MojiSyoriCount % 2 == 0) 			// 2���1��
				MojiDisp[i] = 0;					// ����
			else
				MojiDisp[i] = 1;					// �_��
		}
		MojiSyoriCount++;							// ���������J�E���g
		if (MojiSyoriCount == kaisu) {				// �w��񐔂܂�
			MojiProcess++;							// ���̃v���Z�X��
			MojiSyoriCount = 0;						// ���������J�E���g�N���A
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
}
