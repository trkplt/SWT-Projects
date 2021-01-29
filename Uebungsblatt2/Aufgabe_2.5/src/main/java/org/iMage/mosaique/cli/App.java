package org.iMage.mosaique.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.rectangle.RectangleArtist;

/**
 * This class parses all command line parameters and creates a mosaique.
 */
public final class App {
	private App() {
		throw new IllegalAccessError();
	}

	private static final String CMD_OPTION_INPUT_IMAGE = "i";
	private static final String CMD_OPTION_INPUT_TILES_DIR = "t";
	private static final String CMD_OPTION_OUTPUT_IMAGE = "o";

	private static final String CMD_OPTION_TILE_W = "w";
	private static final String CMD_OPTION_TILE_H = "h";

	public static void main(String[] args) {
		// Don't touch...
		CommandLine cmd = null;
		try {
			cmd = App.doCommandLineParsing(args);
		} catch (ParseException e) {
			System.err.println("Wrong command line arguments given: " + e.getMessage());
			System.exit(1);
		}
		// ...this!

		BufferedImage input = null;

		try {
			File inputFile = new File(cmd.getOptionValue(CMD_OPTION_INPUT_IMAGE));
			input = ImageIO.read(inputFile);
		} catch (IOException e) {
			System.err.println("The input image couldn't be read: " + e.getMessage());
			System.exit(1);
		}

		List<BufferedArtImage> tiles = new ArrayList<BufferedArtImage>();

		try {
			File tilesDir = new File(cmd.getOptionValue(CMD_OPTION_INPUT_TILES_DIR));

			for (File file : tilesDir.listFiles()) {
				tiles.add(new BufferedArtImage(ImageIO.read(file)));
			}

			if (tiles.size() < 10) {
				System.err.println("There are less than 10 mosaique images.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("There was a problem while reading mosaique images: " + e.getMessage());
			System.exit(1);
		}

		int tileWidth = -1;
		int tileHeight = -1;

		String optH = cmd.getOptionValue(CMD_OPTION_TILE_H);
		String optW = cmd.getOptionValue(CMD_OPTION_TILE_W);

		try {
			tileHeight = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_H));
			tileWidth = Integer.parseInt(cmd.getOptionValue(CMD_OPTION_TILE_W));
		} catch (NumberFormatException e) {

			if (optH == null) {
				tileHeight = Math.round(input.getHeight() / 10);
			}

			if (optW == null) {
				tileWidth = Math.round(input.getWidth() / 10);
			}

			if (tileHeight == -1 | tileWidth == -1) {
				System.err.println("The given tile width or height parameters' format is wrong: " + e.getMessage());
				System.exit(1);
			}
		}

		if (tileHeight < 1 | tileHeight > input.getHeight() | tileWidth < 1 | tileWidth > input.getWidth()) {
			System.err.println("The given dimensions for tiles are smaller than 0"
					+ " or greater than the input image dimensions.");
			System.exit(1);
		}

		RectangleArtist artist = new RectangleArtist(tiles, tileWidth, tileHeight);
		MosaiqueEasel create = new MosaiqueEasel();

		BufferedImage output = create.createMosaique(input, artist);

		File outputFile = new File(cmd.getOptionValue(CMD_OPTION_OUTPUT_IMAGE));

		try {
			ImageIO.write(output, "png", outputFile);
		} catch (IOException e) {

			if (outputFile.getParent() == null) {
				System.err.println("The given path to output image is not correct: " + e.getMessage());
			} else {
				System.err.println("Something went wrong while writing to output file: " + e.getMessage());
			}

			System.exit(1);
		}

	}

	/**
	 * Parse and check command line arguments
	 *
	 * @param args command line arguments given by the user
	 * @return CommandLine object encapsulating all options
	 * @throws ParseException if wrong command line parameters or arguments are
	 *                        given
	 */
	private static CommandLine doCommandLineParsing(String[] args) throws ParseException {
		Options options = new Options();
		Option opt;

		/*
		 * Define command line options and arguments
		 */
		opt = new Option(App.CMD_OPTION_INPUT_IMAGE, "input-images", true, "path to input image");
		opt.setRequired(true);
		opt.setType(String.class);
		options.addOption(opt);

		opt = new Option(App.CMD_OPTION_INPUT_TILES_DIR, "tiles-dir", true, "path to tiles directory");
		opt.setRequired(true);
		opt.setType(String.class);
		options.addOption(opt);

		opt = new Option(App.CMD_OPTION_OUTPUT_IMAGE, "image-output", true, "path to output image");
		opt.setRequired(true);
		opt.setType(String.class);
		options.addOption(opt);

		opt = new Option(App.CMD_OPTION_TILE_W, "tile-width", true, "the width of a tile");
		opt.setRequired(false);
		opt.setType(Integer.class);
		options.addOption(opt);

		opt = new Option(App.CMD_OPTION_TILE_H, "tile-height", true, "the height of a tile");
		opt.setRequired(false);
		opt.setType(Integer.class);
		options.addOption(opt);

		CommandLineParser parser = new DefaultParser();
		return parser.parse(options, args);
	}

}
