import java.applet.*;								// Applet
import java.awt.*;									// Graphics, Image, MediaTracker
import java.awt.image.*;							// MemoryImageSource

public class ImageDisplay extends Applet implements Runnable {
	int AppletWidth, AppletHeight;					// �A�v���b�g�̃T�C�Y
	Thread thread = null;							// �X���b�h	
	int Number;										// �摜��
	Image image[ ] = new Image[30];					// �`��摜
	Image WorkImage;								// ��Ɨp�C���[�W
	Graphics WorkGraphics;							// ��Ɨp�O���t�B�b�N�X
	Image[ ] FadeImage = new Image[16];				// ���F�t�F�[�h�C���[�W�i����16�i�K�j

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		AppletWidth = getSize( ).width;				// �A�v���b�g�̕�
		AppletHeight = getSize( ).height;			// �A�v���b�g�̍���

		WorkImage = createImage(AppletWidth, AppletHeight);	// ��ƃC���[�W�쐬
		WorkGraphics = WorkImage.getGraphics( );			// ��ƃO���t�B�b�N�X�擾

		// �p�����[�^���f�[�^����
		Number = Integer.parseInt(getParameter("number"));	// �摜��
		MediaTracker mediatracker = new MediaTracker(this);
		for (int i = 0; i < Number; i++) {					// �摜�f�[�^����
			image[i] = getImage(getCodeBase( ), getParameter("image" + i));
			mediatracker.addImage(image[i], 0);
		}
		try {
			mediatracker.waitForID(0);				// ���f�B�A�g���b�J�œ��͊Ď�
		}
		catch (InterruptedException e) {
		}

