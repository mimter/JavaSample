import java.applet.*;				// Applet, AudioClip
import java.awt.*;					// Graphics, Image, Color, Font, MediaTracker
import java.awt.event.*;			// MouseListener, MouseEvent, InputEvent

public class Othello extends Applet implements Runnable, MouseListener {
    int Row, Column;								// �I�Z���Ղ̍s�E��
	boolean PreparationFlag = false;				// �����t���O
	Image PlateImage;								// �I�Z���ՃC���[�W�摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	int Board[ ][ ] = new int[8][8];				// �I�Z���Ղ̓��e
	int Work[ ][ ] = new int[8][8];					// ��Ɨp�Ղ̓��e
	Thread thread;									// �X���b�h

	Image KomaImage[ ] = new Image[8];				// �R�}��]�C���[�W�i���O�`���V�j
	int Koma = 0;									// 0:���̔�, 7:���̔�
	int Change = 0;									// �ω��ʁi�����獕�͂P�C�����甒��-1�j
	int Komacount[ ] = {2, 2};						// �R�}�J�E���g(�����l�j
    AudioClip ReverseSound;							// ���]�T�E���h

	// ���������� -----------------------------------------------------------------------
    public void init( ) {
		addMouseListener(this);						// �}�E�X���X�i�[�ǉ�

        ReverseSound = getAudioClip(getCodeBase( ), "sound/click.au");	// ���]�T�E���h

		WorkImage = createImage(440, 440);			// ��Ɨp�C���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );	// ��Ɨp�O���t�B�b�N�X�擾

		MediaTracker mediatracker = new MediaTracker(this);	// ���f�B�A�g���b�J�쐬
		PlateImage = getImage(getCodeBase( ), "image/plate.gif");	// �I�Z���Չ摜
		mediatracker.addImage(PlateImage, 0);		// �摜�����f�B�A�g���b�J�ɃZ�b�g

		for (int i = 0; i < 8; i++) {				// �R�}�摜��荞��
			KomaImage[i] = getImage(getCodeBase( ), "image/koma" + i +".gif");
			mediatracker.addImage(KomaImage[i], 0);	// �摜�����f�B�A�g���b�J�ɃZ�b�g
		}
		try {
			mediatracker.waitForID(0);				// �摜���͂̊�����҂�
		} catch (InterruptedException e) {			// waitForID�ɑ΂����O����
			showStatus(" " + e);
		}

		for (int i = 0; i < 8; i++)					// �I�Z���Ղ̔z��ɒl��ݒ�
			for (int j = 0; j < 8; j++)
				Board[i][j] = -1;					// -1�F�����u���Ă��Ȃ���Ԃ�\��
		Board[3][3] = Board[4][4] = 0;				// 0:���R�}
		Board[3][4] = Board[4][3] = 7;				// 7:���R�}

		PreparationFlag = true;						// �����t���O
    }
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
    public void paint(Graphics g) {
		// ��Ɨ̈悪�܂��m�ۂ���Ȃ����C�܂��͏����t���O���I�t�̏ꍇ
		if (WorkGraphics == null || PreparationFlag == false)
			return;

		WorkGraphics.drawImage(PlateImage, 0, 0, this);	// �I�Z���Ղ�`��

		for (int i = 0; i < 8; i++) {				// �I�Z���Ղ̏󋵂�`��
			for (int j = 0; j < 8; j++) {
				if (Board[i][j] != -1)				// �I�Z���Ղ�i,j�̈ʒu����łȂ��ꍇ
					WorkGraphics.drawImage(KomaImage[Board[i][j]],
											j*50+25, i*50+25, this);
			}
		}
		WorkGraphics.drawString(					// �擾�R�}���\��
				"You(White) : " + Komacount[0] + " - Computer(Black) : " + Komacount[1],
				130, 435);

		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W���A�v���b�g�ɕ`��
    }
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		while (thread != null) {					// �X���b�h�����݂��Ă����
			repaint( );								// �ĕ`��
			if (ChangeKoma(Change) == 0) {			// �ω��Ȃ��̏ꍇ
				if (Koma == 1)						// �ω���C�l�Ԃ̔�
					Koma =  0;						// �l�Ԃ̔�
				else if (Koma == 6) {				// �ω���C�R���s���[�^�i���R�}�j�̔�
					Koma = 7;						// �R���s���[�^�i���R�})�̔�
					Change = 1;						// ���]�ω��l�{�P�i���O���獕�V�ցj
					computerprocess( );				// �R���s���[�^�̎v�l����
					try {
						thread.sleep(1000);			// 1�b�҂�
					} catch (InterruptedException e) {	// ���̃X���b�h�̊��荞�ݗ�O����
						break;
					}
					Koma = 1;						// �ω���C�l�ԁi���R�})�̔�
				}
			}

			try {
				thread.sleep(100);					// �X���b�h��100�~���b�X���[�v
			} catch (InterruptedException e) {		// ���̃X���b�h�̊��荞�ݗ�O����
				break;
			}
		}
	}
	// �R���s���[�^���̎v�l���� ---------------------------------------------------------
	void computerprocess( ) {
		int Row = -1, Column = -1;	// �R���s���[�^���u�����Ƃ��ł���Ղ̍s�Ɨ�̕ۊǗp
		boolean flag = false;

		// -----------------------------------------------------------------------------
		// �����ɒu�����Ƃ��ł���Βu��
		Board2work( );								// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
		if (check(0, 0, Koma, Change, 0) > 0) {			// ������ɒu����ꍇ
			flag = true;
			Row = 0;
			Column = 0;
		}
		else if (check(0, 7, Koma, Change, 0) > 0) {	// �E����ɒu����ꍇ
			flag = true;
			Row = 0;
			Column = 7;
		}
		else if (check(7, 0, Koma, Change, 0) > 0) {	// �������ɒu����ꍇ
			flag = true;
			Row = 7;
			Column = 0;
		}
		else if (check(7, 7, Koma, Change, 0) > 0) {	// �E�����ɒu����ꍇ
			flag = true;
			Row = 7;
			Column = 7;
		}

		// ------------------------------------------------------------------------------
		// ���u������C���̎�ŋ�����邱�Ƃ��ł���
		if (flag == false) {
			for (int i = 0; flag == false && i <= 7; i++) {
				for (int j = 0; flag == false && j <= 7; j++) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(i, j, Koma, Change, 1) > 0) {		// �u�����Ƃ��ł���ꍇ
						if (check(0, 0, Koma, Change, 0) > 0	// ���ɒu����ꍇ
						 || check(0, 7, Koma, Change, 0) > 0
						 || check(7, 0, Koma, Change, 0) > 0
						 || check(7, 7, Koma, Change, 0) > 0) {
							flag = true;
							Row = i;
							Column = j;
						}
					}
				}
			}
		}

		// ------------------------------------------------------------------------------
		// �����͂ɃR�}��u������C����ɔ��]�������邱�Ƃ��Ȃ��C�����
		// �l�Ԃ����̃��C���̋󂢂Ă���Ƃ���ɒu���ċ��𒼐ڎ�邱�Ƃ��Ȃ��ꍇ�͒u��
		// 0�s��
		if (flag == false) {
			for (int j = 0; flag == false && j <= 7; j++) {
				Board2work( );						// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
				if (check(0, j, 7, Change, 1) > 0) {// �R���s���[�^���u�����ꍇ
					WorkInsideClear( );				// work�̓����N���A�i�[�̃��C������)
					// �u������C�[�����Ől�Ԃ����]�����Ȃ����`�F�b�N
					int	hanten = 0;					// �l�Ԃ����]������
					for (int p = 0; hanten == 0 && p <= 7; p++) {
						hanten = check(0, p, 0, Change, 1);	// �l�Ԃ����]�ł��邩
					}
					if (hanten > 0)					// �l�Ԃ����]�������ꍇ
						continue;					// �����ɂ͒u�����Ƃ��ł��Ȃ�
					else {							// ����Ƀ`�F�b�N
						// �u������C�S�̓I�ɒ[�ɐl�Ԃ��u���邩�`�F�b�N
						flag = true;				// ���v�Ɖ���
						for (int p = 0; flag == true && p <= 7; p++) {
							// �ēx�C�R���s���[�^���u������ʂ�ݒ�
							Board2work( );			// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
							check(0, j, 7, Change, 1);//��̈ʒu�ɃR���s���[�^���u���Ƃ���
							if ((hanten = check(0, p, 0, Change, 1)) > 0) {	
								// �l�Ԃ��u�����ꍇ
								// ����ɃR���s���[�^���[�ɒu�����Ƃ��ł��邩�`�F�b�N
								WorkInsideClear( );	// work�̓����N���A�i�[�̃��C������)
								hanten = 0;
								for (int q = 0; hanten == 0 && q <= 7; q++)
									hanten = check(0, q, 7, Change, 1);
								// ���̂Ƃ��ɒ��ځC�l�Ԃ����ɒu�����Ƃ��ł��邩�`�F�b�N
								if (check(0, 0, 0, Change, 0) > 0
								 || check(0, 7, 0, Change, 0) > 0)
									flag = false;	// �����ɂ͒u�����Ƃ��ł��Ȃ�
							}
						}
						if (flag == true) {			// ���ׂĒ��ׂĐl�Ԃ��[�����Ȃ��ꍇ
							Row = 0;
							Column = j;
						}
					}
				}
			}
		}
		// 7�s��
		if (flag == false) {
			for (int j = 0; flag == false && j <= 7; j++) {
				Board2work( );						// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
				if (check(7, j, 7, Change, 1) > 0) {// �R���s���[�^���u�����ꍇ
					WorkInsideClear( );				// work�̓����N���A�i�[�̃��C������)
					// �u������C�[�����Ől�Ԃ����]�����Ȃ����`�F�b�N
					int	hanten = 0;					// �l�Ԃ����]������
					for (int p = 0; hanten == 0 && p <= 7; p++) {
						hanten = check(7, p, 0, Change, 1);	// �l�Ԃ����]�ł��邩
					}
					if (hanten > 0)					// �l�Ԃ����]�������ꍇ
						continue;					// �����ɂ͒u�����Ƃ��ł��Ȃ�
					else {							// ����Ƀ`�F�b�N
						// �u������C�S�̓I�ɒ[�ɐl�Ԃ��u���邩�`�F�b�N
						flag = true;				// ���v�Ɖ���
						for (int p = 0; flag == true && p <= 7; p++) {
							// �ēx�C�R���s���[�^���u������ʂ�ݒ�
							Board2work( );			// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
							check(7, j, 7, Change, 1);//��̈ʒu�ɃR���s���[�^���u���Ƃ���
							if ((hanten = check(7, p, 0, Change, 1)) > 0) {
								// �l�Ԃ��u�����ꍇ
								// ����ɃR���s���[�^���[�ɒu�����Ƃ��ł��邩�`�F�b�N
								WorkInsideClear( );	// work�̓����N���A�i�[�̃��C������)
								hanten = 0;
								for (int q = 0; hanten == 0 && q <= 7; q++)
									hanten = check(7, q, 7, Change, 1);
								// ���̂Ƃ��ɒ��ځC�l�Ԃ����ɒu�����Ƃ��ł��邩�`�F�b�N
								if (check(7, 0, 0, Change, 0) > 0
								 || check(7, 7, 0, Change, 0) > 0)
									flag = false;	// �����ɂ͒u�����Ƃ��ł��Ȃ�
							}
						}
						if (flag == true) {			// ���ׂĒ��ׂĐl�Ԃ��[�����Ȃ��ꍇ
							Row = 7;
							Column = j;
						}
					}
				}
			}
		}
		// 0���
		if (flag == false) {
			for (int i = 0; flag == false && i <= 7; i++) {
				Board2work( );						// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
				if (check(i, 0, 7, Change, 1) > 0) {// �R���s���[�^���u�����ꍇ
					WorkInsideClear( );				// work�̓����N���A�i�[�̃��C������)
					// �u������C�[�����Ől�Ԃ����]�����Ȃ����`�F�b�N
					int	hanten = 0;					// �l�Ԃ����]������
					for (int p = 0; hanten == 0 && p <= 7; p++) {
						hanten = check(p, 0, 0, Change, 1);	// �l�Ԃ����]�ł��邩
					}
					if (hanten > 0)					// �l�Ԃ����]�������ꍇ
						continue;					// �����ɂ͒u�����Ƃ��ł��Ȃ�
					else {							// ����Ƀ`�F�b�N
						// �u������C�S�̓I�ɒ[�ɐl�Ԃ��u���邩�`�F�b�N
						flag = true;				// ���v�Ɖ���
						for (int p = 0; flag == true && p <= 7; p++) {
							// �ēx�C�R���s���[�^���u������ʂ�ݒ�
							Board2work( );			// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
							check(i, 0, 7, Change, 1);//��̈ʒu�ɃR���s���[�^���u���Ƃ���
							if ((hanten = check(p, 0, 0, Change, 1)) > 0) {
								// �l�Ԃ��u�����ꍇ
								// ����ɃR���s���[�^���[�ɒu�����Ƃ��ł��邩�`�F�b�N
								WorkInsideClear( );	// work�̓����N���A�i�[�̃��C������)
								hanten = 0;
								for (int q = 0; hanten == 0 && q <= 7; q++)
									hanten = check(q, 0, 7, Change, 1);
								// ���̂Ƃ��ɒ��ځC�l�Ԃ����ɒu�����Ƃ��ł��邩�`�F�b�N
								if (check(0, 0, 0, Change, 0) > 0
								 || check(7, 0, 0, Change, 0) > 0)
									flag = false;	// �����ɂ͒u�����Ƃ��ł��Ȃ�
							}
						}
						if (flag == true) {			// ���ׂĒ��ׂĐl�Ԃ��[�����Ȃ��ꍇ
							Row = i;
							Column = 0;
						}
					}
				}
			}
		}
		// 7���
		if (flag == false) {
			for (int i = 0; flag == false && i <= 7; i++) {
				Board2work( );						// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
				if (check(i, 7, 7, Change, 1) > 0) {// �R���s���[�^���u�����ꍇ
					WorkInsideClear( );				// work�̓����N���A�i�[�̃��C������)
					// �u������C�[�����Ől�Ԃ����]�����Ȃ����`�F�b�N
					int	hanten = 0;					// �l�Ԃ����]������
					for (int p = 0; hanten == 0 && p <= 7; p++) {
						hanten = check(p, 7, 0, Change, 1);	// �l�Ԃ����]�ł��邩
					}
					if (hanten > 0)					// �l�Ԃ����]�������ꍇ
						continue;					// �����ɂ͒u�����Ƃ��ł��Ȃ�
					else {							// ����Ƀ`�F�b�N
						// �u������C�S�̓I�ɒ[�ɐl�Ԃ��u���邩�`�F�b�N
						flag = true;				// ���v�Ɖ���
						for (int p = 0; flag == true && p <= 7; p++) {
							// �ēx�C�R���s���[�^���u������ʂ�ݒ�
							Board2work( );			// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
							check(i, 7, 7, Change, 1);//��̈ʒu�ɃR���s���[�^���u���Ƃ���
							if ((hanten = check(p, 7, 0, Change, 1)) > 0) {
								// �l�Ԃ��u�����ꍇ
								// ����ɃR���s���[�^���[�ɒu�����Ƃ��ł��邩�`�F�b�N
								WorkInsideClear( );	// work�̓����N���A�i�[�̃��C������)
								hanten = 0;
								for (int q = 0; hanten == 0 && q <= 7; q++)
									hanten = check(q, 7, 7, Change, 1);
								// ���̂Ƃ��ɒ��ځC�l�Ԃ����ɒu�����Ƃ��ł��邩�`�F�b�N
								if (check(0, 7, 0, Change, 0) > 0
								 || check(7, 7, 0, Change, 0) > 0)
									flag = false;	// �����ɂ͒u�����Ƃ��ł��Ȃ�
							}
						}
						if (flag == true) {			// ���ׂĒ��ׂĐl�Ԃ��[�����Ȃ��ꍇ
							Row = i;
							Column = 7;
						}
					}
				}
			}
		}

		// ------------------------------------------------------------------------------
		// ��������u�����Ƃ��ł���Βu��
		// ���ŋ�������C�܂��͋��������̃R�}�̏ꍇ�͋����珉�߂ċ󂢂��ꏊ�ɒu��
		// 0,0��
		if (flag == false) {
			Row = 0; Column = 0;
			// �����u���Ă��Ȃ����܂��͎����̃R�}�̏ꍇ
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Column <= 7 && Board[Row][Column] != -1)
					Column++;
				// ������A�����Ēu�����Ƃ��ł���ꍇ
				if (Column <= 7 && Board[Row][Column] == -1) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 7,0��
		if (flag == false) {
			Row = 7; Column = 0;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Column <= 7 && Board[Row][Column] != -1)
					Column++;
				// ������A�����Ēu�����Ƃ��ł���
				if (Column <= 7 && Board[Row][Column] == -1) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 0,7��
		if (flag == false) {
			Row = 0; Column = 7;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Column >= 0 && Board[Row][Column] != -1)
					Column--;
				// ������A�����Ēu�����Ƃ��ł���
				if (Column >= 0 && Board[Row][Column] == -1) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 7,7��
		if (flag == false) {
			Row = 7; Column = 7;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Column >= 0 && Board[Row][Column] != -1)
					Column--;
				// ������A�����Ēu�����Ƃ��ł���
				if (Column >= 0 && Board[Row][Column] == -1) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 0,0��
		if (flag == false) {
			Row = 0; Column = 0;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Row <= 7 && Board[Row][Column] != -1)
					Row++;
				// ������A�����Ēu�����Ƃ��ł���
				if (Row <= 7 && Board[Row][Column] == -1)	{
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 0,7��
		if (flag == false) {
			Row = 0; Column = 7;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Row <= 7 && Board[Row][Column] != -1)
					Row++;
				// ������A�����Ēu�����Ƃ��ł���
				if (Row <= 7 && Board[Row][Column] == -1)	{
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 7,0��
		if (flag == false) {
			Row = 7; Column = 0;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Row >= 0 && Board[Row][Column] != -1)
					Row--;
				// ������A�����Ēu�����Ƃ��ł���
				if (Row >= 0 && Board[Row][Column] == -1)	{
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}
		// 7,7��
		if (flag == false) {
			Row = 7; Column = 7;
			if (Board[Row][Column] == -1 || Board[Row][Column] == Koma) {
				while (Row >= 0 && Board[Row][Column] != -1)
					Row--;
				// ������A�����Ēu�����Ƃ��ł���
				if (Row >= 0 && Board[Row][Column] == -1)	{
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					if (check(Row, Column, Koma, Change, 0) > 0)
						flag = true;
				}
			}
		}

		// ------------------------------------------------------------------------------
		// ���ŏ��ɐݒ肵�Ă���R�}�̎��͂��Ƃ���@�����
		if (flag == false) {
			int max = 1, w = 0;						// �ő唽�]����0�N���A
			for (int i = 2; i <= 5; i++) {
				for (int j = 2; j <= 5; j++) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					// �ő唽�]���ȏ�ɒu�����Ƃ��ł���ꍇ
					if ((w = check(i, j, Koma, Change, 0)) >= max) {	
						// �ő唽�]�����������C�܂��͍ő吔�Ɠ����ŗ�����0.5�ȏ�̏ꍇ
						if (w > max || w == max && Math.random( ) >= 0.5) {
							max = w;				// ���]�����ő唽�]���Ƃ���
							Row = i;				// ���̈ʒu�̍s�Ɨ��ۊ�
							Column = j;
							flag = true;
						}
					}
				}
			}
		}

		// ------------------------------------------------------------------------------
		// �����͂���юl���̓����ȊO�ōł���������ʒu�����
		// �@���]�ő吔�������ꍇ�́C2����1�̊m���łǂ��炩��I������
		if (flag == false) {
			int max = 1, w = 0;						// �ő唽�]����0�N���A
			for (int i = 0; i <= 7; i++) {
				for (int j = 0; j <= 7; j++) {
					if (i == 0 || i == 7 || j == 0 || j == 7)  		// ����
						continue;
					if ((i == 1 && j == 1) || (i == 1 && j == 6)	// �l���̎΂ߓ���
					 || (i == 6 && j == 1) || (i == 6 && j == 6))
						continue;

					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					// �ő唽�]���ȏ�ɒu�����Ƃ��ł���ꍇ
					if ((w = check(i, j, Koma, Change, 0)) >= max) {	
						// �ő唽�]�����������C�܂��͍ő吔�Ɠ����ŗ�����0.5�ȏ�̏ꍇ
						if (w > max || w == max && Math.random( ) >= 0.5) {
							max = w;				// ���]�����ő唽�]���Ƃ���
							Row = i;				// ���̈ʒu�̍s�Ɨ��ۊ�
							Column = j;
							flag = true;
						}
					}
				}
			}
		}

		// ------------------------------------------------------------------------------
		// ���ł���������ʒu�����
		// �@���]�ő吔�������ꍇ�́C2����1�̊m���łǂ��炩��I������
		if (flag == false) {
			int max = 1, w = 0;						// �ő唽�]����0�N���A
			for (int i = 0; i <= 7; i++) {
				for (int j = 0; j <= 7; j++) {
					Board2work( );					// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
					// �ő唽�]���ȏ�ɒu�����Ƃ��ł���ꍇ
					if ((w = check(i, j, Koma, Change, 0)) >= max) {
						// �ő唽�]�����������C�܂��͍ő吔�Ɠ����ŗ�����0.5�ȏ�̏ꍇ
						if (w > max || w == max && Math.random( ) >= 0.5) {
							max = w;				// ���]�����ő唽�]���Ƃ���
							Row = i;				// ���̈ʒu�̍s�Ɨ��ۊ�
							Column = j;
							flag = true;
						}
					}
				}
			}
		}

		// ------------------------------------------------------------------------------
		if (flag == true) {							// �u�����ꍇ
			Board2work( );							// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
			check(Row, Column, Koma, Change, 1);	// ���]����
			work2Board( );							// ��Ɨp�z��̓��e���I�Z���ՂɃR�s�[
			repaint( );
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
	// �w�肵���ʒu�ɃR�}���u���邩�`�F�b�N ---------------------------------------------
	int check(int Row, int Column, int Koma, int Change, int hanten) {
		int totalcount;								// ���]����
		int count[ ] = new int[8];					// �e�����̔��]��

		if (Work[Row][Column] != -1)				// ���łɃR�}���u���Ă���ꍇ
			return 0;

		count[0] = hantenshori(Row, Column, -1,  0, Koma, Change, hanten);// ��`�F�b�N
		count[1] = hantenshori(Row, Column, -1,  1, Koma, Change, hanten);// �E��`�F�b�N
		count[2] = hantenshori(Row, Column,  0,  1, Koma, Change, hanten);// �E�`�F�b�N
		count[3] = hantenshori(Row, Column,  1,  1, Koma, Change, hanten);// �E���`�F�b�N
		count[4] = hantenshori(Row, Column,  1,  0, Koma, Change, hanten);// ���`�F�b�N
		count[5] = hantenshori(Row, Column,  1, -1, Koma, Change, hanten);// �����`�F�b�N
		count[6] = hantenshori(Row, Column,  0, -1, Koma, Change, hanten);// ���`�F�b�N
		count[7] = hantenshori(Row, Column, -1, -1, Koma, Change, hanten);// ����`�F�b�N

		totalcount = 0;								// ���]����0�N���A
		for (int i = 0; i < 8; i++)					// 8�����̔��]����
			totalcount += count[i];

		if (totalcount > 0 && hanten == 1) {		// ���ۂɒu���ꍇ�C���]����
			Work[Row][Column] = Koma;				// ��ƔՂ̎w��ʒu�ɃR�}��u��
		}

		return totalcount;							// ���]������Ԃ�
	}
	// ���]���� -------------------------------------------------------------------------
	int hantenshori(int Row, int Column, int Rowstep, 
					int Columnstep, int Koma, int Change, int hanten) {
		int count;

		count = 1;									// �ׂ̈ʒu����`�F�b�N
		// �s���͈͓��C�񂪔͈͓��C��łȂ��C�����̃R�}�łȂ��ԁC�J��Ԃ�
		while (Row + Rowstep * count >= 0 && Row + Rowstep * count <= 7 &&
			   Column + Columnstep * count >= 0 && Column + Columnstep * count <= 7 &&
			   Work[Row + Rowstep * count][Column + Columnstep * count] != -1 &&
			   Work[Row + Rowstep * count][Column + Columnstep * count] != Koma) {
			count++;							// ���̈ʒu���`�F�b�N
		}

		// ���]�����P���傫���C�s���͈͓��C�񂪔͈͓��C�����̃R�}�̏ꍇ
		if (count > 1 && 																			Row + Rowstep * count >= 0 && Row + Rowstep * count <= 7 &&
			Column + Columnstep * count >= 0 && Column + Columnstep * count <= 7 &&
			Work[Row + Rowstep * count][Column + Columnstep * count] == Koma) {

			if (hanten == 1) {						// ���]�w�����o�Ă���ꍇ
				ReverseSound.play( );				// ���]�T�E���h
				count = 1;							// �ׂ���

				// �����̃R�}�łȂ��ԁC�J��Ԃ�
				while (Work[Row+Rowstep*count][Column+Columnstep*count] != Koma) {
					// �R�}�̏�Ԃ��P�ω�
					Work[Row + Rowstep * count][Column + Columnstep * count] += Change;
					count++;						// ���̃R�}
				}
				count--;							// �`�F�b�N��1�s������������
			}
		}
		else
			count = 0;								// ���]�ł��Ȃ������ꍇ

		return count;								// ���]��
	}
	// �R�}�𔽓]�����鏈�� -------------------------------------------------------------
	int ChangeKoma(int Change) {
		int flag = 0;								// ��ԕω��t���O

		Komacount[0] = Komacount[1] = 0;			// �R�}�J�E���g�N���A
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (Board[i][j] == 0)				// ���R�}�̏ꍇ
					Komacount[0]++;					// ���R�}�̑���
				else if (Board[i][j] == 7)			// ���R�}�̏ꍇ
					Komacount[1]++;					// ���R�}�̑���

				// ��̏ꏊ�łȂ��C���R�}�łȂ��C���R�}�łȂ��C�ω��r���̃R�}�̏ꍇ
				if (Board[i][j] != -1 && Board[i][j] != 0 && Board[i][j] != 7) {
					Board[i][j] += Change;			// �R�}��ω�������
					flag = 1;						// ��Ԃ��ω�����
				}
			}
		}
		return flag;								// ��ԕω���Ԃ�
	}

	// �I�Z���Ղ̓��e����Ɣz��work�փR�s�[ --------------------------------------------
	void Board2work( ) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				Work[i][j] = Board[i][j];
	}
	// ��Ɣz��work�̓��e���I�Z���Ղ̔z��փR�s�[ --------------------------------------
	void work2Board( ) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				Board[i][j] = Work[i][j];
	}
	// ��Ɣz��work�̓������N���A ------------------------------------------------------
	void WorkInsideClear( ) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if (!(i == 0 || i == 7 || j == 0 || j ==7))
					Work[i][j] = -1;
	}

	// MouseListener�C���^�[�t�F�[�X������ ----------------------------------------------
	// �l�ԑ����}�E�X���N���b�N�������̏���
    public void mousePressed(MouseEvent evt) {
		if (Koma != 0)								// �l�Ԃ̔ԂłȂ��ꍇ
			return;

 		if ((evt.getModifiers( ) & InputEvent.BUTTON3_MASK) != 0) {	// �E�{�^���C�p�X
			Koma = 6;								// ���́C�R���s���[�^���̔�
			return;
		}

        Column = (evt.getX( ) - 20) / 50;			// �}�E�X�̈ʒu���I�Z���Ղ̍s��ɕϊ�
		Row = (evt.getY( ) - 20) / 50;

		Change = -1;								// �R�}�ω���-1�i���V���甒�O�Ɍ����j
		Board2work( );								// �I�Z���Ղ̓��e����Ɨp�̔ՂɃR�s�[
		if (check(Row, Column, Koma, Change, 1) != 0) {	// �u�����Ƃ��ł���ꍇ�C���]����
			work2Board( );							// ��Ɨp�z��̓��e���I�Z���ՂɃR�s�[
			Koma = 6;								// �ω���C�R���s���[�^���̔�
		}
	}

    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
}
