package io.github.astrapi69.swing.menu.model.transform;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import io.github.astrapi69.collection.list.ListExtensions;
import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import lombok.NonNull;

/**
 * The class {@link KeyStrokeInfoExtensions} provides utility methods for handling
 * {@link KeyStrokeInfo} and {@link KeyStroke} objects
 */
public class KeyStrokeInfoExtensions
{

	/**
	 * Private constructor to prevent instantiation
	 */
	private KeyStrokeInfoExtensions()
	{
	}

	/**
	 * Sets all values of this {@link KeyStrokeInfo} object from the given {@link KeyStroke} object
	 *
	 * @param keyStrokeInfo
	 *            the {@link KeyStrokeInfo} object to be set
	 * @param keyStroke
	 *            the {@link KeyStroke} object from which values will be copied
	 */
	public static void set(@NonNull final KeyStrokeInfo keyStrokeInfo,
		final @NonNull KeyStroke keyStroke)
	{
		keyStrokeInfo.setKeyCode(keyStroke.getKeyCode());
		keyStrokeInfo.setModifiers(keyStroke.getModifiers());
		keyStrokeInfo.setOnKeyRelease(keyStroke.isOnKeyRelease());
		keyStrokeInfo.setKeystrokeAsString(keyStroke.toString());
	}

	/**
	 * Converter method that creates a {@link KeyStroke} object from the given {@link KeyStrokeInfo}
	 * object
	 *
	 * @param keyStrokeInfo
	 *            the {@link KeyStrokeInfo} object from which the {@link KeyStroke} is created
	 * @return the new created {@link KeyStroke} object or null if the keyStrokeInfo is invalid
	 */
	public static KeyStroke toKeyStroke(@NonNull final KeyStrokeInfo keyStrokeInfo)
	{
		KeyStroke keyStroke = null;
		if (keyStrokeInfo.getKeystrokeAsString() != null
			&& !keyStrokeInfo.getKeystrokeAsString().isEmpty())
		{
			keyStroke = KeyStroke.getKeyStroke(keyStrokeInfo.getKeystrokeAsString());
			if (keyStroke != null)
			{
				return keyStroke;
			}
		}
		if (keyStrokeInfo.getKeyCode() != null)
		{

			if (keyStrokeInfo.getOnKeyRelease() != null && keyStrokeInfo.getModifiers() != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyStrokeInfo.getKeyCode(),
					keyStrokeInfo.getModifiers(), keyStrokeInfo.getOnKeyRelease());
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}

			if (keyStrokeInfo.getModifiers() != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyStrokeInfo.getKeyCode(),
					keyStrokeInfo.getModifiers());
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}
		}
		return keyStroke;
	}

	/**
	 * Gets the list of {@link KeyStrokeInfo} objects from the given {@link JComponent} object
	 *
	 * @param jComponent
	 *            the {@link JComponent} object from which {@link KeyStrokeInfo} objects will be
	 *            retrieved
	 * @return the list of {@link KeyStrokeInfo} objects
	 */
	@SuppressWarnings(value = "unchecked")
	public static List<KeyStrokeInfo> getKeyStrokeInfos(JComponent jComponent)
	{
		Object whenInFocusedWindow = jComponent.getClientProperty("_WhenInFocusedWindow");
		List<KeyStrokeInfo> keyStrokeInfos = new ArrayList<>();
		if (whenInFocusedWindow instanceof Hashtable)
		{
			Hashtable<KeyStroke, KeyStroke> hashtable = (Hashtable<KeyStroke, KeyStroke>)whenInFocusedWindow;
			Set<KeyStroke> keySet = hashtable.keySet();
			for (KeyStroke key : keySet)
			{
				keyStrokeInfos.add(KeyStrokeExtensions.toKeyStrokeInfo(key));
			}
		}
		return keyStrokeInfos;
	}

	/**
	 * Gets the {@link KeyStrokeInfo} object from the given {@link JComponent} object
	 *
	 * @param jComponent
	 *            the {@link JComponent} object from which the first {@link KeyStrokeInfo} object
	 *            will be retrieved
	 * @return the {@link KeyStrokeInfo} object or null if no {@link KeyStrokeInfo} is found
	 */
	public static KeyStrokeInfo getKeyStrokeInfo(JComponent jComponent)
	{
		return ListExtensions.getFirst(getKeyStrokeInfos(jComponent));
	}

}
