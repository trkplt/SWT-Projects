package org.iMage.mosaique.rectangle;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;

/**
 * This class represents an {@link IMosaiqueArtist} who uses rectangles as
 * tiles.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleArtist implements IMosaiqueArtist<BufferedArtImage> {

	private RectangleShape[] tiles;
	private int[] imagesAverageAlpha;
	private int[] imagesAverageRed;
	private int[] imagesAverageGreen;
	private int[] imagesAverageBlue;
	private int length = -1;
	private int width = -1;
	private int height = -1;

	/**
	 * Create an artist who works with {@link RectangleShape RectangleShapes}
	 *
	 * @param images     the images for the tiles
	 * @param tileWidth  the desired width of the tiles
	 * @param tileHeight the desired height of the tiles
	 * @throws IllegalArgumentException iff tileWidth or tileHeight &lt;= 0, or
	 *                                  images is empty.
	 */
	public RectangleArtist(Collection<BufferedArtImage> images, int tileWidth, int tileHeight) {
		length = images.size();

		this.tiles = new RectangleShape[length];
		this.imagesAverageAlpha = new int[length];
		this.imagesAverageRed = new int[length];
		this.imagesAverageGreen = new int[length];
		this.imagesAverageBlue = new int[length];
		this.width = tileWidth;
		this.height = tileHeight;

		int counter = 0;
		Iterator<BufferedArtImage> iterator = images.iterator();

		while (iterator.hasNext()) {
			tiles[counter] = new RectangleShape(iterator.next(), tileWidth, tileHeight);
			counter++;
		}

		for (int i = 0; i < length; i++) {
			Color color = new Color(tiles[i].getAverageColor(), true);
			imagesAverageAlpha[i] = color.getAlpha();
			imagesAverageRed[i] = color.getRed();
			imagesAverageGreen[i] = color.getGreen();
			imagesAverageBlue[i] = color.getBlue();
		}
	}

	@Override
	public List<BufferedImage> getThumbnails() {
		List<BufferedImage> thumbs = new ArrayList<BufferedImage>();

		for (int i = 0; i < length; i++) {
			thumbs.add(tiles[i].getThumbnail());
		}
		return thumbs;
	}

	@Override
	public BufferedArtImage getTileForRegion(BufferedArtImage region) {

		Color color = new Color(new RectangleShape(region, region.getWidth(), region.getHeight()).getAverageColor(),
				true);
		int regionAlpha = color.getAlpha();
		int regionRed = color.getRed();
		int regionGreen = color.getGreen();
		int regionBlue = color.getBlue();

		int bestImage = -1;
		int leastLengthSq = Integer.MAX_VALUE;
		double temp = Integer.MAX_VALUE;

		for (int i = 0; i < length; i++) {
			if ((temp = Math.pow(regionAlpha - imagesAverageAlpha[i], 2) + Math.pow(regionRed - imagesAverageRed[i], 2)
					+ Math.pow(regionGreen - imagesAverageGreen[i], 2)
					+ Math.pow(regionBlue - imagesAverageBlue[i], 2)) < leastLengthSq) {
				leastLengthSq = (int) temp;
				bestImage = i;
			}
		}

		tiles[bestImage].drawMe(region);
		return region;
	}

	@Override
	public int getTileWidth() {
		return this.width;
	}

	@Override
	public int getTileHeight() {
		return this.height;
	}
}
