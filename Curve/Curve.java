import java.applet.*;						// Applet
import java.awt.*;							// Graphics, Color, Button, Label, TextField, Canvas
import java.awt.event.*;					// ActionListener, ActionEvent

public class Curve extends Applet implements ActionListener {
	CanvasMake canvas;								// �O���t�L�����o�X
	TextField ValueField[ ] = new TextField[3];		// �e�萔�̓��͈�
 	Label label[ ] = new Label[3];					// �����\�����x��
	double Value[ ] = new double[3];				// �e�萔
	Button DispButton;								// �\���{�^��

	// ���������� -----------------------------------------------------------------------
	public void init( ) {
		// ���x������
		label[0] = new Label("F(X)=");
		label[1] = new Label("X2 + ");
		label[2] = new Label("X + ");
		for (int i = 0; i < 3; i++) {
			add(label[i]);
			ValueField[i] = new TextField( );
			add(ValueField[i]);
		}

		DispButton = new Button("Disp");
		add(DispButton);
		DispButton.addActionListener(this);			// �{�^���Ƀ��X�i�[�Z�b�g

		canvas = new CanvasMake( );					// �L�����o�X����
		canvas.setSize(200, 200);					// �L�����o�X�T�C�Y�Đݒ�
		add(canvas);								// �L�����o�X���A�v���b�g�ɕt��
	}
	// ActionListener�C���^�[�t�F�[�X�̃��\�b�h���` -----------------------------------
    public void actionPerformed(ActionEvent evt) {	// �C�x���g����
		Button button = (Button)evt.getSource( );
		if (button == DispButton) {
			for (int i = 0; i < 3; i++) {
				if (ValueField[i].getText( ).equals(""))		// �������͂���Ă��Ȃ��ꍇ
					Value[i] = 0;
				else
					Value[i] = new Double(ValueField[i].getText( )).floatValue( );
			}
			canvas.GraphDisp(Value);				// �O���t�\��
		}
	}
}

// �L�����o�X�쐬�N���X =================================================================
class CanvasMake extends Canvas{
	double Value[ ] = new double[3];
	int sw;
	// �R���X�g���N�^ -------------------------------------------------------------------
	public CanvasMake ( ) {
		setBackground(Color.lightGray);				// �L�����o�X�̔w�i�F
		sw = 0;
	}
	// �O���t�`�� -----------------------------------------------------------------------
	void GraphDisp(double v[ ]) {					// �e�萔
		// �������O���[�o���ϐ��ɃZ�b�g
		for (int i = 0; i < 3; i++)
			Value[i] = v[i];
		sw = 1;										// �`��X�C�b�`ON
		repaint( );
	}
	// �`�揈�� -------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.setColor(Color.white);
		for (int p = 0; p < 200; p += 10) {			// �ڐ���
			g.drawLine(0, p, 200, p);				// �����C��
			g.drawLine(p, 0, p, 200);				// �c���C��
		}

		g.setColor(Color.black);
		g.drawLine(0, 100, 200, 100);				// X��
		g.drawLine(100, 0, 100, 200);				// Y��

		if (sw == 0)
			return;

		g.setColor(Color.red);
		for (double x = -10.0; x <= 10.0; x += 0.01) {
			double f = Value[0]*x*x + Value[1]*x + Value[2];	// �֐��̒l�v�Z
			int xp = (int)(x * 10)		+ 100;		// ���W(100,100)�����_
			int yp = (int)(f * 10)*(-1) + 100;		// ���̐���������Cxy���ꂼ��10�{
			g.drawLine(xp, yp, xp, yp);
		}
	}
}
