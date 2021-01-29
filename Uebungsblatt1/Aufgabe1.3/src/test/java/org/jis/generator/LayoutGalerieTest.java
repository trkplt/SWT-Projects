package org.jis.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LayoutGalerieTest {

	private LayoutGalerie galerieUnderTest;

	private byte[] array = new byte[10];
	private String firstRandomString = null;
	private String secondRandomString = null;

	private File resourceFolder;
	private File fromFile;
	private File toFile;

	@Before
	public void setUp() {

		galerieUnderTest = new LayoutGalerie(null, null);

		new Random().nextBytes(array);
		firstRandomString = new String(array);

		new Random().nextBytes(array);
		secondRandomString = new String(array);

		try {
			resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			toFile = new File(resourceFolder, "to");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {

		// delete methods didn't work without gc()
		System.gc();
		fromFile.delete();
		toFile.delete();
	}

	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyFile() {

		try {
			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, firstRandomString);

			galerieUnderTest.copyFile(fromFile, toFile);

			assertTrue(toFile.exists());

			Path toPath = FileSystems.getDefault().getPath(toFile.getPath());
			String contents = Files.readString(toPath);

			assertEquals(firstRandomString, contents);
		} catch (IOException e) {
			fail();
		}

	}

	/*
	 * expected exception could be used rather than repeating catch but I prefer
	 * repeating catch for some too-long-to-explain reasons.
	 */
	@Test
	public final void testCopyFileFolder() {

		try {
			galerieUnderTest.copyFile(resourceFolder, toFile);
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {

		}

		fail();
	}

	/*
	 * see the comment above testCopyFileFolder().
	 */
	@Test
	public final void testCopyFileAbsent() {

		try {
			galerieUnderTest.copyFile(fromFile, toFile);
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {

		}

		fail();
	}

	@Test
	public final void testCopyFileExists() {

		try {
			toFile.createNewFile();
			Path toPath = FileSystems.getDefault().getPath(toFile.getPath());
			Files.writeString(toPath, firstRandomString);

			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, secondRandomString);

			galerieUnderTest.copyFile(fromFile, toFile);

			String contents = Files.readString(toPath);

			assertEquals(secondRandomString, contents);
		} catch (IOException e) {
			fail();
		}

	}

	@Test
	public final void testCopyFileReadable() {

		boolean go = true;

		FileChannel channel = null;
		FileLock lock = null;

		try {
			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, firstRandomString);

			channel = FileChannel.open(fromPath, StandardOpenOption.WRITE);
			lock = channel.lock();
		} catch (IOException e) {
			go = false;
		}

		if (go) {

			try {
				galerieUnderTest.copyFile(fromFile, toFile);
			} catch (IOException e) {

				try {
					// maybe a little bit overkill but had quite a problem with delete()
					lock.close();
					lock = null;
					channel.close();
					channel = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				return;
			}

		}

		fail();

	}

	// Didn't work correctly with FileLock
	@Test
	public final void testCopyFileWritable() {

		boolean go = true;

		try {
			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, firstRandomString);

			toFile.createNewFile();
			Path toPath = FileSystems.getDefault().getPath(toFile.getPath());
			Files.writeString(toPath, secondRandomString);

			assertTrue(toFile.setWritable(false, false));
		} catch (IOException e) {
			go = false;
		}

		if (go) {

			try {
				galerieUnderTest.copyFile(fromFile, toFile);
			} catch (IOException e) {
				return;
			}

		}

		fail();
	}

}
