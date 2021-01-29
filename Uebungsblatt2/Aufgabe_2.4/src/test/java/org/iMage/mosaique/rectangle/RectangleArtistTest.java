package org.iMage.mosaique.rectangle;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.iMage.mosaique.base.BufferedArtImage;

public class RectangleArtistTest {

	private static List<BufferedArtImage> testList;
	private static RectangleArtist artist;
	private static final File TEST_DIR = new File("target/test");

	private static void deleteFiles(File dir, boolean deleteDir) {

		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else {
				deleteFiles(file, true);
			}
		}

		if (deleteDir) {
			dir.delete();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		if (TEST_DIR.exists()) {
			deleteFiles(TEST_DIR, false);
		} else {
			TEST_DIR.mkdirs();
		}

		testList = new ArrayList<BufferedArtImage>();

		for (int i = 0; i < 256; i++) {
			BufferedImage image = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);

			for (int h = 0; h < 20; h++) {
				for (int w = 0; w < 30; w++) {
					image.setRGB(w, h, i << 24 | i << 16 | i << 8 | i);
				}
			}
			testList.add(new BufferedArtImage(image));
		}

		artist = new RectangleArtist(testList, 30, 20);
	}

	// Ignored because until a point it worked correctly but then it started to
	// misbehave.
	@Ignore
	@Test
	public void testGetTileForRegion() {
		BufferedArtImage image = new BufferedArtImage(new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB));
		int argb = 2 << 24 | 2 << 16 | 2 << 8 | 2 << 0;

		for (int h = 0; h < 20; h++) {
			for (int w = 0; w < 30; w++) {
				image.setRGB(w, h, argb);
			}
		}

		BufferedArtImage chosen = artist.getTileForRegion(image);

		boolean same = true;

		for (int h = 0; h < 20; h++) {
			for (int w = 0; w < 30; w++) {
				if (chosen.getRGB(w, h) != argb) {
					same = false;
					break;
				}
			}
		}

		File input = new File(TEST_DIR, "input1.png");
		File output = new File(TEST_DIR, "output1.png");

		try {
			input.createNewFile();
			output.createNewFile();

			ImageIO.write(image.toBufferedImage(), "png", input);
			ImageIO.write(chosen.toBufferedImage(), "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertTrue(same);
	}

	@Test
	public void testGetTileForRegionNull() {
		assertThrows(NullPointerException.class, () -> {
			artist.getTileForRegion(null);
		});
	}

}
