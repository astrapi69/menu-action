/**
 * The MIT License
 *
 * Copyright (C) 2026 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.swing.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link OpenFileAction}
 */
class OpenFileActionTest
{

	/**
	 * Test stub that returns the given option without showing a real dialog
	 */
	private static class StubFileChooser extends JFileChooser
	{
		private static final long serialVersionUID = 1L;
		final int returnValue;
		Component shownParent;
		int shownCount;

		StubFileChooser(final int returnValue, final File selectedFile)
		{
			this.returnValue = returnValue;
			setSelectedFile(selectedFile);
		}

		@Override
		public int showOpenDialog(final Component parent)
		{
			shownParent = parent;
			shownCount++;
			return returnValue;
		}
	}

	/**
	 * Test subclass that records the callback methods
	 */
	private static class RecordingOpenFileAction extends OpenFileAction
	{
		private static final long serialVersionUID = 1L;
		final List<File> approvedFiles = new ArrayList<>();
		final List<ActionEvent> approvedEvents = new ArrayList<>();
		final List<ActionEvent> cancelEvents = new ArrayList<>();

		RecordingOpenFileAction()
		{
			super();
		}

		RecordingOpenFileAction(final String name, final Component parent)
		{
			super(name, parent);
		}

		@Override
		protected JFileChooser newJFileChooser()
		{
			return new StubFileChooser(JFileChooser.APPROVE_OPTION, null);
		}

		@Override
		protected void onApproveOption(final File file, final ActionEvent actionEvent)
		{
			approvedFiles.add(file);
			approvedEvents.add(actionEvent);
		}

		@Override
		protected void onCancel(final ActionEvent actionEvent)
		{
			cancelEvents.add(actionEvent);
		}
	}

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "open");
	}

	@Test
	void defaultConstructor()
	{
		RecordingOpenFileAction action = new RecordingOpenFileAction();

		assertEquals("", action.getValue(Action.NAME));
		assertNull(action.getParent());
		assertNull(action.getFileChooser());
		// without a file chooser the action can not choose a file
		assertThrows(NullPointerException.class, () -> action.actionPerformed(newEvent()));
	}

	@Test
	void constructorRequiresParent()
	{
		assertThrows(NullPointerException.class, () -> new RecordingOpenFileAction("Open", null));
	}

	@Test
	void constructorCreatesFileChooserFromFactoryMethod()
	{
		JPanel parent = new JPanel();

		RecordingOpenFileAction action = new RecordingOpenFileAction("Open", parent);

		assertEquals("Open", action.getValue(Action.NAME));
		assertSame(parent, action.getParent());
		assertInstanceOf(StubFileChooser.class, action.getFileChooser());
	}

	@Test
	void defaultFactoryMethodCreatesPlainJFileChooser()
	{
		OpenFileAction action = new OpenFileAction("Open", new JPanel())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onApproveOption(final File file, final ActionEvent actionEvent)
			{
			}

			@Override
			protected void onCancel(final ActionEvent actionEvent)
			{
			}
		};

		assertNotNull(action.getFileChooser());
		assertEquals(JFileChooser.class, action.getFileChooser().getClass());
	}

	@Test
	void approveOptionCallsOnApproveOptionWithSelectedFile()
	{
		JPanel parent = new JPanel();
		File selectedFile = new File("selected-file.txt");
		RecordingOpenFileAction action = new RecordingOpenFileAction("Open", parent);
		StubFileChooser fileChooser = new StubFileChooser(JFileChooser.APPROVE_OPTION,
			selectedFile);
		action.setFileChooser(fileChooser);
		ActionEvent event = newEvent();

		action.actionPerformed(event);

		assertEquals(1, fileChooser.shownCount);
		assertSame(parent, fileChooser.shownParent);
		assertEquals(List.of(selectedFile), action.approvedFiles);
		assertEquals(List.of(event), action.approvedEvents);
		assertTrue(action.cancelEvents.isEmpty());
	}

	@Test
	void cancelOptionCallsOnCancel()
	{
		RecordingOpenFileAction action = new RecordingOpenFileAction("Open", new JPanel());
		StubFileChooser fileChooser = new StubFileChooser(JFileChooser.CANCEL_OPTION,
			new File("ignored.txt"));
		action.setFileChooser(fileChooser);
		ActionEvent event = newEvent();

		action.actionPerformed(event);

		assertEquals(1, fileChooser.shownCount);
		assertTrue(action.approvedFiles.isEmpty());
		assertEquals(List.of(event), action.cancelEvents);
	}

	@Test
	void errorOptionIsTreatedAsCancel()
	{
		RecordingOpenFileAction action = new RecordingOpenFileAction("Open", new JPanel());
		action.setFileChooser(new StubFileChooser(JFileChooser.ERROR_OPTION, null));
		ActionEvent event = newEvent();

		action.actionPerformed(event);

		assertTrue(action.approvedFiles.isEmpty());
		assertEquals(List.of(event), action.cancelEvents);
	}

	@Test
	void setParentChangesTheParentOfTheFileChooserDialog()
	{
		RecordingOpenFileAction action = new RecordingOpenFileAction("Open", new JPanel());
		StubFileChooser fileChooser = new StubFileChooser(JFileChooser.APPROVE_OPTION,
			new File("selected-file.txt"));
		action.setFileChooser(fileChooser);
		JPanel newParent = new JPanel();
		action.setParent(newParent);

		action.actionPerformed(newEvent());

		assertSame(newParent, fileChooser.shownParent);
	}
}
