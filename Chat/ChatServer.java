import java.net.*;					// Socket, ServerSocket
import java.io.*;					// InputStream, BufferedReader, InputStreamReader,
									// OutputStream, PrintStream

public class ChatServer {
	int MAX = 100;									// �ő�ڑ���
	ChatClient chatclient[ ]
		= new ChatClient[MAX];						// �`���b�g�N���X�z��
	Thread chatThread[ ] = new Thread[MAX];			// �N���C�A���g�Ƃ̓��o�̓X���b�h
	String Member[ ] = new String[MAX];				// �Q���Җ�

	// ���C������ -----------------------------------------------------------------------
	public static void main(String args[ ]) {
		ChatServer obj = new ChatServer( );			// ChatServer�I�u�W�F�N�g����
		obj.chatprocess( );							// �`���b�g����
	}
	// �`���b�g���� ---------------------------------------------------------------------
	void chatprocess( ){
		try {
			// �w��|�[�g�ŃT�[�o�\�P�b�g�쐬
			ServerSocket serversocket = new ServerSocket(8004);
			System.out.println("ServerSocket=" + serversocket);

			while (true) {							// �i�v�ɌJ��Ԃ�
				int p;
				for (p = 0; p < MAX; p++)			// �ڑ��`�F�b�N
					if (chatclient[p] == null)		// �󂢂Ă���ꍇ
						break;
				if (p == MAX)						// �󂢂Ă��Ȃ��ꍇ
					continue; 						// ���[�v����E�o

				Socket socket = serversocket.accept( );	// �N���C�A���g����̐ڑ���҂�

				chatclient[p] = new ChatClient(socket, this, p);	// �`���b�g�쐬
				chatThread[p] = new Thread(chatclient[p]);			// �X���b�h�쐬
				chatThread[p].start( );				// �X���b�h�X�^�[�g
			}
		}catch(Exception e) {						// ��O�G���[����
			System.out.println("check point 1 : " + e);
		}
	}
	// �e�L�X�g���M ---------------------------------------------------------------------
	synchronized void sendalltext(String message) { // �r���I�ɓ������Ƃ��đ��M
		for (int i = 0; i < MAX; i++) {				// �S���ɑ���
			if (chatclient[i] != null) {			// �ڑ�����Ă���ꍇ
				chatclient[i].sendMessage("T:" + message);// i�Ԗڂ̃N���C�A���g�ɑ��M
			}
		}
	}
	// �����o�[�ꗗ���M -----------------------------------------------------------------
	synchronized void sendallmember( ) { 
		String message = "M:";						// �����o�[�쐬
		for (int i = 0; i < MAX; i++) {
			if (Member[i] != null)					// ���݁C�Q�����Ă���ꍇ
				message = message + Member[i] + ":";// �����o�[�̖��O��t��
		}
		for (int i = 0; i < MAX; i++) {				// �S���ɑ���
			if (chatclient[i] != null && Member[i] != null) {
				chatclient[i].sendMessage(message);// i�Ԗڂ̃N���C�A���g�Ƀ����o�[���M
			}
		}
	}
	// �`���b�g�I�� ---------------------------------------------------------------------
	void chatquit(int clientnumber) {
		chatclient[clientnumber] = null;			// �X���b�h������
		chatThread[clientnumber] = null;			// �X���b�h������
	}
	// �������� -------------------------------------------------------------------------
	void entermember(String name, int clientnumber) {
		Member[clientnumber] = name;				// �����o�[�o�^
	}
	// �ޏo���� -------------------------------------------------------------------------
	void exitmember(int clientnumber) {
		Member[clientnumber] = null;				// �����o�[�폜	
	}
}

// --------------------------------------------------------------------------------------
// �`���b�g�N���C�A���g�Ǘ��N���X
class ChatClient implements Runnable {
	BufferedReader NetInput;						// �l�b�g���[�N�o�R���̓X�g���[��
	PrintStream NetOutput;							// �l�b�g���[�N�o�R�o�̓X�g���[��
	Socket socket;									// �\�P�b�g
	ChatServer chatserver;							// ChatServer�I�u�W�F�N�g
	int clientnumber;								// �N���C�A���g�ԍ�

	// �R���X�g���N�^ -------------------------------------------------------------------
	ChatClient(Socket socket, ChatServer chatserver, int clientnumber) {
		// �����̒l�����̃N���X�̕ϐ��ɃZ�b�g
		this.socket = socket;
		this.chatserver = chatserver;
		this.clientnumber = clientnumber;

		try {
			// �\�P�b�g������̓X�g���[�����쐬
			InputStream inputstream = socket.getInputStream( );
			// ���̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_���쐬
			NetInput = new BufferedReader(new InputStreamReader(inputstream));

			// �\�P�b�g����o�̓X�g���[�����쐬
			OutputStream outputstream = socket.getOutputStream( );
			// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
			NetOutput = new PrintStream(outputstream);

		} catch(Exception e) {						// ��O����
			System.out.println("->" + e);
		}
	}
	// ���b�Z�[�W���M -------------------------------------------------------------------
	synchronized void sendMessage(String message) {
		NetOutput.println(message);					// �T�[�o����N���C�A���g�ɑ��M
		NetOutput.flush( );							// �o�b�t�@���f�[�^�������I�ɏo��
	}
	// �X���b�h���s ---------------------------------------------------------------------
	public void run( ) {
		String name = null;							// ���O
		String sendmessage = null;					// ���M���b�Z�[�W
		try {
			while (true) {
				sendmessage = NetInput.readLine( );	// �N���C�A���g����̃��b�Z�[�W��M
				System.out.println(sendmessage);
				if (sendmessage.equals("chat_quit")) {			// chat_quit�̏ꍇ
					chatserver.exitmember(clientnumber);		// �����o�[�ޏo
					chatserver.sendallmember( );				// �V�����o�[�ꗗ���M
					stop( );									// �`���b�g�I��
					break;
				} else if (name == null) {						// ���O�����ݒ�̏ꍇ
					name = sendmessage;							// ��M���b�Z�[�W�𖼑O��
					chatserver.entermember(name, clientnumber);	// �����o�[����
					chatserver.sendallmember( );				// �V�����o�[�ꗗ���M
				} else {
					chatserver.sendalltext("<" + name + "> " + sendmessage);
				}
			}
		} catch(Exception e) {						// ��O����
			System.out.println("check point 4 : " + e);
		}
	}
	// �`���b�g�I�� ---------------------------------------------------------------------
	public void stop( ) {
		try {
			NetInput.close( );						// �l�b�g�o�R���̓X�g���[���N���[�Y
			NetOutput.close( );						// �l�b�g�o�R�o�̓X�g���[���N���[�Y
			socket.close( );						// �\�P�b�g�N���[�Y
		} catch (Exception e) {						// ��O����
			System.out.println(e);
		}
		chatserver.chatquit(clientnumber);			// �`���b�g�X���b�h���
	}
}
