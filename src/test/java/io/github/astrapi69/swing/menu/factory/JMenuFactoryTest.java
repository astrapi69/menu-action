package io.github.astrapi69.swing.menu.factory;

import java.io.File;
import java.io.IOException;

import io.github.astrapi69.file.create.FileFactory;
import io.github.astrapi69.file.read.ReadFileExtensions;
import io.github.astrapi69.file.search.PathFinder;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.astrapi69.junit.jupiter.callback.before.test.IgnoreHeadlessExceptionExtension;

public class JMenuFactoryTest
{

	File xmlFile;
	String xml;

	@BeforeEach
	public void beforeEach()
	{
		String filename;
//		filename = "app-menu.xml";
//		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
//		xml = RuntimeExceptionDecorator.decorate(() -> ReadFileExtensions.fromFile(xmlFile));
	}
	@ExtendWith(IgnoreHeadlessExceptionExtension.class)
	@Test
	public void testBuildMenuFromXml() throws IOException
	{

	}
}
