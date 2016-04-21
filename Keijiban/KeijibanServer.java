import java.net.*;				// Socket, ServerSocket
import java.io.*;				// InputStream, InputStreamReader, BufferedReader etc

public class KeijibanServer {

	// ���C������ -----------------------------------------------------------------------
	public static void main(String args[ ]) {
		KeijibanServer obj = new KeijibanServer( );
		obj.KeijibanProcess( );				// (new KeijibanServer( )).KeijibanProcess( );
	}
	// �f������ -----------------------------------------------------------------------
	void KeijibanProcess( ){

	 	BufferedReader netinput = null;					// �l�b�g���[�N�o�R����
		try {
			// �w��|�[�g�ŃT�[�o�\�P�b�g�쐬
			ServerSocket serversocket = new ServerSocket(8005);
			System.out.println("socket=" + serversocket);

			while (true) {
				Socket socket = serversocket.accept( );// �N���C�A���g����̎�M�ڑ���҂�
				try {
					// �\�P�b�g������̓X�g���[�����쐬
					InputStream inputstream = socket.getInputStream( );
					// ���̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_���쐬
					netinput = new BufferedReader(new InputStreamReader(inputstream));					} catch(Exception e) {				// ��O����
					System.out.println("check point 1 : " + e);
				}

				// �N���C�A���g����t�@�C������M
				String filename = netinput.readLine( );		// �t�@�C����

				try {
					// �t�@�C���ɐڑ�����FileInputStream�쐬
					FileInputStream fis = new FileInputStream(filename);
					// �o�b�t�@�����O���ꂽ�����^���̓X�g���[�����쐬
					BufferedReader br = 
						new BufferedReader(new InputStreamReader(fis));
					// �t�@�C�������w�肵�� FileWriter �I�u�W�F�N�g���\�z
					FileWriter fw = new FileWriter("temp");
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);

					// �N���C�A���g����̃f�[�^���ꎞ�t�@�C���ɕۑ�
					while (true) {
 						// �N���C�A���g����̃��b�Z�[�W��M
						String data = netinput.readLine( );
						if (data.equals("end"))
							break;
						else
							pw.println(data);
					}
					pw.flush( );					// �o�b�t�@���f�[�^�������I�ɏo��

					// �w��t�@�C�����ꎞ�t�@�C���ɕۑ�
					String line;
					while ((line = br.readLine( )) != null) {	// 1�s����
						pw.println(line);
					}
					br.close( );					// �w��t�@�C���̃X�g���[�����N���[�Y
					fis.close( ); 

					pw.close( );					// �ꎞ�t�@�C���̃X�g���[�����N���[�Y
					bw.close( );
					fw.close( );
				}catch(Exception e) {				// ��O����
					System.out.println("check point 2 : " + e);
				}

				// �ꎞ�t�@�C��temp�̖��O���w��t�@�C�����ɕύX
				File f1 = new File("temp");			// �t�@�C���I�u�W�F�N�g����
				File f2 = new File(filename);
				f2.delete( );						// �t�@�C���폜
				f1.renameTo(f2);					// �t�@�C�����ύX
			}

		}catch(Exception e) {						// ��O����
			System.out.println("check point 3 : " + e);
		}
	}
}
