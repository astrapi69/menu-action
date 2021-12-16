package io.github.astrapi69.swing.listener.mouse;

import io.github.astrapi69.window.adapter.CloseWindow;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

class MouseDoubleClickListenerTest
{

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Test Double Click");
		frame.addWindowListener(new CloseWindow());
		frame.addMouseListener(new MouseDoubleClickListener()
		{
			public void onSingleClick(MouseEvent e)
			{
				System.out.println("single click");
			}

			public void onDoubleClick(MouseEvent e)
			{
				System.out.println("double click");
			}
		});
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
}