		makeFadeImages( );							// �t�F�[�h�̃C���[�W�쐬
	}
	// �A�v���b�g�J�n -------------------------------------------------------------------
	public void start( ) {
		thread = new Thread(this);					// �X���b�h����
		thread.start( );							// �X���b�h�X�^�[�g
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(WorkImage, 0, 0, this);			// ��ƃC���[�W��`��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		int current = 0,							// ���݂̉摜�ԍ�
			next;									// ���̉摜�ԍ�
		while (thread != null) {					// �X���b�h�����݂��Ă����
			do {
				next = (int)(Math.random( ) * Number);	// �\���摜�������_���ɐݒ�
			} while (current == next);					// �\���摜��������

			int effect = (int)(Math.random( ) * 21);	// �\���p�^�[���������_���ɐݒ�

			switch (effect) {
				case  0: 	// ������X�N���[���C��
							Scroll_In_Display(image[current], image[next],  2,  0);
						 	break;
				case  1: 	// �ォ��
							Scroll_In_Display(image[current], image[next],  0,  2);
						 	break;
				case  2: 	// �E����
							Scroll_In_Display(image[current], image[next], -2,  0);
						 	break;
				case  3: 	// ������
							Scroll_In_Display(image[current], image[next],  0, -2);
						 	break;

				case  4: 	// �E�ɃX�N���[���A�E�g
							Scroll_Out_Display(image[current], image[next],  2,  0);
						 	break;
				case  5: 	// ����
							Scroll_Out_Display(image[current], image[next],  0,  2);
						 	break;
				case  6: 	// ����
							Scroll_Out_Display(image[current], image[next], -2,  0);
						 	break;
				case  7: 	// ���
							Scroll_Out_Display(image[current], image[next],  0, -2); 
						 	break;

				case  8: 	// �����甍�����
							Tear_Display(image[current], image[next],  2,  0);
						 	break;
				case  9: 	// �ォ��
							Tear_Display(image[current], image[next],  0,  2);
						 	break;
				case 10: 	// �E����
							Tear_Display(image[current], image[next], -2,  0);
						 	break;
				case 11: 	// ������
							Tear_Display(image[current], image[next],  0, -2);
							break;

				case 12: 	// �������獶�E�ɃI�[�v��
							Open_Close_Display(image[current], image[next],  2,  0);
						 	break;
				case 13: 	// ��������㉺��
							Open_Close_Display(image[current], image[next],  0,  2);
						 	break;
				case 14: 	// ���E�̒[���璆����
							Open_Close_Display(image[current], image[next], -2,  0);
							break;
				case 15: 	// �㉺�̒[���璆����
							Open_Close_Display(image[current], image[next],  0, -2);
							break;
				case 16: 	// ��������S�̂�
							Open_Close_Display(image[current], image[next],  2,  2);
						 	break;
				case 17: 	// �S���̒[���璆����
							Open_Close_Display(image[current], image[next], -2, -2);
						 	break;

				case 18: 	// �t�F�[�h�A�E�g�E�C��
							Fade_Display(image[current], image[next]);
						 	break;

				case 19: 	// ���U�C�N����
							Mosaic_Display(image[current], image[next]);
						 	break;

				case 20: 	// �u���C�h����
							Blind_Display(image[current], image[next], 2);
						 	break;
			}
			current = next;							// ���̉摜��`��ԍ��Ƃ���

			try {									// ���̉摜�\���܂ł̑҂�����
				Thread.sleep(3000);					// �X���b�h�X���[�v�@3�b�ԁC�摜�\��
			} catch (InterruptedException e) {
			}
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
	// �X�N���[���C�� -------------------------------------------------------------------
	// �摜�̏c���̃T�C�Y�͋����ł��邱��
	void Scroll_In_Display(Image current, Image next, int dx, int dy) {
		// current:���݂̉摜 next:���̉摜 dx:x�����ړ��X�e�b�v dy:�������ړ��X�e�b�v

		int x = 0, y = 0;							// �`��ʒu
		// �`��X�e�b�v�ɂ���ĕ`�揉���ʒu�ݒ�
		if (dx > 0)									// �`��x�X�e�b�v�{�@�E�Ɉړ�
			x = -AppletWidth;						// ��������X�N���[���C��
		else if (dx < 0)							// �`��x�X�e�b�v�|�@���Ɉړ�
			x = AppletWidth;						// �E������X�N���[���C��
		else if (dy > 0)							// �`��y�X�e�b�v�{�@���Ɉړ�
			y = -AppletHeight;						// �㑤����X�N���[���C��
		else if (dy < 0)							// �`��y�X�e�b�v�[�@��Ɉړ�
			y = AppletHeight;						// ��������X�N���[���C��

		do {
			x += dx;								// �`��ʒu���ړ�
			y += dy;
			WorkGraphics.drawImage(current, 0, 0, this);	// ����̉摜�`��
			WorkGraphics.drawImage(next, x, y, this);		// ����̉摜�`��
			repaint( );
			try {
				Thread.currentThread( ).sleep(50);
			} catch (InterruptedException e) {
			}
		} while (x != 0 || y != 0);
	}
	// �X�N���[���A�E�g -----------------------------------------------------------------
	void Scroll_Out_Display(Image current, Image next, int dx, int dy) {
		// current:���݂̉摜 next:���̉摜 dx:x�����ړ��X�e�b�v dy:�������ړ��X�e�b�v

		int x = 0, y = 0;									// �`��ʒu
		while (x >= -AppletWidth && x <= AppletWidth		// �`��ʒu���A�v���b�g�͈͓�
			   && y >= -AppletHeight && y <= AppletHeight) {
			WorkGraphics.drawImage(next, 0, 0, this);		// ����̉摜��`��
			WorkGraphics.drawImage(current, x, y, this);	// ����̉摜��`��
			repaint( );
			try {
				Thread.currentThread( ).sleep(50);
			} catch (InterruptedException e) {
			}
			x += dx;										// �`��ʒu���ړ�
			y += dy;
		}
	}
	// �������悤�ɕ\���i�I�[�o�[���C�g�j----------------------------------------------
	void Tear_Display(Image current, Image next, int dx, int dy) {
		// current:���݂̉摜 next:���̉摜 dx:x�����ړ��X�e�b�v dy:�������ړ��X�e�b�v

		WorkGraphics.drawImage(current, 0, 0, this);// ����̉摜��`��

		int x = 0, y = 0;							// �`��ʒu
		int w = AppletWidth;						// �`�敝
		int h = AppletHeight;						// �`�捂��
		if (dx > 0) {
			x = 0;  w = 0;
		}
		else if (dx < 0) {
			x = AppletWidth;  w = 0;
		}
		else if (dy > 0) {
			y = 0;  h = 0;
		}
		else if (dy < 0) {
			y = AppletHeight;  h = 0;
		}
		do {
			WorkGraphics.clipRect(x, y, w, h);  		// �`��̈���N���b�v
			WorkGraphics.drawImage(next, 0, 0, null);	// ���̉摜�`��
			repaint( );
			try {
				Thread.currentThread( ).sleep(50);
			} catch (InterruptedException e) {
			}
			WorkGraphics = WorkImage.getGraphics( );	// �ēx�`��̈���擾

			if (dx > 0)								// �`��ʒu���ړ�
				w += dx;							// ���n��������E�ɔ������
			else if(dx < 0) {
				w -= dx; x += dx;					// ���n���E���獶�ɔ������
			}
			else if (dy > 0)						// ���n���ォ�牺�ɔ������
				h += dy;
			else if (dy < 0) {
				h -= dy; y += dy;					// ���n���������ɔ������
			}
		} while (w <= AppletWidth && h <= AppletHeight);
	}
	// �Z���^�[�ɑ΂��ẴI�[�v���N���[�Y�\�� -------------------------------------------
	void Open_Close_Display(Image current, Image next, int dx, int dy) {
		// current:���݂̉摜 next:���̉摜 dx:x�����ړ��X�e�b�v dy:�������ړ��X�e�b�v

		int x = 0, y = 0;							// �`��ʒu
		int w = AppletWidth;						// �`�敝
		int h = AppletHeight;						// �`�捂��
		// dx,dy�����̏ꍇ�C���S����I�[�v�����Ȃ���\��
		//        ���̏ꍇ�C�O������N���[�Y���Ȃ���w�i�ɕ\��
		if (dx > 0) {
			x = AppletWidth / 2;  w = 0;
		}
		if (dy > 0) {
			y = AppletHeight / 2;  h = 0;
		}

		if (dx > 0 || dy > 0)
			WorkGraphics.drawImage(current, 0, 0, this);	// ���݂̉摜��`��

		int flag = 1;										// �J��Ԃ��t���O
		do {
			if (dx < 0 || dy < 0)  							// �N���[�Y���Ă����ꍇ
				WorkGraphics.drawImage(next, 0, 0, this);

			WorkGraphics.clipRect(x, y, w, h);  			// �`��̈���N���b�v

			if (dx > 0 || dy > 0)  							// ���S����I�[�v�����Ă����ꍇ
				WorkGraphics.drawImage(next, 0, 0, this);	// ���̉摜��������茻���
			else
				WorkGraphics.drawImage(current, 0, 0, this);// ���݂̉摜�������ɋ��܂�

			repaint( );
			try {
				Thread.currentThread( ).sleep(50);
			} catch (InterruptedException e) {
			}

			WorkGraphics = WorkImage.getGraphics( );// �ēx�`��̈���擾

			x -= dx;  w += (dx * 2);
			y -= dy;  h += (dy * 2);

			// �����𑱂��邩�ǂ����̃`�F�b�N
			flag = 0;
			if (dx > 0 && w <= AppletWidth || dy > 0 && h <= AppletHeight ||
				dx < 0 && w >= 0  || dy < 0 && h >= 0)
				flag = 1;

			// w, h ���}�C�i�X�ɂȂ�ꍇ, �܂��̓T�C�Y���傫���Ȃ�ꍇ
			if (flag == 0) {	
				WorkGraphics.drawImage(next, 0, 0, this);
				repaint( );
			}
		} while (flag == 1);
	}
	// �t�F�[�h�A�E�g�E�C�� -------------------------------------------------------------
	void Fade_Display(Image current, Image next) {
		// current:���݂̉摜 next:���̉摜

		for (int i = 0; i < 16; i++) {						// ���X�ɏ���
			WorkGraphics.drawImage(current, 0, 0, this);	// ���݂̉摜��`��
			for (int y = 0; y < AppletHeight; y += 64) {
				for (int x = 0; x < AppletWidth; x += 64)
					WorkGraphics.drawImage(FadeImage[i], x, y, this);
			}

			repaint( );
			try {
				Thread.currentThread( ).sleep(100);
			} catch (InterruptedException e) { };
		}

		for (int i = 16-1; i >= 0; i--) {					// ���X�ɕ\��
			WorkGraphics.drawImage(next, 0, 0, this);		// ���̉摜��`��
			for (int y = 0; y < AppletHeight; y += 64)	{
				for (int x = 0; x < AppletWidth; x += 64)
					WorkGraphics.drawImage(FadeImage[i], x, y, this);
			}
			repaint( );
			try {
				Thread.currentThread( ).sleep(100);
			} catch (InterruptedException e) { }
		}
	}
	// �t�F�[�h�C���[�W�쐬 -------------------------------------------------------------
	void makeFadeImages( ) {
		// 64 x 64�̗̈�̃A���t�@�l�i�s�����x�j��16�i�K�쐬
		int[ ][ ] pix = new int[16][64 * 64];

		for (int i = 0; i < 16; i++) {				// 0x00 ���� <-----> �s���� 0xff
			int p = 0;
			for  (int  y  =  0;  y <  64;  y++) {
				for  (int  x  =  0;  x  <  64;  x++)
					pix[i][p++]  =  (255 * i / 16)  <<  24 | 0x0ffffff;
													// �t�F�[�h�F��0x0ffffff�i���F)
			}
			MemoryImageSource mis = new MemoryImageSource(64, 64, pix[i], 0, 64);
			FadeImage[i] = createImage(mis);
		}
	}
/*
public MemoryImageSource(int  w, int  h, int  pix[ ],int  off, int  scan)				  
�@int �^�̔z�񂩂�C���[�W�I�u�W�F�N�g�̃f�[�^���쐬����C���[�W�v���f���[�T�̃I�u�W�F�N�g���쐬���܂��B���W (i, j) �̃s�N�Z���́C�z��̃C���f�b�N�X j �~ scan + i + offset �̗v�f�ɂ���Č��肳��܂��B	�C���[�W�̓f�t�H���g�� RGB �J���[���f�����g�p���č쐬����܂��B
	�p�����[�^
		w - �C���[�W�̕�
		h - �C���[�W�̍���
		pix - �s�N�Z���l�̔z��
		off - �ŏ��̃s�N�Z���̔z����I�t�Z�b�g
		scan - �z����ł� 1 �s������̃s�N�Z����

public static ColorModel getRGBdefault( )												  
�@�f�t�H���g�� AWT (Abstract Window Toolkit) �J���[���f����Ԃ��܂��BAWT �́C32 �r�b�g�̐����Ńs�N�Z���l�������܂��B24 �` 31 �r�b�g�̓A���t�@�l�i�s�����x�j�C16 �` 23 �r�b�g�͐ԁC8 �` 15 �r�b�g�͗΁C0 �` 7 �r�b�g�͐̒l�ł��B���̃I�u�W�F�N�g�͓��`���̃s�N�Z���l����A���t�@�l�C�ԁC�΁C����ѐ̒l�����o���̂Ɏg�p�ł��܂��B
*/
	// ���U�C�N���ɕ\�� -----------------------------------------------------------------
	void Mosaic_Display(Image current, Image next) {
		// current:���݂̉摜 next:���̉摜

		// ���U�C�N�ɕ�����c�Ɖ��̐�
		int MSIZE = 32;								// ���U�C�N�̏c���̃T�C�Y
		int row = AppletHeight / MSIZE;				// �s
		int col = AppletWidth / MSIZE;				// ��
		if (row * MSIZE < AppletHeight)				// ���r���[�̏ꍇ�؂�グ
			row++;
		if (col * MSIZE < AppletWidth)				// ���r���[�̏ꍇ�؂�グ
			col++;

		Point mosaic_point[ ] = new Point[row * col];

		for (int i = 0; i < mosaic_point.length; i++)	// �ʒu����ݒ�
			mosaic_point[i] = new Point((i % col)*MSIZE, (i / col)*MSIZE);

		// ���U�C�N�ɕ������e�z��̃|�C���g�������_���ɓ��ւ���
		for (int i = 0; i < mosaic_point.length; i++) {
			int rp = (int)(Math.random( ) * mosaic_point.length);
			// i�Ԗڂ�rp�Ԗڂ����ւ�
			Point temp = mosaic_point[i];
			mosaic_point[i] = mosaic_point[rp];
			mosaic_point[rp] = temp;
		}
		
		WorkGraphics.drawImage(current, 0, 0, this);

		for (int i = 0; i < mosaic_point.length; i++) {		// �����_���ɓ��ւ������e��`��
			int xp = mosaic_point[i].x;
			int yp = mosaic_point[i].y;
			// �N���b�v�̈�ݒ�
			WorkGraphics.clipRect(xp, yp, MSIZE, MSIZE);	// �`��̈���N���b�v
			WorkGraphics.drawImage(next, 0, 0, null);		// ���̉摜��`��
			repaint( );
			try {
				Thread.currentThread( ).sleep(50);
			} catch (InterruptedException e) {
			}
			WorkGraphics = WorkImage.getGraphics( ); 		// �ēx�`��̈���擾
		}
	}
	// �u���C���h�\�� -------------------------------------------------------------------
	void Blind_Display(Image current, Image next, int haba) {
		// current:���݂̉摜 next:���̉摜 haba:�u���C���h��

		int left = 0;
		int right = AppletWidth - 1;
		WorkGraphics.drawImage(current, 0, 0, this);
		do {
			// �N���b�v�̈�ݒ�
			WorkGraphics.clipRect(left, 0, 1, AppletHeight);	// �`��̈���N���b�v
			WorkGraphics.drawImage(next, 0, 0, null);			// �����玟�̉摜��
			repaint( );
			WorkGraphics = WorkImage.getGraphics( ); 			// �ēx�`��̈���擾
			WorkGraphics.clipRect(right, 0, 1, AppletHeight);	// �`��̈���N���b�v
			WorkGraphics.drawImage(next, 0, 0, null);			// �E���玟�̉摜��
			repaint( );
			WorkGraphics = WorkImage.getGraphics( ); 			// �ēx�`��̈���擾
			try {
				Thread.currentThread( ).sleep(20);
			} catch (InterruptedException e) {
			}
			left += haba;										// ���̈ʒu���E�Ɉړ�
			right -= haba;										// �E�̈ʒu�����Ɉړ�
		} while (left < AppletWidth || right > 0);				// �O�ɏo���ꍇ
	}
}
