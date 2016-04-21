import java.applet.*;			// Applet
import java.awt.*;				// Font, TextField, TextArea, Label, Color, Button
import java.awt.event.*;		// ActionListener, ActionEvent
import java.io.*;				// InputStream, InputStreamReader, BufferedReader etc
import java.net.*;				// Socket, URL, URLConnection, UnknownHostException etc
import java.util.*; 			// Calendar

public class Keijiban extends Applet implements ActionListener {
	TextArea DisplayArea;							// �\���G���A
	TextField TitleField;							// �^�C�g�����̓t�B�[���h
	TextField NameField;							// ���e�Җ����̓t�B�[���h
	TextArea InputArea;								// ���e���̓G���A
	Button SendButton;								// ���M�{�^��
	String filename;								// �f���t�@�C����

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		filename = getParameter("keijiban");		// HTML�p�����[�^

		setLayout(null);							// ���R���C�A�E�g�ݒ�

		Label keijibanlabel = new Label("�f�@�@���@�@��");
		add(keijibanlabel);	keijibanlabel.setBounds(280, 10, 140, 20);
		keijibanlabel.setFont(new Font("TimesRoman", Font.BOLD, 20));
		// �\���G���A
		DisplayArea = new TextArea(20, 100);		add(DisplayArea);
		DisplayArea.setBounds(10, 40, 680, 330);
		DisplayArea.setEditable(false);				// �����݋֎~
		DisplayArea.setBackground(new Color(0, 102, 0));
		DisplayArea.setForeground(Color.white);
		// �^�C�g�����x��
		Label titlelabel = new Label("�^�C�g��");	add(titlelabel);
		titlelabel.setBounds(10, 380, 50, 20);
		// 	�^�C�g�����̓t�B�[���h
		TitleField = new TextField(20);				add(TitleField);
		TitleField.setBounds(60, 380, 400, 20);
		TitleField.setBackground(new Color(153, 255, 204));
		// ���O���x��
		Label namelabel = new Label("���e�Җ�");	add(namelabel);
		namelabel.setBounds(470, 380, 50, 20);
		// ���O���̓t�B�[���h
		NameField = new TextField(16);				add(NameField);	
		NameField.setBounds(530, 380, 100, 20);
		NameField.setBackground(new Color(153, 255, 204));
		// ���e���x��
		Label naiyoulabel = new Label("���@�@�e");	add(naiyoulabel);
		naiyoulabel.setBounds(10, 410, 45, 15);
		// ���e���̓G���A
		InputArea = new TextArea(5, 100);			add(InputArea);	
		InputArea.setBounds(60, 410, 570, 80);
		InputArea.setBackground(new Color(153, 255, 204));
		// ���M�{�^��
		SendButton = new Button("���M");
		add(SendButton);
		SendButton.setBounds(640, 470, 50, 20);
		SendButton.addActionListener(this);			// �{�^���Ƀ��X�i�[�ǉ� 

		URL hosturl = getCodeBase( );				// �A�v���b�g���g�̊��URL�擾
													// http://xxx/xxxx/
		String UrlFile = hosturl + filename;		// http://xxx/xxxx/keijiban.dat
		URL url = null;								// URL�I�u�W�F�N�g
		try {
			url = new URL(UrlFile);					// URL�I�u�W�F�N�g�쐬
		} catch (MalformedURLException e) {			// URL�ɑ΂����O����
			showStatus("Not Found URL-File");
		}

