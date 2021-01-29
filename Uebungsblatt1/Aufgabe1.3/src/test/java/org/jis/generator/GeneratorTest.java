package org.jis.generator;

import org.jis.Main;
import org.jis.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GeneratorTest {
	/**
	 * Class under test.
	 */
	private Generator generator;
	private Generator generatorM;

	@Mock
	private Main main;

	private int imageHeight, imageWidth;
	private static final File TEST_DIR = new File("target/test");
	private static final String IMAGE_FILE = "/image.jpg";
	private String imageName;

	/**
	 * Input for test cases
	 */
	private BufferedImage testImage;
	/**
	 * Metadata for saving the image
	 */
	private IIOMetadata imeta;
	/**
	 * output from test cases
	 */
	private BufferedImage rotatedImageTestResult;

	private File sourceImageFile = new File(this.getClass().getResource(IMAGE_FILE).getFile());

	protected static void deleteTestDirFiles(File directory) {

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else {
				deleteTestDirFiles(file);
			}
		}

		if (!directory.getAbsolutePath().equals(TEST_DIR.getAbsolutePath())) {
			directory.delete();
		}
	}

	/**
	 * Sicherstellen, dass das Ausgabeverzeichnis existiert und leer ist.
	 */
	@BeforeClass
	public static void beforeClass() {
		if (TEST_DIR.exists()) {
			deleteTestDirFiles(TEST_DIR);
		} else {
			TEST_DIR.mkdirs();
		}
	}

	@Before
	public void setUp() {
		this.generator = new Generator(null, 0);
		this.generatorM = new Generator(main, 0);

		this.testImage = null;
		this.imeta = null;
		this.rotatedImageTestResult = null;

		final URL imageResource = this.getClass().getResource(IMAGE_FILE);
		imageName = extractFileNameWithoutExtension(new File(imageResource.getFile()));

		try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
			ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
			reader.setInput(iis, true);
			ImageReadParam params = reader.getDefaultReadParam();
			this.testImage = reader.read(0, params);
			this.imageHeight = this.testImage.getHeight();
			this.imageWidth = this.testImage.getWidth();
			this.imeta = reader.getImageMetadata(0);
			reader.dispose();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private String extractFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		if (fileName.indexOf(".") > 0) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		} else {
			return fileName;
		}
	}

	/**
	 * Automatisches Speichern von testImage.
	 */
	@After
	public void tearDown() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH.mm.ss.SSS");
		String time = sdf.format(new Date());

		File outputFile = new File(MessageFormat.format("{0}/{1}_rotated_{2}.jpg", TEST_DIR, imageName, time));

		if (this.rotatedImageTestResult != null) {
			try (FileOutputStream fos = new FileOutputStream(outputFile);
					ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
				writer.setOutput(ios);

				ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
				iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // mode explicit necessary

				// set JPEG Quality
				iwparam.setCompressionQuality(1f);
				writer.write(this.imeta, new IIOImage(this.rotatedImageTestResult, null, null), iwparam);
				writer.dispose();
			} catch (IOException e) {
				fail();
			}
		}
	}

	@Test
	public void testRotateImage_RotateImage0() {
		this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, 0);

		assertTrue(imageEquals(this.testImage, this.rotatedImageTestResult));
	}

	@Test
	public void testRotateImage_RotateNull0() {
		this.rotatedImageTestResult = this.generator.rotateImage(null, 0);

		assertNull(this.rotatedImageTestResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRotateImage_Rotate042() {
		this.generator.rotateImage(this.testImage, 0.42);
	}

	@Test
	public void testRotateImage_Rotate90() {
		this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_90);

		assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
		assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				assertEquals(this.testImage.getRGB(j, i),
						this.rotatedImageTestResult.getRGB(this.imageHeight - 1 - i, j));
			}
		}
	}

	@Test
	public void testRotateImage_Rotate270() {
		this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_270);

		assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
		assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				assertEquals(this.testImage.getRGB(j, i),
						this.rotatedImageTestResult.getRGB(i, this.imageWidth - 1 - j));
			}
		}
	}

	@Test
	public void testRotateImage_RotateM90() {
		this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Math.toRadians(-90));

		assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
		assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				assertEquals(this.testImage.getRGB(j, i),
						this.rotatedImageTestResult.getRGB(i, this.imageWidth - 1 - j));
			}
		}
	}

	@Test
	public void testRotateImage_RotateM270() {
		this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Math.toRadians(-270));

		assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
		assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				assertEquals(this.testImage.getRGB(j, i),
						this.rotatedImageTestResult.getRGB(this.imageHeight - 1 - i, j));
			}
		}
	}

	/**
	 * Check if two images are identical - pixel wise.
	 * 
	 * @param expected the expected image
	 * @param actual   the actual image
	 * @return true if images are equal, false otherwise.
	 */
	protected static boolean imageEquals(BufferedImage expected, BufferedImage actual) {
		if (expected == null || actual == null) {
			return false;
		}

		if (expected.getHeight() != actual.getHeight()) {
			return false;
		}

		if (expected.getWidth() != actual.getWidth()) {
			return false;
		}

		for (int i = 0; i < expected.getHeight(); i++) {
			for (int j = 0; j < expected.getWidth(); j++) {
				if (expected.getRGB(j, i) != actual.getRGB(j, i)) {
					return false;
				}
			}
		}

		return true;
	}

	protected BufferedImage imageFileToBuff(File imageFile) throws IOException {

		try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile.toURI().toURL().openStream());) {
			ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
			reader.setInput(iis, true);
			ImageReadParam params = reader.getDefaultReadParam();
			BufferedImage image = reader.read(0, params);
			reader.dispose();
			return image;
		}

	}

	@Test
	public void testCreateZip() {

		File zipFile = new File(TEST_DIR, "zipFile.zip");

		Vector<File> imageVector = new Vector<File>();
		imageVector.add(sourceImageFile);

		generator.createZip(zipFile, imageVector);

		File extractionDir = new File(TEST_DIR, "extract");
		extractionDir.mkdir();

		BufferedImage extractedBufferedImage = null;

		try {

			byte[] buffer = new byte[1024];

			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {
				File fileToWrite = new File(extractionDir, zipEntry.getName());
				FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
				int length;

				while ((length = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, length);
				}
				fileOutputStream.close();

				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.closeEntry();
			zipInputStream.close();

			String[] nameList = extractionDir.list();
			File extractedImage = new File(
					extractionDir.getAbsolutePath() + System.getProperty("file.separator") + nameList[0]);
			extractedBufferedImage = imageFileToBuff(extractedImage);
		} catch (IOException e) {
			fail();
		}

		assertTrue(imageEquals(this.testImage, extractedBufferedImage));

	}

	@Test
	public void testCreateZipBlocked() {

		File zipFile = new File(TEST_DIR, "zipFile2.zip");
		File copyFile = new File(TEST_DIR, "copyFile.jpg");

		LayoutGalerie layoutGalerie = new LayoutGalerie(null, null);
		try {
			layoutGalerie.copyFile(sourceImageFile, copyFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Vector<File> imageVector = new Vector<File>();
		imageVector.add(copyFile);

		FileChannel channel = null;
		FileLock lock = null;

		try {
			Path copyPath = FileSystems.getDefault().getPath(copyFile.getPath());
			channel = FileChannel.open(copyPath, StandardOpenOption.WRITE);
			lock = channel.lock();

			generator.createZip(zipFile, imageVector);

			lock.release();
			channel.close();
			lock = null;
			channel = null;
		} catch (IOException e) {
			fail();
		}

		assertEquals(0, zipFile.length());
	}

	@Test
	public void testGenerateImage() {

		File imageOutDir = new File(TEST_DIR, "generatedImage");
		File generatedImageFile = null;
		imageOutDir.mkdir();

		BufferedImage generatedBufferedImage = null;

		try {
			generatedImageFile = generator.generateImage(sourceImageFile, imageOutDir, false, this.imageWidth,
					this.imageHeight, "");
			// System.gc();

			generatedBufferedImage = imageFileToBuff(generatedImageFile);
		} catch (IOException e) {
			fail();
		}

		assertEquals(this.imageHeight, generatedBufferedImage.getHeight());
		assertEquals(this.imageWidth, generatedBufferedImage.getWidth());
	}

	@Test
	public void testGenerateImageLandscape() {

		File imageOutDir = new File(TEST_DIR, "generatedLandscapeImage");
		File generatedImageFile = null;
		imageOutDir.mkdir();

		BufferedImage generatedBufferedImage = null;

		BufferedImage landscapeBufferedImage = generator.rotateImage(this.testImage, Generator.ROTATE_90);

		File outputFile = new File(TEST_DIR, "landscapeImage.jpg");

		try (FileOutputStream fos = new FileOutputStream(outputFile);
				ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			writer.setOutput(ios);

			ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

			iwparam.setCompressionQuality(1f);
			writer.write(null, new IIOImage(landscapeBufferedImage, null, null), iwparam);
			writer.dispose();
		} catch (IOException e) {
			fail();
		}

		try {

			generatedImageFile = generator.generateImage(outputFile, imageOutDir, false, this.imageHeight,
					this.imageWidth, "");
			// System.gc();

			generatedBufferedImage = imageFileToBuff(generatedImageFile);
		} catch (IOException e) {
			fail();
		}

		assertEquals(this.imageHeight, generatedBufferedImage.getWidth());
		assertEquals(this.imageWidth, generatedBufferedImage.getHeight());
	}

	@Test
	public void testRotateBlocked() {

		File rotatedImage = new File(TEST_DIR, "rotatedImage2.jpg");
		BufferedImage rotatedBufferedImage = null;

		LayoutGalerie layoutGalerie = new LayoutGalerie(null, null);
		FileChannel channel = null;
		FileLock lock = null;

		try {
			rotatedImage.createNewFile();

			layoutGalerie.copyFile(sourceImageFile, rotatedImage);

			Path rotatedPath = FileSystems.getDefault().getPath(rotatedImage.getPath());
			channel = FileChannel.open(rotatedPath, StandardOpenOption.WRITE);
			lock = channel.lock();

			generator.rotate(rotatedImage);

			lock.release();
			channel.close();
			lock = null;
			channel = null;

			rotatedBufferedImage = imageFileToBuff(rotatedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertTrue(imageEquals(testImage, rotatedBufferedImage));
	}

	@Test
	public void testGenerateTextMainNull() {

		File generateTextFile = new File(TEST_DIR, "generateText");
		generateTextFile.mkdir();
		File fromDir = new File(generateTextFile, "from");
		fromDir.mkdir();
		File toDir = new File(generateTextFile, "to");
		toDir.mkdir();

		File fromImage = new File(fromDir, "image.jpg");
		LayoutGalerie layoutGalerie = new LayoutGalerie(null, null);
		try {
			layoutGalerie.copyFile(sourceImageFile, fromImage);

			generator.generateText(fromDir, toDir, this.imageWidth, this.imageHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertNotEquals(fromDir.list().length, toDir.list().length);
	}

	@Test
	public void testRotateIMCActivateError() {

		File rotatedImage = new File(TEST_DIR, "rotatedImage.jpg");

		LayoutGalerie layoutGalerie = new LayoutGalerie(null, null);
		try {
			layoutGalerie.copyFile(sourceImageFile, rotatedImage);

			generatorM.rotate(rotatedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(0, rotatedImage.length());
	}

	@Test
	public void testRotateDeprecated() {

		File copiedFile = new File(TEST_DIR, "rotateDepr.jpg");
		BufferedImage copyBufferedImage = null;

		LayoutGalerie layoutGalerie = new LayoutGalerie(null, null);
		try {
			layoutGalerie.copyFile(sourceImageFile, copiedFile);

			generatorM.rotate(copiedFile, 90);

			copyBufferedImage = imageFileToBuff(copiedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertTrue(imageEquals(testImage, copyBufferedImage));
	}

}
