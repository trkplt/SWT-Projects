package org.iMage.mosaique;

import java.awt.image.BufferedImage;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;
import org.iMage.mosaique.base.IMosaiqueEasel;

/**
 * This class defines an {@link IMosaiqueEasel} which operates on
 * {@link BufferedArtImage BufferedArtImages}.
 *
 * @author Dominik Fuchss
 *
 */
public class MosaiqueEasel implements IMosaiqueEasel<BufferedArtImage> {

	@Override
	public BufferedImage createMosaique(BufferedImage input, IMosaiqueArtist<BufferedArtImage> artist) {

		int tileWidth = artist.getTileWidth();
		int tileHeight = artist.getTileHeight();
		int columns = input.getWidth() / tileWidth;
		int rows = input.getHeight() / tileHeight;
		int widthRemainder = input.getWidth() % tileWidth;
		int heightRemainder = input.getHeight() % tileHeight;

		boolean widthFull = false;
		boolean heightFull = false;

		if (widthRemainder == 0) {
			widthFull = true;
			columns--;
		}
		if (heightRemainder == 0) {
			heightFull = true;
			rows--;
		}

		BufferedArtImage output = new BufferedArtImage(input);

		for (int r = 0; r <= rows; r++) {
			for (int c = 0; c <= columns; c++) {

				int usedWidth = tileWidth;
				int usedHeight = tileHeight;

				if (r == rows & !heightFull) {
					usedHeight = heightRemainder;
				}
				if (c == columns & !widthFull) {
					usedWidth = widthRemainder;
				}

				BufferedImage region = input.getSubimage(c * tileWidth, r * tileHeight, usedWidth, usedHeight);
				BufferedArtImage regionArt = new BufferedArtImage(region);
				BufferedArtImage tile = artist.getTileForRegion(regionArt);
				output.setSubimage(c * tileWidth, r * tileHeight, tile);
			}
		}

		return output.toBufferedImage();
	}

}