		try {
			URLConnection uc = url.openConnection( );	// URL�R�l�N�V�����I�u�W�F�N�g
			uc.setDoInput(true);						// URL�ڑ����g�p���ē��͂��s��
			uc.setUseCaches(false);						// �L���b�V���𖳎����ď���]��
			InputStream is = uc.getInputStream( );		// ���̓X�g���[��
			InputStreamReader isr = new InputStreamReader(is,"SJIS");// �G���R�[�f�B���O
			BufferedReader netinput = new BufferedReader(isr);	// �o�b�t�@�����O����

			StringBuffer buf = new StringBuffer( );				// ������o�b�t�@
			String line;
			while((line = netinput.readLine( )) != null) {		// �P�s����
				buf.append(line+"\n");				// ���̓f�[�^��\n��buf�ɕt��
			}
			netinput.close( );						// �l�b�g�o�R���̓X�g���[���N���[�Y

			DisplayArea.setText(buf.toString( ));	// buf���f���G���A�ɕ\��
		}
		catch(IOException e) {						// IO��O����
			System.out.println(e);
		}
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {		// �A�N�V�����C�x���g����
		Button button = (Button)evt.getSource( );		// �C�x���g�̃\�[�X�擾
		if (button == SendButton) {						// �C�x���g�����M�{�^���̏ꍇ
			if ((NameField.getText( )).equals("")		// ���O���͂��Ȃ��ꍇ
				|| (TitleField.getText( )).equals("")	// �^�C�g�����͂��Ȃ��ꍇ
				|| (InputArea.getText( )).equals(""))	// ���e���͂��Ȃ��ꍇ
				return; 								// ���������ɖ߂�

			StringBuffer buf = new StringBuffer( );		// StringBuffer�I�u�W�F�N�g�쐬
			buf.append("�w"+TitleField.getText( )+"�x�@�@");	// �^�C�g���t��
			buf.append(NameField.getText( )+"�@�@");	// ���e�Җ��t��
			buf.append(NowTime( )+"\n\n");				// ���݂̎��ԕt��
			buf.append(InputArea.getText( )+"\n");		// ���e�t��

			for (int i = 1; i <= 100; i++)
				buf.append("-");						// ��؂���쐬�t��
			buf.append("\n");							// ���s�t��

			SendProcess(filename, buf.toString( ));		// �쐬����buf�𑗐M
			NameField.setText(null);					// ���O���̓t�B�[���h�N���A
			TitleField.setText(null);					// �^�C�g�����̓t�B�[���h�N���A
			InputArea.setText(null);					// ���e���̓G���A�N���A

			StringBuffer buf2 = new StringBuffer( );	// StringBuffer�I�u�W�F�N�g�쐬
			buf2.append(buf.toString( ));				// �쐬����buf��buf2�ɃZ�b�g
			buf2.append("\n");							// \n�t��
			buf2.append(DisplayArea.getText( ));		// �\���G���A��buf2�ɃZ�b�g
			DisplayArea.setText(buf2.toString( ));		// buf2��\���G���A�ɃZ�b�g
		}
	}
	// ���݂̎��� -----------------------------------------------------------------------
	String NowTime( ) {
        String Weeks[ ] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

		Calendar cal = Calendar.getInstance( );
		int Year = cal.get(Calendar.YEAR);						// �N
		int Month = cal.get(Calendar.MONTH) + 1;				// ��
		int Day = cal.get(Calendar.DAY_OF_MONTH);				// ��
		String Week = Weeks[cal.get(Calendar.DAY_OF_WEEK)-1];	// �j��
		int Hour = cal.get(Calendar.HOUR);						// ����
		int Minute = cal.get(Calendar.MINUTE);					// ��

		return Year + "/" + Month + "/" + Day + "(" + Week + ")" + Hour + ":" + Minute;
	}
	// ���M���� -------------------------------------------------------------------------
	void SendProcess(String filename, String contents) {	// �t�@�C�����C���e
		Socket socket = null;						// �\�P�b�g�錾
		PrintStream netoutput;						// �l�b�g���[�N�o�R�o��

		try {
			URL url = getCodeBase( );				// �A�v���b�g���g�̊��URL�擾
			String host = url.getHost( );			// URL�̃z�X�g��
			int port = 8005;						// �|�[�g 8005

			try {
				socket = new Socket(host, port);	// �\�P�b�g����(�z�X�g�C�|�[�g)
			} catch (UnknownHostException e) {
				DisplayArea.append("Not able to connect, sorry \n");
			}

			// �\�P�b�g����o�̓X�g���[�����쐬
			OutputStream outputstream = socket.getOutputStream( );
			// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
			netoutput = new PrintStream(outputstream);

			// �t�@�C�����𑗐M
			netoutput.println(filename);			// String�f�[�^���o��
			// �o�̓X�g���[�����t���b�V��(�o�b�t�@�ɓ����Ă���o�̓o�C�g�������I�ɏo��)
			netoutput.flush( );

			// ���e���M
			netoutput.println(contents);			// String�f�[�^�� UTF �`���ŏo��
			netoutput.flush( );						// �o�b�t�@���f�[�^�������I�ɏo��

			// �I���f�[�^���M
			netoutput.println("end");				// String�f�[�^�� UTF �`���ŏo��
			netoutput.flush( );						// �o�b�t�@���f�[�^�������I�ɏo��

			netoutput.close( );						// �l�b�g�o�R�o�̓X�g���[���N���[�Y
			socket.close( );						// �\�P�b�g�N���[�Y
		}catch(IOException e) {						// ��O����
		}
	}
}
