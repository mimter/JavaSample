import java.awt.*;					// Graphics, Frame, TextArea, Color, FileDialog, Menu etc
import java.awt.event.*;			// ActionListener, ActionEvent
import java.io.*;					// FileReader, BufferedReader, FileWriter, PrintWriter, IOException
import java.util.*;					// StringTokenizer

// �������N���X -------------------------------------------------------------------------
class Memo {
	public static void main(String args[ ]) {
		MemoFrame jed = new MemoFrame("Memo", 80, 50);
		jed.init( );
	}
}

// �������t���[���N���X -----------------------------------------------------------------
class MemoFrame extends Frame implements ActionListener, WindowListener {
								  // ActionListener�� WindowListener�C���^�[�t�F�[�X����
	TextArea textarea;
	int width=500, height=300;						// �e�L�X�g�G���A�̃T�C�Y�ݒ�
	// �R���X�g���N�^ -------------------------------------------------------------------
	MemoFrame(String title, int Cols, int Rows) {
		setTitle(title);							// �t���[���̃^�C�g��
		textarea = new TextArea(Cols, Rows);		// �e�L�X�g�G���A�̃T�C�Y
		textarea.setBackground(new Color(180, 220, 255));	// �w�i�F�ݒ�
		add(textarea);								// �t���[���Ƀe�L�X�g�G�����ǉ�
		MenuBar menubar = new MenuBar( );			// ���j���[�o�[�ݒ�
		Menu menu_file = new Menu("File");			// ���j���[�t�B�[���h�ݒ�
		MenuItem menuItem_load = new MenuItem("Load");	// ���j���[���ڐݒ�
		MenuItem menuItem_save = new MenuItem("Save");
		MenuItem menuItem_print = new MenuItem("Print");
		MenuItem menuItem_quit = new MenuItem("Quit");
		menuItem_load.addActionListener(this);		// ���j���[���ڂɃ��X�i�[�t��
		menuItem_save.addActionListener(this);
		menuItem_print.addActionListener(this);
		menuItem_quit.addActionListener(this);
		menu_file.add(menuItem_load);				// ���j���[�t�B�[���h�Ƀ��j���[���ړ\�t��
		menu_file.add(menuItem_save);
		menu_file.add(menuItem_print);
		menu_file.add(menuItem_quit);
		menubar.add(menu_file);						// ���j���[�t�B�[���h�����j���[�o�[�ɓ\�t
		setMenuBar(menubar);						// ���j���[�o�[���t���[���ɓ\�t
	}
	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		show( );									// �t���[���\��
		setSize(width, height);						// �t���[���T�C�Y�ݒ�
		addWindowListener(this);					// �E�B���h�E���X�i�[�ǉ�
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
   	public void actionPerformed(ActionEvent evt) {
 		MenuItem mi = (MenuItem)evt.getSource( );
 		String label = mi.getLabel( );
 		if (label.equals("Load")) {					// ���[�h
			// �t�@�C���_�C�A���O���I�[�v��
			FileDialog dialog = new FileDialog(this, "OPEN", FileDialog.LOAD);
			dialog.show( );							// �_�C�A���O�\��
			String filename = dialog.getFile( );	// �t�@�C�����擾
			if (filename == null)
				return;
			filename = dialog.getDirectory( ) + 
 			System.getProperty("file.separator") + filename;
			try {
				textarea.setText("");				// �e�L�X�g�G�������N���A
				// �w�肵���t�@�C������FileReader�̃I�u�W�F�N�g�쐬
				FileReader fr = new FileReader(filename);
				// �s�P�ʂɓǍ��ނ��߂�BufferedReader�̃I�u�W�F�N�g�쐬
				BufferedReader br = new BufferedReader(fr);
				String buf;
				while ((buf = br.readLine( )) != null) {	// �s�P�ʂɓǂݍ���
					// �e�L�X�g�G������1�s�̃f�[�^�Ɖ��s��ǉ��}��
					textarea.append(buf + System.getProperty("line.separator"));
				}
				br.close( );
			} catch (IOException e) {
			}
		}
		else if (label.equals("Save")) {			// �Z�[�u
			// �t�@�C���_�C�A���O���I�[�v��
			FileDialog dialog = new FileDialog(this, "OPEN", FileDialog.LOAD);
			dialog.show( );							// �t�@�C���_�C�A���O��\��
			String filename = dialog.getFile( );	// �t�@�C�����擾
			if (filename == null)
				return;
			filename = dialog.getDirectory( ) +
						System.getProperty("file.separator") + filename;
			try {
				// �w�肵���t�@�C������FileWriter�̃I�u�W�F�N�g�쐬
				FileWriter fw = new FileWriter(filename);
				// ����unicode�������R���o�[�g����X�g���[���쐬
				PrintWriter pw = new PrintWriter(fw);
				pw.print(textarea.getText( ));
				pw.close( );
			} catch (IOException e) {
			}
		}
		else if (label.equals("Print")) {
			// PrintJob�̃I�u�W�F�N�g�쐬
			PrintJob printJob = 
 				getToolkit( ).getPrintJob(this, "Printing Text", null);
			if (printJob == null)
				return;
			Graphics printGraphics = null;
			setSize(width, height);					// �t���[���T�C�Y�ݒ�
			textarea.setSize(width, height);		// ����T�C�Y�ݒ�
			// �e�L�X�g�G�����̃e�L�X�g�擾
			String sentence = textarea.getText( );
			String delimiter = "\n";				// ��؂蕶���擾
			StringTokenizer st = new StringTokenizer(sentence, delimiter);
			int linecount = 0;						// �s�J�E���g�O�N���A
			while  (st.hasMoreTokens( ))  {
				if (linecount % 50 == 0) {
					if (linecount > 0) {
						textarea.printAll(printGraphics);	// �������
				       	printGraphics.dispose( );	// �p����r�o����
					}
					textarea.setText("");			// �e�L�X�g�G�����N���A
 					// PrintJob�̃O���t�B�b�N�R���e�L�X�g�擾
					printGraphics =  printJob.getGraphics( );	
				}
				String s = st.nextToken( );			// 1�s���o��
				linecount++;						// �s���J�E���g�A�b�v
				String number = "000" + String.valueOf(linecount);
 				// �s�ԍ���3���ɕҏW
				String linenumber = number.substring(number.length( ) - 3);
 				// �s�ԍ��C1�s���̃f�[�^�Ƌ�؂�
				textarea.append(linenumber + "|" + s + delimiter);
				}
			textarea.printAll(printGraphics);		// �������
		   	printGraphics.dispose( );				// �p����r�o����
	  		printJob.end( );						// ����I��
			textarea.setText(sentence);				// ���͂����ɖ߂�
		}
		else if (label.equals("Quit")) {
			dispose( );								// �t���[�����g�p�����������
			System.exit(1);							// �v���O�����I��
		}
	}
 	// WindowListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
	public void windowOpened(WindowEvent evt) { }
	public void windowClosing(WindowEvent evt) {
		dispose( );  								// �t���[���̔p��
	}
	public void windowClosed(WindowEvent evt) {
		System.exit(0);								// �v���O�����I��
	}
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }
}
