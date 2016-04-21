import java.net.*; 	// ServerSocket, Socket
import java.io.*;	// InputStream, BufferedReader, InputStreamReader,
					// OutputStream, PrintStream, IOException
import java.sql.*;	// DriverManager ,Connection, Statement, ResultSet

public class ScheduleServer {

	// ���C������ -----------------------------------------------------------------------
	public static void main(String argv[ ] ) {
		String GetMessage = null;					// ��M���b�Z�[�W
		int port = 8003;							// ����M�̃|�[�g�ԍ�
		Socket socket = null;						// ����M�p�\�P�b�g
		String order = null;						// ���� Select Delete Insert Update
		String date = null;							// ���t
		String hour = null;							// ����
		String content = null;						// ���e

		ScheduleServer obj = new ScheduleServer( );	// ���g�̃N���X�̃I�u�W�F�N�g�쐬

		try {
			// �w��|�[�g�ŃT�[�o�\�P�b�g�쐬
			ServerSocket serversocket = new ServerSocket(port);
			System.out.println("ServerSocket=" + serversocket);

			while (true) {
				// accept( )�ŃN���C�A���g����̐ڑ���҂��C�ڑ��������socket���쐬
				socket = serversocket.accept( );

				// �\�P�b�g������̓X�g���[�����쐬
				InputStream inputstream = socket.getInputStream( );
				// ���̓X�g���[������s�P�ʂɓ��͂ł���o�b�t�@�h���[�_���쐬
				BufferedReader netinput
 					= new BufferedReader(new InputStreamReader(inputstream));

				// �\�P�b�g����o�̓X�g���[�����쐬
				OutputStream outputstream = socket.getOutputStream( );
				// �o�̓X�g���[������f�[�^�o�̓X�g���[���쐬
				PrintStream netoutput = new PrintStream(outputstream);

				// �N���C�A���g���疽�߂����
				try {
					GetMessage = netinput.readLine( );	// 1�s���̓��e����M
				} catch (IOException e) {				// ���o�͂ɑ΂����O����
					System.out.println("read error");
				}

				// ���b�Z�[�W�𕪉� -----------------------------------------------------
				/* ��M���b�Z�[�W��
				Select#1999/11/25#
				Insert#1999/11/25#10#Meeting#
				Update#1999/11/25#14#Official trip#
				Delete#1999/11/25#10#
				*/
				int p1, p2;
				order = GetMessage.substring(0, 6); 		// �f�[�^�x�[�X�ŏ���������e
				p1 = GetMessage.indexOf("#", 0);			// �ŏ��́��̈ʒu
				p2 = GetMessage.indexOf("#", p1+1);			// ���́��̈ʒu
				date = GetMessage.substring(p1+1, p2);		// p1+1�`p2�̑O�܂ł̕�����
															// ���t
				if (order.compareTo("Select") != 0) {		// Select�ȊO
					p1 = p2;
					p2 = GetMessage.indexOf("#", p1+1);		// ���́��̈ʒu
					hour = GetMessage.substring(p1+1, p2);	// p1+1�`p2�̑O�܂ł̕�����
															// ����
					if (order.compareTo("Delete") != 0) {	// Delete�ȊO
						p1 = p2;
						p2 = GetMessage.indexOf("#", p1+1);	// ���́��̈ʒu
						content = GetMessage.substring(p1+1, p2);	// ���e
					}
				}
				// �N���C�A���g����̗v�����T�[�o�̉�ʂɕ\��
				System.out.println("Order=" + order
							+ " Date=" + date+" Hour=" + hour + " Content=" + content);

				// �e���� ---------------------------------------------------------------
				if (order.compareTo("Insert") == 0) {		// order��Insert�̏ꍇ
					obj.Insert(date, hour, content);
				}
				else if (order.compareTo("Update") == 0) {	// order��Update�̏ꍇ
					obj.Update(date, hour, content);
				}
				else if (order.compareTo("Delete") == 0) {	// order��Delete�̏ꍇ
					obj.Delete(date, hour);
				}
				String SendMessage = obj.Select(date);		// Select����т��̑�
				netoutput.println(SendMessage);		// ���b�Z�[�W���l�b�g�o�R�ŏo��
				netoutput.flush( );					// �o�b�t�@���f�[�^�������I�ɏo��
				socket.close( );					// �\�P�b�g����
			}
		} catch(Exception e) {						// �e���O����
			System.out.println(e);
		}
	}
	// �����E�I�� -----------------------------------------------------------------------
	// �w��f�[�^���e�[�u�����猟�����āCSendMessage�ɃZ�b�g
	String Select(String date) {
		int DATAMAX = 18;							// 6������23���܂ł�18��
		String contents[ ] = new String[DATAMAX];	// �e�����̓��e
		for (int i = 0; i < DATAMAX; i++) 			// ���e������
			contents[i] = " ";

		try {
			// ODBC-JDBC Bridge �h���C�o���[�h
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			// ODBC DSN����ăf�[�^�x�[�X�ɐڑ�
			String url = "jdbc:odbc:scheduleDB";
			String user = "guest";
			String password = "007";
			Connection con = DriverManager.getConnection(url, user, password);
			// �w����t�̃X�P�W���[�����e���f�[�^�x�[�X������o��SQL���쐬�E���s
			// �t�B�[���hdate��SQL�̗\���ł���̂�[ ]�ň͂�Ŏg�p
			String sql = "SELECT [date],hour,content FROM schedule WHERE "
							+ "date='" + date + "'";
			Statement stmt = con.createStatement( );	// DB�I�u�W�F�N�g�쐬
			ResultSet rs = stmt.executeQuery(sql);		// SQL�����s
			// ���ʎ擾
			while(rs.next( )) {							// ResultSet�̌��ʂ����Ɏ�o��
				String hour = rs.getString("hour");		// hour�t�B�[���h�̓��e�擾
				int p = Integer.parseInt(hour) - 6;		// �ߑO6������
				contents[p] = rs.getString("content");	// �X�P�W���[�����e
			}
			// �f�[�^�x�[�X�N���[�Y�i�I�[�v���������Ƃ͋t�ɕ��Ă����j
			rs.close( );
			stmt.close( );
			con.close( );
		} catch (Exception e) {
			System.out.println("->"+e);
		}

		// SendMessage��  "1999/11/25#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@#�@"
		// ���t��6������23���܂ł̓��e
		String SendMessage = date;
		for (int i = 0; i < DATAMAX; i++) 			// ���e�ݒ�
			SendMessage = SendMessage + "#" + contents[i];
		return SendMessage;
	}
	// �ǉ� -----------------------------------------------------------------------------
	// �w��f�[�^���e�[�u���ɒǉ�
	void Insert(String date, String hour, String content) {
		try {
			// ODBC-JDBC Bridge �h���C�o���[�h
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			// ODBC DSN����ăf�[�^�x�[�X�ɐڑ�
			String url = "jdbc:odbc:scheduleDB";
			String user = "guest";
			String password = "007";
			Connection con = DriverManager.getConnection(url, user, password);
			Statement stmt = con.createStatement( );// DB�I�u�W�F�N�g�쐬
			// �f�[�^�x�[�X�ɒǉ�����SQL���쐬
			// �t�B�[���hdate��SQL�̗\���ł���̂�[ ]�ň͂�Ŏg�p
			String sql = "INSERT INTO schedule([date], hour, content) VALUES ("
							+ "'" + date + "',"
							+ "'" + hour + "',"
							+ "'" + content + "'"+")";
			stmt.executeUpdate(sql);				// SQL�����s

			// �f�[�^�x�[�X�N���[�Y�i�I�[�v���������Ƃ͋t�ɕ��Ă����j
			stmt.close( );
			con.close( );
		} catch (Exception e) {
			System.out.println("Error->"+e);
		}
	}
	// �C�� -----------------------------------------------------------------------------
	// �e�[�u���̃f�[�^���C��
	void Update(String date, String hour, String content) {
		try {
			// ODBC-JDBC Bridge �h���C�o���[�h
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			// ODBC DSN����ăf�[�^�x�[�X�ɐڑ�
			String url = "jdbc:odbc:scheduleDB";
			String user = "guest";
			String password = "007";
			Connection con = DriverManager.getConnection(url, user, password);

			// �w�肵�������̃f�[�^�x�[�X�̃X�P�W���[���̓��e���X�V����SQL���쐬�E���s
			String sql = "UPDATE schedule SET "
							+ "content='" + content + "' "
							+ "WHERE date='" + date + "' AND hour='" + hour + "'";
			Statement stmt = con.createStatement( );	// DB�I�u�W�F�N�g�쐬
			stmt.executeUpdate(sql);					// SQL�����s

			// �f�[�^�x�[�X�N���[�Y�i�I�[�v���������Ƃ͋t�ɕ��Ă����j
			stmt.close( );
			con.close( );
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	// �폜 -----------------------------------------------------------------------------
	// �e�[�u���̃f�[�^���폜
	void Delete(String date, String hour) {
		try {
			// ODBC-JDBC Bridge �h���C�o���[�h
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			// ODBC DSN����ăf�[�^�x�[�X�ɐڑ�
			String url = "jdbc:odbc:scheduleDB";
			String user = "guest";
			String password = "007";
			Connection con = DriverManager.getConnection(url, user, password);

			//�w�肵�������̃f�[�^�x�[�X�̃X�P�W���[���̓��e���폜����SQL���쐬�E���s
			String sql = "DELETE FROM schedule WHERE "
 							+ "date='" + date + "' AND hour='" + hour + "'";
			Statement stmt = con.createStatement( );	// DB�I�u�W�F�N�g�쐬
			stmt.executeUpdate(sql);					// SQL�����s

			// �f�[�^�x�[�X�N���[�Y�i�I�[�v���������Ƃ͋t�ɕ��Ă����j
			stmt.close( );
			con.close( );
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
