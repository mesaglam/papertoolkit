package edu.stanford.hci.r3.util.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> ( ronyeh(AT)cs.stanford.edu )
 * 
 */
public class ImageCache {

	// cache for images
	private WeakHashMap<File, ImageObject> cache = new WeakHashMap<File, ImageObject>();

	private static ImageCache instance = new ImageCache();

	public static ImageCache getInstance() {
		return instance;
	}

	private ImageCache() {
		// nothing
	}

	/**
	 * 
	 */
	public class ImageObject {
		private SoftReference<BufferedImage> bImgSoft;

		private SoftReference<PlanarImage> pImgSoft;

		public ImageObject() {
		}

		/**
		 * @return
		 */
		public PlanarImage getPlanarImage(File imagePath) {
			if (pImgSoft != null) {
				final PlanarImage cachedImg = pImgSoft.get();
				if (cachedImg != null) {
					// not yet released by the Garbage Collector
					// System.out.println("Image Cache Hit for Planar Image");
					return cachedImg;
				} else {
					System.out.println("Image Cache MISS for Planar Image");
				}
			}
			pImgSoft = new SoftReference<PlanarImage>(JAI.create("fileload", imagePath
					.getAbsolutePath()));
			return pImgSoft.get();
		}

		/**
		 * For adding stuff to the cache, programmatically.
		 * 
		 * @param bImg
		 */
		public void setBufferedImage(BufferedImage bImg) {
			bImgSoft = new SoftReference<BufferedImage>(bImg);
		}

		/**
		 * @return
		 */
		public BufferedImage getBufferedImage(File imagePath) {
			if (bImgSoft != null) {
				final BufferedImage cachedImg = bImgSoft.get();
				if (cachedImg != null) {
					// System.out.println("Cache Hit!");
					return cachedImg;
				}
			}

			// System.out.println("Cache Miss =(");
			// released by the Garbage Collector
			// OR, never loaded
			try {
				bImgSoft = new SoftReference<BufferedImage>(ImageIO.read(imagePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bImgSoft.get();
		}
	}

	/**
	 * @param pathToImage
	 * @return
	 */
	private ImageObject get(final File pathToImage) {
		ImageObject image = cache.get(pathToImage);
		if (image == null) { // evicted, or never loaded
			image = new ImageObject();
			cache.put(pathToImage, image);
		}
		return image;
	}

	/**
	 * Use this only if you need to explicitly add something to the cache. Usually, you do NOT need
	 * to use this function.
	 * 
	 * @param File
	 */
	public void addBufferedImageToCache(final File pathToImage, BufferedImage bImg) {
		// System.out.println("Adding...");
		ImageObject image = cache.get(pathToImage);
		if (image == null) { // evicted, or never loaded
			image = new ImageObject();
			cache.put(pathToImage, image);
		}
		image.setBufferedImage(bImg);
	}

	/**
	 * @param pathToImage
	 * @return
	 */
	public PlanarImage getPlanarImage(File pathToImage) {
		if (pathToImage == null) {
			return null;
		}
		return get(pathToImage).getPlanarImage(pathToImage);
	}

	/**
	 * Normally, just call this function to get a BufferedImage and put it in the cache. Set your
	 * hard references to null when you are done.
	 * 
	 * @param pathToImage
	 * @return
	 */
	public BufferedImage getBufferedImage(File pathToImage) {
		return get(pathToImage).getBufferedImage(pathToImage);
	}
}
