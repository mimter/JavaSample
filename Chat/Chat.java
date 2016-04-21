import java.applet.*;		// Applet
import java.io.*;			// InputStream, BufferedReader, InputStreamReader etc
import java.net.*;			// URL, Socket, UnknownHostException
import java.awt.*;			// Label, TextField, TextArea, Color, Button
import java.awt.event.*;	// ActionListener, ActionEvent
import java.util.*;			// StringTokenizer

public class Chat extends Applet implements Runnable, ActionListener {
							// Runnable, ActionListener�C���^�[�t�F�[�X����

	Label TitleLabel;								// �^�C�g�����x��
	TextField InputField;							// ���̓t�B�[���h
	Button SendButton;								// ���M�{�^��
	Button QuitButton;								// �I���{�^��
	TextArea DisplayArea;							// �\���G���A
	TextArea MemberArea;							// �����o�[�G���A
	Thread thread;									// �X���b�h
	Socket socket;									// �\�P�b�g
	String Name = null;								// ���O
	AudioClip Chime;								// �`���C��
 	BufferedReader NetInput;						// �l�b�g���[�N�o�R����
	PrintStream NetOutput;							// �l�b�g���[�N�o�R�o��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
        Chime = getAudioClip(getCodeBase( ), "sound/chime.au");

		setLayout(null);							// ���R���C�A�E�g�ݒ�

		// �e��GUI�쐬
		TitleLabel = new Label("�������  Your name");
		TitleLabel.setBounds(10, 10, 130, 20);

		InputField = new TextField("guest", 50);
		InputField.setBackground(new Color(153, 255, 204));
		InputField.setBounds(150, 10, 340, 20);

		SendButton = new Button("���M");
		SendButton.addActionListener(this);			// ���X�i�[�ݒ�
		SendButton.setBounds(500, 10, 40, 20);

		QuitButton = new Button("�ޏo");
		QuitButton.addActionListener(this);
		QuitButton.setBounds(550, 10, 40, 20);

		DisplayArea = new TextArea(20, 80);
		DisplayArea.setEditable(false);				// �����݋֎~
		DisplayArea.setBackground(new Color(0, 102, 0));
		DisplayArea.setForeground(Color.white);
		DisplayArea.setBounds(10, 40, 480, 250);

		MemberArea = new TextArea(20, 10);
		MemberArea.setEditable(false);				// �����݋֎~
		MemberArea.setBackground(new Color(153, 255, 51));
		MemberArea.setBounds(500, 40, 90, 250);

		add(TitleLabel);
		add(InputField);
		add(SendButton);
		add(QuitButton);
		add(DisplayArea);
		add(MemberArea);

		try {
			URL homeURL = getCodeBase( );			// ��{URL�擾
			String host = homeURL.getHost( );		// �z�X�g��
			int port = 8004;						// ����M�̃|�[�g�ԍ�
			try {
				socket = new Socket(host, port);	// �X�g���[���\�P�b�g����
			} catch (Exception e) {
				DisplayArea.append("Not able to connect, sorry \n");
			}

			// �\�P�b�g������̓X�g���[�����쐬
			InputStream inputstream = socket.getInputStream( );
			// ���̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_���쐬
			NetInput = new BufferedReader(new InputStreamReader(inputstream));

			// �\�P�b�g����o�̓X�g���[�����쐬
			OutputStream outputstream = socket.getOutputStream( );
			// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
			NetOutput = new PrintStream(outputstream);

			thread = new Thread(this);				// �X���b�h����
			thread.start( );						// �X���b�h�X�^�[�g
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		String message;

		Chime.play( );								// �`���C���Đ�
		try {
			while ((message = NetInput.readLine( )) != null) {
				Chime.play( );						// �`���C���Đ�
				if ((message.substring(0,1)).equals("T"))
					DisplayArea.append(message.substring(2) + "\n");
				else {
					MemberArea.setText("");			// �����o�[�G���A�N���A
					String members = message.substring(2);
					StringTokenizer st = 
						new StringTokenizer(members, ":");
					while(st.hasMoreTokens( ))  {
						String member = st.nextToken( );
						MemberArea.append(member + "\n");
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �C�x���g����
		String message;
		Button button = (Button)evt.getSource( );	// �C�x���g�\�[�X

		if (button == SendButton) {					// ���M�{�^��
			message = InputField.getText( );
			if (message == null || (message.substring(0,2)).equals("�@"))
				return;
			if (Name == null) {
				Name = message;
				TitleLabel.setText("�������  Message");
			}
			NetOutput.println(message);				// �l�b�g���[�N�o�R��mesage�o��
			NetOutput.flush( );						// �o�b�t�@���f�[�^�������I�ɏo��
			InputField.setText("");					// ���̓t�B�[���h���N���A
		} else if (button == QuitButton) {			// �ޏo�{�^��
			SendButton.setVisible(false);			// ���M�{�^�����B��
			QuitButton.setVisible(false);			// �ޏo�{�^�����B��
			stop( );								// �������I��
		}
	}
	// �A�v���b�g��~ -------------------------------------------------------------------
	public void stop( ) {
		NetOutput.println("chat_quit");				// �I�����b�Z�[�W���M
		NetOutput.flush( );							// �o�b�t�@���f�[�^�������I�ɏo��

		try {
			thread.sleep(3000);						// �I�����b�Z�[�W�̑��M�����҂�
		} catch (InterruptedException e) {
			System.out.println(e);
		}

		try {
			NetInput.close( );						// �l�b�g���[�N�o�R�X�g���[���N���[�Y
			NetOutput.close( );
			socket.close( );						// �\�P�b�g�N���[�Y
		} catch (Exception e) {
			System.out.println(e);
		}
		thread = null;								// �X���b�h�𖳌���
	}
}
