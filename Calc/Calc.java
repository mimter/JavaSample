import java.applet.*;								// Applet, AudioClip
import java.awt.*;									// Panel, Button, Label, Color, GridLayout, BorderLayout
import java.awt.event.*;							// ActionListener, ActionEvent

public class Calc extends Applet
				  implements ActionListener {		// ActionListener�C���^�[�t�F�[�X����
	Label DisplayLabel;								// �\�����x��
	String PreOpe;									// �O��̉��Z�q
	StringBuffer InputBuffer; 						// ���̓o�b�t�@
	double PreValue, MemoryValue;					// �O��܂ł̒l�C�������l
	int NewDigitInputSw;							// �V�������̓X�C�b�`
	AudioClip ClickSound;							// �N���b�N�T�E���h

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		String ButtonName[ ] = { " ",				// �X�y�[�X�̓_�~�[
 								"C", "M+", "M-", "MR", 	// ���{�^���ݒ�
								"7", "8", "9", "/",
								"4", "5", "6", "*",
								"1", "2", "3", "-",
								"0", ".", "=", "+" };
		Button CalcButton[ ] = new Button[21];		// �{�^���z�񐶐�
		ClickSound = getAudioClip(getCodeBase( ), "sound/click.au");	// �N���b�N��
		setLayout(new BorderLayout( ));	   			// �A�v���b�g���{�[�_�[���C�A�E�g�ݒ�

		Panel DisplayPanel = new Panel( );			// �\���p�l���ݒ�
		DisplayLabel = new Label("", Label.RIGHT);	// �E�����w��̃��x���ݒ�
		DisplayLabel.setBackground(Color.black);	// �w�i�F�F��
		DisplayLabel.setForeground(Color.green);	// �\���F�F��
		DisplayPanel.add(DisplayLabel);				// ���x�����p�l���ɒǉ�
		Panel ButtonPanel = new Panel( );			// �{�^���p�l���쐬
		ButtonPanel.setLayout(new GridLayout(5, 4));// 5�s4��̃O���b�h���C�A�E�g
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 4; j++) {
				int p = (i - 1) * 4 + j;
				CalcButton[p] = new Button(ButtonName[p]);
				ButtonPanel.add(CalcButton[p]);		// �{�^���𐶐��C�{�^���p�l���ɕt��
				CalcButton[p].addActionListener(this);
			}
		}
		add("North", DisplayPanel);					// �\���p�l����k���ɕt��
		add("Center", ButtonPanel);					// �{�^���p�l�����Z���^�[�ɕt��
		InitialPro( );								// �v�Z����������
	}
	// �v�Z���������� -------------------------------------------------------------------
	void InitialPro( ) {
		PreValue = 0;								// �O��܂ł̒l
		PreOpe = "";								// �O��̉��Z�q
		MemoryValue = 0;							// �������l
		InputBuffer = new StringBuffer( );			// ���̓o�b�t�@
		NewDigitInputSw = 0;						// �V�������̓X�C�b�`
		DisplayLabel.setText("                            0");
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �C�x���g����
		ClickSound.play( );							// �N���b�N��
		Button bt = (Button)evt.getSource( );
		String value = bt.getLabel( );
		if ("C".equals(value)) {					// �N���A�̏ꍇ
			InitialPro( );							// �v�Z����������
		}
		else if ((".".equals(value)) || ("0".equals(value)) || 	// �����L�[�̏ꍇ
				 ("1".equals(value)) || ("2".equals(value)) || 
				 ("3".equals(value)) || ("4".equals(value)) || 
				 ("5".equals(value)) || ("6".equals(value)) || 
				 ("7".equals(value)) || ("8".equals(value)) || 
				 ("9".equals(value)) ) {
			if (NewDigitInputSw == 0) {
				InputBuffer = new StringBuffer( );	// ���̓o�b�t�@����
				InputBuffer.append(value);
				DisplayLabel.setText(value);
				NewDigitInputSw = 1;
			} else {
				InputBuffer.append(value);			// ���͒l��ǉ�
				DisplayLabel.setText(InputBuffer.toString( ));	// ������
			}
		} else if (("+".equals(value)) || ("-".equals(value)) || 
				 ("*".equals(value)) || ("/".equals(value)) || 
				 ("=".equals(value)) ||
				 ("M+".equals(value)) || ("M-".equals(value)) ) {

			// �f�B�X�v���C���x���ɕ\������Ă��镶�����double�l�ɕϊ�
			double Nowvalue =
					(Double.valueOf(DisplayLabel.getText( ))).doubleValue( );

			if ("+".equals(PreOpe))					// �O��̉��Z�q���{�̏ꍇ
				PreValue = PreValue + Nowvalue;		// �O��̒l�ɍ���̒l�����Z
			else if ("-".equals(PreOpe))
				PreValue = PreValue - Nowvalue;
			else if ("*".equals(PreOpe))
				PreValue = PreValue * Nowvalue;
			else if ("/".equals(PreOpe))
				PreValue = PreValue / Nowvalue;
			else
				PreValue = Nowvalue;				// ���݂̒l��ۊ�

			if ("M+".equals(value)) {
				MemoryValue = MemoryValue + PreValue;	// �������ɉ��Z
				PreOpe = "";
			} else if ("M-".equals(value)) {
				MemoryValue = MemoryValue - PreValue;	// ���������猸�Z
				PreOpe = "";
			} else
				PreOpe = (String)value;				// ����̉��Z�q���L��

			DisplayLabel.setText(""+PreValue);
			NewDigitInputSw = 0; 					// �V�����������̓X�C�b�`�N���A
		} else if ("MR".equals(value)) {
			DisplayLabel.setText(""+MemoryValue);
			NewDigitInputSw = 0;					// �V�����������̓X�C�b�`�N���A
		}
	}
}
